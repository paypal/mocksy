package org.mocksy.config.xml;

/*
 * Copyright 2009, PayPal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.BeanUtils;
import org.mocksy.Response;
import org.mocksy.config.RulesetFactoryFactory;
import org.mocksy.config.UpdateableRulesetFactory;
import org.mocksy.filter.ResponseFilter;
import org.mocksy.rules.Matcher;
import org.mocksy.rules.ResponseRule;
import org.mocksy.rules.Rule;
import org.mocksy.rules.Ruleset;
import org.mocksy.rules.RulesetRule;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.rules.http.HttpProxyRule;
import org.mocksy.rules.xml.XmlMatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * RulesetFactory that sets up Rulesets and the related components from XML
 * files.  This Factory is updateable based on the needsUpdate logic of the
 * Source.
 * 
 * @author Saleem Shafi
 */
public class XmlRulesetFactory implements UpdateableRulesetFactory {
	private static final String FILTER_TAG = "filter";
	private static final String XPATH_ATTRIB = "xpath";
	private static final String CLASS_ATTRIB = "class";
	private static final String HEADER_ATTRIB = "header";
	private static final String PARAM_ATTRIB = "param";
	private static final String RULE_TAG = "rule";
	private static final String DEFAULT_RULE_TAG = "default-rule";
	private static final String MATCH_TAG = "match";
	private static final String NOT_MATCH_TAG = "not-match";
	private static final String RULESET_ATTRIB = "ruleset";
	private static final String PROXY_URL_ATTRIB = "proxy-url";
	private static final String FILE_ATTRIB = "file";
	private static final String CONTENT_TYPE_ATTRIB = "content-type";
	private static final String RESPONSE_TAG = "response";
	private static final String SOURCE_TAG = "source";
	private static final String OPTIONS_TAG = "options";

	private Ruleset ruleset;
	private XmlSource source;

	/**
	 * Creates the RulesetFactory from the given Source.
	 * 
	 * @param source the XmlSource containing the Ruleset info
	 * @throws Exception
	 */
	public XmlRulesetFactory(XmlSource source) throws Exception {
		this.source = source;
		this.ruleset = new Ruleset( this );
		this.updateData();
	}

	/**
	 * Creates the RulesetFactory from the given File.
	 * 
	 * @param rulesetFile the File containing the XML Ruleset data
	 * @throws Exception
	 */
	public XmlRulesetFactory(File rulesetFile) throws Exception {
		this( new FileXmlSource( rulesetFile ) );
	}

	/**
	 * Creates the RulesetFactory from the given URL.
	 * 
	 * @param rulesetURL the URL containing the XML Ruleset data
	 * @throws Exception
	 */
	public XmlRulesetFactory(URL rulesetURL) throws Exception {
		this( new UrlXmlSource( rulesetURL ) );
	}

	/**
	 * Get the Ruleset built by this Configuration.
	 * 
	 * @return the configured Ruleset
	 */
	public Ruleset getRuleset() {
		return this.ruleset;
	}

	/**
	 * Updates the Ruleset if the configuration has changed.
	 */
	public synchronized void checkForUpdates() throws Exception {
		if ( this.source.needsUpdate() ) {
			updateData();
		}
	}

	/**
	 * Updates the Ruleset managed be this Configuration and rebuilds
	 * it with the latest XML configuration.
	 * 
	 * @throws Exception
	 */
	protected void updateData() throws Exception {
		// Clear the ruleset
		this.ruleset.clear();
		Element ruleset = this.source.getRulesetElement();
		String version = ruleset.getAttribute( "version" );
		if ( version == null ) {
			// this is for later, when we might have to change the
			// structure of the ruleset XML
			version = "1.0";
		}
		// Get this list of Rules
		NodeList nodeList = ruleset.getElementsByTagName( RULE_TAG );
		for ( int i = 0; i < nodeList.getLength(); i++ ) {
			Element ruleNode = (Element) nodeList.item( i );
			this.ruleset
			        .addRule( getRule( ruleNode, "Rule[" + ( i + 1 ) + "]" ) );
		}
		// Setup the default response
		// TODO need to do the same thing here for custom responses.
		Element defaultRuleNode = getSingleChild( ruleset, DEFAULT_RULE_TAG );
		if ( defaultRuleNode == null ) {
			throw new IOException( DEFAULT_RULE_TAG + " element is required." );
		}
		this.ruleset.setDefaultRule( getRule( defaultRuleNode, "default" ) );
	}

