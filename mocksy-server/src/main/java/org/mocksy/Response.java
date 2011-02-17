package org.mocksy;

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

public interface Response {

	/**
	 * Returns the response contents as a byte[].
	 * 
	 * @return response contents as a byte array
	 * @throws IOException if there's a problem producing the content
	 */
	public abstract byte[] toByteArray() throws IOException;

	/**
	 * Returns the MIME content type for this response.
	 * 
	 * @return MIME content type
	 */
	public abstract String getContentType();

	/**
	 * Returns the ID of the response.
	 * 
	 * @return ID of the response
	 */
	public abstract String getId();

}