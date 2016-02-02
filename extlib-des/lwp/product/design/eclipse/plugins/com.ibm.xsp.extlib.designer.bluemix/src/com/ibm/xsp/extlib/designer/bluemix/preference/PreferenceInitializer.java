/*
 * © Copyright IBM Corp. 2015, 2016
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

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.AbstractDominoPreferenceInitializer;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class PreferenceInitializer extends AbstractDominoPreferenceInitializer {

    public PreferenceInitializer() {
        if (StringUtil.isEmpty(prefMgr.getValue(KEY_BLUEMIX_HYBRID_PROFILE_NAME, false))) {
            // No profile names - check do we have an old style profile
            if (StringUtil.isNotEmpty(prefMgr.getValue(KEY_BLUEMIX_HYBRID_SERVER_ADDR, false)) &&
                StringUtil.isNotEmpty(prefMgr.getValue(KEY_BLUEMIX_HYBRID_SERVER_NAME, false)) &&
                StringUtil.isNotEmpty(prefMgr.getValue(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, false)) &&
                StringUtil.isNotEmpty(prefMgr.getValue(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, false))) {
                
                // There's an old valid profile - Make a name for it
                prefMgr.setValue(KEY_BLUEMIX_HYBRID_PROFILE_NAME, "Hybrid Profile 1"); // $NLX-PreferenceInitializer.HybridProfile1-1$
            }
        }
    }

    @Override
    public void initializeDefaultPreferences() {
        prefMgr.setDefault(KEY_BLUEMIX_DEPLOY_WAIT, true);
        prefMgr.setDefault(KEY_BLUEMIX_DEPLOY_WAIT_TIMEOUT, "180");
        prefMgr.setDefault(KEY_BLUEMIX_DEPLOY_WAIT_SHOW_SUCCESS, true);
        prefMgr.setDefault(KEY_BLUEMIX_DEPLOY_COPY_METHOD, "copy"); // $NON-NLS-1$
        prefMgr.setDefault(KEY_BLUEMIX_IMPORT_COPY_METHOD, "copy"); // $NON-NLS-1$
        
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_PROFILE_NAME, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_SERVER_ADDR, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_SERVER_NAME, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_DA_ENABLE, false);
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_DA_DOMAIN, "");
        prefMgr.setDefault(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, "names.nsf"); // $NON-NLS-1$
    }
}