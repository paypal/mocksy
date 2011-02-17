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
import static org.junit.Assert.assertNull;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.mocksy.RawResponse;
import org.mocksy.Response;
import org.mocksy.rules.http.HttpMatcher;
import org.mocksy.server.http.MockHttpRequest;

public class DefaultRuleTest {
	private static final String BASE_URL = "http://localhost";
	private Ruleset ruleset;
	private Ruleset nestedRuleset;

	@Before
	public void setUp() throws Exception {
		// nested ruleset
		this.nestedRuleset = new Ruleset();
		// nested default rule
		this.nestedRuleset.setDefaultRule( new ResponseRule( new RawResponse(
		        "nested-default", "This is a nested default response." ) ) );

		this.ruleset = new Ruleset();
		// main "nested" rule
		RulesetRule nestedRule = new RulesetRule( this.nestedRuleset );
		HttpMatcher nestedMatcher = new HttpMatcher();
		nestedMatcher.setPattern( Pattern.compile( ".*/nested/.*" ) );
		nestedRule.addMatcher( nestedMatcher );
		this.ruleset.addRule( nestedRule );
		// main default rule
		this.ruleset.setDefaultRule( new ResponseRule( new RawResponse( "default",
		        "This is a default response." ) ) );
	}

	@Test
	public void testMatchDefaultRule() throws Exception {
		MockHttpRequest request = new MockHttpRequest( BASE_URL + "/" );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "default", response.getId() );
		assertEquals( "This is a default response.", response.toString() );
	}

	@Test
	public void testMatchNestedRulesetDefaultRule() throws Exception {
		MockHttpRequest request = new MockHttpRequest( BASE_URL + "/nested/" );
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "nested-default", response.getId() );
		assertEquals( "This is a nested default response.", response.toString() );
	}

	@Test
	public void testMissWithNoDefaultRule() throws Exception {
		MockHttpRequest request = new MockHttpRequest( BASE_URL + "/" );
		this.ruleset.clear();
		Response response = this.ruleset.process( request );
		assertNull( response );
	}

	@Test
	public void testMissNestedWithNoDefaultRule() throws Exception {
		MockHttpRequest request = new MockHttpRequest( BASE_URL + "/nested/" );
		this.ruleset.clear();

		RulesetRule nestedRule = new RulesetRule( this.nestedRuleset );
		HttpMatcher nestedMatcher = new HttpMatcher();
		nestedMatcher.setPattern( Pattern.compile( ".*/nested/.*" ) );
		nestedRule.addMatcher( nestedMatcher );
		this.ruleset.addRule( nestedRule );

		this.nestedRuleset.clear();

		Response response = this.ruleset.process( request );
		assertNull( response );
	}

	@Test
	public void testMissNestedWithMainDefault() throws Exception {
		MockHttpRequest request = new MockHttpRequest( BASE_URL + "/nested/" );
		this.nestedRuleset.clear();
		Response response = this.ruleset.process( request );
		assertNotNull( response );
		assertEquals( "default", response.getId() );
		assertEquals( "This is a default response.", response.toString() );
	}
}
