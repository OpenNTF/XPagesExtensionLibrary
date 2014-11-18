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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 17 Feb 2012
* TestFrameworkPlatform.java
*/

package com.ibm.xsp.test.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.ibm.commons.IPlatformService;
import com.ibm.commons.Platform;
import com.ibm.commons.log.LogMgrFactory;
import com.ibm.commons.log.jdk.JdkLogMgrFactory;
import com.ibm.commons.util.NotImplementedException;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TestFrameworkPlatform extends Platform {
    private File dominoFolder;
    private static String[][] aliasStringToAliasChar;
    private Map<Character, File> aliasCharToFolder = new HashMap<Character, File>();
    
    public TestFrameworkPlatform(File dominoFolder) {
        super();
        // it may be necessary at a later point to also add the dominoDataFolder as an argument
        this.dominoFolder = dominoFolder;
    }
    @Override
    public String getName() {
        return "TestFrameworkPlatform";
    }
    @Override
    protected LogMgrFactory createLogMgrFactory() {
        // TODO Platform needs non-api LogMgrFactory.
        // Platform is @ibm-api, but LogMgrFactory is @ibm-not-published
        // and you can't implement Platform without a LogMgrFactory
        return new JdkLogMgrFactory(); 
    }
    @Override
    public PrintStream getOutputStream() {
        return System.out;
    }
    @Override
    public PrintStream getErrorStream() {
        return System.err;
    }
    @Override
    public boolean isEclipseBased() {
        // it's a legacy method from 8.5.0, asking if this is Notes.
        return false;
    }
    @Override
    public boolean isFeatureEnabled(String featureId) {
        // not enabling the advanced state saving
        // nor any other feature queried.
        return false;
    }
    @Override
    public File getGlobalResourceFile(String resourceName) {
        if( null == aliasStringToAliasChar ){
            aliasStringToAliasChar = new String[][]{
                    {"/stylekits/","t"}, // t for themes
                    {"/properties/acf-config.xml","a"},
            };
        }
        String matchedAliasPrefix = null;
        char matchedAliasChar = '-';
        for (String[] mapping : aliasStringToAliasChar) {
            if( resourceName.startsWith(mapping[0]) ){
                matchedAliasPrefix = mapping[0];
                matchedAliasChar = mapping[1].charAt(0);
                break;
            }
        }
        if ( null != matchedAliasPrefix ){
            resourceName = resourceName.substring(matchedAliasPrefix.length());
            
            File folder = aliasCharToFolder.get(matchedAliasChar);
            if( null != folder ){
                return new File(folder, resourceName);
            }
            switch(matchedAliasChar){
            case 't': {
                folder = new File(dominoFolder, "xsp/nsf/themes");
                break;
            }
            case 'a': {
                // acf-config.xml 
                return null;
            }
            }
            if( null != folder ){
                if( ! folder.exists() ){
                    throw new RuntimeException("Folder does not exist: "+folder+ ", checking for file: "+resourceName);
                }
                aliasCharToFolder.put(matchedAliasChar, folder);
                return new File(folder, resourceName);
            }
        }
        throw new NotImplementedException(resourceName);
//        if(resourceName.startsWith("/properties/")) { //$NON-NLS-1$
//            File file;           
//            // Client - try the user's data directory first
//            if (null != userDataDirectory) {
//                file = new File(userDataDirectory,"properties/"+resourceName.substring(12)); //$NON-NLS-1$
//                if( file.exists() ){
//                    return file;
//                }
//            }
//            // Client - try the shared user data directory second
//            if (null != sharedDataDirectory) {
//                file = new File(sharedDataDirectory,"properties/"+resourceName.substring(12)); //$NON-NLS-1$
//                if( file.exists() ){
//                    return file;
//                }
//            }
//            // Server - fall back on checking the propertiesDirectory
//            file = new File(propertiesDirectory,resourceName.substring(12));
//            return file;
//        }
//        if(resourceName.startsWith("/icons/")) { //$NON-NLS-1$
//            File file = new File(notesIconsDirectory,resourceName.substring(7));
//            return file;
//        }
//        File file = new File(nsfDirectory,resourceName);
//        return file;
    }
    @Override
    public InputStream getGlobalResource(String resourceName) {
        File file = getGlobalResourceFile(resourceName);
        if(file!=null) {
            try {
                return new FileInputStream(file);
            } 
            catch(FileNotFoundException ex) {
            }
        }
        return null;
    }
    @Override
    protected void initialize() {
        super.initialize();
    }
    @Override
    public boolean isPlatform(String name) {
        if( "TestFrameworkPlatform".equals(name) ){
            return true;
        }
//        if( "Domino".equals(name) ){
//            // maybe should have an option to pretend to be "Notes"
//            // or to be "Domino"
//            return true;
//        }
        if( "WebAppServer".equals(name) ){
            return true;
        }
        return false;
    }
    @Override
    public IPlatformService getPlatformService(String serviceId) {
        return null;
    }
    @Override
    public void registerPlatformService(String serviceId,
            IPlatformService platformService) {
        throw new NotImplementedException();
    }
    
    @Override
    public String getProperty(String key) {
        return null;
    }
    @Override
    public void putProperty(String key, String object) {
        throw new NotImplementedException();
    }
    @Override
    public void removeProperty(String key) {
        throw new NotImplementedException();
    }
    /* (non-Javadoc)
     * @see com.ibm.commons.Platform#getObject(java.lang.String)
     */
    @Override
    public Object getObject(String key) {
        // could provide com.ibm.designer.runtime.Application.IApplicationFinder
        // here if it becomes necessary for junits.
        return super.getObject(key);
    }

}
