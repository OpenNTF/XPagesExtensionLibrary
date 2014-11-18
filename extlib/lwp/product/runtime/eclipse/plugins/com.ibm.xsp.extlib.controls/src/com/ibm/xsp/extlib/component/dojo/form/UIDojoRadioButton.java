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

package com.ibm.xsp.extlib.component.dojo.form;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.util.FacesUtil;


/**
 * Dojo RadioButton component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoRadioButton extends UIDojoButton {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.RadioButton"; //$NON-NLS-1$
	
    // Radio Button
    private Object _selectValue;
    private String _groupName;
	private Integer _skipContainers;

    public UIDojoRadioButton() {
		setRendererType(RENDERER_TYPE);
	}

    public Object getSelectedValue() {
    	if (_selectValue != null) {
    		return _selectValue;
    	}
    	
        ValueBinding vb = getValueBinding("selectedValue"); //$NON-NLS-1$
        if (vb != null) {
        	return vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setSelectedValue(Object selectedValue) {
    	_selectValue = selectedValue;
    }

    public String getGroupName() {
    	if (_groupName != null) {
    		return _groupName;
    	}
    	
        ValueBinding vb = getValueBinding("groupName"); //$NON-NLS-1$
        if (vb != null) {
        	return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setGroupName(String groupName) {
    	_groupName = groupName;
    }

	public int getSkipContainers() {
		if(_skipContainers!=null) {
			return _skipContainers;
		}
		ValueBinding binding = getValueBinding("skipContainers"); //$NON-NLS-1$
		if (binding != null) {
			Object result = binding.getValue(getFacesContext());
			if (result != null) {
				return ((Number)result).intValue();
			}
        }
		return 0;
	}

	/**
	 * @param skip Set the number of containers to skip
	 * @designer.publicmethod
	 */
	public void setSkipContainers(int skip) {
		_skipContainers = skip;
	}
    
    public String getClientGroupName(FacesContext context) {
        String name = getGroupName();
        if (name == null) {
            name = getClientId(context);
        }
        else {
            int skip = getSkipContainers();
            UIComponent nc = (UIComponent)FacesUtil.getNamingContainer(this, skip);
            name = nc.getClientId(context) + ":" + name;
        }
        return name;
    }

    // State management
    @Override
	public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = _selectValue;
        values[2] = _groupName;
        values[3] = _skipContainers;
        return values;
    }
    
    @Override
	public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _selectValue = values[1];
        _groupName = (java.lang.String)values[2];
        _skipContainers = (Integer)values[3];
    }
    
}
