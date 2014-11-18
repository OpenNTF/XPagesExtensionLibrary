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

package com.ibm.domino.services.rest.das.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.RestDocumentNavigator;

public class RestViewJsonServiceExt extends RestViewJsonService {
	protected List<IRestViewJsonEventListener> _listeners;

	public RestViewJsonServiceExt(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			ViewParameters parameters) {
		super(httpRequest, httpResponse, parameters);
	}

	public void addEventListener(IRestViewJsonEventListener listener) {
		if (_listeners == null) {
			_listeners = new ArrayList<IRestViewJsonEventListener>();
		}
		_listeners.add(listener);
	}

	public void removeEventListener(IRestViewJsonEventListener listener) {
		if (_listeners != null && _listeners.contains(listener)) {
			_listeners.remove(listener);
		}
	}

	@Override
	protected void createDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, JsonJavaObject items)
			throws ServiceException, JsonException, IOException {
		for (IRestViewJsonEventListener l : _listeners) {
			boolean more = l.queryCreateDocument(viewNav, docNav, items);
			if (!more) { // anything but true means cancel the create...
				return;
			}
		}
		super.createDocument(viewNav, docNav, items);
		for (IRestViewJsonEventListener l : _listeners) {
			l.postCreateDocument(viewNav, docNav, items);
		}
	}

	@Override
	protected void updateDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id,
			JsonJavaObject items) throws ServiceException, JsonException, IOException {
		for (IRestViewJsonEventListener l : _listeners) {
			boolean more = l.queryUpdateDocument(viewNav, docNav, id, items);
			if (!more) { // anything but true means cancel the create...
				return;
			}
		}
		super.createDocument(viewNav, docNav, items);
		for (IRestViewJsonEventListener l : _listeners) {
			l.postUpdateDocument(viewNav, docNav, id, items);
		}
	}

	@Override
	protected void deleteDocument(RestViewNavigator viewNav, RestDocumentNavigator docNav, String id,
			JsonJavaObject items) throws ServiceException, JsonException, IOException {
		for (IRestViewJsonEventListener l : _listeners) {
			boolean more = l.queryDeleteDocument(viewNav, docNav, id, items);
			if (!more) { // anything but true means cancel the create...
				return;
			}
		}
		super.createDocument(viewNav, docNav, items);
		for (IRestViewJsonEventListener l : _listeners) {
			l.postDeleteDocument(viewNav, docNav, id, items);
		}
	}

}
