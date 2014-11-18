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


/**
 * Object that encapsulates data access.
 * <p>
 * This object is specifically designed to work with an extended iterator data model.<br/>
 * A DataAccessor has 3 modes of working:
 * <ul>
 *  <li>Live access to data (just using count and get)
 *  <li>Live access to data block - one block is retrieved at a time
 *  <li>Cached access to data, until the cache fills up...
 * </ul>
 * </p>
 * @author Philippe Riand
 */
public abstract class DataAccessor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private transient DataAccessorSource dataSource;
    
    public DataAccessor() {
    }
    public DataAccessor(DataAccessorSource dataSource) {
        this.dataSource = dataSource;
    }

    
    //////////////////////////////////////////////////////////////////////
    // Data source access
    //////////////////////////////////////////////////////////////////////
    
    public DataAccessorSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataAccessorSource dataSource) {
        this.dataSource = dataSource;
    }
    
    
    //////////////////////////////////////////////////////////////////////
    // Required methods
    //////////////////////////////////////////////////////////////////////

    //
    // Data access
    //
    public abstract int getCount();
    public abstract Object get(int index);

    
    //////////////////////////////////////////////////////////////////////
    // Optional methods
    //////////////////////////////////////////////////////////////////////
    
    public boolean handlePrefetch() {
        return false;
    }
    
    public boolean isPrefetched(int index) {
        return true;
    }
    
    //
    // Read a block
    // This function is called by the model when it knows in advance the block of
    // data it will need.
    //
    public void prefetchData(int start, int count) {
    }
    
    public void clearData(boolean recomputeCount) {
    }
    public void updateCount() {
    }
    
    //
    // More rows management
    // This is an extended way for counting entries, when the exact number of rows
    // is not known, and the pager needs to display ... if more rows are available 
    //
    public boolean canHaveMoreRows() {
        return false;
    }

    public int hasMoreRows(int maxCount) {
        return maxCount;
    }
    
    // Deleting a row with a particular id
    public void deleteRow(String rowId) {
    }
}
