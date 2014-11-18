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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;

public class LinksListRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.LinksListRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    protected Object getProperty(int prop) {
    	// Should we rename these styles?
        switch(prop) {
            case PROP_LINKSLIST_INLINELIST:   	return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_LINKSLIST_FIRST:  		return "lotusFirst"; // $NON-NLS-1$
        }
        return null;
    }

    public LinksListRenderer() {
    }

    public LinksListRenderer(UIComponent component) {
        super(component);
    }
    
    @Override
    protected void writePopupImage(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Render the popup image (down arrow)
        // Uniquely if it has multiple choices
        if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
            writer.writeText(" ",null); // $NON-NLS-1$
            writer.startElement("img",null); // $NON-NLS-1$
            writer.writeAttribute("class","lotusArrow lotusDropDownSprite",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("src",HtmlRendererUtil.getImageURL(context,Resources.get().BLANK_GIF),null); // $NON-NLS-1$
            writer.writeAttribute("aria-label","Show Menu",null);  // $NON-NLS-1$ // $NLS-OneUILinksListRenderer_AriaLabel_ShowMenu-2$ 
            writer.writeAttribute("alt","Show Menu",null);  // $NON-NLS-1$ // $NLS-OneUILinksListRenderer_Alt_ShowMenu-2$
            writer.endElement("img"); // $NON-NLS-1$
            writer.startElement("span",null); // $NON-NLS-1$
            writer.writeAttribute("class","lotusAltText",null); // $NON-NLS-1$ $NON-NLS-2$
            // Unicode Character 'BLACK DOWN-POINTING TRIANGLE' 
            writer.writeText("\u25BC", null); //$NON-NLS-1$ 
            writer.endElement("span"); // $NON-NLS-1$
        }
    }    
}