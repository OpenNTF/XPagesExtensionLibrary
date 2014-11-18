/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.xsp.extlib.relational.component.jdbc.rest;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.rest.AbstractRestService;
import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * @author Andrejus Chaliapinas
 *
 */
public abstract class JdbcService extends AbstractRestService {
	private Boolean	compact;
	private String contentType;
	private String connectionName;
	private String connectionUrl;
	private String defaultOrderBy;
	private String sqlCountQuery;
	private String sqlFile;
	private List<SqlParameter> sqlParameters;
	private String sqlQuery;
	private String sqlTable;
	
	private Integer hintStart;
	private Integer hintCount;

	public JdbcService() {
	}
	
	@Override
	public String getStoreDojoType() {
		return null;
	}

	@Override
	public DojoModuleResource getStoreDojoModule() {
		return null;
	}
	
	public String getContentType() {
        if (contentType != null) {
            return contentType;
        }        
        ValueBinding vb = getValueBinding("contentType"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isCompact() {
        if (compact != null) {
            return compact;
        }        
        ValueBinding vb = getValueBinding("compact"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean)vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
        }
        return false;
	}
	public void setCompact(boolean compact) {
		this.compact = compact;
	}

	public String getConnectionName() {
        if (connectionName != null) {
            return connectionName;
        }        
        ValueBinding vb = getValueBinding("connectionName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getConnectionUrl() {
        if (connectionUrl != null) {
            return connectionUrl;
        }        
        ValueBinding vb = getValueBinding("connectionUrl"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public String getDefaultOrderBy() {
        if (defaultOrderBy != null) {
            return defaultOrderBy;
        }        
        ValueBinding vb = getValueBinding("defaultOrderBy"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setDefaultOrderBy(String defaultOrderBy) {
		this.defaultOrderBy = defaultOrderBy;
	}
	
	public String getSqlCountQuery() {
        if (sqlCountQuery != null) {
            return sqlCountQuery;
        }        
        ValueBinding vb = getValueBinding("sqlCountQuery"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setSqlCountQuery(String sqlCountQuery) {
		this.sqlCountQuery = sqlCountQuery;
	}

	public String getSqlFile() {
        if (sqlFile != null) {
            return sqlFile;
        }        
        ValueBinding vb = getValueBinding("sqlFile"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}

	public List<SqlParameter> getSqlParameters() {
        return null;
	}
    public void addSqlParameter(SqlParameter attribute) {
        if(sqlParameters==null) {
        	sqlParameters = new ArrayList<SqlParameter>();
        }
        sqlParameters.add(attribute);
    }
	public void setSqlParameters(List<SqlParameter> sqlParameters) {
		this.sqlParameters = sqlParameters;
	}

	public String getSqlQuery() {
        if (sqlQuery != null) {
            return sqlQuery;
        }        
        ValueBinding vb = getValueBinding("sqlQuery"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getSqlTable() {
        if (sqlTable != null) {
            return sqlTable;
        }        
        ValueBinding vb = getValueBinding("sqlTable"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
	public void setSqlTable(String sqlTable) {
		this.sqlTable = sqlTable;
	}

	public int getHintStart() {
        if (hintStart != null) {
            return hintStart;
        }        
        ValueBinding vb = getValueBinding("hintStart"); //$NON-NLS-1$
        if (vb != null) {
            Integer val = (Integer)vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
        }
		return 0;
	}
	public void setHintStart(int hintStart) {
		this.hintStart = hintStart;
	}
	
	public int getHintCount() {
        if (hintCount != null) {
            return hintCount;
        }        
        ValueBinding vb = getValueBinding("hintCount"); //$NON-NLS-1$
        if (vb != null) {
            Integer val = (Integer)vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
        }
		return 0;
	}
	public void setHintCount(int hintCount) {
		this.hintCount = hintCount;
	}
	
	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[12];
        state[0] = super.saveState(context);
        state[1] = compact;
        state[2] = contentType;
        state[3] = connectionName;
        state[4] = connectionUrl;
        state[5] = defaultOrderBy;
        state[6] = sqlCountQuery;
        state[7] = sqlFile;
        state[8] = sqlQuery;
        state[9] = sqlTable;
        state[10] = hintStart;
        state[11] = hintCount;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        compact = (Boolean) state[1];
        contentType = (String) state[2];
        connectionName = (String) state[3];
        connectionUrl = (String) state[4];
        defaultOrderBy = (String) state[5];
        sqlCountQuery = (String) state[6];
        sqlFile = (String) state[7];
        sqlQuery = (String) state[8];
        sqlTable = (String) state[9];
        hintStart = (Integer) state[10];
        hintCount = (Integer) state[11];
    }    
	
}
