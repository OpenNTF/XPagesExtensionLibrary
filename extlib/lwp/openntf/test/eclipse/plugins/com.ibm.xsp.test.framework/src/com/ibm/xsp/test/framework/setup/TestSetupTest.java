/*
 * © Copyright IBM Corp. 2011, 2013
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
* Date: 21 Apr 2011
* CoreTestConfigTest.java
*/
package com.ibm.xsp.test.framework.setup;

import com.ibm.xsp.test.framework.AbstractXspTest;


/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TestSetupTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that this ..xsp.test JUnit test plugin is correctly configured";
    }
    public void testConfigurationOfTestProject() throws Exception {
        // do nothing.
        // When the junit framework supported running against both 8.5.2
        // and 8.5.3, there was code here to verify that the 8.5.3 test classes
        // were available. The test framework no longer supports running in 8.5.2.
        // May need to use this test again, if need to run in multiple versions.
    }
}
