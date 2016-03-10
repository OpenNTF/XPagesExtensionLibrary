/*
 * © Copyright IBM Corp. 2014, 2015
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

import java.io.InputStream;

import com.ibm.xsp.theme.bootstrap.themes.StyleKitFactory;
import com.ibm.xsp.test.framework.AbstractXspTest;

/**
 *
 */
public class BootstrapJunitableTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that the bootstrap theme can be tested in junit tests";
    }
    public void testBootstrapJunitTestable() throws Exception {
        // Note, some of the other themes had problems loading in junit tests (see OneUIJunitableTest).
        // This test verifies that the bootstrap theme file can be loaded in the junits
        String themeId = "Bootstrap3.2.0";
        com.ibm.xsp.theme.bootstrap.themes.StyleKitFactory themeFactory = new com.ibm.xsp.theme.bootstrap.themes.StyleKitFactory();
        InputStream bootstrapThemeFileStream = themeFactory.getThemeAsStream(themeId, StyleKitFactory.STYLEKIT_GLOBAL);
        assertNotNull("Bootstrap3.2.0 not loading in junit tests", bootstrapThemeFileStream );
    }
}
