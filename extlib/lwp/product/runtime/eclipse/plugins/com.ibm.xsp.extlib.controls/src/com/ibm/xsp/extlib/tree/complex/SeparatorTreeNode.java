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


import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.tree.ITreeNode;



/**
 * Leaf Tree node that acts as a separator.
 * 
 * @author Philippe Riand
 */
public class SeparatorTreeNode extends AbstractComplexTreeNode {

	private static final long serialVersionUID = 1L;

    private String style;
    private String styleClass;
    private Boolean rendered;
	
	public SeparatorTreeNode() {
	}

    @Override
    public String getStyle() {
        if (null != this.style) {
            return this.style;
        }
        ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getStyle();
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        if (null != this.styleClass) {
            return this.styleClass;
        }
        ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getStyleClass();
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public boolean isRendered() {
        if (null != this.rendered) {
            return this.rendered;
        }
        ValueBinding _vb = getValueBinding("rendered"); //$NON-NLS-1$
        if (_vb != null) {
            Object obj = _vb.getValue(getFacesContext());
            if( obj instanceof Boolean ){// non-null
                return (Boolean) obj;
            }
        }
        return super.isRendered();
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = style;
        state[2] = styleClass;
        state[3] = rendered;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.style = (String)state[1]; 
        this.styleClass = (String)state[2]; 
        this.rendered = (Boolean)state[3]; 
    }
	
	
	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return null;
	}

	public int getType() {
		return ITreeNode.NODE_SEPARATOR;
	}
}
