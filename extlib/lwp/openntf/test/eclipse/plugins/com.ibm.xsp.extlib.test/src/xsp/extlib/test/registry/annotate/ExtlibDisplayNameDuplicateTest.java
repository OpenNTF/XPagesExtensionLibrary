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
* Date: 23 Nov 2012
* ExtlibDisplayNameDuplicateTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.BaseDisplayNameDuplicateTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibDisplayNameDuplicateTest extends BaseDisplayNameDuplicateTest {

    private String[] temporarySkips = new String[]{
            // TODO in 9.0.0 invented a new WidgetBaseEx component-type,
            // same as the WidgetBase component-type except without a tooltip.
            // For 9.0.0 it is too late to translate a different description
            // for the new base control, so reusing the parent description
            // After 9.0.0 should provide a new base description for the Ex component-type
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config xe:com.ibm.xsp.extlib.dojo.WidgetBase  Duplicate def description/display-name. Existing: com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:com.ibm.xsp.extlib.dojo.WidgetBaseEx(Dojo Widget Base|\"The base Dojo widget that all widgets inherit from\")",
    };
    @Override
    protected String[] getSkips() {
        String[] arr = super.getSkips();
        // "9.0.0.v00_00" < CurrentRuntimeVersion
        if( 0 < Version.parseVersion("9.0.0.v00_00").compareToWithQualifier(Version.parseVersion(ExtlibVersion.getCurrentVersionString())) ){
            arr = XspTestUtil.concat(arr, temporarySkips);
        }
        return arr;
    }
}
