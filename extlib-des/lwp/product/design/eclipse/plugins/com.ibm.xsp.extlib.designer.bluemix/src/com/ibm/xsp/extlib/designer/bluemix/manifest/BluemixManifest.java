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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.commons.util.StringUtil;

/**
 * @author Gary Marjoram
 *
 */
public class BluemixManifest {
    
    public final static String            APPLICATIONS_TAG = "applications"; // $NON-NLS-1$

    private final File                    _manifestFile;
    private ManifestBaseProps             _baseProps; 
    private Map<String, ManifestAppProps> _appMap;
    private Map<Object, Object>           _rawMap; // The raw parsed yaml
    
    public BluemixManifest(File manifestFile) {
        _manifestFile = manifestFile;
        loadFromYamlFile();
    }
    
    private void loadFromYamlFile() {
        _rawMap = ManifestUtil.loadManifest(_manifestFile);
        loadObjects();
    }  
    
    public void loadFromYamlString(String contents) {
        _rawMap = ManifestUtil.loadManifest(contents);
        loadObjects();        
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    private void loadObjects() {
        // Get the base properties
        _baseProps = new ManifestBaseProps(_rawMap);
        
        // Load the applications
        _appMap = new LinkedHashMap<String, ManifestAppProps>();
        if (_rawMap != null) {
            List<Map<Object, Object>> apps = (List<Map<Object, Object>>) _rawMap.get(APPLICATIONS_TAG);
            if (apps != null) {
                for (Map<Object, Object> app : apps) {
                    String appName = ManifestUtil.getStringValue(app, ManifestAppProps.NAME_TAG);
                    if (StringUtil.isNotEmpty(appName)) {
                        _appMap.put(appName, new ManifestAppProps(app));
                    }
                }
            }
        }
    }
    
    public boolean isValid() {
        // Manifest is valid if it has at least one application
        // If the Yaml file could not be parsed there won't be any applications
        return _appMap.size() > 0;
    }
    
    public ManifestAppProps getFirstApp() {
        if (isValid()) {
            return _appMap.values().iterator().next();
        }
        
        return null;
    }
    
    // Replaces the first app in the _rawMap and returns the resulting yaml as a String
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public String updateAndGetContents(ManifestAppProps firstApp) {
        // Create a new app array
        List<Map<String, Object>> newApps = new ArrayList<Map<String, Object>>();
        newApps.add(firstApp.convertToMap());
        
        // Copy the original apps to the new list skipping the first
        List<Map<String, Object>> apps = (List<Map<String, Object>>) _rawMap.get(APPLICATIONS_TAG);
        if (apps != null) {
            boolean first = true;
            for (Map<String, Object> app : apps) {
                // Skip the first app
                if (!first) {
                    newApps.add(app);
                } else {
                    first = false;
                }
            }
        }                
        
        // Replace the old app list with the new one
        _rawMap.put(APPLICATIONS_TAG, newApps);
        loadObjects();
        
        return (ManifestUtil.getContents(_rawMap));
    }
    
    public Set<String> getAppNames() {
        if (_appMap != null) {
            return _appMap.keySet();
        }
        return null;
    }
    
    public Integer getMemory(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getMemory() != null ? appProps.getMemory() : _baseProps.getMemory();
        }
        return null;
    }
    
    public Integer getInstances(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getInstances() != null ? appProps.getInstances() : _baseProps.getInstances();
        }
        return null;
    }

    public String getBuildPack(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getBuildPack() != null ? appProps.getBuildPack() : _baseProps.getBuildPack();
        }
        return null;
    }

    public String getCommand(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getCommand() != null ? appProps.getCommand() : _baseProps.getCommand();
        }
        return null;
    }
    
    public Integer getTimeout(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getTimeout() != null ? appProps.getTimeout() : _baseProps.getTimeout();
        }
        return null;
    }

    public String getStack(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getStack() != null ? appProps.getStack() : _baseProps.getStack();
        }
        return null;
    }

    public String getPath(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getPath() != null ? appProps.getPath() : _baseProps.getPath();
        }
        return null;
    }
    
    public Boolean getNoRoute(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getNoRoute() != null ? appProps.getNoRoute() : _baseProps.getNoRoute();
        }
        return null;
    }
    
    public String getHost(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getHost() != null ? appProps.getHost() : _baseProps.getHost();
        }
        return null;
    }

    public String getDomain(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getDomain() != null ? appProps.getDomain() : _baseProps.getDomain();
        }
        return null;
    }

    public List<String> getUris(String appName, String defaultDomain) {
        Boolean noRoute = getNoRoute(appName);
        if ((noRoute != null) && (noRoute == Boolean.TRUE)) {
            // No-route is set
            return new ArrayList<String>();
        } else {
            // Add a route
            String host = getHost(appName);
            host = StringUtil.isNotEmpty(host) ? host : appName;
            String domain = getDomain(appName);
            domain = StringUtil.isNotEmpty(domain) ? domain : defaultDomain;
            if (StringUtil.isNotEmpty(host) && StringUtil.isNotEmpty(domain)) {
                List<String> uris = new ArrayList<String>();
                uris.add(host + "." + domain);
                return uris;
            }
        } 
        return null;
    }
    
    public Map<String, Object> getEnv(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            LinkedHashMap<String, Object> env = appProps.getEnv();
            return env != null ? env : _baseProps.getEnv();
        }
        return null;
    }
    
    public Integer getDiskQuota(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getDiskQuota() != null ? appProps.getDiskQuota() : _baseProps.getDiskQuota();
        }
        return null;        
    }
    
    public List<String> getServices(String appName) {
        ManifestAppProps appProps = _appMap.get(appName);
        if (appProps != null) {
            return appProps.getServices() != null ? appProps.getServices() : _baseProps.getServices();
        }
        return null;                
    }
}