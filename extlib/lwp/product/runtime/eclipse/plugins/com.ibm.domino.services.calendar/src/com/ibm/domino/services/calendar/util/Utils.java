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

package com.ibm.domino.services.calendar.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.fortuna.ical4j.model.Property;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.calendar.store.Action;
import com.ibm.domino.calendar.store.CounterAction;
import com.ibm.domino.calendar.store.DeclineAction;
import com.ibm.domino.calendar.store.DelegateAction;
import com.ibm.domino.calendar.store.RecurrenceRange;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.services.calendar.service.CalendarService;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_ACCEPT;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_CANCEL;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_COUNTER;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_DECLINE;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_DELEGATE;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_DELETE;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_REQUEST_INFO;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_TENTATIVE;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_PROCESS_ALL;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE_ACTION_REMOVE_CANCELLED;

public class Utils {

    private static SimpleDateFormat ISO8601_UTC = getUtcFormatter();
    private static SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // $NON-NLS-1$
    private static int CALENDAR_STORE_ERROR_BASE = 1024;
    
    /**
     * Converts a date to an ISO8601 string.
     * 
     * @param value The date.
     * @param utc   If <code>true</code>, format the time in UTC.  If <code>false</code>,
     *              format the time in the local time zone.
     * @return      The ISO8601 string.
     * @throws IOException
     */
    public static String dateToString(Date value, boolean utc) throws IOException {
        
        String result = null;
        
        if ( utc ) {
            result = ISO8601_UTC.format((Date)value);
        }
        else {
            result = ISO8601.format((Date)value);
        }
        
        return result;
    }
    
    public static Date dateFromString(String value) throws ParseException {
        return ISO8601_UTC.parse(value);
    }
    
    private static SimpleDateFormat getUtcFormatter() {
        TimeZone tz = TimeZone.getTimeZone("UTC"); // $NON-NLS-1$
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
        formatter.setTimeZone(tz);
        return formatter;
    }
    
    /**
     * Translates a URL parameter to an action type.
     * 
     * @param type
     * @return
     */
    public static int translateActionType(String type) {
        int actionType = -1;

        if ( NOTICE_ACTION_ACCEPT.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_ACCEPT;
        }
        else if ( NOTICE_ACTION_CANCEL.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_CANCEL;
        }
        else if ( NOTICE_ACTION_COUNTER.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_COUNTER;
        }
        else if ( NOTICE_ACTION_DECLINE.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_DECLINE;
        }
        else if ( NOTICE_ACTION_DELEGATE.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_DELEGATE;
        }
        else if ( NOTICE_ACTION_DELETE.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_DELETE;
        }
        else if ( NOTICE_ACTION_REQUEST_INFO.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_REQUEST_INFO;
        }
        else if ( NOTICE_ACTION_TENTATIVE.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_TENTATIVE;
        }
        else if ( NOTICE_ACTION_PROCESS_ALL.equalsIgnoreCase(type) ) { // $NON-NLS-1$
            actionType = Action.ACTION_PROCESS_ALL;
        }
        else if ( NOTICE_ACTION_REMOVE_CANCELLED.equalsIgnoreCase(type) ){ // $NON-NLS-1$
            actionType = Action.ACTION_REMOVE_CANCEL;            
        }
        
        return actionType;
    }

    /**
     * Translates a URL parameter to a recurrencerange.
     * 
     * @param range
     * @return
     */
    public static RecurrenceRange translateRecurrenceRange(String range) {
        RecurrenceRange rr = RecurrenceRange.THIS_INSTANCE;
        if(null!= range){
            if ( "all".equalsIgnoreCase(range) ) { // $NON-NLS-1$
                rr = RecurrenceRange.ALL_INSTANCES;
            }
            else if ( "future".equalsIgnoreCase(range) ) { // $NON-NLS-1$
                rr = RecurrenceRange.THIS_AND_FUTURE;
            }
            else if ( "previous".equalsIgnoreCase(range) ) { // $NON-NLS-1$
                rr = RecurrenceRange.THIS_AND_PREVIOUS;
            }
        }
        return rr;
    }
    /**
     * Creates an Action instance.
     * 
     * @param actionType
     * @param contentType
     * @param requestEntity
     * @return
     * @throws JsonException
     * @throws ParseException 
     */
    public static Action createAction(int actionType, String contentType, String requestEntity) throws JsonException, ParseException {
        
        String comments = null;
        String delegateTo = null;
        Date counterStart = null;
        Date counterEnd = null;
        Boolean keepInformed = null;
        
        if ( StringUtil.isNotEmpty(requestEntity) ) {
            if ( contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON) ) {
                Object object = JsonParser.fromJson(JsonJavaFactory.instanceEx, requestEntity);
                if ( object instanceof JsonObject ) {
                    Object property = ((JsonObject)object).getJsonProperty("comments"); // $NON-NLS-1$
                    if ( property instanceof String ) {
                        comments = (String)property;
                    }
    
                    property = ((JsonObject)object).getJsonProperty("delegateTo"); // $NON-NLS-1$
                    if ( property instanceof String ) {
                        delegateTo = (String)property;
                    }
                    
                    property = ((JsonObject)object).getJsonProperty("counterStart"); // $NON-NLS-1$
                    if ( property instanceof String ) {
                        counterStart = dateFromString((String)property);
                    }
                    
                    property = ((JsonObject)object).getJsonProperty("counterEnd"); // $NON-NLS-1$
                    if ( property instanceof String ) {
                        counterEnd = dateFromString((String)property);
                    }
                    
                    property = ((JsonObject)object).getJsonProperty("keepInformed"); // $NON-NLS-1$
                    if ( property instanceof Boolean ) {
                        keepInformed = (Boolean)property;
                    }
                }
            }
            else {
                throw new WebApplicationException(CalendarService.createErrorResponse("Unsupported media type.", Status.UNSUPPORTED_MEDIA_TYPE)); // $NLX-Utils.Unsupportedmediatype-1$
            }
        }
        
