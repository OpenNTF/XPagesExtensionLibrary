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
import com.ibm.xsp.context.RequestParameters;

/**
 * Basic Mobile request customizer.
 * 
 * @author Philippe Riand
 * @author tony.mcguckin@ie.ibm.com
 */
public class MobilePageCustomizer implements RequestParameters.RunningContextProvider {

    public static final boolean USE_MOBILE_THEME = false;
    
    private FacesContext context;
    
    public MobilePageCustomizer(FacesContext context, RequestParameters parameters) {
        this.context = context;
    }
    
    public FacesContext getFacesContext() {
        return context;
    }

    public boolean isRunningContext(String context) {
        if(StringUtil.equals(context, MobileConstants.MOBILE_CONTEXT)) {
            return true;
        }
        return false;
    }
     
}
          