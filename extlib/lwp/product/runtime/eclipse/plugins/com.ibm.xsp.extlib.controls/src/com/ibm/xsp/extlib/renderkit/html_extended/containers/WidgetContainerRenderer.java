/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.renderkit.html_extended.containers;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.extlib.component.util.EventHandlerUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.RootContainerTreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * Widget container renderer.
 */
public class WidgetContainerRenderer extends FacesRendererEx {

    // Compatible with UISection for reusing the same script
    final protected String _INITCLOSED = "_closed"; // $NON-NLS-1$
    final protected String _CONTENTS = "_contents"; // $NON-NLS-1$
    final protected String _OPENED = "_open"; // $NON-NLS-1$
    final protected String _CLOSED = "_close"; // $NON-NLS-1$
    final protected String _LKOPENED = "_lk_open"; // $NON-NLS-1$
    final protected String _LKCLOSED = "_lk_close"; // $NON-NLS-1$
    final protected String _TITLE = "_title"; // $NON-NLS-1$

    
    // default style class 
    final protected String _DISPLAY_NONE = "display: none;"; // $NON-NLS-1$
    final protected String _DISPLAY_BLOCK = "display: block;"; // $NON-NLS-1$
    
    // ==========================================================================
    // Rendering Properties
    // ==========================================================================

    // Main widget
    protected static final int PROP_CSSWIDGETBASIC          = 1;
    protected static final int PROP_CSSWIDGETSIDEBAR        = 2;
    protected static final int PROP_CSSWIDGETPLAIN          = 3;
    protected static final int PROP_CONTAINER_STYLE_DEFAULT = 4;
    protected static final int PROP_BLANKIMG                = 10;
    
    // Title bar
    protected static final int PROP_TAGTITLE                = 11;
    protected static final int PROP_CSSTITLEBAR             = 12;
    protected static final int PROP_STYLETITLEBAR           = 13;
    protected static final int PROP_TAGTITLETEXT            = 14;
    protected static final int PROP_CSSTITLETEXT            = 15;
    protected static final int PROP_CSSTITLEIMG             = 16;
    protected static final int PROP_TREEDROPDOWN            = 17;
    protected static final int PROP_TITLE_PREVENT_BLANK     = 18;
    protected static final int PROP_TWISTYCLASSLINK         = 19;
    protected static final int PROP_TWISTYCLASSIMGOPEN      = 20;
    protected static final int PROP_TWISTYCLASSIMGCLOSE     = 21;
    
    // Header
    protected static final int PROP_TAGHEADER               = 30;
    protected static final int PROP_CSSHEADER               = 41;

    // Body
    protected static final int PROP_CSSBODY                 = 40;
    protected static final int PROP_CSSSCROLLUP             = 41;
    protected static final int PROP_CSSSCROLLUPLINK         = 42;
    protected static final int PROP_CSSSCROLLUPALTTEXT      = 43;
    protected static final int PROP_CSSSCROLLDOWN           = 44;
    protected static final int PROP_CSSSCROLLDOWNLINK       = 45;
    protected static final int PROP_CSSSCROLLDOWNALTTEXT    = 46;
    protected static final int PROP_BODY_PREVENT_BLANK      = 47;
    
    // Footer
    protected static final int PROP_TAGFOOTER               = 60;
    protected static final int PROP_CSSFOOTER               = 61;
    

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // TODO talk to Tony about the non-OneUI widget container styling.
            // TODO dropdown menu not displayed for non-OneUI widget container
            // Container div
            case PROP_CONTAINER_STYLE_DEFAULT: return "border: solid thin black; padding:0px; margin: 2px;"; //$NON-NLS-1$

            // Twisty 
            // Title Bar
            case PROP_TAGTITLE:         return "div"; // $NON-NLS-1$
            case PROP_TAGTITLETEXT:     return "span"; // $NON-NLS-1$
            case PROP_TITLE_PREVENT_BLANK: return true;
            case PROP_STYLETITLEBAR:    return "border-bottom: solid thin black; background-color:#DDDDDD"; //$NON-NLS-1$
            
            // Header
            case PROP_TAGHEADER:        return "h3"; // $NON-NLS-1$
            
