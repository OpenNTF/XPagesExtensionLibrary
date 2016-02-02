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

package com.ibm.xsp.extlib.designer.bluemix.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.manifest.BluemixManifest;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ConfigManager {
    
    private static final String  _MAP_FILE     = "config.properties"; // $NON-NLS-1$
    private static final String  _BLUEMIX_FILE = "bluemix.properties"; // $NON-NLS-1$
    private static final String  _ORG          = "organization"; // $NON-NLS-1$
    private static final String  _SPACE        = "space"; // $NON-NLS-1$
    private static final String  _URI          = "uri"; // $NON-NLS-1$
    private static final String  _COPY_METHOD  = "copyMethod"; // $NON-NLS-1$

    // Singleton
    private static ConfigManager _instance     = null;

    // Members
    private Properties           _mapProps     = new Properties();
    private IPath                _mapPath;
    
    private ConfigManager() {
        Bundle bundle = Platform.getBundle(BluemixPlugin.PLUGIN_ID);
        _mapPath = Platform.getStateLocation(bundle).addTrailingSeparator().append(_MAP_FILE);
        BluemixUtil.readProperties(_mapProps, _mapPath);
        
        // Cleanup the config file
        // Build a list of invalid items
        List<String> removeList = new ArrayList<String>();
        for (String prj: _mapProps.stringPropertyNames()) {
            String dir = _mapProps.getProperty(prj);
            if (StringUtil.isNotEmpty(dir)) {
                File file = new File(dir);
                if (!file.exists() || !file.isDirectory()) {
                    // Dir does not exist
                    removeList.add(prj);                    
                }
            } else {
                // Rogue key
                removeList.add(prj);
            }
        }
        
        // Remove the invalid items
        for (String removeKey : removeList) {
            _mapProps.remove(removeKey);
        }
        
        // Rewrite the file if necessary
        if (removeList.size() > 0) {
            BluemixUtil.writeProperties(_mapProps, _mapPath);            
        }
    }
    
    public static ConfigManager getInstance() {
        // Singleton pattern
        if (_instance == null) {
            _instance = new ConfigManager();
        }
        return _instance;
    }
    
    public void setConfig(IDominoDesignerProject project, BluemixConfig config, boolean replaceManifest, LinkedHashMap<String, String> extraEnv) {
        // Write the NSF -> deploy dir config file
        _mapProps.setProperty(getProjectKey(project), config.directory);
        BluemixUtil.writeProperties(_mapProps, _mapPath);

        // Write the Bluemix properties file
        Properties bmProps = new Properties();
        bmProps.setProperty(_ORG, config.org);
        bmProps.setProperty(_SPACE, config.space);
        if (StringUtil.isNotEmpty(config.copyMethod)) {
            bmProps.setProperty(_COPY_METHOD, config.copyMethod);
        }
        if (StringUtil.isNotEmpty(config.uri)) {
            bmProps.setProperty(_URI, config.uri);
        }
        
        IPath path = new Path(config.directory).addTrailingSeparator().append(_BLUEMIX_FILE);
        BluemixUtil.writeProperties(bmProps, path);
        
        // Write the default manifest to the deploy dir
        if (replaceManifest) {
            ManifestUtil.writeDefaultManifest(config, project.getDatabaseName(), extraEnv);
        }
    }
    
    public BluemixConfig getConfig(IDominoDesignerProject project) {
        if (project != null) {
            return(getConfigFromDirectory(_mapProps.getProperty(getProjectKey(project))));
        }
       
        // Return a blank config
        return new BluemixConfig();
    }
    
    public BluemixConfig getConfigFromDirectory(String directory) {
        // Read the config from a specified directory
        BluemixConfig config = new BluemixConfig();
        if (StringUtil.isNotEmpty(directory)) {
            IPath path = new Path(directory).addTrailingSeparator().append(_BLUEMIX_FILE);
            Properties bmProps = new Properties();
            BluemixUtil.readProperties(bmProps, path);
            config.org = bmProps.getProperty(_ORG);
            config.space = bmProps.getProperty(_SPACE);
            config.uri = bmProps.getProperty(_URI);
            config.copyMethod = bmProps.getProperty(_COPY_METHOD);
            config.directory = directory;
            
            BluemixManifest manifest = new BluemixManifest(ManifestUtil.getManifestFile(config));
            Set<String>appNames = manifest.getAppNames();
            for (String appName:appNames) {
                config.appName = appName;
                config.host = manifest.getFirstHost(appName);
                break;
            }
        }

        return config;        
    }
    
    // Constructs the key for the NSF -> deploy dir mapping
    private String getProjectKey(IDominoDesignerProject project) {
        return (project.getNsfPath() + "-" + normalizeReplicaId(project.getReplicaId()));
    }
    
    // Sometimes the replica Id has leading zeros and sometimes
    // it doesn't - this function normalizes it
    private String normalizeReplicaId(String replicaId) {
        String[] parts = replicaId.split(":");
        if (parts.length == 2) {
            parts[0] = Long.toHexString(Long.parseLong(parts[0], 16));
            parts[1] = Long.toHexString(Long.parseLong(parts[1], 16));
            return (parts[0] + ":" + parts[1]);
        } 
        
        return null;
    }
        
}