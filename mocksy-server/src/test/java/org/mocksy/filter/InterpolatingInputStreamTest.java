package org.mocksy.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mocksy.filter.PropertyFilter.InterpolatingInputStream;

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

public class InterpolatingInputStreamTest {

	private final Map<String, String> properties;

	public InterpolatingInputStreamTest() {
		this.properties = new HashMap<String, String>();
		this.properties.put( "name", "${firstName}" );
		this.properties.put( "firstName", "Saleem" );
		this.properties.put( "lastName", null );

	}

	private String read(String originalText, Map<String, String> properties)
	        throws IOException
	{
		InterpolatingInputStream filteredStream = new InterpolatingInputStream(
		        new ByteArrayInputStream( originalText.getBytes() ), properties );
		byte[] data = new byte[4096];
		int i = 0;
		while ( filteredStream.available() > 0 ) {
			int value = filteredStream.read();
			if ( value != -1 ) {
				data[i++] = (byte) value;
			}
		}
		return new String( data, 0, i );

	}

	private String readBulk(String originalText, Map<String, String> properties)
	        throws IOException
	{
		InterpolatingInputStream filteredStream = new InterpolatingInputStream(
		        new ByteArrayInputStream( originalText.getBytes() ), properties );
		StringBuffer returnValue = new StringBuffer();
		byte[] data = new byte[4096];
		while ( filteredStream.available() > 0 ) {
			int length = filteredStream.read( data );
			if ( length != -1 ) {
				returnValue.append( new String( data, 0, length ) );
			}
		}
		return returnValue.toString();
	}

	@Test
	public void testReadWithNoPropertyTags() throws Exception {
		String text = "hello, world!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, world!", result );
	}

	@Test
	public void testReadWithDollarSign() throws Exception {
		String text = "hello, $world!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, $world!", result );
	}

	@Test
	public void testReadWithDollarSignAndOpenBracket() throws Exception {
		String text = "hello, ${world!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, ${world!", result );
	}

	@Test
	public void testReadWithValidProperty() throws Exception {
		String text = "hello, ${firstName}!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, Saleem!", result );

	}

	@Test
	public void testReadWithNestedProperties() throws Exception {
		String text = "hello, ${name}!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, Saleem!", result );
	}

	@Test
	public void testReadWithInvalidProperty() throws Exception {
		String text = "hello, ${myName}!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, ${myName}!", result );
	}

	@Test
	public void testReadWithNullProperty() throws Exception {
		String text = "hello, ${lastName}!";
		String result = this.read( text, this.properties );
		Assert.assertEquals( "hello, ${lastName}!", result );

	}

	@Test
	public void testBulkReadWithNoPropertyTags() throws Exception {
		String text = "hello, world!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, world!", result );
	}

	@Test
	public void testBulkReadWithDollarSign() throws Exception {
		String text = "hello, $world!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, $world!", result );
	}

	@Test
	public void testBulkReadWithDollarSignAndOpenBracket() throws Exception {
		String text = "hello, ${world!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, ${world!", result );
	}

	@Test
	public void testBulkReadWithValidProperty() throws Exception {
		String text = "hello, ${firstName}!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, Saleem!", result );

	}

	@Test
	public void testBulkReadWithNestedProperties() throws Exception {
		String text = "hello, ${name}!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, Saleem!", result );
	}

	@Test
	public void testBulkReadWithInvalidProperty() throws Exception {
		String text = "hello, ${myName}!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, ${myName}!", result );
	}

	@Test
	public void testBulkReadWithNullProperty() throws Exception {
		String text = "hello, ${lastName}!";
		String result = this.readBulk( text, this.properties );
		Assert.assertEquals( "hello, ${lastName}!", result );

	}

}
