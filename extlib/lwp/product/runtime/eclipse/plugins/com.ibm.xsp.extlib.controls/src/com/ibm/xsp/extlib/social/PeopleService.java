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

package com.ibm.xsp.extlib.social;


/**
 * People service.
 * @author Philippe Riand
 */
public interface PeopleService extends SocialService {

    // Selection fields
    public static final String SELECTOR_SELF        = "@self";   // $NON-NLS-1$
    public static final String SELECTOR_ALL         = "@all";    // $NON-NLS-1$
    public static final String SELECTOR_FRIENDS     = "@friends";    // $NON-NLS-1$
    
    public Person getPerson(String id);
    
    public Person[] getPersons(String[] ids, boolean initialize);
    
    public String getUserIdentityFromId(String target, String id);
    
    public String getUserIdFromIdentity(String target, String identity);
}