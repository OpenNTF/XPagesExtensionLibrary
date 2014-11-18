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
* Date: 5 Dec 2007
* LabelsLocalizableTest.java
*/

package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 5 Dec 2007
 * Unit: LabelsLocalizableTest.java
 */
public class LabelsLocalizableTest extends AbstractXspTest{

    // all properties with these names should be localizable: 
    private String[] commonLocalizableNames = new String[]{
            "label",
            "title",
            "alt",
            "accesskey",
            "message",
            "summary",
            "description",
            "confirm",
            "text",
            "lang",
            "caption",
            "tooltip",
            "legend",
    };
    private List<String> capitalizedLocNames;
    @Override
    public String getDescription() {
        return "that properties with 'label' in the name are localizable";
    }
    
    public void testLabelsLocalizable() throws Exception {
        
        String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyTagsAnnotater());
        List<FacesDefinition> allDefs = TestProject.getLibDefinitions(reg, this);
        List<String> expectedCommonPropNames = getCommonLocalizableNames();
        List<String> expectedDefProps = getExpectedLocalizedDefProps();
        
        for (FacesDefinition def : allDefs) {
            for (FacesProperty prop : RegistryUtil.getDefinedProperties(def)) {
                if( prop instanceof FacesContainerProperty){
                    prop = ((FacesContainerProperty)prop).getItemProperty();
                }
                if( !(prop instanceof FacesSimpleProperty) ){
                    continue;
                }
                FacesSimpleProperty simple = (FacesSimpleProperty) prop; 
                
                String name = prop.getName();
                if ( contains(expectedCommonPropNames, name) ) {
                    if (!simple.isLocalizable()
                            && String.class.equals(simple.getJavaClass())
                            || Object.class.equals(simple.getJavaClass())) {
                        
                        // expect localizable
                        
                        if( PropertyTagsAnnotater.isTagged(prop, "not-localizable") ){
                            // if contains:
                            // <tags>
                            //    not-localizable
                            // <tags>
                            // then this property is skipped in this JUnit test
                            continue;
                        }
                        
                        String msg = def.getFile().getFilePath()+" "+descr(def, prop)+"  not localizable";
                        fails+= msg +"\n";
                    }
                }
                else{
                    if( simple.isLocalizable() ){
                        String descr = descr(def, prop);
                        if( ! expectedDefProps.contains(descr) ){
                            if( PropertyTagsAnnotater.isTagged(prop, "localizable-text") ){
                                // if contains:
                                // <tags>
                                //    localizable-text
                                // <tags>
                                // then this property is skipped in this JUnit test
                                continue;
                            }
                            String msg = def.getFile().getFilePath()+" "+descr+"  unexpected localizable prop";
                            fails+= msg +"\n";
                        }else{
                            // found 
                            expectedDefProps.remove(descr);
                            
                            if ("value".equals(prop.getName())) {
                                FacesProperty other = def.getProperty("defaultValue");
                                if (null != other && 
                                        !((FacesSimpleProperty) other).isLocalizable()) {
                                    String msg = def.getFile().getFilePath()+" "+descr(def, other)
                                            + "  value localizable but defaultValue not localizable";
                                    fails+= msg +"\n";
                                }
                            }
                            else if ("defaultValue".equals(prop.getName())) {
                                FacesProperty other = def.getProperty("value");
                                if (null != other && 
                                        !((FacesSimpleProperty) other).isLocalizable()) {
                                    String msg = def.getFile().getFilePath()+" "+descr(def, other)
                                            + "  defaultValue localizable but value not localizable";
                                    fails+= msg +"\n";
                                }
                            }
                        }
                    }
                }
            }
        }
        if( ! expectedDefProps.isEmpty() ){
            // check all expected found
            for (String descr : expectedDefProps) {
                fails+= descr+"  expected not localizable\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testLabelsLocalizable"));
        if( fails.length() > 0){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

	/**
	 * Note, this is not a skip list but a configuration list, 
	 * so there's no checking that these localizable names are used.
	 * @return
	 */
	protected List<String> getCommonLocalizableNames() {
		return Arrays.asList(commonLocalizableNames);
	}
	protected List<String> getExpectedLocalizedDefProps() {
		return new ArrayList<String>();
	}
	protected String[] getSkips() {
		return StringUtil.EMPTY_STRING_ARRAY;
	}
	/**
     * @param capitalizedLocNames2
     * @param name
     * @return
     */
    private boolean contains(List<String> expectedLocNames, String name) {
        if( null == capitalizedLocNames ){
            capitalizedLocNames = new ArrayList<String>(expectedLocNames.size());
            for (String locName : expectedLocNames) {
                String capitalized = Character.toUpperCase(locName.charAt(0))+ locName.substring(1);
                capitalizedLocNames.add(capitalized);
            }
        }
        // "title".equals(name)
        if( expectedLocNames.contains(name) ){
            return true;
        }
        // name.contains("Title");
        for (String locName : capitalizedLocNames ) {
            if( name.contains(locName) ){
                return true;
            }
        }
        // name.startsWith("title");
        for (String locName : expectedLocNames ) {
            if( name.startsWith(locName) ){
                return true;
            }
        }
        return false;
    }

    private String descr(FacesDefinition def, FacesProperty prop) {
        return XspRegistryTestUtil.descr(def, prop);
    }
}
