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

import java.security.Principal;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.extlib.beans.UserBean;
import com.ibm.xsp.extlib.tree.ITreeNode;


/**
 * Leaf Tree node used to display the current user.
 * 
 * @author Philippe Riand
 */
public class UserTreeNode extends BasicComplexTreeNode {

	private static final long serialVersionUID = 1L;

    private String userField;
    
	public UserTreeNode() {
	}

	public int getType() {
		return ITreeNode.NODE_LEAF;
	}
	
	// It is rendered uniquely when running on the server
	@Override
	public boolean isRendered() {
		if(!super.isRendered()) {
			return false;
		}
		if(NotesContext.isClient()) {
			return false;
		}
		return super.isRendered();
	}
	
    public String getUserField() {
        if (null != this.userField) {
            return this.userField;
        }
        ValueBinding _vb = getValueBinding("userField"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }
    
	@Override
	public String getLabel() {
		String label = super.getLabel();
		if(StringUtil.isNotEmpty(label)) {
			return label;
		}

		FacesContext context = FacesContext.getCurrentInstance();
		
		// Look of there is a user bean
		UserBean bean = UserBean.get(context);
		if(bean!=null) {
			String name;
			String field = getUserField();
			if(StringUtil.isNotEmpty(field)) {
				name = (String)bean.getField(field);
			} else{
				name = bean.getDisplayName();
			}
			return name;
		}
		
		// Else, get it from the current user
		Principal p = context.getExternalContext().getUserPrincipal();
		if(p!=null) {
			return p.getName();
		}
		
		return null;
	}

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = super.saveState(context);
        state[1] = userField;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.userField = (String)state[1]; 
    }    
}
