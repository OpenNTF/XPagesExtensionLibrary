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

import static com.ibm.domino.commons.json.JsonConstants.JSON_TZID;

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DAYLIGHT;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_STANDARD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzId;

import com.ibm.commons.util.io.json.JsonObject;

/**
 * Adapts an iCal4j VTimeZone object to a JsonObject.
 *
 */
public class JsonTimeZoneAdapter implements JsonObject {

    private String _properties[];
    
    private VTimeZone _tz;

    public JsonTimeZoneAdapter(VTimeZone tz) {
        _tz = tz;
    }
    
    public VTimeZone getTimeZone() {
        return _tz;
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
                
                // The list of properties depends on the time zone
                
                if ( _tz.getTimeZoneId() != null ) {
                    properties.add(JSON_TZID);
                }

                //X-properties
                PropertyList propertyList = _tz.getProperties();
                
                Iterator propertyIterator = propertyList.iterator();
                while( propertyIterator.hasNext() ) {
                    Property tmpProperty = (Property)propertyIterator.next();
                    if(tmpProperty.getName().startsWith("X-")){ // $NON-NLS-1$
                        properties.add(tmpProperty.getName().toLowerCase());   
                    }
                } 
                
                ComponentList components = _tz.getObservances();
                if ( components != null ) {
                    Iterator<Component> iterator = components.iterator();
                    while (iterator.hasNext()) {
                        Component component = iterator.next();
                        if ( component instanceof Standard ) {
                            properties.add(JSON_STANDARD);
                        }
                        else if ( component instanceof Daylight ) {
                            properties.add(JSON_DAYLIGHT);
                        }
                    }
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
        if ( JSON_TZID.equals(property)) {
            return _tz.getTimeZoneId().getValue();
        }
        else if ( JSON_STANDARD.equals(property) ) {
            ComponentList components = _tz.getObservances();
            if ( components != null ) {
                Iterator<Component> iterator = components.iterator();
                while (iterator.hasNext()) {
                    Component component = iterator.next();
                    if ( component instanceof Standard ) {
                        return new JsonObservanceAdapter((Observance)component);
                    }
                }
            }
        }
        else if ( JSON_DAYLIGHT.equals(property) ) {
            ComponentList components = _tz.getObservances();
            if ( components != null ) {
                Iterator<Component> iterator = components.iterator();
                while (iterator.hasNext()) {
                    Component component = iterator.next();
                    if ( component instanceof Daylight ) {
                        return new JsonObservanceAdapter((Observance)component);
                    }
                }
            }
        }
        
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        if ( JSON_TZID.equals(propertyName) ) {
            Property property = new TzId((String)propertyValue);
            _tz.getProperties().add(property);
        }
        // X-properties
        else if ( propertyName.toUpperCase().startsWith("X-")) { // $NON-NLS-1$
            if ( propertyValue instanceof JsonXPropertyAdapter ) {
                JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)propertyValue;
                _tz.getProperties().add(adapter.compose(propertyName));
            }
            else if ( propertyValue instanceof List<?> ) {
                Iterator<?> iterator = ((List<?>)propertyValue).iterator();
                while (iterator.hasNext()) {
                    Object item = iterator.next();
                    if ( item instanceof JsonXPropertyAdapter) {
                        JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)item;
                        _tz.getProperties().add(adapter.compose(propertyName));
                    }
                }                    
            }
        }
    }

}