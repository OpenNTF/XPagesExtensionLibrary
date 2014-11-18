/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.component.misc;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Dump the content of a object to the page.
 * <p>
 * This component recursively displays the object fields
 * </p>
 */
public class UIDumpObject extends UIPanel implements ThemeControl {
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.misc.DumpObject"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.misc.DumpObject"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Misc"; //$NON-NLS-1$

    private String title;
    private Object value;
    private String objectNames;
    private Integer levels;
    private String startFilter;
    private Integer maxGridRows;
    private Boolean useBeanProperties;
    
    public UIDumpObject() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getStyleKitFamily() {
        return "Debug"; // $NON-NLS-1$
    }
    
    public String getTitle() {
        if (null != this.title) {
            return this.title;
        }
        ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } 
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Object getValue() {
        if (null != this.value) {
            return this.value;
        }
        ValueBinding _vb = getValueBinding("value"); //$NON-NLS-1$
        if (_vb != null) {
            return _vb.getValue(FacesContext.getCurrentInstance());
        } 
        return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public int getLevels() {
        if (null != this.levels) {
            return this.levels;
        }
        ValueBinding _vb = getValueBinding("levels"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (java.lang.Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }
    
    public String getObjectNames() {
        if (null != this.objectNames) {
            return this.objectNames;
        }
        ValueBinding _vb = getValueBinding("objectNames"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } 
        return null;
    }

    public void setObjectNames(String objectNames) {
        this.objectNames = objectNames;
    }
    
    public String getStartFilter() {
        if (null != this.startFilter) {
            return this.startFilter;
        }
        ValueBinding _vb = getValueBinding("startFilter"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } 
        return null;
    }

    public void setStartFilter(String startFilter) {
        this.startFilter = startFilter;
    }
    
    public int getMaxGridRows() {
        if (null != this.maxGridRows) {
            return this.maxGridRows;
        }
        ValueBinding _vb = getValueBinding("maxGridRows"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (java.lang.Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setMaxGridRows(int maxGridRows) {
        this.maxGridRows = maxGridRows;
    }
    
    public boolean isUseBeanProperties() {
        if (null != this.useBeanProperties) {
            return this.useBeanProperties;
        }
        ValueBinding _vb = getValueBinding("useBeanProperties"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.booleanValue();
            }
        } 
        return false;
    }

    public void setUseBeanProperties(boolean useBeanProperties) {
        this.useBeanProperties = useBeanProperties;
    }
    
    public Object findObject(FacesContext context) {
        String names = getObjectNames();
        if(StringUtil.isNotEmpty(names)) {
            String[] n = StringUtil.splitString(names,',');
            if(n.length==1) {
                return ExtLibUtil.resolveVariable(context, n[0]);
            } else {
                Object[] o = new Object[n.length];
                for(int i=0; i<o.length; i++) {
                    ExtLibUtil.resolveVariable(context, n[i]);
                }
                return o;
            }
        }
        Object value = getValue();
        return value;
    }
    
    public void toggleRendered() {
        setRendered(!isRendered());
    }
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.value = StateHolderUtil.restoreObjectState(_context,this,_values[1]);
        this.objectNames = (String)_values[2];
        this.levels = (Integer)_values[3];
        this.startFilter = (String)_values[4];
        this.title = (String)_values[5];
        this.maxGridRows = (Integer)_values[6];
        this.useBeanProperties = (Boolean)_values[7];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[8];
        _values[0] = super.saveState(_context);
        _values[1] = StateHolderUtil.saveObjectState(_context, value);
        _values[2] = objectNames;
        _values[3] = levels;
        _values[4] = startFilter;
        _values[5] = title;
        _values[6] = maxGridRows;
        _values[7] = useBeanProperties;
        return _values;
    }
}