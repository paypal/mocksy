package org.mocksy.filter;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.mocksy.config.Source;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * ResponseFilter that uses a XSL transformation to modify the Response
 * data.
 *  
 * @author Saleem Shafi
 */
public class XslFilter implements ResponseFilter {
	private Transformer transformer;
	private Source relativeSource;
	private String stylesheetLocation;

	public XslFilter() {
		this.transformer = null;
	}

	/**
	 * Create the filter with the given File as the XSL stylesheet.
	 * 
	 * @param xsltFile the XSL template
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	public XslFilter(File xsltFile) throws TransformerConfigurationException,
	        TransformerFactoryConfigurationError
	{
		StreamSource source = new StreamSource( xsltFile );
		this.stylesheetLocation = xsltFile.getAbsolutePath();
		this.transformer = TransformerFactory.newInstance().newTransformer(
		        source );
	}

	/**
	 * Create the filter with the given String as the XSL stylesheet.  Note:
	 * the String is NOT the name of the file containing the XSL stylesheet,
	 * but the actual XSL, itself.
	 * 
	 * @param xslTemplate the XSL template (not the filename) 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	public XslFilter(String xslTemplate)
	        throws TransformerConfigurationException,
	        TransformerFactoryConfigurationError
	{
		StreamSource source = new StreamSource( new ByteArrayInputStream(
		        xslTemplate.getBytes() ) );
		this.transformer = TransformerFactory.newInstance().newTransformer(
		        source );
	}

	/**
	 * Initializes the XslFilter.  The given Map of properties must contain
	 * a "stylesheet" key whose value is a relative location to an XSL file.
	 * The file will be discovered relative to the Source object set with
	 * {@link #setRelativeSource(Source)}, which will probably be the 
	 * location of the containing {@link org.mocksy.rules.Ruleset}.
	 * 
	 * @param properties the Map containing the "stylesheet" key
	 * @throws FilterException if the XSL stylesheet couldn't be found or
	 * 		couldn't be processed properly.
	 */
	public void initialize(Map<String, String> properties)
	        throws FilterException
	{
		if ( properties == null || !properties.containsKey( "stylesheet" ) ) {
			throw new IllegalArgumentException(
			        "XmlResponseFilter requires a 'stylesheet' property." );
		}
		try {
			URL xsltLocation = this.relativeSource.getRelativeURL( properties
			        .get( "stylesheet" ) );
			this.stylesheetLocation = xsltLocation.toString();
			StreamSource source = new StreamSource( xsltLocation.openStream() );
			this.transformer = TransformerFactory.newInstance().newTransformer(
			        source );
		}
		catch ( IOException e ) {
			throw new FilterException( e );
		}
		catch ( TransformerConfigurationException e ) {
			throw new FilterException( e );
		}
		catch ( TransformerFactoryConfigurationError e ) {
			throw new FilterException( e );
		}
	}

	/**
	 * Sets the Source relative to whice the stylesheet will be found.
	 * @param relativeSource Source object relative to which we'll look for
	 * 		the stylesheet.
	 */
	public void setRelativeSource(Source relativeSource) {
		this.relativeSource = relativeSource;
	}

	public Map<String, String> getProperties() {
		Map<String, String> props = new HashMap<String, String>();
		if ( this.stylesheetLocation != null ) {
			props.put( "stylesheet", this.stylesheetLocation );
		}
		return props;
	}

	/**
	 * Transforms the XML document in the InputStream using the XSL
	 * stylesheet in this filter.
	 * 
	 * @param input the InputStream containing the XML document to be
	 * 	transformed.
	 * @return InputStream containing the transformed document
	 * @throws FilterException if the InputStream doesn't contain a valid
	 * 	XML document or if there's a problem during the transformation.
	 */
	public InputStream filter(InputStream input) throws FilterException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
		        .newInstance();
		domFactory.setNamespaceAware( false );
		DocumentBuilder builder;
		try {
			builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse( input );

			ByteArrayOutputStream output = new ByteArrayOutputStream( 1024 );
			this.transformer.transform( new DOMSource( doc ), new StreamResult(
			        output ) );
			return new ByteArrayInputStream( output.toByteArray() );
		}
		catch ( ParserConfigurationException e ) {
			throw new FilterException( e );
		}
		catch ( IOException e ) {
			// if the input stream isn't good XML
			throw new FilterException( e );
		}
		catch ( SAXException e ) {
			// if the input stream isn't good XML
			throw new FilterException( e );
		}
		catch ( TransformerException e ) {
			// if the transformation failed
			throw new FilterException( e );
		}
	}
}
