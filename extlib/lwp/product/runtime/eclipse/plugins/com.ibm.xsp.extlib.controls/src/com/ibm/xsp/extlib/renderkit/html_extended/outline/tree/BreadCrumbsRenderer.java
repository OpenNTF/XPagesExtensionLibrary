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

package com.ibm.xsp.extlib.renderkit.html_extended.outline.tree;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.outline.UIOutlineBreadCrumbs;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class BreadCrumbsRenderer extends HtmlTagsRenderer {
    protected static final int PROP_BREADCRUMBS_CONTAINER	= 0;
    protected static final int PROP_BREADCRUMBS_LABEL		= 1;
    protected static final int PROP_BREADCRUMBS_SEPARATOR	= 2;

    private static final long serialVersionUID = 1L;

    public BreadCrumbsRenderer() {
    }

    public BreadCrumbsRenderer(UIComponent component) {
        super(component);
    }
	
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BREADCRUMBS_CONTAINER:   return "lotusBreadcrumbs"; // $NON-NLS-1$
            case PROP_BREADCRUMBS_LABEL:   return "lotusBreadcrumbsLabel"; // $NON-NLS-1$
            case PROP_BREADCRUMBS_SEPARATOR:  return "lotusBreadcrumbsSeparator"; // $NON-NLS-1$
        }
        return null;
    }
    
	@Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("role", "navigation", null); // $NON-NLS-1$ $NON-NLS-2$
        
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
                writer.startElement("span", null); // $NON-NLS-1$
                writer.writeAttribute("class", (String)getProperty(PROP_BREADCRUMBS_LABEL), null); // $NON-NLS-1$
                writer.writeAttribute("title", text, null); // $NON-NLS-1$
                writer.writeText(text, null);
                writer.endElement("span"); // $NON-NLS-1$
            }
        }
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void renderEntryNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        if(!tree.getNodeContext().isFirstNode()) {
            renderSeparator(context, writer, tree);
        }
        super.renderEntryNode(context, writer, tree);
    }

    protected void renderSeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.startElement("span", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_BREADCRUMBS_SEPARATOR), null); // $NON-NLS-1$
        writer.writeText(" > ", null);
        writer.endElement("span"); // $NON-NLS-1$
    }

    @Override
    public boolean isNodeEnabled(ITreeNode node) {
        // If the node is the latest one
        if(node.getNodeContext().isLastNode()) {
            return false;
        }
        return super.isNodeEnabled(node);
    }
    
    @Override
    protected boolean alwaysRenderItemLink(TreeContextImpl tree,
    		boolean enabled, boolean selected) {
    	return enabled;
    }
}
