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
* ForEditorTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
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
public class ForEditorTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // note, the editor for controls in the current XPage was added
        // in 8.5.3 as part of SPR#MKEE8ECFFJ
        return "that 'for'-like properties use the <editor> for picking a control id in this .xsp file";
    }
    public void testForEditor() throws Exception {
        
        // note, xp:includePage "componentId" uses a different editor - the controlPicker editor.
        String[] forPropertyNames = new String[]{
                // "-" at start indicates suffix.
                "for", // xp:label "for", xp:pager "for"
                "refreshId", // xp:eventHandler "refreshId"
                "targetId",
                "execId", // xp:eventHandler "execId"
                "inputId", // xp:typeAhead "inputId"
                "modifiedControl", // xp:view "modifiedControl"
                "userId", // ActivityStreams
                "groupId", // ActivityStreams
                "appId", // ActivityStreams
                "activityId", // ActivityStreams
        };
        String[] forPropertyNameSuffixes = new String[]{
                "Control",
                "ComponentId",
                "Id",
        };
        String[] forDisplayNameSuffixes = new String[]{
                "Id",
                "Control",
        };
        String[] forDescriptionSnippets = new String[]{
                "the target control",
                "the id of the",
                "ID of the",
                "ID of a",
                "Identifies the control",
                "he control that", //modifiedControl description contains "The control that"
                "id of the component",
                "ID of the component",
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
                String displayName = (String) prop.getExtension("display-name");
                String description = (String) prop.getExtension("description");
                
                boolean suspectedForProperty = false;
                String reason = null;
                
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.indexOf(forPropertyNames, propName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<property-name> is '" +forPropertyNames[index]+"'";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.endsWithIndex(forPropertyNameSuffixes, propName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<property-name> endsWith '" +forPropertyNameSuffixes[index]+"'";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.endsWithIndex(forDisplayNameSuffixes, displayName);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<display-name> endsWith '" +forDisplayNameSuffixes[index]+"'";
                    }
                }
                if( ! suspectedForProperty ){
                    int index = XspTestUtil.containsSubstringIndex(forDescriptionSnippets, description);
                    if( -1 != index ){
                        suspectedForProperty = true;
                        reason = "<description> contains '" +forDescriptionSnippets[index]+"'";
                    }
                }
                if( suspectedForProperty ){
                    if( PropertyTagsAnnotater.isTagged(prop, "not-control-id-reference") ){
                        suspectedForProperty = false;
                    }
                }
                if( suspectedForProperty ){
                    // verify editor as expected
                    
                    // idpicker was added in Designer 8.5.3 for SPR#MKEE8ECFFJ
                    // and gives a combo with all the ids in the current XPage
                    String expectedEditorAnyControl = "com.ibm.designer.domino.xsp.idpicker";
                    // XPageControlIDEditor was added in Designer 8.5.3UP1
                    // and gives a combo with IDs restricted to just those with the given namespace/tagNames
                    String expectedEditorSpecificControls = "com.ibm.xsp.extlib.designer.tooling.editor.XPageControlIDEditor";
                    
                    String actualEditor = (String) prop.getExtension("editor");
                    boolean isMatchAnyControlEditor = expectedEditorAnyControl.equals(actualEditor);
                    boolean isMatchSpecificControlEditor = expectedEditorSpecificControls.equals(actualEditor);
                    if (!(isMatchAnyControlEditor || isMatchSpecificControlEditor)) {
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "
                                + propName
                                + "  <editor> not as expected. Was: "
                                + actualEditor + ", expected one of the 2 control pickers. "
                                + "The property is considered 'for'-like because " 
                                +reason+"\n";
                    }
                    
                    String editorParamStr = (String) prop.getExtension("editor-parameter");
                    if( isMatchAnyControlEditor && null != editorParamStr ){
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "
                                + propName
                                + "  Unexpected <editor-parameter>\n";
                    }
                    if( isMatchSpecificControlEditor ){
                        String[][] editorParamArr = null == editorParamStr? null : parseIdEditorParams(editorParamStr);
                        if( null == editorParamArr || editorParamArr.length == 0 ){
                            fails += def.getFile().getFilePath()+ " "
                                    + ParseUtil.getTagRef(def) + " "
                                    + propName
                                    + "  Missing expected <editor-parameter> tag with list of namespace|tagName\n";
                        }else{
                        for (String[] namespaceAndTag : editorParamArr) {
                            FacesDefinition referenced = reg.findDef(namespaceAndTag[0], namespaceAndTag[1]);
                            if( null == referenced || !(referenced instanceof FacesComponentDefinition) 
                                    || ! referenced.isTag() ){
                                fails += def.getFile().getFilePath()+ " "
                                        + ParseUtil.getTagRef(def) + " "
                                        + propName
                                        + "  <editor-parameter> tag reference " 
                                        + "does not resolve to a known namespace|tagName: "+
                                        namespaceAndTag[0]+"|"+namespaceAndTag[1]+"\n";
                            }
                        } // end for
                        } // end else editorParam.length > 0
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testForEditor"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private String[][] parseIdEditorParams(String params){
        // Same code as in design-time code that uses the params
        ArrayList<String[]> controlsToInclude = new ArrayList<String[]>();
        //we allow parameters separated by commas or carriage returns, so those are our tokens.
        String tokens = ",\r\n";  // $NON-NLS-1$
        StringTokenizer st = new StringTokenizer(params, tokens);
        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();

            if(StringUtil.isNotEmpty(line)) {
                String namespace;
                String tagName;
                //namespace and tagName are separated by colons. 
                int pos = line.indexOf("|");
                if(pos>=0) {
                    namespace = line.substring(0,pos);
                    tagName = line.substring(pos+1);
                }else{
                    namespace = null;
                    tagName = line;
                }
                controlsToInclude.add(new String[]{namespace,tagName});
            }
        }
        return controlsToInclude.toArray(new String[controlsToInclude.size()][]);
    }

}
