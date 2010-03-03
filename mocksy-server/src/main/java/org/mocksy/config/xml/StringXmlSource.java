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

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.mocksy.config.Source;
import org.w3c.dom.Element;

/**
 * Simple XmlSource that is created with a String.
 *  
 * @author Saleem Shafi
 */
public class StringXmlSource extends XmlSource {
	private final String xml;
	private Element root;

	/**
	 * Creates an XmlSource from the given XML String.
	 * 
	 * @param xml the XML as a String
	 */
	public StringXmlSource(String xml) {
		this.xml = xml;
		this.root = null;
	}

	public URL getRelativeURL(String relativePath) throws MalformedURLException
	{
		return new URL( relativePath );
	}

	/**
	 * This Source is based on a constant value, so we never need to update it.
	 * 
	 * @return false
	 */
	public boolean needsUpdate() {
		return false;
	}

	@Override
	public Element getRootElement() throws Exception {
		if ( this.root == null ) {
			this.root = getRootElement( new ByteArrayInputStream( this.xml
			        .getBytes() ) );
		}
		return this.root;
	}

	public Source getRelativeSource(String relativePath) {
		return this;
	}

}
