/*
 * © Copyright IBM Corp. 2010, 2013
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.mobile.UIApplication;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.StyleSheetResource;
import com.ibm.xsp.util.JSUtil;

/**
 * @author Niklas Heidloff
 */

public class ApplicationRenderer extends Renderer {
	public static final String IPHONE_THEME_NAME = "iphone"; //$NON-NLS-1$
	public static final String ANDROID_THEME_NAME = "android";//$NON-NLS-1$
	public static final String BLACKBERRY_THEME_NAME = "blackberry";//$NON-NLS-1$
	
	static final StyleSheetResource[] IPHONE_STYLE_SHEETS = {
			ExtLibResources.dojoXMobileIPhoneCSS,
            ExtLibResources.customMobileCSS,
            ExtLibResources.customIPhoneCSS
	};
	static final StyleSheetResource[] BLACKBERRY_STYLE_SHEETS = {
			ExtLibResources.dojoXMobileBlackBerryCSS,
            ExtLibResources.customMobileCSS,
            ExtLibResources.customBlackBerryCSS
	};
	static final StyleSheetResource[] ANDROID_STYLE_SHEETS = {
			ExtLibResources.dojoXMobileAndroidCSS,
            ExtLibResources.customMobileCSS,
            ExtLibResources.customAndroidCSS
	};
	
	@Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
		
        UIApplication appPageContainer = (UIApplication)component;
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        String themeName = ((FacesContextEx)context).getStyleKit().getName();
        
        StyleSheetResource[] styleSheets = {};
        
        if(themeName.equals(ANDROID_THEME_NAME)){
        	styleSheets = ANDROID_STYLE_SHEETS;
        } else if(themeName.equals(BLACKBERRY_THEME_NAME)){
        	styleSheets = BLACKBERRY_STYLE_SHEETS;
        } else if(themeName.equals(IPHONE_THEME_NAME)){
            styleSheets = IPHONE_STYLE_SHEETS; 
        } else {
            // don't provide the resources at this level but instead let the *.theme provide them
            // ... this ensures correct ordering determined by the theme resource order
        	styleSheets = new StyleSheetResource[] {};
        }
        for(StyleSheetResource css : styleSheets){
        	ExtLibResources.addEncodeResource(rootEx, css);
        }
        ResponseWriter w = context.getResponseWriter();      

        w.startElement("div", null); // $NON-NLS-1$
        String dojoType = getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            throw new IllegalStateException();
        }
        // always write the ID, so any event listeners will work
        w.writeAttribute("id", appPageContainer.getClientId(context), null); //$NON-NLS-1$
        w.writeAttribute("dojoType", dojoType, "dojoType"); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("class", "singlePageApp", "class"); //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
		w.writeAttribute("selectedAppPage",appPageContainer.getSelectedPageName(),"selectedAppPage"); // $NON-NLS-1$ $NON-NLS-2$
        String onOrientationChange = appPageContainer.getOnOrientationChange();
        if( null != onOrientationChange ){
            w.writeAttribute("onOrientationChange", onOrientationChange, "onOrientationChange"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String onResize = appPageContainer.getOnResize();
        if( null != onResize ){
            w.writeAttribute("onResize", onResize, "onResize"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        w.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(w);
    }
	
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
    }
    
    protected DojoModuleResource getDojoModule() {
        return ExtLibResources.extlibMobile;
    }
    
    protected String getDojoType() {
        return "extlib.dijit.mobile.Application"; // $NON-NLS-1$
    }   
}
