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

package com.ibm.xsp.extlib.renderkit.dojo.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.util.JSUtil;

public class DojoTabPaneContentRenderer extends FacesRendererEx {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
        ResponseWriter w = context.getResponseWriter();
        
        String clientId = component.getClientId(context);
        
        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$

        // Force the width/height to the parent
        w.writeAttribute("style", "width:100%;height:100%", null); // $NON-NLS-1$ $NON-NLS-2$
        
/*        
        UIDojoTabPane pane = (UIDojoTabPane)component.getParent();
        String style = pane.getContentStyle();
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = pane.getContentStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
*/        
    }

//  @Override
//    public boolean getRendersChildren() {
//      return true;
//    }
//  
//  @Override
//    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
//      super.encodeChildren(context, component);
//    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(w);
    }
}