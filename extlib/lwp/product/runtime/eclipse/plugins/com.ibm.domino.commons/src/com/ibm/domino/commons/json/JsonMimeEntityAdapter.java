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

import static com.ibm.domino.commons.json.JsonConstants.BOUNDARY_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CONTENT_DISPOSITION_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CONTENT_ID_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CONTENT_TRANSFER_ENCODING_PROP;
import static com.ibm.domino.commons.json.JsonConstants.CONTENT_TYPE_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DATA_PROP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.Stream;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.commons.util.io.json.parser.ParseException;
import com.ibm.domino.commons.internal.Logger;

/**
 * Adapts a MIMEEntity to a JsonObject.
 */
public class JsonMimeEntityAdapter implements JsonObject {
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type"; //$NON-NLS-1$
	private static final String CONTENT_ID_HEADER = "Content-ID"; //$NON-NLS-1$
	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition"; //$NON-NLS-1$
	private static final String CONTENT_TRANSFER_ENCODING_HEADER = "Content-Transfer-Encoding"; //$NON-NLS-1$
	private static final String BOUNDARY_EQUALS = "boundary="; //$NON-NLS-1$
	private static final String MULTIPART = "multipart"; //$NON-NLS-1$
	
	private static final String ENCODING_BASE64 = "base64";	//$NON-NLS-1$	
	private static final String ENCODING_7BIT = "7bit";	//$NON-NLS-1$	
	private static final String ENCODING_8BIT = "8bit";	//$NON-NLS-1$
	private static final String ENCODING_BINARY = "binary";	//$NON-NLS-1$
	private static final String ENCODING_QUOTED_PRINTABLE = "quoted-printable";	//$NON-NLS-1$

	MIMEEntity _entity = null;
	private String[] _propertyNames;
	private ParserContext _context;
	private JsonObject _objectCache;
	
    /**
     * Inner class for holding the parser context.
     * 
     * <p>A parser context is constructed once and shared by all MIME entities in an array.
     */
    public static class ParserContext {
    	
    	private MIMEEntity _rootEntity;
    	private Document _document;
    	private String _itemName;
		private JsonMimeEntityAdapter _currentEntityAdapter;
    	private Map<String, MIMEEntity> _entityMap = new HashMap<String, MIMEEntity>();
    	
		public ParserContext(Document document, String itemName) {
    		_document = document;
    		_itemName = itemName;
    	}

		public JsonMimeEntityAdapter getCurrentEntityAdapter() {
			return _currentEntityAdapter;
		}

		public void setCurrentEntityAdapter(JsonMimeEntityAdapter currentEntityAdapter) {
			_currentEntityAdapter = currentEntityAdapter;
		}

    	public Document getDocument() {
			return _document;
		}

    	public String getItemName() {
			return _itemName;
		}

		public MIMEEntity getRootEntity() {
			return _rootEntity;
		}

		public void setRootEntity(MIMEEntity rootEntity) {
			_rootEntity = rootEntity;
		}

		public void addEntity(String key, MIMEEntity entity) {
			_entityMap.put(key, entity);
		}
		
		public MIMEEntity getEntity(String key) {
			return _entityMap.get(key);
		}
    }
    
	/**
	 * Constructor used when generating JSON output.
	 * 
	 * @param entity
	 */
	public JsonMimeEntityAdapter(MIMEEntity entity) {
		_entity = entity;
	}
	
	/**
	 * Constructor used when parsing JSON input.
	 * 
	 * @param context
	 */
	public JsonMimeEntityAdapter(ParserContext context, JsonJavaObject jsonObject) {
		_context = context;
		_objectCache = jsonObject;
	}
	
