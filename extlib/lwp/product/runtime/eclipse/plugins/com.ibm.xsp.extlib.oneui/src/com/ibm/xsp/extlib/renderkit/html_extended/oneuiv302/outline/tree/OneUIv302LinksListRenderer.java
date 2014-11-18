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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.tree.OneUILinksListRenderer;

public class OneUIv302LinksListRenderer extends OneUILinksListRenderer {

	private static final long serialVersionUID = 1L;
	

	public OneUIv302LinksListRenderer() {
	}

	public OneUIv302LinksListRenderer(UIComponent component) {
		super(component);
	}
	
	 @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LINKSLIST_INLINELIST:   return "lotusInlinelist lotusActions"; // $NON-NLS-1$
            case PROP_LINKSLIST_FIRST:  return "lotusFirst"; // $NON-NLS-1$
        }
        return null;
    }
	 
	 @Override
	    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
		 	startRenderContainer(context, writer, tree);
	    }
	 
	 @Override
	    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
	        endRenderContainer(context, writer, tree);
	    }

}
