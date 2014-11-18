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

import java.io.IOException;
import java.io.Writer;

import lotus.domino.Document;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonGenerator.WriterGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;

public class JsonMessageGenerator {
	
	private Generator _generator = null;
	
	public JsonMessageGenerator(StringBuilder sb) {
		_generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
	}

	public JsonMessageGenerator(Writer writer) {
		_generator = new WriterGenerator(JsonJavaFactory.instanceEx, writer, false);
	}
	
	public void toJson(Document document, String url) throws JsonException, IOException {
		JsonMessageAdapter messageAdapter = new JsonMessageAdapter(document, url);
	    _generator.toJson(messageAdapter);
	}
}
