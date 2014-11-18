/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.util.json;

import java.util.Date;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.xml.util.XMIConverter;

/**
 * JSON Object extension for easier manipulation in Java.
 * 
 * @author priand
 *
 */
public class JObject extends JsonJavaObject {

    private static final long serialVersionUID = 1L;

    public Date getDate(String property) {
        String s = getString(property);
        if (s != null) {
            Date dt = XMIConverter.parseDate(s);
            return dt;
        }
        return null;
    }

    public void putDate(String property, Date value) {
        if (value != null) {
            String dt = XMIConverter.composeDate(value.getTime());
            put(property, dt);
        }
    }
}