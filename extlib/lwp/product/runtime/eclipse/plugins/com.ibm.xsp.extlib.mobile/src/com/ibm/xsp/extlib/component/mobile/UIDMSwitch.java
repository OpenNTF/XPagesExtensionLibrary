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
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;

/**
 * Mobile page button.
 * <p>
 * </p>
 */
public class UIDMSwitch extends UIDojoWidgetBase { //UIDojoCheckbox code should be used in here

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.DojoSwitch"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.Switch"; //$NON-NLS-1$
	private String leftLabel;
	private String rightLabel;
	private String value; //remove this. Should be inheriting from dojo checkbox??
	private String onClick;
	private String onTouchStart;
	private String onTouchEnd;
	private String onTouchMove;
	private String onStateChanged;
	
	public UIDMSwitch() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}	
	
	public void setLeftLabel(String label) {
		this.leftLabel= label;
	}	
	
	public String getLeftLabel() {
		if (null != this.leftLabel) {
			return this.leftLabel;
		}
		ValueBinding _vb = getValueBinding("leftLabel"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
		
	public void setRightLabel(String label) {
		this.rightLabel= label;
	}
	
	public String getRightLabel() {
		if (null != this.rightLabel) {
			return this.rightLabel;
		}
		ValueBinding _vb = getValueBinding("rightLabel"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setOnClick(String value){
        this.onClick = value;
    }
    public String getOnClick(){
        if(null != this.onClick){
            return this.onClick;
        }
        ValueBinding _vb = getValueBinding("onClick"); //$NON-NLS-1$
        if(null != _vb) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(null != val){
                return val;
            }
        }
        return null;
    }
    
	public void setOnTouchStart(String value){
        this.onTouchStart = value;
    }
    public String getOnTouchStart(){
        if(null != this.onTouchStart){
            return this.onTouchStart;
        }
        ValueBinding _vb = getValueBinding("onTouchStart"); //$NON-NLS-1$
        if(null != _vb) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(null != val){
                return val;
            }
        }
        return null;
    }
	public void setOnTouchEnd(String value){
        this.onTouchEnd = value;
    }
    public String getOnTouchEnd(){
        if(null != this.onTouchEnd){
            return this.onTouchEnd;
        }
        ValueBinding _vb = getValueBinding("onTouchEnd"); //$NON-NLS-1$
        if(null != _vb) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(null != val){
                return val;
            }
        }
        return null;
    }
	public void setOnTouchMove(String value){
        this.onTouchMove = value;
    }
    public String getOnTouchMove(){
        if(null != this.onTouchMove){
            return this.onTouchMove;
        }
        ValueBinding _vb = getValueBinding("onTouchMove"); //$NON-NLS-1$
        if(null != _vb) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(null != val){
                return val;
            }
        }
        return null;
    }
    
    public void setOnStateChanged(String value){
        this.onStateChanged = value;
    }
    public String getOnStateChanged(){
        if(null != this.onStateChanged){
            return this.onStateChanged;
        }
        ValueBinding _vb = getValueBinding("onStateChanged"); //$NON-NLS-1$
        if(null != _vb) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(null != val){
                return val;
            }
        }
        return null;
    }
	
	public void setValue(String value) {
		this.value= value;
	}	
	
	public String getValue() {
		if (null != this.value) {
			return this.value;
		}
		ValueBinding _vb = getValueBinding("value"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);					
		this.leftLabel = (String)_values[1];
		this.rightLabel = (String)_values[2];
		this.value = (String)_values[3];
	    this.onClick = (String) _values[4];
	    this.onTouchStart = (String) _values[5];
	    this.onTouchEnd = (String) _values[6];
	    this.onTouchMove = (String) _values[7];
	    this.onStateChanged = (String) _values[8];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[9];
		_values[0] = super.saveState(_context);		
		_values[1] = leftLabel;
		_values[2] = rightLabel;
		_values[3] = value;	    
	    _values[4] = onClick;	    
	    _values[5] = onTouchStart;
	    _values[6] = onTouchEnd;
	    _values[7] = onTouchMove;
	    _values[8] = onStateChanged;

		return _values;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.MOBILE_FORM_TOGGLESWITCH;
	}	
}

