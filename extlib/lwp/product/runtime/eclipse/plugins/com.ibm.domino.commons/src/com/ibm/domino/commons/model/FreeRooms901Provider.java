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
import java.util.StringTokenizer;
import java.util.Vector;

import lotus.domino.DateTime;
import lotus.domino.Name;
import lotus.domino.NotesError;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.commons.util.BackendUtil;

/**
 * Free rooms provider
 * 
 * <p>This class uses the back-end (BE) classes to find an avaliable room.
 * The required BE support was added in Domino 9.0.1.
 *
 */
public class FreeRooms901Provider implements IFreeRoomsProvider {

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IFreeRoomsProvider#getFreeRooms(lotus.domino.Session, java.lang.String, java.util.Date, java.util.Date, int)
     */
    public List<Room> getFreeRooms(Session session, String site, Date start, Date end, int capacity) throws ModelException {
        List<Room> rooms = new ArrayList<Room>();
        DateTime dtStart = null;
        DateTime dtEnd = null;
        
        try {
            String userName = session.getEffectiveUserName();
            
            dtStart = session.createDateTime(start);
            dtEnd = session.createDateTime(end);
            
            Vector results = session.freeResourceSearch(dtStart, dtEnd, site, 0, 
                                        999, // TODO: What is the right default for maxResults? 
                                        userName, capacity, null, null, 0);
            if ( results != null ) {
                Iterator iterator = results.iterator();
                while (iterator.hasNext()) {
                    String rawValue = (String)iterator.next();
                    
                    // Parse the raw value into a Room object
                    Room room = parseRoom(session, rawValue);
                    rooms.add(room);
                }
            }
        } 
        catch (NotesException e) {
            throw new ModelException(e.text, mapError(e.id));   
        }
        finally {
            BackendUtil.safeRecycle(dtStart);
            BackendUtil.safeRecycle(dtEnd);
        }
        
        return rooms;
    }

    private Room parseRoom(Session session, String rawValue) throws NotesException {
        Room room = null;
        String displayName = null;
        String distinguishedName = null;
        String domain = null;
        String email = null;
        int capacity = 0;
        
        StringTokenizer tokenizer = new StringTokenizer(rawValue, ";");
        if ( tokenizer.hasMoreTokens() ) {
            // Get room name
            String rawName = tokenizer.nextToken();
            Name name = session.createName(rawName);
            displayName = name.getCommon();
            distinguishedName = name.getAbbreviated();
        }
        
        if ( tokenizer.hasMoreTokens() ) {
            try {
                // Get room capacity
                String rawCapacity = tokenizer.nextToken();
                capacity = Integer.parseInt(rawCapacity);
            }
            catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        if ( tokenizer.hasMoreTokens() ) {
            // Get email address, but ignore bad addresses.
            // TODO: Revisit this.  The initial implementation of freeResourceSearch was returning
            // a bad email address for rooms without a stored email address.  The bad address
            // had a trailing "@". When the freeResourceSearch is fixed, we can remove some of
            // this code.
            String rawEmail = tokenizer.nextToken();
            if ( rawEmail != null && !rawEmail.endsWith("@") ) {
                email = rawEmail;
            }
        }
        
        return new Room(displayName, distinguishedName, domain, email, capacity); 
    }
    
    private int mapError(int notesError) {
        int storeError = ModelException.ERR_GENERAL;
        
        switch(notesError) {
            case NotesError.NOTES_ERR_SITENOTFOUND:
                // Now we only need to identify code NOTES_ERR_SITENOTFOUND
                storeError = ModelException.ERR_NOT_FOUND;
                break;
            default :
                break;
        }
        
        return storeError;
    }
}
