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
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.mocksy.Response;
import org.mocksy.config.Configurator;
import org.mocksy.filter.ResponseFilter;
import org.mocksy.rules.Matcher;
import org.mocksy.rules.ResponseRule;
import org.mocksy.rules.Rule;
import org.mocksy.rules.Ruleset;
import org.mocksy.rules.RulesetRule;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.rules.xml.XmlMatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Configurator that sets up Rulesets and the related components from XML
 * files.
 * 
 * @author Saleem Shafi
 */
public class XmlRulesetConfig implements Configurator {
	private static final String FILTER_TAG = "filter";
	private static final String XPATH_ATTRIB = "xpath";
	private static final String CLASS_ATTRIB = "class";
	private static final String HEADER_ATTRIB = "header";
	private static final String PARAM_ATTRIB = "param";
	private static final String RULE_TAG = "rule";
	private static final String DEFAULT_RESPONSE_TAG = "default-response";
	private static final String MATCH_TAG = "match";
	private static final String RULESET_ATTRIB = "ruleset";
	private static final String FILE_ATTRIB = "file";
	private static final String CONTENT_TYPE_ATTRIB = "content-type";

	private Ruleset ruleset;
	private XmlSource source;

	/**
	 * Creates the Configurator from the given Source.
	 * 
	 * @param source the XmlSource containing the Ruleset info
	 * @throws Exception
	 */
	public XmlRulesetConfig(XmlSource source) throws Exception {
		this.source = source;
		this.ruleset = new Ruleset( this );
		this.updateData();
	}

	/**
	 * Creates the Configurator from the given File.
	 * 
	 * @param rulesetFile the File containing the XML Ruleset data
	 * @throws Exception
	 */
	public XmlRulesetConfig(File rulesetFile) throws Exception {
		this( new FileXmlSource( rulesetFile ) );
	}

	/**
	 * Creates the Configurator from the given URL.
	 * 
	 * @param rulesetURL the URL containing the XML Ruleset data
	 * @throws Exception
	 */
	public XmlRulesetConfig(URL rulesetURL) throws Exception {
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
		Element root = this.source.getRootElement();
		// Get this list of Rules
		NodeList nodeList = root.getElementsByTagName( RULE_TAG );
		for ( int i = 0; i < nodeList.getLength(); i++ ) {
			Element ruleNode = (Element) nodeList.item( i );
			Rule rule = null;
			// Setup a RulesetRule
			if ( ruleNode.hasAttribute( "ruleset" ) ) {
				rule = getRulesetRule( ruleNode );
			}
			// Setup a ResponseRule
			else {
				rule = getResponseRule( ruleNode, i + 1 );
			}
			// Setup the Matchers for the Rule
			NodeList matcherNodes = ruleNode.getElementsByTagName( MATCH_TAG );
			for ( int j = 0; j < matcherNodes.getLength(); j++ ) {
				Element elem = (Element) matcherNodes.item( j );
				Matcher matcher = getMatcher( elem );
				rule.addMatcher( matcher );
			}
			this.ruleset.addRule( rule );
		}
		// Setup the default response
		nodeList = root.getElementsByTagName( DEFAULT_RESPONSE_TAG );
		if ( nodeList.getLength() > 0 ) {
			// there should only be one
			Element ruleNode = (Element) nodeList.item( 0 );
			this.ruleset
			        .setDefaultResponse( getResponse( "default", ruleNode ) );
		}
	}

	private RulesetRule getRulesetRule(Element ruleNode) throws Exception {
		String rulesetName = getRequiredAttribute( ruleNode, RULESET_ATTRIB );
		XmlSource subSource = (XmlSource) this.source
		        .getRelativeSource( rulesetName );
		Ruleset subRuleset = new XmlRulesetConfig( subSource ).getRuleset();
		return new RulesetRule( subRuleset );

	}

	private ResponseRule getResponseRule(Element ruleNode, int index)
	        throws Exception
	{
		// the (optional) id
		String idAttrib = getAttribute( ruleNode, "id" );
		String id = ( ( idAttrib != null ) ? idAttrib : "Rule[" + index + "]" );

		return new ResponseRule( getResponse( id, ruleNode ) );

	}

	private Response getResponse(String id, Element ruleNode) throws Exception {
		// get the filename
		String fileName = getRequiredAttribute( ruleNode, FILE_ATTRIB );
		// create response file object
		URL responseURL = this.source.getRelativeURL( fileName );

		// the (optional) filter class
		List<ResponseFilter> filters = getFilters( ruleNode );

		String contentType = getAttribute( ruleNode, CONTENT_TYPE_ATTRIB );
		if ( contentType == null ) {
			contentType = figureOutContentType( fileName );
		}
		Response response = new Response( id, responseURL.openStream(),
		        contentType, filters );
		if ( ruleNode.hasAttribute( "delay" ) ) {
			response.setDelay( Integer.parseInt( ruleNode
			        .getAttribute( "delay" ) ) );
		}
		return response;
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

}
