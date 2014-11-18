/*
 * © Copyright IBM Corp. 2010, 2013
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

import javax.faces.context.FacesContext;

import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.platform.AbstractNotesDominoPlatform;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.JavaScriptUtil;


/**
 * Domino related user bean data provider.
 * <p>
 * </p>
 * @author Philippe Riand
 * @author Tony McGuckin, IBM
 */
public class DominoUserBeanDataProvider extends AbstractPeopleDataProvider {
    
    public static final String FIELD_COMMONNAME         = "commonName"; // $NON-NLS-1$
    public static final String FIELD_DISTINGUISHEDNAME  = "distinguishedName"; // $NON-NLS-1$
    public static final String FIELD_ABBREVIATEDNAME    = "abbreviatedName"; // $NON-NLS-1$
    public static final String FIELD_CANONICALNAME      = "canonicalName"; // $NON-NLS-1$
    public static final String FIELD_EFFECTIVEUSERNAME  = "effectiveUserName"; // $NON-NLS-1$
    
    private static final long serialVersionUID = 1L;
    
    // Internal class that stores the user related data
    public static class PeopleData extends PersonImpl.Properties {
        private static final long serialVersionUID = 1L;
        
        // User names
        String displayName;
        String abbreviatedName;
        String canonicalName;
        String effectiveUserName;
    }
    private static PeopleData EMPTY_DATA = new PeopleData(); 
    
    public DominoUserBeanDataProvider() {
    }
    
    @Override
    protected String getDefaultCacheScope() {
        return "global"; // $NON-NLS-1$
    }
    @Override
    protected int getDefaultCacheSize() {
        return 500;
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
        return "Domino"; // $NON-NLS-1$
    }

    @Override
    public void enumerateProperties(Set<String> propNames) {
        propNames.add(FIELD_COMMONNAME);
        propNames.add(FIELD_DISTINGUISHEDNAME);
        propNames.add(FIELD_ABBREVIATEDNAME);
        propNames.add(FIELD_CANONICALNAME);
        propNames.add(FIELD_EFFECTIVEUSERNAME);
    }

    @Override
    public int getWeight() {
        return WEIGHT_LOW;
    }

    @Override
    public Object getValue(PersonImpl person, Object key) {
        if(key.equals(PersonImpl.FIELD_DISPLAYNAME) || key.equals(FIELD_COMMONNAME)) {
            return getPeopleData(person).displayName;
        }
        if(key.equals(FIELD_DISTINGUISHEDNAME)) {
            return person.getId();
        }
        if(key.equals(FIELD_ABBREVIATEDNAME)) {
            return getPeopleData(person).abbreviatedName;
        }
        if(key.equals(FIELD_CANONICALNAME)) {
            return getPeopleData(person).canonicalName;
        }
        if(key.equals(FIELD_EFFECTIVEUSERNAME)) {
            return getPeopleData(person).effectiveUserName;
        }
        return null;
    }
    
    @Override
    public Class<?> getType(PersonImpl person, Object key) {
        if(key.equals(FIELD_ABBREVIATEDNAME)) {
            return String.class;
        }
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
                        // TODO get a Notes/Domino id from an identity provider...
                        Name n = session.createName(id);
                        data.displayName = getCommonName(session, id, n); //n.getCommon();
                        data.abbreviatedName = n.getAbbreviated();
                        data.canonicalName = n.getCanonical();
                        data.effectiveUserName = session.getEffectiveUserName();
                        addProperties(id,data);
                    } catch(NotesException ex) {
                        throw new FacesExceptionEx(ex,"Error while retrieving user names"); // $NLX-DominoUserBeanDataProvider.Errorwhileretrievingusernames-1$
                    }
                }
            }
        }
        return data;
    }
    protected String getCommonName(Session session, String id, Name n) throws NotesException {
    	// Ref SPR# PHAN94NCQA - account for LDAP server
        if(!StringUtil.equalsIgnoreCase(id, "Anonymous")) {  // $NON-NLS-1$
            String ldapName = (String)session.evaluate("@NameLookup([NoUpdate];\""+JavaScriptUtil.toJavaScriptString(id)+"\";\"cn\")").firstElement(); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ 
            if(!StringUtil.isSpace(ldapName)) {
                return ldapName;
            }
        }
        return n.getCommon();
    }
}