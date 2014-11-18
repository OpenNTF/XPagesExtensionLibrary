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

package com.ibm.xsp.extlib.actions.client.dojo;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Dojo animation property. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public class AnimationProps extends ValueBindingObjectImpl {

	private String name;
	private String start;
	private String end;
	private String unit;
	
	public AnimationProps() {
	}
	
    public String getName() {
        if (name == null) {
            ValueBinding vb = getValueBinding("name"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStart() {
        if (start == null) {
            ValueBinding vb = getValueBinding("start"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
        if (end == null) {
            ValueBinding vb = getValueBinding("end"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getUnit() {
        if (unit == null) {
            ValueBinding vb = getValueBinding("unit"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[5];
        state[0] = super.saveState(context);
        state[1] = name;
        state[2] = start;
        state[3] = end;
        state[4] = unit;
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        name = (String)values[1];
        start = (String)values[1];
        end = (String)values[1];
        unit = (String)values[1];
    }
}
