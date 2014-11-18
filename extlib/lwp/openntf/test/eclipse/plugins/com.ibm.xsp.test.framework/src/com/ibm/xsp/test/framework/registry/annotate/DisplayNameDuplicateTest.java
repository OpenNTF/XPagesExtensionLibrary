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
* Date: 30 May 2011
* DisplayNameDuplicateTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.HashMap;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.page.translator.JavaUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DisplayNameDuplicateTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "to prevent copy/paste errors when copy a <property> within a descr, " 
                + "or when copying a <component> or <complex-type>";
    }
    public void testDisplayNameDuplicate() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new DescriptionDisplayNameAnnotater());
        
        String fails = "";
        Map<String, FacesDefinition> textToDef = new HashMap<String, FacesDefinition>();
        Map<String, FacesProperty> textToProp = new HashMap<String, FacesProperty>();
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            
            {
                String displayName = (String) def.getExtension("display-name");
                String description = (String) def.getExtension("description");
                
                boolean sameDescrDisplayName = !StringUtil.isEmpty(displayName)
                        && StringUtil.equals(description, displayName);
                if( sameDescrDisplayName ){
                    fails += def.getFile().getFilePath()+ " "+ ParseUtil.getTagRef(def)
                            + "  Same def description and display-name: " +displayName+"\n";
                }
                
                FacesDefinition existingNameDef = null;
                if( ! StringUtil.isEmpty(displayName) ){
                    existingNameDef =  textToDef.put(displayName, def);
                }
                FacesDefinition existingDescrDef = null; 
                if( !StringUtil.isEmpty(description) && !sameDescrDisplayName ){ 
                    existingDescrDef = textToDef.put(description, def);
                }
                if( null != existingNameDef || null != existingDescrDef){
                    String existing = "";
                    if( null != existingNameDef ){
                        existing += existingNameDef.getFile().getFilePath()+" "+ParseUtil.getTagRef(existingNameDef)
                            +getNameAndDescr(existingNameDef);
                    }
                    if( null != existingDescrDef && existingDescrDef != existingNameDef){
                        if( existing.length() > 0 ){
                            existing += " "; 
                        }
                        existing += existingDescrDef.getFile().getFilePath()+" "+ParseUtil.getTagRef(existingDescrDef)
                            +getNameAndDescr(existingDescrDef);
                    }
                    fails += def.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(def)
                            + "  Duplicate def description/display-name. Existing: " + existing+ "\n";
                }
            }
            
            textToProp.clear();
            for (FacesProperty prop : RegistryUtil.getProperties(def,
                    def.getDefinedInlinePropertyNames())) {
                String displayName = (String) prop.getExtension("display-name");
                String description = (String) prop.getExtension("description");
                
                boolean sameDescrDisplayName = !StringUtil.isEmpty(displayName)
                        && StringUtil.equals(description, displayName);
                if( sameDescrDisplayName ){
                    fails += def.getFile().getFilePath()+ " "+ ParseUtil.getTagRef(def)+" "+prop.getName()
                            + "  Same prop description and display-name: " +displayName+"\n";
                }
                
                FacesProperty existingNameProp = null;
                if( !StringUtil.isEmpty(displayName) ){
                    existingNameProp =  textToProp.put(displayName, prop); 
                }
                FacesProperty existingDescrProp = null;
                if (!StringUtil.isEmpty(description) && !sameDescrDisplayName) {
                    existingDescrProp = textToProp.put(description, prop);
                }
                if( null != existingNameProp || null != existingDescrProp ){
                    String existing = "";
                    if( null != existingNameProp ){
                        existing += existingNameProp.getName() + getNameAndDescr(existingNameProp);
                    }
                    if( null != existingDescrProp && existingDescrProp != existingNameProp ){
                        if( existing.length() > 0 ){
                            existing += " "; 
                        }
                        existing += existingDescrProp.getName() + getNameAndDescr(existingDescrProp);
                    }
                    fails += def.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(def) +" "
                            + prop.getName()
                            + "  Duplicate prop description/display-name. Existing: " + existing+ "\n";
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testDisplayNameDuplicate"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    /**
     * @param existingNameProp
     * @return
     */
    private String getNameAndDescr(FacesExtensibleNode existingNameProp) {
        return ("("
                + existingNameProp.getExtension("display-name")
                + "|"
                + JavaUtil.toJavaString((String)existingNameProp.getExtension("description"))
                + ")");
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }

}
