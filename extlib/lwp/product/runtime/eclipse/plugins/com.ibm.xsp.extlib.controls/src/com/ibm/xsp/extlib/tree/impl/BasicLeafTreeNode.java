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

import com.ibm.xsp.extlib.tree.ITreeNode;



/**
 * Basic Tree node which uses a set of members.
 * 
 * @author Philippe Riand
 */
public class BasicLeafTreeNode extends BasicTreeNode {

	private static final long serialVersionUID = 1L;

	public BasicLeafTreeNode() {
	}
	
	public int getType() {
		return ITreeNode.NODE_LEAF;
	}
}
