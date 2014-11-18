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

package com.ibm.xsp.extlib.renderkit.dojo.form;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoRadioButton;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


public class DojoRadioButtonRenderer extends DojoFormWidgetRenderer {

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dijitFormRadioButton;
    }
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dijit.form.RadioButton"; // $NON-NLS-1$
    }

    @Override
    protected boolean needHiddenField() {
        return false;
    }

    @Override
    protected String getTagName() {
        return "input"; // $NON-NLS-1$
    }        
    
    @Override
    protected String getInputType() {
        return "radio"; // $NON-NLS-1$
    }        

    @Override
    protected String getNameAttribute(FacesContext context, UIComponent component) {
        UIDojoRadioButton rb = (UIDojoRadioButton)component;
        return rb.getClientGroupName(context);
    }
    
    @Override
    protected void writeValueAttribute(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        UIDojoRadioButton rb = (UIDojoRadioButton)component;
        Object selectedObj = rb.getSelectedValue();
        if(selectedObj != null) {
            String selectedValue = selectedObj.toString();
            if(StringUtil.equals(selectedValue, currentValue)) {
                writer.writeAttribute("checked", "true", null); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if(StringUtil.isNotEmpty(selectedValue)) {
                writer.writeAttribute("value", selectedValue, "selectedValue"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Get the UIInputEx
        if (!(component instanceof UIDojoRadioButton)) {
            return;
        }
        UIDojoRadioButton uiInput = (UIDojoRadioButton) component;

        // If the component is disabled, do not change the value of the
        // component, since its state cannot be changed.
        if(_isReadOnly(context,uiInput)) {
            return;
        }

        String groupName = uiInput.getClientGroupName(context);
        if(StringUtil.isNotEmpty(groupName)) {
            Map<?,?> requestMap = context.getExternalContext().getRequestParameterMap();
    
            // Don't overwrite the value unless you have to!
            if(requestMap.containsKey(groupName)) {
                String value = (String)requestMap.get(groupName);
                Object selectedValue = uiInput.getSelectedValue();
                if(selectedValue != null) {
                    String checkedValue = selectedValue.toString();
                    if(value!=null && value.equals(checkedValue)){
                        uiInput.setSubmittedValue(value);
                    }
                }
            } else {
                uiInput.setSubmittedValue("");
            }
        }
    }
    
    @Override
    protected void renderTagBody(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        UIDojoRadioButton c = (UIDojoRadioButton)component;
        String label = c.getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.startElement("label", c); //$NON-NLS-1$
            writer.writeAttribute("for", c.getClientId(context), null); //$NON-NLS-1$
            writer.writeText(label, "label"); //$NON-NLS-1$
            writer.endElement("label"); //$NON-NLS-1$
        }
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoRadioButton) {
            UIDojoRadioButton c = (UIDojoRadioButton)dojoComponent;
            //DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel());
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"showLabel",c.isShowLabel(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconClass",c.getIconClass()); // $NON-NLS-1$
        }
    }
}