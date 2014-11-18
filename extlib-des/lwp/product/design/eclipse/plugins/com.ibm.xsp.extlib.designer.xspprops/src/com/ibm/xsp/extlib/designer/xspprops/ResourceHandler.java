/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.xspprops;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.commons.util.StringUtil;


/**
 * @author doconnor
 *
 */
public class ResourceHandler {

    private static ResourceBundle _resourceBundle;
    private static ResourceBundle _loggingResourceBundle;
    private static ResourceBundle _specialAudience;
    

    /**
     * @return
     */
    private static ResourceBundle getResourceBundle(String bundle) {
        try {
            String bundlePackage = buildResourcePath(bundle); //NON-NLS-1
            return ResourceBundle.getBundle( bundlePackage );
        }
        catch (MissingResourceException e) {
            // does nothing - this method will return null and
            // getString(String) will return the key
            // it was called with
        }
        return null;
    }
    
   /**
     * @param name
     * @return
     */
    private static String buildResourcePath(String name) {
        String clName = ResourceHandler.class.getName();
        return clName.substring( 0, clName.lastIndexOf('.') + 1 ) + name; 
    }

    /**
     * @param key
     * @return
     */
    public static String getString(String key) {
        if (_resourceBundle == null) {
            _resourceBundle = getResourceBundle("messages");//NON-NLS-1 $NON-NLS-1$
        }
      return getResourceBundleString(_resourceBundle, key);
        
    }
    
    
    /**
     * @param key
     * @return
     */
    public static String getLoggingString(String key) {
        if (_loggingResourceBundle == null) {
            _loggingResourceBundle = getResourceBundle("logging");//NON-NLS-1 $NON-NLS-1$
        }
             return getResourceBundleString(_loggingResourceBundle, key);
            
    }
    

    /**
     * @param key
     * @param args
     * @return
     */
    public static String getString(String key, Object[] args) {
        return format(getString(key), args);
    }

    /**
     * @param key
     * @param args
     * @param x
     * @return
     */
    public static String getString(String key, Object[] args, int x) {
        return getString(key);
    }
    
    
    /**
     * @param key
     * @param args
     * @return
     */
    public static String getLoggingString(String key, Object[] args) {
        return format(getLoggingString(key), args);
    }
    private static String format(String msg, Object[] args) {
        try {
            return StringUtil.format(msg, args);
        }
        catch (IllegalArgumentException e) {
            return msg;
        }
    }

    /**
     * @param key
     * @param args
     * @param x
     * @return
     */
    public static String getLoggingString(String key, Object[] args, int x) {
        return getLoggingString(key);
    }
    
    
    public static String getResourceBundleString(ResourceBundle _bundle, String key){
       if (_bundle != null) {
            try {
                return _bundle.getString(key);
            }
            catch (MissingResourceException e) {
                return "!" + key + "!";//NON-NLS-2//NON-NLS-1
            }
        }
        else {
            return "!" + key + "!";//NON-NLS-2//NON-NLS-1
        }
    }
    
    public static String getSpecialAudienceString(String key) {
        if(_specialAudience == null){
            _specialAudience = getResourceBundle("specialAudience"); //$NON-NLS-1$
        }
        return getResourceBundleString(_specialAudience, key);
    }
    
}
