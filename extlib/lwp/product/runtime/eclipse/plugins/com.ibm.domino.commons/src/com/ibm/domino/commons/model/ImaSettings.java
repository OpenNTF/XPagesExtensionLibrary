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

import java.util.Map;

/**
 * IBM mail add-in settings
 */
public class ImaSettings {
    
    public static String FB_URL = "FB_URL"; //$NON-NLS-1$
    public static String PW_CHANGE_URL = "PW_CHANGE_URL"; //$NON-NLS-1$
    
    // The LDAP settings below are DEPRECATED
    
    public static String LDAP_SERVER = "LDAP_SERVER"; //$NON-NLS-1$
    public static String LDAP_USER_NAME = "LDAP_USER_NAME"; //$NON-NLS-1$
    public static String LDAP_PASSWORD = "LDAP_PASSWORD"; //$NON-NLS-1$
    public static String LDAP_PORT = "LDAP_PORT"; //$NON-NLS-1$
    public static String LDAP_USE_SSL = "LDAP_USE_SSL"; //$NON-NLS-1$
    public static String LDAP_TIMEOUT = "LDAP_TIMEOUT"; //$NON-NLS-1$
    public static String LDAP_MAX_ENTRIES = "LDAP_MAX_ENTRIES"; //$NON-NLS-1$
    public static String LDAP_CUSTOM_SEARCH_BASE = "LDAP_CUSTOM_SEARCH_BASE"; //$NON-NLS-1$ 
    
    private Map<String, Object> _settings;
    
    public ImaSettings(Map<String, Object> settings) {
        _settings = settings;
    }

    /**
     * @return the fbUrl
     */
    public String getFbUrl() {
        String fbUrl = null;
        
        Object setting = _settings.get(FB_URL);
        if ( setting instanceof String ) {
            fbUrl = (String)setting;
        }
        
        return fbUrl;
    }

    /**
     * @return the pwChangeUrl
     */
    public String getPwChangeUrl() {
        String pwChangeUrl = null;
        
        Object setting = _settings.get(PW_CHANGE_URL);
        if ( setting instanceof String ) {
            pwChangeUrl = (String)setting;
        }
        
        return pwChangeUrl;
    }

}
