/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.domino.commons.json;

import static com.ibm.domino.commons.json.JsonConstants.BCC_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CC_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CONTENT_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DATE_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DISTINGUISHED_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.EMAIL_PROP;
import static com.ibm.domino.commons.json.JsonConstants.FROM_PROP;
import static com.ibm.domino.commons.json.JsonConstants.HREF_PROP;
import static com.ibm.domino.commons.json.JsonConstants.IN_REPLY_TO_PROP;
import static com.ibm.domino.commons.json.JsonConstants.MESSAGE_ID_PROP;
import static com.ibm.domino.commons.json.JsonConstants.RECEIPT_TO_PROP;
import static com.ibm.domino.commons.json.JsonConstants.SUBJECT_PROP;
import static com.ibm.domino.commons.json.JsonConstants.THREADID_PROP;
import static com.ibm.domino.commons.json.JsonConstants.TO_PROP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.internal.Logger;
import com.ibm.domino.commons.json.JsonMimeEntityAdapter.ParserContext;
import com.ibm.domino.commons.mime.MimeEntityHelper;
import com.ibm.domino.commons.model.Person;
import com.ibm.domino.commons.util.BackendUtil;

public class JsonMessageAdapter implements JsonObject {

    public static final String FORM_ITEM = "Form"; // $NON-NLS-1$
    public static final String MEMO_FORM = "Memo"; // $NON-NLS-1$
    public static final String REPLY_FORM = "Reply"; // $NON-NLS-1$
    
    private static final String SENDTO_ITEM = "SendTo"; // $NON-NLS-1$
    private static final String ENTERSENDTO_ITEM = "EnterSendTo"; // $NON-NLS-1$
    private static final String COPYTO_ITEM = "CopyTo"; // $NON-NLS-1$
    private static final String ENTERCOPYTO_ITEM = "EnterCopyTo"; // $NON-NLS-1$
    private static final String BLINDCOPYTO_ITEM = "BlindCopyTo"; // $NON-NLS-1$
    private static final String ENTERBLINDCOPYTO_ITEM = "EnterBlindCopyTo"; // $NON-NLS-1$
    private static final String FROM_ITEM = "From"; // $NON-NLS-1$
    private static final String MESSAGE_ID_ITEM = "$MessageID"; // $NON-NLS-1$
    private static final String IN_REPLY_TO_ITEM = "In_Reply_To"; // $NON-NLS-1$
    private static final String RETURN_RECEIPT_ITEM = "ReturnReceipt"; //$NON-NLS-1$
    private static final String DISPOSITION_NOTIFICATION_TO_ITEM = "Disposition_Notification_To"; //$NON-NLS-1$
    private static final String POSTEDDATE_ITEM = "PostedDate"; // $NON-NLS-1$
    private static final String SUBJECT_ITEM = "Subject"; // $NON-NLS-1$
    private static final String BODY_ITEM = "Body"; // $NON-NLS-1$
    private static final String INET_PREFIX = "INet"; // $NON-NLS-1$
    private static final String ENTER_PREFIX = "Enter"; // $NON-NLS-1$
    private static final String THREADID_ITEM = "$TUA"; // $NON-NLS-1$
    
	private static SimpleDateFormat ISO8601 = getUtcFormatter();
	
	private Document _document = null;
	private String _url = null;
    private String[] _propertyNames = null;
	private ParserContext _context = null;
	
	/**
	 * Constructor used when generating JSON output.
	 * 
	 * @param document
	 * @param url
	 */
	public JsonMessageAdapter(Document document, String url) {
		_document = document;
		_url = url;
	}
	
	/**
	 * Constructor used when parsing JSON input.
	 * 
	 * @param document
	 * @param context
	 */
	public JsonMessageAdapter(Document document, ParserContext context) {
		_document = document;
		_context = context;
		
		// SPR #BBRL9QYA6A: Remove old items from the document
		removeOldItems();
	}

