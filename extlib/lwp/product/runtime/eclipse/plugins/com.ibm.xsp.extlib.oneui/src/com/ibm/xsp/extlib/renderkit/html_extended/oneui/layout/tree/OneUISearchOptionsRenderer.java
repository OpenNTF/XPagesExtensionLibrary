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

import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlComboBoxRenderer;



public class OneUISearchOptionsRenderer extends HtmlComboBoxRenderer {
    
    private static final boolean ENCLOSING_DIV = false;
     
    private static final long serialVersionUID = 1L;

    public OneUISearchOptionsRenderer() {
    }
    
    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        if(ENCLOSING_DIV) {
            writer.startElement("div", null);//$NON-NLS-1$
            writer.writeAttribute("style","text-align: center; border-width: 1px; border-style: solid;",null); //$NON-NLS-1$ //$NON-NLS-2$
            writer.writeAttribute("class","lotusText",null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        super.preRenderTree(context, writer, tree);
    }
    
    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        super.postRenderTree(context, writer, tree);
        if(ENCLOSING_DIV) {
            writer.endElement("div"); // $NON-NLS-1$
        }
    }
        
    @Override
    public String getStyle() {
        return "border-width: 1px; border-style: solid;"; // $NON-NLS-1$
//      return "border-style: none;";
    }
    
    @Override
    public String getStyleClass() {
        return "lotusInactive"; // $NON-NLS-1$
    }
}