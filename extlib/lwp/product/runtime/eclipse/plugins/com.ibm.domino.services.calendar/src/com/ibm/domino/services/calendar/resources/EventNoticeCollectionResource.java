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

import static com.ibm.domino.commons.model.IGatekeeperProvider.FEATURE_REST_API_CALENDAR_EVENT;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_HREF;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_NOTICES;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SCHEDULE_METHOD;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_SUMMARY;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_PARAM_EVENT;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_EVENTS;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_NOTICES;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_SEPERATOR;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_MISC;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.calendar.store.ICalendarStore;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

@Path("calendar/events/" + PATH_PARAM_EVENT + "/notices") // $NON-NLS-2$ $NON-NLS-1$
public class EventNoticeCollectionResource {
    
    @GET
    public Response getEventNotices(@Context UriInfo uriInfo, @PathParam(EVENT) String eventId) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getEventNotices"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_EVENT, STAT_MISC);
        String jsonEntity = null;

        CalendarService.verifyDatabaseContext();

        try {
            // Get the base URL for all notices
            URI invitationURI;
            try {
                invitationURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
                invitationURI = CalendarService.adaptUriToScn(invitationURI);
            }
            catch(IllegalArgumentException e){
                throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
            }
            invitationURI = UriHelper.trimAtLast(invitationURI, PATH_SEGMENT_SEPERATOR +PATH_SEGMENT_EVENTS);
            invitationURI = UriHelper.appendPathSegment(invitationURI, PATH_SEGMENT_NOTICES);
            
            // Get the iCalendar representation for each new invitations
            
            ICalendarStore store = StoreFactory.getEventStore();
            String iCalendarNotices[] = store.getUnappliedNotices(eventId);
            
            // Convert to a JSON entity
            if (iCalendarNotices != null)
                jsonEntity = EventNoticeCollectionResource.generateJsonNoticeList(store, iCalendarNotices, invitationURI);
        }
        catch(StoreException e) {
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
        
        ResponseBuilder builder = Response.ok();
        if (jsonEntity != null)
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getEventNotices"); // $NON-NLS-1$
        
        return response;
    }

    /**
     * Generates a JSON representation for a list of notices.
     * 
     * @param store
     * @param iCalendarNotices
     * @param baseUrl
     * @return
     * @throws IOException
     * @throws StoreException
     * @throws ParserException
     * @throws JsonException 
     */
    public static String generateJsonNoticeList(ICalendarStore store, String iCalendarNotices[], URI baseUrl) throws IOException, StoreException, ParserException, JsonException {
        
        StringBuilder sb = new StringBuilder();
        Generator generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
        generator.out("{");
        generator.nl();
        generator.incIndent();

        generator.indent();
        generator.outPropertyName(JSON_NOTICES);
        generator.out(":[");
        generator.nl();
        generator.incIndent();
        
        if ( iCalendarNotices != null && iCalendarNotices.length > 0 ) {
            for ( int i = 0; i < iCalendarNotices.length; i++ ) {
                String icalendar = iCalendarNotices[i];
                
                // Get the event from iCalendar

                StringReader reader = new StringReader(icalendar);
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(reader);
                VEvent event = null;
                
                if ( calendar.getComponents() != null ) {
                    Iterator<?> iterator = calendar.getComponents().iterator();
                    while (iterator.hasNext()) {
                        Object component = iterator.next();
                        if ( component instanceof VEvent ) {
                            event = (VEvent)component;
                            break;
                        }
                    }
                }
                
                if ( event == null ) {
                    continue;
                }
                
                // Start outputting a new event
                
                generator.indent();
                generator.out("{");
                generator.nl();
                generator.incIndent();

                // Output summary
                
                String summary = null;
                if ( event.getSummary() != null ) {
                    summary = event.getSummary().getValue();
                }
                
                generator.indent();
                generator.outPropertyName(JSON_SUMMARY);
                generator.out(":");
                if ( summary == null ) {
                    generator.outNull();
                }
                else { 
                    generator.outStringLiteral(summary);
                }
                generator.out(",");
                generator.nl();

                // Output schedule method
                
                String method = null;
                if ( calendar.getMethod() != null ) {
                    method = calendar.getMethod().getValue().toLowerCase();
                }
                
                generator.indent();
                generator.outPropertyName(JSON_SCHEDULE_METHOD);
                generator.out(":");
                if ( method == null ) {
                    generator.outNull();
                }
                else {
                    generator.outStringLiteral(method);
                }
                generator.out(",");
                generator.nl();
                
                // Output HREF
                
                String id = null;
                Property idProp = event.getProperty("X-LOTUS-UNID"); // $NON-NLS-1$
                if ( idProp != null ) {
                    id = idProp.getValue();
                }
                
                generator.indent();
                generator.outPropertyName(JSON_HREF);
                generator.out(":");
                URI uri = UriHelper.appendPathSegment(baseUrl, id);
                generator.outStringLiteral(uri.toString());
                generator.nl();

                // Close this notice
                
                generator.decIndent();
                generator.indent();
                generator.out("}");
                if ( i != (iCalendarNotices.length - 1) ) {
                    generator.out(",");
                }
                generator.nl();
            }
        }

        generator.decIndent();
        generator.indent();
        generator.out("]");
        
        generator.decIndent();
        generator.nl();
        generator.indent();
        generator.out("}");

        return sb.toString();
    }

}