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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui;

import com.ibm.xsp.extlib.resources.OneUIResources;


/**
 * Shared OneUI V2.1 Dojo resources.
 * 
 * @author priand
 *
 */
public class OneUIv21Resources extends OneUIResources {
    
    public static final OneUIv21Resources instance = new OneUIv21Resources();
    
    public OneUIv21Resources() {
        this.BLANK_GIF      = "/.ibmxspres/domino/oneuiv2.1/images/blank.gif"; // $NON-NLS-1$
        
        // The following images no longer exist in OneUIv2.1 -> we default to OneUIv2
//        this.DROPDOWN_PNG   = "/.ibmxspres/domino/oneuiv2/images/btnDropDown2.png"; // $NON-NLS-1$
//        this.ICON_ERROR     = "/.ibmxspres/domino/oneuiv2/images/iconError16.png"; // $NON-NLS-1$
//        this.ICON_WARN      = "/.ibmxspres/domino/oneuiv2/images/iconWarning16.png"; // $NON-NLS-1$
//        this.ICON_INFO      = "/.ibmxspres/domino/oneuiv2/images/iconInfo16.png"; // $NON-NLS-1$
//        this.ICON_HELP      = "/.ibmxspres/domino/oneuiv2/images/iconHelp16.png"; // $NON-NLS-1$
    }
}
