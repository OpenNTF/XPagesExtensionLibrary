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

package com.ibm.xsp.extlib.component.mobile;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;

/**
 * Mobile page header.
 * <p>
 * </p>
 */
public class UIDMHeading extends UIDojoWidgetBase {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.DojoHeading"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.DojoHeading"; //$NON-NLS-1$
	
	private String back;
	private String href;
	private String moveTo;
	private String transition;
	private String label;
	
	public UIDMHeading() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}	

	public String getBack() {
		if (null != this.back) {
			return this.back;
		}
		ValueBinding _vb = getValueBinding("back"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setBack(String back) {
		this.back = back;
	}		

	/**
	 * @deprecated Use {@link #getHref()} instead
	 */
	public String getHRef() {
		return getHref();
	}

	public String getHref() {
		if (null != this.href) {
			return this.href;
		}
		ValueBinding _vb = getValueBinding("href"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	/**
	 * @deprecated Use {@link #setHref(String)} instead
	 */
	public void setHRef(String href) {
		setHref(href);
	}

	public void setHref(String href) {
		this.href = href;
	}		

	public String getMoveTo() {
		if (null != this.moveTo) {
			return this.moveTo;
		}
		ValueBinding _vb = getValueBinding("moveTo"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setMoveTo(String moveTo) {
		this.moveTo = moveTo;
	}		

	public String getTransition() {
		if (null != this.transition) {
			return this.transition;
		}
		ValueBinding _vb = getValueBinding("transition"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setTransition(String transition) {
		this.transition = transition;
	}		

	public String getLabel() {
		if (null != this.label) {
			return this.label;
		}
		ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
		
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
				
		this.back = (String)_values[1];
		this.href = (String)_values[2];
		this.moveTo = (String)_values[3];
		this.transition = (String)_values[4];
		this.label = (String)_values[5];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[6];
		_values[0] = super.saveState(_context);
		
		_values[1] = back;
		_values[2] = href;
		_values[3] = moveTo;
		_values[4] = transition;
		_values[5] = label;
		return _values;
	}

	public String getStyleKitFamily() {
		// TODO Auto-generated method stub
		return null;
	}
}
