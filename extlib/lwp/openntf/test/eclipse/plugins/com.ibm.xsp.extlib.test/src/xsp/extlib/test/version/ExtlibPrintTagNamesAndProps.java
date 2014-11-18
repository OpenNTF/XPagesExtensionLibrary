/*
 * © Copyright IBM Corp. 2012, 2013
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
* Date: 23 Jan 2012
* ExtlibPrintTagNamesAndProps.java
*/
package xsp.extlib.test.version;

import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.extlib.library.ExtlibLibrary;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.version.PrintTagNamesAndProps;
import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibPrintTagNamesAndProps extends PrintTagNamesAndProps {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FacesSharableRegistry registry = StandardRegistryMaintainer.getStandardRegistry();
        
        System.out.println("PrintTagNamesAndProps.main()");
        
        List<Object[]> tagsAndProps = getTagsAndProps(registry);
        
        tagsAndProps = filterToDependsLibrary(registry, (new ExtlibLibrary()).getLibraryId(), tagsAndProps);
        
        removeAll(tagsAndProps, (new ExtlibSinceVersionNullList()).tagsAndProps(), true);
        List<SinceVersionList> versionInfos = new ArrayList<SinceVersionList>();
        versionInfos.addAll(ExtlibSinceVersion853UP2StreamLists.getSinceVersionLists());
        versionInfos.addAll(ExtlibSinceVersion900StreamLists.getSinceVersionLists());
        versionInfos.addAll(ExtlibSinceVersion901StreamLists.getSinceVersionLists());
        // checking for most recent, so don't skip it.
        versionInfos.remove(versionInfos.size() - 1);
        for (SinceVersionList versionInfo : versionInfos) {
            removeAll(tagsAndProps, versionInfo.tagsAndProps(), true);
        }
        
        print(tagsAndProps);
    }

}
