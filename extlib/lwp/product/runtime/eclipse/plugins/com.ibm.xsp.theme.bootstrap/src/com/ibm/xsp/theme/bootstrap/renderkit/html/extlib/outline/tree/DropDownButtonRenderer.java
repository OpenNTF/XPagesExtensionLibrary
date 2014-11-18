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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.util.NavButtonRenderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.JSUtil;


public class DropDownButtonRenderer extends NavButtonRenderer {
    
    private static final long serialVersionUID = 1L;

    public DropDownButtonRenderer() {
    }
 
	@Override
	protected boolean buttonGroup() {
		return false;
	}
	
    @Override
	protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            writer.startElement("div",null);
            writer.writeAttribute("class", "btn-group", null); // $NON-NLS-1$
        } else {
        	super.startRenderContainer(context, writer, tree);
        }
    }
    
    @Override
    protected void endRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            writer.endElement("div");
            writer.write('\n');
        } else {
        	super.endRenderContainer(context, writer, tree);
        }
    }

    @Override
	protected void renderEntryNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==2) {
            boolean enabled = tree.getNode().isEnabled(); 
            boolean selected = tree.getNode().isSelected();
            renderPopupButton(context, writer, tree, enabled, selected);
        } else {
        	super.renderEntryNode(context, writer, tree);
        }
    }

    @Override
    protected String getItemTag() {
        return "li"; // $NON-NLS-1$
    }
	@Override
	protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
    	if(tree.getDepth()==2) {
			return "btn-group";
    	}
		return super.getItemStyleClass(tree, enabled, selected);
	}

    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
    	boolean popup = tree.getNode().getType()==ITreeNode.NODE_CONTAINER; 

    	writer.startElement("div",null); //$NON-NLS-1$
        writer.writeAttribute("class", "btn-group", null); // $NON-NLS-1$
    	
        writer.startElement("button",null); //$NON-NLS-1$
        writer.writeAttribute("type", "button", null); // $NON-NLS-1$
        
        if(popup) {
            // A popup button requires an id
            String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(clientId)) {
                writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
            }
        	writer.writeAttribute("class","btn btn-default dropdown-toggle",null); // $NON-NLS-1$ $NON-NLS-2$
        	writer.writeAttribute("data-toggle","dropdown",null); // $NON-NLS-1$ $NON-NLS-2$
        } else {
        	writer.writeAttribute("class","btn btn-default",null); // $NON-NLS-1$ $NON-NLS-2$
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
        
        // Add the action if necessary
        if(!popup && enabled) {
            String href = tree.getNode().getHref();
            String onclick = findNodeOnClick(tree);
            if (StringUtil.isNotEmpty(onclick)) {
                writer.writeAttribute("onclick", onclick, null); // $NON-NLS-1$
            } else if (StringUtil.isNotEmpty(href)) {
                StringBuilder b = new StringBuilder();
                b.append("window.location.href='"); // $NON-NLS-1$
                JSUtil.appendJavaScriptString(b,RenderUtil.formatLinkRef(context,href));
                b.append("'");
                writer.writeAttribute("onclick", b.toString(), null); // $NON-NLS-1$
            }           
        }
        
        // Render the text
        String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.writeText(label, "label"); // $NON-NLS-1$
        }

        if(popup) {
        	writePopupImage(context, writer, tree);
        }

        writer.endElement("button");//$NON-NLS-1$
        JSUtil.writeln(writer);
        
        // Render the children
        renderChildren(context, writer, tree);

        writer.endElement("div");//$NON-NLS-1$
        JSUtil.writeln(writer);
    }
    
    @Override
	protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
    	boolean popup = tree.getNode().getType()==ITreeNode.NODE_CONTAINER;
    	if(popup) {
            writer.writeAttribute("class", "dropdown-toggle", null); // $NON-NLS-1$
            writer.writeAttribute("data-toggle", "dropdown", null); // $NON-NLS-1$
            writer.writeAttribute("href", "#", null); // $NON-NLS-1$
        }
   }
}