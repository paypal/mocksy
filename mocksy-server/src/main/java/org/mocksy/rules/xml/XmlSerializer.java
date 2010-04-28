package org.mocksy.rules.xml;

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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.serialize.XMLSerializer;
import org.mocksy.Response;
import org.mocksy.filter.ResponseFilter;
import org.mocksy.rules.Matcher;
import org.mocksy.rules.ResponseRule;
import org.mocksy.rules.Rule;
import org.mocksy.rules.Ruleset;
import org.mocksy.rules.RulesetRule;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.rules.http.HttpProxyRule;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Writes a Ruleset description in XML.
 *  
 * @author Saleem Shafi
 */
public class XmlSerializer {
	private OutputStream stream;

	/**
	 * Creates the serializer that will write to the given OutputStream.
	 * 
	 * @param stream the OutputStream to write the XML Ruleset info to.
	 */
	public XmlSerializer(OutputStream stream) {
		this.stream = stream;
	}

	/**
	 * Serializes the given Ruleset into an XML format.
	 * 
	 * @param rules the Ruleset to serialize
	 * @throws IOException if there's a problem writing the XML
	 */
	public void serialize(Ruleset rules) throws IOException {
		XMLSerializer ser = new XMLSerializer();
		ser.setOutputByteStream( this.stream );
		try {
			Document rulesDoc = DocumentBuilderFactory.newInstance()
			        .newDocumentBuilder().newDocument();
			// start the XML by building a Ruleset element
			Element rootElement = getRulesetElement( rulesDoc, rules );
			rulesDoc.appendChild( rootElement );
			ser.serialize( rulesDoc );
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException( e );
		}
	}

	private Element getRulesetElement(Document rulesDoc, Ruleset rules)
	        throws IOException
	{
		Element rulesElement = rulesDoc.createElement( "rules" );
		// default response
		rulesElement.appendChild( getDefaultRuleElement( rulesDoc, rules
		        .getDefaultRule() ) );
		// rules
		for ( Rule rule : rules.getRules() ) {
			rulesElement.appendChild( getRuleElement( rulesDoc, rule ) );
		}

		return rulesElement;
	}

	private Node getRuleElement(Document rulesDoc, Rule rule)
	        throws IOException
	{
		Element ruleElem = rulesDoc.createElement( "rule" );
		for ( Matcher matcher : rule.getMatchers() ) {
			ruleElem.appendChild( getMatcherElement( rulesDoc, matcher ) );
		}
		if ( rule instanceof ResponseRule ) {
			Element responseElem = rulesDoc.createElement( "response" );
			Response response = ( (ResponseRule) rule ).getResponse();
			if ( response != null ) {
				populateResponseElement( rulesDoc, responseElem, response );
			}
			ruleElem.appendChild( responseElem );
		}
		else if ( rule instanceof RulesetRule ) {
			ruleElem.appendChild( getRulesetElement( rulesDoc,
			        ( (RulesetRule) rule ).getRuleset() ) );
		}
		else if ( rule instanceof HttpProxyRule ) {
			ruleElem.setAttribute( "proxy-url", ( (HttpProxyRule) rule )
			        .getProxyUrl() );
		}
		return ruleElem;
	}

	private Node getMatcherElement(Document rulesDoc, Matcher matcher) {
		Element matcherElem = rulesDoc.createElement( "matcher" );
		matcherElem.setTextContent( matcher.getPattern().pattern() );
		if ( matcher instanceof HttpMatcher ) {
			HttpMatcher httpMatcher = (HttpMatcher) matcher;
			if ( httpMatcher.getHeader() != null ) {
				matcherElem.setAttribute( "header", httpMatcher.getHeader() );
			}
			if ( httpMatcher.getParam() != null ) {
				matcherElem.setAttribute( "param", httpMatcher.getParam() );
			}
		}
		else if ( matcher instanceof XmlMatcher ) {
			XmlMatcher xmlMatcher = (XmlMatcher) matcher;
			if ( xmlMatcher.getXpath() != null ) {
				matcherElem.setAttribute( "xpath", xmlMatcher.getXpath() );
			}
		}
		return matcherElem;
	}

	private Node getDefaultRuleElement(Document rulesDoc, Rule defaultRule)
	        throws DOMException, IOException
	{
		Element defaultRuleElem = rulesDoc.createElement( "default-rule" );
		if ( defaultRule != null ) {
			defaultRuleElem
			        .appendChild( getRuleElement( rulesDoc, defaultRule ) );
		}
		return defaultRuleElem;
	}

	private void populateResponseElement(Document rulesDoc, Element element,
	        Response response)
	{
		element.setAttribute( "id", response.getId() );
		element.setAttribute( "contentType", response.getContentType() );
		element.setTextContent( response.toString() );
		if ( !response.getFilters().isEmpty() ) {
			for ( ResponseFilter filter : response.getFilters() ) {
				Element filterNode = rulesDoc.createElement( "filter" );
				filterNode.setAttribute( "class", filter.getClass().getName() );
				element.appendChild( filterNode );
				// TODO need to render the filter properties
				Map<String, String> properties = filter.getProperties();
				for ( String propName : properties.keySet() ) {
					Element propNode = rulesDoc.createElement( propName );
					propNode.setNodeValue( properties.get( propName ) );
					filterNode.appendChild( propNode );
				}
			}
		}
	}
}
