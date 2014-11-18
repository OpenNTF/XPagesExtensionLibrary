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

package com.ibm.xsp.extlib.renderkit.dojo.form;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoFormWidgetBase;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_basic.BasicInputTextRenderer;
import com.ibm.xsp.renderkit.html_basic.InputRendererUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.util.JSUtil;

/**
 * Form control renderer.
 * 
 * Such a renderer renders a dojo form control (inputtext, combobox...) and eventually a
 * hidden field for carrying out the data (some controls, like editor or toggle button do
 * not automatically bind to a form field). In that last case, it also generates a piece 
 * of JavaScript for the binding.
 * 
 * The decoding part is also handled, taking care od multiple values if applicable.
 * 
 * @author priand
 *
 */
public abstract class DojoFormWidgetRenderer extends BasicInputTextRenderer {

    // ==========================================================================
    // Rendering Properties
    // ==========================================================================

    protected Object getProperty(int prop) {
        return null;
    }

    // This is not needed post 852  
    protected boolean _isReadOnly(FacesContext context, UIInput input) {
        return super.isReadOnly(context, input);
    }
    
    
    // ==========================================================================
    // Renderer utilities
    // ==========================================================================

    // Internal debug flag
    protected static final boolean DEBUG = false;
    
    protected void newLine(ResponseWriter w) throws IOException {
        JSUtil.writeln(w);
    }
    
