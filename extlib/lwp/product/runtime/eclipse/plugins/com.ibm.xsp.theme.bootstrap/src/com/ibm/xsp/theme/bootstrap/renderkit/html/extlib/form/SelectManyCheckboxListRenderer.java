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
* Date: 16 Sep 2014
* SelectManyCheckboxListRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.component.xp.XspSelectManyCheckbox;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class SelectManyCheckboxListRenderer extends com.ibm.xsp.renderkit.html_basic.SelectManyCheckboxListRenderer {
	
	@Override
    protected void renderBeginText(UIComponent component, int border,
            boolean alignVertical, FacesContext context, boolean outerTable) throws IOException {
        renderBeginText(component, border, alignVertical, context);
    }

    public void renderBeginText(UIComponent component, int border, boolean alignVertical, FacesContext context) throws IOException {
        
        XspSelectManyCheckbox checkboxGroup = (component instanceof XspSelectManyCheckbox) ? (XspSelectManyCheckbox)component : null;
        if (null != checkboxGroup) {
            ResponseWriter writer = context.getResponseWriter();

            String accesskey = checkboxGroup.getAccesskey();
            String dir = checkboxGroup.getDir();
            String lang = checkboxGroup.getLang();
            String role = checkboxGroup.getRole();
            String style = checkboxGroup.getStyle();
            String styleClass = checkboxGroup.getStyleClass();
            String title = checkboxGroup.getTitle();
            String legend = checkboxGroup.getLegend();

            //Start the containing div for the checkbox group
            writer.startElement("div", component); // $NON-NLS-1$
            if(checkboxGroup.getDojoType()==null) {
            	writer.writeAttribute("class", "checkbox", "class");
            }else{
            	writer.writeAttribute("style", "display: inline;", "style");
            }
            
            //dojoType and dojoAttributes if they are present
            DojoUtil.addDojoAttributes(context, checkboxGroup);
            
            String clientId = checkboxGroup.getClientId(context);
            if (null != clientId) {
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
            if (checkboxGroup.isRequired()) {
                writer.writeAttribute("aria-required", "true", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (!checkboxGroup.isValid()) {
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
            AttrsUtil.encodeAttrs(context, writer, checkboxGroup);
            
            writer.startElement("table", component); // $NON-NLS-1$
            writer.writeAttribute("role", "presentation", null); //$NON-NLS-1$ //$NON-NLS-2$
            if (StringUtil.isNotEmpty(role)) {
                writer.writeAttribute("title", title, null); // $NON-NLS-1$
            }

            if (Integer.MIN_VALUE != border) {
                writer.writeAttribute("border", Integer.toString(border), "border"); //$NON-NLS-1$ //$NON-NLS-2$
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
                writer.writeAttribute("class", "xspGroupCaption", null);
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
        renderEndText(component, alignVertical, context);
    }

    protected void renderEndText(UIComponent component, boolean alignVertical,
            FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (!alignVertical) {
            writer.endElement("tr"); // $NON-NLS-1$
            writer.writeText("\n", null); // $NON-NLS-1$
        }
        writer.endElement("table"); // $NON-NLS-1$
        writer.endElement("div"); // $NON-NLS-1$
    }
}
