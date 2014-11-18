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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.containers;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.extlib.component.util.EventHandlerUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.containers.OneUIWidgetContainerRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline.tree.OneUIv302WidgetDropDownRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * @author kevin
 *
 */
public class OneUIv302WidgetContainerRenderer extends OneUIWidgetContainerRenderer {
    
    protected static final int PROP_SECTION_HEADER_CSS    =   100;
    protected static final int PROP_SECTION_INNER_CSS     =   101;
    protected static final int PROP_SECTION_SUBHEADER_CSS =   102;
    protected static final int PROP_CSSSUBHEADER          =   103;
    protected static final int PROP_CSSTITLEBAR_PORTLET   =   110;
    protected static final int PROP_CSSSCROLLUPIMAGE      =   120;
    protected static final int PROP_CSSSCROLLDOWNIMAGE    =   121;
    
    // default style class 
    final private String _DISPLAY_NONE = "display: none;"; // $NON-NLS-1$
    final private String _DISPLAY_BLOCK = "display: block;"; // $NON-NLS-1$
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            //Main
            case PROP_CSSWIDGETBASIC:       return "lotusSection2"; // $NON-NLS-1$
            case PROP_CSSWIDGETSIDEBAR:     return "lotusSection2 lotusPortlet"; // $NON-NLS-1$
            case PROP_CSSWIDGETPLAIN:       return "lotusSection lotusSectionPlain"; // $NON-NLS-1$
            case PROP_CONTAINER_STYLE_DEFAULT: return null;
            case PROP_SECTION_HEADER_CSS:   return "lotusSectionHeader"; // $NON-NLS-1$
            case PROP_SECTION_SUBHEADER_CSS:   return "lotusSubHeader"; // $NON-NLS-1$
            case PROP_SECTION_INNER_CSS:    return "lotusInner"; // $NON-NLS-1$
            
            // Title Bar
            case PROP_TAGTITLE:             return "h2"; // $NON-NLS-1$
            case PROP_CSSTITLEBAR:          return "lotusHeading"; // $NON-NLS-1$
            case PROP_CSSTITLEBAR_PORTLET:  return "lotusHeading lotusFirst"; // $NON-NLS-1$
            case PROP_TAGTITLETEXT:         return "span"; // $NON-NLS-1$
            case PROP_CSSTITLETEXT:         return "lotusLeft"; // $NON-NLS-1$
            case PROP_CSSTITLEIMG:          return "lotusRight"; // $NON-NLS-1$
            case PROP_STYLETITLEBAR:        return ""; // Not movable.... $NON-NLS-1$
            case PROP_TREEDROPDOWN:         return new OneUIv302WidgetDropDownRenderer();
            // title bar looks ok when no text present - no need to insert nbsp 
            case PROP_TITLE_PREVENT_BLANK:  return false;

            // Header
            case PROP_TAGHEADER:            return "h3"; // $NON-NLS-1$
            case PROP_CSSSUBHEADER:         return "lotusHeading3"; // $NON-NLS-1$
            
            // Body
            case PROP_CSSBODY:              return "lotusSectionBody"; // $NON-NLS-1$
            case PROP_CSSSCROLLUP:          return "lotusSectionScroll"; // $NON-NLS-1$
            case PROP_CSSSCROLLUPLINK:      return "lotusSprite lotusArrow"; // $NON-NLS-1$
            case PROP_CSSSCROLLUPIMAGE:     return "lotusScrollUp"; // $NON-NLS-1$
           // case PROP_CSSSCROLLUPALTTEXT:   return "&#x25b2;"; //$NON-NLS-1$
            case PROP_CSSSCROLLUPALTTEXT:   return "\u25B2"; //$NON-NLS-1$
            
            case PROP_CSSSCROLLDOWN:        return "lotusSectionScroll"; // $NON-NLS-1$
            case PROP_CSSSCROLLDOWNLINK:    return "lotusSprite lotusArrow"; // $NON-NLS-1$
            case PROP_CSSSCROLLDOWNIMAGE:   return "lotusScrollDown"; // $NON-NLS-1$
            
            //case PROP_CSSSCROLLDOWNALTTEXT: return "&#x25bc;"; //$NON-NLS-1$
            case PROP_CSSSCROLLDOWNALTTEXT: return "\u25BC"; //$NON-NLS-1$
            
            // body looks ok when no text present - no need to insert nbsp 
            case PROP_BODY_PREVENT_BLANK:   return false;
            
