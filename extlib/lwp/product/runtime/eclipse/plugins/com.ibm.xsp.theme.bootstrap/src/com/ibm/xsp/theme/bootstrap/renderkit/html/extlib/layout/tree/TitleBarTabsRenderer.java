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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.util.NavRenderer;
import com.ibm.xsp.util.JSUtil;

public class TitleBarTabsRenderer extends NavRenderer {
    
    private static final long serialVersionUID = 1L;

    public TitleBarTabsRenderer() {
    }

    @Override
    protected boolean makeSelectedActive(TreeContextImpl node) {
        return true;
    }

    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        if(node.getDepth()==1) {
            return "nav nav-tabs applayout-titlebar-tabs"; // $NON-NLS-1$
        }
        return super.getContainerStyleClass(node);
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        return super.getItemStyleClass(tree, enabled, selected);
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Do not render the children - only one level...
//        if(tree.getDepth()==1) {
            super.renderChildren(context, writer, tree);
//        }
    }
    
    @Override
    protected void renderContainerRole(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Defect201652 - A11Y - add correct tab role
        writer.writeAttribute("role", "tablist",null); // $NON-NLS-1$ $NON-NLS-2$
    }
    
    @Override
    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
        String nodeRole = tree.getNode().getRole();
        if(StringUtil.isNotEmpty(nodeRole)) {
            return tree.getNode().getRole();
        }else{
            return "tab"; // $NON-NLS-1$
        }
    }
    
    @Override
    protected void renderEntryNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled(); 
        boolean selected = tree.getNode().isSelected();

        String itemTag = getItemTag();
        if(StringUtil.isNotEmpty(itemTag)) {
            writer.startElement(itemTag,null);
            String style = getItemStyle(tree,enabled,selected);
            if(StringUtil.isNotEmpty(style)) {
                writer.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            String styleClass = getItemStyleClass(tree,enabled,selected);
            if(StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            
            String title = getItemTitle(tree,enabled,selected);
            if(StringUtil.isNotEmpty(title)) {
                writer.writeAttribute("title",title,null); // $NON-NLS-1$
            }
        }

        boolean separate = isChildrenSeparate();

        // render the item content
        renderEntryItemContent(context, writer, tree, enabled, selected);
        
        // and its children
        if(!separate) {
            renderChildren(context, writer, tree);
        }
        
        if(StringUtil.isNotEmpty(itemTag)) {
            writer.endElement(itemTag);
        }
        JSUtil.writeln(writer);
        
        // and its children
        if(separate && tree.getNodeContext().hasChildren()) {
            if(StringUtil.isNotEmpty(itemTag)) {
                writer.startElement(itemTag,null);
                renderEntryNodeChildAttributes(context, writer, tree, enabled, selected);
            }
            renderChildren(context, writer, tree);
            if(StringUtil.isNotEmpty(itemTag)) {
                writer.endElement(itemTag);
            }
            JSUtil.writeln(writer);
        }
    }
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean hasLink = false;
        boolean alwaysRenderLinks = alwaysRenderItemLink(tree, enabled, selected);
        if(enabled) {
            String href = tree.getNode().getHref();
            if(StringUtil.isNotEmpty(href)) {
                writer.startElement("a",null);
                String role = getItemRole(tree, enabled, selected);
                if (StringUtil.isNotEmpty(role)) {
                    writer.writeAttribute("role", role, null); // $NON-NLS-1$
                }
                RenderUtil.writeLinkAttribute(context,writer,href);
                hasLink = true;
            } else {
                String onclick = findNodeOnClick(tree);
                if(StringUtil.isNotEmpty(onclick)) {
                    writer.startElement("a",null);
                    String role = getItemRole(tree, enabled, selected);
                    if (StringUtil.isNotEmpty(role)) {
                        writer.writeAttribute("role", role, null); // $NON-NLS-1$
                    }
                    writer.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                    writer.writeAttribute("onclick", "javascript:"+onclick, null); // $NON-NLS-1$ $NON-NLS-2$
                    hasLink = true;
                }
            }
        }
        if(!hasLink && alwaysRenderLinks) {
            // Render an empty link...
            writer.startElement("a",null);
            String role = getItemRole(tree, enabled, selected);
            if (StringUtil.isNotEmpty(role)) {
                writer.writeAttribute("role", role, null); // $NON-NLS-1$
            }
            hasLink = true;
        }else if(!hasLink && !alwaysRenderLinks) {
            //No link, so add role to containing LI element
            String role = getItemRole(tree, enabled, selected);
            if (StringUtil.isNotEmpty(role)) {
                writer.writeAttribute("role", role, null); // $NON-NLS-1$
            }
        }
        if(hasLink) {
            renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
        }

        String image = tree.getNode().getImage();
        boolean hasImage = StringUtil.isNotEmpty(image);
        if(hasImage) {
            writer.startElement("img",null); // $NON-NLS-1$
            image = HtmlRendererUtil.getImageURL(context, image);
            writer.writeAttribute("src",image,null); // $NON-NLS-1$
            String imageAlt = tree.getNode().getImageAlt();
            if (StringUtil.isNotEmpty(imageAlt)) {
                writer.writeAttribute("alt",imageAlt,null); // $NON-NLS-1$
            }
            String imageHeight = tree.getNode().getImageHeight();
            if (StringUtil.isNotEmpty(imageHeight)) {
                writer.writeAttribute("height",imageHeight,null); // $NON-NLS-1$
            }
            String imageWidth = tree.getNode().getImageWidth();
            if (StringUtil.isNotEmpty(imageWidth)) {
                writer.writeAttribute("width",imageWidth,null); // $NON-NLS-1$
            }
            writer.endElement("img"); // $NON-NLS-1$
        }
        
        // Generate a regular node
        renderEntryItemLabel(context, writer, tree, enabled, selected);
        
        // Render a popup image, if any
        writePopupImage(context, writer, tree);

        if(hasLink || alwaysRenderLinks) {
            writer.endElement("a");
            tree.markCurrentAsAction();
        }
    }
}