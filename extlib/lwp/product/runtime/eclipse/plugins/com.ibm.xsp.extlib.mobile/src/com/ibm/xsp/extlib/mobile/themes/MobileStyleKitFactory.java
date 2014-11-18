/*
 * © Copyright IBM Corp. 2010, 2012
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

package com.ibm.xsp.extlib.mobile.themes;

import java.io.InputStream;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.stylekit.StyleKitFactory;
import com.ibm.xsp.stylekit.StyleKitListFactory;


/**
 * Mobile Theme Factory.
 * @author Philippe Riand
 * @author Tony McGuckin
 */
public class MobileStyleKitFactory implements StyleKitFactory, StyleKitListFactory {

    public MobileStyleKitFactory() {}
    
    public InputStream getThemeAsStream(String themeId, int scope) {
        // Contribute the mobile themes at a global level
        if(scope==StyleKitFactory.STYLEKIT_GLOBAL) {
            String folderPath = "com/ibm/xsp/extlib/mobile/themes/";// $NON-NLS-1$
            if(StringUtil.equals(themeId, "android")) { // $NON-NLS-1$
                return getThemeFromBundle(folderPath + "android.theme"); // $NON-NLS-1$
            } else if(StringUtil.equals(themeId, "iphone")) { // $NON-NLS-1$
                return getThemeFromBundle(folderPath + "iphone.theme"); // $NON-NLS-1$
            } else if(StringUtil.equals(themeId, "blackberry")) { // $NON-NLS-1$
                return getThemeFromBundle(folderPath + "blackberry.theme"); // $NON-NLS-1$
            }
        }
        return null;
    }
    public String[] getThemeIds(){
        return new String[]{
                "android", // $NON-NLS-1$
                "iphone", // $NON-NLS-1$
                "blackberry", // $NON-NLS-1$
        };
    }

    
    public InputStream getThemeFragmentAsStream(String themeId, int scope) {
        // No fragments are contributed
        return null;
    }

    private InputStream getThemeFromBundle(final String fileName) {
        // The class loader doesn't require the security manager to be enabled...
        // But this requires the bundle to be packaged as a single jar plug-in
        ClassLoader cl = getClass().getClassLoader();
        return cl.getResourceAsStream(fileName);
    }
}