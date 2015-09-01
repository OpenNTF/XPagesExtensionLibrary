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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.theme.bootstrap.resources.Resources;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.outline.UIOutlineNavigator;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.TypedUtil;

public class MenuRenderer extends HtmlListRenderer {
    
    public static final int TYPE_PILL   = 0;
    public static final int TYPE_LIST   = 1;

    private static final long serialVersionUID = 1L;

    protected static final int PROP_MENU_SELECTED   = 6;
    protected static final int PROP_MENU_EXPANDED   = 7;
    protected static final int PROP_MENU_COLLAPSED  = 8;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_MENU_SELECTED:    return "active"; // $NON-NLS-1$
            case PROP_MENU_EXPANDED:    return Resources.get().getIconClass("minus-sign"); // $NON-NLS-1$
            case PROP_MENU_COLLAPSED:   return Resources.get().getIconClass("plus-sign"); // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    private boolean expandable;
    private String expandEffect;
    private int expandLevel;
    
    private int type;

    public MenuRenderer() {
    }

    public MenuRenderer(UIComponent component, int type) {
        super(component);
        this.type = type;
    }

    @Override
    protected boolean renderCollapsedChildren() throws IOException {
        // We render the children if the menu is exandable
        return isExpandable();
    }
    
    public boolean isExpandable() {
        return expandable;
    }
    
    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
    
    public String getExpandEffect() {
        return expandEffect;
    }
    
    public void setExpandEffect(String expandEffect) {
        this.expandEffect = expandEffect;
    }
    
    public int getExpandLevel() {
        return expandLevel;
    }
    
    public void setExpandLevel(int expandLevel) {
        this.expandLevel = expandLevel;
    }
    
    /*@Override
    protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        if(tree.getDepth()>1) {
            writer.startElement("div", null); // $NON-NLS-1$
            writer.writeAttribute("style", "margin-top: 0",null); // $NON-NLS-1$ $NON-NLS-2$
        }
        super.startRenderContainer(context, writer, tree);
    }
    
    @Override
    protected void endRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        super.endRenderContainer(context, writer, tree);
        if(tree.getDepth()>1) {
            writer.endElement("div"); // $NON-NLS-1$
        }
    }
    */
    @Override
    protected boolean alwaysRenderItemLink(TreeContextImpl tree, boolean enabled, boolean selected) {
        //return true;
        return tree.getNode().getType()!=ITreeNode.NODE_CONTAINER;
    }
    
    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        if(type==TYPE_LIST) {
            return "nav nav-list"; // $NON-NLS-1$
        }
        return "nav nav-pills nav-stacked"; // $NON-NLS-1$
    }

