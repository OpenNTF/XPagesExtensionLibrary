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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree.WidgetDropDownRenderer;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.util.FacesUtil;

public class WidgetContainerRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.containers.WidgetContainerRenderer {

    protected static final int PROP_TWISTYCLASSIMGOPEN = 20;
    protected static final int PROP_TWISTYCLASSIMGCLOSE = 21;
    protected static final int PROP_CSSHEADERCLASS = 31;

    public WidgetContainerRenderer() {}

    @Override
    protected Object getProperty(int prop) {
        switch (prop) {
            // Main
            case PROP_CSSWIDGETBASIC:                   return "panel xspWidgetContainer"; // $NON-NLS-1$
            case PROP_CSSWIDGETSIDEBAR:                 return "panel xspWidgetContainer"; // $NON-NLS-1$
            case PROP_CSSWIDGETPLAIN:                   return "panel xspWidgetContainer"; // $NON-NLS-1$
            case PROP_CONTAINER_STYLE_DEFAULT:          return null;
            // Title Bar
            case PROP_TAGTITLE:                         return "div"; // $NON-NLS-1$
            case PROP_CSSTITLEBAR:                      return "panel-heading"; // $NON-NLS-1$
            case PROP_STYLETITLEBAR:                    return "overflow: auto; cursor: auto;"; // $NON-NLS-1$
            case PROP_TAGTITLETEXT:                     return "h5"; // $NON-NLS-1$
            case PROP_CSSTITLETEXT:                     return "panel-title pull-left"; // $NON-NLS-1$
            case PROP_TREEDROPDOWN:                     return new WidgetDropDownRenderer();
            // title bar looks ok when no text present - no need to insert nbsp
            case PROP_TITLE_PREVENT_BLANK:              return false;
            case PROP_TWISTYCLASSIMGOPEN:               return Resources.get().getIconClass("chevron-down") + " pull-left"; // $NON-NLS-1$ $NON-NLS-2$
            case PROP_TWISTYCLASSIMGCLOSE:              return Resources.get().getIconClass("chevron-right") + " pull-left"; // $NON-NLS-1$ $NON-NLS-2$
            // Header
            case PROP_TAGHEADER:                        return "div"; // $NON-NLS-1$
            case PROP_CSSHEADERCLASS:                   return "xspWidgetHeader"; // $NON-NLS-1$
            // Body
            case PROP_CSSSCROLLUP:                      return "widget-section-scroll"; // $NON-NLS-1$
            case PROP_CSSSCROLLUPLINK:                  return "widget-section-arrow " + Resources.get().getIconClass("arrow-up"); // $NON-NLS-1$ $NON-NLS-2$
            case PROP_CSSSCROLLUPALTTEXT:               return "&#x25b2;"; //$NON-NLS-1$
            case PROP_CSSSCROLLDOWN:                    return "widget-section-scroll"; // $NON-NLS-1$
            case PROP_CSSSCROLLDOWNLINK:                return "widget-section-arrow " + Resources.get().getIconClass("arrow-down"); // $NON-NLS-1$ $NON-NLS-2$
            case PROP_CSSSCROLLDOWNALTTEXT:             return "&#x25bc;"; //$NON-NLS-1$
            // body looks ok when no text present - no need to insert nbsp
            case PROP_BODY_PREVENT_BLANK:               return false;
            // Footer
            case PROP_TAGFOOTER:                        return "div"; // $NON-NLS-1$
            case PROP_CSSFOOTER:                        return "panel-footer"; // $NON-NLS-1$
            case PROP_CSSBODY:                          return "xspWidgetBody"; // $NON-NLS-1$
        }
        return null;
    }

    @Override
    protected void writeDropDown(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        w.startElement("div", null); // $NON-NLS-1$
        w.writeAttribute("class", "pull-right", null); // $NON-NLS-1$ $NON-NLS-2$
        super.writeDropDown(context, w, c);
        w.endElement("div"); // $NON-NLS-1$
    }
    
    // ================================================================
    // Header
    // ================================================================
    @Override
    protected void writeHeader(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        UIComponent header = c.getFacet(UIWidgetContainer.FACET_HEADER);
        if(header!=null) {
            String tag = (String)getProperty(PROP_TAGHEADER);
            w.startElement(tag, c);

            //TODO Possible bug in extlib WidgetContainer. PROP_CSSHEADER & PROP_CSSSCROLLUP are
            //both set to the same property value (41). Overriding the writeHeader method to fix it here
            //but it may need to be fixed in extlib instead
            String cls = (String)getProperty(PROP_CSSHEADERCLASS);
            if(StringUtil.isNotEmpty(cls)) {
                w.writeAttribute("class", cls, null); // $NON-NLS-1$
            }
            FacesUtil.renderChildren(context, header);
            
            w.endElement(tag);
        }
    }
    
