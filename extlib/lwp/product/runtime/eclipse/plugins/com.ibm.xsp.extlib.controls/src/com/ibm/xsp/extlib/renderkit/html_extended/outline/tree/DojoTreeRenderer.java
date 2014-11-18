/*
 * © Copyright IBM Corp. 2010, 2013
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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.JSUtil;

public class DojoTreeRenderer extends AbstractTreeRenderer {

    private static final long serialVersionUID = 1L;

    private String treeClientId;
    
    public DojoTreeRenderer(String treeClientId) {
        this.treeClientId = treeClientId;
    }

    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.setDojoTheme(true);
        rootEx.setDojoParseOnLoad(true);
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dijitTree);
        
        writer.startElement("div", null); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(treeClientId)) {
            writer.writeAttribute("id",treeClientId,null); // $NON-NLS-1$
        }
        writer.writeAttribute("dojoType", "dijit.Tree",null); // $NON-NLS-1$ $NON-NLS-2$
        JSUtil.writeln(writer);
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Generate a separator
        int type = tree.getNode().getType();
        if(type==ITreeNode.NODE_SEPARATOR) {
            // Not supported in a tree
            return;
        }
        
        // Generate a regular node
        String label = tree.getNode().getLabel();
//        String image = tree.getNode().getImage();
        boolean enabled = tree.getNode().isEnabled();
//        String style = getItemStyle(tree,enabled,false);
//        String styleClass = getItemStyleClass(tree,enabled,false);

        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        String href = null;
        String onclick = null;
        if(leaf) {
            href = tree.getNode().getHref();
            onclick = findNodeOnClick(tree);
        }
        
        boolean hasLink = leaf && enabled && (StringUtil.isNotEmpty(onclick) || StringUtil.isNotEmpty(href));
//        boolean hasImage = StringUtil.isNotEmpty(image);
        
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("dojoType", "dijit._TreeNode",null); // $NON-NLS-1$ $NON-NLS-2$
        
        // Are those available?
//      if(StringUtil.isNotEmpty(style)) {
//          writer.writeAttribute("style",style,null);
//      }
//      if(StringUtil.isNotEmpty(styleClass)) {
//          writer.writeAttribute("class",styleClass,null);
//      }

        if(hasLink) {
            if (StringUtil.isNotEmpty(onclick)) {
                writer.writeAttribute("onClick", onclick, null); // $NON-NLS-1$
            } else if (StringUtil.isNotEmpty(href)) {
                StringBuilder b = new StringBuilder();
                b.append("window.location.href="); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(b,RenderUtil.formatLinkRef(context,href));
                writer.writeAttribute("onClick", b.toString(), null); // $NON-NLS-1$
            }           
        }

//      if(hasImage) {
//          writer.startElement("img",outline);
//          if(StringUtil.isNotEmpty(clientId)) {
//              writer.writeAttribute("id",clientId+"_img",null);
//          }
//          if(StringUtil.isNotEmpty(image)) {
//              image = HtmlRendererUtil.getImageURL(context, image);
//              writer.writeAttribute("src",image,null);
//          }
//          writer.endElement("img");
//      }

        if(StringUtil.isNotEmpty(label)) {
            writer.writeText(label, "label"); // $NON-NLS-1$
        }
        
        renderChildren(context, writer, tree);
        
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }
}