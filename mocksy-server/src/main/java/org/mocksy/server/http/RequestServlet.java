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
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mocksy.Request;
import org.mocksy.Response;
import org.mocksy.filter.FilterException;
import org.mocksy.rules.Ruleset;

/**
 * Main servlet that processes incoming requests through a Mocksy Ruleset.
 * Each request is converted into a {@link org.mocksy.server.http.HttpRequest}
 * and then processed with the Ruleset.
 *  
 * @author Saleem Shafi
 */
public class RequestServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger( RequestServlet.class
	        .getName() );
	private static final long serialVersionUID = -7412950822802129704L;

	private Ruleset rules;

	/**
	 * Creates the RequestServlet with the given Ruleset.
	 * 
	 * @param rules the Ruleset to process requests with
	 */
	RequestServlet(Ruleset rules) {
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
		try {
			// Wrap the request and response for logging
			LoggingRequestWrapper reqWrapper = new LoggingRequestWrapper( req );
			LoggingResponseWrapper resWrapper = new LoggingResponseWrapper(
			        resp );
			long start = System.currentTimeMillis();

			Request request = new HttpRequest( reqWrapper );
			// Process the request through the Ruleset
			Response matchResponse = this.rules.process( request );
			// if no response is found, return a 404
			if ( matchResponse == null ) {
				resp.sendError( 404, "No matching rules" );
			}
			else {
				// else return the response
				respond( matchResponse, resWrapper );
			}

			long end = System.currentTimeMillis();

			// log the request and response data
			if ( logger.isLoggable( Level.FINE ) ) {
				String requestString = new String( reqWrapper.getRequestData() );
				String responseString = new String( resWrapper
				        .getResponseData() );
				long duration = end - start;
				logger.log( Level.FINE, "Request: " + requestString + "\n"
				        + "Response: " + responseString + "\n" + "Duration: "
				        + duration );
			}

		}
		catch ( IOException e ) {
			throw e;
		}
		catch ( Exception e ) {
			throw new ServletException( "Cannot generate response: "
			        + e.getMessage(), e );
		}
	}

	private void respond(Response matchResponse, HttpServletResponse resp)
	        throws IOException
	{
		// log which response is being returned
		if ( logger.isLoggable( Level.INFO ) ) {
			logger.info( "Matched " + matchResponse.getId() );
		}

		byte[] data;
		try {
			// get the response data
			data = matchResponse.toByteArray();
		}
		catch ( FilterException e ) {
			// handle filtering problems
			String msg = "Error processing response '" + matchResponse.getId()
			        + "'";
			logger.severe( msg );
			resp.setContentType( "text/plain" );
			resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg );
			return;
		}
		catch ( IOException e ) {
			// report processing errors as 404s
			String msg = "Cannot locate '" + matchResponse.getId() + "'";
			logger.severe( msg );
			resp.setContentType( "text/plain" );
			resp.sendError( HttpServletResponse.SC_NOT_FOUND, msg );
			return;
		}
		boolean isError = false;
		if ( matchResponse instanceof HttpResponse ) {
			HttpResponse httpResponse = (HttpResponse) matchResponse;
			int statusCode = httpResponse.getStatusCode();
			if ( statusCode < 400 ) {
				resp.setStatus( statusCode );
			}
			else {
				resp.sendError( statusCode );
				isError = true;
			}

			for ( String headerName : httpResponse.getHeaderNames() ) {
				// need to skip the Content-Encoding header on errors since this
				// server will send it's own error content and it might not be 
				// encoded the same way, e.g. gzip
				if (isError && "Content-Encoding".equals(headerName)) continue;
				String headerValue = httpResponse.getHeader( headerName );
				resp.addHeader( headerName, headerValue );
			}
		}
		else {
			resp.setStatus( HttpServletResponse.SC_OK );
		}

		if ( !isError ) {
			String contentType = matchResponse.getContentType();
			if ( contentType != null ) {
				resp.setContentType( contentType );
			}
			// resp.setContentLength( data.length );
			OutputStream out = resp.getOutputStream();
			out.write( data );
			out.flush();
		}
		resp.flushBuffer();
	}
}
