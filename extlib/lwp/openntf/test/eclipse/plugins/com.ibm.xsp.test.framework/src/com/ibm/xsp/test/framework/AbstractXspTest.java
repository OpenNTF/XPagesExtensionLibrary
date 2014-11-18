/*
 * © Copyright IBM Corp. 2013
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 28-Jul-2010
*/
package com.ibm.xsp.test.framework;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


public abstract class AbstractXspTest extends TestCase {
	private Map<String, String> config = null;
	private Map<String, Object> testLocalVars = null;
	/**
	 * @return returns the description of test being run
	 */
	abstract public String getDescription();

	@Override
	protected void setUp() throws Exception {
		XspTestUtil.printDescription(this, getDescription());
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestProject.tearDown(this);
	}

	public Map<String, String> getConfig(){
		if( null == config ){
			String[][] extraConfig = getExtraConfig();
			config = ConfigUtil.readConfig(extraConfig);
		}
		return config;
	}
	public Map<String, Object> getTestLocalVars(){
		if( null == testLocalVars ){
			testLocalVars = new HashMap<String, Object>();
		}
		return testLocalVars;
	}

	protected String[][] getExtraConfig(){ return new String[0][]; }
}