//  protected String getContainerStyle(TreeContextImpl node) {
//        //return "font-weight: 400;"; // when nested within nav-header
//        return null;
//    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String clazz=null;
        if(tree.getNode().getType()==ITreeNode.NODE_CONTAINER) {
            //clazz = "nav-header";
        } else if(tree.getNode().getType()==ITreeNode.NODE_SEPARATOR) {
            clazz = "divider"; // $NON-NLS-1$
        }
        if(!enabled) {
            clazz = ExtLibUtil.concatStyleClasses(clazz, "disabled"); // $NON-NLS-1$
        }
        if(selected) {
            clazz = ExtLibUtil.concatStyleClasses(clazz, "active"); // $NON-NLS-1$
        }
        return clazz;
    }
    

    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Add the JS support if necessary
        if(isExpandable()) {
            UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
            rootEx.setDojoTheme(true);
            ExtLibResources.addEncodeResource(rootEx, Resources.bootstrapNavigator);
            // Specific dojo effects
            String effect = getExpandEffect();
            if(StringUtil.isNotEmpty(effect)) {
                rootEx.addEncodeResource(ExtLibResources.dojoFx);
                ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dojoFx);
            }
        }
        super.preRenderTree(context, writer, tree);
    }
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean section = tree.getNode().getType()!=ITreeNode.NODE_LEAF;
        if(section && isExpandable()) {
            if(selected) {
                writer.writeAttribute("class", "selected",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            
            // Check if the node has a link within
            //With no link within, we require padding on the li tag
            if(!hasLink(tree)) {
            //    writer.writeAttribute("style", "padding: 10px 0px;",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            
            //Containing div element with icon class
            writer.startElement("div", null); // $NON-NLS-1$
            
            int depth = tree.getDepth()-2;

            UIComponent uiTree = tree.getComponent();
            boolean keepState = false;
            if (uiTree instanceof UIOutlineNavigator) {
                keepState = ((UIOutlineNavigator)uiTree).isKeepState();
            }
            String nodeId = tree.getClientId(context, "node", tree.getDepth());//$NON-NLS-1$
            
            boolean userExpanded = false;
            boolean userCollapsed = false;
            if (keepState) {
                Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
                String value = params.get(nodeId);
                if (!StringUtil.isEmpty(value)) {
                    if (value.equals("1")) { // $NON-NLS-1$
                        userExpanded = true;
                    }
                    else if (value.equals("0")) { // $NON-NLS-1$
                        userCollapsed = true;
                    }
                }
            }
            String styleClass = null;
            if (userExpanded || userCollapsed) {
                if (userExpanded) {
                    styleClass = (String)getProperty(PROP_MENU_EXPANDED);
                }
                else {
                    styleClass = (String)getProperty(PROP_MENU_COLLAPSED);
                    tree.getNodeContext().setHidden(true);
                }
            }
            else {
                boolean expanded = depth<expandLevel && tree.getNode().isExpanded(); 
                if(expanded) {
                    styleClass = (String)getProperty(PROP_MENU_EXPANDED);
                } else {
                    styleClass = (String)getProperty(PROP_MENU_COLLAPSED);
                    tree.getNodeContext().setHidden(true);
                }
            }
            writer.writeAttribute("class", ExtLibUtil.concatStyleClasses(styleClass,"navigator-twisty"),null); // $NON-NLS-2$ $NON-NLS-1$
            writer.writeAttribute("role", "button",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("onclick", "javascript:XSP.xbtMenuSwap(event,'" + getExpandEffect() + "','" + nodeId + "', '" // $NON-NLS-1$ $NON-NLS-2$
                + getProperty(PROP_MENU_COLLAPSED) + "', '" + getProperty(PROP_MENU_EXPANDED) + "')", null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$

            writer.startElement("img", null); // $NON-NLS-1$
            String bgif = Resources.get().BLANK_GIF;
            if(StringUtil.isNotEmpty(bgif)) {
                writer.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
            }
            
            writer.startElement("span", null); //$NON-NLS-1$
            writer.writeAttribute("class", "lotusAltText",null); //$NON-NLS-1$ //$NON-NLS-2$
            writer.writeText("&#x25ba;", null); //$NON-NLS-1$
            writer.writeText("&#x25bc;", null); //$NON-NLS-1$
            writer.endElement("span"); //$NON-NLS-1$
            writer.endElement("img"); //$NON-NLS-1$
            writer.endElement("div"); //$NON-NLS-1$
            
            // Preserve user's Expanded/Collapsed state
            if (keepState) {
                writer.startElement("input", uiTree); // $NON-NLS-1$
                writer.writeAttribute("type", "hidden", null); // $NON-NLS-1$ $NON-NLS-2$
                writer.writeAttribute("id", nodeId, "id"); //$NON-NLS-1$ $NON-NLS-2$
                writer.writeAttribute("name", nodeId, "name"); // $NON-NLS-1$ $NON-NLS-2$
                if (userExpanded) {
                    writer.writeAttribute("value", "1", "value"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                }
                else if (userCollapsed) {
                    writer.writeAttribute("value", "0", "value"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                }
                else {
                    writer.writeAttribute("value", "", "value"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                }
                writer.endElement("input"); //$NON-NLS-1$
            }
        }
        if(section) {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        } else {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        }
    }
    
    @Override
    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean section = tree.getNode().getType()!=ITreeNode.NODE_LEAF;
        if(section) {
            writer.writeAttribute("style","position: static; text-decoration:none; padding-left: 0",null); // $NON-NLS-1$ $NON-NLS-2$
        } else {
            super.renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
        }
    }
    @Override
    protected void renderEntryItemLabel(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            boolean addDiv = tree.getNode().getType()!=ITreeNode.NODE_LEAF && isExpandable() && !hasLink(tree);
            if(addDiv) {
                writer.startElement("div",  tree.getComponent()); // $NON-NLS-1$
                String divStyle = "";
                divStyle = "padding:10px;cursor:default;"; // $NON-NLS-1$
                writer.writeAttribute("style", divStyle, null); // $NON-NLS-1$
            }
            writer.writeText(label, "label"); // $NON-NLS-1$
            if(addDiv) {
                writer.endElement("div"); // $NON-NLS-1$
            }
        }
    }
    @Override
    protected boolean isChildrenSeparate() {
        // We need the children to be generated in a separate <li>, else the lotusSelected class
        // applies to the entire hierarchy
        return true;
    }
    protected boolean hasLink(TreeContextImpl tree) {
        String href = tree.getNode().getHref();
        if(StringUtil.isNotEmpty(href)) {
            return true;
        } else {
            String onclick = findNodeOnClick(tree);
            if(StringUtil.isNotEmpty(onclick)) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void renderEntryNodeChildAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        if(tree.getNodeContext().isHidden()) {
            writer.writeAttribute("style","display:none",null); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
    
    @Override
    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
        if(tree.getNode().getType()==ITreeNode.NODE_LEAF) {
            return "menuitem"; // $NON-NLS-1$
        }
        return null;
    }
}