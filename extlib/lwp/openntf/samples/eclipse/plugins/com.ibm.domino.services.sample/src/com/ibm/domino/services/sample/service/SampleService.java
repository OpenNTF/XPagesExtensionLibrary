/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services.sample.service;

import java.util.HashSet;
import java.util.Set;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;
import com.ibm.domino.das.service.RestService;
import com.ibm.domino.services.sample.resources.ContactsListResource;
import com.ibm.domino.services.sample.resources.RootResource;


public class SampleService extends RestService {
    
    public static final LogMgr SAMPLE_SERVICE_LOGGER = Log.load("com.ibm.domino.services.sample"); // $NON-NLS-1$

    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        SAMPLE_SERVICE_LOGGER.getLogger().fine("Adding sample service resources."); // $NON-NLS-1$
        classes.add(RootResource.class);
        classes.add(ContactsListResource.class);
        return classes;
    }
    
}