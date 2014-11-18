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
package com.ibm.domino.commons.json;

import static com.ibm.domino.commons.json.JsonConstants.JSON_DATE;
import static com.ibm.domino.commons.json.JsonConstants.JSON_END;
import static com.ibm.domino.commons.json.JsonConstants.JSON_TIME;
import static com.ibm.domino.commons.json.JsonConstants.JSON_TZID;
import static com.ibm.domino.commons.json.JsonConstants.JSON_UTC;
import static net.fortuna.ical4j.model.Parameter.TZID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;



public class JsonDatePropertyAdapter implements JsonObject {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd"; // $NON-NLS-1$
    private static final String TIME_FORMAT = "HH:mm:ss"; // $NON-NLS-1$
    
    private static java.util.TimeZone s_utcTimeZone = TimeZone.getTimeZone("UTC"); // $NON-NLS-1$

    private DateProperty _dateProperty;
    private Component _parent = null;
    private String _propertyName;
    private String[] _properties;
    
    // These fields are only used when parsing JSON
    
    private String _dateValue;
    private String _timeValue;
    private String _tzidValue;
    private boolean _utcValue;
    
    /**
     * Constructor used when generating JSON
     * 
     * @param dateProperty
     */
    public JsonDatePropertyAdapter(DateProperty dateProperty) {
        _dateProperty = dateProperty;
    }
    
    /**
     * Constructor used when parsing JSON
     * 
     * @param propertyName
     * @param parent
     */
    public JsonDatePropertyAdapter(String propertyName, Component parent) {
        _propertyName = propertyName;
        _parent = parent;
    }
     
    /**
     * Compose the date property from its constituent parts.
     * 
     * <p>Call this method after the date, time, tzid and other properties
     * have all be set by the parser.  The <code>compose</code> method
     * assembles a date property from the parts.
     * 
     * @param propertyName
     * @return
     * @throws JsonException 
     */
    public DateProperty compose(Map<String, TimeZone> timeZones)  {
        
        DateProperty dp = null;
        SimpleDateFormat formatter = null;
        String input = null;
        java.util.TimeZone tz = null;
        
        try {
            if ( _dateValue == null ) {
                // Must at least have a date value
                throw new ParseException("Must at least have a date value",0); // $NLX-JsonDatePropertyAdapter.Mustatleasthaveadatevalue-1$
                //return null;
            }
            
            if ( _timeValue == null ) {
                // Parse just the date
                formatter = new SimpleDateFormat(DATE_FORMAT);
                input = _dateValue;
            }
            else {
                // Parse the date and time
                formatter = new SimpleDateFormat(DATE_FORMAT + "'T'" + TIME_FORMAT); // $NON-NLS-1$
                input = _dateValue + "T" + _timeValue;
                
                if (_utcValue && _tzidValue != null)
                    throw new ParseException("Bad date time property",0); // $NLX-JsonDatePropertyAdapter.Baddatetimeproperty-1$
                
                if ( _utcValue ) {
                    tz = s_utcTimeZone;
                    formatter.setTimeZone(s_utcTimeZone);
                }
                else if ( _tzidValue != null ) {
                    if ( timeZones != null ) {
                        tz = timeZones.get(_tzidValue);
                    }
                    if(tz==null){
                        throw new ParseException("The Timezone ID is not found",0); // $NLX-JsonDatePropertyAdapter.TheTimezoneIDisnotfound-1$
                    }
                }
            }
            
            // Set up the time zone prior to parsing
            
            if ( tz != null ) {
                formatter.setTimeZone(tz);
            }
            
            // Parse the inner date object
            
            java.util.Date  date = formatter.parse(input);
            Date ical4jDate = null;
            if ( _timeValue == null ) {
                ical4jDate = new Date(date); 
            }
            else {
                ical4jDate = new DateTime(date);
            }
            
            // Construct the relevant ical4j date property
            
            if ( JSON_END.equals(_propertyName) ) {
                dp = new DtEnd(ical4jDate);
            }
            else {
                // When the property name is "exceptDates" we construct a DtStart property.
                // This is a kludge, but it gets around a layering problem.  Leave it
                // for now.
                dp = new DtStart(ical4jDate);
            }
            
            // Update the ical4j time zone
            
            if ( dp != null ) {
                if ( _utcValue ) {
                    dp.setUtc(true);
                }
                else if ( tz != null ) {
                    dp.setTimeZone((TimeZone)tz);
                }
            }
        }
        catch (ParseException e) {
            throw new JsonIllegalValueException(e);
        }
        
        return dp;
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
                Date date = _dateProperty.getDate();

                // There is always a date property
                
                properties.add(JSON_DATE);
                
                // The rest of the properties depend on the date innards
                
                if ( date instanceof DateTime ) {
                    DateTime datetime = (DateTime)date;
                    
                    properties.add(JSON_TIME);
                    
                    if ( datetime.isUtc() ) {
                        properties.add(JSON_UTC);
                    }
                    else if ( datetime.getTimeZone() != null ) {
                        properties.add(JSON_TZID);
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

    public Object getJsonProperty(String propertyName) {

        // This method is called when converting iCalendar to JSON
        
        Date date = _dateProperty.getDate();
        DateTime datetime = null;
        if ( date instanceof DateTime ) {
            datetime = (DateTime)date;
        }
        
        if ( JSON_DATE.equals(propertyName) ) {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            if ( datetime != null ) {
                if ( datetime.isUtc() ) {
                    formatter.setTimeZone(s_utcTimeZone);
                }
                else if ( datetime.getTimeZone() != null ) {
                    formatter.setTimeZone(datetime.getTimeZone());
                }
            }
            return formatter.format(date);
        }
        else if ( JSON_TIME.equals(propertyName) ) {
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
            if ( datetime != null ) {
                if ( datetime.isUtc() ) {
                    formatter.setTimeZone(s_utcTimeZone);
                }
                else if ( datetime.getTimeZone() != null ) {
                    formatter.setTimeZone(datetime.getTimeZone());
                }
            }
            return formatter.format(date);
        }
        else if ( JSON_TZID.equals(propertyName) ) {
            return _dateProperty.getParameter(TZID).getValue();
        }
        else if ( JSON_UTC.equals(propertyName) ) {
            if ( datetime == null) {
                return false;
            }
            else {
                return datetime.isUtc();
            }
        }
        
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        
        if ( JSON_DATE.equals(propertyName) ) {
            _dateValue = (String)propertyValue; 
        }
        else if ( JSON_TIME.equals(propertyName) ) {
            _timeValue = (String)propertyValue; 
        }
        else if ( JSON_TZID.equals(propertyName) ) {
            _tzidValue = (String)propertyValue; 
        }
        else if ( JSON_UTC.equals(propertyName) ) {
            if ( propertyValue instanceof Boolean ) {
                _utcValue = ((Boolean)propertyValue).booleanValue(); 
            }
        }
        
    }

    /**
     * Get the parent component
     */
    public Component getParent() {
        return _parent;
    }

    public String getPropertyName() {
        return _propertyName;
    }
}