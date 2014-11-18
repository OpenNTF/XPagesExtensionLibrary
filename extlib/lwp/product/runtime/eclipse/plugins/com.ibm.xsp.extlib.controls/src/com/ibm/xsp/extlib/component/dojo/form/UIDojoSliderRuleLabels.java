/*
 * © Copyright IBM Corp. 2010, 2013
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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.dojo.form.constraints.NumberConstraints;
import com.ibm.xsp.extlib.dojo.form.SliderRuleLabel;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Dojo slider rule label. 
 * 
 * @author Philippe Riand
 */
public class UIDojoSliderRuleLabels extends UIDojoSliderRule {

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.form.SliderRuleLabels"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.SliderRuleLabels"; //$NON-NLS-1$
	
    // Horizontal & Vertical Slider Rule Labels
    private String labelStyle;
    private String labels;	
    private List<SliderRuleLabel> labelsList;
    private Integer numericMargin;	
    private Integer minimum;	
    private Integer maximum;	
    private NumberConstraints constraints;	
     
    public UIDojoSliderRuleLabels() {
		setRendererType(RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
    	return COMPONENT_FAMILY;
    }
    
	public String getLabelStyle() {
		if (null != this.labelStyle) {
			return this.labelStyle;
		}
		ValueBinding _vb = getValueBinding("labelStyle"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public String getLabels() {
		if (null != this.labels) {
			return this.labels;
		}
		ValueBinding _vb = getValueBinding("labels"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public List<SliderRuleLabel> getLabelsList() {
	    return labelsList;
	}

	public void addLabel(SliderRuleLabel label) {
	    if (labelsList == null) {
            labelsList = new ArrayList<SliderRuleLabel>();
        }
	    labelsList.add(label);
	}

	public int getNumericMargin() {
		if (null != this.numericMargin) {
			return this.numericMargin;
		}
		ValueBinding _vb = getValueBinding("numericMargin"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}
	
	public void setNumericMargin(int numericMargin) {
		this.numericMargin = numericMargin;
	}

	public int getMinimum() {
		if (null != this.minimum) {
			return this.minimum;
		}
		ValueBinding _vb = getValueBinding("minimum"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMaximum() {
		if (null != this.maximum) {
			return this.maximum;
		}
		ValueBinding _vb = getValueBinding("maximum"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public NumberConstraints getConstraints() {
		return this.constraints;
	}

	public void setConstraints(NumberConstraints constraints) {
		this.constraints = constraints;
	}

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.labelStyle = (String)_values[1];
        this.labels = (String)_values[2];
        this.labelsList = StateHolderUtil.restoreList(_context, this, _values[3]);
        this.numericMargin = (Integer)_values[4];
        this.minimum = (Integer)_values[5];
        this.maximum = (Integer)_values[6];
        this.constraints = (NumberConstraints) FacesUtil.objectFromSerializable(_context, this, _values[7]);
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[8];
		_values[0] = super.saveState(_context);
		_values[1] = labelStyle;
		_values[2] = labels;
		_values[3] = StateHolderUtil.saveList(_context, labelsList);
		_values[4] = numericMargin;
		_values[5] = minimum;
		_values[6] = maximum;
        _values[7] = FacesUtil.objectToSerializable(_context, constraints);
		return _values;
	}

	public String createLabels() {
        List<SliderRuleLabel> list = getLabelsList();
        if (list == null) {
            return getLabels();
        }
        int count = list.size();
        if (count == 0) {
            return "";
        }
        if (count == 1) {
            String label = list.get(0).getLabel();
            return prepString(label);
        }
        String[] arr = new String[count];
        String itemLabel;
        int i = 0;
        for (SliderRuleLabel item : list) {
            itemLabel = item.getLabel();
            arr[i++] = prepString(itemLabel);
        }
        return StringUtil.concatStrings(arr, ',', false);
	}

	private String prepString(String str) {
	    if (StringUtil.isEmpty(str)) {
	        return "\'\'";
	    } else {
            StringBuilder b = new StringBuilder();
            JSUtil.addSingleQuoteString(b, str);
            return b.toString();
	    }
	}
}
