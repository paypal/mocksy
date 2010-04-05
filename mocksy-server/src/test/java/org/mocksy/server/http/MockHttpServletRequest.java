package org.mocksy.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

public class MockHttpServletRequest implements HttpServletRequest {
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String[]> parameters = new HashMap<String, String[]>();
	private URL url;
	private String method = "GET";

	public MockHttpServletRequest(String urlString)
	        throws MalformedURLException
	{
		this.url = new URL( urlString );
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		return Long.parseLong( this.getHeader( name ) );
	}

	public void addHeader(String name, String value) {
		this.headers.put( name, value );
	}

	@Override
	public String getHeader(String name) {
		return this.headers.get( name );
	}

	@Override
	public Enumeration getHeaderNames() {
		return Collections.enumeration( this.headers.keySet() );
	}

	@Override
	public Enumeration getHeaders(String name) {
		Collection<String> values = new ArrayList<String>();
		String value = this.getHeader( name );
		if ( value != null ) {
			values.add( value );
		}
		return Collections.enumeration( values );
	}

	@Override
	public int getIntHeader(String name) {
		return Integer.parseInt( this.getHeader( name ) );
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getPathInfo() {
		return this.url.getPath();
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		return this.url.getQuery();
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		int port = this.url.getPort();
		return this.url.getProtocol() + "://" + this.url.getHost()
		        + ( port == -1 || port == 80 || port == 443 ? "" : ":" + port )
		        + this.url.getPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer( this.url.toString() );
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addParameter(String name, String[] values) {
		// TODO make this a little smarter so that subsequent calls
		// just add to the array
		this.parameters.put( name, values );
	}

	@Override
	public String getParameter(String name) {
		String[] params = this.getParameterValues( name );
		if ( params != null && params.length > 0 ) {
			return params[0];
		}
		return null;
	}

	@Override
	public Map getParameterMap() {
		return this.parameters;
	}

	@Override
	public Enumeration getParameterNames() {
		return Collections.enumeration( this.parameters.keySet() );
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.parameters.get( name );
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String name) {
	// TODO Auto-generated method stub

	}

	@Override
	public void setAttribute(String name, Object o) {
	// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterEncoding(String env)
	        throws UnsupportedEncodingException
	{
	// TODO Auto-generated method stub

	}

}
