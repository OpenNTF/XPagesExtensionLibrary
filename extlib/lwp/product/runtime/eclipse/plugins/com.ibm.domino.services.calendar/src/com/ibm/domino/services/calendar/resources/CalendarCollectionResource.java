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

import static com.ibm.domino.commons.json.JsonConstants.DISPLAY_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.DISTINGUISHED_NAME_PROP;
import static com.ibm.domino.commons.json.JsonConstants.EMAIL_PROP;
import static com.ibm.domino.commons.json.JsonConstants.HOME_SERVER_PROP;
import static com.ibm.domino.das.service.RestService.URL_PARAM_OWNER;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_HREF;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_LINKS;
import static com.ibm.domino.services.calendar.json.JsonConstants.JSON_RELATIONSHIP;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_API;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_CALENDAR;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_EVENTS;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_SEGMENT_INVITATIONS;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_MISC;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import lotus.domino.Name;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.commons.util.UriHelper;
import com.ibm.domino.das.utils.ScnContext;
import com.ibm.domino.das.utils.StatsContext;
import com.ibm.domino.das.utils.UserHelper;
import com.ibm.domino.services.calendar.service.CalendarService;

@Path("calendar") // $NON-NLS-1$
public class CalendarCollectionResource {
    
    /**
     * Gets a list of calendars to which the authenticated user has access.
     * 
     * <p>For now this just returns the user's primary calendar.
     * 
     * @param uriInfo
     * @return
     */
    @GET
    public Response getCalendars(@Context UriInfo uriInfo,
                            @QueryParam(URL_PARAM_OWNER) String owner) {

        StatsContext.getCurrentInstance().setRequestCategory(STAT_MISC);
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "getCalendars"); // $NON-NLS-1$
        String jsonEntity = null;

        CalendarService.verifyNoDatabaseContext();
        
        try {

            String url = "";
            if ( !CalendarService.isUseRelativeUrls() ) {
                int index = uriInfo.getAbsolutePath().toString().indexOf("/api/"); // $NON-NLS-1$
                if ( index != -1 ) {
                    url = uriInfo.getAbsolutePath().toString().substring(0, index);
                }
            }
            
            UserHelper userHelper = null;
            if ( StringUtil.isEmpty(owner) ) {
                // Lookup the authenticated user's mail file
                userHelper = UserHelper.getUser(url);
            }
            else {
                // Lookup the named user's mail file
                userHelper = UserHelper.getNamedUser(owner, url);
            }
            
            // Write the output
            
            StringBuilder sb = new StringBuilder();
            Generator generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
            generator.out("{");
            generator.nl();
            generator.incIndent();

            generator.indent();
            generator.outPropertyName("calendars"); // $NON-NLS-1$
            generator.out(":[");
            generator.nl();
            generator.incIndent();
            
            writeCalendarObject(generator, userHelper);
            
            generator.decIndent();
            generator.nl();
            generator.indent();
            generator.out("]");
            
            generator.decIndent();
            generator.nl();
            generator.indent();
            generator.out("}");

            jsonEntity = sb.toString();
        }
        catch(NotesException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        catch(IOException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        catch(JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.INTERNAL_SERVER_ERROR));
        }
        
        ResponseBuilder builder = Response.ok();
        builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        
        Response response = builder.build();
        CALENDAR_SERVICE_LOGGER.traceExit(this, "getCalendars", response); // $NON-NLS-1$

        return response;
    }
    
    /**
     * Writes the JSON representation of a single calendar object.
     * 
     * @param generator
     * @param name
     * @param url
     * @throws IOException
     * @throws JsonException
     * @throws NotesException
     */
    private void writeCalendarObject(Generator generator, UserHelper helper) 
                    throws IOException, JsonException, NotesException {
        generator.indent();
        generator.out("{");
        generator.nl();
        generator.incIndent();
        
        generator.indent();
        generator.outPropertyName("owner"); // $NON-NLS-1$
        generator.out(":{");
        generator.nl();
        generator.incIndent();
        
        generator.indent();
        generator.outPropertyName(DISPLAY_NAME_PROP);
        generator.out(":");
        generator.outLiteral(helper.getUserName().getCommon());
        generator.out(",");
        generator.nl();
        
        generator.indent();
        generator.outPropertyName(DISTINGUISHED_NAME_PROP);
        generator.out(":");
        generator.outLiteral(helper.getUserName().getAbbreviated());
        generator.out(",");
        generator.nl();
        
        if ( !ScnContext.getCurrentInstance().isScn() ) {
            Name homeServerName = helper.getMailServerName();
            if ( homeServerName != null ) {
                generator.indent();
                generator.outPropertyName(HOME_SERVER_PROP);
                generator.out(":");
                generator.outLiteral(homeServerName.getAbbreviated());
                generator.out(",");
                generator.nl();
            }
        }
        generator.indent();
        generator.outPropertyName(EMAIL_PROP);
        generator.out(":");
        generator.outLiteral(helper.getEmailAddress());
        generator.nl();
        generator.decIndent();

        generator.indent();
        generator.out("},");
        generator.nl();
        
        // Start the array of links
        
        generator.indent();
        generator.outPropertyName(JSON_LINKS);
        generator.out(":[");
        generator.incIndent();
        generator.nl();
        
        URI calendarURI = null;
        if ( ScnContext.getCurrentInstance().isHideDbPath() ) {
            calendarURI = UriHelper.create(CalendarService.SERVICE_PATH, true);
        }
        else {
            URI baseURI;
            try{
                baseURI = UriHelper.create(helper.getUrl(), CalendarService.isUseRelativeUrls());
            }
            catch(IllegalArgumentException e){
                throw new WebApplicationException(CalendarService.createErrorResponse(e, Response.Status.BAD_REQUEST));
            }
            calendarURI = UriHelper.appendPathSegment(baseURI, PATH_SEGMENT_API);
        }
        calendarURI = UriHelper.appendPathSegment(calendarURI, PATH_SEGMENT_CALENDAR);
        
        // Add event link
        
        writeLinkObject(generator, "events", UriHelper.appendPathSegment(calendarURI, PATH_SEGMENT_EVENTS), true); // $NON-NLS-1$
        
        // Add invitations link
        
        writeLinkObject(generator, "invitations", UriHelper.appendPathSegment(calendarURI, PATH_SEGMENT_INVITATIONS), false); // $NON-NLS-1$
        
        // End the array of links
        
        generator.decIndent();
        generator.indent();
        generator.out("]");
        generator.nl();
        
        // End the calendar object

        generator.decIndent();
        generator.indent();
        generator.out("}");
    }
    
    private void writeLinkObject(Generator generator, String rel, URI url, boolean addComma) 
                        throws IOException, JsonException {
        
        generator.indent();
        generator.out("{");
        generator.incIndent();
        generator.nl();
        
        generator.indent();
        generator.outPropertyName(JSON_RELATIONSHIP);
        generator.out(":");
        generator.outLiteral(rel);
        generator.out(",");
        generator.nl();
        
        generator.indent();
        generator.outPropertyName(JSON_HREF);
        generator.out(":");
        generator.outLiteral(CalendarService.adaptUriToScn(url).toString());
        generator.nl();
        
        generator.decIndent();
        generator.indent();
        generator.out("}");
        if ( addComma ) {
            generator.out(",");
        }
        generator.nl();
    }
}