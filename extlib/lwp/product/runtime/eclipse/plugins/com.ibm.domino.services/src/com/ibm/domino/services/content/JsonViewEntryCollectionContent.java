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

package com.ibm.domino.services.content;

import static com.ibm.domino.services.rest.RestServiceConstants.*;
import static com.ibm.domino.services.util.ContentUtil.getContentRangeHeaderString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.RestDocumentNavigator;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.RestViewNavigator;
import com.ibm.domino.services.rest.das.view.RestViewNavigatorFactory;
import com.ibm.domino.services.rest.das.view.RestViewService;
import com.ibm.domino.services.rest.das.view.ViewParameters;
import com.ibm.domino.services.util.JsonWriter;
//import com.ibm.domino.services.util.ContentUtil;

public class JsonViewEntryCollectionContent extends JsonContent {
    
    public static final int GLOBAL_ALL = 0xFFFFFFFF;
    
    private static final String FOLDER_OP_ADD = "add";          //$NON-NLS-1$
    private static final String FOLDER_OP_REMOVE = "remove";    //$NON-NLS-1$   

    private View _view;
    private RestViewService _service;
    private RestViewNavigator _navigator;
    private String _baseUri;
    private String _baseDocumentUri;
    
    public JsonViewEntryCollectionContent(View view) {
        _view = view;
    }   

    public JsonViewEntryCollectionContent(View view, String baseUri, String baseDocumentUri) {
        _view = view;
        _baseUri = baseUri;
        _baseDocumentUri = baseDocumentUri;
    }   

    public JsonViewEntryCollectionContent(View view, RestViewService service) {
        _view = view;
        _service = service;
    }
    
