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

package com.ibm.xsp.extlib.renderkit.html_extended.dynamiccontent;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.dynamiccontent.AbstractDynamicContent;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl;
import com.ibm.xsp.extlib.controls.ExtlibControlsLogger;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.util.JSUtil;

public class DynamicControlRenderer extends FacesRendererEx {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        super.encodeBegin(context, component);
        ResponseWriter w = context.getResponseWriter();

        String clientId = component.getClientId(context);
        
        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$
        //w.writeAttribute("style", "display: none", "style");
        
        if(component instanceof UIDynamicControl) {
        	w.writeAttribute("data-param", ((UIDynamicControl) component).getContentParam(), null); //$NON-NLS-1$
        }
        
        // Add the newly added resources, if some
        if(AbstractDynamicContent.USE_DYNAMIC_RESOURCES) {
            Integer rc = (Integer)context.getExternalContext().getRequestMap().get(AbstractDynamicContent.DYNAMIC_RESOURCES);
            if(rc!=null) {
                UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
                List<Resource> resources = rootEx.getResources();
                int count = resources.size();
                boolean isTraceDebug = ExtlibControlsLogger.CONTROLS.isTraceDebugEnabled();
                for(int i=rc; i<count; i++) {
                    Resource resource = resources.get(i);
                    if( isTraceDebug ){
                        ExtlibControlsLogger.CONTROLS.traceDebugp(this, "encodeBegin", //$NON-NLS-1$ 
                            StringUtil.format("Added a dynamic resource, {0}", resource.getClass().getName())); //$NON-NLS-1$
                    }
                    rootEx.addEncodeResource(resource);
                }
            }
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        super.encodeChildren(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(w);
    }
}