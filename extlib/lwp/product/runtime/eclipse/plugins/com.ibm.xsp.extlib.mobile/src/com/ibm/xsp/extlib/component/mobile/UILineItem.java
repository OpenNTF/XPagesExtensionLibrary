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
 * @author Niklas Heidloff
 */

public class UILineItem extends UIDojoWidgetBase {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.LineItem"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.LineItem"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
	
	public UILineItem() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}	

	private String label;
	private String rightText;
	private String transition;
	private String moveTo;
	private String icon;

	public String getIcon() {
		if (null != this.icon) {
			return this.icon;
		}
		ValueBinding _vb = getValueBinding("icon"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setIcon(String iconUrl) {
		this.icon = iconUrl;
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
	
	public String getRightText() {
		if (null != this.rightText) {
			return this.rightText;
		}
		ValueBinding _vb = getValueBinding("rightText"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setRightText(String rightText) {
		this.rightText = rightText;
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
				
		this.label = (String)_values[1];
		this.rightText = (String)_values[2];
		this.transition = (String)_values[3];
		this.moveTo = (String)_values[4];
		this.icon = (String)_values[5];		
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[6];
		_values[0] = super.saveState(_context);		
		_values[1] = label;
		_values[2] = rightText;
		_values[3] = transition;
		_values[4] = moveTo;
		_values[5] = icon;		
		return _values;
	}

	public String getStyleKitFamily() {
		// TODO Auto-generated method stub
		return null;
	}
}
