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
* Date: 24 Feb 2009
* NoRunTimeBindingsTest.java
*/

package com.ibm.xsp.test.framework.registry;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObject;
import com.ibm.xsp.registry.*;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 24 Feb 2009
 * 
 * Unit: NoRunTimeBindingsTest.java
 */
public class NoRunTimeBindingsTest extends AbstractXspTest {
    
    @Override
    public String getDescription() {
        return "that only the expected properties disallow runtime bindings (and are hardcoded in this test)";
    }
    public void testNoRunTimeBindings() throws Exception {
        String fails = "";
        List<DefinitionDisallowInfo> disallowInfos = parseDisallowInfo(getDisallowedBindingPropList());
        String[] propNamesAlwaysDisallowArr = getPropNamesAlwaysDisallow();
        String[] propNameSuffixesAlwaysDisallowArr = getPropNameSuffixesAlwaysDisallow();
        int[] disallowIndexHint = new int[]{-1};
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesDefinition def : TestProject.getLibCompComplexDefs(reg, this)) {
            clearIndexHint(disallowIndexHint);
            
            // only testing inline and group-type-ref props, not inherited
            for (String propName : def.getDefinedPropertyNames()) {
                FacesProperty prop = def.getProperty(propName);
                boolean isContainerProp = prop instanceof FacesContainerProperty;
                if( isContainerProp ){
                    prop = ((FacesContainerProperty)prop).getItemProperty();
                }
                if( prop instanceof FacesMethodBindingProperty ){
                    continue;
                }
                
                boolean actualDisallow = prop instanceof FacesSimpleProperty && 
                        !((FacesSimpleProperty)prop).isAllowRunTimeBinding();
                
                boolean isSuspectShouldDisallow = false;
                String reason = null;
                int disallowPropNameIndex;
                int disallowPropNameSuffixIndex;
                if( isContainerProp ){
                    isSuspectShouldDisallow = true;
                    reason = "property allows multiple values";
                }else if( (def instanceof FacesComplexDefinition) && 
                        !(ValueBindingObject.class.isAssignableFrom(def.getJavaClass())) ){
                    isSuspectShouldDisallow = true;
                    reason = "complex-type not implementing ValueBindingObject";
                }
                else if( -1 != (disallowPropNameIndex = XspTestUtil.indexOf(propNamesAlwaysDisallowArr, propName)) ){
                    isSuspectShouldDisallow = true;
                    reason = "property-name " 
                                + propNamesAlwaysDisallowArr[disallowPropNameIndex]
                                    + " usually disallows";
                }else if( -1 != (disallowPropNameSuffixIndex = XspTestUtil.endsWithIndex(propNameSuffixesAlwaysDisallowArr, propName)) ){
                        isSuspectShouldDisallow = true;
                        reason = "property-name suffix " 
                                    +propNameSuffixesAlwaysDisallowArr[disallowPropNameSuffixIndex]
                                        +" usually disallows";
                }else if( prop instanceof FacesSimpleProperty && null != ((FacesSimpleProperty)prop).getTypeDefinition() ){
                    isSuspectShouldDisallow = true;
                    reason = "property-class corresponds to complex-type class";
                }
                if( actualDisallow){
                    if( !isMarkSkipped(disallowInfos, disallowIndexHint, def, propName)){
                        if( isSuspectShouldDisallow ){
                            fails += def.getFile().getFilePath()+ " "
                                    + ParseUtil.getTagRef(def) + " "+ propName
                                    + "  Property has <allow-run-time-binding>false< " 
                                    + "but not in junit hard-coded list of disallow props. " 
                                    + "Disallow is expected because: " +reason
                                    +". Skip would be: " 
                                    + toSkipString(def.getJavaClass(), propName)
                                    +"\n";
                        }else{
                            fails += def.getFile().getFilePath()+ " "
                                    + ParseUtil.getTagRef(def) + " "+ propName
                                    + "  Property has <allow-run-time-binding>false< " 
                                    + "but not in junit hard-coded list of disallow props. " 
                                    + "Intentional disallow?" 
                                    +" Skip would be: " 
                                    + toSkipString(def.getJavaClass(), propName)
                                    +"\n";
                        }
                    }// else is disallow & in hardcoded list of disallows
                }else{
                    // !actualDisallow
                    if( isSuspectShouldDisallow ){
                        fails += def.getFile().getFilePath()+ " "
                                + ParseUtil.getTagRef(def) + " "+ propName
                                + "  Property does not have <allow-run-time-binding>false< " 
                                + "but disallow is expected because: "+reason
                                +"\n";
                    }
                }
            }
            clearIndexHint(disallowIndexHint);
        }
        for (DefinitionDisallowInfo info : disallowInfos) {
            if( info.isDisallowAllProps ){
                if( ! info.disallowAllSkipUsed ){
                    String skipStr = toSkipString(info.isById, info.definitionId,
                            info.definitionClass, null);
                    fails += "Unused disallowed skip: "+skipStr+"\n";
                }
            }else{ // !info.isDisallowAllProps
                int len = info.disallowedPropNames.length;
                for (int i = 0; i < len; i++) {
                    boolean used = info.disallowedPropSkipUsed[i];
                    if( ! used ){
                        String propName = info.disallowedPropNames[i];
                        String skipStr = toSkipString(info.isById, info.definitionId,
                                info.definitionClass, propName);
                        fails += "Unused disallowed skip: "+skipStr+"\n";
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkips(), this, "testNoRunTimeBindings"));
        
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getPropNamesAlwaysDisallow() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected String[] getPropNameSuffixesAlwaysDisallow() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected Object[][] getDisallowedBindingPropList() {
        return new Object[0][];
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }

    private String toSkipString(Class<?> definitionClass, String propName) {
        return toSkipString(false, null, definitionClass, propName);
    }
    private String toSkipString(boolean isById, String definitionId,
            Class<?> definitionClass, String propName) {
        StringBuilder skipAsStr = new StringBuilder();
        skipAsStr.append("new Object[]{");
        if( isById ){
            skipAsStr.append('"');
            skipAsStr.append(definitionId);
            skipAsStr.append('"');
        }else{
            skipAsStr.append(XspTestUtil.getShortClass(definitionClass));
            skipAsStr.append(".class");
        }
        if( null != propName ){
            skipAsStr.append(", new String[]{\"");
            skipAsStr.append(propName);
            skipAsStr.append("\"}},");
        }else{
            skipAsStr.append("},");
        }
        String skipStr = skipAsStr.toString();
        return skipStr;
    }
    private List<DefinitionDisallowInfo> parseDisallowInfo(Object[][] disallowedArray){
        List<DefinitionDisallowInfo> infos = new ArrayList<DefinitionDisallowInfo>(disallowedArray.length);
        Set<Object> definitionIdSet = new HashSet<Object>();
        for (Object[] classToPropNames : disallowedArray) {
            Object definitionIdObj = classToPropNames[0];
            
            boolean isById = definitionIdObj instanceof String;
            Class<?> definitionClass;
            String definitionId;
            if( isById ){
                definitionClass = null;
                definitionId = (String) definitionIdObj;
            }else{
                definitionClass = (Class<?>) definitionIdObj;
                definitionId = null;
            }
            
            boolean isDisallowAllProps = (classToPropNames.length == 1);
            String[] disallowedPropNames;
            if( ! isDisallowAllProps ){
                disallowedPropNames = (String[]) classToPropNames[1];
            }else{
                disallowedPropNames = null;
            }
            if( definitionIdSet.contains(definitionIdObj) ){
                DefinitionDisallowInfo existingInfo = null;
                for (DefinitionDisallowInfo info : infos) {
                    if( match(info, isById, definitionId, definitionClass) ){
                        existingInfo = info;
                        break;
                    }
                }
                if( null == existingInfo ){
                    throw new RuntimeException("Bad junit test state");
                }
                if( isDisallowAllProps || existingInfo.isDisallowAllProps ){
                    throw new RuntimeException("Cannot merge incompatible skips for: "+definitionIdObj);
                }
                existingInfo.addPropNames(disallowedPropNames);
            }else{ // ! existingInfo
                infos.add(new DefinitionDisallowInfo(isById, definitionClass, definitionId, isDisallowAllProps, disallowedPropNames));
                definitionIdSet.add(definitionIdObj);
            }
        }
        return infos;
    }
    /**
     * @param info
     * @param isById
     * @param definitionId
     * @param definitionClass
     * @return
     */
    private boolean match(DefinitionDisallowInfo info, boolean isById,
            String definitionId, Class<?> definitionClass) {
        if( info.isById != isById ){
            return false;
        }
        if( !StringUtil.equals(info.definitionId, definitionId) ){
            return false;
        }
        if( !StringUtil.equals(info.definitionClass, definitionClass) ){
            return false;
        }
        return true;
    }
    private boolean isMarkSkipped(List<DefinitionDisallowInfo> disallowedInfos, int[] indexHint, FacesDefinition def, String propName){
        
        boolean recentlyFound = false;
        if( -1 == indexHint[0] ){
            int len = disallowedInfos.size();
            for (int i = 0; i < len; i++) {
                DefinitionDisallowInfo info = disallowedInfos.get(i);
                if( match(info, def) ){
                    indexHint[0] = i;
                    recentlyFound = true;
                    break;
                }
            }
        }
        if( -1 == indexHint[0] ){
            return false;
        }
        DefinitionDisallowInfo indexedInfo = disallowedInfos.get(indexHint[0]);
        if( !match(indexedInfo, def) ){
            throw new RuntimeException("JUnit test in bad state, didn't call clearIndexHint before move to next definition");
        }
        if( indexedInfo.isDisallowAllProps ){
            if( recentlyFound ){
                indexedInfo.disallowAllSkipUsed = true;
            }
            return true;
        }
        
        int propIndex = XspTestUtil.indexOf(indexedInfo.disallowedPropNames, propName);
        if( -1 == propIndex ){
            return false;
        }
        indexedInfo.disallowedPropSkipUsed[propIndex] = true;
        return true;
    }
    private boolean match(DefinitionDisallowInfo info, FacesDefinition def){
        if( info.isById ){
            return info.definitionId.equals(def.getId());
        }
        return info.definitionClass.equals(def.getJavaClass());
    }
    private void clearIndexHint(int[] indexHint){
        indexHint[0] = -1;
    }
    private static class DefinitionDisallowInfo{
        boolean isById;
        Class<?> definitionClass;
        String definitionId;
        boolean isDisallowAllProps;
        boolean disallowAllSkipUsed = false;
        String[] disallowedPropNames;
        boolean[] disallowedPropSkipUsed;
        public DefinitionDisallowInfo(boolean isById, Class<?> definitionClass,
                String definitionId, boolean isDisallowAllProps,
                String[] disallowedPropNames) {
            super();
            this.isById = isById;
            this.definitionClass = definitionClass;
            this.definitionId = definitionId;
            this.isDisallowAllProps = isDisallowAllProps;
            this.disallowedPropNames = disallowedPropNames;
            this.disallowedPropSkipUsed = isDisallowAllProps? null : new boolean[disallowedPropNames.length];
        }
        public void addPropNames(String[] moreDisallowedPropNames) {
            disallowedPropNames = XspTestUtil.concat(disallowedPropNames, moreDisallowedPropNames);
            disallowedPropSkipUsed = new boolean[disallowedPropNames.length];
        }
    }
}
