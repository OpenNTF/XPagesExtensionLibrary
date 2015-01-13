/*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 19 May 2011
* GroupReuseTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.PropertiesHaveCategoriesTest;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class GroupReuseTest extends AbstractXspTest {

	@Override
	public String getDescription() {
        // Note, see the Console view System.err text for the proposed <group>s 
        // that might be used in the place of each <property> with JUnit test fail. 
		return "that controls should not declare <property>s where there are existing <group>s containing those <property>s";
	}
	public void testGroupReuse() throws Exception {
		
		FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this,
		        new PropertyTagsAnnotater(), new PropertyReuseFamilyAnnotater(),
		        new PropertiesHaveCategoriesTest.PropertyCategoryAnnotater(), 
		        new SpellCheckTest.DescriptionDisplayNameAnnotater(),
		        new DefinitionTagsAnnotater());
		// The relevant xsp-config skips are:
		// <designer-extension>
		//  <reuse-family>(propName)-(logical group)</reuse-family>
		// e.g.:
		//  <reuse-family>title-accessibility</reuse-family>
		//  <reuse-family>title-columnHeader</reuse-family>
		// and
		//  <reuse-changes>(changes)</reuse-changes>
		// used with <reuse-family> where the property is the same as the definitive
		// property declaration, except that it differs somewhat
		
		Map<String, List<FacesGroupDefinition>> propNameToDefiningGroups = new HashMap<String, List<FacesGroupDefinition>>();
		for (FacesDefinition def : reg.findDefs()) {
			if( def instanceof FacesGroupDefinition ){
				FacesGroupDefinition group = (FacesGroupDefinition)def;
				for (String propName : group.getPropertyNames()) {
					List<FacesGroupDefinition> groupList = propNameToDefiningGroups.get(propName);
					if( null == groupList ){
						groupList = new ArrayList<FacesGroupDefinition>();
						propNameToDefiningGroups.put(propName, groupList);
					}
					groupList.add(group);
				}
			}
		}
		addGroupUsageExtension(reg);
		Map<String, List<FacesDefinition>> propNameToDefiningDefs = new HashMap<String, List<FacesDefinition>>();
		for (FacesDefinition def : reg.findDefs()) {
			if( def instanceof FacesGroupDefinition ){
				continue;
			}
			for (String inlinePropName : def.getDefinedInlinePropertyNames()) {
				List<FacesDefinition> defList = propNameToDefiningDefs.get(inlinePropName);
				if( null == defList ){
					defList = new ArrayList<FacesDefinition>();
					propNameToDefiningDefs.put(inlinePropName, defList);
				}
				defList.add(def);
			}
		}
		
		Map<String,SuggestedGroups> propNameToSuggestedGroups = getHardCodedSuggestedGroups();
		
		String[] reuseFamilyIds = getLibReuseFamilys(reg, this);
		Map<String,DefinitiveDeclaration> reuseFamilyDeclarations = findDefinitiveDeclarations(reg, reuseFamilyIds);
		
		String fails = "";
		boolean isCheckOtherDefs = isCheckOtherDefs();
		boolean isCheckOtherGroups = isCheckOtherGroups();
		// for the definitions in the library.
		for (FacesDefinition def : TestProject.getDefinitions(reg, this)) {
			boolean isControl = def instanceof FacesComponentDefinition;
			boolean isComplex = def instanceof FacesComplexDefinition;
			boolean isGroup = def instanceof FacesGroupDefinition;
			String defUsage = isControl? "control" : isComplex? "complex" : "other";
			for (String inlinePropName : def.getDefinedInlinePropertyNames()) {
			    
			    FacesProperty prop = def.getProperty(inlinePropName);
			    String reuseFamily = getReuseFamily(prop);
			    if( null != reuseFamily ){
			        
			        DefinitiveDeclaration original = reuseFamilyDeclarations.get(reuseFamily);
			        if( def == original.def && prop == original.prop ){
			            // this is the first declaration of a reuse family
			            continue;
			        }
			        
			        String allChanges = getChanges(prop, original);
			        
			        String expectedReuseChanges = getReuseChanges(prop);
			        if( null == expectedReuseChanges ){
			            expectedReuseChanges = "";
			        }
			        if( ! StringUtil.equals(expectedReuseChanges, allChanges) ){
                        fails += def.getFile().getFilePath() + " "
                                + ParseUtil.getTagRef(def) + " " + inlinePropName+
                                " (reuse-family=" +reuseFamily+
                                ") differs from the original by: " +allChanges+
                                " [expected reuse-changes: " +expectedReuseChanges+
                                "] [reuse-family original is in: " +ParseUtil.getTagRef(original.def)+"]\n";
                    }
                    // Already has a reuse-family so shouldn't suggest reuse.
			        continue;
			    }
			    
			    
			    SuggestedGroups hardCodedSuggestion = propNameToSuggestedGroups.get(inlinePropName);
			    String[] suggestedGroupIds = (null == hardCodedSuggestion)? null : 
			          isControl? hardCodedSuggestion.suggestedComponentGroups
			                  : isComplex? hardCodedSuggestion.suggestedComplexGroups : null;
                if( null != suggestedGroupIds ){
                    String groups = XspTestUtil.concatStrings(suggestedGroupIds);
                    
                    String msg = def.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(def) + " " + inlinePropName
                            + " Should reuse <group-type-ref> for an existing "
                            + defUsage + " group: " + groups + "";
                    System.err.println(msg + "\n existing " +defUsage+ " groups: " +groups);
                    fails += msg + "\n";
                    continue;
                }
			    
				String groups = isGroup? null : toGroupListString(propNameToDefiningGroups.get(inlinePropName), defUsage);
				if( isCheckOtherGroups && null != groups ){
					String msg = def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "+inlinePropName
						+" Maybe should reuse <group-type-ref> of an existing " +defUsage+ " group.";
					System.err.println(msg + "\n existing " +defUsage+ " groups: " +groups);
                    fails += msg;
                    fails += " Proposed " +defUsage+ " groups for reuse: " + groups + "\n";
					continue;
				}
				if( isCheckOtherDefs && !isGroup){
					List<FacesDefinition> defList = propNameToDefiningDefs.get(inlinePropName);
					String defs = toDefListString(defList, def, isControl);
					if( null != defs ){
						if( isControl ){
							String msg = def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+ " "+ inlinePropName
								+ " Declared <property> also present in other <component>s, "
								+ "consider refactor a <group>.";
							fails += msg + "\n";
							System.err.println(msg + "\n other components: "+ defs);
						} else if( isComplex ){
							String msg = def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+ " "+ inlinePropName
								+ " Declared <property> also present in other <complex>s, "
								+ "consider refactor a <group>.";
							fails += msg + "\n";
							System.err.println(msg + "\n other complexes: "+ defs);
						}
						continue;
					}
				}
				// else, unique property, ok to declare inline
			}
		}
		fails = XspTestUtil.removeMultilineFailSkips(fails,
		        SkipFileContent.concatSkips(getSkipFails(), this, "testGroupReuse"));
		
		if( fails.length() > 0 ){
			fail(XspTestUtil.getMultilineFailMessage(fails));
		}
	}
    /**
     * @param prop
     * @param original
     * @return
     */
    protected String getChanges(FacesProperty prop,
            DefinitiveDeclaration original) {
        String functionalChanges = KnownPropertyRedefinitionTest.getChanges(prop, original.prop);
        if( "none".equals(functionalChanges) ){
            functionalChanges = "";
        }
        String categoryChanges = getCategoryChanges(prop, original.prop);
        String descrNameChanges = getDescrNameChanges(prop, original.prop);
        
        String allChanges = functionalChanges;
        if( categoryChanges.length() > 0 ){
            allChanges += KnownPropertyRedefinitionTest.comma(allChanges)+categoryChanges;
        }
        if( descrNameChanges.length() > 0 ){
            allChanges += KnownPropertyRedefinitionTest.comma(allChanges)+descrNameChanges;
        }
        return allChanges;
    }
	/**
     * @param prop
     * @param prop2
     * @return
     */
    private String getCategoryChanges(FacesProperty prop, FacesProperty originalProp) {
        String changes = "";

        String propName = (String) prop.getExtension("category");
        String originalPropName = (String) originalProp.getExtension("category");
        if( !StringUtil.equals(propName, originalPropName) ){
            changes += KnownPropertyRedefinitionTest.comma(changes)+"category="+propName;
        }
        return changes;
    }
    /**
     * @param prop
     * @param prop2
     * @return
     */
    private String getDescrNameChanges(FacesProperty prop, FacesProperty originalProp) {
        String changes = "";

        String propName = (String) prop.getExtension("display-name");
        String originalPropName = (String) originalProp.getExtension("display-name");
        if( !StringUtil.equals(propName, originalPropName) ){
            changes += "display-name";
        }
        String propDescr = (String) prop.getExtension("description");
        String originalPropDescr = (String) originalProp.getExtension("description");
        if( ! StringUtil.equals(propDescr, originalPropDescr) ){
            changes += KnownPropertyRedefinitionTest.comma(changes)+"description";
        }
        return changes;
    }
    /**
     * @param reg
     * @param relevantReuseFamilyIds
     * @return
     */
    private Map<String, DefinitiveDeclaration> findDefinitiveDeclarations(
            FacesSharableRegistry reg, String[] relevantReuseFamilyIds) {
        if( relevantReuseFamilyIds.length == 0){
            return Collections.emptyMap();
        }
        int notFoundCount = relevantReuseFamilyIds.length;
        
        Map<String, DefinitiveDeclaration> map = new HashMap<String, GroupReuseTest.DefinitiveDeclaration>();
        
        outer:
        for (FacesProject proj : reg.getProjectList()) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                for (FacesDefinition def : file.getDefs()) {
                    for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedInlinePropertyNames()) ) {
                        String reuseFamily = getReuseFamily(prop);
                        if( null == reuseFamily ){
                            continue;
                        }
                        int index = Arrays.binarySearch(relevantReuseFamilyIds, reuseFamily);
                        if( index >= 0 && ! map.containsKey(reuseFamily) ){
                            
                            map.put(reuseFamily, new DefinitiveDeclaration(
//                                    reuseFamily, 
                                    def, prop));
                            notFoundCount--;
                            if( 0 == notFoundCount ){
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
    /**
     * @param reg
     * @param groupReuseTest
     * @return
     */
    private String[] getLibReuseFamilys(FacesSharableRegistry reg,
            AbstractXspTest test) {
        
        List<String> list = null;
        
        List<FacesDefinition> defs = TestProject.getLibDefinitions(reg, test);
        // want to find the 1st definition, instead of usually the last defined
        Collections.reverse(defs);
        for (FacesDefinition def : defs) {
            
            for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedInlinePropertyNames())) {
                String reuseFamily = getReuseFamily(prop);
                if( null != reuseFamily ){
                    if( null == list ){
                        list = new ArrayList<String>();
                        list.add(reuseFamily);
                    }else{
                        if(  !list.contains(reuseFamily) ){
                            list.add(reuseFamily);
                        }
                    }
                }
            }
        }
        if( null == list ){
            return StringUtil.EMPTY_STRING_ARRAY;
        }
        Collections.sort(list);
        return StringUtil.toStringArray(list);
    }
    protected String[] getSkipFails(){
		return StringUtil.EMPTY_STRING_ARRAY;
	}
    protected Map<String,SuggestedGroups> getHardCodedSuggestedGroups(){
        return new HashMap<String, GroupReuseTest.SuggestedGroups>();
    }
    protected void addAll(Map<String, SuggestedGroups> existingSuggestions, SuggestedGroups[] extra){
        for (SuggestedGroups suggestion : extra) {
            
            SuggestedGroups existing = existingSuggestions.get(suggestion.forPropertyName);
            if( null != existing ){
                // merge suggestion with extra
                suggestion = new SuggestedGroups(suggestion.forPropertyName,
                        nonEmpty(XspTestUtil.concat(existing.suggestedComponentGroups, suggestion.suggestedComponentGroups)),
                        nonEmpty(XspTestUtil.concat(existing.suggestedComplexGroups, suggestion.suggestedComplexGroups)));
            }
            existingSuggestions.put(suggestion.forPropertyName, suggestion);
        }
    }
    private String[] nonEmpty(String[] arr){
       // convert String[0] to null
       if( null != arr && arr.length == 0){
           return null;
       }
       return arr;
    }
    protected SuggestedGroups controlGroups(String forPropertyName, String... suggestedComponentGroups){
        return new SuggestedGroups(forPropertyName, 
                /*suggestedComponentGroups*/suggestedComponentGroups, 
                /*suggestedComplexGroups*/null);
    }
    protected static class SuggestedGroups{
        public final String forPropertyName;
        
        public final String[] suggestedComponentGroups;
        public final String[] suggestedComplexGroups;
        /**
         * @param forPropertyName
         * @param preventAnySuggestion
         * @param useSuggestedComponentGroups
         * @param suggestedComponentGroups
         * @param useSuggestedComplexGroups
         * @param suggestedComplexGroups
         */
        public SuggestedGroups(String forPropertyName,
                String[] suggestedComponentGroups,
                String[] suggestedComplexGroups) {
            super();
            this.forPropertyName = forPropertyName;
            this.suggestedComponentGroups = suggestedComponentGroups;
            this.suggestedComplexGroups = suggestedComplexGroups;
        }
	}
	/**
	 * Available to override in subclasses, defaults to true.
	 * @return
	 */
	protected boolean isCheckOtherDefs() {
		return true;
	}
    /**
     * Available to override in subclasses, defaults to true.
     * @return
     */
    protected boolean isCheckOtherGroups() {
        return true;
    }
	private void addGroupUsageExtension(FacesSharableRegistry reg) {
		for (FacesDefinition def : reg.findDefs()) {
			String usage;
			if( def instanceof FacesComponentDefinition){
				usage = "control";
			}else if( def instanceof FacesComplexDefinition ){
				usage = "complex";
			}else{
				continue;
			}
			for (String groupTypeRef : def.getGroupTypeRefs()) {
				addGroupUsageExtension(reg, groupTypeRef, usage);
			}
		}
	}
	private void addGroupUsageExtension(FacesSharableRegistry reg,
			String groupTypeRef, String usage) {
		FacesGroupDefinition group = (FacesGroupDefinition) reg.findDef(groupTypeRef);
		if( null == group ){
			return;
		}
		String existingUsage = (String) group.getExtension("usage");
		if( null != existingUsage ){
			return;
		}
		group.setExtension("usage", usage);
		for (String innerGroupTypeRef : group.getGroupTypeRefs()) {
			addGroupUsageExtension(reg, innerGroupTypeRef, usage);
		}
	}
	private String toDefListString(List<? extends FacesDefinition> list, FacesDefinition except, boolean isControl) {
		StringBuilder result = new StringBuilder();
		for (FacesDefinition def : list) {
			if( def == except ){
				continue;
			}
			if( isControl ){
				if( !(def instanceof FacesComponentDefinition) ){
					continue;
				}
			}else{
				if( !(def instanceof FacesComplexDefinition) ){
					continue;
				}
			}
			result.append(ParseUtil.getTagRef(def)).append(' ');
		}
		if( result.length() == 0 ){
			return null;
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
	private String toGroupListString(List<FacesGroupDefinition> list, String matchUsage) {
		if( null == list ){
			return null;
		}
		StringBuilder result = new StringBuilder();
		for (FacesGroupDefinition group : list) {
			if( !matchUsage.equals(group.getExtension("usage")) ){
				continue;
			}
			result.append(ParseUtil.getTagRef(group)).append(' ');
		}
		if( result.length() == 0 ){
			return null;
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
	private class PropertyReuseFamilyAnnotater extends DesignerExtensionSubsetAnnotater{
	    @Override
	    protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
	        return parsed instanceof FacesProperty;
	    }
	    @Override
	    protected String[] createExtNameArr() {
	        return new String[]{
                    "reuse-family",
	                "reuse-changes",
	        };
	    }
	    @Override
	    protected Object parseValue(String extensionName, String value) {
//	        if( "reuse-family".equals(extensionName) || "reuse-family-decl".equals(extensionName) || ... ){
	            return value;
//	        }
//	        return value;
	    }
	}

    public static String getReuseFamily(FacesProperty prop){
        return (String) prop.getExtension("reuse-family");
    }
    public static String getReuseChanges(FacesProperty prop){
        return (String) prop.getExtension("reuse-changes");
    }
    private static class DefinitiveDeclaration{
//        public String reuseFamily;
        public FacesDefinition def;
        public FacesProperty prop;
        public DefinitiveDeclaration(
//                String reuseFamily, 
                FacesDefinition def,
                FacesProperty prop) {
            super();
//            this.reuseFamily = reuseFamily;
            this.def = def;
            this.prop = prop;
        }
    }
}
