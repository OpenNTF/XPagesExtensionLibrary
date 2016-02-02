/*
 * � Copyright IBM Corp. 2012
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

package com.ibm.domino.services.calendar.resources;

import static com.ibm.domino.commons.model.IGatekeeperProvider.FEATURE_REST_API_CALENDAR_EVENT_LIST;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.FORMAT_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.MEDIA_TYPE_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_SEPERATOR;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_CREATE_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_VIEW_EVENTS;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_BEFORE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_COUNT;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_DAYS;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_FIELDS;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_FORMAT;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_SINCE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_SINCE_NOW;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_START;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_WORKFLOW;
import static com.ibm.domino.services.calendar.service.CalendarService.WORKSPACE_TITLE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.wink.common.annotations.Workspace;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.domino.calendar.store.EventSet;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.json.JsonIllegalValueException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.json.JsonCalendarGenerator;
import com.ibm.domino.services.calendar.json.JsonCalendarParser;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

/**
 * Event collection resource
 */
@Workspace(workspaceTitle = WORKSPACE_TITLE, collectionTitle = "Events") // $NON-NLS-1$
@Path("calendar/events") // $NON-NLS-1$
public class EventCollectionResource {

    private static final String UNKNOWN_UID = "unknown"; // $NON-NLS-1$
    private static final long ONE_DAY = 24L * 60 * 60 * 1000; 
    

    /**
     * Gets events.
     * 
     * @param linksBuilders
     * @param uriInfo
     * @param format
     * @return
     */
    @GET
    public Response getEvents(@Context UriInfo uriInfo,
                        @QueryParam(URL_PARAM_FORMAT) String format,
                        @QueryParam(URL_PARAM_SINCE) String since,
                        @QueryParam(URL_PARAM_BEFORE) String before,
                        @QueryParam(URL_PARAM_COUNT) String count,
                        @QueryParam(URL_PARAM_START) String start,
                        @QueryParam(URL_PARAM_FIELDS) String fields,
                        @QueryParam(URL_PARAM_SINCE_NOW) String sincenow,
                        @QueryParam(URL_PARAM_DAYS) String days) {

        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getEvents"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT_LIST, STAT_VIEW_EVENTS);
        
        String jsonEntity = null;
        String eventString = null;
        String contentRange = null;
        Date fromDate = null;
        Date toDate = null;

        CalendarService.verifyDatabaseContext();

        try {
            // Validate parameters
            
            if ( StringUtil.isNotEmpty(since) && StringUtil.isNotEmpty(sincenow) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("A request cannot include both the \"since\" and \"sincenow\" parameters.",  // $NLX-EventCollectionResource.Arequestcannotincludeboththesince-1$
                        Response.Status.BAD_REQUEST));
            }

            if ( StringUtil.isNotEmpty(before) && StringUtil.isNotEmpty(days) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("A request cannot include both the \"before\" and \"days\" parameters.",  // $NLX-EventCollectionResource.Arequestcannotincludeboththebefor-1$
                        Response.Status.BAD_REQUEST));
            }

            // Get date range (if any)
            
            Date now = new Date();
            if ( StringUtil.isNotEmpty(since) ) {
                fromDate = Utils.dateFromString(since);
            }
            else if ( StringUtil.isNotEmpty(sincenow) ) {
                int iSinceNow = CalendarService.getParameterInt(URL_PARAM_SINCE_NOW, sincenow, false);
                fromDate = new Date(now.getTime() + (ONE_DAY * iSinceNow));
            }
            
            if ( StringUtil.isNotEmpty(before) ) {
                toDate = Utils.dateFromString(before);
                
                if ( fromDate != null && (toDate.getTime() <= fromDate.getTime()) ) {
                    // Before date cannot be less than since date
                    throw new WebApplicationException(CalendarService.createErrorResponse(
                                "The \"before\" date must be after the \"since\" date", // $NLX-EventCollectionResource.Beforedatecannotbelessthanorequal-1$ 
                                Response.Status.BAD_REQUEST));
                }
            }
            else if ( StringUtil.isNotEmpty(days) ) {
                int iDays = CalendarService.getParameterInt(URL_PARAM_DAYS, days, false);
                if ( iDays <= 0 ) {
                    throw new WebApplicationException(CalendarService.createErrorResponse(
                            MessageFormat.format("Invalid \"days\" parameter: {0}. The value must be greater than 0.", iDays), // $NLX-EventCollectionResource.Invaliddaysparameter0Itmustbegrea-1$
                            Response.Status.BAD_REQUEST));
                }
                
                if ( fromDate == null ) {
                    fromDate = now;
                }
                
                toDate = new Date(fromDate.getTime() + (ONE_DAY * iDays));
            }
            
