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

import static com.ibm.domino.commons.json.JsonConstants.CAPACITY_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DISPLAY_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DISTINGUISHED_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.EMAIL_PROP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.model.Room;
import com.ibm.domino.commons.model.SmtpAddressBuilder;

/**
 * Adapts a Room to a JsonObject
 */
public class JsonRoomAdapter implements JsonObject {
    
    private Room _room;
    private SmtpAddressBuilder _builder;
    private String[] _propertyNames;
    
    /**
     * Constructor used when generating JSON output.
     * 
     * @param room
     */
    public JsonRoomAdapter(Room room) {
        _room = room;
    }
    
    public JsonRoomAdapter(Room room, SmtpAddressBuilder builder) {
        _room = room;
        _builder = builder;
    }

    /**
     * Constructor used when parsing JSON input.
     */
    public JsonRoomAdapter() {
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

                if ( _room.getDisplayName() != null ) {
                    properties.add(DISPLAY_NAME_PROP);
                }

                if ( _room.getDistinguishedName() != null ) {
                    properties.add(DISTINGUISHED_NAME_PROP);
                }

                properties.add(EMAIL_PROP);
                properties.add(CAPACITY_PROP);

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
        
        if ( EMAIL_PROP.equals(property) ) {
            if ( StringUtil.isEmpty(_room.getEmailAddress()) && _builder != null ) {
                value = _builder.build(_room.getDistinguishedName(), _room.getDomain());
            }
            else {
                value = _room.getEmailAddress();
            }
        }
        else if ( DISPLAY_NAME_PROP.equals(property) ) {
            value = _room.getDisplayName();
        }
        else if ( DISTINGUISHED_NAME_PROP.equals(property) ) {
            value = _room.getDistinguishedName();
        }
        else if ( CAPACITY_PROP.equals(property) ) {
            value = new Integer(_room.getCapacity());
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String property, Object value) {
        // TODO: Implement this
    }

}
