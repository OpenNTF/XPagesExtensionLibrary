/*
 * © Copyright IBM Corp. 2013
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

import static com.ibm.domino.commons.json.JsonConstants.JSON_FREEBUSY_BUSYTIMES;
import static com.ibm.domino.commons.json.JsonConstants.JSON_START;
import static com.ibm.domino.commons.json.JsonConstants.JSON_END;

import static net.fortuna.ical4j.model.Property.FREEBUSY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.FreeBusy;

import com.ibm.commons.util.io.json.JsonObject;

/**
 * @author fenghan
 *
 */
public class JsonFreeBusyAdapter implements JsonObject {

    private String _properties[];
    
    private VFreeBusy _freebusy;

    public JsonFreeBusyAdapter(VFreeBusy component) {
        _freebusy = component;
    }
    
    public VFreeBusy getFreeBusy() {
        return _freebusy;
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
                           
                if( _freebusy.getStartDate() != null){
                    properties.add(JSON_START);                    
                }
                if( _freebusy.getEndDate() != null){
                    properties.add(JSON_END);                    
                }
                PropertyList freebusys =  _freebusy.getProperties(FREEBUSY);
                if ( freebusys != null && freebusys.size() > 0 ) {
                    properties.add(JSON_FREEBUSY_BUSYTIMES);
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
        if ( JSON_START.equals(property) ) {
            DateProperty dp = _freebusy.getStartDate();
            JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(dp);
            return adapter;
        }
        else if ( JSON_END.equals(property) ) {
            DateProperty dp = _freebusy.getEndDate();
            JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(dp);
            return adapter;
        }
        else if ( JSON_FREEBUSY_BUSYTIMES.equals(property) ) {
            List<JsonDateTimeRangeAdapter> busyTimes = new ArrayList<JsonDateTimeRangeAdapter>();
            
            PropertyList list = _freebusy.getProperties(FREEBUSY);
            if ( list != null ) {
                Iterator iterator = list.iterator();
                // cross freebusy times
                while (iterator.hasNext() ) {
                    FreeBusy freebusytime = (FreeBusy)iterator.next();
                    PeriodList periods = freebusytime.getPeriods();
                    Iterator iteratorTimes = periods.iterator();
                    // cross values
                    while (iteratorTimes.hasNext() ) {
                        Period period = (Period)iteratorTimes.next();
                        busyTimes.add(new JsonDateTimeRangeAdapter(period));
                    }
                }
            }
            
            return busyTimes;
        }
        
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        // This method is called when converting JSON to iCalendar
        
        }
    }
