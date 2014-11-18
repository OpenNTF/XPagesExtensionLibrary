/*
 * © Copyright IBM Corp. 2010, 2012
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
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.actions.client.data.DataIteratorAddRows;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.data.UIPagerAddRows;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;


public class PagerAddRowsRenderer extends AbstractPagerRenderer {

    protected static final int PROP_LISTTAG             = 20;
    protected static final int PROP_LISTCLASS           = 21;
    protected static final int PROP_LISTSTYLE           = 22;
    
    protected static final int PROP_ITEMTAG             = 25;
    protected static final int PROP_ITEMTAGCLASS        = 26;
    protected static final int PROP_ITEMTAGSTYLE        = 27;
    
    protected static final int PROP_LINKTEXT            = 30;
    protected static final int PROP_LINKCLASS           = 31;
    protected static final int PROP_LINKSTYLE           = 32;
    protected static final int PROP_DISABLEDFORMATDEFAULT = 33;

    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LINKTEXT:             return "Show more..."; // $NLS-PagerAddRowsRenderer.Showmore-1$
            case PROP_LISTTAG:              return "ul"; // $NON-NLS-1$
            case PROP_ITEMTAG:              return "li"; // $NON-NLS-1$
            case PROP_DISABLEDFORMATDEFAULT: return DataIteratorAddRows.DISABLED_FORMAT_TEXT;
        }
        return super.getProperty(prop);
    }
    
    @Override
    protected boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix) {
        UIPagerAddRows pager = (UIPagerAddRows)component;
        if(pager.isRefreshPage()) {
            try {
                if(idSuffix.equals("ar")) { // $NON-NLS-1$
                    pagerEvent.setAction(UIPagerAddRows.ACTION_ADDROWS);
                    return true;
                }
            } catch(Exception ex) {}
        }
        return false;
    }

    @Override
    protected void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager _pager, FacesDataIterator dataIterator) throws IOException {
        UIPagerAddRows pager = (UIPagerAddRows)_pager;
        // Hide it if disabled and this is the option
        boolean disabled = isDisabled(context, w, pager, dataIterator);
        
        String disabledFormat = pager.getDisabledFormat();
        // disabledFormat=hide|link|text|auto
        String rendererDefaultDisabledFormat = (String)getProperty(PROP_DISABLEDFORMATDEFAULT);
        disabledFormat = DataIteratorAddRows.computeDisabledFormat(context, disabledFormat, rendererDefaultDisabledFormat);
        if( DataIteratorAddRows.DISABLED_FORMAT_HIDE.equals(disabledFormat) && disabled ){ //$NON-NLS-1$
            return;
        }
        
        // Else display it
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
        writeShowMore(context, w, pager, dataIterator, disabled, disabledFormat);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }
    
    protected boolean isDisabled(FacesContext context, ResponseWriter w, UIPagerAddRows pager, FacesDataIterator dataIterator) {
        DataModel dm = dataIterator.getDataModel();
        int first = dataIterator.getFirst();
        int rows = dataIterator.getRows();
        int rc = dm.getRowCount(); 
        if(rc<=first+rows) {
            if(dm instanceof TabularDataModel) {
                TabularDataModel tm = (TabularDataModel)dm;
                if(tm.canHaveMoreRows()) {
                    int mr = first+rows+1;
                    if(tm.hasMoreRows(mr)>=mr) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected void writeShowMore(FacesContext context, ResponseWriter w, UIPagerAddRows pager, FacesDataIterator dataIterator, boolean disabled, String disabledFormat) throws IOException {
        String tag = (String)getProperty(PROP_ITEMTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
            String style = (String)getProperty(PROP_ITEMTAGSTYLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style,null); // $NON-NLS-1$
            }
            String styleClass = (String)getProperty(PROP_ITEMTAGCLASS);
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
            }
        }
        boolean useTextFormat = disabled && DataIteratorAddRows.DISABLED_FORMAT_TEXT.equals(disabledFormat); //$NON-NLS-1$ 
        if( useTextFormat ){
            w.startElement("span", null); //$NON-NLS-1$
        }else{
            w.startElement("a", null); //$NON-NLS-1$
            w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        String clientId = pager.getClientId(context);
        String sourceId = clientId+"_ar"; // $NON-NLS-1$
        w.writeAttribute("id", sourceId,null); // $NON-NLS-1$
        
        if( !useTextFormat ){
            w.writeAttribute("href", "javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        // note, when text click will still submit, but the page content 
        // will be the same after submit.
        if(pager.isRefreshPage()) {
            // Server side round trip
            setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
        } else {
            // PHIL: in case of hide, the id should be the entire pager, and the link
            String linkId = sourceId;
            if(DataIteratorAddRows.DISABLED_FORMAT_HIDE.equals(disabledFormat)) {
                linkId = clientId;
            }
            // Pure client side refresh
            int count = pager.getRowCount();
            boolean saveStateServerSide = pager.isState();
            String onClick = DataIteratorAddRows.generateJavaScript(context, 
                    dataIterator, count, saveStateServerSide, linkId, disabledFormat);
            w.writeAttribute("onclick", onClick,null); // $NON-NLS-1$
        }
        
        // bad HTML attribute "disabled", setting styleClass instead.
//        if(disabled) {
//            w.writeAttribute("disabled", "disabled",null); // $NON-NLS-1$ $NON-NLS-2$
//        }
        String style = (String)getProperty(PROP_LINKSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style,null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_LINKCLASS);
        if( disabled ){
            styleClass = ExtLibUtil.concatStyleClasses("disabled", styleClass); //$NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass,null); // $NON-NLS-1$
        }
        
        writeLinkContent(context, w, pager, dataIterator, disabled);
        
        if(useTextFormat ){
            w.endElement("span"); //$NON-NLS-1$
        }else{
            w.endElement("a"); //$NON-NLS-1$
        }

        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }
    protected void writeLinkContent(FacesContext context, ResponseWriter w, UIPagerAddRows pager, FacesDataIterator dataIterator, boolean disabled) throws IOException {
        String text = pager.getText();
        if(StringUtil.isEmpty(text)) {
            text = (String)getProperty(PROP_LINKTEXT);
        }
        if(StringUtil.isNotEmpty(text)) {
            w.writeText(text,null);
        }
    }
}