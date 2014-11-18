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

import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.ITreeNodeDefaultValues;

/**
 * Base class that renders a tree.
 * <p>
 * </p>
 * @author priand
 */
public abstract class AbstractTreeContext implements ITreeNode.TreeContext {

	protected static class NodeContextImpl implements ITreeNode.NodeContext {
		protected AbstractTreeContext	treeContext;
		protected ITreeNode				node;
		protected boolean				lastNode;
		protected int					indexInParent;
		protected boolean				previousActions;
		protected boolean				hidden;
		NodeContextImpl(AbstractTreeContext treeContext) {
			this.treeContext = treeContext;
		}
		public ITreeNodeDefaultValues getTreeNodeDefault() {
			return treeContext.treeNodeDefault;
		}
		public ITree getTree() {
			return treeContext.tree;
		}
		public ITreeNode getNode() {
			return node;
		}
		public int getIndexInParent() {
			return indexInParent;
		}
		public boolean isLastNode() {
			ITreeNode.NodeIterator it = getTree().iterateChildren(0, Integer.MAX_VALUE);
			int lastNodeIndex = -1;
			while (it.hasNext()) {
				it.next();
				lastNodeIndex++;
			}
			return indexInParent == lastNodeIndex;
		}
		public boolean isFirstNode() {
			return indexInParent==0;
		}
		public boolean isFirstNonStatic() {
			return indexInParent==0 || !previousActions;
		}
		public boolean isHidden() {
			return hidden;
		}
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}
		public boolean hasChildren() {
			return node!=null ? node.getType()==ITreeNode.NODE_CONTAINER : true;
		}
		public ITreeNode.NodeIterator iterateChildren(int start, int count) {
			if(node!=null) {
				return node.iterateChildren(0, Integer.MAX_VALUE);
			} else {
				return treeContext.tree.iterateChildren(0, Integer.MAX_VALUE);
			}
		}
	}
	
	
	protected ITree tree;
	protected int count;
	protected NodeContextImpl[] contexts;
	protected NodeContextImpl currentContext;
	protected ITreeNodeDefaultValues treeNodeDefault;
	
	public AbstractTreeContext(ITree tree, ITreeNodeDefaultValues treeNodeDefault) {
		this.tree = tree;
		this.treeNodeDefault = treeNodeDefault;
		//this.contexts = new NodeContext[1]; //  for tests... 
		this.contexts = new NodeContextImpl[16];
		this.contexts[count++] = currentContext = new NodeContextImpl(this);
	}
	public ITree getTree() {
		return tree;
	}
	public int getDepth() {
		return count;
	}
	public ITreeNodeDefaultValues getTreeNodeDefaultValues() {
		return treeNodeDefault;
	}
	public void push(ITreeNode node, int indexInParent, boolean lastNode) {
		if(count==contexts.length) {
			//NodeContext[] oldc = new NodeContext[contexts.length+1]; // for tests...
			NodeContextImpl[] oldc = new NodeContextImpl[contexts.length*2];
			System.arraycopy(contexts, 0, oldc, 0, contexts.length);
			contexts = oldc;
		}
		if(contexts[count]==null) {
			contexts[count] = new NodeContextImpl(this);
		}
		currentContext = contexts[count++]; 
		currentContext.node = node; 
		currentContext.lastNode = lastNode; 
		currentContext.indexInParent= indexInParent;
		currentContext.hidden = contexts[count-2].hidden;
		node.setNodeContext(currentContext);
	}
	public void pop() {
		currentContext.node.setNodeContext(null);
		count--;
		currentContext = contexts[count-1]; 
	}

	public ITreeNode getNode() {
		return currentContext.getNode();
	}

	public ITreeNode.NodeContext getNodeContext() {
		return currentContext;
	}

	public ITreeNode.NodeContext getNodeContext(int depth) {
		return contexts[count-1-depth];
	}
	
	public void markCurrentAsAction() {
		currentContext.previousActions = true;
	}
}

