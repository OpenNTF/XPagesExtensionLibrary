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

package com.ibm.xsp.extlib.designer.bluemix.manifest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestBaseProps {

    public final static String       MEMORY_TAG     = "memory";     // $NON-NLS-1$
    public final static String       INSTANCES_TAG  = "instances";  // $NON-NLS-1$
    public final static String       BUILD_PACK_TAG = "buildpack";  // $NON-NLS-1$
    public final static String       COMMAND_TAG    = "command";    // $NON-NLS-1$
    public final static String       DOMAIN_TAG     = "domain";     // $NON-NLS-1$
    public final static String       HOST_TAG       = "host";       // $NON-NLS-1$
    public final static String       TIMEOUT_TAG    = "timeout";    // $NON-NLS-1$
    public final static String       STACK_TAG      = "stack";      // $NON-NLS-1$
    public final static String       NO_ROUTE_TAG   = "no-route";   // $NON-NLS-1$
    public final static String       PATH_TAG       = "path";       // $NON-NLS-1$
    public final static String       ENV_TAG        = "env";        // $NON-NLS-1$
    public final static String       DISK_QUOTA_TAG = "disk_quota"; // $NON-NLS-1$
    public final static String       SERVICES_TAG   = "services";   // $NON-NLS-1$
    public final static String       HOSTS_TAG      = "hosts";      // $NON-NLS-1$
    public final static String       DOMAINS_TAG    = "domains";    // $NON-NLS-1$
    
    // App Environment Variable Tags
    public final static String       APP_HOME_URL_TAG                   = "APP_HOME_URL";                   // $NON-NLS-1$
    public final static String       APP_PRELOAD_DB                     = "APP_PRELOAD_DB";                 // $NON-NLS-1$
    public final static String       APP_INCLUDE_XPAGES_TOOLBOX         = "APP_INCLUDE_XPAGES_TOOLBOX";     // $NON-NLS-1$
    public final static String       APP_JVM_HEAPSIZE                   = "APP_JVM_HEAPSIZE";               // $NON-NLS-1$
    public final static String       APP_VERBOSE_STAGING                = "APP_VERBOSE_STAGING";            // $NON-NLS-1$
    public final static String       APP_JAVA_POLICY_ALL_PERMISSION     = "APP_JAVA_POLICY_ALL_PERMISSION"; // $NON-NLS-1$
    public final static String       APP_REDIRECT_TO_SSL                = "APP_REDIRECT_TO_SSL";            // $NON-NLS-1$
    public final static String       APP_DEBUG_DIRECTORY_ASSISTANCE     = "APP_DEBUG_DIRECTORY_ASSISTANCE"; // $NON-NLS-1$
    public final static String       APP_DEBUG_NAMELOOKUP               = "APP_DEBUG_NAMELOOKUP";           // $NON-NLS-1$
    public final static String       APP_DEBUG_THREADS                  = "APP_DEBUG_THREADS";              // $NON-NLS-1$
    public final static String       APP_DEBUG_STAGING                  = "APP_DEBUG_STAGING";              // $NON-NLS-1$
    public final static String       APP_REMOTE_DATA_SERVER_ADDRESS     = "APP_REMOTE_DATA_SERVER_ADDRESS"; // $NON-NLS-1$
    public final static String       APP_REMOTE_DATA_SERVER_NAME        = "APP_REMOTE_DATA_SERVER_NAME";    // $NON-NLS-1$
    public final static String       APP_RUNTIME_SERVER_NAME            = "APP_RUNTIME_SERVER_NAME";        // $NON-NLS-1$
    public final static String       APP_RUNTIME_SERVER_IDFILE          = "APP_RUNTIME_SERVER_IDFILE";      // $NON-NLS-1$
    public final static String       APP_RUNTIME_SERVER_PASSWORD        = "APP_RUNTIME_SERVER_PASSWORD";    // $NON-NLS-1$
    public final static String       APP_DA_ENABLED                     = "APP_DA_ENABLED";                 // $NON-NLS-1$
    public final static String       APP_DA_DOMAIN                      = "APP_DA_DOMAIN";                  // $NON-NLS-1$
    public final static String       APP_DA_ADDRESS_BOOK                = "APP_DA_ADDRESS_BOOK";            // $NON-NLS-1$
    

    private Integer                  _memory;
    private Integer                  _instances;
    private Integer                  _diskQuota;
    private String                   _buildPack;
    private String                   _command;
    private String                   _host;
    private String                   _domain;
    private Integer                  _timeout;
    private String                   _stack;
    private Boolean                  _noRoute;
    private String                   _path;
    private Map<String, Object>      _env;
    private List<String>             _services;
    private List<String>             _hosts;
    private List<String>             _domains;
    
    // App Environment Variables
    private String                   _appHomeUrl;
    private String                   _appPreloadDb;
    private Boolean                  _appIncludeXPagesToolbox;
    private Integer                  _appJvmHeapsize;
    private Boolean                  _appVerboseStaging;
    private Boolean                  _appJavaPolicyAllPermission;
    private Boolean                  _appRedirectToSSL;
    private Boolean                  _appDebugDa;
    private Boolean                  _appDebugNameLookup;
    private Boolean                  _appDebugThreads;
    private Boolean                  _appDebugStaging;
    private String                   _appRemoteDataServerAddress;
    private String                   _appRemoteDataServerName;
    private String                   _appRuntimeServerName;
    private String                   _appRuntimeServerIdfile;
    private String                   _appRuntimeServerPassword;
    private Boolean                  _appDaEnabled;
    private String                   _appDaDomain;
    private String                   _appDaAddressBook;

    public ManifestBaseProps() {
    }

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public ManifestBaseProps(Map<Object, Object> manifest) {
        if (manifest != null) {
            _memory = ManifestUtil.getMemoryValue(manifest, MEMORY_TAG, "M", "G");
            _instances = ManifestUtil.getIntegerValue(manifest, INSTANCES_TAG);
            _diskQuota = ManifestUtil.getMemoryValue(manifest, DISK_QUOTA_TAG, "M", "G");
            _buildPack = ManifestUtil.getStringValue(manifest, BUILD_PACK_TAG);
            _command = ManifestUtil.getCommandValue(manifest, COMMAND_TAG);
            _host = ManifestUtil.getStringValue(manifest, HOST_TAG);
            _domain = ManifestUtil.getStringValue(manifest, DOMAIN_TAG);
            _timeout = ManifestUtil.getIntegerValue(manifest, TIMEOUT_TAG);
            _stack = ManifestUtil.getStringValue(manifest, STACK_TAG);
            _noRoute = ManifestUtil.getBooleanValue(manifest, NO_ROUTE_TAG);
            _path = ManifestUtil.getStringValue(manifest, PATH_TAG);
            _env = ManifestUtil.getMapValueAsStrings(manifest, ENV_TAG);
            _services = (List<String>) ManifestUtil.getListValue(manifest, SERVICES_TAG);
            _hosts = (List<String>) ManifestUtil.getListValue(manifest, HOSTS_TAG);
            _domains = (List<String>) ManifestUtil.getListValue(manifest, DOMAINS_TAG);
            extractAppEnv();
        }
    }
    
    public Integer getMemory() {
        return _memory;
    }

    public void setMemory(Integer memory) {
        _memory = memory;
    }

    public Integer getInstances() {
        return _instances;
    }

    public void setInstances(Integer instances) {
        _instances = instances;
    }

    public Integer getDiskQuota() {
        return _diskQuota;
    }

    public void setDiskQuota(Integer diskQuota) {
        _diskQuota = diskQuota;
    }

    public String getBuildPack() {
        return _buildPack;
    }

    public void setBuildPack(String buildPack) {
        _buildPack = buildPack;
    }

    public String getCommand() {
        return _command;
    }

    public void setCommand(String command) {
        _command = command;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getDomain() {
        return _domain;
    }

    public void setDomain(String domain) {
        _domain = domain;
    }

    public Integer getTimeout() {
        return _timeout;
    }

    public void setTimeout(Integer timeout) {
        _timeout = timeout;
    }

    public String getStack() {
        return _stack;
    }

    public void setStack(String stack) {
        _stack = stack;
    }

    public Boolean getNoRoute() {
        return _noRoute;
    }

    public void setNoRoute(Boolean noRoute) {
        if (noRoute.booleanValue() == false) {
            _noRoute = null;
        } else {
            _noRoute = noRoute;
        }
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public Map<String, Object> getUserEnv() {
        return _env;
    }

    public void setUserEnv(Map<String, Object> env) {
        _env = env;
        
        // User may have enterered an App env in the User Env Section
        // Pull them out here
        extractAppEnv();        
    }
    
    public LinkedHashMap<String, Object> getEnv() {
        LinkedHashMap<String, Object> newMap;
        if (_env != null) {
            newMap = new LinkedHashMap<String, Object>(_env);
        } else {
            newMap = new LinkedHashMap<String, Object>();
        }
        
        // Add the App Env
        ManifestUtil.setStringValue(newMap, APP_HOME_URL_TAG, _appHomeUrl, true);
        ManifestUtil.setStringValue(newMap, APP_PRELOAD_DB, _appPreloadDb, true);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_INCLUDE_XPAGES_TOOLBOX, _appIncludeXPagesToolbox);
        ManifestUtil.setMemoryValue(newMap, APP_JVM_HEAPSIZE, _appJvmHeapsize, "MB", null); // $NON-NLS-1$
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_VERBOSE_STAGING, _appVerboseStaging);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_JAVA_POLICY_ALL_PERMISSION, _appJavaPolicyAllPermission);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_REDIRECT_TO_SSL, _appRedirectToSSL);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_DEBUG_DIRECTORY_ASSISTANCE, _appDebugDa);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_DEBUG_NAMELOOKUP, _appDebugNameLookup);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_DEBUG_THREADS, _appDebugThreads);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_DEBUG_STAGING, _appDebugStaging);

        ManifestUtil.setStringValue(newMap, APP_REMOTE_DATA_SERVER_ADDRESS, _appRemoteDataServerAddress, true);
        ManifestUtil.setStringValue(newMap, APP_REMOTE_DATA_SERVER_NAME, _appRemoteDataServerName, true);
        ManifestUtil.setStringValue(newMap, APP_RUNTIME_SERVER_NAME, _appRuntimeServerName, true);
        ManifestUtil.setStringValue(newMap, APP_RUNTIME_SERVER_IDFILE, _appRuntimeServerIdfile, true);
        ManifestUtil.setStringValue(newMap, APP_RUNTIME_SERVER_PASSWORD, _appRuntimeServerPassword, true);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_DA_ENABLED, _appDaEnabled);
        ManifestUtil.setStringValue(newMap, APP_DA_DOMAIN, _appDaDomain, true);
        ManifestUtil.setStringValue(newMap, APP_DA_ADDRESS_BOOK, _appDaAddressBook, true);
        
        return newMap.size() > 0 ? newMap : null;
    }       

    public List<String> getServices() {
        return _services;
    }

    public void setServices(List<String> services) {
        _services = services;
    }    
    
    public List<String> getHosts() {
        return _hosts;
    }

    public void setHosts(List<String> hosts) {
        _hosts = hosts;
    }    

    public List<String> getDomains() {
        return _domains;
    }

    public void setDomains(List<String> domains) {
        _domains = domains;
    }    

    public String getAppHomeUrl() {
        return _appHomeUrl;
    }
    
    public void setAppHomeUrl(String appHomeUrl) {
        _appHomeUrl = appHomeUrl;
    }
    
    public String getAppPreloadDb() {
        return _appPreloadDb;
    }

    public void setAppPreloadDb(String appPreloadDb) {
        _appPreloadDb = appPreloadDb;
    }

    public Boolean getAppIncludeXPagesToolbox() {
        return _appIncludeXPagesToolbox;
    }

    public void setAppIncludeXPagesToolbox(Boolean appIncludeXPagesToolbox) {
        _appIncludeXPagesToolbox = appIncludeXPagesToolbox;
    }

    public Integer getAppJvmHeapsize() {
        return _appJvmHeapsize;
    }

    public void setAppJvmHeapsize(Integer appJvmHeapsize) {
        if (appJvmHeapsize == 0) {
            // Apply a default of 256MB
            _appJvmHeapsize = 256;
        } else {
            _appJvmHeapsize = appJvmHeapsize;
        }
    }

    public Boolean getAppVerboseStaging() {
        return _appVerboseStaging;
    }

    public void setAppVerboseStaging(Boolean appVerboseStaging) {
        _appVerboseStaging = appVerboseStaging;
    }

    public Boolean getAppJavaPolicyAllPermission() {
        return _appJavaPolicyAllPermission;
    }

    public void setAppJavaPolicyAllPermission(Boolean appJavaPolicyAllPermission) {
        _appJavaPolicyAllPermission = appJavaPolicyAllPermission;
    }

    public Boolean getAppRedirectToSSL() {
        if (_appRedirectToSSL == null) {
            return true;
        }
        return _appRedirectToSSL;
    }

    public void setAppRedirectToSSL(Boolean appRedirectToSSL) {
        _appRedirectToSSL = appRedirectToSSL;
    }

    public Boolean getAppDebugDa() {
        return _appDebugDa;
    }

    public void setAppDebugDa(Boolean appDebugDa) {
        _appDebugDa = appDebugDa;
    }

    public Boolean getAppDebugNameLookup() {
        return _appDebugNameLookup;
    }

    public void setAppDebugNameLookup(Boolean appDebugNameLookup) {
        _appDebugNameLookup = appDebugNameLookup;
    }

    public Boolean getAppDebugThreads() {
        return _appDebugThreads;
    }

    public void setAppDebugThreads(Boolean appDebugThreads) {
        _appDebugThreads = appDebugThreads;
    }

    public Boolean getAppDebugStaging() {
        return _appDebugStaging;
    }

    public void setAppDebugStaging(Boolean appDebugStaging) {
        _appDebugStaging = appDebugStaging;
    }

    public String getAppRemoteDataServerAddress() {
        return _appRemoteDataServerAddress;
    }

    public void setAppRemoteDataServerAddress(String appRemoteDataServerAddress) {
        _appRemoteDataServerAddress = appRemoteDataServerAddress;
    }

    public String getAppRemoteDataServerName() {
        return _appRemoteDataServerName;
    }

    public void setAppRemoteDataServerName(String appRemoteDataServerName) {
        _appRemoteDataServerName = appRemoteDataServerName;
    }

    public String getAppRuntimeServerName() {
        return _appRuntimeServerName;
    }

    public void setAppRuntimeServerName(String appRuntimeServerName) {
        _appRuntimeServerName = appRuntimeServerName;
    }

    public String getAppRuntimeServerIdfile() {
        return _appRuntimeServerIdfile;
    }

    public void setAppRuntimeServerIdfile(String appRuntimeServerIdfile) {
        _appRuntimeServerIdfile = appRuntimeServerIdfile;
    }

    public String getAppRuntimeServerPassword() {
        return _appRuntimeServerPassword;
    }

    public void setAppRuntimeServerPassword(String appRuntimeServerPassword) {
        _appRuntimeServerPassword = appRuntimeServerPassword;
    }

    public Boolean getAppDaEnabled() {
        return _appDaEnabled;
    }

    public void setAppDaEnabled(Boolean appDaEnabled) {
        _appDaEnabled = appDaEnabled;
    }

    public String getAppDaDomain() {
        return _appDaDomain;
    }

    public void setAppDaDomain(String appDaDomain) {
        _appDaDomain = appDaDomain;
    }

    public String getAppDaAddressBook() {
        return _appDaAddressBook;
    }

    public void setAppDaAddressBook(String appDaAddressBook) {
        _appDaAddressBook = appDaAddressBook;
    }
    
    public void convertToMap(Map<String, Object> map) {
        ManifestUtil.setStringValue (map, HOST_TAG, _host, false);
        ManifestUtil.setStringValue (map, DOMAIN_TAG, _domain, false);
        ManifestUtil.setIntegerValue(map, INSTANCES_TAG, _instances);
        ManifestUtil.setMemoryValue (map, MEMORY_TAG, _memory, "M", "G");
        ManifestUtil.setIntegerValue(map, TIMEOUT_TAG, _timeout);
        ManifestUtil.setStringValue (map, STACK_TAG, _stack, false);
        ManifestUtil.setMemoryValue (map, DISK_QUOTA_TAG, _diskQuota, "M", "G");
        ManifestUtil.setStringValue (map, BUILD_PACK_TAG, _buildPack, false);
        ManifestUtil.setStringValue (map, COMMAND_TAG, _command, false);
        ManifestUtil.setBooleanValue(map, NO_ROUTE_TAG, _noRoute);
        ManifestUtil.setStringValue (map, PATH_TAG, _path, false);
        ManifestUtil.setMapValue    (map, ENV_TAG, getEnv());
        ManifestUtil.setListValue   (map, SERVICES_TAG, _services);
        ManifestUtil.setListValue   (map, HOSTS_TAG, _hosts);
        ManifestUtil.setListValue   (map, DOMAINS_TAG, _domains);
    }    
    
    private void extractAppEnv() {
        if (_env != null) {
            Object val;
            
            val = ManifestUtil.getStringValue(_env, APP_HOME_URL_TAG);
            if (val != null) _appHomeUrl = (String)val;
            
            val = ManifestUtil.getStringValue(_env, APP_PRELOAD_DB);
            if (val != null) _appPreloadDb = (String)val;

            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_INCLUDE_XPAGES_TOOLBOX);
            if (val != null) _appIncludeXPagesToolbox = (Boolean)val;
            
            val = ManifestUtil.getMemoryValue(_env, APP_JVM_HEAPSIZE, "MB", "G"); // $NON-NLS-1$
            if (val != null) _appJvmHeapsize = (Integer)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_VERBOSE_STAGING);
            if (val != null) _appVerboseStaging = (Boolean)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_JAVA_POLICY_ALL_PERMISSION);
            if (val != null) _appJavaPolicyAllPermission = (Boolean)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_REDIRECT_TO_SSL);
            if (val != null) _appRedirectToSSL = (Boolean)val;

            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_DEBUG_DIRECTORY_ASSISTANCE);
            if (val != null) _appDebugDa = (Boolean)val;

            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_DEBUG_NAMELOOKUP);
            if (val != null) _appDebugNameLookup = (Boolean)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_DEBUG_THREADS);
            if (val != null) _appDebugThreads = (Boolean)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_DEBUG_STAGING);
            if (val != null) _appDebugStaging = (Boolean)val;

            val = ManifestUtil.getStringValue(_env, APP_REMOTE_DATA_SERVER_ADDRESS);
            if (val != null) _appRemoteDataServerAddress = (String)val;
            
            val = ManifestUtil.getStringValue(_env, APP_REMOTE_DATA_SERVER_NAME);
            if (val != null) _appRemoteDataServerName = (String)val;

            val = ManifestUtil.getStringValue(_env, APP_RUNTIME_SERVER_NAME);
            if (val != null) _appRuntimeServerName = (String)val;
            
            val = ManifestUtil.getStringValue(_env, APP_RUNTIME_SERVER_IDFILE);
            if (val != null) _appRuntimeServerIdfile = (String)val;

            val = ManifestUtil.getStringValue(_env, APP_RUNTIME_SERVER_PASSWORD);
            if (val != null) _appRuntimeServerPassword = (String)val;
            
            val = ManifestUtil.getZeroOneBooleanValue(_env, APP_DA_ENABLED);
            if (val != null) _appDaEnabled = (Boolean)val;
            
            val = ManifestUtil.getStringValue(_env, APP_DA_DOMAIN);
            if (val != null) _appDaDomain = (String)val;
            
            val = ManifestUtil.getStringValue(_env, APP_DA_ADDRESS_BOOK);
            if (val != null) _appDaAddressBook = (String)val;
            
            // Remove the App env from the Map
            _env.remove(APP_HOME_URL_TAG);
            _env.remove(APP_PRELOAD_DB);
            _env.remove(APP_INCLUDE_XPAGES_TOOLBOX);
            _env.remove(APP_JVM_HEAPSIZE);
            _env.remove(APP_VERBOSE_STAGING);
            _env.remove(APP_JAVA_POLICY_ALL_PERMISSION);                 
            _env.remove(APP_REDIRECT_TO_SSL);                 
            _env.remove(APP_DEBUG_DIRECTORY_ASSISTANCE);                 
            _env.remove(APP_DEBUG_NAMELOOKUP);                 
            _env.remove(APP_DEBUG_THREADS);                 
            _env.remove(APP_DEBUG_STAGING);                 
            _env.remove(APP_REMOTE_DATA_SERVER_ADDRESS);
            _env.remove(APP_REMOTE_DATA_SERVER_NAME);
            _env.remove(APP_RUNTIME_SERVER_NAME);
            _env.remove(APP_RUNTIME_SERVER_IDFILE);
            _env.remove(APP_RUNTIME_SERVER_PASSWORD);
            _env.remove(APP_DA_ENABLED);
            _env.remove(APP_DA_DOMAIN);
            _env.remove(APP_DA_ADDRESS_BOOK);                    
        }
    }
}