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

import java.util.Vector;

import lotus.domino.AdministrationProcess;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.commons.util.BackendUtil;

/**
 * The 9.0.1 implementation of IDelegateProvider.
 * 
 * <p>This extends <code>DelegateProvider</code> which is available in the extlib
 * on OpenNTF.  While <code>DelegateProvider</code> edits the ACL directly, this
 * extension uses adminp to add, modify and remove delegates.  It depends on 
 * new adminp support added to the backend classes in 9.0.1.
 */
public class Delegate901Provider extends DelegateProvider {
    
    private static final String OWNER_ITEM = "owner"; // $NON-NLS-1$

    private static final String READ_CALENDAR_ITEM = "ReadCalendar"; // $NON-NLS-1$
    private static final String WRITE_CALENDAR_ITEM = "WriteCalendar"; // $NON-NLS-1$
    private static final String READ_MAIL_ITEM = "ReadMail"; // $NON-NLS-1$
    private static final String WRITE_MAIL_ITEM = "WriteMail"; // $NON-NLS-1$
    private static final String EDIT_MAIL_ITEM = "EditMail"; // $NON-NLS-1$
    private static final String DELETE_MAIL_ITEM = "DeleteMail"; // $NON-NLS-1$
    
    private static final int READ_CALENDAR = 0;
    private static final int WRITE_CALENDAR = 1;
    private static final int READ_MAIL = 2;
    private static final int WRITE_MAIL = 3;
    private static final int EDIT_MAIL = 4;
    private static final int DELETE_MAIL = 5;

