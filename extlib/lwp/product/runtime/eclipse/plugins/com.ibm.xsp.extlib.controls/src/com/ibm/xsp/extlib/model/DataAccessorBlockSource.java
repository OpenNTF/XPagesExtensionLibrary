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

package com.ibm.xsp.extlib.model;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Generic data source that handle data coming from a data accessor.
 * @author Philippe Riand
 */
public abstract class DataAccessorBlockSource extends DataAccessorSource {

    private Integer maxBlockCount;
    private Integer timeout;
    
    protected DataAccessorBlockSource() {
    }

    public int getMaxBlockCount() {
        if (null != this.maxBlockCount) {
            return this.maxBlockCount;
        }
        ValueBinding _vb = getValueBinding("maxBlockCount"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }
    public void setMaxBlockCount(int maxBlockCount) {
        this.maxBlockCount = maxBlockCount;
    }

    public int getTimeout() {
        if (null != this.timeout) {
            return this.timeout;
        }
        ValueBinding _vb = getValueBinding("timeout"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    
    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = maxBlockCount;
        state[2] = timeout;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        maxBlockCount = (Integer)values[1];
        timeout = (Integer)values[2];
    }
    
    @Override
    protected abstract DataBlockAccessor createAccessor();
    
}
