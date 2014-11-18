/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.social.util;

import com.ibm.commons.util.StringUtil;


/**
 * Sophisticated 2 ways cache, which MRU list management.
 * <p>
 * This class is used to maintain a map between string, like a NotesID and another
 * external ID. One can use it to retrive a NotesID from the social ID, and vice
 * versa.<br>
 * It also maintain a maximum number of entries in memory, thus prevent the list from
 * hijacking the whole available memory in case of big directories.<br>
 * This class is abstract as it requires an actual implementation to find the string
 * correpondance. The implementation is required to ensure that the mapping are unique.
 * This is not checked by the map for performance reasons, and can have unpredictable
 * results if it happens.   
 * </p> 
 */
public abstract class NamesMap {
	
	private int slotCount;
    private int maxSize;
    private int size;
    
    // List for implementing MRUs
    private MapEntry listStart;
    private MapEntry listEnd;
    
    // Hash map entries
    private MapEntry[] idEntries;
    private MapEntry[] nameEntries;

    private static final class MapEntry {
    	// List in the Hash table
        MapEntry    idPrevHash;
        MapEntry    idNextHash;
        MapEntry    namePrevHash;
        MapEntry    nameNextHash;
        // List in the Linked list
        MapEntry    prevList;
        MapEntry    nextList;
        
        int         idHashCode;
        int         nameHashCode;
        
        String      id;
        String      name;
        MapEntry( String id, String name ) {
            this.id = id;
            this.name = name;
            this.idHashCode = id.hashCode();
            this.nameHashCode = name.hashCode();
        }
    }

    public NamesMap(int maxSize) {
        this(maxSize,57);  // See Aho for good values here...
    }
    
    public NamesMap(int maxSize, int slotCount) {
        this.maxSize = maxSize;
        this.slotCount = slotCount;
        this.idEntries = new MapEntry[slotCount];
        this.nameEntries = new MapEntry[slotCount];
    }
    
    protected abstract String findIdByName(String name);
    protected abstract String findNameByid(String id);

    public int size() {
        return size;
    }
    
    public int getCapacity() {
        return maxSize;
    }
    
    public synchronized void clear() {
    	this.size = 0;
        this.idEntries = new MapEntry[slotCount];
        this.nameEntries = new MapEntry[slotCount];
        this.listStart = null;
        this.listEnd = null;
    }
    
    public synchronized String getNameById( String id ) {
		if(StringUtil.isEmpty(id)) {
			return id;
		}
    	
    	// Look if the name is already in the collection
        MapEntry e = getEntryById(id);
        if( e!=null ) {
			moveToStart(e);
        	return e.name;
        }
        // Ok the entry does not exist, try to create it
        String name = findNameByid(id);
        if(name!=null) {
        	e = new MapEntry(id,name);
        	put(e);
        	return name;
        }
        // Not found
        return null;
    }
    public synchronized String getIdByName( String name ) {
		if(StringUtil.isEmpty(name)) {
			return name;
		}
		
    	// Look if the id is already in the collection
        MapEntry e = getEntryByName(name);
        if( e!=null ) {
			moveToStart(e);
        	return e.id;
        }
        // Ok the entry does not exist, try to create it
        String id = findIdByName(name);
        if(id!=null) {
        	e = new MapEntry(id,name);
        	put(e);
        	return id;
        }
        // Not found
        return null;
    }

    private final int getSlot(int hashCode) {
        return (hashCode & 0x7FFFFFFF) % slotCount;
    }
    private final MapEntry getEntryById( String id ) {
        int hashCode = id.hashCode();
        for( MapEntry e=idEntries[getSlot(hashCode)]; e!=null; e=e.idNextHash ) {
            if( e.idHashCode==hashCode && e.id.equals(id) ) {
                return e;
            }
        }
        return null;
    }
    private final MapEntry getEntryByName( String name ) {
        int hashCode = name.hashCode();
        for( MapEntry e=nameEntries[getSlot(hashCode)]; e!=null; e=e.nameNextHash ) {
            if( e.nameHashCode==hashCode && e.name.equals(name) ) {
                return e;
            }
        }
        return null;
    }


    private void put( MapEntry e ) {
        // Remove the oldest one?
        if( size==maxSize ) {
            removeEntry(listEnd);
        }
        
        // Insert the new entry in the ID HashTable
        int idSlot = getSlot(e.idHashCode);
        e.idNextHash = idEntries[idSlot];
        if(e.idNextHash!=null) {
        	e.idNextHash.idPrevHash = e;
        }
        idEntries[idSlot] = e;

        // Insert the new entry in the Name HashTable
        int nameSlot = getSlot(e.nameHashCode);
        e.nameNextHash = nameEntries[nameSlot];
        if(e.nameNextHash!=null) {
        	e.nameNextHash.namePrevHash = e;
        }
        nameEntries[nameSlot] = e;
        
        // And in the list
        if( listStart!=null ) {
            listStart.prevList = e;
        }
        e.nextList = listStart;
        listStart = e;
        if( listEnd==null ) {
            listEnd = e;
        }
        size++;
    }

    private final void moveToStart( MapEntry e ) {
        if( e!=listStart ) {
            // Remove it from the list
            e.prevList.nextList = e.nextList;
            if( e.nextList!=null ) {
                e.nextList.prevList = e.prevList;
            } else {
                listEnd = e.prevList;
            }
            // And add it a the top
            listStart.prevList = e;
            e.nextList = listStart;
            listStart = e;
        }
    }

    private final void removeEntry(MapEntry e) {
        // Remove the entry from the ID hashtable
        if(e.idPrevHash!=null) {
        	e.idPrevHash.idNextHash = e.idNextHash;
        } else {
        	idEntries[getSlot(e.idHashCode)] = e.idNextHash;
        }
        if(e.idNextHash!=null) {
        	e.idNextHash.idPrevHash = e.idPrevHash; 
        }
        // Remove the entry from the Name hashtable
        if(e.namePrevHash!=null) {
        	e.namePrevHash.nameNextHash = e.nameNextHash;
        } else {
        	nameEntries[getSlot(e.nameHashCode)] = e.nameNextHash;
        }
        if(e.nameNextHash!=null) {
        	e.nameNextHash.namePrevHash = e.namePrevHash; 
        }
        // Remove the entry from the linked list
        if( e.prevList!=null ) {
            e.prevList.nextList = e.nextList;
        } else {
            listStart = e.nextList;
        }
        if( e.nextList!=null ) {
            e.nextList.prevList = e.prevList;
        } else {
            listEnd = e.prevList;
        }
        
        // Decrease the map count
        size--;
    }
}
