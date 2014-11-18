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

import static com.ibm.domino.services.HttpServiceConstants.*;
import static com.ibm.domino.services.ResponseCode.OK;
import static com.ibm.domino.services.ResponseCode.RSRC_CREATED;
import static com.ibm.domino.services.ResponseCode.RSRC_NOT_FOUND;
import static com.ibm.domino.services.rest.RestParameterConstants.*;
import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FORM;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.ResponseCode;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.content.DefaultJsonContentFactory;
import com.ibm.domino.services.content.JsonContentFactory;
import com.ibm.domino.services.content.JsonViewDesignContent;
import com.ibm.domino.services.content.JsonViewEntryCollectionContent;
import com.ibm.domino.services.rest.RestServiceOperationHandler;
import com.ibm.domino.services.rest.das.RestDocumentNavigator;
import com.ibm.domino.services.rest.das.RestDocumentNavigatorFactory;
import com.ibm.domino.services.util.JsonWriter;


/**
 * Domino JSON View Service.
 * This service is compliant with the dojox.JsonRest data store.
 */
public class RestViewJsonService extends RestViewService {
    
    static public final int POST = 0;
    static public final int PUT = 1;
    static public final int DELETE = 2;
    

    public static final String FOLDER_OP_ADD =      "add";      //$NON-NLS-1$
    public static final String FOLDER_OP_REMOVE =   "remove";   //$NON-NLS-1$   
    
    // Indicated if the attributes should be written when they have a default value
    // for example, when a count==0
    protected boolean forceDefaultAttributes;
    
    private JsonContentFactory factory = DefaultJsonContentFactory.get();