	public Iterator<String> getJsonProperties() {
		
		return new Iterator<String>() {
			
            private int _index = 0;
            
            public boolean hasNext() {
                String properties[] = getProperties();
                return _index < properties.length ;
            }

            public String next() {
                String properties[] = getProperties();
                return properties[_index++];
            }

			public void remove() {
				// The JSON IO classes shouldn't call remove
			}
			
            @SuppressWarnings("unchecked")//$NON-NLS-1$
			private String[] getProperties() {
                if ( _propertyNames != null ) {
                    return _propertyNames;
                }

                List<String> properties = new ArrayList<String>();
                
                try {
                    String from = _document.getItemValueString(FROM_ITEM);
                    if ( StringUtil.isNotEmpty(from) ) {
                        properties.add(FROM_PROP);
                    }

                    Vector<Object> items = _document.getItemValue(SENDTO_ITEM);
                    if ( items != null ) {
                        Iterator<Object> iterator = items.iterator();
                        if ( iterator.hasNext() ) {
                            properties.add(TO_PROP);
                        }
                        // SPR #BBRL9R99N7: replace item "SendTo" with "EnterSendTo" when there is no "SendTo" in Drafts view
                        // Similar with "CopyTo" and "BlindCopyTo"
                        else if ( !_document.hasItem(POSTEDDATE_ITEM ) ) {
                            items = _document.getItemValue(ENTERSENDTO_ITEM);
                            if ( items != null ) {
                                iterator = items.iterator();
                                if ( iterator.hasNext() ) {
                                    properties.add(TO_PROP);
                                }
                            }
                        }
                    }

                    items = _document.getItemValue(COPYTO_ITEM);
                    if ( items != null ) {
                        Iterator<Object> iterator = items.iterator();
                        if ( iterator.hasNext() ) {
                            properties.add(CC_PROP);
                        }
                        else if ( !_document.hasItem(POSTEDDATE_ITEM ) ) {
                            items = _document.getItemValue(ENTERCOPYTO_ITEM);
                            if ( items != null ) {
                                iterator = items.iterator();
                                if ( iterator.hasNext() ) {
                                    properties.add(CC_PROP);
                                }
                            }
                        }
                    }

                    items = _document.getItemValue(BLINDCOPYTO_ITEM);
                    if ( items != null ) {
                        Iterator<Object> iterator = items.iterator();
                        if ( iterator.hasNext() ) {
                            properties.add(BCC_PROP);
                        }
                        else if ( !_document.hasItem(POSTEDDATE_ITEM ) ) {
                            items = _document.getItemValue(ENTERBLINDCOPYTO_ITEM);
                            if ( items != null ) {
                                iterator = items.iterator();
                                if ( iterator.hasNext() ) {
                                    properties.add(BCC_PROP);
                                }
                            }
                        }
                    }

                    String subject = _document.getItemValueString(SUBJECT_ITEM);
                    if ( StringUtil.isNotEmpty(subject) ) {
                        properties.add(SUBJECT_PROP);
                    }
                    
                    String messageId = _document.getItemValueString(MESSAGE_ID_ITEM);
                    if ( StringUtil.isNotEmpty(messageId) ) {
                        properties.add(MESSAGE_ID_PROP);
                    }
                    
                    String inReplyTo = _document.getItemValueString(IN_REPLY_TO_ITEM);
                    if ( StringUtil.isNotEmpty(inReplyTo) ) {
                        properties.add(IN_REPLY_TO_PROP);
                    }
                    
                    String returnReceipt = _document.getItemValueString(RETURN_RECEIPT_ITEM);
                    if ( "1".equals(returnReceipt) ) {
                        properties.add(RECEIPT_TO_PROP);
                    }
                    
                    properties.add(DATE_PROP);
                    properties.add(HREF_PROP);
                    properties.add(CONTENT_PROP);
                    
					items = _document.getItemValue(THREADID_ITEM);
					if (items != null) {
						Iterator<Object> iterator = items.iterator();
						if (iterator.hasNext()) {
							properties.add(THREADID_PROP);
						}
					}
                }
                catch(NotesException e) {
                    // Ignore
                    Logger.get().warnp(this, "getProperties",//$NON-NLS-1$
                            e, "Unhandled exception getting the list of JSON property names");// $NLW-JsonMessageAdapter_UnhandledExceptionInGetProperties-1$
                }

                // Convert to array
                
                String[] array = new String[properties.size()];
                Iterator<String> iterator = properties.iterator();
                for ( int i = 0; iterator.hasNext(); i++ ) {
                    array[i] = iterator.next();
                }
                
                // Cache the array for next time
                _propertyNames = array;
                
                return _propertyNames;
            }
		};
	}

