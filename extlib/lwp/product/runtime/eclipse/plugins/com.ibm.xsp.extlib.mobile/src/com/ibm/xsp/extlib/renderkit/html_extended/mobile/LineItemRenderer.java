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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.mobile.UILineItem;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * This renderer fills in all of the properties for a dojox.mobile.ListItem component.
 * @author Niklas Heidloff
 */

public class LineItemRenderer extends DojoWidgetRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if (AjaxUtil.isAjaxNullResponseWriter(writer)) {
            return;
        }

        // And write the value
        if (component instanceof UILineItem) {
            writeTag(context, (UILineItem) component, writer);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    	if (component instanceof UILineItem) {
    		ResponseWriter writer = context.getResponseWriter();
    		endTag(context, writer, component);
    	}

    }

	@Override
	protected DojoModuleResource getDefaultDojoModule(FacesContext context,
			FacesDojoComponent component) {
		return ExtLibResources.dojoxMobile;
	}

	@Override
	protected String getDefaultDojoType(FacesContext context,
			FacesDojoComponent component) {
		return "dojox.mobile.ListItem"; // $NON-NLS-1$
	}

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UILineItem) {
        	UILineItem c = (UILineItem)dojoComponent;       
        	
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rightText",c.getRightText()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"transition",c.getTransition()); // $NON-NLS-1$
            String moveTo = c.getMoveTo();
            if(StringUtil.isNotEmpty(moveTo) && !moveTo.startsWith("#")) { // $NON-NLS-1$
                moveTo = "#" + moveTo;  // $NON-NLS-1$
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"moveTo",moveTo); // $NON-NLS-1$
            String icon = c.getIcon();
        	if( StringUtil.isNotEmpty(icon)){
        		DojoRendererUtil.addDojoHtmlAttributes(attrs,"icon", HtmlRendererUtil.getImageURL(context, icon)); // $NON-NLS-1$
        	}
        }
    }
	
	@Override
	protected String getTagName() {
		return "li"; //$NON-NLS-1$
	}
}