    public RestViewJsonService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
        super(httpRequest, httpResponse,parameters);
    }
    
    /**
     * Constructs a <code>RestViewJsonService</code> object.
     * 
     * <p>Use this constructor if you want the service to use a subclass
     * of <code>JsonViewEntryCollectionContent</code>.  You must implement
     * a factory that creates the desired subclass of 
     * <code>JsonViewEntryCollectionContent</code>. 
     * 
     * @param httpRequest   The HTTP request.
     * @param httpResponse  The HTTP response.
     * @param parameters    View parameters (perhaps parsed from a URL).
     * @param factory       The factory the service should use to create
     *                      an instance of <code>JsonViewEntryCollectionContent</code>.
     */
    public RestViewJsonService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, 
                ViewParameters parameters, JsonContentFactory factory) {
        super(httpRequest, httpResponse,parameters);
        if ( factory != null ) {
            this.factory = factory;
        }
    }
    
    @Override
    public void renderService() throws ServiceException {
        String method = getHttpRequest().getMethod();
        if (HTTP_GET.equalsIgnoreCase(method)) {
            renderServiceJSONGet();
        } else if (HTTP_POST.equalsIgnoreCase(method)) {
            String override = getHttpRequest().getHeader(HEADER_X_HTTP_METHOD_OVERRIDE);
            if (HTTP_PUT.equalsIgnoreCase(override)) {
                renderServiceJSONUpdate(PUT);
            } else if (HTTP_DELETE.equalsIgnoreCase(override)) {
                renderServiceJSONUpdate(DELETE);
            } else {
                renderServiceJSONUpdate(POST);
            }
        } else if (HTTP_PUT.equalsIgnoreCase(method)) {
            renderServiceJSONUpdate(PUT);
        } else if (HTTP_DELETE.equalsIgnoreCase(method)) {
            renderServiceJSONUpdate(DELETE);
        } else {
            throw new ServiceException(null, ResponseCode.METHOD_NOT_ALLOWED, "Method {0} is not allowed with JSON Rest Service", method); // $NLX-RestViewJsonService.Method0isnotallowedwithJSONRestSe-1$
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
            // Header submitted by the client:
            //    Range: items=0-24
            String range=getHttpRequest().getHeader(HEADER_RANGE);
            if(StringUtil.isNotEmpty(range) && range.startsWith(HEADER_RANGE_ITEMS)) {
                int pos = HEADER_RANGE_ITEMS.length();
                int sep = range.indexOf('-',pos);
                start = Integer.valueOf(range.substring(pos,sep));
                int last = Integer.valueOf(range.substring(sep+1));
                count = last-start+1;
            } else { 
                // If the header does not contain range information, we still look at the url.
                // Currently the plan is to support both start and count and page, ps and si.               
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
                        //if (page > 0)
                        //  page-=1;
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
        }
        @Override
        public boolean isIgnoreRequestParams() {
            return ignoreRequestParams;
        }
        // Header returned by the server
        //    Content-Range: items 0-24/66
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
        
        @Override
        public Object getKeys() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_KEYS); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getKeys();
        }
        @Override
        public String getParentId() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_PARENTID); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getParentId();
        }
        @Override
        public int getSearchMaxDocs() throws ServiceException {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_SEARCHMAXDOCS); 
                if (StringUtil.isNotEmpty(param)) {
                    try {
                        return Integer.parseInt(param);
                    } catch (NumberFormatException nfe) {
                        throw new ServiceException(nfe, ResponseCode.BAD_REQUEST, "Invalid max parameter"); // $NLX-RestViewJsonService.Invalidmaxparameter-1$
                    }
                }
            }
            return super.getSearchMaxDocs();
        }
        @Override
        public boolean isCompact() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_COMPACT); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isCompact();
        }
        @Override
        public boolean isKeysExactMatch() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_KEYSEXACTMATCH); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isKeysExactMatch();
        }
        @Override
        public boolean isComputeWithForm() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_COMPUTEWITHFORM); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isComputeWithForm();
        }
        @Override
        public String getFormName() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_VIEW_FORM); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getFormName();
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
            
            String requestUri = super.getHttpRequest().getRequestURI();     
            if(!StringUtil.isEmpty(requestUri) && (requestUri.endsWith("/"+PARAM_DESIGN) || requestUri.endsWith("/"+PARAM_DESIGN+"/"))) // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
            {
                JsonViewDesignContent content = factory.createViewDesignContent(getView());
                content.writeViewDesign(g);
            }
            else
            {
                JsonViewEntryCollectionContent content = factory.createViewEntryCollectionContent(getView(), this);
                String rangeHeader = content.getContentRangeHeader(parameters);
                content.writeViewEntryCollection(g, parameters);
                getHttpResponse().setHeader(HEADER_CONTENT_RANGE, rangeHeader);
                writer.flush();
            }

        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch (NotesException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }   
    
    // ==========================================================================
    // POST: Create a new document
    // ==========================================================================

    protected void renderServiceJSONUpdate(int op) throws ServiceException {
        ViewParameters parameters = getParameters();
    
        // Read the Json Document
        JsonJavaObject json = null;
        if (op != DELETE) {
            // Look if the request seems correct
            String reqContentType = getHttpRequest().getContentType();
            if(!reqContentType.contains(CONTENTTYPE_APPLICATION_JSON)) {
                throw new ServiceException(null,ResponseCode.BAD_REQUEST,"Request does not contains 'application/json' but {0}",reqContentType); // $NLX-RestViewJsonService.Requestdoesnotcontainsapplication-1$
            }
            JsonJavaFactory factory = JsonJavaFactory.instanceEx;           
            // Ok, parse the JSON content           
            try {
                Reader r = getHttpRequest().getReader();
                try {
                    json = (JsonJavaObject)JsonParser.fromJson(factory, r);
                } finally {
                    r.close();
                }
                // check to see if it's a folder PUT operation
                if (op == PUT) {
                    if (isFolderOperation(json)) {
                    
                        // make sure we are working with a folder
                        if (getView().isFolder()) {
                            HandleOperations opsHandler = new HandleOperations(json);
                            opsHandler.run();
                            try {
                                String contentType = parameters.getContentType();
                                if(StringUtil.isEmpty(contentType)) {
                                    contentType = CONTENTTYPE_APPLICATION_JSON;
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
                            return;
                            
                        } else
                            throw new ServiceException(null,ResponseCode.BAD_REQUEST,"Attempting folder operation on a view");  //$NLX-RestViewJsonService.Attemptingfolderoperationonaview-1$                   
                    }
                }
            } catch(Exception ex) {
                throw new ServiceException(ex,"Error while parsing the JSON content"); // $NLX-RestViewJsonService.ErrorwhileparsingtheJSONcontent-1$
            }
        }
        
        // Read the doc id being updated
        String id = findUpdateId(json);

        RestDocumentNavigator docNav = null;
        try {
            // Get a view navigator to get access to the columns
            RestViewNavigator viewNav = RestViewNavigatorFactory.createNavigatorForDesign(RestViewJsonService.this.getView(),parameters);

            // Get a document docNav
            docNav = RestDocumentNavigatorFactory.createNavigator(this.getView(),getParameters());
            
            processRow(viewNav, docNav, op, id, json);
        } catch(Throwable ex) {
            if(ex instanceof ServiceException) {
                throw (ServiceException)ex;
            }
            throw new ServiceException(ex,"Error while updating data"); // $NLX-RestViewJsonService.Errorwhileupdatingdata-1$
        } finally {
            if (docNav != null)
                docNav.recycle();
        }

        try {
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_APPLICATION_JSON;
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
    protected String findUpdateId(JsonJavaObject json) {
        String pathInfo = getHttpRequest().getPathInfo();
        if(StringUtil.isNotEmpty(pathInfo)) {
            String id = pathInfo.substring(pathInfo.lastIndexOf("/")+1); // $NON-NLS-1$
            return getEntryUNID(id);
        }
        return null;
    }

    protected void processRow(RestViewNavigator viewNav, RestDocumentNavigator docNav, int op, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        switch(op) {
            case POST:      createDocument(viewNav, docNav, items); break;
            case PUT:       updateDocument(viewNav, docNav, id,items); break;
            case DELETE:    deleteDocument(viewNav, docNav, id, items); break;
        }
    }

    protected void createDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryNewDocument()) {
            throw new ServiceException(null, msgErrorCreatingDocument());
        }
        docNav.createDocument();
        Document doc = docNav.getDocument();
        postNewDocument(doc);
        JsonViewEntryCollectionContent content = factory.createViewEntryCollectionContent(view, this);
        content.updateFields(viewNav, docNav, items);
        String form = getParameters().getFormName();
        if (StringUtil.isNotEmpty(form)) {
            docNav.replaceItemValue(ITEM_FORM, form);
        }
        String parentId = getParameters().getParentId();
        if (StringUtil.isNotEmpty(parentId)) {
            Document parent = null;
            try {
                parent = database.getDocumentByUNID(parentId);
                docNav.getDocument().makeResponse(parent);
            }
            catch (NotesException e) {
                throw new ServiceException(e, msgErrorCreatingDocument());
            } finally {
                if ( parent != null ) {
                    try {
                        parent.recycle();
                    } catch (NotesException e) {
                    	if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                    		Loggers.SERVICES_LOGGER.traceDebug("Exception thrown when recycling parent.", e); // $NON-NLS-1$
                    	}
                    }
                    parent = null;
                }
            }
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
        try {
            String baseURL = getHttpRequest().getRequestURI();
            getHttpResponse().addHeader(HEADER_LOCATION, baseURL + doc.getUniversalID());
        }
        catch (NotesException e) {
            throw new ServiceException(null, msgErrorCreatingDocument()); // $NLX-RestViewJsonService.Errorcreatingdocument-1$
        }
    }
    private String msgErrorCreatingDocument() {
        return "Error creating document."; // $NLX-RestViewJsonService.Errorcreatingdocument.3-1$
    }

    protected void updateDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryOpenDocument(id)) {
            throw new ServiceException(null, msgErrorUpdatingData());
        }
        docNav.openDocument(id);
        Document doc = docNav.getDocument();
        postOpenDocument(doc);
        JsonViewEntryCollectionContent content = factory.createViewEntryCollectionContent(view, this);
        content.updateFields(viewNav, docNav, items);
        if (getParameters().isComputeWithForm()) {
            docNav.computeWithForm();
        }           
        if(!querySaveDocument(doc)) {
            throw new ServiceException(null, msgErrorUpdatingData());
        }
        docNav.save();
        postSaveDocument(doc);
    }
    private String msgErrorUpdatingData() {
        return "Error updating data."; // $NLX-RestViewJsonService.Errorupdatingdata.1-1$
    }
    
    protected void deleteDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        if(!queryDeleteDocument(id)) {
            throw new ServiceException(null, msgErrorDeletingDocument());
        }
        try {
            docNav.deleteDocument(id);
        } catch (Exception e) {
            throw new ServiceException(e, RSRC_NOT_FOUND, msgErrorDeletingDocument());
        }
        postDeleteDocument(id);
    }
    private String msgErrorDeletingDocument() {
        return "Error deleting document."; // $NLX-RestViewJsonService.Errordeletingdocument.1-1$
    }
    
    private boolean isFolderOperation(JsonJavaObject json) {
        boolean folderOp = false;
        
        if ((json.getJsonProperty(FOLDER_OP_ADD) != null) || 
            (json.getJsonProperty(FOLDER_OP_REMOVE) != null)) {
            folderOp = true;
        }
        
        return folderOp;
    }
    
    private class HandleOperations extends RestServiceOperationHandler  {

        
        private HashMap <String, String> docOperations;  // docunid, operation (add/remove)
        public HandleOperations(Object content) {
            super(content);
            this.docOperations = new HashMap<String, String>();
        }

        @Override
        public void run() throws ServiceException {

            JsonJavaObject requestBody = (JsonJavaObject) getContent(); // contains the json request info
            setupOperations(requestBody);
            
            try {
                Database db = getView().getParent();
                String folderName = getView().getName();
                Document doc = null;

                for( Map.Entry<String,String> e: docOperations.entrySet() ) {
                    String docunid = e.getKey();
                    boolean add = (e.getValue().equalsIgnoreCase(FOLDER_OP_ADD));

                    doc = db.getDocumentByUNID(docunid);

                    if (add)
                        doc.putInFolder(folderName);
                    else
                        doc.removeFromFolder(folderName);   
                    
                    if (doc != null)
                        doc.recycle();
                }
            } catch (NotesException e) {
                throw new ServiceException(e, "");
            } finally {
                getHttpResponse().setStatus(OK.httpStatusCode);
            }
        }
        
        @SuppressWarnings("unchecked")       // $NON-NLS-1$
        private void setupOperations (JsonJavaObject requestBody) {
            for(Iterator<String> it = requestBody.getJsonProperties(); it.hasNext(); ) {    
                String opName = it.next();
                if ((opName.equalsIgnoreCase(FOLDER_OP_ADD)) || 
                    (opName.equalsIgnoreCase(FOLDER_OP_REMOVE))) {
                    Object value = requestBody.get(opName);
                    if (value instanceof List) {
                        Vector<?> vector = new Vector((List)value);
                        if (!vector.isEmpty()) {
                            for(Iterator<?> docs = vector.iterator(); docs.hasNext(); ) {
                                String docunid = (String) docs.next();
                                //System.out.println("docunid is = " + docunid);
                                if (docunid.length() > 0)
                                    docOperations.put(docunid, opName);     
                            }
                        }
                    }
                }
            }
        }
    }
}