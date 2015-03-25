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

import static com.ibm.domino.services.HttpServiceConstants.CONTENTTYPE_TEXT_HTML;
import static com.ibm.domino.services.HttpServiceConstants.HEADER_CONTENT_CONTENT_DISPOSITION;
import static com.ibm.domino.services.HttpServiceConstants.HEADER_CONTENT_TYPE;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_ATTACHMENTS;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_AUTHORS;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_CONTENT;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_CONTENTTYPE;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_CREATED;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_DATA;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_FORM;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_HREF;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_MODIFIED;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_NOTEID;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_PARENTID;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_TYPE;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_UNID;
import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FILE;
import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FONTS;
import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FORM;
import static com.ibm.domino.services.rest.RestServiceConstants.TYPE_DATETIME;
import static com.ibm.domino.services.rest.RestServiceConstants.TYPE_MULTIPART;
import static com.ibm.domino.services.rest.RestServiceConstants.TYPE_RICHTEXT;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Form;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.commons.json.JsonMimeEntityAdapter;
import com.ibm.domino.commons.json.JsonMimeEntityAdapter.ParserContext;
import com.ibm.domino.commons.mime.MimeEntityHelper;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceConstants;
import com.ibm.domino.services.rest.das.document.DocumentParameters;
import com.ibm.domino.services.rest.das.document.RestDocumentItem;
import com.ibm.domino.services.rest.das.document.RestDocumentService;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.model.domino.DominoUtils;

public class JsonDocumentContent extends JsonContent {  
	
	private RestDocumentService service;
    private Document document;
    private boolean strongType;
    private String rtType;
    private String baseUri;
    
    private static final SimpleDateFormat DATETIME_FORMAT = getDateTimeFormatter();
    private static final int DATETIME_LENGTH = "2015-02-03T17:22:01Z".length(); //$NON-NLS-1$
    
    public JsonDocumentContent(Document document) {
        this.document = document;
    }
    
    public JsonDocumentContent(Document document, RestDocumentService service) {
        this.document = document;
        this.service = service;
    } 
        
    /**
     * Older version of writeDocumentAsJson for compatibility with existing callers.
     * 
     * @param jsonWriter
     * @param sysItems
     * @param defItems
     * @param custItems
     * @param strongType
     * @param rtType
     * @param baseUri
     * @throws IOException
     * @throws ServiceException
     * @throws NotesException
     */
    public void writeDocumentAsJson(JsonWriter jsonWriter, int sysItems, boolean defItems, 
            List<RestDocumentItem> custItems, boolean strongType, String rtType, String baseUri) 
            throws IOException, ServiceException, NotesException {
        writeDocumentAsJson(jsonWriter, sysItems, defItems, 
                null, // No item filter
                false, // By default, do not lowercase item names
                custItems, strongType, rtType, baseUri);
    }
    
    /**
     * Emit JSON output for a Notes document.
     * 
     * Called by Document Service and Document Resource for: 
     * GET Requests.
     * 
     * @param jsonWriter - JSON Writer.
     * @param sysItems - Emit system items.
     * @param defItems - Emit default items.
     * @param custItems - Emit custom items.
     * @param lowercaseItems - Force item names to lower case
     * @param strongtype - Use strong type for objects.
     * @param rtType - Use JSON/MIME output for rich text and mime items.
     * @param baseUri - Use to generate document links.
     * @throws IOException
     * @throws ServiceException
     * @throws NotesException
     */
    public void writeDocumentAsJson(JsonWriter jsonWriter, int sysItems, boolean defItems, 
            List<String> defItemFilter,
            boolean lowercaseItems, List<RestDocumentItem> custItems, 
            boolean strongType, String rtType, String baseUri) 
            throws IOException, ServiceException, NotesException {
        jsonWriter.startObject();
        try {
            this.strongType = strongType;
            this.rtType = rtType;
            this.baseUri = baseUri;
            writeSystemItems(jsonWriter,  sysItems);
            writeItems(jsonWriter, defItems, defItemFilter, lowercaseItems, custItems);
        } finally {
            jsonWriter.endObject();
        }       
    }
    
