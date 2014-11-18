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

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoComboBox;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;

public class DojoComboBoxRenderer extends DojoValidationTextBoxRenderer {

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dijit.form.ComboBox"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dijitFormComboBox;
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoComboBox) {
            UIDojoComboBox c = (UIDojoComboBox)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"pageSize",c.getPageSize()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"store",c.getStore()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"autoComplete",c.isAutoComplete(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"searchDelay",c.getSearchDelay()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"searchAttr",c.getSearchAttr()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"queryExpr",c.getQueryExpr()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"ignoreCase",c.isIgnoreCase(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"hasDownArrow",c.isHasDownArrow(),true); // $NON-NLS-1$
        }
    }
    
    @Override
    protected String getTagName() {
        return "select"; // $NON-NLS-1$
    }

    @Override
    protected void renderTagBody(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        if(component instanceof UIDojoComboBox) {
            // Render the "options" portion if some children are available
            if(component.getChildCount()>0) {
                renderOptions(context, (UIDojoComboBox)component, writer, currentValue);
            }
        }
    }
    
    void renderOptions(FacesContext context, UIDojoComboBox component, ResponseWriter writer, String currentValue) throws IOException {
        // Find the converter
        Converter converter = component.getConverter();
        
        for( SelectItem curItem: FacesUtil.getSelectItems(component)) {
            // Dojo does not support optgroup - just ignore them
            // http://trac.dojotoolkit.org/ticket/1887
            if (!(curItem instanceof SelectItemGroup)) {
                renderOption(context, writer, component, converter, curItem, currentValue);
            }
        }
    }

    protected void renderOption(FacesContext context, ResponseWriter writer, UIDojoComboBox component, Converter converter, SelectItem curItem, String currentValue) throws IOException {
        writer.writeText(" ", null);
        writer.startElement("option", component); // $NON-NLS-1$

        String value = convertValue(context,component, converter, curItem.getValue());
        writer.writeAttribute("value", value, "value"); // $NON-NLS-1$ $NON-NLS-2$

        // Get the value to compare to
        boolean isSelected = isSelected(curItem, currentValue);

        if (isSelected) {
            writer.writeAttribute("selected", "selected", "selected"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (curItem.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }

        writer.writeText(curItem.getLabel(), "label"); // $NON-NLS-1$
        writer.endElement("option"); // $NON-NLS-1$
        writer.writeText("\n", null); // $NON-NLS-1$

    }
    boolean isSelected(Object itemValue, Object value) {
        if (value == null) {
            if (itemValue == null) {
                return true;
            }
        } else if (value.equals(itemValue)) {
            return true;
        }
        return false;
    }

    public String convertValue(FacesContext context, UIComponent component, Converter converter, Object value) throws ConverterException {
        if(value!=null) {
            if(converter==null) {
                Application application = context.getApplication();
                converter = application.createConverter(value.getClass());
            }
            // Format it using the converter if necessary, or just converter it to a simple string
            String strValue = converter!=null ? converter.getAsString(context, component, value) 
                                              : value.toString(); 
            return strValue;
        }

        return "";
    }
    
}