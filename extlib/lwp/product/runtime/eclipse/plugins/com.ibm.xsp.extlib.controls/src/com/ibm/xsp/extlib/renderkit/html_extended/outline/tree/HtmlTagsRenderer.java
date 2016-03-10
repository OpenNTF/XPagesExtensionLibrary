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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.JSUtil;

public abstract class HtmlTagsRenderer extends AbstractTreeRenderer {

    private static final long serialVersionUID = 1L;
    
    private UIComponent component;
    
    private String itemStyle;
    private String itemStyleClass;
    private String containerStyle;
    private String containerStyleClass;
    private String selectedItemStyle;
    private String selectedItemStyleClass;
    private String disabledItemStyle;
    private String disabledItemStyleClass;
    
    public HtmlTagsRenderer() {
    }

    public HtmlTagsRenderer(UIComponent component) {
        this.component = component;
    }
    
    public UIComponent getContainerComponent() {
    	return component;
    }
    
    public String getItemStyle() {
        if (null != this.itemStyle) {
            return this.itemStyle;
        }
        ValueBinding _vb = getValueBinding("itemStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }
    
    public String getItemStyleClass() {
        if (null != this.itemStyleClass) {
            return this.itemStyleClass;
        }
        ValueBinding _vb = getValueBinding("itemStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setItemStyleClass(String itemStyleClass) {
        this.itemStyleClass = itemStyleClass;
    }
    
    public String getContainerStyle() {
        if (null != this.containerStyle) {
            return this.containerStyle;
        }
        ValueBinding _vb = getValueBinding("containerStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        if(component!=null) {
            return (String)component.getAttributes().get("style"); // $NON-NLS-1$
        }
        return null;
    }
    public void setContainerStyle(String containerStyle) {
        this.containerStyle = containerStyle;
    }
    
    public String getContainerStyleClass() {
        if (null != this.containerStyleClass) {
            return this.containerStyleClass;
        }
        ValueBinding _vb = getValueBinding("containerStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        if(component!=null) {
            return (String)component.getAttributes().get("styleClass"); // $NON-NLS-1$
        }
        return null;
    }
    public void setContainerStyleClass(String containerStyleClass) {
        this.containerStyleClass = containerStyleClass;
    }
    
    public String getSelectedItemStyle() {
        if (null != this.selectedItemStyle) {
            return this.selectedItemStyle;
        }
        ValueBinding _vb = getValueBinding("selectedItemStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setSelectedItemStyle(String selectedItemStyle) {
        this.selectedItemStyle = selectedItemStyle;
    }
    
    public String getSelectedItemStyleClass() {
        if (null != this.selectedItemStyleClass) {
            return this.selectedItemStyleClass;
        }
        ValueBinding _vb = getValueBinding("selectedItemStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setSelectedItemStyleClass(String selectedItemStyleClass) {
        this.selectedItemStyleClass = selectedItemStyleClass;
    }
    
    public String getDisabledItemStyle() {
        if (null != this.disabledItemStyle) {
            return this.disabledItemStyle;
        }
        ValueBinding _vb = getValueBinding("disabledItemStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setDisabledItemStyle(String disabledItemStyle) {
        this.disabledItemStyle = disabledItemStyle;
    }
    
    public String getDisabledItemStyleClass() {
        if (null != this.disabledItemStyleClass) {
            return this.disabledItemStyleClass;
        }
        ValueBinding _vb = getValueBinding("disabledItemStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setDisabledItemStyleClass(String disabledItemStyleClass) {
        this.disabledItemStyleClass = disabledItemStyleClass;
    }

    @Override
    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        startRenderContainer(context, writer, tree);
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        endRenderContainer(context, writer, tree);
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        renderEntryItem(context, writer, tree);
    }
    
    
    // ========================================================================
    //  Render HTML tags 
    // ========================================================================

    protected String getContainerTag() {
        return null;
    }
        
    protected String getItemTag() {
        return null;
    }
    
    
    protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        String containerTag = getContainerTag();
        if(StringUtil.isNotEmpty(containerTag)) {
            writer.startElement(containerTag,null);
            String style = null;
            String styleClass = null;
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
                    styleClass = (String)c.getAttributes().get("styleClass");//$NON-NLS-1$
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
            renderContainerRole(context, writer, tree);
            JSUtil.writeln(writer);
        }
    }

    protected void renderContainerRole(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Overridden in subclasses
    }
    
    protected void endRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        String containerTag = getContainerTag();
        if(StringUtil.isNotEmpty(containerTag)) {
            writer.endElement(containerTag);
            JSUtil.writeln(writer);
        }
    }
    
    protected void renderEntryItem(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Check for a separator node
        // TODO: How to render it?
        int type = tree.getNode().getType();
        if(type==ITreeNode.NODE_SEPARATOR) {
            renderEntrySeparator(context, writer, tree);
        } else {
            renderEntryNode(context, writer, tree);
        }
        
    }
    protected void renderEntrySeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled(); 
        boolean selected = tree.getNode().isSelected();

        writer.startElement("li", null); // $NON-NLS-1$
        
        writer.startElement("div", null); // $NON-NLS-1$
        String style = getItemStyle(tree,enabled,selected);
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = getItemStyleClass(tree,enabled,selected);
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        writer.endElement("div"); // $NON-NLS-1$
        
        writer.endElement("li"); // $NON-NLS-1$
    }
    protected void renderEntryNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled(); 
        boolean selected = tree.getNode().isSelected();

        String itemTag = getItemTag();
        if(StringUtil.isNotEmpty(itemTag)) {
            writer.startElement(itemTag,null);
            String style = getItemStyle(tree,enabled,selected);
            if(StringUtil.isNotEmpty(style)) {
                writer.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            String styleClass = getItemStyleClass(tree,enabled,selected);
            if(StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            
            if(enabled) {
                String href = tree.getNode().getHref();
                if(StringUtil.isEmpty(href)) {
                    String role = getItemRole(tree,enabled,selected);
                    if(StringUtil.isNotEmpty(role)) {
                        writer.writeAttribute("role",role,null); // $NON-NLS-1$
                    }
                }
            }
            
            String title = getItemTitle(tree,enabled,selected);
            if(StringUtil.isNotEmpty(title)) {
                writer.writeAttribute("title",title,null); // $NON-NLS-1$
            }
        }

        boolean separate = isChildrenSeparate();

        // render the item content
        renderEntryItemContent(context, writer, tree, enabled, selected);
        
        // and its children
        if(!separate) {
            renderChildren(context, writer, tree);
        }
        
        if(StringUtil.isNotEmpty(itemTag)) {
            writer.endElement(itemTag);
        }
        JSUtil.writeln(writer);
        
        // and its children
        if(separate && tree.getNodeContext().hasChildren()) {
            if(StringUtil.isNotEmpty(itemTag)) {
                writer.startElement(itemTag,null);
                renderEntryNodeChildAttributes(context, writer, tree, enabled, selected);
            }
            renderChildren(context, writer, tree);
            if(StringUtil.isNotEmpty(itemTag)) {
                writer.endElement(itemTag);
            }
            JSUtil.writeln(writer);
        }
    }
    protected void renderEntryNodeChildAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
    }
    protected boolean isChildrenSeparate() {
        return false;
    }
    protected boolean alwaysRenderItemLink(TreeContextImpl tree, boolean enabled, boolean selected) {
        return true;
    }
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean hasLink = false;
        boolean alwaysRenderLinks = alwaysRenderItemLink(tree, enabled, selected);
        if(enabled) {
            String href = tree.getNode().getHref();
            if(StringUtil.isNotEmpty(href)) {
                writer.startElement("a",null);
                String role = getItemRole(tree, enabled, selected);
                if (StringUtil.isNotEmpty(role)) {
                    writer.writeAttribute("role", role, null); // $NON-NLS-1$
                }
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
            writer.startElement("a",null);
            hasLink = true;
        }
        if(hasLink) {
            renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
        }

        String image = tree.getNode().getImage();
        boolean hasImage = StringUtil.isNotEmpty(image);
        if(hasImage) {
            writer.startElement("img",null); // $NON-NLS-1$
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
            writer.endElement("img"); // $NON-NLS-1$
        }
        
        // Generate a regular node
        renderEntryItemLabel(context, writer, tree, enabled, selected);
        
        // Render a popup image, if any
        writePopupImage(context, writer, tree);

        if(hasLink || alwaysRenderLinks) {
            writer.endElement("a");
            tree.markCurrentAsAction();
        }
    }
    
    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.writeAttribute("style","text-decoration:none",null); // $NON-NLS-1$ $NON-NLS-2$
    }
    protected void writePopupImage(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }
    
    //Adding this method so it can be over-ridden in subclasses. See com.ibm.xsp.theme.twitter.bootstrap.MenuRenderer
    protected void renderEntryItemLabel(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
    	String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            writer.writeText(label, "label"); // $NON-NLS-1$
        }
    }
    
    // ===================================================================
    // Styles to be used
    // ===================================================================
    
    @Override
    protected String getContainerStyle(TreeContextImpl node) {
        String s = getContainerStyle();
        if(s!=null) {
            return s;
        }
        s = getItemStyle();
        if(s!=null) {
            return s;
        }
        return super.getContainerStyle(node);
    }

    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        String s = getContainerStyleClass();
        if(s!=null) {
            return s;
        }
        s = getItemStyleClass();
        if(s!=null) {
            return s;
        }
        return super.getContainerStyleClass(node);
    }
    
