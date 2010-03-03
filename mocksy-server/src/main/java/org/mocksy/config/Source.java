package org.mocksy.config;

import java.net.MalformedURLException;
import java.net.URL;

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

/**
 * Abstraction of the source of a ruleset, rule, response file or filter
 * components.
 */
public interface Source {

	/**
	 * Returns a new Source object relative to the current one.
	 * 
	 * @param relativePath path to desired Source
	 * @return new Source object relative to the current
	 */
	Source getRelativeSource(String relativePath);

	/**
	 * Returns a URL reference to a resource relative to the current Source.
	 * 
	 * @param relativePath path to desired resource
	 * @return URL reference to resource
	 * @throws MalformedURLException if the relativePath results in an invalid URL
	 */
	URL getRelativeURL(String relativePath) throws MalformedURLException;

	/**
	 * Returns whether or not this source has changed recently. 
	 * @return whether or not the source has changed
	 */
	boolean needsUpdate();

}
