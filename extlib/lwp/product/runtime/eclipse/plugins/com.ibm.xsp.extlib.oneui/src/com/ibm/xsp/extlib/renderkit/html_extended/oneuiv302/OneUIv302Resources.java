/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302;

import com.ibm.xsp.extlib.resources.OneUIResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.StyleSheetResource;


/**
 * Shared OneUI 3.0.2 Dojo resources.
 * 
 * @author priand
 *
 */
public class OneUIv302Resources extends OneUIResources {
    
    public static final OneUIv302Resources instance = new OneUIv302Resources();
    public static final DojoModuleResource oneUIv302Dialog = new DojoModuleResource("extlib.dijit.OneUIv302Dialog"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIv302PickerCheckbox = new DojoModuleResource("extlib.dijit.OneUIv302PickerCheckbox"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIv302PickerList = new DojoModuleResource("extlib.dijit.OneUIv302PickerList"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIv302PickerListSearch = new DojoModuleResource("extlib.dijit.OneUIv302PickerListSearch"); // $NON-NLS-1$
    public static final DojoModuleResource oneUIv302PickerName = new DojoModuleResource("extlib.dijit.OneUIv302PickerName"); // $NON-NLS-1$
    public static final DojoModuleResource extlibMenu = new DojoModuleResource("extlib.dijit.OneUIv302Menu");   // $NON-NLS-1$
    public static final DojoModuleResource extlibMenuItem = new DojoModuleResource("extlib.dijit.OneUIv302MenuItem");   // $NON-NLS-1$
    
    public static final StyleSheetResource oneUIv302xspCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/oneui302xsp.css");  // $NON-NLS-1$
    public static final StyleSheetResource oneUIv302xspRTLCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/oneui302xspRTL.css");  // $NON-NLS-1$

    public OneUIv302Resources() {
        this.BLANK_GIF      = "/.ibmxspres/.oneuiv302/oneui/css/images/blank.gif"; // $NON-NLS-1$
        this.DROPDOWN_PNG   = "/.ibmxspres/.oneuiv302/oneuicompat/btnDropDown2.png"; // $NON-NLS-1$
        this.ICON_ERROR     = "/.ibmxspres/.oneuiv302/oneuicompat/iconError16.png"; // $NON-NLS-1$
        this.ICON_WARN      = "/.ibmxspres/.oneuiv302/oneuicompat/iconWarning16.png"; // $NON-NLS-1$
        this.ICON_INFO      = "/.ibmxspres/.oneuiv302/oneuicompat/iconInfo16.png"; // $NON-NLS-1$
        this.ICON_HELP      = "/.ibmxspres/.oneuiv302/oneuicompat/iconHelp16.png"; // $NON-NLS-1$
    }
}