            // Body
            // TODO scrollUp and scrollDown buttons not appearing for non-OneUI widget container styling
            case PROP_BODY_PREVENT_BLANK:  return true;
            
            // Footer
            case PROP_TAGFOOTER:        return "div"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(component instanceof UIWidgetContainer) {
            UIWidgetContainer wc = (UIWidgetContainer)component;
            
            // Get current state from request map
            String fieldId = component.getClientId(context) + _INITCLOSED; 
            String it_closed = (String)context.getExternalContext().getRequestParameterMap().get(fieldId);
            if(StringUtil.isNotEmpty(it_closed)) {
                if(Boolean.valueOf(it_closed)){
                   wc.setClosed(true);
                } else {
                   wc.setClosed(false);
                }
            }
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        UIWidgetContainer container = (UIWidgetContainer)component;
        if(!container.isRendered()) {
            return;
        }
        ResponseWriter w = context.getResponseWriter();
        writeMainFrame(context, w, container);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
    
    
    // ================================================================
    // Main Frame
    // ================================================================
    
    protected void writeMainFrame(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        // Start the main frame
        w.startElement("div",c); // $NON-NLS-1$
        String id = c.getClientId(context);
        w.writeAttribute("id",id,null); // $NON-NLS-1$

        w.writeAttribute("role", "region", null); // $NON-NLS-1$ $NON-NLS-2$
        if (c.isTitleBar()) {
            w.writeAttribute("aria-labelledby", id+_TITLE, null); // $NON-NLS-1$
        }

        boolean closed = c.isClosed();
        w.writeAttribute("aria-expanded", Boolean.toString(!closed), null); // $NON-NLS-1$

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
        
        // Write the title bar
        if(c.isTitleBar()) {
            writeTitleBar(context, w, c);
        }
        
        // Write the widget content (the children)
        writeContent(context, w, c);

        // Write the footer, if any
        writeFooter(context, w, c);
        
        // Close the main frame
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
        
    }

    
    // ================================================================
    // TitleBar
    // ================================================================
    
    protected void writeTitleBar(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String tag = (String)getProperty(PROP_TAGTITLE);
        w.startElement(tag,c);

        String id = c.getClientId(context);
        w.writeAttribute("id",id+_TITLE,null); // $NON-NLS-1$

        String cls = (String)getProperty(PROP_CSSTITLEBAR);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class",cls,null); // $NON-NLS-1$
        }
        String style = (String)getProperty(PROP_STYLETITLEBAR);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }       

        // Write the collapsible arrow
        writeCollapsible(context, w, c);
        
        // Write the title
        writeTitle(context, w, c);
        
        // Write the dropdown menu
        if(c.isDropDownRendered()) {
            writeDropDown(context, w, c);
        }
        
        w.endElement(tag);

