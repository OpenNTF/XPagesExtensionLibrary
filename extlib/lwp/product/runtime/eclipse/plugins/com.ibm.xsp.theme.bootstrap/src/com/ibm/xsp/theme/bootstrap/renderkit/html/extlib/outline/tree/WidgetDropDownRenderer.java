/*
 * � Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;

public class WidgetDropDownRenderer extends TreePopupMenuRenderer {

    private static final long serialVersionUID = 1L;

    public WidgetDropDownRenderer() {
    }

    // No tags for the popup button....
    @Override
    protected String getContainerTag() {
        return null;
    }

    @Override
    protected String getItemTag() {
        return null;
    }

    @Override
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("a", null);
        // A popup button requires an id
        String clientId = tree.getClientId(context, "ab", 1); // $NON-NLS-1$
        if (StringUtil.isNotEmpty(clientId)) {
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        }
        writer.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
                                                                // $NON-NLS-2$
        writer.writeAttribute("title", "click for actions", null); // $NON-NLS-1$ $NLS-WidgetDropDownRenderer.clickforactions-2$
                                                                    // $NLS-OneUIWidgetDropDownRenderer.clickforactions-2$
        writer.writeAttribute("aria-haspopup", "true", null); // $NON-NLS-1$ $NON-NLS-2$
                                                                // $NON-NLS-2$
        writer.startElement("img", null); // $NON-NLS-1$
        writer.writeAttribute("src", HtmlRendererUtil.getImageURL(context, "/.ibmxspres/.extlib/responsive/xpages/img/widget_dropdown.png"), null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("alt", "", null); // $NON-NLS-1$
        writer.writeAttribute("aria-label", "action button", null); // $NON-NLS-1$ $NLS-WidgetDropDownRenderer.actionbutton-2$
                                                                    // $NON-NLS-2$
        writer.endElement("img"); // $NON-NLS-1$
        writer.startElement("span", null); // $NON-NLS-1$
        writer.writeAttribute("class", "lotusAltText", null); // $NON-NLS-1$ $NON-NLS-2$
                                                                // $NON-NLS-2$
        writer.writeText("Actions", null); // $NLS-OneUIWidgetDropDownRenderer.Actions-1$
        writer.endElement("span"); // $NON-NLS-1$
        writer.endElement("a");
        JSUtil.writeln(writer);
    }
}