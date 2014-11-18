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
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRule;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class DojoSliderRuleRenderer extends DojoWidgetRenderer {

    private boolean isHorizontal(FacesDojoComponent component) {
        return ((UIComponent)component).getParent() instanceof UIDojoHorizontalSlider;
    }
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return isHorizontal(component) ? "dijit.form.HorizontalRule" : "dijit.form.VerticalRule"; // $NON-NLS-1$ $NON-NLS-2$
    }
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return isHorizontal(component) ? ExtLibResources.dijitFormHorizontalRule : ExtLibResources.dijitFormVerticalRule;
    }
    @Override
    protected String getTagName() {
        return "div"; // $NON-NLS-1$
    }    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoSliderRule) {
            UIDojoSliderRule c = (UIDojoSliderRule)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"ruleStyle",c.getRuleStyle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"count",c.getCount()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"container",c.getContainer()); // $NON-NLS-1$
        }
    }
}