    protected void newLine(ResponseWriter w, String comment) throws IOException {
        if(DEBUG){
            if(  comment!=null) {
                w.writeComment(comment);
            }
        }
        JSUtil.writeln(w);
    }   

    
    public static final String HIDDEN_SUFFIX = "_field"; // $NON-NLS-1$
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(needDecode()) {
            super.decode(context, component);
        }
    }

    @Override
    protected void writeTag(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        // Don't care
        throw new NotImplementedException("");
    }

    /**
     * Checks if the control should actually decode the value.
     * As an example, input fields should while push buttons should not. 
     * @return
     */
    protected boolean needDecode() {
        return true;
    }
    
    /**
     * This method checks if a hidden field that hold the control value might be generated
     * to pass a value between the client and the server.
     * For example, a TextBox should not at it is already generated as an input tag, but a
     * toggle should, as it has not field backing the value.
     * @return
     */
    protected boolean needHiddenField() {
        return false;
    }

    /**
     * Tag generation.
     * The tag generation is done as:
     *  1- Main dijit tag with attributes   - encodeBegin()
     *  2- Tag content if applicable        - encodeBegin()
     *  3- Tag children if applicable       - encodeChildren()
     *  4- Main digit tag end               - encodeEnd()
     *  5- Hidden field, if needed          - encodeEnd() 
     *  6- JS binding code                  - encodeEnd() 
     */
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
    
        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();
    
        // Do not render if it is not needed
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }
    
        // Get the UIInput
        if (!(component instanceof UIInput)) {
            return;
        }
        UIInput uiInput = (UIInput) component;
        
        boolean needHiddenField = needHiddenField();
    
        // And write the value
        startTag(context, writer, uiInput);
        
        //write out dojoType and dojoAttributes if they are present
        boolean forceId = false;
        if(component instanceof FacesDojoComponent) {
            // Should we force the Id all the time here???
            writeDojoAttributes(context, (FacesDojoComponent)component);
            forceId = true;
        }
        //id
        writeId(writer, context, component, forceId);
        
        //name
        if(!needHiddenField) {
            String name = getNameAttribute(context, component);
            String currentValue = getCurrentValue(context, uiInput);
            writer.writeAttribute("name", name, "name"); //$NON-NLS-1$ //$NON-NLS-2$
            // Write the actual value
            // For an input tag, it is passed as a parameter
            writeValueAttribute(context, uiInput, writer, currentValue);
            // Render the tag body, if any
            renderTagBody(context, uiInput, writer, currentValue);
        } else {
            // Render the tag body, if any
            renderTagBody(context, uiInput, writer, null);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
    
        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();
    
        // Do not render if it is not needed
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }
    
        // Get the UIInput
        if (!(component instanceof UIInput)) {
            return;
        }
        UIInput uiInput = (UIInput) component;
    
        // close the input after writing the event attributes
        endTag(context, writer, uiInput);

        boolean needHiddenField = needHiddenField();
        if(needHiddenField) {
            String name = getNameAttribute(context, uiInput);
            writer.startElement("input", uiInput); //$NON-NLS-1$
            writer.writeAttribute("type", "hidden", null); //$NON-NLS-1$ //$NON-NLS-2$
            writer.writeAttribute("id", uiInput.getClientId(context)+HIDDEN_SUFFIX, "id"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.writeAttribute("name", name, "name"); //$NON-NLS-1$ //$NON-NLS-2$
            // Write the actual value
            // For an input tag, it is passed as a parameter
            String currentValue = getCurrentValue(context, uiInput);
            writeValueAttribute(context, uiInput, writer, currentValue);
            writer.endElement("input"); //$NON-NLS-1$
            // If some script is needed...
            renderJavaScriptBinding(context, writer, uiInput);
        }
        
        // Write the client side validators
        // Encode the dirty flag update if necessary
        encodeValidation(context, writer, uiInput);
        encodeDirtyState(context, writer, uiInput);
    }
    protected void encodeValidation(FacesContext context, ResponseWriter writer, UIInput component) throws IOException {
        // Dojo client side validation is never triggered for dojo control
        // Dojo uses it form validation capability instead, displaying tooltips
        //InputRendererUtil.encodeValidation(context, writer, component);
    }
    protected void encodeDirtyState(FacesContext context, ResponseWriter writer, UIInput component) throws IOException {
        InputRendererUtil.encodeDirtyState(context, writer, component);
    }

    protected void startTag(FacesContext context, ResponseWriter writer, UIInput component) throws IOException {
        String tagName = getTagName();
        writer.startElement(tagName, component); //$NON-NLS-1$
        String type = getInputType();
        if(StringUtil.isNotEmpty(type)) {
            writer.writeAttribute("type", type, null); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    protected void endTag(FacesContext context, ResponseWriter writer, UIInput component) throws IOException {
        String tagName = getTagName();
        writer.endElement(tagName); 
    }

    protected void writeValueAttribute(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        if(StringUtil.isNotEmpty(currentValue)) {
            writer.writeAttribute("value", currentValue, "value"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    protected void renderTagBody(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        // Nothing
    }

    protected String getTagName() {
        return "input"; // $NON-NLS-1$
    }

    protected String getNameAttribute(FacesContext context, UIComponent component) {
        return component.getClientId(context);
    }

    protected String getInputType() {
        return null;
    }        
    
    protected void renderJavaScriptBinding(FacesContext context, ResponseWriter writer, UIInput component) {
    }
    
    protected String writeDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent) throws IOException {
        UIViewRootEx viewEx = (UIViewRootEx)context.getViewRoot();
        String dojoType = dojoComponent.getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
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

        Map<String,String> attrs = DojoRendererUtil.createMap(context);

        // Compose the list of attributes from the list of dojo attributes
        DojoRendererUtil.getDojoAttributeMap(dojoComponent,attrs);
        
        // Add the attributes specific to this control
        initDojoAttributes(context, dojoComponent, attrs);
        
        // And generate them
        DojoRendererUtil.writeDojoHtmlAttributes(context,(UIComponent)dojoComponent,dojoType,attrs);
        
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
        // Add the extra resources
        Resource[] res = getExtraResources(context, dojoComponent);
        if(res!=null) {
            for(int i=0; i<res.length; i++) {
                ExtLibResources.addEncodeResource(viewEx, res[i]);
            }
        }
    }
    
    protected abstract String getDefaultDojoType(FacesContext context, FacesDojoComponent component);
    protected abstract DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component);
    
    protected Resource[] getExtraResources(FacesContext context, FacesDojoComponent component) {
        // None by default...
        return null;
    }
    
    protected boolean isRequirable() {
        return true;
    }
    
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        if(dojoComponent instanceof UIDojoFormWidgetBase) {
            UIDojoFormWidgetBase c = (UIDojoFormWidgetBase)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tooltip",c.getTooltip()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"dir",c.getDir()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"dragRestriction",c.isDragRestriction()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"group",c.getGroup()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"lang",c.getLang()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"style",c.getStyle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"class",c.getStyleClass()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"title",c.getTitle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"waiRole",c.getWaiRole()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"waiState",c.getWaiState()); // $NON-NLS-1$

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onBlur",c.getOnBlur()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onChange",c.getOnChange()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onClick",c.getOnClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onClose",c.getOnClose()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onDblClick",c.getOnDblClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onFocus",c.getOnFocus()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onKeyDown",c.getOnKeyDown()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onKeyPress",c.getOnKeyPress()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onKeyUp",c.getOnKeyUp()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseDown",c.getOnMouseDown()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseEnter",c.getOnMouseEnter()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseLeave",c.getOnMouseLeave()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseMove",c.getOnMouseMove()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseOut",c.getOnMouseOut()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseOver",c.getOnMouseOver()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onMouseUp",c.getOnMouseUp()); // $NON-NLS-1$

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"alt",c.getAlt()); // $NON-NLS-1$
            if( null == getInputType() ){
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"type",c.getType()); // $NON-NLS-1$
            }// else use the hard-coded Renderer inputType, instead of the type property set in the XPage source
            // TODO deprecate the unused type properties.
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabIndex",c.getTabIndex()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"disabled",c.isDisabled()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"intermediateChanges",c.isIntermediateChanges()); // $NON-NLS-1$

            // aria-required
            if(isRequirable() && c.isRequired()) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"aria-required","true"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // aria-invalid
            if(!c.isValid()) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"aria-invalid","true"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Particular case for the read-only attribute
            // The flag can come from the readOnly property, or the readonly context?
            if(_isReadOnly(context,c)) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"readOnly",true); // $NON-NLS-1$
            }
        }
    }
}
 
