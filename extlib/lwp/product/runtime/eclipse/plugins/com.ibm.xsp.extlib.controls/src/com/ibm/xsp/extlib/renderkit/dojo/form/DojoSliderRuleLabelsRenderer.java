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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoHorizontalSlider;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels;
import com.ibm.xsp.extlib.component.dojo.form.constraints.NumberConstraints;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class DojoSliderRuleLabelsRenderer extends DojoSliderRuleRenderer {

    private boolean isHorizontal(FacesDojoComponent component) {
        return ((UIComponent)component).getParent() instanceof UIDojoHorizontalSlider;
    }
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return isHorizontal(component) ? "dijit.form.HorizontalRuleLabels" : "dijit.form.VerticalRuleLabels"; // $NON-NLS-1$ $NON-NLS-2$
    }
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return isHorizontal(component) ? ExtLibResources.dijitFormHorizontalRuleLabels : ExtLibResources.dijitFormVerticalRuleLabels;
    }
    @Override
    protected String getTagName() {
        return "ol"; // $NON-NLS-1$
    }    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoSliderRuleLabels) {
            UIDojoSliderRuleLabels c = (UIDojoSliderRuleLabels)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"labelStyle",c.getLabelStyle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"labels",c.createLabels()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"numericMargin",c.getNumericMargin()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"minimum",c.getMinimum()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"maximum",c.getMaximum()); // $NON-NLS-1$
            NumberConstraints nc = c.getConstraints();
            if(nc!=null) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"constraints",nc.createNumberConstraintsAsJson()); // $NON-NLS-1$
            }
        }
    }
}