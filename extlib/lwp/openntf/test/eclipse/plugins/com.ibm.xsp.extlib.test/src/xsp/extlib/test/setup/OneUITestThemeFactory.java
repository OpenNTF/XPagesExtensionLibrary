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
* OneUITestThemeFactory.java
*/
package xsp.extlib.test.setup;

import java.io.InputStream;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.stylekit.StyleKitFactory;
import com.ibm.xsp.stylekit.StyleKitListFactory;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * This test class is to workaround problems in 
 * the ThemeStyleKitFactory classes in the 9.0.1 versions of the plugins:
 * com.ibm.xsp.theme.oneui.idx
 * com.ibm.xsp.theme.oneuiv302
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class OneUITestThemeFactory implements StyleKitFactory, StyleKitListFactory{
    private static final String[] v302_themes = new String[]{
            "oneuiv3.0.2",
            "oneuiv3.0.2_blue",
            "oneuiv3.0.2_gen2",
            "oneuiv3.0.2_gold",
            "oneuiv3.0.2_green",
            "oneuiv3.0.2_onyx",
            "oneuiv3.0.2_orange",
            "oneuiv3.0.2_pink",
            "oneuiv3.0.2_purple",
            "oneuiv3.0.2_red",
            "oneuiv3.0.2_silver",
        };
    public OneUITestThemeFactory() {
        super();
    }

	@Override
    public InputStream getThemeAsStream(String themeId, int scope) {
        if(scope == StyleKitFactory.STYLEKIT_GLOBAL) {
            // only global themes, not application-specific themes
            if( -1 != XspTestUtil.indexOf(v302_themes, themeId) ){
                String path = "resources/themes/";
                return getThemeFromBundle(path + themeId+".theme");
            }
            if( "oneui_idx_v1.3".equals(themeId) || "oneui_idx_v1.3_base".equals(themeId)){
                String path = "resources/themes/";
                return getThemeFromBundle(path +"oneui_idx_v1.3.theme");
            }
        }
        
        return null;
    }

    @Override
    public InputStream getThemeFragmentAsStream(String themeId, int scope) {
        // no fragments, only full themes.
        if(scope == StyleKitFactory.STYLEKIT_GLOBAL) {
            if( "oneui_idx_v1.3".equals(themeId)  || "oneui_idx_v1.3_base".equals(themeId)){
                FacesContextEx context = FacesContextEx.getCurrentInstance();
                if(null != context && context.isRunningContext("mobile") ){
                    // "mobile" is com.ibm.xsp.extlib.request.MobileConstants.MOBILE_CONTEXT
                    String path = "resources/themes/";
                    return getThemeFromBundle(path + "oneui_idx_v1.3_mobile_renderers_fragment.theme"); //$NON-NLS-1$
                }
            }
        }
        return null;
    }

    @Override
    public String[] getThemeIds() {
        // Not listed here, listed in these classes:
        // com.ibm.xsp.theme.oneui_idx.ThemeStyleKitFactory
        // com.ibm.xsp.theme.oneuiv302.ThemeStyleKitFactory 
        return StringUtil.EMPTY_STRING_ARRAY;
    }

    private InputStream getThemeFromBundle(final String fileName) {
        // The class loader doesn't require the security manager to be enabled...
        // But this requires the bundle to be packaged as a single jar plug-in
        ClassLoader cl = getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(fileName);
        return is;
    }
}