    /**
     * Get content range header for a view entry collection.
     * 
     * @param parameters
     * @return The HEADER_CONTENT_RANGE used for response header.
     * @throws ServiceException
     */
    public String  getContentRangeHeader(ViewParameters parameters) throws ServiceException {
    
        String contentRangeHeader = null;
        
        try {
            // Create the new XPages view navigator
            if (_navigator == null) {
                _navigator = RestViewNavigatorFactory.createNavigator(_view, parameters);
            }
            int global = parameters.getGlobalValues();
            if((global & ViewParameters.GLOBAL_ENTRIES)!=0) {
                // Read all the entries
                int start = parameters.getStart();
                int count = parameters.getCount();
                // The TopLevelEntryCount can be expensive.
                _navigator.first(start,count);
                if((global & ViewParameters.GLOBAL_TOPLEVEL)!=0) {
                    // Header returned to the client:
                    //    Content-Range: items 0-24/66
                    int entryCount = _navigator.getTopLevelEntryCount();
                    if(entryCount>0 && start<entryCount) {
                        int last = entryCount-1<count+start-1?entryCount-1:count+start-1;       
                        contentRangeHeader = getContentRangeHeaderString(start, last, entryCount);
                    }
                } 
            }                           
        } catch(Exception ex) {
            throw new ServiceException(ex,"");  // $NON-NLS-1$ 
        } 
        return contentRangeHeader;
    }   
    /**
     * Write JSON for a view entry collection.
     * 
     * @param jsonWriter
     * @param parameters
     * @throws ServiceException
     */
    public void  writeViewEntryCollection(JsonWriter jsonWriter,  ViewParameters parameters) throws ServiceException {
    
        try {
            // Create the new XPages view navigator
            if (_navigator == null) {
                _navigator = RestViewNavigatorFactory.createNavigator(_view, parameters);
            }
            try {
                int global = parameters.getGlobalValues();
                if((global & ViewParameters.GLOBAL_TIMESTAMP)!=0) {
                    // Should go to the HTTP header
//                  jsonWriter.startProperty("@timestamp");
//                  jsonWriter.outDateLiteral(new Date());
//                  jsonWriter.endProperty();
                }
                if((global & ViewParameters.GLOBAL_ENTRIES)!=0) {
                    jsonWriter.startArray();
                    // Read all the entries
                    int start = parameters.getStart();
                    int count = parameters.getCount();
                    int syscol = parameters.getSystemColumns();
                    boolean defColumns = parameters.isDefaultColumns();
                    List<RestViewColumn> columns = parameters.getColumns();
    
                    int idx = 0;
                    for( boolean b=_navigator.first(start,count); b && idx<count; b=_navigator.next(), idx++) {
                        jsonWriter.startArrayItem();
                        writeEntryAsJson(jsonWriter,syscol,defColumns,columns,_navigator,_baseUri,_baseDocumentUri);
                        jsonWriter.endArrayItem();
                    }   
                    jsonWriter.endArray();
                }
            } finally {
                if (_navigator != null) {
                    _navigator.recycle();
                }
            }           
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,"");  // $NON-NLS-1$ 
        } catch(IOException ex) {
            throw new ServiceException(ex,"");  // $NON-NLS-1$ 
        }
    }
    
    protected void writeEntryAsJson(JsonWriter jsonWriter, int syscol, boolean defColumns, List<RestViewColumn> columns, RestViewNavigator navigator,
                    String baseUri, String baseDocumentUri) throws IOException, ServiceException {
        jsonWriter.startObject();
        writeSystemColumns(jsonWriter, syscol, navigator, false, baseUri, baseDocumentUri);
        writeColumns(jsonWriter, defColumns, columns, navigator);
        jsonWriter.endObject();
    }
    
    protected void writeColumns(JsonWriter jsonWriter, boolean defColumns, List<RestViewColumn> columns, RestViewNavigator navigator) throws IOException, ServiceException {
        int colIdx = 0;

        // Read the default columns
        if(defColumns) {
            int colCount = navigator.getColumnCount();
            for(int i=0; i<colCount; i++) {
                String colName = navigator.getColumnName(i);
                Object colValue = navigator.getColumnValue(i);
                writeColumn(jsonWriter, navigator, colIdx++, colName, colValue);
            }
        }
        // Calculate the extra columns
        int ccount = columns!=null ? columns.size() : 0; 
        if(ccount>0) {
            for( int i=0; i<ccount; i++) {
                RestViewColumn c = columns.get(i);
                String colName = c.getName();
                Object colValue = c.evaluate(_service, navigator);
                writeCustomColumn(jsonWriter, navigator, colIdx++, colName, colValue, c);
            }
        }
    }
    
    protected void writeSystemColumns(JsonWriter jsonWriter, int syscol, RestViewNavigator navigator, boolean forceDefaultAttributes,
                    String baseUri, String baseDocumentUri) throws IOException, ServiceException {
        // write the system columns
        if ((syscol & ViewParameters.SYSCOL_HREF)!=0 && baseUri != null  && !navigator.isCategory() && navigator.isDocument()) {
            jsonWriter.startProperty(ATTR_HREF);
            jsonWriter.outStringLiteral(baseUri + "/" + navigator.getUniversalId());  // $NON-NLS-1$ 
            jsonWriter.endProperty();
        }
        if ((syscol & ViewParameters.SYSCOL_LINK)!=0 && baseDocumentUri != null && !navigator.isCategory() && navigator.isDocument()) {
	        	jsonWriter.startProperty(ATTR_LINK);
	            jsonWriter.startObject();
	            jsonWriter.startProperty(ATTR_LINK_REL);
	            jsonWriter.outStringLiteral("document"); // TODO: Check Vulcan guidelines $NON-NLS-1$
	            jsonWriter.endProperty();   
	            jsonWriter.startProperty(ATTR_LINK_HREF);
	            jsonWriter.outStringLiteral(baseDocumentUri + "/" + navigator.getUniversalId());  // $NON-NLS-1$ 
	            jsonWriter.endProperty();
	            jsonWriter.endObject();
	            jsonWriter.endProperty();
        }
        if(true) { // Always write the entry id
            jsonWriter.startProperty(ATTR_ENTRYID);
            //jsonWriter.outStringLiteral(getEntryId(navigator));
            jsonWriter.outStringLiteral(navigator.getPosition()+"-"+navigator.getUniversalId());  // $NON-NLS-1$ 
            jsonWriter.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_UNID)!=0) {
            jsonWriter.startProperty(ATTR_UNID);
            jsonWriter.outStringLiteral(navigator.getUniversalId());
            jsonWriter.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_NOTEID)!=0) {
            jsonWriter.startProperty(ATTR_NOTEID);
            jsonWriter.outStringLiteral(navigator.getNoteId());
            jsonWriter.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_POSITION)!=0) {
            jsonWriter.startProperty(ATTR_POSITION);
            jsonWriter.outStringLiteral(navigator.getPosition());
            jsonWriter.endProperty();
        }
        if((syscol & ViewParameters.SYSCOL_READ)!=0) {
            boolean read = navigator.getRead();
            if(forceDefaultAttributes || read) {
                jsonWriter.startProperty(ATTR_READ);
                jsonWriter.outBooleanLiteral(true);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_SIBLINGS)!=0) {
            int count = navigator.getSiblings();
            if(forceDefaultAttributes || count>0) {
                jsonWriter.startProperty(ATTR_SIBLINGS);
                jsonWriter.outIntLiteral(count);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_DESCENDANTS)!=0) {
            int count = navigator.getDescendants();
            if(forceDefaultAttributes || count>0) {
                jsonWriter.startProperty(ATTR_DESCENDANTS);
                jsonWriter.outIntLiteral(count);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_CHILDREN)!=0) {
            int count = navigator.getChildren();
            if(forceDefaultAttributes || count>0) {
                jsonWriter.startProperty(ATTR_CHILDREN);
                jsonWriter.outIntLiteral(count);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_INDENT)!=0) {
            int indent = navigator.getIndent();
            if(forceDefaultAttributes || indent>0) {
                jsonWriter.startProperty(ATTR_INDENT);
                jsonWriter.outIntLiteral(indent);
                jsonWriter.endProperty();
            }
        }
        // Added by Tim Tripcony
        // Expect it to disappear but hope it will remain
        if((syscol & ViewParameters.SYSCOL_FORM)!=0) {
            String form = navigator.getForm();
            if(forceDefaultAttributes || !StringUtil.isEmpty(form)) {
                jsonWriter.startProperty(ATTR_FORM);
                jsonWriter.outStringLiteral(form);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_CATEGORY)!=0) {
            boolean category = navigator.isCategory();
            if(forceDefaultAttributes || category) {
                jsonWriter.startProperty(ATTR_CATEGORY);
                jsonWriter.outBooleanLiteral(category);
                jsonWriter.endProperty();
            }
        }
        if((syscol & ViewParameters.SYSCOL_RESPONSE)!=0) {
            boolean response = navigator.isResponse();
            if(forceDefaultAttributes || response) {
                jsonWriter.startProperty(ATTR_RESPONSE);
                jsonWriter.outBooleanLiteral(response);
                jsonWriter.endProperty();
            }
        }
    }
    
    protected void writeColumn(JsonWriter jsonWriter, RestViewNavigator navigator, int colIdx, String colName, Object colValue) throws IOException, ServiceException {
        writeDominoProperty(jsonWriter, colName, colValue);
    }
    
    protected void writeCustomColumn(JsonWriter jsonWriter, RestViewNavigator navigator, int colIdx, String colName, Object colValue, RestViewColumn column) throws IOException, ServiceException {
        writeDominoProperty(jsonWriter, colName, colValue);
    }
    
    /**
     * Update fields in view entry collection.
     * 
     * @param viewNav
     * @param docNav
     * @param items
     * @throws ServiceException
     * @throws JsonException
     * @throws IOException
     */
    public void updateFields(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items) throws ServiceException, JsonException, IOException {
        for(Iterator<String> it = items.getJsonProperties(); it.hasNext(); ) {
            String columnName = it.next();
            String fieldName = findItemName(viewNav, columnName);
            if(fieldName!=null) { // Ignore columns that are not based on fields
                Object value = items.get(columnName);
                updateField(viewNav, docNav, items, columnName, fieldName, value);
            }
        }
    }
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    private void updateField(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items, String columnName, String fieldName, Object value) throws ServiceException, JsonException, IOException {
        if (value instanceof List) {
            Vector<?> vector = new Vector((List)value);
            docNav.replaceItemValue(fieldName, vector);
        }
        else {
            docNav.replaceItemValue(fieldName, value);
        }
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
        
        if(viewNav.getParameters().isDefaultColumns()) {
            itemName = viewNav.getItemName(columnName);
        }
        // Look for a custom column pointing to an actual column
        if(itemName==null) {
            List<RestViewColumn> cols = viewNav.getParameters().getColumns();
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
        if(itemName!=null) {
            itemsMap.put(columnName, itemName);
        }
        
        return itemName;
    }
    
    /**
     * Updates a folder (adds and/or removes documents)
     * 
     * <p>TODO:  Some of this code is duplicated in RestViewJsonService.  We need
     * to make RestViewJsonService use this code instead.
     * 
     * @param folderOperations
     * @throws ServiceException
     */
    public void updateFolder(JsonJavaObject folderOperations) throws ServiceException {
        
        Map<String, String> operations = getOperationsMap(folderOperations);
        Database db = null;
        String folderName = null;
        int errors = 0;
        
        try {
            db = _view.getParent();
            folderName = _view.getName();
        }
        catch (NotesException ex) {
            throw new ServiceException(ex, "");  // $NON-NLS-1$ 
        }

        for( Map.Entry<String,String> op: operations.entrySet() ) {
            Document doc = null;
            String docunid = op.getKey();
            boolean add = (op.getValue().equalsIgnoreCase(FOLDER_OP_ADD));

            try {
                doc = db.getDocumentByUNID(docunid);

                if (add)
                    doc.putInFolder(folderName);
                else
                    doc.removeFromFolder(folderName);
            }
            catch (NotesException ex) {
                // Keep a count of errors.  Arguably, it's better to keep going
                // through a large set of changes than it is to abort.  This at 
                // least makes the result deterministic.
                errors++;
            }
            
            if (doc != null) {
                try {
                    doc.recycle();
                }
                catch (NotesException ex) {
                    // Ignore this
                }
                doc = null;
            }
        }

        if ( errors > 0 ) {
            throw new ServiceException(new NotesException(), "Error(s) performing {0} of {1} folder operation(s).", errors, operations.size()); // $NLX-JsonViewEntryCollectionContent.Errorsperforming0of1folderoperati-1$
        }
    }

    private Map<String, String> getOperationsMap (JsonJavaObject folderOperations) {
        Map<String, String> docOperations = new HashMap<String, String>();

        for(Iterator<String> it = folderOperations.getJsonProperties(); it.hasNext(); ) {    
            String opName = it.next();
            if ((opName.equalsIgnoreCase(FOLDER_OP_ADD)) || 
                (opName.equalsIgnoreCase(FOLDER_OP_REMOVE))) {
                Object value = folderOperations.get(opName);
                if (value instanceof List) {
                    Vector<?> vector = new Vector((List)value);
                    if (!vector.isEmpty()) {
                        for(Iterator<?> docs = vector.iterator(); docs.hasNext(); ) {
                            String docunid = (String) docs.next();
                            if (docunid.length() > 0)
                                docOperations.put(docunid, opName);     
                        }
                    }
                }
            }
        }
        
        return docOperations;
    }

}