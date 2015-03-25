/*
 * © Copyright IBM Corp. 2014
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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 10 Oct 2014
* NavbarRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.impl.TreeImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.BootstrapLogger;
import com.ibm.xsp.theme.bootstrap.components.responsive.UINavbar;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers.tree.NavbarLinksRenderer;
//import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers.tree.NavbarRightLinksRenderer;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class NavbarRenderer extends FacesRendererEx {

    //Container
    protected static final int PROP_CLASSCONTAINER                 = 1;
    protected static final int PROP_STYLECONTAINER                 = 2;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // Container div
            case PROP_CLASSCONTAINER:                return "xspNavbar"; //$NON-NLS-1$
            case PROP_STYLECONTAINER:                return ""; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    // ================================================================
    // Nav Bar
    // ================================================================
    protected void writeNavbar(FacesContext context, ResponseWriter w, UINavbar component) throws IOException{
        boolean inverted    = false;
        String fixed        = "";
        String style        = "";
        String styleClass   = "";
        String title        = "";
        String pageWidth    = "";
        boolean hasChildren = false;
        ITree beforeLinks   = null;
        ITree afterLinks    = null;

        if(component!=null) {
            inverted    = component.isInverted();
            fixed       = component.getFixed();
            styleClass  = component.getStyleClass();
            style       = component.getStyle();
            title       = component.getTitle();
            pageWidth   = component.getPageWidth();
            hasChildren = component.getChildCount() > 0;
            beforeLinks = TreeImpl.get(component.getNavbarBeforeLinks());
            afterLinks  = TreeImpl.get(component.getNavbarAfterLinks());
        }
        
        // write navbar container
        w.startElement("div", component); // $NON-NLS-1$
        
        if(HtmlUtil.isUserId(component.getId())) {
            String clientId = component.getClientId(context);
            w.writeAttribute("id", clientId, null); // $NON-NLS-1$ $NON-NLS-2$
        }

        if(StringUtil.isNotEmpty(title)) {
            w.writeAttribute("title", title, null); // $NON-NLS-1$
        }else{
            w.writeAttribute("title", "navigation bar", null); // $NON-NLS-1$ $NLS-NavbarRenderer.navigationbar-2$
        }
        
        String role = "navigation"; // $NON-NLS-1$
        w.writeAttribute("role", role, null); // $NON-NLS-1$

        String fixedClass = "";
        if(!StringUtil.isEmpty(fixed)){
            if(fixed.equals(UINavbar.NAVBAR_FIXED_TOP)) {
                fixedClass = "navbar-fixed-top"; // $NON-NLS-1$
            }else if(fixed.equals(UINavbar.NAVBAR_FIXED_BOTTOM)) {
                fixedClass = "navbar-fixed-bottom"; // $NON-NLS-1$
            }else if(fixed.equals(UINavbar.NAVBAR_UNFIXED_TOP)) {
                fixedClass = "navbar-static-top"; // $NON-NLS-1$
            }else{
                fixedClass = "";
            }
        }
        
        String navClass = ExtLibUtil.concatStyleClasses("navbar " + (inverted ? "navbar-inverse " : "navbar-default ") + fixedClass, styleClass); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        String containerMixinClass = ExtLibUtil.concatStyleClasses(navClass, (String)getProperty(PROP_CLASSCONTAINER));
        if(StringUtil.isNotEmpty(containerMixinClass)) {
            w.writeAttribute("class", containerMixinClass, null); // $NON-NLS-1$
        }
        String containerMixinStyle = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_STYLECONTAINER), style);
        if(StringUtil.isNotEmpty(containerMixinStyle)) {
            w.writeAttribute("style", containerMixinStyle, null); // $NON-NLS-1$
        }
        newLine(w);
        
        //CSS required for fixed navbar
        if (!StringUtil.isEmpty(fixed)) {
            if(fixed.equals(UINavbar.NAVBAR_FIXED_TOP) || fixed.equals(UINavbar.NAVBAR_FIXED_BOTTOM)) {
                w.startElement("style", component); // $NON-NLS-1$
                w.writeAttribute("type", "text/css", null); // $NON-NLS-1$ $NON-NLS-2$
                if(fixed.equals(UINavbar.NAVBAR_FIXED_TOP)) {
                    w.writeText("body {padding-top:51px;} @media (min-width: 768px) {.sidebar{top:52px;bottom:0px;}}", null); // $NON-NLS-1$
                }else if(fixed.equals(UINavbar.NAVBAR_FIXED_BOTTOM)){ // $NON-NLS-1$
                    w.writeText("body {padding-bottom:51px;}  @media (min-width: 768px) {.sidebar{top:0px;bottom:52px;}}", null); // $NON-NLS-1$
                }else{
                    // don't write any styles
                }
                w.endElement("style"); // $NON-NLS-1$
                newLine(w);
            }
        }
        
        //container div
        w.startElement("div", component); // $NON-NLS-1$
        if ( pageWidth != null && pageWidth.equals(UINavbar.WIDTH_FLUID)) {
            w.writeAttribute("class", "container-fluid", null); // $NON-NLS-1$ $NON-NLS-2$
        } else if ( pageWidth != null && pageWidth.equals(UINavbar.WIDTH_FIXED)) {
            w.writeAttribute("class", "container", null); // $NON-NLS-1$ $NON-NLS-2$
        } else {
            w.writeAttribute("class", "container-fluid", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        // write navbar-header
        boolean collapsible = hasChildren || beforeLinks != null || afterLinks != null;
        writeHeading(context, w, component, collapsible);

        // start collapse container div
        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("class", "navbar-collapse collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        
        // write navbar before links
        if(beforeLinks != null) {
            writeBeforeLinks(context, w, component, beforeLinks);
        }
        // write navbar children
        if(hasChildren && getRendersChildren()) {
            w.startElement("div", component); // $NON-NLS-1$
            w.writeAttribute("class", "nav navbar-nav", null); // $NON-NLS-1$ $NON-NLS-2$
            renderChildren(context, w, component);
            w.endElement("div"); // $NON-NLS-1$
        }
        // write navbar after links
        if(afterLinks != null) {
            writeAfterLinks(context, w, component, afterLinks);
        }
        
        // close collapse container div
        w.endElement("div"); // $NON-NLS-1$

        // close container div
        w.endElement("div"); // $NON-NLS-1$
        
        // Close the main frame
        w.endElement("div"); // $NON-NLS-1$
    }
    
    protected void writeHeading(FacesContext context, ResponseWriter w, UINavbar c, boolean linksExist) throws IOException {
        String headingText  = c.getHeadingText();
        String headingStyle = c.getHeadingStyle();
        String headingClass = c.getHeadingStyleClass();
        
        //start header div
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar-header", null); // $NON-NLS-1$ $NON-NLS-2$
        
        //Write hidden div for attaching collapsible menus
        if(linksExist) {
            writeCollapsedLink(context, w, c);
        }
        
        // start brand div
        if(StringUtil.isNotEmpty(headingText)) {
            w.startElement("div", c); // $NON-NLS-1$
            String headerClazz = ExtLibUtil.concatStyleClasses("navbar-brand", headingClass); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(headerClazz)) {
                w.writeAttribute("class", headerClazz, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(headingStyle)) {
                w.writeAttribute("style", headingStyle, null); // $NON-NLS-1$
            }
            w.writeText(headingText, null); // $NON-NLS-1$
            
            //end brand div
            w.endElement("div"); // $NON-NLS-1$
        }
        // close header div
        w.endElement("div"); // $NON-NLS-1$
    }
    
    protected void writeBeforeLinks(FacesContext context, ResponseWriter w, UINavbar c, ITree beforeLinks) throws IOException {
        if(null != beforeLinks) {
            AbstractTreeRenderer renderer = new NavbarLinksRenderer(NavbarLinksRenderer.POSITION_LEFT);
            if (renderer != null) {
                renderer.render(context, c, "navbar_left", beforeLinks, w); // $NON-NLS-1$
            }
        }
    }
    
    protected void writeAfterLinks(FacesContext context, ResponseWriter w, UINavbar c, ITree afterLinks) throws IOException {
        if(null != afterLinks) {
            AbstractTreeRenderer renderer = new NavbarLinksRenderer(NavbarLinksRenderer.POSITION_RIGHT);
            if (renderer != null) {
                renderer.render(context, c, "navbar_right", afterLinks, w); // $NON-NLS-1$
            }
        }
    }
    
    protected void writeCollapsedLink(FacesContext context, ResponseWriter w, UINavbar c) throws IOException {
        w.startElement("button", c); // $NON-NLS-1$
        w.writeAttribute("type",  "button",  null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("class", "navbar-toggle", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("data-toggle", "collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("data-target", ".navbar-collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "sr-only", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        
        w.endElement("button"); // $NON-NLS-1$
    }
    
    /**
     * Render the children of the navbar
     * @designer.publicmethod
     */
    public void renderChildren(FacesContext context, ResponseWriter w, UIComponent component) throws IOException {
        // encode component and children
        // for children of the navbar we need to add a CSS class
        
        int count = component.getChildCount();
        if(count>0) {
            List<?> children = component.getChildren();
            for (int i=0; i<count; i++) {
                Object child = children.get(i);
                boolean addDiv = false;
                
                try{
                    //Determine if the child has a 'navbar-' CSS class assigned already
                    Method method = child.getClass().getMethod("getStyleClass", (Class[])null); // $NON-NLS-1$
                    Object result = method.invoke(child, (Object[])null);
                    String styleClass = (result != null) ? (String)result : null;
                    if(styleClass != null && styleClass.contains("navbar-")){ // $NON-NLS-1$
                        //navbar class already assigned, don't do anything
                        addDiv = false;
                    }else{
                        //no navbar class assigned, wrap the control in a div with class 'navbar-text'
                        addDiv = true;
                        w.startElement("div", component); // $NON-NLS-1$
                        w.writeAttribute("class", "navbar-text", null); // $NON-NLS-1$ $NON-NLS-2$
                    }
                } catch (Exception e) {
                    if(BootstrapLogger.BOOTSTRAP.isErrorEnabled()) {
                        BootstrapLogger.BOOTSTRAP.errorp(this, "renderChildren", e, "Exception occured while rendering Navbar children"); // $NON-NLS-1$ $NLX-NavbarRenderer.ExceptionoccuredwhilstrenderingNa-2$
                    }
                }
                
                UIComponent compChild = (UIComponent)child;
                FacesUtil.renderComponent(context, compChild);
                if(addDiv) {
                    //close containing div
                    w.endElement("div"); // $NON-NLS-1$
                }
            }
        }
    }
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UINavbar c = (UINavbar) component;
        if (!c.isRendered()) {
            return;
        }
        writeNavbar(context, w, c);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        //Nothing to do here, all handled in write nav bar
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        
    }

}