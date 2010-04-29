package org.mocksy.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.mocksy.config.xml.FileXmlSource;
import org.mocksy.config.xml.XmlRulesetFactory;
import org.mocksy.config.xml.XmlSource;

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
 *  Helper class that creates a {@link org.mocksy.config.RulesetFactory}
 *  based on a String. 
 */
public final class RulesetFactoryFactory {

	/**
	 * Creates a {@link org.mocksy.config.RulesetFactory} based on the
	 * given String.  If the String represents a valid classname, a new
	 * instance of that class is created and is assumed to be an instance
	 * of RulesetFactory.
	 * 
	 * Otherwise, we return an XmlRulesetFactory, in which case the String
	 * argument needs to be either a URL or a filesystem path.
	 * 
	 * @param rulesetFactory the identifier for the RulesetFactory, either
	 * 		a Java classname, URL or filesystem path.
	 * @return a RulesetFactory instance
	 * @throws IllegalArgumentException if rulesetFactory is a Java class,
	 * 		but the class cannot be instantiated or isn't an subtype of
	 * 		RulesetFactory.
	 * @throws IllegalArgumentException if rulesetFactory is interpreted
	 * 		to be filesystem path, but the file doesn't exist.
	 * @throws Exception if there's a problem creating an XmlRulesetFactory.
	 */
	public static RulesetFactory getRulesetFactory(String rulesetFactory)
	        throws Exception
	{
		try {
			return getClassBasedFactory( rulesetFactory );
		}
		catch ( ClassNotFoundException e ) {
			// in this case, we probably didn't get a classname
			// don't worry, we'll just move on and assume this
			// is either a filename or URL
		}

		try {
			return new XmlRulesetFactory( new URL( rulesetFactory ) );
		}
		catch ( MalformedURLException e ) {
			// ok, we'll just have to assume this is a filesystem path
		}

		return new XmlRulesetFactory( new FileXmlSource( new File(
		        rulesetFactory ) ) );
	}

	public static RulesetFactory getRulesetFactory(String rulesetFactory,
	        Source baseSource) throws Exception
	{
		try {
			return getClassBasedFactory( rulesetFactory );
		}
		catch ( ClassNotFoundException e ) {
			// in this case, we probably didn't get a classname
			// don't worry, we'll just move on and assume this
			// is either a filename or URL
		}

		XmlSource subSource = (XmlSource) baseSource
		        .getRelativeSource( rulesetFactory );
		return new XmlRulesetFactory( subSource );
	}

	private static RulesetFactory getClassBasedFactory(String rulesetFactory)
	        throws ClassNotFoundException
	{
		Class<?> factoryClass = Class.forName( rulesetFactory );
		if ( !RulesetFactory.class.isAssignableFrom( factoryClass ) ) {
			throw new IllegalArgumentException( rulesetFactory
			        + " needs to be a " + RulesetFactory.class.getName() );
		}

		try {
			return (RulesetFactory) factoryClass.newInstance();
		}
		catch ( InstantiationException e ) {
			throw new IllegalArgumentException( "Error creating instance of "
			        + rulesetFactory, e );
		}
		catch ( IllegalAccessException e ) {
			throw new IllegalArgumentException( "Error creating instance of "
			        + rulesetFactory, e );
		}
	}
}
