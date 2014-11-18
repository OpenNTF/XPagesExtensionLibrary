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

package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.tree.OneUIv3TreePopupMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;


public class OneUIv3PlaceBarActionsRenderer extends OneUIv3TreePopupMenuRenderer {
    
    private static final long serialVersionUID = 1L;

    public OneUIv3PlaceBarActionsRenderer() {
    }

    @Override
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("a",null);
        
        // A popup button requires an id
        String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(clientId)) {
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        }
        
        writer.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$

        String image = tree.getNode().getImage();
        boolean hasImage = StringUtil.isNotEmpty(image);
        if(hasImage) {
            writer.startElement("img",null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(image)) {
                image = HtmlRendererUtil.getImageURL(context, image);
                writer.writeURIAttribute("src",image,null); // $NON-NLS-1$
            }
            writer.endElement("img"); // $NON-NLS-1$
        }
        
        // Render the text
        String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.writeText(label, "label"); // $NON-NLS-1$
        }

        // Render the popup image (down arrow)
        // Uniquely if it has multiple choices
        if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            writer.startElement("img",null); // $NON-NLS-1$
            //writer.writeAttribute("class","yourProductSprite yourProductSprite-btnDropDown2",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("src",HtmlRendererUtil.getImageURL(context,OneUIResources.get().DROPDOWN_PNG),null); // $NON-NLS-1$
            writer.writeAttribute("aria-label","Show Menu",null);  // $NON-NLS-1$ $NLS-OneUIv3PlaceBarActionsRenderer.ShowMenu-2$
            writer.writeAttribute("alt","Show Menu",null);  // $NON-NLS-1$ $NLS-OneUIv3PlaceBarActionsRenderer.ShowMenu.1-2$
            writer.endElement("img"); // $NON-NLS-1$
            writer.startElement("span",null); // $NON-NLS-1$
            writer.writeAttribute("class","lotusAltText",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeText("\u25BC", null); //$NON-NLS-1$
            writer.endElement("span"); // $NON-NLS-1$
        }

        writer.endElement("a");
        JSUtil.writeln(writer);
    }
    
    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        return "lotusBtnContainer"; // $NON-NLS-1$
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String value = "lotusBtn"; // $NON-NLS-1$
        String s = super.getItemStyleClass(tree,enabled,selected);
        return ExtLibUtil.concatStyleClasses(value, s);
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            super.renderChildren(context, writer, tree);
        } else {
            if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
                DojoMenuRenderer r = new DojoMenuRenderer();
                String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$
                
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