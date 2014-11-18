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

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonReference;


/**
 * Dojo fade abstract class. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public abstract class AbstractFadeEffect extends AbstractDojoEffectAction {

	private Integer _duration;
	private String _easing;

	public int getDuration() {
		if (null != this._duration) {
			return this._duration;
		}
		ValueBinding _vb = getValueBinding("duration"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		}
		return -1;
	}

	public void setDuration(int duration) {
		this._duration = duration;
	}
	
	public String getEasing() {
        if (_easing == null) {
            ValueBinding vb = getValueBinding("easing"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return _easing;
	}

	public void setEasing(String easing) {
		_easing = easing;
	}

    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = _easing;
        state[2] = _duration;
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _easing = (String)values[1];
        _duration = (Integer)values[2];
    }

    @Override
	public void generateAnimation(FacesContext context, StringBuilder b, JsonJavaObject o) {
		super.generateAnimation(context, b, o);
		
		String easing = getEasing();
		if(StringUtil.isNotEmpty(easing)) {
			String fct = generateFunction(context, b, easing); 
			if(StringUtil.isNotEmpty(fct)) {
				o.put("easing", new JsonReference(fct)); //$NON-NLS-1$
			}
		}
		
		int duration = getDuration();
		if(duration>=0) {
			o.put("duration", duration); //$NON-NLS-1$
		}
	}
}
