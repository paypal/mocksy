package org.mocksy.rules.http;

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

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.mocksy.Request;
import org.mocksy.Response;
import org.mocksy.rules.Matcher;
import org.mocksy.rules.Rule;
import org.mocksy.server.http.HttpRequest;
import org.mocksy.server.http.HttpResponse;

public class HttpProxyRule implements Rule {
	private Collection<Matcher> matchers = new ArrayList<Matcher>();
	private String proxyUrl;

	public HttpProxyRule(String proxyUrl) {
		this.proxyUrl = proxyUrl;
		this.clear();
	}

	public void addMatcher(Matcher matcher) {
		this.matchers.add( matcher );
	}

	public boolean matches(Request request) {
		if ( this.matchers.isEmpty() ) return false;
		for ( Matcher matcher : this.matchers ) {
			if ( !matcher.matches( request ) ) return false;
		}
		return true;
	}

	public void clear() {
		this.matchers.clear();
	}

	public String getProxyUrl() {
		return this.proxyUrl;
	}

	public Response process(Request request) throws Exception {
		if ( !( request instanceof HttpRequest ) ) {
			throw new IllegalArgumentException(
			        "ProxyRule only works for HttpRequests" );
		}
		HttpRequest httpRequest = (HttpRequest) request;
		HttpClient httpClient = new DefaultHttpClient();
		HttpRequestBase method = this.getProxyMethod( httpRequest
		        .getServletRequest() );
		// httpRequest.getServletRequest();
		HttpResponse response = null;
		try {
			org.apache.http.HttpResponse httpResp = httpClient.execute( method );

			response = new HttpResponse( "proxied response", httpResp
			        .getEntity().getContent() );
			response.setStatusCode( httpResp.getStatusLine().getStatusCode() );

			// Pass response headers back to the client
			Header[] headerArrayResponse = httpResp.getAllHeaders();
			for ( Header header : headerArrayResponse ) {
				response.setHeader( header.getName(), header.getValue() );
			}
		}
		catch ( ConnectException e ) {
			response = new HttpResponse( "connection failed",
			        "Proxied server at " + method.getURI() + " is unavailable." );
			response.setStatusCode( 503 );
		}

		return response;
	}

	public Collection<Matcher> getMatchers() {
		return this.matchers;
	}

	protected HttpRequestBase getProxyMethod(HttpServletRequest request) {
		String proxyUrl = this.proxyUrl;
		proxyUrl += request.getPathInfo();
		if ( request.getQueryString() != null ) {
			proxyUrl += "?" + request.getQueryString();
		}
		HttpRequestBase method = null;
		if ( "GET".equals( request.getMethod() ) ) {
			method = new HttpGet( proxyUrl );
			method.addHeader( "Cache-Control", "no-cache" );
			method.addHeader( "Pragma", "no-cache" );
		}
		else if ( "POST".equals( request.getMethod() ) ) {
			method = new HttpPost( proxyUrl );

			Map<String, String[]> paramMap = request.getParameterMap();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for ( String paramName : paramMap.keySet() ) {
				String[] values = paramMap.get( paramName );
				for ( String value : values ) {
					NameValuePair param = new BasicNameValuePair( paramName,
					        value );
					params.add( param );
				}
			}

			try {
				( (HttpPost) method ).setEntity( new UrlEncodedFormEntity(
				        params ) );
			}
			catch ( UnsupportedEncodingException e ) {
				// don't worry, this won't happen
			}
		}

		Enumeration headers = request.getHeaderNames();
		while ( headers.hasMoreElements() ) {
			String header = (String) headers.nextElement();
			if ( "If-Modified-Since".equals( header )
			        || "Content-Length".equals( header )
			        || "Transfer-Encoding".equals( header ) ) continue;
			Enumeration values = request.getHeaders( header );
			while ( values.hasMoreElements() ) {
				String value = (String) values.nextElement();
				method.addHeader( header, value );
			}
		}

		return method;
	}
}
