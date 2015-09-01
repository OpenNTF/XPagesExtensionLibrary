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

package com.ibm.xsp.extlib.designer.bluemix.manifest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestBaseProps {

    public final static String       MEMORY_TAG     = "memory"; // $NON-NLS-1$
    public final static String       INSTANCES_TAG  = "instances"; // $NON-NLS-1$
    public final static String       BUILD_PACK_TAG = "buildpack"; // $NON-NLS-1$
    public final static String       COMMAND_TAG    = "command"; // $NON-NLS-1$
    public final static String       DOMAIN_TAG     = "domain"; // $NON-NLS-1$
    public final static String       HOST_TAG       = "host"; // $NON-NLS-1$
    public final static String       TIMEOUT_TAG    = "timeout"; // $NON-NLS-1$
    public final static String       NO_ROUTE_TAG   = "no-route"; // $NON-NLS-1$
    public final static String       PATH_TAG       = "path"; // $NON-NLS-1$
    public final static String       ENV_TAG        = "env"; // $NON-NLS-1$
    public final static String       DISK_QUOTA_TAG = "disk_quota"; // $NON-NLS-1$
    public final static String       SERVICES_TAG   = "services"; // $NON-NLS-1$
    
    // XPages Environment Variable Tags
    public final static String       APP_HOME_URL_TAG               = "APP_HOME_URL"; // $NON-NLS-1$
    public final static String       APP_PRELOAD_DB                 = "APP_PRELOAD_DB"; // $NON-NLS-1$
    public final static String       APP_INCLUDE_XPAGES_TOOLBOX     = "APP_INCLUDE_XPAGES_TOOLBOX"; // $NON-NLS-1$
    public final static String       APP_JVM_HEAPSIZE               = "APP_JVM_HEAPSIZE"; // $NON-NLS-1$
    public final static String       APP_VERBOSE_STAGING            = "APP_VERBOSE_STAGING"; // $NON-NLS-1$
    public final static String       APP_JAVA_POLICY_ALL_PERMISSION = "APP_JAVA_POLICY_ALL_PERMISSION"; // $NON-NLS-1$

    private Integer                  _memory;
    private Integer                  _instances;
    private Integer                  _diskQuota;
    private String                   _buildPack;
    private String                   _command;
    private String                   _host;
    private String                   _domain;
    private Integer                  _timeout;
    private Boolean                  _noRoute;
    private String                   _path;
    private Map<String, Object>      _env;
    private List<String>             _services;
    
    // XPages Environment Variables
    private String                   _appHomeUrl;
    private String                   _appPreloadDb;
    private Boolean                  _appIncludeXPagesToolbox;
    private Integer                  _appJvmHeapsize;
    private Boolean                  _appVerboseStaging;
    private Boolean                  _appJavaPolicyAllPermission;
    
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
            _noRoute = ManifestUtil.getBooleanValue(manifest, NO_ROUTE_TAG);
            _path = ManifestUtil.getStringValue(manifest, PATH_TAG);
            _env = ManifestUtil.getMapValueAsStrings(manifest, ENV_TAG);
            _services = (List<String>) ManifestUtil.getListValue(manifest, SERVICES_TAG);
            extractXPagesEnv();
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
        
        // User may have enterered an XPages env in the User Env Section
        // Pull them out here
        extractXPagesEnv();        
    }
    
    public LinkedHashMap<String, Object> getEnv() {
        LinkedHashMap<String, Object> newMap;
        if (_env != null) {
            newMap = new LinkedHashMap<String, Object>(_env);
        } else {
            newMap = new LinkedHashMap<String, Object>();
        }
        
        // Add the XPages Env
        ManifestUtil.setStringValue(newMap, APP_HOME_URL_TAG, _appHomeUrl, true);
        ManifestUtil.setStringValue(newMap, APP_PRELOAD_DB, _appPreloadDb, true);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_INCLUDE_XPAGES_TOOLBOX, _appIncludeXPagesToolbox);
        ManifestUtil.setMemoryValue(newMap, APP_JVM_HEAPSIZE, _appJvmHeapsize, "MB", null); // $NON-NLS-1$
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_VERBOSE_STAGING, _appVerboseStaging);
        ManifestUtil.setZeroOneBooleanValue(newMap, APP_JAVA_POLICY_ALL_PERMISSION, _appJavaPolicyAllPermission);
        
        return newMap.size() > 0 ? newMap : null;
    }       

    public List<String> getServices() {
        return _services;
    }

    public void setServices(List<String> services) {
        _services = services;
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

    public void convertToMap(Map<String, Object> map) {
        ManifestUtil.setStringValue (map, HOST_TAG, _host, false);
        ManifestUtil.setStringValue (map, DOMAIN_TAG, _domain, false);
        ManifestUtil.setIntegerValue(map, INSTANCES_TAG, _instances);
        ManifestUtil.setMemoryValue (map, MEMORY_TAG, _memory, "M", "G");
        ManifestUtil.setIntegerValue(map, TIMEOUT_TAG, _timeout);
        ManifestUtil.setMemoryValue (map, DISK_QUOTA_TAG, _diskQuota, "M", "G");
        ManifestUtil.setStringValue (map, BUILD_PACK_TAG, _buildPack, false);
        ManifestUtil.setStringValue (map, COMMAND_TAG, _command, false);
        ManifestUtil.setBooleanValue(map, NO_ROUTE_TAG, _noRoute);
        ManifestUtil.setStringValue (map, PATH_TAG, _path, false);
        ManifestUtil.setMapValue    (map, ENV_TAG, getEnv());
        ManifestUtil.setListValue   (map, SERVICES_TAG, _services);
    }    
    
    private void extractXPagesEnv() {
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
            
            // Remove the XPages env from the Map
            _env.remove(APP_HOME_URL_TAG);
            _env.remove(APP_PRELOAD_DB);
            _env.remove(APP_INCLUDE_XPAGES_TOOLBOX);
            _env.remove(APP_JVM_HEAPSIZE);
            _env.remove(APP_VERBOSE_STAGING);
            _env.remove(APP_JAVA_POLICY_ALL_PERMISSION);                    
        }
    }
}