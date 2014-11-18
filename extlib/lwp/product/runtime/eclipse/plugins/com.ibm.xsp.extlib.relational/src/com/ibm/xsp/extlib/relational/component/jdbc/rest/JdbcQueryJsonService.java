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

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.extlib.component.rest.UIBaseRestService;
import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParameters;
import com.ibm.xsp.extlib.relational.jdbc.rest.query.RestJdbcQueryJsonService;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

/**
 * @author Andrejus Chaliapinas
 *
 */
public class JdbcQueryJsonService extends JdbcService {
	public JdbcQueryJsonService() {
	}
	
    @Override
    public String getStoreDojoType() {
        return "extlib.dojo.data.XPagesRestStore"; // $NON-NLS-1$
    }
    
    @Override
    public DojoModuleResource getStoreDojoModule() {
        return ExtLibResources.extlibXPagesRestStore;
    }
    
    @Override
    public void writeDojoStoreAttributes(FacesContext context, UIBaseRestService parent, ResponseWriter writer, String dojoType) throws IOException {
        String pathInfo = parent.getPathInfo();
        String url = parent.getUrlPath(context,pathInfo,null);
        writer.writeAttribute("dojoType",dojoType,null); // $NON-NLS-1$
        writer.writeAttribute("target",url,null); // $NON-NLS-1$
        writer.writeAttribute("idAttribute","@entryid",null); // $NON-NLS-1$ $NON-NLS-2$

        // Create the extra parameters
        StringBuilder b = new StringBuilder();
        String viewId = parent.getAjaxViewid(context);
        if(StringUtil.isNotEmpty(viewId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_VIEWID);
            b.append('=');
            b.append(viewId);
        }
        String targetId = parent.getAjaxTarget(context,pathInfo);
        if(StringUtil.isNotEmpty(targetId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_AXTARGET);
            b.append('=');
            b.append(targetId);
        }
        String extraArgs = context.getExternalContext().encodeActionURL(b.toString());
        if(StringUtil.isNotEmpty(extraArgs)) {
            // remove the leading '?'
            writer.writeAttribute("extraArgs",extraArgs.substring(1),null); // $NON-NLS-1$
        }
    }
    
	private class Engine extends RestJdbcQueryJsonService {
		Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Parameters params) {
			super(httpRequest, httpResponse, params);
		}
	}
	
	protected class Parameters implements JdbcParameters {
		Parameters(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest) {
		}

		public String getContentType() {
			return JdbcQueryJsonService.this.getContentType();
		}

		public boolean isCompact() {
			return JdbcQueryJsonService.this.isCompact();
		}

		public String getConnectionName() {
			return JdbcQueryJsonService.this.getConnectionName();
		}

		public String getConnectionUrl() {
			return JdbcQueryJsonService.this.getConnectionUrl();
		}

		public String getDefaultOrderBy() {
			return JdbcQueryJsonService.this.getDefaultOrderBy();
		}

		public String getSqlCountQuery() {
			return JdbcQueryJsonService.this.getSqlCountQuery();
		}

		public String getSqlFile() {
			return JdbcQueryJsonService.this.getSqlFile();
		}

		public List<SqlParameter> getSqlParameters() {
			return JdbcQueryJsonService.this.getSqlParameters();
		}

		public String getSqlQuery() {
			return JdbcQueryJsonService.this.getSqlQuery();
		}

		public String getSqlTable() {
			return JdbcQueryJsonService.this.getSqlTable();
		}

		public int getHintStart() {
			return JdbcQueryJsonService.this.getHintStart();
		}

		public int getHintCount() {
			return JdbcQueryJsonService.this.getHintCount();
		}

		public String getSortColumn() {
			return null;
		}

		public String getSortOrder() {
			return null;
		}
	}
	
 	public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Parameters params = new Parameters(context, parent, httpRequest);
		return new Engine(httpRequest, httpResponse, params);
	}
}
