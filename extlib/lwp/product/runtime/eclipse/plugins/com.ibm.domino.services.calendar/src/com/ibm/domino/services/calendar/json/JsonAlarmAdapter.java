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

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ACTION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ATTACH;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ATTENDEES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DESCRIPTION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DURATION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_REPEAT;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SUMMARY;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_TRIGGER;
import static net.fortuna.ical4j.model.Property.ATTENDEE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Summary;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;
import com.ibm.domino.services.calendar.util.Utils;

/**
 * Adapts an iCal4j VAlarm object to a JsonObject.
 *
 */
public class JsonAlarmAdapter implements JsonObject {

    private String _properties[];
    
    private VAlarm _alarm;

    public JsonAlarmAdapter(VAlarm component) {
        _alarm = component;
    }
    
    public VAlarm getAlarm() {
        return _alarm;
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
                                
                if ( _alarm.getAction() != null ) {
                    properties.add(JSON_ACTION);
                }
                if ( _alarm.getTrigger() != null ) {
                    properties.add(JSON_TRIGGER);
                }
                if ( _alarm.getDescription() != null ) {
                    properties.add(JSON_DESCRIPTION);
                }
                if ( _alarm.getRepeat() != null ) {
                    properties.add(JSON_REPEAT);
                }
                if ( _alarm.getDuration() != null ) {
                    properties.add(JSON_DURATION);
                }
                if ( _alarm.getSummary() != null ) {
                    properties.add(JSON_SUMMARY);
                }
                if ( _alarm.getAttachment() != null ) {
                    properties.add(JSON_ATTACH);
                }
                
                //X-properties
                PropertyList propertyList = _alarm.getProperties();
                
                Iterator propertyIterator = propertyList.iterator();
                while( propertyIterator.hasNext() ) {
                    Property tmpProperty = (Property)propertyIterator.next();
                    if(tmpProperty.getName().startsWith("X-")){ // $NON-NLS-1$
                        properties.add(tmpProperty.getName().toLowerCase());   
                    }
                } 
                
                PropertyList attendees = _alarm.getProperties(ATTENDEE);
                if ( attendees != null && attendees.size() > 0 ) {
                    properties.add(JSON_ATTENDEES);
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
        if ( JSON_ACTION.equals(property)) {
            return _alarm.getAction().getValue();
        }
        else if ( JSON_TRIGGER.equals(property)) {
            return new JsonTriggerAdapter(_alarm.getTrigger());
        }
        else if ( JSON_DESCRIPTION.equals(property)) {
            return Utils.getUnescapedString(_alarm.getDescription());
        }
        else if ( JSON_SUMMARY.equals(property) ) {
            return Utils.getUnescapedString(_alarm.getSummary());
        }
        else if ( JSON_REPEAT.equals(property) ) {
            return _alarm.getRepeat().getValue();
        }
        else if ( JSON_DURATION.equals(property) ) {
            return _alarm.getDuration().getValue();
        }
        else if ( JSON_ATTACH.equals(property)) {
            // attach may need a adapter
            return _alarm.getAttachment().getValue();
        }
        else if ( JSON_ATTENDEES.equals(property) ) {
            List<JsonAttendeeAdapter> attendees = new ArrayList<JsonAttendeeAdapter>();
            
            PropertyList list = _alarm.getProperties(ATTENDEE);
            if ( list != null ) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext() ) {
                    attendees.add(new JsonAttendeeAdapter((Property)iterator.next()));
                }
            }
            
            return attendees;
        }
        
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        // This method is called when converting JSON to iCalendar
        
        Property property = null;
        
        if ( JSON_ACTION.equals(propertyName) ) {
            property = new Action((String)propertyValue);
        }
        else if ( JSON_TRIGGER.equals(propertyName) ) {
            if ( propertyValue instanceof JsonTriggerAdapter ) {
                JsonTriggerAdapter adapter = (JsonTriggerAdapter)propertyValue;
                property = adapter.compose(propertyName);
            }
        }
        else if ( JSON_DESCRIPTION.equals(propertyName) ) {
            property = new Description((String)propertyValue);
        }
        else if ( JSON_REPEAT.equals(propertyName) ) {
            property = new Repeat(Integer.parseInt((String)propertyValue));
        }
        else if ( JSON_DURATION.equals(propertyName) ) {
            property = new Duration();
            ((Duration)property).setValue((String)propertyValue);
        }
        else if ( JSON_SUMMARY.equals(property) ) {
            property = new Summary((String)propertyValue);
        }
        else if ( JSON_ATTACH.equals(propertyName) ) {
            try {
                property = new Attach(new URI((String)propertyValue));
            } catch (URISyntaxException e) {
                throw new JsonIllegalValueException(e);
            }
        }
        // X-properties
        else if ( propertyName.toUpperCase().startsWith("X-")) { // $NON-NLS-1$
            property = null;
            if ( propertyValue instanceof JsonXPropertyAdapter ) {
                JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)propertyValue;
                _alarm.getProperties().add(adapter.compose(propertyName));
            }
            else if ( propertyValue instanceof List<?> ) {
                Iterator<?> iterator = ((List<?>)propertyValue).iterator();
                while (iterator.hasNext()) {
                    Object item = iterator.next();
                    if ( item instanceof JsonXPropertyAdapter) {
                        JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)item;
                        _alarm.getProperties().add(adapter.compose(propertyName));
                    }
                }                    
            }
        }        
        
        if ( property != null ) {
            _alarm.getProperties().add(property);
        }
    }
    

    /**
     * Compose the date property from its constituent parts.
     *      * 
     * @param propertyName
     * @return
     */
    public VAlarm compose(String propertyName) {
        return this._alarm;
    }

}