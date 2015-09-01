/*
 * © Copyright IBM Corp. 2013, 2015
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
* Date: 13 Aug 2008
* PropertyAllowsValueTest.java
*/

package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesPropertyType;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.types.FacesSimpleTypes;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 13 Aug 2008
 * Unit: PropertyAllowsValueTest.java
 */
public class PropertyAllowsValueTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that every property has some value that can be set";
    }
    public void testCanSetProperty() throws Exception {
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        HashMap<Class<?>, Boolean> propertyClassToHasComplex = new HashMap<Class<?>, Boolean>();
        List<Class<?>> propertyClassSkips = getPropertyClassSkips();
        List<Class<?>> checkedPropClassSkips = new ArrayList<Class<?>>(propertyClassSkips);
        
        String fails = "";
        for(FacesDefinition def : TestProject.getLibCompComplexDefs(reg, this)){
            
            // for each local property
            for (String name : def.getDefinedPropertyNames()) {
                FacesProperty localProp = def.getProperty(name);
                FacesSimpleProperty simple = toSimple(localProp);
                if( null == simple ){
                    // it's a method binding prop, assume they're valid
                    continue;
                }
                
                if( isPrimitive(simple) ){
                    if (!simple.isAllowNonBinding()
                            && !simple.isAllowLoadTimeBinding()
                            && !simple.isAllowRunTimeBinding()) {
                        fails += def.getFile().getFilePath()+" "+descr(def, name)+" property cannot be set to a primitive nor a binding\n";
                    }
                }else{ // non-primitive, usually complex-type
                    if( !simple.isAllowNonBinding() ){
                        // not allow complex-type, so should allow runtime or load time binding.
                        if( !simple.isAllowLoadTimeBinding()
                            && !simple.isAllowRunTimeBinding() ){
                            fails += def.getFile().getFilePath()+" "+descr(def, name)+" property cannot be set to a binding nor a complex-type\n";
                        }
                    }else{ // allow non-binding, so should have a corresponding complex-type
                        
                    Class<?> propertyClass = simple.getJavaClass();
                    Boolean hasComplex = propertyClassToHasComplex.get(propertyClass);
                    if( null == hasComplex ){
                        hasComplex = computeHasComplex(reg,simple);
                        if( !hasComplex){ 
                            int skipIndex = propertyClassSkips.indexOf(propertyClass);
                            if( -1 != skipIndex ){
                                hasComplex = true;
                                // mark propertyClassSkip used
                                checkedPropClassSkips.set(skipIndex, null);
                            }
                        }
                        propertyClassToHasComplex.put(propertyClass, hasComplex);
                    }
                    if( ! hasComplex.booleanValue() ){
                        String fail = def.getFile().getFilePath()+" "+descr(def, name)
                                + " No complex-type that can be set for property-class "
                                + propertyClass.getName() + " and not explicitly preventing non-binding";
                        fails += fail + "\n";
                    }
                    }
                }
            }
        }
        List<Object[]>extraToTest = getExtraLibPropClassesToTest();
        for (Object[] libToClassArr : extraToTest) {
            String libId = (String) libToClassArr[0];
            Class<?>[] propClassArr = (Class<?>[]) libToClassArr[1];
            
            List<Class<?>> extraPropClassList = Arrays.asList(propClassArr);
            boolean[] foundPropClass = new boolean[propClassArr.length];
            
            for (FacesDefinition def : getLibCompComplexDefs(reg, libId) ) {
                // for each local property
                for (String name : def.getDefinedPropertyNames()) {
                    FacesProperty localProp = def.getProperty(name);
                    FacesSimpleProperty simple = toSimple(localProp);
                    if( null == simple || isPrimitive(simple)){
                        // not an extra class
                        continue;
                    }
                    
                    Class<?> propertyClass = simple.getJavaClass();
                    int extraPropClassIndex = extraPropClassList.indexOf(propertyClass);
                    if( -1 == extraPropClassIndex ){
                        // not in the list of extra property-classes to check
                        continue;
                    }
                    foundPropClass[extraPropClassIndex] = true;
                    
                    Boolean hasComplex = propertyClassToHasComplex.get(propertyClass);
                    if( null == hasComplex ){
                        hasComplex = computeHasComplex(reg,simple);
                        if( !hasComplex){ 
                            int skipIndex = propertyClassSkips.indexOf(propertyClass);
                            if( -1 != skipIndex ){
                                hasComplex = false;
                                // mark propertyClassSkip used
                                checkedPropClassSkips.set(skipIndex, null);
                            }
                        }
                        propertyClassToHasComplex.put(propertyClass, hasComplex);
                    }
                    if( ! hasComplex.booleanValue() ){
                        String fail = def.getFile().getFilePath()+" "+descr(def, name)
                                + " No complex-type that can be set for the extra property-class "
                                + propertyClass.getName();
                        fails += fail+"\n";
                    }
                }
            }
            int i = 0;
            for (boolean found : foundPropClass) {
                if( ! found ){
                    fails += "Extra library property-class not found: "+propClassArr[i].getName()+"\n";
                }
                i++;
            }
        }
        
        for (Class<?> skip : checkedPropClassSkips) {
            if( null != skip ){
                fails += "Unused property-class skip: "+skip.getName()+"\n";
            }
        }
        
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testCanSetProperty"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private List<FacesDefinition> getLibCompComplexDefs(FacesSharableRegistry reg, String libId) {
        
        boolean libraryFound = false;
        List<FacesDefinition> list = null;
        for (FacesSharableRegistry depend : reg.getDepends()) {
            if( !depend.getId().endsWith(libId) ){
                continue;
            }
            libraryFound = true;
            for (FacesProject proj : depend.getLocalProjectList()) {
                for (FacesLibraryFragment file : proj.getFiles()) {
                    for (FacesDefinition def : file.getDefs()) {
                        if( def instanceof FacesComplexDefinition || def instanceof FacesComponentDefinition ){
                            // found one
                            if( null == list ){
                                list = new ArrayList<FacesDefinition>();
                            }
                            list.add(def);
                        }
                    }
                }
            }
        }
        if( ! libraryFound ){
            throw new RuntimeException("Library not found in depends registries: "+libId);
        }
        if( null == list ){
            return Collections.emptyList();
        }
        return list;
    }
    protected List<Class<?>> getPropertyClassSkips() {
        return new ArrayList<Class<?>>();
    }
    /** 
     *  {stringLibName1, Class[]{propClass1, propClass2} },
     *  {stringLibNam2, Class[]{propClass1, propClass2, propClass3} }
     * @return
     */
    protected List<Object[]> getExtraLibPropClassesToTest(){
       return new ArrayList<Object[]>();
    }
    private String descr(FacesDefinition def, String name) {
        return XspRegistryTestUtil.descr(def, name);
    }
    protected String[] getSkips() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private Boolean computeHasComplex(FacesSharableRegistry reg, FacesSimpleProperty simple) {
        // find if there's some complex-type tag that can be used for the
        // property. This is the same mechanism DDE uses to populate the [+]
        // menu in the All Properties view.
        FacesDefinition typeDef = simple.getTypeDefinition();
        if( typeDef instanceof FacesPropertyType ) {
            return Boolean.TRUE;
        }
        FacesComplexDefinition complexInterface = (FacesComplexDefinition) typeDef;
        for(FacesDefinition def : RegistryUtil.getSubstitutableDefinitions(complexInterface, reg)){
            if( def.isTag() && def instanceof FacesComplexDefinition ){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    private boolean isPrimitive(FacesSimpleProperty simple) {
        int type = simple.getType();
        return FacesSimpleTypes.isPrimitive(type)
                || FacesSimpleTypes.isPrimitiveObject(type)
                || FacesSimpleTypes.isGeneric(type);
    }
    private FacesSimpleProperty toSimple(FacesProperty prop) {
        if( prop instanceof FacesContainerProperty ){
            prop = ((FacesContainerProperty)prop).getItemProperty();
        }
        if( prop instanceof FacesSimpleProperty){
            return (FacesSimpleProperty) prop;
        }
        return null;
    }
}
