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

import java.util.ArrayList;

import com.ibm.xsp.extlib.social.PeopleService;
import com.ibm.xsp.extlib.social.Person;


/**
 * People service default implementation.
 * @author Philippe Riand
 */
public class PeopleServiceImpl extends ServiceImpl implements PeopleService {

    private IdentityMapper[] identityProviders;
    
	public PeopleServiceImpl(PeopleDataProvider[] dataProviders, IdentityMapper[] identityProviders) {
		super(dataProviders);
		this.identityProviders = identityProviders;  
	}
	
    public String getUserIdentityFromId(String target, String id) {
        if(identityProviders!=null) {
            for(int i=0; i<identityProviders.length; i++) {
                IdentityMapper ip = identityProviders[i];
                String res = ip.getUserIdentityFromId(target, id);
                if(res!=null) {
                    return res;
                }
            }
            return null;
        }
        return null;
    }
    
    public String getUserIdFromIdentity(String target, String identity) {
        if(identityProviders!=null) {
            for(int i=0; i<identityProviders.length; i++) {
                IdentityMapper ip = identityProviders[i];
                String res = ip.getUserIdFromIdentity(target, identity);
                if(res!=null) {
                    return res;
                }
            }
            return null;
        }
        return null;
    }
    
	
	public Person getPerson(String id) {
		return new PersonImpl(this,id);
	}
	
	public Person[] getPersons(String[] ids, boolean initialize) {
		ArrayList<PersonImpl> persons = new ArrayList<PersonImpl>();
		if(ids!=null) {
			for(int i=0; i<ids.length; i++) {
				PersonImpl p = (PersonImpl)getPerson(ids[i]);
				persons.add(p);
			}
		}
		PersonImpl[] p = persons.toArray(new PersonImpl[persons.size()]);
		if(initialize) {
			PeopleDataProvider[] dataProviders = (PeopleDataProvider[])getResourceDataProviders();
			for(int i=0; i<dataProviders.length; i++) {
				PeopleDataProvider pd = dataProviders[i];
				if(pd instanceof AbstractPeopleDataProvider) {
					((AbstractPeopleDataProvider)pd).readValues(p); 
				}
			}
		}
		return p;
	}
}
