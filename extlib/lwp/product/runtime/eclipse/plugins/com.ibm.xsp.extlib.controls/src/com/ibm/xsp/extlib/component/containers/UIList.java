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

package com.ibm.xsp.extlib.component.containers;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.stylekit.ThemeControl;




/**
 * List of controls.
 */
public class UIList extends UIComponentBase implements ThemeControl {
	
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.containers.List"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.containers.List"; //$NON-NLS-1$
	
	private String style;
	private String styleClass;
	private String itemStyle;
	private String itemStyleClass;
	private String firstItemStyle;
	private String firstItemStyleClass;
	private String lastItemStyle;
	private String lastItemStyleClass;
    
	public UIList() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.CONTAINER_LIST;
	}

	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		if (null != this.styleClass) {
			return this.styleClass;
		}
		ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getItemStyle() {
		if (null != this.itemStyle) {
			return this.itemStyle;
		}
		ValueBinding _vb = getValueBinding("itemStyle"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setItemStyle(String itemStyle) {
		this.itemStyle = itemStyle;
	}

	public String getItemStyleClass() {
		if (null != this.itemStyleClass) {
			return this.itemStyleClass;
		}
		ValueBinding _vb = getValueBinding("itemStyleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setItemStyleClass(String itemStyleClass) {
		this.itemStyleClass = itemStyleClass;
	}

	public String getFirstItemStyle() {
		if (null != this.firstItemStyle) {
			return this.firstItemStyle;
		}
		ValueBinding _vb = getValueBinding("firstItemStyle"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setFirstItemStyle(String firstItemStyle) {
		this.firstItemStyle = firstItemStyle;
	}

	public String getFirstItemStyleClass() {
		if (null != this.firstItemStyleClass) {
			return this.firstItemStyleClass;
		}
		ValueBinding _vb = getValueBinding("firstItemStyleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setFirstItemStyleClass(String firstItemStyleClass) {
		this.firstItemStyleClass = firstItemStyleClass;
	}

	public String getLastItemStyle() {
		if (null != this.lastItemStyle) {
			return this.lastItemStyle;
		}
		ValueBinding _vb = getValueBinding("lastItemStyle"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLastItemStyle(String lastItemStyle) {
		this.lastItemStyle = lastItemStyle;
	}

	public String getLastItemStyleClass() {
		if (null != this.lastItemStyleClass) {
			return this.lastItemStyleClass;
		}
		ValueBinding _vb = getValueBinding("lastItemStyleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLastItemStyleClass(String lastItemStyleClass) {
		this.lastItemStyleClass = lastItemStyleClass;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.style = (String)_values[1];
		this.styleClass = (String)_values[2];
		this.itemStyle = (String)_values[3];
		this.itemStyleClass = (String)_values[4];
		this.firstItemStyle = (String)_values[5];
		this.firstItemStyleClass = (String)_values[6];
		this.lastItemStyle = (String)_values[7];
		this.lastItemStyleClass = (String)_values[8];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[9];
		_values[0] = super.saveState(_context);
        _values[1] = style;
        _values[2] = styleClass;
        _values[3] = itemStyle;
        _values[4] = itemStyleClass;
        _values[5] = firstItemStyle;
        _values[6] = firstItemStyleClass;
        _values[7] = lastItemStyle;
        _values[8] = lastItemStyleClass;
		return _values;
	}
}
