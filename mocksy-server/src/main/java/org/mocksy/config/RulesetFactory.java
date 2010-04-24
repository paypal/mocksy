package org.mocksy.config;

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

/**
 * Interface that represents a factory for creating Rulesets.
 * 
 * @author Saleem Shafi
 */
public interface RulesetFactory {
	/**
	 * Creates and returns a Ruleset.  The manner in which the Ruleset
	 * is created and whether or not the same Ruleset is returned on
	 * multiple invocations is up to each implementation.
	 * 
	 * @return a Ruleset
	 */
	Ruleset getRuleset();
}
