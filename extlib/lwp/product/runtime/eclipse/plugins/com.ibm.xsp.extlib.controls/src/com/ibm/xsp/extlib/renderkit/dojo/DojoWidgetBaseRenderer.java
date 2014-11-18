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

package com.ibm.xsp.extlib.renderkit.dojo;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.util.JavaScriptUtil;

public abstract class DojoWidgetBaseRenderer extends FacesRendererEx {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if (AjaxUtil.isAjaxNullResponseWriter(writer)) {
            return;
        }

        // And write the value
        if (component instanceof UIDojoWidgetBase) {
            writeTag(context, (UIDojoWidgetBase) component, writer);
        }
    }

    @Override
    public boolean getRendersChildren() {
        return false;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if (AjaxUtil.isAjaxNullResponseWriter(writer)) {
            return;
        }

        // And write the value
        if (component instanceof UIDojoWidgetBase) {
            endTag(context, writer, (UIDojoWidgetBase) component);
        }
    }

    protected void writeTag(FacesContext context, UIComponent component, ResponseWriter writer) throws IOException {
        startTag(context, writer, component);

        // write out dojoType and dojoAttributes if they are present
        boolean forceId = false;
        if (component instanceof FacesDojoComponent) {
            // Should we force the Id all the time here???
            String dojoType = writeDojoAttributes(context, (FacesDojoComponent) component);
            if (StringUtil.isNotEmpty(dojoType)) {
                forceId = true;
            }
        }
        // id
        writeId(writer, context, component, forceId);
    }

    protected void startTag(FacesContext context, ResponseWriter writer, UIComponent component) throws IOException {
        String tagName = getTagName();
        writer.startElement(tagName, component);
    }

    protected void endTag(FacesContext context, ResponseWriter writer, UIComponent component) throws IOException {
        String tagName = getTagName();
        writer.endElement(tagName);
        // writer.write('\n');
    }

    protected void writeValueAttribute(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        if (StringUtil.isNotEmpty(currentValue)) {
            writer.writeAttribute("value", currentValue, "value"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    protected String writeDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent) throws IOException {
        UIViewRootEx viewEx = (UIViewRootEx)context.getViewRoot();
        String dojoType = dojoComponent.getDojoType();
        if (StringUtil.isEmpty(dojoType)) {
            dojoType = getDefaultDojoType(context, dojoComponent);

            // If the resources for the dojo type haven't been emitted yet, then do it
            // Not that the XPages runtime ensures that the resources are not emitted multiple times
            // This is simply an optimization
            if(shouldWriteModule(context, viewEx, dojoComponent, dojoType)) {
                writeDefaultDojoModule(context, viewEx, dojoComponent, dojoType);
            }
        } else {
            if(shouldWriteModule(context, viewEx, dojoComponent, dojoType)) {
                writeDojoModule(context, viewEx, dojoComponent, dojoType);
            }
        }

        if (StringUtil.isNotEmpty(dojoType)) {
            Map<String, String> attrs = DojoRendererUtil.createMap(context);
    
            // Compose the list of attributes from the list of dojo attributes
            DojoRendererUtil.getDojoAttributeMap(dojoComponent, attrs);
    
            // Add the attributes specific to this control
            initDojoAttributes(context, dojoComponent, attrs);
    
            // And generate them
            DojoRendererUtil.writeDojoHtmlAttributes(context, (UIComponent) dojoComponent, dojoType, attrs);
        }
        
        return dojoType;
    }
    
    protected boolean shouldWriteModule(FacesContext context, UIViewRootEx viewEx, FacesDojoComponent dojoComponent, String dojoType) {
        if (StringUtil.isNotEmpty(dojoType)) {
            if(viewEx.getEncodeProperty(dojoType)==null) {
                viewEx.putEncodeProperty(dojoType, Boolean.TRUE);
                return true;
            }
        }
        return false;
    }
    protected void writeDojoModule(FacesContext context, UIViewRootEx viewEx, FacesDojoComponent dojoComponent, String dojoType) {
    }
    protected void writeDefaultDojoModule(FacesContext context, UIViewRootEx viewEx, FacesDojoComponent dojoComponent, String dojoType) {
        // Add the default modules
        DojoModuleResource module = getDefaultDojoModule(context, dojoComponent);
        if(module!=null) {
            ExtLibResources.addEncodeResource(viewEx, module);
        }
        if (module != null && 
        		module.getName().equals(ExtLibResources.dojoxGridDataGrid.getName())) {
        	// 1.6.1 loads dojox.grid.DataGrid, which loads dojox.html.metrics, which adds one extra IFrame
        	ExtLibResources.addEncodeResource(viewEx, ExtLibResources.dojoIFrameAdjuster);
        }
        // Add the extra resources
        Resource[] res = getExtraResources(context, dojoComponent);
        if(res!=null) {
            for(int i=0; i<res.length; i++) {
                ExtLibResources.addEncodeResource(viewEx, res[i]);
            }
        }
    }

    protected abstract String getTagName();
    protected abstract String getDefaultDojoType(FacesContext context, FacesDojoComponent component);
    protected abstract DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component);

    protected Resource[] getExtraResources(FacesContext context, FacesDojoComponent component) {
        // None by default...
        return null;
    }

    
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        if (dojoComponent instanceof UIDojoWidgetBase) {
            UIDojoWidgetBase c = (UIDojoWidgetBase) dojoComponent;
            if( isHasTooltipProperty() ){
                DojoRendererUtil.addDojoHtmlAttributes(attrs, "tooltip", c.getTooltip()); // $NON-NLS-1$
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "dir", c.getDir()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "lang", c.getLang()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "style", c.getStyle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "class", c.getStyleClass()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "title", c.getTitle()); // $NON-NLS-1$
        }
    }
    protected boolean isHasTooltipProperty(){
        // in 8.5.3UP1 mobile controls would have a tooltip property
        // since 9.0.0 new controls do not have that property
        // because you can't hover for tooltips on touch-screen devices.
        return true;
    }
    
    protected static void encodeDojoEvents(FacesContext context, UIDojoWidgetBase component,
            String eventName, String eventValue) throws IOException {
        if(eventValue != null) {
            StringBuilder buff = new StringBuilder();
            String clientId = component.getClientId(context);
            // if event value set
            // state the function
            String clientSideScriptName =  JavaScriptUtil.startMethodJS(context, component, "clientSide_" + eventName, buff); // $NON-NLS-1$
            // add scripts to the function block
            buff.append( eventValue );
            buff.append("\n"); // $NON-NLS-1$
            JavaScriptUtil.endMethodJS(buff);

            boolean submit = false; // clientSide script only
            int validationMode = JavaScriptUtil.VALIDATION_FULL; // since not submit, not immediate
            // XSP.attachEvent
            JavaScriptUtil.appendAttachEvent(buff, clientId, clientId, eventName, clientSideScriptName, submit, validationMode);
            buff.append("\n"); // $NON-NLS-1$

            if( buff.length() == 0 ){
                // no events
                return;
            }
            // then add the script block we just generated.
            // when using dojo, can't just put onload code inline into the page,
            // as the dojo dom manipulation won't have happened by then.
            // Have to put it into an addOnLoad method.
            JavaScriptUtil.addScriptOnLoad(buff.toString());
        }
    }
}