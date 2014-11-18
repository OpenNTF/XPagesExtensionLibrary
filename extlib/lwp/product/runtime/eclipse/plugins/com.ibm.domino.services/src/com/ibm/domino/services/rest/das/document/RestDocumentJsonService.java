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

package com.ibm.domino.services.rest.das.document;

import static com.ibm.domino.services.HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON;
import static com.ibm.domino.services.HttpServiceConstants.ENCODING_UTF8;
import static com.ibm.domino.services.HttpServiceConstants.HEADER_LOCATION;
import static com.ibm.domino.services.HttpServiceConstants.HEADER_X_HTTP_METHOD_OVERRIDE;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_DELETE;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_GET;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_PATCH;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_POST;
import static com.ibm.domino.services.HttpServiceConstants.HTTP_PUT;
import static com.ibm.domino.services.ResponseCode.RSRC_CREATED;
import static com.ibm.domino.services.rest.RestParameterConstants.*;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_COMPUTEWITHFORM;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_DOCUMENTID;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_FORM;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_MARKREAD;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_PARENTID;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_SEARCH;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_SEARCHMAXDOCS;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_SINCE;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_DOC_STRONGTYPE;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_SEPERATOR;
import static com.ibm.domino.services.rest.RestParameterConstants.PARAM_VALUE_TRUE;
import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FORM;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.ResponseCode;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.content.DefaultJsonContentFactory;
import com.ibm.domino.services.content.JsonContentFactory;
import com.ibm.domino.services.content.JsonDocumentCollectionContent;
import com.ibm.domino.services.content.JsonDocumentContent;
import com.ibm.domino.services.util.JsonWriter;

/**
 * Domino Document Service as a JSON object.
 */
public class RestDocumentJsonService extends RestDocumentService {
    
    private JsonContentFactory factory = DefaultJsonContentFactory.get();
    
