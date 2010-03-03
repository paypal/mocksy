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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.mocksy.Response;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.rules.xml.XmlMatcher;
import org.mocksy.server.http.MockHttpRequest;

public class MatcherTest {
	private Ruleset ruleset;
	private Ruleset nestedRuleset;

	@Before
	public void setUp() throws Exception {
		// nested ruleset
		this.nestedRuleset = new Ruleset();
		// nested default rule
		this.nestedRuleset.setDefaultResponse( new Response( "nested-default",
		        "This is a nested default response.", "text/plain" ) );

		ResponseRule responseRule = new ResponseRule( new Response(
		        "xml-response-one", "<doc type=\"xml\">response</doc>",
		        "text/xml" ) );
		XmlMatcher xmlMatcher = new XmlMatcher( "/envelope" );
		responseRule.addMatcher( xmlMatcher );
		xmlMatcher.setPattern( Pattern.compile( "empty" ) );
		this.nestedRuleset.addRule( responseRule );

		this.ruleset = new Ruleset();
		// main "nested" rule
		RulesetRule nestedRule = new RulesetRule( this.nestedRuleset );
		HttpMatcher nestedMatcher = new HttpMatcher();
		nestedMatcher.setPattern( Pattern.compile( ".*/nested/.*" ) );
		nestedRule.addMatcher( nestedMatcher );
		this.ruleset.addRule( nestedRule );

		responseRule = new ResponseRule( new Response( "response-one",
		        "This is the first response", "text/plain" ) );
		HttpMatcher matcher = new HttpMatcher();
		responseRule.addMatcher( matcher );
		matcher.setPattern( Pattern.compile( ".*/resp-one/.*" ) );
		this.ruleset.addRule( responseRule );

		responseRule = new ResponseRule( new Response( "response-two",
		        "This is the second response", "text/plain" ) );
		matcher = new HttpMatcher();
		responseRule.addMatcher( matcher );
		matcher.setHeader( "testHeader" );
		matcher.setPattern( Pattern.compile( "two" ) );
		this.ruleset.addRule( responseRule );

		responseRule = new ResponseRule( new Response( "response-three",
		        "This is the third response", "text/plain" ) );
		matcher = new HttpMatcher();
		responseRule.addMatcher( matcher );
		matcher.setParam( "testParam" );
		matcher.setPattern( Pattern.compile( "three*" ) );
		this.ruleset.addRule( responseRule );
	}

	@Test
	public void testMatchXPath() throws Exception {
		MockHttpRequest request = new MockHttpRequest( "/nested/" );
		request.setData( new ByteArrayInputStream( "<envelope>empty</envelope>"
		        .getBytes() ) );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "xml-response-one", response.getId() );
		assertEquals( "<doc type=\"xml\">response</doc>", response.toString() );
	}

	@Test
	public void testMatchPostData() throws Exception {}

	@Test
	public void testMatchURI() throws Exception {
		MockHttpRequest request = new MockHttpRequest( "/resp-one/" );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "response-one", response.getId() );
		assertEquals( "This is the first response", response.toString() );
	}

	@Test
	public void testMatchHeader() throws Exception {
		MockHttpRequest request = new MockHttpRequest( "/" );
		request.addHeader( "testHeader", "two" );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "response-two", response.getId() );
		assertEquals( "This is the second response", response.toString() );
	}

	@Test
	public void testMatchParam() throws Exception {
		MockHttpRequest request = new MockHttpRequest( "/" );
		request.addParameter( "testParam", new String[] { "three" } );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "response-three", response.getId() );
		assertEquals( "This is the third response", response.toString() );
	}
}
