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
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.extlib.component.calendar.UINotesCalendarStore;
import com.ibm.xsp.extlib.renderkit.html_basic.domino.NotesDatabaseStoreRenderer;
import com.ibm.xsp.extlib.resources.domino.DojoResourceConstants;
import com.ibm.xsp.extlib.resources.domino.DojoResources;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * @author akosugi
 * 
 *        renderer for notes calendar store
 */
public class NotesCalendarStoreRenderer extends NotesDatabaseStoreRenderer {

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
        UINotesCalendarStore notesCalendarStore = (UINotesCalendarStore) component;
        boolean rendered = component.isRendered();
        if (!rendered)
            return;
        
        String dojoType;
        DojoModuleResource dojoModule;
        dojoType = notesCalendarStore.getDojoType();
        if( null == dojoType ){ // default dojoType
            dojoType = DojoResourceConstants.NotesCalendarStore;
            dojoModule = DojoResources.notesCalendarStore;
        }else{
            dojoModule = new DojoModuleResource(dojoType);
        }

        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.addEncodeResource(dojoModule);
        rootEx.setDojoParseOnLoad(true);

        String url = this.getDbUrlInName(context, notesCalendarStore) + "/iNotes/Proxy/"; // $NON-NLS-1$
        w.startElement("span", null); // $NON-NLS-1$
        
        w.writeAttribute("dojoType", dojoType, "dojoType"); // $NON-NLS-1$ $NON-NLS-2$
        List<DojoAttribute> attributes = notesCalendarStore.getDojoAttributes();
        if(attributes!=null) {
            for(DojoAttribute a: attributes) {
                String name = a.getName();
                String value = a.getValue();
                if(StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(value)) {
                    w.writeAttribute(name, value, null);
                }
            }
        }
        
        String clientId = notesCalendarStore.getClientId(context);
        // Always write the id when there is a dojoType; this control always has a dojoType:
        //if (StringUtil.isNotEmpty(id) && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
            w.writeAttribute("id", clientId, null); // $NON-NLS-1$
        String jsId = notesCalendarStore.getDojoWidgetJsId(context);
        if (StringUtil.isNotEmpty(jsId))
            w.writeAttribute("jsId", jsId, null); // $NON-NLS-1$
        w.writeAttribute("url", url, null); // $NON-NLS-1$
        String storeTitle = notesCalendarStore.getStoreTitle();
        if (StringUtil.isNotEmpty(storeTitle))
            w.writeAttribute("storeTitle", storeTitle, null); // $NON-NLS-1$
        w.endElement("span"); // $NON-NLS-1$
    }

    @Override
    public void encodeChildren(FacesContext facescontext,
            UIComponent uicomponent) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent uicomponent)
            throws IOException {
//      ResponseWriter w = context.getResponseWriter();
    }
}