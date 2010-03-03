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

import java.util.ArrayList;
import java.util.Collection;
import org.mocksy.Request;
import org.mocksy.Response;

/**
 * A RulesetRule is a {@link org.mocksy.rules.Rule} that delegates its processing
 * to a {@link org.mocksy.rules.Ruleset}.  This is used primarily in cases where
 * a Ruleset is nested inside of another Ruleset.  This type of rule identifies
 * whether the Request should be processed by the nested Ruleset.
 * 
 * @author Saleem Shafi
 */
public class RulesetRule implements Rule {
	private Collection<Matcher> matchers = new ArrayList<Matcher>();
	private Ruleset ruleset;

	/**
	 * A RulesetRule must be created with a Ruleset.
	 * 
	 * @param ruleset the {@link org.mocksy.rules.Ruleset} to delegate to
	 */
	public RulesetRule(Ruleset ruleset) {
		if ( ruleset == null ) {
			throw new IllegalArgumentException(
			        "'ruleset' argument in RulesetRule constructor cannot be null" );
		}
		this.ruleset = ruleset;
	}

	/**
	 * Returns the {@link org.mocksy.rules.Ruleset} associated with this Rule.
	 * 
	 * @return the associated Ruleset
	 */
	public Ruleset getRuleset() {
		return this.ruleset;
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

	public Response process(Request request) throws Exception {
		return this.ruleset.process( request );
	}

	public Collection<Matcher> getMatchers() {
		return this.matchers;
	}
}
