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
* Date: 14 Jun 2013
* ExtlibPropertiesHaveCategoriesTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BasePropertiesHaveCategoriesTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibPropertiesHaveCategoriesTest extends
        BasePropertiesHaveCategoriesTest {
    private String[] _skips = new String[]{
            // In 9.0N_03/9.0.1, all of the references to the extlib aria role group were replaced with references
            // to the group: com.ibm.xsp.extlib.group.aria.role.deprecated, so the original group became unused.
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config com.ibm.xsp.extlib.group.aria_role Unused group, cannot check <property>s for <category>s",
    };
    @Override
    protected String[] getSkips() {
        return _skips;
    }
}
