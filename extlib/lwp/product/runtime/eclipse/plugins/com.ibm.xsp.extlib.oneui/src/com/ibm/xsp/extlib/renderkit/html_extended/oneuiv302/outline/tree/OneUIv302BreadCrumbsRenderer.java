/*
 * © Copyright IBM Corp. 2012
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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.outline.UIOutlineBreadCrumbs;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.BreadCrumbsRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class OneUIv302BreadCrumbsRenderer extends BreadCrumbsRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OneUIv302BreadCrumbsRenderer(){
		
	}
	
	public OneUIv302BreadCrumbsRenderer(UIComponent component){
		// super(component);
	}
	
	@Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.startElement("nav", null); // $NON-NLS-1$
        writer.writeAttribute("role", "navigation", null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("aria-label", "Breadcrumbs", null);//$NON-NLS-1$ // $NLS-OneUIv302BreadCrumbsRenderer_Breadcrumbs_IndicationOfCurrentNavigationLocation-2$
        
        String style = getContainerStyle(tree);
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_BREADCRUMBS_CONTAINER),getContainerStyleClass(tree));
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        
        UIComponent c = tree.getComponent();
        if(c instanceof UIOutlineBreadCrumbs) {
            String text = ((UIOutlineBreadCrumbs)c).getLabel(); 
            if(StringUtil.isNotEmpty(text)) {
                writer.writeText(text, null);
            }
        }
    }
	
	 @Override
	    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
	        writer.endElement("nav"); // $NON-NLS-1$
	    }
	 
	 @Override
	protected void renderSeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
	        writer.writeText(" > ", null); // $NON-NLS-1$
	    }

	 @Override
		protected void renderEntryItemLinkAttributes(FacesContext context,ResponseWriter writer, TreeContextImpl tree, boolean enabled,boolean selected) throws IOException {
	
		
	 }
}
