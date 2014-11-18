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

package com.ibm.xsp.extlib.renderkit.contenttype;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.renderkit.ContentTypeRenderer;

/**
 * ContentType renderer that generates links to search google.
 * @author Philippe Riand
 */
public class GoogleSearch implements ContentTypeRenderer {

    public static final String CONTENT_TYPE = "xs:GoogleSearch"; // $NON-NLS-1$
    public static final String CONTENT_TYPE_INC_LABEL = CONTENT_TYPE + "|Google Search"; // $NON-NLS-1$
    public static final String[] CONTENT_TYPES = new String[]{CONTENT_TYPE_INC_LABEL};

    public String[] getContentTypes() {
        return CONTENT_TYPES;
    }

    public boolean render(FacesContext context, UIComponent component, ResponseWriter writer, String contentType, String value) throws IOException {
        if(contentType.equals(CONTENT_TYPE)) {
            renderLink(writer, component, value);
            return true;
        }
        return false;
    }
    // http://www.google.com/search?q=aa
    private void renderLink(ResponseWriter writer, UIComponent component, String value) throws IOException {
        writer.startElement("a", component);
        String lk = "http://www.google.com/search?q="+value; // $NON-NLS-1$
        writer.writeURIAttribute("href", lk, null); // $NON-NLS-1$
        writer.writeAttribute("target", "_blank", null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("title", "Google search link", null); // $NON-NLS-1$ $NLS-GoogleSearch.Googlesearchlink-2$
        if(null != value && StringUtil.isNotEmpty(value)) {
            writer.writeText(value, null);
        }else{
            writer.writeAttribute("style", "display:none", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        writer.endElement("a");
    }
}