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

import static com.ibm.domino.commons.json.JsonConstants.JSON_OOO_ENABLED;
import static com.ibm.domino.commons.json.JsonConstants.JSON_QUOTA_ACTUAL;
import static com.ibm.domino.commons.json.JsonConstants.JSON_QUOTA_SIZE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_QUOTA_USED;
import static com.ibm.domino.commons.json.JsonConstants.JSON_QUOTA_WARNING;

import java.io.IOException;

import lotus.domino.Database;
import lotus.domino.NotesException;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;

/**
 * Generates database quota representation in JSON format 
 */
public class JsonDbQuotaGenerator {
    
    private Generator _generator = null;

    public JsonDbQuotaGenerator(StringBuilder sb) {
        _generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
    }

    public void toJson(Database database) throws NotesException, JsonException, IOException {
        
        JsonJavaObject obj = new JsonJavaObject();
        boolean enabled = true;
        double actualRaw = database.getSize();
        double percentUsed = database.getPercentUsed();
        long actual = (long)(actualRaw / 1024);
        long used = (long)((percentUsed * actual)/100);
        
        int quota = database.getSizeQuota();
        if ( quota == 0 ) {
            enabled = false;
        }
        
        obj.put(JSON_OOO_ENABLED, enabled);
        obj.put(JSON_QUOTA_ACTUAL, actual);
        obj.put(JSON_QUOTA_USED, used);
        
        if ( enabled ) {
            obj.put(JSON_QUOTA_SIZE, quota);
            
            long warning = database.getSizeWarning();
            obj.put(JSON_QUOTA_WARNING, warning);
        }

        _generator.toJson(obj);
    }
}
