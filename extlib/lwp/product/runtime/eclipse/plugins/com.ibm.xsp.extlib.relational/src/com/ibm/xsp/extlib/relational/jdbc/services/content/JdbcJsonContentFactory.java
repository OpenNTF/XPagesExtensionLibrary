/*
 * © Copyright IBM Corp. 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.ibm.xsp.extlib.relational.jdbc.services.content;

import com.ibm.domino.services.content.JsonContentFactory;

/**
 * @author Andrejus Chaliapinas
 *
 */
public class JdbcJsonContentFactory extends JsonContentFactory {
	private static JsonContentFactory s_factory = new JdbcJsonContentFactory();
	
	/**
	 * Private constructor because this is a singleton.
	 */
	private JdbcJsonContentFactory() {
	}

	public static JsonContentFactory get() {
		return s_factory;
	}
	
	public JsonJdbcQueryContent createJdbcQueryContent() {
		return new JsonJdbcQueryContent();
	}

}
