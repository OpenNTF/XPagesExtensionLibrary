/*
 * © Copyright IBM Corp. 2007, 2013
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
* Date: 19 Jul 2007
* RegisteredDecodeTest.java
*/

package com.ibm.xsp.test.framework.lifecycle;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 19 Jul 2007
 * 
 * Unit: RegisteredDecodeTest.java
 */
public class RegisteredDecodeTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // Note, if there is no renderer defined for any 
        // component that has a renderer-type, it will throw 
        // an NullPointerException
        return "calls decode() on every component in the library registry";
    }
    
    public void testDecodeRegisteredComponents() throws Exception {
        
        String fails = "";

        TestProject.createRegistry(this);
        FacesSharableRegistry registry = TestProject.getRegistry(this);
        String libraryId = ConfigUtil.getTargetLibrary(this);
        List<FacesComponentDefinition> tags = findLibraryTags(registry, libraryId);
        
        // the config.properties says there there are no controls in the library
        // but some were found. Or the config.properties says there are controls
        // in the library, but none were found.
        boolean expectNoControls = ConfigUtil.isLibraryNoControls(this);
        assertEquals("Controls in library? mismatch ", expectNoControls, tags.isEmpty());
        if( tags.isEmpty() ){
            // no control tags to test.
            return;
        }
        
        
        // create an empty view
        FacesContext context = TestProject.createFacesContext(this);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        List<UIComponent> children = TypedUtil.getChildren(root);
        children.clear();
        
        for (FacesComponentDefinition def : tags) {
            try{
                UIComponent component = (UIComponent) def.getJavaClass().newInstance();
                component.processDecodes(context);
            }catch(Exception ex){
                fails += XspTestUtil.loc(def)+" decode() throws: "+ex+"\n";
                ex.printStackTrace();
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkips(), this, "testDecodeRegisteredComponents"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private List<FacesComponentDefinition> findLibraryTags(FacesSharableRegistry registry, String libraryId ){
        List<FacesComponentDefinition> defs = new ArrayList<FacesComponentDefinition>();
        for (FacesProject proj : registry.getProjectList()) {
            if( ! libraryId.equals(proj.getId()) ){
                // Mostly this is the same as restricting to the files returned by
                //   TestProject.getLibComponents(registry, this);
                // but also, in xsp.core.test the library registry has 2 projects
                // one for the ..xsp.core defs, and one for the JSF Html* controls.
                // ignore the unused Html* controls.
                continue;
            }
            for (FacesLibraryFragment file : proj.getFiles()) {
                for (FacesDefinition def : file.getDefs()) {
                    if( ! def.isTag() ){
                        continue;
                    }
                    if( def instanceof FacesComponentDefinition
                            && !(def instanceof FacesCompositeComponentDefinition)){
                        defs.add((FacesComponentDefinition)def);
                    }
                }
            }
        }
        return defs;
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
