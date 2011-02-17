package org.mocksy.filter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;
import org.mocksy.config.Source;

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
 * ResponseFilter that replaces Velocity-style properties, e.g. ${prop},
 * in the Response data.  The property values come from both the
 * System properties, as well as any extra values sent into the {@link #initialize(Map)}
 * method.
 * 
 * If you have other desires for dynamic data replacement, you could use
 * this class as a base class, and override the {@link #initialize(Map)} or
 * {@link #getProperties()} methods.
 * 
 * @author Saleem Shafi
 */
public class PropertyFilter implements ResponseFilter {
	private Map<String, String> properties;

	/**
	 * Filters the incoming data by replacing ${} tags with the
	 * corresponding values in the set of properties defined for the
	 * filter.  If the property name identified in the tag has no
	 * corresponding value in this filter, then the tag will not
	 * be replaced.
	 * 
	 * @param input the unfiltered InputStream
	 * @return the InputStream with the property tags replaced
	 */
	public InputStream filter(InputStream input) throws FilterException {
		return new InterpolatingInputStream( input, this.getProperties() );
	}

	/**
	 * Defines the list of properties to be used for filtering.  This
	 * implementation also includes System properties, but the given
	 * properties will override those values.
	 */
	public void initialize(Map<String, String> properties)
	        throws FilterException
	{
		this.properties = new HashMap( System.getProperties() );
		this.properties.putAll( properties );

	}
 
	@Override
	/**
	 * Returns null, indicating that this filter does not modify the format
	 * of the data in the input stream.
	 */
    public String getNewContentType() {
	    return null;
    }

	/**
	 * Returns the Map of properties to use for replacement in this
	 * filter.
	 * 
	 * @return the properties to use for filtering
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * There's really no need for any extra resources in this basic
	 * implementation of a PropertyFilter, so this method really
	 * does nothing, but feel free to override this in a subclass 
	 * that could make use of it.
	 * 
	 * @param source the Source object to use as a relative location
	 * 		for any needed resources
	 */
	public void setRelativeSource(Source source) {
	// no use for a relative Source in this class
	}

	/**
	 * FilterInputStream that interpolates the root InputStream data by
	 * replacing ${propName} with a corresponding property value.  The
	 * list of properties, name-value pairs, and the root InputStream
	 * are provided in the constructor.  Reading from this InputStream
	 * results in the same data as the root stream, except that instances
	 * of ${propName} are replace with the corresponding value from the
	 * Map.
	 *   
	 * @author Saleem Shafi
	 */
	static class InterpolatingInputStream extends FilterInputStream {
		private final Map<String, String> properties;
		// this is essentially the largest property name you can have
		private static final int bufferSize = 2048;

		/**
		 * Creates the InterpolatingInputStream with the given root
		 * InputStream and set of properties.
		 *  
		 * @param input root InputStream to interpolate
		 * @param properties list of properties to use for replacement
		 */
		public InterpolatingInputStream(InputStream input,
		        Map<String, String> properties)
		{
			super( new PushbackInputStream( input, bufferSize ) );
			this.properties = properties;
		}

		@Override
		public int read() throws IOException {
			int data = super.read();
			// if we've hit the beginning of a property tag
			if ( data == '$' ) {
				byte[] propertyBuffer = new byte[bufferSize];
				// read as much as we can to try to get the rest of the tag
				int read = this.in.read( propertyBuffer );
				boolean replaced = false;
				int lastChar = -1;
				// if this really is the beginning of the property tag
				if ( propertyBuffer[0] == '{' ) {
					// read until we find the end tag
					for ( int i = 1; i < read && lastChar != '}'; i++ ) {
						lastChar = propertyBuffer[i];
						// if we find it
						if ( i > 2 && lastChar == '}' ) {
							// then we can get a property name
							String propertyName = new String( propertyBuffer,
							        1, i - 1 );
							// and look for a property value
							String propertyValue = this.properties
							        .get( propertyName );
							// if we find one
							if ( propertyValue != null ) {
								// put back everything after the '}'
								( (PushbackInputStream) this.in )
								        .unread( propertyBuffer, i + 1, read
								                - ( i + 1 ) );
								// then put the replaced property value in front
								// of it
								( (PushbackInputStream) this.in )
								        .unread( propertyValue.getBytes() );
								replaced = true;
								data = this.read();
								// WARNING we are susceptible to infinite
								// recursion.
							}
						}
					}
				}
				if ( !replaced ) {
					// put the whole thing back
					( (PushbackInputStream) this.in ).unread( propertyBuffer,
					        0, read );
				}
			}
			return data;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int readLength = Math.min( len, bufferSize );
			byte[] data = new byte[readLength];
			int read = this.in.read( data );
			if ( read == -1 ) return -1;

			// if the stream starts with property markers
			if ( data[0] == '$' && data[1] == '{' ) {
				int i = 2;
				// try to find the end property marker
				while ( i < read && data[i] != '}' )
					i++;
				// if we find the end property marker
				if ( data[i] == '}' ) {
					// put back everything after the end-property marker
					( (PushbackInputStream) this.in ).unread( data, i + 1, read
					        - ( i + 1 ) );

					String propertyName = new String( data, 2, i - 2 );
					String propertyValue = this.properties.get( propertyName );
					// if there was no property with that name
					if ( propertyValue == null ) {
						// read everything up to and including the end-property
						// marker
						System.arraycopy( data, 0, b, off, i + 1 );
						// report back how much we read
						return i + 1;
						// if we have a property with that name
					}
					else {
						byte[] propBytes = propertyValue.getBytes();
						// put back the property value so we can re-read it
						// and process nested properties
						( (PushbackInputStream) this.in ).unread( propBytes );
						return this.read( b, off, len );
					}
				}
				// if we couldn't find an end-property marker
				else {
					// read the whole data
					System.arraycopy( data, 0, b, off, i );
					return i;
				}
			}
			// if we don't start with the property markers
			else {
				int i = 1;
				// look for the potential start-property marker
				while ( i < read && data[i] != '$' )
					i++;
				// if we found one
				if ( i < read ) {
					// put back everything starting from the start-property
					// marker
					( (PushbackInputStream) this.in )
					        .unread( data, i, read - i );
				}
				// read everything else
				System.arraycopy( data, 0, b, off, i );
				return i;
			}
		}
	}

}
