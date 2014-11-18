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

package com.ibm.xsp.extlib.request;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.context.RequestParameters;

/**
 * BlackBerry request customizer.
 * 
 * @author Philippe Riand
 * @author tony.mcguckin@ie.ibm.com
 */
public class BBerryPageCustomizer extends MobilePageCustomizer {

    public BBerryPageCustomizer(FacesContext context, RequestParameters parameters) {
        super(context, parameters);

        // WARN: we cannot read the property from FacesContext as goes to
        // infinite
        // recursion, because FacesContext also relies on the request parameters
        // -> we directly read the property from the application object, which
        // can be then set at either the application or server level.
        ApplicationEx app = ApplicationEx.getInstance(context);
        String s = app.getApplicationProperty(MobileConstants.XSP_THEME_MOBILE_BBERRY, null);

        if (StringUtil.isNotEmpty(s)) {
            parameters.setProperty(MobileConstants.XSP_THEME_WEB, s);
        }
        else {
            s = app.getApplicationProperty(MobileConstants.XSP_THEME_MOBILE, BBerryConstants.BBERRY_THEME_NAME);
            parameters.setProperty(MobileConstants.XSP_THEME_WEB, s);
        }
    }

    @Override
    public boolean isRunningContext(String context) {
        if (StringUtil.equals(context, BBerryConstants.BBERRY_CONTEXT)) {
            return true;
        }
        return super.isRunningContext(context);
    }
}
