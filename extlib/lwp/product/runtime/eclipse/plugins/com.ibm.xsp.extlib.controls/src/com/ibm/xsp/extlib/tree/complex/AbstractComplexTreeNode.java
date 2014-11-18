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

package com.ibm.xsp.extlib.tree.complex;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.ITreeNodeDefaultValues;


/**
 * Abstract tree node complex type.
 * 
 * @author Philippe Riand
 */
public abstract class AbstractComplexTreeNode extends ValueBindingObjectImpl implements ITreeNode {

	private static final long serialVersionUID = 1L;

	private transient ITreeNode.NodeContext context;
	
	
	public ITreeNode.NodeContext getNodeContext() {
		return context;
	}
	
	public void setNodeContext(ITreeNode.NodeContext context) {
		this.context = context;
	}
	
	public ITreeNodeDefaultValues getTreeNodeDefault() {
		if(context!=null) {
			return context.getTreeNodeDefault();
		}
		return null;
	}
	
	public String getHref() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeHref(this);
		}
		return null;
	}

	public String getOnClick() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeOnClick(this);
		}
		return null;
	}

	public String getSubmitValue() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeSubmitValue(this);
		}
		return null;
	}

	public String getImage() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeImage(this);
		}
		return null;
	}

    public String getImageAlt() {
        ITreeNodeDefaultValues tree = getTreeNodeDefault();
        if(tree!=null) {
            return tree.getNodeImageAlt(this);
        }
        return null;
    }

    public String getImageHeight() {
        ITreeNodeDefaultValues tree = getTreeNodeDefault();
        if(tree!=null) {
            return tree.getNodeImageHeight(this);
        }
        return null;
    }

    public String getImageWidth() {
        ITreeNodeDefaultValues tree = getTreeNodeDefault();
        if(tree!=null) {
            return tree.getNodeImageWidth(this);
        }
        return null;
    }

	public String getLabel() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeLabel(this);
		}
		return null;
	}

	public String getStyle() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeStyle(this);
		}
		return null;
	}

	public String getStyleClass() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeStyleClass(this);
		}
		return null;
	}

	public String getRole() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.getNodeRole(this);
		}
		return null;
	}

    public String getTitle() {
        ITreeNodeDefaultValues tree = getTreeNodeDefault();
        if(tree!=null) {
            return tree.getNodeTitle(this);
        }
        return null;
    }

	public boolean isEnabled() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.isNodeEnabled(this);
		}
		return true;
	}

	public boolean isExpanded() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.isNodeExpanded(this);
		}
		return true;
	}

	public boolean isRendered() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.isNodeRendered(this);
		}
		return true;
	}
	
	public boolean isSelected() {
		ITreeNodeDefaultValues tree = getTreeNodeDefault();
		if(tree!=null) {
			return tree.isNodeSelected(this);
		}
		return false;
	}
}
