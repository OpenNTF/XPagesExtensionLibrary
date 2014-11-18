/*
 * © Copyright IBM Corp. 2014
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class BreadCrumbsRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.BreadCrumbsRenderer {
    
    private static final long serialVersionUID = 1L;
	
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BREADCRUMBS_CONTAINER:   	return "breadcrumb"; // $NON-NLS-1$
            case PROP_BREADCRUMBS_LABEL:   		return ""; // $NON-NLS-1$
            case PROP_BREADCRUMBS_SEPARATOR:  	return "divider"; // $NON-NLS-1$
        }
        return null;
    }

    public BreadCrumbsRenderer() {
    }

    public BreadCrumbsRenderer(UIComponent component) {
        super(component);
    }
    
    @Override
	protected void renderSeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.startElement("span", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_BREADCRUMBS_SEPARATOR), null); // $NON-NLS-1$
        writer.writeText(" / ", null);
        writer.endElement("span"); // $NON-NLS-1$
    }

}