            // Footer
            case PROP_CSSFOOTER:            return "lotusSectionFooter"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
    protected void writeMainFrame(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        // Start the main frame
        w.startElement("div",c); // $NON-NLS-1$
        w.writeAttribute("id",c.getClientId(context),null); // $NON-NLS-1$
        
        String type = c.getType();
        String cls = c.getStyleClass();
        if(StringUtil.equals(type, UIWidgetContainer.TYPE_SIDEBAR)) {
            cls = ExtLibUtil.concatStyleClasses(cls, (String)getProperty(PROP_CSSWIDGETSIDEBAR));
        } else if(StringUtil.equals(type, UIWidgetContainer.TYPE_PLAIN)) {
            cls = ExtLibUtil.concatStyleClasses(cls, (String)getProperty(PROP_CSSWIDGETPLAIN));
        } else {
            cls = ExtLibUtil.concatStyleClasses(cls, (String)getProperty(PROP_CSSWIDGETBASIC));
        }
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class",cls,null); // $NON-NLS-1$
        }
        String style = c.getStyle();
        if( StringUtil.isEmpty(style) ){
            // note, this default style is not concat'd
            style = (String) getProperty(PROP_CONTAINER_STYLE_DEFAULT);
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        newLine(w);
       if(!StringUtil.equals(type, UIWidgetContainer.TYPE_PLAIN)){   
        // Write the title bar
        if(c.isTitleBar()) {
            writeTitleBar(context, w, c);
        }
       }
       
        // Write the wub-header, if any
        writeSubHeader(context, w, c);
       
        // Write the widget content (the children)
        writeContent(context, w, c);

        // Write the footer, if any
        writeFooter(context, w, c);
        
        // Close the main frame
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }
    
    @Override
    protected void writeTitleBar(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String type = c.getType();
        w.startElement("div", c); // $NON-NLS-1$
        String secClass = (String) getProperty(PROP_SECTION_HEADER_CSS);
        w.writeAttribute("class", secClass, null); // $NON-NLS-1$
        w.startElement("div", c); // $NON-NLS-1$
        String innerCss = (String) getProperty(PROP_SECTION_INNER_CSS);
        w.writeAttribute("class", innerCss, null); // $NON-NLS-1$

        // Write the collapsible arrow
        writeCollapsible(context, w, c);
        
        String tag = (String)getProperty(PROP_TAGTITLE);
        w.startElement(tag,c);
        String cls = "";
        if(StringUtil.equals(type, UIWidgetContainer.TYPE_SIDEBAR)) {
           cls  = (String)getProperty(PROP_CSSTITLEBAR_PORTLET);
        }else{
            cls = (String)getProperty(PROP_CSSTITLEBAR);
        }
        
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class",cls,null); // $NON-NLS-1$
        }
        String style = (String)getProperty(PROP_STYLETITLEBAR);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        
        // Write the title
        writeTitle(context, w, c);
        w.endElement(tag);
        // Write the dropdown menu
        if(c.isDropDownRendered()) {
            writeDropDown(context, w, c);
        }
        
        w.endElement("div"); // $NON-NLS-1$
        w.endElement("div"); // $NON-NLS-1$

