/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.util;

import javax.faces.context.FacesContext;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.core.Version;
import com.ibm.xsp.stylekit.StyleKitImpl;
import com.ibm.xsp.stylekit.ThemeControl;



/**
 * Some utility used to deal with themes.
 */
public class ThemeUtil {
    
    
    public static final Version ONEUI_NONE  = new Version(0, 0);    // Not OneUI
    public static final Version ONEUI_V1    = new Version(1, 0);    // OneUI v1 - old, should not be used anymore
    public static final Version ONEUI_V2    = new Version(2, 0);    // OneUI v2
    public static final Version ONEUI_V21   = new Version(2, 1);    // OneUI v2.1 (8.5.3 and above)
    public static final Version ONEUI_V3    = new Version(3, 0);    // OneUI v3.0 (9.0.0 and above)
    public static final Version ONEUI_V302  = new Version(3, 0, 2); // OneUI v3.0.2 (9.0.0 and above)
    public static final Version ONEUI_IDX_V13  = new Version(1, 3, 0, "idx");  // $NON-NLS-1$ // OneUI IDX v1.3 (9.0.1 and above)
    
    /**
     * No longer needed - was for pre-853 runtime.
     */
    @Deprecated
    public static String getDefaultRendererType(ThemeControl c, String defaultRenderer) {
        return defaultRenderer;
    }

    public static Version getOneUIVersion(FacesContext context) {
        return getOneUIVersion((FacesContextEx)context);
    }
    public static Version getOneUIVersion(FacesContextEx context) {
        Version v = (Version)context.getAttributes().get("extlib.oneui.Version"); // $NON-NLS-1$
        if(v!=null) {
            return v;
        }
        v = findOneUIVersion(context);
        context.getAttributes().put("extlib.oneui.Version", v); // $NON-NLS-1$
        return v;
    }
    private static Version findOneUIVersion(FacesContextEx ctxEx) {
        for(StyleKitImpl st = (StyleKitImpl)ctxEx.getStyleKit(); st!=null; st=st.getParent()) {
            if(st.getName().startsWith("oneui")) { // $NON-NLS-1$
                String oneui = st.getName();
                if(oneui.equals("oneui")) { // $NON-NLS-1$
                    return ONEUI_V1;
                }
                if(oneui.equals("oneuiv2") || oneui.startsWith("oneuiv2_")) { // $NON-NLS-1$ $NON-NLS-2$
                    return ONEUI_V2;
                }
                if(oneui.equals("oneuiv2.1") || oneui.startsWith("oneuiv2.1_")) { // $NON-NLS-1$ $NON-NLS-2$
                    return ONEUI_V21;
                }
                if(oneui.equals("oneuiv3") || oneui.startsWith("oneuiv3_")) { // $NON-NLS-1$ $NON-NLS-2$
                    return ONEUI_V3;
                }
                if(oneui.equals("oneuiv3.0.2") || oneui.startsWith("oneuiv3.0.2_")) { // $NON-NLS-1$ $NON-NLS-2$
                    return ONEUI_V302;
                }
                if(oneui.equals("oneui_idx_v1.3") || oneui.startsWith("oneui_idx_v1.3_")) { // $NON-NLS-1$ $NON-NLS-2$
                    return ONEUI_IDX_V13;
                }
            }
        }
        // OneUI is not enabled
        return ONEUI_NONE;
    }

    public static boolean isOneUITheme(FacesContextEx context) {
        return getOneUIVersion(context)!=ONEUI_NONE;
    }
    
    public static boolean isOneUIVersion(FacesContext context, int major, int minor, int micro) {
        Version v = getOneUIVersion(context);
        if(v.getMajor()==major && v.getMinor()==minor && v.getMicro()==micro) {
            return true;
        }
        return false;
    }
    public static boolean isOneUIVersion(FacesContext context, int major, int minor) {
        Version v = getOneUIVersion(context);
        if(v.getMajor()==major && v.getMinor()==minor) {
            return true;
        }
        return false;
    }
    
    public static boolean isOneUIVersion(int major, int minor, int micro) {
        return isOneUIVersion(FacesContext.getCurrentInstance(), major, minor, micro);
    }
    public static boolean isOneUIVersion(int major, int minor) {
        return isOneUIVersion(FacesContext.getCurrentInstance(), major, minor);
    }
    
    public static boolean isOneUIVersionAtLeast(FacesContext context, int major, int minor) {
        Version v = getOneUIVersion(context);
        if(v.getMajor()>major) {
            return true;
        }
        if(v.getMajor()==major) {
            if(v.getMinor()>=minor) {
                return true;
            }
        }
        return false;
    }
    public static boolean isOneUIVersionAtLeast(int major, int minor) {
        return isOneUIVersionAtLeast(FacesContext.getCurrentInstance(), major, minor);
    }
}