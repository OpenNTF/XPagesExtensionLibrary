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

import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.FORMAT_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.MEDIA_TYPE_ICALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_PARAM_NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_READ_NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_FORMAT;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;

import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.das.utils.StatsContext;
import com.ibm.domino.services.calendar.json.JsonCalendarGenerator;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

@Path("calendar/notices/" + PATH_PARAM_NOTICE) // $NON-NLS-1$
public class NoticeResource {
    
    @GET
    public Response getNotice(@Context UriInfo uriInfo, @PathParam(NOTICE) String id,
                        @QueryParam(URL_PARAM_FORMAT) String format) {
        
        String jsonEntity = null;
        String eventString = null;

        StatsContext.getCurrentInstance().setRequestCategory(STAT_READ_NOTICE);
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getNotice"); // $NON-NLS-1$

        CalendarService.verifyDatabaseContext();

        try {
            // Get the notice
            eventString = StoreFactory.getEventStore().getNotice(id);
            
            if ( ! FORMAT_ICALENDAR.equalsIgnoreCase(format) ) {
                // Parse the iCalendar into JSON format

                StringReader reader = new StringReader(eventString);
                StringBuilder sb = new StringBuilder();
                CalendarParser parser = CalendarParserFactory.getInstance().createParser();
                URI baseURI;
                try {
                    baseURI = UriHelper.copy(uriInfo.getAbsolutePath(),CalendarService.isUseRelativeUrls());
                    baseURI = CalendarService.adaptUriToScn(baseURI);
                }
                catch(IllegalArgumentException e){
                    throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
                }
                parser.parse(new UnfoldingReader(reader, true), new JsonCalendarGenerator(sb, baseURI, true));

                jsonEntity = sb.toString();
            }
        }
        catch (StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse(e, Utils.mapStatus(e), Utils.getExtraProps(e)));
        } catch (IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        } catch (ParserException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        
        ResponseBuilder builder = Response.ok();
        if ( jsonEntity != null ) {
            builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        }
        else {
            builder.type(MEDIA_TYPE_ICALENDAR).entity(eventString);
        }
        
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getNotice", response); // $NON-NLS-1$
        
        return response;
    }
}