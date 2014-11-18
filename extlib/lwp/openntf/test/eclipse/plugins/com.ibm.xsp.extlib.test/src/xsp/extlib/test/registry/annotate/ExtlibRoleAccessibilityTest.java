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
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseRoleAccessibilityTest;

/**
 * @author nsmeta
 *
 */
public class ExtlibRoleAccessibilityTest extends BaseRoleAccessibilityTest {
    private String[] skips = new String[]{
            // not using role property because the base dojo control does not provide one
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:toolBarButton Expected role property for accessibility does not exist.",
    };

    @Override
    protected String[] getSkipFails(){
        return skips;
    }
    
}
