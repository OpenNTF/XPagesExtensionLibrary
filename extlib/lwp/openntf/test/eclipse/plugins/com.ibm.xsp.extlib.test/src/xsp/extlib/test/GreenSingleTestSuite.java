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
* Date: 22 Mar 2012
* GreenSingleTestSuite.java
*/
package xsp.extlib.test;

import com.ibm.xsp.test.framework.registry.annotate.BaseTableAccessibilityTest;
import com.ibm.xsp.test.framework.setup.SkipFileTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class GreenSingleTestSuite extends TestSuite {

    public static Test suite() { 
        TestSuite mainSuite = new TestSuite();
        
        // change this test to whichever test you need
        // to run with the SkipFileTestSetup in place
        mainSuite.addTestSuite(BaseTableAccessibilityTest.class);
        
        GreenSingleTestSuite greenSuite = new GreenSingleTestSuite();
        greenSuite.addTest(new SkipFileTestSetup(mainSuite,"results/junit-results.txt"));
        return greenSuite;
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }

}
