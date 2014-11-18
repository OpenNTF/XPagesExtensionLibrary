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

package com.ibm.xsp.extlib.renderkit.dojo.form;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTextarea;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class DojoTextareaRenderer extends DojoTextBoxRenderer {

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dijit.form.Textarea"; // $NON-NLS-1$
    }
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dijitFormTextArea;
    }

    @Override
    protected String getTagName() {
        return "textarea"; // $NON-NLS-1$
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoTextarea) {
            UIDojoTextarea c = (UIDojoTextarea)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"cols",c.getCols(),-1); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rows",c.getRows(),-1); // $NON-NLS-1$
        }
    }
}