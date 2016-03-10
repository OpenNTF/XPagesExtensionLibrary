/*
 * © Copyright IBM Corp. 2010, 2015
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
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;

public class DropDownButtonRenderer extends HtmlDivSpanRenderer {

    public static final String MENUID_SUFFIX = "_mn";//$NON-NLS-1$
    
    private static final long serialVersionUID = 1L;

    public DropDownButtonRenderer() {
    }
    
    protected Object getProperty(int prop) {
        {
            // translating some extra strings that are unused here in the extlib.control plugin,
            // but are used in the other themes - e.g. the bootstrap DataViewRenderer.
            String str = "";
            str = "Click for actions";  // $NLS-DropDownButtonRenderer.Clickforactions-1$
            str = "Action button";  // $NLS-DropDownButtonRenderer.Actionbutton-1$
            str = "Actions";  // $NLS-DropDownButtonRenderer.Actions-1$
            // end strings
            str.getClass(); // prevent unused variable warning
        }// end translating extra string
        
        return null;
    }
    
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean leaf = tree.getNode().getType() == ITreeNode.NODE_LEAF;
        if (leaf) {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        }
        else {
            renderPopupButton(context, writer, tree, enabled, selected);
        }
    }

    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("button",null); //$NON-NLS-1$
        
        // A popup button requires an id
        String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(clientId)) {
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        }
        
        String style = tree.getNode().getStyle();
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style", style, null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        String styleClass = tree.getNode().getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$ $NON-NLS-2$
        }

        String image = tree.getNode().getImage();
        boolean hasImage = StringUtil.isNotEmpty(image);
        if(hasImage) {
            writer.startElement("img",null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(image)) {
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
            }
            writer.endElement("img"); // $NON-NLS-1$
        }
        
        // Render the text
        String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.writeText(label, "label"); // $NON-NLS-1$
        }

        writePopupImage(context, writer, tree);

        writer.endElement("button");//$NON-NLS-1$
        JSUtil.writeln(writer);
    }
    
    protected void writePopupImage(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Render the popup image (down arrow)
        // Uniquely if it has multiple choices
        if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            // space and Unicode Character 'BLACK DOWN-POINTING TRIANGLE' 
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
}