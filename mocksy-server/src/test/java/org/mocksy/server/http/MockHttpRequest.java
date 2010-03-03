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
import java.util.HashMap;
import java.util.Map;

public class MockHttpRequest extends HttpRequest {
	private Map<String, String> headers = new HashMap<String, String>();
	private String url;
	private Map<String, String[]> parameters = new HashMap<String, String[]>();
	private InputStream data;

	public MockHttpRequest(String url) {
		super( null );
		this.url = url;
	}

	public void addHeader(String header, String value) {
		this.headers.put( header, value );
	}

	@Override
	public String getHeader(String headerName) {
		return this.headers.get( headerName );
	}

	public void addParameter(String paramName, String[] values) {
		this.parameters.put( paramName, values );
	}

	@Override
	public String[] getParamValues(String param) {
		return this.parameters.get( param );
	}

	@Override
	public String getFullURL() {
		return this.url;
	}

	@Override
	public InputStream getData() {
		return this.data;
	}

	public void setData(InputStream data) {
		this.data = data;
	}

}
