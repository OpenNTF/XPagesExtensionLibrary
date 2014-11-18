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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.outline.UIOutlineNavigator;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

public class MenuRenderer extends HtmlListRenderer {
    
    protected static final int PROP_MENU_SECTION	= 0;
    protected static final int PROP_MENU_SUBSECTION	= 1;
    protected static final int PROP_MENU_MENU	= 2;
    protected static final int PROP_MENU_BOTTOMCORNER	= 3;
    protected static final int PROP_MENU_INNER	= 4;
    protected static final int PROP_MENU_HEADER	= 5;
    protected static final int PROP_MENU_SELECTED	= 6;
    protected static final int PROP_MENU_EXPANDED	= 7;
    protected static final int PROP_MENU_COLLAPSED	= 8;
    protected static final int PROP_MENU_SECTION_HEADING	= 9;
    protected static final int PROP_MENU_SECTION_LINK_TITLE = 10;
    
    private static final long serialVersionUID = 1L;

    private boolean expandable;
    private String expandEffect;
    private int expandLevel;
    
    public MenuRenderer() {
    }

    public MenuRenderer(UIComponent component) {
        super(component);
    }

    @Override
    protected boolean renderCollapsedChildren() throws IOException {
        // We render the children if the menu is exandable
        return isExpandable();
    }

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_MENU_SECTION: return "lotusMenuSection"; // $NON-NLS-1$
            case PROP_MENU_SUBSECTION: return "lotusMenuSubsection"; // $NON-NLS-1$
            case PROP_MENU_MENU: return "lotusMenu"; // $NON-NLS-1$
            case PROP_MENU_BOTTOMCORNER: return "lotusBottomCorner"; // $NON-NLS-1$
            case PROP_MENU_INNER: return "lotusInner"; // $NON-NLS-1$
            case PROP_MENU_HEADER: return "lotusMenuHeader"; // $NON-NLS-1$
            case PROP_MENU_SELECTED: return "lotusSelected"; // $NON-NLS-1$
            case PROP_MENU_EXPANDED: return "lotusSprite lotusArrow lotusTwistyOpenMenu"; // $NON-NLS-1$
            case PROP_MENU_COLLAPSED: return "lotusSprite lotusArrow lotusTwistyClosedMenu"; // $NON-NLS-1$
            case PROP_MENU_SECTION_HEADING: return "lotusHeading"; // $NON-NLS-1$
            case PROP_MENU_SECTION_LINK_TITLE: return "Click to expand or collapse this section"; // $NLS-MenuRenderer.Clicktoexpandorcollapsethissectio-1$
        }
        return super.getProperty(prop);
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
    protected void startRenderContainer(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        if(tree.getDepth()>1) {
            writer.startElement("div", null); // $NON-NLS-1$
            writer.writeAttribute("class", (String)getProperty(PROP_MENU_SUBSECTION), null); // $NON-NLS-1$
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
    
    @Override
    protected void renderEntrySeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled(); 
        boolean selected = tree.getNode().isSelected();

        writer.startElement("li", null); // $NON-NLS-1$
        
        writer.startElement("div", null); // $NON-NLS-1$
        String style = getItemStyle(tree,enabled,selected);
        if(StringUtil.isNotEmpty(style)) {
            writer.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_MENU_SECTION),getItemStyleClass(tree,enabled,selected));
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        writer.endElement("div"); // $NON-NLS-1$
        
        writer.endElement("li"); // $NON-NLS-1$
    }
    
    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Add the JS support if necessary
