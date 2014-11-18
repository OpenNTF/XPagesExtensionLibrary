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

package com.ibm.xsp.extlib.beans;

import java.util.Set;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.ACL;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.platform.AbstractNotesDominoPlatform;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;


/**
 * Domino related user bean data provider.
 * <p>
 * The data provided by this class are relative to the curent database.
 * </p>
 * @author Philippe Riand
 * @author Tony McGuckin, IBM
 */
public class DominoDBUserBeanDataProvider extends AbstractPeopleDataProvider {
    
    public static final String FIELD_DBACL_CREATE_DOCS = "canCreateDocs"; // $NON-NLS-1$
    public static final String FIELD_DBACL_DELETE_DOCS = "canDeleteDocs"; // $NON-NLS-1$
    public static final String FIELD_DBACL_CREATE_PRIV_AGENTS = "canCreatePrivAgents"; // $NON-NLS-1$
    public static final String FIELD_DBACL_CREATE_PRIV_FOLDERS_VIEWS = "canCreatePrivFoldersViews"; // $NON-NLS-1$
    public static final String FIELD_DBACL_CREATE_SHARED_FOLDERS_VIEWS = "canCreateSharedFoldersViews"; // $NON-NLS-1$
    public static final String FIELD_DBACL_CREATE_SCRIPT_AGENTS = "canCreateScriptAgents"; // $NON-NLS-1$
    public static final String FIELD_DBACL_READ_PUBLIC_DOCS = "canReadPublicDocs"; // $NON-NLS-1$
    public static final String FIELD_DBACL_WRITE_PUBLIC_DOCS = "canWritePublicDocs"; // $NON-NLS-1$
    public static final String FIELD_DBACL_REPLICATE_COPY_DOCS = "canReplicateCopyDocs"; // $NON-NLS-1$
    
    public static final String FIELD_DBACL_ACCESS_LEVEL = "accessLevel"; // $NON-NLS-1$
    public static final String FIELD_DBACL_ACCESS_LEVEL_AS_STRING = "accessLevelAsString"; // $NON-NLS-1$
    
    public static final String FIELD_DBACL_ACCESS_ROLES = "accessRoles"; // $NON-NLS-1$
    
    private static final long serialVersionUID = 1L;
    
    // Internal class that stores the user related data
    public static class PeopleData extends PersonImpl.Properties {
        private static final long serialVersionUID = 1L;
        
        int DBACL_CREATE_DOCS;
        int DBACL_DELETE_DOCS;
        int DBACL_CREATE_PRIV_AGENTS;
        int DBACL_CREATE_PRIV_FOLDERS_VIEWS;
        int DBACL_CREATE_SHARED_FOLDERS_VIEWS;
        int DBACL_CREATE_SCRIPT_AGENTS;
        int DBACL_READ_PUBLIC_DOCS;
        int DBACL_WRITE_PUBLIC_DOCS;
        int DBACL_REPLICATE_COPY_DOCS;
        
        int DBACL_ACCESS_LEVEL;
        String DBACL_ACCESS_LEVEL_AS_STRING;
        
        Vector<String> DBACL_ACCESS_ROLES;
    }
    private static PeopleData EMPTY_DATA = new PeopleData(); 
    
    public static final String PROP_SIZE = "extlib.social.people.dominodb.cachesize"; // $NON-NLS-1$
    public static final String PROP_SCOPE = "extlib.social.people.dominodb.scope"; // $NON-NLS-1$

    
    public DominoDBUserBeanDataProvider() {
    }
    
    @Override
    protected String getDefaultCacheScope() {
        return "application"; // $NON-NLS-1$
    }
    @Override
    protected int getDefaultCacheSize() {
        return 100;
    }

    @Override
    public boolean isDefaultProvider() {
        // If running on Notes/Domino, then we become a default provider
        if(Platform.getInstance() instanceof AbstractNotesDominoPlatform) {
            return true;
        }
        return super.isDefaultProvider();
    }
    
    @Override
    public String getName() {
        return "DominoDB"; // $NON-NLS-1$
    }

    @Override
    public void enumerateProperties(Set<String> propNames) {
        propNames.add(FIELD_DBACL_CREATE_DOCS);
        propNames.add(FIELD_DBACL_DELETE_DOCS);
        propNames.add(FIELD_DBACL_CREATE_PRIV_AGENTS);
        propNames.add(FIELD_DBACL_CREATE_PRIV_FOLDERS_VIEWS);
        propNames.add(FIELD_DBACL_CREATE_SHARED_FOLDERS_VIEWS);
        propNames.add(FIELD_DBACL_CREATE_SCRIPT_AGENTS);
        propNames.add(FIELD_DBACL_READ_PUBLIC_DOCS);
        propNames.add(FIELD_DBACL_WRITE_PUBLIC_DOCS);
        propNames.add(FIELD_DBACL_REPLICATE_COPY_DOCS);
        
        propNames.add(FIELD_DBACL_ACCESS_LEVEL);
        propNames.add(FIELD_DBACL_ACCESS_LEVEL_AS_STRING);
        
        propNames.add(FIELD_DBACL_ACCESS_ROLES);
    }

    @Override
    public int getWeight() {
        return WEIGHT_LOW+1;
    }

