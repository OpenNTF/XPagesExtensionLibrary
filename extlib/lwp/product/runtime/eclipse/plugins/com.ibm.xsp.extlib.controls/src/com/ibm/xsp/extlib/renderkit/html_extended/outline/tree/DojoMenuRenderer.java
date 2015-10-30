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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JavaScriptUtil;

public class DojoMenuRenderer extends AbstractTreeRenderer {

    private static final long serialVersionUID = 1L;

    private UIComponent component;
    
    // Parameters
    private String menuId;
    private String connectId;
    private String connectEvent;

    // Internal methods
    private String menuCtorName;
    private String menuEventName;
    private String varName;
    private StringBuilder script;
    private int subId;
    private int chId;
    
    public DojoMenuRenderer() {
    }

    public DojoMenuRenderer(UIComponent component) {
        this.component = component;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    
    public String getConnectId() {
        return connectId;
    }
    
    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }
    
    public String getConnectEvent() {
        return connectEvent;
    }
    
    public void setConnectEvent(String connectEvent) {
        this.connectEvent = connectEvent;
    }

    protected String getMenuType() {
        return "dijit.Menu"; // $NON-NLS-1$
    }
    protected String getMenuItemType() {
        return "dijit.MenuItem"; // $NON-NLS-1$
    }
    protected String getPopupMenuItemType() {
        return "dijit.PopupMenuItem"; // $NON-NLS-1$
    }
    protected String getSeparatorType() {
        return "dijit.MenuSeparator"; // $NON-NLS-1$
    }
    
