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

package com.ibm.domino.services.calendar.resources;

import static com.ibm.domino.calendar.store.ICalendarStore.FLAG_NO_WORKFLOW;
import static com.ibm.domino.calendar.store.ICalendarStore.FLAG_REPLACE_COMPLETELY;
import static com.ibm.domino.calendar.store.ICalendarStore.FLAG_SMART_SEQUENCE;
import static com.ibm.domino.commons.model.IGatekeeperProvider.FEATURE_REST_API_CALENDAR_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.FORMAT_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.INSTANCE;
import static com.ibm.domino.services.calendar.service.CalendarService.MEDIA_TYPE_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_PARAM_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_PARAM_INSTANCE;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_INSTANCES;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_SEPERATOR;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_DELETE_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_MISC;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_READ_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_UPDATE_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_ACTION_TYPE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_FORMAT;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_LITERALLY;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_RECURRENCERANGE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_STRICT_SEQUENCE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_WORKFLOW;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_WORKFLOWCOMMENT;
import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.model.Property.EXDATE;
import static net.fortuna.ical4j.model.Property.RDATE;
import static net.fortuna.ical4j.model.Property.RRULE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.w3c.dom.DOMException;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.domino.calendar.store.RecurrenceRange;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.json.JsonIllegalValueException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.json.JsonCalendarGenerator;
import com.ibm.domino.services.calendar.json.JsonCalendarParser;
import com.ibm.domino.services.calendar.json.JsonEventAdapter;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

@Path("calendar/events/" + PATH_PARAM_EVENT) // $NON-NLS-1$
public class EventResource {

    private static final long ONE_YEAR = 365L * 24 * 60 * 60 * 1000; 

