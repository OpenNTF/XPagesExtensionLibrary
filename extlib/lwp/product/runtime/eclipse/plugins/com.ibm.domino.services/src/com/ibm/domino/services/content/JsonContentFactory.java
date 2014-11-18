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

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;

import com.ibm.domino.services.rest.das.document.RestDocumentService;
import com.ibm.domino.services.rest.das.view.RestViewService;

public abstract class JsonContentFactory {
	
	public JsonViewEntryCollectionContent createViewEntryCollectionContent(
			View view, RestViewService service) {
		return new JsonViewEntryCollectionContent(view, service);
	}

	public JsonDatabaseCollectionContent createDatabaseCollectionContent(
			Session session, String baseUri, String resourcePath) {
		return new JsonDatabaseCollectionContent(session, baseUri, resourcePath);
	}

	public JsonDocumentContent createDocumentContent(Document document, RestDocumentService service) {
		return new JsonDocumentContent(document, service);
	}

	public JsonDocumentCollectionContent createDocumentCollectionContent(Database database, String uri, String search, String since, int max) {
		return new JsonDocumentCollectionContent(database, uri, search, since, max);
	}

	public JsonViewCollectionContent createViewCollectionContent(
			Database database, String uri) {
		return new JsonViewCollectionContent(database, uri);
	}

	public JsonViewDesignContent createViewDesignContent(View view) {
		return new JsonViewDesignContent(view);
	}
}
