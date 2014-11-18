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
* Date: 5 Oct 2014
* BootstrapJunitableTest.java
*/
package xsp.extlib.test.setup;

import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.theme.bootstrap.themes.StyleKitFactory;
import com.ibm.xsp.test.framework.AbstractXspTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BootstrapJunitableTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that the bootstrap theme can be tested in junit tests";
    }
    public void testBootstrapJunitTestable() throws Exception {
        // Note, ExtlibRenderThemeControlTest used to fail with fail messages
        //Problem setting theme to TwitterBootstrap, current theme is: <empty>
        // until the xsp.extlib.test.setup.BootstrapTestThemeFactory class
        // was added to work around the problems in the class:
        // com.ibm.xsp.theme.twitter.bootstrap.ThemeStyleKitFactory
        // that was preventing junit testing of those themes.
        
        if( /*currentVersion < 9.0.2*/ compareVersions(ExtlibVersion.getCurrentVersionString(), "9.0.2") < 0){
            // current version is 9.0.1 or 9.0.1 openNTF release
            // So will be using the OneUITestThemeFactory to work around the problem
            Class<?> workaroundClass = BootstrapTestThemeFactory.class;
            workaroundClass.toString();
            return;
        }
        String msg = "ThemeStyleKitFactory still not working in junit tests";
        assertNotNull(msg, (new com.ibm.xsp.theme.bootstrap.themes.StyleKitFactory()).getThemeAsStream("TwitterBootstrap", StyleKitFactory.STYLEKIT_GLOBAL) );
        try{
            Class.forName("xsp.extlib.test.setup.BootstrapTestThemeFactory");
            fail("Expect to remove the BootstrapTestThemeFactory class once the theme factories are fixed, " 
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