    @Override
    protected String getItemStyle(TreeContextImpl tree, boolean enabled, boolean selected) {
        if(selected) {
            String s = getSelectedItemStyle();
            if(s!=null) {
                return s;
            }
        }
        if(!enabled) {
            String s = getDisabledItemStyle();
            if(s!=null) {
                return s;
            }
        }
        String s = getItemStyle();
        if(s!=null) {
            return s;
        }
        return super.getItemStyle(tree,enabled,selected);
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        if(selected) {
            String s = getSelectedItemStyleClass();
            if(s!=null) {
                return s;
            }
        }
        if(!enabled) {
            String s = getDisabledItemStyleClass();
            if(s!=null) {
                return s;
            }
        }
        String s = getItemStyleClass();
        if(s!=null) {
            return s;
        }
        return super.getItemStyleClass(tree,enabled,selected);
    }
    
    @Override
    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
        return tree.getNode().getRole();
    }
    
    @Override
    protected String getItemTitle(TreeContextImpl tree, boolean enabled, boolean selected) {
        return tree.getNode().getTitle();
    }

    
    // ===================================================================
    // State management
    // ===================================================================
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.itemStyle = (String)_values[1];
        this.itemStyleClass = (String)_values[2];
        this.containerStyle = (String)_values[3];
        this.containerStyleClass = (String)_values[4];
        this.selectedItemStyle = (String)_values[5];
        this.selectedItemStyleClass = (String)_values[6];
        this.disabledItemStyle = (String)_values[7];
        this.disabledItemStyleClass = (String)_values[8];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[9];
        _values[0] = super.saveState(_context);
        _values[1] = itemStyle;
        _values[2] = itemStyleClass;
        _values[3] = containerStyle;
        _values[4] = containerStyleClass;
        _values[5] = selectedItemStyle;
        _values[6] = selectedItemStyleClass;
        _values[7] = disabledItemStyle;
        _values[8] = disabledItemStyleClass;
        return _values;
    }
}