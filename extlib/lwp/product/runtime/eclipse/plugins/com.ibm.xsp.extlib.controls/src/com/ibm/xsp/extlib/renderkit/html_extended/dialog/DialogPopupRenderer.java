/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.renderkit.html_extended.dialog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIEventHandler;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.dialog.UIDialog;
import com.ibm.xsp.extlib.renderkit.html_extended.dynamiccontent.DynamicControlRenderer;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

public class DialogPopupRenderer extends DynamicControlRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        super.encodeEnd(context, component);

        // Render the pending action, if any...
        UIDialog.PopupContent popup = (UIDialog.PopupContent) component;
        UIDialog dlg = (UIDialog) popup.getDialog();
        //renderPendingAction(context, dlg);
        
        if(!UIDialog.DIALOG_NEXT) {
            // If the dialog is just being created, then register the events
            Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
            if(StringUtil.equals(params.get("$$created"),"true")) { // $NON-NLS-1$ $NON-NLS-2$
                List<UIComponent> children = TypedUtil.getChildren(dlg);
                if(children.size()>1) {
                    for(UIComponent c: children) {
                        if(c instanceof UIEventHandler) {
                            FacesUtil.renderComponent(context, c);
                        }
                    }
                }
            }
            // If the dialog id being created, then update its title dynamically
            if(dlg.isDialogCreateRequest((FacesContextEx)context)) {
                UIScriptCollector c = UIScriptCollector.find(context);
                StringBuilder b = new StringBuilder();
                String title = dlg.getTitle();
                if(title==null) {
                    title = "";
                }
                // dijit.byId("view:_id1:dialog").attr("title", "");
                b.append("dijit.byId("); // $NON-NLS-1$
                JavaScriptUtil.addString(b, dlg.getClientId(context));
                b.append(").attr("); // $NON-NLS-1$
                JavaScriptUtil.addString(b,"title"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b,title);
                b.append(");"); // $NON-NLS-1$
                c.addScript(b.toString());
            }
        }
    }
}