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
package xsp.extlib.test.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.test.framework.registry.annotate.PageEditorTest;

/**
 * @author Arturas Lebedevas
 *
 */
public class MobilePageEditorTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // this is attempting to guess which <property>s are likely to need that picker in extlib-mobile.xsp-config.
        return "that <property>s called 'moveTo' or 'pageName' have the corresponding <editor> in extlib-mobile.xsp-config";
    }
    
    public void testMobilePageEditor() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PageEditorTest.EditorAnnotater(), new SpellCheckTest.DescriptionDisplayNameAnnotater(), 
                new PropertyTagsAnnotater());
        String[] pagePropertyNames = new String[]{
                "moveTo",
                "pageName",
        };
        
        String[] pagePropertyNameSuffixes = new String[]{
                "PageName",
                "MoveTo",
        };
        
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            if(def.getFile().getFilePath().endsWith("-mobile.xsp-config")) {
                for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedInlinePropertyNames())) {
                    String propName = prop.getName();
                    
                    boolean suspectedPageProperty = false;
                    String reason = null;
                    
                    if(!suspectedPageProperty) {
                        int index = XspTestUtil.indexOf(pagePropertyNames, propName);
                        if(index != -1) {
                            suspectedPageProperty = true;
                            reason = "<property-name> is \""+pagePropertyNames[index]+"\"";
                        }
                    }
                    
                    if(!suspectedPageProperty) {
                        int index = XspTestUtil.endsWithIndex(pagePropertyNameSuffixes, propName);
                        if(index != -1) {
                            suspectedPageProperty = true;
                            reason = "<property-name> ends with \""+pagePropertyNameSuffixes[index]+"\"";
                        }
                    }
                    
                    // moveTo
                    if(suspectedPageProperty ) {
                        if( PropertyTagsAnnotater.isTagged(prop, "not-mobile-appPage-ref") ){
                            suspectedPageProperty = false;
                        }
                    }
                    
                    if(suspectedPageProperty) {
                        // verify editor as expected
                        String actualEditor = (String) prop.getExtension("editor");
                        String actualEditorParams = (String) prop.getExtension("editor-parameter");
                        String expectedEditor = "com.ibm.designer.domino.xsp.attrvalpicker";
                        String expectedEditorParams = "http://www.ibm.com/xsp/coreex,appPage,pageName";
                        
                        if(!StringUtil.equals(expectedEditor, actualEditor)) {
                            fails += def.getFile().getFilePath()+ " "
                                    + ParseUtil.getTagRef(def) + " "
                                    + propName
                                    + "  <editor> not as expected. Was: "
                                    + actualEditor + ", expected: " + expectedEditor
                                    + ". The property is considered a mobile appPage name because " 
                                    + reason + "\n";
                        }
                        if(!StringUtil.equals(expectedEditorParams, actualEditorParams)) {
                            fails += def.getFile().getFilePath()+ " "
                                    + ParseUtil.getTagRef(def) + " "
                                    + propName
                                    + "  <editor-parameter> not as expected. Was: "
                                    + actualEditorParams + ", expected: " + expectedEditorParams
                                    + ". The property is considered a mobile appPage name because " 
                                    + reason + "\n";
                        }
                    }
                    
                }
            }
        }
        
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testMobilePageEditor"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    
}
