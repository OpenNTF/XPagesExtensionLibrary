/*
 * © Copyright IBM Corp. 2014, 2015
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
import com.ibm.xsp.extlib.component.data.UIPagerSizes;
import com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractPagerRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PagerSizesRenderer extends AbstractPagerRenderer {

    @Override
    protected boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix) {
        try {
            int nrows = Integer.parseInt(idSuffix);
            pagerEvent.setAction(UIPagerSizes.ACTION_SETROWS);
            pagerEvent.setPage(Math.min(nrows, UIPagerSizes.ALL_MAX));
            component.queueEvent(pagerEvent);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager _pager, FacesDataIterator dataIterator) throws IOException {
        UIPagerSizes pager = (UIPagerSizes) _pager;
        w.startElement("div", null); // $NON-NLS-1$

        w.startElement("ul", null); // $NON-NLS-1$
        String styleClass = pager.getStyleClass();
        String pgClass = ExtLibUtil.concatStyleClasses("pagination", styleClass); // $NON-NLS-1$
        if (StringUtil.isNotEmpty(pgClass)) {
            w.writeAttribute("class", pgClass, null); // $NON-NLS-1$
        }

        String text = pager.getText();
        if (StringUtil.isEmpty(text)) {
            text = "Show {0} items per page"; // $NLS-PagerSizesRenderer.Show0itemsperpage-1$
        }
        int pos = text.indexOf("{0}"); //$NON-NLS-1$
        writerStartText(context, w, pager, dataIterator, text, pos);
        writerPages(context, w, pager, dataIterator, text, pos);
        writerEndText(context, w, pager, dataIterator, text, pos);
        w.endElement("ul"); // $NON-NLS-1$

        w.endElement("div"); // $NON-NLS-1$
    }

    protected void writerStartText(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        if (-1 != pos) {
            text = text.substring(0, pos);
        }// else, output entire text at start when {0} absent
        if (StringUtil.isNotEmpty(text)) {
            w.startElement("li", null); // $NON-NLS-1$
            w.writeAttribute("class", "disabled", null); // $NON-NLS-1$ $NON-NLS-2$
            w.startElement("a", null);
            w.writeText(text, null);
            w.endElement("a");
            w.endElement("li"); // $NON-NLS-1$
        }
    }

    protected void writerEndText(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        if (-1 != pos) {
            text = text.substring(pos + 3);
        } else {
            // do not output end text when {0} absent
            return;
        }
        if (StringUtil.isNotEmpty(text)) {
            w.startElement("li", null); // $NON-NLS-1$
            w.writeAttribute("class", "disabled", null); // $NON-NLS-1$ $NON-NLS-2$
            w.startElement("a", null);
            w.writeText(text, null);
            w.endElement("a");
            w.endElement("li"); // $NON-NLS-1$
        }
    }

    protected void writeSeparator(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator) throws IOException {
    }

    protected void writerPages(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String text, int pos) throws IOException {
        String sizestr = pager.getSizes();
        if (StringUtil.isEmpty(sizestr)) {
            // note, not a translatable string.
            sizestr = "10|25|50|all";//$NON-NLS-1$
        }
        if (StringUtil.isNotEmpty(sizestr)) {
            String[] sizes = StringUtil.splitString(sizestr, '|', true);
            for (int i = 0; i < sizes.length; i++) {
                writePage(context, w, pager, dataIterator, sizes, i);
            }
        }
    }

    protected void writePage(FacesContext context, ResponseWriter w, UIPagerSizes pager, FacesDataIterator dataIterator, String[] sizes, int idx) throws IOException {
        w.startElement("li", null); // $NON-NLS-1$
        int val = getItemValue(sizes[idx]);
        if (val >= 0) {
            int rows = dataIterator.getRows();
            boolean selected = val == rows || (val == UIPagerSizes.ALL_MAX && rows >= UIPagerSizes.ALL_MAX);
            if (selected) {
                w.writeAttribute("class", "active", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.startElement("a", null);
            String clientId = pager.getClientId(context);
            String sourceId = clientId + "_" + val;
            w.writeAttribute("id", sourceId, null); // $NON-NLS-1$
            w.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
                                                            // $NON-NLS-2$
            setupSubmitOnClick(context, w, pager, dataIterator, clientId, sourceId);
            w.writeText(getItemString(val), null);
            w.endElement("a");
        }
        w.endElement("li"); // $NON-NLS-1$
    }

    protected int getItemValue(String s) throws IOException {
        if (StringUtil.equalsIgnoreCase(s, "all")) { // $NON-NLS-1$
            return UIPagerSizes.ALL_MAX;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            //TODO add error logging
        }
        return -1;
    }

    protected String getItemString(int value) throws IOException {
        if (value >= UIPagerSizes.ALL_MAX) {
            return "All"; // $NLS-PagerSizesRenderer.All-1$
        }
        return Integer.toString(value);
    }
}