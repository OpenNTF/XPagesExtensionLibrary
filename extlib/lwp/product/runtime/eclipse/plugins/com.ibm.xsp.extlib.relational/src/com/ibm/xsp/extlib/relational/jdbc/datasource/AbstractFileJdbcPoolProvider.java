/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.extlib.relational.jdbc.datasource;

import org.w3c.dom.Document;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMAccessor;
import com.ibm.commons.xml.XMLException;
import com.ibm.xsp.extlib.util.StringReplacer;


public abstract class AbstractFileJdbcPoolProvider implements IFileJdbcPoolProvider{

    // For ${...} replacement
    private StringReplacer replacer = new StringReplacer();
    protected String type;
    
    public String getType() {
        return type;
    }
    
    // ================================================================
    // DOM Utilities
    
    protected String getStringValue(Document doc, String xPath, String defaultValue) throws XMLException {
        String v = DOMAccessor.getStringValue(doc, xPath);
        if(StringUtil.isNotEmpty(v)) {
            return replacer.replace(v);
        }
        return defaultValue;
    }
    protected int getIntValue(Document doc, String xPath, int defaultValue) throws XMLException {
        String v = DOMAccessor.getStringValue(doc, xPath);
        if(StringUtil.isNotEmpty(v)) {
            return Integer.parseInt(v);
        }
        return defaultValue;
    }
    protected long getLongValue(Document doc, String xPath, long defaultValue) throws XMLException {
        String v = DOMAccessor.getStringValue(doc, xPath);
        if(StringUtil.isNotEmpty(v)) {
            return Long.parseLong(v);
        }
        return defaultValue;
    }
    
    
}

