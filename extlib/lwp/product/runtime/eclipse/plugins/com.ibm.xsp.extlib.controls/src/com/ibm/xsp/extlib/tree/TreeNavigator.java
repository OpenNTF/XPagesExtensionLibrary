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
 * Tree Navigator.
 * <p>
 * </p>
 */
public class TreeNavigator {
/*	
	public static class Options {
		private String position;
		private int minLevel;
		private int maxLevel = Integer.MAX_VALUE;
		private int count = Integer.MAX_VALUE;
		public Options() {
		}
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
		public int getMinLevel() {
			return minLevel;
		}
		public void setMinLevel(int minLevel) {
			this.minLevel = minLevel;
		}
		public int getMaxLevel() {
			return maxLevel;
		}
		public void setMaxLevel(int maxLevel) {
			this.maxLevel = maxLevel;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
	}
	
	public interface NodeIterator {
		public boolean hasNext();
		public ITreeNode next();
	}

	public static final class NodeContext {
		ITreeNodeDefault	treeNodeDefault;
		NodeContext			parent;
		ITreeNode			node;
		boolean				lastNode;
		int					indexInParent;
		NodeIterator		iterator;
		public ITreeNodeDefault getTreeNodeDefault() {
			if(treeNodeDefault!=null) {
				return treeNodeDefault;
			}
			if(parent!=null) {
				return parent.getTreeNodeDefault();
			}
			return null;
		}
		public NodeContext getParent() {
			return parent;
		}
		public ITreeNode getNode() {
			return node;
		}
		public boolean isLastNode() {
			return lastNode;
		}
		public int getIndexInParent() {
			return indexInParent;
		}
	}
	
	private int count;
	private NodeContext[] contexts;
	private NodeContext currentContext;
	
	private ITree tree;
	private Options options;
	
	public TreeNavigator(ITree tree, Options options) {
		this.tree = tree;
		this.options = options;
		this.contexts = new NodeContext[16];
		this.contexts[count++] = currentContext = new NodeContext();
	}

	private ITree getTree() {
		return tree;
	}

	public ITreeNodeDefault	getTreeNodeDefault() {
		return currentContext.treeNodeDefault;
	}
	
	public void setTreeNodeDefault(ITreeNodeDefault	treeNodeDefault) {
		currentContext.treeNodeDefault = treeNodeDefault;
	}

	public NodeIterator getIterator() {
		return currentContext.iterator;
	}

	
	// =========================================================================
	// Access to the current node
	// =========================================================================
	
	public int getDepth() {
		return count;
	}
	public ITreeNode getNode() {
		return currentContext.node;
	}
	public boolean isFirstNode() {
		return currentContext.indexInParent==0;
	}
	public boolean isLastNode() {
		return currentContext.lastNode;
	}
	public int getIndexInParent() {
		return currentContext.indexInParent;
	}

	
	// =========================================================================
	// Iteration methods
	// =========================================================================

	public boolean hasChildren() {
		return currentContext.node.hasChildren(this);
	}
	
	public boolean gotoChildren() {
		NodeIterator it = currentContext.node.getChildrenIterator(this);
		if(it==null) {
			return false;
		}

		// Now, create a context for the children
		if(count==contexts.length) {
			NodeContext[] oldc = new NodeContext[contexts.length*2];
			System.arraycopy(contexts, 0, oldc, 0, contexts.length);
			contexts = oldc;
		}
		if(contexts[count]==null) {
			contexts[count] = new NodeContext();
		}
		currentContext = contexts[count++]; 
		currentContext.iterator = it; 
		currentContext.node = null; 
		currentContext.lastNode = false; 
		currentContext.indexInParent= -1;
		
		return true;
	}

	public boolean resetIterator() {
		NodeIterator it = currentContext.node.getChildrenIterator(this);
		if(it==null) {
			return false;
		}
		
		// Get the iterator and store it into the last context
		currentContext.iterator = it; 
		currentContext.node = null; 
		currentContext.lastNode = false; 
		currentContext.indexInParent= -1;
		return true;
	}

	public boolean gotoParent() {
		count--;
		currentContext = contexts[count-1];
		currentContext.node.setNodeContext(currentContext);
		return true;
	}

	public boolean hasNext() {
		return currentContext.iterator.hasNext();
	}
	
	public ITreeNode next() {
		ITreeNode node = currentContext.iterator.next();
		if(node!=null) {
			node.setNodeContext(currentContext);
			currentContext.node = node;
			currentContext.indexInParent++;
			currentContext.lastNode = hasNext();
		}
		return node;
	}
*/	
}
