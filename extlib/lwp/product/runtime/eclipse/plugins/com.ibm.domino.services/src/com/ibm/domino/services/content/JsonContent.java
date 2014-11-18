/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services.content;

import java.io.IOException;

import com.ibm.domino.services.util.JsonWriter;

public class JsonContent {

    /**
     * Writes a JSON string property.
     * 
     * @param jwriter
     * @param propName
     * @param propValue
     * @throws IOException
     */	
	protected void writeProperty(JsonWriter jwriter, String propName, String propValue) throws IOException {
		jwriter.startProperty(propName);
		jwriter.outStringLiteral(propValue);
		jwriter.endProperty();
	}
	
    /**
     * Writes a JSON integer property.
     * 
     * @param jwriter
     * @param propName
     * @param propValue
     * @throws IOException
     */	
	protected void writeProperty(JsonWriter jwriter, String propName, int propValue) throws IOException {
		jwriter.startProperty(propName);
		jwriter.outIntLiteral(propValue);
		jwriter.endProperty();
	}
	
    /**
     * Writes a JSON boolean property.
     * 
     * @param jwriter
     * @param propName
     * @param propValue
     * @throws IOException
     */	
	protected void writeProperty(JsonWriter jwriter, String propName, boolean propValue) throws IOException {
		jwriter.startProperty(propName);
		jwriter.outBooleanLiteral(propValue);
		jwriter.endProperty();
	}

	/**
     * Writes a JSON property from a Domino object (DateTime, Vector, etc).
     * 
     * @param jwriter
     * @param propName
     * @param propValue
     * @throws IOException
     */
    protected void writeDominoProperty(JsonWriter jwriter, String propName, Object propValue) throws IOException {
    	if ( propValue != null ) {
			jwriter.startProperty(propName);
			jwriter.outDominoValue(propValue);
			jwriter.endProperty();
    	}
	}
}
