 /*
 * © Copyright IBM Corp. 2011
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 19 Sep 2011
* ExtlibJsIdUtil.java
*/

package com.ibm.xsp.extlib.component.domino;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.util.FacesUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibJsIdUtil {

    public static String findDojoWidgetId(FacesContext context, UIComponent from, String componentId) {
        if(StringUtil.isNotEmpty(componentId)) {
            UIComponent sc = FacesUtil.getComponentFor(from, componentId);
            if( null == sc ){
                return null;
            }
            if(!(sc instanceof FacesExtlibJsIdWidget)) {
                Object jsId = sc.getAttributes().get("jsId"); //$NON-NLS-1$
                if( jsId instanceof String ){
                    return (String)jsId;
                }
            }
            return ((FacesExtlibJsIdWidget)sc).getDojoWidgetJsId(context);
        }
        return null;
    }

    /**
     * @param context
     * @return
     */
    public static String getClientIdAsJsId(UIComponent component, FacesContext context) {
        String jsId = component.getClientId(context);
        return jsId.replaceAll("[:.,-]", "_");
    }

}
