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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;

/**
 * Dojo Data Grid. 
 * 
 * @author Philippe Riand
 */
public class UIDojoDataGrid extends UIDojoWidget {

	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.grid.DojoDataGrid"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.DojoDataGrid"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.grid.DojoDataGrid"; //$NON-NLS-1$

	private String jsId;
	private String store;
	private String rowSelector;
	private String selectionMode;
	private String headerMenu;
	private Integer autoHeight;
	private Boolean singleClickEdit;
	private String loadingMessage;
	private String errorMessage;
	private Integer rowsPerPage;
	private String query; 

	private Boolean selectable;
	private Integer updateDelay;
	private String initialWidth;
	private Boolean escapeHTMLInData;		

	private String onStyleRow;
	private String onRowClick;
	private String onRowDblClick;
	private String onRowContextMenu;

	// XPages extensions?
	private String storeComponentId;
	
	public UIDojoDataGrid() {
        setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getJsId() {
		if (null != this.jsId) {
			return this.jsId;
		}
		ValueBinding _vb = getValueBinding("jsId"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return getId();
		}
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public String getStore() {
		if (null != this.store) {
			return this.store;
		}
		ValueBinding _vb = getValueBinding("store"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getRowSelector() {
		if (null != this.rowSelector) {
			return this.rowSelector;
		}
		ValueBinding _vb = getValueBinding("rowSelector"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setRowSelector(String rowSelector) {
		this.rowSelector = rowSelector;
	}

	public String getSelectionMode() {
		if (null != this.selectionMode) {
			return this.selectionMode;
		}
		ValueBinding _vb = getValueBinding("selectionMode"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	public String getHeaderMenu() {
		if (null != this.headerMenu) {
			return this.headerMenu;
		}
		ValueBinding _vb = getValueBinding("headerMenu"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setHeaderMenu(String headerMenu) {
		this.headerMenu = headerMenu;
	}

	public int getAutoHeight() {
		if (null != this.autoHeight) {
			return this.autoHeight;
		}
		ValueBinding _vb = getValueBinding("autoHeight"); //$NON-NLS-1$
		if (_vb != null) {
			Number val =  (java.lang.Number) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val.intValue();
			}
		}
		return 0;
	}

	public void setAutoHeight(int autoHeight) {
		this.autoHeight = autoHeight;
	}

	public boolean isSingleClickEdit() {
		if (null != this.singleClickEdit) {
			return this.singleClickEdit;
		}
		ValueBinding _vb = getValueBinding("singleClickEdit"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setSingleClickEdit(boolean singleClickEdit) {
		this.singleClickEdit = singleClickEdit;
	}

	public String getLoadingMessage() {
		if (null != this.loadingMessage) {
			return this.loadingMessage;
		}
		ValueBinding _vb = getValueBinding("loadingMessage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setLoadingMessage(String loadingMessage) {
		this.loadingMessage = loadingMessage;
	}

	public String getErrorMessage() {
		if (null != this.errorMessage) {
			return this.errorMessage;
		}
		ValueBinding _vb = getValueBinding("errorMessage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSelectable() {
		if (null != this.selectable) {
			return this.selectable;
		}
		ValueBinding _vb = getValueBinding("selectable"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public int getUpdateDelay() {
		if (null != this.updateDelay) {
			return this.updateDelay;
		}
		ValueBinding _vb = getValueBinding("updateDelay"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (java.lang.Number) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val.intValue();
			}
		}
		return -1;
	}

	public void setUpdateDelay(int updateDelay) {
		this.updateDelay = updateDelay;
	}

	public String getInitialWidth() {
		if (null != this.initialWidth) {
			return this.initialWidth;
		}
		ValueBinding _vb = getValueBinding("initialWidth"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setInitialWidth(String initialWidth) {
		this.initialWidth = initialWidth;
	}

	public boolean isEscapeHTMLInData() {
		if (null != this.escapeHTMLInData) {
			return this.escapeHTMLInData;
		}
		ValueBinding _vb = getValueBinding("escapeHTMLInData"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		}
		return true;
	}

	public void setEscapeHTMLInData(boolean escapeHTMLInData) {
		this.escapeHTMLInData = escapeHTMLInData;
	}

	public String getOnStyleRow() {
		if (null != this.onStyleRow) {
			return this.onStyleRow;
		}
		ValueBinding _vb = getValueBinding("onStyleRow"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnStyleRow(String onStyleRow) {
		this.onStyleRow = onStyleRow;
	}

	public String getOnRowClick() {
		if (null != this.onRowClick) {
			return this.onRowClick;
		}
		ValueBinding _vb = getValueBinding("onRowClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnRowClick(String onRowClick) {
		this.onRowClick = onRowClick;
	}

	public String getOnRowDblClick() {
		if (null != this.onRowDblClick) {
			return this.onRowDblClick;
		}
		ValueBinding _vb = getValueBinding("onRowDblClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnRowDblClick(String onRowDblClick) {
		this.onRowDblClick = onRowDblClick;
	}

	public String getOnRowContextMenu() {
		if (null != this.onRowContextMenu) {
			return this.onRowContextMenu;
		}
		ValueBinding _vb = getValueBinding("onRowContextMenu"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnRowContextMenu(String onRowContextMenu) {
		this.onRowContextMenu = onRowContextMenu;
	}


	public String getStoreComponentId() {
		if (null != this.storeComponentId) {
			return this.storeComponentId;
		}
		ValueBinding _vb = getValueBinding("storeComponentId"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setStoreComponentId(String storeComponentId) {
		this.storeComponentId = storeComponentId;
	}

	public String getQuery() {
		if (null != this.query) {
			return this.query;
		}
		ValueBinding _vb = getValueBinding("query"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
    public int getRowsPerPage() {
        if (rowsPerPage!=null) {
            return rowsPerPage;
        }
        // check for a value binding
        ValueBinding valueBinding = getValueBinding("rowsPerPage"); //$NON-NLS-1$
        if (valueBinding != null) {
            Object result = valueBinding.getValue(getFacesContext());
            if (result != null) {
                return (Integer)result;
            }
        } 
        
        return -1;
    }
    
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
	
//	public void setQuery(String query) {
//		this.query = query;
//	}
//	public String getRestComponentId() {
//		if (null != this.restComponentId) {
//			return this.restComponentId;
//		}
//		ValueBinding _vb = getValueBinding("restComponentId"); //$NON-NLS-1$
//		if (_vb != null) {
//			return (java.lang.String) _vb.getValue(getFacesContext());
//		} else {
//			return null;
//		}
//	}


	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        
		this.jsId = (String)_values[1];
		this.store = (String)_values[2];
		this.rowSelector = (String)_values[3];
		this.selectionMode = (String)_values[4];
		this.headerMenu = (String)_values[5];
		this.autoHeight = (Integer)_values[6];
		this.singleClickEdit = (Boolean)_values[7];
		this.loadingMessage = (String)_values[8];
		this.errorMessage = (String)_values[9];
		this.selectable = (Boolean)_values[10];
		this.updateDelay = (Integer)_values[11];
		this.initialWidth = (String)_values[12];
		this.escapeHTMLInData = (Boolean)_values[13];
		this.onStyleRow = (String)_values[14];
		this.onRowClick = (String)_values[15];
		this.onRowDblClick = (String)_values[16];
		this.onRowContextMenu = (String)_values[17];
		this.storeComponentId = (String)_values[18];
		this.rowsPerPage = (Integer)_values[19];
		this.query = (String)_values[20];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[21];
		_values[0] = super.saveState(_context);
		_values[1] = jsId;
		_values[2] = store;
		_values[3] = rowSelector;
		_values[4] = selectionMode;
		_values[5] = headerMenu;
		_values[6] = autoHeight;
		_values[7] = singleClickEdit;
		_values[8] = loadingMessage;
		_values[9] = errorMessage;
		_values[10] = selectable;
		_values[11] = updateDelay;
		_values[12] = initialWidth;
		_values[13] = escapeHTMLInData;
		_values[14] = onStyleRow;
		_values[15] = onRowClick;
		_values[16] = onRowDblClick;
		_values[17] = onRowContextMenu;
		_values[18] = storeComponentId;
		_values[19] = rowsPerPage;
		_values[20] = query;
		return _values;
	}
}