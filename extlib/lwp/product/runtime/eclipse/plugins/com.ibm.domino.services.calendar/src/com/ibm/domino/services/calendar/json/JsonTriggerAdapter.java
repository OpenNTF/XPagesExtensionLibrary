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

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_VALUEDATATYPE;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RELATED;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_VALUE;
import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.model.Parameter.RELATED;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Trigger;


import com.ibm.commons.util.io.json.JsonObject;

/**
 * @author fenghan
 *
 */
public class JsonTriggerAdapter implements JsonObject {

    private String _properties[];
    private String _value;
    private Trigger _trigger;

    public JsonTriggerAdapter(Trigger property) {
        _trigger = property;
    }
    
    public Trigger getTrigger() {
        return _trigger;
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
                if(_trigger.getParameter(VALUE) != null ){
                    properties.add(JSON_VALUEDATATYPE);     
                }
                if(_trigger.getParameter(RELATED) != null ){
                    properties.add(JSON_RELATED);   
                }
                if(_trigger.getValue()!= null){
                    properties.add(JSON_VALUE);   
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
        if ( JSON_VALUEDATATYPE.equals(property)) {
            return _trigger.getParameter(VALUE).getValue();
        }      
        else if ( JSON_RELATED.equals(property)) {
            return _trigger.getParameter(RELATED).getValue();
        }    
        else if ( JSON_VALUE.equals(property)) {
            return _trigger.getValue();
        }    
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        // This method is called when converting JSON to iCalendar        
        if ( JSON_VALUEDATATYPE.equals(propertyName) ) {
            _trigger.getParameters().add(new Value((String)propertyValue));
        }
        else if ( JSON_RELATED.equals(propertyName) ) {
            _trigger.getParameters().add(new Related((String)propertyValue));
        }
        else if ( JSON_VALUE.equals(propertyName) ) {
            _trigger.setValue((String)propertyValue);
        }
    }
    

    /**
     * Compose the date property from its constituent parts.
     *      * 
     * @param propertyName
     * @return
     */
    public Trigger compose(String propertyName) {
        return this._trigger;
    }

}
