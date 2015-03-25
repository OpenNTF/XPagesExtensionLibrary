/*
 * © Copyright IBM Corp. 2014
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 19 Aug 2014
* RadioRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.UIInputRadio;
import com.ibm.xsp.component.xp.XspInputRadio;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;
import com.ibm.xsp.renderkit.html_basic.InputRendererUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RadioRenderer extends com.ibm.xsp.renderkit.html_extended.RadioRenderer {

    static final String RADIO = "radio"; //$NON-NLS-1$
    static final String CHECKEDVALUE = "checkedValue"; //$NON-NLS-1$
    static final String[] ATTRS = { "tabindex", "accesskey" }; // $NON-NLS-1$ $NON-NLS-2$
    static final String DIR = "dir"; //$NON-NLS-1$
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        // validate the context and component
        if (context == null || component == null) {
            throw new NullPointerException(); 
        }
        ResponseWriter writer = context.getResponseWriter();
        if (writer == null) {
            throw new NullPointerException(); 
        }

        // should this component be rendered
        if (!component.isRendered()) {
            return;
        }

        // must extend this class
        if (!(component instanceof UIInputRadio)) {
            throw new IllegalStateException();
        }
        UIInputRadio radio = (UIInputRadio) component;
        String name = UIInputRadio.getClientGroupName(context, radio);
        
        // start the wrapping div tag
        writer.startElement("div", component); // $NON-NLS-1$
        encodeHtmlStyleAttributes(writer, component);
        
        // start the label
        String text = (String)radio.getAttributes().get(TEXT);
        if (!StringUtil.isEmpty(text)) {
            writer.startElement(LABEL, component);
            String clientId = radio.getClientId(context);
            writer.writeAttribute(FOR, clientId, CLIENTID);
            String dir = (String)radio.getAttributes().get(DIR);
            if (!StringUtil.isEmpty(dir))
                writer.writeAttribute(DIR, dir, DIR);
            encodeHtmlStyleAttributes(writer, component);
        }
        
        // encode the input tag
        writer.startElement(INPUT, component);
        
        //dojoType and dojoAttributes if they are present
        boolean forceId = false;
            // Only force the Id if dojoType is set
            if(DojoUtil.addDojoAttributes(context, radio)!= null){
                forceId = true;
            }
        
        writeId(writer, context, component, forceId);
        writer.writeAttribute(TYPE, RADIO, TYPE);
        writer.writeAttribute(NAME, name, NAME);
        
        // readonly 
        boolean readonly = ReadOnlyAdapterRenderer.isReadOnly(context,component); 
        if(readonly) {
            writer.writeAttribute("readonly", "readonly", "readonly"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            // TODO when showReadonlyAsDisabled, writing the disabled attribute.
            // would it be OK to only write the readonly attribute?
            writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        
        // Add ARIA role attribute
        if (component instanceof XspInputRadio) {
            String role = ((XspInputRadio)component).getRole();
            if (StringUtil.isNotEmpty(role)) {
            writer.writeAttribute("role", role, null); //$NON-NLS-1$
            }
            
            if (((XspInputRadio)component).isRequired()) {
            writer.writeAttribute("aria-required", "true", null); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (!((XspInputRadio)component).isValid()) {
            writer.writeAttribute("aria-invalid", "true", null); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        
        // encode the attributes listed in ATTRS...
        for(int i = 0; i < ATTRS.length; i++)
            writeAttribute(writer, component, ATTRS[i]);
        
        writer.writeAttribute(CHECKED, 
                UIInputRadio.isSelected(context, component) ? Boolean.TRUE : Boolean.FALSE, VALUE);
        writer.writeAttribute(VALUE, getSelectedValue(radio), CHECKEDVALUE);
        if( !readonly ){ // when readonly will have written disabled above.
            // check if disabled, and if so, write the disabled attribute
            writeBooleanAttribute(writer, component, "disabled"); // //$NON-NLS-N$
        }
        encodeHtmlAttributes(writer, component, false);
        
        String onchangeTrigger = null;
        if( context instanceof FacesContextEx ){
            FacesContextEx contextEx = (FacesContextEx)context;
            //xsp.client.script.radioCheckbox.ie.onchange.trigger= early-onclick | late-onblur[default]
            onchangeTrigger = contextEx.getProperty(XspPropertyConstants.XSP_RADIO_ONCHANGE_TRIGGER);
        }
        if( null != onchangeTrigger && !"late-onblur".equals(onchangeTrigger) ){ //$NON-NLS-1$
            writer.writeAttribute("onchangeTrigger", onchangeTrigger, null); //$NON-NLS-1$
        }
        
        if (component instanceof FacesAttrsObject) {
            FacesAttrsObject attrsHolder = (FacesAttrsObject) component;
            AttrsUtil.encodeAttrs(context, writer, attrsHolder);
        }
        writer.endElement(INPUT);
        
        // end the label
        if (!StringUtil.isEmpty(text)) {
            writer.writeText(text, TEXT);
            writer.endElement(LABEL);
        }
       
        //end the div
        writer.endElement("div"); // $NON-NLS-1$
        
        InputRendererUtil.encodeValidation(context, context.getResponseWriter(), (UIInput)component);
        
        InputRendererUtil.encodeDirtyState(context, context.getResponseWriter(), (UIInput)component);
    }

}
