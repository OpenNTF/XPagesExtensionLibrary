/*
 * © Copyright IBM Corp. 2010
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
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.util.JsonBuilder;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtListTextBox;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.picker.data.IPickerEntry;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.form.DojoFormWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;

public class DojoExtListTextBoxRenderer extends DojoFormWidgetRenderer {

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.ListTextBox"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibListTextBox;
    }
    @Override
    protected String getInputType() {
        return "text"; // $NON-NLS-1$
    }
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoExtListTextBox) {
            UIDojoExtListTextBox c = (UIDojoExtListTextBox)dojoComponent;
            String msep = c.getMultipleSeparator();
            if(!StringUtil.equals(msep, ",")) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"msep",msep); // $NON-NLS-1$
            }
            
            // Fill the labels if required
            if(c.isDisplayLabel()) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"displayLabel",true); // $NON-NLS-1$
                IPickerData vp = c.findDataProvider();
                if(vp!=null) {
                    Object[] values = getValues(context, c, msep);
                    if(values!=null) {
                        StringBuilder b = new StringBuilder();
                        JsonBuilder w = new JsonBuilder(b,true);
                        w.startObject();
                        List<IPickerEntry> entries = vp.loadEntries(values, null);
                        if(entries!=null) {
                            for(IPickerEntry e: entries) {
                                // The entry can be null if the id wa not found
                                if(e!=null) {
                                    Object value = e.getValue();
                                    Object label = e.getLabel();
                                    if(value!=null && label!=null) {
                                        w.startProperty(value.toString());
                                        w.outStringLiteral(label.toString());
                                        w.endProperty();
                                    }
                                }
                            }
                        }
                        w.endObject();
                        DojoRendererUtil.addDojoHtmlAttributes(attrs,"labels",b.toString()); // $NON-NLS-1$
                    }
                }
            }
        }
    }
    protected String[] getValues(FacesContext context, UIDojoExtListTextBox c, String msep) {
        String value = FacesUtil.convertValue(context, c);
        if(StringUtil.isNotEmpty(value)) {
            String[] values = StringUtil.isNotEmpty(msep) ? StringUtil.splitString(value, msep.charAt(0)) : new String[]{value};
            return values;
        }
        return null;
    }
}