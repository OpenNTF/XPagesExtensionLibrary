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

import java.util.Iterator;
import java.util.List;

import com.ibm.xsp.extlib.tree.ITreeNode;


/**
 * Some tree utility.
 * @author Philippe Riand
 */
public class TreeUtil {

//	private static class ListIterator implements ITree.NodeIterator {
//		private List<ITreeNode> list;
//		private int current;
//		private int count;
//		ListIterator(List<ITreeNode> list, int start, int count) {
//			this.list = list;
//			this.current = start;
//			this.count = count;
//		}
//		public boolean hasNext() {
//			return current<list.size() && count>0;
//		}
//		public ITreeNode next() {
//			count--;
//			return list.get(current++);
//		}
//	}

	private static class ListIterator implements ITreeNode.NodeIterator {
		ITreeNode current;
		Iterator<ITreeNode> mainIterator;
		ITreeNode.NodeIterator listIterator;
		int count;
		ListIterator(List<ITreeNode> list, int start, int count) {
			this.mainIterator = list.iterator();
			this.count = count;

			if(start>0) {
				// todo...
			}
			moveToNext();
		}
		public boolean hasNext() {
			return current!=null;
		}
		public ITreeNode next() {
			ITreeNode res = current;
			moveToNext();
			return res;
		}
		private void moveToNext() {
			current = null;
			
			// If no more entries to retrieve, then stop!
			if(count==0) {
				return;
			}
			
			// If there is a child list iterator, use it
			if(listIterator!=null) {
				if(listIterator.hasNext()) {
					current = listIterator.next();
					count--;
					return;
				}
				listIterator = null;
			}
			
			while(mainIterator.hasNext()) {
				current = mainIterator.next();
				if(current.getType()==ITreeNode.NODE_NODELIST) {
					// The current doesn't count and we try to look at its children
					listIterator = current.iterateChildren(0, Integer.MAX_VALUE);
					if(listIterator.hasNext()) {
						current = listIterator.next();
						return;
					}
					listIterator = null;
					current = null;
				} else {
					// The current counts and will be used
					count--;
					return;
				}
			}
		}
	}
	
	public static ITreeNode.NodeIterator getIterator(List<ITreeNode> list, int start, int count) {
		if(list!=null) {
			return new ListIterator(list,start,count);
		}
		return null;
	}
	
/**
	private static class TransparentIterator implements Iterator<ITreeNode> {
		ITreeNode current;
		Stack<Iterator<ITreeNode>> parents;
		Iterator<ITreeNode> currentIterator;
		TransparentIterator(Iterator<ITreeNode> iterator) {
			moveToNext();
		}
		public boolean hasNext() {
			return current!=null;
		}
		public ITreeNode next() {
			ITreeNode res = current;
			moveToNext();
			return res;
		}
		private void moveToNext() {
			current = null;
			if(currentIterator.hasNext()) {
				current = currentIterator.next();
				if(current.isTransparent()) {
					if(parents==null) {
						parents = new Stack<Iterator<ITreeNode>>();
					}
					parents.push(currentIterator);
					currentIterator = current.iterateChildren(0, Integer.MAX_VALUE);
					moveToNext();
				}
			} else {
				if(parents!=null) {
					currentIterator = parents.pop();
					moveToNext();
				}
			}
		}
		public void remove() {
		}
	}
 */
}
