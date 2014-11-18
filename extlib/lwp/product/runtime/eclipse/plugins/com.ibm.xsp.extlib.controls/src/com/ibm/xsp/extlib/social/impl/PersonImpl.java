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

package com.ibm.xsp.extlib.social.impl;

import java.security.Principal;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.beans.UserBean;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.social.PeopleService;
import com.ibm.xsp.extlib.social.Person;


/**
 * Person resource.
 * @author Philippe Riand
 */
public class PersonImpl extends ResourceImpl implements Person {

    private static final long serialVersionUID = 1L;

    public PersonImpl(PeopleService service, String id) {
        super(service,id);
    }

    @Override
    public PeopleService getService() {
        return (PeopleService)super.getService();
    }
    
    public String getIdentity(String target) {
        return getService().getUserIdentityFromId(target, getId());
    }
    
    
    public boolean isAuthenticatedUser() {
        Principal p = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(p!=null) {
            return StringUtil.equals(getId(), p.getName());
        }
        return false;
    }

    public boolean isOwner() {
        return false;
    }
    
    public boolean isViewer() {
        UserBean b = UserBean.get();
        return StringUtil.equals(b.getId(),getId());
    }
    
    @Override
    public Object getField(String key) {
        if(key.equals(FIELD_ID)) {
            return getId();
        }
        
        // Look at the different providers
        PeopleDataProvider[] dataProviders = (PeopleDataProvider[])getService().getResourceDataProviders();
        for(int i=0; i<dataProviders.length; i++) {
            PeopleDataProvider pd = dataProviders[i];
            if(pd instanceof AbstractPeopleDataProvider) {
                Object value = ((AbstractPeopleDataProvider)pd).getValue(this, key);
                if(value!=null) {
                    return value;
                }
            }
        }

        if(key.equals(FIELD_DISPLAYNAME)) {
            return getId();
        }
        if(key.equals(FIELD_OWNER)) {
            return isOwner();
        }
        if(key.equals(FIELD_VIEWER)) {
            return isViewer();
        }
        if(key.equals(FIELD_THUMBNAIL_URL)) {
            return ExtLibResources.noPhotoImg;
        }
        
        return super.getField(key); 
    }
    
    /**
     * This is mostly for dump/debug capability
     * @param key
     * @return
     */
    @Override
    public Object getFieldByProvider(String provider, String key) {
        PeopleDataProvider[] dataProviders = (PeopleDataProvider[])getService().getResourceDataProviders();
        for(int i=0; i<dataProviders.length; i++) {
            PeopleDataProvider pd = dataProviders[i];
            if(StringUtil.equals(pd.getName(),provider)) {
                if(pd instanceof AbstractPeopleDataProvider) {
                    Object value = ((AbstractPeopleDataProvider)pd).getValue(this, key);
                    return value;
                }
            }
        }
        return null; 
    }

    @Override
    public void setField(String key, Object value) {
        throw new FacesExceptionEx(null,"The PersonImpl object is read only. Use the sessionScope instead to store the user related data"); // $NLX-PersonImpl.ThePersonImplobjectisreadonlyUset-1$
    }
    
    @Override
    public Class<?> getType(Object key) {
        if(key.equals(FIELD_ID)) {
            return String.class;
        }
        PeopleDataProvider[] dataProviders = (PeopleDataProvider[])getService().getResourceDataProviders();
        
        // Look at the different providers
        for(int i=0; i<dataProviders.length; i++) {
            PeopleDataProvider pd = dataProviders[i];
            if(pd instanceof AbstractPeopleDataProvider) {
                Class<?> type = ((AbstractPeopleDataProvider)pd).getType(this, key);
                if(type!=null) {
                    return type;
                }
            }
        }

        // Else, look at the default properties
        if(key.equals(FIELD_DISPLAYNAME)) {
            return String.class;
        }
        if(key.equals(FIELD_OWNER)) {
            return Boolean.TYPE;
        }
        if(key.equals(FIELD_VIEWER)) {
            return Boolean.TYPE;
        }
        
        return super.getType(key);
    }

    
    public String getDisplayName() {
        return (String)getField(FIELD_DISPLAYNAME);
    }
    protected void setDisplayName(String displayName) {
        setField(FIELD_DISPLAYNAME,displayName);
    }
    
    public String getThumbnailUrl() {
        return (String)getField(FIELD_THUMBNAIL_URL);
    }
    protected void setThumbnailUrl(String thumbnailUrl) {
        setField(FIELD_THUMBNAIL_URL,thumbnailUrl);
    }
}