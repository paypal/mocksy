package org.mocksy.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import junit.framework.TestCase;
import org.mocksy.RawResponse;
import org.mocksy.Response;
import org.mocksy.config.Source;
import org.mocksy.util.StreamData;

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

public class TestFilteredResponse extends TestCase {
	public void testFilteredResponseID() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse );
		assertEquals( "filtered-test", filteredResponse.getId() );
	}

	public void testFilteredToString() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new ReverseFilter() );
		assertEquals( "tnetnoc", filteredResponse.toString() );
	}

	public void testFilteredStream() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new ReverseFilter() );
		assertEquals( "tnetnoc", new String( filteredResponse.toByteArray() ) );
	}

	public void testFilteredContentType() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new ReverseFilter() );
		assertEquals( "text/plain", baseResponse.getContentType() );
		assertEquals( "text/reverse", filteredResponse.getContentType() );

	}

	public void testFilterWithNullContentType() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new AllCapsFilter() );
		assertEquals( "text/plain", baseResponse.getContentType() );
		assertEquals( "text/plain", filteredResponse.getContentType() );
	}

	public void testFilteredResponseIDWithMultipleFilters() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new AllCapsFilter(), new ReverseFilter() );
		assertEquals( "filtered-test", filteredResponse.getId() );
	}

	public void testFilteredContentTypeWithTopFilterNotSet() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new AllCapsFilter(), new ReverseFilter() );
		assertEquals( "text/plain", baseResponse.getContentType() );
		assertEquals( "text/reverse", filteredResponse.getContentType() );
	}

	public void testFilteredContentTypeWithMiddleFilterNotSet()
	        throws Exception
	{
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new ReverseFilter(), new AllCapsFilter() );
		assertEquals( "text/plain", baseResponse.getContentType() );
		assertEquals( "text/reverse", filteredResponse.getContentType() );
	}

	public void testFilteredStreamWithMultipleFilters() throws Exception {
		Response baseResponse = new RawResponse( "test", "content" );
		Response filteredResponse = new FilteredResponse( baseResponse,
		        new ReverseFilter(), new AllCapsFilter() );
		assertEquals( "TNETNOC", new String( filteredResponse.toByteArray() ) );
	}

	static class ReverseFilter implements ResponseFilter {

		@Override
		public InputStream filter(InputStream input) throws FilterException {
			try {
				byte[] data = StreamData.getBytesFromStream( input, 1024 );
				byte[] reverseData = new byte[ data.length ];
				for ( int i = 0; i < data.length; i++ ) {
					reverseData[i] = data[data.length-1 - i];
				}
				return new ByteArrayInputStream( reverseData );
			}
			catch ( IOException e ) {
				throw new FilterException( e );
			}
		}

		@Override
		public String getNewContentType() {
			return "text/reverse";
		}

		@Override
		public Map<String, String> getProperties() {
			return null;
		}

		@Override
		public void initialize(Map<String, String> properties)
		        throws FilterException
		{}

		@Override
		public void setRelativeSource(Source source) {}
	}

	static class AllCapsFilter implements ResponseFilter {

		@Override
		public InputStream filter(InputStream input) throws FilterException {
			try {
				byte[] data = StreamData.getBytesFromStream( input, 1024 );
				return new ByteArrayInputStream( new String( data )
				        .toUpperCase().getBytes() );
			}
			catch ( IOException e ) {
				throw new FilterException( e );
			}
		}

		@Override
		public String getNewContentType() {
			return null;
		}

		@Override
		public Map<String, String> getProperties() {
			return null;
		}

		@Override
		public void initialize(Map<String, String> properties)
		        throws FilterException
		{}

		@Override
		public void setRelativeSource(Source source) {}
	}
}
