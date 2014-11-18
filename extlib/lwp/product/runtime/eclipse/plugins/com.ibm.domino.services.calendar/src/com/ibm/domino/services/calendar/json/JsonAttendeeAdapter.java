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

import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_CUTYPE;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DELEGATED_FROM;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DELEGATED_TO;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DIR;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_DISPLAY_NAME;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_EMAIL;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_MEMBER;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_ROLE;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RSVP;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SENT_BY;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_STATUS;
import static net.fortuna.ical4j.model.Parameter.CN;
import static net.fortuna.ical4j.model.Parameter.CUTYPE;
import static net.fortuna.ical4j.model.Parameter.DELEGATED_FROM;
import static net.fortuna.ical4j.model.Parameter.DELEGATED_TO;
import static net.fortuna.ical4j.model.Parameter.DIR;
import static net.fortuna.ical4j.model.Parameter.MEMBER;
import static net.fortuna.ical4j.model.Parameter.PARTSTAT;
import static net.fortuna.ical4j.model.Parameter.ROLE;
import static net.fortuna.ical4j.model.Parameter.RSVP;
import static net.fortuna.ical4j.model.Parameter.SENT_BY;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.DelegatedFrom;
import net.fortuna.ical4j.model.parameter.DelegatedTo;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Member;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.commons.json.JsonIllegalValueException;

/**
 * Adapts an attendee (organizer or guest) to a JsonObject.
 */
public class JsonAttendeeAdapter implements JsonObject {
	
	private static final String MAILTO = "mailto:"; //$NON-NLS-1$
	
	// JSON properties
    private String _properties[];
	private Property _property;
	
