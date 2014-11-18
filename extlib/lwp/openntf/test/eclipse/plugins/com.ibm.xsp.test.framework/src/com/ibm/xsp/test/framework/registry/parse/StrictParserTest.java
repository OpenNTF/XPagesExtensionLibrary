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

package com.ibm.xsp.test.framework.registry.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.config.XspRegistryManager;
import com.ibm.xsp.registry.config.XspRegistryProvider;
import com.ibm.xsp.registry.parse.ConfigParserFactory;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;


public class StrictParserTest extends AbstractXspTest{
    
    private StrictParserHandler strictHandler;

    @Override
    public String getDescription() {
        return "tests the xsp-config files load ok when the parser is in strict mode.";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        strictHandler = new StrictParserHandler();
        ConfigParserFactory.setProblemHandler(strictHandler);
        discardRegistryManager();
    }
    
    public void testConfigsStrict(){
        String fails = "";
        
        XspRegistryManager libs = XspRegistryManager.getManager();
        List<String> libsToCheck = new ArrayList<String>(2);
        String targetLibId = ConfigUtil.getTargetLibrary(this);
        if( null != targetLibId ){
            libsToCheck.add(targetLibId);
        }
        if( ConfigUtil.isTestJsfHtml(this) ){
            libsToCheck.add("com.ibm.xsp.core-html.library");
        }
        String extraLibIds = getConfig().get("registry.parse.StrictParserTest.extraLibIds");
        if( StringUtil.isNotEmpty(extraLibIds) ){
            libsToCheck.addAll( Arrays.asList(StringUtil.splitString(extraLibIds, ',')) );
        }
        for (String libId : libsToCheck) {
            XspRegistryProvider lib = libs.getRegistryProvider(libId);
            if( null == lib ){
                fails += "Cannot find library with id "+libId+"\n";
                continue;
            }
            try{
                FacesRegistry reg = lib.getRegistry();
                // call getProperties on all definitions to trigger the
                // PropertyRedefinitionChecker s
                iterateOverDefinitionProps(reg);
                
            }catch(StrictParserException ex){
                String message = "Problem in library with id " + libId + " : "
                        + ex.getMessage();
                System.err.println(StrictParserTest.class.getName()
                        + ".testConfigsStrict() : FAIL: "+message);
                ex.printStackTrace();
                
                fails += message+"\n";
            }
        }
        if( ConfigUtil.isTargetLocalXspconfigs(this) ){
        // Ignore test project warnings about unpublished property extensions 
        boolean oldIgnore = strictHandler.setIgnoreUnpublishedExtensions(true);
        try{
            FacesRegistry local = TestProject.createRegistry(this);
            assertNotNull(local);
            iterateOverDefinitionProps(local);
        }catch(StrictParserException ex){
            String message = "Problem in local library (or its depends): "
                    + ex.getMessage();
            System.err.println(StrictParserTest.class.getName()
                    + ".testConfigsStrict() : FAIL: "+message);
            ex.printStackTrace();
            
            fails += message+"\n";
        }finally{
            strictHandler.setIgnoreUnpublishedExtensions(oldIgnore);
        }
        }else{
            // not test local
            if( libsToCheck.isEmpty() ){
                throw new RuntimeException("Not testing local xsp-configs, and not configured to test any library configs, so nothing to test.");
            }
        }
        
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    @SuppressWarnings("deprecation")
    private void discardRegistryManager() {
        XspRegistryManager.initManager(/*XspRegistryLoader loader*/null, /*discard*/true);
    }
    private void iterateOverDefinitionProps(FacesRegistry reg) {
        for (FacesProject proj : reg.getProjectList()) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                for (FacesDefinition def : file.getDefs()) {
                    def.getPropertyNames();
                }
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ConfigParserFactory.setProblemHandler(null);
        discardRegistryManager();
    }
}
