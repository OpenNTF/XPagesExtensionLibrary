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

package com.ibm.xsp.extlib.renderkit.html_extended.dialog;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dialog.UITooltipDialog;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;


public class TooltipDialogRenderer extends DialogRenderer {

    @Override
    protected String getPlaceHolderWrapperType() {
        return "extlib.dijit._TooltipDialogWrapper"; // $NON-NLS-1$
    }
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.TooltipDialog"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibTooltipDialog;
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UITooltipDialog) {
            UITooltipDialog c = (UITooltipDialog)dojoComponent;
            String _for = c.getFor();
            if(StringUtil.isNotEmpty(_for)) {
                UIComponent uc = FacesUtil.getComponentFor(c,_for);
                if(uc!=null) {
                    DojoRendererUtil.addDojoHtmlAttributes(attrs,"for",uc.getClientId(context)); // $NON-NLS-1$
                }
            }
            
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel()); // $NON-NLS-1$
        }
    }   
}