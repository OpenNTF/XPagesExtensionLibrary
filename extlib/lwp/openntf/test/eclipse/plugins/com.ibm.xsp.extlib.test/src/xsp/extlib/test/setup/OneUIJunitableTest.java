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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 15 Jan 2014
* OneUIJunitableTest.java
*/
package xsp.extlib.test.setup;

import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.stylekit.StyleKitFactory;
import com.ibm.xsp.test.framework.AbstractXspTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class OneUIJunitableTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that the oneui302 and oneui.idx themes can be tested in junit tests";
    }
    public void testOneUIJunitTestable() throws Exception {
        // Note, ExtlibRenderThemeControlTest used to fail with fail messages
        //Problem setting theme to oneuiv3.0.2, current theme is: <empty>
        //Problem setting theme to oneuiv3.0.2_blue, current theme is: <empty>
        //[...]
        //Problem setting theme to oneuiv3.0.2_silver, current theme is: <empty>
        //Problem setting theme to oneui_idx_v1.3, current theme is: <empty>
        //Problem setting theme to oneui_idx_v1.3_base, current theme is: <empty>
        // until the xsp.extlib.test.setup.OneUITestThemeFactory class
        // was added to work around the problems in the classes:
        // com.ibm.xsp.theme.oneui_idx.ThemeStyleKitFactory
        // com.ibm.xsp.theme.oneuiv302.ThemeStyleKitFactory
        // that were preventing junit testing of those themes.
        
        if( /*currentVersion < 9.0.2*/ compareVersions(ExtlibVersion.getCurrentVersionString(), "9.0.2") < 0){
            // current version is 9.0.1 or 9.0.1 openNTF release
            // So will be using the OneUITestThemeFactory to work around the problem
            Class<?> workaroundClass = OneUITestThemeFactory.class;
            workaroundClass.toString();
            return;
        }
        String msg = "ThemeStyleKitFactory still not working in junit tests";
        assertNotNull(msg, (new com.ibm.xsp.theme.oneuiv302.ThemeStyleKitFactory()).getThemeAsStream("oneuiv3.0.2", StyleKitFactory.STYLEKIT_GLOBAL) );
        assertNotNull(msg, (new com.ibm.xsp.theme.oneui_idx.ThemeStyleKitFactory()).getThemeAsStream("oneui_idx_v1.3", StyleKitFactory.STYLEKIT_GLOBAL) );
        try{
            Class.forName("xsp.extlib.test.setup.OneUITestThemeFactory");
            fail("Expect to remove the OneUITestThemeFactory class once the theme factories are fixed, " 
                    + "and remove the reference to it in " 
                    + "com.ibm.xsp.extlib.test/src/META-INF/services/com.ibm.xsp.stylekit.StyleKitFactory");
        }catch(ClassNotFoundException ex){
            // expected, pass
        }
    }
    private int compareVersions(String first, String second) {
        return (new Version(first)).compareToWithQualifier(new Version(second));
    }

}
