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

import java.io.Serializable;

import com.ibm.xsp.model.ViewRowData;


/**
 * Abstract ViewRowData class..
 * <p>
 * </p>
 * @author Philippe Riand
 */
public abstract class AbstractViewRowData implements ViewRowData, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public AbstractViewRowData() {
    }            
    
    public abstract Object getColumnValue(String name);

    public void setColumnValue(String name, Object value) {
        // Read only by default...
    }

    public boolean isReadOnly(String name) {
        return true;
    }

    public String getOpenPageURL(String pageName, boolean readOnly) {
        return null;
    }

    public Object getValue(String name) {
        return getColumnValue(name);
    }

    // Not used...
    public ColumnInfo getColumnInfo(String name) {
        return null;
    }
}
