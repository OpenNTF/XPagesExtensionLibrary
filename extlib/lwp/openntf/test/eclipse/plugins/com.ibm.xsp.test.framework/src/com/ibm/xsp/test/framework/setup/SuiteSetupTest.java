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
* Date: 2011-Jul-14
* SuiteSetupTest.java
*/
package com.ibm.xsp.test.framework.setup;

import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.SampleTestSuite;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SuiteSetupTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that the "+getTestedSuiteName()+" is up to date, " 
                + "reflecting the current state of "+XspTestUtil.getShortClass(SampleTestSuite.class);
    }
    public void testSuiteVersion() throws Exception {
        
        long expectedSuiteVersion = SampleTestSuite.SUITE_VERSION;
        long actualSuiteVersion = getTestedSuiteVersion();
        if( expectedSuiteVersion != actualSuiteVersion ){
            String failMsg = "The "
                    + getTestedSuiteName()
                    + " is out of date "
                    + "- version does not match the version of "
                    + XspTestUtil.getShortClass(SampleTestSuite.class) + ".";
            assertEquals(failMsg, expectedSuiteVersion, actualSuiteVersion);
        }
    }
    protected String getTestedSuiteName() {
        throw new UnsupportedOperationException("getTestedSuiteName() "
                + "method must be overridden in a subclass.");
    }

    protected long getTestedSuiteVersion() {
        throw new UnsupportedOperationException("getTestedSuiteVersion() "
                + "method must be overridden in a subclass.");
    }

}
