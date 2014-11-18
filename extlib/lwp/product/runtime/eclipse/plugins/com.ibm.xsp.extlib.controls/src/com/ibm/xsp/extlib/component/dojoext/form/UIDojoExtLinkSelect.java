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

package com.ibm.xsp.extlib.component.dojoext.form;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.form.UIDojoFormWidgetBase;
import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dojo component used to input a value with links. 
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class UIDojoExtLinkSelect extends UIDojoFormWidgetBase {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojoext.form.LinkSelect"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojoext.form.LinkSelect"; //$NON-NLS-1$
    
    private IValuePickerData dataProvider;
    private String itemStyle;
    private String itemStyleClass;
    private String firstItemStyle;
    private String firstItemStyleClass;
    private String lastItemStyle;
    private String lastItemStyleClass;
    private String enabledLinkStyle;
    private String enabledLinkStyleClass;
    private String disabledLinkStyle;
    private String disabledLinkStyleClass;
    
    public UIDojoExtLinkSelect() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DOJO_FORM_LINKSELECT;
    }
    
    public IValuePickerData getDataProvider() {
        return this.dataProvider;
    }

    public void setDataProvider(IValuePickerData dataProvider) {
        this.dataProvider = dataProvider;
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
    
    /**
     * @return the enabledLinkStyle
     */
    public String getEnabledLinkStyle() {
        if (null != this.enabledLinkStyle) {
            return this.enabledLinkStyle;
        }
        ValueBinding _vb = getValueBinding("enabledLinkStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    /**
     * @param enabledLinkStyle the enabledLinkStyle to set
     */
    public void setEnabledLinkStyle(String enabledLinkStyle) {
        this.enabledLinkStyle = enabledLinkStyle;
    }

    /**
     * @return the enabledLinkStyleClass
     */
    public String getEnabledLinkStyleClass() {
        if (null != this.enabledLinkStyleClass) {
            return this.enabledLinkStyleClass;
        }
        ValueBinding _vb = getValueBinding("enabledLinkStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    /**
     * @param enabledLinkStyleClass the enabledLinkStyleClass to set
     */
    public void setEnabledLinkStyleClass(String enabledLinkStyleClass) {
        this.enabledLinkStyleClass = enabledLinkStyleClass;
    }

    /**
     * @return the disabledLinkStyle
     */
    public String getDisabledLinkStyle() {
        if (null != this.disabledLinkStyle) {
            return this.disabledLinkStyle;
        }
        ValueBinding _vb = getValueBinding("disabledLinkStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    /**
     * @param disabledLinkStyle the disabledLinkStyle to set
     */
    public void setDisabledLinkStyle(String disabledLinkStyle) {
        this.disabledLinkStyle = disabledLinkStyle;
    }

    /**
     * @return the disabledLinkStyleClass
     */
    public String getDisabledLinkStyleClass() {
        if (null != this.disabledLinkStyleClass) {
            return this.disabledLinkStyleClass;
        }
        ValueBinding _vb = getValueBinding("disabledLinkStyleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    /**
     * @param disabledLinkStyleClass the disabledLinkStyleClass to set
     */
    public void setDisabledLinkStyleClass(String disabledLinkStyleClass) {
        this.disabledLinkStyleClass = disabledLinkStyleClass;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.dataProvider = (IValuePickerData) FacesUtil.objectFromSerializable(_context, this, _values[1]);
        this.itemStyle = (String)_values[2];
        this.itemStyleClass = (String)_values[3];
        this.firstItemStyle = (String)_values[4];
        this.firstItemStyleClass = (String)_values[5];
        this.lastItemStyle = (String)_values[6];
        this.lastItemStyleClass = (String)_values[7];
        this.enabledLinkStyle = (String)_values[8];
        this.enabledLinkStyleClass = (String)_values[9];
        this.disabledLinkStyle = (String)_values[10];
        this.disabledLinkStyleClass = (String)_values[11];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[12];
        _values[0] = super.saveState(_context);
        _values[1] = FacesUtil.objectToSerializable(_context, dataProvider);
        _values[2] = itemStyle;
        _values[3] = itemStyleClass;
        _values[4] = firstItemStyle;
        _values[5] = firstItemStyleClass;
        _values[6] = lastItemStyle;
        _values[7] = lastItemStyleClass;
        _values[8] = enabledLinkStyle;
        _values[9] = enabledLinkStyleClass;
        _values[10] = disabledLinkStyle;
        _values[11] = disabledLinkStyleClass;
        return _values;
    }
}