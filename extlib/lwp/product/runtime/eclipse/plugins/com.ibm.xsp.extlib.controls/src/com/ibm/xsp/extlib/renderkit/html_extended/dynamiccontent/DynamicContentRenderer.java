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

package com.ibm.xsp.extlib.renderkit.html_extended.dynamiccontent;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicContent;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.util.JavaScriptUtil;


public class DynamicContentRenderer extends DynamicControlRenderer {

    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        super.encodeBegin(context, component);
    
        UIDynamicContent dynFacets = (UIDynamicContent)component;
        
        // Add the dojo module
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibDynamicContent);
                
    	// 1.6.1 dojo.hash creates IFrame without any title assigned
    	ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dojoIFrameAdjuster);
    	
        boolean useHash = dynFacets.isUseHash();
        if(useHash) {
            UIScriptCollector col = UIScriptCollector.find();
            StringBuilder b = new StringBuilder();
            b.append("XSP.registerHash("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, dynFacets.getClientId(context));
            b.append(")");
            col.addScriptOnLoad(b.toString());
        }
        
        // Update the HashString if necessary
        if(useHash) {
            String hashString = dynFacets.getHashString();
            if(hashString!=null) {
                UIScriptCollector col = UIScriptCollector.find();
                StringBuilder b = new StringBuilder();
                b.append("XSP.updateHash("); // $NON-NLS-1$
                JavaScriptUtil.addString(b,hashString);
                b.append(")");
                col.addScriptOnLoad(b.toString());
            }
        }
    }
}