            // Parse paging parameters
            
            int iStart = 0;
            int iCount = 50;
            
            if ( StringUtil.isNotEmpty(count) ) {
                iCount = CalendarService.getParameterInt(URL_PARAM_COUNT, count, false);
            }
            
            if ( StringUtil.isNotEmpty(start) ) {
                iStart = CalendarService.getParameterInt(URL_PARAM_START, start, false);
            }
            
            if ( iStart < 0 ) {
                throw new WebApplicationException(CalendarService.createErrorResponse(
                        MessageFormat.format("Invalid start parameter: {0}.  It must be greater than or equal to 0.", iStart),  // $NLX-EventCollectionResource.Invalidstartparameter0Itmustbegre-1$
                        Response.Status.BAD_REQUEST));
            }
            
            if ( iCount < 1 || iCount > 100 ) {
                throw new WebApplicationException(CalendarService.createErrorResponse(
                        MessageFormat.format("Invalid count parameter: {0}.  It must be greater than 0 and less than 101.", iCount),  // $NLX-EventCollectionResource.Invalidcountparameter0Itmustbegre-1$
                        Response.Status.BAD_REQUEST));
            }

            ArrayList<String> fieldList = null;
            if ( StringUtil.isNotEmpty(fields) ) {
                fieldList = getParameterStringList(URL_PARAM_FIELDS, fields);
            
            }
            // Get the events in iCalendar format
            
            EventSet set = StoreFactory.getEventStore().getEvents(fromDate, toDate, iStart, iCount, fieldList);
            if ( set != null && !StringUtil.isEmpty(set.getEvents())) {
                eventString = set.getEvents();
            }
            
