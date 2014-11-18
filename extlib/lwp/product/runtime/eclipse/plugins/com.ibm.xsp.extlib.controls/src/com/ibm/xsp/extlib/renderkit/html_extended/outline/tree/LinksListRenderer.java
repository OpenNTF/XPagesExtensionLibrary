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
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class LinksListRenderer extends HtmlListRenderer {
    
    protected static final int PROP_LINKSLIST_INLINELIST	= 0;
    protected static final int PROP_LINKSLIST_FIRST			= 1;
    
    public static final String MENUID_SUFFIX = "_mn"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    public LinksListRenderer() {
    }

    public LinksListRenderer(UIComponent component) {
        super(component);
    }

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LINKSLIST_INLINELIST:   return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_LINKSLIST_FIRST:  return "lotusFirst"; // $NON-NLS-1$
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
        String styleClass = getContainerStyleClass(tree);
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
        String value = super.getContainerStyleClass(node);
        if(StringUtil.isNotEmpty(value)) {
            value = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LINKSLIST_INLINELIST),value);
            return value;
        }
        return (String)getProperty(PROP_LINKSLIST_INLINELIST);
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
            value = (String)getProperty(PROP_LINKSLIST_FIRST);
        }
        value = ExtLibUtil.concatStyleClasses(value,super.getItemStyleClass(tree,enabled,selected));
        return value;
    }
        
    protected void writePopupImage(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Render the popup image (down arrow) if the link has children
        if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            // a space and Unicode Character 'BLACK DOWN-POINTING TRIANGLE' 
            writer.writeText(" \u25BC", null); //$NON-NLS-1$
            //writer.writeText(" \u02C5", null);
        }
    }

    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if (depth == 1) {
            super.renderChildren(context, writer, tree);
        }
        else {
            if (tree.getNode().getType() != ITreeNode.NODE_LEAF) {
                DojoMenuRenderer r = new DojoMenuRenderer();
                String clientId = tree.getClientId(context, "ab", 1); // $NON-NLS-1$

                String mid = clientId + MENUID_SUFFIX; // $NON-NLS-1$
                r.setMenuId(mid);

                if (StringUtil.isNotEmpty(clientId)) {
                    r.setConnectId(clientId);
                }

                r.setConnectEvent("onclick"); // $NON-NLS-1$
                r.render(context, writer, tree);
            }
        }
    }

    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        super.renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
        
        if (tree.getNode().getType() != ITreeNode.NODE_LEAF) {
            String clientId = tree.getClientId(context, "ab", 1); // $NON-NLS-1$
            writer.writeAttribute("id",clientId,null); // $NON-NLS-1$
        }
    }
    
}
