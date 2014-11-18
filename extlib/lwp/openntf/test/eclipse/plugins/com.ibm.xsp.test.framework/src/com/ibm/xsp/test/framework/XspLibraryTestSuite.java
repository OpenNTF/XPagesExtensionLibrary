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
* Date: 29-Jul-2010
*/
package com.ibm.xsp.test.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.xsp.test.framework.registry.BasePropertiesHaveSettersTest;
import com.ibm.xsp.test.framework.registry.parse.StrictParserTest;

public class XspLibraryTestSuite extends TestSuite {
	private Class<?>[] testClasses = new Class[]{
		BasePropertiesHaveSettersTest.class,
		StrictParserTest.class,
	};
	private List<Class<?>> classesList;

	public XspLibraryTestSuite() {
		super();
	}
	public void init(){
		for (Class<?> testClass : getTestClasses()) {
			addTestSuite(testClass);
		}
	}
	public List<Class<?>> getTestClasses() {
		if( null == classesList ){
			classesList = new ArrayList<Class<?>>();
			classesList.addAll(Arrays.asList(testClasses));
		}
		return classesList;
	}
	
  public static Test suite() { 
      XspLibraryTestSuite suite = new XspLibraryTestSuite();
      suite.init();
      return suite; 
  }
  public static void main(String args[]) { 
      junit.textui.TestRunner.run(suite());
  }
}
