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
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class SortLinksRenderer extends HtmlListRenderer {
    protected static final int PROP_SORTLINKS_SORT 			= 0;
    protected static final int PROP_SORTLINKS_INLINELIST	= 1;
    protected static final int PROP_SORTLINKS_FIRST			= 2;
    protected static final int PROP_SORTLINKS_ACTIVESORT 	= 3;
    protected static final int PROP_SORTLINKS_MORESORTS		= 4;

	private static final long serialVersionUID = 1L;

    public SortLinksRenderer() {
    }

    public SortLinksRenderer(UIComponent component) {
        super(component);
    }

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_SORTLINKS_SORT:   return "lotusSort"; // $NON-NLS-1$
            case PROP_SORTLINKS_INLINELIST:   return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_SORTLINKS_FIRST:  return "lotusFirst"; // $NON-NLS-1$
            case PROP_SORTLINKS_ACTIVESORT:  return "lotusActiveSort"; // $NON-NLS-1$
            case PROP_SORTLINKS_MORESORTS:  return "lotusMoreSorts"; // $NON-NLS-1$
        }
        return null;
    }
    
    @Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        
        String style = getContainerStyle(tree);
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SORTLINKS_SORT),getContainerStyleClass(tree));
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }

        super.preRenderList(context, writer, tree);
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        super.postRenderList(context, writer, tree);
        writer.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        return (String)getProperty(PROP_SORTLINKS_INLINELIST);
    }
    
    @Override
    public boolean isNodeEnabled(ITreeNode node) {
        // The user node should not be enabled by default... 
        return !(node instanceof UserTreeNode);
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String value = null;
        if(tree.getNodeContext().isFirstNonStatic()) {
            value = (String)getProperty(PROP_SORTLINKS_FIRST);
        }
        value = ExtLibUtil.concatStyleClasses(value,super.getItemStyleClass(tree,enabled,selected));
        return value;
    }   

    @Override
    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        if(tree.getNode().getType()==ITreeNode.NODE_LEAF) {
            writer.writeAttribute("class", (String)getProperty(PROP_SORTLINKS_ACTIVESORT), null); // $NON-NLS-1$
        } else {
            writer.writeAttribute("class", (String)getProperty(PROP_SORTLINKS_MORESORTS), null); // $NON-NLS-1$
        }
        super.renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
    }
}
