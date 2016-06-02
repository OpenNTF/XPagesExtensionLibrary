/*
 * © Copyright IBM Corp. 2014, 2015
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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.outline.UIOutlineNavigator;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.theme.bootstrap.util.BootstrapUtil;
import com.ibm.xsp.util.JSUtil;
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
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String clazz = super.getItemStyleClass(tree, enabled, selected);
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
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibExtLib);
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
            boolean expanded = depth<expandLevel && tree.getNode().isExpanded(); 
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
            
            String twistyLabel = "";
            String twistyTitle = "";
            String styleClass  = "";
            
            String expandedStyleClass  = (String)getProperty(PROP_MENU_EXPANDED);
            String collapsedStyleClass = (String)getProperty(PROP_MENU_COLLAPSED);
            String expandedLabel  = com.ibm.xsp.extlib.controls.ResourceHandler.getString("MenuRenderer.Expandedsection"); // $NON-NLS-1$
            String collapsedLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("MenuRenderer.Collapsedsection"); // $NON-NLS-1$
            String expandedTitle  = com.ibm.xsp.extlib.controls.ResourceHandler.getString("WidgetContainerRenderer.clicktocollapsethesection"); // $NON-NLS-1$
            String collapsedTitle = com.ibm.xsp.extlib.controls.ResourceHandler.getString("WidgetContainerRenderer.clicktoexpandthesection"); // $NON-NLS-1$
            
            if (userExpanded || expanded) {
                styleClass = expandedStyleClass;
                // "Expanded section"
                twistyLabel = expandedLabel; 
                // "Click to collapse the section"
                twistyTitle = expandedTitle;
            }
            else {
                styleClass = collapsedStyleClass;
                tree.getNodeContext().setHidden(true);
                // "Collapsed section"
                twistyLabel = collapsedLabel;
                // "Click to expand the section"
                twistyTitle = collapsedTitle;
            }
            
            writer.writeAttribute("class", ExtLibUtil.concatStyleClasses(styleClass,"navigator-twisty"),null); // $NON-NLS-2$ $NON-NLS-1$
            writer.writeAttribute("role", "button",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("tabindex","0",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("aria-label", twistyLabel, null); // $NON-NLS-1$
            writer.writeAttribute("title", twistyTitle, null); // $NON-NLS-1$
            
            // Build JS for onclick event to swap collapse/expand properties (see Navigator.js)
            StringBuilder onclick = new StringBuilder();
            onclick.append("return XSP.xbtMenuSwap(event,"); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, getExpandEffect());
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, nodeId); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, collapsedStyleClass); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, expandedStyleClass); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, collapsedLabel); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, expandedLabel); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, collapsedTitle); // $NON-NLS-1$
            onclick.append(", "); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(onclick, expandedTitle); // $NON-NLS-1$
            onclick.append(");"); // $NON-NLS-1$
            writer.writeAttribute("onclick", onclick.toString(), null); // $NON-NLS-1$
            
            // Build JS for onkeyup event to swap collapse/expand properties
            // when enter or space are pressed (see ExtLib.js)
            StringBuilder onkeydown = new StringBuilder();
            onkeydown.append("var xbtIsTriggerKey = XSP.xbtIsTriggerKey(event);"); // $NON-NLS-1$
            onkeydown.append("if(xbtIsTriggerKey){"); // $NON-NLS-1$
            onkeydown.append("event.preventDefault();"); // $NON-NLS-1$
            onkeydown.append("event.stopPropagation();"); // $NON-NLS-1$
            onkeydown.append(onclick);
            onkeydown.append("}"); // $NON-NLS-1$
            writer.writeAttribute("onkeydown", onkeydown.toString(), null); // $NON-NLS-1$
            
            BootstrapUtil.renderIconTextForA11Y(writer, twistyLabel);
            
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
        writer.writeAttribute("tabindex","0",null); // $NON-NLS-1$ $NON-NLS-2$
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
            return "treeitem"; // $NON-NLS-1$
        }
        return null;
    }
    @Override
    protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        String containerTag = getContainerTag();
        if (StringUtil.isNotEmpty(containerTag)) {
            writer.startElement(containerTag, null);
            writer.writeAttribute("role", "tree", null); // $NON-NLS-1$ $NON-NLS-2$
            // aria label
            UIComponent component = tree.getComponent();
            UIOutlineNavigator tcomponent = component instanceof UIOutlineNavigator ? (UIOutlineNavigator)component : null;
            // "Navigation menu"
            String ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("MenuRenderer.Navigationmenu"); // $NON-NLS-1$
            if (tcomponent != null) {
                ariaLabel = tcomponent.getAriaLabel();
            }
            if (StringUtil.isNotEmpty(ariaLabel)) {
                writer.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
            }
            String style = null;
            String styleClass = null;
            if (tree.getDepth() == 1) {
                // ac: LHEY92PFY3 - A11Y | RPT | xc:viewMenu : ID values must be unique
                if (!tree.isOuterTagEmitted()) {
                    String id = getClientId(context, tree);
                    if (StringUtil.isNotEmpty(id)) {
                        writer.writeAttribute("id", id, null); // $NON-NLS-1$
                    }
                }
                UIComponent c = tree.getComponent();
                if (c != null) {
                    style = (String) c.getAttributes().get("style"); //$NON-NLS-1$
                    styleClass = (String) c.getAttributes().get("styleClass");//$NON-NLS-1$
                }
            }
            style = ExtLibUtil.concatStyles(style, getContainerStyle(tree));
            if (StringUtil.isNotEmpty(style)) {
                writer.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            styleClass = ExtLibUtil.concatStyleClasses(styleClass, getContainerStyleClass(tree));
            if (StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            JSUtil.writeln(writer);
        }
    }
}