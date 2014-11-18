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
* Date: 11 Sep 2006
* DefinitionsHaveDisplayNamesTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesRenderKitFragment;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;


/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 11 Sep 2006
 * Unit: DefinitionsHaveDisplayNamesTest.java
 */
public class DefinitionsHaveDisplayNamesTest extends
        AbstractXspTest {

    @Override
    public String getDescription() {
        return "that the tag definitions in xsp-config files have descriptions & display names (ignoring xsp-configs in the test project)";
    }
//    private String[] skips = new String[]{
//            // these 2 are only in the xsp.editor.test project
//            "iconUrl tem:commandIcons  displayName is null.",
//            "iconUrl tem:commandIcons  description is null.",
//    };
    private String _fails = "";
    
    public void testDefinitionsHaveDisplayNames() throws Exception {
        
        DefinitionDescrAnnotater annotater = new DefinitionDescrAnnotater();
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, annotater);
        checkRegistryContents(reg, TestProject.getLibProjects(reg, this));
        
        _fails = XspTestUtil.removeMultilineFailSkips(_fails,
                SkipFileContent.concatSkips(getSkips(), this, "testDefinitionsHaveDisplayNames"));
        
        if( _fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(_fails) );
        }
    }
    protected String[] getSkips() {
//        return skips;
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    @Override
    protected String[][] getExtraConfig() {
        String[][] config = super.getExtraConfig();
        config = XspTestUtil.concat(config, new String[][]{
                // ignore local xsp-config files
                {"target.local.xspconfigs", "false"},
        });
        return config;
    }
    /**
     * @param def
     * @param fragId
     * @param prefix 
     */
    protected void checkDef(FacesDefinition def, String fragId, String prefix) {
        if( ! def.isTag() ){
            // if not a tag, ignore.
            return;
        }
        String displayName = (String) def.getExtension("display-name");
        if( null == displayName || displayName.trim().length() == 0){
            _fails += fragId + " "+prefix+":"+def.getId()+"  display-name is null.\n";
        }
        String description = (String) def.getExtension("description");
        if( null == description  || description.trim().length() == 0){
            _fails += fragId + " "+prefix+":"+def.getId()+"  description is null.\n";
        }
    }
    private void checkRegistryContents(FacesSharableRegistry reg, List<FacesProject> projs) {
        for (FacesProject proj : projs) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                String fragId = file.getFilePath();
                fragId = fragId.substring(fragId.lastIndexOf('/')+1);
                int lastDot = fragId.lastIndexOf('.');
                if( lastDot >=0 ){ // non-negative
                    fragId = fragId.substring(0, lastDot);
                }
                
                String prefix = reg.getLibrary( file.getNamespaceUri() ).getFirstDefaultPrefix();
                
                for (FacesDefinition def : file.getDefs()) {
                    checkDef(def, fragId, prefix);
                }
                
                for (String kitId : file.getRenderKitIds()) {
                    FacesRenderKitFragment kitFrag = file.getRenderKitFragment(kitId);
                    
                    List<String> aliases = reg.getRenderKitLibrary(kitFrag.getRenderKitId()).getAliases();
                    String suffix = !aliases.isEmpty()? aliases.get(0) :  kitFrag.getRenderKitId();
                    
                    String fragId2 = fragId+"."+suffix;
                    for (FacesDefinition def : kitFrag.getDefs()) {
                        checkDef(def, fragId2, prefix);
                    }
                }
            }
        }
    }
    public static class DefinitionDescrAnnotater extends DescriptionDisplayNameAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            if (super.isApplicableExtensibleNode(parsed)) {
                if (parsed instanceof FacesDefinition) {
                    return true;
                }
            }
            return false;
        }
    }
}