    protected void writeSystemItems(JsonWriter jsonWriter,  int sysItem) 
            throws IOException, NotesException, ServiceException {
        // Write the system properties.
        if (StringUtil.isNotEmpty(baseUri)) {
            writeProperty(jsonWriter, ATTR_HREF, baseUri + "/" + document.getUniversalID());  // $NON-NLS-1$ 
        }       
        if ((sysItem & DocumentParameters.SYS_ITEM_UNID)!=0) {
            writeProperty(jsonWriter, ATTR_UNID, document.getUniversalID());
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_NOTEID)!=0) {
            writeProperty(jsonWriter, ATTR_NOTEID, document.getNoteID());
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_PARENTID)!=0 && document.isResponse()) {
            writeProperty(jsonWriter, ATTR_PARENTID, document.getParentDocumentUNID());
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_CREATED)!=0) {
            writeProperty(jsonWriter, ATTR_CREATED, document.getCreated());         
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_MODIFIED)!=0) {
            writeProperty(jsonWriter, ATTR_MODIFIED, document.getLastModified());           
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_AUTHORS)!=0) {
            writeProperty(jsonWriter, ATTR_AUTHORS, document.getAuthors());         
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_FORM)!=0) {
            String form = document.getItemValueString(ITEM_FORM);
            if (form != null && form.length() > 0) {
                writeProperty(jsonWriter, ATTR_FORM, form);
            }
        }
        if ((sysItem & DocumentParameters.SYS_ITEM_HIDDEN)!=0) {
            writeHiddenSystemItems(jsonWriter, document);
        }
    }
        
    protected void writeHiddenSystemItems(JsonWriter jsonWriter, Document document) 
            throws IOException, ServiceException {
        try {
            Vector<?> items = document.getItems();
            
            for (int i = 0; i < items.size(); ++i) {
                Item item = (Item)items.get(i);
                String itemName = item.getName();
                if (itemName.charAt(0) == '$') {
//                  String text = item.getText();
//                  if (text.length() > 0) {
//                      writeProperty(jsonWriter, itemName, text);  
//                  }
                    if (itemName.equalsIgnoreCase(ITEM_FILE)) {
                        // Ignore $FILE item.
                    }
                    if (itemName.equalsIgnoreCase(ITEM_FONTS)) {
                        // Ignore $FONTS item.
                    } else {
                        writeItem(jsonWriter, item, false);
                    }
                }
            }
        } catch (NotesException ex) {
            throw new ServiceException(ex);
        }
    }
    
    protected void writeItems(JsonWriter jsonWriter,  boolean defItems, List<String> defItemFilter,
            boolean lowercaseItems, List<RestDocumentItem> custItems) 
            throws IOException, ServiceException, NotesException {
        @SuppressWarnings("unchecked") //  // $NON-NLS-1$
        Vector<Item> items = document.getItems();       

        // Read the default items
        if (defItems) {
            for (Item item: items) {
                String itemName = item.getName();
                if (itemName.charAt(0) == '$') { // $NON-NLS-1$
                    // Written in System Items. 
                }
                else if (itemName.equalsIgnoreCase(ITEM_FORM)) {
                    // Written in System Items. 
                }
                else if ( defItemFilter == null || defItemFilter.contains(itemName.toLowerCase())) {
                    writeItem(jsonWriter, item, lowercaseItems);
                }
            }
        }
        // Calculate the custom items
        if (custItems != null) {
            for (RestDocumentItem custItem : custItems) {               
                String name = custItem.getName();
                Object itemValue = custItem.evaluate(service, document);
                writeProperty(jsonWriter, name, itemValue);
            }
        }
    }

    protected void writeItem(JsonWriter jsonWriter, Item item, boolean lowercaseItems) 
            throws NotesException, IOException, ServiceException {
        writeItem(jsonWriter, item, 
            lowercaseItems ? item.getName().toLowerCase() : item.getName());
    }
    
    Set<String> writtenMimeItems = null;
    Set<String> writtenRichTextItems = null;
    
    protected void writeItem(JsonWriter jsonWriter, Item item, String propName) 
            throws NotesException, IOException, ServiceException {        
        String itemName = item.getName();
        int itemType = item.getType();
        
        if (itemType == Item.OTHEROBJECT) {
            // Skip Other Object Types
        }
        else if (itemType == Item.ATTACHMENT) {
            // Skip Attachment Types
        }
        else if (itemType == Item.NOTELINKS) {
            // Skip Note Links Types
        }
        else if (itemType == Item.SIGNATURE) {
            // Skip Signature Types
        }       
        else if (itemType == Item.RICHTEXT) {
            if (writtenRichTextItems == null) {
                writtenRichTextItems = new HashSet<String>();
            }
            if (!writtenRichTextItems.contains(itemName)) {
                writtenRichTextItems.add(itemName);
                writeRichTextItem(jsonWriter, item, propName);      
            }
        } 
        else if (itemType == Item.MIME_PART) {
            if (writtenMimeItems == null) {
                writtenMimeItems = new HashSet<String>();
            }
            if (!writtenMimeItems.contains(itemName)) {
                writtenMimeItems.add(itemName);
                writeRichTextItem(jsonWriter, item, propName);      
            }
        } 
        else {
            Vector<?> values = item.getValues();
            if (values != null && values.size() != 0) {
                if (values.size() == 1) {                           
                    writeProperty(jsonWriter, propName, values.get(0));
                } else {
                    writeProperty(jsonWriter, propName, values);
                }
            }
        }
    }

    protected void writeRichTextItem(JsonWriter jsonWriter, Item item, String propName) throws IOException, NotesException, ServiceException  {
        if (StringUtil.isNotEmpty(rtType) && rtType.contentEquals(TYPE_RICHTEXT)) {
        	 writeItemAsHtml(jsonWriter, item, propName);  
        }
        else {
        	writeItemAsMime(jsonWriter, item, propName);           
        }
    }
    
    protected void writeItemAsMime(JsonWriter jsonWriter, Item item, String propName) throws IOException, NotesException, ServiceException   {
        MimeEntityHelper helper = null;
    	String itemName = item.getName();
        jsonWriter.startProperty(propName); 
        jsonWriter.startObject();   
        writeProperty(jsonWriter, ATTR_TYPE, TYPE_MULTIPART);
        jsonWriter.startProperty(ATTR_CONTENT);
        try {
			List<JsonMimeEntityAdapter> adapters = new ArrayList<JsonMimeEntityAdapter>();
			helper = new MimeEntityHelper(document, itemName);
			MIMEEntity entity = helper.getFirstMimeEntity(true);
			if ( entity != null ) {
				JsonMimeEntityAdapter.addEntityAdapter(adapters, entity);
			}
			jsonWriter.outLiteral(adapters);
        }
        catch (JsonException e) {
        	throw new ServiceException(e);
        }  
        finally {
        	if ( helper != null ) {
        		helper.recycle();
        	}
        	jsonWriter.endProperty();
            jsonWriter.endObject();
            jsonWriter.endProperty();
        }
    }

    protected void writeItemAsHtml(JsonWriter jsonWriter, Item item, String propName) throws IOException, NotesException  {
    	DominoUtils.HtmlConverterWrapper converter = null;
    	String itemName = item.getName();
        jsonWriter.startProperty(propName); 
        jsonWriter.startObject();
        try {
            converter = new DominoUtils.HtmlConverterWrapper();
            writeProperty(jsonWriter, ATTR_TYPE, TYPE_RICHTEXT);
            writeProperty(jsonWriter, ATTR_CONTENTTYPE, CONTENTTYPE_TEXT_HTML); 
            // Convert item to HTML.
            converter.convertItem(item.getParent(), itemName);          
            String htmlContent = converter.getConverterText();      
            Vector<String> attachments = converter.getReferneceUrls();
            writeHtmlAttachmentReference(jsonWriter, htmlContent, attachments);
        }  finally {
            jsonWriter.endObject();
            jsonWriter.endProperty();
        	if (converter != null) {
        		converter.recycle();
        	}
        }
    }
    
    private void writeHtmlAttachmentReference(JsonWriter jsonWriter, String htmlContent, Vector<String> attachments) throws IOException {
        
        if (StringUtil.isEmpty(htmlContent) && attachments.isEmpty()) {
            return;
        }       
        String basePathUri = "";  // $NON-NLS-1$ 
        if (StringUtil.isNotEmpty(baseUri) && StringUtil.isNotEmpty(htmlContent)) {
            // SPR# DDEY9EYLC2: When it's a relative URL, leave basePathUri empty.
            if ( !baseUri.startsWith("/") ) {
                basePathUri = baseUri.substring(0, baseUri.indexOf('/', baseUri.indexOf("//") + 2));// $NON-NLS-1$
                for (String attachment: attachments) {              
                    htmlContent = htmlContent.replace(attachment, basePathUri + attachment);
                }
            }
        }
        if (StringUtil.isNotEmpty(htmlContent)) {
            writeProperty(jsonWriter, ATTR_DATA, htmlContent);
        }
        if (!attachments.isEmpty()) {
            jsonWriter.startProperty(ATTR_ATTACHMENTS); 
            jsonWriter.startArray();
            for (String attachment: attachments) {
                jsonWriter.startArrayItem();
                jsonWriter.startObject();
                writeProperty(jsonWriter, RestServiceConstants.ATTR_HREF, basePathUri + attachment);
                jsonWriter.endObject();
                jsonWriter.endArrayItem();
            }
            jsonWriter.endArray();
            jsonWriter.endProperty();
        }
    }

    protected void writeProperty(JsonWriter jsonWriter, String propName, Vector<?> propValues) throws IOException {
        Object value = null;
        if (propValues != null && propValues.size() > 0) {                          
            value = propValues.get(0);
        } 
        else {
            return;
        }       
        if(value instanceof DateTime && strongType) {
            jsonWriter.startProperty(propName); 
            jsonWriter.startObject();
            try {
                if (propValues.size() <= 1) {                           
                    writeDominoProperty(jsonWriter, ATTR_DATA, propValues.get(0));
                } else {
                    writeDominoProperty(jsonWriter, ATTR_DATA, propValues);
                }
                writeProperty(jsonWriter, ATTR_TYPE, TYPE_DATETIME);

            }  finally {
                jsonWriter.endObject();
                jsonWriter.endProperty();
            }       
        }
        else {
            if (propValues.size() <= 1) {                           
                writeDominoProperty(jsonWriter, propName, propValues.get(0));
            } else {
                writeDominoProperty(jsonWriter, propName, propValues);
            }
        }
    }

    protected void writeProperty(JsonWriter jsonWriter, String propName, DateTime propValue) throws IOException {
        if(strongType) {
            jsonWriter.startProperty(propName); 
            jsonWriter.startObject();
            try {
                writeDominoProperty(jsonWriter, ATTR_DATA, propValue);
                writeProperty(jsonWriter, ATTR_TYPE, TYPE_DATETIME);

            }  finally {
                jsonWriter.endObject();
                jsonWriter.endProperty();
            }
        }
        else {
            writeDominoProperty(jsonWriter, propName, propValue);
        }
    }

    protected void writeProperty(JsonWriter jsonWriter, String propName, Object propValue) throws IOException {
        if (propValue instanceof DateTime) {
            writeProperty(jsonWriter, propName, (DateTime)propValue);
        }
        else if (propValue instanceof Vector) {
            writeProperty(jsonWriter, propName, (Vector<?>)propValue);
        }
        else {
            writeDominoProperty(jsonWriter, propName, propValue);
        }
    }

    
    /**
     * Update Notes Items in a Notes Document.
     * Called by Document Service and Document Resource for: 
     * POST PUT PATCH Requests.
     * 
     * @param items
     * @param put
     * @throws NotesException
     * @throws ServiceException 
     */
    public void updateFields(JsonJavaObject items, boolean put) throws NotesException, ServiceException { 
        Form form = null;
        if (document.hasItem(ITEM_FORM)) {
            String formName = document.getItemValueString(ITEM_FORM);
            form = document.getParentDatabase().getForm(formName);
        }       
        // Create a list of Notes items. 
        Set<String> notesItems = null;
        if (put) {
            notesItems = new HashSet<String> ();    
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            Vector<Item> nItems = document.getItems();
            for (Item item: nItems) {           
                //Item item = (Item) nItems.get(i);
                String itemName = item.getName();
                if (itemName.charAt(0) != '$' && !itemName.equalsIgnoreCase(ITEM_FORM)) {
                    notesItems.add(itemName);
                }               
            }
        }
        // Processs items and remove the item from list if updated.
        for(Iterator<String> it = items.getJsonProperties(); it.hasNext(); ) {
            String itemName = it.next();
            if (itemName != null) { 
                Object value = items.get(itemName);
                if (itemName.charAt(0) != '$' && itemName.charAt(0) != '@' && !itemName.equalsIgnoreCase(ITEM_FORM)) {
                    updateField(form, itemName, value);
                    if (put) {
                        notesItems.remove(itemName);
                    }
                }
            }
        }
        // Remove the item if not updated.
        if (put) {
            for (String itemName : notesItems) {
                document.removeItem(itemName);  
            }
        }   
    }

    protected void updateField(Form form, String itemName, Object value) 
            throws NotesException, ServiceException {
        if (value instanceof JsonJavaObject) {
            String type = (String)((JsonJavaObject)value).get(ATTR_TYPE); 
            if (type == null) {
                // Backward compatible with earlier version we may want to change.
                //updateRichText(itemName, value);
            }
            else if (type.equalsIgnoreCase(TYPE_DATETIME)) {
                Object data = ((JsonJavaObject)value).get(ATTR_DATA);   
                if (data instanceof List) {
                    updateDateTimeList(itemName, (List<?>)data);
                }
                else {
                    updateDateTime(itemName, data);
                }
            }
            else if (type.equalsIgnoreCase(TYPE_RICHTEXT)) {
                updateRichText(itemName, (JsonJavaObject)value);
            }
            else if (type.equalsIgnoreCase(TYPE_MULTIPART)) {
            	try {
            		updateMimePart(itemName, (JsonJavaObject)value);
            	}
            	catch (JsonException e) {
            		throw new ServiceException(e);
            	}
            }
        }       
        else if (value instanceof List) {
            //Vector<?> vector = new Vector((List)value);
            //document.replaceItemValue(itemName, vector);
            replaceItemValueList(form, itemName, (List<?>)value);
        }
        else {
            //document.replaceItemValue(itemName, value);
            replaceItemValue(form, itemName, value);
        }
    }

    private void replaceItemValue(Form form, String itemName, Object value) 
            throws NotesException {
        if (form != null) {
            int type = form.getFieldType(itemName);
            if (type == Item.DATETIMES) {
                updateDateTime(itemName, value);
                return;
            }       
        }               
        document.replaceItemValue(itemName, value);
    }
    
    private void replaceItemValueList(Form form, String itemName, List<?> list) 
            throws NotesException {
        if (form != null) {
            int type = form.getFieldType(itemName);
            if (type == Item.DATETIMES || type == 1025) {
                updateDateTimeList(itemName, list);
                return;
            }           
        }               
        Vector<?> vector = new Vector<Object>((List<?>)list);
        document.replaceItemValue(itemName, vector);
    }
    
    private DateTime toDateTime(Object value) throws NotesException {
        DateTime datetime = null;
        
        if ( value instanceof String ) {
            // SPR# DDEY9SRL6C: If possible, parse to Java Date.  This approach
            // gives the same result regardless of regional settings in the OS.
            
            SimpleDateFormat formatter = null;
            if ( ((String)value).length() == DATETIME_LENGTH && ((String)value).endsWith("Z") ) {
                formatter = DATETIME_FORMAT;
            }
            
            // TODO: Ideally, we should parse date-only values (e.g. "2015-02-03") in Java too.  
            // However, the result of parsing such a value is different in Notes.  In Notes
            // it truly is a date without a time.  In Java the time defaults to midnight UTC.
            // For now, rely on the Notes parser -- even though regional settings may vary the result.
            
            if ( formatter != null ) {
                try {
                    value = formatter.parse((String)value);
                }
                catch (Throwable t) {
                    // Ignore parser exceptions and fall through
                    // to Notes parser below
                }
            }
        }
        
        if (value instanceof Date){
            datetime = document.getParentDatabase().getParent().createDateTime((Date)value); 
        }
        else if (value instanceof String){                          
            String dtString = (String)value;
            if (dtString.endsWith("Z")) { // $NON-NLS-1$
                dtString = dtString.substring(0, dtString.lastIndexOf('Z')) + " GMT"; // $NON-NLS-1$ // $NON-NLS-2$
            }
            datetime = document.getParentDatabase().getParent().createDateTime(dtString);
        }
        
        return datetime;
    }

    private void updateDateTime(String itemName, Object value)
            throws NotesException {
        DateTime datetime = toDateTime(value);

        if (datetime != null) {
            document.replaceItemValue(itemName, datetime);
            datetime.recycle();
            datetime = null;
        }
    }   

    private void updateDateTimeList(String itemName, List<?> list) 
            throws NotesException {
        Vector<DateTime> datetimes = new Vector<DateTime>();
        for(Object value: list) {
            DateTime datetime = toDateTime(value);
            datetimes.add(datetime);
        }
        if (datetimes != null) {
            document.replaceItemValue(itemName, datetimes);
            for (DateTime datetime: datetimes) {
                datetime.recycle();
                datetimes = null;
            }
        }
    }

    private void updateRichText(String itemName, Object value)
            throws NotesException, ServiceException {
        MIMEEntity mime = null;
        Object attachmentObject =  (Object)((JsonJavaObject)value).get(ATTR_ATTACHMENTS);  
        // If the request contains any references to attachments throw an exception.
        if (attachmentObject != null){
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            ArrayList<JsonJavaObject> jsonObjects = (ArrayList<JsonJavaObject>) attachmentObject;
            if (!jsonObjects.isEmpty()) {
                throw new ServiceException(null, msgUpdatingEmbeddedObjectsNotSupported());  
            }
        }
        if (document.hasItem(itemName)) {
            Item item = document.getFirstItem(itemName);            
            if (item.getType() == Item.MIME_PART) {             
                MIMEEntity mimeEntity = document.getMIMEEntity(itemName);
                MIMEEntity mChild = mimeEntity.getFirstChildEntity();
                while (mChild != null) {
                    MIMEHeader mimeHeader = mChild.getNthHeader(HEADER_CONTENT_TYPE);
                    // Check Content-Type header for embedded objects.
                    if (mimeHeader != null) {
                        String contentType = mimeHeader.getHeaderValAndParams();
                        if (StringUtil.isNotEmpty(contentType) && contentType.toLowerCase().contains(" name=\"")) { // $NON-NLS-1$
                        	mimeEntity.recycle();
                        	throw new ServiceException(null, msgUpdatingEmbeddedObjectsNotSupported());
                        }
                    }
                    // Check Content-Disposition header for embedded objects.
                    mimeHeader = mChild.getNthHeader(HEADER_CONTENT_CONTENT_DISPOSITION);
                    if (mimeHeader != null) {
                        String contentDisposition = mimeHeader.getHeaderValAndParams();
                        if (StringUtil.isNotEmpty(contentDisposition) && contentDisposition.toLowerCase().contains(" filename=\"")) { // $NON-NLS-1$
                        	mimeEntity.recycle();
                        	throw new ServiceException(null, msgUpdatingEmbeddedObjectsNotSupported());
                        }
                    }
                    MIMEEntity mChild2 = mChild.getFirstChildEntity();                    
                    if (mChild2 == null) {
                        mChild2 = mChild.getNextSibling();
                        if (mChild2 == null) {
                            mChild2 = mChild.getParentEntity();
                            if (mChild2 != null)
                                mChild2 = mChild2.getNextSibling();
                        }
                    }
                    mChild.recycle();
                    mChild = mChild2;
                }
                mimeEntity.remove();
                mimeEntity.recycle();               
                document.closeMIMEEntities(false, itemName);
            } 
            if (item.getType() == Item.RICHTEXT) {
                RichTextItem rtItem = (RichTextItem)item;
                if (!rtItem.getEmbeddedObjects().isEmpty()) {
                    throw new ServiceException(null, msgUpdatingEmbeddedObjectsNotSupported());
                }
            }
            document.removeItem(itemName);  
            mime = document.createMIMEEntity(itemName);
        }
        else {
            mime = document.createMIMEEntity(itemName);
        }
        if (mime != null) {
            String contentType = (String) ((JsonJavaObject)value).get(ATTR_CONTENTTYPE);
            // For now we treat everything as text/html.
            if (StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_TEXT_HTML;
            }
            String data = (String) ((JsonJavaObject)value).get(ATTR_DATA);         
            if (StringUtil.isEmpty(data)) {
                // If the data is empty the call to closeMIMEEntities throws and exception.
                data = " "; // $NON-NLS-1$
            }
            lotus.domino.Session session = document.getParentDatabase().getParent();
            lotus.domino.Stream stream = session.createStream();
            stream.writeText(data);
            // TODO: Add support for other encodings. 
            MIMEEntity childEntity = mime.createChildEntity();
            childEntity.setContentFromText(stream, contentType, lotus.domino.MIMEEntity.ENC_NONE);
            stream.close();
            stream.recycle();
            document.closeMIMEEntities(true, itemName);
            mime.recycle();
        }
        else {
            throw new NullPointerException();
        }
    }

    /**
     * @return
     */
    private String msgUpdatingEmbeddedObjectsNotSupported() {
        // extracted to a method so it is only translated once,
        // not re-translated for every occurrance
        return "Updating embedded objects is not supported.";// $NLX-JsonDocumentContent.Updatingembeddedobjectsisnotsuppo-1$
    }

    private void updateMimePart(String itemName, JsonJavaObject jsonObject) throws NotesException, JsonException {
    	
    	if ( document.hasItem(itemName) ) {
    		Item item = document.getFirstItem(itemName);
    		if ( item.getType() == Item.MIME_PART ) {
    			
    			// SPR #JJVX8RGJZG:  When updating _existing_ MIME parts, we were
    			// leaving old attachments around.  The fix is to explicitly remove
    			// the parts first.
    			
    			MIMEEntity entity = item.getMIMEEntity();
    			entity.remove();
    			document.closeMIMEEntities(true, itemName);
    		}
    	}
    	
        Object jsonMime =  jsonObject.get(ATTR_CONTENT);
    	List<JsonJavaObject> parts = (List<JsonJavaObject>)jsonMime;
    	ParserContext context = new ParserContext(document, itemName);
    	for ( int i = 0; i < parts.size(); i++ ) {
    		JsonJavaObject obj = parts.get(i);
    		boolean lastEntity = false;
    		if ( (i+1) == parts.size() ) {
    			lastEntity = true;
    		}
    		
    		JsonMimeEntityAdapter adapter = new JsonMimeEntityAdapter(context, obj);
    		context.setCurrentEntityAdapter(adapter);
    		adapter.flushJsonProperties(lastEntity);
    	}
    }

    private static SimpleDateFormat getDateTimeFormatter() {
        TimeZone tz = TimeZone.getTimeZone("UTC"); // $NON-NLS-1$
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
        formatter.setTimeZone(tz);
        return formatter;
    }

}