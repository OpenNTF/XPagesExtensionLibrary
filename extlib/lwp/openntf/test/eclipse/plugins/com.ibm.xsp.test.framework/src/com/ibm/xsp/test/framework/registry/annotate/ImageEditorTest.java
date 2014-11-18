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
* ImageEditorTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;


import com.ibm.commons.util.StringUtil;
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
public class ImageEditorTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // this is attempting to guess which <property>s are likely to need that picker.
        return "that <property>s called 'image' or 'icon' have the ImagePicker <editor>";
    }

    public void testImageEditor() throws Exception {
        String[] imagePropertyNames = new String[]{
                "image",
                "icon",
                "imageUrl",
                "imagePath",
                "iconUrl",
                "iconPath",
                "picture",
                "src",
                "logo",
        };
        String[] imagePropertyNameSuffixes = new String[]{
                "Image",
                "ImageUrl",
                "ImagePath",
                "Icon",
                "IconUrl",
                "IconPath",
                "Picture",
                "Src",
                "Logo",
        };
        String[] imagePropertyNamePrefixes = new String[]{
                "src",
                "image",
                "icon",
        };
        String[] imageDisplayNameSuffixes = new String[]{
            "image", // wrong case in the display-name, should be capital cased
            "Image",
            "Image URL",
            "icon", // wrong case in the display-name, should be capital cased
            "Icon",
            "Icon URL",
            "Logo",
        };
        String[] imageDescriptionSnippets = new String[]{
            "to an icon", // Path to an icon ...
            "to the icon", // Path to the icon ...
            "of an icon",
            "of the icon",
            "to an image", // URL to an image representing ...
            "to the image", // URL to the image representing ...
            "of an image",
            "of the image",
            "mage to be displayed", // Image to be displayed when ...
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
                
                boolean suspectedImageProperty = false;
                String reason = null;
                
                if( ! suspectedImageProperty ){
                    int index = XspTestUtil.indexOf(imagePropertyNames, propName);
                    if( -1 != index ){
                        suspectedImageProperty = true;
                        reason = "<property-name> is \""+imagePropertyNames[index]+"\"";
                    }
                }
                if( ! suspectedImageProperty ){
                    int index = XspTestUtil.endsWithIndex(imagePropertyNameSuffixes, propName);
                    if( -1 != index ){
                        suspectedImageProperty = true;
                        reason = "<property-name> endsWith \""+imagePropertyNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedImageProperty ){
                    int index = XspTestUtil.startsWithIndex(imagePropertyNamePrefixes, propName);
                    if( -1 != index ){
                        suspectedImageProperty = true;
                        reason = "<property-name> startsWith \""+imagePropertyNamePrefixes[index]+"\"";
                    }
                }
                if( ! suspectedImageProperty ){
                    int index = XspTestUtil.endsWithIndex(imageDisplayNameSuffixes, displayName);
                    if( -1 != index ){
                        suspectedImageProperty = true;
                        reason = "<display-name> endsWith \""+imageDisplayNameSuffixes[index]+"\"";
                    }
                }
                if( ! suspectedImageProperty ){
                    int index = XspTestUtil.containsSubstringIndex(imageDescriptionSnippets, description);
                    if( -1 != index ){
                        suspectedImageProperty = true;
                        reason = "<description> contains \""+imageDescriptionSnippets[index]+"\"";
                    }
                }
                
                if( suspectedImageProperty){
                    if( PropertyTagsAnnotater.isTagged(prop, "not-image-path") ){
                        suspectedImageProperty = false;
                    }
                }
                
                if (suspectedImageProperty) {
                    // verify editor as expected
                    String expectedEditor = "com.ibm.workplace.designer.property.editors.ImagePicker";
                    String actualEditor = (String) prop.getExtension("editor");
                    if (!StringUtil.equals(expectedEditor, actualEditor)) {
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "
                                + propName
                                + "  <editor> not as expected. Was: "
                                + actualEditor + ", expected: " + expectedEditor
                                + ". The property is considered an image because " 
                                + reason + "\n";
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testImageEditor"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
