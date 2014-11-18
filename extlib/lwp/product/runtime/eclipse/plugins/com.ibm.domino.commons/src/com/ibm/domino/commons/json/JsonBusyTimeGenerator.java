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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Stack;

import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.component.VFreeBusy;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;

/**
 * As freebusys are parsed from iCalendar format, this class generates JSON content.
 */
public class JsonBusyTimeGenerator implements ContentHandler {

    // These fields are initialized by the constructor    
    
    // These fields hold the current state while parsing

    private int _freebusyCount = 0;
    
    private boolean _startComponment = false;
    
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
     */
    public JsonBusyTimeGenerator(StringBuilder sb) {
        _generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
    }
        
    public void startCalendar() {
        // Don't need calendar level
    }

    public void endCalendar() {
        // Don't need calendar level
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
                if ( component instanceof VFreeBusy ) {
                    if ( _freebusyCount == 0 ) {
                        VFreeBusy freebusy = (VFreeBusy)component;                    
                        
                        JsonFreeBusyAdapter freebusyAdapter = new JsonFreeBusyAdapter(freebusy);
                        _generator.toJson(freebusyAdapter);
                    }
                    else {
                        // Don't support multi freebusy times                         
                    }
                    _freebusyCount++;
                }
                else  {
                    // Don't support other components                
                }
            }
            else {                
                // The stack is not empty.  Add this component to it's parent.
                // Don't support multi level components
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
            if( _componentStack.empty() && _propertyValue != null){
                // the property in the icalendar must before any component 
                // icalbody   = calprops component
                if(_startComponment){
//                    CALENDAR_SERVICE_LOGGER.getLogger().fine("Un expected top level property:["+propertyName+"]");                        
                    return;                    
                }        
            }            
            
            Property property = null;
            property = PropertyFactoryImpl.getInstance().createProperty(propertyName, _parameters, _propertyValue);
                        
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