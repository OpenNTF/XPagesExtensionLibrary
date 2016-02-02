/*
 * © Copyright IBM Corp. 2010, 2015
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

package com.ibm.xsp.extlib.relational.jdbc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;

/**
 * Url Parameter.
 * @author Philippe Riand
 */
public class SqlParameter extends ValueBindingObjectImpl {

    private static final long serialVersionUID = -1L;
    
    private Object _value;

    public SqlParameter() {
    }

    public SqlParameter(Object value) {
        this._value = value;
    }

    /**
     * 
     * @param p
     * @return <code>null</code> or a non-empty list of Object values, 
     * where each object implements Serializable or is <code>null</code>.
     */
    public static List<Object> computeParameterValues(List<SqlParameter> p){
        if(p!=null && !p.isEmpty()) {
            List<Object> parameters = new ArrayList<Object>();
            for(int i=0; i<p.size(); i++) {
                SqlParameter paramComplex = p.get(i);
                Object value = paramComplex.getValue();
                if(value!=null) {
                    if(!(value instanceof Serializable)) {
                        String msg = "The value returned by the SQL parameter #{0} must be Serializable ({1})"; // $NLX-SqlParameter.Thevaluereturnedbythe0parameter1m-1$
                        msg = StringUtil.format(msg, i, value.getClass().getName());
                        throw new FacesExceptionEx(null, msg);
                    }
                }
                parameters.add(value);
            }
            return parameters;
        }
        return null;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        if (_value != null){
            return _value;
        }
        ValueBinding vb = getValueBinding("value"); //$NON-NLS-1$
        if (vb != null){
            return vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        _value = value;
        if( null != value ){
            if(!(value instanceof Serializable)) {
                String exMsg = StringUtil.format("The SQL parameter value must be Serializable ({0}).", value.getClass().getName()); // $NLX-SqlParameter.The0parametervaluemustbe12-1$
                throw new FacesExceptionEx(null,exMsg);
            }
        }
        
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = super.saveState(context);
        state[1] = _value;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        _value = (String)state[1];
    }
}