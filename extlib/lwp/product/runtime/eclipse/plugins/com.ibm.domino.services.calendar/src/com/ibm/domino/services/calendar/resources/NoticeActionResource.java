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

import static com.ibm.domino.commons.model.IGatekeeperProvider.FEATURE_REST_API_CALENDAR_NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.CALENDAR_SERVICE_LOGGER;
import static com.ibm.domino.services.calendar.service.CalendarService.NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.PATH_PARAM_NOTICE;
import static com.ibm.domino.services.calendar.service.CalendarService.STAT_MISC;
import static com.ibm.domino.services.calendar.service.CalendarService.URL_PARAM_ACTION_TYPE;

import java.text.ParseException;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.domino.calendar.store.Action;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

@Path("calendar/notices/" + PATH_PARAM_NOTICE + "/action") // $NON-NLS-1$ $NON-NLS-2$
public class NoticeActionResource {
    
    @PUT
    public Response putNoticeAction(String requestEntity, @HeaderParam("Content-Type") String contentType,  // $NON-NLS-1$
                        @Context UriInfo uriInfo, @PathParam(NOTICE) String id,
                        @QueryParam(URL_PARAM_ACTION_TYPE) String type) {
        
        CALENDAR_SERVICE_LOGGER.traceEntry(this, "putNoticeAction"); // $NON-NLS-1$
        CalendarService.beforeRequest(FEATURE_REST_API_CALENDAR_NOTICE, STAT_MISC);

        CalendarService.verifyDatabaseContext();

        if ( type == null || type.length() == 0 ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Action type not specified.", Status.BAD_REQUEST)); // $NLX-NoticeActionResource.Actiontypenotspecified-1$
        }
        
        int actionType = Utils.translateActionType(type);
        if ( actionType == -1 ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Unknown action type.", Status.BAD_REQUEST)); // $NLX-NoticeActionResource.Unknownactiontype-1$
        }
        
        try {
            Action action = Utils.createAction(actionType, contentType, requestEntity);
            StoreFactory.getEventStore().processNoticeAction(id, action);
        } 
        catch (StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse("Error processing notice action.", Utils.mapStatus(e), Utils.getExtraProps(e))); // $NLX-NoticeActionResource.Errorprocessingnoticeaction-1$
        } 
        catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Error processing notice action.", Status.BAD_REQUEST)); // $NLX-NoticeActionResource.Errorprocessingnoticeaction.1-1$
        } 
        catch (ParseException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Error processing notice action.", Status.BAD_REQUEST)); // $NLX-NoticeActionResource.Errorprocessingnoticeaction.2-1$
        }

        ResponseBuilder builder = Response.ok();
        Response response = builder.build();

        CALENDAR_SERVICE_LOGGER.traceExit(this, "putNoticeAction", response); // $NON-NLS-1$
        return response;
    }
    
}