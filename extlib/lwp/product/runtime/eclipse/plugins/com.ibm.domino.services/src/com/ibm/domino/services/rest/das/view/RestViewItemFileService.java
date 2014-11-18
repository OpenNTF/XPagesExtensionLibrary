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

package com.ibm.domino.services.rest.das.view;

import static com.ibm.domino.services.ResponseCode.RSRC_CREATED;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_CATEGORY;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_COUNT;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_EXPANDLEVEL;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_PAGEINDEX;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_PAGESIZE;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_SEARCH;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_SORTCOLUMN;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_SORTORDER;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_START;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_STARTINDEX;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VIEW_STARTKEYS;
import static com.ibm.domino.services.rest.RestServiceConstants.*;
import static com.ibm.domino.services.HttpServiceConstants.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.RestDocumentNavigator;
import com.ibm.domino.services.rest.das.RestDocumentNavigatorFactory;
import com.ibm.domino.services.util.JsonWriter;

/**
 * Domino View Service.
 */
public class RestViewItemFileService extends RestViewService {

    // Indicated if the attributes should be written when they have a default value
    // for example, when a count==0
    protected boolean forceDefaultAttributes;

    public RestViewItemFileService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
        super(httpRequest, httpResponse,parameters);
    }
    
    @Override
    public void renderService() throws ServiceException {
        if (HTTP_GET.equalsIgnoreCase(getHttpRequest().getMethod())) {
            renderServiceJSONGet();
        } else if (HTTP_POST.equalsIgnoreCase(getHttpRequest().getMethod())) {
            renderServiceJSONPost();
        } else {
            // Use a different status for an error?
            //HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            throw new ServiceException(null,"Method {0} is not allowed with JSON format",getHttpRequest().getMethod()); // $NLX-RestViewItemFileService.Method0isnotallowedwithJSONformat-1$
        }
    }

    
    // ==========================================================================
    // Access to the parameters from the request
    // ==========================================================================

    @Override
    protected ViewParameters wrapViewParameters(ViewParameters parameters) {
        return new RequestViewParameter(parameters);
    }   

    protected class RequestViewParameter extends ViewParametersDelegate {
        private boolean ignoreRequestParams;
        private int start;
        private int count;
        protected RequestViewParameter(ViewParameters delegate) {
            super(delegate);
            this.ignoreRequestParams = delegate.isIgnoreRequestParams();
            String param = getHttpRequest().getParameter(PARAM_VIEW_START); 
            if (StringUtil.isNotEmpty(param)) {
                try {
                    start = Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            } else {
                start = delegate.getStart();
            }
            param = getHttpRequest().getParameter(PARAM_VIEW_COUNT); 
            if (StringUtil.isNotEmpty(param)) {
                try {
                    count = Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            } else {
                count = delegate.getCount();
            }
            // The following three parameters page, ps and si map to start and count.
            // count = ps
            // start = ps * page + si
            param = getHttpRequest().getParameter(PARAM_VIEW_PAGESIZE); 
            if (StringUtil.isNotEmpty(param)) {
                try {
                    count = Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            } /*else {
                count = delegate.getPageSize();
            }*/             
            param = getHttpRequest().getParameter(PARAM_VIEW_PAGEINDEX); 
            if (StringUtil.isNotEmpty(param)) {
                try {
                    int page = Integer.parseInt(param);
                    if (page > 0)
                        page-=1;
                    start = page * count;
                    
                } catch (NumberFormatException nfe) {}
            } /*else {
                start = delegate.getPageIndex();
            }   */          
            param = getHttpRequest().getParameter(PARAM_VIEW_STARTINDEX); 
            if (StringUtil.isNotEmpty(param)) {
                try {
                    int si = Integer.parseInt(param);
                    start += si;
                } catch (NumberFormatException nfe) {}
            } /*else {
                start = delegate.getStartIndex();
            }*/
        }
        @Override
        public boolean isIgnoreRequestParams() {
            return ignoreRequestParams;
        }
        @Override
        public int getStart() {
            return start;
        }
        @Override
        public int getCount() {
            return count;
        }
        @Override
        public String getSortColumn() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_SORTCOLUMN); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getSortColumn();
        }
        @Override
        public String getSortOrder() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_SORTORDER); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getSortOrder();
        }
        @Override
        public String getCategoryFilter() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_CATEGORY);
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getCategoryFilter();
        }
        @Override
        public int getExpandLevel() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_EXPANDLEVEL);
                if (StringUtil.isNotEmpty(param)) {
                    try {
                        return Integer.parseInt(param);
                    } catch (NumberFormatException nfe) {}
                }
            }
            return super.getExpandLevel();
        }
        @Override
        public String getSearch() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_SEARCH);
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getSearch();
        }
        @Override
        public Object getStartKeys() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_STARTKEYS);
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getStartKeys();
        }
    }

    
    // ==========================================================================
    // GET: read the data
    // ==========================================================================
    
    protected void renderServiceJSONGet() throws ServiceException {
        try {
            ViewParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_APPLICATION_JSON;
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding(ENCODING_UTF8);
            
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            boolean compact = parameters.isCompact();
            JsonWriter g = new JsonWriter(writer,compact); 

            // Create the new XPages view navigator
            RestViewNavigator nav = RestViewNavigatorFactory.createNavigator(this.getView(),parameters);
            try {
                // Start the main Object
                g.startObject();
                
                // writer.
                int global = parameters.getGlobalValues();
                if((global & ViewParameters.GLOBAL_TIMESTAMP)!=0) {
                    g.startProperty(ATTR_TIMESTAMP);
                    g.outDateLiteral(new Date());
                    g.endProperty();
                }
                if((global & ViewParameters.GLOBAL_TOPLEVEL)!=0) {
                    g.startProperty(ATTR_TOPLEVELENTRIES);
                    g.outIntLiteral(nav.getTopLevelEntryCount());
                    g.endProperty();
                }
                if((global & ViewParameters.GLOBAL_ENTRIES)!=0) {
                    g.startProperty(ATTR_ITEMS);
                    g.startArray();
                    
                    // Read all the entries
                    int start = parameters.getStart();
                    int count = parameters.getCount();
                    int syscol = parameters.getSystemColumns();
                    boolean defColumns = parameters.isDefaultColumns();
                    List<RestViewColumn> columns = parameters.getColumns();
    
                    int idx = 0;
                    for( boolean b=nav.first(start,count); b && idx<count; b=nav.next(), idx++) {
                        g.startArrayItem();
                        writeEntryAsJson(g,syscol,defColumns,columns,nav);
                        g.endArrayItem();
                    }
                    
                    g.endArray();
                    g.endProperty();
                }
                
                // Terminate the main object
                g.endObject();
            } finally {
                nav.recycle();
            }
            
            writer.flush();
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch (NotesException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }

    
    
    protected void writeEntryAsJson(JsonWriter g, int syscol, boolean defColumns, List<RestViewColumn> columns, RestViewNavigator nav) throws IOException, ServiceException {
        g.startObject();
        writeSystemColumns(g, syscol, nav);
        writeColumns(g, defColumns, columns, nav);
        g.endObject();
    }
    protected void writeColumns(JsonWriter g, boolean defColumns, List<RestViewColumn> columns, RestViewNavigator nav) throws IOException, ServiceException {
        int colIdx = 0;

        // Read the default columns
        if(defColumns) {
            int colCount = nav.getColumnCount();
            for(int i=0; i<colCount; i++) {
                String colName = nav.getColumnName(i);
                Object colValue = nav.getColumnValue(i);
                writeColumn(g, nav, colIdx++, colName, colValue);
            }
        }
        
        // Calculate the extra columns
        int ccount = columns!=null ? columns.size() : 0; 
        if(ccount>0) {
            for( int i=0; i<ccount; i++) {
                RestViewColumn c = columns.get(i);
                String colName = c.getName();
                Object colValue = c.evaluate(this, nav);
                writeCustomColumn(g, nav, colIdx++, colName, colValue, c);
            }
        }
    }
    protected void writeSystemColumns(JsonWriter g, int syscol, RestViewNavigator nav) throws IOException, ServiceException {
        // write the system columns
        if(true) { // Always write the entry id
            g.startProperty(ATTR_ENTRYID);
            g.outStringLiteral(getEntryId(nav));
            g.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_UNID)!=0) {
            g.startProperty(ATTR_UNID);
            g.outStringLiteral(nav.getUniversalId());
            g.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_NOTEID)!=0) {
            g.startProperty(ATTR_NOTEID);
            g.outStringLiteral(nav.getNoteId());
            g.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_POSITION)!=0) {
            g.startProperty(ATTR_POSITION);
            g.outStringLiteral(nav.getPosition());
            g.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_READ)!=0) {
            boolean read = nav.getRead();
            if(forceDefaultAttributes || read) {
                g.startProperty(ATTR_READ);
                g.outBooleanLiteral(true);
                g.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_SIBLINGS)!=0) {
            int count = nav.getSiblings();
            if(forceDefaultAttributes || count>0) {
                g.startProperty(ATTR_SIBLINGS);
                g.outIntLiteral(count);
                g.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_DESCENDANTS)!=0) {
            int count = nav.getDescendants();
            if(forceDefaultAttributes || count>0) {
                g.startProperty(ATTR_DESCENDANTS);
                g.outIntLiteral(count);
                g.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_CHILDREN)!=0) {
            int count = nav.getChildren();
            if(forceDefaultAttributes || count>0) {
                g.startProperty(ATTR_CHILDREN);
                g.outIntLiteral(count);
                g.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_INDENT)!=0) {
            int indent = nav.getIndent();
            if(forceDefaultAttributes || indent>0) {
                g.startProperty(ATTR_INDENT);
                g.outIntLiteral(indent);
                g.endProperty();
            }
        }
}
    protected void writeColumn(JsonWriter g, RestViewNavigator nav, int colIdx, String colName, Object colValue) throws IOException, ServiceException {
        if(colValue!=null) {
            g.startProperty(colName);
            g.outDominoValue(colValue);
            g.endProperty();
        }
    }
    protected void writeCustomColumn(JsonWriter g, RestViewNavigator nav, int colIdx, String colName, Object colValue, RestViewColumn column) throws IOException, ServiceException {
        if(colValue!=null) {
            g.startProperty(colName);
            g.outDominoValue(colValue);
            g.endProperty();
        }
    }

    
    // ==========================================================================
    // POST: update the data
    // ==========================================================================

    protected void renderServiceJSONPost() throws ServiceException {
        ViewParameters parameters = getParameters();
        // Look if the request seems correct
        String reqContentType = getHttpRequest().getContentType();
        if(!reqContentType.contains(CONTENTTYPE_APPLICATION_JSON)) {
            throw new ServiceException(null,"Request does not contains 'application/json' but {0}",reqContentType); // $NLX-RestViewItemFileService.Requestdoesnotcontainsapplication-1$
        }

        JsonJavaFactory factory = JsonJavaFactory.instanceEx;
        
        // Ok, parse the JSON content
        JsonJavaObject json;
        try {
            Reader r = getHttpRequest().getReader();
            try {
                json = (JsonJavaObject)JsonParser.fromJson(factory, r);
            } finally {
                r.close();
            }
        } catch(Exception ex) {
            throw new ServiceException(ex,"Error while parsing the JSON content"); // $NLX-RestViewItemFileService.ErrorwhileparsingtheJSONcontent-1$
        }
        
        RestDocumentNavigator docNav = null;        
        try {
            // Get a view navigator to get access to the columns
            RestViewNavigator viewNav = RestViewNavigatorFactory.createNavigatorForDesign(RestViewItemFileService.this.getView(),parameters);
    
            // Get a document docNav
            docNav = RestDocumentNavigatorFactory.createNavigator(this.getView(),getParameters());

            // Start the transaction
            boolean supportsTransaction = docNav.supportsTransaction();
            
            // Now, browse the different entries
            List<JsonJavaObject> rows = (List<JsonJavaObject>)json.get(JSON_OBJECT_PROPERTY_ROWS);
            if(rows!=null) {
                try {
                    for(JsonJavaObject row: rows) {
                        int op = row.getInt(JSON_OBJECT_PROPERTY_OP);
                        String id = row.getString(JSON_OBJECT_PROPERTY_ID);
                        id = getEntryUNID(id);
                        JsonJavaObject items = row.getJsonObject(JSON_OBJECT_PROPERTY_ITEMS);
                        processRow(viewNav, docNav, op, id, items);
                    }
                    if(supportsTransaction) {
                        docNav.commit();
                    }
                } catch(Throwable ex) {
                    if(supportsTransaction) {
                        docNav.rollback();
                    }
                    if(ex instanceof ServiceException) {
                        throw (ServiceException)ex;
                    }
                    throw new ServiceException(ex,"Error while updating data"); // $NLX-RestViewItemFileService.Errorwhileupdatingdata-1$
                }
            }
        } catch (NotesException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } finally {
            if (docNav != null)
                docNav.recycle();
        }

        try {
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_TEXT_HTML;
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding(ENCODING_UTF8);
            
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            writer.write("{}"); // $NON-NLS-1$
            writer.flush();
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }

    protected void processRow(RestViewNavigator viewNav, RestDocumentNavigator docNav, int op, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        switch(op) {
            case 0:     createDocument(viewNav, docNav, items); break;
            case 1:     updateDocument(viewNav, docNav, id,items); break;
            case 2:     deleteDocument(viewNav, docNav, id, items); break;
        }
    }

    protected void createDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryNewDocument()) {
            throw new ServiceException(null, msgErrorCreatingDocument());
        }
        docNav.createDocument();
        Document doc = docNav.getDocument();
        postNewDocument(doc);
        try {
            updateFields(viewNav, docNav, items);
            String form = getParameters().getFormName();
            if (StringUtil.isNotEmpty(form)) {
                docNav.replaceItemValue(ITEM_FORM, form);
            }           
            if (getParameters().isComputeWithForm()) {
                docNav.computeWithForm();
            }
            if(!querySaveDocument(doc)) {
                throw new ServiceException(null, msgErrorCreatingDocument());
            }
            docNav.save();
            postSaveDocument(doc);
            getHttpResponse().setStatus(RSRC_CREATED.httpStatusCode);
        } finally {
            docNav.recycle(); 
        }
    }
    private String msgErrorCreatingDocument() {
        return "Error creating document."; // $NLX-RestViewItemFileService.Errorcreatingdocument.1-1$
    }

    protected void updateDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryOpenDocument(id)) {
            throw new ServiceException(null, msgErrorUpdatingData());
        }
        docNav.openDocument(id);
        Document doc = docNav.getDocument();
        postOpenDocument(doc);
        try {
            updateFields(viewNav, docNav, items);
            if (getParameters().isComputeWithForm()) {
                docNav.computeWithForm();
            }           
            if(!querySaveDocument(doc)) {
                throw new ServiceException(null, msgErrorUpdatingData());
            }
            docNav.save();
            postSaveDocument(doc);
        } finally {
            docNav.recycle();
        }
    }

    /**
     * @return
     */
    private String msgErrorUpdatingData() {
        return "Error updating data."; // $NLX-RestViewItemFileService.Errorupdatingdata.1-1$
    }

    protected void updateFields(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        for(Iterator<String> it = items.getJsonProperties(); it.hasNext(); ) {
            String columnName = it.next();
            String fieldName = findItemName(viewNav, columnName);
            Object value = items.get(columnName);
            updateField(viewNav, docNav, items, columnName, fieldName, value);
        }
    }

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    protected void updateField(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items, String columnName, String fieldName, Object value) throws ServiceException, JsonException, IOException {
        if (value instanceof List) {
            Vector<?> vector = new Vector((List)value);
            docNav.replaceItemValue(fieldName, vector);
        }
        else {
            docNav.replaceItemValue(fieldName, value);
        }
    }

    protected void deleteDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryDeleteDocument(id)) {
            throw new ServiceException(null, "Error deleting document."); // $NLX-RestViewItemFileService.Errordeletingdocument-1$
        }
        docNav.deleteDocument(id);
        postDeleteDocument(id);
    }
    
    private HashMap<String,String> itemsMap;
    protected String findItemName(RestViewNavigator viewNav, String columnName) throws ServiceException {
        if(itemsMap==null) {
            itemsMap = new HashMap<String, String>();
        }
        String itemName = itemsMap.get(columnName);
        if(itemName!=null) {
            return itemName;
        }
        // Look for a predefined column
        if(getParameters().isDefaultColumns()) {
            itemName = viewNav.getItemName(columnName);
        }
        // Look for a custom column pointing to an actual column
        if(itemName==null) {
            List<RestViewColumn> cols = getParameters().getColumns();
            if(cols!=null) {
                for(RestViewColumn c: cols) {
                    if(StringUtil.equals(c.getName(),columnName)) {
                        String colname = c.getColumnName();
                        if(StringUtil.isNotEmpty(colname)) {
                            itemName = viewNav.getItemName(colname);
                        }
                        break;
                    }
                }
            }
        }
        // If not found, throw an error
        if(itemName==null) {
            throw new ServiceException(null,"The column {0} doesn't map a Document field",columnName); // $NLX-RestViewItemFileService.Thecolumn0doesntmapaDocumentfield-1$
        }
        
        itemsMap.put(columnName, itemName);
        return itemName;
    }
}