    private static final String s_items[] = {READ_CALENDAR_ITEM, WRITE_CALENDAR_ITEM, READ_MAIL_ITEM,
                                            WRITE_MAIL_ITEM, EDIT_MAIL_ITEM, DELETE_MAIL_ITEM};

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.DelegateProvider#setImpl(lotus.domino.Database, com.ibm.domino.commons.model.Delegate)
     */
    protected void setImpl(Database database, Delegate delegate, Document profile) throws ModelException, NotesException {
        AdministrationProcess adminp = null;
        
        try {
            Session session = database.getParent();
            
            // Can't modify the owner's access
            
            String owner = profile.getItemValueString(OWNER_ITEM);
            verifyDelegateNotOwner(session, delegate.getName(), owner);
            
            // Can't modify a delegate that's not there
            
            Vector[] vectors = loadVectors(profile);
            Name name = session.createName(delegate.getName());
            if ( !delegateExists(vectors, name.getCanonical()) ) {
                throw new ModelException("Delegate not found", ModelException.ERR_NOT_FOUND); // $NON-NLS-1$
            }
            
            // Update the right vector(s)

            delegateRemove(vectors, name.getCanonical());
            delegateAdd(vectors, name.getCanonical(), delegate.getAccess());
            
            // Send the adminp request
            
            String mailFile = database.getFilePath();
            String server = session.getServerName();
            
            adminp = session.createAdministrationProcess(null);
            String unid = adminp.delegateMailFile(owner, 
                                vectors[0], vectors[1], vectors[2], vectors[3], vectors[4], vectors[5],  
                                null, mailFile, server);
        }
        finally {
            BackendUtil.safeRecycle(adminp);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.DelegateProvider#addImpl(lotus.domino.Database, com.ibm.domino.commons.model.Delegate)
     */
    protected void addImpl(Database database, Delegate delegate, Document profile) throws ModelException, NotesException {
        AdministrationProcess adminp = null;
        
        try {
            Session session = database.getParent();
            
            // Can't add the owner as a delegate
            
            String owner = profile.getItemValueString(OWNER_ITEM);
            verifyDelegateNotOwner(session, delegate.getName(), owner);
            
            // Can't add someone that's already there
            
            Vector[] vectors = loadVectors(profile);
            Name name = session.createName(delegate.getName());
            if ( delegateExists(vectors, name.getCanonical()) ) {
                throw new ModelException("A delegate of that name already exists", ModelException.ERR_CONFLICT); // $NON-NLS-1$
            }
            
            // Add the delegate to the right vector(s)
            
            delegateAdd(vectors, name.getCanonical(), delegate.getAccess());
            
            // Send the adminp request
            
            String mailFile = database.getFilePath();
            String server = session.getServerName();
            
            adminp = session.createAdministrationProcess(null);
            String unid = adminp.delegateMailFile(owner, 
                                vectors[0], vectors[1], vectors[2], vectors[3], vectors[4], vectors[5],  
                                null, mailFile, server);
        }
        finally {
            BackendUtil.safeRecycle(adminp);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.DelegateProvider#deleteImpl(lotus.domino.Database, java.lang.String, java.lang.String)
     */
    protected void deleteImpl(Database database, String name, Document profile) throws ModelException, NotesException {
        AdministrationProcess adminp = null;
        
        try {
            Session session = database.getParent();
            
            // Can't remove the owner
            
            String owner = profile.getItemValueString(OWNER_ITEM);
            verifyDelegateNotOwner(session, name, owner);
            
            // Can't remove a delegate that's not there
            
            Vector[] vectors = loadVectors(profile);
            Name no = session.createName(name);
            if ( !delegateExists(vectors, no.getCanonical()) ) {
                throw new ModelException("Delegate not found", ModelException.ERR_NOT_FOUND); // $NON-NLS-1$
            }

            // Send the adminp request
            
            Vector removeList = new Vector();
            removeList.add(no.getCanonical());
            
            String mailFile = database.getFilePath();
            String server = session.getServerName();
            
            adminp = session.createAdministrationProcess(null);
            String unid = adminp.delegateMailFile(owner, 
                                null, null, null, null, null, null, 
                                removeList, mailFile, server);
        }
        finally {
            BackendUtil.safeRecycle(adminp);
        }
    }
    
    private void verifyDelegateNotOwner(Session session, String delegate, String canonicalOwner) throws ModelException, NotesException {
        Name no = session.createName(canonicalOwner);
        if ( delegate.equalsIgnoreCase(no.getAbbreviated()) ) {
            throw new ModelException("Owner cannot be a delegate.", ModelException.ERR_NOT_ALLOWED); // $NON-NLS-1$
        }
    }
    
    private Vector[] loadVectors(Document profile) throws NotesException {
        Vector[] vectors = new Vector[s_items.length];
        
        for ( int i = 0; i < s_items.length; i++ ) {
            String item = s_items[i];
            vectors[i] = profile.getItemValue(item);
        }
        
        return vectors;
    }
    
    private boolean delegateExists(Vector[] vectors, String canonicalName) {
        boolean exists = false;
        
        for ( int i = 0; i < vectors.length; i++ ) {
            Vector values = vectors[i];
            if ( values != null ) {
                for ( int j = 0; j < values.size(); j++) {
                    String thisName = (String)values.get(j);
                    if ( thisName.equalsIgnoreCase(canonicalName) ) {
                        exists = true;
                        break;
                    }
                }
                
                if ( exists ) break;
            }
        }

        return exists;
    }
    
    /**
     * Removes a delegate name from the array of vectors corresponding to the calendar profile.
     * 
     * <p>This method just modifies the vectors in memory.  It does not update the profile.
     * 
     * @param vectors
     * @param canonicalName
     */
    private void delegateRemove(Vector[] vectors, String canonicalName) {
        for ( int i = 0; i < vectors.length; i++ ) {
            Vector values = vectors[i];
            if ( values != null ) {
                for ( int j = 0; j < values.size(); j++) {
                    String thisName = (String)values.get(j);
                    if ( thisName.equalsIgnoreCase(canonicalName) ) {
                        values.remove(j);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Adds a delegate name to a single vector.
     * 
     * @param vectors
     * @param i
     * @param canonicalName
     */
    private void delegateAdd(Vector[] vectors, int i, String canonicalName) {
        if ( vectors[i] == null ) {
            vectors[i] = new Vector();
        }
        vectors[i].add(canonicalName);
    }
    
    /**
     * Adds a delegate name to the array of vectors corresponding to the calendar profile.
     * 
     * <p>This method just modifies the vectors in memory.  It does not update the profile.
     * 
     * @param vectors
     * @param canonicalName
     * @param da
     */
    private void delegateAdd(Vector[] vectors, String canonicalName, DelegateAccess da) {
        if ( da.getWhat() == DelegateAccess.What.CALENDAR ) {
            delegateAdd(vectors, READ_CALENDAR, canonicalName);
            if ( da.isCreate() || da.isEdit() || da.isDelete() ) {
                delegateAdd(vectors, WRITE_CALENDAR, canonicalName);
            }
        }
        else if ( da.getWhat() == DelegateAccess.What.MAIL ) {
            if ( da.isEdit() ) {
                delegateAdd(vectors, WRITE_CALENDAR, canonicalName);
                delegateAdd(vectors, EDIT_MAIL, canonicalName);
                if ( da.isDelete() ) {
                    delegateAdd(vectors, DELETE_MAIL, canonicalName);
                }
            }
            else if ( da.isCreate() ) {
                delegateAdd(vectors, WRITE_CALENDAR, canonicalName);
                delegateAdd(vectors, WRITE_MAIL, canonicalName);
                if ( da.isDelete() ) {
                    delegateAdd(vectors, DELETE_MAIL, canonicalName);
                }
            }
            else {
                delegateAdd(vectors, READ_MAIL, canonicalName);
            }
        }
    }
}
