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

package com.ibm.xsp.eclipse.tools.doc;

import com.ibm.commons.util.StringUtil;


/**
 * 
 */
public class Namespace {

	public static String LIB_CORE	= "http://www.ibm.com/xsp/core";
	public static String LIB_COREEX	= "http://www.ibm.com/xsp/coreex";
	public static String LIB_CUSTOM	= "http://www.ibm.com/xsp/custom";
	
    private String id;
    private String prefix;
    private String uri;
    private boolean standard;
    private String description;
    
    public Namespace(String id, String prefix, String uri) {
        this.id = id;
        this.prefix = prefix;
        this.uri = uri;
        if(StringUtil.equals(uri,LIB_CORE)) {
        	this.standard = true;
        	this.description = "XPages Default Library";
        } else if(StringUtil.equals(uri,LIB_COREEX)) {
        	this.standard = true;
        	this.description = "XPages Extension Library";
        } else if(StringUtil.equals(uri,LIB_CUSTOM)) {
        	this.standard = true;
        	this.description = "Custom Control";
        } else {
        	this.standard = true;
        	this.description = id;
        }
    }
    
    public boolean isStandard() {
    	return standard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
