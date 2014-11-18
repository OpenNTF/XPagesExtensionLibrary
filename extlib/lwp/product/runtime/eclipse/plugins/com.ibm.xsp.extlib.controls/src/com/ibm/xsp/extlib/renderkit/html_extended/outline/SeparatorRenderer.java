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

import com.ibm.xsp.extlib.component.outline.UISeparator;
import com.ibm.xsp.renderkit.FacesRenderer;

/**
 * Render a separator between list items.
 */
public class SeparatorRenderer extends FacesRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        writeSeparator(context, w, (UISeparator)component);
    }
    
    protected void writeSeparator(FacesContext context, ResponseWriter w, UISeparator c) throws IOException {
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("style", "margin-left: 3px; margin-right: 3px;", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("role", "separator", null); // $NON-NLS-1$ $NON-NLS-2$
        w.write("|"); // $NLS-SeparatorRenderer.|-1$
        w.endElement("span"); // $NON-NLS-1$
    }
}