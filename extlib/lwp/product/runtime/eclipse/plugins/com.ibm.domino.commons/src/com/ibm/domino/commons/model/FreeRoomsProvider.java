/*
 * © Copyright IBM Corp. 2013
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

package com.ibm.domino.commons.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lotus.domino.DateRange;
import lotus.domino.DateTime;
import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.commons.internal.Logger;
import com.ibm.domino.commons.util.BackendUtil;

/**
 * Free rooms provider.
 * 
 * <p>WARNING: You should never construct an instance of this directly.
 * Get an instance of IFreeRoomsProvider from ProviderFactory.
 */
public class FreeRoomsProvider implements IFreeRoomsProvider {
    
    private static Vector<String> s_lookupItems = lookupItems();

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IFreeRoomsProvider#getFreeRooms(java.lang.String, java.util.Date, java.util.Date, int)
     */
    public List<Room> getFreeRooms(Session session, String site, 
                            Date start, Date end, int capacity) 
                            throws ModelException {
        List<Room> rooms = null;
        
        try {
            
            // Get a list of matching rooms
            
            List<Room> matches = findMatchingRooms(session, site, capacity);
            
            // Reduce the list to the rooms that are free
            
            if ( matches != null ) {
                if ( matches.size() == 0 ) {
                    rooms = matches;
                }
                else {
                    rooms = findFreeRooms(session, matches, start, end);
                }
            }
            
        } 
        catch (NotesException e) {
            throw new ModelException("Error finding rooms matching the site and capacity", e); // $NLX-FreeRoomsProvider.Errorfindingroomsmatchingthesitea-1$
        }
        
        return rooms;
    }
    
    /**
     * Given a list of rooms, find the ones that are free for a given range.
     * 
     * @param session
     * @param candidates
     * @param start
     * @param end
     * @return
     * @throws NotesException
     */
    private List<Room> findFreeRooms(Session session, List<Room> candidates, Date start, Date end) throws NotesException {

        List<Room> rooms = new ArrayList<Room>();
        DateRange range = null;
        Vector freetimes = null;
        
        try {
        
            DateTime dtStart = session.createDateTime(start);
            DateTime dtEnd = session.createDateTime(end);
            
            range = session.createDateRange();
            range.setStartDateTime(dtStart);
            range.setEndDateTime(dtEnd);
            
            Iterator<Room> iterator = candidates.iterator();
            while ( iterator.hasNext() ) {
                
                if ( freetimes != null ) {
                    BackendUtil.safeRecycle(freetimes);
                    freetimes = null;
                }
                
                Room room = iterator.next();
                String item = room.getEmailAddress();
                if ( StringUtil.isEmpty(item) ) {
                    item = room.getDistinguishedName();
                }
                Vector<String> names = new Vector<String>(1);
                names.addElement(item);
                
                // Get the free time for this room
                
                Logger.get().getLogger().fine("Searching free time for " + item); // $NON-NLS-1$
                
                try {
                    freetimes = session.freeTimeSearch(range, 5, names, false);
                }
                catch (Throwable e) {
                    Logger.get().warn(e, "Exception thrown searching free time for {0}", item); // $NLW-FreeRoomsProvider.Exceptionthrownsearchingfreetimef-1$
                }
                
                if ( freetimes == null ) {
                    continue;
                }
                
                // Compare the start and end times of the first free block
                
                DateRange freeRange = (DateRange)freetimes.get(0);
                Date freeStart = freeRange.getStartDateTime().toJavaDate();
                Date freeEnd = freeRange.getEndDateTime().toJavaDate();
                
                if ( start.getTime() != freeStart.getTime() || 
                     end.getTime() != freeEnd.getTime() ) {
                    continue;
                }
                
                // It's completely free.  Add it to the list.
                
                rooms.add(room);
            }
        }
        finally {
            BackendUtil.safeRecycle(range);
            BackendUtil.safeRecycle(freetimes);
        }
        
        return rooms;
    }

