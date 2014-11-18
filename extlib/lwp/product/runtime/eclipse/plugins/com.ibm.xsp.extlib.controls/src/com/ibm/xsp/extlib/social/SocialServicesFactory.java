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

import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.Application;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.extlib.social.impl.IdentityMapper;
import com.ibm.xsp.extlib.social.impl.PeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PeopleServiceImpl;




/**
 * Get access to the social services
 * @author Philippe Riand
 */
public class SocialServicesFactory {

    private static final String SOCIAL_SERVICE_FACTORY = "com.ibm.xsp.extlib.social.SocialServicesFactory";  // $NON-NLS-1$
    private static final String SOCIAL_SERVICE_KEY     = "extlib.social.serviceFactory";  // $NON-NLS-1$
    
    private static final String PEOPLE_DATAPROVIDER_SERVICE = "com.ibm.xsp.extlib.social.PersonDataProvider";  // $NON-NLS-1$
    private static final String PEOPLE_IDENTITYPROVIDER_SERVICE = "com.ibm.xsp.extlib.social.IdentityProvider";  // $NON-NLS-1$
    
    private static final String PREF_PROVIDER = "extlib.people.provider"; //$NON-NLS-1$
    
    
//    public static SocialServicesFactory getInstance(FacesContext context) {
//        return getInstance();
//    }
    public static SocialServicesFactory getInstance() {
        // Use the Designer application as it is cleared-out after a deployment
        // So we read back the new class, if any
        SocialServicesFactory f = (SocialServicesFactory)Application.get().getObject(SOCIAL_SERVICE_KEY);
        if(f==null) {
            synchronized(SocialServicesFactory.class) {
                f = (SocialServicesFactory)Application.get().getObject(SOCIAL_SERVICE_KEY);
                if(f==null) {
                    List<SocialServicesFactory> l = AccessController.doPrivileged(new PrivilegedAction<List<SocialServicesFactory>>() {
                        public List<SocialServicesFactory> run() {
                            List<SocialServicesFactory> l = ApplicationEx.getInstance().findServices(SOCIAL_SERVICE_FACTORY);
                            return l;
                        }
                    });
                    if(!l.isEmpty()) {
                        f = l.get(0);
                    } else {
                        f = new SocialServicesFactory();
                    }
                    Application.get().putObject(SOCIAL_SERVICE_KEY,f);
                }
            }
        }
        return f;
    }
    
    private PeopleService peopleService;

    public SocialServicesFactory() {
    }

    
    ///// ID mapping
    
    public String getAuthenticatedUserId(FacesContext context) {
        Principal p = context.getExternalContext().getUserPrincipal();
        if(p!=null) {
            String name = p.getName();
            return name;
        }
        return null;
    }
    
    
    ///// People service
    public PeopleService getPeopleService() {
        if(peopleService==null) {
            // Execute everything in a privileged block as it accesses class loaders and read extension points
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    //
                    // Find the people providers
                    //

                    // Read the authorized providers
                    String providersProp = ApplicationEx.getInstance().getApplicationProperty(PREF_PROVIDER, null);
                    String[] providersList = StringUtil.splitString(providersProp, ';'); 
                    
                    List<PeopleDataProvider> allDataProviders = ApplicationEx.getInstance().findServices(PEOPLE_DATAPROVIDER_SERVICE);
                    List<PeopleDataProvider> dataProviders = new ArrayList<PeopleDataProvider>(allDataProviders.size());
                    for(int i=0; i<allDataProviders.size(); i++) {
                        PeopleDataProvider p = allDataProviders.get(i);
                        if(acceptPeopleDataProvider(providersList,p)) {
                            dataProviders.add(p);
                        }
                    }
                    PeopleDataProvider[] personDataProviders = dataProviders.toArray(new PeopleDataProvider[dataProviders.size()]);
                    sortDataProviders(providersList,personDataProviders);
                    
                    //
                    // Find the identity providers
                    //
                    List<IdentityMapper> allIdentityProviders = ApplicationEx.getInstance().findServices(PEOPLE_IDENTITYPROVIDER_SERVICE);
                    List<IdentityMapper> identityProvidersList = new ArrayList<IdentityMapper>(allIdentityProviders.size());
                    for(int i=0; i<allIdentityProviders.size(); i++) {
                        IdentityMapper p = allIdentityProviders.get(i);
                        if(acceptIdentityProvider(p)) {
                            identityProvidersList.add(p);
                        }
                    }
                    IdentityMapper[] identityProviders = identityProvidersList.toArray(new IdentityMapper[identityProvidersList.size()]);
                    
                    // Create the service
                    peopleService = createPeopleService(personDataProviders,identityProviders);
                    
                    return null;
                }
            });
        }
        return peopleService;
    }

    protected PeopleService createPeopleService(PeopleDataProvider[] providers, IdentityMapper[] identityProviders) {
        return new PeopleServiceImpl(providers,identityProviders);
    }
    
    protected boolean acceptPeopleDataProvider(String[] providersList, PeopleDataProvider provider) {
        if(provider.isDefaultProvider()) {
            return true;
        }
        if(indexOf(providersList, provider.getName())>=0) {
            return true;
        }
        return false;
    }

    protected boolean acceptIdentityProvider(IdentityMapper provider) {
        return true;
    }
    
    protected void sortDataProviders(final String[] providerList, PeopleDataProvider[] providers) {
        (new QuickSort.ObjectArray(providers) {
            @Override
            public int compare(Object o1, Object o2) {
                PeopleDataProvider p1 = (PeopleDataProvider)o1; 
                PeopleDataProvider p2 = (PeopleDataProvider)o2;
                // Compare their position in the list
                int p = indexOf(providerList, p1.getName())-indexOf(providerList, p2.getName());
                if(p!=0) {
                    return p;
                }
                // Then compare their respective weight
                int w1 = ((PeopleDataProvider)o1).getWeight();
                int w2 = ((PeopleDataProvider)o2).getWeight();
                return w1-w2;
            }
        }).sort();
    }
    
    private static int indexOf(String[] array, String s) {
        if(array!=null) {
            for(int i=0; i<array.length; i++) {
                if(array[i].equalsIgnoreCase(s)) {
                    return i;
                }
            }
        }
        return -1;
    }
}