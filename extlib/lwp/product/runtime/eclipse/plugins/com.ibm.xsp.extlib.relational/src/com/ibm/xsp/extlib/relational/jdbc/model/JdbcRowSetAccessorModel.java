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

package com.ibm.xsp.extlib.relational.jdbc.model;

import com.ibm.xsp.extlib.model.DataAccessorModel;
import com.ibm.xsp.model.TabularDataModel;


/**
 * Data model for a JDBC data source using a cached row set.
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class JdbcRowSetAccessorModel extends DataAccessorModel {
    
    public JdbcRowSetAccessorModel(JdbcRowSetSource data, JdbcRowSetSource.Container dataContainer) {
    	super(data,dataContainer);
    }

    @Override
    public JdbcDataSource.Container getDataContainer() {
        return (JdbcDataSource.Container)super.getDataContainer();
    }

    @Override
    public JdbcRowSetAccessor getDataAccessor() {
        return (JdbcRowSetAccessor)getDataContainer().getDataAccessor();
    }

    public String getQuery() {
    	return ((JdbcRowSetAccessor)getDataContainer().getDataAccessor()).getQuery();
    }

	@Override
	public String getRowId() {
	    return Integer.toString(getRowIndex());
	}

	
	//
	// Handling sort
	//

	@Override
	public boolean isColumnSortable(String columnName) {
		// none of the columns are sortable dynamically
		return true;
	}
	
	@Override
	public int getResortType(String columnName) {
		return TabularDataModel.RESORT_NONE;
	}

	@Override
	public int getResortState(String columnName) {
		return TabularDataModel.RESORT_NONE;
	}

	@Override
	public String getResortColumn() {
		return null;
	}
	
	@Override
	public void setResortOrder(String columnName, String resortOrder) {
        return;
	}
}
