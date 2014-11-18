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

public class JsonConstants {

	// General constants
	
	public static final String JSON_HREF = "href"; //$NON-NLS-1$
	public static final String JSON_EVENTS = "events"; //$NON-NLS-1$
	public static final String JSON_SCHEDULE_METHOD = "scheduleMethod"; //$NON-NLS-1$
	public static final String JSON_TIMEZONES = "timezones"; //$NON-NLS-1$
	public static final String JSON_NOTICES = "notices"; //$NON-NLS-1$
	public static final String JSON_PRODUCT_ID = "productId"; //$NON-NLS-1$
    public static final String JSON_LINKS = "links"; //$NON-NLS-1$
    public static final String JSON_RELATIONSHIP = "rel"; //$NON-NLS-1$

	// Attendee constants
	
	public static final String JSON_DISPLAY_NAME = "displayName"; //$NON-NLS-1$
	public static final String JSON_EMAIL = "email"; //$NON-NLS-1$
	public static final String JSON_STATUS = "status"; //$NON-NLS-1$
    public static final String JSON_SENT_BY = "sentBy"; //$NON-NLS-1$
    public static final String JSON_DIR = "dir"; //$NON-NLS-1$
    public static final String JSON_ROLE = "role"; //$NON-NLS-1$
    public static final String JSON_CUTYPE = "userType"; //$NON-NLS-1$
    public static final String JSON_MEMBER = "member"; //$NON-NLS-1$
    public static final String JSON_RSVP = "rsvp"; //$NON-NLS-1$
    public static final String JSON_DELEGATED_TO = "delegatedTo"; //$NON-NLS-1$
    public static final String JSON_DELEGATED_FROM = "delegatedFrom"; //$NON-NLS-1$
    
	// Event constants
	
	public static final String JSON_ATTENDEES = "attendees"; //$NON-NLS-1$
	public static final String JSON_ORGANIZER = "organizer"; //$NON-NLS-1$
	public static final String JSON_RDATES = "recurrenceDates"; //$NON-NLS-1$
	public static final String JSON_EXCEPTDATES = "exceptDates"; //$NON-NLS-1$
	public static final String JSON_SUMMARY = "summary"; //$NON-NLS-1$
	public static final String JSON_LOCATION = "location"; //$NON-NLS-1$
	public static final String JSON_ID = "id"; //$NON-NLS-1$
	public static final String JSON_RECURRENCE_ID = "recurrenceId"; //$NON-NLS-1$
	public static final String JSON_RECURRENCE_RULE = "recurrenceRule"; //$NON-NLS-1$
	public static final String JSON_TRANSPARENCY = "transparency"; //$NON-NLS-1$
	public static final String JSON_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String JSON_CLASS = "class"; //$NON-NLS-1$
	public static final String JSON_SEQUENCE = "sequence"; //$NON-NLS-1$
    public static final String JSON_CATEGORIES = "categories"; //$NON-NLS-1$
    public static final String JSON_PRIORITY = "priority"; //$NON-NLS-1$
    public static final String JSON_LASTMODIFIED = "last-modified"; //$NON-NLS-1$
    public static final String JSON_COMMENT = "comment"; //$NON-NLS-1$
    
    public static final String JSON_CHARSET = "charSet"; //$NON-NLS-1$
	// Time zone properties
	
	public static final String JSON_STANDARD = "standard"; //$NON-NLS-1$
	public static final String JSON_DAYLIGHT = "daylight"; //$NON-NLS-1$
	
	// Time zone observance properties
	
	public static final String JSON_OFFSET_FROM = "offsetFrom"; //$NON-NLS-1$
	public static final String JSON_OFFSET_TO = "offsetTo"; //$NON-NLS-1$
	
	// Alarm zone properties
    public static final String JSON_ALARM = "alarm"; //$NON-NLS-1$	
    public static final String JSON_ACTION = "action"; //$NON-NLS-1$
    public static final String JSON_TRIGGER = "trigger"; //$NON-NLS-1$
    public static final String JSON_ATTACH = "attach"; //$NON-NLS-1$
    public static final String JSON_REPEAT = "repeat"; //$NON-NLS-1$
    public static final String JSON_DURATION = "duration"; //$NON-NLS-1$

    // Trigger zone properties
    public static final String JSON_VALUEDATATYPE = "valueDataType"; //$NON-NLS-1$  
    public static final String JSON_RELATED = "related"; //$NON-NLS-1$
    public static final String JSON_VALUE = "value"; //$NON-NLS-1$

    // X property zone
    public static final String JSON_XPROPERTYNAME = "name"; //$NON-NLS-1$  
    public static final String JSON_PROPERTYDATA = "data"; //$NON-NLS-1$  
    public static final String JSON_PROPERTYTYPE = "type"; //$NON-NLS-1$  
    public static final String JSON_PARAMETERS = "parameters"; //$NON-NLS-1$  
    
    // Attach zone
    public static final String JSON_URI = "uri"; //$NON-NLS-1$  
    public static final String JSON_BINARY = "binary"; //$NON-NLS-1$  
    public static final String JSON_ENCODING = "encoding"; //$NON-NLS-1$
    public static final String JSON_FMTTYPE = "fmttype"; //$NON-NLS-1$
    
}
