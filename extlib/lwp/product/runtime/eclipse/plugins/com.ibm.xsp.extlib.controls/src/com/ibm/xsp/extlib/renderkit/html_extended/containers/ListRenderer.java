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

package com.ibm.xsp.extlib.renderkit.html_extended.containers;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.containers.UIList;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * List container renderer.
 */
public class ListRenderer extends FacesRendererEx {

    // ==========================================================================
    // Rendering Properties
    // ==========================================================================

    // List style
    protected static final int PROP_LISTTAG             = 1;
    protected static final int PROP_LISTSTYLE           = 2;
    protected static final int PROP_LISTSTYLECLASS      = 3;
    protected static final int PROP_ITEMTAG             = 4;
    protected static final int PROP_ITEMSTYLE           = 5;
    protected static final int PROP_ITEMSTYLECLASS      = 6;
    protected static final int PROP_FIRSTITEMSTYLE      = 7;
    protected static final int PROP_FIRSTITEMSTYLECLASS = 8;
    protected static final int PROP_LASTITEMSTYLE       = 9;
    protected static final int PROP_LASTITEMSTYLECLASS  = 10;
    protected static final int PROP_ITEMDISPLAYNONESTYLE = 11;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LISTTAG:          return "ul"; // $NON-NLS-1$
            case PROP_ITEMTAG:          return "li"; // $NON-NLS-1$
            case PROP_ITEMDISPLAYNONESTYLE:          return "display: none;"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
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
        UIList container = (UIList)component;
        if(!container.isRendered()) {
            return;
        }
        ResponseWriter w = context.getResponseWriter();
        writeMainList(context, w, container);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
    
    
    // ================================================================
    // Main List
    // ================================================================
    
    protected void writeMainList(FacesContext context, ResponseWriter w, UIList c) throws IOException {
        String tag = (String)getProperty(PROP_LISTTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag,c);
            writeListAttributes(context, w, c);
            newLine(w);
        }
                
        if(c.getChildCount()>0) {
            writeChildren(context, w, c);
        }
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
            newLine(w);
        }
    }

    protected void writeListAttributes(FacesContext context, ResponseWriter w, UIList c) throws IOException {
        HtmlUtil.writeIdAttribute(context, c); // "id"
        String style = ExtLibUtil.concatStyles((String)getProperty(PROP_LISTSTYLE),c.getStyle());
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LISTSTYLECLASS),c.getStyleClass());
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
    }

    protected void writeChildren(FacesContext context, ResponseWriter w, UIList c) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(c);
        // this mostly just iterates through the children and calls writeChild
        // with logic control to determine whether this is the first and/or last
        // child where rendered evaluates to true.
        int count = children.size();
        boolean foundFirstRendered = false;
        int nextRenderedIndex = -1;
        for(int i=0; i<count; i++) {
            UIComponent child = children.get(i);
            
            if( nextRenderedIndex != -1 && nextRenderedIndex < i ){
                continue;
            }else if( i == nextRenderedIndex ){
                nextRenderedIndex = -1;
                // will render this child
            }else{ // check rendered
                if( ! child.isRendered() ){
                    if(count == 1) {
                        //SPR #TWET97NLR3 - a11y - when list is empty, render 
                        //a hidden list element
                        writeHiddenChild(context, w, c);
                    }
                    continue;
                }
            }
            // rendered evaluated to true for this child
            
            boolean first = ! foundFirstRendered;
            foundFirstRendered = true;
            
            boolean last;
            if( i == count-1 ){
                last = true;
            }else{
                nextRenderedIndex = -1;
                for (int j = i+1; j < count; j++) {
                    UIComponent sibling = children.get(j);
                    if( sibling.isRendered() ){
                        nextRenderedIndex = j;
                        break;
                    }
                }
                last = nextRenderedIndex == -1;
            }
            
            writeChild(context, w, c, child, first, last);
        }
    }

    protected void writeChild(FacesContext context, ResponseWriter w, UIList c, UIComponent child, boolean first, boolean last) throws IOException {
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag,c);
            writeItemAttributes(context, w, c, child, first, last);
            newLine(w);
        }

        FacesUtil.renderComponent(context, child);
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
            newLine(w);
        }
    }
    protected void writeHiddenChild(FacesContext context, ResponseWriter w, UIList c) throws IOException {
        //Writes a hidden list element
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag,c);
            String style = (String)getProperty(PROP_ITEMDISPLAYNONESTYLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
        }
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
        JSUtil.writeTextln(w);
    }
    protected void writeItemAttributes(FacesContext context, ResponseWriter w, UIList c, UIComponent child, boolean first, boolean last) throws IOException {
        String style = ExtLibUtil.concatStyles((String)getProperty(PROP_ITEMSTYLE),c.getItemStyle());
        if(first) {
            style = ExtLibUtil.concatStyles(style,(String)getProperty(PROP_FIRSTITEMSTYLE));
            style = ExtLibUtil.concatStyles(style,c.getFirstItemStyle());
        }
        if(last) {
            style = ExtLibUtil.concatStyles(style,(String)getProperty(PROP_LASTITEMSTYLE));
            style = ExtLibUtil.concatStyles(style,c.getLastItemStyle());
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        
        String styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_ITEMSTYLECLASS),c.getItemStyleClass());
        if(first) {
            styleClass = ExtLibUtil.concatStyleClasses(styleClass,(String)getProperty(PROP_FIRSTITEMSTYLECLASS));
            styleClass = ExtLibUtil.concatStyleClasses(styleClass,c.getFirstItemStyleClass());
        }
        if(last) {
            styleClass = ExtLibUtil.concatStyles(styleClass,(String)getProperty(PROP_LASTITEMSTYLECLASS));
            styleClass = ExtLibUtil.concatStyleClasses(styleClass,c.getLastItemStyleClass());
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
    }
}