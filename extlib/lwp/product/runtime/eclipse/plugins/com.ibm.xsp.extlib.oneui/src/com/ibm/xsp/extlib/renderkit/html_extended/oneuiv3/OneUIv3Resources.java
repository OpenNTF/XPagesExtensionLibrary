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

package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3;

import com.ibm.xsp.extlib.resources.OneUIResources;


/**
 * Shared OneUI Dojo resources.
 * 
 * @author priand
 *
 */
public class OneUIv3Resources extends OneUIResources {
    
    public static final OneUIv3Resources instance = new OneUIv3Resources();

    public OneUIv3Resources() {
        this.BLANK_GIF      = "/.ibmxspres/.oneuiv3/oneui/css/images/blank.gif"; // $NON-NLS-1$
        this.DROPDOWN_PNG   = "/.ibmxspres/.oneuiv3/oneuicompat/btnDropDown2.png"; // $NON-NLS-1$
        this.ICON_ERROR     = "/.ibmxspres/.oneuiv3/oneuicompat/iconError16.png"; // $NON-NLS-1$
        this.ICON_WARN      = "/.ibmxspres/.oneuiv3/oneuicompat/iconWarning16.png"; // $NON-NLS-1$
        this.ICON_INFO      = "/.ibmxspres/.oneuiv3/oneuicompat/iconInfo16.png"; // $NON-NLS-1$
        this.ICON_HELP      = "/.ibmxspres/.oneuiv3/oneuicompat/iconHelp16.png"; // $NON-NLS-1$
    }
}
