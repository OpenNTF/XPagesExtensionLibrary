/*
 * © Copyright IBM Corp. 2012, 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUITitleBarTabsRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.JSUtil;

public class OneUIv302TitleBarTabsRenderer extends OneUITitleBarTabsRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String getContainerRole(TreeContextImpl node){
		return "toolbar"; //$NON-NLS-1$
	}

	@Override
	protected void startRenderContainer(FacesContext context,ResponseWriter writer, TreeContextImpl tree) throws IOException {
		 String containerTag = getContainerTag();
	        if(StringUtil.isNotEmpty(containerTag)) {
	            writer.startElement(containerTag,null);
	            String style = null;
	            String styleClass = null;
	            String role = null;
	            if(tree.getDepth()==1) {
	            	// ac: LHEY92PFY3 - A11Y | RPT | xc:viewMenu : ID values must be unique
	            	if (!tree.isOuterTagEmitted()) {
		                String id = getClientId(context,tree);
		                if(StringUtil.isNotEmpty(id)) {
		                    writer.writeAttribute("id",id,null); // $NON-NLS-1$
		                }
	            	}
	                UIComponent c = tree.getComponent();
	                if(c!=null) {
	                    style = (String)c.getAttributes().get("style"); //$NON-NLS-1$
	                    styleClass = (String)c.getAttributes().get("styleClass"); //$NON-NLS-1$
	                }
	            }
	            style = ExtLibUtil.concatStyles(style,getContainerStyle(tree));
	            if(StringUtil.isNotEmpty(style)) {
	                writer.writeAttribute("style",style,null); // $NON-NLS-1$
	            }
	            styleClass = ExtLibUtil.concatStyleClasses(styleClass,getContainerStyleClass(tree));
	            if(StringUtil.isNotEmpty(styleClass)) {
	                writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
	            }
	            role = getContainerRole(tree);
	            writer.writeAttribute("role", role, null); //$NON-NLS-1$
	            JSUtil.writeln(writer);
	        }
	}

	 @Override
	    protected String getContainerStyleClass(TreeContextImpl node) {
	        UIComponent component = node.getComponent();
	        String titleBarName = null;
	        if (component instanceof UIApplicationLayout) {
	        	Object configuration = ((UIApplicationLayout)component).getConfiguration();
	        	if (configuration instanceof BasicApplicationConfigurationImpl) {
	        		titleBarName = 
	        			((BasicApplicationConfigurationImpl)configuration).getTitleBarName();
	        		if( StringUtil.isNotEmpty(titleBarName) ){
	        			return "lotusNavTabs"; //$NON-NLS-1$
	        		}
	        	}
	        }    	
	        return "lotusNavTabs"; // $NON-NLS-1$
	    }
	    
	 @Override
	 protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
	        boolean hasLink = false;
	        boolean alwaysRenderLinks = alwaysRenderItemLink(tree, enabled, selected);
	        writer.startElement("div", null); // $NON-NLS-1$
	        writer.writeAttribute("class", "lotusTabWrapper", null); //$NON-NLS-1$ //$NON-NLS-2$
	        if(enabled) {
	            String href = tree.getNode().getHref();
	            if(StringUtil.isNotEmpty(href)) {
	                writer.startElement("a",null); //$NON-NLS-1$
	                RenderUtil.writeLinkAttribute(context,writer,href);
	                hasLink = true;
	            } else {
	                String onclick = findNodeOnClick(tree);
	                if(StringUtil.isNotEmpty(onclick)) {
	                    writer.startElement("a",null);
	                    writer.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
	                    writer.writeAttribute("onclick", "javascript:"+onclick, null); // $NON-NLS-1$ $NON-NLS-2$
	                    hasLink = true;
	                }
	            }
	        }
	        if(!hasLink && alwaysRenderLinks) {
	            // Render an empty link...
	            writer.startElement("a",null); //$NON-NLS-1$
	            hasLink = true;
	        }
	        if(hasLink) {
	            renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
	        }
	        writer.startElement("span",null); //$NON-NLS-1$
            writer.writeAttribute("class","lotusTabInner", null); //$NON-NLS-1$ //$NON-NLS-2$

	        String image = tree.getNode().getImage();
	        boolean hasImage = StringUtil.isNotEmpty(image);
	        if(hasImage) {
                writer.startElement("img",null); // $NON-NLS-1$
                writer.writeAttribute("class", "lotusIcon", null); //$NON-NLS-1$ //$NON-NLS-2$
                image = HtmlRendererUtil.getImageURL(context, image);
                writer.writeAttribute("src",image,null); // $NON-NLS-1$
                String imageAlt = tree.getNode().getImageAlt();
                if (StringUtil.isNotEmpty(imageAlt)) {
                    writer.writeAttribute("alt",imageAlt,null); // $NON-NLS-1$
                }else{
                    writer.writeAttribute("alt","",null); // $NON-NLS-1$
                }
                String imageHeight = tree.getNode().getImageHeight();
                if (StringUtil.isNotEmpty(imageHeight)) {
                    writer.writeAttribute("height",imageHeight,null); // $NON-NLS-1$
                }
                String imageWidth = tree.getNode().getImageWidth();
                if (StringUtil.isNotEmpty(imageWidth)) {
                    writer.writeAttribute("width",imageWidth,null); // $NON-NLS-1$
                }
                writer.endElement("img"); // $NON-NLS-1$
            }
	        
	        // Generate a regular node
	        String label = tree.getNode().getLabel();
	        if(StringUtil.isNotEmpty(label)) {
	        	writer.writeText(label, "label"); // $NON-NLS-1$
	        }
	        writer.endElement("span"); //$NON-NLS-1$

	        if(hasLink || alwaysRenderLinks) {
	            writer.endElement("a"); //$NON-NLS-1$
	            tree.markCurrentAsAction();
	        }
	        writer.endElement("div"); // $NON-NLS-1$
	    }

	@Override
	protected void renderEntryItemLinkAttributes(FacesContext context,ResponseWriter writer, TreeContextImpl tree, boolean enabled,boolean selected) throws IOException {
		writer.writeAttribute("class", "lotusTab",null); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("role","button", null); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
        return "presentation"; //$NON-NLS-1$
    }
	

}
