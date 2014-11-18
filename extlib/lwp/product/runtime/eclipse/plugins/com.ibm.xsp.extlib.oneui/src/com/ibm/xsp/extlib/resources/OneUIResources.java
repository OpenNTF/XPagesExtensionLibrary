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

package com.ibm.xsp.extlib.resources;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.OneUIv21Resources;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.OneUIv2Resources;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.OneUIv3Resources;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.OneUIv302Resources;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui_idx_v13.OneUIIDXv13Resources;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * Shared OneUI Dojo resources.
 * 
 * @author priand
 *
 */
public abstract class OneUIResources {
    
    public static OneUIResources get() {
        return get(FacesContextEx.getCurrentInstance());
    }
    
    public static OneUIResources get(FacesContextEx context) {
        OneUIResources r = (OneUIResources)context.getAttributes().get("extlib.oneui.Resources"); // $NON-NLS-1$
        if(r!=null) {
            return r;
        }
        Version v = ThemeUtil.getOneUIVersion(context);
        if(v==ThemeUtil.ONEUI_IDX_V13) {
            r = OneUIIDXv13Resources.instance;
        } else if(v==ThemeUtil.ONEUI_V3) {
            r = OneUIv3Resources.instance;
        } else if(v==ThemeUtil.ONEUI_V302) {
            r = OneUIv302Resources.instance;
        } else if(v==ThemeUtil.ONEUI_V21) {
            r = OneUIv21Resources.instance;
        } else {
            // Default to v2
            r = OneUIv2Resources.instance;
        }
        context.getAttributes().put("extlib.oneui.Resources",r); // $NON-NLS-1$
        return r;
    }
    
    
    // Dojo modules
    public static final DojoModuleResource oneUINavigator = new DojoModuleResource("extlib.dijit.OneUINavigator"); // $NON-NLS-1$
    
    public static final DojoModuleResource oneUIDialog = new DojoModuleResource("extlib.dijit.OneUIDialog"); // $NON-NLS-1$

    public static final DojoModuleResource oneUIPickerCheckbox = new DojoModuleResource("extlib.dijit.OneUIPickerCheckbox"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIPickerList = new DojoModuleResource("extlib.dijit.OneUIPickerList"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIPickerListSearch = new DojoModuleResource("extlib.dijit.OneUIPickerListSearch"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIPickerName = new DojoModuleResource("extlib.dijit.OneUIPickerName"); // $NON-NLS-1$

    
    public String BLANK_GIF;
    public String DROPDOWN_PNG;
    public String ICON_ERROR;
    public String ICON_WARN;
    public String ICON_INFO;
    public String ICON_HELP;

    public String VIEW_COLUMN_SORT_NONE;
    public String VIEW_COLUMN_SORT_BOTH_ASCENDING;
    public String VIEW_COLUMN_SORT_BOTH_DESCENDING;
    public String VIEW_COLUMN_SORT_NORMAL;  
    public String VIEW_COLUMN_SORT_REVERSE;

    public OneUIResources() {
        // Use v2 by default
        this.BLANK_GIF      = "/.ibmxspres/domino/oneuiv2/images/blank.gif"; // $NON-NLS-1$
        this.DROPDOWN_PNG   = "/.ibmxspres/domino/oneuiv2/images/btnDropDown2.png"; // $NON-NLS-1$
        this.ICON_ERROR     = "/.ibmxspres/domino/oneuiv2/images/iconError16.png"; // $NON-NLS-1$
        this.ICON_WARN      = "/.ibmxspres/domino/oneuiv2/images/iconWarning16.png"; // $NON-NLS-1$
        this.ICON_INFO      = "/.ibmxspres/domino/oneuiv2/images/iconInfo16.png"; // $NON-NLS-1$
        this.ICON_HELP      = "/.ibmxspres/domino/oneuiv2/images/iconHelp16.png"; // $NON-NLS-1$
        
        // This is not specific to a particular version of OneUI
        this.VIEW_COLUMN_SORT_NONE            = "/.ibmxspres/.extlib/icons/oneui/sort_none.gif"; // $NON-NLS-1$ 
        this.VIEW_COLUMN_SORT_BOTH_ASCENDING  = "/.ibmxspres/.extlib/icons/oneui/sort_both_ascending.gif"; // $NON-NLS-1$ 
        this.VIEW_COLUMN_SORT_BOTH_DESCENDING = "/.ibmxspres/.extlib/icons/oneui/sort_both_descending.gif"; // $NON-NLS-1$ 
        this.VIEW_COLUMN_SORT_NORMAL          = "/.ibmxspres/.extlib/icons/oneui/sort_normal.gif"; // $NON-NLS-1$   
        this.VIEW_COLUMN_SORT_REVERSE         = "/.ibmxspres/.extlib/icons/oneui/sort_reverse.gif"; // $NON-NLS-1$
    }
}
