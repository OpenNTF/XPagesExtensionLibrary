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

package com.ibm.xsp.extlib.renderkit.html_basic.listview;

import java.io.IOException;
import java.net.URLEncoder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.listview.UINotesListViewStore;
import com.ibm.xsp.extlib.renderkit.html_basic.domino.NotesDatabaseStoreRenderer;
import com.ibm.xsp.extlib.resources.domino.DojoResourceConstants;
import com.ibm.xsp.extlib.resources.domino.DojoResources;

/**
 * @author akosugi
 * 
 *        renderer for note list view data store
 */
public class NotesListViewStoreRenderer extends NotesDatabaseStoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UINotesListViewStore uiComponent = (UINotesListViewStore) component;
        boolean rendered = component.isRendered();
        if (!rendered)
            return;

        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.addEncodeResource(DojoResources.dominoDataStore);
        rootEx.setDojoParseOnLoad(true);

        String url = getDbUrl(context, uiComponent);

        w.startElement("span", null); // $NON-NLS-1$
        w.writeAttribute(DojoResourceConstants.dojoType,
                DojoResourceConstants.DominoDataStore, null);
        String id = uiComponent.getClientId(context);
        if (StringUtil.isNotEmpty(id))
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        String jsId = uiComponent.getDojoWidgetJsId(context);
        if (StringUtil.isNotEmpty(jsId))
            w.writeAttribute("jsId", jsId, null); // $NON-NLS-1$

        String folderName = uiComponent.getViewName();
        if(null != folderName)
            w.writeAttribute("folderName", folderName, null); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(url)){
            url=URLEncoder.encode(url,"UTF_8"); //$NON-NLS-1$
            w.writeAttribute("url", url, null); // $NON-NLS-1$
        }
        w.writeAttribute("dwa", "false", null); // $NON-NLS-1$ $NON-NLS-2$
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("span"); // $NON-NLS-1$
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}