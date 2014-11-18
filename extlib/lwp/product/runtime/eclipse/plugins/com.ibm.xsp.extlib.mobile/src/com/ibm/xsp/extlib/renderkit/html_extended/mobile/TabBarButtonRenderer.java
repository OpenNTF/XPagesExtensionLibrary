/*
 * © Copyright IBM Corp. 2010, 2014
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
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.component.mobile.UITabBarButton;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetBaseRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * 
 */
public class TabBarButtonRenderer extends DojoWidgetBaseRenderer { 
	
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
        if (component instanceof UIDojoWidgetBase) {
            writeTag(context, (UIDojoWidgetBase) component, writer);
        }
        
	}
	
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibMobile;
    }

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dojox.mobile.TabBarButton"; // $NON-NLS-1$
    }
    
    @Override
    protected String getTagName() {
        return "li"; // $NON-NLS-1$
    }

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UITabBarButton) {
        	UITabBarButton c = (UITabBarButton)dojoComponent;
        	
        	DojoRendererUtil.addDojoHtmlAttributes(attrs,"label", c.getLabel()); // $NON-NLS-1$
        	DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconPos1", c.getIconPos1()); // $NON-NLS-1$
        	DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconPos2", c.getIconPos2()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"selected", c.isSelected(), /*defaultValue*/false); // $NON-NLS-1$
        	DojoRendererUtil.addDojoHtmlAttributes(attrs,"transition", c.getTransition()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"selectOne", c.isSelectOne(), /*defaultValue*/true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onClick", c.getOnClick()); // $NON-NLS-1$
            
            String icon1 = c.getIcon1();
        	if( StringUtil.isNotEmpty(icon1) ){
        		DojoRendererUtil.addDojoHtmlAttributes(attrs,"icon1",HtmlRendererUtil.getImageURL(context, icon1)); // $NON-NLS-1$
        	}
        	String icon2 = c.getIcon2();
        	if( StringUtil.isNotEmpty(icon2) ){
        		DojoRendererUtil.addDojoHtmlAttributes(attrs,"icon2", HtmlRendererUtil.getImageURL(context,icon2)); // $NON-NLS-1$
        	}
        }
    }    
}