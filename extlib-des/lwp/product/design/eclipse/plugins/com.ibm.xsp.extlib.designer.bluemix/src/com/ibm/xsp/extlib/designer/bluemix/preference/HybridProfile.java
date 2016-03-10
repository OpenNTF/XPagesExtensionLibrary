/*
 * © Copyright IBM Corp. 2016
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

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;

/**
 * @author Gary Marjoram
 *
 */
public class HybridProfile implements Cloneable {
    public  static final int MAX_HYBRID_PROFILES = 20;
    
    private static final DominoPreferenceManager _prefMgr = DominoPreferenceManager.getInstance();
    
    private String  _name;
    private String  _remoteServerAddress;
    private String  _remoteServerName;
    private String  _runtimeServerName;
    private String  _runtimeServerIdFile;
    private String  _runtimeServerIdPassword;
    private boolean _daEnabled;
    private String  _daDomainName;
    private String  _daDominoDirectory;
    
    public HybridProfile() {
        loadProfile(0, true);
    }
    
    private HybridProfile(int index) {
        loadProfile(index, false);
    }
    
    private void loadProfile(int index, boolean def) {
        // Read the profile from the preferences
        _name                = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_PROFILE_NAME, index), def);
        _remoteServerAddress = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_ADDR, index), def);
        _remoteServerName    = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_NAME, index), def);
        _runtimeServerName   = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, index), def);
        _runtimeServerIdFile = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, index), def);
        _daEnabled           = _prefMgr.getBooleanValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_ENABLE, index), def);
        _daDomainName        = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DOMAIN, index), def);
        _daDominoDirectory   = _prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, index), def);   
        
        // Server ID password is stored in the secure preferences
        if (def) {
            _runtimeServerIdPassword = "";
        } else {
            _runtimeServerIdPassword = PreferencePage.getSecurePreference(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, index), "");
        }
    }
    
    public void save(int index) {
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_PROFILE_NAME, index), _name);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_ADDR, index), _remoteServerAddress);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_NAME, index), _remoteServerName);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, index), _runtimeServerName);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, index), _runtimeServerIdFile);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_ENABLE, index), Boolean.toString(_daEnabled));
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DOMAIN, index), _daDomainName);
        _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, index), _daDominoDirectory);   
        
        // Server ID password is stored in the secure preferences
        PreferencePage.setSecurePreference(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, index), _runtimeServerIdPassword);                
    }
    
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name;
    }    
    
    public String getRemoteServerAddress() {
        return _remoteServerAddress;
    }

    public void setRemoteServerAddress(String remoteServerAddress) {
        _remoteServerAddress = remoteServerAddress;
    }

    public String getRemoteServerName() {
        return _remoteServerName;
    }

    public void setRemoteServerName(String remoteServerName) {
        _remoteServerName = remoteServerName;
    }

    public String getRuntimeServerName() {
        return _runtimeServerName;
    }

    public void setRuntimeServerName(String runtimeServerName) {
        _runtimeServerName = runtimeServerName;
    }

    public String getRuntimeServerIdFile() {
        return _runtimeServerIdFile;
    }

    public void setRuntimeServerIdFile(String runtimeServerIdFile) {
        _runtimeServerIdFile = runtimeServerIdFile;
    }

    public String getRuntimeServerIdPassword() {
        return _runtimeServerIdPassword;
    }

    public void setRuntimeServerIdPassword(String runtimeServerIdPassword) {
        _runtimeServerIdPassword = runtimeServerIdPassword;
    }

    public boolean isDaEnabled() {
        return _daEnabled;
    }

    public void setDaEnabled(boolean daEnabled) {
        _daEnabled = daEnabled;
    }

    public String getDaDomainName() {
        return _daDomainName;
    }

    public void setDaDomainName(String daDomainName) {
        _daDomainName = daDomainName;
    }

    public String getDaDominoDirectory() {
        return _daDominoDirectory;
    }

    public void setDaDominoDirectory(String daDominoDirectory) {
        _daDominoDirectory = daDominoDirectory;
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "clone", e, "Failed to clone"); // $NON-NLS-1$ $NLE-HybridProfile.Failedtoclone-2$
            }
            return null;
        }
    }
    
    public static HybridProfile load(int index) {
        if (exists(index)) {
            return new HybridProfile(index);
        }
        return null;
    }
    
    public static void delete(int index) {
        if (exists(index)) {
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_PROFILE_NAME, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_ADDR, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_SERVER_NAME, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_ENABLE, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DOMAIN, index), "");
            _prefMgr.setValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, index), "");   
            
            // Server ID password is stored in the secure preferences
            PreferencePage.setSecurePreference(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, index), "");        
        }
    }
    
    public static boolean exists(int index) {
        return (StringUtil.isNotEmpty(_prefMgr.getValue(getIndexedPrefKey(KEY_BLUEMIX_HYBRID_PROFILE_NAME, index), false)));
    }
    
    private static String getIndexedPrefKey(String prefKey, int index) {
        if (index == 0) {
            return prefKey;
        }
        return prefKey + "_" + index; 
    }   
}