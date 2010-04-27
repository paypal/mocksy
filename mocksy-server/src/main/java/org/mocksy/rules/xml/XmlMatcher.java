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

import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.mocksy.Request;
import org.mocksy.rules.Matcher;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Matcher that can grab portions of an XML document using XPath.
 * 
 * @author Saleem Shafi
 */
public class XmlMatcher extends Matcher {
	private static final Logger logger = Logger.getLogger( XmlMatcher.class
	        .getName() );
	private static final Map<Request, Document> documentCache = new WeakHashMap<Request, Document>();
	private String xpath;

	/**
	 * Creates the XmlMatcher with the given XPath query
	 * 
	 * @param xpath the XPath query to use on the XML document
	 */
	public XmlMatcher(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public boolean matches(Request request) {
		// Look up all values for the given xpath. If none, then no match.
		Document document = getDocument( request );
		String[] values = null;
		String xpath = this.xpath;
		// this will be null if the request wasn't XML
		if ( document != null ) {
			try {
				// find the values matching the XPath query
				values = getValues( document, xpath, true );
			}
			catch ( XPathExpressionException e ) {
				// trap the exception for when the XPath is invalid, just log it
				logger.log( Level.SEVERE, "Invalid XPath expression: " + xpath,
				        e );
			}
		}
		// there are no query matches, just bail out now
		if ( values == null || values.length == 0 ) {
			return this.isNegative();
		}
		// otherwise see if any of them match the pattern
		return this.matchValues( values ) ^ this.isNegative();
	}

	/**
	 * Return the text values at the specified location in the document, or an
	 * empty list if not found.
	 * 
	 * @param expression
	 *            The XPath expression for the element
	 * @param trim
	 *            Trim resulting data
	 * @return The text values of the elements found (empty if not found).
	 * @throws XPathExpressionException
	 */
	public static String[] getValues(Document doc, String expression,
	        boolean trim) throws XPathExpressionException
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate( expression, doc,
		        XPathConstants.NODESET );
		if ( nodes == null ) {
			return new String[0];
		}
		String[] result = new String[nodes.getLength()];
		for ( int i = 0; i < result.length; i++ ) {
			result[i] = nodes.item( i ).getTextContent();
			if ( trim ) {
				result[i] = result[i].trim();
			}
		}
		return result;
	}

	/**
	 * Returns the XPath query used by the Matcher
	 * 
	 * @return the XPath query
	 */
	public String getXpath() {
		return this.xpath;
	}

	@Override
	public String toString() {
		return "XML Matcher, xpath " + this.xpath + ", pattern "
		        + this.getPattern();
	}

	/**
	 * Returns the XML document represented by the Request.
	 * 
	 * @param request the Request to get the XML out of 
	 * @return the XML document in the Request, null if it's not XML
	 */
	protected Document getDocument(Request request) {
		synchronized ( documentCache ) {
			// if we haven't seen this request before
			if ( !documentCache.containsKey( request ) ) {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory
				        .newInstance();
				domFactory.setNamespaceAware( false );
				try {
					// get the XML from the request
					DocumentBuilder builder = domFactory.newDocumentBuilder();
					InputStream stream = request.getData();
					// cache it because we can't guarantee that the Request's
					// InputStream is re-readable
					documentCache.put( request, builder.parse( stream ) );
				}
				catch ( Exception e ) {
					// If this isn't XML, just log it and move on
					logger.log( Level.WARNING,
					        "Trying to process a non-XML request as XML.", e );
					documentCache.put( request, null );
				}
			}
		}
		return documentCache.get( request );
	}
}
