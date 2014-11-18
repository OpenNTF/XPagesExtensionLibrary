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

package com.ibm.xsp.extlib.renderkit.html_extended.outline;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.tree.ITreeRenderer;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;


public class DojoOutlineMenuRenderer extends AbstractOutlineRenderer {

    @Override
    protected ITreeRenderer findTreeRenderer(FacesContext context, AbstractOutline outline) {
        DojoMenuRenderer r = new DojoMenuRenderer(outline);
        r.setMenuId(JavaScriptUtil.encodeFunctionName(context,outline,"menu")); // $NON-NLS-1$
        return r;
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        super.encodeEnd(context, component);

        ResponseWriter writer = context.getResponseWriter();
        AbstractOutline outline = (AbstractOutline)component;
    
        // Generate an empty div for the menu to make the event working (partial refresh..)
        JSUtil.writeln(writer);
        writer.startElement("div", outline); // $NON-NLS-1$
        writer.writeAttribute("id", outline.getClientId(context),null); // $NON-NLS-1$
        writer.endElement("div"); // $NON-NLS-1$
    }
    
}