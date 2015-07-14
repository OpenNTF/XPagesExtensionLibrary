/*
 * © Copyright IBM Corp. 2015
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
* Date: 8 Apr 2015
* ExtlibRegisteredDecodeTest.java
*/
package xsp.extlib.test.lifecycle;

import com.ibm.xsp.test.framework.lifecycle.BaseRegisteredDecodeTest;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class ExtlibRegisteredDecodeTest extends BaseRegisteredDecodeTest {

    private String[] skips = new String[]{
            // these 3 new controls are bootstrap-specific and only work in the bootstrap themes. 
            "com/ibm/xsp/theme/bootstrap/config/extlib-bootstrap.xsp-config xe:dashboard decode() throws: com.ibm.commons.util.NotImplementedException: This control is not implemented for the current theme (null) and render-kit(HTML_BASIC), try a different theme, perhaps bootstrap.",
            "com/ibm/xsp/theme/bootstrap/config/extlib-bootstrap.xsp-config xe:navbar decode() throws: com.ibm.commons.util.NotImplementedException: This control is not implemented for the current theme (null) and render-kit(HTML_BASIC), try a different theme, perhaps bootstrap.",
            "com/ibm/xsp/theme/bootstrap/config/extlib-bootstrap.xsp-config xe:carousel decode() throws: com.ibm.commons.util.NotImplementedException: This control is not implemented for the current theme (null) and render-kit(HTML_BASIC), try a different theme, perhaps bootstrap.",
    };
    @Override
    protected String[] getSkips() {
        return skips;
    }
}
