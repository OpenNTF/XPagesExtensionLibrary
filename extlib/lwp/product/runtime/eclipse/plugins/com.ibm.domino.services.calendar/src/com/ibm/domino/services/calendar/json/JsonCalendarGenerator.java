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
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_HREF;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_PRODUCT_ID;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SCHEDULE_METHOD;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_TIMEZONES;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static net.fortuna.ical4j.model.Parameter.TZID;
import static net.fortuna.ical4j.model.Property.DTEND;
import static net.fortuna.ical4j.model.Property.DTSTART;
import static net.fortuna.ical4j.model.Property.METHOD;
import static net.fortuna.ical4j.model.Property.PRODID;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RecurrenceId;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.commons.util.UriHelper;

/**
 * As events are parsed from iCalendar format, this class generates JSON content.
 * 
 * TODO:  Revisit this for poorly formed iCalendar.  There are some cases where poorly
 * formed iCalendar will result in malformed JSON.  For example, when generating the
 * JSON for a notice, we can leave a trailing comma after the noticeType property.
 * This doesn't seem to be an issue for well formed iCalendar, so I'm leaving it for
 * now.
 */
public class JsonCalendarGenerator implements ContentHandler {

    // These fields are initialized by the constructor
    
    private URI _baseUrl;
    private boolean _isNotice;
    
    // These fields hold the current state while parsing
    
    private int _eventCount = 0;
    private int _timeZoneCount = 0;
    
    private boolean _startComponment = false;
    private int processState = 0;   
    
    private Map<String, TimeZone> _timeZones = new HashMap<String, TimeZone>();
    private Stack<Component> _componentStack = new Stack<Component>();
    private ParameterList _parameters = null;
    private String _propertyValue = null;
    private Generator _generator = null;

    
    /**
     * Constructs a new JsonCalendarGenerator.
     * 
     * @param sb Destination for JSON content.
     * @param baseUrl Base URL for all events to be parsed.  When this is a notice, specify the 
     *                URL for the notice.
     * @param isNotice <code>true</code> when this is a notice.
     */
    public JsonCalendarGenerator(StringBuilder sb, URI baseUrl, boolean isNotice) {
        _baseUrl = baseUrl;
        _isNotice = isNotice;
        _generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
    }
        
