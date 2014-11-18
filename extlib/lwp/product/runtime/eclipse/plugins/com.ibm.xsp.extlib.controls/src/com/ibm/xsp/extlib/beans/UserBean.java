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

import java.io.Serializable;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.social.PeopleService;
import com.ibm.xsp.extlib.social.Person;
import com.ibm.xsp.extlib.social.SocialServicesFactory;
import com.ibm.xsp.model.DataObject;


/**
 * Class that represents a the current user.
 * <p>
 * Such a bean is to be added to the request scope, representing the current user. Being
 * in the refresh scope allows it to be refreshed on a request basis. Internally, it delegates
 * to a person object which is stored in the session scope for caching purposes. 
 * </p>
 * @author Philippe Riand
 */
public class UserBean implements Person, DataObject, Serializable {

	public static final String BEAN_DATA = "extlib.people.userBeanData"; //$NON-NLS-1$
	
	private static final long serialVersionUID = 1L;

	public UserBean() {
	}

	
	// ======================================================================
	// Access to the bean itself
	// ======================================================================

	public static final String BEAN_NAME = "userBean"; //$NON-NLS-1$

	public static UserBean get(FacesContext context) {
		UserBean bean = (UserBean)context.getApplication().getVariableResolver().resolveVariable(context, BEAN_NAME);
		return bean;
	}
	
	public static UserBean get() {
		return get(FacesContext.getCurrentInstance());
	}

	
	// ======================================================================
	// Data Object
	// ======================================================================

    public Object getValue(Object key) {
		return ((DataObject)getPerson()).getValue(key); 
    }

    public Class<?> getType(Object key) {
		return ((DataObject)getPerson()).getType(key); 
    }

    public boolean isReadOnly(Object key) {
		return ((DataObject)getPerson()).isReadOnly(key); 
    }

    public void setValue(Object key, Object value) {
		((DataObject)getPerson()).setValue(key,value); 
    }

	
	// ======================================================================
	// Access to the bean properties
	// ======================================================================

    public Person getPerson(FacesContext context) {
        Person person = (Person)context.getExternalContext().getRequestMap().get(BEAN_DATA);
        if(person==null) {
            SocialServicesFactory factory = SocialServicesFactory.getInstance();
            PeopleService svc = factory.getPeopleService();
            person = svc.getPerson(factory.getAuthenticatedUserId(context));
            context.getExternalContext().getRequestMap().put(BEAN_DATA,person);
        }
    	return person;
    }
    public Person getPerson() {
        return getPerson(FacesContext.getCurrentInstance());
    }
    
	public String getDisplayName() {
		return getPerson().getDisplayName();
	}

	public Object getField(String id) {
		return getPerson().getField(id);
	}

    public Object getFieldByProvider(String provider, String key) {
        return getPerson().getFieldByProvider(provider, key);
    }

	public String getId() {
		return getPerson().getId();
	}

	public String getIdentity(String target) {
        return getPerson().getIdentity(target);
	}

	public boolean isAuthenticatedUser() {
        return getPerson().isAuthenticatedUser();
	}

	public String getThumbnailUrl() {
		return (String)getField(Person.FIELD_THUMBNAIL_URL);
	}

	public boolean isOwner() {
		return getPerson().isOwner();
	}

	public boolean isViewer() {
		return getPerson().isViewer();
	}

	public void setField(String id, Object value) {
	    getPerson().setField(id, value);
	}
}
