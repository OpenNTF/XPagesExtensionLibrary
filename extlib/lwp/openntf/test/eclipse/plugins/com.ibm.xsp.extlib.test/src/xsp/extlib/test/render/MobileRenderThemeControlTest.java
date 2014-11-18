/*
 * © Copyright IBM Corp. 2013, 2014
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
* Date: 22 Nov 2013
* MobileRenderThemeControlTest.java
*/
package xsp.extlib.test.render;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.extlib.mobile.themes.MobileStyleKitFactory;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.BaseRenderThemeControlTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class MobileRenderThemeControlTest extends BaseRenderThemeControlTest {
    @Override
    public String getDescription() {
        // testing all controls render in mobile,
        // i.e. including the XPages runtime controls, not just the Extlib controls.
        return "that *all* controls render in the default mobile themes (android iphone blackberry) without Exceptions";
    }

    @Override
    protected String[][] getExtraConfig() {
        String[][] extra = super.getExtraConfig();
        
        extra = XspTestUtil.concat(extra, new String[][]{
                // Test all controls in the registry, not just the ExtLib controls
                {"target.all", "true"},
                {"target.library", null},
                // ignore the _blue, etc files.
                {"RenderThemeControl.ignoreFilesWithUnderscore", "true"},
                // not require the oneui302 themes [Required by default]
                {"RenderThemeControlTest.requireOneui302Theme", "false"},
                // Do require the android, iphone, blackberry themes [Not tested by default]
                {"RenderThemeControlTest.requireMobileThemes", "true"},
                // the list of libraries whose faces-config.xml files should be loaded
                {"extra.library.depends.runtime", "com.ibm.xsp.extlib.library"}
        });
        return extra;
    }

    @Override
    protected List<String> detectThemeFileIds(File themesFolder) {
        // Detect the theme files in: C:\Domino\xsp\nsf\themes
        List<String> detected = super.detectThemeFileIds(themesFolder);
        if( ! detected.contains("oneuiv2.1") ){
            throw new RuntimeException("Unexpected absent theme: oneuiv2.1");
        }
        // Do not test all the theme files, only the oneuiv2.1 file.
        ArrayList<String> list = new ArrayList<String>();
        list.add("oneuiv2.1");
        return list;
    }

    @Override
    protected List<String> computeContributedThemes(List<String> themeFileIds) {
        // call to superclass verifies mobile themes are contributed (and finds all contributed themes, unused)
        super.computeContributedThemes(themeFileIds);
        
        // only test the mobile themes, not all the contributed themes.
        // Also testing null theme (defaults to webstandard), and oneuiv2.1.
        // Testing null so can see if the any exceptions are also present in webstandard.
        // Testing oneuiv2.1 so the system.out.printlns show it as contrast to the mobile themes.
        List<String> defaultMobileThemes = Arrays.asList((new MobileStyleKitFactory()).getThemeIds());
        
        return defaultMobileThemes;
    }
    
}
