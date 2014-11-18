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
 * PageEditorTest.java
 */
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
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
public class PageEditorTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // this is attempting to guess which <property>s are likely to need that picker.
        return "that <property>s called 'page' or 'pageName' have the PagePicker <editor>";
    }

    public void testPageEditor() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new EditorAnnotater(), new DescriptionDisplayNameAnnotater(), 
                new PropertyTagsAnnotater());
        String[] pagePropertyNames = new String[]{
                "pageName",
                "page",
                "pageUrl", // like the xp:viewColumn pageUrl prop.
        };
        pagePropertyNames = update("pagePropertyNames", pagePropertyNames);
        String[] pagePropertyNameSuffixes = new String[]{
                "PageName",
                "Page",
        };
        pagePropertyNameSuffixes = update("pagePropertyNameSuffixes", pagePropertyNameSuffixes);
        String[] pageDisplayNameSuffixes = new String[]{
                "Page",
                "Page Name",
                "Page URL",
        };
        pageDisplayNameSuffixes = update("pageDisplayNameSuffixes", pageDisplayNameSuffixes);
        String[] pageDescriptionSnippets = new String[]{
                "ame of the page", // name of the page to open when ...
                "page URL",
                "URL page",
        };
        pageDescriptionSnippets = update("pageDescriptionSnippets", pageDescriptionSnippets);
        
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for (FacesProperty prop : RegistryUtil.getProperties(def,
                    def.getDefinedInlinePropertyNames())) {
                if( !String.class.equals(prop.getJavaClass()) ){
                    // only look at String propertys
                    continue;
                }
                
                String displayName = (String) prop.getExtension("display-name");
                displayName = (null == displayName)? "" : displayName;
                String description = (String) prop.getExtension("description");
                description = (null == description)? "" : description;
                String propName = prop.getName();
                
                boolean suspectedPageProperty = false;
                String reason = null;
                
                if( ! suspectedPageProperty ){
                    int index = XspTestUtil.indexOf(pagePropertyNames, propName);
                    if( -1 != index ){
                        suspectedPageProperty = true;
                        reason = "<property-name> is \""+pagePropertyNames[index]+"\"";
                    }
                }
                if( ! suspectedPageProperty ){
                    int index = XspTestUtil.endsWithIndex(pagePropertyNameSuffixes, propName);
                    if( -1 != index ){
                        suspectedPageProperty = true;
                        reason = "<property-name> endsWith \""+pagePropertyNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedPageProperty ){
                    int index = XspTestUtil.endsWithIndex(pageDisplayNameSuffixes, displayName);
                    if( -1 != index ){
                        suspectedPageProperty = true;
                        reason = "<display-name> endsWith \""+pageDisplayNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedPageProperty ){
                    int index = XspTestUtil.containsSubstringIndex(pageDescriptionSnippets, description);
                    if( -1 != index ){
                        suspectedPageProperty = true;
                        reason = "<description> contains \""+pageDescriptionSnippets[index]+"\"";
                    }
                }
                
                if( suspectedPageProperty){
                    if( PropertyTagsAnnotater.isTagged(prop, "not-xpage-name") ){
                        suspectedPageProperty = false;
                    }
                }
                if ( suspectedPageProperty ) {
                    // verify editor as expected
                    String expectedEditor = "com.ibm.workplace.designer.property.editors.PagePicker";
                    String actualEditor = (String) prop.getExtension("editor");
                    if (!StringUtil.equals(expectedEditor, actualEditor)) {
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "
                                + propName
                                + "  <editor> not as expected. Was: "
                                + actualEditor + ", expected: " + expectedEditor
                                + ". The property is considered a page name because " 
                                + reason + "\n";
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testPageEditor"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected String[] update(String arrType, String[] defaultValue){
        return defaultValue;
    }

    public static final class EditorAnnotater extends
            DesignerExtensionSubsetAnnotater {
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return (parsed instanceof FacesProperty);
        }
        @Override
        protected String[] createExtNameArr() {
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#editor
            return new String[] { "editor", "editor-parameter" };
        }
    }

}
