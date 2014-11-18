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
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_CATEGORIES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_CLASS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DESCRIPTION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DURATION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EXCEPTDATES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_HREF;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ID;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_LASTMODIFIED;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_LINKS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_LOCATION;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ORGANIZER;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_PRIORITY;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RDATES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RECURRENCE_ID;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RECURRENCE_RULE;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RELATIONSHIP;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SEQUENCE;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_STATUS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SUMMARY;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_TRANSPARENCY;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_COMMENT;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;

import static net.fortuna.ical4j.model.Property.ATTENDEE;
import static net.fortuna.ical4j.model.Property.CATEGORIES;
import static net.fortuna.ical4j.model.Property.DURATION;
import static net.fortuna.ical4j.model.Property.EXDATE;
import static net.fortuna.ical4j.model.Property.LAST_MODIFIED;
import static net.fortuna.ical4j.model.Property.PRIORITY;
import static net.fortuna.ical4j.model.Property.RDATE;
import static net.fortuna.ical4j.model.Property.RRULE;
import static net.fortuna.ical4j.model.Property.COMMENT;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.services.calendar.util.Utils;

import com.ibm.domino.commons.json.JsonDatePropertyAdapter;
import com.ibm.domino.commons.json.JsonDateTimeRangeAdapter;

/**
 * Adapts an iCal4j VEvent object to a JsonObject.
 */
public class JsonEventAdapter implements JsonObject {
    
    private VEvent _event;
    private URI _url;
    private String[] _properties;
    
