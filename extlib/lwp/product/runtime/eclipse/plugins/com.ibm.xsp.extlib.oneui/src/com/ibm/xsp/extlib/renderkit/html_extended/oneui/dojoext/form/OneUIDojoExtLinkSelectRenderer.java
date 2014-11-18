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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.dojoext.form;

import com.ibm.xsp.extlib.renderkit.dojoext.form.DojoExtLinkSelectRenderer;

public class OneUIDojoExtLinkSelectRenderer extends DojoExtLinkSelectRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LISTSTYLE:            return "display: inline;"; // $NON-NLS-1$
            case PROP_LISTCLASS:            return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_FIRSTITEMCLASS:       return "lotusFirst"; // $NON-NLS-1$
            //case PROP_LASTITEMSTYLE:      return "padding-right: 0";
            //LHEY97KEXJ - changing the styling of this control to remove reliance on colour
            //old version is commented out in the 2 lines below
            //case PROP_ENABLEDLINKSTYLE:     return "font-weight: bold;"; // $NON-NLS-1$
            //case PROP_DISABLEDLINKSTYLE:    return "color: rgb(128, 128, 128); font-weight: normal;"; // $NON-NLS-1$
            case PROP_ENABLEDLINKSTYLE:     return "font-weight: normal;"; // $NON-NLS-1$
            case PROP_DISABLEDLINKSTYLE:    return "font-weight: bold;"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}