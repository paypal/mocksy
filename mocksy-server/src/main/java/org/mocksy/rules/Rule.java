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

import java.util.Collection;
import org.mocksy.Request;
import org.mocksy.Response;

/**
 * A Rule is the basic function in Mocksy.  It takes a {@link org.mocksy.Request}
 * and converts it into a {@link org.mocksy.Response} with the {@link #process(Request)}
 * method.
 * 
 * The associated Matchers are used to determine the suitability of this Rule
 * for the given Request.
 * 
 * There are two main Rule implementations: {@link org.mocksy.rules.ResponseRule}
 * and {@link org.mocksy.rules.RulesetRule}.  The former creates a Response object
 * directly, while the latter delegates to another set of Rules.
 * 
 * @author Saleem Shafi
 */
public interface Rule {

	/**
	 * Adds a {@link org.mocksy.rules.Matcher} to this Rule.  All Matchers
	 * have to match the Request during processing for the rule to take
	 * effect.
	 *  
	 * @param matcher the Matcher to add
	 */
	void addMatcher(Matcher matcher);

	/**
	 * Evaluates the {@link org.mocksy.Request} to see if this Rule can
	 * be used to generate the Response.  Generally, if all of the associated 
	 * Matchers match the Request, then this should return true; otherwise,
	 * if any of the Matchers don't like the Request, it should return false.
	 * 
	 * @param request
	 * @return whether or not this Rule can handle the given Request
	 */
	boolean matches(Request request);

	/**
	 * Processes the given Request.  The {@link #matches(Request)} method 
	 * should be invoked to ensure that this Rule should even be used for 
	 * this Request before calling {@link #process(Request)}.
	 * 
	 * @param request the Request to process
	 * @return the {@link org.mocksy.Response} that this Rule creates for 
	 * 		the given Request
	 * @throws Exception
	 */
	Response process(Request request) throws Exception;

	/**
	 * Returns the list of Matchers used to determine the suitability of
	 * this Rule for processing a given Request.
	 * 
	 * @return list of Matchers for this Rule
	 */
	Collection<Matcher> getMatchers();

}
