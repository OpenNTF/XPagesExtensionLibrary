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




/**
 * Defines the default values for a tree.
 * <p>
 * A TreeNode implementation should delegate to this interface when a value is not set
 * explicitly by the user or not fixed by the node.<br>
 * This gives an opportunity to the renderer to control the node properties (for example,
 * a bread crumbs controls will make the last entry not enabled)
 * </p>
 * @author Philippe Riand
 */
public interface ITreeNodeDefaultValues {

	/**
	 * Get the Node label.
	 */
	public String getNodeLabel(ITreeNode node);
	
	/**
	 * Get the Node icon.
	 */
	public String getNodeImage(ITreeNode node);
	
    /**
     * Get the Node icon's alternative information.
     */
    public String getNodeImageAlt(ITreeNode node);
    
    /**
     * Get the Node icon's height.
     */
    public String getNodeImageHeight(ITreeNode node);
    
    /**
     * Get the Node icon's width.
     */
    public String getNodeImageWidth(ITreeNode node);
    
	/**
	 * Get the Node link.
	 */
	public String getNodeHref(ITreeNode node);
	
	/**
	 * Get the node style.
	 */
	public String getNodeStyle(ITreeNode node);
	
	/**
	 * Get the node style class.
	 */
	public String getNodeStyleClass(ITreeNode node);
	
	/**
	 * Get the node role.
	 */
	public String getNodeRole(ITreeNode node);
    
    /**
     * Get the node title.
     */
    public String getNodeTitle(ITreeNode node);
	
	/**
	 * Check if a node is selected.
	 */
	public boolean isNodeSelected(ITreeNode node);
	
	/**
	 * Check if a node is enabled.
	 */
	public boolean isNodeEnabled(ITreeNode node);
	
	/**
	 * Check if a node is rendered.
	 */
	public boolean isNodeRendered(ITreeNode node);
	
	/**
	 * Check if a node is expanded.
	 */
	public boolean isNodeExpanded(ITreeNode node);
	
	/**
	 * Return the javascript onclick event associated to this node.
	 */
	public String getNodeOnClick(ITreeNode node);

	/**
	 * Value submitted as part of the event.
	 */
	public String getNodeSubmitValue(ITreeNode node);
	
	/**
     * Check if the entry's label should be HTML-escaped.
     */
    public boolean isNodeEscape(ITreeNode node);
}