        Action action = null;
        if ( actionType == Action.ACTION_DELEGATE) {
            action = new DelegateAction(comments, delegateTo, keepInformed);
        }
        else if ( actionType == Action.ACTION_DECLINE) {
            action = new DeclineAction(comments, keepInformed);
        }
        else if ( actionType == Action.ACTION_COUNTER ) {
            action = new CounterAction(comments, counterStart, counterEnd);
        }
        else {
            action = new Action(actionType, comments);
        }
        
        return action;
    }

    /**
     * Maps a calendar store error to an HTTP status code.
     * 
     * @param e
     * @return
     */
    public static Response.Status mapStatus(StoreException e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        
        switch(e.getErrorCode()) {
            case StoreException.ERR_BAD_IDENTIFIER:
                status = Response.Status.BAD_REQUEST;
                break;

            case StoreException.ERR_SENDING_NOTICES:
                status = Response.Status.INTERNAL_SERVER_ERROR;
                break;

            case StoreException.ERR_NEWER_VERSION_EXISTS:
                status = Response.Status.CONFLICT;
                break;
                
            case StoreException.ERR_ACTION_NOT_SUPPORTED:
                status = Response.Status.BAD_REQUEST;
                break;
                
            case StoreException.ERR_INVITE_NOT_ACCEPTED:
                status = Response.Status.BAD_REQUEST;
                break;
                
            case StoreException.ERR_PERSONAL_CHANGES:
                status = Response.Status.CONFLICT;
                break;
                
            case StoreException.ERR_IDENTIFIER_NOT_FOUND:
                status = Response.Status.NOT_FOUND;
                break;
                
            case StoreException.ERR_ENTRY_EXISTS:
                status = Response.Status.CONFLICT;
                break;
                
            case StoreException.ERR_BAD_ACTION:
                status = Response.Status.BAD_REQUEST;
                break;    
            case StoreException.ERR_INVALID_ICALSTR:
                status = Response.Status.BAD_REQUEST;
                break;    
                
        }
        
        return status;
    }
    
    /**
     * Gets a map of extra properties to add to a JSON error.
     * 
     * <p>For now, the map holds one name/value pair called "cserror".
     * 
     * @param e
     * @return
     */
    public static Map<String, Object> getExtraProps(StoreException e) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cserror", new Integer(CALENDAR_STORE_ERROR_BASE + e.getErrorCode())); // $NON-NLS-1$
        return map;
    }

    /**
     * Reads an iCalendar property value and then unescapes it.
     * 
     * @param property
     * RFC         Java
     * \n          \r\n
     * \,          ,
     * \;          ; 
     * \\          \
     * @return
     */
    public static String getUnescapedString(Property property) {
        String value = null;
        
        if ( property != null ) {
            value = property.getValue();
        }
        
        if ( value != null ) {
            
            // Replace all occurences of "\\n" with "\r\n".
            value = value.replaceAll("(?<!\\\\)\\\\n", "\r\n"); // $NON-NLS-1$ $NON-NLS-2$
            value = value.replaceAll("(?<!\\\\)\\\\N", "\r\n"); // $NON-NLS-1$ $NON-NLS-2$
            
            // Unescape all commas.
            value = value.replaceAll("\\\\,", ",");
            // Unescape all semicolons
            value = value.replaceAll("\\\\;", ";");
            // Unescape all backslashes.
            value = value.replaceAll("\\\\\\\\", "\\\\");
        }
        
        return value;
    }
    
}