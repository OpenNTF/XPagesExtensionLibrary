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

package com.ibm.xsp.extlib.renderkit.html_extended.outline.tree;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.extlib.component.util.EventHandlerUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.ITreeNodeDefaultValues;
import com.ibm.xsp.extlib.tree.ITreeRenderer;
import com.ibm.xsp.extlib.tree.impl.AbstractTreeContext;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * Base class that renders a tree.
 * <p>
 * </p>
 * @author priand
 */
public abstract class AbstractTreeRenderer extends ValueBindingObjectImpl implements ITreeRenderer, ITreeNodeDefaultValues {

    private static final long serialVersionUID = 1L;

	// ==========================================================================
	// From FacesRendererEx
	// ==========================================================================

    // Internal debug flag
	public static final boolean DEBUG = FacesRendererEx.DEBUG;

	protected Object getProperty(int prop) {
		return null;
	}

	protected void newLine(ResponseWriter w) throws IOException {
		JSUtil.writeln(w);
	}
	
	protected void newLine(ResponseWriter w, String comment) throws IOException {
		if(DEBUG && comment!=null) {
			w.writeComment(comment);
		}
		JSUtil.writeln(w);
	}	

	
    
    protected static class TreeContextImpl extends AbstractTreeContext {
        UIComponent component;
        XspEventHandler handler;
        String clientIdSuffix;
        private boolean outerTagEmitted;
        public TreeContextImpl(UIComponent component, ITree tree, ITreeNodeDefaultValues treeNodeDefault) {
            super(tree, treeNodeDefault);
            this.component = component;
            if(component!=null) {
                this.handler = EventHandlerUtil.findHandler(component,"onItemClick"); // $NON-NLS-1$
            }
        }
        public UIComponent getComponent() {
            return component;
        }
        public String getClientIdSuffix() {
            return clientIdSuffix;
        }
        public void setClientIdSuffix(String clientIdSuffix) {
            this.clientIdSuffix = clientIdSuffix;
        }
        public String getClientId(FacesContext context, String prefix, int depth) {
            UIComponent c = getComponent();
            if(c!=null) {
                String clientId = c.getClientId(context);
                StringBuilder b = null;
                if(StringUtil.isNotEmpty(clientIdSuffix)) {
                    if(b==null) {
                        b = new StringBuilder(32);
                        b.append(clientId);
                    }
                    b.append('_');
                    b.append(clientIdSuffix);
                }
                if(StringUtil.isNotEmpty(prefix)) {
                    if(b==null) {
                        b = new StringBuilder(32);
                        b.append(clientId);
                    }
                    b.append('_');
                    b.append(prefix);
                    // We ignore the first index as it is always '0' (the tree...)
                    // so we start the index then at 1
                    if(depth>0) {
                        int cc = Math.min(depth+1, count);
                        for(int i=1; i<cc; i++) {
                            b.append('_');
                            b.append(contexts[i].getIndexInParent());
                        }
                    }
                }
                return b!=null ? b.toString() : clientId;
            }
            return null;
        }
        public XspEventHandler getOnClickHandler() {
            return handler;
        }
		public boolean isOuterTagEmitted() {
			return outerTagEmitted;
		}
		public void setOuterTagEmitted(boolean outerTagEmitted) {
			this.outerTagEmitted = outerTagEmitted;
		}
    }
    
    private String clientIdSuffix;
    
    public AbstractTreeRenderer() {
    }
    
    public String getClientIdSuffix() {
        return clientIdSuffix;
    }
    
    public void setClientIdSuffix(String clientIdSuffix) {
        this.clientIdSuffix = clientIdSuffix;
    }
    
    
    // ===================================================================================
    //  Client id utility
    // ===================================================================================
    
    public String getClientId(FacesContext context, TreeContextImpl treeContext) {
        return treeContext.getClientId(context, getClientIdSuffix(), 0);
    }

    public void writeClientIdIfNecessary(FacesContext context, ResponseWriter w, TreeContextImpl tree) throws IOException {
        UIComponent component = tree.getComponent();
        if(component!=null) {
            if(HtmlUtil.isUserId(component.getId())) {
                String clientId = tree.getClientId(context, /*prefix*/null, /*depth=0, the tree*/0);
                w.writeAttribute("id", clientId, null); // $NON-NLS-1$ $NON-NLS-2$
               	tree.setOuterTagEmitted(true);
            }
        }
    }

    
    // ===================================================================================
    //  Tree rendering.
    // ===================================================================================

    public void render(FacesContext context, UIComponent component, ITree tree, ResponseWriter writer) throws IOException {
        TreeContextImpl tc = new TreeContextImpl(component,tree,getTreeNodeDefault());
        render(context, writer, tc);
    }
    public void render(FacesContext context, UIComponent component, String clientIdSuffix, ITree tree, ResponseWriter writer) throws IOException {
        TreeContextImpl tc = new TreeContextImpl(component,tree,getTreeNodeDefault());
        tc.setClientIdSuffix(clientIdSuffix);
        render(context, writer, tc);
    }
    
    //public void render(FacesContext context, ResponseWriter writer, ITree.TreeContext treeContext) throws IOException;
    public void render(FacesContext context, ResponseWriter writer, ITreeNode.TreeContext treeContext) throws IOException {
        render(context, writer, (TreeContextImpl)treeContext);
    }

