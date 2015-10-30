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
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.HtmlUtil;

public class DojoAccordionRenderer extends AbstractTreeRenderer {
    
    private static final long serialVersionUID = 1L;

    private String accordionClientId;
    
    public DojoAccordionRenderer(String accordionClientId) {
        this.accordionClientId = accordionClientId;
    }
    
    public String getClientId() {
        return accordionClientId;
    }
    
    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        AbstractOutline outline = (AbstractOutline)tree.getComponent();
        
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.setDojoTheme(true);
        rootEx.setDojoParseOnLoad(true);
        
        writer.startElement("div", null); // $NON-NLS-1$
        String id = getClientId();
        if(StringUtil.isNotEmpty(id)) {
            writer.writeAttribute("id",id,null); // $NON-NLS-1$
        }
        
        String dojoType = outline.getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            dojoType = "dijit.layout.AccordionContainer"; // $NON-NLS-1$
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dijitLayoutAccordion);
        }

        String style = outline.getStyle();
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = outline.getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }

        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        DojoRendererUtil.getDojoAttributeMap(outline,attrs);
        initDojoAttributes(context, outline, attrs);
        DojoUtil.addDojoHtmlAttributes(context,dojoType,null,attrs);
        
        writer.write('\n');
    }
    protected DojoModuleResource getDojoModule() {
        return ExtLibResources.dijitLayoutAccordionPane;
    }
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
//        if(dojoComponent instanceof UIDojoAccordionContainer) {
//            UIDojoAccordionContainer c = (UIDojoAccordionContainer)dojoComponent;
//        }
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.endElement("div"); // $NON-NLS-1$
        writer.write('\n');
    }

    @Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    	// Don't need to perform any output here
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    	// Don't need to perform any output here
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // TODO: Delegate to a HTML list renderer       
//      if(indexes.length>1) {
//          renderEntryLi(context, tree, writer, node, indexes);
//          return;
//      }

        // Generate a separator
        int type = tree.getNode().getType();
        if(type==ITreeNode.NODE_SEPARATOR) {
            // Not supported in a accordion
            return;
        }
        
        // Generate a regular node
        String label = tree.getNode().getLabel();
//        String image = tree.getNode().getImage();
        boolean enabled = tree.getNode().isEnabled();
        String style = getItemStyle(tree,enabled,false);
        String styleClass = getItemStyleClass(tree,enabled,false);

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
       // writer.writeAttribute("dojoType", "dijit.layout.AccordionPane",null); // $NON-NLS-1$ $NON-NLS-2$

        String dojoType = "dijit.layout.AccordionPane"; // $NON-NLS-1$
        Map<String,String> attrs = DojoRendererUtil.createMap(context);

        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        if(tree.getNode().isSelected()) {
//            writer.writeAttribute("selected",true,null); // $NON-NLS-1$
            attrs.put("selected", "true"); // $NON-NLS-1$ $NON-NLS-2$
        }
        if(hasLink) {
            if (StringUtil.isNotEmpty(onclick)) {
//                writer.writeAttribute("onClick", onclick, null); // $NON-NLS-1$
                attrs.put("onClick", onclick); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                // How to generate a link here?
                //RenderUtil.writeLinkAttribute(context,writer,href,null);
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
            //writer.writeAttribute("title", label, null); // $NON-NLS-1$
        	attrs.put("title", HtmlUtil.toHTMLContentString(label, false)); //$NON-NLS-1$
        }

        DojoRendererUtil.writeDojoHtmlAttributes(context, getComponent(), dojoType, attrs);
        
    	writer.startElement(new HtmlListRenderer().getContainerTag(), null);
    	writer.writeAttribute("style", "list-style-type:none; margin:0; padding:0", null); //$NON-NLS-1$ //$NON-NLS-2$
        renderChildren(context, writer, tree);
    	writer.endElement(new HtmlListRenderer().getContainerTag());
        
        writer.endElement("div"); // $NON-NLS-1$
        writer.write('\n');
    }

    @Override
    protected AbstractTreeRenderer getChildrenRenderer(TreeContextImpl tree) {
        if(tree.getDepth()==1) {
            return this;
        }
//        UIComponent c = tree.getComponent();
        return new HtmlListRenderer();
    }
}