    @Override
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.setDojoTheme(true);
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibMenu);
        
        String mid = ExtLibUtil.encodeJSFunctionName(getMenuId(context,tree));
        
        this.script = new StringBuilder();
        this.menuCtorName = mid+"_ctor"; // $NON-NLS-1$
        this.menuEventName = mid+"_event"; // $NON-NLS-1$
        this.varName = "m";
        
        script.append("function "); // $NON-NLS-1$
        script.append(menuCtorName);
        script.append("(){\n"); // $NON-NLS-1$
        script.append("var "); // $NON-NLS-1$
        script.append(varName);
        script.append("=new "); // $NON-NLS-1$
        script.append(getMenuType());
        
        // ({"title":"Drop Down Menu"
        script.append("({"); // $NON-NLS-1$
        JavaScriptUtil.addString(script, "title"); // $NON-NLS-1$
        script.append(":"); // $NON-NLS-1$
        String menuTitle = "Drop Down Menu"; // $NLS-DojoMenuRenderer.DropDownMenu-1$
        JavaScriptUtil.addString(script, menuTitle);
        if(component!=null) {
            boolean hasp = true;
            String style = (String)component.getAttributes().get("style"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(style)) {
                if(hasp) script.append(","); else hasp=true;
                script.append("style:"); // $NON-NLS-1$
                JavaScriptUtil.addString(script, style);
            }
            String styleClass = (String)component.getAttributes().get("styleClass"); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(styleClass)) {
                if(hasp) script.append(","); else hasp=true;
                JavaScriptUtil.addString(script, "class");//$NON-NLS-1$
                script.append(":"); // $NON-NLS-1$
                JavaScriptUtil.addString(script, styleClass);
            }
        }
        script.append("});\n"); // $NON-NLS-1$
    }

    @Override
    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        UIScriptCollector collector = UIScriptCollector.find();
        
        script.append("return "); // $NON-NLS-1$
        script.append(varName);
        script.append(";\n"); // $NON-NLS-1$
        script.append("}\n"); // $NON-NLS-1$
        collector.addScript(script.toString());

        script.setLength(0);
        script.append("function "); // $NON-NLS-1$
        script.append(menuEventName);
        script.append("(thisEvent){"); // $NON-NLS-1$
        script.append("XSP.openMenu(thisEvent,"); // $NON-NLS-1$
        script.append(menuCtorName);
        script.append(");}\n"); // $NON-NLS-1$
        
        // Connect the menu to the id
        script.append("dojo.connect(dojo.byId("); // $NON-NLS-1$
        JavaScriptUtil.addString(script, getConnectId());
        script.append("),");
        JavaScriptUtil.addString(script, getConnectEvent());
        script.append(",");
        script.append(menuEventName);
        script.append(");\n"); // $NON-NLS-1$

        collector.addScriptOnLoad(script.toString());
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int type = tree.getNode().getType();
        if(type==ITreeNode.NODE_SEPARATOR) {
            renderSeparator(context, writer, tree);
        } else {
            renderTreeNode(context, writer, tree);
        }
    }

    protected void renderSeparator(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        script.append(varName);
        script.append(".addChild(new "); // $NON-NLS-1$
        script.append(getSeparatorType());
        script.append("({");
        boolean hasp = false;
        String style = tree.getNode().getStyle();
        if(StringUtil.isNotEmpty(style)) {
            if(hasp) script.append(","); else hasp=true;
            script.append("style:"); // $NON-NLS-1$
            JavaScriptUtil.addString(script, style);
        }

        String styleClass = tree.getNode().getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            if(hasp) script.append(","); else hasp=true;
            JavaScriptUtil.addString(script, "class"); // $NON-NLS-1$
            script.append(":"); // $NON-NLS-1$
            JavaScriptUtil.addString(script, styleClass);
        }
        script.append("}));\n"); // $NON-NLS-1$
    }

    protected void renderTreeNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        boolean hasp = false;
        if(leaf) {
//            script.append(varName);
//            script.append(".addChild(new "); // $NON-NLS-1$
            script.append("var ch"); // $NON-NLS-1$
            script.append( chId++ );
            script.append("=(new "); // $NON-NLS-1$ $NON-NLS-2$
            script.append(getMenuItemType());
            script.append("({");
        } else {
            // Create the child menu
        	chId = 0;
            String menuName = "m"+(subId++);
            script.append("var "); // $NON-NLS-1$
            script.append(menuName);
            script.append("=new "); // $NON-NLS-1$
            script.append(getMenuType());
            script.append("();\n"); // $NON-NLS-1$

            String oldVarName = varName;
            varName = menuName;
            
            renderChildren(context, writer, tree);
            
            varName = oldVarName;
            
//            script.append(varName);
//            script.append(".addChild(new "); // $NON-NLS-1$
            script.append("var popup=(new "); // $NON-NLS-1$
            script.append(getPopupMenuItemType());
            script.append("({");
            script.append("popup:"); // $NON-NLS-1$
            script.append(menuName);
            hasp = true;
        }
        
        // Add the menu parameters
        String label = tree.getNode().getLabel();
        if(StringUtil.isNotEmpty(label)) {
            if(hasp) script.append(","); else hasp=true;
            script.append("label:"); // $NON-NLS-1$
            JavaScriptUtil.addString(script, HtmlUtil.toHTMLContentString(label, false));
        }

        boolean enabled = tree.getNode().isEnabled();
        if(!enabled) {
            if(hasp) script.append(","); else hasp=true;
            script.append("enabled:false"); // $NON-NLS-1$
        }

        String style = tree.getNode().getStyle();
        if(StringUtil.isNotEmpty(style)) {
            if(hasp) script.append(","); else hasp=true;
            script.append("style:"); // $NON-NLS-1$
            JavaScriptUtil.addString(script,style);
        }

        String styleClass = tree.getNode().getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            if(hasp) script.append(","); else hasp=true;
            JavaScriptUtil.addString(script,"class");// $NON-NLS-1$
            script.append(":"); // $NON-NLS-1$
            JavaScriptUtil.addString(script,styleClass);
        }
        
        if(leaf) {
            String href = tree.getNode().getHref();
            if(StringUtil.isNotEmpty(href)) {
                if(hasp) script.append(","); else hasp=true;
                script.append("onClick:function(){window.location.href="); // $NON-NLS-1$
                JavaScriptUtil.addString(script,RenderUtil.formatLinkRef(context,href));
                script.append("}");
            } else {
                String onclick = findNodeOnClick(tree);
                if(StringUtil.isNotEmpty(onclick)) {
                    if(hasp) script.append(","); else hasp=true;
                    script.append("onClick:function(){"); // $NON-NLS-1$
                    script.append(onclick);
                    script.append("}");
                }
            }
        }

        String image = tree.getNode().getImage();
        if(StringUtil.isNotEmpty(image)) {
            // SPR#PHAN8YZE42
            // Override Dojo 1.8 "dijitNoIcon" iconClass and make it behave same way as it was in Dojo 1.6
            // SPR#BGLN9GJJPH
            // Adjusted the fix for PHAN8YZE42 to also apply to container nodes
            script.append(",iconClass: "); // $NON-NLS-1$
            JavaScriptUtil.addString(script, getMenuItemIconClass());
            script.append(""); // $NON-NLS-1$            
        }
        
        script.append("}));\n"); // $NON-NLS-1$
        
        if(leaf) {
        	if(StringUtil.isNotEmpty(image)) {
        		script.append("ch" + (chId-1) + ".iconNode.src ="); // $NON-NLS-1$ $NON-NLS-2$
        		JavaScriptUtil.addString(script, HtmlRendererUtil.getImageURL(context, image));
        		script.append(";\n"); // $NON-NLS-1$
        	}
			script.append(varName);
			script.append(".addChild(ch" + (chId-1) + ");\n"); // $NON-NLS-1$ $NON-NLS-2$
        }
        else {
        	if(StringUtil.isNotEmpty(image)) {
				script.append("popup.iconNode.src ="); // $NON-NLS-1$
				JavaScriptUtil.addString(script, HtmlRendererUtil.getImageURL(context, image));
	    		script.append(";\n"); // $NON-NLS-1$
        	}
			script.append(varName);
			script.append(".addChild(popup);\n"); // $NON-NLS-1$
        }
    }

    protected String getMenuId(FacesContext context, TreeContextImpl tree) {
        return getMenuId();
    }
    
    protected String getMenuItemIconClass() {
        return ""; // $NON-NLS-1$
    }
}