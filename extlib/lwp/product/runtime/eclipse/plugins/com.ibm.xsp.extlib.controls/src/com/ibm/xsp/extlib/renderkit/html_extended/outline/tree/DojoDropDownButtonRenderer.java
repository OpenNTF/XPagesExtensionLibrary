/*
 * © Copyright IBM Corp. 2010, 2012
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
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.JSUtil;



public class DojoDropDownButtonRenderer extends DropDownButtonRenderer {
    protected static final int PROP_DROPDOWN_BUTTON_CLASS       = 0;
    protected static final int PROP_DROPDOWN_ALTTEXT_CLASS      = 1;
    protected static final int PROP_DROPDOWN_IMAGEURL_CLASS     = 2;
    protected static final int PROP_DROPDOWN_IMAGE_ARIA_LABEL   = 3;
    protected static final int PROP_DROPDOWN_IMAGE_ALT          = 4;

	private static final long serialVersionUID = 1L;

    public DojoDropDownButtonRenderer() {
    }
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_DROPDOWN_BUTTON_CLASS:   return "lotusBtn"; // $NON-NLS-1$
            case PROP_DROPDOWN_ALTTEXT_CLASS:  return "lotusAltText"; // $NON-NLS-1$
            case PROP_DROPDOWN_IMAGEURL_CLASS: return ExtLibResources.dropDownImg; // $NON-NLS-1$
            case PROP_DROPDOWN_IMAGE_ARIA_LABEL: return "Show Menu";// $NLS-DojoDropDownButtonRenderer_ImageLabelShowMenu-1$
            case PROP_DROPDOWN_IMAGE_ALT:      return "Show Menu";// $NLS-DojoDropDownButtonRenderer_ImageLabelShowMenu-1$
        }
        return null;
    }
    
    @Override
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$

        writer.startElement("span",null); // $NON-NLS-1$
        
        writer.writeAttribute("aria-owns", clientId+MENUID_SUFFIX, null); // $NON-NLS-1$
        writer.writeAttribute("aria-haspopup", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        writer.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
            
        writer.startElement("a",null);
        
        // A popup button requires an id
        writer.writeAttribute("id", clientId, null); // $NON-NLS-1$
        
        writer.writeAttribute("href", "javascript:;", null); // $NON-NLS-2$ $NON-NLS-1$
        
        String style = tree.getNode().getStyle();
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style", style, null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        String styleClass = ExtLibUtil.concatStyleClasses(tree.getNode().getStyleClass(),(String)getProperty(PROP_DROPDOWN_BUTTON_CLASS));
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
                if ( ExtLibRenderUtil.isAltPresent(imageAlt) ) {
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
        writer.writeText(" ",null); // $NON-NLS-1$
        
        // Render the popup image (down arrow)
        writePopupImage(context, writer, tree);

        writer.endElement("a");
        writer.endElement("span"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }

	protected void writePopupImage(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
		if (tree.getNode().getType() != ITreeNode.NODE_LEAF) {
			writer.startElement("img", null); // $NON-NLS-1$
			// writer.writeAttribute("class","yourProductSprite yourProductSprite-btnDropDown2",null); // $NON-NLS-1$ $NON-NLS-2$
			writer.writeAttribute("src", HtmlRendererUtil.getImageURL(context, (String)getProperty(PROP_DROPDOWN_IMAGEURL_CLASS)), null); // $NON-NLS-1$
			String dropDownImageAriaLabel = (String)getProperty(PROP_DROPDOWN_IMAGE_ARIA_LABEL);
			if( ExtLibRenderUtil.isAltPresent(dropDownImageAriaLabel) ){
			    writer.writeAttribute("aria-label", dropDownImageAriaLabel, null); //$NON-NLS-1$
			}
			String dropDownImageAlt = (String)getProperty(PROP_DROPDOWN_IMAGE_ALT);
			if( ExtLibRenderUtil.isAltPresent(dropDownImageAlt) ){
			    writer.writeAttribute("alt", dropDownImageAlt, null); //$NON-NLS-1$
			}
			writer.endElement("img"); // $NON-NLS-1$
			writer.startElement("span", null); // $NON-NLS-1$
			writer.writeAttribute("class", (String)getProperty(PROP_DROPDOWN_ALTTEXT_CLASS), null); // $NON-NLS-1$
			writer.writeText("\u25BC", null); // $NLS-OneUIDropDownButtonRenderer.u25BC-1$
			writer.endElement("span"); // $NON-NLS-1$
		}
	}
   
}
