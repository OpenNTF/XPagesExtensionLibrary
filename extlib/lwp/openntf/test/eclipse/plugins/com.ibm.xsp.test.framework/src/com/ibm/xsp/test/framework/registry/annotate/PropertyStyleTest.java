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
* Date: 17 Dec 2007
* PropertyStyleTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.*;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 17 Dec 2007
 * Unit: PropertyStyleTest.java
 */
public class PropertyStyleTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that style and styleClass attributes have correct -extension contents";
    }
    
    public void testStyleProperties() throws Exception {
    
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new StyleExtensionAnnotater(),
                new DefinitionTagsAnnotater(),
                new PropertyTagsAnnotater());
        
        String fails = "";
        
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            
            // for all defined properties
            for (FacesProperty prop : RegistryUtil.getDefinedProperties(def)) {
                fails += checkProp(def, prop);
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testStyleProperties"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    /**
	 * @return
	 */
	protected String[] getSkips() {
		return StringUtil.EMPTY_STRING_ARRAY;
	}
    private String checkProp(FacesDefinition def, FacesProperty prop) {
        
        // these 3 extensions are parsed and added in StyleExtensionAnnotater
        String editor = (String) prop.getExtension("editor");
        String stylesExcluded = (String) prop.getExtension("styles-excluded");
        String category = (String) prop.getExtension("category");
        
        // || (null != ext && "styling".equals(category) )
        boolean isStyleProp = "style".equals(prop.getName())
                || prop.getName().endsWith("Style")
                || prop.getName().endsWith("Styles")
                || "com.ibm.workplace.designer.property.editors.StylesEditor"
                        .equals(editor)
                || null != stylesExcluded
                || (prop.getName().startsWith("style") && !prop.getName().startsWith("styleClass") )
                || (prop.getName().contains("Style") && !prop.getName().contains("StyleClass"))
                || false;
        
        boolean isStyleClassProp = "styleClass".equals(prop.getName())
                || prop.getName().startsWith("styleClass")
                || prop.getName().endsWith("Class") 
                || prop.getName().contains("Class")
                || prop.getName().endsWith("Classes")
                || "com.ibm.workplace.designer.property.editors.StyleClassEditor"
                        .equals(editor);
        
        if( isStyleProp ){
            // check for 
            // <designer-extension><tags>not-css-style</tags></designer-extension>
            if( PropertyTagsAnnotater.isTaggedNotCssStyle(prop) ){
                isStyleProp = false;
            }
        }
        if( isStyleClassProp ){
            // check for 
            // <designer-extension><tags>not-css-class</tags></designer-extension>
            if( PropertyTagsAnnotater.isTaggedNotCssClass(prop) ){
                isStyleClassProp = false;
            }
        }
        
        String fails = "";
        
        // check styling category
        if (!isStyleProp && !isStyleClassProp) {
            if ( "styling".equals(category) ) {
                fails += def.getFile().getFilePath()+" "+descr(def, prop)
                        + " - category styling but neither style nor styleClass [consider the format category]\n";
            }
        }
        else if (isStyleProp || isStyleClassProp) {
            Boolean expectNull;
            if( def instanceof FacesComplexDefinition ){
                expectNull = isExpectNullComplexCategory((FacesComplexDefinition)def);
                if( null == expectNull ){
                    expectNull = Boolean.TRUE;
                }
            }else if(def instanceof FacesGroupDefinition){
                FacesGroupDefinition group = (FacesGroupDefinition)def;
                expectNull = isExpectNullGroupCategory(group);
                if( null == expectNull ){
                    if( DefinitionTagsAnnotater.isGroupTaggedGroupInComplex(group) ){
                        expectNull = Boolean.TRUE;
                    }
                    if( DefinitionTagsAnnotater.isGroupTaggedGroupInControl(group) ){
                        expectNull = Boolean.FALSE;
                    }
                }
            }else{
                expectNull = Boolean.FALSE;
            }
            if( null == expectNull ){
                // note, must provide a skip for every complex or group style-like category
                fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - category(" +category+") may be >styling< or null (when property on complex-type)\n";
            }else if( Boolean.TRUE.equals(expectNull) ){
                if( null != category ){
                    fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - category(" +category+") should be null (when property on complex-type)\n";
                }
            }else{ // Boolean.FALSE.equals(expectNull)
                if( !"styling".equals(category) ){
                    fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - category(" +category+") not styling \n";
                }
            }
        }
        // check editor & styles-excluded
        if( isStyleProp ){
            if (!"com.ibm.workplace.designer.property.editors.StylesEditor"
                    .equals(editor)) {
                fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - editor not StylesEditor (is:" +editor+")\n";
            }
            if( null != stylesExcluded ){
                boolean allowStylesExcluded;
                if( def instanceof FacesComponentDefinition ){
                    allowStylesExcluded = true;
                }else if( def instanceof FacesGroupDefinition ){
                    if( DefinitionTagsAnnotater.isGroupTaggedGroupInControl((FacesGroupDefinition)def) ){
                        allowStylesExcluded = true;
                    }else{
                        allowStylesExcluded = false;
                    }
                }else{
                    allowStylesExcluded = false;
                }
                if( ! allowStylesExcluded ){
                fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - styles-excluded can only be set on a component (or a group used only by a component)\n";
                }
            }
        }
        if( isStyleClassProp ){
            if (!"com.ibm.workplace.designer.property.editors.StyleClassEditor"
                    .equals(editor)) {
                fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - editor not StyleClassEditor \n";
            }
            if(null != stylesExcluded ){
                fails += def.getFile().getFilePath()+" "+descr(def, prop) + " - styles-excluded cannot be set on styleClass (only on style)\n";
            }
            // doesn't have to be isStyleClass because where it is multiple
            // styleClasses isStyleClass is not set
        }
        return fails;
    }

    /**
     * true, false or null (uncertain)
	 * @param def
	 * @return
	 */
    protected Boolean isExpectNullGroupCategory(FacesGroupDefinition def) {
        // TODO will be able to remove this ambiguity once all xpages runtime core 
        // <group> definitions contain <tags>group-in-complex<  or group-in-control
		return null;
	}
	/**
	 * @param def
	 * @return
	 */
	protected Boolean isExpectNullComplexCategory(FacesComplexDefinition def) {
		return Boolean.TRUE;
	}
//    private boolean isNoCategorySkipped(FacesDefinition def, FacesProperty prop ){
//        for (int i = 0; i < noCategorySkips.length; i++) {
//            Object[] skip = noCategorySkips[i];
//            if( null == skip ){
//                continue;
//            }
//            
//            if( def.getJavaClass() == skip[0] ){
//                if( skip[1].equals(prop.getName()) ){
//                    noCategorySkips[i] = null;
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
    private String descr(FacesDefinition def, FacesProperty prop) {
        return XspRegistryTestUtil.descr(def, prop);
    }
    private static class StyleExtensionAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesProperty;
        }
        @Override
        protected String[] createExtNameArr() {
            return new String[]{
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-property-category
                    "category",
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-editor
                    "editor",
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-styles-excluded
                    "styles-excluded",
            };
        }
    }
}
