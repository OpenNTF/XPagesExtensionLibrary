/*
 * © Copyright IBM Corp. 2010, 2014
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.JSUtil;


/**
 * Dojo toolbar renderer.
 * This renders a toolbar with buttons, including popup menu for sub nodes.
 * @author priand
 */
public class DojoToolbarRenderer extends AbstractTreeRenderer {

    private static final long serialVersionUID = 1L;

    private UIComponent component;
    private String toolbarClientId;
    private boolean showButtonLabels = true;
    
    public DojoToolbarRenderer(UIComponent component, String toolbarClientId) {
        this.component = component;
        this.toolbarClientId = toolbarClientId;
    }
    
    public String getClientId() {
        return toolbarClientId;
    }
    
    public boolean isShowButtonLabels() {
        return showButtonLabels;
    }

    public void setShowButtonLabels(boolean showButtonLabels) {
        this.showButtonLabels = showButtonLabels;
    }

    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.setDojoTheme(true);
        rootEx.setDojoParseOnLoad(true);
        
        writer.startElement("div", null); // $NON-NLS-1$
        String id = getClientId();
        if(StringUtil.isNotEmpty(id)) {
            writer.writeAttribute("id",id,null); // $NON-NLS-1$
        }
        
        String dojoType = component!=null ? (String)component.getAttributes().get("dojoType") : null; // $NON-NLS-1$
        if(StringUtil.isEmpty(dojoType)) {
            dojoType = "dijit.Toolbar"; // $NON-NLS-1$
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dijitToolbar);
        }
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dijitFormButton);

        String style = component!=null ? (String)component.getAttributes().get("style") : null; // $NON-NLS-1$
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = component!=null ? (String)component.getAttributes().get("styleClass") : null; // $NON-NLS-1$
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }

        Map<String,String> attrs = null;
        if(component instanceof FacesDojoComponent) {
            attrs = DojoRendererUtil.createMap(context);
            DojoRendererUtil.getDojoAttributeMap((FacesDojoComponent)component,attrs);
            initDojoAttributes(context, (FacesDojoComponent)component, attrs);
        }
        DojoUtil.addDojoHtmlAttributes(context,dojoType,null,attrs);
        
        JSUtil.writeln(writer);
    }
    protected DojoModuleResource getDojoModule() {
        return ExtLibResources.extlibDialog;
    }
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
//        if(dojoComponent instanceof UIOutlineToolbar) {
//            UIOutlineToolbar c = (UIOutlineToolbar)dojoComponent;
//        }
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }
    
    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled();
        String style = getItemStyle(tree,enabled,false);
        String styleClass = getItemStyleClass(tree,enabled,false);

        // Generate a separator
        int type = tree.getNode().getType();
        if(type==ITreeNode.NODE_SEPARATOR) {
            writer.startElement("div", null); // $NON-NLS-1$
            //writer.writeAttribute("dojoType", "dijit.ToolbarSeparator",null); // $NON-NLS-1$ $NON-NLS-2$
            DojoRendererUtil.writeDojoHtmlAttributes(context,getComponent(),"dijit.ToolbarSeparator"); // $NON-NLS-1$ $NON-NLS-2$
            if(StringUtil.isNotEmpty(style)) {
                writer.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            writer.endElement("div"); // $NON-NLS-1$
            JSUtil.writeln(writer);
            return;
        }
        
        // Generate a regular node
        String label = tree.getNode().getLabel();
        String image = tree.getNode().getImage();

        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        String href = null;
        String onclick = null;
        if(leaf) {
            href = tree.getNode().getHref();
            onclick = findNodeOnClick(tree);
        }
        
        boolean hasLink = leaf && enabled && (StringUtil.isNotEmpty(onclick) || StringUtil.isNotEmpty(href));
        boolean hasImage = StringUtil.isNotEmpty(image);
        
        writer.startElement("div", null); // $NON-NLS-1$

        // The button must have an id to connect the menu to it
        String bid  = tree.getClientId(context,"bt",1); // $NON-NLS-1$
        
        String dojoType = "dijit.form.Button";  // $NON-NLS-1$
        //String dojoType = leaf ? "dijit.form.Button" : "dijit.form.DropDownButton"; 
        //writer.writeAttribute("dojoType", dojoType,null); // $NON-NLS-1$
        writer.writeAttribute("id", bid,null); // $NON-NLS-1$
        
        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        
        boolean showLabel = isShowButtonLabels();
        
        // SPR#PHAN9DEFVF when using <img> for icon (not style/styleClass icon)
        // and showLabel="false" the client-side dojo control was handling it badly,
        // by hiding both the label and the <img> tag. So instead, doing a 
        // server-side prevent label, by not setting the client-side showLabel=false,
        // by not outputting the label as nested text and by using the label as the image alt.
        boolean doServerSidePreventLabel = (hasImage) && (!showLabel);
        
        if(!showLabel && !doServerSidePreventLabel ) {
            //writer.writeAttribute("showLabel","false",null); // $NON-NLS-1$ $NON-NLS-2$
            attrs.put("showLabel","false");//$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        
        if(StringUtil.isNotEmpty(styleClass)) {
            // Which one to use?
            //writer.writeAttribute("class",styleClass,null);
            
        	//writer.writeAttribute("iconClass",styleClass,null); // $NON-NLS-1$
            attrs.put("iconClass",styleClass); // $NON-NLS-1$
        }

        if(hasLink) {
            if (StringUtil.isNotEmpty(onclick)) {
                //writer.writeAttribute("onClick", onclick, null); // $NON-NLS-1$
                attrs.put("onClick",onclick); // $NON-NLS-1$
            } else if (StringUtil.isNotEmpty(href)) {
                StringBuilder b = new StringBuilder();
                b.append("window.location.href="); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(b, RenderUtil.formatLinkRef(context,href));
                //writer.writeAttribute("onClick", b.toString(), null); // $NON-NLS-1$
                attrs.put("onClick",b.toString()); // $NON-NLS-1$
            }           
        } else {
//          if(!leaf) {
//              writer.writeAttribute("onClick", "alert('clicked')", null);
//          }
        }

        if(hasImage) {
            DojoRendererUtil.writeDojoHtmlAttributes(context, getComponent(), dojoType, attrs);

            // In case of an image, we insert the image and the text within the button
            writer.startElement("img",null); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(image)) {
                image = HtmlRendererUtil.getImageURL(context, image);
                writer.writeAttribute("src",image,null); // $NON-NLS-1$
                String imageAlt = tree.getNode().getImageAlt();
                if( doServerSidePreventLabel && StringUtil.isEmpty(imageAlt) ){
                    imageAlt = label;
                }
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
            if(StringUtil.isNotEmpty(label) && !doServerSidePreventLabel ) {
                writer.writeText(label, null);
            }
        } else {
            // When there isn't any image, then the label is simply an attribute
            if(StringUtil.isNotEmpty(label)) {
                //writer.writeAttribute("label",label,null); // $NON-NLS-1$
                attrs.put("label",label); // $NON-NLS-1$
            }
            DojoRendererUtil.writeDojoHtmlAttributes(context, getComponent(), dojoType, attrs);
        }

        
        renderChildren(context, writer, tree);
        
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }

    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            super.renderChildren(context, writer, tree);
        } else {
            if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
                DojoMenuRenderer r = new DojoMenuRenderer();
                String clientId = tree.getClientId(context,"bt",1); // $NON-NLS-1$

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