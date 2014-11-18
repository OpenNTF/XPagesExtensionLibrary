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

package com.ibm.xsp.extlib.renderkit.html_basic.calendar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.calendar.UIiCalReadStore;
import com.ibm.xsp.extlib.resources.domino.DojoResourceConstants;
import com.ibm.xsp.extlib.resources.domino.DojoResources;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;

/**
 * @author akosugi
 * 
 *        renderer for iCalendar read store
 */
public class iCalReadStoreRenderer extends FacesRenderer {

    @Override
    public void decode(FacesContext facescontext, UIComponent uicomponent) {
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIiCalReadStore store = (UIiCalReadStore) component;
        boolean rendered = component.isRendered();
        if (!rendered)
            return;

        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.addEncodeResource(DojoResources.iCalReadStore);
        rootEx.setDojoParseOnLoad(true);

        String url = store.getUrl();
        if (StringUtil.isEmpty(url))
            throw new FacesExceptionEx(
                    "No URL specified for iCalReadStoreRenderer"); // $NLX-iCalReadStoreRenderer.nourlspecifiedforiCalReadStoreRen-1$
        url = HtmlRendererUtil.getImageURL(context, url);

        w.startElement("span", null); // $NON-NLS-1$
        w.writeAttribute(DojoResourceConstants.dojoType,
                DojoResourceConstants.iCalReadStore, null);
        String id = store.getClientId(context);
        if (StringUtil.isNotEmpty(id))
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        String jsId = store.getDojoWidgetJsId(context);
        if (!StringUtil.isEmpty(jsId))
            w.writeAttribute("jsId", jsId, null); // $NON-NLS-1$
        w.writeAttribute("url", url, null); // $NON-NLS-1$
        String storeTitle = store.getStoreTitle();
        if (StringUtil.isNotEmpty(storeTitle))
            w.writeAttribute("storeTitle", storeTitle, null); // $NON-NLS-1$
        String fontColor = store.getFontColorMeeting();
        if (!StringUtil.isEmpty(fontColor)) {
            w.writeAttribute("fontColorMeeting", fontColor, null); // $NON-NLS-1$
        }
        String bgColor = store.getBgColorMeeting();
        if (!StringUtil.isEmpty(bgColor)) {
            w.writeAttribute("bgColorMeeting", bgColor, null); // $NON-NLS-1$
        }
        String borderColor = store.getBorderColorMeeting();
        if (!StringUtil.isEmpty(borderColor)) {
            w.writeAttribute("borderColorMeeting", borderColor, null); // $NON-NLS-1$
        }
    }

    @Override
    public void encodeChildren(FacesContext facescontext,
            UIComponent uicomponent) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent uicomponent)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("span"); // $NON-NLS-1$
    }
}