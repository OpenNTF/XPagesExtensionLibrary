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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.data;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.data.UIPagerDetail;
import com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractPagerRenderer;

public class PagerDetailRenderer extends AbstractPagerRenderer {

    @Override
    protected boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix) {
        try {
            if (idSuffix.equals("sd")) { // $NON-NLS-1$
                pagerEvent.setAction(UIPagerDetail.ACTION_SHOWDETAIL);
                return true;
            } else if (idSuffix.equals("hd")) { // $NON-NLS-1$
                pagerEvent.setAction(UIPagerDetail.ACTION_HIDEDETAIL);
                return true;
            }
        } catch (Exception ex) {
            //TODO add error logging
        }
        return false;
    }

    @Override
    protected void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager _pager, FacesDataIterator dataIterator) throws IOException {

        UIPagerDetail pager = (UIPagerDetail) _pager;
        w.startElement("div", null); // $NON-NLS-1$

        String pgClass = pager.getStyleClass();
        if (StringUtil.isNotEmpty(pgClass)) {
            w.writeAttribute("class", pgClass, null); // $NON-NLS-1$
        }

        w.startElement("ul", null); // $NON-NLS-1$
        w.writeAttribute("class", "pagination", null); // $NON-NLS-1$ $NON-NLS-2$

        writeShowAll(context, w, pager, dataIterator);
        writeSeparator(context, w, pager, dataIterator);
        writeHideAll(context, w, pager, dataIterator);
        w.endElement("ul"); // $NON-NLS-1$

        w.endElement("div"); // $NON-NLS-1$
    }

    protected void writeShowAll(FacesContext context, ResponseWriter w, UIPagerDetail pager, FacesDataIterator dataIterator) throws IOException {
        String text = pager.getShowText();
        if (StringUtil.isEmpty(text)) {
            text = "Show details"; // $NLS-PagerDetailRenderer.ShowDetails-1$
        }
        if (StringUtil.isNotEmpty(text)) {
            w.startElement("li", null); // $NON-NLS-1$
            boolean selected = pager.isShowAll();
            if (selected) {
                w.writeAttribute("class", "disabled", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.startElement("a", null);
            String clientId = pager.getClientId(context);
            String sourceId = clientId + "_sd"; // $NON-NLS-1$
            w.writeAttribute("id", sourceId, null); // $NON-NLS-1$
            w.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
                                                            // $NON-NLS-2$
            setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            w.writeText(text, null);
            w.endElement("a");
            w.endElement("li"); // $NON-NLS-1$
        }
    }

    protected void writeSeparator(FacesContext context, ResponseWriter w, UIPagerDetail pager, FacesDataIterator dataIterator) throws IOException {
        // not write any separator text, instead the separator is achieved using
        // CSS styles
        // so there are no character encoding issues in other countries
    }

    protected void writeHideAll(FacesContext context, ResponseWriter w, UIPagerDetail pager, FacesDataIterator dataIterator) throws IOException {
        String text = pager.getHideText();
        if (StringUtil.isEmpty(text)) {
            text = "Hide details"; // $NLS-PagerDetailRenderer.HideDetails-1$
        }
        if (StringUtil.isNotEmpty(text)) {
            w.startElement("li", null); // $NON-NLS-1$
            boolean selected = pager.isHideAll();
            if (selected) {
                w.writeAttribute("class", "disabled", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.startElement("a", null);
            String clientId = pager.getClientId(context);
            String sourceId = clientId + "_hd"; // $NON-NLS-1$
            w.writeAttribute("id", sourceId, null); // $NON-NLS-1$
            w.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
                                                            // $NON-NLS-2$
            setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            w.writeText(text, null);
            w.endElement("a");
            w.endElement("li"); // $NON-NLS-1$
        }
    }
}