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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.mocksy.Request;

/**
 * A Matcher is a mechanism for determining whether or not a particular Request
 * meets certain criteria and can therefore be processed by the containing Rule.
 * 
 * This abstract class provides most of the basic functionality, but it is 
 * designed for extensibility so that you could theoretically introduce new 
 * Matcher types that either perform different kinds of calculations, or handle
 * different kinds of Requests.
 * 
 * Despite the fact that this class holds a RegEx {@link java.util.regex.Pattern}
 * property, it is not necessary that all subtypes make use of it.  Because it 
 * is so useful/common for Matchers, it was added at this level to reduce code
 * duplication.
 * 
 * @author Saleem Shafi
 */
abstract public class Matcher {
	private static final Logger logger = Logger.getLogger( Matcher.class
	        .getName() );
	private Pattern pattern;
	private boolean negative;

	/**
	 * Returns the RegEx pattern used to perform the match.
	 * 
	 * @return RegEx pattern used for matching the Request
	 */
	public Pattern getPattern() {
		return this.pattern;
	}

	/**
	 * Sets the RegEx pattern for matching the Request
	 * 
	 * @param pattern RegEx pattern to use for matching
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * Sets whether or not this Matcher should negate it's normal
	 * evaluation.  If this flag is set to true, then the {@link Matcher#matches(Request)}
	 * method should return the exact opposite value that it would
	 * if this flag were set to false.
	 * 
	 * @param isNegative boolean flag for whether or to negate the evaluation
	 */
	public void setNegative(boolean isNegative) {
		this.negative = isNegative;
	}

	/**
	 * Main method that defines the responsibilities of a Matcher, namely
	 * to determine whether or not a particular Request meets the criteria
	 * established by this Matcher.
	 * 
	 * Implementations must reference the {@link #isNegative()} method to
	 * determine whether or not the return value should be negated.
	 * 
	 * @param request
	 * @return
	 */
	abstract public boolean matches(Request request);

	/**
	 * Returns whether or not this Matcher should negate its evaluation.
	 * 
	 *  @return whether or not to negate the {@link #matches(Request)} evaluation
	 */
	protected boolean isNegative() {
		return this.negative;
	}

	/**
	 * Helper method for subtypes that determines whether any of the
	 * given String values matches this object's Pattern.  If multiple
	 * values are given, it is not necessary for all of the values to
	 * match the Pattern, just for one of them to match.
	 * 
	 * @param values the String values to check
	 * @return whether or not any of the values matched
	 */
	protected boolean matchValues(String... values) {
		boolean matched = false;
		int i = 0;
		if ( values != null ) {
			// Check all values at this path for a match.
			// If one matches, then this is a good match, go to next path.
			Pattern pattern = this.getPattern();
			for ( ; i < values.length && !matched; i++ ) {
				String value = values[i];
				if ( value != null && pattern.matcher( value ).matches() ) {
					matched = true;
				}
			}
		}
		if ( logger.isLoggable( Level.FINER ) ) {
			if ( matched ) {
				logger.log( Level.FINER, this.toString() + ": Matched value "
				        + values[i - 1] );
			}
			else {
				logger.log( Level.FINER, this.toString() + ": No match." );
			}
		}
		return matched;
	}

}
