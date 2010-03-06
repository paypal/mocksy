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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mocksy.config.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Represents an XML-based Source.
 * 
 * @author Saleem Shafi
 */
abstract public class XmlSource implements Source {

	// TODO: clean up the throws clauses

	/**
	 * Returns the ruleset element from the XML source.
	 * 
	 * @return ruleset XML element
	 * @throws Exception if there's a problem retrieving the ruleset element
	 */
	abstract public Element getRulesetElement() throws Exception;

	/**
	 * Returns the ruleset element from an XML InputStream.  This helper
	 * method abstracts the actual XML processing from subtypes of XmlSource.
	 * 
	 * @param input InputStream containing the XML document
	 * @return the ruleset XML element from the document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected static Element getRulesetElement(InputStream input)
	        throws ParserConfigurationException, SAXException, IOException
	{
		if ( input == null ) {
			throw new IllegalArgumentException(
			        "input parameter cannot be null" );
		}
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
		        .newInstance();
		domFactory.setNamespaceAware( false );
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse( input );
		return doc.getDocumentElement();
	}

}
