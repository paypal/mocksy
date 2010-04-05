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

import java.io.InputStream;
import java.net.MalformedURLException;

public class MockHttpRequest extends HttpRequest {
	private MockHttpServletRequest servletRequest;
	private InputStream data;

	public MockHttpRequest(String url) throws MalformedURLException {
		super( new MockHttpServletRequest( url ) );
		this.servletRequest = (MockHttpServletRequest) this.getServletRequest();
	}

	public void addHeader(String header, String value) {
		this.servletRequest.addHeader( header, value );
	}

	public void addParameter(String paramName, String[] values) {
		this.servletRequest.addParameter( paramName, values );
		this.servletRequest.setMethod( "POST" );
	}

	@Override
	public InputStream getData() {
		return this.data;
	}

	public void setData(InputStream data) {
		this.data = data;
		this.servletRequest.setMethod( "POST" );
	}

}
