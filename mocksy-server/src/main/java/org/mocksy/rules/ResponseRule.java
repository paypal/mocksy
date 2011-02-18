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
import org.mocksy.filter.FilteredResponse;
import org.mocksy.filter.ResponseFilter;

public class ResponseRule implements Rule {
	private Collection<Matcher> matchers = new ArrayList<Matcher>();
	private Response response;

	public ResponseRule(Response response) {
		this.response = response;
		this.clear();
	}

	public void addMatcher(Matcher matcher) {
		this.matchers.add( matcher );
	}

	public void addFilter(ResponseFilter filter) {
		if (!(this.response instanceof FilteredResponse) ) {
			this.response = new FilteredResponse( this.response );
		}
		((FilteredResponse)this.response).addFilter( filter );
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

	public Response process(Request request) {
		return this.response;
	}

	public Collection<Matcher> getMatchers() {
		return this.matchers;
	}

	public Response getResponse() {
		return this.response;
	}

}
