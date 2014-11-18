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

package com.ibm.xsp.extlib.renderkit.html_extended.tooltip;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.tooltip.UITooltip;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.util.FacesUtil;

public class TooltipRenderer extends FacesRendererEx {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        
        UITooltip tooltip = (UITooltip)component;
        String clientId = tooltip.getClientId(context);

        boolean dynamicContent = tooltip.isDynamicContent();
        
        // Add the dojo module
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        String dojoType;
        if(dynamicContent) {
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibTooltip);
            dojoType = "extlib.dijit.Tooltip"; // $NON-NLS-1$
        } else {
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dijitTooltip);
            dojoType = "dijit.Tooltip"; // $NON-NLS-1$
        }

        // Main dialog div 
        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$

        // Compose the list of attributes from the list of dojo attributes
        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        DojoRendererUtil.getDojoAttributeMap(tooltip,attrs);
        initDojoAttributes(context, tooltip, attrs);
        DojoRendererUtil.writeDojoHtmlAttributes(context,tooltip,dojoType,attrs);
    }
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        if(dojoComponent instanceof UITooltip) {
            UITooltip c = (UITooltip)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel()); // $NON-NLS-1$
            
            if(c.getShowDelay() != 0 )
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"showDelay",c.getShowDelay()); // $NON-NLS-1$
            
            String _for = c.getFor();
            if(StringUtil.isNotEmpty(_for)) {
                UIComponent f = FacesUtil.getComponentFor(c, _for);
                if(f==null) {
                    
                    throw new FacesExceptionEx(null,"Unknown 'for' component {0}", _for); // $NLX-TooltipRenderer.Unknownforcomponent0-1$
                }
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"connectId",f.getClientId(context)); // $NON-NLS-1$
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"position",c.getPosition()); // $NON-NLS-1$
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();

        //UITooltip tooltip = (UITooltip)component;
        
        w.endElement("div"); // $NON-NLS-1$
    }
}