    public JsonEventAdapter(VEvent event, URI url) {
        _event = event;
        _url = url;
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
                
                // The list of properties depends on the event
                
                if ( _url != null ) {
                    properties.add(JSON_HREF);
                }
            
                if ( _event.getUid() != null ) {
                    properties.add(JSON_ID);
                }
                
                if ( _event.getSummary() != null ) {
                    properties.add(JSON_SUMMARY);
                }
                
                if ( _event.getLocation() != null ) {
                    properties.add(JSON_LOCATION);
                }
                
                if ( _event.getDescription() != null ) {
                    properties.add(JSON_DESCRIPTION);
                }
                
                if ( _event.getStartDate() != null ) {
                    properties.add(JSON_START);
                }
                
                if ( _event.getEndDate() != null ) {
                    properties.add(JSON_END);
                }
                
                if ( _event.getProperty(RRULE) != null ) {
                    properties.add(JSON_RECURRENCE_RULE);
                }

                if ( _event.getProperty(EXDATE) != null ) {
                    properties.add(JSON_EXCEPTDATES);
                }

                if ( _event.getProperty(RDATE) != null ) {
                    properties.add(JSON_RDATES);
                }
                
                if ( _url != null && (_event.getProperty(RRULE) != null || _event.getProperty(RDATE) != null )) {
                    // The links property is an array of related resources.  In this case,
                    // there is a link to a resource listing the individual instances of 
                    // a recurring event. Filter this property for notice
                    properties.add(JSON_LINKS);
                }

                if ( _event.getRecurrenceId() != null ) {
                    properties.add(JSON_RECURRENCE_ID);
                }
                
                if ( _event.getClassification() != null ) {
                    properties.add(JSON_CLASS);
                }
                
                if ( _event.getTransparency() != null ) {
                    properties.add(JSON_TRANSPARENCY);
                }
                
                if ( _event.getStatus() != null ) {
                    properties.add(JSON_STATUS);
                }
                
                if ( _event.getSequence() != null ) {
                    properties.add(JSON_SEQUENCE);
                }
                
                if ( _event.getProperty(LAST_MODIFIED) != null ) {
                    properties.add(JSON_LASTMODIFIED);
                }
                if ( _event.getProperty(DURATION) != null ) {
                    properties.add(JSON_DURATION);
                }
                if ( _event.getProperty(CATEGORIES) != null ) {
                    properties.add(JSON_CATEGORIES);
                }
                if ( _event.getProperty(PRIORITY) != null ) {
                    properties.add(JSON_PRIORITY);
                }                       
                PropertyList attendees = _event.getProperties(ATTENDEE);
                if ( attendees != null && attendees.size() > 0 ) {
                    properties.add(JSON_ATTENDEES);
                }
                if ( _event.getOrganizer() != null ) {
                    properties.add(JSON_ORGANIZER);
                }
                if ( _event.getProperty(COMMENT) != null ) {
                    properties.add(JSON_COMMENT);
                }
                
                //X-properties
                PropertyList propertyList = _event.getProperties();
                
                Iterator propertyIterator = propertyList.iterator();
                while( propertyIterator.hasNext() ) {
                    Property tmpProperty = (Property)propertyIterator.next();
                    if(tmpProperty.getName().startsWith("X-")){ // $NON-NLS-1$
                        properties.add(tmpProperty.getName().toLowerCase());   
                    }
                } 

                ComponentList alarmList = _event.getAlarms();
                if ( alarmList != null && alarmList.size() > 0 ) {
                    properties.add(JSON_ALARM);
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

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#getJsonProperty(java.lang.String)
     */
    public Object getJsonProperty(String property) {
        
        // This method is called when converting iCalendar to JSON
        
        if ( JSON_SUMMARY.equals(property)) {
            return Utils.getUnescapedString(_event.getSummary());
        }
        else if ( JSON_LOCATION.equals(property) ) {
            return Utils.getUnescapedString(_event.getLocation());
        }
        else if ( JSON_ID.equals(property) ) {
            return _event.getUid().getValue();
        }
        else if ( JSON_DESCRIPTION.equals(property) ) {
            return Utils.getUnescapedString(_event.getDescription());
        }
        else if ( JSON_START.equals(property) ) {
            DateProperty dp = _event.getStartDate();
            JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(dp);
            return adapter;
        }
        else if ( JSON_END.equals(property) ) {
            DateProperty dp = _event.getEndDate();
            JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(dp);
            return adapter;
        }
        else if ( JSON_HREF.equals(property) ) {
            return _url.toString();
        }
        else if ( JSON_RECURRENCE_RULE.equals(property) ) {
            Property rruleProp = _event.getProperty(RRULE);
            return rruleProp.getValue();
        }
        else if ( JSON_EXCEPTDATES.equals(property) ) {
            List<JsonDatePropertyAdapter> adapters = new ArrayList<JsonDatePropertyAdapter>();
            Property exdateProperty = _event.getProperty(EXDATE);
            if ( exdateProperty instanceof ExDate ) {
                ExDate exdate = (ExDate)exdateProperty;
                
                Iterator<Date> iterator = exdate.getDates().iterator();
                while (iterator.hasNext()) {
                    adapters.add(new JsonDatePropertyAdapter(new DtStart(iterator.next())));
                }
            }
            
            return adapters;
        }
        else if ( JSON_RDATES.equals(property) ) {
            List<JsonDateTimeRangeAdapter> adapters = new ArrayList<JsonDateTimeRangeAdapter>();
            Property rdateProp = _event.getProperty(RDATE);
            if ( rdateProp instanceof RDate ) {
                RDate rdate = (RDate)rdateProp;
                
                Iterator<Period> iterator = rdate.getPeriods().iterator();
                while (iterator.hasNext()) {
                    adapters.add(new JsonDateTimeRangeAdapter(iterator.next()));
                }
            }
            
            return adapters;
        }
        else if ( JSON_LINKS.equals(property) ) {
            List<JsonJavaObject> links = new ArrayList<JsonJavaObject>();
            JsonJavaObject instances = new JsonJavaObject();
            instances.putJsonProperty(JSON_RELATIONSHIP, "instances"); // $NON-NLS-1$
            URI uri = UriHelper.appendPathSegment(_url, "instances"); // $NON-NLS-1$
            instances.putJsonProperty(JSON_HREF, uri.toString());
            links.add(instances);
            return links;
        }
        else if ( JSON_RECURRENCE_ID.equals(property) ) {
            return _event.getRecurrenceId().getValue();
        }
        else if ( JSON_TRANSPARENCY.equals(property) ) {
            return _event.getTransparency().getValue().toLowerCase();
        }
        else if ( JSON_CLASS.equals(property) ) {
            return _event.getClassification().getValue().toLowerCase();
        }
        else if ( JSON_STATUS.equals(property) ) {
            return _event.getStatus().getValue().toLowerCase();
        }
        else if ( JSON_SEQUENCE.equals(property) ) {
            return _event.getSequence().getSequenceNo();
        }
        else if ( JSON_ORGANIZER.equals(property) ) {
            return new JsonAttendeeAdapter(_event.getOrganizer());
        }
        else if ( JSON_ATTENDEES.equals(property) ) {
            List<JsonAttendeeAdapter> attendees = new ArrayList<JsonAttendeeAdapter>();
            
            PropertyList list = _event.getProperties(ATTENDEE);
            if ( list != null ) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext() ) {
                    attendees.add(new JsonAttendeeAdapter((Property)iterator.next()));
                }
            }
            
            return attendees;
        }
        else if ( JSON_ALARM.equals(property) ) {
            List<JsonAlarmAdapter> alarms = new ArrayList<JsonAlarmAdapter>();
            
            ComponentList list = _event.getAlarms();
            if ( list != null ) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext() ) {
                    VAlarm alarmTmp=(VAlarm)iterator.next();
                    alarms.add(new JsonAlarmAdapter(alarmTmp));
                }
            }
            return alarms;
        }
        else if ( JSON_LASTMODIFIED.equals(property)) {
            //  The property value MUST be specified in the UTC time format.
            return _event.getLastModified().getValue();
        }
        else if ( JSON_DURATION.equals(property) ) {
            return _event.getProperty(DURATION).getValue();
        }
        else if ( JSON_CATEGORIES.equals(property) ) {
            // As the core api represent Categories like CATEGORIES:Projects;Phone Calls but not CATEGORIES:Projects,Phone Calls
            // ical4j can't identify these are two values. So we must represent them as one value
            // Actually we'd better use Categories.getCategories();
            return Utils.getUnescapedString(_event.getProperty(CATEGORIES));
        }
        else if ( JSON_PRIORITY.equals(property) ) {
            return _event.getPriority().getLevel();
        }
        else if(JSON_COMMENT.equals(property)){
            return Utils.getUnescapedString(_event.getProperty(COMMENT));
        }
        else {
            // X- property
            Property tmpProperty = _event.getProperty(property.toUpperCase());
            if(tmpProperty!=null){
                return new JsonXPropertyAdapter(tmpProperty);
            }
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String propertyName, Object value) {
        
        // This method is called when converting JSON to iCalendar
        
        Property property = null;
        
        try {
            if ( JSON_SUMMARY.equals(propertyName) ) {
                property = new Summary((String)value);
            }
            else if ( JSON_LOCATION.equals(propertyName) ) {
                property = new Location((String)value);
            }
            else if ( JSON_ID.equals(propertyName) ) {
                property = new Uid((String)value);
            }           
            else if ( JSON_RECURRENCE_ID.equals(propertyName) ) {
                String recurID = (String)value;
                if(recurID.indexOf("T")>0){
                    property = new RecurrenceId(new DateTime((String)value));                   
                }
                else{
                    property = new RecurrenceId(new Date((String)value));
                }
            }
            else if ( JSON_DESCRIPTION.equals(propertyName) ) {
                property = new Description((String)value);
            }
            else if ( JSON_TRANSPARENCY.equals(propertyName) ) {
                property = new Transp(((String)value).toUpperCase());
            }
            else if ( JSON_CLASS.equals(propertyName) ) {
                property = new Clazz(((String)value).toUpperCase());
            }
            else if ( JSON_STATUS.equals(propertyName) ) {
                property = new Status(((String)value).toUpperCase());
            }
            else if ( JSON_LASTMODIFIED.equals(propertyName) ) {
                property = new LastModified(new DateTime((String)value));
            }
            else if ( JSON_SEQUENCE.equals(propertyName) ) {
                property = new Sequence(((Double)value).intValue());
            }
            else if ( JSON_RECURRENCE_RULE.equals(propertyName) ) {
                property = new RRule(null, (String)value);
            }
            else if ( JSON_ALARM.equals(propertyName)) {
                if ( value instanceof List<?> ) {
                    Iterator<?> iterator = ((List<?>)value).iterator();
                    while (iterator.hasNext()) {
                        Object item = iterator.next();
                        if ( item instanceof JsonAlarmAdapter) {
                            JsonAlarmAdapter adapter = (JsonAlarmAdapter)item;
                            _event.getAlarms().add(adapter.compose(propertyName));
                        }
                    }                    
                }
            }
            else if ( JSON_DURATION.equals(propertyName) ) {
                property = new Duration();
                ((Duration)property).setValue((String)value);
            }
            else if ( JSON_CATEGORIES.equals(propertyName) ) {
                property = new Categories(((String)value));
            }
            else if ( JSON_PRIORITY.equals(propertyName) ) {
                property = new Priority(((Double)value).intValue());
            }
            else if ( JSON_COMMENT.equals(propertyName) ) {
                property = new Comment((String)value);
            }
            else if ( propertyName.toUpperCase().startsWith("X-")) { // $NON-NLS-1$
                if ( value instanceof JsonXPropertyAdapter ) {
                    JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)value;
                    _event.getProperties().add(adapter.compose(propertyName));
                }
                else if ( value instanceof List<?> ) {
                    Iterator<?> iterator = ((List<?>)value).iterator();
                    while (iterator.hasNext()) {
                        Object item = iterator.next();
                        if ( item instanceof JsonXPropertyAdapter) {
                            JsonXPropertyAdapter adapter = (JsonXPropertyAdapter)item;
                            _event.getProperties().add(adapter.compose(propertyName));
                        }
                    }                    
                }
            }
            if ( property != null ) {
                _event.getProperties().add(property);
            }
        }
        catch (ParseException e) {
            CALENDAR_SERVICE_LOGGER.getLogger().fine(e.getMessage());
            throw new JsonIllegalValueException(e);
        }
    }
    
    public VEvent getEvent() {
        return _event;
    }
    
    /**
     * Get a list of event instances in JSON format.
     * 
     * <p>TODO: Move this method to another class.  It's only here now for convenience.
     * 
     * @param list
     * @param baseUrl
     * @return
     * @throws IOException
     * @throws JsonException
     */
    public static String getInstances(ArrayList<String> list, URI baseUrl) throws IOException, JsonException {
        
        StringBuilder sb = new StringBuilder();
        Generator generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
        generator.out("{");
        generator.nl();
        generator.incIndent();

        generator.indent();
        generator.outPropertyName("instances"); // $NON-NLS-1$
        generator.out(":[");
        generator.nl();
        generator.incIndent();
        
        String recurrenceId = null;
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {         
            recurrenceId = iterator.next();

            generator.indent();
            generator.out("{");
            generator.incIndent();
            generator.nl();
            
            generator.indent();
            generator.outPropertyName(JSON_RECURRENCE_ID);
            generator.out(":");
            generator.outLiteral(recurrenceId);
            generator.out(",");
            generator.nl();
            
            generator.indent();
            generator.outPropertyName(JSON_HREF);
            generator.out(":");
            URI instacneURI = UriHelper.appendPathSegment(baseUrl, recurrenceId);
            generator.outLiteral(instacneURI.toString());
            generator.nl();
            
            generator.decIndent();
            generator.indent();
            generator.out("}");
            if(iterator.hasNext()){
                generator.out(",");
            }
            generator.nl();
        }

        generator.decIndent();
        generator.indent();
        generator.out("]");
        
        generator.decIndent();
        generator.nl();
        generator.indent();
        generator.out("}");

        return sb.toString();
    }

}