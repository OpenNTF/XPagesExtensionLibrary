/*
 * © Copyright IBM Corp. 2013
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

package com.ibm.domino.commons.model;

import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * Factory for Out of Office provider.
 * 
 * <p>In the future, this factory may eventually produce other provider interfaces.
 */
public class ProviderFactory {
    
    private static IOooStatusProvider s_oooProvider = null;
    private static IDelegateProvider s_delegateProvider = null;
    private static IFreeRoomsProvider s_freeRoomsProvider = null;
    private static ISiteProvider s_siteProvider = null;
    private static IImaSettingsProvider s_imaSettingsProvider = null;
    private static ILookupProvider s_lookupProvider = null;
    private static ITrustProvider s_trustProvider = null;
    private static IStatisticsProvider s_statsProvider = null;
    private static IRecentContactsProvider s_recentContactsProvider = null;
    private static IGatekeeperProvider s_gatekeeperProvider = null;
    private static ICustomerProvider s_customerProvider = null;
    private static IMutedThreadsProvider s_mutedThreadsProvider = null;
    
    private static IProviderLoader s_loader = null;
    private static boolean s_loaderNotFound = false;
    
    /**
     * Loads a provider instance from a domino.commons fragment.
     * 
     * @param clazz
     * @return
     */
    private static Object loadFromFragment(Class<?> clazz) {
        Object provider =  null;
        
        if ( s_loader == null && !s_loaderNotFound ) {
            // Initialize the loader
            try {
                Class<?> loaderClazz = Class.forName("com.ibm.domino.commons.model.ProviderLoader"); // $NON-NLS-1$
                s_loader = (IProviderLoader)loaderClazz.newInstance();
            } 
            catch (Throwable e) {
                s_loaderNotFound = true;
            }
        }
        
        if ( s_loader == null ) {
            
            // For backward compatibility with a 9.0.1 version of
            // domino.commons.ext, try some hard-coded class names.
            
            try {
                Class<?> oldClazz = null;
                if ( clazz.equals(IOooStatusProvider.class) ) {
                    oldClazz = Class.forName("com.ibm.domino.commons.model.OooStatusProvider"); // $NON-NLS-1$
                }
                else if ( clazz.equals(IImaSettingsProvider.class) ) {
                    oldClazz = Class.forName("com.ibm.domino.commons.model.ImaSettingsProvider"); // $NON-NLS-1$
                }
                
                if ( oldClazz != null ) {
                    provider = oldClazz.newInstance();
                }
            } 
            catch (Throwable e) {
                // Do nothing
            }
        }
        else {
            // Load the provider from the fragment
            provider = s_loader.load(clazz);
        }

        if ( provider != null && !clazz.isInstance(provider) ) {
            // Wrong type
            provider = null;
        }
        
        return provider;
    }
    
    public static IOooStatusProvider getOooStatusProvider() {
        if ( s_oooProvider == null ) {
            s_oooProvider = (IOooStatusProvider)loadFromFragment(IOooStatusProvider.class);
        }
        
        return s_oooProvider;
    }
    
    public static IImaSettingsProvider getImaSettingsProvider() {
        if ( s_imaSettingsProvider == null ) {
            s_imaSettingsProvider = (IImaSettingsProvider)loadFromFragment(IImaSettingsProvider.class);
        }
        return s_imaSettingsProvider;
    }
    
    public static IDelegateProvider getDelegateProvider(Session session) {
        if ( s_delegateProvider == null ) {
            s_delegateProvider = (IDelegateProvider)loadFromFragment(IDelegateProvider.class);

            if ( s_delegateProvider == null ) {
                boolean useOldProvider = false;
                
                try {
                    String var = session.getEnvironmentString("MailServiceDelegateWithACL", true); // $NON-NLS-1$
                    if ( "1".equals(var) ) {
                        useOldProvider = true;
                    }
                }
                catch (NotesException e) {
                    // Ignore
                }
                
                if ( useOldProvider ) {
                    s_delegateProvider = new DelegateProvider();
                }
                else {
                    s_delegateProvider = new Delegate901Provider();
                }
            }
        }
        
        return s_delegateProvider;
    }

