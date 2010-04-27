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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mocksy.Request;
import org.mocksy.rules.Matcher;
import org.mocksy.server.http.HttpRequest;

/**
 * Matcher specialized for HTTP requests.  It can match against HTTP headers,
 * POST parameters, and full URLs.
 * 
 * @author Saleem Shafi
 */
public class HttpMatcher extends Matcher {
	private static final Logger logger = Logger.getLogger( HttpMatcher.class
	        .getName() );
	private String header;
	private String param;

	/**
	 * Sets the HTTP header name to match against.
	 * 
	 * @param header the HTTP header to match
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Sets the HTTP parameter to match against.
	 *  
	 * @param param the name of the HTTP parameter to match
	 */
	public void setParam(String param) {
		this.param = param;
	}

	/**
	 * Returns the HTTP Header that this Matcher will investigate.
	 * 
	 * @return the HTTP header name that the Matcher will check
	 */
	public String getHeader() {
		return this.header;
	}

	/**
	 * Returns the HTTP parameter that this Matcher will investigate.
	 * 
	 * @return the HTTP parameter name that the Matcher will check
	 */
	public String getParam() {
		return this.param;
	}

	@Override
	public boolean matches(Request request) {
		// We can only match against HttpRequests
		if ( !( request instanceof HttpRequest ) ) {
			logger.log( Level.WARNING,
			        "Trying to process a non-HTTP request as HTTP." );
			return this.isNegative();
		}
		HttpRequest httpRequest = ( (HttpRequest) request );
		String[] values = null;
		if ( this.header != null ) {
			// Header match
			String headerValue = httpRequest.getHeader( this.header );
			values = new String[] { headerValue };
			if ( logger.isLoggable( Level.FINEST ) ) {
				logger.log( Level.FINEST,
				        "Attempting HTTP header match.  Header '" + this.header
				                + "' with value '" + headerValue + "'" );
			}
		}
		else if ( this.param != null ) {
			// Parameter match
			values = httpRequest.getParamValues( this.param );
			if ( logger.isLoggable( Level.FINEST ) ) {
				logger.log( Level.FINEST,
				        "Attempting HTTP parameter match.  Parmeter '"
				                + this.param + "' with value(s) '" + values
				                + "'" );
			}
		}
		else {
			// Full URI match
			String fullUrl = httpRequest.getFullURL();
			values = new String[] { fullUrl };
			if ( logger.isLoggable( Level.FINEST ) ) {
				logger.log( Level.FINEST,
				        "Attempting HTTP Query match.  Query String: "
				                + fullUrl );
			}
			// would be nice to be able to get post data
		}

		return this.matchValues( values ) ^ this.isNegative();
	}

	@Override
	public String toString() {
		if ( this.header != null ) {
			return "HTTP Header matcher, header " + this.header + ", pattern "
			        + this.getPattern();
		}
		else if ( this.param != null ) {
			return "HTTP Param matcher, parameter " + this.param + ", pattern "
			        + this.getPattern();
		}
		else {
			return "HTTP Query matcher, pattern " + this.getPattern();
		}
	}

}
