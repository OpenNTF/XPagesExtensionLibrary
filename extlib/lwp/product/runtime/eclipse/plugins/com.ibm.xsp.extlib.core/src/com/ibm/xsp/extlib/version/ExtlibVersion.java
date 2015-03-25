/*
 * © Copyright IBM Corp. 2012, 2014
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
* Date: 18 Jan 2012
* ExtlibVersion.java
*/
package com.ibm.xsp.extlib.version;

import com.ibm.xsp.core.Version;
import com.ibm.xsp.extlib.log.ExtlibCoreLogger;
import com.ibm.xsp.library.XspLibrary;
import com.ibm.xsp.registry.FacesDefinition;

/**
 * The Extension Library tag version, used with {@link XspLibrary#getTagVersion()} 
 * and {@link FacesDefinition#getSince()}.
 * 
 * From 9.0 the versions are in the form:
 *   "a.a.a.vbb_dd"
 * like 
 *   "9.0.1.v00_03"
 * where
 * "a.a.a" is the Notes/Domino version (like 9.0.0)
 * "v" is the letter "v"
 * "bb" is the Domino upgrade pack level
 *      e.g. Upgrade Pack 01 or Upgrade Pack 02. The level 00 is not an UpgradePack.
 * "dd" is the openNTF release number, which is generally incremented with 
 *      every openNTF release, where 01 is the first openNTF release, 
 *      and 00 is not an openNTF release.
 * 
 * The early releases, before 9.0, the versions were in the form:
 *   "a.a.abcdd"
 * like 
 *   "8.5.32005"
 * where
 * "a.a.a" is the Notes/Domino version (like 8.5.3)
 * "b" is the Domino upgrade pack level / stream in active development 
 *      e.g. Upgrade Pack 1 or Upgrade Pack 2, or 0 - no upgradePack.
 * "c" is usually 0, reserved for fix pack levels (FixPack1 or FixPack2)
 * "dd" is the release number on openNTF - which may be incremented 
 *      with every openNTF release or upgrade pack release (or may not, if no tags are added)
 * 
 * 
 * null is the 8.5.3 Upgrade Pack 1 release, or any openNTF release prior 
 *      to the introduction of versioning.
 * 
 * "8.5.32001" is an openNTF release 
 *             ExtensionLibraryOpenNTF-853.20120126-0415.zip
 *             Released on: 26 Jan 2012
 * "8.5.32002" is an openNTF release
 *             ExtensionLibraryOpenNTF-853.20120320-1003.zip
 *             Released on: 3 Apr 2012
 * "8.5.32003" was a planned openNTF release that was never published.
 *             Tags added in that release were first made available
 *             on openNTF in the following "8.5.32004" release.
 * "8.5.32004" is an openNTF release
 *             ExtensionLibraryOpenNTF-853.20120605-0921.zip
 *             Released on: 6 Jun 2012
 * "8.5.32005" is an openNTF release
 *             ExtensionLibraryOpenNTF-853.20121022-1354.zip
 *             Released on: 26 Oct 2012
 * "8.5.32006" is an openNTF release
 *             XPagesExtensionLibraryOpenNTF-853.20121217-1354.zip
 *             Released on: 20 Dec 2012
 * "8.5.32007" is an openNTF release 
 *             ExtensionLibraryOpenNTF-853.20130315-0724.zip
 *             Released on: 4 Apr 2013.
 * 
 * 9.0.0.v00_00 this is the Notes/Domino 9.0 release 
 *              or the Beta version of that release
 *              or any early pre-release code drops.
 * 9.0.0.v00_01 is an OpenNTF 9.0 release
 *              ExtensionLibraryOpenNTF-900v00_01.20130415-0518.zip
 *              Released on: 22 Apr 2013.
 * 9.0.0.v00_02 is an OpenNTF 9.0 release
 *              ExtensionLibraryOpenNTF-900v00_02.20130515-2200.zip
 *              Released on: 17 May 2013.
 * 9.0.0.v00_03 is an OpenNTF 9.0 release
 *              ExtensionLibraryOpenNTF-900v00_03.20131001-1400.zip
 *              Released on: 22 Oct 2013.
 * 
 * 9.0.0.v00_00 this is the Notes/Domino 9.0.1 release
 *              or any early pre-release code drops.
 * 9.0.1.v00_01 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_01.20131029-1400.zip
 *              Released on: 5 Nov 2013.
 * 9.0.1.v00_02 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_02.20131212-1115.zip
 *              Released on: 13 Dec 2013.
 * 9.0.1.v00_03 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_03.20140120-0650.zip
 *              Released on: 24 Jan 2014.
 * 9.0.1.v00_04 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_04.20140226-1506.zip
 *              Released on: 3 Mar 2014.
 * 9.0.1.v00_05 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_05.20140324-1415.zip
 *              Released on: 28 Mar 2014.
 * 9.0.1.v00_06 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_06.20140424-0600.zip
 *              Released on: 29 Apr 2014.
 * 9.0.1.v00_07 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_07.20140619-1400.zip
 *              Released on: 27 Jun 2014.
 * 9.0.1.v00_08 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_08.20140729-0805.zip
 *              Released on: 31 Jul 2014.
 * 9.0.1.v00_09 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_09.20140903-1400.zip
 *              Released on: 8 Sep 2014.
 * 9.0.1.v00_10 is an OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_10.20141105-0922.zip
 *              Released on: 10 Nov 2014.
 * 9.0.1.v00_11 this will be the eleventh OpenNTF 9.0.1 release
 *              ExtensionLibraryOpenNTF-901v00_11.20141217-1000.zip
 *              Released on: 19 Dec 2014.
 * 9.0.1.v00_12 this will be the twelfth OpenNTF 9.0.1 release
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibVersion {
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
            versionStr = "9.0.1.v00_12"; // $NON-NLS-1$
            if( ExtlibCoreLogger.COMPONENT_DATA.isTraceDebugEnabled() ){
                ExtlibCoreLogger.COMPONENT_DATA.traceDebugp(ExtlibVersion.class, "computeCurrentVersion", //$NON-NLS-1$ 
                        "Current Extlib version is "+versionStr); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }else{
            versionStr = "9.0.1.v00_00"; // $NON-NLS-1$
        }
        return Version.parseVersion(versionStr);
    }
}