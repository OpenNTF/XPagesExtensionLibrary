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
 * Date: 24-Mar-2006
 * PropertiesHaveCategoriesTest.java
 */

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.AbstractFacesDefinition;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;


/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com) 24-Mar-2006
 * 
 * Unit: PropertiesHaveCategoriesTest.java
 */
public class PropertiesHaveCategoriesTest extends AbstractXspTest{
    @Override
    public String getDescription() {
        return "that most <property>s have a <category> in the <designer-extensio>, " +
                "except for <property>s in <complex-type> defs " +
                "(or <validator>s, <converter>s or <group>s used in complex defs)";
    }
    public void testPropertyCategory() throws Exception {

        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(), new DefinitionTagsAnnotater());
        
        String fails = "";
        
        // scan through all defs, except for the groups, will handle them below
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            if( ! def.isTag() ){
                continue;
            }
            boolean expectPropCategory;
            if( def instanceof FacesComplexDefinition ){
                expectPropCategory = false;
            }else if( def instanceof FacesComponentDefinition ){
                expectPropCategory = true;
            }else{
               continue; // group definition, check below
            }
            for (FacesProperty prop : RegistryUtil.getProperties(def)) {
                String propCategory = (String) prop.getExtension("category");
                boolean hasPropCategory = null != propCategory;
                if( expectPropCategory != hasPropCategory ){
                    String propName = prop.getName();
                    String ref = def.getFile().getFilePath()+" "+def.getFirstDefaultPrefix()+":"+def.getTagName()+" \"" +propName+"\"";
                    if( expectPropCategory ){
                        fails += ref+" Category not present in <component> <property>.\n";
                    }else{
                        fails += ref+" Category should not be present in <complex-type> <property>, was " +propCategory+".\n";
                    }
                }
            }
        }
        // find uses of groups.
        List<FacesGroupDefinition> groupsInAllLibs = new ArrayList<FacesGroupDefinition>();
        Map<String, List<FacesDefinition>> groupIdToUses = new HashMap<String, List<FacesDefinition>>();
        for (FacesDefinition def : reg.findDefs()) {
            if( def instanceof FacesGroupDefinition ){
                groupsInAllLibs.add((FacesGroupDefinition)def);
            }
            Collection<String> groupTypeRefs = ((AbstractFacesDefinition)def).getGroupTypeRefs();
            if( ! groupTypeRefs.isEmpty() ){
                for (String groupRef : groupTypeRefs) {
                    List<FacesDefinition> uses = groupIdToUses.get(groupRef);
                    if( null == uses ){
                        uses = new ArrayList<FacesDefinition>();
                        groupIdToUses.put(groupRef, uses);
                    }
                    uses.add(def);
                }
            }
        }
        // Build a map of <String groupReferenceId, 
        //      FacesComplexDefinition.class|FacesComponentDefinition.class|null|int.class>
        Map<String, Class<?>> groupIdToType = buildUseTypeMap(groupsInAllLibs, groupIdToUses);
        // for the groups in the library (not all groups in the reg)
        List<FacesGroupDefinition> groupsToTest = new ArrayList<FacesGroupDefinition>();
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            if( def instanceof FacesGroupDefinition ){
                groupsToTest.add((FacesGroupDefinition)def);
            }
        }
        for (String groupId : getExtraGroupsToTest()) {
            FacesGroupDefinition group = (FacesGroupDefinition) reg.findDef(groupId);
            if( null == group ){
               fails += groupId+" Cannot find group for id in getExtraGroupsToTest()\n";
            }else{
                groupsToTest.add(group);
            }
        }
        for (FacesGroupDefinition group : groupsToTest) {
            Class<?> type = groupIdToType.get(group.getReferenceId());
            
            boolean expectPropCategory;
            if( null == type ){
                fails += group.getFile().getFilePath()+" "+group.getReferenceId()+" Unused group, cannot check <property>s for <category>s\n";
                continue;
            }else if( int.class.equals(type) ){
                fails += group.getFile().getFilePath()+" "+group.getReferenceId()+" Dependancy loop, cannot check <property>s for <category>s\n";
                continue;
            }else if( FacesComplexDefinition.class.equals(type) ){
                expectPropCategory = false;
            }else if( FacesComponentDefinition.class.equals(type) ){
                expectPropCategory = true;
            }else{
                throw new RuntimeException("Unexpected problem problem: " +type.getName()+" "+group.getReferenceId());
            }
            
            boolean isTaggedGroupInComplex = DefinitionTagsAnnotater.isGroupTaggedGroupInComplex(group);
            if( (isTaggedGroupInComplex) != (!expectPropCategory) ){
                // This test is the only place that actually computes whether a group is a complex
                // or a component group, so this is where we verify the presence of 
                // <tags>group-in-complex<. That tag is not actually needed by this test,
                // it is used in PropertyStyleTest and other tests where the <category> is verified.
                if( expectPropCategory ){
                    // control.
                    fails += group.getFile().getFilePath()+" "+group.getReferenceId()
                        +" Unexpected <tags>group-in-complex< in a <component> <group>\n";
                }else{
                    // complex.
                    fails += group.getFile().getFilePath()+" "+group.getReferenceId()
                        +" Missing <tags>group-in-complex< in a <complex-type> <group>\n";
                }
            }
            boolean isTaggedGroupInControl = DefinitionTagsAnnotater.isGroupTaggedGroupInControl(group);
            if( (isTaggedGroupInControl) != (expectPropCategory) ){
                // This test is the only place that actually computes whether a group is a complex
                // or a component group, so this is where we verify the presence of 
                // <tags>group-in-control<. That tag is not actually needed by this test,
                // it is used in PropertyStyleTest and other tests where the <category> is verified.
                if( expectPropCategory ){
                    // control.
                    fails += group.getFile().getFilePath()+" "+group.getReferenceId()
                        +" Missing <tags>group-in-control< in a <component> <group>\n";
                }else{
                    // complex.
                    fails += group.getFile().getFilePath()+" "+group.getReferenceId()
                        +" Unexpected <tags>group-in-control< in a <complex-type> <group>\n";
                }
            }
            
            for (FacesProperty prop : RegistryUtil.getProperties(group)) {
                String propCategory = (String) prop.getExtension("category");
                boolean hasPropCategory = null != propCategory;
                if( expectPropCategory != hasPropCategory ){
                    String propName = prop.getName();
                    String ref = group.getFile().getFilePath()+" "+group.getReferenceId()+" \"" +propName+"\"";
                    if( expectPropCategory ){
                        fails += ref+" Category not present in <component> <group>.\n";
                    }else{
                        fails += ref+" Category should not be present in <complex-type> <group>, was " +propCategory+".\n";
                    }
                }
            }
            
            // have the value expectPropCategory based on the 1st def that uses this group,
            // check that all the defs that use this group have the type
            List<FacesDefinition> uses = groupIdToUses.get(group.getReferenceId());
            for (FacesDefinition use : uses) {
                boolean useExpectPropCategory;
                if( use instanceof FacesComplexDefinition ){
                    useExpectPropCategory = false;
                }else if( use instanceof FacesComponentDefinition ){
                    useExpectPropCategory = true;
                }else{
                    Class<?> useType = groupIdToType.get(use.getReferenceId());
                    if( FacesComplexDefinition.class.equals(useType) ){
                        useExpectPropCategory = false;
                    }else if( FacesComponentDefinition.class.equals(useType) ){
                        useExpectPropCategory = true;
                    }else{
                        continue; // will add a fail when iterating through the groups
                    }
                }
                if( expectPropCategory != useExpectPropCategory ){
                    fails +=group.getFile().getFilePath()+" "+group.getReferenceId()+ " is used by "
                            + use.getReferenceId()+ " as a "
                            + (useExpectPropCategory ? "<component> <group>" : "<complex-type> <group>")
                            + " but it is normally used as a "
                            + (expectPropCategory ? "<component> <group>" : "<complex-type> <group>")
                            + " (expectPropCategory=" + expectPropCategory + ")\n";
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testPropertyCategory"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected List<String> getExtraGroupsToTest() {
        return new ArrayList<String>();
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private Map<String, Class<?>> buildUseTypeMap(
            List<FacesGroupDefinition> groups,
            Map<String, List<FacesDefinition>> groupIdToUses) {
        Map<String, Class<?>> groupIdToUseType = new HashMap<String, Class<?>>();
        for (FacesGroupDefinition group : groups) {
            String groupId = group.getReferenceId();
            if( groupIdToUseType.containsKey(groupId) ){
                continue;
            }
            Class<?> useType = findUseType(groupId, groupIdToUseType, groupIdToUses);
            groupIdToUseType.put(groupId, useType);
        }
        return groupIdToUseType;
    }

    private Class<?> findUseType(String groupId,
            Map<String, Class<?>> groupIdToUseType,
            Map<String, List<FacesDefinition>> groupIdToUses) {
        
        if( groupIdToUseType.containsKey(groupId) ){
            return groupIdToUseType.get(groupId);
        }
        List<FacesDefinition> uses = groupIdToUses.get(groupId);
        if( null == uses ){
            return null;
        }

        for (FacesDefinition use : uses) {
            if( use instanceof FacesComplexDefinition ){
                return FacesComplexDefinition.class;
            }else if( use instanceof FacesComponentDefinition ){
                return FacesComponentDefinition.class;
            }
        }
        // else all the uses are groups
        FacesGroupDefinition use = (FacesGroupDefinition)uses.get(0);
        // use int.class as a marker to prevent StackOverflowError when find dependancy loops
        groupIdToUseType.put(groupId, int.class);
        Class<?> useType = findUseType(use.getReferenceId(), groupIdToUseType, groupIdToUses);
        groupIdToUseType.put(use.getReferenceId(), useType);
        return useType;
    }
    public static class PropertyCategoryAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesProperty;
        }
        @Override
        protected String[] createExtNameArr() {
            return new String[]{
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-property-category
                    "category",
            };
        }
    }
}
