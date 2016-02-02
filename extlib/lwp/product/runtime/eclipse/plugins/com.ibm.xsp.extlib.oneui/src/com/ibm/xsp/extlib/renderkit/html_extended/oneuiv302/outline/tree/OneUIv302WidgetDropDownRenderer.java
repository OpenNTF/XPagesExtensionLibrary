/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.tree.OneUIWidgetDropDownRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * @author kevin
 *
 */
public class OneUIv302WidgetDropDownRenderer extends OneUIWidgetDropDownRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("a",null);
        // A popup button requires an id
        String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(clientId)) {
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        }
        writer.writeAttribute("class","lotusIcon lotusActionIcon",null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
        // "Click for actions"
        String buttonTitle = com.ibm.xsp.extlib.controls.ResourceHandler.getString("DropDownButtonRenderer.Clickforactions"); // $NON-NLS-1$
        writer.writeAttribute("title", buttonTitle, null); // $NON-NLS-1$
        writer.writeAttribute("aria-haspopup","true",null); // $NON-NLS-1$ $NON-NLS-2$
        writer.startElement("img",null); // $NON-NLS-1$
        writer.writeAttribute("src", HtmlRendererUtil.getImageURL(context,OneUIResources.get().BLANK_GIF), null); // $NON-NLS-1$
        writer.writeAttribute("alt","",null); // $NON-NLS-1$
        writer.writeAttribute("aria-owns",clientId,null); // $NON-NLS-1$ $NON-NLS-2$
        writer.endElement("img"); // $NON-NLS-1$
        writer.startElement("span",null); // $NON-NLS-1$
        writer.writeAttribute("class","lotusAltText",null); // $NON-NLS-1$ $NON-NLS-2$
        // "Actions"
        String buttonText = com.ibm.xsp.extlib.controls.ResourceHandler.getString("DropDownButtonRenderer.Actions"); // $NON-NLS-1$
        writer.writeText(buttonText, null);
        writer.endElement("span"); // $NON-NLS-1$
        writer.endElement("a");
        JSUtil.writeln(writer);
    }
}
