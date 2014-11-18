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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoCheckBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoToggleButton;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.JavaScriptUtil;


public class DojoToggleButtonRenderer extends DojoButtonRenderer {

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dijitFormToggleButton;
    }

    @Override
    protected boolean needHiddenField() {
        return true;
    }
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dijit.form.ToggleButton"; // $NON-NLS-1$
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
        if(isReadOnly(context,uiInput)) {
            return;
        }

        String clientId = uiInput.getClientId(context);
        Map<?,?> requestMap = context.getExternalContext().getRequestParameterMap();

        // Don't overwrite the value unless you have to!
        Object value;
        if(requestMap.containsKey(clientId) && StringUtil.equals(requestMap.get(clientId), "on")) { // $NON-NLS-1$
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
        uiInput.setSubmittedValue(value);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
        super.encodeEnd(context, component);
        
        // Generate JS binding the button to 
        
    }
    
    @Override
    protected void renderJavaScriptBinding(FacesContext context, ResponseWriter writer, UIInput component) {
        StringBuilder sb = new StringBuilder();
        String clientId = component.getClientId(context);
        String name = getNameAttribute(context, component)+HIDDEN_SUFFIX;
        
        // dijit.byId("view:_id1:djToggleButton1").setChecked(dojo.byId("view:_id1:djToggleButton1_field").value=="on");\n
        sb.append("dijit.byId("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,clientId);
        sb.append(").setChecked(dojo.byId("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,name);
        sb.append(").value=="); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,"on"); //$NON-NLS-1$
        sb.append(");\n"); //$NON-NLS-1$
        
        // dojo.connect(dijit.byId("view:_id1:djToggleButton1"),"onClick",function(){
        //   dojo.byId("view:_id1:djToggleButton1_field").value=dijit.byId("view:_id1:djToggleButton1").attr("checked")?"on":""
        // });
        sb.append("dojo.connect(dijit.byId("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,clientId);
        sb.append("),"); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,"onClick"); //$NON-NLS-1$
        sb.append(",function(){dojo.byId("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,name);
        sb.append(").value=dijit.byId("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,clientId);
        sb.append(").attr("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,"checked"); //$NON-NLS-1$
        sb.append(")?"); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,"on"); //$NON-NLS-1$
        sb.append(":"); //$NON-NLS-1$
        JavaScriptUtil.addString(sb,""); //$NON-NLS-1$
        sb.append("});"); //$NON-NLS-1$
        
        ((UIViewRootEx)context.getViewRoot()).addScriptOnLoad(sb.toString());
    }

    @Override
    protected void writeValueAttribute(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        UIDojoToggleButton ck = (UIDojoToggleButton)component;
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
            writer.writeAttribute("value", "on", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
}
