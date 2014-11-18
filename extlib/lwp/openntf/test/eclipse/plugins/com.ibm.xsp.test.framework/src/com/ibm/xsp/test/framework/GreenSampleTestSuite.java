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
* GreenSampleTestSuite.java
*/
package com.ibm.xsp.test.framework;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.test.framework.setup.SkipFileTestSetup;
import com.ibm.xsp.test.framework.setup.SkipFileUsedTest;

/**
 * This is a sample showing how you might implement a Green*TestSuite,
 * using the SkipFileTestSetup class, which takes a .txt file containing
 * a junit failure list, and saves the set of fails from that file statically,
 * so that the fails corresponding to a given test may be accessed throug
 * {@link SkipFileContent#getSkips(String, String)}.
 * Tests that support the Green mechanism, would then invoke that method
 * to skip the reporting of known fails that are present in the .txt file,
 * so that the Green suite of tests would only fail for new problems that
 * are not already listed in the .txt file.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class GreenSampleTestSuite extends TestSuite {

    public static Test suite(){ 
        SampleTestSuite suite = new SampleTestSuite();
        List<Class<?>> testClasses = SampleTestSuite.getTestClassList();
        // if necessary replace a class with a green subclass (that will always pass), like so:
        // testClasses.set(testClasses.indexOf(RegisteredDecodeTest.class), GreenRegisteredDecodeTest.class);
        
        TestClassList.addAll(suite, testClasses);
        GreenSampleTestSuite greenSuite = new GreenSampleTestSuite();
        greenSuite.addTest(new SkipFileTestSetup(suite,"results/junit-results.txt"));
        greenSuite.addTestSuite(SkipFileUsedTest.class);
        return greenSuite;
    }

    public static void main(String args[]) 
    { 
        junit.textui.TestRunner.run(suite());
    }
}
