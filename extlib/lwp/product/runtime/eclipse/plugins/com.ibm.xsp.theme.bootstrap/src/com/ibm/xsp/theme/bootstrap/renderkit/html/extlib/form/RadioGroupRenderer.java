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
* Date: 25 Sep 2014
* RadioGroupRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.xp.XspSelectOneRadio;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RadioGroupRenderer extends com.ibm.xsp.renderkit.html_basic.RadioRenderer {

    @Override
    protected void renderBeginText(UIComponent component, int border,
            boolean alignVertical, FacesContext context, boolean outer) throws IOException {

        XspSelectOneRadio radioGroup = (component instanceof XspSelectOneRadio) ? (XspSelectOneRadio)component : null;
        if (null != radioGroup) {
            ResponseWriter writer = context.getResponseWriter();
            String accesskey = radioGroup.getAccesskey();
            String dir = radioGroup.getDir();
            String lang = radioGroup.getLang();
            String role = radioGroup.getRole();
            String style = radioGroup.getStyle();
            String styleClass = radioGroup.getStyleClass();
            String title = radioGroup.getTitle();
            String legend = radioGroup.getLegend();

            writer.startElement("div", component); // $NON-NLS-1$
            
            //dojoType and dojoAttributes if they are present
            DojoUtil.addDojoAttributes(context, radioGroup);

            // for SPR#MKEE89GMU5, always output fieldset id, so it can be compared
            // to the name attribute of the inner input controls.
            String clientId = radioGroup.getClientId(FacesContext.getCurrentInstance());
            if (clientId != null ) {
                writer.writeAttribute("id", clientId, "id"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (StringUtil.isNotEmpty(accesskey)) {
                writer.writeAttribute("accesskey", accesskey, null); // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(dir)) {
                writer.writeAttribute("dir", dir, null); // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(lang)) {
                writer.writeAttribute("lang", lang, null); // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(role)) {
                writer.writeAttribute("role", role, null); // $NON-NLS-1$
            }
            if (radioGroup.isRequired()) {
            writer.writeAttribute("aria-required", "true", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (!radioGroup.isValid()) {
            writer.writeAttribute("aria-invalid", "true", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (StringUtil.isNotEmpty(style)) {
                writer.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
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

            writer.startElement("table", component); // $NON-NLS-1$
            writer.writeAttribute("role", "presentation", null); //$NON-NLS-1$ //$NON-NLS-2$

            if (Integer.MIN_VALUE != border) {
                writer.writeAttribute("border", Integer.toString(border), "border"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (StringUtil.isNotEmpty(title)) {
                writer.writeAttribute("title", title, null); // $NON-NLS-1$
            }
            writer.writeText("\n", null); // $NON-NLS-1$
            
            if (StringUtil.isNotEmpty(legend)) {
                writer.startElement("caption", component); // $NON-NLS-1$
                if (StringUtil.isNotEmpty(dir)) {
                    writer.writeAttribute("dir", dir, null); // $NON-NLS-1$
                }
                if (StringUtil.isNotEmpty(lang)) {
                    writer.writeAttribute("lang", lang, null); // $NON-NLS-1$
                }
                writer.writeAttribute("class", "xspGroupCaption", null); // $NON-NLS-1$ $NON-NLS-2$
                writer.writeText(legend, "legend"); // $NON-NLS-1$
                writer.endElement("caption"); // $NON-NLS-1$
            }
            if (!alignVertical) {
                writer.startElement("tr", component); // $NON-NLS-1$
                writer.writeText("\n", null); // $NON-NLS-1$
            }
        }
    }
    
    @Override
    protected void renderEndText(UIComponent component, boolean alignVertical,
            FacesContext context, boolean outerTable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (!alignVertical) {
            writer.endElement("tr"); // $NON-NLS-1$
            writer.writeText("\n", null); // $NON-NLS-1$
        }
        writer.endElement("table"); // $NON-NLS-1$
        writer.endElement("div"); // $NON-NLS-1$
    }
}
