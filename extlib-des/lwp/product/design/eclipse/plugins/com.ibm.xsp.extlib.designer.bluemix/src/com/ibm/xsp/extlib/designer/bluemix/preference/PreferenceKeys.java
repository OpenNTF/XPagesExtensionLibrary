/*
 * © Copyright IBM Corp. 2015
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

package com.ibm.xsp.extlib.designer.bluemix.preference;

/**
 * @author Gary Marjoram
 *
 */
public class PreferenceKeys {
    
    // Secure Preferences - Not stored in the workspace
    public static final String KEY_BLUEMIX_SERVER_URL               = "domino.prefs.keys.bluemix.server.url";                // $NON-NLS-1$
    public static final String KEY_BLUEMIX_SERVER_USERNAME          = "domino.prefs.keys.bluemix.server.username";           // $NON-NLS-1$
    public static final String KEY_BLUEMIX_SERVER_PASSWORD          = "domino.prefs.keys.bluemix.server.password";           // $NON-NLS-1$

    // Normal Preferences
    public static final String KEY_BLUEMIX_DEPLOY_WAIT              = "domino.prefs.keys.bluemix.deploy.wait";               // $NON-NLS-1$
    public static final String KEY_BLUEMIX_DEPLOY_WAIT_TIMEOUT      = "domino.prefs.keys.bluemix.deploy.wait.timeout";       // $NON-NLS-1$
    public static final String KEY_BLUEMIX_DEPLOY_WAIT_SHOW_SUCCESS = "domino.prefs.keys.bluemix.deploy.wait.show.success";  // $NON-NLS-1$
    public static final String KEY_BLUEMIX_DEPLOY_COPY_METHOD       = "domino.prefs.keys.bluemix.deploy.copy.method";        // $NON-NLS-1$
    
    public static final String KEY_BLUEMIX_IMPORT_COPY_METHOD       = "domino.prefs.keys.bluemix.import.copy.method";        // $NON-NLS-1$
}