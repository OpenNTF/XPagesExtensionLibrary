/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.data;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.data.UIPagerSizes;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.data.OneUIPagerSizesRenderer;

public class OneUIv302PagerSizesRenderer extends OneUIPagerSizesRenderer {
    
    protected static final int PROP_LISTROLE      = 23;
    protected static final int PROP_ITEMROLE       = 24;
    
     @Override
        protected Object getProperty(int prop) {
            switch(prop) {
                case PROP_LISTCLASS:    return "lotusLeft lotusInlinelist"; // $NON-NLS-1$
                case PROP_LISTROLE:     return "toolbar"; // $NON-NLS-1$
                case PROP_ITEMROLE:     return "button"; // $NON-NLS-1$
                case PROP_PAGERTAG:     return "div"; // $NON-NLS-1$
                case PROP_PAGERROLE:    return "navigation"; // $NON-NLS-1$
                // these controls don't have a dojoType so id not forced
                case PROP_FORCEID:      return false;
            }
            return super.getProperty(prop);
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
                String role = (String)getProperty(PROP_LISTROLE);
                if(StringUtil.isNotEmpty(role)){
                    w.writeAttribute("role", role, null); // $NON-NLS-1$
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
     
        @Override
        protected void writerItemContent(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
            int val = getItemValue(sizes[idx]);
            if(val>=0) {
                int rows = dataIterator.getRows();
                boolean selected = val==rows || (val==UIPagerSizes.ALL_MAX && rows>=UIPagerSizes.ALL_MAX);
                if(!selected) {
                    w.startElement("a", null);
                    String clientId = pager.getClientId(context);
                    String sourceId = clientId+"_"+val;
                    w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                    String role = (String)getProperty(PROP_ITEMROLE);
                    if(StringUtil.isNotEmpty(role)){
                        w.writeAttribute("role", role, null); // $NON-NLS-1$
                    }
                    w.writeAttribute("aria-pressed", "false", null); // $NON-NLS-1$ $NON-NLS-2$
                    w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                    setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
                }
                //>tmg:a11y
                else{
                    w.startElement("a", null);
                    String clientId = pager.getClientId(context);
                    String sourceId = clientId+"_"+val;
                    w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
                    String role = (String)getProperty(PROP_ITEMROLE);
                    if(StringUtil.isNotEmpty(role)){
                        w.writeAttribute("role", role, null); // $NON-NLS-1$
                    }
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
        
         @Override
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
                     w.writeAttribute("role", "presentation", null); // $NON-NLS-1$ $NON-NLS-2$
                 }
                 w.writeText(text,null);
                 if(StringUtil.isNotEmpty(tag)) {
                     w.endElement(tag);
                 }
             }
         }
     
        @Override
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
                    
                    w.writeAttribute("role", "presentation", null); // $NON-NLS-1$ $NON-NLS-2$
                }
                w.writeText(text,null);
                if(StringUtil.isNotEmpty(tag)) {
                    w.endElement(tag);
                }
            }
        }
    
}