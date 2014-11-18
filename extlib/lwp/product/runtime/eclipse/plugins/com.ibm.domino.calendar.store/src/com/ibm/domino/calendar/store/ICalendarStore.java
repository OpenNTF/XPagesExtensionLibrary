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

package com.ibm.domino.calendar.store;

import java.util.ArrayList;
import java.util.Date;

/**
 * The public interface for accessing events on a calendar.
 * 
 * <p>You get an instance of this interface by calling a factory method.
 * For example, a factory might return an instance bound to a specific
 * Notes database.  Factory details are to be determined.
 */
public interface ICalendarStore {
	
	public static final int FLAG_NO_WORKFLOW = 0x0001;
    public static final int FLAG_REPLACE_COMPLETELY = 0x0002;
    public static final int FLAG_SMART_SEQUENCE = 0x0004;

	/**
	 * Gets a range of events in iCalendar format.
	 * 
	 * @param fromDate	Start date and time.  If not <code>null</code>,
	 * 					the method will only return events
	 * 					occurring after this date and time.
	 *  
	 * @param toDate	End date and time.  If not <code>null</code>, 
	 * 					the method will only return events
	 * 					occurring before this date and time.
	 * 
     * @param rangeFieldsFilter    Filters the fields (or properties) returned for each event.
     *                  If not <code>null</code>, 
     *                  the method will return the default fields.
	 * @return 			The events in iCalendar format.
	 * 
	 * @throws 			StoreException
	 */
	public EventSet getEvents(Date fromDate, Date toDate, int skipCount, int maxEvents, ArrayList<String> rangeFieldsFilter) throws StoreException;
	
	/**
	 * Gets an individual event or event instance.
	 * 
	 * @param id			The event ID.
	 * 
	 * @param recurrenceId	The ID of a specific instance of a recurring
	 * 						event.  May be <code>null</code>.
	 * 
	 * @return 				The event in iCalendar format.
	 * @throws 				StoreException
	 */
	public String getEvent(String id, String recurrenceId) throws StoreException;
	
	/**
	 * Creates an event.
	 * 
	 * @param event		The event in iCalendar format. This can be a recurring
	 * 					or non-recurring event.
	 * 
	 * @return 			The created event in iCalendar format.  This will include
	 * 					the unique ID of the event and any other data added
	 * 					by <code>createEvent</code>
	 * 
	 * @throws 			StoreException
	 */
	public String createEvent(String event, int flags) throws StoreException;
	
	/**
	 * Updates an event.
	 * 
	 * @param event		The event in iCalendar format.
	 * 
	 * @param id			The event ID.
	 * 
	 * @param recurrenceId	The ID of a specific instance of a recurring
	 * 						event.  May be <code>null</code>.
	 * 
	 * @return 			The updated event in iCalendar format.  This will include
	 * 					the unique ID of the event and any other data added
	 * 					by <code>updateEvent</code>
	 * 
	 * @throws 			StoreException
	 */
	public String updateEvent(String event, String id, String recurrenceId,
					String comments, int flags) throws StoreException;
	
	/**
	 * Deletes an event.
	 * 
	 * @param id			The event ID.
	 * 
	 * @param recurrenceId	The ID of a specific instance of a recurring
	 * 						event.  May be <code>null</code>.
	 * 
	 * @param range			The range of instances to delete.  May be 
	 * 						<code>null</code> when <code>recurrenceId</code> is 
	 * 						also <code>null</code>.
	 *
	 * @throws StoreException
	 */
	public void deleteEvent(String id, String recurrenceId, RecurrenceRange range, int flags) throws StoreException;
	
	/**
	 * Applies an action to an event.
	 * 
	 * @param id
	 * @param recurrenceId
	 * @param range
	 * @param action
	 * @throws StoreException
	 */
	public void processEventAction(String id, String recurrenceId, RecurrenceRange range,
					Action action) throws StoreException;
	
	/**
	 * Gets an array of unapplied notices for an event.
	 * 
	 * @param eventId
	 * @return An array of iCalendar representations, one for each unapplied notice.
	 * @throws StoreException
	 */
	public String[] getUnappliedNotices(String eventId) throws StoreException;
	
	/**
	 * Gets an array of new invitations.
	 * 
	 * @param start
	 * @param since
     * @param eventUNID Returns only the invitations associated with the given event UNID.
     *        if not <code>null</code>, return all invitations
	 * @return An array of iCalendar representations, one for each new invitation.
	 * @throws StoreException
	 */
	public String[] getNewInvitations(Date start, Date since, String eventUNID) throws StoreException;
	
	/**
	 * Gets the iCalendar representation of a notice.
	 * 
	 * @param id
	 * @return
	 * @throws StoreException
	 */
	public String getNotice(String id) throws StoreException;
	
	/**
	 * Applies an action to a notice.
	 * 
	 * @param id
	 * @param action
	 * @throws StoreException
	 */
	public void processNoticeAction(String id, Action action) throws StoreException;
}
