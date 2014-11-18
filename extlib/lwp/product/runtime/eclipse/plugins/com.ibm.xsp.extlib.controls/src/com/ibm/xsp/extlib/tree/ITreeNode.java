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

package com.ibm.xsp.extlib.tree;

import java.io.Serializable;



/**
 * Interface that defines a TreeNode
 * @author Philippe Riand
 */
public interface ITreeNode extends Serializable {
	
	public static interface NodeIterator {
		public boolean hasNext();
		public ITreeNode next();
	}


	public static interface NodeContext {
		public ITreeNodeDefaultValues getTreeNodeDefault();
	
		public ITree getTree();
		public ITreeNode getNode();
		
		public boolean hasChildren();
		public NodeIterator iterateChildren(int start, int count);
		
		public int getIndexInParent();
		public boolean isFirstNode();
		public boolean isLastNode();
		public boolean isFirstNonStatic();
		
		public boolean isHidden();
		public void setHidden(boolean hidden);
	}


	public static interface TreeContext {
		public int getDepth();
		public ITree getTree();
		public ITreeNode getNode();
		public NodeContext getNodeContext();
		public NodeContext getNodeContext(int depth);
	}


	/**
	 * A node is a leaf when it cannot have children. For example,
	 * in a file system tree, a leaf is a file.
	 */
	public static final int NODE_LEAF			= 0;
	/**
	 * A node is a leaf when it might have children. Note that if it can
	 * have children, this is not required. For example, in a file system 
	 * tree, a leaf is a directory.
	 */
	public static final int NODE_CONTAINER		= 1;
	/**
	 * Return if the tree node is a node list.
	 * When a node is node list, then the node itself is not displayed, but
	 * it contributes its children to its parent. Note that the list delegation
	 * have to be managed by the parent children iterator (see: TreeUtils.getIterator
	 * for a List).
	 */
	public static final int NODE_NODELIST		= 2;
	/**
	 * Define a separator.
	 */
	public static final int NODE_SEPARATOR		= 3;
	
	/**
	 * Get the current node context.
	 * This can be accessed by the property getter implementation to get the default
	 * values for a node.
	 */
	public ITreeNode.NodeContext getNodeContext();
	
	/**
	 * Set the current node context.
	 * This is generally called by the renderers when rendering recursively the tree.
	 */
	public void setNodeContext(ITreeNode.NodeContext context);

	/**
	 * Get the Node type.
	 * Currently, it can be a regular tree node or a separator. It mostly
	 * influes the UI rendering of the node, as well as actions applied to
	 * it.
	 */
	public int getType();

	/**
	 * Iterate through the list of children for the node.
	 * The returned iterator can be null.
	 * @param start TODO
	 * @param count TODO
	 */
	public ITreeNode.NodeIterator iterateChildren(int start, int count); 
	
	
	// =====================================================
	// Node description
	// =====================================================

	/**
	 * Get the Node label.
	 */
	public String getLabel();
	
	/**
	 * Get the Node icon.
	 * @return a url to an image, or null if no link is assigned 
	 */
	public String getImage();
	
    /**
     * Get the Node icon's alternative information.
     * @return an alternative information for an image, or null if not specified 
     */
    public String getImageAlt();
    
    /**
     * Get the Node icon's height.
     * @return an image height, or null if not specified 
     */
    public String getImageHeight();
    
    /**
     * Get the Node icon's width.
     * @return an image width, or null if not specified 
     */
    public String getImageWidth();
    
	/**
	 * Get the Node link.
	 * @return a url to a link, or null if no link is assigned 
	 */
	public String getHref();
	
	/**
	 * Check if the entry is selected.
	 */
	public boolean isSelected();
	
	/**
	 * Check if the entry should be enabled.
	 */
	public boolean isEnabled();
	
	/**
	 * Check if the entry should be rendered.
	 */
	public boolean isRendered();
	
	/**
	 * Get the style for node.
	 */
	public String getStyle();
	
	/**
	 * Get the style class for node.
	 */
	public String getStyleClass();
	
	/**
	 * Get the role for node.
	 */
	public String getRole();
    
    /**
     * Get the title for node.
     */
    public String getTitle();
	
	
	// =====================================================
	// Event management
	// =====================================================
	
	/**
	 * Return the javascript onclick event associated to this node.
	 */
	public String getOnClick();

	/**
	 * Value submitted as part of the event.
	 */
	public String getSubmitValue();
	
	
	// =====================================================
	// Hierarchy Management
	// =====================================================
	
	/**
	 * Check if the node is expanded.
	 * @return
	 */
	public boolean isExpanded();
}
