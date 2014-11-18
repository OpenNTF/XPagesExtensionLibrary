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

package com.ibm.xsp.extlib.renderkit.dojo;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;

public abstract class DojoWidgetRenderer extends DojoWidgetBaseRenderer {

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if (dojoComponent instanceof UIDojoWidget) {
            UIDojoWidget c = (UIDojoWidget) dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "dragRestriction", c.isDragRestriction()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "waiRole", c.getWaiRole()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "waiState", c.getWaiState()); // $NON-NLS-1$

            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onBlur", c.getOnBlur()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onClick", c.getOnClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onClose", c.getOnClose()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onShow", c.getOnShow()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onHide", c.getOnHide()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onDblClick", c.getOnDblClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onFocus", c.getOnFocus()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onKeyDown", c.getOnKeyDown()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onKeyPress", c.getOnKeyPress()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onKeyUp", c.getOnKeyUp()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseDown", c.getOnMouseDown()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseEnter", c.getOnMouseEnter()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseLeave", c.getOnMouseLeave()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseMove", c.getOnMouseMove()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseOut", c.getOnMouseOut()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseOver", c.getOnMouseOver()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onMouseUp", c.getOnMouseUp()); // $NON-NLS-1$
        }
    }
}