	@SuppressWarnings("rawtypes")//$NON-NLS-1$
	public Object getJsonProperty(String property) {
		Object value = null;
		
		// This method is called when generating a JSON message
		// from an existing Notes document
		
		try {
			if ( FROM_PROP.equals(property) ) {
				String from = _document.getItemValueString(FROM_ITEM);
				if ( StringUtil.isNotEmpty(from) ) {
	                String email = _document.getItemValueString(INET_PREFIX + FROM_ITEM);
	                if ( StringUtil.isEmpty(email) ) {
	                    email = null;
	                }
	                
	                Session session = _document.getParentDatabase().getParent();
	                Name name = null;
	                try {
	                    // This try block is here to force a recycle of the name object
	                    // before it goes out of scope
	                    
    	                name = session.createName(from);
    	                Person person = new Person(name.getCommon(), name.getAbbreviated(), email);
                        value = new JsonPersonAdapter(person);
	                }
	                finally {
	                    BackendUtil.safeRecycle(name);
	                }
				}
			}
			else if ( SUBJECT_PROP.equals(property) ) {
				String subject = _document.getItemValueString(SUBJECT_ITEM);
				if ( subject != null ) {
					value = subject;
				}
			}
            else if ( MESSAGE_ID_PROP.equals(property) ) {
                String messageId = _document.getItemValueString(MESSAGE_ID_ITEM);
                if ( messageId != null ) {
                    value = messageId;
                }
            }
            else if ( IN_REPLY_TO_PROP.equals(property) ) {
                String inReplyTo = _document.getItemValueString(IN_REPLY_TO_ITEM);
                if ( inReplyTo != null ) {
                    value = inReplyTo;
                }
            }
            else if ( RECEIPT_TO_PROP.equals(property) ) {
                Session session = _document.getParentDatabase().getParent();
                String receiptTo = _document.getItemValueString(DISPOSITION_NOTIFICATION_TO_ITEM);
                String inetReceiptTo = null;
                if ( StringUtil.isEmpty(receiptTo) ) {
                    receiptTo = _document.getItemValueString(FROM_ITEM);
                    inetReceiptTo = _document.getItemValueString(INET_PREFIX + FROM_ITEM);
                }

                if ( StringUtil.isNotEmpty(receiptTo)) {
                    String displayName = null;
                    String distinguishedName = null;
                    String emailAddress = null;
                    Name name = null;
                    
                    try {
                        // This try block is here to force a recycle of the name object 
                        // before it goes out of scope
                        
                        name = session.createName(receiptTo);
    
                        if ( name.isHierarchical() ) {
                            displayName = name.getCommon();
                            distinguishedName = name.getAbbreviated();
                            if ( StringUtil.isNotEmpty(inetReceiptTo) ) {
                                emailAddress = inetReceiptTo;
                            }
                        }
                        else {
                            displayName = trimCommonName(name.getCommon());
                            emailAddress = name.getAddr821();
                        }
                    }
                    finally {
                        BackendUtil.safeRecycle(name);
                    }
                    
                    Person person = new Person(displayName, distinguishedName, emailAddress);
                    value = new JsonPersonAdapter(person);
                }
            }
			else if ( DATE_PROP.equals(property) ) {
				DateTime dt = null;
				
				if ( _document.hasItem(POSTEDDATE_ITEM)) {
    				Vector values = _document.getItemValueDateTimeArray(POSTEDDATE_ITEM);
    				if ( values != null && values.size() > 0 ) {
    					if ( values.get(0) instanceof DateTime ) {
    						dt = (DateTime)values.get(0);
    					}
    				}
				}
				
				if ( dt == null ) {
				    dt = _document.getLastModified();
				}
				
				Date date = dt.toJavaDate();
				value = ISO8601.format(date);
			}
			else if ( HREF_PROP.equals(property) ) {
				value = _url;
			}
			else if ( TO_PROP.equals(property) ) {
			    List<JsonPersonAdapter> listValue = getAddressList(SENDTO_ITEM);
			    if ( listValue.size() > 0 ) {
			        value = listValue;
			    }
			    else if ( !_document.hasItem(POSTEDDATE_ITEM) ) {
			        value = getAddressList(ENTERSENDTO_ITEM);
			    }
            }
            else if ( CC_PROP.equals(property) ) {
                List<JsonPersonAdapter> listValue = getAddressList(COPYTO_ITEM);
                if ( listValue.size() > 0 ) {
                    value = listValue;
                }
                else if ( !_document.hasItem(POSTEDDATE_ITEM) ) {
                    value = getAddressList(ENTERCOPYTO_ITEM);
                }
            }
            else if ( BCC_PROP.equals(property) ) {
                List<JsonPersonAdapter> listValue = getAddressList(BLINDCOPYTO_ITEM);
                if ( listValue.size() > 0 ) {
                    value = listValue;
                }
                else if ( !_document.hasItem(POSTEDDATE_ITEM) ) {
                    value = getAddressList(ENTERBLINDCOPYTO_ITEM);
                }
            }
			else if ( CONTENT_PROP.equals(property) ) {
				List<JsonMimeEntityAdapter> adapters = new ArrayList<JsonMimeEntityAdapter>();
				MimeEntityHelper helper = new MimeEntityHelper(_document, BODY_ITEM);
				MIMEEntity entity = helper.getFirstMimeEntity();
				if ( entity != null ) {
					JsonMimeEntityAdapter.addEntityAdapter(adapters, entity);
				}
				value = adapters;
			} else if ( THREADID_PROP.equals(property) ) {
				Vector values = _document.getItemValue(THREADID_ITEM);
				if ( values != null && values.size() > 0 ) {
					value = values.get(0).toString();
				}
			}
		}
		catch (NotesException e) {
            Logger.get().warnp(this, "getJsonProperty",//$NON-NLS-1$
                    e, "Unhandled exception getting a JSON property value");// $NLW-JsonMessageAdapter_UnhandledExceptionInGetJsonProperty-1$
			// default to null
		}

		return value;
	}

