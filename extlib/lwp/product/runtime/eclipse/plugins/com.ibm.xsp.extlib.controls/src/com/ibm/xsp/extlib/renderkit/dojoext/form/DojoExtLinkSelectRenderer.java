/*
 * © Copyright IBM Corp. 2010, 2015
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

package com.ibm.xsp.extlib.renderkit.dojoext.form;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import com.ibm.domino.services.util.JsonBuilder;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtLinkSelect;
import com.ibm.xsp.extlib.component.picker.data.IPickerEntry;
import com.ibm.xsp.extlib.component.picker.data.IPickerResult;
import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.component.picker.data.SimplePickerOptions;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.form.DojoFormWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.sun.faces.util.Util;

public class DojoExtLinkSelectRenderer extends DojoFormWidgetRenderer {
    
    public static final int MAX_LINKS = 1024; // Security...

    protected static final int PROP_LISTSTYLE           = 1;
    protected static final int PROP_LISTCLASS           = 2;
    protected static final int PROP_ITEMSTYLE           = 3;
    protected static final int PROP_ITEMCLASS           = 4;
    protected static final int PROP_FIRSTITEMSTYLE      = 5;
    protected static final int PROP_FIRSTITEMCLASS      = 6;
    protected static final int PROP_LASTITEMSTYLE       = 7;
    protected static final int PROP_LASTITEMCLASS       = 8;
    protected static final int PROP_ENABLEDLINKSTYLE    = 9;
    protected static final int PROP_ENABLEDLINKCLASS    = 10;
    protected static final int PROP_DISABLEDLINKSTYLE   = 11;
    protected static final int PROP_DISABLEDLINKCLASS   = 12;
    

    @Override
    protected Object getProperty(int prop) {
        return super.getProperty(prop);
    }
    
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.LinkSelect"; // $NON-NLS-1$
    }
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibLinkSelect;
    }
    @Override
    protected String getInputType() {
        return "text"; //$NON-NLS-1$
    }        

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoExtLinkSelect) {
            UIDojoExtLinkSelect c = (UIDojoExtLinkSelect)dojoComponent;

            // Add the different styles/classes
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"listStyle",combineStyles(PROP_LISTSTYLE, c.getStyle())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"listClass",combineStyleClasses(PROP_LISTCLASS, c.getStyleClass())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"itemStyle",combineStyles(PROP_ITEMSTYLE, c.getItemStyle())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"itemClass",combineStyleClasses(PROP_ITEMCLASS, c.getItemStyleClass())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"firstItemStyle",combineStyles(PROP_FIRSTITEMSTYLE, c.getFirstItemStyle())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"firstItemClass",combineStyleClasses(PROP_FIRSTITEMCLASS, c.getFirstItemStyleClass())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"lastItemStyle",combineStyles(PROP_LASTITEMSTYLE, c.getLastItemStyle())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"lastItemClass",combineStyleClasses(PROP_LASTITEMCLASS, c.getLastItemStyleClass())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"enabledLinkStyle",(String)getProperty(PROP_ENABLEDLINKSTYLE)); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"enabledLinkClass",(String)getProperty(PROP_ENABLEDLINKCLASS)); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"disabledLinkStyle",(String)getProperty(PROP_DISABLEDLINKSTYLE)); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"disabledLinkClass",(String)getProperty(PROP_DISABLEDLINKCLASS)); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"controlDisabled", (c.isDisabled() ? "true" : "false")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabindex", (c.isDisabled() ? -1 : c.getTabIndex())); // $NON-NLS-1$
            
            // Generate the list of options as JSON
            StringBuilder b = new StringBuilder();
            JsonBuilder w = new JsonBuilder(b,true);
            w.startArray();

            IValuePickerData d = c.getDataProvider();
            if(d!=null) {
                IPickerResult r = d.readEntries(new SimplePickerOptions(0,MAX_LINKS));
                if(r!=null) {
                    for( IPickerEntry e: r.getEntries() ) {
                        Object o = e.getValue();
                        if(o!=null) {
                            addJsonEntry(w,o,e.getLabel());
                        }
                    }
                }
            } else {
                Converter converter = c.getConverter();
                // Call the Sun method here. Should we just rewrite it?
                for( Iterator<SelectItem> items = (Iterator<SelectItem>)Util.getSelectItems(context, c); items.hasNext(); ) {
                    SelectItem curItem = items.next();
                    String value = convertValue(context,c, converter, curItem.getValue());
                    if(value!=null) {
                        String label = curItem.getLabel();
                        addJsonEntry(w,value,label);
                    }
                }
            }
            
            w.endArray();
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"valueList",b.toString()); // $NON-NLS-1$
        }
    }

    private String combineStyles(int propertyId, String explicitValue) {
        String rendererPropertyValue = (String)getProperty(propertyId);
        return ExtLibUtil.concatStyles(rendererPropertyValue, explicitValue);
    }
    private String combineStyleClasses(int propertyId, String explicitValue) {
        String rendererPropertyValue = (String)getProperty(propertyId);
        return ExtLibUtil.concatStyleClasses(rendererPropertyValue, explicitValue);
    }


    private void addJsonEntry(JsonBuilder w, Object value, Object label) throws IOException {
        w.startArrayItem();
        w.startObject();
        w.startProperty("v"); //$NON-NLS-1$
        w.outStringLiteral(value.toString());
        w.endProperty();
        if(label!=null) {
            w.startProperty("l"); //$NON-NLS-1$
            w.outStringLiteral(label.toString());
            w.endProperty();
        }
        w.endObject();
        w.endArrayItem();
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