    public void startCalendar() {
        try {
            _generator.out("{");
            _generator.nl();
            _generator.incIndent();
            
            if ( _isNotice && _baseUrl != null ) {
                _generator.indent();
                _generator.outPropertyName(JSON_HREF);
                _generator.out(":");
                _generator.outStringLiteral(_baseUrl.toString());
                _generator.out(",");
                _generator.nl();
            }
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void endCalendar() {
        try {
            if ( _eventCount > 0 || _timeZoneCount > 0 ) {
                _generator.nl();
                _generator.decIndent();
                _generator.indent();
                _generator.out("]");
                _generator.nl();
                _generator.decIndent();
            }
            
            _generator.out("}");
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void startComponent(String componentName) {
        // Create a component and push it on the stack
        Component component = ComponentFactory.getInstance().createComponent(componentName);
        _componentStack.push(component);
        
        _startComponment = true;
    }

    public void endComponent(String componentName) {
        
        try {
            // Pop the current component off the stack
            Component component = _componentStack.pop();
            
            if ( _componentStack.empty() ) {
                
                // The stack is empty.  Write out this component.
                
                if ( component instanceof VEvent ) {
                    
                    if ( _eventCount == 0 ) {
                        if ( _timeZoneCount > 0 ) {
                            // Close the time zone array
                            _generator.nl();
                            _generator.decIndent();
                            _generator.indent();
                            _generator.out("],");
                            _generator.nl();
                        }

                        // Start the event array
                        _generator.indent();
                        _generator.outPropertyName(JSON_EVENTS);
                        _generator.out(": [");
                        _generator.nl();
                        _generator.incIndent();
                        
                    }
                    else {
                        // Add a new event to the array
                        _generator.out(",");
                        _generator.nl();
                    }
                    
                    VEvent event = (VEvent)component;
                    
                    URI eventURI = null;
                    if ( !_isNotice ) {
                        eventURI = UriHelper.appendPathSegment(_baseUrl, event.getUid().getValue());
                        RecurrenceId recurrenceId = event.getRecurrenceId();
                        if ( recurrenceId != null ) {
                            eventURI = UriHelper.appendPathSegment(eventURI, recurrenceId.getValue());
                        }
                    }
                    
                    JsonEventAdapter eventAdapter = new JsonEventAdapter(event, eventURI);
                    _generator.toJson(eventAdapter);
                    _eventCount++;
                }
                else if ( component instanceof VTimeZone ) {
                    if ( _eventCount > 0 ) {
                        // If we have already started processing events,
                        // ignore this time zone.
                        return;
                    }
                    
                    if ( _timeZoneCount == 0 ) {
                        // Start the time zone array
                        _generator.indent();
                        _generator.outPropertyName(JSON_TIMEZONES);
                        _generator.out(": [");
                        _generator.nl();
                        _generator.incIndent();
                    }
                    else {
                        // Add a time zone to the array
                        _generator.out(",");
                        _generator.nl();
                    }
                    
                    VTimeZone vtz = (VTimeZone)component;
                    JsonTimeZoneAdapter tzAdapter = new JsonTimeZoneAdapter(vtz);
                    _generator.toJson(tzAdapter);
                    _timeZoneCount++;

                    // Add this time zone to the map
                    
                    String tzid = vtz.getTimeZoneId().getValue();
                    TimeZone tz = new TimeZone(vtz);
                    _timeZones.put(tzid, tz);
                }
                else  {
                    CALENDAR_SERVICE_LOGGER.getLogger().fine("Un support top level component:["+component.getClass()+"]");                     // $NON-NLS-1$
                }
            }
            else {
                
                // The stack is not empty.  Add this component to it's parent.
                Component parent = _componentStack.peek();
                if ( parent instanceof VTimeZone ) {
                    ((VTimeZone)parent).getObservances().add(component);
                }
                else if ( parent instanceof VEvent ) {
                    if ( component instanceof VAlarm ) {
                        ((VEvent)parent).getAlarms().add(component);
                    }                    
                }
                // TODO: Handle other types of nested components here
            }
        }
        catch (JsonException e) {
            throw new IllegalArgumentException(e);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void startProperty(String property) {
        // Start a new parameter list
        _parameters = new ParameterList();
    }
    
    @SuppressWarnings("unused") // $NON-NLS-1$
    public void endProperty(String propertyName) {
        try {
            // TODO: Some of this code should be moved to JsonCalendarAdapter.
            if( _componentStack.empty() && _propertyValue != null){
                // the property in the icalendar must before any component 
                // icalbody   = calprops component
                if(_startComponment){
                    CALENDAR_SERVICE_LOGGER.getLogger().fine("Un expected top level property:["+propertyName+"]");                         // $NON-NLS-1$
                    return;                    
                }
                if ( METHOD.equals(propertyName)) {
                    _generator.indent();
                    _generator.outPropertyName(JSON_SCHEDULE_METHOD);
                    _generator.out(":");
                    _generator.outStringLiteral(_propertyValue.toLowerCase());
                    _generator.out(",");
                    _generator.nl();
                    return;
                }

                if ( propertyName.startsWith("X-") && _componentStack.empty() && _propertyValue != null) { // $NON-NLS-1$
                    _generator.indent();
                    _generator.outPropertyName(propertyName.toLowerCase());
                    _generator.out(":");
                    Property xProperty = PropertyFactoryImpl.getInstance().createProperty(propertyName, _parameters, _propertyValue);
                    JsonXPropertyAdapter tzAdapter = new JsonXPropertyAdapter(xProperty);
                    try {
                        _generator.toJson(tzAdapter);
                    } catch (JsonException e) {
                        _generator.outStringLiteral(_propertyValue);
                    }
                    
                    // iCalendar must have at last one component
                    // component  = 1*(eventc / todoc / journalc / freebusyc /
                    // timezonec / iana-comp / x-comp)
                    _generator.out(",");
                    _generator.nl();
                    return;
                }
                
                // TODO: Remove this code once we make a final decision about the
                // productId property.  For now, we don't generate productId.                
                if ( false && PRODID.equals(propertyName) && _componentStack.empty() && _propertyValue != null) {
                    _generator.indent();
                    _generator.outPropertyName(JSON_PRODUCT_ID);
                    _generator.out(":");
                    _generator.outStringLiteral(_propertyValue);
                    _generator.out(",");
                    _generator.nl();
                    return;
                }                
            }
            

            TimeZone tz = null;
            Parameter tzid = _parameters.getParameter(TZID);
            if ( tzid != null && tzid.getValue() != null ) {
                tz = _timeZones.get(tzid.getValue());
            }
            
            Property property = null;
            if ( tz != null ) {
                if ( DTSTART.equals(propertyName) ) {
                    property = new DtStart(_propertyValue, tz);
                }
                else if ( DTEND.equals(propertyName) ) {
                    property = new DtEnd(_propertyValue, tz);
                }
            }
            
            if ( property == null ) {
                // Create a property from the current parameter list and property value
                property = PropertyFactoryImpl.getInstance().createProperty(propertyName, _parameters, _propertyValue);
            }
            
            // Add the property to the current component
            if ( ! _componentStack.empty() ) {
                Component component = _componentStack.peek();
                component.getProperties().add(property);
            }
        } 
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        } 
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        } 
        catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        finally {
            _parameters = null;
            _propertyValue = null;
        }
    }

    public void parameter(String paramName, String paramValue)
            throws URISyntaxException {
        
        // Add this parameter to the current parameter list
        Parameter parameter = ParameterFactoryImpl.getInstance().createParameter(paramName, paramValue);
        _parameters.add(parameter);
    }

    public void propertyValue(String value) throws URISyntaxException,
            ParseException, IOException {
        _propertyValue = value;
    }
}