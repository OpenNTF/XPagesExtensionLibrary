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

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_PROPERTYDATA;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_PROPERTYTYPE;
import static net.fortuna.ical4j.model.Parameter.VALUE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Value;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;
import com.ibm.domino.services.calendar.util.Utils;

/**
 * @author fenghan
 * x-prop = x-name *(";" icalparameter) ":" value CRLF
 *
 */
public class JsonXPropertyAdapter  implements JsonObject {

    private String _properties[];

    private Property _property;
    private Object _data = null;       // for put
    private Parameter _valueType = null;   // for put
    private ParameterList _parameters = null;   // for put
    
    public JsonXPropertyAdapter(Property property) {
        _property = property;
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
                // data
                properties.add(JSON_PROPERTYDATA);
                // type
                if( getValueParameter()!=null){
                    properties.add(JSON_PROPERTYTYPE); 
                }
                                
                // Convert to array                
                String[] array = new String[properties.size()];
                Iterator<String> sIterator = properties.iterator();
                for ( int i = 0; sIterator.hasNext(); i++ ) {
                    array[i] = sIterator.next();
                }
                
                // Cache the array for next time
                _properties = array;
                
                return array;
            }
        };
    }
        
    public Object getJsonProperty(String property){
        if ( JSON_PROPERTYDATA.equals(property)) {
            Parameter valueType = getValueParameter();
            if(Value.INTEGER.equals(valueType)){
                return Integer.parseInt(_property.getValue());
            }
            else if(Value.BOOLEAN.equals(valueType)){
                return Boolean.parseBoolean(_property.getValue());
            }
            else if(Value.FLOAT.equals(valueType)){
                return Float.parseFloat(_property.getValue());
            }
            // Others will be treat as text
            return Utils.getUnescapedString(_property);
        }
        else if ( JSON_PROPERTYTYPE.equals(property)) {
            Parameter valueType = getValueParameter();
            if( null != valueType ){
                return valueType.getValue().toLowerCase();
            }
            return "text"; // $NON-NLS-1$
        }
        return null;
    }

    public void putJsonProperty(String propertyName, Object propertyValue) {
        // This method is called when converting JSON to iCalendar  
        if ( JSON_PROPERTYDATA.equals(propertyName) ) {
            _data = propertyValue;
        }
        else if ( JSON_PROPERTYTYPE.equals(propertyName) ) {
            String value = (String)propertyValue;
            _valueType = new Value(value.toUpperCase());
        }
    }
    

    /**
     * Compose the date property from its constituent parts.
     *      * 
     * @param propertyName
     * @return
     */
    public Property compose(String propertyName) {
        String valueType = "TEXT"; // $NON-NLS-1$
        
        // add parameter VALUE
        if( null!=_valueType ){
            _property.getParameters().add(_valueType);
            valueType = _valueType.getValue();
        }
        
        // add data
        try {
            if(Value.INTEGER.getValue().equals(valueType)){
                _property.setValue(((Double)_data).intValue()+"");
            }
            else if(Value.FLOAT.getValue().equals(valueType)){
                _property.setValue(((Double)_data).floatValue()+"");
            }
            else if(Value.BOOLEAN.getValue().equals(valueType)){
                _property.setValue(((Boolean)_data).toString());
            }
            else {
                // others represent as TEXT
                _property.setValue(((String)_data));
            }         
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (URISyntaxException e) {
            throw new JsonIllegalValueException(e);
        } catch (ParseException e) {
            throw new JsonIllegalValueException(e);
        }   
                
        return _property;
    }    

    private Parameter getValueParameter(){
        if(_valueType != null){
            return _valueType;
        }
        
        _valueType = _property.getParameter(VALUE);
        
        return _valueType;   
    }

}