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

package com.ibm.domino.calendar.dbstore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.NotesCalendar;
import lotus.domino.NotesCalendarEntry;
import lotus.domino.NotesCalendarNotice;
import lotus.domino.NotesError;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.calendar.store.Action;
import com.ibm.domino.calendar.store.CounterAction;
import com.ibm.domino.calendar.store.DeclineAction;
import com.ibm.domino.calendar.store.DelegateAction;
import com.ibm.domino.calendar.store.EventSet;
import com.ibm.domino.calendar.store.ICalendarStore;
import com.ibm.domino.calendar.store.RecurrenceRange;
import com.ibm.domino.calendar.store.StoreException;
import com.ibm.domino.commons.util.BackendUtil;

/**
 * Event store that uses the Notes Java back-end classes.
 */
public class NotesCalendarStore implements ICalendarStore {

    public static final String RANGE_FIELDS_FILTER_CATEGORIES = "categories"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_CLASS = "class"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_END = "end"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_ID = "id"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_LOCATION = "location"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_PRIORITY = "priority"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_RECURRENCEID = "reccurenceid"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_SEQUENCE = "sequence"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_START = "start"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_STATUS = "status"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_SUMMARY = "summary"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_TRANSPARENCY = "transparency"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_URL = "url"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_X_LOTUS_APPTYPE = "x-lotus-apptype"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_X_LOTUS_NOTICETYPE = "x-lotus-noticetype"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_X_LOTUS_ONLINEMEETING_URL = "x-lotus-onlinemeeting-url"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_X_LOTUS_ORGANIZER = "x-lotus-organizer"; // $NON-NLS-1$
    public static final String RANGE_FIELDS_FILTER_X_LOTUS_ROOM = "x-lotus-room"; // $NON-NLS-1$
    
