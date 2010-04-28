package org.mocksy.rules;

import java.util.regex.Pattern;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mocksy.Request;
import org.mocksy.Response;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.rules.http.HttpProxyRule;
import org.mocksy.server.http.HttpResponse;
import org.mocksy.server.http.MockHttpRequest;
import org.mocksy.server.http.MocksyServer;

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

public class ProxyTest {
	private MocksyServer proxiedServer;

	@Before
	public void setUp() throws Exception {
		Ruleset rules = new Ruleset();

		HttpResponse headerResponse = new HttpResponse( "header",
		        "custom header response" );
		headerResponse.setContentType( "text/plain" );
		headerResponse.setHeader( "x-mocksy-custom", "custom value" );
		ResponseRule headerRule = new ResponseRule( headerResponse );
		// match the URL
		HttpMatcher headerMatcher = new HttpMatcher();
		headerMatcher.setPattern( Pattern.compile( ".*/custom_header.*" ) );
		headerRule.addMatcher( headerMatcher );
		// and match the header value so we can test to make sure the proxy
		// is proxying the headers
		headerMatcher = new HttpMatcher();
		headerMatcher.setPattern( Pattern.compile( "test" ) );
		headerMatcher.setHeader( "x-mocksy-custom" );
		headerRule.addMatcher( headerMatcher );
		rules.addRule( headerRule );

		HttpResponse statusResponse = new HttpResponse( "error",
		        "error code response" );
		statusResponse.setContentType( "text/plain" );
		statusResponse.setStatusCode( 403 );
		ResponseRule statusRule = new ResponseRule( statusResponse );
		// match the parameter, rather than the URL so we can set for
		// proxying the POST parameters
		HttpMatcher statusMatcher = new HttpMatcher();
		statusMatcher.setPattern( Pattern.compile( "true" ) );
		statusMatcher.setParam( "error" );
		statusRule.addMatcher( statusMatcher );
		rules.addRule( statusRule );

		HttpResponse simpleResponse = new HttpResponse( "simple",
		        "simple response" );
		simpleResponse.setContentType( "text/plain" );
		rules.setDefaultRule( new ResponseRule( simpleResponse ) );
		this.proxiedServer = new MocksyServer( rules, 11112 );
		this.proxiedServer.start();
	}

	@After
	public void tearDown() throws Exception {
		this.proxiedServer.stop();
	}

	@Test
	public void testProxyNoConnection() throws Exception {
		HttpProxyRule proxyRule = new HttpProxyRule( "http://127.0.0.1:11111" );
		HttpMatcher matcher = new HttpMatcher();
		matcher
		        .setPattern( Pattern
		                .compile( "http://test.mocksy.org/proxy/.*" ) );
		proxyRule.addMatcher( matcher );
		Ruleset ruleset = new Ruleset();
		ruleset.addRule( proxyRule );

		Request request = new MockHttpRequest(
		        "http://test.mocksy.org/proxy/no_connection?error=true" );
		Response response = ruleset.process( request );
		Assert.assertEquals( 503, ( (HttpResponse) response ).getStatusCode() );
	}

	@Test
	public void testProxySuccess() throws Exception {
		HttpProxyRule proxyRule = new HttpProxyRule( "http://127.0.0.1:11112" );
		HttpMatcher matcher = new HttpMatcher();
		matcher
		        .setPattern( Pattern
		                .compile( "http://test.mocksy.org/proxy/.*" ) );
		proxyRule.addMatcher( matcher );
		Ruleset ruleset = new Ruleset();
		ruleset.addRule( proxyRule );

		Request request = new MockHttpRequest(
		        "http://test.mocksy.org/proxy/success?error=false" );
		Response response = ruleset.process( request );
		Assert.assertEquals( "simple response", response.toString() );
		Assert.assertEquals( "text/plain", response.getContentType() );
	}

	// Tests proxying of POST parameters and error code
	@Test
	public void testProxyWithHttpErrorCode() throws Exception {
		HttpProxyRule proxyRule = new HttpProxyRule( "http://127.0.0.1:11112" );
		HttpMatcher matcher = new HttpMatcher();
		matcher
		        .setPattern( Pattern
		                .compile( "http://test.mocksy.org/proxy/.*" ) );
		proxyRule.addMatcher( matcher );
		Ruleset ruleset = new Ruleset();
		ruleset.addRule( proxyRule );

		MockHttpRequest request = new MockHttpRequest(
		        "http://test.mocksy.org/proxy/error_code" );
		request.addParameter( "error", new String[] { "true" } );

		HttpResponse response = (HttpResponse) ruleset.process( request );
		Assert.assertEquals( 403, response.getStatusCode() );
	}

	// Tests proxying of both request and response headers
	@Test
	public void testProxyWithCustomHeaders() throws Exception {
		HttpProxyRule proxyRule = new HttpProxyRule( "http://127.0.0.1:11112" );
		HttpMatcher matcher = new HttpMatcher();
		matcher
		        .setPattern( Pattern
		                .compile( "http://test.mocksy.org/proxy/.*" ) );
		proxyRule.addMatcher( matcher );
		Ruleset ruleset = new Ruleset();
		ruleset.addRule( proxyRule );

		MockHttpRequest request = new MockHttpRequest(
		        "http://test.mocksy.org/proxy/custom_header?error=true" );
		request.addHeader( "x-mocksy-custom", "test" );
		HttpResponse response = (HttpResponse) ruleset.process( request );
		Assert.assertEquals( "text/plain", response.getContentType() );
		Assert.assertEquals( "custom value", response
		        .getHeader( "x-mocksy-custom" ) );
		Assert.assertEquals( "custom header response", response.toString() );
	}

}
