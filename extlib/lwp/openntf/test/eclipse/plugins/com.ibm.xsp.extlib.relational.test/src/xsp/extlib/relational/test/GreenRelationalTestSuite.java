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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 30 Jan 2012
* GreenRelationalTestSuite.java
*/
package xsp.extlib.relational.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.xsp.test.framework.TestClassList;
import com.ibm.xsp.test.framework.setup.SkipFileTestSetup;
import com.ibm.xsp.test.framework.setup.SkipFileUsedTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class GreenRelationalTestSuite extends TestSuite {

    public static Test suite() {
        RelationalTestSuite mainSuite = new RelationalTestSuite();
        List<Class<?>> testClasses = RelationalTestSuite.getTestClassList();
        // if necessary replace a class with a green subclass (that will always pass), like so:
        // testClasses.set(testClasses.indexOf(RegisteredDecodeTest.class), GreenRegisteredDecodeTest.class);
        
        TestClassList.addAll(mainSuite, testClasses);
        GreenRelationalTestSuite greenSuite = new GreenRelationalTestSuite();
        greenSuite.addTest(new SkipFileTestSetup(mainSuite,"results/junit-results-relational.txt"));
        greenSuite.addTestSuite(SkipFileUsedTest.class);
        return greenSuite;
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }
}
