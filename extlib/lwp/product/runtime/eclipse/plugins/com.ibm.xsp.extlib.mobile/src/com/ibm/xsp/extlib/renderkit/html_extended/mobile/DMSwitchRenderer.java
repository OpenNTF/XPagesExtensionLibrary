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
package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.component.mobile.UIDMSwitch;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class DMSwitchRenderer extends DojoWidgetRenderer {

    @Override
    protected String getTagName() {
        return "div"; // $NON-NLS-1$
    }

    @Override
    protected String getDefaultDojoType(FacesContext context,
            FacesDojoComponent component) {
        return "dojox.mobile.Switch"; // $NON-NLS-1$
    }

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context,
            FacesDojoComponent component) {
        return ExtLibResources.dojoxMobile;
    }

     @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDMSwitch) {
            UIDMSwitch c = (UIDMSwitch)dojoComponent;           
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"leftLabel",c.getLeftLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rightLabel",c.getRightLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"value",c.getValue()); // $NON-NLS-1$
            
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchStart", c.getOnTouchStart()); //$NON-NLS-1$
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchEnd", c.getOnTouchEnd()); //$NON-NLS-1$
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchMove", c.getOnTouchMove()); //$NON-NLS-1$
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onStateChanged", c.getOnStateChanged()); //$NON-NLS-1$
        }
    }
}