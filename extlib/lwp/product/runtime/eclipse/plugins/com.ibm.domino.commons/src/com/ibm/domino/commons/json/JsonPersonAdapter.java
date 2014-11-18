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

import static com.ibm.domino.commons.json.JsonConstants.DISPLAY_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DISTINGUISHED_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.EMAIL_PROP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.model.Person;

/**
 * Adaptes a Person to a JsonObject
 */
public class JsonPersonAdapter implements JsonObject {
    
    private Person _person;
    private String[] _propertyNames;
    
    /**
     * Constructor used when generating JSON output.
     * 
     * @param person
     */
    public JsonPersonAdapter(Person person) {
        _person = person;
    }
    
    /**
     * Constructor used when parsing JSON input.
     */
    public JsonPersonAdapter() {
        _person = new Person();
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

                if ( StringUtil.isNotEmpty(_person.getDisplayName()) ) {
                    properties.add(DISPLAY_NAME_PROP);
                }

                if ( StringUtil.isNotEmpty(_person.getDistinguishedName()) ) {
                    properties.add(DISTINGUISHED_NAME_PROP);
                }

                if ( StringUtil.isNotEmpty(_person.getEmailAddress()) ) {
                    properties.add(EMAIL_PROP);
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

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#getJsonProperty(java.lang.String)
     */
    public Object getJsonProperty(String property) {
        Object value = null;
        
        if ( EMAIL_PROP.equals(property) ) {
            value = _person.getEmailAddress();
        }
        else if ( DISPLAY_NAME_PROP.equals(property) ) {
            value = _person.getDisplayName();
        }
        else if ( DISTINGUISHED_NAME_PROP.equals(property) ) {
            value = _person.getDistinguishedName();
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String property, Object value) {
        if ( EMAIL_PROP.equals(property) ) {
            _person.setEmailAddress((String)value);
        }
        else if ( DISPLAY_NAME_PROP.equals(property) ) {
            _person.setDisplayName((String)value);
        }
        else if ( DISTINGUISHED_NAME_PROP.equals(property) ) {
            _person.setDistinguishedName((String)value);
        }
    }

}
