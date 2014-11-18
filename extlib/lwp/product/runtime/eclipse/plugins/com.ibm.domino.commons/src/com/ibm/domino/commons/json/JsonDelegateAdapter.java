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

import static com.ibm.domino.commons.json.JsonConstants.EMAIL_PROP;
import static com.ibm.domino.commons.json.JsonConstants.HREF_PROP;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_ACCESS;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_NAME;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_TYPE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_TYPE_DEFAULT;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_TYPE_GROUP;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_TYPE_PERSON;
import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_TYPE_UNSPECIFIED;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.model.Delegate;
import com.ibm.domino.commons.model.DelegateAccess;
import com.ibm.domino.commons.util.UriHelper;

/**
 * Adapts a Delegate object to JsonObject
 */
public class JsonDelegateAdapter implements JsonObject {

    // Fields used when generating JSON
    
    private Delegate _delegate;
    private URI _baseUri;
    private String _propertyNames[];
    
    // Fields used when parsing JSON
    
    private String _name;
    private String _email;
    private Delegate.Type _type = Delegate.Type.UNSPECIFIED;
    private DelegateAccess _access;
    
    /**
     * Use this constructor when generating the JSON representation
     * of a delegate.
     * 
     * @param delegate
     * @param baseUri
     */
    public JsonDelegateAdapter(Delegate delegate, URI baseUri) {
        _delegate = delegate;
        _baseUri = baseUri;
    }
    
    /**
     * Use this constructor when parsing the JSON representation of a delegate.
     */
    public JsonDelegateAdapter() {
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
                
                properties.add(HREF_PROP);
                if ( _delegate.getType() != Delegate.Type.DEFAULT ) {
                    properties.add(JSON_DELEGATE_NAME);
                }
                properties.add(JSON_DELEGATE_TYPE);
                properties.add(JSON_DELEGATE_ACCESS);

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
        
        if ( JSON_DELEGATE_NAME.equals(property) ) {
            value = _delegate.getName();
        }
        else if ( HREF_PROP.equals(property) ) {
            URI uri = null;
            if ( _delegate.getType() == Delegate.Type.DEFAULT ) {
                uri = UriHelper.appendPathSegment(_baseUri, Delegate.DEFAULT_NAME);
            }
            else {
                uri = UriHelper.appendPathSegment(_baseUri, _delegate.getName());
            }
            
            value = uri.toString();
        }
        else if ( JSON_DELEGATE_ACCESS.equals(property) ) {
            value = new JsonDelegateAccessAdapter(_delegate.getAccess());
        }
        else if ( JSON_DELEGATE_TYPE.equals(property) ) {
            value = JSON_DELEGATE_TYPE_UNSPECIFIED;
            if ( _delegate.getType() == Delegate.Type.DEFAULT ) {
                value = JSON_DELEGATE_TYPE_DEFAULT;
            }
            else if ( _delegate.getType() == Delegate.Type.PERSON ) {
                value = JSON_DELEGATE_TYPE_PERSON;
            }
            else if ( _delegate.getType() == Delegate.Type.GROUP ) {
                value = JSON_DELEGATE_TYPE_GROUP;
            }
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String property, Object value) {
        if ( JSON_DELEGATE_NAME.equals(property) ) {
            _name = (String)value;
        }
        else if ( EMAIL_PROP.equals(property) ) {
            _email = (String)value;
        }
        else if ( JSON_DELEGATE_TYPE.equals(property) ) {
            String type = (String)value;

            _type = Delegate.Type.UNSPECIFIED;
            if ( JSON_DELEGATE_TYPE_DEFAULT.equals(type) ) {
                _type = Delegate.Type.DEFAULT;
            }
            else if ( JSON_DELEGATE_TYPE_PERSON.equals(type) ) {
                _type = Delegate.Type.PERSON;
            }
            else if ( JSON_DELEGATE_TYPE_GROUP.equals(type) ) {
                _type = Delegate.Type.GROUP;
            }
        }
        else if ( JSON_DELEGATE_ACCESS.equals(property) ) {
            if ( value instanceof JsonDelegateAccessAdapter ) {
                _access = ((JsonDelegateAccessAdapter)value).compose();
            }
        }
    }
    
    /**
     * Compose the Delegate object from its constiuent parts.
     * 
     * @return
     */
    public Delegate compose() {
        Delegate delegate = null;
        
        if ( _type == Delegate.Type.DEFAULT || _name != null ) {
            delegate = new Delegate(_name, _type, _access);
        }
        else {
            delegate = new Delegate(_email, _access);
        }
        
        return delegate;
    }

}
