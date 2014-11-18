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
 * Created on 11-Jul-2005
 * Created by Maire Kehoe (mkehoe@ie.ibm.com)
 */
package com.ibm.xsp.test.framework.registry;


import javax.faces.component.UIComponent;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 
 */
public class ComponentTypeTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "That the xsp-config component-type equals the "
                + "UIComponent.COMPONENT_TYPE";
    }
    public void testComponentType() {

        FacesSharableRegistry reg = TestProject.createRegistry(this);
        String fails = "";

        // Iterate through all components and check component type
        for (FacesComponentDefinition compDef: TestProject.getLibComponents(reg, this) ) {
            
            if( compDef instanceof FacesCompositeComponentDefinition ){
                continue;
            }

            String configType = compDef.getComponentType();
            
            // check the value of any public static String COMPONENT_TYPE
            String declaredType = getDeclaredCompType(compDef.getJavaClass());
            if (null != declaredType) {
                if ( !configType.equals(declaredType) ) {
                    fails += compDef.getFile().getFilePath()+" "+"component-type " + compDef.getComponentType()
                            + " (!= " + className(compDef) + ".COMPONENT_TYPE "
                            + declaredType + ")\n";
                }
            }
            else if ( isRequireComponentTypeConst( compDef, false ) ) {
                fails += compDef.getFile().getFilePath()+" "+"No constant COMPONENT_TYPE " + configType
                + " in " + className(compDef) + "\n";
            }
            
            String declaredFamily = compDef instanceof FacesCompositeComponentDefinition? null
                   : getDeclaredCompFamily(compDef.getJavaClass());
            if( null != declaredFamily ){
                if( !StringUtil.equals(compDef.getComponentFamily(), declaredFamily)){
                    fails += compDef.getFile().getFilePath()+" "+"component-family " + compDef.getComponentFamily()
                    + " (!= " + className(compDef) + ".COMPONENT_FAMILY "
                    + declaredFamily + ")\n";
                }
            }else if( isRequireComponentFamilyConst(compDef, false) ){
                FacesComponentDefinition parent = (FacesComponentDefinition)compDef.getParent();
                boolean nonInheritedFamily = null == parent || !StringUtil.equals(compDef.getComponentFamily(), parent.getComponentFamily());
                if( nonInheritedFamily ){
                    fails += compDef.getFile().getFilePath() + " "
                            + "No constant COMPONENT_FAMILY in "
                            + className(compDef) + ". <component-family>"
                            + compDef.getComponentFamily()+"</ \n";
                }
            }
            
            // check that the application is a factory for the
            // specified type.
            // i.e. that it's specified in a faces-config.xml file.
            // uncomment this when supporting JSF properly, and createComponent works
            // No, will never use these createComponent methods nor register component-types 
            // in faces-config.xml files.
//            if( false ){
//                    UIComponent created;
//                    try{
//                        created = app.createComponent( 
//                            compDef.getComponentType() );
//                    }catch (FacesException e) {
//                        e.printStackTrace();
//                        fails += "component with component-type "
//                            + compDef.getComponentType()
//                            + " not found in the faces-config.xml files \n";
//                        continue;
//                    }
//                    
//                    // check the factory returns an instance of the def's 
//                    // java class
//                    assertEquals(compDef.getJavaClass(), created.getClass());
//            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(null, this, "testComponentType"));
        if (StringUtil.isNotEmpty(fails)) {
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected boolean isRequireComponentTypeConst(FacesComponentDefinition compDef, boolean defaultValue){
//        String shortClass = TestUtil.getShortClass(compDef.getJavaClass());
//        boolean required = !shortClass.startsWith("Xsp");
//        return required;
        return defaultValue;
    }
    protected boolean isRequireComponentFamilyConst(FacesComponentDefinition compDef, boolean defaultValue){
//        String shortClass = TestUtil.getShortClass(compDef.getJavaClass());
//        if( shortClass.startsWith("Xsp") ){
//            return false;
//        }
//        return isSuggestFamilyConstant(compDef);
      return defaultValue;
    }

	/**
	 * Util available to call in subclass.
	 * @param compDef
	 * @return
	 */
	protected boolean isSuggestFamilyConstant(FacesComponentDefinition compDef) {
		FacesComponentDefinition parent = (FacesComponentDefinition) compDef.getParent();
        if( null == parent ){
            // this is the UIComponent def
            return false;
        }
        if( parent.getJavaClass().equals(UIComponent.class) ){
            // controls without an ancestor should have their own component-family.
            return true;
        }
        boolean familyDiffers = !StringUtil.equals(compDef.getComponentFamily(), parent.getComponentFamily());
        return familyDiffers;
	}
    private String className(FacesComponentDefinition compDef) {
        return XspTestUtil.getShortClass(compDef.getJavaClass());
    }

    private static String getDeclaredCompType(Class<?> clazz) {
        return XspTestUtil.getStringConstant(clazz, "COMPONENT_TYPE", true);
    }

    private static String getDeclaredCompFamily(Class<?> clazz) {
        return XspTestUtil.getStringConstant(clazz, "COMPONENT_FAMILY", true);
    }
}
