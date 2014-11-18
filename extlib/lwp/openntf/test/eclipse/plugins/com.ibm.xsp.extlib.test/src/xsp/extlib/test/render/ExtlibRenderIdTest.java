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
* Date: 4 Oct 2012
* ExtlibRenderIdTest.java
*/
package xsp.extlib.test.render;

import com.ibm.xsp.test.framework.render.BaseRenderIdTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibRenderIdTest extends BaseRenderIdTest {
    private String[] skips = new String[]{
            // for SPR#BGLN8XZJTK the xe:tagCloud will always render an id attribute,
            // even where the id property was absent in the xpage source.
            // The id is needed for the slider control to work. 
            "com/ibm/xsp/extlib/config/extlib-tagcloud.xsp-config xe:tagCloud Unexpected id= attribute in HTML. Should not be present when the id property is absent in the XPage source, and there is no default dojoType.",
    };
    @Override
    protected String[] getSkipFails() {
        return skips;
    }

}