//        if(isExpandable()) {
//            UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
//            rootEx.setDojoTheme(true);
//            ExtLibResources.addEncodeResource(rootEx, OneUIResources.oneUINavigator);
//            // Specific dojo effects
//            String effect = getExpandEffect();
//            if(StringUtil.isNotEmpty(effect)) {
//                rootEx.addEncodeResource(ExtLibResources.dojoFx);
//                ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dojoFx);
//            }
//        }
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_MENU),null); // $NON-NLS-1$
        // Accessibility
        writer.writeAttribute("role", "tree",null); // $NON-NLS-1$ $NON-NLS-2$

        UIComponent component = tree.getComponent();
        UIOutlineNavigator tcomponent = component instanceof UIOutlineNavigator ? (UIOutlineNavigator)component : null;

        // aria-label
        String ariaLabel = "";
        if (tcomponent != null) {
            ariaLabel = tcomponent.getAriaLabel();
        }
        if (StringUtil.isNotEmpty(ariaLabel)) {
            writer.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
        }

        writeClientIdIfNecessary(context, writer, tree);
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_BOTTOMCORNER),null); // $NON-NLS-1$
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("class", (String)getProperty(PROP_MENU_INNER),null); // $NON-NLS-1$ $NON-NLS-2$
        if(ThemeUtil.isOneUIVersionAtLeast(context, 2, 1)) {
        	// Should actually be for OneUI 3.0
            writer.startElement("header", null); // $NON-NLS-1$
            writer.writeAttribute("class", (String)getProperty(PROP_MENU_HEADER),null); // $NON-NLS-1$ $NON-NLS-2$
        }
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        if(ThemeUtil.isOneUIVersionAtLeast(context, 2, 1)) {
            writer.endElement("header"); // $NON-NLS-1$
        }
        writer.endElement("div"); // $NON-NLS-1$
        writer.endElement("div"); // $NON-NLS-1$
        writer.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean section = tree.getNode().getType()!=ITreeNode.NODE_LEAF;
        if(section && isExpandable()) {
            if(selected) {
                writer.writeAttribute("class", "selected",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            writer.startElement("a", null);
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
            if (userExpanded || userCollapsed) {
            	if (userExpanded) {
	                writer.writeAttribute("class", (String)getProperty(PROP_MENU_EXPANDED),null); // $NON-NLS-1$
            	}
            	else {
	                writer.writeAttribute("class", (String)getProperty(PROP_MENU_COLLAPSED),null); // $NON-NLS-1$
	                tree.getNodeContext().setHidden(true);
            	}
            }
            else {
	            boolean expanded = depth<expandLevel && tree.getNode().isExpanded(); 
	            if(expanded) {
	                writer.writeAttribute("class", (String)getProperty(PROP_MENU_EXPANDED),null); // $NON-NLS-1$
	            } else {
	                writer.writeAttribute("class", (String)getProperty(PROP_MENU_COLLAPSED),null); // $NON-NLS-1$
	                tree.getNodeContext().setHidden(true);
	            }
            }
            // OneUI v2.1 looks better with this...
            if(ThemeUtil.isOneUIVersion(context, 2, 1)) {
                writer.writeAttribute("style", "padding: 0px; margin-top: 4px;",null); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                writer.writeAttribute("style", "padding: 0px",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            writer.writeAttribute("role", "button",null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("href", "#",null); // $NON-NLS-1$
            
            StringBuilder b = new StringBuilder();
            b.append("javascript:XSP.oneUIMenuSwap(event,"); //$NON-NLS-1$
            JSUtil.addSingleQuoteString(b, getExpandEffect());
            b.append(","); //$NON-NLS-1$
            JSUtil.addSingleQuoteString(b, nodeId);
            b.append(")"); //$NON-NLS-1$
            writer.writeAttribute("onclick", b.toString(),null); // $NON-NLS-1$
            String menuSectionLinkTitle = (String)getProperty(PROP_MENU_SECTION_LINK_TITLE);
            if( ExtLibRenderUtil.isAltPresent(menuSectionLinkTitle) ){
                writer.writeAttribute("title", menuSectionLinkTitle,null); // $NON-NLS-1$
            }

            
            writer.startElement("span", null); //$NON-NLS-1$
            writer.writeAttribute("class", "lotusAltText",null); //$NON-NLS-1$ //$NON-NLS-2$
            if(userCollapsed || userExpanded){
                if(userExpanded){
                    // down arrow
                    writer.writeText("\u25BC", null); //$NON-NLS-1$
                }else{
                    // right arrow
                    writer.writeText("\u25BA", null); //$NON-NLS-1$
                }
            }else{
                boolean isExpanded = depth < expandLevel && tree.getNode().isExpanded(); 
                if(isExpanded){
                    // down arrow
                    writer.writeText("\u25BC", null); //$NON-NLS-1$
                }else{
                    // right arrow
                    writer.writeText("\u25BA", null); //$NON-NLS-1$
                }
            }
            writer.endElement("span"); //$NON-NLS-1$

            
            writer.endElement("a"); //$NON-NLS-1$
            
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
            writer.startElement("h3", null); // $NON-NLS-1$
            writer.writeAttribute("class", (String)getProperty(PROP_MENU_SECTION_HEADING),null); // $NON-NLS-1$
            // Accessibility
            writer.writeAttribute("role", "treeitem",null); // $NON-NLS-1$ $NON-NLS-2$
            if(ThemeUtil.isOneUIVersionAtLeast(context, 2, 1)) {
                writer.writeAttribute("style","padding-left: 0px;",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
            writer.endElement("h3"); // $NON-NLS-1$
        } else {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        }
    }
    
    @Override
    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean section = tree.getNode().getType()!=ITreeNode.NODE_LEAF;
        if(section) {
            if(ThemeUtil.isOneUIVersionAtLeast(context, 2, 1)) {
                writer.writeAttribute("style","text-decoration:none; position: static; padding-bottom: 0px; padding-top: 0px;",null); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                writer.writeAttribute("style","text-decoration:none; position: static; padding-left: 0px",null); // $NON-NLS-1$ $NON-NLS-2$
            }
        } else {
            super.renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
        }
    }
    
    @Override
    protected boolean isChildrenSeparate() {
        // We need the children to be generated in a separate <li>, else the lotusSelected class
        // applies to the entire hierarchy
        return true;
    }
    
    @Override
    protected boolean alwaysRenderItemLink(TreeContextImpl tree, boolean enabled, boolean selected) {
        // Always render a link tag for the item, even when the text is empty.
        // Else the menu is not rendered properly
        // There is currently an issue with OneUI 3.0 where the dojo style reset the h3 style for .oneui30
//        if(false) {
//            boolean section = tree.getNode().getType()!=ITreeNode.NODE_LEAF;
//            if(section) {
//                return false;
//            }
//        }
        return true;
    }

    @Override
    protected void renderEntryNodeChildAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        if(tree.getNodeContext().isHidden()) {
            writer.writeAttribute("style","display:none",null); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String s = super.getItemStyleClass(tree, enabled, selected);
        if(selected) {
            return ExtLibUtil.concatStyleClasses(s,(String)getProperty(PROP_MENU_SELECTED));
        }
        return s;
    }
}