        writeCollapsibleInput(context, w, c);
    }
    
    @Override
    protected void writeTitle(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String title = c.getTitleBarText();
        if(StringUtil.isNotEmpty(title)) {
            String href = c.getTitleBarHref();
            boolean hasLink = StringUtil.isNotEmpty(href);
            if(hasLink) {
                w.startElement("a",c); //$NON-NLS-1$
                RenderUtil.writeLinkAttribute(context, w, href);
            }

            w.writeText(title,null);

            if(hasLink) {
                w.endElement("a"); //$NON-NLS-1$
            }
           
        }else{ 
            boolean isTitlePreventBlank = (Boolean) getProperty(PROP_TITLE_PREVENT_BLANK);
            if( isTitlePreventBlank ){
                JSUtil.writeTextBlank(w); // &nbsp;
            }
        }
    }
    
    @Override
    protected void writeBodyScrollUp(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(!c.isDisableScrollUp()) {
            w.startElement("div", c); // $NON-NLS-1$
            String clsDiv = (String)getProperty(PROP_CSSSCROLLUP);
            if(StringUtil.isNotEmpty(clsDiv)) {
                w.writeAttribute("class", clsDiv, null); // $NON-NLS-1$
            }
            w.startElement("a", c); // $NON-NLS-1$
            String clsA = (String)getProperty(PROP_CSSSCROLLUPLINK);
            if(StringUtil.isNotEmpty(clsA)) {
                w.writeAttribute("class", clsA, null); // $NON-NLS-1$
            }
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            String script = EventHandlerUtil.getEventScript(context, c, "onScrollUp"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(script)) {
                w.writeAttribute("onClick", script, null); // $NON-NLS-1$
            }
            w.writeAttribute("title","Scroll up",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.Scrollup-2$
            String upImg = (String)getProperty(PROP_CSSSCROLLUPIMAGE);
            w.startElement("img", c); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(upImg)){
                w.writeAttribute("class", upImg, null); // $NON-NLS-1$
            }
            
            String blankImg = (String)getProperty(PROP_BLANKIMG);
            if(StringUtil.isNotEmpty(blankImg)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,blankImg),null); // $NON-NLS-1$
            }
            w.writeAttribute("alt", "", null); // $NON-NLS-1$
            
            String alt = (String)getProperty(PROP_CSSSCROLLUPALTTEXT);
            if(StringUtil.isNotEmpty(alt)) {
                w.startElement("span", c); // $NON-NLS-1$
                w.writeAttribute("class", "lotusAltText", null); //$NON-NLS-1$ //$NON-NLS-2$
                w.writeText(alt, null);
                w.endElement("span"); // $NON-NLS-1$
            }
            w.endElement("a"); // $NON-NLS-1$
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    @Override
    protected void writeBodyScrollDown(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(!c.isDisableScrollDown()) {
            w.startElement("div", c); // $NON-NLS-1$
            String clsDiv = (String)getProperty(PROP_CSSSCROLLDOWN);
            if(StringUtil.isNotEmpty(clsDiv)) {
                w.writeAttribute("class", clsDiv, null); // $NON-NLS-1$
            }
            w.startElement("a", c); // $NON-NLS-1$
            String clsA = (String)getProperty(PROP_CSSSCROLLDOWNLINK);
            if(StringUtil.isNotEmpty(clsA)) {
                w.writeAttribute("class", clsA, null); // $NON-NLS-1$
            }
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            String script = EventHandlerUtil.getEventScript(context, c, "onScrollDown"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(script)) {
                w.writeAttribute("onClick", script, null); // $NON-NLS-1$
            }
            w.writeAttribute("title","Scroll down",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.Scrolldown-2$
            String dwnImg = (String)getProperty(PROP_CSSSCROLLDOWNIMAGE);
            w.startElement("img", c); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(dwnImg)){
                w.writeAttribute("class", dwnImg, null); // $NON-NLS-1$
            }
            
            String blankImg = (String)getProperty(PROP_BLANKIMG);
            if(StringUtil.isNotEmpty(blankImg)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,blankImg),null); // $NON-NLS-1$
            }
            
            w.writeAttribute("alt", "", null); // $NON-NLS-1$
            
            String alt = (String)getProperty(PROP_CSSSCROLLDOWNALTTEXT);
            if(StringUtil.isNotEmpty(alt)) {
                w.startElement("span", c); // $NON-NLS-1$
                w.writeAttribute("class", "lotusAltText", null); //$NON-NLS-1$ //$NON-NLS-2$
                w.writeText(alt, null);
                w.endElement("span"); // $NON-NLS-1$
            }
            w.endElement("a"); // $NON-NLS-1$
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    
    // ================================================================
    // Header
    // ================================================================
    @Override
    protected void writeHeader(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        //Create empty header within content div
    	//The header in OneUI302 should be in its own subHeader div
    	//writeSubHeader now handles outputting the header
    }
    
    protected void writeSubHeader(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        UIComponent header = c.getFacet(UIWidgetContainer.FACET_HEADER);
        if(header!=null) {
            w.startElement("div", c); // $NON-NLS-1$
            String clsSection = (String)getProperty(PROP_SECTION_SUBHEADER_CSS);
            if(StringUtil.isNotEmpty(clsSection)) {
                w.writeAttribute("class", clsSection, null); // $NON-NLS-1$
            }
            
            // Manage the section state
            boolean closed = c.isClosed();
            if(closed){
                w.writeAttribute("style", _DISPLAY_NONE, null);  // $NON-NLS-1$
            } else {
                w.writeAttribute("style", _DISPLAY_BLOCK, null);  // $NON-NLS-1$
            }
            
            String tag = (String)getProperty(PROP_TAGHEADER);
            w.startElement(tag, c);
            String clsSubheader = (String)getProperty(PROP_CSSSUBHEADER);
            if(StringUtil.isNotEmpty(clsSubheader)) {
                w.writeAttribute("class", clsSubheader, null); // $NON-NLS-1$
            }
            FacesUtil.renderChildren(context, header);
            //close header tag
            w.endElement(tag);
            //close sub-header section
            w.endElement("div"); // $NON-NLS-1$
        }
    }
}
