/*
 * © Copyright IBM Corp. 2010, 2011, 2015
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
import com.ibm.xsp.extlib.component.data.UIPagerSizes;


public class PagerSizesRenderer extends AbstractPagerRenderer {

    protected static final int PROP_TEXT            = 10;
    protected static final int PROP_SIZES           = 16;
    protected static final int PROP_ALLTEXT         = 17;

    protected static final int PROP_LISTTAG         = 20;
    protected static final int PROP_LISTCLASS       = 21;
    protected static final int PROP_LISTSTYLE       = 22;

    protected static final int PROP_ITEMTAG         = 25;
    protected static final int PROP_FIRSTCLASS      = 26;
    protected static final int PROP_FIRSTSTYLE      = 27;
    protected static final int PROP_MIDDLECLASS     = 28;
    protected static final int PROP_MIDDLESTYLE     = 29;
    protected static final int PROP_LASTCLASS       = 30;
    protected static final int PROP_LASTSTYLE       = 31;
    
    @Override
    protected Object getProperty(int prop) {
        {
            // translating some extra strings that are unused here in the extlib.control plugin,
            // but are used in the other themes - e.g. the bootstrap DataViewRenderer.
            String str = "";
            str = "Show all items"; // $NLS-PagerSizesRenderer.Showallitems-1$
            str = "Show {0} items at once"; // $NLS-PagerSizesRenderer.Show0itemsatonce-1$
            // Note these strings are used by the xp:pager control, not the xe:pagerSizes control:
            str = "Pager"; // $NLS-PagerRenderer.Pager-1$
            str = "First page"; // $NLS-PagerRenderer.Firstpage-1$
            str = "Previous page"; // $NLS-PagerRenderer.Previouspage-1$
            str = "Next page"; // $NLS-PagerRenderer.Nextpage-1$
            str = "Last page"; // $NLS-PagerRenderer.Lastpage-1$
            str = "Page {0}"; // $NLS-PagerRenderer.Page0-1$
            // end xp:pager strings
            str.getClass(); // prevent unused variable warning
        }// end translating extra string
        
        switch(prop) {
            case PROP_TEXT:             return "Show {0} items per page"; // $NLS-PagerSizesRenderer.Show0itemsperpage-1$
            case PROP_ALLTEXT:          return "All"; // $NLS-PagerSizesRenderer.All-1$
            // PLEASE note, this string is not localizable - it defines a format.
            case PROP_SIZES:            return "10|25|50|all"; //$NON-NLS-1$
            case PROP_LISTTAG:          return "ul"; // $NON-NLS-1$

            case PROP_ITEMTAG:          return "li"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
    protected boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix) {
        try {
            int nrows = Integer.parseInt(idSuffix);
            pagerEvent.setAction(UIPagerSizes.ACTION_SETROWS);
            pagerEvent.setPage(Math.min(nrows, UIPagerSizes.ALL_MAX));
            component.queueEvent(pagerEvent);
            return true;
        } catch(Exception ex) {return false;}
    }
    
    @Override
    protected void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager _pager, FacesDataIterator dataIterator) throws IOException {
        UIPagerSizes pager = (UIPagerSizes)_pager;

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

        String text = pager.getText();
        if(StringUtil.isEmpty(text)) {
            text = (String)getProperty(PROP_TEXT);
        }
        int pos = text.indexOf("{0}"); //$NON-NLS-1$
        writerStartText(context, w, pager, dataIterator, text,pos);
        writerPages(context, w, pager, dataIterator, text,pos);
        writerEndText(context, w, pager, dataIterator, text,pos);
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }

    protected void writerStartText(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        if(-1 != pos) {
            text = text.substring(0,pos);
        }// else, output entire text at start when {0} absent
        if(StringUtil.isNotEmpty(text)) {
            String tag = (String)getProperty(PROP_ITEMTAG);
            if(StringUtil.isNotEmpty(tag)) {
                w.startElement(tag, null);
                String style = (String)getProperty(PROP_FIRSTSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style,null); // $NON-NLS-1$
                }
                String styleClass = (String)getProperty(PROP_FIRSTCLASS);
                if(StringUtil.isNotEmpty(styleClass)) {
                    w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
                }
            }
            w.writeText(text,null);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
        }
    }
    
    protected void writerEndText(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        if(-1 != pos) {
            text = text.substring(pos+3);
        } else {
            // do not output end text when {0} absent
            return;
        }
        if(StringUtil.isNotEmpty(text)) {
            String tag = (String)getProperty(PROP_ITEMTAG);
            if(StringUtil.isNotEmpty(tag)) {
                w.startElement(tag, null);
                String style = (String)getProperty(PROP_LASTSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style,null); // $NON-NLS-1$
                }
                String styleClass = (String)getProperty(PROP_LASTCLASS);
                if(StringUtil.isNotEmpty(styleClass)) {
                    w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
                }
            }
            w.writeText(text,null);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
        }
    }

    protected void writeSeparator(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator) throws IOException {
    }

    protected void writerPages(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        String sizestr = pager.getSizes();
        if(StringUtil.isEmpty(sizestr)) {
            sizestr = (String)getProperty(PROP_SIZES);
        }
        if(StringUtil.isNotEmpty(sizestr)) {
            String[] sizes = StringUtil.splitString(sizestr, '|', true);
            for(int i=0; i<sizes.length; i++) {
                if(i==0) {
                    writerFirstPages(context, w, pager, dataIterator, sizes, i);
                } else if(i==sizes.length-1) {
                    writeSeparator(context, w, pager, dataIterator);
                    writerLastPages(context, w, pager, dataIterator, sizes, i);
                } else {
                    writeSeparator(context, w, pager, dataIterator);
                    writerMiddlePages(context, w, pager, dataIterator, sizes, i);
                }
            }
        }
    }
    
    protected void writerFirstPages(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
            String style = (String)getProperty(PROP_FIRSTSTYLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style,null); // $NON-NLS-1$
            }
            String styleClass = (String)getProperty(PROP_FIRSTCLASS);
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
            }
        }
        writerItemContent(context, w, pager, dataIterator, sizes, idx);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }
    protected void writerMiddlePages(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
            String style = (String)getProperty(PROP_MIDDLESTYLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style,null); // $NON-NLS-1$
            }
            String styleClass = (String)getProperty(PROP_MIDDLECLASS);
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
            }
        }
        writerItemContent(context, w, pager, dataIterator, sizes, idx);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }
    protected void writerLastPages(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
//          String style = (String)getProperty(PROP_LASTSTYLE);
//          if(StringUtil.isNotEmpty(style)) {
//              w.writeAttribute("style", style,null);
//          }
//          String styleClass = (String)getProperty(PROP_LASTCLASS);
//          if(StringUtil.isNotEmpty(styleClass)) {
//              w.writeAttribute("class", styleClass,null);
//          }
        }
        writerItemContent(context, w, pager, dataIterator, sizes, idx);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }

    protected void writerItemContent(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
        int val = getItemValue(sizes[idx]);
        System.out.println("val: " + val);
        if(val>=0) {
            int rows = dataIterator.getRows();
            boolean selected = val==rows || (val==UIPagerSizes.ALL_MAX && rows>=UIPagerSizes.ALL_MAX);
            if(!selected) {
                w.startElement("a", null);
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                String clientId = pager.getClientId(context);
                String sourceId = clientId+"_"+val;
                w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("aria-pressed", "false", null); // $NON-NLS-1$ $NON-NLS-2$
                setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            }
            //>tmg:a11y
            else{
                w.startElement("a", null);
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                String clientId = pager.getClientId(context);
                String sourceId = clientId+"_"+val;
                w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                w.writeAttribute("style", "pointer-events:none;cursor:default;color:inherit;text-decoration:none;",null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("aria-pressed", "true", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("aria-disabled", "true", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            
            w.writeText(getItemString(val),null);
            w.endElement("a");
            //<tmg:a11y
        }
    }

    protected int getItemValue(String s) throws IOException {
        if(StringUtil.equalsIgnoreCase(s, "all")) { // $NON-NLS-1$
            return UIPagerSizes.ALL_MAX;
        }
        try {
            return Integer.parseInt(s);
        } catch(Exception ex) {}
        return -1;
    }
    
    protected String getItemString(int value) throws IOException {
        if(value>=UIPagerSizes.ALL_MAX) {
            return (String)getProperty(PROP_ALLTEXT);
        }
        return Integer.toString(value);
    }
}