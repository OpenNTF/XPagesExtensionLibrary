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
* Date: 10 May 2011
* FileChangeDates.java
*/
package com.ibm.xsp.test.framework.translator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * The datestamps of the .xsp files in a project, as read from the 
 * /src/translation/translation.properties file in that project.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DatestampList {


    private static final String BUNDLE_NAME = "translation.translation";
    private static final long s_future = System.currentTimeMillis()+5*60*1000; // 5min in the future
    
    private boolean translatorVersionChanged;
    // pageNameToLastChangeDate
    private Map<String, Date> changes;
    private boolean someDatestampChangedSinceRead;
    
    /**
     * @param translatorVersionChanged
     * @param changes
     */
    public DatestampList(boolean translatorVersionChanged,
            Map<String, Date> changes) {
        super();
        this.translatorVersionChanged = translatorVersionChanged;
        if( null == changes ){
            changes = new HashMap<String, Date>();
        }
        this.changes = changes;
    }
    
    /**
     * @return the translatorVersionChanged
     */
    public boolean isTranslatorVersionChanged() {
        return translatorVersionChanged;
    }

    /**
     * @param pageNameToLastChangeDate
     * @param absoluteDir
     * @param sourceFolder
     */
    public void writeLastChangeDates(File userDir, String sourceFolder, int translatorVersion) {
        String propsFileName = "/"+ sourceFolder
                + "/" + BUNDLE_NAME.replace('.', '/') + ".properties";
        File propsFile = new File(userDir, propsFileName);
        
        StringBuffer buf = new StringBuffer();
        buf.append("translator_version_number").append(
                " = ").append(translatorVersion).append("\n");
        
        for (String key : changes.keySet()) {
            Date date = changes.get(key);
            String dateString = s_dateFormat.format(date);
            
            buf.append(key).append(" = ").append(dateString).append("\n");
        }
        GeneratePagesTest.writeToFile(propsFile, buf.toString());
    }

    /**
     * 
     * @return map from String pageName to Date timestamp of .xsp file when last
     *         generated
     */
    private static final SimpleDateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DatestampList readLastChangeDates(int translatorVersion) {

        // get the bundle of the results of the last translation
        // and if it is too old, ignore it and start from scratch
        ResourceBundle bundle;
        try{
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            String oldVersionStr = bundle
                    .getString("translator_version_number");
            int oldVersion = Integer.parseInt(oldVersionStr);
            if (oldVersion != translatorVersion) {
                return new DatestampList(/*translatorVersionChanged*/true, /*changes*/null);
            }
        }catch( MissingResourceException ex){
            return new DatestampList(/*translatorVersionChanged*/false, /*changes*/null);
        }
        
        Map<String, Date> changes = new HashMap<String, Date>();
        for (Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            if( "translator_version_number".equals(key) ){
                continue;
            }
            String pageName = key;
            
            try{
                String oldPageDate = bundle.getString( pageName );
                Date oldDate = s_dateFormat.parse(oldPageDate);

                changes.put(pageName, oldDate);
            }catch( MissingResourceException ex){
                continue;
            }
            catch (ParseException e) {
                continue;
            }
        }

        return new DatestampList(/*translatorVersionChanged*/false, changes);
    }
    
    public boolean isFileDatestampChanged(String pageName, long newDatestamp){
         Date oldDate = changes.get(pageName);
         if( null != oldDate && oldDate.getTime() + 1000 >= newDatestamp ){
             // unchanged (note 1000ms leeway due to difference in Java time vs filesystem time).
             return false;
         }
         return true;
    }
    public void updateTimestamp(String pageName, long newDatestamp){
        someDatestampChangedSinceRead = true;
        changes.put(pageName, new Date(newDatestamp));
    }

    /**
     * @return the someDatestampChangedSinceRead
     */
    public boolean isSomeDatestampChangedSinceRead() {
        return someDatestampChangedSinceRead;
    }

    /**
     * Utility, called here and available to call in subclasses,
     * reads the file last modified datestamp.
     * @param xspFile
     * @return
     */
    public long getCurrentDateLong(File xspFile) {
        if( ! xspFile.exists() ){
            return s_future;
        }
        long lastModified = xspFile.lastModified();
        if( 0 == lastModified ){
            return s_future;
        }
        return lastModified;
    }
}
