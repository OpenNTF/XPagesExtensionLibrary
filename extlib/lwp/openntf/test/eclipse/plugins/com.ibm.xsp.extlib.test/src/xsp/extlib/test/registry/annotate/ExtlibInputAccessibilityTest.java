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

import com.ibm.xsp.test.framework.registry.annotate.BaseInputAccessibilityTest;

/**
 * @author nsmeta
 *
 */
public class ExtlibInputAccessibilityTest extends BaseInputAccessibilityTest {
    private String[] skips = new String[]{
            // not using accesskey property because the base dojo control does not provide one
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:toolBarButton Expected accesskey attribute in input tag for accessibility does not exist.",
            // not using tabindex property because the base dojo control does not provide one but provides tabIndex(capital i)
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:toolBarButton Expected tabindex attribute in input tag for accessibility does not exist.",
            // Widget Container is a container type control which can be collapsed or expanded; as such the anchor responsible for this supports both these properties.
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config xe:widgetContainer Expected accesskey attribute in input tag for accessibility does not exist.",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config xe:widgetContainer Expected tabindex attribute in input tag for accessibility does not exist."
    };
    
    @Override
    protected String[] getSkipFails() {
        return skips;
    }
}
