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
package com.ibm.xsp.extlib.component.picker.data;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.ManagedBeanUtil;


/**
 * Bean data provider for a value picker.
 * <p>
 * This data provider is delegating to a bean that implements the IValuePickerData
 * </p>
 */
public class BeanValuePickerData extends ValueBindingObjectImpl implements IValuePickerData {

    private String dataBean;
    private transient IValuePickerData bean;

    public BeanValuePickerData() {
    }

    public String[] getSourceLabels() {
        return null;
    }
    
    public String getDataBean() {
        if (dataBean != null) {
            return dataBean;
        }        
        ValueBinding vb = getValueBinding("dataBean"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setDataBean(String dataBean) {
        this.dataBean = dataBean;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.dataBean = (String)_values[1];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = dataBean;
        return _values;
    }

    protected IValuePickerData getBeanInstance() {
        if(bean==null) {
            String beanName = getDataBean();
            Object b = ManagedBeanUtil.getBean(FacesContext.getCurrentInstance(), beanName);
            if(b!=null) {
                if(!(b instanceof IValuePickerData)) {
                    throw new FacesExceptionEx(null,"Bean {0}({1}) is not a IValuePickerData",beanName,b.getClass()); // $NLX-BeanValuePickerData.Bean01isnotaIValuePickerData-1$
                }
                bean = (IValuePickerData)b;
            } else {
                throw new FacesExceptionEx(null,"Bean {0} does not exist",beanName); // $NLX-BeanValuePickerData.Bean0doesnotexist-1$
            }
        }
        return bean;
    }
    
    
    // ===================================================================
    // Value picker delegation
    // ===================================================================

    public boolean hasCapability(int capability) {
        IValuePickerData bean = getBeanInstance();
        return bean.hasCapability(capability);
    }

    public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames) {
        IValuePickerData bean = getBeanInstance();
        return bean.loadEntries(ids,attributeNames);
    }

    public IPickerResult readEntries(IPickerOptions options) {
        IValuePickerData bean = getBeanInstance();
        return bean.readEntries(options);
    }
}