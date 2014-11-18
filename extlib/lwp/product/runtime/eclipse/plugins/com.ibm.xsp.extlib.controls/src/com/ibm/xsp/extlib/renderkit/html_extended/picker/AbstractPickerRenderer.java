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

package com.ibm.xsp.extlib.renderkit.html_extended.picker;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.controls.ExtlibControlsLogger;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * Abstract Picker renderer.
 */
public abstract class AbstractPickerRenderer extends FacesRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
    
        AbstractPicker picker = (AbstractPicker)component;
        IPickerData data = picker.getDataProvider();
        
        String dojoType = picker.getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            dojoType = getDefaultDojoType(); //"extlib.dijit.ValuePickerList";
        }
        dojoType = encodeDojoType(dojoType);

        // Encode the necessary resources
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.setDojoTheme(true);
        rootEx.setDojoParseOnLoad(true);
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibPicker);
        encodeExtraResources(context, picker, data, rootEx, dojoType);

        writeLink(context, w, picker, data, dojoType);
    }

    protected void writeLink(FacesContext context, ResponseWriter w, AbstractPicker picker, IPickerData data, String dojoType) throws IOException {
        UIComponent _for = getFor(context,picker);
        
        boolean readOnly = _for!=null ? FacesUtil.isComponentReadOnly(context, _for) : false;
        
        if(!readOnly) {
            Boolean _disabled_ = _for!=null ? (Boolean)_for.getAttributes().get("disabled") : null; // $NON-NLS-1$
            boolean disabled = _disabled_!=null ? _disabled_:false; // $NON-NLS-1$
            
            if(disabled) {
                w.startElement("span", null); // $NON-NLS-1$
            } else {
                w.startElement("a", null);
                w.writeAttribute("class", "xspPickerLink", null); // $NON-NLS-1$ $NON-NLS-2$
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
            }
            // Get the text/icon
            String text = picker.getPickerText();
            String icon = picker.getPickerIcon();
            
            boolean custom = StringUtil.isNotEmpty(text) || StringUtil.isNotEmpty(icon);
            if(!custom) {
                icon = getImageLink();
            }
            
            if(StringUtil.isNotEmpty(icon)) {
                w.startElement("img", null); // $NON-NLS-1$
                w.writeAttribute("src", HtmlRendererUtil.getImageURL(context,icon), null); // $NON-NLS-1$
                String iconAlt = "Open Picker"; // $NLS-AbstractPickerRenderer.OpenPicker-1$
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
    
    protected UIComponent getFor(FacesContext context, AbstractPicker picker) {
        // Associated control
        String control = picker.getFor();
        if(StringUtil.isNotEmpty(control)) {
            UIComponent c = FacesUtil.getComponentFor(picker, control);
            return c;
        }
        return null;
    }

    protected abstract String getDefaultDojoType();
    protected abstract String getDialogTitleSingleSelect();
    protected abstract String getDialogTitleMultipleSelect();
    protected abstract String getImageLink();

    protected String encodeDojoType(String dojoType) {
        return dojoType;
    }
    
    protected void encodeExtraResources(FacesContext context, AbstractPicker picker, IPickerData data, UIViewRootEx rootEx, String dojoType) {
        // if the grid is to be used, load the grid CSS
        if(StringUtil.equals(dojoType, "extlib.dijit.ValuePickerGrid")) { // $NON-NLS-1$
            ExtLibResources.addEncodeResources(rootEx, ExtLibResources.GRID_EXTRA_RESOURCES);
        }
    }
    
    protected String createParametersAsJson(FacesContext context, AbstractPicker picker, UIComponent _for, IPickerData data, String dojoType) {
        try {
            JsonJavaObject json = new JsonJavaObject();
            DojoRendererUtil.getDojoAttributeMap(picker,json);
            initDojoAttributes(context, picker, _for, data, dojoType, json);
            // And generate them
            return DojoRendererUtil.getDojoAttributesAsJson(context,picker,json);
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
    }
    
    protected void initDojoAttributes(FacesContext context, AbstractPicker picker, UIComponent _for, IPickerData data, String dojoType, JsonJavaObject json) throws IOException {
        // Associated control
        boolean allowMultiple = false;
        if(_for!=null) {
            json.putString("control",_for.getClientId(context)); // $NON-NLS-1$
            if(_for instanceof UIInputEx) {
                UIInputEx iex = (UIInputEx)_for; 
                // Check for a multiple separator
                String ch = iex.getMultipleSeparator();
                if(StringUtil.isNotEmpty(ch)) {
                    boolean trim = iex.isMultipleTrim();
                    json.putString("msep",ch); // $NON-NLS-1$
                    json.putBoolean("trim",trim); // $NON-NLS-1$
                    allowMultiple = true;
                }
            }
        }

        // Dialog title
        String title = picker.getDialogTitle();
        if(StringUtil.isEmpty(title)) {
            if( allowMultiple ){
                title = getDialogTitleMultipleSelect();
            }else{
                title = getDialogTitleSingleSelect();
            }
        }
        json.putString("dlgTitle",title); // $NON-NLS-1$
        // Dialog sizes
        String lw = picker.getListWidth();
        if(StringUtil.isNotEmpty(lw)) {
            json.putString("listWidth",lw); // $NON-NLS-1$
        }
        String lh = picker.getListHeight();
        if(StringUtil.isNotEmpty(lh)) {
            json.putString("listHeight",lh); // $NON-NLS-1$
        }
        // The rest service URL
        String url = picker.getUrl(context, null);
        json.putString("url",url); // $NON-NLS-1$
        // Generate the source list, if applicable
        boolean hasMultipleSource = data.hasCapability(IPickerData.CAPABILITY_MULTIPLESOURCES);
        if(hasMultipleSource) {
            String[] labels = data.getSourceLabels();
            if(labels!=null && labels.length>=2) {
                json.putObject("sources",labels); // $NON-NLS-1$
            }
        }
    }    
}