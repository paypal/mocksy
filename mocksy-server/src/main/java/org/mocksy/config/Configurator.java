package org.mocksy.config;

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
 * Simple representation of how the rulesets are configured.  This
 * is really just a mechanism to allow rules to be refreshed without having
 * to restart the server.
 * 
 * The lone {@link #checkForUpdates()} method is invoked whenever a ruleset
 * is processed and offers the opportunity to update the ruleset with new
 * values.
 */
public interface Configurator {

	/**
	 * Checks to see if the source of the configuration has been updated,
	 * and if so, updates the associated {@link org.mocksy.rules.Ruleset}.
	 * 
	 * @throws Exception if there is an update, but the Ruleset can't be updated.
	 */
	void checkForUpdates() throws Exception;

}
