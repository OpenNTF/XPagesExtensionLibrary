/*
 * © Copyright IBM Corp. 2012
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
* Date: 20 Mar 2012
* UnversionedSinceVersionsSetTest.java
*/
package com.ibm.xsp.test.framework.version;

import com.ibm.xsp.library.LibraryServiceLoader;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class UnversionedSinceVersionsSetTest extends BaseSinceVersionsSetTest {

    @Override
    public String getDescription() {
        return "verify that the tags and properties in the library do not have a <since> version.";
    }

    @Override
    public void testSinceVersionsSet() throws Exception {
        
        {
            // verify the library tag version is null (this test is for libraries with null version)
            String libraryId = ConfigUtil.getTargetLibrary(this);
            LibraryWrapper lib = (null == libraryId)? null : LibraryServiceLoader.getLibrary(libraryId);
            String libTagVersion = (null == lib)? null : lib.getTagVersion();
            if( null != libTagVersion ){
                String libClassName = lib.getLibraryClass().getName();
                libClassName = libClassName.substring(libClassName.lastIndexOf('.')+1);
                throw new RuntimeException("Non-null tag version - "+libClassName+".getTagName() gives \"" +libTagVersion+"\" "
                        + "- should use BaseSinceVersionSetTest instead of this UnversionedSinceVersionsSetTest.");
            }
        }
        
        // unlike the superclass, instead of verifying <since> is present
        // verify <since> is absent from the library.
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        String fails = "";
        for (FacesProject proj : TestProject.getLibProjects(reg, this)) {
            
            for (FacesLibraryFragment xspConfigFile : proj.getFiles()) {
                
                for (FacesDefinition def : xspConfigFile.getDefs()) {
                    
                    if( null != def.getSince() ){
                        fails += XspTestUtil.loc(def)+" has a <since> version " +def.getSince()+"\n";
                    }
                    
                    for (FacesProperty prop : RegistryUtil.getProperties(def,def.getDefinedPropertyNames()) ) {
                        if( null != prop.getSince() ){
                            fails += XspTestUtil.loc(def)+" "+prop.getName()+" has a <since> version " +prop.getSince()+"\n";
                        }
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getExtraSkips(), this, "testSinceVersionsSet"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    @Override
    public void testCurrentSinceListCorrect() throws Exception {
        // do nothing. Since the library is unversioned, 
        // there will be no current since list.
    }

}