    @Override
    public Object getValue(PersonImpl person, Object key) {
        if(key.equals(FIELD_DBACL_CREATE_DOCS)){
            return (getPeopleData(person).DBACL_CREATE_DOCS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_DELETE_DOCS)){
            return (getPeopleData(person).DBACL_DELETE_DOCS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_CREATE_PRIV_AGENTS)){
            return (getPeopleData(person).DBACL_CREATE_PRIV_AGENTS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_CREATE_PRIV_FOLDERS_VIEWS)){
            return (getPeopleData(person).DBACL_CREATE_PRIV_FOLDERS_VIEWS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_CREATE_SHARED_FOLDERS_VIEWS)){
            return (getPeopleData(person).DBACL_CREATE_SHARED_FOLDERS_VIEWS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_CREATE_SCRIPT_AGENTS)){
            return (getPeopleData(person).DBACL_CREATE_SCRIPT_AGENTS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_READ_PUBLIC_DOCS)){
            return (getPeopleData(person).DBACL_READ_PUBLIC_DOCS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_WRITE_PUBLIC_DOCS)){
            return (getPeopleData(person).DBACL_WRITE_PUBLIC_DOCS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_REPLICATE_COPY_DOCS)){
            return (getPeopleData(person).DBACL_REPLICATE_COPY_DOCS > 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if(key.equals(FIELD_DBACL_ACCESS_LEVEL)){
            switch(getPeopleData(person).DBACL_ACCESS_LEVEL){
                case(ACL.LEVEL_NOACCESS) : {
                    return new Integer(ACL.LEVEL_NOACCESS);
                }
                case(ACL.LEVEL_DEPOSITOR) : {
                    return new Integer(ACL.LEVEL_DEPOSITOR);
                }
                case(ACL.LEVEL_READER) : {
                    return new Integer(ACL.LEVEL_READER);
                }
                case(ACL.LEVEL_AUTHOR) : {
                    return new Integer(ACL.LEVEL_AUTHOR);
                }
                case(ACL.LEVEL_EDITOR) : {
                    return new Integer(ACL.LEVEL_EDITOR);
                }
                case(ACL.LEVEL_DESIGNER) : {
                    return new Integer(ACL.LEVEL_DESIGNER);
                }
                case(ACL.LEVEL_MANAGER) : {
                    return new Integer(ACL.LEVEL_MANAGER);
                }
                default : {
                    return new Integer(ACL.LEVEL_NOACCESS);
                }
            }
        }
        if(key.equals(FIELD_DBACL_ACCESS_LEVEL_AS_STRING)){
            switch(getPeopleData(person).DBACL_ACCESS_LEVEL){
                case(ACL.LEVEL_NOACCESS) : {
                    return "NOACCESS"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_DEPOSITOR) : {
                    return "DEPOSITOR"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_READER) : {
                    return "READER"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_AUTHOR) : {
                    return "AUTHOR"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_EDITOR) : {
                    return "EDITOR"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_DESIGNER) : {
                    return "DESIGNER"; // $NON-NLS-1$
                }
                case(ACL.LEVEL_MANAGER) : {
                    return "MANAGER"; // $NON-NLS-1$
                }
                default : {
                    return "NOACCESS"; // $NON-NLS-1$
                }
            }
        }
        if(key.equals(FIELD_DBACL_ACCESS_ROLES)){
            Vector<String> rolesVector = getPeopleData(person).DBACL_ACCESS_ROLES;
            int i = rolesVector.size();
            String[] rolesArray = new String[i];
            return rolesVector.toArray(rolesArray);
        }
        return null;
    }
    
    @Override
    public Class<?> getType(PersonImpl person, Object key) {
        return null;
    }
    
    @Override
    public void readValues(PersonImpl[] persons) {
        for(int i=0; i<persons.length; i++) {
            getPeopleData(persons[i]);
        }
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    private PeopleData getPeopleData(PersonImpl person) {
        String id = person.getId();
        if(StringUtil.isEmpty(id)) {
            return EMPTY_DATA;
        }
        PeopleData data = (PeopleData)getProperties(id,PeopleData.class);
        if(data==null) {
            synchronized(getSyncObject()) {
                data = (PeopleData)getProperties(id,PeopleData.class);
                if(data==null) {
                    try {
                        data = new PeopleData(); 
                        Session session = ExtLibUtil.getCurrentSession(FacesContext.getCurrentInstance());
                        lotus.domino.Database database = session.getCurrentDatabase();
                        // TODO get a Notes/Domino id from an identity provider...
                        String effectiveUserName = session.getEffectiveUserName();
                        data.DBACL_CREATE_DOCS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_CREATE_DOCS;
                        data.DBACL_DELETE_DOCS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_DELETE_DOCS;
                        data.DBACL_CREATE_PRIV_AGENTS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_CREATE_PRIV_AGENTS;
                        data.DBACL_CREATE_PRIV_FOLDERS_VIEWS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_CREATE_PRIV_FOLDERS_VIEWS;
                        data.DBACL_CREATE_SHARED_FOLDERS_VIEWS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_CREATE_SHARED_FOLDERS_VIEWS;
                        data.DBACL_CREATE_SCRIPT_AGENTS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_CREATE_SCRIPT_AGENTS;
                        data.DBACL_READ_PUBLIC_DOCS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_READ_PUBLIC_DOCS;
                        data.DBACL_WRITE_PUBLIC_DOCS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_WRITE_PUBLIC_DOCS;
                        data.DBACL_REPLICATE_COPY_DOCS = database.queryAccessPrivileges(effectiveUserName) & lotus.domino.Database.DBACL_REPLICATE_COPY_DOCS;
                        data.DBACL_ACCESS_LEVEL = database.queryAccess(effectiveUserName);
                        data.DBACL_ACCESS_ROLES = database.queryAccessRoles(effectiveUserName);
                        addProperties(id,data);
                    } catch(NotesException ex) {
                        throw new FacesExceptionEx(ex,"Error while retrieving user names"); // $NLX-DominoDBUserBeanDataProvider.Errorwhileretrievingusernames-1$
                    }
                }
            }
        }
        return data;
    }
}