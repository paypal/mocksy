package org.mocksy.config;

import junit.framework.Assert;
import org.junit.Test;
import org.mocksy.rules.Ruleset;

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

public class RulesetFactoryTest {

	public static class TestFactory implements RulesetFactory {

		@Override
		public Ruleset getRuleset() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Test
	public void testJavaClassFactory() throws Exception {
		RulesetFactory factory = RulesetFactoryFactory
		        .getRulesetFactory( TestFactory.class.getName() );
		Assert.assertNotNull( factory );
		Assert.assertTrue( factory instanceof TestFactory );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidJavaClassFactory() throws Exception {
		RulesetFactoryFactory.getRulesetFactory( String.class.getName() );
	}

	@Test
	public void testXmlURLFactory() throws Exception {

	}

	@Test
	public void testXmlFileSystemFactory() throws Exception {

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidFactory() throws Exception {
		RulesetFactoryFactory.getRulesetFactory( "asdfasdf" );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testXmlStringFactory() throws Exception {
		RulesetFactoryFactory
		        .getRulesetFactory( "<rules><default-rule proxy-url=\"http://localhost/\"/></rules>" );
	}
}
