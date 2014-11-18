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

package com.ibm.xsp.extlib.component.dojo.grid;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * View Grid Column.
 * 
 * Note that it doesn't inherit from UIColumn as there is no notion of header/footer, but
 * only content.
 * 
 * @author Philippe Riand
 */
public class UIDojoDataGridColumn extends UIComponentBase {

	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.grid.DojoDataGridColumn"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.DojoDataGrid"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.grid.DojoDataGridColumn"; //$NON-NLS-1$

	// Dojo grid column properties
	private String field; 
	private String width; 
	private String cellType; 
	private String formatter;
    private String get;
	private String options; 
	private Boolean editable; 
	private Boolean hidden;
	
	// XPages extension
	private String label; 
	
	public UIDojoDataGridColumn() {
		// No renderer to be set to this component
	    // but a renderer-type is set in case it needs 
	    // to be provided in some theme.
	    setRendererType(RENDERER_TYPE);
	}

	public String getField() {
		if (null != this.field) {
			return this.field;
		}
		ValueBinding _vb = getValueBinding("field"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getWidth() {
		if (null != this.width) {
			return this.width;
		}
		ValueBinding _vb = getValueBinding("width"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getCellType() {
		if (null != this.cellType) {
			return this.cellType;
		}
		ValueBinding _vb = getValueBinding("cellType"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getFormatter() {
		if (null != this.formatter) {
			return this.formatter;
		}
		ValueBinding _vb = getValueBinding("formatter"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}
	
	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

    public String getGet() {
        if (null != this.get) {
            return this.get;
        }
        ValueBinding _vb = getValueBinding("get"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    
    public void setGet(String get) {
        this.get = get;
    }

	public String getOptions() {
		if (null != this.options) {
			return this.options;
		}
		ValueBinding _vb = getValueBinding("options"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public boolean isEditable() {
		if (null != this.editable) {
			return this.editable;
		}
		ValueBinding _vb = getValueBinding("editable"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isHidden() {
		if (null != this.hidden) {
			return this.hidden;
		}
		ValueBinding _vb = getValueBinding("hidden"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getLabel() {
		if (null != this.label) {
			return this.label;
		}
		ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIPanel#getFamily()
	 */
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#restoreState(javax.faces.context
	 * .FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        int idx = 1;
        
		this.field = (String)_values[idx++];
        this.width = (String)_values[idx++];
        this.cellType = (String)_values[idx++];
        this.formatter = (String)_values[idx++];
        this.get = (String)_values[idx++];
        this.options = (String)_values[idx++];
        this.editable = (Boolean)_values[idx++];
        this.hidden = (Boolean)_values[idx++];
        this.label = (String)_values[idx++];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.faces.component.UIComponentBase#saveState(javax.faces.context.
	 * FacesContext)
	 */
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[11];
		_values[0] = super.saveState(_context);
        int idx = 1;

		_values[idx++] = field;
		_values[idx++] = width;
		_values[idx++] = cellType;
        _values[idx++] = formatter;
        _values[idx++] = get;
		_values[idx++] = options;
		_values[idx++] = editable;
		_values[idx++] = hidden;
		_values[idx++] = label;
		
		assert(idx==_values.length);
		return _values;
	}
}
