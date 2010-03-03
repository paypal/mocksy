/**
 * 
 */
package org.mocksy.server.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

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
 * HttpServletResponse wrapper that captures the response data to log later.
 * 
 * @author Saleem Shafi
 */
class LoggingResponseWrapper extends HttpServletResponseWrapper {
	private LoggingOutputStream output;

	/**
	 * Creates the wrapper for the given servlet response.
	 * 
	 * @param request
	 * @throws IOException
	 */
	public LoggingResponseWrapper(HttpServletResponse response)
	        throws IOException
	{
		super( response );
		this.output = new LoggingOutputStream( response.getOutputStream() );
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.output;
	}

	/**
	 * Returns the data sent in on the response.
	 * 
	 * @return the request data
	 */
	public byte[] getResponseData() {
		return this.output.getData();
	}

	/**
	 * Wrapper ServletOutputStream that records all of the data that
	 * passes through the wrapped stream.
	 * 
	 * @author Saleem Shafi
	 */
	class LoggingOutputStream extends ServletOutputStream {
		private ServletOutputStream outputStream;
		private ByteArrayOutputStream data;

		public LoggingOutputStream(ServletOutputStream os) {
			this.outputStream = os;
			this.data = new ByteArrayOutputStream();
		}

		@Override
		public void write(int b) throws IOException {
			this.data.write( b );
			this.outputStream.write( b );
		}

		public byte[] getData() {
			return this.data.toByteArray();
		}
	}
}