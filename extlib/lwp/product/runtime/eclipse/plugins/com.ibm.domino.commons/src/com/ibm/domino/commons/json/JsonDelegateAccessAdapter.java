/*
 * © Copyright IBM Corp. 2013
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

import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_CREATE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_DELETE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_EDIT;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_READ;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_WHAT;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_WHAT_CALENDAR;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_WHAT_MAIL;
import static com.ibm.domino.commons.json.JsonConstants.JSON_ACCESS_WHAT_NOTHING;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.model.DelegateAccess;

/**
 * Adapts a DelegateAccess object to a JsonObject
 */
public class JsonDelegateAccessAdapter implements JsonObject {
    
    // Fields for generating JSON
    
    private DelegateAccess _access;
    private String _propertyNames[];
    
    // Fields for parsing JSON
    
    private DelegateAccess.What _what = DelegateAccess.What.NOTHING;
    private boolean _read;
    private boolean _create;
    private boolean _delete;
    private boolean _edit;
    
    /**
     * Use this constructor when generating JSON
     * 
     * @param access
     */
    public JsonDelegateAccessAdapter(DelegateAccess access) {
        _access = access;
    }
    
    /**
     * Use this constructor when parsing
     */
    public JsonDelegateAccessAdapter() {
        
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#getJsonProperties()
     */
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

                properties.add(JSON_ACCESS_WHAT);
                properties.add(JSON_ACCESS_READ);
                properties.add(JSON_ACCESS_CREATE);
                properties.add(JSON_ACCESS_EDIT);
                properties.add(JSON_ACCESS_DELETE);

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

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#getJsonProperty(java.lang.String)
     */
    public Object getJsonProperty(String property) {
        Object value = null;
        
        if ( JSON_ACCESS_WHAT.equals(property) ) {
            value = JSON_ACCESS_WHAT_NOTHING;
            if ( _access.getWhat() == DelegateAccess.What.CALENDAR ) {
                value = JSON_ACCESS_WHAT_CALENDAR;
            }
            else if ( _access.getWhat() == DelegateAccess.What.MAIL ) {
                value = JSON_ACCESS_WHAT_MAIL;
            }
        }
        else if ( JSON_ACCESS_READ.equals(property) ) {
            value = _access.isRead();
        }
        else if ( JSON_ACCESS_CREATE.equals(property) ) {
            value = _access.isCreate();
        }
        else if ( JSON_ACCESS_EDIT.equals(property) ) {
            value = _access.isEdit();
        }
        else if ( JSON_ACCESS_DELETE.equals(property) ) {
            value = _access.isDelete();
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String property, Object value) {
        if ( JSON_ACCESS_WHAT.equals(property) ) {
            _what = DelegateAccess.What.NOTHING;
            if ( JSON_ACCESS_WHAT_CALENDAR.equals(value) ) {
                _what = DelegateAccess.What.CALENDAR;
            }
            else if ( JSON_ACCESS_WHAT_MAIL.equals(value) ) {
                _what = DelegateAccess.What.MAIL;
            }
        }
        else if ( JSON_ACCESS_READ.equals(property) ) {
            _read = (Boolean)value;
        }
        else if ( JSON_ACCESS_CREATE.equals(property) ) {
            _create = (Boolean)value;
        }
        else if ( JSON_ACCESS_EDIT.equals(property) ) {
            _edit = (Boolean)value;
        }
        else if ( JSON_ACCESS_DELETE.equals(property) ) {
            _delete = (Boolean)value;
        }
    }
    
    /**
     * Compose the DelegateAccess object from its constiuent parts.
     * 
     * @return
     */
    public DelegateAccess compose() {
        return new DelegateAccess(_what, _read, _create, _delete, _edit);
    }

}
