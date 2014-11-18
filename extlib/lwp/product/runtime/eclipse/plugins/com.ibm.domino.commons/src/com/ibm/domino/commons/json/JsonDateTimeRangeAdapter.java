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

import static com.ibm.domino.commons.json.JsonConstants.JSON_START;
import static com.ibm.domino.commons.json.JsonConstants.JSON_END;

import java.text.MessageFormat;
import java.util.Iterator;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RDate;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;

/**
 * Adapts an iCal4j Period object to a JsonObject.
 */
public class JsonDateTimeRangeAdapter implements JsonObject {
    
    private static String s_properties[] = {JSON_START, JSON_END};
    
    private Period _dtRange;

    private RDate _rdate;
    private DateTime _parsedDtStart;
    private DateTime _parsedDtEnd;
    
    /**
     * Constructor for converting from iCalendar to JSON.
     * 
     * @param dtRange
     */
    public JsonDateTimeRangeAdapter(Period dtRange) {
        _dtRange = dtRange;
    }
    
    /**
     * Constructor for converting from JSON to iCalendar.
     * 
     * @param rdate
     */
    public JsonDateTimeRangeAdapter(RDate rdate) {
        _rdate = rdate;
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
            }};
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#getJsonProperty(java.lang.String)
     */
    public Object getJsonProperty(String property) {
        
        // This method is called when parsing iCalendar and converting to JSON
        
        if ( JSON_START.equals(property) ) {
          Date startDate = _dtRange.getStart();
          DtStart ds = new DtStart();
          ds.setDate(startDate);
          JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(ds);
          return adapter;
        }
        else if ( JSON_END.equals(property) ) {
          Date endDate = _dtRange.getEnd();
          DtEnd de = new DtEnd();
          de.setDate(endDate);
          JsonDatePropertyAdapter adapter = new JsonDatePropertyAdapter(de);
          return adapter;
        }
        else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.util.io.json.JsonObject#putJsonProperty(java.lang.String, java.lang.Object)
     */
    public void putJsonProperty(String propertyName, Object value) {
        if ( JSON_START.equals(propertyName) ) {
            if ( value instanceof JsonDatePropertyAdapter ) {
                JsonDatePropertyAdapter adapter = (JsonDatePropertyAdapter)value;
                DateProperty dtStart =  adapter.compose(null);
                _parsedDtStart = new DateTime(dtStart.getDate());
            }
            else{
                throw new JsonIllegalValueException(
                        MessageFormat.format("Parameter {0} invalid format: {1}.",propertyName, value.toString())); // $NLX-JsonDateTimeRangeAdapter.Parameter0invalidformat1-1$
            }
        }
        else if ( JSON_END.equals(propertyName) ) {
            if ( value instanceof JsonDatePropertyAdapter ) {
                JsonDatePropertyAdapter adapter = (JsonDatePropertyAdapter)value;
                DateProperty dtEnd =  adapter.compose(null);
                _parsedDtEnd = new DateTime(dtEnd.getDate());
            }
            else{
                throw new JsonIllegalValueException(
                        MessageFormat.format("Parameter {0} invalid format: {1}.",propertyName, value.toString()));  // $NLX-JsonDateTimeRangeAdapter.Parameter0invalidformat1.1-1$
            }
        }
        else{
            throw new JsonIllegalValueException(
                    MessageFormat.format("Unsupported parameter: {0}.", propertyName));                // $NLX-JsonDateTimeRangeAdapter.Unsupportparameter0-1$
        }

        // When we have both the start and end dates, add a new period to the list
        
        if ( _parsedDtStart != null && _parsedDtEnd != null ) {
            Period period = new Period(_parsedDtStart, _parsedDtEnd);
            _rdate.getPeriods().add(period);
            _parsedDtStart = null;
            _parsedDtEnd = null;
        }
    }

}