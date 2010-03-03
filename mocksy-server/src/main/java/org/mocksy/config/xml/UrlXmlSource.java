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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mocksy.config.Source;
import org.w3c.dom.Element;

/**
 * URL-based XML source used to load configuration for Rulesets.
 * 
 * @author Saleem Shafi
 */
public class UrlXmlSource extends XmlSource {
	private static final Logger logger = Logger.getLogger( UrlXmlSource.class
	        .getName() );
	private URL url;
	private URL parentUrl;

	/**
	 * Create a UrlXmlSource for the given URL.
	 * 
	 * @param url the URL of the XML source
	 */
	public UrlXmlSource(URL url) {
		this.url = url;
		this.parentUrl = getParentUrl( this.url );
	}

	public Source getRelativeSource(String relativePath) {
		try {
			return new UrlXmlSource( this.getRelativeURL( relativePath ) );
		}
		catch ( MalformedURLException e ) {
			throw new IllegalArgumentException( e );
		}
	}

	public URL getRelativeURL(String relativePath) throws MalformedURLException
	{
		return new URL( this.parentUrl, relativePath );
	}

	@Override
	public Element getRootElement() throws Exception {
		return getRootElement( this.url.openStream() );
	}

	/**
	 * This URL-based Source does not know how to check for updates,
	 * so this will always return false.
	 */
	public boolean needsUpdate() {
		// Might want to check for updates periodically
		return false;
	}

	/**
	 * Returns the URL that represents one-level up from the given URL.
	 * 
	 * @param url URL to get the parent for
	 * @return parent URL
	 */
	private static URL getParentUrl(URL url) {
		String path = url.getPath();
		if ( path == null || path.length() == 0 || path.equals( "/" ) ) {
			return null;
		}
		String fullUrl = url.toString();
		int index = fullUrl.lastIndexOf( "/" );
		if ( index > 0 ) {
			try {
				return new URL( fullUrl.substring( 0, index + 1 ) );
			}
			catch ( MalformedURLException e ) {
				logger.log( Level.WARNING,
				        "Seriously unexpected error processing parent URL", e );
			}
		}
		return null;
	}

}
