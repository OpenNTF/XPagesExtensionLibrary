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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class UIDojoComboBox extends UIDojoValidationTextBox {
	
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.ComboBox"; //$NON-NLS-1$
	
	private Integer pageSize;
	private String store;
	private Boolean autoComplete;
	private Integer searchDelay;
	private String searchAttr;
	private String queryExpr;
	private Boolean ignoreCase;
	private Boolean hasDownArrow;
	
	public UIDojoComboBox() {
		setRendererType(RENDERER_TYPE); //$NON-NLS-1$
	}
    
	public int getPageSize() {
		if (null != this.pageSize) {
			return this.pageSize;
		}
		ValueBinding _vb = getValueBinding("pageSize"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getStore() {
		if (null != this.store) {
			return this.store;
		}
		ValueBinding _vb = getValueBinding("store"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStore(String store) {
		this.store = store;
	}

	public boolean isAutoComplete() {
		if (null != this.autoComplete) {
			return this.autoComplete;
		}
		ValueBinding _vb = getValueBinding("autoComplete"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}

	public int getSearchDelay() {
		if (null != this.searchDelay) {
			return this.searchDelay;
		}
		ValueBinding _vb = getValueBinding("searchDelay"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setSearchDelay(int searchDelay) {
		this.searchDelay = searchDelay;
	}

	public String getSearchAttr() {
		if (null != this.searchAttr) {
			return this.searchAttr;
		}
		ValueBinding _vb = getValueBinding("searchAttr"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setSearchAttr(String searchAttr) {
		this.searchAttr = searchAttr;
	}

	public String getQueryExpr() {
		if (null != this.queryExpr) {
			return this.queryExpr;
		}
		ValueBinding _vb = getValueBinding("queryExpr"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setQueryExpr(String queryExpr) {
		this.queryExpr = queryExpr;
	}

	public boolean isIgnoreCase() {
		if (null != this.ignoreCase) {
			return this.ignoreCase;
		}
		ValueBinding _vb = getValueBinding("ignoreCase"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean isHasDownArrow() {
		if (null != this.hasDownArrow) {
			return this.hasDownArrow;
		}
		ValueBinding _vb = getValueBinding("hasDownArrow"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setHasDownArrow(boolean hasDownArrow) {
		this.hasDownArrow = hasDownArrow;
	}

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.pageSize = (Integer)_values[1];
        this.store = (String)_values[2];
        this.autoComplete = (Boolean)_values[3];
        this.searchDelay = (Integer)_values[4];
        this.searchAttr = (String)_values[5];
        this.queryExpr = (String)_values[6];
        this.ignoreCase = (Boolean)_values[7];
        this.hasDownArrow = (Boolean)_values[8];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[9];
		_values[0] = super.saveState(_context);
		_values[1] = pageSize;
		_values[2] = store;
		_values[3] = autoComplete;
		_values[4] = searchDelay;
		_values[5] = searchAttr;
		_values[6] = queryExpr;
		_values[7] = ignoreCase;
		_values[8] = hasDownArrow;
		return _values;
	}

}