    /**
     * Find the rooms in a given site with a minimum capacity.
     * 
     * @param session
     * @param site
     * @param capacity
     * @return
     * @throws NotesException
     * @throws ModelException
     */
    private List<Room> findMatchingRooms(Session session, String site, int capacity) 
                            throws NotesException, ModelException {
        List<Room> rooms = new ArrayList<Room>();
        Directory lookupDir = null;
        
        try {
            lookupDir = session.getDirectory();
            
            Vector<String> vName = new Vector<String>();
            vName.addElement(site);

            Logger.get().getLogger().finest("Looking up rooms in site " + site); // $NON-NLS-1$
            
            DirectoryNavigator dirNav = lookupDir.lookupNames("($Rooms)", vName, s_lookupItems, true);  //$NON-NLS-1$
            if( dirNav == null || dirNav.getCurrentMatches() == 0 ){
                throw new ModelException("Site not found", ModelException.ERR_NOT_FOUND); // $NLX-FreeRoomsProvider.Sitenotfound-1$
            }
            
            boolean match = dirNav.findFirstMatch();
            while (match) {
            
                Vector<String> value = null;
                
                // Get the room name
                
                String fullName = null;
                value = dirNav.getFirstItemValue();
                if ( value != null && value.size() > 0 ) {
                    fullName = value.elementAt(0);
                }
                
                // Get the domain
                
                String domain = null;
                value = dirNav.getNextItemValue();
                if ( value != null && value.size() > 0 ) {
                    domain = value.elementAt(0);
                }
                
                // Get the email address
                
                String emailAddress = null;
                value = dirNav.getNextItemValue();
                if ( value != null && value.size() > 0 ) {
                    emailAddress = value.elementAt(0);
                }
                
                // Get the room capacity
                
                Vector<Double> dValue = dirNav.getNextItemValue();
                int roomCapacity = 0;
                if ( dValue != null && dValue.size() > 0 ) {
                    roomCapacity = dValue.elementAt(0).intValue();
                }
                
                // Get the autoprocess type
                
                String apType = null;
                value = dirNav.getNextItemValue();
                if ( value != null && value.size() > 0 ) {
                    apType = value.elementAt(0);
                }
                
                // Get the autoprocess user list
                
                Vector<String> apUsers = dirNav.getNextItemValue();
                
                // Check the room capacity
                
                boolean skipRoom = false;
                if ( roomCapacity < capacity ) {
                    skipRoom = true;
                }
                
                // Check the owner restrictions
                
                if ( !skipRoom && "2".equals(apType) ) {
                    boolean userListed = false;
                    if ( apUsers != null && apUsers.size() > 0 ) {
                        String currentUser = session.getEffectiveUserName();
                        Iterator<String> iterator = apUsers.iterator();
                        while (iterator.hasNext()) {
                            String user = iterator.next();
                            if ( user.equals(currentUser) ) {
                                userListed = true;
                                break;
                            }
                        }
                    }
                    
                    if ( !userListed ) {
                        skipRoom = true;
                    }
                }

                // Add or skip the room
                
                Name name = session.createName(fullName);
                if ( skipRoom ) {
                    Logger.get().getLogger().fine("Skipping room " + name.getAbbreviated() + " (" + roomCapacity + ")"); // $NON-NLS-1$
                }
                else {
                    Logger.get().getLogger().fine("Adding room " + name.getAbbreviated() + " (" + roomCapacity + ")"); // $NON-NLS-1$
                    Room room = new Room(name.getCommon(), name.getAbbreviated(), domain, 
                                        emailAddress, roomCapacity);
                    rooms.add(room);
                }

                match = dirNav.findNextMatch();
            }
        }
        finally {
            BackendUtil.safeRecycle(lookupDir);
        }
        
        return rooms;
    }

    private static Vector<String> lookupItems() {
        Vector<String> lookupItems = new Vector<String>();
        
        lookupItems.addElement("FullName");         //$NON-NLS-1$
        lookupItems.addElement("MailDomain");         //$NON-NLS-1$
        lookupItems.addElement("InternetAddress");  //$NON-NLS-1$
        lookupItems.addElement("ResourceCapacity"); //$NON-NLS-1$
        lookupItems.addElement("AutoprocessType"); //$NON-NLS-1$
        lookupItems.addElement("AutoprocessUserList"); //$NON-NLS-1$
        
        return lookupItems;
    }
    
}