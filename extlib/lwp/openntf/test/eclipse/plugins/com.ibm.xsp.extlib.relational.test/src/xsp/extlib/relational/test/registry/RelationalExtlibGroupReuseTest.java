/*
 * © Copyright IBM Corp. 2012, 2014
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
* Date: 28 May 2014
* RelationalExtlibGroupReuseTest.java
*/
package xsp.extlib.relational.test.registry;

import xsp.extlib.test.registry.BaseExtlibGroupReuseTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalExtlibGroupReuseTest extends BaseExtlibGroupReuseTest {
    private String[] _skipFails = new String[]{
        // There was a bug in this test - the suggestion is a complex-type group so it could not be used by this control anyway:
            // The sqlParamater value has a different name & description to the ValueHolder value
            "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config xe:sqlParameter value Should reuse <group-type-ref> for an existing complex group: com.ibm.xsp.extlib.group.ValueHolder_complex.prop.value",
            
    };
    @Override
    protected String[] getSkipFails() {
        return _skipFails;
    }
}

