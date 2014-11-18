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

package com.ibm.xsp.extlib.actions.client.dojo.fx;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.actions.client.dojo.AbstractFadeEffect;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


/**
 * Dojo fx slide to in action. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public class SlideToAction extends AbstractFadeEffect {
	
	private Integer _left;
	private Integer _top;

	public int getLeft() {
		if (null != this._left) {
			return this._left;
		}
		ValueBinding _vb = getValueBinding("left"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return Integer.MIN_VALUE;
	}

	public void setLeft(int left) {
		this._left = left;
	}

	public int getTop() {
		if (null != this._top) {
			return this._top;
		}
		ValueBinding _vb = getValueBinding("top"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		}
		return Integer.MIN_VALUE;
	}

	public void setTop(int top) {
		this._top = top;
	}

	
    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = _left;
        state[2] = _top;
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _left = (Integer)values[1];
        _top = (Integer)values[2];
    }
	
    @Override
	protected DojoModuleResource getDojoModuleResource(FacesContext context) {
    	return ExtLibResources.dojoFx;
    }
    
	@Override
	protected String getDojoFunction() {
		return "fx.slideTo"; //$NON-NLS-1$
	}

	@Override
    public void generateAnimation(FacesContext context, StringBuilder b, JsonJavaObject o) {
    	super.generateAnimation(context, b, o);

    	int left = getLeft();
    	if(left!=Integer.MIN_VALUE) {
    		o.put("left", left); //$NON-NLS-1$
    	}

    	int top = getTop();
    	if(top!=Integer.MIN_VALUE) {
    		o.put("top", top); //$NON-NLS-1$
    	}
	}
}
