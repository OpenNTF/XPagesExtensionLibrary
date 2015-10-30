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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;


public class AbstractLinkPopupRenderer extends HtmlListRenderer {

    private static final long serialVersionUID = 1L;

    public AbstractLinkPopupRenderer() {
    }

    public static final int PROP_MENUPREFIX			= 100;
    
    @Override
	protected Object getProperty(int prop) {
		switch(prop) {
			case PROP_MENUPREFIX:			return "al";	// Should be overridden!! //$NON-NLS-1$
		}
		return super.getProperty(prop);
	}
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String value = null;
        if (tree.getNodeContext().isFirstNode()) {
            value = "lotusFirst"; // $NON-NLS-1$
        }
        if (selected || !enabled) {
            value = ExtLibUtil.concatStyleClasses(value, "lotusSelected"); // $NON-NLS-1$
            // value = selected ? "lotusFirst lotusSelected" : "lotusFirst";
        }
        if (tree.getNode() instanceof UserTreeNode) {
            value = ExtLibUtil.concatStyleClasses(value, "lotusUser"); // $NON-NLS-1$
        }
        value = ExtLibUtil.concatStyleClasses(value, super.getItemStyleClass(tree, enabled, selected));
        return value;
    }
    
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        if(leaf) {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        } else {
            renderPopupButton(context, writer, tree, enabled, selected);
        }
    }
    
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("a",null);
        
        // LHEY97HJYL Container node requires role button and aria-haspopup true
        writer.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("aria-haspopup", "true", null); // $NON-NLS-1$ $NON-NLS-2$

        // A popup button requires an id
    	String prefix = (String)getProperty(PROP_MENUPREFIX);
        String clientId = tree.getClientId(context,prefix,1); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(clientId)) {
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        }
        
        writer.writeAttribute("href", "javascript:;", null); // $NON-NLS-2$ $NON-NLS-1$

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
        	if(tree.getNode().isEscape()) {
        		writer.writeText(label, "label"); // $NON-NLS-1$
        	} else {
        		writer.write(label);
        	}
        }
        writer.writeText(" ",null); // $NON-NLS-1$

        // Render the popup image (down arrow)
        // Uniquely if it has multiple choices
        if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            writer.startElement("img",null); // $NON-NLS-1$
            writer.writeAttribute("class","lotusArrow lotusDropDownSprite",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("src",HtmlRendererUtil.getImageURL(context,OneUIResources.get().BLANK_GIF),null); // $NON-NLS-1$
            writer.writeAttribute("aria-label","Show Menu",null);  // $NON-NLS-1$ $NLS-OneUIPlaceBarActionsRenderer.ShowMenu-2$
            writer.writeAttribute("alt","Show Menu",null);  // $NON-NLS-1$ $NLS-OneUIPlaceBarActionsRenderer.ShowMenu.1-2$
            writer.endElement("img"); // $NON-NLS-1$
            writer.startElement("span",null); // $NON-NLS-1$
            writer.writeAttribute("class","lotusAltText",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeText("\u25BC", null); // $NLS-OneUIPlaceBarActionsRenderer.u25BC-1$
            writer.endElement("span"); // $NON-NLS-1$
        }

        writer.endElement("a");
        JSUtil.writeln(writer);
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            super.renderChildren(context, writer, tree);
        } else {
            if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            	String prefix = (String)getProperty(PROP_MENUPREFIX);
                DojoMenuRenderer r = new DojoMenuRenderer();
                String clientId = tree.getClientId(context,prefix,1); // $NON-NLS-1$
                
                String mid = clientId+"_mn"; // $NON-NLS-1$
                r.setMenuId(mid);

                if(StringUtil.isNotEmpty(clientId)) {
                    r.setConnectId(clientId);
                }

                r.setConnectEvent("onclick"); // $NON-NLS-1$
                r.render(context, writer, tree);
            }
        }
    }    
}