	/**
	 * Recursively adds entity adapters to a flat list (depth first)
	 * 
	 * @param adapters
	 * @param entity
	 * @throws NotesException
	 */
	public static void addEntityAdapter(List<JsonMimeEntityAdapter> adapters, MIMEEntity entity) throws NotesException {
		
		// Add this entity
		
		JsonMimeEntityAdapter adapter = new JsonMimeEntityAdapter(entity);
		adapters.add(adapter);
		
		// Add children
		
		MIMEEntity child = entity.getFirstChildEntity();
		if ( child != null ) {
			addEntityAdapter(adapters, child);

			// Add siblings
			
			MIMEEntity sibling = child.getNextSibling();
			while ( sibling != null ) {
				addEntityAdapter(adapters, sibling);
				sibling = sibling.getNextSibling();
			}
		}
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

			private String[] getProperties() {
				if ( _propertyNames != null ) {
					return _propertyNames;
				}

				List<String> properties = new ArrayList<String>();
				
				try {
					// Base64 encode the data (if necessary).  We do this first
					// because it can change the headers
					
			        int encoding = _entity.getEncoding();
			        if (encoding == MIMEEntity.ENC_IDENTITY_BINARY) {
			            _entity.encodeContent(MIMEEntity.ENC_BASE64);
			        }           
		            
			        // Assume there is always a content type header
			        
			        properties.add(CONTENT_TYPE_PROP);

					// The remaining properties depend on the entity
					
		            MIMEHeader header = null;
		            header = _entity.getNthHeader(CONTENT_ID_HEADER);
		            if (header != null) {
		            	properties.add(CONTENT_ID_PROP);
		            }

		            header = _entity.getNthHeader(CONTENT_DISPOSITION_HEADER);
		            if (header != null) {
		            	properties.add(CONTENT_DISPOSITION_PROP);
		            }
		            
		            header = _entity.getNthHeader(CONTENT_TRANSFER_ENCODING_HEADER);
		            if (header != null) {
		            	properties.add(CONTENT_TRANSFER_ENCODING_PROP);
		            }
		            
		            // TODO: Review this.  The content can be quite large.
		            // Assigning it to a string here could result in multiple
		            // copies on the heap.
		            
		            String content = _entity.getContentAsText();
		            if (StringUtil.isNotEmpty(content)) { 
		            	properties.add(DATA_PROP);
		            }
		            
		            String boundaryStart = _entity.getBoundaryStart();
		            if (StringUtil.isNotEmpty(boundaryStart)) {
		            	properties.add(BOUNDARY_PROP);
		            }
				}
	            catch (NotesException e) {
	            	Logger.get().warnp(this, "getProperties",//$NON-NLS-1$
	            	        e, "Unhandled exception getting the list of JSON property names");// $NLW-JsonMimeEntityAdapter_UnhandledExceptionInGetProperties-1$
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

	public Object getJsonProperty(String property) {
		
		Object value = null;

		try {
			MIMEHeader header = null;
			
			if ( CONTENT_TYPE_PROP.equals(property) ) {
				header = _entity.getNthHeader(CONTENT_TYPE_HEADER);
			}
			else if ( CONTENT_ID_PROP.equals(property) ) {
				header = _entity.getNthHeader(CONTENT_ID_HEADER);
			}
			else if ( CONTENT_DISPOSITION_PROP.equals(property) ) {
				header = _entity.getNthHeader(CONTENT_DISPOSITION_HEADER);
			}
			else if ( CONTENT_TRANSFER_ENCODING_PROP.equals(property) ) {
				header = _entity.getNthHeader(CONTENT_TRANSFER_ENCODING_HEADER);
			}
			
			if ( header != null ) {
				value = header.getHeaderValAndParams().trim();
			}
			else if ( DATA_PROP.equals(property) ) {
		        String content = _entity.getContentAsText();
				header = _entity.getNthHeader(CONTENT_TYPE_HEADER);
				if ( header != null && header.getHeaderVal().toLowerCase().contains(MULTIPART) ) {
					value = content.trim();
				} 
		        else {
		        	value = content;
		        }
			}
			else if ( BOUNDARY_PROP.equals(property) ) {
	            String boundaryStart = _entity.getBoundaryStart();
	            value = boundaryStart.trim();
			}
			
			if ( value == null ) {
				value = "null"; //$NON-NLS-1$
			}
		}
		catch (NotesException e) {
            Logger.get().warnp(this, "getJsonProperty",//$NON-NLS-1$
                    e, "Unhandled exception getting a JSON property value");// $NLW-JsonMimeEntityAdapter_UnhandledExceptionInGetJsonProperty-1$
		}
		
		return value;
	}

	public void putJsonProperty(String property, Object value) {
		
		// Delegate to the cache
		
		_objectCache.putJsonProperty(property, value);
	}

	/**
	 * When parsing the JSON representation of a MIME entity, we temporarily cache the JSON properties
	 * in memory.  Call this method to flush the entire entity to the document.
	 * 
	 * @param lastEntity
	 */
	public void flushJsonProperties(boolean lastEntity) throws JsonException {
		
		Document document = _context.getDocument();
		String itemName = _context.getItemName();
		MIMEEntity entity = null;
		
		try {
			
			// Create the MIMEEntity
			
			if ( _context.getRootEntity() == null ) {
		        if (document.hasItem(itemName)) {
		            document.removeItem(itemName);
		        }
		        
				entity = document.createMIMEEntity(itemName);
				_context.setRootEntity(entity);
			}
			else {
				// Whose child are we?
				
				MIMEEntity parentEntity = null;
				String boundary = (String)_objectCache.getJsonProperty(BOUNDARY_PROP);
				if ( !StringUtil.isEmpty(boundary) ) {
					parentEntity = _context.getEntity(boundary);
				}
				
				if ( parentEntity == null ) {
					/* entity = _context.getRootEntity().createChildEntity(); */
					throw new JsonException(new ParseException(boundary));
				}
				else {
					entity = parentEntity.createChildEntity();
				}
			}
			
			// Add this entity to the map
			
			String contentType = (String)_objectCache.getJsonProperty(CONTENT_TYPE_PROP);
			String key = getKeyFromContentType(contentType);
			if ( key != null ) {
				_context.addEntity(key, entity);
			}
			
			// Write the headers
			
			addHeader(CONTENT_TYPE_PROP, CONTENT_TYPE_HEADER, entity);
			addHeader(CONTENT_DISPOSITION_PROP, CONTENT_DISPOSITION_HEADER, entity);
			addHeader(CONTENT_ID_PROP, CONTENT_ID_HEADER, entity);
			addHeader(CONTENT_TRANSFER_ENCODING_PROP, CONTENT_TRANSFER_ENCODING_HEADER, entity);
			
			// Write the data
			
			String data = (String)_objectCache.getJsonProperty(DATA_PROP);
            if (!StringUtil.isEmpty(data)) {
                int encoding = MIMEEntity.ENC_NONE;
                String contentEncoding = (String)_objectCache.getJsonProperty(CONTENT_TRANSFER_ENCODING_PROP);
                if (!StringUtil.isEmpty(contentEncoding)) {
                    if(contentEncoding.contains(ENCODING_BASE64)) 
                        encoding = MIMEEntity.ENC_BASE64;
                    else if(contentEncoding.contains(ENCODING_7BIT)) 
                        encoding = MIMEEntity.ENC_IDENTITY_7BIT;
                    else if(contentEncoding.contains(ENCODING_8BIT)) 
                        encoding = MIMEEntity.ENC_IDENTITY_8BIT;
                    else if(contentEncoding.contains(ENCODING_BINARY)) 
                        encoding = MIMEEntity.ENC_IDENTITY_BINARY;
                    else if(contentEncoding.contains(ENCODING_QUOTED_PRINTABLE)) 
                        encoding = MIMEEntity.ENC_QUOTED_PRINTABLE;
                }
                Session session = document.getParentDatabase().getParent();
                Stream stream = session.createStream();
                stream.writeText(data);   
                entity.setContentFromText(stream, contentType, encoding); 
                stream.close();
                stream.recycle();
            }
			
			
			// Dereference the cached object so it can be GC'd
			
			_objectCache = null;
			
			// Clean up for the last entity in the array
			
			if ( lastEntity ) {
		        document.closeMIMEEntities(true, itemName);
	            _context.getRootEntity().recycle();
			}
		}
		catch (NotesException e) {
			throw new JsonException(e);
		}
	}
	
	private String getKeyFromContentType(String contentType) {
		String key = null;
		
		if (contentType != null && contentType.toLowerCase().contains(MULTIPART) && contentType.toLowerCase().contains(BOUNDARY_EQUALS) ) {
			key = contentType.substring(contentType.toLowerCase().indexOf(BOUNDARY_EQUALS) + BOUNDARY_EQUALS.length());
		}
		
		if ( key != null ) {
	        if (key.startsWith("\"")) { //$NON-NLS-1$
	            key = key.substring(1);
	        }
	        if (key.endsWith("\"")) { //$NON-NLS-1$
	            key = key.substring(0, key.length()-1);
	        }
	        if (key.length() > 0) {
	            key = "--" + key; //$NON-NLS-1$
	        }
		}

		return key;
	}
	
	private void addHeader(String property, String header, MIMEEntity entity) throws NotesException {
		String value = (String)_objectCache.getJsonProperty(property);
        if (!StringUtil.isEmpty(value)) {
            MIMEHeader mimeHeader = entity.createHeader(header);
            mimeHeader.setHeaderVal(value);
        }
	}

}
