package org.mocksy.filter;

import java.io.IOException;

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

/**
 * Represents any error that occurs related to the filtering of
 * Response data.  This more-or-less just wraps the underlying
 * exception that caused the problem and groups them under a
 * single type.
 */
public class FilterException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -365452103920160419L;

	/**
	 * Creates the FilterException as a wrapper around the underlying
	 * cause.
	 * 
	 * @param cause the root cause of the exception
	 */
	public FilterException(Throwable cause) {
		super( cause );
	}
}
