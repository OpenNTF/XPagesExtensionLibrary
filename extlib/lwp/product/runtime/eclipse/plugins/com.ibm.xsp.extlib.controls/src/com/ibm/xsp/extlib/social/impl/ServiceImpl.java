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

import java.util.Map;

import javax.faces.context.ExternalContext;

import com.ibm.xsp.extlib.social.SocialService;


/**
 * Social service default implementation.
 * @author Philippe Riand
 */
public class ServiceImpl implements SocialService {

    public static final String CACHE_KEY = "xsp.extlib.social.cache"; // $NON-NLS-1$
    
    private ResourceDataProvider[] dataProviders;
    
    public ServiceImpl(ResourceDataProvider[] providers) {
        this.dataProviders = providers;
    }
    
    public ResourceDataProvider[] getResourceDataProviders() {
        return dataProviders;
    }
    
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    private static Map<String, Object> getApplicationMap(ExternalContext externalContext) {
        return (Map<String, Object>)externalContext.getApplicationMap();
    }
    
    public void clearCache() {
        for(int i=0; i<dataProviders.length; i++) {
            dataProviders[i].clearCache();
        }
    }
    
    public void clearCache(String id) {
        for(int i=0; i<dataProviders.length; i++) {
            dataProviders[i].clearCache(id);
        }
    }
}