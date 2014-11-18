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
* Date: 28 Jul 2011
* VarEditorTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.*;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PageEditorTest.EditorAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class VarEditorTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // Note, in 8.5.3 as part of SPR#MKEE8ECFFJ,
        // there was an editor added for pointing to an existing variable name
        // declared elsewhere in the XPage (as in the xp:deleteDocument simple action's 
        // "var" property, which points to a variable name declared 
        // in some xp:dominoDocument in the XPage).
        // There is not yet an editor for declaring a variable name to be published,
        // such as would be used by the xp:dominoDocument "var" property.
        // TODO var declaring properties should have validation using the regular expression "[a-zA-Z][a-zA-Z0-9]*"
        // TODO validation-formula uses a JavaScript expression 
        // - request a designer-extension validation-regexp, using a regular expression for validation.
        return "that properties named var either declare a variable name, " 
            + "or refer to an existing variable name declared elsewhere in the XPage.";
    }
    public void testVarEditor() throws Exception {
        
        String[] varPropertyNames = new String[]{
                "var", // xp:dominoDocument "var", xp:repeat "var", xp:deleteDocument "var" (a variable reference)
        };
        String[] varPropertyNameSuffixes = new String[]{
                "Var", // xp:repeat "indexVar"
        };
        String[] varDisplayNameSuffixes = new String[]{
                "Var",
                "var",
                "Variable",
                "Variable Name",
        };
        String[] varDescriptionSnippets = new String[]{
                "variable name",
                "equest scope variable", // Request scope variable
                "will be made available",
                "equest-scope attribute", // Request scope variable
                "equest scope attribute",
                "is made available",
        };
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new EditorAnnotater(), new DescriptionDisplayNameAnnotater(), 
                new PropertyTagsAnnotater());
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for (FacesProperty prop : RegistryUtil.getProperties(def,
                    def.getDefinedInlinePropertyNames())) {
                if( !String.class.equals(prop.getJavaClass()) ){
                    // only look at String propertys
                    continue;
                }
                
                String propName = prop.getName();
//                String displayName = (String) prop.getExtension("display-name");
                String description = (String) prop.getExtension("description");
                
                boolean suspectedForProperty = false;
                String reason = null;
                
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.indexOf(varPropertyNames, propName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<property-name> is \"" +varPropertyNames[index]+"\"";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.endsWithIndex(varPropertyNameSuffixes, propName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<property-name> endsWith \"" +varPropertyNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.endsWithIndex(varDisplayNameSuffixes, propName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<display-name> endsWith \"" +varDisplayNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.containsSubstringIndex(varDescriptionSnippets, description);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<description> contains \"" +varDescriptionSnippets[index]+"\"";
                    }
                }

                if( suspectedForProperty ){
                    // check for 
                    // <designer-extension><tags>not-server-variable-name</tags></designer-extension>
                    if( PropertyTagsAnnotater.isTaggedNotServerVariableName(prop) ){
                        suspectedForProperty = false;
                    }
                }
                
                if( suspectedForProperty ){
                    // verify editor as expected
                    
                    // The 3 expected editors are:
                    // a) the editor that refers to vars from other data sources in the page
                    String dataSourceReferenceEditor = "com.ibm.workplace.designer.property.editors.dataSourcePicker";
                    // b) the editor that refers to other vars in the page, from both data sources and controls
                    String anyVarReferenceEditor = "com.ibm.designer.domino.xsp.varpicker";
                    // c) the editor used for specifying a var name that will be published
                    //    and made available to server-side script in the XPage
                    String varPublishEditor = "com.ibm.xsp.extlib.designer.tooling.editor.VarNameEditor"; 
                    
                    String actualEditor = (String) prop.getExtension("editor");
                    boolean isOneOfExpectedEditors = 
                            StringUtil.equals(dataSourceReferenceEditor, actualEditor)
                            || StringUtil.equals(anyVarReferenceEditor,actualEditor)
                            || StringUtil.equals(varPublishEditor, actualEditor);
                    if( ! isOneOfExpectedEditors ){
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "+ propName
                                + "  <editor> not as expected. " 
                                + "Was: " + actualEditor + ", expected one of the 3 var editors. " 
                                + "The property is considered 'var'-like because " +reason+". "
                                + "\n";
                    }
                    boolean isExplicitDisallowRuntimeBindings;
                    FacesProperty itemProp = (prop instanceof FacesContainerProperty)?
                            ((FacesContainerProperty)prop).getItemProperty() : prop;
                    isExplicitDisallowRuntimeBindings = (itemProp instanceof FacesSimpleProperty) 
                            && !((FacesSimpleProperty)itemProp).isAllowRunTimeBinding();
                    if( ! isExplicitDisallowRuntimeBindings ){
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "+ propName
                                + "  <allow-run-time-binding>false</ not found - expected for var props. "
                                + "The property is considered 'var'-like because " +reason+". "
                                + "\n";
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testVarEditor"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }

}
