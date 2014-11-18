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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.commons.util.EmptyIterator;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.model.TabularDataModel;


/**
 * Data model for DataAccessor.
 * <p>
 * This data model serves data from a data accessor.
 * </p>
 * @author Philippe Riand
 */
public class DataAccessorModel extends TabularDataModel {



    private DataAccessorSource data;
    private DataAccessorSource.Container dataContainer;
    private ArrayList<String> selectedIds;
    
    public DataAccessorModel(DataAccessorSource data, DataAccessorSource.Container dataContainer) {
        this.data = data;
        this.dataContainer = dataContainer;
        setWrappedData(dataContainer.getDataAccessor());
    }

    public DataAccessorSource getData() {
        return data;
    }

    public DataAccessorSource.Container getDataContainer() {
        return dataContainer;
    }

    public DataAccessor getDataAccessor() {
        return dataContainer.getDataAccessor();
    }
    
    @Override
    public String getRowId() {
       Object rowData = getRowData();
       return super.getRowId();
    }
    
    @Override
    public boolean isRowAvailable() {
        if(!super.isRowAvailable()) {
            // Ok, try to prefetch the data
            // This is done for the Ajax request when rows are added dynamically
            DataAccessor accessor = getDataAccessor();
            if(accessor!=null && accessor.handlePrefetch()) {
                accessor.prefetchData(getRowIndex(), -1);
                return super.isRowAvailable();
            }
            return false;
        }
        return true;
    }
    
    @Override
    public int getRowCount() {
        int rowCount = getDataAccessor().getCount();
        if(!getDataAccessor().handlePrefetch()) {
            return rowCount; 
        }
        if(rowCount<0) {
            // We anticipate the rows that will be read by the table
            // That updates the count
            int dcFirst = dataControl.getFirst(); 
            int dcCount = dataControl.getRows();
            getDataAccessor().prefetchData(dcFirst, dcCount);
            rowCount = getDataAccessor().getCount();
        } 
        return rowCount;
    }

    @Override
    public Object getRowData() {
        int index = getRowIndex();
        
        // 1- If the accessor does not handle blocks, then return the value as is
        if(!getDataAccessor().handlePrefetch()) {
            return getDataAccessor().get(index);
        }
        
        // 2- Look if the data access block already contains the data
        // if so, then return it
        if(getDataAccessor().isPrefetched(index)) {
            return getDataAccessor().get(index);
        }
        
        // 2- We need to load the entry
        // We ensure that the block is properly loaded for the index
        int first = dataControl.getFirst();
        int count = dataControl.getRows(); 
        if(index<first || index>=(first+count)) {
            first = index;
            // note, rows defaults to 30 in the dataControl
        }

        // 3- Ask the data accessor to read the actual entries
        getDataAccessor().prefetchData(first, count);
        return getDataAccessor().get(index);
    }
    
    @Override
    public boolean canHaveMoreRows() {
        DataAccessor da = getDataAccessor();
        if(da!=null) {
            return da.canHaveMoreRows();
        }
        return false;
    }

    @Override
    public int hasMoreRows(int maxCount) {
        DataAccessor da = getDataAccessor();
        if(da!=null) {
            return da.hasMoreRows(maxCount);
        }
        return 0;
    }
    
    
    @Override
    public void setDataControl(FacesDataIterator dataControl) {
        super.setDataControl(dataControl);
        getDataContainer().installFacesListener();
    }

    //
    // Selected IDs
    //
    @Override
    public Iterator getSelectedIds() {
        if(selectedIds!=null) {
            return selectedIds.iterator();
        }
        return EmptyIterator.getInstance();
    }

    @Override
    public void clearSelectedIds() {
        selectedIds = null;
    }

    @Override
    public void addSelectedId(String id) {
        if(selectedIds==null) {
            selectedIds = new ArrayList<String>();
        }
        selectedIds.add(id);
    }
    
    @Override
    public boolean isSelectedId(String id) {
        if(selectedIds!=null) {
            return selectedIds.contains(id);
        }
        return false;
    }

    @Override
    public void removeSelectedId(String id) {
        if(selectedIds!=null) {
            selectedIds.remove(id);
        }
    }
    

    @Override
    public void deleteSelectedItems() throws IOException {
        Iterator<String> it=getSelectedIds();
        if(it!=null) {
            DataAccessor accessor = getDataAccessor(); 
            while(it.hasNext()) {
                String rowId = it.next();
                accessor.deleteRow(rowId);
            }
        }
    }
}
