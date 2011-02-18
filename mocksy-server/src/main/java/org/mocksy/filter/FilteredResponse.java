package org.mocksy.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mocksy.Response;
import org.mocksy.util.StreamData;

/*
 * Copyright 2011, PayPal
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

public class FilteredResponse implements Response {
	private static final Logger logger = Logger.getLogger( FilteredResponse.class
	        .getName() );
	private static final int BUFFER_SIZE = 1024 * 10;
	private Response baseResponse;
	private List<ResponseFilter> filters;
	private String newContentType;

	/**
	 * Creates a filtered response based on the given {@link Response} object.
	 * 
	 * @param response the base {@link Response} to filter
	 */
	public FilteredResponse(Response response) {
		if (response == null) {
			throw new IllegalArgumentException("Response to be filtered cannot be null.");	
		}
		this.baseResponse = response;
		this.filters = new LinkedList<ResponseFilter>();
	}
	
	/**
	 * Creates a filtered response based on the given {@link Response} object
	 * along with an initial set of {@link ResponseFilter}s.  The filters
	 * will be applied in the order they are provided, so the last one in the
	 * list will be the last one applied.
	 * 
	 * @param response the base {@link Response} to filter
	 * @param filters the initial list of {@link ResponseFilter}s to apply
	 */
	public FilteredResponse(Response response, ResponseFilter...filters) {
		this(response);
		for (ResponseFilter filter : filters) {
			this.addFilter( filter );
		}
	}
	
	@Override
    public String getContentType() {
		if (this.newContentType == null) {
			return this.baseResponse.getContentType();
		}
		return this.newContentType;
    }

	@Override
    public String getId() {
	    return "filtered-"+this.baseResponse.getId();
    }

	/**
	 * Adds a new filter to the end of the list.  Filters are applied 
	 * in the order they are added, so the last filter added will be the last
	 * filter applied.
	 * 
	 * @param filter the ResponseFilter to add to the end of the list.
	 */
	public void addFilter(ResponseFilter filter) {
		this.filters.add( filter );
		if (filter.getNewContentType() != null) {
			this.newContentType = filter.getNewContentType();
		}
	}
	
	/**
	 * Returns the list of filters that are to be applied as part of this
	 * response.
	 * 
	 * @return an unmodifiable list of the {@link ResponseFilter}s on this response. 
	 */
	public List<ResponseFilter> getFilters() {
		return Collections.unmodifiableList( this.filters );
	}
	
	@Override
	/**
	 * Returns the response contents as a byte[] after applying the filters.
	 * 
	 * @return response contents as a byte array
	 * @throws IOException if there's a problem producing the content
	 * @throws  \--FilterException if there's a problem filtering the response
	 */
    public byte[] toByteArray() throws IOException {
		byte[] data = this.baseResponse.toByteArray();
		
		// convert response contents to an InputStream
		InputStream stream = null;
		try {
			// process through the filters
			stream = this.getFilteredStream( new ByteArrayInputStream( data ) );
			byte[] filteredData = StreamData.getBytesFromStream( stream, BUFFER_SIZE );
			return filteredData;
		}
		finally {
			if ( stream != null ) {
				stream.close();
			}
		}
    }
	
	@Override
    public FilteredResponse clone() {
		FilteredResponse clone = new FilteredResponse(this.baseResponse);
		clone.newContentType = this.newContentType;
		clone.filters.addAll( this.filters );
		return clone;
	}

	/**
	 * Returns the filtered response contents as a String.
	 *
	 * @return response contents
	 */
	@Override
	public String toString() {
		try {
			return new String( this.toByteArray( ) );
		}
		catch ( IOException e ) {
			logger.log( Level.SEVERE, "Error getting response data", e );
			return e.getMessage();
		}
	}

	/**
	 * Returns an InputStream that flows through the list of filters.
	 * 
	 * @param stream the InputStream to filter 
	 * @return the filtered InputStream 
	 * @throws FilterException if there is a problem filtering the response data
	 */
	private InputStream getFilteredStream(InputStream stream)
	        throws FilterException
	{
		InputStream filteredStream = stream;
		// this seems like it's happening in the reverse order
		for ( ResponseFilter filter : this.filters ) {
			filteredStream = filter.filter( filteredStream );
		}
		return filteredStream;
	}
}
