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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestUtil {
    
    private final static String _MANIFEST_FILENAME = "manifest.yml"; // $NON-NLS-1$

    public static void writeDefaultManifest(BluemixConfig config, String dbName) {
        // Create the manifest objects
        Map<Object, Object> manifest = new LinkedHashMap<Object, Object>();
        List<Map<Object, Object>> applications = new ArrayList<Map<Object, Object>>();
        Map<Object, Object> application = new LinkedHashMap<Object, Object>();

        // Build up the application map
        application.put(ManifestAppProps.NAME_TAG, config.appName);
        application.put(ManifestAppProps.HOST_TAG, config.host);
        application.put(ManifestAppProps.INSTANCES_TAG, new Integer(1));
        application.put(ManifestAppProps.MEMORY_TAG, "512M"); // $NON-NLS-1$
        application.put(ManifestAppProps.TIMEOUT_TAG, new Integer(180));
        application.put(ManifestAppProps.BUILD_PACK_TAG, "xpages_buildpack"); // $NON-NLS-1$
        application.put(ManifestAppProps.COMMAND_TAG, "/app/launch_xpages_webcontainer"); // $NON-NLS-1$
        
        // Environment Vars
        LinkedHashMap<String, String> envMap = new LinkedHashMap<String, String>();
        envMap.put("APP_HOME_URL", "/" + BluemixUtil.getNsfName(dbName)); // $NON-NLS-1$
        envMap.put("APP_PRELOAD_DB", BluemixUtil.getNsfName(dbName)); // $NON-NLS-1$
        application.put(ManifestAppProps.ENV_TAG, envMap);
        
        // Add application 
        applications.add(application);
        manifest.put(BluemixManifest.APPLICATIONS_TAG, applications);

        // Dump the yaml to a String
        DumperOptions options = new DumperOptions();
        options.setExplicitStart(true);
        options.setCanonical(false);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String manifestString = yaml.dump(manifest);

        // Write the yaml String to the file
        OutputStream os = null;
        try {
            File file = getManifestFile(config);
            os = new FileOutputStream(file);
            os.write(manifestString.getBytes());
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(ManifestUtil.class, "writeDefaultManifest", e, "Failed to write Manifest"); // $NON-NLS-1$ $NLE-ManifestUtil.FailedtowriteManifest-2$
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(ManifestUtil.class, "writeDefaultManifest", e, "Failed to close os"); // $NON-NLS-1$ $NLE-ManifestUtil.Failedtocloseos-2$
                    }
                }                
            }
        }
    }
    
    public static File getManifestFile(BluemixConfig config) {
        return getManifestFile(config.directory);
    }
    
    public static File getManifestFile(String directory) {
        IPath path = new Path(directory).addTrailingSeparator().append(_MANIFEST_FILENAME);
        return new File(path.toOSString());                
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static Map<Object, Object> loadManifest(File manifestFile) {
        Map<Object, Object> results = null;
        if (manifestFile.exists() && manifestFile.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(manifestFile);
                Yaml yaml = new Yaml();
                results = (Map<Object, Object>) yaml.load(inputStream);    
            } catch (Exception e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(ManifestUtil.class, "loadManifest", e, "Failed to load yaml file"); // $NON-NLS-1$ $NLE-ManifestUtil.Failedtoloadyamlfile-2$
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                            BluemixLogger.BLUEMIX_LOGGER.errorp(ManifestUtil.class, "loadManifest", e, "Failed to close inputStream"); // $NON-NLS-1$ $NLE-ManifestUtil.FailedtocloseinputStream-2$
                        }
                    }
                }
            }
        }
        return results != null ? results : new LinkedHashMap<Object, Object>();
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static Map<Object, Object> loadManifest(String contents) {
        Map<Object, Object> results = null;
        try {
            Yaml yaml = new Yaml();
            results = (Map<Object, Object>) yaml.load(contents);    
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(ManifestUtil.class, "loadManifest", e, "Failed to load yaml file from String"); // $NON-NLS-1$ $NLE-ManifestUtil.FailedtoloadyamlfilefromString-2$
            }
        }
        return results != null ? results : new LinkedHashMap<Object, Object>();
    }
    
    // Given a yaml map - returns the String representation
    public static String getContents(Map<Object, Object> rawMap) {
        DumperOptions options = new DumperOptions();
        options.setExplicitStart(true);
        options.setCanonical(false);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        return (yaml.dump(rawMap));
    }
    
    public static boolean doesManifestExist(BluemixConfig config) {
        File file = getManifestFile(config);
        if (file.exists() && file.isFile()) {
            return true;
        }
        
        return false;
    }
    
    protected static String getStringValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof String) {
            return (String) valObj;
        }
        return null;
    }

    protected static Integer getIntegerValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof Integer) {
            return (Integer) valObj;
        }
        return null;
    }
    
    protected static Boolean getBooleanValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof Boolean) {
            return (Boolean) valObj;
        }
        return null;
    }

    protected static Map<String, Object> getMapValueAsStrings(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof Map<?, ?>) {
            // Convert everything to Strings
            Map<String, Object> newMap = new LinkedHashMap<String, Object>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) valObj).entrySet()) {
                if (entry.getValue() != null) {
                    newMap.put(entry.getKey().toString(), entry.getValue().toString());
                } else {
                    newMap.put(entry.getKey().toString(), "");                    
                }
            }            
            return newMap;
        }
        return null;
    }
    
    protected static List<?> getListValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof List<?>) {
            return (List<?>) valObj;
        }
        return null;
    }
    
    protected static Integer getMemoryValue( Map<?, ?> containingMap, String propertyName, String megTag, String gigTag) {
        Integer mem = ManifestUtil.getIntegerValue(containingMap, propertyName);
        if (mem == null) {
            String memStr = ManifestUtil.getStringValue(containingMap, propertyName);
            if (StringUtil.isNotEmpty(memStr)) {
                memStr = memStr.trim().toUpperCase();
                try {
                    if (memStr.endsWith(megTag)) {
                        mem = Integer.parseInt(memStr.substring(0, memStr.length() - megTag.length()));
                    }
                    else if (memStr.endsWith(gigTag)) {
                        mem = Integer.parseInt(memStr.substring(0, memStr.length() - gigTag.length())) * 1024;
                    }
                } catch (Exception e) {
                    // Ignore - null will be returned
                }
            }
        }

        return mem;
    }
    
    protected static String getCommandValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof String) {
            return (String) valObj;
        }
        
        // Manifest can contain a blank command
        if (containingMap.containsKey(propertyName)) {
            return "";
        }
        
        return null;
    }
    
    protected static void setStringValue(Map<String, Object> containingMap, String key, String value, boolean allowEmpty) {
        if ((value != null) && (allowEmpty || StringUtil.isNotEmpty(value))) {
            containingMap.put(key, value);
        } else {
            containingMap.remove(key);
        }
    }

    protected static void setIntegerValue(Map<String, Object> containingMap, String key, Integer value) {
        if (value != null && value > 0) {
            containingMap.put(key, value);                
        } else {
            containingMap.remove(key);
        }
    }    

    protected static void setMemoryValue(Map<String, Object> containingMap, String key, Integer value, String megTag, String gigTag) {
        if (value != null && value > 0) {
            if ((gigTag != null) && (value % 1024 == 0)) {
                containingMap.put(key, value/1024 + gigTag);
            } else {
                containingMap.put(key, value + megTag);                
            }
        } else {
            containingMap.remove(key);
        }
    }    

    protected static void setBooleanValue(Map<String, Object> containingMap, String key, Boolean value) {
        if (value != null) {
            containingMap.put(key, value);
        } else {
            containingMap.remove(key);
        }
    }    
    
    protected static void setMapValue(Map<String, Object> containingMap, String key, Map<?, ?> value) {
        if (value != null) {
            containingMap.put(key, value);
        } else {
            containingMap.remove(key);
        }
    }
    
    protected static void setListValue(Map<String, Object> containingMap, String key, List<?> value) {
        if (value != null) {
            containingMap.put(key, value);
        } else {
            containingMap.remove(key);
        }
    }
    
    protected static Boolean getZeroOneBooleanValue(Map<?, ?> containingMap, String propertyName) {
        Object valObj = containingMap.get(propertyName);
        if (valObj instanceof Integer) {
            return ((Integer) valObj) == 1;
        } else if (valObj instanceof String) {
            return ((String) valObj).equals("1");
        }

        return null;        
    }
    
    protected static void setZeroOneBooleanValue(Map<String, Object> containingMap, String key, Boolean value) {
        if (value != null) {
            if (value) {
                containingMap.put(key, 1);
            } else {
                containingMap.put(key, 0);                
            }
        } else {
            containingMap.remove(key);
        }
    }    
    
    public static LinkedHashMap<String, String> convertToStringMap(Map<String, Object> map) {
        LinkedHashMap<String, String> newMap = null;
        if (map != null) {
            newMap = new LinkedHashMap<String, String>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    newMap.put(entry.getKey(), entry.getValue().toString());
                } else {
                    newMap.put(entry.getKey(), "");                    
                }
            }            
        }
        return newMap;
    }
}
