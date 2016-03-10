/*
 * © Copyright IBM Corp. 2013, 2015
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lotus.domino.ACL;
import lotus.domino.ACLEntry;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.commons.util.BackendUtil;

/**
 * PIM delegate provider.
 * 
 * <p>WARNING: You should never construct an instance of this directly.
 * Get an instance of IDelegateProvider from ProviderFactory.
 */
public class DelegateProvider implements IDelegateProvider {
    
    private static final String OWNER_ITEM = "Owner"; // $NON-NLS-1$
    
    private static final String READ_CALENDAR_ITEM = "ReadCalendar"; // $NON-NLS-1$
    private static final String WRITE_CALENDAR_ITEM = "WriteCalendar"; // $NON-NLS-1$
    private static final String READ_MAIL_ITEM = "ReadMail"; // $NON-NLS-1$
    private static final String WRITE_MAIL_ITEM = "WriteMail"; // $NON-NLS-1$
    private static final String EDIT_MAIL_ITEM = "EditMail"; // $NON-NLS-1$
    private static final String DELETE_MAIL_ITEM = "DeleteMail"; // $NON-NLS-1$
    
    // NOTE: Do not change the order of the items in this array.  The getDelegates
    // method depends on this order.
    private static final String s_items[] = {READ_CALENDAR_ITEM, WRITE_CALENDAR_ITEM, READ_MAIL_ITEM,
                                            WRITE_MAIL_ITEM, EDIT_MAIL_ITEM, DELETE_MAIL_ITEM};
    
    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#get(lotus.domino.Database, java.lang.String)
     */
    public Delegate get(Database database, String name) throws ModelException {
        
        Delegate delegate = null;
        
        try {
            List<Delegate> list = getDelegates(database);
            Iterator<Delegate> iterator = list.iterator();
            while ( iterator.hasNext() ) {
                Delegate thisDelegate = iterator.next();
                if ( thisDelegate.getName().equalsIgnoreCase(name) ) {
                    delegate = thisDelegate;
                    break;
                }
            }
        } 
        catch (NotesException e) {
            throw new ModelException("Error getting delegate access", e); // $NLX-DelegateProvider.Errorgettingdelegateaccess-1$
        }

        return delegate;
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#set(lotus.domino.Database, com.ibm.domino.commons.model.Delegate)
     */
    public void set(Database database, Delegate delegate) throws ModelException {
        Document profile = null;
        
        try {
            validateDelegateAccess(delegate.getAccess());
            if ( StringUtil.isEmpty(delegate.getName()) ) {
                throw new ModelException("A delegate must have a name.", ModelException.ERR_INVALID_INPUT); // $NLX-DelegateProvider.Adelegatemusthaveaname-1$
            }

            // Update the delegate
            
            profile = profileGet(database);
            setImpl(database, delegate, profile);
            
            // Update the profile

            if ( delegate.getType() != Delegate.Type.DEFAULT ) {
                try {
                    profileRemoveDelegate(profile, delegate.getName());
                    profileAddDelegate(profile, delegate);
                    profile.save();
                }
                catch (NotesException e) {
                    // Ignore errors.  As long as the ACL is updated, we shouldn't lose sleep
                    // about the calendar profile.
                }
            }
        } 
        catch (NotesException e) {
            throw new ModelException("Error creating delegate", e); // $NLX-DelegateProvider.Errorcreatingdelegate-1$
        }
        finally {
            BackendUtil.safeRecycle(profile);
        }
    }
    
    /**
     * Does most of the work of updating a delegate.
     * 
     * <p>This version edits the ACL directly. A subclass may use adminp
     * to update the delegate.
     * 
     * @param database
     * @param delegate
     * @throws ModelException
     * @throws NotesException
     */
    protected void setImpl(Database database, Delegate delegate, Document profile) throws ModelException, NotesException {
        ACL acl = null;
        
        try {
            if ( !hasManagerAccess(database) ) {
                throw new ModelException("Manager access is required to modify a delegate.", ModelException.ERR_NOT_ALLOWED); // $NLX-DelegateProvider.Manageraccessisrequiredtomodifyad-1$
            }
            
            acl = database.getACL();
            
            // Find the matching delegate
            
            boolean updated = false;
            ACLEntry entry = acl.getFirstEntry();
            while ( entry != null ) {
                Name no = entry.getNameObject();

                if ( delegate.getName().equalsIgnoreCase(no.getAbbreviated()) ) {
                    // Clear the current access
                    entry.setLevel(ACL.LEVEL_NOACCESS);
                    entry.setPublicReader(false);
                    entry.setPublicWriter(false);
                    
                    // Apply the new access
                    aclEntryFromDelegate(delegate, entry);
                    acl.save();
                    updated = true;
                    break;
                }
                
                entry = acl.getNextEntry();
            }

            if ( !updated ) {
                throw new ModelException("Delegate not found", ModelException.ERR_NOT_FOUND); // $NLX-DelegateProvider.Delegatenotfound-1$
            }
        } 
        finally {
            BackendUtil.safeRecycle(acl);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#add(lotus.domino.Database, com.ibm.domino.commons.model.Delegate)
     */
    public void add(Database database, Delegate delegate) throws ModelException {
        Document profile = null;
        
        try {
            validateDelegateAccess(delegate.getAccess());
            if ( StringUtil.isEmpty(delegate.getName()) ) {
                throw new ModelException("A delegate must have a name.", ModelException.ERR_INVALID_INPUT); // $NLX-DelegateProvider.Adelegatemusthaveaname.1-1$
            }
            
            // Add the delegate
            
            profile = profileGet(database);
            addImpl(database, delegate, profile);
            
            // Update the profile
            
            try {
                profileAddDelegate(profile, delegate);
                profile.save();
            }
            catch (NotesException e) {
                // Ignore errors.  As long as the ACL is updated, we shouldn't lose sleep
                // about the calendar profile.
            }
        } 
        catch (NotesException e) {
            throw new ModelException("Error creating delegate", e); // $NLX-DelegateProvider.Errorcreatingdelegate.1-1$
        }
        finally {
            BackendUtil.safeRecycle(profile);
        }
    }
    
    /**
     * Does most of the work of creating a delegate.
     * 
     * <p>This version edits the ACL directly. A subclass may use adminp
     * to createe the delegate.
     * 
     * @param database
     * @param delegate
     * @throws ModelException
     * @throws NotesException
     */
    protected void addImpl(Database database, Delegate delegate, Document profile) throws ModelException, NotesException {
        ACL acl = null;
        
        try {
            if ( !hasManagerAccess(database) ) {
                throw new ModelException("Manager access is required to add a delegate.", ModelException.ERR_NOT_ALLOWED); // $NLX-DelegateProvider.Manageraccessisrequiredtoaddadele-1$
            }
            
            acl = database.getACL();
            String delegateName = delegate.getName();
            
            // Check for conflicts
            
            ACLEntry entry = acl.getFirstEntry();
            while ( entry != null ) {
                Name no = entry.getNameObject();
                if ( delegateName.equalsIgnoreCase(no.getAbbreviated()) ) {
                    throw new ModelException("A delegate of that name already exists", ModelException.ERR_CONFLICT); // $NLX-DelegateProvider.Adelegateofthatnamealreadyexists-1$
                }
                entry = acl.getNextEntry();
            }
            
            // Create the new entry
            
            entry = acl.createACLEntry(delegateName, ACL.LEVEL_NOACCESS);
            aclEntryFromDelegate(delegate, entry);
            acl.save();
        } 
        finally {
            BackendUtil.safeRecycle(acl);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#delete(lotus.domino.Database, java.lang.String)
     */
    public void delete(Database database, String name) throws ModelException {
        Document profile = null;
        
        if ( Delegate.DEFAULT_NAME.equalsIgnoreCase(name) ) {
            throw new ModelException("Cannot remove the default delegate", ModelException.ERR_NOT_ALLOWED); // $NLX-DelegateProvider.Cannotremovethedefaultdelegate-1$
        }

        try {
            // Remove the delegate
            
            profile = profileGet(database);
            deleteImpl(database, name, profile);
            
            // Update the profile
            
            try {
                profileRemoveDelegate(profile, name);
                profile.save();
            }
            catch (NotesException e) {
                // Ignore errors.  As long as the ACL is updated, we shouldn't lose sleep
                // about the calendar profile.
            }
        } 
        catch (NotesException e) {
            throw new ModelException("Error deleting delegate", e); // $NLX-DelegateProvider.Errordeletingdelegate-1$
        }
        finally {
            BackendUtil.safeRecycle(profile);
        }
    }
    
    /**
     * Does most of the work of deleting a delegate.
     * 
     * <p>This version edits the ACL directly. A subclass may use adminp
     * to delete the delegate.
     * 
     * @param database
     * @param name The abbreviated name of the delegate to delete.
     * @param owner The canonical name of the mail file owner.
     * @throws ModelException
     * @throws NotesException
     */
    protected void deleteImpl(Database database, String name, Document profile) throws ModelException, NotesException {
        ACL acl = null;
        
        try {
            if ( !hasManagerAccess(database) ) {
                throw new ModelException("Manager access is required to remove a delegate.", ModelException.ERR_NOT_ALLOWED); // $NLX-DelegateProvider.Manageraccessisrequiredtoremovead-1$
            }
            
            // Get the ACL
            
            acl = database.getACL();
            boolean deleted = false;
            ACLEntry entry = acl.getFirstEntry();
            
            // Get the owner of the mailfile
            
            String owner = profile.getItemValueString(OWNER_ITEM);
            
            // Look at each ACL entry
            
            while ( entry != null ) {
                
                Name no = entry.getNameObject();
                
                // If it's a match, delete it
                
                if ( name.equalsIgnoreCase(no.getAbbreviated()) ) {

                    // But you can't remove the owner's access
                    
                    if ( owner != null && owner.equalsIgnoreCase(entry.getName()) ) {
                        throw new ModelException("Cannot remove the owner's access", ModelException.ERR_NOT_ALLOWED); // $NLX-DelegateProvider.Cannotremovetheownersaccess-1$
                    }
                    
                    // It's gone
                    
                    acl.removeACLEntry(name);
                    acl.save();
                    deleted = true;
                    break;
                }
                
                entry = acl.getNextEntry();
            }
            
            if ( !deleted ) {
                throw new ModelException("Delegate not found", ModelException.ERR_NOT_FOUND); // $NLX-DelegateProvider.Delegatenotfound.1-1$
            }
        } 
        finally {
            BackendUtil.safeRecycle(acl);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#getNames(lotus.domino.Database)
     */
    public List<Delegate> getList(Database database) throws ModelException {
        
        List<Delegate> list = null;
        
        try {
            list = getDelegates(database);
        } 
        catch (NotesException e) {
            throw new ModelException("Error getting delegate list", e); // $NLX-DelegateProvider.Errorgettingdelegatelist-1$
        }

        return list;
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.IDelegateProvider#getEffectiveAccess(lotus.domino.Database)
     */
    public DelegateAccess getEffectiveAccess(Database database) throws ModelException {
        
        DelegateAccess access = null;
        
        try {
            
            Session session = database.getParent();
            String user = session.getEffectiveUserName();
            if ( StringUtil.isEmpty(user) ) {
                throw new ModelException("Error getting effective user name"); // $NLX-DelegateProvider.Errorgettingeffectiveusername-1$
            }
            
            int level = database.queryAccess(user);
            int privileges = database.queryAccessPrivileges(user);
            
            DelegateAccess.What what = DelegateAccess.What.NOTHING;
            boolean read = false;
            boolean create = false;
            boolean delete = false;
            boolean edit = false;
            
            if ( level < ACL.LEVEL_READER ) {
                if ( (privileges & Database.DBACL_READ_PUBLIC_DOCS) != 0 ) {
                    what = DelegateAccess.What.CALENDAR;
                    read = true;
                    if ( (privileges & Database.DBACL_WRITE_PUBLIC_DOCS) != 0 ) {
                        create = true;
                    }
                }
            }
            else {
                what = DelegateAccess.What.MAIL;
                read = true;
                if ( level == ACL.LEVEL_AUTHOR ) {
                    create = true;
                }
                else if ( level >= ACL.LEVEL_EDITOR ) {
                    create = true;
                    edit = true;
                }
                
                if ( (privileges & Database.DBACL_DELETE_DOCS) != 0 ) {
                    delete = true;
                }
            }
            
            access = new DelegateAccess(what, read, create, delete, edit);
        }
        catch (NotesException e) {
            throw new ModelException("Error getting effective access", e); // $NLX-DelegateProvider.Errorgettingeffectiveaccess-1$
        }
        
        return access;
    }

    /**
     * Reads the list of delegates from the ACL.
     * 
     * <p>We don't use this code anymore, but it's kept here for
     * sentimental reasons.
     * 
     * @param database
     * @return
     * @throws NotesException
     */
    private List<Delegate> getDelegatesFromAcl(Database database) throws NotesException {
        List<Delegate> delegates = new ArrayList<Delegate>();
        ACL acl = null;
        Document profile = null;
        
        try {
            // Get the owner of the mailfile
            
            profile = profileGet(database);
            String owner = profile.getItemValueString(OWNER_ITEM);
            
            // Get the ACL
            
            acl = database.getACL();
            ACLEntry entry = acl.getFirstEntry();
            
            // Convert each ACL entry to a delegate
            
            while ( entry != null ) {
                
                Delegate delegate = null;
                
                // Convert entry to delegate, unless this is the owner of the mail file
                
                if ( owner == null || !owner.equalsIgnoreCase(entry.getName()) ) {
                    delegate = getDelegateFromAclEntry(entry);
                }
                
                // Add the delegate to the list
                
                if ( delegate != null ) {
                    delegates.add(delegate);
                }
                
                entry = acl.getNextEntry();
            }

        }
        finally {
            BackendUtil.safeRecycle(acl);
            BackendUtil.safeRecycle(profile);
        }
        
        return delegates;
    }
    
    private List<Delegate> getDelegates(Database database) throws NotesException {
        List<Delegate> delegates = new ArrayList<Delegate>();
        Document profile = null;
        
        try {
            // Get the calendar profile
            
            profile = profileGet(database);
            String owner = profile.getItemValueString(OWNER_ITEM);
            
            // One time init of variables
            
            Session session = database.getParent();
            Map<String, Delegate> map = new HashMap<String, Delegate>();
            Vector deleteMailValues = null;
            
            // Walk the list of items BACKWARDS (highest access level to lowest)
            
            for ( int i = s_items.length - 1; i > -1; i--) {
                String item = s_items[i];
                Vector values = profile.getItemValue(item);
                
                if ( DELETE_MAIL_ITEM.equals(item) ) {
                    deleteMailValues = values;
                    continue;
                }
                
                if ( values != null ) {
                    
                    // Do for each delegate name
                    
                    for ( int j = 0; j < values.size(); j++ ) {
                        String canonicalName = (String)values.get(j);
                        
                        // Ignore the owner of the mail file
                        
                        if ( canonicalName.equalsIgnoreCase(owner) ) {
                            continue;
                        }
                        
                        // Is this delegate already accounted for?
                        
                        if ( map.get(canonicalName) != null ) {
                            // Yes.  Skip it.
                            continue;
                        }
                        
                        // Calculate the access level
                        
                        Name name = session.createName(canonicalName); 
                        DelegateAccess access = null;
                        if ( EDIT_MAIL_ITEM.equals(item) ) {
                            boolean delete = inVector(canonicalName, deleteMailValues);
                            access = new DelegateAccess(DelegateAccess.What.MAIL, true, true, delete, true);
                        }
                        else if ( WRITE_MAIL_ITEM.equals(item) ) {
                            boolean delete = inVector(canonicalName, deleteMailValues);
                            access = new DelegateAccess(DelegateAccess.What.MAIL, true, true, delete, false);
                        }
                        else if ( READ_MAIL_ITEM.equals(item) ) {
                            access = new DelegateAccess(DelegateAccess.What.MAIL, true, false, false, false);
                        }
                        else if ( WRITE_CALENDAR_ITEM.equals(item) ) {
                            access = new DelegateAccess(DelegateAccess.What.CALENDAR, true, true, true, true);
                        }
                        else if ( READ_CALENDAR_ITEM.equals(item) ) {
                            access = new DelegateAccess(DelegateAccess.What.CALENDAR, true, false, false, false);
                        }
                        
                        // Calculate the delegate type
                        
                        Delegate.Type type = Delegate.Type.GROUP;
                        if ( canonicalName.contains("/") ) {
                            type = Delegate.Type.PERSON;
                        }
                        
                        // Add the new delegate
                        
                        Delegate delegate = new Delegate(name.getAbbreviated(), type, access);
                        delegates.add(delegate);
                        map.put(canonicalName, delegate);
                    }
                }
            }

        }
        finally {
            BackendUtil.safeRecycle(profile);
        }
        
        return delegates;
    }
    
    private boolean inVector(String canonicalName, Vector values) {
        boolean in = false;
        
        for ( int i = 0; i < values.size(); i++ ) {
            String thisName = (String)values.get(i);
            if ( thisName.equalsIgnoreCase(canonicalName) ) {
                in = true;
                break;
            }
        }
        
        return in;
    }
    
    /**
     * Translates an ACLEntry to a Delegate.
     * 
     * @param entry
     * @return The delegate or null if this entry doesn't map to a real delegate.
     * @throws NotesException
     */
    private Delegate getDelegateFromAclEntry(ACLEntry entry) throws NotesException {
        
        Delegate delegate = null;
        
        do {
            String name = entry.getName();
            int userType = entry.getUserType();

            Delegate.Type dt = null;
            if ( Delegate.DEFAULT_NAME.equals(name) ) {
                dt = Delegate.Type.DEFAULT;
            }
            else {
                if ( userType == ACLEntry.TYPE_PERSON ) {
                    dt = Delegate.Type.PERSON; 
                }
                else if ( userType == ACLEntry.TYPE_PERSON_GROUP || userType == ACLEntry.TYPE_MIXED_GROUP ) {
                    dt = Delegate.Type.GROUP; 
                }
                else if ( userType == ACLEntry.TYPE_UNSPECIFIED ) {
                    dt = Delegate.Type.UNSPECIFIED; 
                }
                else {
                    break;
                }
            }
            
            DelegateAccess.What what = DelegateAccess.What.NOTHING;
            boolean read = false;
            boolean create = false;
            boolean delete = false;
            boolean edit = false;
            
            int level = entry.getLevel();
            if ( level == ACL.LEVEL_NOACCESS || level == ACL.LEVEL_DEPOSITOR ) {
                if ( entry.isPublicReader() ) {
                    what = DelegateAccess.What.CALENDAR;
                    read = true;
                    if ( entry.isPublicWriter() ) {
                        create = true;
                        delete = true;
                        edit = true;
                    }
                }
            }
            else {
                // Entity has at least reader access
                what = DelegateAccess.What.MAIL;
                read = true;
                
                if ( level > ACL.LEVEL_READER ) {
                    create = true;
                }
                
                if ( level > ACL.LEVEL_AUTHOR ) {
                    edit = true;
                }
                
                if  ( entry.isCanDeleteDocuments() ) {
                    delete = true;
                }
            }
            
            if ( what == DelegateAccess.What.NOTHING && dt != Delegate.Type.DEFAULT ) {
                // Ignore an entry with no access, unless it's the default entry
                break;
            }
            
            // Create the delgate object
            
            Name no = entry.getNameObject();
            DelegateAccess access = new DelegateAccess(what, read, create, delete, edit);
            delegate = new Delegate((no == null) ? name : no.getAbbreviated(), 
                                dt, access);
            
        }
        while ( false );
        
        return delegate;
    }

    /**
     * Translates a Delegate to an ACLEntry
     * 
     * @param delegate
     * @param entry
     * @throws ModelException
     * @throws NotesException
     */
    private void aclEntryFromDelegate(Delegate delegate, ACLEntry entry) throws ModelException, NotesException {

        DelegateAccess.What what = delegate.getAccess().getWhat();

        if ( what == DelegateAccess.What.NOTHING ) {
            if ( delegate.getType() != Delegate.Type.DEFAULT ) {
                throw new ModelException("A delegate must have access to something", ModelException.ERR_INVALID_INPUT); // $NLX-DelegateProvider.Adelegatemusthaveaccesstosomethin-1$
            }
        }
        else if ( what == DelegateAccess.What.CALENDAR ) {
            entry.setLevel(ACL.LEVEL_NOACCESS);
            entry.setPublicReader(true);
            DelegateAccess dc = delegate.getAccess();
            if ( dc.isCreate() || dc.isEdit() || dc.isDelete() ) {
                entry.setPublicWriter(true);
            }
        }
        else {
            int level = ACL.LEVEL_READER;
            if ( delegate.getAccess().isEdit() ) {
                level = ACL.LEVEL_EDITOR;
            }
            else if ( delegate.getAccess().isCreate() ) {
                level = ACL.LEVEL_AUTHOR;
            }
            
            entry.setLevel(level);
            if ( level == ACL.LEVEL_AUTHOR ) {
                // SPR# XYXZ9AVCT7: Mail creator can also write public docs
                entry.setPublicWriter(true);
            }

            entry.setCanDeleteDocuments(delegate.getAccess().isDelete());
        }
        
        int userType = ACLEntry.TYPE_UNSPECIFIED;
        Delegate.Type type = delegate.getType();
        if ( type == Delegate.Type.PERSON ) {
            userType = ACLEntry.TYPE_PERSON;
        }
        else if ( type == Delegate.Type.GROUP ) {
            userType = ACLEntry.TYPE_MIXED_GROUP;
        }
        
        entry.setUserType(userType);
    }

    /**
     * Does the user have Manager access to the database?
     * 
     * <p>This method doesn't check the ACL's internet access level.  That
     * must be checked at a level that knows we are executing in the
     * HTTP task.
     * 
     * @param database
     * @return
     * @throws NotesException
     */
    private boolean hasManagerAccess(Database database) throws NotesException {
        boolean managerAccess = false;
        
        Session session = database.getParent();
        String user = session.getEffectiveUserName();
        if ( !StringUtil.isEmpty(user) ) {
            int level = database.queryAccess(user);
            if ( level >= ACL.LEVEL_MANAGER ) {
                managerAccess = true;
            }
        }

        return managerAccess;
    }
    
    /**
     * Gets an up-to-date copy of the profile document.
     * 
     * @param database
     * @return
     * @throws NotesException
     */
    private Document profileGet(Database database) throws NotesException {
        
        // Open the calendar profile.  This returns a cached copy (may not be up to date).
        
        Document profile = database.getProfileDocument("CalendarProfile", null); // $NON-NLS-1$
        String unid = profile.getUniversalID();
        profile.recycle();
        
        // Open the same document by UNID.  This ensures we get the latest copy.
        
        profile = database.getDocumentByUNID(unid);
        
        return profile;
    }
    
    /**
     * Adds a delegate to the calendar profile.
     * 
     * @param profile
     * @param delegate
     * @throws NotesException
     */
    private void profileAddDelegate(Document profile, Delegate delegate) throws NotesException {
        
        Session session = profile.getParentDatabase().getParent();
        Name name = session.createName(delegate.getName());
        DelegateAccess da = delegate.getAccess();
        String appendItems[] = null;
        
        if ( da.getWhat() == DelegateAccess.What.CALENDAR ) {
            if ( da.isCreate() || da.isEdit() || da.isDelete() ) {
                appendItems = new String[] {READ_CALENDAR_ITEM, WRITE_CALENDAR_ITEM};
            }
            else {
                appendItems = new String[] {READ_CALENDAR_ITEM};
            }
        }
        else if ( da.getWhat() == DelegateAccess.What.MAIL ) {
            if ( da.isEdit() ) {
                if ( da.isDelete() ) {
                    appendItems = new String[] {WRITE_CALENDAR_ITEM, EDIT_MAIL_ITEM, DELETE_MAIL_ITEM};
                }
                else {
                    appendItems = new String[] {WRITE_CALENDAR_ITEM, EDIT_MAIL_ITEM};
                }
            }
            else if ( da.isCreate() ) {
                if ( da.isDelete() ) {
                    appendItems = new String[] {WRITE_CALENDAR_ITEM, WRITE_MAIL_ITEM, DELETE_MAIL_ITEM};
                }
                else {
                    appendItems = new String[] {WRITE_CALENDAR_ITEM, WRITE_MAIL_ITEM};
                }
            }
            else {
                appendItems = new String[] {READ_MAIL_ITEM};
            }
        }
        
        // Do for each delegate access item
        
        if ( appendItems != null ) {
            for ( int i = 0; i < appendItems.length; i++ ) {
    
                // Read the item value
                Vector values = profile.getItemValue(appendItems[i]);
                
                // Add the name to the vector
                values.add(name.getCanonical());
                profile.replaceItemValue(appendItems[i], values);
            }
        }
    }
    
    /**
     * Removes a delegate from the calendar profile.
     * 
     * @param profile
     * @param delegateName
     * @throws NotesException
     */
    private void profileRemoveDelegate(Document profile, String delegateName) throws NotesException {
        Session session = profile.getParentDatabase().getParent();
        
        // Do for each delegate access item
        
        for ( int i = 0; i < s_items.length; i++ ) {

            // Read the item value
            Vector values = profile.getItemValue(s_items[i]);
            
            // Remove this name from the vector
            for ( int j = 0; j < values.size(); j++ ) {
                String strName = (String)values.get(j);
                Name name = session.createName(strName);
                if ( delegateName.equals(name.getAbbreviated())) {
                    values.remove(j);
                    profile.replaceItemValue(s_items[i], values);
                    break;
                }
            }
        }
    }
    
    private void validateDelegateAccess(DelegateAccess access) throws ModelException {
        
        // SPR# BBRL9S9AWZ:  You have to have access to something.
        
        if ( access.getWhat() == DelegateAccess.What.NOTHING ) {
            throw new ModelException("A delegate must have access to either mail or calendar.", ModelException.ERR_INVALID_INPUT); // $NLX-DelegateProvider.Adelegatemusthaveaccesstosomethin.1-1$
        }
        
        // SPR# XZHU9AD9FQ:  You can't have just delete access.
        
        if ( access.isDelete() && !(access.isRead() || access.isCreate() || access.isEdit()) ) {
            throw new ModelException("You must have at least read access to delete documents.", ModelException.ERR_INVALID_INPUT); // $NLX-DelegateProvider.Cannotdeletedocumentswithoutatlea-1$
        }
    }
    
}
    