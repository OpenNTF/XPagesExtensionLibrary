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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.component.outline.UIOutlineGeneric;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.ITreeRenderer;
import com.ibm.xsp.resource.DojoModuleResource;

public class MobileNavigatorRenderer extends AbstractTreeRenderer {

    private static final long serialVersionUID = 1L;

    JsonJavaObject jsonTree = new JsonJavaObject();
    ArrayList<JsonJavaObject> treeObj = new ArrayList<JsonJavaObject>();    

    public MobileNavigatorRenderer() {
        this.jsonTree.put("items","");   // $NON-NLS-1$
    }


    protected ITreeRenderer findTreeRenderer(FacesContext context,AbstractOutline menu) {
        ITreeRenderer renderer=null;
        if (menu instanceof UIOutlineGeneric) {
            renderer=((UIOutlineGeneric) menu).findTreeRenderer();
            if (renderer!=null){
                return renderer;
            }
            else{
                //by default the accordion is used
                return new MobileNavigatorAccordionRenderer();
            }           
        }
        return new MobileNavigatorAccordionRenderer();
    }

    protected String getRendererWidget(){
        return ""; // $NON-NLS-1$
    }
    
    @Override
    protected void preRenderList(FacesContext context, ResponseWriter writer,
            TreeContextImpl tree) throws IOException {
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();     
        ExtLibResources.addEncodeResource(rootEx, new DojoModuleResource(
        getDojoType()));
    }

    @Override
    protected void postRenderList(FacesContext context, ResponseWriter writer,
            TreeContextImpl tree) throws IOException {
        endRenderContainer(context, writer, tree);
    }

    @Override
    protected void renderNode(FacesContext context, ResponseWriter writer,
            TreeContextImpl tree) throws IOException {
        renderEntryItem(context, writer, tree);
    }

    protected void endRenderContainer(FacesContext context,
            ResponseWriter writer, TreeContextImpl tree) throws IOException {
        this.jsonTree.put("items",treeObj); // $NON-NLS-1$
        String jsonOutput=this.jsonTree.toString();         
        jsonOutput=jsonOutput.replace("\"", "\\\""); // $NON-NLS-1$
        writer.startElement("div", null);            // $NON-NLS-1$
        
        String dojoType=this.getDojoType();
        if(!StringUtil.isEmpty(dojoType)){
            HashMap<String, String> attr=new HashMap<String, String>();
            attr.put("jsonTree", jsonOutput);                    // $NON-NLS-1$
            
            DojoRendererUtil.writeDojoHtmlAttributes(context, null, dojoType, attr);
        }
        
        writer.endElement("div"); // $NON-NLS-1$
    }

    protected String getDojoType(){
        return null;
    }
    
    protected void renderEntryItem(FacesContext context, ResponseWriter writer,
            TreeContextImpl tree) throws IOException {
        // Check for a separator node
        // TODO: How to render it?
        int type = tree.getNode().getType();
        if (type == ITreeNode.NODE_SEPARATOR) {
            renderEntrySeparator(context, writer, tree);
        } else {                    
            this.treeObj.add(renderEntryNode(tree));        
        }

    }

    protected void renderEntrySeparator(FacesContext context,
            ResponseWriter writer, TreeContextImpl tree) throws IOException {
        /*
         * TODO: HOW TO RENDER SEPARATORS? WHAT ABOUT USING ITEMCLASS?
         */
        return;
    }

