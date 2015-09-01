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
package com.ibm.xsp.theme.bootstrap.resources;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.resource.DojoModuleResource;

public abstract class Resources {
    
    public String BLANK_GIF;
    public String VIEW_COLUMN_SORT_NONE;
    public String VIEW_COLUMN_SORT_BOTH_ASCENDING;
    public String VIEW_COLUMN_SORT_BOTH_DESCENDING;
    public String VIEW_COLUMN_SORT_NORMAL;
    public String VIEW_COLUMN_SORT_REVERSE;

    //We can add new icon fonts here in the future if we need to
    //For now only glyph icon is used
    public static final int GLYPH_ICON              = 0;
    public int DEFAULT_ICON                         = GLYPH_ICON;

    public static Resources get() {
        return get(FacesContextEx.getCurrentInstance());
    }

    public static Resources get(FacesContextEx context) {
        Resources r = (Resources) context.getAttributes().get("xsp.responsive.bootstrap.Resources"); // $NON-NLS-1$
        if (r != null) {
            return r;
        }
        r = BootstrapResources.instance;
        context.getAttributes().put("xsp.responsive.bootstrap.Resources", r); // $NON-NLS-1$
        return r;
    }

    // Dojo module
    public static final DojoModuleResource bootstrapNavigator = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.Navigator"); // $NON-NLS-1$
    public static final DojoModuleResource bootstrapDialog = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.Dialog"); // $NON-NLS-1$

    public static final DojoModuleResource bootstrapTooltip = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.Tooltip"); // $NON-NLS-1$

    public static final DojoModuleResource bootstrapListTextBox = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.ListTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource bootstrapNameTextBox = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.NameTextBox"); // $NON-NLS-1$

    public static final DojoModuleResource bootstrapPickerCheckbox = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.PickerCheckbox"); // $NON-NLS-1$
    public static final DojoModuleResource bootstrapPickerList = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.PickerList"); // $NON-NLS-1$
    public static final DojoModuleResource bootstrapPickerListSearch = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.PickerListSearch"); // $NON-NLS-1$
    public static final DojoModuleResource bootstrapPickerName = new DojoModuleResource("extlib.responsive.dijit.xsp.bootstrap.PickerName"); // $NON-NLS-1$

    public Resources() {
        this.BLANK_GIF = "/.ibmxspres/.extlib/responsive/xpages/img/blank.gif"; // $NON-NLS-1$
        this.VIEW_COLUMN_SORT_NONE = "/.ibmxspres/.extlib/responsive/xpages/img/sort_none.gif"; // $NON-NLS-1$
        this.VIEW_COLUMN_SORT_BOTH_ASCENDING = "/.ibmxspres/.extlib/responsive/xpages/img/sort_both_ascending.gif"; // $NON-NLS-1$
        this.VIEW_COLUMN_SORT_BOTH_DESCENDING = "/.ibmxspres/.extlib/responsive/xpages/img/sort_both_descending.gif"; // $NON-NLS-1$
        this.VIEW_COLUMN_SORT_NORMAL = "/.ibmxspres/.extlib/responsive/xpages/img/sort_normal.gif"; // $NON-NLS-1$
        this.VIEW_COLUMN_SORT_REVERSE = "/.ibmxspres/.extlib/responsive/xpages/img/sort_reverse.gif"; // $NON-NLS-1$
    }

    public String getIconClass(String iconName) {
        return getIconClass(iconName, DEFAULT_ICON);
    }
    
    public String getIconClass(String iconName, int type) {
        if(type == GLYPH_ICON) {
            return "glyphicon glyphicon-" + iconName; // $NON-NLS-1$
        }
        return iconName;
    }
}
