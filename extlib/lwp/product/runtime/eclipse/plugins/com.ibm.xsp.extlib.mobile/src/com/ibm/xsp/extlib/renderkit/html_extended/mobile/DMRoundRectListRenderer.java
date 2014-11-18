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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * 
 */
public class DMRoundRectListRenderer extends DojoWidgetRenderer {   
    
    
    @Override
    protected void startTag(FacesContext context, ResponseWriter writer, UIComponent component) throws IOException {
        super.startTag(context, writer, component);        
       // writer.writeAttribute("class", "mblFullRectList", null); //$NON-NLS-2$ $NON-NLS-1$
        
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibMobile;
    }

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dojox.mobile.RoundRectList"; // $NON-NLS-1$
    }
    
    @Override
    protected String getTagName() {
        return "ul"; // $NON-NLS-1$
    }

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList) {
            UIDMRoundRectList c = (UIDMRoundRectList)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"transition",c.getTransition()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconBase",c.getIconBase()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconPos",c.getIconPos()); // $NON-NLS-1$
        }
    }    
}
