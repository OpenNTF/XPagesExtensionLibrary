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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Dojo animation action. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public class AnimatePropertyAction extends AbstractFadeEffect {

	private Integer _repeat;
	private Integer _rate;
	private Integer _delay;
	private List<AnimationProps> _properties;

	public int getRepeat() {
		if (null != this._repeat) {
			return this._repeat;
		}
		ValueBinding _vb = getValueBinding("repeat"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return Integer.MIN_VALUE;
	}

	public void setRepeat(int repeat) {
		this._repeat = repeat;
	}
	
	public int getRate() {
		if (null != this._rate) {
			return this._rate;
		}
		ValueBinding _vb = getValueBinding("rate"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return -1;
	}

	public void setRate(int rate) {
		this._rate = rate;
	}

	public int getDelay() {
		if (null != this._delay) {
			return this._delay;
		}
		ValueBinding _vb = getValueBinding("delay"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setDelay(int delay) {
		this._delay = delay;
	}

	public List<AnimationProps> getProperties() {
		return _properties;
	}

	public void addProperty(AnimationProps property) {
		if(_properties==null) {
			_properties = new ArrayList<AnimationProps>();
		}
		_properties.add(property);
	}
	
    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[5];
        state[0] = super.saveState(context);
        state[1] = _repeat;
        state[2] = _rate;
        state[3] = _delay;
        state[4] = StateHolderUtil.saveList(context, _properties);
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _repeat = (Integer)values[1];
        _rate = (Integer)values[2];
        _delay = (Integer)values[3];
        _properties = StateHolderUtil.restoreList(context, getComponent(), values[4]);
    }

    @Override
    protected String getDojoFunction() {
    	return "animateProperty"; //$NON-NLS-1$
    }
    
    @Override
    public void generateAnimation(FacesContext context, StringBuilder b, JsonJavaObject o) {
    	super.generateAnimation(context, b, o);

    	int repeat = getRepeat();
		if(repeat!=Integer.MIN_VALUE) { // Default is Integer.MIN_VALUE
			o.put("repeat", repeat); //$NON-NLS-1$
		}

    	int rate = getRate();
		if(rate>=0) { // Default is -1
			o.put("rate", rate); //$NON-NLS-1$
		}

    	int delay = getDelay();
		if(delay>0) { // Default is 0
			o.put("delay", delay); //$NON-NLS-1$
		}
    	
		List<AnimationProps> lp = getProperties();
		if(lp!=null) {
			JsonJavaObject props = new JsonJavaObject(); 
			for(AnimationProps p: lp) {
				String name = p.getName();
				if(StringUtil.isNotEmpty(name)) {
					JsonJavaObject prop = new JsonJavaObject();
					String start = p.getStart();
					if(start!=null) {
						prop.put("start", start); //$NON-NLS-1$
					}
					String end = p.getEnd();
					if(end!=null) {
						prop.put("end", end); //$NON-NLS-1$
					}
					String unit = p.getUnit();
					if(unit!=null) {
						prop.put("unit", unit); //$NON-NLS-1$
					}
					props.put(name, prop);
				}
			}
			o.put("properties", props); //$NON-NLS-1$
		}
	}
}
