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

import java.text.ParseException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.domino.calendar.store.Action;
import com.ibm.domino.calendar.store.RecurrenceRange;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.services.calendar.service.CalendarService;
import com.ibm.domino.services.calendar.store.StoreFactory;
import com.ibm.domino.services.calendar.util.Utils;

public class EventActionResource {
    
    public static void putEventActionInternal(String requestEntity, String contentType, String id,
            String instanceId, String range, String type) {
        
        if ( type == null || type.length() == 0 ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Action type not specified.", Status.BAD_REQUEST)); // $NLX-EventActionResource.Actiontypenotspecified-1$
        }
        
        int actionType = Utils.translateActionType(type);
        if ( actionType == -1 ) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Unknown action type.", Status.BAD_REQUEST)); // $NLX-EventActionResource.Unknownactiontype-1$
        }
        
        RecurrenceRange rr = null;
        if ( StringUtil.isNotEmpty(instanceId) ) {            
            rr = Utils.translateRecurrenceRange(range);
        }
        
        try {
            Action action = Utils.createAction(actionType, contentType, requestEntity);
            StoreFactory.getEventStore().processEventAction(id, instanceId, rr, action);
        } 
        catch (StoreException e) {
            throw new WebApplicationException(ErrorHelper.createErrorResponse("Error processing event action.", Utils.mapStatus(e), Utils.getExtraProps(e))); // $NLX-EventActionResource.Errorprocessingeventaction-1$
        } 
        catch (JsonException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Error processing event action.", Status.BAD_REQUEST)); // $NLX-EventActionResource.Errorprocessingeventaction.1-1$
        } 
        catch (ParseException e) {
            throw new WebApplicationException(CalendarService.createErrorResponse("Error processing event action.", Status.BAD_REQUEST)); // $NLX-EventActionResource.Errorprocessingeventaction.2-1$
        }

    }
}