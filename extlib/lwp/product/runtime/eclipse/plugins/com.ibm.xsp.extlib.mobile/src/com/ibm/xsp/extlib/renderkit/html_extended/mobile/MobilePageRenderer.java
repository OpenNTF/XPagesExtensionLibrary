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
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.JSUtil;

public class MobilePageRenderer extends Renderer {
    
    /*
     * MobilePages have three important attributes that should be noted: preload, resetContent and selected.
     * preload - If true, this MobilePage will be loaded in the DOM before it is accessed.
     * resetContent - If true, this MobilePage will always reload all of its content when it is accessed.
     * selected - If true, this MobilePage will be loaded and be the page that is displayed to the user.
     */
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
        ResponseWriter w = context.getResponseWriter();
        
        UIMobilePage page = (UIMobilePage)component;

        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        rootEx.setDojoParseOnLoad(true);
        rootEx.setDojoTheme(true);

        String clientId = component.getClientId(context);
        
        w.startElement("div", component); // $NON-NLS-1$
        
        // Setting a page name overrides the clientId. Therefore the user will need to make
        // sure that it's unique across the single application. Dojox.mobile requires us 
        // to use HTML ids meaning that normally the JSF client ids may appear in the URL, 
        // depending on the application this may be sub optimal, requiring this override.
        String pageName = page.getPageName();
        if(pageName != null){                
        	w.writeAttribute("pageName", pageName, "pageName"); // $NON-NLS-1$ $NON-NLS-2$        
        	w.writeAttribute("id", pageName, "id"); // $NON-NLS-1$ $NON-NLS-2$
        }
        else{
        	w.writeAttribute("pageName", clientId, "pageName"); // $NON-NLS-1$ $NON-NLS-2$        
        	w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$        	
        }
        
        String dojoType = getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            throw new IllegalStateException();
        }
        w.writeAttribute("dojoType", dojoType, "dojoType"); // $NON-NLS-1$ $NON-NLS-2$
        DojoModuleResource module = getDojoModule();
        if(module!=null) {
            ExtLibResources.addEncodeResource(rootEx, module);
        }
        
        boolean keepScrollPos = page.isKeepScrollPos();
        if(keepScrollPos != true/*defaults to true*/) {
            w.writeAttribute("keepScrollPos", "false", "keepScrollPos"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        
        boolean resetContent = page.isResetContent();
        if(resetContent) {
            w.writeAttribute("resetContent", resetContent, "resetContent"); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        boolean preload = page.isPreload();
        if ( preload ) {
            w.writeAttribute("preload", preload, "preload"); // $NON-NLS-1$ $NON-NLS-2$
        }

        
        //TODO: should this be changed/removed/merged with preload?
        boolean loaded = preload; //loaded is not editable by the user
        if ( loaded ) {
            w.writeAttribute("loaded", loaded, "loaded"); // $NON-NLS-1$ $NON-NLS-2$
        }
        String onBeforeTransitionIn = page.getOnBeforeTransitionIn();
        if( null != onBeforeTransitionIn ){
            w.writeAttribute("onBeforeTransitionIn", onBeforeTransitionIn, "onBeforeTransitionIn"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String onAfterTransitionIn = page.getOnAfterTransitionIn();
        if( null != onAfterTransitionIn ){
            w.writeAttribute("onAfterTransitionIn", onAfterTransitionIn, "onAfterTransitionIn"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String onBeforeTransitionOut = page.getOnBeforeTransitionOut();
        if( null != onBeforeTransitionOut ){
            w.writeAttribute("onBeforeTransitionOut", onBeforeTransitionOut, "onBeforeTransitionOut"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String onAfterTransitionOut = page.getOnAfterTransitionOut();
        if( null != onAfterTransitionOut ){
            w.writeAttribute("onAfterTransitionOut", onAfterTransitionOut, "onAfterTransitionOut"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        AttrsUtil.encodeAttrs(context, w, page);
        JSUtil.writeln(w);
    }


    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(w);
    }
    
    protected DojoModuleResource getDojoModule() {
        return ExtLibResources.extlibMobile;
    }
    
    protected String getDojoType() {
        return "extlib.dijit.mobile.View"; // $NON-NLS-1$
    }   
}
