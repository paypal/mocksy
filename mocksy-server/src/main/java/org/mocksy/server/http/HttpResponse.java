package org.mocksy.server.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mocksy.RawResponse;
import org.mocksy.filter.ResponseFilter;

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

public class HttpResponse extends RawResponse {
	private int statusCode = 200;
	private Map<String, String> headers = new HashMap<String, String>();

	public HttpResponse(String id, InputStream content,
	        List<ResponseFilter> filters)
	{
		super( id, content, filters );
	}

	public HttpResponse(String id, InputStream content) {
		super( id, content );
	}

	public HttpResponse(String id, String content) {
		super( id, content );
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getHeader(String name) {
		return this.headers.get( name );
	}

	public void setHeader(String headerString) {
		if (headerString == null) throw new IllegalArgumentException("setHeader(String) parameter cannot be null");
		int colon = headerString.indexOf( ':' );
		if (colon == -1) throw new IllegalArgumentException("setHeader(String) must contain a ':' character, or use the setHeader(String, String) variant.");
		String headerName = headerString.substring( 0, colon ).trim();
		String headerValue = headerString.substring( colon+1 ).trim();
		this.setHeader(headerName, headerValue);
	}
	
	public void setHeader(String name, String value) {
		if ( "Content-Type".equals( name ) ) {
			this.setContentType( value );
		}
		else {
			this.headers.put( name, value );
		}
	}

	public Set<String> getHeaderNames() {
		return this.headers.keySet();
	}
	// TODO need to override/move contentType accessor/mutators to relate to
	// header value
}
