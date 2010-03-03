package org.mocksy;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mocksy.filter.FilterException;
import org.mocksy.filter.ResponseFilter;

/**
 * Basic abstraction for a service response.
 */
public class Response {
	private static final Logger logger = Logger.getLogger( Response.class
	        .getName() );
	private String id;
	private InputStream stream;
	private byte[] data;
	private List<ResponseFilter> filters;
	private String contentType;
	private int delay;

	/**
	 * Creates a response with given content and content type.
	 * 
	 * @param id Identifier for the response
	 * @param content the response content as a String
	 * @param contentType the MIME type of the content
	 */
	public Response(String id, String content, String contentType) {
		this( id, new ByteArrayInputStream( content.getBytes() ), contentType );
	}

	/**
	 * Creates a response with given content and content type.
	 * 
	 * @param id Identifier for the response
	 * @param content the response content as an InputStream
	 * @param contentType the MIME type of the content
	 */
	public Response(String id, InputStream content, String contentType) {
		this( id, content, contentType, Collections.EMPTY_LIST );
	}

	/**
	 * Creates a response with given content, content type, and a list
	 * of filters.
	 * 
	 * @param id Identifier for the response
	 * @param content the response content as an InputStream
	 * @param contentType the MIME type of the content
	 * @param filters List of ResponseFilters
	 */
	public Response(String id, InputStream content, String contentType,
	        List<ResponseFilter> filters)
	{
		// TODO throw IllegalArgumentException if any of these are null
		this.id = id;
		this.stream = content;
		this.contentType = contentType;
		this.filters = filters;
	}

	/**
	 * Returns the list of ResponseFilters configured for this response.
	 * 
	 * @return List of ResponseFilters
	 */
	public List<ResponseFilter> getFilters() {
		return this.filters;
	}

	/**
	 * Returns the response contents as a byte[].
	 * 
	 * @return response contents as a byte array
	 * @throws FilterException if there's a problem filtering the response
	 * @throws IOException if there's a problem producing the content
	 */
	public byte[] toByteArray() throws FilterException, IOException {
		return this.toByteArray( true );
	}

	/**
	 * Returns the response contents as a byte[].
	 * 
	 * @param filter whether or not to filter the response data
	 * @return response contents as a byte array
	 * @throws FilterException if there's a problem filtering the response
	 * @throws IOException if there's a problem producing the content
	 */
	public byte[] toByteArray(boolean filter) throws FilterException,
	        IOException
	{
		byte[] data = this.getData();
		if ( !filter ) {
			return data;
		}
		// convert response contents to an InputStream
		InputStream stream = null;
		ByteArrayOutputStream responseData = new ByteArrayOutputStream();
		try {
			// process through the filters
			stream = this.getFilteredStream( new ByteArrayInputStream( data ) );
			while ( stream.available() > 0 ) {
				byte[] bytes = new byte[stream.available()];
				int numBytes = stream.read( bytes );
				responseData.write( bytes, 0, numBytes );
			}
		}
		finally {
			if ( stream != null ) {
				stream.close();
			}
		}
		return responseData.toByteArray();
	}

	/**
	 * Returns the unfiltered response contents as a String.
	 *
	 * @return response contents
	 */
	@Override
	public String toString() {
		return this.toString( false );
	}

	/**
	 * Returns the response contents as a String.
	 *
	 * @param filter whether or not to filter the response data
	 * @return response contents
	 */
	public String toString(boolean filter) {
		try {
			return new String( this.toByteArray( filter ) );
		}
		catch ( IOException e ) {
			logger.log( Level.SEVERE, "Error getting response data", e );
			return e.getMessage();
		}
		catch ( FilterException e ) {
			logger.log( Level.SEVERE, "Error getting response data", e );
			return e.getMessage();
		}
	}

	/**
	 * Returns the MIME content type for this response.
	 * 
	 * @return MIME content type
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Returns the ID of the response.
	 * 
	 * @return ID of the response
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the amount of delay in the response as milliseconds.
	 * 
	 * @return milliseconds of delay in response
	 */
	public int getDelay() {
		return this.delay;
	}

	/**
	 * Sets the amount of time to delay the processing of the response.
	 * The argument provided defines the time in milliseconds for the 
	 * mock server to wait before returning the response.  This makes it
	 * easier to simulates real server processing a little better.
	 * 
	 * @param delay time of delay in milliseconds
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Processing the contents of the response and returns it as a byte
	 * array.  If the response was created with an InputStream, this will
	 * read the data and hold on to the bytes.  
	 * 
	 * @return the byte[] of response contents
	 */
	private synchronized byte[] getData() {
		if ( this.data == null && this.stream != null ) {
			try {
				this.data = new byte[this.stream.available()];
				this.stream.read( this.data );
			}
			catch ( IOException e ) {
				logger.log( Level.SEVERE, "Error reading response stream", e );
			}
			finally {
				this.stream = null;
			}
		}
		return this.data;
	}

	/**
	 * Returns an InputStream that flows through the response's list
	 * of filters.
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