            if ( eventString != null ) {
            
                // Set the content range header
                
                if ( set.getEventCount() > 0 ) {
                    int iLast = iStart + set.getEventCount() - 1;
                    contentRange = "items " + iStart + "-" + iLast; // $NON-NLS-1$
                }
                
                // Convert to alternate format (if necessary)
                
                if ( ! FORMAT_ICALENDAR.equalsIgnoreCase(format) ) {
                    jsonEntity = createJsonArray(eventString, uriInfo);
                }
            }
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        catch(ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        catch(IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        catch (ParseException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }

        ResponseBuilder builder = Response.ok();
        if ( contentRange != null ) {
            builder.header("Content-Range", contentRange); // $NON-NLS-1$
        }
        
        // If there are no events in the range, we return HTTP 200, no Content-Type, and 
        // no entity body.  After reading about best practices, I'm sure the HTTP 200 is right.
        // However, it might be better to return an an empty object in JSON or iCalendar format.
        
        if ( jsonEntity != null ) {
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        }
        else if ( eventString != null ) {
            builder.type(MEDIA_TYPE_ICALENDAR).entity(eventString);
        }
        
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getEvents", response); // $NON-NLS-1$

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createJsonEvent(String requestEntity, @Context UriInfo uriInfo,
            @QueryParam(URL_PARAM_WORKFLOW) String workflow) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "createJsonEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT_LIST, STAT_CREATE_EVENT);

        CalendarService.verifyDatabaseContext();

        String responseEntity = null;
        URI location = null;
        
        try {
            
            if ( StringUtil.isEmpty(requestEntity) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventCollectionResource.Emptyrequestbody-1$
            }
            
            // Parse JSON to iCal4j object model
            
            StringReader reader = new StringReader(requestEntity);
            JsonCalendarParser parser = new JsonCalendarParser();
            Calendar calendar = parser.parse(reader);
            
            // Serialize to iCalendar format
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CalendarOutputter outputter = new CalendarOutputter(false);
            outputter.output(calendar, baos);
            String icalendar = baos.toString("UTF-8");//$NON-NLS-1$
            
            // Store new event
            
            int flags = EventResource.getFlags(workflow);
            String output = StoreFactory.getEventStore().createEvent(icalendar, flags);
            
            // Extract the UID from the new event
            
            String uid = extractUid(output);
            if ( uid == null ) {
                uid = UNKNOWN_UID;
            }
            
            // Set the Location header for the response
            
            String url = uriInfo.getAbsolutePath().toString() + PATH_SEGMENT_SEPERATOR + uid;
            location = new URI(url);
            location = CalendarService.adaptUriToScn(location);
            
            // Prepare response
            
            StringReader sr = new StringReader(output);
            StringBuilder sb = new StringBuilder();
            CalendarParser cp = CalendarParserFactory.getInstance().createParser();
            URI baseURI;
            try {
                baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
                baseURI = CalendarService.adaptUriToScn(baseURI);
            }
            catch(IllegalArgumentException e){
                throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
            }
            cp.parse(new UnfoldingReader(sr, true), new JsonCalendarGenerator(sb, baseURI, false));
            responseEntity = sb.toString();
        }
        catch(JsonIllegalValueException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        } 
        catch (IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (ValidationException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        } 
        catch (ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (URISyntaxException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        
        ResponseBuilder builder = Response.created(location);
        builder.type(MediaType.APPLICATION_JSON).entity(responseEntity);

        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "createJsonEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    /**
     * Creates an event.
     * 
     * @param requestEntity
     * @param uriInfo
     * @return
     */
    @POST
    @Consumes(MEDIA_TYPE_ICALENDAR)
    public Response createEvent(String requestEntity, @Context UriInfo uriInfo,
            @QueryParam(URL_PARAM_WORKFLOW) String workflow) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "createEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT_LIST, STAT_CREATE_EVENT);

        CalendarService.verifyDatabaseContext();

        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        
        String responseEntity = null;
        URI location = null;
        
        try {

            if ( StringUtil.isEmpty(requestEntity) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventCollectionResource.Emptyrequestbody.1-1$
            }
            
            // Create the event
            
            int flags = EventResource.getFlags(workflow);
            responseEntity = StoreFactory.getEventStore().createEvent(requestEntity, flags);
            
            // Extract the UID from the new event
            
            String uid = extractUid(responseEntity);
            if ( uid == null ) {
                uid = UNKNOWN_UID;
            }
            
            // Set the Location header for the response
            
            String url = uriInfo.getAbsolutePath().toString() + PATH_SEGMENT_SEPERATOR + uid;
            location = new URI(url);
            location = CalendarService.adaptUriToScn(location);
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        catch(URISyntaxException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        finally {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
        }
        
        ResponseBuilder builder = Response.created(location);
        builder.type(MEDIA_TYPE_ICALENDAR).entity(responseEntity);
        Response response = builder.build();
        
        CALENDAR_SERVICE_LOGGER.traceExit(this, "createEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    private String extractUid(String icalendar) throws IOException, ParserException {
        
        String uid = null;
        StringReader reader = new StringReader(icalendar);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new UnfoldingReader(reader, true));
        if ( calendar != null && calendar.getComponents() != null ) {
            Iterator<Component> iterator = calendar.getComponents().iterator();
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if ( component instanceof VEvent ) {
                    uid = ((VEvent)component).getUid().getValue();
                    break;
                }
            }
        }
        
        return uid;
    }
    
    /**
     * Formats a list of events as a JSON array.
     * 
     * @param events
     * @param uriInfo
     * @return
     * @throws JsonException
     * @throws IOException
     */
    private String createJsonArray(String eventString, UriInfo uriInfo) 
                        throws ParserException, IOException {
    
        StringReader reader = new StringReader(eventString);
        StringBuilder sb = new StringBuilder();
        CalendarParser parser = CalendarParserFactory.getInstance().createParser();
        URI baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
        baseURI = CalendarService.adaptUriToScn(baseURI);
        parser.parse(new UnfoldingReader(reader, true), new JsonCalendarGenerator(sb, baseURI, false));

        return sb.toString();
    }
    
    // parameter should like: fields=start,end
    protected ArrayList<String> getParameterStringList(String paramName, String paramValue) {
        // check the format of parameter list
        Pattern pattern = Pattern.compile("(\\s*[\\w-]+\\s*,)*\\s*([\\w-]+)\\s*");         // $NON-NLS-1$
        Matcher matcher = pattern.matcher(paramValue);
        if(!matcher.matches()){
            String msg = MessageFormat.format("Invalid {0} parameter: {1} ", paramName, paramValue); // $NLX-EventCollectionResource.Invalid0parameter1-1$
            throw new WebApplicationException(
                    CalendarService.createErrorResponse(
                            new Exception(msg), Response.Status.BAD_REQUEST));            
        }
        
        // if match, at last have one field
        ArrayList<String> vaueList = new ArrayList<String>();
        String[] valueArray = paramValue.split(",");
        for(int i = 0; i< valueArray.length; i++){
            vaueList.add(valueArray[i].trim());
        }
        return vaueList;
    }

}