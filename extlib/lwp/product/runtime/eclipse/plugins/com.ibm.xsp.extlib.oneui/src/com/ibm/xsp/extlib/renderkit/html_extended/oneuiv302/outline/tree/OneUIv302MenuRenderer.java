/*
 * © Copyright IBM Corp. 2012, 2013
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
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.tree.OneUIMenuRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.resources.OneUIResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.JSUtil;

public class OneUIv302MenuRenderer extends OneUIMenuRenderer {

    protected static final int PROP_MENU_INACTIVE   = 11;
	private static final long serialVersionUID = 1L;

	public OneUIv302MenuRenderer() {
		
	}

	public OneUIv302MenuRenderer(UIComponent component) {
		super(component);
		
	}
	
	 @Override
	    protected Object getProperty(int prop) {
	        switch(prop) {
	            case PROP_MENU_INACTIVE: return "lotusInactive"; // $NON-NLS-1$
	        }
	        return super.getProperty(prop);
	    }
	
	@Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        
		if(isExpandable()) {
            UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
            rootEx.setDojoTheme(true);
            ExtLibResources.addEncodeResource(rootEx, OneUIResources.oneUINavigator);
            // Specific dojo effects
            String effect = getExpandEffect();
            if(StringUtil.isNotEmpty(effect)) {
                rootEx.addEncodeResource(ExtLibResources.dojoFx);
                ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dojoFx);
            }
        }
		
		writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_MENU),null); // $NON-NLS-1$
        writer.writeAttribute("role", "navigation",null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("aria-label", "Menu navigation", null); // $NON-NLS-1$ // $NLS-OneUIv302MenuRenderer_NavAriaLabel_MenuNavigation-2$
        writeClientIdIfNecessary(context, writer, tree);
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_BOTTOMCORNER),null); // $NON-NLS-1$
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_INNER),null); // $NON-NLS-1$ $NON-NLS-2$
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_HEADER), null);//$NON-NLS-1$
        writer.writeAttribute("role", "tree", null); // $NON-NLS-1$ // $NON-NLS-2$
        
    }
	
	 @Override
	    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
	        writer.endElement("div"); // $NON-NLS-1$
	        writer.endElement("div"); // $NON-NLS-1$
	        writer.endElement("div"); // $NON-NLS-1$
	        writer.endElement("div"); // $NON-NLS-1$
	    }
	 
	 @Override
	    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
	        return null;
	    }
	 
	 @Override
    protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
		 if(tree.getDepth()>1) {
	            writer.startElement("div", null); // $NON-NLS-1$
	            writer.writeAttribute("class", (String)getProperty(PROP_MENU_SUBSECTION), null); // $NON-NLS-1$
	            writer.writeAttribute("style", "margin-top: 0",null); // $NON-NLS-1$ $NON-NLS-2$
	        }
		 	String containerTag = getContainerTag();
	        if(StringUtil.isNotEmpty(containerTag)) {
	            writer.startElement(containerTag,null);
	            String style = null;
	            String styleClass = null;
	            if(tree.getDepth()==1) {
	            	// ac: LHEY92PFY3 - A11Y | RPT | xc:viewMenu : ID values must be unique
	            	if (!tree.isOuterTagEmitted()) {
		                String id = getClientId(context,tree);
		                if(StringUtil.isNotEmpty(id)) {
		                    writer.writeAttribute("id",id,null); // $NON-NLS-1$
		                }
	            	}
	                writer.writeAttribute("role", "toolbar", null); // $NON-NLS-1$ // $NON-NLS-2$
	                UIComponent c = tree.getComponent();
	                if(c!=null) {
	                    style = (String)c.getAttributes().get("style"); // $NON-NLS-1$
	                    styleClass = (String)c.getAttributes().get("styleClass"); // $NON-NLS-1$
	                }
	            }
	            style = ExtLibUtil.concatStyles(style,getContainerStyle(tree));
	            if(StringUtil.isNotEmpty(style)) {
	                writer.writeAttribute("style",style,null); // $NON-NLS-1$
	            }
	            styleClass = ExtLibUtil.concatStyleClasses(styleClass,getContainerStyleClass(tree));
	            if(StringUtil.isNotEmpty(styleClass)) {
	                writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
	            }
	            JSUtil.writeln(writer);
	        }
	    }
	 
	 
	 @Override
	    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
	       writer.writeAttribute("role", "treeitem", null); // $NON-NLS-1$ // $NON-NLS-2$
	       if(!tree.getNode().isEnabled()){
	           writer.writeAttribute("aria-disabled", "true", null); // $NON-NLS-1$ // $NON-NLS-2$
	          // writer.writeAttribute("class", "lotusInactive", null);
	       }
	       
	    }
	 
	 @Override
	    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
	        String s = super.getItemStyleClass(tree, enabled, selected);
	        if(selected) {
	            return ExtLibUtil.concatStyleClasses(s,(String)getProperty(PROP_MENU_SELECTED));
	        }
	        if(!tree.getNode().isEnabled()){
	            return ExtLibUtil.concatStyleClasses(s,(String)getProperty(PROP_MENU_INACTIVE));
	        }
	        
	        return s;
	    }
	 
}