	public JsonAttendeeAdapter(Property property) {
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
                
                if ( _property instanceof Organizer ) {
                    Organizer organizer = (Organizer)_property;
                    
                    if(organizer.getParameter(CN)!=null){
                        properties.add(JSON_DISPLAY_NAME);                        
                    }
                    if(organizer.getParameter(SENT_BY)!=null){
                        properties.add(JSON_SENT_BY);                        
                    }
                    if(organizer.getParameter(DIR)!=null){
                        properties.add(JSON_DIR);                        
                    }
                    if(organizer.getCalAddress()!=null){
                        properties.add(JSON_EMAIL);                   
                    }
                }
                else if ( _property instanceof Attendee) {
                    Attendee attendee = (Attendee)_property;
                    
                    if(attendee.getParameter(ROLE)!=null){
                        properties.add(JSON_ROLE);                        
                    }
                    if(attendee.getParameter(CUTYPE)!=null){
                        properties.add(JSON_CUTYPE);                        
                    }
                    if(attendee.getParameter(MEMBER)!=null){
                        properties.add(JSON_MEMBER);                        
                    }
                    if(attendee.getParameter(PARTSTAT)!=null){
                        properties.add(JSON_STATUS);                        
                    }
                    if(attendee.getParameter(RSVP)!=null){
                        properties.add(JSON_RSVP);                        
                    }
                    if(attendee.getParameter(DELEGATED_TO)!=null){
                        properties.add(JSON_DELEGATED_TO);                        
                    }
                    if(attendee.getParameter(DELEGATED_FROM)!=null){
                        properties.add(JSON_DELEGATED_FROM);                        
                    }
                    if(attendee.getParameter(CN)!=null){
                        properties.add(JSON_DISPLAY_NAME);                        
                    }
                    if(attendee.getParameter(SENT_BY)!=null){
                        properties.add(JSON_SENT_BY);                        
                    }
                    if(attendee.getParameter(DIR)!=null){
                        properties.add(JSON_DIR);                        
                    }
                    if(attendee.getCalAddress()!=null){
                        properties.add(JSON_EMAIL);                   
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
		if ( !(_property instanceof Organizer) && !(_property instanceof Attendee) ) {
			return null;
		}
		
		String value = null;
		
		if ( JSON_EMAIL.equals(propertyName) ) {
			URI uri = null;
			
			if ( _property instanceof Organizer ) {
				Organizer organizer = (Organizer)_property;
				uri = organizer.getCalAddress();
			}
			else {
				Attendee attendee = (Attendee)_property;
				uri = attendee.getCalAddress();
			}
			
			value = uri.getSchemeSpecificPart();
		}
		else if ( JSON_DISPLAY_NAME.equals(propertyName) ) {
			Parameter  param = _property.getParameter(CN);
			if ( param != null ) {
				value = param.getValue();
			}
		}
		else if ( JSON_STATUS.equals(propertyName ) ) {
			Parameter param = _property.getParameter(PARTSTAT);
			if ( param != null ) {
				value = param.getValue().toLowerCase();
			}
		}
        else if ( JSON_ROLE.equals(propertyName ) ) {
            Parameter param = _property.getParameter(ROLE);
            if ( param != null ) {
                value = param.getValue().toLowerCase();
            }
        }
        else if ( JSON_CUTYPE.equals(propertyName ) ) {
            Parameter param = _property.getParameter(CUTYPE);
            if ( param != null ) {
                value = param.getValue().toLowerCase();
            }
        }
        else if ( JSON_MEMBER.equals(propertyName ) ) {
            Parameter param = _property.getParameter(MEMBER);
            if ( param != null ) {
                value = param.getValue();
            }
        }
        else if ( JSON_DELEGATED_TO.equals(propertyName ) ) {
            Parameter param = _property.getParameter(DELEGATED_TO);
            if ( param != null ) {
                value = param.getValue();
            }
        }
        else if ( JSON_DELEGATED_FROM.equals(propertyName ) ) {
            Parameter param = _property.getParameter(DELEGATED_FROM);
            if ( param != null ) {
                value = param.getValue();
            }
        }		
        else if ( JSON_SENT_BY.equals(propertyName ) ) {
            Parameter param = _property.getParameter(SENT_BY);
            if ( param != null ) {
                value = param.getValue();
            }
        }
        else if ( JSON_DIR.equals(propertyName ) ) {
            Parameter param = _property.getParameter(DIR);
            if ( param != null ) {
                value = param.getValue();
            }
        }
        else if ( JSON_RSVP.equals(propertyName ) ) {
            Parameter param = _property.getParameter(RSVP);
            if ( param != null ) {
                return  Boolean.parseBoolean(param.getValue());
            }
        }
        
		return value;
	}

	public void putJsonProperty(String propertyName, Object value) {
		
		try {
			if ( JSON_DISPLAY_NAME.equals(propertyName) ) {
				_property.getParameters().add(new Cn((String)value));
			}
			else if ( JSON_EMAIL.equals(propertyName) ) {
				
				if ( _property instanceof Organizer ) {
					Organizer organizer = (Organizer)_property;
					organizer.setCalAddress(new URI(MAILTO + (String)value));
				}
				else {
					Attendee attendee = (Attendee)_property;
					attendee.setCalAddress(new URI(MAILTO + (String)value));
				}
			}
			else if ( JSON_STATUS.equals(propertyName) ) {
				if ( value instanceof String ) {
					String paraValue = ((String)value).toUpperCase();
					_property.getParameters().add(new PartStat(paraValue));
				}
			}
	        else if ( JSON_ROLE.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    String paraValue = ((String)value).toUpperCase();
                    _property.getParameters().add(new Role(paraValue));
                }
	        }
	        else if ( JSON_CUTYPE.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    String paraValue = ((String)value).toUpperCase();
                    _property.getParameters().add(new CuType(paraValue));
                }
	        }
	        else if ( JSON_MEMBER.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    _property.getParameters().add(new Member((String)value));
                }
	        }
	        else if ( JSON_DELEGATED_TO.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    _property.getParameters().add(new DelegatedTo((String)value));
                }
	        }
	        else if ( JSON_DELEGATED_FROM.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    _property.getParameters().add(new DelegatedFrom((String)value));
                }
	        }       
	        else if ( JSON_SENT_BY.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    _property.getParameters().add(new SentBy((String)value));
                }
	        }
	        else if ( JSON_DIR.equals(propertyName ) ) {
                if ( value instanceof String ) {
                    _property.getParameters().add(new Dir((String)value));
                }
	        }
	        else if ( JSON_RSVP.equals(propertyName ) ) {
                if ( value instanceof Boolean ) {
                    _property.getParameters().add(new Rsvp((Boolean)value));
                }
	        }
		}
		catch (URISyntaxException e) {
            throw new JsonIllegalValueException(e);
		}
	}
	
}
