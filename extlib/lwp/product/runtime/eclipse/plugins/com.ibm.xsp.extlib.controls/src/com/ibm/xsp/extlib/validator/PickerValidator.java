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


package com.ibm.xsp.extlib.validator;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.validator.ValidatorException;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.picker.data.IPickerEntry;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.validator.AbstractValidator;

/**
 * Validate a value based on a data provider.
 * <p>
 * The data provider can be assigned to the 
 * </p>
 */
public class PickerValidator extends AbstractValidator  {
    
    private IPickerData dataProvider;
    private String _for;
    
    public PickerValidator() {
    }
    
    public IPickerData getDataProvider() {
        return this.dataProvider;
    }

    public void setDataProvider(IPickerData dataProvider) {
        this.dataProvider = dataProvider;
    }

    public String getFor() {
        if (null != this._for) {
            return this._for;
        }
        ValueBinding _vb = getValueBinding("for"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // An empty value is considered as ok
        if(isEmpty(value)) {
            return;
        }

        // Else, look into the list
        // No data provider means ok
        IPickerData dp = findDataProvider();
        if(dp==null) {
            return;
        }
        
        List<IPickerEntry> val = dp.loadEntries(new Object[]{value}, null);
        if(val!=null && val.get(0)!=null) {
            return;
        }

        Severity severity = FacesMessage.SEVERITY_ERROR;
        String summary = getInvalidMessage();
        addInputInvalidMessage(context, component, severity, summary);
    }
    
    protected boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if ((value instanceof String) && StringUtil.isEmpty((String)value)) {
            return true;
        }
        return false;
    }
    
    protected IPickerData findDataProvider() {
        IPickerData d = getDataProvider();
        if(d!=null) {
            return d;
        }
        String control = getFor();
        if(StringUtil.isNotEmpty(control)) {
            UIComponent c = FacesUtil.getComponentFor(getComponent(), control);
            if(c instanceof AbstractPicker) {
                return ((AbstractPicker)c).getDataProvider();
            }
        }
        return null;
    }
    
    public String getInvalidMessage() {
        String msg = getMessage();
        if( null == msg ){
            msg = "Entered value is not in list.";  // $NLS-PickerValidator.Enteredvalueisnotinlist-1$
        }
        return msg;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = FacesUtil.objectToSerializable(context, dataProvider);
        values[2] = _for;
        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        this.dataProvider = (IPickerData) FacesUtil.objectFromSerializable(context, getComponent(), values[1]);
        this._for = (String)values[2];
    }
}