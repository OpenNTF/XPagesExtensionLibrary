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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.model.DataAccessorModel;
import com.ibm.xsp.model.TabularDataModel;


/**
 * Data model for a JDBC data source.
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class JdbcDataAccessorModel extends DataAccessorModel {
    
    public JdbcDataAccessorModel(JdbcDataSource data, JdbcDataSource.Container dataContainer) {
    	super(data,dataContainer);
    }

    @Override
    public JdbcDataSource.Container getDataContainer() {
        return (JdbcDataSource.Container)super.getDataContainer();
    }

    @Override
    public JdbcDataBlockAccessor getDataAccessor() {
        return (JdbcDataBlockAccessor)getDataContainer().getDataAccessor();
    }

    public String getQuery() {
    	return ((JdbcDataBlockAccessor)getDataContainer().getDataAccessor()).getQuery();
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
		// All the columns are sortable using ORDER BY in SQL
		return true;
	}
	
	@Override
	public int getResortType(String columnName) {
		return TabularDataModel.RESORT_BOTH;
	}

	@Override
	public int getResortState(String columnName) {
		String c = getDataAccessor().getSortedColumnName();
		if(StringUtil.equalsIgnoreCase(c, columnName)) {
			if(getDataAccessor().isSortedColumnDescending()) {
				return TabularDataModel.RESORT_DESCENDING;
			} else {
				return TabularDataModel.RESORT_ASCENDING;
			}
		}
		return TabularDataModel.RESORT_NONE;
	}

	@Override
	public String getResortColumn() {
		return getDataAccessor().getSortedColumnName();
	}
	
	@Override
	public void setResortOrder(String columnName, String resortOrder) {
        // basic command validation
        if (StringUtil.isEmpty(columnName) || StringUtil.isEmpty(resortOrder)) {
            return;
        }
        String colName = getResortColumn();
        if(StringUtil.equals(colName, columnName)) {
        	if(StringUtil.endsWithIgnoreCase(resortOrder, TabularDataModel.SORT_ASCENDING)) {
        		getDataAccessor().resort(columnName,false);
        	} else if(StringUtil.endsWithIgnoreCase(resortOrder, TabularDataModel.SORT_DESCENDING)) {
        		getDataAccessor().resort(columnName,false);
        	} else if(StringUtil.endsWithIgnoreCase(resortOrder, TabularDataModel.SORT_TOGGLE)) {
        		if(!getDataAccessor().isSortedColumnDescending()) {
            		getDataAccessor().resort(columnName,true);
        		} else { 
            		getDataAccessor().resort(null,false);
        			//getDataAccessor().resort(columnName,!getDataAccessor().isSortedColumnDescending());
        		}
        	} else {
        		getDataAccessor().resort(null,false);
        	}
        } else {
    		getDataAccessor().resort(columnName,false);
        }
	}
}