    protected void render(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        preRenderTree(context, writer, tree);
        renderChildren(context, writer, tree);
        postRenderTree(context, writer, tree);
    }
    
    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    protected void postRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    
    // ===================================================================================
    //  NodeList rendering.
    // ===================================================================================

    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    	// Use lazy <ul> emitting in case of some children are rendered
    	boolean needPostRender = false;
        if(tree.getNodeContext().hasChildren()) {
            boolean expanded = tree.getNode()==null || tree.getNode().isExpanded();
            if(renderCollapsedChildren() || expanded) {
                AbstractTreeRenderer childrenRenderer = getChildrenRenderer(tree);
                if(childrenRenderer!=null) {
                    ITreeNode.NodeIterator it = tree.getNodeContext().iterateChildren(0,Integer.MAX_VALUE);
                    if(it!=null) {
                        for( int i=0; it.hasNext(); i++) {
                            ITreeNode node = it.next();
                            if(node.isRendered()) {
                            	preRenderList(context, writer, tree);
                            	needPostRender = true;
                            	break;
                            }
                        }
                    }
                }
                renderList(context, writer, tree);
                if (needPostRender) {
                	postRenderList(context, writer, tree);
                }
            }
        }
    }   
    
    protected boolean renderCollapsedChildren() throws IOException {
        return false;
    }

    protected void preRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    protected void postRenderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
    }

    protected void renderList(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        AbstractTreeRenderer childrenRenderer = getChildrenRenderer(tree);
        if(childrenRenderer!=null) {
            ITreeNode.NodeIterator it = tree.getNodeContext().iterateChildren(0,Integer.MAX_VALUE);
            if(it!=null) {
                for( int i=0; it.hasNext(); i++) {
                    ITreeNode node = it.next();
                    if(node.isRendered()) {
                        tree.push(node, i, !it.hasNext());
                        childrenRenderer.renderNode(context, writer, tree);
                        tree.pop();
                    }
                }
            }
        }
    }

    protected AbstractTreeRenderer getChildrenRenderer(TreeContextImpl tree) {
        // Recursively use the same renderer 
        return this;
    }
    

    
    // ===================================================================================
    //  Node rendering.
    // ===================================================================================
    
    protected abstract void renderNode(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException;

    
    // ============================================================================
    // ITreeNodeDefault implementation
    // 'this' is an ITreeNodeDefault.
    // ============================================================================

    public ITreeNodeDefaultValues getTreeNodeDefault() {
        return this;
    }

    public String getNodeHref(ITreeNode node) {
        return null;
    }
    public String getNodeImage(ITreeNode node) {
        return null;
    }
    public String getNodeImageAlt(ITreeNode node) {
        return null;
    }
    public String getNodeImageHeight(ITreeNode node) {
        return null;
    }
    public String getNodeImageWidth(ITreeNode node) {
        return null;
    }
    public String getNodeLabel(ITreeNode node) {
        return null;
    }
    public String getNodeOnClick(ITreeNode node) {
        return null;
    }
    public String getNodeRole(ITreeNode node) {
        return null;
    }
    public String getNodeTitle(ITreeNode node) {
        return null;
    }
    public String getNodeStyle(ITreeNode node) {
        return null;
    }
    public String getNodeStyleClass(ITreeNode node) {
        return null;
    }
    public String getNodeSubmitValue(ITreeNode node) {
        return null;
    }
    public boolean isNodeEnabled(ITreeNode node) {
        return true;
    }
    public boolean isNodeExpanded(ITreeNode node) {
        return true;
    }
    public boolean isNodeRendered(ITreeNode node) {
        return true;
    }
    public boolean isNodeSelected(ITreeNode node) {
        return false;
    }

    
    // ===================================================================
    // Styles to be used
    // These methods are called by the renderer and ask the current node
    // ===================================================================
    
    protected String getContainerStyle(TreeContextImpl tree) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getStyle() : null;
    }

    protected String getContainerStyleClass(TreeContextImpl tree) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getStyleClass() : null;
    }
    
    protected String getItemStyle(TreeContextImpl tree, boolean enabled, boolean selected) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getStyle() : null;
    }
    
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getStyleClass() : null;
    }
    
    protected String getItemRole(TreeContextImpl tree, boolean enabled, boolean selected) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getRole() : null;
    }
    
    protected String getItemTitle(TreeContextImpl tree, boolean enabled, boolean selected) {
        ITreeNode n = tree.getNode();
        return n!=null ? n.getTitle() : null;
    }

    
    // ============================================================================
    // Node utility
    // ============================================================================

    public String findNodeOnClick(TreeContextImpl tree) {
        String onclick = tree.getNode().getOnClick();
        if(StringUtil.isNotEmpty(onclick)) {
            return onclick;
        }
        XspEventHandler handler = tree.getOnClickHandler();
        if(handler!=null) {
            // This should be changed to also support partial refresh, which does not work in 8.5.2
            String submitValue = tree.getNode().getSubmitValue();
            if(StringUtil.isNotEmpty(submitValue)) {
                return EventHandlerUtil.getEventScript(FacesContext.getCurrentInstance(), handler, submitValue);
            }
        }
        return null;
    }
}