    @Override
    protected void writeCollapsible(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(c.isCollapsible()) {
            boolean closed = c.isClosed();
            String id = c.getClientId(context);

            //Open twisty
            w.startElement("span",c); // $NON-NLS-1$
            w.writeAttribute("id",id+_OPENED,null); // $NON-NLS-1$
            if(closed){
                w.writeAttribute("style", _DISPLAY_NONE, null);  // $NON-NLS-1$
            } else {
                w.writeAttribute("style", _DISPLAY_BLOCK, null);  // $NON-NLS-1$
            }
            w.startElement("a",c);
            w.writeAttribute("id",id+_LKOPENED,null); // $NON-NLS-1$
            String clsLk = (String)getProperty(PROP_TWISTYCLASSLINK);
            if(StringUtil.isNotEmpty(clsLk)) {
                w.writeAttribute("class",clsLk,null); // $NON-NLS-1$
            }
            w.writeAttribute("role","button",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("title","click to collapse the section",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.clicktocollapsethesection-2$

            if (closed) {
                // accesskey
                String accesskey = c.getAccesskey();
                if (StringUtil.isNotEmpty(accesskey)) {
                    w.writeAttribute("accesskey", accesskey, null); // tabindex $NON-NLS-1$
                }
                // tabindex
                String tabindex = c.getTabindex();
                if (StringUtil.isNotEmpty(tabindex)) {
                    w.writeAttribute("tabindex", tabindex, null); // tabindex $NON-NLS-1$
                } else {
                    w.writeAttribute("tabindex", "0", null); // tabindex $NON-NLS-1$
                }
            }
            
            w.startElement("span",c); // $NON-NLS-1$
            w.writeAttribute("class","lotusAltText", null); // $NON-NLS-1$ $NON-NLS-2$
            // up arrow
            w.writeText("\u25B2", null);  // //$NON-NLS-1$
            //<span class="lotusAltText">&#x25bc;</span></a>
            w.endElement("span"); //$NON-NLS-1$

            w.startElement("div",c); // $NON-NLS-1$
            String collapseStr = "Collapse section"; // $NLS-WidgetContainerRenderer.Collapsesection-1$
            w.writeAttribute("aria-label",collapseStr,null); // $NON-NLS-1$
            String clsOpen = (String)getProperty(PROP_TWISTYCLASSIMGOPEN);
            if(StringUtil.isNotEmpty(clsOpen)) {
                w.writeAttribute("class",clsOpen,null); // $NON-NLS-1$
            }
            w.endElement("div"); //$NON-NLS-1$
            w.endElement("a"); //$NON-NLS-1$
            w.endElement("span"); //$NON-NLS-1$
            
            //Close twisty
            w.startElement("span",c); // $NON-NLS-1$
            w.writeAttribute("id",id+_CLOSED,null); // $NON-NLS-1$
            if(closed){
                w.writeAttribute("style", _DISPLAY_BLOCK, null);  // $NON-NLS-1$
            } else {
                w.writeAttribute("style", _DISPLAY_NONE, null);  // $NON-NLS-1$
            }
            w.startElement("a",c);
            w.writeAttribute("id",id+_LKCLOSED,null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(clsLk)) {
                w.writeAttribute("class",clsLk,null); // $NON-NLS-1$
            }
            w.writeAttribute("role","button",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("title","click to expand the section",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.clicktoexpandthesection-2$

            w.startElement("span",c); // $NON-NLS-1$
            w.writeAttribute("class","lotusAltText", null); // $NON-NLS-1$ $NON-NLS-2$
            // down arrow
            w.writeText("\u25BC", null);  // //$NON-NLS-1$
            //<span class="lotusAltText">&#x25bc;</span></a>
            w.endElement("span"); //$NON-NLS-1$

            w.startElement("div",c); // $NON-NLS-1$
            String expandStr = "Expand section"; //$NLS-WidgetContainerRenderer.Expandsection-1$
            w.writeAttribute("aria-label",expandStr,null); // $NON-NLS-1$ 
            String clsClose = (String)getProperty(PROP_TWISTYCLASSIMGCLOSE);
            if(StringUtil.isNotEmpty(clsClose)) {
                w.writeAttribute("class",clsClose,null); // $NON-NLS-1$
            }
            w.endElement("div"); //$NON-NLS-1$
            w.endElement("a"); //$NON-NLS-1$
            w.endElement("span"); //$NON-NLS-1$
        }
    }
}