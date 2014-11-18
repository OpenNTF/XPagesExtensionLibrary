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

package com.ibm.xsp.extlib.tree.impl;

import java.util.Collections;
import java.util.List;

import com.ibm.xsp.extlib.tree.ITreeNode;


/**
 * Root tree node that contains other nodes.
 * <p>
 * This tree node should only be used a root container node, where none
 * of ots content, besides the children, is being used.
 * </p>
 * 
 * @author Philippe Riand
 */
public class RootContainerTreeNode extends AbstractTreeNode {

	private static final long serialVersionUID = 1L;

	private List<ITreeNode> children;

	public RootContainerTreeNode() {
	}

	public RootContainerTreeNode(ITreeNode child) {
		this.children = Collections.singletonList(child);
	}

	public RootContainerTreeNode(List<ITreeNode> children) {
		this.children = children;
	}

	public int getType() {
		return ITreeNode.NODE_CONTAINER;
	}

	@Override
	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return TreeUtil.getIterator(children, start, count);
	}
}