    public RestDocumentJsonService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, DocumentParameters parameters) {
        super(httpRequest, httpResponse, parameters);
    }

    /**
     * Constructs a <code>RestDocumentJsonService</code> object.
     * 
     * <p>Use this constructor if you want the service to use a subclass
     * of <code>JsonDocumentContent</code>.  You must implement
     * a factory that creates the desired subclass of 
     * <code>JsonDocumentContent</code>. 
     * 
     * @param httpRequest   The HTTP request.
     * @param httpResponse  The HTTP response.
     * @param parameters    Document parameters (perhaps parsed from a URL).
     * @param factory       The factory the service should use to create
     *                      an instance of <code>JsonDocumentContent</code>.
     */
    public RestDocumentJsonService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, 
                DocumentParameters parameters, JsonContentFactory factory) {
        super(httpRequest, httpResponse, parameters);
        if ( factory != null ) {
            this.factory = factory;
        }
    }

    static public enum Method
    {
        POST(HTTP_POST),
        PUT(HTTP_PUT),
        DELETE(HTTP_DELETE),
        GET(HTTP_GET),
        PATCH(HTTP_PATCH);
        
        public final String name;

        Method(final String name)
        {
            this.name = name;
        }
        
        static Method getMethod(final String method)
        {
            Method result = null;
            final String verb = method.toUpperCase();
            if (verb.equals(GET.name))
                result = Method.GET;
            else if (verb.equals(PUT.name))
                result = Method.PUT;
            else if (verb.equals(DELETE.name))
                result = Method.DELETE;
            else if (verb.equals(POST.name))
                result = Method.POST;
            else if (verb.equals(PATCH.name))
                result = Method.PATCH;
            return result;
        }
        
        static boolean isMethod(final Method method, final String name)
        {
            if (method == null || name == null)
                return false;
            return Method.getMethod(name) == method;
        }
        
        boolean isMethod(final Method method)
        {
            if (method == null)
                return false;
            return this == method;
        }
    }
    
    @Override
    public void renderService() throws ServiceException {
        String method = getHttpRequest().getMethod();
        if (Method.isMethod(Method.GET, method )) {
            renderServiceJSONGet();
        } else if (Method.isMethod(Method.POST, method)) {
            String override = getHttpRequest().getHeader(HEADER_X_HTTP_METHOD_OVERRIDE);
            if (Method.isMethod(Method.PUT, override)) {
                renderServiceJSONUpdate(Method.PUT);
            } else if (Method.isMethod(Method.DELETE, override)) {
                renderServiceJSONUpdate(Method.DELETE);
            } else if (Method.isMethod(Method.PATCH, override)) {
                renderServiceJSONUpdate(Method.PATCH);
            }else {
                renderServiceJSONUpdate(Method.POST);
            }
        } else if (Method.isMethod(Method.PUT, method)) {
            renderServiceJSONUpdate(Method.PUT);
        } else if (Method.isMethod(Method.PATCH, method)) {
            renderServiceJSONUpdate(Method.PATCH);
        } else if (HTTP_DELETE.equalsIgnoreCase(method)) {
            renderServiceJSONUpdate(Method.DELETE);
        } else {
            throw new ServiceException(null, ResponseCode.METHOD_NOT_ALLOWED, "Method {0} is not allowed with JSON Rest Service", method); // $NLX-RestDocumentJsonService.Method0isnotallowedwithJSONRestSe-1$
        }
    }
    
    // ==========================================================================
    // Access to the parameters from the request
    // ==========================================================================

    @Override
    protected DocumentParameters wrapDocumentParameters(DocumentParameters parameters) {
        return new RequestDocumentParameter(parameters);
    }   

    protected class RequestDocumentParameter extends DocumentParametersDelegate {
        private boolean ignoreRequestParams;
        protected RequestDocumentParameter(DocumentParameters delegate) {
            super(delegate);
            this.ignoreRequestParams = delegate.isIgnoreRequestParams();
        }
        @Override
        public boolean isIgnoreRequestParams() {
            return ignoreRequestParams;
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
        public String getDocumentUnid() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_DOCUMENTID); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getDocumentUnid();
        }
        @Override
        public String getParentId() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_PARENTID); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getParentId();
        }
        @Override
        public boolean isComputeWithForm() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_COMPUTEWITHFORM); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isComputeWithForm();
        }
        @Override
        public String getFormName() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_FORM); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getFormName();
        }   
        @Override
        public boolean isMarkRead() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_MARKREAD); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isMarkRead();
        }
        @Override
        public String getSince() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_SINCE); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getSince();
        }
        @Override
        public String getSearch() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_SEARCH); 
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return super.getSearch();
        }
        @Override
        public int getSearchMaxDocs() throws ServiceException {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_SEARCHMAXDOCS); 
                if (StringUtil.isNotEmpty(param)) {
                    try {
                        return Integer.parseInt(param);
                    } catch (NumberFormatException nfe) {
                        throw new ServiceException(nfe, ResponseCode.BAD_REQUEST, "Invalid max parameter"); // $NLX-RestDocumentJsonService.Invalidmaxparameter-1$
                    }
                }
            }
            return super.getSearchMaxDocs();
        }
        @Override
        public boolean isStrongType() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter(PARAM_DOC_STRONGTYPE); 
                if (StringUtil.isNotEmpty(param)) {
                    return param.contentEquals(PARAM_VALUE_TRUE);
                }
            }
            return super.isStrongType();
        }

    }       

    // ==========================================================================
    // GET: read the data
    // ==========================================================================
    
    protected void renderServiceJSONGet() throws ServiceException {
        try {
            DocumentParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_APPLICATION_JSON;
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding(ENCODING_UTF8);
            
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            boolean compact = parameters.isCompact();
            JsonWriter jsonWriter = new JsonWriter(writer,compact); 

            try {
                int global = parameters.getGlobalValues();
                if((global & DocumentParameters.GLOBAL_TIMESTAMP)!=0) {
                    // Should go to the HTTP header
//                  jsonWriter.startProperty("@timestamp");
//                  jsonWriter.outDateLiteral(new Date());
//                  jsonWriter.endProperty();
                }
                if((global & DocumentParameters.GLOBAL_ENTRIES)!=0) {
                    boolean defItems = parameters.isDefaultItems();
                    int sysItems = parameters.getSystemItems();
                    if (getDocument() == null) {
                        Database database = this.getDatabase(getParameters());
                        String uri = this.getHttpRequest().getRequestURI() +  PARAM_SEPERATOR;
                        String search = getParameters().getSearch();
                        String since = getParameters().getSince();
                        int max = getParameters().getSearchMaxDocs();
                        JsonDocumentCollectionContent content = factory.createDocumentCollectionContent(database, uri, search, since, max);
                        content.writeDocumentCollection(jsonWriter);
                    }
                    else {
                        boolean strongtype = getParameters().isStrongType();
                        String rtType = null;
                        List<RestDocumentItem> custItems = getParameters().getItems();                                                
                        String id;
                        try {
                            id = getDocumentUnid();
                        } catch (NotesException ex) {
                            throw new ServiceException(ex, msgErrorGettingDocument());
                        }
                        if(!queryOpenDocument(id)) {
                            throw new ServiceException(null, msgErrorGettingDocument());
                        }       
                        Document document;
                        try {
                            document = this.getDatabase(getParameters()).getDocumentByUNID(id); 
                        } catch (NotesException ex) {
                            throw new ServiceException(ex, msgErrorGettingDocument());
                        }
                        postOpenDocument(document);
                        JsonDocumentContent content = factory.createDocumentContent(document, this);
                        // TODO: Add rtType parameter;                        
                        content.writeDocumentAsJson(jsonWriter, sysItems, defItems, custItems, strongtype, rtType, null);
                        if (getParameters().isMarkRead()) {
                            document.markRead();                    
                        }
                    }
                }
            } catch (NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            } finally {
                writer.flush();
            }       
        
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }

    private String msgErrorGettingDocument() {
        // extracted to a method to prevent re-translation for every occurance 
        return "Error getting document.";  // $NLX-RestDocumentJsonService.Errorgettingdocument-1$
    }

    protected void renderServiceJSONUpdate(Method method) throws ServiceException {
        DocumentParameters parameters = getParameters();
        // Parse the JSON content if needed.        
        JsonJavaObject json = null;
        if (!method.isMethod(Method.DELETE)) {
            // Look if the request seems correct
            String reqContentType = getHttpRequest().getContentType();
            if(!reqContentType.contains(CONTENTTYPE_APPLICATION_JSON)) {
                throw new ServiceException(null,ResponseCode.BAD_REQUEST,"Request does not contains 'application/json' but {0}",reqContentType); // $NLX-RestDocumentJsonService.Requestdoesnotcontainsapplication-1$
            }
            JsonJavaFactory factory = JsonJavaFactory.instanceEx;
            try {
                Reader r = getHttpRequest().getReader();
                try {
                    json = (JsonJavaObject)JsonParser.fromJson(factory, r);
                } finally {
                    r.close();
                }
            } catch(Exception ex) {
                throw new ServiceException(ex, "Error while parsing the JSON content"); // $NLX-RestDocumentJsonService.ErrorwhileparsingtheJSONcontent-1$
            }
        }
        // Process the request.
        switch(method) {
            case POST:      createDocument(json); break;
            case PUT:       updateDocument(json, true); break;
            case PATCH:     updateDocument(json, false); break;
            case DELETE:    deleteDocument(); break;
        }
        // Set the response.
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
    
    protected void createDocument(JsonJavaObject items) throws ServiceException {
        if(!queryNewDocument()) {
            throw new ServiceException(null, msgErrorCreatingDocument());
        }
        Document document;      
        try {
            document = this.getDatabase(getParameters()).createDocument();
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorCreatingDocument());
        }
        postNewDocument(document);
        try {
            JsonDocumentContent content = factory.createDocumentContent(document, this);
            String form = getParameters().getFormName();
            if (StringUtil.isNotEmpty(form)) {
                document.replaceItemValue(ITEM_FORM, form);
            }
            content.updateFields(items, false);
            String parentId = getParameters().getParentId();
            if (StringUtil.isNotEmpty(parentId)) {
                Document parent = null;
                try {
                    parent = database.getDocumentByUNID(parentId);
                    document.makeResponse(parent);
                }
                catch (NotesException e) {
                    throw new ServiceException(e, msgErrorCreatingDocument());
                } finally {
                    if ( parent != null ) {
                        try {
                            parent.recycle();
                        } catch (NotesException e) {
                            if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                                Loggers.SERVICES_LOGGER.traceDebugp(this, "createDocument", e, // $NON-NLS-1$
                                        "Exception thrown when recycling parent.");// $NON-NLS-1$
                            }
                        }
                        parent = null;
                    }
                }

            }       
            if (getParameters().isComputeWithForm()) {
                document.computeWithForm(true, true);
            }
            if(!querySaveDocument(document)) {
                throw new ServiceException(null, msgErrorCreatingDocument());
            }
            document.save();
            postSaveDocument(document);
            getHttpResponse().setStatus(RSRC_CREATED.httpStatusCode);
            try {
                String baseURL = getHttpRequest().getRequestURI();
                getHttpResponse().addHeader(HEADER_LOCATION, baseURL + document.getUniversalID());
            }
            catch (NotesException e) {
                throw new ServiceException(null, msgErrorCreatingDocument());
            }
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorCreatingDocument());
        } finally {
            if(document != null) {          
                try {
                    document.recycle();
                } catch(NotesException ex) {
                    if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                        Loggers.SERVICES_LOGGER.traceDebugp(this, "createDocument", ex, // $NON-NLS-1$
                                "Exception thrown on recycle."); // $NON-NLS-1$
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    private String msgErrorCreatingDocument() {
        // extracted to a method to prevent re-translation for every occurance 
        return "Error creating document.";// $NLX-RestDocumentJsonService.Errorcreatingdocument-1$
    }
    
    /**
     *  HTTP PUT method only allows a complete replacement of a document.
     *  This proposal adds a new HTTP method, PATCH, to modify an existing HTTP resource.
     *  
     *  http://tools.ietf.org/html/rfc5789
     */ 
    protected void updateDocument(JsonJavaObject items, boolean put) throws ServiceException {
        String id;
        try {
            id = getDocumentUnid();
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorUpdatingDocument());
        }
        if(!queryOpenDocument(id)) {
            throw new ServiceException(null, msgErrorUpdatingDocument());
        }       
        Document document;
        try {
            document = this.getDatabase(getParameters()).getDocumentByUNID(id); 
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorUpdatingDocument());
        }
        postOpenDocument(document);
        try {
            String form = getParameters().getFormName();
            if (StringUtil.isNotEmpty(form)) {
                document.replaceItemValue(ITEM_FORM, form);
            }
            JsonDocumentContent content = factory.createDocumentContent(document, this);
            content.updateFields(items, put);
            if (getParameters().isComputeWithForm()) {
                document.computeWithForm(true, true);
            }           
            if(!querySaveDocument(document)) {
                throw new ServiceException(null, msgErrorUpdatingDocument());
            }
            document.save();
            postSaveDocument(document);
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorUpdatingDocument());
        } finally {
            if(document != null) {          
                try {
                    document.recycle();
                } catch(NotesException ex) {
                    if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                        Loggers.SERVICES_LOGGER.traceDebugp(this, "updateDocument", ex, // $NON-NLS-1$
                                "Exception thrown on recycle.");// $NON-NLS-1$
                    }
                }
                document = null;
            }
        }
    }

    /**
     * @return
     */
    private String msgErrorUpdatingDocument() {
        // extracted to a method to prevent re-translation for every occurance 
        return "Error updating document."; // $NLX-RestDocumentJsonService.Errorupdatingdocument-1$
    }
    
    protected void deleteDocument() throws ServiceException {
        String id;
        try {
            id = getDocumentUnid();
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorDeletingDocument());
        }
        if(!queryDeleteDocument(id)) {
            throw new ServiceException(null, msgErrorDeletingDocument());
        }
        try {
            Document document = this.getDatabase(getParameters()).getDocumentByUNID(id);
            if(!document.remove(true)) {
                throw new ServiceException(null, "Document is not deleted because another user modified it."); // $NLX-RestDocumentJsonService.Documentisnotdeletedbecauseanothe-1$
            }       
        } catch (NotesException ex) {
            throw new ServiceException(ex, msgErrorDeletingDocument());
        }
        postDeleteDocument(id);
    }

    /**
     * @return
     */
    private String msgErrorDeletingDocument() {
        // extracted to a method to prevent re-translation for every occurance 
        return "Error deleting document."; // $NLX-RestDocumentJsonService.Errordeletingdocument-1$
    }   
    
}