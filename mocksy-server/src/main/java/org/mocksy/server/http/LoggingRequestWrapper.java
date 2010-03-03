/**
 * 
 */
package org.mocksy.server.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

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
 * HttpServletRequest wrapper that captures the request data to be logged later.
 * 
 * @author Saleem Shafi
 */
class LoggingRequestWrapper extends HttpServletRequestWrapper {
	private LoggingInputStream input;

	/**
	 * Creates the wrapper for the given servlet request.
	 * 
	 * @param request
	 * @throws IOException
	 */
	public LoggingRequestWrapper(HttpServletRequest request) throws IOException
	{
		super( request );
		String fullUrl = request.getRequestURL().append( "?" ).append(
		        request.getQueryString() ).toString();
		// create the wrapper ServletInputStream
		this.input = new LoggingInputStream( fullUrl, request.getInputStream() );
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return this.input;
	}

	/**
	 * Returns the data sent in on the request.
	 * 
	 * @return the request data
	 */
	public byte[] getRequestData() {
		return this.input.getData();
	}

	/**
	 * Wrapper ServletInputStream that records all of the data that
	 * passes through the wrapped stream.
	 * 
	 * @author Saleem Shafi
	 */
	class LoggingInputStream extends ServletInputStream {
		private ServletInputStream rootStream;
		private ByteArrayOutputStream data;

		public LoggingInputStream(String url, ServletInputStream is)
		        throws IOException
		{
			this.rootStream = is;
			this.data = new ByteArrayOutputStream();
			this.data.write( url.getBytes() );
		}

		@Override
		public int read() throws IOException {
			int ch = this.rootStream.read();
			if ( ch != -1 ) {
				this.data.write( ch );
			}
			return ch;
		}

		@Override
		public int read(byte b[], int off, int len) throws IOException {
			if ( b == null ) {
				throw new NullPointerException();
			}
			int bytesRead = this.rootStream.read( b, off, len );
			if ( bytesRead > 0 ) {
				this.data.write( b, off, bytesRead );
			}
			return bytesRead;
		}

		@Override
		public int available() throws IOException {
			return this.rootStream.available();
		}

		@Override
		public boolean markSupported() {
			return false;
		}

		@Override
		public synchronized void reset() throws IOException {
			throw new IOException( "mark/reset not supported" );
		}

		public byte[] getData() {
			return this.data.toByteArray();
		}
	}

}