    protected JsonJavaObject renderEntryNode(TreeContextImpl tree) throws IOException {
        boolean enabled = tree.getNode().isEnabled();
        boolean selected = tree.getNode().isSelected();
        boolean escape = tree.getNode().isEscape();
        
        /*
         * json for a menu item
         */
                
        JsonJavaObject jsonTreeItem = new JsonJavaObject();
        
        // and its children
        if (tree.getNodeContext().hasChildren()) {                  
            
            jsonTreeItem.put("items", "");   // $NON-NLS-1$ $NON-NLS-2$
            renderChildren( tree,jsonTreeItem);
        }
        else{
            
            String href = tree.getNode().getHref();
            if (StringUtil.isNotEmpty(href)) {
                jsonTreeItem.putJsonProperty("href", href); // $NON-NLS-1$
            }
        }
        String nodeClientId;
        // Note, outline only has 1 treeNode property, so all outline treeNode HTML elements will contain _t in the ID. 
        String treeNodePropertyPrefix = "t"; //$NON-NLS-1$ 
        nodeClientId = tree.getClientId(FacesContext.getCurrentInstance(), treeNodePropertyPrefix, tree.getDepth());
        if( null != nodeClientId ){
            // "id" support added for SPR#RDIM9FZFPN, for XPages automation.
            // treeNode clientId will be like id="view:_id1:appPage1_content:homeCustom1:outline1_t_3_2"
            // where outline1 has (at index 3) a container treeNode, and under that (at index 2) this treeNode
            jsonTreeItem.putJsonProperty("id", nodeClientId); //$NON-NLS-1$
        }

        jsonTreeItem.putJsonProperty("disabled", Boolean.toString(!enabled)); // $NON-NLS-1$
        jsonTreeItem.putJsonProperty("iconClass", ""); // $NON-NLS-1$ $NON-NLS-2$
        jsonTreeItem.putJsonProperty("escape", Boolean.toString(escape)); // $NON-NLS-1$
        
        // Fix for SPR#BGLN9HYDW9 - No icon image appearing for nodes
        // Pass in the iconImg, iconHeight & iconWidth params
        String iconImg = tree.getNode().getImage();
        if (StringUtil.isNotEmpty(iconImg)) {
            jsonTreeItem.putJsonProperty("iconImg", iconImg); // $NON-NLS-1$
            
            String iconHeight = tree.getNode().getImageHeight();
            String iconWidth = tree.getNode().getImageWidth();
            if (StringUtil.isNotEmpty(iconHeight)) {
                jsonTreeItem.putJsonProperty("iconHeight", iconHeight); // $NON-NLS-1$
            }else{
                jsonTreeItem.putJsonProperty("iconHeight", ""); // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(iconWidth)) {
                jsonTreeItem.putJsonProperty("iconWidth", iconWidth); // $NON-NLS-1$
            }else{
                jsonTreeItem.putJsonProperty("iconWidth", ""); // $NON-NLS-1$
            }
            // Fix for SPR#BGLN9J6K6U - Icon alt text not applied
            String iconAlt = tree.getNode().getImageAlt();
            if (StringUtil.isNotEmpty(iconAlt)) {
                jsonTreeItem.putJsonProperty("iconAlt", iconAlt); // $NON-NLS-1$
            }else{
                jsonTreeItem.putJsonProperty("iconAlt", ""); // $NON-NLS-1$
            }
        }else{
            jsonTreeItem.putJsonProperty("iconImg", ""); // $NON-NLS-1$
            jsonTreeItem.putJsonProperty("iconWidth", ""); // $NON-NLS-1$
            jsonTreeItem.putJsonProperty("iconHeight", ""); // $NON-NLS-1$
            jsonTreeItem.putJsonProperty("iconAlt", ""); // $NON-NLS-1$
        }
        
        String itemClass = getItemStyleClass(tree, enabled, selected);
        if (StringUtil.isNotEmpty(itemClass)) {
            jsonTreeItem.putJsonProperty("itemClass", itemClass); // $NON-NLS-1$
        }
        else{
            jsonTreeItem.putJsonProperty("itemClass", ""); // $NON-NLS-1$
        }
        
        String itemStyle = getItemStyle(tree, enabled, selected);
        if (StringUtil.isNotEmpty(itemStyle)) {
            jsonTreeItem.putJsonProperty("itemStyle", itemStyle); // $NON-NLS-1$
        }
        else{
            jsonTreeItem.putJsonProperty("itemStyle", ""); // $NON-NLS-1$
        }
        
        String containerClass = getContainerStyleClass(tree);
        if (StringUtil.isNotEmpty(containerClass)) {
            jsonTreeItem.putJsonProperty("containerClass", itemClass); // $NON-NLS-1$
        }
        else{
            jsonTreeItem.putJsonProperty("containerClass", ""); // $NON-NLS-1$
        }
        
        String containerStyle = getContainerStyle(tree);
        if (StringUtil.isNotEmpty(itemStyle)) {
            jsonTreeItem.putJsonProperty("containerStyle", containerStyle); // $NON-NLS-1$
        }
        else{
            jsonTreeItem.putJsonProperty("containerStyle", ""); // $NON-NLS-1$
        }

        String targetUrl = tree.getNode().getHref();
        if (targetUrl != null) {
            jsonTreeItem.putJsonProperty("href", targetUrl); // $NON-NLS-1$
        }

        String label = tree.getNode().getLabel();
        if (StringUtil.isNotEmpty(label)) {
            jsonTreeItem.putJsonProperty("label", label); // $NON-NLS-1$
        }
        
        return jsonTreeItem;
    }

    @Override
    protected AbstractTreeRenderer getChildrenRenderer(TreeContextImpl tree) {
        // Recursively use the same renderer
        return this;
    }

    protected JsonJavaObject renderChildren(TreeContextImpl tree,JsonJavaObject childrenJSON)
            throws IOException {
        
        MobileNavigatorRenderer childrenRenderer = (MobileNavigatorRenderer) getChildrenRenderer(tree);
        if (tree.getNode().getType() != ITreeNode.NODE_LEAF) {

            ArrayList<JsonJavaObject> childrenJSONTree = new ArrayList<JsonJavaObject>();

            if (childrenRenderer != null) {
                ITreeNode.NodeIterator it = tree.getNodeContext()
                        .iterateChildren(0, Integer.MAX_VALUE);
                if (it != null) {
                    for (int i = 0; it.hasNext(); i++) {
                        ITreeNode node = it.next();
                        if (node.isRendered()) {
                            tree.push(node, i, !it.hasNext());
                            childrenJSONTree.add(childrenRenderer
                                    .renderEntryNode(tree));
                            tree.pop();
                        }
                    }
                }
            }
            childrenJSON.put("items", childrenJSONTree);                         // $NON-NLS-1$
            return childrenJSON;

        } else {
            return childrenRenderer.renderEntryNode(tree);
        }   
    }

    // ===================================================================
    // State management
    // ===================================================================

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);       
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[1];
        _values[0] = super.saveState(_context);     
        return _values;
    }

}
