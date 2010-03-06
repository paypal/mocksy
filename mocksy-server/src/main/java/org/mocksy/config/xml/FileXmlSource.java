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
import java.net.MalformedURLException;
import java.net.URL;
import org.mocksy.config.Source;
import org.w3c.dom.Element;

/**
 * File-based XmlSource used to load configuration for Rulesets.
 *  
 * @author Saleem Shafi
 */
public class FileXmlSource extends XmlSource {
	private final File xmlFile;
	private final File xmlDir;
	private long timestamp;

	/**
	 * Creates the File-based XmlSource for the given File.
	 * 
	 * @param file the File with the XML in it
	 */
	public FileXmlSource(File file) {
		if ( !file.exists() ) {
			throw new IllegalArgumentException( file.getAbsolutePath()
			        + " does not exist." );
		}
		this.xmlFile = file;
		this.xmlDir = this.xmlFile.getParentFile();
		this.timestamp = -1L;
	}

	public Source getRelativeSource(String relativePath) {
		return new FileXmlSource( new File( this.xmlDir, relativePath ) );
	}

	public URL getRelativeURL(String relativePath) throws MalformedURLException
	{
		return new File( this.xmlDir, relativePath ).toURI().toURL();
	}

	/**
	 * Returns whether or not the underlying File for this Source has changed.
	 * 
	 * @return true, if the underlying File has changed
	 */
	public boolean needsUpdate() {
		// Returns 0 if file does not exist;
		long current = this.xmlFile.lastModified();
		if ( this.timestamp == current || this.timestamp == 0L ) {
			return false;
		}

		this.timestamp = current;
		// Wipe out old data
		return true;
	}

	@Override
	public Element getRulesetElement() throws Exception {
		return getRulesetElement( this.xmlFile.toURI().toURL().openStream() );
	}

}
