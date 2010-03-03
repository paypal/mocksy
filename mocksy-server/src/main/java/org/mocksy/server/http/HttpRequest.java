package org.mocksy.server.http;

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

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import org.mocksy.Request;

/**
 * Mocksy Request specialized for HTTP requests.
 * 
 * @author Saleem Shafi
 */
public class HttpRequest implements Request {
	private HttpServletRequest request;

	/**
	 * Creates the Request instance wrapping the given HttpServletRequest.
	 * 
	 * @param request the HttpServletRequest that this Mocksy Request represents
	 */
	public HttpRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Returns the body of the HTTP request as the Request data.
	 * 
	 * @return the body of the HTTP request
	 */
	public InputStream getData() throws IOException {
		return this.request.getInputStream();
	}

	/**
	 * Returns the HttpServletRequest that this Request represents
	 * 
	 * @return the wrapped HttpServletRequest
	 */
	public HttpServletRequest getServletRequest() {
		return this.request;
	}

	/**
	 * Returns the header value for the given name.
	 * 
	 * @param headerName the name of the HTTP header
	 * @return the value of the HTTP header
	 */
	public String getHeader(String headerName) {
		return this.request.getHeader( headerName );
	}

	/**
	 * Returns the parameter values for the given HTTP parameter
	 * 
	 * @param param the HTTP parameter name
	 * @return the parameter value(s)
	 */
	public String[] getParamValues(String param) {
		return this.request.getParameterValues( param );
	}

	/**
	 * Returns the full URL for this Request.
	 * 
	 * @return the full URL
	 */
	public String getFullURL() {
		String queryStr = this.request.getQueryString();
		String fullQS = this.request.getRequestURI()
		        + ( queryStr != null ? "?" + queryStr : "" );
		return fullQS;
	}
}
