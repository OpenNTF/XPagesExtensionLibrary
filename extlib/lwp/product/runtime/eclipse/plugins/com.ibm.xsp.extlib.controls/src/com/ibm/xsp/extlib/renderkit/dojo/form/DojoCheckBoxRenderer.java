/*
 * © Copyright IBM Corp. 2010, 2014
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
import com.ibm.xsp.extlib.component.dojo.form.UIDojoCheckBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoToggleButton;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


public class DojoCheckBoxRenderer extends DojoFormWidgetRenderer {

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dijitFormCheckBox;
    }

    @Override
    protected boolean needHiddenField() {
        return false;
    }

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dijit.form.CheckBox"; // $NON-NLS-1$
    }

    @Override
    protected String getTagName() {
        return "input"; // $NON-NLS-1$
    }        

    @Override
    protected String getInputType() {
        return "checkbox"; // $NON-NLS-1$
    }
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Get the UIInputEx
        if (!(component instanceof UIDojoToggleButton)) {
            return;
        }
        UIDojoToggleButton uiInput = (UIDojoToggleButton) component;

        // If the component is disabled, do not change the value of the
        // component, since its state cannot be changed.
        if(_isReadOnly(context,uiInput)) {
            return;
        }

        String clientId = uiInput.getClientId(context);
        Map<?,?> requestMap = context.getExternalContext().getRequestParameterMap();

        // Don't overwrite the value unless you have to!
        Object value;
        boolean isDisabled = uiInput.isDisabled();
        if( ! isDisabled ){
        if(requestMap.containsKey(clientId)) {
            value = uiInput.getCheckedValue();
            if( null == value ){
                value = UIDojoToggleButton.CHECKED_VALUE_DEFAULT;
            }
        }else{
            value = uiInput.getUncheckedValue();
            if( null == value ){
                value = UIDojoToggleButton.UNCHECKED_VALUE_DEFAULT;
            }
        }
        }else{ // isDisabled
            // then figure out what state would have been displayed to the user 
            // in the browser, and use the checkedValue/uncheckedValue 
            // corresponding to that state as the submittedValue.
            //(When the control is disabled in the browser, no value will have
            // been submitted from the browser, for either checkbox state,
            // so cannot use the usual requestMap checking to detect state).
            
            // note currentValueAsString may be a value from a document or may be a defaultValue
            String currentValueAsString = getCurrentValue(context, uiInput);
            
            // do same as in writeValueAttribute below
            Object oCheckedValue = uiInput.getCheckedValue();
            String checkedValue = null;
            if (oCheckedValue != null) {
                checkedValue = oCheckedValue.toString();
            }
            if(checkedValue==null) {
                checkedValue = UIDojoCheckBox.CHECKED_VALUE_DEFAULT;
            }
            boolean wouldHaveDisplayedAsChecked = StringUtil.equals(checkedValue, currentValueAsString);
            
            if( wouldHaveDisplayedAsChecked ){
                value = uiInput.getCheckedValue();
                if( null == value ){
                    value = UIDojoToggleButton.CHECKED_VALUE_DEFAULT;
                }
            }else{
                value = uiInput.getUncheckedValue();
                if( null == value ){
                    value = UIDojoToggleButton.UNCHECKED_VALUE_DEFAULT;
                }
            }
        }
        uiInput.setSubmittedValue(value);
    }
    
    @Override
    protected void writeValueAttribute(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        UIDojoCheckBox ck = (UIDojoCheckBox)component;
        Object oCheckedValue = ck.getCheckedValue();
        String checkedValue = null;
        if (oCheckedValue != null) {
            checkedValue = oCheckedValue.toString();
        }
        if(checkedValue==null) {
            checkedValue = UIDojoCheckBox.CHECKED_VALUE_DEFAULT;
        }
        if(StringUtil.equals(checkedValue, currentValue)) {
            // Should it be a dojo attribute instead?
            writer.writeAttribute("checked", currentValue, "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    @Override
    protected void renderTagBody(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        UIDojoCheckBox c = (UIDojoCheckBox)component;
        String label = c.getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.startElement("label", c); //$NON-NLS-1$
            writer.writeAttribute("for", c.getClientId(context), null); //$NON-NLS-1$
            writer.writeText(label, "label"); //$NON-NLS-1$
            writer.endElement("label"); //$NON-NLS-1$
        }
    }
    @Override
    protected boolean isRequirable() {
        return false;
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoCheckBox) {
            UIDojoCheckBox c = (UIDojoCheckBox)dojoComponent;
            //DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel());
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"showLabel",c.isShowLabel(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconClass",c.getIconClass()); // $NON-NLS-1$
        }
    }    
}