	private Rule getRule(Element ruleNode, String defaultId) throws Exception {
		Rule rule = null;
		// Setup a RulesetRule
		if ( ruleNode.hasAttribute( RULESET_ATTRIB ) ) {
			rule = getRulesetRule( ruleNode );
		}
		// Setup a ProxyRule
		else if ( ruleNode.hasAttribute( PROXY_URL_ATTRIB ) ) {
			rule = getProxyRule( ruleNode );
		}
		// Setup a ResponseRule
		else {
			rule = getResponseRule( ruleNode, defaultId );
		}
		// Setup the Matchers for the Rule
		NodeList matcherNodes = ruleNode.getElementsByTagName( MATCH_TAG );
		NodeList notMatcherNodes = ruleNode
		        .getElementsByTagName( NOT_MATCH_TAG );
		// This doesn't work
		// if ( matcherNodes.getLength() + notMatcherNodes.getLength() <= 0 ) {
		// throw new IOException( "At least one <" + MATCH_TAG + "> or <"
		// + NOT_MATCH_TAG + "> tag must be defined" );
		// }
		for ( int j = 0; j < matcherNodes.getLength(); j++ ) {
			Element elem = (Element) matcherNodes.item( j );
			Matcher matcher = getMatcher( elem );
			rule.addMatcher( matcher );
		}
		for ( int j = 0; j < notMatcherNodes.getLength(); j++ ) {
			Element elem = (Element) notMatcherNodes.item( j );
			Matcher matcher = getMatcher( elem );
			matcher.setNegative( true );
			rule.addMatcher( matcher );
		}
		return rule;
	}

	private RulesetRule getRulesetRule(Element ruleNode) throws Exception {
		String rulesetName = getRequiredAttribute( ruleNode, RULESET_ATTRIB );
		Ruleset subRuleset = RulesetFactoryFactory.getRulesetFactory(
		        rulesetName, this.source ).getRuleset();
		return new RulesetRule( subRuleset );
	}

	private HttpProxyRule getProxyRule(Element ruleNode) throws Exception {
		String proxyHost = getRequiredAttribute( ruleNode, PROXY_URL_ATTRIB );
		return new HttpProxyRule( proxyHost );
	}

	private ResponseRule getResponseRule(Element ruleNode, String defaultId)
	        throws Exception
	{
		// the (optional) id
		String idAttrib = getAttribute( ruleNode, "id" );
		String id = ( ( idAttrib != null ) ? idAttrib : defaultId );

		return new ResponseRule( getResponse( id, ruleNode ) );

	}

	private Response getResponse(String id, Element ruleNode) throws Exception {
		String fileName = getAttribute( ruleNode, FILE_ATTRIB );
		Class<Response> responseClass = null;
		URL responseURL = null;
		Map<String, String> options = new HashMap<String, String>();
		if ( fileName != null ) {
			responseURL = this.source.getRelativeURL( fileName );
			responseClass = Response.class;
		}
		else {
			Element responseNode = getSingleChild( ruleNode, RESPONSE_TAG );
			if ( responseNode == null ) {
				throw new IOException( "Element " + ruleNode.getNodeName()
				        + " has no response data." );
			}
			String responseClassName = getRequiredAttribute( responseNode,
			        CLASS_ATTRIB );
			responseClass = (Class<Response>) Class.forName( responseClassName );
			Element sourceNode = getSingleChild( responseNode, SOURCE_TAG );
			if ( sourceNode == null ) {
				throw new IOException( "Element " + RESPONSE_TAG + " needs a <"
				        + SOURCE_TAG + "/> element." );
			}
			responseURL = this.source.getRelativeURL( sourceNode
			        .getTextContent() );

			Element optionsNode = getSingleChild( responseNode, OPTIONS_TAG );
			if ( optionsNode != null ) {
				NodeList optionNodes = optionsNode.getChildNodes();
				for ( int i = 0; i < optionNodes.getLength(); i++ ) {
					Node option = optionNodes.item( i );
					if (option.getNodeType() == Node.ELEMENT_NODE) {
						options.put( option.getNodeName(), option.getTextContent() );
					}
				}
			}
		}

		// the (optional) filter class
		List<ResponseFilter> filters = getFilters( ruleNode );
		Response response = this.createResponse( responseClass, id, responseURL
		        .openStream(), filters );
		if ( ruleNode.hasAttribute( "delay" ) ) {
			response.setDelay( Integer.parseInt( ruleNode
			        .getAttribute( "delay" ) ) );
		}

		String contentType = getAttribute( ruleNode, CONTENT_TYPE_ATTRIB );
		if ( contentType == null ) {
			contentType = figureOutContentType( responseURL.getPath() );
		}
		response.setContentType( contentType );

		this.applyOptions( response, options );
		return response;
	}