    /**
     * Get a single event.
     * 
     * @param uriInfo
     * @param id
     * @return
     */
    @GET
    public Response getEvent(@Context UriInfo uriInfo, @PathParam(EVENT) String id,
                        @QueryParam(URL_PARAM_FORMAT) String format) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_READ_EVENT);

        CalendarService.verifyDatabaseContext();

        // Get the base URL (minus the UID)
        // BaseURI/eventUID
        URI baseURI;
        try{
        baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
        }catch(IllegalArgumentException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR + UriHelper.encodePathSegment(id) );
        baseURI = CalendarService.adaptUriToScn(baseURI);
        
        // Get the event
        
        Response response = getEventInternal(id, null, baseURI, format);
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    @Path("instances") // $NON-NLS-1$
    @GET
    public Response getEventInstances(@Context UriInfo uriInfo, @PathParam(EVENT) String id) {
        
        String jsonEntity = null;
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getEventInstances"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_MISC);

        CalendarService.verifyDatabaseContext();

        try {
            // Get the base URL (minus the UID)
            URI baseURI;
            try{
            baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
            }catch(IllegalArgumentException e){
                throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
            }
            baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR+ PATH_SEGMENT_INSTANCES);
            baseURI = CalendarService.adaptUriToScn(baseURI);
            
            // Get the iCalendar representation for a single event
            
            String eventString = StoreFactory.getEventStore().getEvent(id, null);
            
            // Get the RRULE or RDATE
            ArrayList<String> recurIdList= new ArrayList<String>();
            RRule rrule = null;
            RDate rdate = null;
            ExDate exdate = null;
            DtStart dtStart = null;
            Value value= Value.DATE_TIME;
            
            StringReader reader = new StringReader(eventString);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(new UnfoldingReader(reader, true));
            if ( calendar != null && calendar.getComponents() != null ) {
                Iterator<Component> iterator = calendar.getComponents().iterator();
                while (iterator.hasNext()) {
                    Component component = iterator.next();
                    if ( component instanceof VEvent ) {
                        VEvent event = (VEvent)component;
                        rrule = (RRule)event.getProperty(RRULE);
                        rdate = (RDate)event.getProperty(RDATE);
                        exdate = (ExDate)event.getProperty(EXDATE);
                        dtStart = event.getStartDate();
                        if( dtStart.getParameter(VALUE) != null ){
                            value = (Value) dtStart.getParameter(VALUE);
                        }
                        break;
                    }
                }
            }
            
            // get recurDates
            DateList recurDates = null;
            if ( dtStart != null && rrule != null ) {
                // TODO:  Get more than a ten year range of instances
                Date start = dtStart.getDate();
                long endTime = start.getTime() + ONE_YEAR * 10;
                Date end = new Date(endTime);
                recurDates = rrule.getRecur().getDates(start, end, Value.DATE_TIME);
            }
            else if ( rdate != null ) {
                PeriodList periods = rdate.getPeriods();
                if ( periods != null ) {
                    recurDates = new DateList();
                    Iterator iterator = periods.iterator();
                    while (iterator.hasNext()) {
                        Period period = (Period)iterator.next();
                        recurDates.add(period.getStart());
                    }
                }                        
            }
            
            if ( recurDates == null ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("Event does not recur", Response.Status.BAD_REQUEST)); // $NLX-EventResource.Eventdoesnotrecur-1$
            }
            else {      
                // get exDates
                DateList exDates =  (exdate!=null) ? exdate.getDates(): null;
                recurIdList = getRecurrenceIDList(recurDates, exDates, value);
                jsonEntity = JsonEventAdapter.getInstances(recurIdList, baseURI);   
            }
        } 
        catch (StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        } 
        catch (IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        catch (ParseException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
            
        // Build the response
        
        ResponseBuilder builder = Response.ok();
        if ( jsonEntity != null ) {
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        }

        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getEventInstances", response); // $NON-NLS-1$
        
        return response;
    }
    
    /**
     * Get a single instance of a recurring event.
     * 
     * @param uriInfo
     * @param id
     * @return
     */
    @Path(PATH_PARAM_INSTANCE)
    @GET
    public Response getEventInstance(@Context UriInfo uriInfo, @PathParam(EVENT) String id,
                        @PathParam(INSTANCE) String instanceId,
                        @QueryParam(URL_PARAM_FORMAT) String format) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getEventInstance"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_READ_EVENT);

        CalendarService.verifyDatabaseContext();

        // Get the base URL  (minus the UID, instanceID)
        // BaseURI/eventUID/instanceID
        URI baseURI;
        try{
        baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
        }catch(IllegalArgumentException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        // instanceId don't have special characters, don't need encode
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR + instanceId);
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR );
        baseURI = CalendarService.adaptUriToScn(baseURI);
                
        // Get the event
        
        Response response = getEventInternal(id, instanceId, baseURI, format);
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getEventInstance", response); // $NON-NLS-1$
        
        return response;
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateJsonEvent(String requestEntity,  @Context UriInfo uriInfo,  
                        @PathParam(EVENT) String id, 
                        @QueryParam(URL_PARAM_WORKFLOWCOMMENT) String comments,
                        @QueryParam(URL_PARAM_WORKFLOW) String workflow,
                        @QueryParam(URL_PARAM_LITERALLY) String literally,
                        @QueryParam(URL_PARAM_STRICT_SEQUENCE) String strictSequence) {

        String responseEntity = null;

        CALENDAR_SERVICE_LOGGER.traceEntry(this, "updateJsonEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_UPDATE_EVENT);

        CalendarService.verifyDatabaseContext();

        if ( StringUtil.isEmpty(requestEntity) ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventResource.Emptyrequestbody-1$
        }
        
        int flags = getUpdateFlags(workflow, literally, strictSequence);
        URI baseURI;
        try{
        baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
        }catch(IllegalArgumentException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR + UriHelper.encodePathSegment(id) );
        baseURI = CalendarService.adaptUriToScn(baseURI);
        
        responseEntity = updateJsonEventInternal(requestEntity, baseURI, id, null, comments, flags);
        
        ResponseBuilder builder = Response.ok();
        builder.type(MediaType.APPLICATION_JSON).entity(responseEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "updateJsonEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    @PUT
    @Path(PATH_PARAM_INSTANCE)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateJsonEventInstance(String requestEntity,  @Context UriInfo uriInfo, 
                        @PathParam(EVENT) String id, @PathParam(INSTANCE) String instanceId,
                        @QueryParam(URL_PARAM_WORKFLOWCOMMENT) String comments,
                        @QueryParam(URL_PARAM_WORKFLOW) String workflow,
                        @QueryParam(URL_PARAM_LITERALLY) String literally,
                        @QueryParam(URL_PARAM_STRICT_SEQUENCE) String strictSequence) {

        String responseEntity = null;

        CALENDAR_SERVICE_LOGGER.traceEntry(this, "updateJsonEventInstance"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_UPDATE_EVENT);

        CalendarService.verifyDatabaseContext();

        if ( StringUtil.isEmpty(requestEntity) ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventResource.Emptyrequestbody.1-1$
        }
        
        int flags = getUpdateFlags(workflow, literally, strictSequence);
        URI baseURI;
        try{
        baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
        }catch(IllegalArgumentException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        // instanceId don't have special characters, don't need encode
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR + instanceId);
        baseURI = UriHelper.trimAtLast(baseURI, PATH_SEGMENT_SEPERATOR );
        baseURI = CalendarService.adaptUriToScn(baseURI);
        responseEntity = updateJsonEventInternal(requestEntity, baseURI, id, instanceId, comments, flags);
        
        ResponseBuilder builder = Response.ok();
        builder.type(MediaType.APPLICATION_JSON).entity(responseEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "updateJsonEventInstance", response); // $NON-NLS-1$
        
        return response;
    }
    
    private String updateJsonEventInternal(String requestEntity,  URI baseURI,  
                        String id, String instanceId, String comments, int flags) {
    
        String jsonResponse = null;
        
        try {
            // Parse JSON to iCal4j object model
            
            StringReader reader = new StringReader(requestEntity);
            JsonCalendarParser parser = new JsonCalendarParser();
            Calendar calendar = parser.parse(reader);
            
            // Serialize to iCalendar format
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CalendarOutputter outputter = new CalendarOutputter(false);
            outputter.output(calendar, baos);
            String icalendar = baos.toString("UTF-8");
            
            // Update the event
            
            String output = StoreFactory.getEventStore().updateEvent(icalendar, id, instanceId, comments, flags);
            
            // Convert output to JSON

            StringReader sr = new StringReader(output);
            StringBuilder sb = new StringBuilder();
            CalendarParser cp = CalendarParserFactory.getInstance().createParser();
            
            
            cp.parse(new UnfoldingReader(sr, true), new JsonCalendarGenerator(sb, baseURI, false));
            jsonResponse = sb.toString();
        }
        catch(JsonIllegalValueException e){
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        } catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        } catch (IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } catch (ValidationException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } catch (ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        
        return jsonResponse;
    }

    @PUT
    @Consumes(MEDIA_TYPE_ICALENDAR)
    public Response updateEvent(String requestEntity,  @Context UriInfo uriInfo,
                        @PathParam(EVENT) String id, 
                        @QueryParam(URL_PARAM_WORKFLOWCOMMENT) String comments,
                        @QueryParam(URL_PARAM_WORKFLOW) String workflow,
                        @QueryParam(URL_PARAM_LITERALLY) String literally,
                        @QueryParam(URL_PARAM_STRICT_SEQUENCE) String strictSequence) {
        
        String responseEntity = null;

        CALENDAR_SERVICE_LOGGER.traceEntry(this, "updateEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_UPDATE_EVENT);

        CalendarService.verifyDatabaseContext();

        try {
            if ( StringUtil.isEmpty(requestEntity) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventResource.Emptyrequestbody.2-1$
            }
            
            int flags = getUpdateFlags(workflow, literally, strictSequence);
            responseEntity = StoreFactory.getEventStore().updateEvent(requestEntity, id, null, comments, flags);
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        
        ResponseBuilder builder = Response.ok();
        builder.type(MEDIA_TYPE_ICALENDAR).entity(responseEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "updateEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    @PUT
    @Path(PATH_PARAM_INSTANCE)
    @Consumes(MEDIA_TYPE_ICALENDAR)
    public Response updateEventInstance(String requestEntity,  @Context UriInfo uriInfo, 
                        @PathParam(EVENT) String id, @PathParam(INSTANCE) String instanceId,
                        @QueryParam(URL_PARAM_WORKFLOWCOMMENT) String comments,
                        @QueryParam(URL_PARAM_WORKFLOW) String workflow,
                        @QueryParam(URL_PARAM_LITERALLY) String literally,
                        @QueryParam(URL_PARAM_STRICT_SEQUENCE) String strictSequence) {
        
        String responseEntity = null;
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "updateEventInstance"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_UPDATE_EVENT);

        CalendarService.verifyDatabaseContext();

        try {
            if ( StringUtil.isEmpty(requestEntity) ) {
                throw new WebApplicationException(CalendarService.createErrorResponse("Empty request body.", Status.BAD_REQUEST)); // $NLX-EventResource.Emptyrequestbody.3-1$
            }
            
            int flags = getUpdateFlags(workflow, literally, strictSequence);
            responseEntity = StoreFactory.getEventStore().updateEvent(requestEntity, id, instanceId, comments, flags);
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        
        ResponseBuilder builder = Response.ok();
        builder.type(MEDIA_TYPE_ICALENDAR).entity(responseEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "updateEventInstance", response); // $NON-NLS-1$
        
        return response;
    }
    
    /**
     * Deletes an event.
     * 
     * @param id
     * @return
     */
    @DELETE
    public Response deleteEvent(@PathParam(EVENT) String id, @QueryParam(URL_PARAM_WORKFLOW) String workflow) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "deleteEvent"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_DELETE_EVENT);

        CalendarService.verifyDatabaseContext();

        try {
            int flags = getFlags(workflow);
            StoreFactory.getEventStore().deleteEvent(id, null, null, flags);
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        
        Response response = Response.ok().build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "deleteEvent", response); // $NON-NLS-1$
        
        return response;
    }
    
    @DELETE
    @Path(PATH_PARAM_INSTANCE)
    public Response deleteEventInstance(@PathParam(EVENT) String id,
            @PathParam(INSTANCE) String instanceId, @QueryParam(URL_PARAM_RECURRENCERANGE) String range,
            @QueryParam(URL_PARAM_WORKFLOW) String workflow) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "deleteEventInstance"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_DELETE_EVENT);

        CalendarService.verifyDatabaseContext();

        try {
            RecurrenceRange rr = Utils.translateRecurrenceRange(range);
            
            int flags = getFlags(workflow);
            StoreFactory.getEventStore().deleteEvent(id, instanceId, rr, flags);
        }
        catch(StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        }
        
        Response response = Response.ok().build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "deleteEventInstance", response); // $NON-NLS-1$
        
        return response;
    }
    
    @Path("action") // $NON-NLS-1$
    @PUT
    public Response putEventAction(String requestEntity, @HeaderParam("Content-Type") String contentType, // $NON-NLS-1$
                        @Context UriInfo uriInfo, @PathParam(EVENT) String id,
                        @QueryParam(URL_PARAM_ACTION_TYPE) String type) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "putEventAction"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_MISC);

        CalendarService.verifyDatabaseContext();

        EventActionResource.putEventActionInternal(requestEntity, contentType, id, null, null, type);

        ResponseBuilder builder = Response.ok();
        Response response = builder.build();

        CALENDAR_SERVICE_LOGGER.traceExit(this, "putEventAction", response); // $NON-NLS-1$
        return response;
    }
    
    @Path(PATH_PARAM_INSTANCE + "/action") // $NON-NLS-1$
    @PUT
    public Response putEventInstanceAction(String requestEntity, @HeaderParam("Content-Type") String contentType, // $NON-NLS-1$
            @Context UriInfo uriInfo, @PathParam(EVENT) String id, 
            @PathParam(INSTANCE) String instanceId,  @QueryParam(URL_PARAM_RECURRENCERANGE) String range,
            @QueryParam(URL_PARAM_ACTION_TYPE) String type) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "putEventActionInstance"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_MISC);

        CalendarService.verifyDatabaseContext();

        EventActionResource.putEventActionInternal(requestEntity, contentType, id, instanceId, range, type);

        ResponseBuilder builder = Response.ok();
        Response response = builder.build();

        CALENDAR_SERVICE_LOGGER.traceExit(this, "putEventActionInstance", response); // $NON-NLS-1$
        return response;
    }

    private static int getUpdateFlags(String workflow, String literally, String strictSequence) {
        int flags = getFlags(workflow);

        if ( "true".equalsIgnoreCase(literally) ) { // $NON-NLS-1$
            flags |= FLAG_REPLACE_COMPLETELY;
        }

        // Subtle behavior here: FLAG_SMART_SEQUENCE is the default for updates.
        // Only clear the flag if the URL includes strictsequence=true.
        
        if ( ! "true".equalsIgnoreCase(strictSequence) ) { // $NON-NLS-1$
            flags |= FLAG_SMART_SEQUENCE;
        }
        
        return flags;
    }
    
    public static int getFlags(String workflow) {
        int flags = 0;
        
        if ( "false".equalsIgnoreCase(workflow) ) { // $NON-NLS-1$
            flags |= FLAG_NO_WORKFLOW;
        }
        
        return flags;
    }
    
    private Response getEventInternal(String id, String instanceId, URI baseUrl, String format) {
        
        String jsonEntity = null;
        String eventString = null;
        
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        
        try {
            // Get the iCalendar representation for a single event
            eventString = StoreFactory.getEventStore().getEvent(id, instanceId);
            
            if ( ! FORMAT_ICALENDAR.equalsIgnoreCase(format) ) {
                
                // Parse the iCalendar into JSON format

                StringReader reader = new StringReader(eventString);
                StringBuilder sb = new StringBuilder();
                CalendarParser parser = CalendarParserFactory.getInstance().createParser();
                parser.parse(new UnfoldingReader(reader, true), new JsonCalendarGenerator(sb, baseUrl, false));

                jsonEntity = sb.toString();
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
        catch (DOMException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } 
        finally {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
        }

        ResponseBuilder builder = Response.ok();
        if ( jsonEntity != null ) {
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        }
        else {
            builder.type(MEDIA_TYPE_ICALENDAR).entity(eventString);
        }
        
        return builder.build();
    }

    /**
     * Get a List of RECURRENCE-ID by recurrence dates and EXDATEs
     * 
     * @param recurDates
     * @param exDates
     * @param value     decide the format of RECURRENCE-ID
     * @return
     */
    public ArrayList<String> getRecurrenceIDList(DateList recurDates, DateList exDates, Value value) throws ParseException{
        ArrayList<String> recurIdList= new ArrayList<String>();
        // Date
        if( value == Value.DATE ){
            fillRecurIDListbyDate(recurIdList, recurDates);            
        }
        // DateTime
        else{
            fillRecurIDListbyDateTime(recurIdList, recurDates);            
        }

        // remove the exdates
        if( exDates != null ){
            if( value == Value.DATE ){
                removeRecurIDListbyDate(recurIdList, exDates);   
            }
            else{
                removeRecurIDListbyDateTime(recurIdList, exDates);   
            }
        }
        
        return recurIdList;
    }

    /**
     * @param recurIdList
     * @param iterator      The Data in Iterator should be DateTime
     */
    public void fillRecurIDListbyDateTime(ArrayList<String> recurIdList, DateList recurDates) {
        String sDateTime;
        Iterator iterator = recurDates.iterator();
        while (iterator.hasNext()) {
            DateTime dateTime = (DateTime)iterator.next();
            dateTime.setUtc(true);
            sDateTime = dateTime.toString();
            recurIdList.add(sDateTime);
        }
    }

    /**
     * @param recurIdList
     * @param iterator  The Data in Iterator should be DateTime
     */
    public void fillRecurIDListbyDate(ArrayList<String> recurIdList, DateList recurDates) {
        String sDateTime;
        Iterator iterator = recurDates.iterator();
        while (iterator.hasNext()) {
            DateTime dateTime = (DateTime)iterator.next();
            sDateTime = dateTime.toString();
            int positionT = sDateTime.indexOf("T");
            if(positionT>0){
                sDateTime = sDateTime.substring(0, positionT);
            }
            recurIdList.add(sDateTime);
        }
    }

    /**
     * @param recurIdList
     * @param iterator  The Data in Iterator should be DateTime
     */
    public void removeRecurIDListbyDateTime(ArrayList<String> recurIdList, DateList exDates) {
        String sDateTime;
        Iterator iterator = exDates.iterator();
        while (iterator.hasNext()) {
            DateTime exDate = (DateTime)iterator.next();
            exDate.setUtc(true);
            // exDates must have same type with value
            sDateTime = exDate.toString();
            recurIdList.remove(sDateTime);
        }
    }

    /**
     * @param recurIdList
     * @param iterator  The Data in Iterator should be Date or DateTime
     */
    public void removeRecurIDListbyDate(ArrayList<String> recurIdList, DateList exDates) {
        String sDateTime;
        Iterator iterator = exDates.iterator();
        while (iterator.hasNext()) {
            Date exDate = (Date)iterator.next();
            if(exDate instanceof DateTime){
                ((DateTime) exDate).setUtc(true);
            }
            // exDates must have same type with value
            sDateTime = exDate.toString();
            int positionT = sDateTime.indexOf("T");
            if(positionT>0){
                sDateTime = sDateTime.substring(0, positionT);
            }
            recurIdList.remove(sDateTime);
        }
    }
    
}