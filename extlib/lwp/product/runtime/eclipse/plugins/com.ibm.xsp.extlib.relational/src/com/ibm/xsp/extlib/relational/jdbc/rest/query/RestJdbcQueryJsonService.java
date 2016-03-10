/*
 * © Copyright IBM Corp. 2011, 2015
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

package com.ibm.xsp.extlib.relational.jdbc.rest.query;

import static com.ibm.domino.services.HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON;
import static com.ibm.domino.services.HttpServiceConstants.ENCODING_UTF8;
import static com.ibm.domino.services.HttpServiceConstants.HEADER_CONTENT_RANGE;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_GET;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ResponseCode;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.content.JsonContentFactory;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParameters;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParametersDelegate;
import com.ibm.xsp.extlib.relational.jdbc.services.content.JdbcJsonContentFactory;
import com.ibm.xsp.extlib.relational.jdbc.services.content.JsonJdbcQueryContent;

/**
 * JDBC Query JSON Service.
 * This service is compliant with the dojox.JsonRest data store.
 * @author Andrejus Chaliapinas
 * 
 */
public class RestJdbcQueryJsonService extends RestJdbcQueryService {
    
    static public final int POST = 0;
    static public final int PUT = 1;
    static public final int DELETE = 2;

    private JsonContentFactory factory = JdbcJsonContentFactory.get();

    public RestJdbcQueryJsonService(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, JdbcParameters parameters) {
        super(httpRequest, httpResponse, parameters);
    }

    @Override
    public void renderService() throws ServiceException {
        String method = getHttpRequest().getMethod();
        if (HTTP_GET.equalsIgnoreCase(method)) {
            renderServiceJSONGet();
//        } else if (HTTP_POST.equalsIgnoreCase(method)) {
//            String override = getHttpRequest().getHeader(HEADER_X_HTTP_METHOD_OVERRIDE);
//            if (HTTP_PUT.equalsIgnoreCase(override)) {
//                renderServiceJSONUpdate(PUT);
//            } else if (HTTP_DELETE.equalsIgnoreCase(override)) {
//                renderServiceJSONUpdate(DELETE);
//            } else {
//                renderServiceJSONUpdate(POST);
//            }
//        } else if (HTTP_PUT.equalsIgnoreCase(method)) {
//            renderServiceJSONUpdate(PUT);
//        } else if (HTTP_DELETE.equalsIgnoreCase(method)) {
//            renderServiceJSONUpdate(DELETE);
        } else {
            String msg = "Method {0} is not allowed with JSON REST Service"; // $NLX-RestJdbcQueryJsonService.Method0isnotallowedwithJSONRestSe-1$[[{0} will be "POST" or "PUT" or "DELETE"]]
            msg = StringUtil.format(msg, method);
            throw new ServiceException(null, ResponseCode.METHOD_NOT_ALLOWED, msg);
        }
    }

    // ==========================================================================
    // Access to the parameters from the request
    // ==========================================================================

    @Override
    protected JdbcParameters wrapJdbcParameters(JdbcParameters parameters) {
        return new RequestJdbcParameter(parameters);
    }
    
    protected class RequestJdbcParameter extends JdbcParametersDelegate {
//        private String connectionName;
//        private String sqlQuery;
//        private String sqlTable;
        private int hintStart;
        private int hintCount;
        private String sortColumn;
        private String sortOrder;
        protected RequestJdbcParameter(JdbcParameters delegate) {
            super(delegate);
//            String param = getHttpRequest().getParameter(PARAM_CONN_NAME); 
//            if (StringUtil.isNotEmpty(param)) {
//                try {
//                  connectionName = param;
//                } catch (NumberFormatException nfe) {}
//            } else {
//              connectionName = delegate.getConnectionName();
//            }
//            param = getHttpRequest().getParameter(PARAM_QUERY); 
//            if (StringUtil.isNotEmpty(param)) {
//                try {
//                  sqlQuery = param;
//                } catch (NumberFormatException nfe) {}
//            } else {
//              sqlQuery = delegate.getSqlQuery();
//            }
//            param = getHttpRequest().getParameter(PARAM_TABLE); 
//            if (StringUtil.isNotEmpty(param)) {
//                try {
//                  sqlTable = param;
//                } catch (NumberFormatException nfe) {}
//            } else {
//              sqlTable = delegate.getSqlTable();
//            }
            
            String param = getHttpRequest().getParameter(PARAM_HINT_START);
            if (StringUtil.isNotEmpty(param)) {
                try {
                    hintStart = Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            } else {
                hintStart = delegate.getHintStart();
            }
            param = getHttpRequest().getParameter(PARAM_HINT_COUNT);
            if (StringUtil.isNotEmpty(param)) {
                try {
                    hintCount = Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            } else {
                hintCount = delegate.getHintCount();
            }
            param = getHttpRequest().getParameter(PARAM_SORT_COLUMN);
            if (StringUtil.isNotEmpty(param)) {
                try {
                    sortColumn = param;
                } catch (NumberFormatException nfe) {}
            } else {
                sortColumn = delegate.getSortColumn();
            }
            param = getHttpRequest().getParameter(PARAM_SORT_ORDER);
            if (StringUtil.isNotEmpty(param)) {
                try {
                    sortOrder = param;
                } catch (NumberFormatException nfe) {}
            } else {
                sortOrder = delegate.getSortOrder();
            }
        }
        
//        @Override
//        public String getConnectionName() {
//          return connectionName;
//        }
//        
//      @Override
//      public String getSqlQuery() {
//          return sqlQuery;
//      }
//      
//      @Override
//      public String getSqlTable() {
//          return sqlTable;
//      }
        
        @Override
        public int getHintStart() {
            return hintStart;
        }
        
        @Override
        public int getHintCount() {
            return hintCount;
        }
        
        @Override
        public String getSortColumn() {
            return sortColumn;
        }

        @Override
        public String getSortOrder() {
            return sortOrder;
        }
    }
    
    // ==========================================================================
    // GET: read the data
    // ==========================================================================
    
    protected void renderServiceJSONGet() throws ServiceException {
        try {
            JdbcParameters parameters = getParameters();
            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            	RelationalLogger.RELATIONAL.traceDebugp(this, "renderServiceJSONGet", "parameter connectionName: " + parameters.getConnectionName()); // $NON-NLS-1$ $NON-NLS-2$
            }
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_APPLICATION_JSON;
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding(ENCODING_UTF8);
            
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            boolean compact = parameters.isCompact();
            JsonWriter g = new JsonWriter(writer,compact); 
            
            // Is this check required only when accessing from servlet?
//          String requestUri = super.getHttpRequest().getRequestURI();     
//            if(!StringUtil.isEmpty(requestUri) && 
//                  (requestUri.endsWith("/"+JdbcRestParameterConstants.PARAM_QUERY) || 
//                          requestUri.endsWith("/"+JdbcRestParameterConstants.PARAM_QUERY+"/"))) {
                JsonJdbcQueryContent content = ((JdbcJsonContentFactory)factory).createJdbcQueryContent();
                content.writeJdbcQuery(g, parameters);
                String rangeHeader = content.getContentRangeHeader(parameters);
                if (rangeHeader != null) {
                    getHttpResponse().setHeader(HEADER_CONTENT_RANGE, rangeHeader);
                }
                writer.flush();
//            }
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }
}