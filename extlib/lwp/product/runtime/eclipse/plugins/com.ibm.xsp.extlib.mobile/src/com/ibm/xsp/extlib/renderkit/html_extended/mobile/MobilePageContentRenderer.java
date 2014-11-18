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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.extlib.component.mobile.UIMobilePageContent;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;

public class MobilePageContentRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter w = context.getResponseWriter();

        String clientId = component.getClientId(context);

        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$
        JSUtil.writeln(w);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(w);

        UIMobilePageContent popup = (UIMobilePageContent) component;
        UIMobilePage dlg = (UIMobilePage) popup.getMobilePage();
        renderPendingAction(context, dlg);
    }

        /**
         * renderPendingAction is called at the end of encoding a page and allows for the use of
         * the MoveTo simple action. This method uses the MoveTo action's provided data to set up
         * the necessary JavaScript to carry out the action.
         * @param context
         * @param dlg
         */
    public static void renderPendingAction(FacesContext context, UIMobilePage dlg) {
        UIMobilePage.Action action = dlg.getPendingAction(context);
        if (action != null && UIMobilePage.ACTION_MOVETO_PAGE == action.getAction() ) {
            StringBuilder b = new StringBuilder();
            
            // TODO should not add inline script with dojo.addOnLoad, 
            // should add script collector addOnLoad script.
            b.append("dojo.addOnLoad(function(){"); // $NON-NLS-1$
            // TODO should not have to call XSP.allowSubmit
            b.append("XSP.allowSubmit();"); // $NON-NLS-1$
            
            // XSP.moveToMPage( view, moveToTargetId, dirInt, transition, params )
            b.append("XSP.moveToMPage("); // $NON-NLS-1$
            // view: dijit.byId("appPage1")
            b.append("dijit.byId("); // $NON-NLS-1$
            String pageId = dlg.getClientId(context);
            JavaScriptUtil.addString(b, pageId);
            b.append("),");
            // moveToTargetId: "#appPage2"
            JavaScriptUtil.addString(b, "#"+action.getTargetId());
            b.append(",");
            // direction: -1
            JavaScriptUtil.addInt(b, action.getDirection()); // -1 or 1
            b.append(",");
            // transition: "slide"
            JavaScriptUtil.addString(b,action.getTransitionType());
            b.append(",");
            
            // params: 'resetContent=false'
            // the params value will be passed to dojo.queryToObject
            StringBuilder paramsAsString = new StringBuilder();
            Map<String, Object> params = action.getHashParams();
            for (String k : params.keySet()) {
                String s = params.get(k).toString();
                if (StringUtil.isNotEmpty(s)){
                    paramsAsString.append("&").append(k).append("=").append(s); // $NON-NLS-1$ $NON-NLS-2$
                }
            }
            JavaScriptUtil.addString(b, paramsAsString.toString());
            b.append(");"); // $NON-NLS-1$
            
            
            // TODO should not add inline script with dojo.addOnLoad, 
            // should add script collector addOnLoad script.
            b.append("});"); // $NON-NLS-1$ // end dojo.addOnLoad(function(){
            String script = b.toString();
//          JavaScriptUtil.addScriptOnLoad(script);
            ExtLibUtil.addScript(context, script);
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Note, the children won't exist if we are in the initial GET request.
        // Maybe we should change the behavior to having the initially displayed children
        // present in the GET request, and either render them in the initial GET request
        // or continue the requirement for an AJAX request by changing this code to
        // render the children if we are in an Ajax request (so not in the initial GET request).
        
        // note super.encodeChildren won't render the content of a child panel control
        // so need to use FacesUtil.renderChildren instead. 
        FacesUtil.renderChildren(context, component);
    }

}