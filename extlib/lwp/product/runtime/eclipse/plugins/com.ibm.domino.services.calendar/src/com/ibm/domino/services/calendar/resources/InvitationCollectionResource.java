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

import static com.ibm.domino.commons.model.IGatekeeperProvider.FEATURE_REST_API_CALENDAR_INVITATION_LIST;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_INVITATIONS;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_NOTICES;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_SEPERATOR;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_VIEW_INVITATIONS;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_ID;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_RECEIVED_SINCE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_SINCE;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import net.fortuna.ical4j.data.ParserException;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.domino.calendar.store.ICalendarStore;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

@Path("calendar/invitations") // $NON-NLS-1$
public class InvitationCollectionResource {
    
    @GET
    public Response getInvitations(@Context UriInfo uriInfo, 
            @QueryParam(URL_PARAM_SINCE) String start,
            @QueryParam(URL_PARAM_RECEIVED_SINCE) String since,
            @QueryParam(URL_PARAM_ID) String id) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getInvitations"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_INVITATION_LIST, STAT_VIEW_INVITATIONS);

        CalendarService.verifyDatabaseContext();

        String jsonEntity = null;
        Date startDate = null;
        Date sinceDate = null;
        
        try {
            // Get the base URL for all notices
            URI invitationURI ;
            try{
            invitationURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
            }catch(IllegalArgumentException e){
                throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
            }
            invitationURI = UriHelper.trimAtLast(invitationURI, PATH_SEGMENT_SEPERATOR + PATH_SEGMENT_INVITATIONS);
            invitationURI = UriHelper.appendPathSegment(invitationURI, PATH_SEGMENT_NOTICES);
            invitationURI = CalendarService.adaptUriToScn(invitationURI);
            
            // Convert date strings to Date objects
            
            if ( start != null && start.length() > 0 ) {
                startDate = Utils.dateFromString(start);
            }
            
            if ( since != null && since.length() > 0 ) {
                sinceDate = Utils.dateFromString(since);
            }
            
            // Get the iCalendar representation for each new invitations
            
            ICalendarStore store = StoreFactory.getEventStore();
            String iCalendarNotices[] = store.getNewInvitations(startDate, sinceDate, id);
            
            // Convert to a JSON entity
            if(iCalendarNotices != null)
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
        catch (ParseException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
        }
        catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        
        ResponseBuilder builder = Response.ok();
        if (jsonEntity != null)
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getInvitations"); // $NON-NLS-1$
        
        return response;
    }
    
}