	private Response createResponse(Class<Response> responseClass, String id,
	        InputStream responseContent, List<ResponseFilter> filters)
	        throws Exception
	{
		try {
			Constructor<Response> resConstructor = responseClass
			        .getConstructor( String.class, InputStream.class,
			                List.class );
			Response response = resConstructor.newInstance( id,
			        responseContent, filters );
			return response;
		}
		catch ( Exception e ) {
			// i know it's bad, but there are just too many exceptions here to
			// deal with them one by one.
			throw new Exception(
			        "Couldn't create an instance of the desired response class: "
			                + responseClass.getName(), e );
		}

	}

	private void applyOptions(Response response, Map<String, String> options)
	        throws IOException
	{
		// TODO this could use a lot of work
		try {
			for ( String key : options.keySet() ) {
				String value = options.get( key );
				BeanUtils.setProperty( response, key, value );
//				Field field = response.getClass().getField( key );
//				field.setAccessible( true );
//				field.set( response, value );
			}
		}
		catch ( Exception e ) {
			throw new IOException( "Could not apply options to response.", e );
		}
	}

	private List<ResponseFilter> getFilters(Element ruleNode) throws Exception {
		List<ResponseFilter> filters = new ArrayList<ResponseFilter>();
		NodeList filterNodes = ruleNode.getElementsByTagName( FILTER_TAG );
		int numFilters = filterNodes.getLength();
		for ( int j = 0; j < numFilters; j++ ) {
			Element filterNode = (Element) filterNodes.item( j );
			String filterClassAttrib = getRequiredAttribute( filterNode,
			        CLASS_ATTRIB );
			Class<?> c = Class.forName( filterClassAttrib );
			Constructor<?> ct = c.getConstructor( new Class[0] );
			ResponseFilter filter = (ResponseFilter) ct
			        .newInstance( new Object[0] );

			filter.setRelativeSource( this.source );
			NodeList propNodes = filterNode.getChildNodes();
			int length = propNodes.getLength();
			Map<String, String> properties = new HashMap<String, String>();
			for ( int i = 0; i < length; i++ ) {
				if ( propNodes.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
					Element propNode = (Element) propNodes.item( i );
					properties.put( propNode.getTagName(), propNode
					        .getTextContent() );
				}
			}
			filter.initialize( properties );

			filters.add( filter );
		}
		return filters;
	}

	private Matcher getMatcher(Element matchElem) throws Exception {
		Matcher matcher = null;
		String matchClass = getAttribute( matchElem, CLASS_ATTRIB );
		if ( matchClass != null ) {
			matcher = (Matcher) Class.forName( matchClass ).newInstance();
		}
		else if ( matchElem.hasAttribute( XPATH_ATTRIB ) ) {
			String xpath = getAttribute( matchElem, XPATH_ATTRIB );
			matcher = new XmlMatcher( xpath );
		}
		else {
			matcher = new HttpMatcher();
			( (HttpMatcher) matcher ).setHeader( getAttribute( matchElem,
			        HEADER_ATTRIB ) );
			( (HttpMatcher) matcher ).setParam( getAttribute( matchElem,
			        PARAM_ATTRIB ) );
		}
		String pattern = matchElem.getTextContent();
		if ( pattern != null ) {
			matcher.setPattern( Pattern.compile( pattern ) );
		}
		return matcher;
	}

	private String figureOutContentType(String fileName) {
		String contentType = null;
		String[] tokens = fileName.split( "\\." );
		String extension = tokens[tokens.length - 1];
		if ( "xml".equals( extension ) ) {
			contentType = "text/xml";
		}
		else if ( "json".equals( extension ) ) {
			contentType = "text/json";
		}
		else {

			contentType = "text/plain";
		}
		return contentType;
	}

	private static String getRequiredAttribute(Element elem, String attribute)
	        throws IOException
	{
		String value = getAttribute( elem, attribute );
		if ( value == null ) {
			throw new IOException( "Element '" + elem.getNodeName()
			        + "' is missing '" + attribute + "' attribute" );
		}
		return value;
	}

	private static String getAttribute(Element elem, String attribute) {
		if ( !elem.hasAttribute( attribute ) ) {
			return null;
		}
		return elem.getAttribute( attribute );
	}

	private static Element getSingleChild(Element parent, String tagName) {
		NodeList children = parent.getElementsByTagName( tagName );
		if ( children.getLength() == 0 ) {
			return null;
		}
		return (Element) children.item( 0 );
	}

}
