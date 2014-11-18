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

import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;

/**
 * Dojo slider rule. 
 * 
 * @author Philippe Riand
 */
public class UIDojoSliderRule extends UIDojoWidget {

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.form.SliderRule"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.SliderRule"; //$NON-NLS-1$
    
    // Horizontal & Vertical Slider Rules
    private String ruleStyle;
    private Integer count;  
    private String container;   

    public UIDojoSliderRule() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        // TODO UIDojoSliderRule does not inherit from UIInput, but it inputs a data value.
        return COMPONENT_FAMILY;
    }

    public String getRuleStyle() {
        if (null != this.ruleStyle) {
            return this.ruleStyle;
        }
        ValueBinding _vb = getValueBinding("ruleStyle"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setRuleStyle(String ruleStyle) {
        this.ruleStyle = ruleStyle;
    }

    public int getCount() {
        if (null != this.count) {
            return this.count;
        }
        ValueBinding _vb = getValueBinding("count"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContainer() {
        if (null != this.container) {
            return this.container;
        }
        ValueBinding _vb = getValueBinding("container"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setContainer(String container) {
        this.container = container;
    }

    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.ruleStyle = (String)_values[1];
        this.count = (Integer)_values[2];
        this.container = (String)_values[3];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = ruleStyle;
        _values[2] = count;
        _values[3] = container;
        return _values;
    }
}