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

package com.ibm.xsp.extlib.component.rest;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.util.FacesUtil;

/**
 * Component that handles REST services.
 * <p>
 * This is the user facing component, that can be inserted into a page.
 * </p>
 * @author priand
 */
public class UIRestService extends UIBaseRestService {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.rest.RestService"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.RestService"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.rest.RestService"; //$NON-NLS-1$
	
	private String pathInfo;
	private IRestService service;
	private String jsId;
    private Boolean ignoreRequestParams;
	private Boolean preventDojoStore;
    private Boolean state;
	
	public UIRestService() {
		setRendererType(RENDERER_TYPE); //$NON-NLS-1$
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getPathInfo() {
		if (null != this.pathInfo) {
			return this.pathInfo;
		}
		ValueBinding _vb = getValueBinding("pathInfo"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	@Override
	public IRestService getService() {
		return this.service;
	}

	public void setService(IRestService service) {
		this.service = service;
	}

	@Override
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
	
	@Override
    public boolean isIgnoreRequestParams() {
        if (ignoreRequestParams!=null) {
            return ignoreRequestParams;
        }
        ValueBinding vb = getValueBinding("ignoreRequestParams"); //$NON-NLS-1$
        if (vb != null) {
            Object result = vb.getValue(getFacesContext());
            if (result != null) {
                return ((Boolean) result).booleanValue();
            }
        }
        return false;
    }

    public void setIgnoreRequestParams(boolean ignoreRequestParams) {
        this.ignoreRequestParams = ignoreRequestParams;
    }

	@Override
	public boolean isPreventDojoStore() {
		if (null != this.preventDojoStore) {
			return this.preventDojoStore;
		}
		ValueBinding _vb = getValueBinding("preventDojoStore"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val =  (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setPreventDojoStore(boolean preventDojoStore) {
		this.preventDojoStore = preventDojoStore;
	}

	@Override
    public boolean isState() {
        if (null != this.state) {
            return this.state;
        }
        ValueBinding _vb = getValueBinding("state"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setState(boolean state) {
        this.state = state;
    }
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.pathInfo = (java.lang.String) _values[1];
        this.service = (IRestService) FacesUtil.objectFromSerializable(_context, this, _values[2]);
        this.jsId = (String)_values[3];
        this.ignoreRequestParams = (Boolean)_values[4];
        this.preventDojoStore = (Boolean)_values[5];
        this.state = (Boolean)_values[6];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[7];
		_values[0] = super.saveState(_context);
		_values[1] = pathInfo;
        _values[2] = FacesUtil.objectToSerializable(_context, service);
		_values[3] = jsId;
		_values[4] = ignoreRequestParams;
		_values[5] = preventDojoStore;
        _values[6] = state;
		return _values;
	}
}
