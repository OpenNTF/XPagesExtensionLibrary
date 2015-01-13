/*
 * © Copyright IBM Corp. 2014
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 16 May 2014
* RelationalVersion.java
*/
package com.ibm.xsp.extlib.relational.version;

import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.library.XspLibrary;
import com.ibm.xsp.registry.FacesDefinition;

/**
 * The Relational Library tag version, used with {@link XspLibrary#getTagVersion()} 
 * and {@link FacesDefinition#getSince()}.
 * 
 * The versions are in the form of the Extlib versions:
 *   "a.a.a.vbb_dd"
 * like 
 *   "9.0.1.v00_07"
 * where
 * "a.a.a" is the Notes/Domino version (like 9.0.0)
 * "v" is the letter "v"
 * "bb" is the Domino upgrade pack level
 *      e.g. Upgrade Pack 01 or Upgrade Pack 02. The level 00 is not an UpgradePack.
 * "dd" is the openNTF release number, which is generally incremented with 
 *      every openNTF release, where 01 is the first openNTF release, 
 *      and 00 is not an openNTF release.
 * 
 * 9.0.1.v00_07 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_07.20140619-1400.zip
 *              Released on: 27 Jun 2014.
 *              It is the first OpenNTF 9.0.1 release where the Relational library
 *              is included in the main extlib release, not extlibx.
 * 9.0.1.v00_08 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_08.20140729-0805.zip
 *              Released on: 31 Jul 2014.
 * 9.0.1.v00_09 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_09.20140903-1400.zip
 *              Released on: 8 Sep 2014.
 * 9.0.1.v00_10 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_10.20141105-0922.zip
 *              Released on: 10 Nov 2014.
 * 9.0.1.v00_11 this will be the eleventh OpenNTF 9.0.1 release.
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalVersion {

    private static String s_currentVersion;
    public static String getCurrentVersionString(){
        if( null == s_currentVersion ){
            Version currentVersionObj = computeCurrentVersion();
            s_currentVersion = currentVersionObj.toString();
        }
        return s_currentVersion;
    }
    private static Version computeCurrentVersion(){
        String versionStr;
        /* Note this value is different in the openNTF stream 
         * from Notes/Domino in-development stream. */
        boolean isOpenNTFRelease = true;
        if( isOpenNTFRelease ){
            versionStr = "9.0.1.v00_11"; // $NON-NLS-1$
            if( RelationalLogger.RELATIONAL.isTraceDebugEnabled() ){
            	RelationalLogger.RELATIONAL.traceDebugp(RelationalVersion.class, "computeCurrentVersion", //$NON-NLS-1$ 
                        "Current Relational version is "+versionStr); //$NON-NLS-1$
            }
        }else{
            versionStr = "9.0.1.v00_00"; // $NON-NLS-1$
        }
        return Version.parseVersion(versionStr);
    }
}
