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

package com.ibm.domino.services.calendar.service;

import java.util.HashSet;
import java.util.Set;

import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;
import com.ibm.domino.calendar.store.Action;
import com.ibm.domino.das.service.RestService;
import com.ibm.domino.osgi.core.context.ContextInfo;
import com.ibm.domino.services.calendar.resources.CalendarCollectionResource;
import com.ibm.domino.services.calendar.resources.EventActionResource;
import com.ibm.domino.services.calendar.resources.EventCollectionResource;
import com.ibm.domino.services.calendar.resources.EventNoticeCollectionResource;
import com.ibm.domino.services.calendar.resources.EventResource;
import com.ibm.domino.services.calendar.resources.InvitationCollectionResource;
import com.ibm.domino.services.calendar.resources.NoticeActionResource;
import com.ibm.domino.services.calendar.resources.NoticeResource;

public class CalendarService extends RestService {
    
    public static final LogMgr CALENDAR_SERVICE_LOGGER = Log.load("com.ibm.domino.services.calendar"); // $NON-NLS-1$

    public static final String WORKSPACE_TITLE = "Calendar Service"; // $NON-NLS-1$
    
    public static final String EVENT = "event"; // $NON-NLS-1$
    public static final String INSTANCE = "instance"; // $NON-NLS-1$
    public static final String NOTICE = "notice"; // $NON-NLS-1$
    
    public static final String PATH_PARAM_EVENT = "{" + EVENT + "}";
    public static final String PATH_PARAM_INSTANCE = "{" + INSTANCE + "}";
    public static final String PATH_PARAM_NOTICE = "{" + NOTICE + "}";
    
    public static final String FORMAT_ATOM = "atom"; // $NON-NLS-1$
    public static final String FORMAT_JSON = "json"; // $NON-NLS-1$
    public static final String FORMAT_ICALENDAR = "icalendar"; // $NON-NLS-1$
    
    public static final String URL_PARAM_FORMAT = "format"; // $NON-NLS-1$
    public static final String URL_PARAM_FORMAT_ATOM = URL_PARAM_FORMAT + "=" + FORMAT_ATOM;
    public static final String URL_PARAM_FORMAT_JSON = URL_PARAM_FORMAT + "=" + FORMAT_JSON;
    public static final String URL_PARAM_FORMAT_ICALENDAR = URL_PARAM_FORMAT + "=" + FORMAT_ICALENDAR;
    
    public static final String URL_PARAM_ACTION_TYPE = "type"; // $NON-NLS-1$
    public static final String URL_PARAM_WORKFLOW = "workflow"; // $NON-NLS-1$
    public static final String URL_PARAM_WORKFLOWCOMMENT = "workflowcomment"; // $NON-NLS-1$
    public static final String URL_PARAM_RECURRENCERANGE = "recurrenceRange"; // $NON-NLS-1$
    public static final String URL_PARAM_LITERALLY = "literally"; // $NON-NLS-1$
    public static final String URL_PARAM_STRICT_SEQUENCE = "strictsequence"; // $NON-NLS-1$
    
    public static final String URL_PARAM_SINCE = "since"; // $NON-NLS-1$
    public static final String URL_PARAM_BEFORE = "before"; // $NON-NLS-1$
    public static final String URL_PARAM_RECEIVED_SINCE = "receivedsince"; // $NON-NLS-1$
    public static final String URL_PARAM_ID = "id"; // $NON-NLS-1$
    public static final String URL_PARAM_PAGE_SIZE = "ps"; // $NON-NLS-1$
    public static final String URL_PARAM_PAGE = "page"; // $NON-NLS-1$
    public static final String URL_PARAM_COUNT = "count"; // $NON-NLS-1$
    public static final String URL_PARAM_START = "start"; // $NON-NLS-1$
    public static final String URL_PARAM_FIELDS = "fields"; // $NON-NLS-1$

    public static final String PATH_SEGMENT_API = "api"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_CALENDAR = "calendar"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_INVITATIONS = "invitations"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_NOTICES = "notices"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_EVENTS = "events"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_INSTANCES = "instances"; // $NON-NLS-1$
    public static final String PATH_SEGMENT_SEPERATOR = "/";   //$NON-NLS-1$

    public static final String NOTICE_ACTION_ACCEPT = "accept"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_CANCEL = "cancel"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_COUNTER = "counter"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_DECLINE = "decline"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_DELEGATE = "delegate"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_DELETE = "delete"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_REQUEST_INFO = "requestInfo";   //$NON-NLS-1$
    public static final String NOTICE_ACTION_TENTATIVE = "tentative"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_PROCESS_ALL = "processAll"; // $NON-NLS-1$
    public static final String NOTICE_ACTION_REMOVE_CANCELLED = "removeCancelled";   //$NON-NLS-1$
    
    public static final String STAT_VIEW_EVENTS = "ViewEvents"; // $NON-NLS-1$
    public static final String STAT_CREATE_EVENT = "CreateEvent"; // $NON-NLS-1$
    public static final String STAT_READ_EVENT = "ReadEvent"; // $NON-NLS-1$
    public static final String STAT_UPDATE_EVENT = "UpdateEvent"; // $NON-NLS-1$
    public static final String STAT_DELETE_EVENT = "DeleteEvent"; // $NON-NLS-1$
    public static final String STAT_VIEW_INVITATIONS = "ViewInvitations"; // $NON-NLS-1$
    public static final String STAT_READ_NOTICE = "ReadNotice"; // $NON-NLS-1$
    public static final String STAT_MISC = "Misc"; // $NON-NLS-1$
    
    public static final String MEDIA_TYPE_ICALENDAR = "text/calendar"; // $NON-NLS-1$
    
    public static final String ICALENDAR_PRODID = "-//IBM Corporation//NONSGML Domino Calendar Service 9.0//EN"; // $NON-NLS-1$

    private static Boolean s_useRelativeUrls = null;
    
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(CalendarCollectionResource.class);
        classes.add(EventCollectionResource.class);
        classes.add(EventNoticeCollectionResource.class);
        classes.add(EventResource.class);
        classes.add(InvitationCollectionResource.class);
        classes.add(NoticeActionResource.class);
        classes.add(NoticeResource.class);

        return classes;
    }

    public static boolean isUseRelativeUrls() {
        
        boolean useRelativeUrls = true;
        
        try {
            Session session = ContextInfo.getUserSession();
            if ( s_useRelativeUrls == null && session != null ) {
                // One time intialization
                String value = session.getEnvironmentString("CalendarServiceAbsoluteUrls", true); // $NON-NLS-1$
                if ( "1".equals(value) ) {
                    useRelativeUrls = false;
                }
                
                s_useRelativeUrls = new Boolean(useRelativeUrls);
            } 
        }
        catch (NotesException e) {
            // Ignore this
        }
        
        if ( s_useRelativeUrls != null ) {
            useRelativeUrls = s_useRelativeUrls;
        }
        
        return useRelativeUrls;
    }
}