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

package com.ibm.xsp.extlib.renderkit.html_extended.data;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.data.UIPagerExpand;


public class PagerExpandRenderer extends AbstractPagerRenderer {

    protected static final int PROP_COLLAPSETEXT    = 10;
    protected static final int PROP_EXPANDTEXT      = 15;

    protected static final int PROP_LISTTAG         = 20;
    protected static final int PROP_LISTCLASS       = 21;
    protected static final int PROP_LISTSTYLE       = 22;

    protected static final int PROP_ITEMTAG         = 25;
    protected static final int PROP_COLLAPSECLASS   = 26;
    protected static final int PROP_COLLAPSESTYLE   = 27;
    protected static final int PROP_EXPANDCLASS     = 28;
    protected static final int PROP_EXPANDSTYLE     = 29;
        
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_COLLAPSETEXT:     return "Collapse All"; // $NLS-PagerExpandRenderer.CollapseAll-1$
            case PROP_EXPANDTEXT:       return "Expand All"; // $NLS-PagerExpandRenderer.ExpandAll-1$
            
            case PROP_LISTTAG:          return "ul"; // $NON-NLS-1$

            case PROP_ITEMTAG:          return "li"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    
    @Override
    protected boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix) {
        try {
            if(idSuffix.equals("ea")) { // $NON-NLS-1$
                pagerEvent.setAction(UIPagerExpand.ACTION_EXPANDALL);
                return true;
            } else if(idSuffix.equals("ca")) { // $NON-NLS-1$
                pagerEvent.setAction(UIPagerExpand.ACTION_COLLAPSEALL);
                return true;
            }
        } catch(Exception ex) {}
        return false;
    }
    
    @Override
    protected void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager _pager, FacesDataIterator dataIterator) throws IOException {
        UIPagerExpand pager = (UIPagerExpand)_pager;
        String tag = (String)getProperty(PROP_LISTTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
            String style = (String)getProperty(PROP_LISTSTYLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style,null); // $NON-NLS-1$
            }
            String styleClass = (String)getProperty(PROP_LISTCLASS);
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
            }
        }
        writeCollapseAll(context, w, pager, dataIterator);
        writeSeparator(context, w, pager, dataIterator);
        writeExpandAll(context, w, pager, dataIterator);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }

    protected void writeCollapseAll(FacesContext context, ResponseWriter w, UIPagerExpand pager, FacesDataIterator dataIterator) throws IOException {
        String text = pager.getCollapseText();
        if(StringUtil.isEmpty(text)) {
            text = (String)getProperty(PROP_COLLAPSETEXT);
        }
        if(StringUtil.isNotEmpty(text)) {
            String tag = (String)getProperty(PROP_ITEMTAG);
            if(StringUtil.isNotEmpty(tag)) {
                w.startElement(tag, null);
                String style = (String)getProperty(PROP_COLLAPSESTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style,null); // $NON-NLS-1$
                }
                String styleClass = (String)getProperty(PROP_COLLAPSECLASS);
                if(StringUtil.isNotEmpty(styleClass)) {
                    w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
                }
            }
            boolean selected = pager.isCollapseAll();
            if(!selected) {
                w.startElement("a", null);
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                String clientId = pager.getClientId(context);
                String sourceId = clientId+"_ca"; // $NON-NLS-1$
                w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            }
            w.writeText(text,null);
            if(!selected) {
                w.endElement("a");
            }
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
        }
    }

    protected void writeSeparator(FacesContext context, ResponseWriter w, UIPagerExpand pager, FacesDataIterator dataIterator) throws IOException {
        // not write any separator text, instead the separator is achieved using CSS styles
        // so there are no character encoding issues in other countries
    }

    protected void writeExpandAll(FacesContext context, ResponseWriter w, UIPagerExpand pager, FacesDataIterator dataIterator) throws IOException {
        String text = pager.getExpandText();
        if(StringUtil.isEmpty(text)) {
            text = (String)getProperty(PROP_EXPANDTEXT);
        }
        if(StringUtil.isNotEmpty(text)) {
            String tag = (String)getProperty(PROP_ITEMTAG);
            if(StringUtil.isNotEmpty(tag)) {
                w.startElement(tag, null);
                String style = (String)getProperty(PROP_EXPANDSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style,null); // $NON-NLS-1$
                }
                String styleClass = (String)getProperty(PROP_EXPANDCLASS);
                if(StringUtil.isNotEmpty(styleClass)) {
                    w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
                }
            }
            boolean selected = pager.isExpandAll();
            if(!selected) {
                w.startElement("a", null);
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                String clientId = pager.getClientId(context);
                String sourceId = clientId+"_ea"; // $NON-NLS-1$
                w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            }
            w.writeText(text,null);
            if(!selected) {
                w.endElement("a");
            }
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
        }
    }
}