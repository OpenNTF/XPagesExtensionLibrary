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

import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;



/**
 * Tree implementation that is a wrapper on top of nodes.
 * @author Philippe Riand
 */
public class TreeImpl implements ITree {

	public static TreeImpl get(ITreeNode root) {
		if(root!=null) {
			return new TreeImpl(root);
		}
		return null;
	}

	public static TreeImpl get(List<ITreeNode> children) {
		if(children!=null) {
			return new TreeImpl(children);
		}
		return null;
	}

	private List<ITreeNode> children;
	
	public TreeImpl(ITreeNode root) {
		this.children = Collections.singletonList(root);
	}

	public TreeImpl(List<ITreeNode> children) {
		this.children = children;
	}
	
	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return TreeUtil.getIterator(children, start, count);
	}
}
