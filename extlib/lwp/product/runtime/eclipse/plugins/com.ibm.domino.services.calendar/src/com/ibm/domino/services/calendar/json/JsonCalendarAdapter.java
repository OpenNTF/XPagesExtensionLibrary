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

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EVENTS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EXCEPTDATES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_PRODUCT_ID;
import static net.fortuna.ical4j.model.Property.EXDATE;

import com.ibm.domino.commons.json.JsonDatePropertyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ProdId;

import com.ibm.commons.util.io.json.JsonObject;

public class JsonCalendarAdapter implements JsonObject {
    
    // JSON properties
    
    private static String s_properties[] = {JSON_EVENTS};

    
    private Calendar _calendar;
    private List<JsonDatePropertyAdapter> _dateList;
    
    public JsonCalendarAdapter(Calendar calendar,List<JsonDatePropertyAdapter> dateList) {
        _calendar = calendar;
        _dateList = dateList;
    }
    
    public Calendar getCalendar() {
        return _calendar;
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
        // This method is called when converting iCalendar to JSON
        
        if ( JSON_EVENTS.equals(property)) {
            List<JsonEventAdapter> adapters = new ArrayList<JsonEventAdapter>();
            Iterator<Object> iterator = _calendar.getComponents().iterator();
            while (iterator.hasNext()) {
                Object component = iterator.next();
                if ( component instanceof VEvent ) {
                    adapters.add(new JsonEventAdapter((VEvent)iterator.next(), null));
                }
            }
            
            return adapters;
        }
        
        return null;
    }

    @SuppressWarnings("unused") // $NON-NLS-1$
    public void putJsonProperty(String property, Object value) {
        // This method is called when converting JSON to iCalendar
        if ( property.toUpperCase().startsWith("X-")) { // $NON-NLS-1$
            if ( value instanceof JsonXPropertyAdapter ) {
                JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)value;
                _calendar.getProperties().add(adapter.compose(property));
            }
            else if ( value instanceof List<?> ) {
                Iterator<?> iterator = ((List<?>)value).iterator();
                while (iterator.hasNext()) {
                    Object item = iterator.next();
                    if ( item instanceof JsonXPropertyAdapter) {
                        JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)item;
                        _calendar.getProperties().add(adapter.compose(property));
                    }
                }                    
            }
        }
        // TODO: Remove this code once we make a final decision about the 
        // productId property.  For now, we do not parse productId.
        
        if ( false && JSON_PRODUCT_ID.equals(property) ) {
            if ( value instanceof String ) {
                ProdId prodId = (ProdId)_calendar.getProperty(Property.PRODID);
                if ( prodId == null ) {
                    // Create new product ID property
                    prodId = new ProdId((String)value);
                    _calendar.getProperties().add(prodId);
                }
                else {
                    // Product ID already exists, so replace it
                    prodId.setValue((String)value);
                }
            }
        }

    }
    
    /**
     * Compose all properties that have been deferred until now.
     * 
     * <p>This includes date properties that have TZIDs.  In the future, it
     * might include other properties too.
     */
    public void composeDeferredProperties()  {
        Map<String, TimeZone> timeZones = new HashMap<String, TimeZone>();
        Iterator<Component> iterator = _calendar.getComponents().iterator();
        while(iterator.hasNext()) {
            Component component = iterator.next();
            if ( component instanceof VTimeZone) {
                VTimeZone tz = (VTimeZone)component;
                timeZones.put(tz.getTimeZoneId().getValue(), new TimeZone(tz));
            }
           
        }   
        Iterator<JsonDatePropertyAdapter> iteratorAdapter = _dateList.iterator();
        while(iteratorAdapter.hasNext()){
            JsonDatePropertyAdapter adapter = iteratorAdapter.next();
            Component component = adapter.getParent();
            if ( component != null ) {
                DateProperty dp = adapter.compose(timeZones); 
                // ExDate
                if(JSON_EXCEPTDATES.equals(adapter.getPropertyName())){
                    ExDate exdate = (ExDate)component.getProperty(EXDATE);
                    if ( exdate == null ) {
                        exdate = new ExDate();
                        component.getProperties().add(exdate); 
                    }     
                    exdate.getDates().add(dp.getDate());                      
                }
                // DtStart StEnd
                else{
                    component.getProperties().add(dp);                            
                }
            }
        }                
    }
}