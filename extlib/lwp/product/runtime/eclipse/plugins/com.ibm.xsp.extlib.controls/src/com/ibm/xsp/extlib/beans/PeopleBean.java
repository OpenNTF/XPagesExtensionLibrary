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

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.social.PeopleService;
import com.ibm.xsp.extlib.social.Person;
import com.ibm.xsp.extlib.social.SocialServicesFactory;
import com.ibm.xsp.extlib.social.impl.PeopleServiceImpl;



/**
 * People bean.
 * <p>
 * Define a bean for accessing people social data.
 * </p>
 * @author Philippe Riand
 */
public class PeopleBean {

	public static final String BEAN_NAME = "peopleBean"; //$NON-NLS-1$
	
	public static PeopleBean get(FacesContext context) {
		PeopleBean bean = (PeopleBean)context.getApplication().getVariableResolver().resolveVariable(context, BEAN_NAME);
		return bean;
	}
	
	public static PeopleBean get() {
		return get(FacesContext.getCurrentInstance());
	}
	
	public PeopleBean() {
	}

	public Person get(String id) {
		PeopleService svc = SocialServicesFactory.getInstance().getPeopleService();
		return svc.getPerson(id);
	}

	public Person getPerson(String id) {
		PeopleService svc = SocialServicesFactory.getInstance().getPeopleService();
		return svc.getPerson(id);
	}
	
	public void refreshPerson(String id){
		PeopleServiceImpl service = (PeopleServiceImpl)SocialServicesFactory.getInstance().getPeopleService();
		service.clearCache(id);
		service.getPerson(id);
	}

	public PeopleService getPeopleService() {
        PeopleServiceImpl service = (PeopleServiceImpl)SocialServicesFactory.getInstance().getPeopleService();
        return service;
	}
	
    public void clearCache() {
        getPeopleService().clearCache();
    }
    
    public void clearcache(String id) {
        getPeopleService().clearCache(id);
    }	
}
