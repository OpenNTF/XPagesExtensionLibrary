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

package com.ibm.xsp.extlib.renderkit.dojo.layout;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


public class DojoStackContainerRenderer extends DojoLayoutRenderer {
    
    private static final String _SELECTION = "_sel";  // $NON-NLS-1$
    
    protected boolean isTrackClientSelection() {
        return true;
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(isTrackClientSelection()) {
            String fieldId = component.getClientId(context) + _SELECTION; 
            String selectedTab = (String)context.getExternalContext().getRequestParameterMap().get(fieldId);
            if(StringUtil.isNotEmpty(selectedTab)) {
                ((UIDojoStackContainer)component).setSelectedTab(selectedTab);
            }
        }
    }
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.StackContainer"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibStack;
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoStackContainer) {
            UIDojoStackContainer c = (UIDojoStackContainer)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"doLayout",c.isDoLayout(),true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"persist",c.isPersist()); // $NON-NLS-1$
        }
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
        ResponseWriter writer = context.getResponseWriter();
        newLine(writer);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        super.encodeEnd(context, component);

        if(!(component.isRendered())) {
            return;
        }
        ResponseWriter writer = context.getResponseWriter();
        newLine(writer);

        UIDojoStackContainer c = (UIDojoStackContainer)component;
    
        if(isTrackClientSelection()) {
            String id = c.getClientId(context)+_SELECTION;
            String selectedTab = c.getSelectedTab();
            if(selectedTab==null) {
                selectedTab = "";
            }
            writer.startElement("input",component);  // this is for the uistate $NON-NLS-1$
            writer.writeAttribute("id",id,null); // $NON-NLS-1$
            writer.writeAttribute("name",id,null); // $NON-NLS-1$
            writer.writeAttribute("type","hidden",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("value",selectedTab,null);     // $NON-NLS-1$
            writer.endElement("input"); // $NON-NLS-1$
        }
    }
}