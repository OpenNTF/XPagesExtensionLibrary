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
import com.ibm.xsp.extlib.tree.ITreeNodeDefaultValues;




/**
 * Abstract default values for a tree.
 * @author Philippe Riand
 */
public abstract class AbstractTreeNodeDefault implements ITreeNodeDefaultValues{

	public String getNodeHref(ITreeNode node) {
		return null;
	}

	public String getNodeImage(ITreeNode node) {
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

}
