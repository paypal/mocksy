package org.mocksy.rules;

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

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.mocksy.Request;
import org.mocksy.Response;
import org.mocksy.server.http.HttpRequest;
import org.mocksy.server.http.HttpResponse;

public class HttpProxyRule implements Rule {
	private Collection<Matcher> matchers = new ArrayList<Matcher>();
	private String proxyHost;
	private int proxyPort;

	public HttpProxyRule(String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.clear();
	}

	public void addMatcher(Matcher matcher) {
		this.matchers.add( matcher );
	}

	public boolean matches(Request request) throws Exception {
		if ( this.matchers.isEmpty() ) return false;
		for ( Matcher matcher : this.matchers ) {
			if ( !matcher.matches( request ) ) return false;
		}
		return true;
	}

	public void clear() {
		this.matchers.clear();
	}

	public Response process(Request request) throws Exception {
		if ( !( request instanceof HttpRequest ) ) {
			throw new IllegalArgumentException(
			        "ProxyRule only works for HttpRequests" );
		}
		HttpRequest httpRequest = (HttpRequest) request;
		HttpClient httpClient = new HttpClient();
		HttpMethod method = this.getProxyMethod( httpRequest
		        .getServletRequest() );
		// httpRequest.getServletRequest();
		HttpResponse response = null;
		try {
			int responseCode = httpClient.executeMethod( method );

			response = new HttpResponse( "proxied response", method
			        .getResponseBodyAsStream() );
			response.setStatusCode( responseCode );

			// Pass response headers back to the client
			Header[] headerArrayResponse = method.getResponseHeaders();
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

	protected HttpMethod getProxyMethod(HttpServletRequest request) {
		String proxyUrl = "http://" + this.proxyHost + ":" + this.proxyPort;
		proxyUrl += request.getPathInfo();
		if ( request.getQueryString() != null ) {
			proxyUrl += "?" + request.getQueryString();
		}
		HttpMethod method = null;
		if ( "GET".equals( request.getMethod() ) ) {
			method = new GetMethod( proxyUrl );
			method.setFollowRedirects( true );
			method.setRequestHeader( "Cache-Control", "no-cache" );
			method.setRequestHeader( "Pragma", "no-cache" );
		}
		else if ( "POST".equals( request.getMethod() ) ) {
			method = new PostMethod( proxyUrl );

			Map<String, String[]> paramMap = request.getParameterMap();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for ( String paramName : paramMap.keySet() ) {
				String[] values = paramMap.get( paramName );
				for ( String value : values ) {
					NameValuePair param = new NameValuePair( paramName, value );
					params.add( param );
				}
			}
			( (PostMethod) method ).setRequestBody( params
			        .toArray( new NameValuePair[] {} ) );
		}

		Enumeration headers = request.getHeaderNames();
		while ( headers.hasMoreElements() ) {
			String header = (String) headers.nextElement();
			if ( "If-Modified-Since".equals( header ) ) continue;
			Enumeration values = request.getHeaders( header );
			while ( values.hasMoreElements() ) {
				String value = (String) values.nextElement();
				method.setRequestHeader( new Header( header, value ) );
			}
		}

		return method;
	}
}