    public static IFreeRoomsProvider getFreeRoomsProvider(Session session) {
        if ( s_freeRoomsProvider == null ) {
            s_freeRoomsProvider = (IFreeRoomsProvider)loadFromFragment(IFreeRoomsProvider.class);
            
            if ( s_freeRoomsProvider == null ) {
                boolean useOldProvider = false;
                
                try {
                    String var = session.getEnvironmentString("FbUseOldRoomsAPI", true); // $NON-NLS-1$
                    if ( "1".equals(var) ) {
                        useOldProvider = true;
                    }
                }
                catch (NotesException e) {
                    // Ignore
                }
                
                if ( useOldProvider ) {
                    s_freeRoomsProvider = new FreeRoomsProvider();
                }
                else {
                    s_freeRoomsProvider = new FreeRooms901Provider();
                }
            }
        }
        
        return s_freeRoomsProvider;
    }
    
    public static ISiteProvider getSiteProvider() {
        if ( s_siteProvider == null ) {
            s_siteProvider = (ISiteProvider)loadFromFragment(ISiteProvider.class);
            
            if ( s_siteProvider == null ) {
                s_siteProvider = new SiteProvider();
            }
        }
        
        return s_siteProvider;
    }
    
    public static ILookupProvider getLookupProvider() {
        if ( s_lookupProvider == null ) {
            s_lookupProvider = (ILookupProvider)loadFromFragment(ILookupProvider.class);
            
            if ( s_lookupProvider == null ) {
                s_lookupProvider = new LookupProvider();
            }
        }
        
        return s_lookupProvider;
    }
    
    public static ITrustProvider getTrustProvider() {
        if ( s_trustProvider == null ) {
            s_trustProvider = (ITrustProvider)loadFromFragment(ITrustProvider.class);
        }
        return s_trustProvider;
    }
    
    public static IStatisticsProvider getStatisticsProvider() {
        
        if ( s_statsProvider == null ) {
            s_statsProvider = (IStatisticsProvider)loadFromFragment(IStatisticsProvider.class);
        }
        return s_statsProvider;
    }
    
    public static IRecentContactsProvider getRecentContactsProvider() {
        if ( s_recentContactsProvider == null ) {
            s_recentContactsProvider = (IRecentContactsProvider)loadFromFragment(IRecentContactsProvider.class);
            
            if ( s_recentContactsProvider == null ) {
                s_recentContactsProvider = new RecentContactsProvider();
            }
        }
        
        return s_recentContactsProvider;
    }
    
    public static IGatekeeperProvider getGatekeeperProvider() {

        if ( s_gatekeeperProvider == null ) {
            s_gatekeeperProvider = (IGatekeeperProvider)loadFromFragment(IGatekeeperProvider.class);
            
            if ( s_gatekeeperProvider == null ) {
                
                // Return the default implemenation.  
                s_gatekeeperProvider = new IGatekeeperProvider() {

                    public boolean isFeatureEnabled(int feature, String CustomerID, String userID) {
                        // By default everything is enabled
                        return true;
                    }
                };
            }
        }

        return s_gatekeeperProvider;
    }
    
    public static ICustomerProvider getCustomerProvider() {
        if ( s_customerProvider == null ) {
            s_customerProvider = (ICustomerProvider)loadFromFragment(ICustomerProvider.class);
        }

        return s_customerProvider;
    }

    public static IMutedThreadsProvider getMutedThreadsProvider() {
        if ( s_mutedThreadsProvider == null ) {
            s_mutedThreadsProvider = (IMutedThreadsProvider)loadFromFragment(IMutedThreadsProvider.class);
        }

        return s_mutedThreadsProvider;
        
    }
}