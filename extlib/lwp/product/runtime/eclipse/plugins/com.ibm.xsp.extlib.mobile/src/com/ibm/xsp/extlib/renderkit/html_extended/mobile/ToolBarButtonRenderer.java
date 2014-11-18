/*
 * © Copyright IBM Corp. 2012
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

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.component.mobile.UIToolBarButton;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetBaseRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.DirLangUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.webapp.FacesResourceServlet;

/**
 * @author Arturas Lebedevas
 */
public class ToolBarButtonRenderer extends DojoWidgetBaseRenderer {
    
    private static boolean isGlobalResourceUri(String uri) {
        int n = uri.indexOf(FacesResourceServlet.RESOURCE_PREFIX);
        return n >= 0;
    }
    
    
    private static String encodeHref(FacesContext context, String href) {
        if(href==null) {
            return null;
        }
        
        if (!FacesUtil.isAbsoluteUrl(href)) {// $NON-NLS-1$
            // adapt relative URLS
            // [Note, this used to check the target property too, like
            //   StringUtil.isEmpty(target) && !isGlobalResourceUri(href)
            // but that check was removed as part of TSOE7R7LUG
            // We think this had been introduced for managing some portal 
            // behaviors where targets where not available. On Domino 
            // it should encode the actions when a target is present. ]
            if ( !isGlobalResourceUri(href) ) {
                // We have 3 kinds of URLs:
                //    1. dir/resource
                //          Relative to the current page - nothing should be done
                //    2. /dir/resource
                //          Relative to the app root - should adapt it using getResourceURL()
                //    3. /[appcontextpath/dir/resource
                //          Relative to the app context path. Nothing should then be done here
                if (href.startsWith("/")) { // $NON-NLS-1$
                    String appContextPath = context.getExternalContext().getRequestContextPath();
                    if (!href.startsWith(appContextPath)){
                        ViewHandler viewHandler = context.getApplication()
                                .getViewHandler();
                        href = viewHandler.getResourceURL(context, href);
                    }
                }
                
                // If this URL is pointing to an xsp page, then we should encode it as an action URL
                // This encoding delegates to the servlet engine and add a sessionId parameter if
                // needed (cookies not enabled, for example).
                if(RenderUtil.isXspUrl(href)) {
                    href = context.getExternalContext().encodeActionURL(href);
                } else {
                    href = context.getExternalContext().encodeResourceURL(href);
                }
            } else {
                // This is a global url, starting with "/.ibmxspres/"
                // we had to add a prefix if requested
                href = context.getExternalContext().encodeResourceURL(href);
            }
        }
        return href;
    }

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
        return "dojox.mobile.ToolBarButton"; // $NON-NLS-1$
    }
    
    @Override
    protected String getTagName() {
        return "span"; // $NON-NLS-1$
    }
    @Override
    protected boolean isHasTooltipProperty() {
        // no tooltip property, doesn't make sense on a mobile device.
        return false;
    }

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIToolBarButton) {
            UIToolBarButton c = (UIToolBarButton)dojoComponent;
            
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"label", c.getLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onClick", c.getOnClick()); // $NON-NLS-1$
            
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"transition", c.getTransition()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs,"transitionDir", c.getTransitionDir()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs,"icon", c.getIcon()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"iconPos", c.getIconPos()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs,"moveTo", c.getMoveTo()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"hrefTarget", c.getHrefTarget()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs,"href", c.getHref()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs,"url", c.getUrl()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"urlTarget", c.getUrlTarget()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"back", c.isBack()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"callback", c.getCallback()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"toggle", c.isToggle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"alt", c.getAlt()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabIndex", c.getTabIndex()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"selected", c.isSelected()); // $NON-NLS-1$
//            DojoRendererUtil.addDojoHtmlAttributes(attrs, "arrow", c.getArrow()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "light", c.isLight(), /*default*/true); // $NON-NLS-1$
            
            /* moveTo property */
            String moveTo = c.getMoveTo();
            if(StringUtil.isNotEmpty(moveTo) && !moveTo.startsWith("#")) { // $NON-NLS-1$
                moveTo = "#" + moveTo; // $NON-NLS-1$
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"moveTo", moveTo); // $NON-NLS-1$
            
            /* href property */
            String href = c.getHref();
            if(StringUtil.isNotEmpty(href)) {
                href = encodeHref(context, href);
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"href", href); // $NON-NLS-1$
            }
            
            /* url property */
            String url = c.getUrl();
            if(StringUtil.isNotEmpty(url)) {
                url = encodeHref(context, url);
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"url", url); // $NON-NLS-1$
            }
            
            /* icon property */
            String icon = c.getIcon();
            if(StringUtil.isNotEmpty(icon)) {
                icon = HtmlRendererUtil.getImageURL(context, icon);
                DojoRendererUtil.addDojoHtmlAttributes(attrs, "icon", icon); // $NON-NLS-1$
            }

            /* urlTarget property */
            String urlTarget = c.getUrlTarget();
            if(StringUtil.isNotEmpty(urlTarget)) {
                UIComponent targetComponent = FacesUtil.getComponentFor(c, urlTarget);
                String targetID = (targetComponent != null) ? targetComponent.getClientId(context) : urlTarget;
                DojoRendererUtil.addDojoHtmlAttributes(attrs, "urlTarget", targetID); // $NON-NLS-1$
            }
            
            /* transitionDir property */
            String tranitionDir = c.getTransitionDir();
            if(StringUtil.isNotEmpty(tranitionDir)) {
                String res = null; // $NON-NLS-1$
                if(StringUtil.equals(tranitionDir, "rtl")) { // $NON-NLS-1$
                    res = "-1";  // $NON-NLS-1$
                } else if(StringUtil.equals(tranitionDir, "ltr")) {  // $NON-NLS-1$
                    res = "1";  // $NON-NLS-1$
                } else {
                    res = tranitionDir;
                }
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"transitionDir", res); // $NON-NLS-1$
            }
            
            
            /* arrow property */
            String arrow = c.getArrow();
            if(StringUtil.isNotEmpty(arrow)) {
                String res = arrow;
                boolean isRTL = DirLangUtil.isRTL(c);
                if(StringUtil.equals(arrow, "previous")) { // $NON-NLS-1$
                    res = (!isRTL ? "left" : "right"); // $NON-NLS-1$ // $NON-NLS-2$
                } else if(StringUtil.equals(arrow, "next")) { // $NON-NLS-1$
                    res = (!isRTL ? "right" : "left"); // $NON-NLS-1$ // $NON-NLS-2$
                }
                DojoRendererUtil.addDojoHtmlAttributes(attrs, "arrow", res); // $NON-NLS-1$
            }
        }
    }    
}