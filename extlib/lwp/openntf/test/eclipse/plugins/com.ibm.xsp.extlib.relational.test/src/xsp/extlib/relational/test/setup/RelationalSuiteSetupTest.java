/*
 * © Copyright IBM Corp. 2014
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
* Date: 28 Sep 2011
* RelationalSuiteSetupTest.java
*/

package xsp.extlib.relational.test.setup;

import xsp.extlib.relational.test.RelationalTestSuite;

import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.BaseSuiteSetupTest;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalSuiteSetupTest extends BaseSuiteSetupTest {

    /**
     * @return
     */
    @Override
    protected String getTestedSuiteName() {
        return XspTestUtil.getShortClass(RelationalTestSuite.class);
    }
    /**
     * @return
     */
    @Override
    protected long getTestedSuiteVersion() {
        return RelationalTestSuite.BASED_ON_SAMPLE_SUITE_VERSION;
    }
}
