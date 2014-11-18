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

package com.ibm.domino.services.sample.json;

import static com.ibm.domino.services.sample.json.JsonConstants.CITY_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.EMAIL_ADDRESS_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.FIRST_NAME_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.HREF_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.LAST_NAME_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.STATE_PROP;

import java.util.Iterator;
import java.util.Vector;

import lotus.domino.NotesException;
import lotus.domino.ViewEntry;

import com.ibm.commons.util.io.json.JsonObject;

public class JsonViewEntryAdapter implements JsonObject {
	
	private static String s_properties[] = {FIRST_NAME_PROP, LAST_NAME_PROP, EMAIL_ADDRESS_PROP, CITY_PROP, STATE_PROP, HREF_PROP};
	
	private ViewEntry _entry = null;
	private Vector _values = null;
	private String _baseUrl = null;
	
	public JsonViewEntryAdapter(ViewEntry entry, String baseUrl) {
		_entry = entry;
		_baseUrl = baseUrl;
		
		try {
			_values = entry.getColumnValues();
		}
		catch (NotesException e) {
			// Shouldn't happen
		}
	}

	public Iterator<String> getJsonProperties() {
		return new Iterator<String>() {
			
			private int _index = 0;
			
			public boolean hasNext() {
				return _index < s_properties.length ;
			}

			public String next() {
				return s_properties[_index++];
			}

			public void remove() {
				// The JSON IO classes shouldn't call remove
			}
		};
	}

	public Object getJsonProperty(String property) {
		if ( _values == null ) {
			return null;
		}
		
		if ( FIRST_NAME_PROP.equals(property) ) {
			Object value = _values.get(1);
			if ( value instanceof String ) {
				return (String)value;
			}
		}
		else if ( LAST_NAME_PROP.equals(property) ) {
			Object value = _values.get(2);
			if ( value instanceof String ) {
				return (String)value;
			}
		}
		else if ( EMAIL_ADDRESS_PROP.equals(property) ) {
			Object value = _values.get(3);
			if ( value instanceof String ) {
				return (String)value;
			}		
		}
		else if ( CITY_PROP.equals(property) ) {
			Object value = _values.get(4);
			if ( value instanceof String ) {
				return (String)value;
			}		
		}
		else if ( STATE_PROP.equals(property) ) {
			Object value = _values.get(5);
			if ( value instanceof String ) {
				return (String)value;
			}		
		}
		else if ( HREF_PROP.equals(property) ) {
			String unid = null;
			
			try {
				unid = _entry.getUniversalID(); 
			}
			catch (NotesException e) {
				// TODO: Something better than this.
			}
			
			return _baseUrl + "/" + unid;
		}

		return null;
	}

	public void putJsonProperty(String property, Object value) {
		// This method would be called when converting JSON to a view entry.
		// That cannot happen.
		
	}

}
