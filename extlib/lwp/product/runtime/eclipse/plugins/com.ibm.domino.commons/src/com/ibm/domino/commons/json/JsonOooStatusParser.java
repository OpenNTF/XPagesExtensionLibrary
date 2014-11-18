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

import static com.ibm.domino.commons.json.JsonConstants.ISO8601_UTC;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_APPEND_RETURN;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_ENABLED;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_END;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_MESSAGE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_SEND_EXTERNAL;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_START;
import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_SUBJECT;

import java.io.Reader;
import java.text.ParseException;
import java.util.Date;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.commons.model.OooStatus;

/**
 * Parses Out of Office status in JSON format.
 */
public class JsonOooStatusParser {
    
    private static long ONE_YEAR = 365L * 24 * 60 * 60 * 1000;

    private Reader _reader;

    public JsonOooStatusParser(Reader reader) {
        _reader = reader;
    }
    
    public OooStatus fromJson() throws JsonException, ParseException {
        
        Date start = null;
        Date end = null;
        String subject = null;
        String message = null;
        boolean sendExternal = true;
        boolean appendReturnToSubject = false;

        JsonJavaObject obj = (JsonJavaObject)JsonParser.fromJson(JsonJavaFactory.instanceEx, _reader);
        boolean enabled = obj.getBoolean(JSON_OOO_ENABLED);
        
        if ( enabled ) {
            String value = null;
            
            value = obj.getString(JSON_OOO_START);
            if ( value == null ) {
                start = new Date();
            }
            else {
                start = ISO8601_UTC.parse(value);
            }
            
            value = obj.getString(JSON_OOO_END);
            if ( value == null ) {
                end = new Date(start.getTime() + ONE_YEAR);
            }
            else {
                end = ISO8601_UTC.parse(value);
            }
            
            subject = obj.getString(JSON_OOO_SUBJECT);
            message = obj.getString(JSON_OOO_MESSAGE);
            
            Object bool = obj.get(JSON_OOO_SEND_EXTERNAL);
            if ( bool instanceof Boolean ) {
                sendExternal = (Boolean)bool;
            }

            bool = obj.get(JSON_OOO_APPEND_RETURN);
            if ( bool instanceof Boolean ) {
                appendReturnToSubject = (Boolean)bool;
            }
        }
        
        return new OooStatus(enabled, start, end, subject, message, 
                        sendExternal, appendReturnToSubject);
    }
}
