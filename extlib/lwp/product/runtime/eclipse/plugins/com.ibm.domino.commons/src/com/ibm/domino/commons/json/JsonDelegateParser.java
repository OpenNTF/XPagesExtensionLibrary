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

import static com.ibm.domino.commons.json.JsonConstants.JSON_DELEGATE_ACCESS;

import java.io.Reader;

import lotus.domino.Document;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.commons.model.Delegate;

/**
 * Parses the JSON representation of a delegate
 */
public class JsonDelegateParser {
    
    Reader _reader;
    JsonDelegateAdapter _adapter;

    private class JsonObjectFactory extends JsonJavaFactory {
        public Object createObject(Object parent, String propertyName) throws JsonException {
            if ( parent == null && propertyName == null ) {
                _adapter =  new JsonDelegateAdapter();
                return _adapter;
            }
            else if ( parent instanceof JsonDelegateAdapter ) {
                if ( JSON_DELEGATE_ACCESS.equals(propertyName) ) {
                    return new JsonDelegateAccessAdapter();
                }
            }

            return super.createObject(parent, propertyName);
        }
    }

    public JsonDelegateParser(Reader reader) {
        _reader = reader;
    }
    
    public Delegate fromJson() throws JsonException {
        Delegate delegate = null;
        
        JsonFactory factory = new JsonObjectFactory();
        JsonParser.fromJson(factory, _reader);
        
        if ( _adapter != null ) {
            delegate = _adapter.compose();
        }
        
        return delegate;
    }
}
