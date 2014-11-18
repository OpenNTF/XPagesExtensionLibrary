/*
 * © Copyright IBM Corp. 2012
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
* Date: 21 Mar 2012
* TestClassList.java
* Initial version based off inner class in SampleTestSuite,
* added to that class on 24 Feb 2012.
*/
package com.ibm.xsp.test.framework;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;
/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public final class TestClassList {
    private List<Class<?>> tests = new ArrayList<Class<?>>();
    public TestClassList() {
        super();
    }
    public void addTestSuite(Class<?> testClass){
        tests.add(testClass);
    }
    public List<Class<?>> getTests() {
        return tests;
    }
    /**
     * @param suite
     * @param testClassList
     */
    public static void addAll(TestSuite suite, List<Class<?>> testClassList) {
        for (Class<?> testClass : testClassList) {
            suite.addTestSuite(testClass);
        }
    }
}