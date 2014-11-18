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
import static com.ibm.domino.commons.json.JsonConstants.JSON_END;

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ALARM;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ATTENDEES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DAYLIGHT;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EVENTS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EXCEPTDATES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ORGANIZER;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RDATES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_STANDARD;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_TIMEZONES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_TRIGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;

import static net.fortuna.ical4j.model.Property.RDATE;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Version;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.services.calendar.service.CalendarService;

import com.ibm.domino.commons.json.JsonDatePropertyAdapter;
import com.ibm.domino.commons.json.JsonDateTimeRangeAdapter;

public class JsonCalendarParser {

    private JsonFactory _factory = new JsonObjectFactory();
    
    private class JsonObjectFactory extends JsonJavaFactory {
        
        private List<JsonDatePropertyAdapter> _dateList = new ArrayList<JsonDatePropertyAdapter>();
        
        public Object createObject(Object parent, String propertyName) throws JsonException {
            if ( parent == null && propertyName == null ) {
                Calendar calendar = new Calendar();
                calendar.getProperties().add(new ProdId(CalendarService.ICALENDAR_PRODID)); // $NON-NLS-1$
                calendar.getProperties().add(new Version("2.0", null));
                return new JsonCalendarAdapter(calendar,_dateList);
            }
            
            if ( parent instanceof JsonCalendarAdapter ) {
                Calendar calendar = ((JsonCalendarAdapter)parent).getCalendar();
                if ( JSON_EVENTS.equals(propertyName) ) {                    
                    VEvent event = new VEvent();
                    calendar.getComponents().add(event);
                    return new JsonEventAdapter(event, null);
                }
                else if ( JSON_TIMEZONES.equals(propertyName) ) {
                    VTimeZone tz = new VTimeZone();
                    calendar.getComponents().add(tz);
                    return new JsonTimeZoneAdapter(tz);
                }
            }
            
            if ( parent instanceof JsonEventAdapter ) {

                VEvent event = ((JsonEventAdapter)parent).getEvent();
                
                if ( JSON_START.equals(propertyName) ) {
                   JsonDatePropertyAdapter dsa =  new JsonDatePropertyAdapter(propertyName, event);
                   _dateList.add(dsa);
                    return dsa;
                }
                
                if( JSON_END.equals(propertyName) ) {
                    JsonDatePropertyAdapter dea = new JsonDatePropertyAdapter(propertyName, event);
                    _dateList.add(dea);
                    return dea;
                }
                
                if ( JSON_RDATES.equals(propertyName) ) {
                    RDate rdate = (RDate)event.getProperty(RDATE);
                    if ( rdate == null ) {
                        rdate = new RDate(new PeriodList());
                        event.getProperties().add(rdate);
                    }
                    
                    return new JsonDateTimeRangeAdapter(rdate);
                }

                if ( JSON_EXCEPTDATES.equals(propertyName) ) {
                    JsonDatePropertyAdapter dsa =  new JsonDatePropertyAdapter(propertyName, event);
                    _dateList.add(dsa);
                    return dsa;
                }
                
                if ( JSON_ATTENDEES.equals(propertyName) ) {
                    Attendee attendee = new Attendee();
                    event.getProperties().add(attendee);
                    return new JsonAttendeeAdapter(attendee);
                }
                
                if ( JSON_ORGANIZER.equals(propertyName) ) {
                    Organizer organizer = new Organizer();
                    event.getProperties().add(organizer);
                    return new JsonAttendeeAdapter(organizer);
                }
                if ( JSON_ALARM.equals(propertyName) ) {
                    VAlarm alarm = new VAlarm();   
                    return new JsonAlarmAdapter(alarm);
                }
            }
            
            if ( parent instanceof JsonTimeZoneAdapter ) {
                Observance observance = null;
                if ( JSON_STANDARD.equals(propertyName) ) {
                    observance = new Standard();
                }
                else if ( JSON_DAYLIGHT.equals(propertyName) ) {
                    observance = new Daylight();
                }
                
                if ( observance != null ) {
                    VTimeZone tz = ((JsonTimeZoneAdapter)parent).getTimeZone();
                    tz.getObservances().add(observance);
                    return new JsonObservanceAdapter(observance);
                }
            }      
            if ( parent instanceof JsonObservanceAdapter ) {
                if ( JSON_START.equals(propertyName) ) {
                    return new JsonDatePropertyAdapter(propertyName, null);
                }
            }

            if ( parent instanceof JsonDateTimeRangeAdapter ) {
                // RDate don't support TimeZone, don't need add to _dateList
                if ( JSON_START.equals(propertyName) ) {
                   JsonDatePropertyAdapter sa =  new JsonDatePropertyAdapter(propertyName, null);
                   return sa;
                }
                
                if( JSON_END.equals(propertyName) ) {
                    JsonDatePropertyAdapter ea =  new JsonDatePropertyAdapter(propertyName, null);
                    return ea;
                }
            }
            if ( parent instanceof JsonAlarmAdapter ) {
                VAlarm valarm = ((JsonAlarmAdapter)parent).getAlarm();
                if ( JSON_ATTENDEES.equals(propertyName) ) {
                    Attendee attendee = new Attendee();
                    valarm.getProperties().add(attendee);
                    return new JsonAttendeeAdapter(attendee);
                }     
                if ( JSON_TRIGGER.equals(propertyName) ) {
                    Trigger trigger = new Trigger();
                    return new JsonTriggerAdapter(trigger);
                }       
            }
            // X-property
            if(propertyName.toUpperCase().startsWith("X-")){ // $NON-NLS-1$
                Property property = PropertyFactoryImpl.getInstance().createProperty(propertyName.toUpperCase());
                return new JsonXPropertyAdapter(property);                
            }
//            // X-parameter
//            if( JSON_PARAMETERS.equals(propertyName)){
//                return new JsonParameterListAdapter(new ParameterList());
//            }
            CALENDAR_SERVICE_LOGGER.getLogger().fine("Unexpect property:[ "+ propertyName+"]"); // $NON-NLS-1$
            return super.createObject(parent, propertyName);
        }
        
    };

    /**
     * Parses a JSON string to an iCal4j Calendar object.
     * 
     * @param jsonInput
     * @return
     * @throws JsonException
     */
    public Calendar parse(Reader jsonInput) throws JsonException {
        
        Calendar calendar = null;
        Object object = JsonParser.fromJson(_factory, jsonInput);

        if ( object instanceof JsonCalendarAdapter ) {
            JsonCalendarAdapter json = (JsonCalendarAdapter)object;
            json.composeDeferredProperties();
            calendar = json.getCalendar();
        }
        
        return calendar;
    }

  }