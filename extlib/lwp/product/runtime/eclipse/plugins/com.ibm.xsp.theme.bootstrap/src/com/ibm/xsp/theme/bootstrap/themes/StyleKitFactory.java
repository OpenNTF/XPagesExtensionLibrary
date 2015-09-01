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
package com.ibm.xsp.theme.bootstrap.themes;

import java.io.InputStream;

public class StyleKitFactory implements com.ibm.xsp.stylekit.StyleKitFactory, com.ibm.xsp.stylekit.StyleKitListFactory {

    public StyleKitFactory() {
    }

    @Override
    public InputStream getThemeAsStream(String themeId, int scope) {
        if (scope == StyleKitFactory.STYLEKIT_GLOBAL) {
            String folderPath = "com/ibm/xsp/theme/bootstrap/themes"; //$NON-NLS-1$
            if (themeId.equalsIgnoreCase("Bootstrap3.2.0")) { //$NON-NLS-1$
                return getThemeFromBundle(folderPath + "/Bootstrap3.2.0.theme"); //$NON-NLS-1$
            }else if(themeId.equalsIgnoreCase("Bootstrap3.2.0_flat")) { //$NON-NLS-1$
                return getThemeFromBundle(folderPath + "/Bootstrap3.2.0_flat.theme"); //$NON-NLS-1$
            }
        }
        return null;
    }

    @Override
    public InputStream getThemeFragmentAsStream(String themeId, int scope) {
        if (scope == StyleKitFactory.STYLEKIT_GLOBAL) {
            String folderPath = "com/ibm/xsp/theme/bootstrap/themes"; //$NON-NLS-1$
            if (themeId.equalsIgnoreCase("Bootstrap3.2.0") || themeId.equalsIgnoreCase("Bootstrap3.2.0_flat")) { //$NON-NLS-1$ $NON-NLS-2$
                return getThemeFromBundle(folderPath + "/Bootstrap3.2.0_extlib.theme"); //$NON-NLS-1$
            }
        }
        return null;
    }

    private InputStream getThemeFromBundle(final String fileName) {
        ClassLoader cl = getClass().getClassLoader();
        return cl.getResourceAsStream(fileName);
    }
    
    @Override
    public String[] getThemeIds(){
        return new String[]{
            "Bootstrap3.2.0", // $NON-NLS-1$
            "Bootstrap3.2.0_flat" // $NON-NLS-1$
        };
    }
}