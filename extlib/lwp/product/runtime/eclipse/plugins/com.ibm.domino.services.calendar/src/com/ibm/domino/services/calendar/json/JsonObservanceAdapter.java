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

package com.ibm.domino.services.calendar.json;

import static com.ibm.domino.commons.json.JsonConstants.JSON_START;

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_OFFSET_FROM;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_OFFSET_TO;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RECURRENCE_RULE;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;

import static net.fortuna.ical4j.model.Property.RRULE;

import com.ibm.domino.commons.json.JsonDatePropertyAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;


public class JsonObservanceAdapter implements JsonObject {
	
	private Observance _observance;
	private String[] _properties;
	
	public JsonObservanceAdapter(Observance observance) {
		_observance = observance;
	}
	
	public Iterator<String> getJsonProperties() {
		return new Iterator<String>() {
			
            private int _index = 0;
            
            public boolean hasNext() {
                String properties[] = getProperties();
                return _index < properties.length ;
            }

            public String next() {
                String properties[] = getProperties();
                return properties[_index++];
            }

            public void remove() {
                // The JSON IO classes shouldn't call remove
            }          

            private String[] getProperties() {
                if ( _properties != null ) {
                    return _properties;
                }
                
                List<String> properties = new ArrayList<String>();
                
                // The list of properties depends on the observance object
                
                properties.add(JSON_START);
                properties.add(JSON_OFFSET_FROM);
                properties.add(JSON_OFFSET_TO);
                
                if ( _observance.getProperty(RRULE) != null ) {
                    properties.add(JSON_RECURRENCE_RULE);
                }

                // Convert to array
                
                String[] array = new String[properties.size()];
                Iterator<String> iterator = properties.iterator();
                for ( int i = 0; iterator.hasNext(); i++ ) {
                    array[i] = iterator.next();
                }
                
                // Cache the array for next time
                _properties = array;
                
                return array;
            }
		};
	}

	public Object getJsonProperty(String property) {
		if ( JSON_START.equals(property)) {
			return new JsonDatePropertyAdapter(_observance.getStartDate());
		}
		else if ( JSON_OFFSET_FROM.equals(property) ) {
			return _observance.getOffsetFrom().getValue();
		}
		else if ( JSON_OFFSET_TO.equals(property) ) {
			return _observance.getOffsetTo().getValue();
		}
		else if ( JSON_RECURRENCE_RULE.equals(property) ) {
			Property rrule = _observance.getProperty(RRULE);
			if ( rrule != null ) {
				return rrule.getValue();
			}
		}
		
		return null;
	}

	public void putJsonProperty(String propertyName, Object propertyValue) {
		Property property = null;
		
		try {
			if ( JSON_START.equals(propertyName) ) {
				if ( propertyValue instanceof JsonDatePropertyAdapter ) {
					JsonDatePropertyAdapter adapter = (JsonDatePropertyAdapter)propertyValue;
					property = adapter.compose(null);
				}
			}
			else if ( JSON_OFFSET_FROM.equals(propertyName) ) {
				property = new TzOffsetFrom((String)propertyValue);
			}
			else if ( JSON_OFFSET_TO.equals(propertyName) ) {
				property = new TzOffsetTo(null, (String)propertyValue);
			}
			else if ( JSON_RECURRENCE_RULE.equals(propertyName) ) {
			    if ( propertyValue != null ) {
			        property = new RRule(null, (String)propertyValue);
			    }
			}
		}
		catch(ParseException e) {
			CALENDAR_SERVICE_LOGGER.getLogger().fine(e.getMessage());
            throw new JsonIllegalValueException(e);
        } 
		
		if ( property != null ) {
			_observance.getProperties().add(property);
		}
	}

}