	public void putJsonProperty(String property, Object value) {
		// This method is called when converting JSON to a message
	    
	    // PLEASE READ if you are adding a new JSON property here.
	    // When you write a document item here, you should also remove 
	    // it in removeOldItems().  That's important when updating
	    // an existing document from the JSON input.
		
		try {
			if ( TO_PROP.equals(property) ) {
				putAddressList(SENDTO_ITEM, value);
			}
			else if ( CC_PROP.equals(property) ) {
				putAddressList(COPYTO_ITEM, value);
			}
            else if ( BCC_PROP.equals(property) ) {
                putAddressList(BLINDCOPYTO_ITEM, value);
            }
			else if ( SUBJECT_PROP.equals(property) ) {
				_document.replaceItemValue(SUBJECT_ITEM, value);
			}
            else if ( IN_REPLY_TO_PROP.equals(property) ) {
                _document.replaceItemValue(IN_REPLY_TO_ITEM, value);
                
                // SPR #DDEY9L6MX5: Change the Form item to indicate it's a reply
                _document.replaceItemValue(FORM_ITEM, REPLY_FORM);
            }
            else if ( RECEIPT_TO_PROP.equals(property) ) {
                if ( value instanceof JsonPersonAdapter ) {
                    JsonPersonAdapter adapter = (JsonPersonAdapter)value;
                    String email = (String)adapter.getJsonProperty(EMAIL_PROP);
                    String distinguishedName = (String)adapter.getJsonProperty(DISTINGUISHED_NAME_PROP);
                    String itemValue = null;
                    if ( StringUtil.isNotEmpty(distinguishedName) ) {
                        itemValue = distinguishedName;
                    }
                    else if ( StringUtil.isNotEmpty(email) ) {
                        itemValue = email;
                    }
                    
                    if ( itemValue != null ) {
                        _document.replaceItemValue(DISPOSITION_NOTIFICATION_TO_ITEM, itemValue);
                        _document.replaceItemValue(RETURN_RECEIPT_ITEM, "1");
                    }
                }
            }
			else if ( CONTENT_PROP.equals(property) ) {
				
				// Flush the last MIME entity to the document
				
				JsonMimeEntityAdapter entityAdapter = _context.getCurrentEntityAdapter();
				if ( entityAdapter != null ) {
					entityAdapter.flushJsonProperties(true);
				}
			}
		}
		catch (NotesException e) {
			// Ignore !?!
		}
		catch (JsonException e) {
			// Ignore !?!
		}
	}

    private static SimpleDateFormat getUtcFormatter() {
        TimeZone tz = TimeZone.getTimeZone("UTC"); // $NON-NLS-1$
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
        formatter.setTimeZone(tz);
        return formatter;
    }
    
