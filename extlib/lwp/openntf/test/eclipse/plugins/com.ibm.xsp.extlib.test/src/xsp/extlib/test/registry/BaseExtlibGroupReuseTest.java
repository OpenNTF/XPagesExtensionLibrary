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
* Date: 20 May 2011
* ExtlibGroupReuseTest.java
*/
package xsp.extlib.test.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.registry.BaseGroupReuseTest;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;

/**
 * This is a base test for all libraries that depend on extlib (including extlib, extlibX, incubator).
 * Note, there is a subclass containing extlib-specific skips.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseExtlibGroupReuseTest extends BaseGroupReuseTest {
    @Override
    protected Map<String, SuggestedGroups> getHardCodedSuggestedGroups() {
        Map<String, SuggestedGroups> map = super.getHardCodedSuggestedGroups();
        // suggest the groups in the extlib-common.xsp-config file.
        addAll(map, getExtlibCommonSinglePropGroups());
        return map;
    }
    private SuggestedGroups[] getExtlibCommonSinglePropGroups(){
        List<SuggestedGroups> list = new ArrayList<SuggestedGroups>();
        
        String libraryId = "com.ibm.xsp.extlib.library";
        String commonFilePathSuffix = "/extlib-common.xsp-config";
        FacesLibraryFragment commonFile = getFile(TestProject.getRegistry(this), libraryId, commonFilePathSuffix);
        for (FacesDefinition def : commonFile.getDefs()) {
            if( def instanceof FacesGroupDefinition ){
                FacesGroupDefinition group = (FacesGroupDefinition) def;
                if( 1 == group.getPropertyNames().size() ){
                    
                    // found a single-property group
                    String propName = group.getPropertyNames().iterator().next();
                    String groupType = group.getGroupType();
                    
                    // is is a control group or a complex-type group?
                    String[] suggestedComponentGroups = null;
                    String[] suggestedComplexGroups = null;
                    String[] groupArr = new String[]{groupType};
                    if( DefinitionTagsAnnotater.isGroupTaggedGroupInControl(group) ){
                        suggestedComponentGroups = groupArr;
                    }else if( DefinitionTagsAnnotater.isGroupTaggedGroupInComplex(group) ){
                        suggestedComplexGroups = groupArr;
                    }else {
                        suggestedComponentGroups = groupArr;
                        suggestedComplexGroups = groupArr;
                    }
                    
                    // add it to the list of hardCodedSuggestedGroups
                    list.add(new SuggestedGroups(propName, suggestedComponentGroups,suggestedComplexGroups));
                }
            }
        }
        return list.toArray(new SuggestedGroups[list.size()]);
    }
    private FacesLibraryFragment getFile(FacesSharableRegistry registry, String libraryId,
            String configFilePathSuffix) {
        
        FacesSharableRegistry libraryReg = null;
        for (FacesSharableRegistry depend : registry.getDepends()) {
            if( libraryId.equals(depend.getId()) ){
                libraryReg = depend;
                break;
            }
        }
        if( null == libraryReg ){
            throw new RuntimeException("Library not found: "+libraryId);
        }
        
        for (FacesProject proj : libraryReg.getProjectList() ) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                if( file.getFilePath().endsWith(configFilePathSuffix) ){
                    return file;
                }
            }
        }
        
        throw new RuntimeException("Registry did not contain "+configFilePathSuffix);
    }

}
