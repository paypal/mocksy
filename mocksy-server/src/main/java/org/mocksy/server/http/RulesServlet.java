package org.mocksy.server.http;

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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mocksy.rules.Ruleset;
import org.mocksy.rules.xml.XmlSerializer;

/**
 * Servlet that displays the XML description of the rules running on the server.
 * 
 * @author Saleem Shafi
 */
public class RulesServlet extends HttpServlet {
	private static final long serialVersionUID = -7412950822802129704L;

	private Ruleset rules;

	/**
	 * Creates the servlet that will display the Ruleset as XML.
	 * 
	 * @param rules the Ruleset to display
	 */
	RulesServlet(Ruleset rules) {
		if ( rules == null ) {
			throw new IllegalArgumentException(
			        "RulesServlet cannot be setup with a 'null' Ruleset." );
		}
		this.rules = rules;
	}

	@Override
	public void destroy() {
		this.rules = null;
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException
	{
		if ( this.rules != null ) {
			XmlSerializer serializer = new XmlSerializer( resp
			        .getOutputStream() );
			serializer.serialize( this.rules );
			resp.flushBuffer();
		}
	}
}