    /**
     * Creates a list of JsonPersonAdapters from a list of Notes addresses
     * 
     * <p>This method may get data from items other than <code>itemName</code>.  For example, 
     * if the item name is SendTo, this method reads the INetSendTo item for corresponding 
     * internet addresses.
     * 
     * @param itemName
     * @return
     * @throws NotesException
     */
    private List<JsonPersonAdapter> getAddressList(String itemName) throws NotesException {
    	List<JsonPersonAdapter> list = new ArrayList<JsonPersonAdapter>();
    	Vector<Object> items = _document.getItemValue(itemName);
    	Vector<Object> inetItems = _document.getItemValue(INET_PREFIX + itemName);
    	Session session = _document.getParentDatabase().getParent();
    	
    	Iterator<Object> inetIterator = null;
    	if ( inetItems != null ) {
    	    inetIterator = inetItems.iterator();
    	}
    	
    	if ( items != null ) {
    		Iterator<Object> iterator = items.iterator();
    		while ( iterator.hasNext() ) {
    			Object value = iterator.next();
    			
    			Object inetValue = null;
    			if ( inetIterator != null && inetIterator.hasNext() ) {
    			    inetValue = inetIterator.next();
    			}
    			
    			if ( value instanceof String ) {
    			    String displayName = null;
    			    String distinguishedName = null;
    			    String emailAddress = null;
                    Name name = null;
                    
                    try {
                        // This try block is here to force a recycle of the name object 
                        // before it goes out of scope
                        
                        name = session.createName((String)value);
    
                        if ( name.isHierarchical() ) {
                            displayName = name.getCommon();
                            distinguishedName = name.getAbbreviated();
                            if ( inetValue instanceof String ) {
                                emailAddress = (String)inetValue;
                            }
                            else {
                                emailAddress = name.getAddr821();
                            }
        			    }
        			    else {
        			        displayName = trimCommonName(name.getCommon());
        			        emailAddress = name.getAddr821();
        			    }
                    }
                    finally {
                        BackendUtil.safeRecycle(name);
                    }
    			    
    			    Person person = new Person(displayName, distinguishedName, emailAddress);
    				list.add(new JsonPersonAdapter(person));
    			}
    		}
    	}
    	
    	return list;
    }
    
    private void putAddressList(String itemName, Object list) throws NotesException {
    	Vector<Object> values = new Vector<Object>();
    	
    	if ( list instanceof List ) {
    		Iterator<Object> iterator = ((List)list).iterator();
    		while (iterator.hasNext()) {
    		    Object value = iterator.next();
    		    if ( value instanceof JsonPersonAdapter ) {
    		        JsonPersonAdapter adapter = (JsonPersonAdapter)value;
    		        String email = (String)adapter.getJsonProperty(EMAIL_PROP);
    		        String distinguishedName = (String)adapter.getJsonProperty(DISTINGUISHED_NAME_PROP);
                    if ( StringUtil.isNotEmpty(distinguishedName) ) {
                        values.add(distinguishedName);
                    }
                    else if ( StringUtil.isNotEmpty(email) ) {
    		            values.add(email);
    		        }
    		    }
    		}
    		_document.replaceItemValue(itemName, values);
    	}
    }
    
    /**
     * Removes leading and trailing quotes and/or whitespace.
     * 
     * @param name
     * @return
     */
    private String trimCommonName(String name) {
        name = name.replaceAll("(^\")|(\"$)", ""); // $NON-NLS-1$
        name = name.trim();
        return name;
    }
    
    /**
     * Removes old items from the document.
     */
    private void removeOldItems() {
        try {
            _document.removeItem(SENDTO_ITEM);
            _document.removeItem(ENTER_PREFIX + SENDTO_ITEM);
            _document.removeItem(INET_PREFIX + SENDTO_ITEM);
            
            _document.removeItem(COPYTO_ITEM);
            _document.removeItem(ENTER_PREFIX + COPYTO_ITEM);
            _document.removeItem(INET_PREFIX + COPYTO_ITEM);
            
            _document.removeItem(BLINDCOPYTO_ITEM);
            _document.removeItem(ENTER_PREFIX + BLINDCOPYTO_ITEM);
            _document.removeItem(INET_PREFIX + BLINDCOPYTO_ITEM);
            
            _document.removeItem(SUBJECT_ITEM);
            _document.removeItem(IN_REPLY_TO_ITEM);
            _document.removeItem(DISPOSITION_NOTIFICATION_TO_ITEM);
            _document.removeItem(RETURN_RECEIPT_ITEM);
            _document.removeItem(BODY_ITEM);
        }
        catch (NotesException e) {
            // Ignore
        }
    }
}
