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

import java.io.InputStream;
import java.util.Map;
import org.mocksy.config.Source;

/**
 * ResponseFilters allow you to augment the response data that is produced 
 * from a Request.  The interface is flexible enough to allow to do to just
 * about anything, but the intent is that ResponseFilters are used to add
 * a dynamic nature to the otherwise static Responses, or to transform
 * shared Responses for a particular scenario.
 * 
 * @author Saleem Shafi
 */
public interface ResponseFilter {
	/**
	 * Applies the filtering logic to the InputStream and returns a filtered
	 * InputStream.  Because the input and output types are the same, it is
	 * possible to chain multiple ResponseFilters together.
	 * 
	 * @param input unfiltered InputStream
	 * @return filtered InputStream
	 * @throws FilterException if there's any error in applying the filter
	 */
	InputStream filter(InputStream input) throws FilterException;

	/**
	 * Initialization method that accepts a generic Map of String pairs
	 * to provide any settings/parameters that might be needed to make the
	 * filter work properly.
	 * 
	 * @param properties Map of properties to be used to configure the filter
	 * @throws FilterException if there's a problem with any of the properties
	 */
	void initialize(Map<String, String> properties) throws FilterException;

	/**
	 * Allows the code that is configuring the filter to define a relative
	 * Source object so that if the ResponseFilter needs to find resources
	 * for its filtering, it can do so relative to this Source object.
	 * 
	 * @param source the Source object to treat as the relative location for
	 * 			any needed resources.
	 */
	void setRelativeSource(Source source);

	/**
	 * Returns the properties used to process this filter.  This will usually
	 * be the same as the ones passed into the {@link #initialize(Map)} method,
	 * however, the ResponseFilter implementation might want to modify these. 
	 * 
	 * @return the properties used by this filter
	 */
	Map<String, String> getProperties();

	/**
	 * Returns the new MIME type of the resulting InputStream after calling
	 * {@link #filter(InputStream)}.  This could be particularly useful in cases
	 * where the content is being transformed from one format to another.
	 * 
	 * In cases where the format is not changing, the return value should be null.
	 * 
	 * @return the MIME type of the filtered stream, or null if the filter is 
	 * 		not changing the content type
	 */
	String getNewContentType();
}
