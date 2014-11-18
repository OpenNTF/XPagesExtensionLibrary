/*
 * © Copyright IBM Corp. 2010, 2012
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

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


public class DojoTabContainerRenderer extends DojoStackContainerRenderer {

    
//    @Override
//    protected void startTag(FacesContext context, ResponseWriter writer, UIComponent component) throws IOException {
//      writer.startElement("div", component);
//      writer.writeAttribute("id", ((FacesRefreshableComponent)component).getNonChildClientId(context),null);
//      super.startTag(context, writer, component);
//    }
//
//    @Override
//    protected void endTag(FacesContext context, ResponseWriter writer, UIComponent component) throws IOException {
//      super.endTag(context, writer, component);
//      writer.endElement("div");
//    }
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return UIDojoTabContainer.DEFAULT_DOJO_TYPE;
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibTabs; // Include all resources at once (useful when tabs are only created dynamically)
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
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoTabContainer) {
            UIDojoTabContainer c = (UIDojoTabContainer)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabStrip",c.isTabStrip()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabPosition",c.getTabPosition()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"useMenu",c.isUseMenu(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"useSlider",c.isUseSlider(), true); // $NON-NLS-1$
        }
    }
}