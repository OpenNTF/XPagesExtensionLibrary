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
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.JSUtil;

public class HtmlComboBoxRenderer extends ComboBoxRenderer {
    
    private static final long serialVersionUID = 1L;
    
    public HtmlComboBoxRenderer() {
    }

    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        //AbstractOutline outline = (AbstractOutline)tree.getComponent();

        // Write a regular combox
        // How can we use a Dojo combobox with images here?
        writer.startElement("select", null); // $NON-NLS-1$
        String id = getClientId();
        if(StringUtil.isNotEmpty(id)) {
            writer.writeAttribute("name",id,null); // $NON-NLS-1$
            writer.writeAttribute("id",id,null); // $NON-NLS-1$
        }

        String style = getStyle();
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        String accTitle = getAccTitle();
        if(StringUtil.isNotEmpty(accTitle)) {
            writer.writeAttribute("title",accTitle,null); // $NON-NLS-1$
        }
        JSUtil.writeln(writer);
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.endElement("select"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }

    @Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Generate a regular node
        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        if(leaf) {
            String label = tree.getNode().getLabel();
            String value = tree.getNode().getSubmitValue();
            writer.startElement("option", null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(value)) {
                writer.writeAttribute("value",value,null); // $NON-NLS-1$
            }
            boolean enabled = tree.getNode().isEnabled();
            if(!enabled) {
                writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            }
            boolean selected = tree.getNode().isSelected();
            if(selected) {
                writer.writeAttribute("selected", "selected", "selected"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            }
            // Don't obey escape=false here, since HTML inside option tags would be invalid
            writer.writeText(label,"label"); // $NON-NLS-1$
            writer.endElement("option"); // $NON-NLS-1$
            JSUtil.writeln(writer);
        }
    }   
}