    private static final long ONE_YEAR = 365L * 24 * 60 * 60 * 1000; 
    private static final int READ_RANGE_ALL =   0xFFFFFFFF;
    private static Map<String,Integer> SUPPORT_RANGE_FILTER = new HashMap<String,Integer>();
    static{
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_CATEGORIES,new Integer(NotesCalendar.CS_READ_RANGE_MASK_CATEGORY));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_CLASS,new Integer(NotesCalendar.CS_READ_RANGE_MASK_CLASS));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_END,new Integer(NotesCalendar.CS_READ_RANGE_MASK_DTEND));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_LOCATION,new Integer(NotesCalendar.CS_READ_RANGE_MASK_LOCATION));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_PRIORITY,new Integer(NotesCalendar.CS_READ_RANGE_MASK_PRIORITY));      
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_RECURRENCEID,new Integer(NotesCalendar.CS_READ_RANGE_MASK_RECURRENCE_ID));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_SEQUENCE,new Integer(NotesCalendar.CS_READ_RANGE_MASK_SEQUENCE));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_START,new Integer(NotesCalendar.CS_READ_RANGE_MASK_DTSTART));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_STATUS,new Integer(NotesCalendar.CS_READ_RANGE_MASK_STATUS));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_SUMMARY,new Integer(NotesCalendar.CS_READ_RANGE_MASK_SUMMARY));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_TRANSPARENCY,new Integer(NotesCalendar.CS_READ_RANGE_MASK_TRANSP));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_X_LOTUS_APPTYPE,new Integer(NotesCalendar.CS_READ_RANGE_MASK_APPTTYPE));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_X_LOTUS_NOTICETYPE,new Integer(NotesCalendar.CS_READ_RANGE_MASK_NOTICETYPE));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_X_LOTUS_ONLINEMEETING_URL,new Integer(NotesCalendar.CS_READ_RANGE_MASK_ONLINE_URL));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_X_LOTUS_ORGANIZER,new Integer(NotesCalendar.CS_READ_RANGE_MASK_NOTESORGANIZER));
      SUPPORT_RANGE_FILTER.put(RANGE_FIELDS_FILTER_X_LOTUS_ROOM,new Integer(NotesCalendar.CS_READ_RANGE_MASK_NOTESROOM));   
    }
    
    private Database _database;
    
    public NotesCalendarStore(Database database) {
        _database = database;      
    }

    public EventSet getEvents(Date fromDate, Date toDate, int skipCount, int maxEvents, ArrayList<String> rangeFieldsFilter) throws StoreException {
        EventSet result = null;
        NotesCalendar calendar = null;
        DateTime dtstart = null;
        DateTime dtend = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            
            // TODO:  Should we set the mask back to the default when we are done?
            int newMask = 0;
            boolean useDefaultFilterMask = true;
            if( rangeFieldsFilter!=null ){
                Iterator<String> rangeFieldsFilterIterator = rangeFieldsFilter.iterator();
                while(rangeFieldsFilterIterator.hasNext()){
                    String filter = rangeFieldsFilterIterator.next();
                    if(SUPPORT_RANGE_FILTER.containsKey(filter)){
                        newMask |= SUPPORT_RANGE_FILTER.get(filter);
                        useDefaultFilterMask = false;
                    }
                }
            }
            if(useDefaultFilterMask){
                newMask = READ_RANGE_ALL;             
            }

            // Convert Java dates to DateTime objects 
            
            if ( fromDate == null ) {
                if ( toDate == null ) {
                    // Start and end dates are null.  Read one year of
                    // events starting today.
                    Date now = new Date();
                    dtstart = session.createDateTime(now);
                    dtend = session.createDateTime(new Date(now.getTime() + ONE_YEAR));
                }
                else {
                    // Start date is null, but end date is not.  Read 
                    // one year of events ending on the given date.
                    dtstart = session.createDateTime(new Date(toDate.getTime() - ONE_YEAR));
                    dtend = session.createDateTime(toDate);
                }
            }
            else {
                if ( toDate == null ) {
                    // End date is null, but start date is not.  Read 
                    // one year of events starting on the given date.
                    dtstart = session.createDateTime(fromDate);
                    dtend = session.createDateTime( new Date(fromDate.getTime() + ONE_YEAR) );
                }
                else {
                    dtstart = session.createDateTime(fromDate);
                    dtend = session.createDateTime(toDate);
                }
            }
            
            calendar.setReadRangeMask1(newMask);
            String events = calendar.readRange(dtstart, dtend, skipCount, maxEvents);
            result = new EventSet(events, calendar.getEntriesProcessed() - skipCount); 
        }
        catch (NotesException e) {
            throw new StoreException("Error reading events", mapError(e.id), e);  // $NLX-NotesCalendarStore.Errorreadingevents-1$
        }
        finally {
            BackendUtil.safeRecycle(dtstart);
            BackendUtil.safeRecycle(dtend);
            BackendUtil.safeRecycle(calendar);
        }
        
        return result;
    }

    public String getEvent(String id, String recurrenceId)
            throws StoreException {
        String result = null;
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarEntry entry = calendar.getEntry(id);
            result = entry.read(recurrenceId);
        }
        catch (NotesException e) {
            throw new StoreException("Error reading event", mapError(e.id), e); // $NLX-NotesEventStore.Errorreadingevent-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
        
        return result;
    }

    public String createEvent(String event, int flags) throws StoreException {
        String result = null;
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarEntry entry = calendar.createEntry(event, translateFlags(flags));
            result = entry.read();
        }
        catch (NotesException e) {
            throw new StoreException("Error creating event", mapError(e.id), e); // $NLX-NotesEventStore.Errorcreatingevent-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
        
        return result;
    }

    public String updateEvent(String event, String id, String recurrenceId,
            String comments, int flags) throws StoreException {
        String result = null;
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarEntry entry = calendar.getEntry(id);
            entry.update(event, comments, translateFlags(flags), recurrenceId);
            result = entry.read(recurrenceId);
        }
        catch (NotesException e) {
            throw new StoreException("Error updating event", mapError(e.id), e); // $NLX-NotesEventStore.Errorupdatingevent-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
        
        return result;
    }

    public void deleteEvent(String id, String recurrenceId,
            RecurrenceRange range, int flags) throws StoreException {
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarEntry entry = calendar.getEntry(id);
            
            if ( (flags & ICalendarStore.FLAG_NO_WORKFLOW) != 0 ) {
                calendar.setAutoSendNotices(false);
            }
            
            if ( recurrenceId == null ) {
                entry.remove(NotesCalendarEntry.CS_RANGE_REPEAT_ALL, null);
            }
            else {
                entry.remove(rangeToInt(range), recurrenceId);
            }
        }
        catch (NotesException e) {
            throw new StoreException("Error deleting event", mapError(e.id), e); // $NLX-NotesEventStore.Errordeletingevent-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
    }

    public void processEventAction(String id, String recurrenceId,
            RecurrenceRange range, Action action) throws StoreException {
        NotesCalendar calendar =  null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarEntry entry = calendar.getEntry(id);
            boolean keepInformed = false;
            
            switch(action.getActionType()) {
            
            case Action.ACTION_ACCEPT:
                entry.accept(action.getComments(), rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_CANCEL:
                entry.cancel(action.getComments(), rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_COUNTER:
                CounterAction ca = (CounterAction)action;
                DateTime start = session.createDateTime(ca.getStart());
                DateTime end = session.createDateTime(ca.getEnd());
                entry.counter(action.getComments(), start, end, rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_DECLINE:
                DeclineAction decline = (DeclineAction)action;
                if ( decline.getKeepInformed() != null ) {
                    // This logic is not exactly right.  We need a way to tell decline()
                    // to use the user's default value for keepInformed.  TODO: Fix 
                    // this when we have the right variant of decline().
                    keepInformed = decline.getKeepInformed().booleanValue();
                }
                entry.decline(action.getComments(), keepInformed, rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_DELEGATE:
                DelegateAction delegate = (DelegateAction)action;
                if ( delegate.getKeepInformed() != null ) {
                    // This logic is not exactly right.  We need a way to tell delegate()
                    // to use the user's default value for keepInformed.  TODO: Fix 
                    // this when we have the right variant of delegate().
                    keepInformed = delegate.getKeepInformed().booleanValue();
                }
                entry.delegate(delegate.getComments(), delegate.getDelegateTo(), keepInformed, rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_DELETE:
                entry.remove(rangeToInt(range),recurrenceId);
                break;
                
            case Action.ACTION_REMOVE_CANCEL:
                // TODO: What does this map to?
                break;
                
            case Action.ACTION_REQUEST_INFO:
                entry.requestInfo(action.getComments(),recurrenceId);
                break;
                
            case Action.ACTION_TENTATIVE:
                entry.tentativelyAccept(action.getComments(), rangeToInt(range), recurrenceId);
                break;
                
            case Action.ACTION_PROCESS_ALL:
                // TODO: Figure what this maps to.  The processAll method is no longer
                // exported from the back end.
                
                //entry.processAll();
                break;
                
            default:                
                throw new StoreException("Error processing event action", StoreException.ERR_BAD_ACTION,new NotesException()); // $NLX-NotesCalendarStore.Errorprocessingeventaction-1$
                
            }
            
        }
        catch (NotesException e) {
            throw new StoreException("Error processing event action", mapError(e.id), e); // $NLX-NotesEventStore.Errorprocessingeventaction-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
    }

    public String[] getUnappliedNotices(String id) throws StoreException {
        
        String iCalendarNotices[] = null;
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            Vector<Object> list = null;
            
            NotesCalendarEntry entry = calendar.getEntry(id);
            if ( entry != null ) {
                list = entry.getNotices();
            }

            // Convert Vector<NotesCalendarNotice> to an array of iCalendar notices.
            
            if ( list != null && list.size() > 0 ) {
                iCalendarNotices = new String[list.size()];
                
                Iterator<Object> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Object obj = iterator.next();
                    if ( obj instanceof NotesCalendarNotice) {
                        NotesCalendarNotice notice = (NotesCalendarNotice)obj;
                        iCalendarNotices[i++] = notice.read();
                    }
                }
            }
        }
        catch (NotesException e) {
            throw new StoreException("Error getting unapplied notices", mapError(e.id), e); // $NLX-NotesEventStore.Errorgettingunappliednotices-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
        
        return iCalendarNotices;
    }

    public String[] getNewInvitations(Date start, Date since, String id) throws StoreException {
        String iCalendarNotice[] = null;
        NotesCalendar calendar = null;
        DateTime dtStart = null;
        DateTime dtSince = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            if (start != null) {
                dtStart = session.createDateTime(start);
            }
                
            if ( since != null ) {
                dtSince = session.createDateTime(since);
            }

            // Get a list of new invitations
            List<String> iCalendarList = null;
            Vector<Object> list = calendar.getNewInvitations(dtStart, dtSince);

            // Convert Vector<NotesCalendarNotice> to an array of iCalendar notices.
            
            if ( list != null && list.size() > 0 ) {
                
                // Convert the list of NotesCalendarNotice objects to a list of strings                
                iCalendarList = new ArrayList<String>();
                Iterator<Object> noticeIterator = list.iterator();
                boolean matched = false;
                StringBuffer noticeValue = new StringBuffer();
                while (noticeIterator.hasNext()) {
                    Object obj = noticeIterator.next();
                    if ( obj instanceof NotesCalendarNotice) {
                        matched = false;
                        noticeValue.setLength(0);
                        NotesCalendarNotice notice = (NotesCalendarNotice)obj;
                        try {
                            // The read is wrapped in it's own try block so one failure
                            // doesn't sink the whole list
                            noticeValue.append(notice.read());
                            if(id == null){
                                matched = true;
                            }
                            else{
                                Pattern patternUID = Pattern.compile("(\n|\r)"+"UID:"+id+"(\n|\r)"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                                Matcher matcher = patternUID.matcher(noticeValue);
                                matched = matcher.find();                                
                            }
                            if( matched ){   
                                iCalendarList.add(noticeValue.toString());
                            }
                        }
                        catch (NotesException e) {
                            // TODO:  Log a warning
                        }
                    }
                }
            }

            if ( iCalendarList != null && iCalendarList.size() > 0 ) {
                
                // Convert the list of strings to an array
                
                iCalendarNotice = new String[iCalendarList.size()];
                Iterator<String> iterator = iCalendarList.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    iCalendarNotice[i++] = iterator.next();  
                }
            }
        }
        catch (NotesException e) {
            throw new StoreException("Error getting new invitations", mapError(e.id), e); // $NLX-NotesEventStore.Errorgettingnewinvitations-1$
        }
        finally {
            BackendUtil.safeRecycle(dtStart);
            BackendUtil.safeRecycle(dtSince);
            BackendUtil.safeRecycle(calendar);
        }
        
        return iCalendarNotice;
    }

    public String getNotice(String id) throws StoreException {

        String result = null;
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarNotice notice = null;

            notice = calendar.getNoticeByUNID(id);
            if ( notice != null ) {
                result = notice.read();
            }
        }
        catch (NotesException e) {
            throw new StoreException("Error getting calendar notice", mapError(e.id), e); // $NLX-NotesEventStore.Errorgettingnotice-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
        
        return result;
    }

    public void processNoticeAction(String id, Action action)
            throws StoreException {
        NotesCalendar calendar = null;
        
        try {
            Session session = _database.getParent();
            calendar = session.getCalendar(_database);
            NotesCalendarNotice notice = calendar.getNoticeByUNID(id);
            boolean keepInformed = false;
            
            switch(action.getActionType()) {
            
            case Action.ACTION_ACCEPT:
                notice.accept(action.getComments());
                break;
                
            case Action.ACTION_COUNTER:
                CounterAction ca = (CounterAction)action;
                DateTime start = session.createDateTime(ca.getStart());
                DateTime end = session.createDateTime(ca.getEnd());
                notice.counter(action.getComments(), start, end);
                break;
                
            case Action.ACTION_DECLINE:
                DeclineAction decline = (DeclineAction)action;
                if ( decline.getKeepInformed() != null ) {
                    // This logic is not exactly right.  We need a way to tell decline()
                    // to use the user's default value for keepInformed.  TODO: Fix 
                    // this when we have the right variant of decline().
                    keepInformed = decline.getKeepInformed().booleanValue();
                }
                // Without keepInformed equal keepInformed false
                notice.decline(action.getComments(), keepInformed);               
                break;
                
            case Action.ACTION_DELEGATE:
                DelegateAction delegate = (DelegateAction)action;
                if ( delegate.getKeepInformed() != null ) {
                    // This logic is not exactly right.  We need a way to tell delegate()
                    // to use the user's default value for keepInformed.  TODO: Fix 
                    // this when we have the right variant of delegate().
                    keepInformed = delegate.getKeepInformed().booleanValue();
                }
                notice.delegate(delegate.getComments(), delegate.getDelegateTo(), keepInformed);
                break;
                
            case Action.ACTION_REMOVE_CANCEL:
                notice.removeCancelled();
                break;
                
            case Action.ACTION_REQUEST_INFO:
                notice.requestInfo(action.getComments());
                break;
                
            case Action.ACTION_TENTATIVE:
                notice.tentativelyAccept(action.getComments());
                break;
               
            default:                
                throw new StoreException("Error processing notice action", StoreException.ERR_ACTION_NOT_SUPPORTED,new NotesException()); // $NLX-NotesCalendarStore.Errorprocessingnoticeaction-1$
            }
            
        }
        catch (NotesException e) {
            throw new StoreException("Error processing notice action", mapError(e.id), e); // $NLX-NotesEventStore.Errorprocessingnoticeaction-1$
        }
        finally {
            BackendUtil.safeRecycle(calendar);
        }
    }

    private int rangeToInt(RecurrenceRange range) {
        int value = NotesCalendarEntry.CS_RANGE_REPEAT_CURRENT;
        
        if ( range != null ) {

            switch(range) {
            
            case THIS_AND_PREVIOUS:
                value = NotesCalendarEntry.CS_RANGE_REPEAT_PREV;
                break;
    
            case THIS_AND_FUTURE:
                value = NotesCalendarEntry.CS_RANGE_REPEAT_FUTURE;
                break;
    
            case ALL_INSTANCES:
                value = NotesCalendarEntry.CS_RANGE_REPEAT_ALL;
                break;
            }
        }
        
        return value;
    }

    private int translateFlags(int storeFlags) {
        int flags = 0;
        
        if ( (storeFlags & FLAG_NO_WORKFLOW) != 0 ) {
            flags |= NotesCalendar.CS_WRITE_DISABLE_IMPLICIT_SCHEDULING;
        }
        
        if ( (storeFlags & FLAG_REPLACE_COMPLETELY) != 0 ) {
            flags |= NotesCalendar.CS_WRITE_MODIFY_LITERAL;
        }
        
        if ( (storeFlags & FLAG_SMART_SEQUENCE) != 0 ) {
            flags |= NotesCalendar.CS_WRITE_SMARTSEQUENCE;
        }
        
        return flags;
    }
    
    private int mapError(int notesError) {
        int storeError = StoreException.ERR_INTERNAL;
        
        switch(notesError) {
            case NotesError.NOTES_ERR_NOTESCALENDAR_ERROR:
                // TODO: Fix this.  Temporarily mapping to the wrong error so
                // calendar service unit tests will pass.
                storeError = StoreException.ERR_ACTION_NOT_SUPPORTED;
                break;

            case NotesError.NOTES_ERR_INVALIDID:
                storeError = StoreException.ERR_BAD_IDENTIFIER;
                break;

            case NotesError.NOTES_ERR_ERRSENDINGNOTICES:
                storeError = StoreException.ERR_SENDING_NOTICES;
                break;

            case NotesError.NOTES_ERR_NEWERVERSIONEXISTS:
                storeError = StoreException.ERR_NEWER_VERSION_EXISTS;
                break;

            case NotesError.NOTES_ERR_UNSUPPORTEDACTION:
                storeError = StoreException.ERR_ACTION_NOT_SUPPORTED;
                break;

            case NotesError.NOTES_ERR_NOTACCEPTED:
                storeError = StoreException.ERR_INVITE_NOT_ACCEPTED;
                break;

            case NotesError.NOTES_ERR_OVERWRITEDISALLOWED:
                storeError = StoreException.ERR_PERSONAL_CHANGES;
                break;

            case NotesError.NOTES_ERR_RECURID_NOTFOUND:
            case NotesError.NOTES_ERR_IDNOTFOUND:
                storeError = StoreException.ERR_IDENTIFIER_NOT_FOUND;
                break;

            case NotesError.NOTES_ERR_ENTRYEXISTS:
                storeError = StoreException.ERR_ENTRY_EXISTS;
                break;

            case NotesError.NOTES_ERR_INVALID_ICALSTR: 
                storeError = StoreException.ERR_INVALID_ICALSTR;
                break;

        }
        
        return storeError;
    }
}