        writeCollapsibleInput(context, w, c);
    }

    protected void writeCollapsibleInput(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(c.isCollapsible()) {
            // Add a hidden field to cary out the section state
            String id = c.getClientId(context);
            boolean closed = c.isClosed();
            w.startElement("input",c);  // this is for the uistate $NON-NLS-1$
            w.writeAttribute("id",id + _INITCLOSED,null); // $NON-NLS-1$
            w.writeAttribute("name",id + _INITCLOSED,null); // $NON-NLS-1$
            w.writeAttribute("type","hidden",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("value",Boolean.toString(closed),null);     // closed = true means section will be closed $NON-NLS-1$
            w.endElement("input"); // $NON-NLS-1$
            
            // Initialize the script for the links
            StringBuilder buff = new StringBuilder();
            JavaScriptUtil.appendInitSectionScript(
                    context, 
                    buff, 
                    id+_LKCLOSED,
                    id,
                    true); 
            JavaScriptUtil.appendInitSectionScript(
                    context, 
                    buff, 
                    id+_LKOPENED,
                    id,
                    false); 
            JavaScriptUtil.addScriptOnLoad(buff.toString());
        }
    }
    
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

            w.startElement("img",c); // $NON-NLS-1$
            String collapseStr = "Collapse section"; // $NLS-WidgetContainerRenderer.Collapsesection-1$
            w.writeAttribute("aria-label",collapseStr,null); // $NON-NLS-1$
            w.writeAttribute("alt",collapseStr,null); // $NON-NLS-1$
            String bgif = (String)getProperty(PROP_BLANKIMG);
            if(StringUtil.isNotEmpty(bgif)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
            }
            String clsOpen = (String)getProperty(PROP_TWISTYCLASSIMGOPEN);
            if(StringUtil.isNotEmpty(clsOpen)) {
                w.writeAttribute("class",clsOpen,null); // $NON-NLS-1$
            }

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

            w.startElement("img",c); // $NON-NLS-1$
            String expandStr = "Expand section"; //$NLS-WidgetContainerRenderer.Expandsection-1$
            w.writeAttribute("aria-label",expandStr,null); // $NON-NLS-1$ 
            w.writeAttribute("alt",expandStr,null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(bgif)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
            }
            String clsClose = (String)getProperty(PROP_TWISTYCLASSIMGCLOSE);
            if(StringUtil.isNotEmpty(clsClose)) {
                w.writeAttribute("class",clsClose,null); // $NON-NLS-1$
            }

            w.endElement("a"); //$NON-NLS-1$
            w.endElement("span"); //$NON-NLS-1$
        }
    }

    protected void writeTitle(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String title = c.getTitleBarText();
        if(StringUtil.isNotEmpty(title)) {
            String href = c.getTitleBarHref();
            boolean hasLink = StringUtil.isNotEmpty(href);
            String tagText = (String)getProperty(PROP_TAGTITLETEXT);
            w.startElement(tagText,c);
            String cls = (String)getProperty(PROP_CSSTITLETEXT);
            if(StringUtil.isNotEmpty(cls)) {
                w.writeAttribute("class",cls,null); // $NON-NLS-1$
            }
            if(hasLink) {
                w.startElement("a",c); //$NON-NLS-1$
                RenderUtil.writeLinkAttribute(context, w, href);
            }

            w.writeText(title,null);

            if(hasLink) {
                w.endElement("a"); //$NON-NLS-1$
            }
            w.endElement(tagText);
        }else{ 
            boolean isTitlePreventBlank = (Boolean) getProperty(PROP_TITLE_PREVENT_BLANK);
            if( isTitlePreventBlank ){
                JSUtil.writeTextBlank(w); // &nbsp;
            }
        }
    }

    protected void writeDropDown(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        List<ITreeNode> nodes = c.getDropDownNodes();
        if(nodes!=null && !nodes.isEmpty()) {
            ITree tree = TreeImpl.get(new RootContainerTreeNode(c.getDropDownNodes()));
            if(tree!=null) {
                // TODO the non-OneUI WidgetContainerRenderer will not output dropDownNodes,
                // need to update this so its possible.
                AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_TREEDROPDOWN);
                if(renderer!=null) {
                    renderer.render(context, c, tree, w);
                }
            }
        }
    }

    
    // ================================================================
    // Main content
    // ================================================================

    protected void writeContent(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        boolean scrollable = c.isScrollable();
        
        w.startElement("div", c); // $NON-NLS-1$
        String cls = (String)getProperty(PROP_CSSBODY);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String id = c.getClientId(context);
        w.writeAttribute("id",id+_CONTENTS,null); // $NON-NLS-1$

        // Manage the section state
        boolean closed = c.isClosed();
        if(closed){
            w.writeAttribute("style", _DISPLAY_NONE, null);  // $NON-NLS-1$
        } else {
            w.writeAttribute("style", _DISPLAY_BLOCK, null);  // $NON-NLS-1$
        }
        
        // Write the header, if any
        writeHeader(context, w, c);
        
        if(scrollable) {
            writeBodyScrollUp(context, w, c);
        }
        
        writeBodyContent(context, w, c);

        if(scrollable) {
            writeBodyScrollDown(context, w, c);
        }
        
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writeBodyContent(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        FacesUtil.renderChildren(context, c);
        if( c.getChildCount() == 0 ){
            boolean isBodyPreventBlank = (Boolean) getProperty(PROP_BODY_PREVENT_BLANK);
            if( isBodyPreventBlank ){
                JSUtil.writeTextBlank(w); // &nbsp;
            }
        }
    }
    protected void writeBodyScrollUp(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(!c.isDisableScrollUp()) {
            w.startElement("div", c); // $NON-NLS-1$
            String clsDiv = (String)getProperty(PROP_CSSSCROLLUP);
            if(StringUtil.isNotEmpty(clsDiv)) {
                w.writeAttribute("class", clsDiv, null); // $NON-NLS-1$
            }
            w.startElement("a", c);
            String clsA = (String)getProperty(PROP_CSSSCROLLUPLINK);
            if(StringUtil.isNotEmpty(clsA)) {
                w.writeAttribute("class", clsA, null); // $NON-NLS-1$
            }
            w.writeAttribute("role","button",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("title","Scroll up",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.Scrollup-2$
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            String script = EventHandlerUtil.getEventScript(context, c, "onScrollUp"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(script)) {
                w.writeAttribute("onClick", script, null); // $NON-NLS-1$
            }
            writeBodyScrollUpAltText(context, w, c);
            w.endElement("a");
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    protected void writeBodyScrollUpAltText(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String alt = (String)getProperty(PROP_CSSSCROLLUPALTTEXT);
        if(StringUtil.isNotEmpty(alt)) {
            w.startElement("span", c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusAltText", null); //$NON-NLS-1$ //$NON-NLS-2$
            w.writeText(alt, "\u25B2"); //$NON-NLS-1$
            w.endElement("span"); // $NON-NLS-1$
        }
    }
    
    protected void writeBodyScrollDown(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        if(!c.isDisableScrollDown()) {
            w.startElement("div", c); // $NON-NLS-1$
            String clsDiv = (String)getProperty(PROP_CSSSCROLLDOWN);
            if(StringUtil.isNotEmpty(clsDiv)) {
                w.writeAttribute("class", clsDiv, null); // $NON-NLS-1$
            }
            w.startElement("a", c);
            String clsA = (String)getProperty(PROP_CSSSCROLLDOWNLINK);
            if(StringUtil.isNotEmpty(clsA)) {
                w.writeAttribute("class", clsA, null); // $NON-NLS-1$
            }
            w.writeAttribute("role","button",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("title","Scroll down",null); // $NON-NLS-1$ $NLS-WidgetContainerRenderer.Scrolldown-2$
            w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            String script = EventHandlerUtil.getEventScript(context, c, "onScrollDown"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(script)) {
                w.writeAttribute("onClick", script, null); // $NON-NLS-1$
            }
            writeBodyScrollDownAltText(context, w, c);
            w.endElement("a");
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    protected void writeBodyScrollDownAltText(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        String alt = (String)getProperty(PROP_CSSSCROLLDOWNALTTEXT);
        if(StringUtil.isNotEmpty(alt)) {
            w.startElement("span", c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusAltText", null); //$NON-NLS-1$ //$NON-NLS-2$
            w.writeText(alt, "\u25BC"); //$NON-NLS-1$
            w.endElement("span"); // $NON-NLS-1$
        }
    }
    
        
    
    // ================================================================
    // Header
    // ================================================================

    protected void writeHeader(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        UIComponent header = c.getFacet(UIWidgetContainer.FACET_HEADER);
        if(header!=null) {
            String tag = (String)getProperty(PROP_TAGHEADER);
            w.startElement(tag, c);
            String cls = (String)getProperty(PROP_CSSHEADER);
            if(StringUtil.isNotEmpty(cls)) {
                w.writeAttribute("class", cls, null); // $NON-NLS-1$
            }
            FacesUtil.renderChildren(context, header);
            
            w.endElement(tag);
        }
    }
    
    
    // ================================================================
    // Footer
    // ================================================================

    protected void writeFooter(FacesContext context, ResponseWriter w, UIWidgetContainer c) throws IOException {
        UIComponent footer = c.getFacet(UIWidgetContainer.FACET_FOOTER);
        if(footer!=null) {
            String tag = (String)getProperty(PROP_TAGFOOTER);
            w.startElement(tag, null);
            String cls = (String)getProperty(PROP_CSSFOOTER);
            if(StringUtil.isNotEmpty(cls)) {
                w.writeAttribute("class", cls, null); // $NON-NLS-1$
            }
            FacesUtil.renderChildren(context, footer);
            
            w.endElement(tag);
        }
    }
}