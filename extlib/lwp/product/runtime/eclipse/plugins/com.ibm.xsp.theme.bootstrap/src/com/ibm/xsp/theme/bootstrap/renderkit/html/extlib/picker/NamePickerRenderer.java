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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.picker;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.theme.bootstrap.util.BootstrapUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;

public class NamePickerRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.picker.NamePickerRenderer {

    protected static final int PROP_USERICONCLASS       = 1;
    
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_USERICONCLASS:             return Resources.get().getIconClass("user"); // $NON-NLS-1$
        }
        return null;
    }
    
    @Override
    protected String encodeDojoType(String dojoType) {
        if (StringUtil.equals(dojoType, "extlib.dijit.PickerName")) { // $NON-NLS-1$
            return "extlib.responsive.dijit.xsp.bootstrap.PickerName"; // $NON-NLS-1$
        }
        return super.encodeDojoType(dojoType);
    }

    @Override
    protected void encodeExtraResources(FacesContext context, AbstractPicker picker, IPickerData data, UIViewRootEx rootEx, String dojoType) {
        if (StringUtil.equals(dojoType, "extlib.responsive.dijit.xsp.bootstrap.PickerName")) { // $NON-NLS-1$
            ExtLibResources.addEncodeResource(rootEx, Resources.bootstrapPickerName);
        }
        super.encodeExtraResources(context, picker, data, rootEx, dojoType);
    }
    
    /** Default is to have empty icon image, and use glyphicon instead **/
    @Override
    protected String getImageLink() {
        return "";
    }
    
    @Override
    protected void writeLink(FacesContext context, ResponseWriter w, AbstractPicker picker, IPickerData data, String dojoType) throws IOException {
        UIComponent _for = getFor(context,picker);
        
        boolean readOnly = _for!=null ? FacesUtil.isComponentReadOnly(context, _for) : false;
        
        if(!readOnly) {
            Boolean _disabled_ = _for!=null ? (Boolean)_for.getAttributes().get("disabled") : null; // $NON-NLS-1$
            boolean disabled = _disabled_!=null ? _disabled_:false; // $NON-NLS-1$
            
            // Get the text/icon
            String text = picker.getPickerText();
            String icon = picker.getPickerIcon();
            
            boolean custom = StringUtil.isNotEmpty(text) || StringUtil.isNotEmpty(icon);
            
            if(disabled) {
                w.startElement("span", null); // $NON-NLS-1$
            } else {
                w.startElement("a", null);
                //If custom icon or text supplied, use that, else use glyphicon
                if(custom) {
                    w.writeAttribute("class", "xspPickerLink", null); // $NON-NLS-1$ $NON-NLS-2$
                }else{
                    w.writeAttribute("class", ExtLibUtil.concatStyleClasses((String)getProperty(PROP_USERICONCLASS), "xspPickerLink"), null); // $NON-NLS-1$ $NON-NLS-2$
                }
                w.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
                if(data!=null) {
                    //PHAN8YWEJZ fix IE namepicker beforeunload event occurring
                    StringBuilder onclick = new StringBuilder();
                    onclick.append("return XSP.selectValue("); // $NON-NLS-1$
                    JSUtil.addSingleQuoteString(onclick, dojoType);
                    onclick.append(","); // $NON-NLS-1$
                    onclick.append(createParametersAsJson(context, picker, _for, data, dojoType));
                    onclick.append(")"); // $NON-NLS-1$
                    w.writeAttribute("onclick", onclick.toString(), null); // $NON-NLS-1$
                    
                    StringBuilder onkeydown = new StringBuilder();
                    onkeydown.append("javascript:var kc=event.keyCode?event.keyCode:event.which;if(kc==32){ return XSP.selectValue("); // $NON-NLS-1$
                    JSUtil.addSingleQuoteString(onkeydown, dojoType);
                    onkeydown.append(","); // $NON-NLS-1$
                    // TODO this onkeydown="javascript: something" contains JSON with double-quotes, breaking the XML attribute
                    onkeydown.append(createParametersAsJson(context, picker, _for,data, dojoType));
                    onkeydown.append(")}"); // $NON-NLS-1$
                    w.writeAttribute("onkeydown", onkeydown.toString(), null); // $NON-NLS-1$ $NON-NLS-2$
                }
                //LHEY97QME8 adding the role= button
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                
                // "Open name picker"
                String ariaLabel = getPickerIconAriaLabel();
                w.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$ 
                if(!custom){
                    // A11Y fix - Defect 198080 - Need an sr-only span with text for glyphicon
                    //<span class="sr-only">Open Name Picker Dialog</span>
                    BootstrapUtil.renderIconTextForA11Y(w, ariaLabel);
                }
            }
            
            if(StringUtil.isNotEmpty(icon)) {
                w.startElement("img", null); // $NON-NLS-1$
                w.writeAttribute("src", HtmlRendererUtil.getImageURL(context,icon), null); // $NON-NLS-1$
                // "Open name picker"
                String iconAlt = getPickerIconAriaLabel();
                w.writeAttribute("alt", iconAlt, null); // $NON-NLS-1$
                w.endElement("img"); // $NON-NLS-1$
            }
            
            if(StringUtil.isNotEmpty(text)) {
                w.writeText(text, null);
            }
            
            if(disabled) {
                w.endElement("span"); // $NON-NLS-1$
            } else {
                w.endElement("a");
            }
        }
    }
}