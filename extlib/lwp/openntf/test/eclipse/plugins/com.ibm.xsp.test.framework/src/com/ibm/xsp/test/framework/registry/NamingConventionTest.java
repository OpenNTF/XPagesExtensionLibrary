/*
 * © Copyright IBM Corp. 2011, 2013
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
* Date: 31 Mar 2011
* NamingConventionTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class NamingConventionTest extends AbstractXspTest {

    protected static int SEV_FATAL = 1;
    protected static int SEV_ERROR = 2;
    protected static int SEV_WARNING = 3;
    protected static int SEV_INFO = 4;
    /**
     * A low severity (high sev number) above the severity cutoff.
     */
    protected static int SEV_IGNORE = 11;
    /**
     * Not the severity of any rule, only used as a cutoff 
     * for {@link #getRuleSeverityLevelCutoff()};
     */
    protected static int SEV_ANYTHING = 10;
	/**
     *
     * @author Maire Kehoe (mkehoe@ie.ibm.com)
     */
    private final class NamingMetaDataAnnotater implements RegistryAnnotater {
        public void annotate(RegistryAnnotaterInfo info,
                FacesExtensibleNode node, Element elem) {
            if (node instanceof FacesLibraryFragment) {
                FacesLibraryFragment file = (FacesLibraryFragment) node;
                
                // <faces-config-extension>
                //   <designer-extension>
                //      <control-subpackage-name>dojo.form</control-subpackage-name>
                //   </designer-extension>
                // </faces-config-extension>
                String controlSubpackageName = null;
                outOfBothForLoops:
                for (Element extensionContainer : ElementUtil.getChildren(elem, "faces-config-extension")) {
                    for (Element extensionBlock : ElementUtil.getChildren(extensionContainer, "designer-extension")) {
                        controlSubpackageName = ElementUtil.extractValue(extensionBlock, "control-subpackage-name");
                        if( null != controlSubpackageName ){
                            break outOfBothForLoops;
                        }
                    }
                }
                if( null != controlSubpackageName ){
                    file.setExtension("control-subpackage-name", controlSubpackageName);
                }
            }
        }
    }
    private static String getControlSubpackageName(FacesLibraryFragment file){
        return (String) file.getExtension("control-subpackage-name");
    }
    // To mark a skip as used, increase the Object[] length to 4. 
	private static final int skipUsedLength = 4;

	@Override
	public String getDescription() {
		// note this only looks at the xsp-config contents, other tests 
		// will check that the actual control / complex-type constants/values 
		// match the xsp-config values.
		return "that the controls in the library match the naming conventions";
	}
	private String[] _expectedPrefixes = new String[]{
			// [0] package-name prefix
			null, //"com.ibm.xsp",
			// [1] abstract component package-name suffix: usually ".component"
			".component",
			// [2] tag component package-name suffix: usually ".component" or "component.xp."
			// can end with ".+" or ".+", like ".component.+"  
			".component.xp",
			// [3] abstract component short java-class prefix: usually "UI"
			"UI",
			// [4] tag component short java-class prefix: usually "Xsp"
			"Xsp",
			// [5] abstract component short java-class suffix: possibly "Ex" or "Ex2"
			"",
			// [6] tag component short java-class suffix: possibly "Ex" or "Ex2"
			"",
			// [7] abstract component-type short-name prefix: usually "UI"
			"UI",
			// [8] tag component-type short-name prefix: usually ""
			"",
	};
	private Pattern camelCaseHeadDown;
    private Pattern camelCaseHeadUp;
    private Map<String, Integer> ruleSeverityOverrides; 

//	@Override
//	protected String[][] getExtraConfig() {
//		String[][] extraConfig = super.getExtraConfig();
//		extraConfig = XspTestUtil.concat(extraConfig, new String[][]{
//				// set these 2 configs to test all libs.
//				{"target.library", null},
//				{"target.all","true"},
//		});
//		return extraConfig;
//	}
	
	public void testNamingConventions() throws Exception {
		// TODO Naming conventions for Complex-types
		FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new NamingMetaDataAnnotater());
		String[] expectedPrefixes = getExpectedPrefixes();
		String expectedPackagePrefix = expectedPrefixes[0];
		if( null == expectedPackagePrefix ){
			expectedPackagePrefix = ConfigUtil.getNamingConventionPackagePrefix(this);
			if( StringUtil.isEmpty(expectedPackagePrefix) ){
				throw new RuntimeException("Prefix not found in config.properties, " 
						+"like: NamingConvention.package.prefix=com.example.foo");
			}
		}
		String expectedAbstractComponentPackageSuffix = expectedPrefixes[1];
		String expectedTagComponentPackageSuffix = expectedPrefixes[2];
		String expectedAbstractComponentShortClassPrefix = expectedPrefixes[3];
		String expectedTagComponentShortClassPrefix = expectedPrefixes[4];
		String expectedAbstractComponentShortClassSuffix = expectedPrefixes[5];
		String expectedTagComponentShortClassSuffix = expectedPrefixes[6];
		String expectedAbstractComponentShortTypePrefix = expectedPrefixes[7];
		String expectedTagComponentShortTypePrefix = expectedPrefixes[8];
		
		List<Object[]> ruleSeverityOverridesArr = getRuleSeverityOverrides();
		ruleSeverityOverrides = toRuleSeverityOverrideMap(ruleSeverityOverridesArr);
		
		List<Object[]> unusualTagness = getUnusualTagnessSkips();
		List<Object[]> nonObviousTagName = getNonObviousTagNameSkips();
		
		List<String> badRendererShortNamePrefixes = getBadRendererShortNamePrefixes();
		List<ComplexTypeExpectedNaming> complexTypeExpectedNamings = parse(reg, getComplexTypeExpectedNamings());
		List<ComplexTypeExpectedNaming> detectedComplexTypeBases = detectOtherComplexTypeBases(reg, TestProject.getLibComplexDefs(reg, this), complexTypeExpectedNamings);
		
		int severityCutoff = getRuleSeverityLevelCutoff();
		
		String fails = "";
		boolean isRequireControlSubpackageName = isRequireControlSubpackageName();
		Set<String> filePathsCheckedForControlSubPackage = isRequireControlSubpackageName? new HashSet<String>() : null;
		List<FacesDefinition> defs = TestProject.getLibDefinitions(reg, this);
		defs = filterDefinitions(defs);
        for (FacesDefinition someDefType : defs ) {
            if( someDefType instanceof FacesComponentDefinition ){
                FacesComponentDefinition def = (FacesComponentDefinition) someDefType;
			
			String packageName = def.getJavaClass().getPackage().getName();
			boolean isUseTagNaming = def.isTag();
			Object[] tagnessSkip = findUnusualTagnessSkip(unusualTagness, def.getJavaClass());
			if( null != tagnessSkip ){
				if( !(isUseTagNaming == ((Boolean)tagnessSkip[1]).booleanValue() ) ){
					fails += path(def)+" Bad unusualTagness skip: isTag(" +isUseTagNaming+") != expectedIsTag(" +tagnessSkip[1]+")\n";
				}
				isUseTagNaming = (Boolean)tagnessSkip[2];
			}
			
            String expectedControlSubpackageName = getControlSubpackageName(def.getFile());
            boolean isControlSubpackageInConfig = null != expectedControlSubpackageName;
            if( isRequireControlSubpackageName && !isControlSubpackageInConfig){
                String filePath = def.getFile().getFilePath();
                if( ! filePathsCheckedForControlSubPackage.contains(filePath) ){
                    filePathsCheckedForControlSubPackage.add(filePath);
                    
                    String[] parsedClass = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, def.getJavaClass().getName());
                    String inferredSubpackage = parsedClass[1];
                    if( inferredSubpackage.equals("component") ){
                        inferredSubpackage = "";
                    }else if( inferredSubpackage.startsWith("component.") ){
                        inferredSubpackage = inferredSubpackage.substring("component.".length());
                    }
                    expectedControlSubpackageName = inferredSubpackage;
                    def.getFile().setExtension("control-subpackage-name", inferredSubpackage);
                    
                    fails += def.getFile().getFilePath()
                            + " Missing required <control-subpackage-name> "
                            + "in config file, inferring subpackage [" +inferredSubpackage+ "]\n";
                }
            }
            String expectedComponentPackageSuffix = isUseTagNaming? expectedTagComponentPackageSuffix : expectedAbstractComponentPackageSuffix;
            
			if( ! packageName.startsWith(expectedPackagePrefix) ){
                if( severityCutoff >= sev("1", SEV_ERROR) ){
                    fails += path(def)+"[Rule1] Bad package name "+def.getJavaClass().getName()+" does not begin with "+expectedPackagePrefix+"\n";
                }
			}else{
                String actualSuffix = packageName.substring(expectedPackagePrefix.length());
                
                if( isControlSubpackageInConfig ){
                    boolean isSuffixAllowFuzzyMatch = expectedComponentPackageSuffix.endsWith(".*") || expectedComponentPackageSuffix.endsWith(".+");
                    if (isSuffixAllowFuzzyMatch) {
                        // remove the fuzzy suffix, as this config file gives a control-subpackage-name
                        // remove trailing ".+" or ".*"
                        expectedComponentPackageSuffix = expectedComponentPackageSuffix.substring(0, expectedComponentPackageSuffix.length()-2);
                    }
                    expectedComponentPackageSuffix = expectedComponentPackageSuffix+"."+expectedControlSubpackageName;
                    if( !expectedComponentPackageSuffix.equals(actualSuffix) ){
                        String expectedPackageName = expectedPackagePrefix + expectedComponentPackageSuffix;
                        if( severityCutoff >= sev("2a", SEV_WARNING) ){
                            fails += path(def)+"[Rule2a] Bad component-class package name "+def.getJavaClass().getName()+" does not match "+expectedPackageName+"\n";
                        }
                    }
                }else{
                    if( !isFuzzyMatch(expectedComponentPackageSuffix, actualSuffix) ){
                        String expectedPackageName = expectedPackagePrefix + expectedComponentPackageSuffix;
                        if( severityCutoff >= sev("2c", SEV_WARNING) ){
                            fails += path(def)+"[Rule2c] Bad component-class package name "+def.getJavaClass().getName()+" not in the form "+expectedPackageName+"\n";
                        }
                    }
                }
			}
			
			String shortClassName = def.getJavaClass().getSimpleName();
			String expectedShortClassPrefix = isUseTagNaming? expectedTagComponentShortClassPrefix : expectedAbstractComponentShortClassPrefix;
			if( ! shortClassName.startsWith(expectedShortClassPrefix) ){
                if( severityCutoff >= sev("3", SEV_ERROR) ){
                    fails += path(def)+"[Rule3] Bad component-class short name "+shortClassName+" does not begin with "+expectedShortClassPrefix+"\n";
                }
			}
			String expectedShortClassSuffix = isUseTagNaming? expectedTagComponentShortClassSuffix : expectedAbstractComponentShortClassSuffix;
			if( ! shortClassName.endsWith(expectedShortClassSuffix) ){
                if( severityCutoff >= sev("4", SEV_WARNING) ){
                    fails += path(def)+"[Rule4] Bad component-class short name "+shortClassName+" does not end with "+expectedShortClassSuffix+"\n";
                }
			}
			
			String shortNameFromClass = shortClassName;
			if( expectedShortClassPrefix.length() > 0 && shortNameFromClass.startsWith(expectedShortClassPrefix) ){
				shortNameFromClass = shortNameFromClass.substring(expectedShortClassPrefix.length());
			}
			if( expectedShortClassSuffix.length() > 0 && shortNameFromClass.endsWith(expectedShortClassSuffix) ){
				int end = shortNameFromClass.length() - expectedShortClassSuffix.length();
				shortNameFromClass = shortNameFromClass.substring(0, end);
			}
			
			String componentType = def.getComponentType();
            String[] componentTypeParsed = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, componentType);
            String componentTypePrefixPackage = componentTypeParsed[0];
            String componentTypeSubpackage = componentTypeParsed[1];
            String componentTypeShortName = componentTypeParsed[2];
			
            if( ! StringUtil.equals(componentTypePrefixPackage, expectedPackagePrefix) ){
                if( severityCutoff >= sev("5a", SEV_ERROR) ){
                    fails += path(def) + "[Rule5a] Bad component-type " + componentType + " does not begin with common-prefix " + expectedPackagePrefix + ", has prefix-package \"" + componentTypePrefixPackage + "\"\n";
                }
            }
            
            if( !StringUtil.isEmpty(expectedComponentPackageSuffix) ){
                if( componentTypeSubpackage.startsWith(expectedComponentPackageSuffix+".") ){
                    // component-type contains .component.
                    if( severityCutoff >= sev("6a", SEV_WARNING) ){
                        fails += path(def)+"[Rule6a] Bad component-type "+componentType+" should not have the class-only subpackage "+("."+expectedComponentPackageSuffix+".")+"\n";
                    }
                }
            }
            
            // expectedControlSubpackageName is "xp" or "dojo.form"
            if( !StringUtil.equals(componentTypeSubpackage, expectedControlSubpackageName) ){
                // fail.
                if( StringUtil.isEmpty(expectedControlSubpackageName) ){
                    // expect no subpackage
                    if( severityCutoff >= sev("6b", SEV_WARNING) ){
                        fails += path(def) + "[Rule6b] Bad component-type " + componentType + " has unexpected subpackage " + componentTypeSubpackage + "\n";
                    }
                }else if( componentTypeSubpackage.startsWith(expectedControlSubpackageName+".") ){
                    // starts with xp.something
                    String extraSubpackage = componentTypeSubpackage
                            .substring(expectedControlSubpackageName
                                    .length() + 1);
                    if( severityCutoff >= sev("6e", SEV_WARNING) ){
                        fails += path(def) + "[Rule6e] Bad component-type " + componentType + " has extra subpackage " + extraSubpackage + "\n";
                    }
                }else{ 
                    // starts with foo, not xp
                    if( severityCutoff >= sev("6d", SEV_ERROR) ){
                        fails += path(def) + "[Rule6d] Bad component-type " + componentType + ", expect subpackage [" + expectedControlSubpackageName + "], was [" + componentTypeSubpackage + "]\n";
                    }
                }
            }
            
            // expectedTagComponentShortTypePrefix be UI or "" (is "" for the XPages runtime)
            String expectedShortTypePrefix = isUseTagNaming? expectedTagComponentShortTypePrefix : expectedAbstractComponentShortTypePrefix;
            if( !StringUtil.isEmpty(expectedShortTypePrefix) ){
                if( !componentTypeShortName.startsWith(expectedShortTypePrefix) ){
                    if( severityCutoff >= sev("6f", SEV_WARNING) ){
                        fails += path(def)+"[Rule6f] Bad component-type "+componentType+" missing short type prefix \""+expectedShortTypePrefix+"\"\n";
                    }
                }
            }
            if( !StringUtil.isEmpty(componentTypeShortName) && !Character.isUpperCase(componentTypeShortName.charAt(0)) ){
                if( severityCutoff >= sev("6h", SEV_ERROR) ){
                    fails += path(def)+"[Rule6h] Bad component-type "+componentType+" short name not Capitalized "+componentTypeShortName+"\n";
                }
            }
            
            if (!StringUtil.isEmpty(expectedShortClassPrefix)
                    && !StringUtil.equals(expectedShortClassPrefix,
                            expectedShortTypePrefix)) {
                if( componentTypeShortName.startsWith(expectedShortClassPrefix) ){
                    if( severityCutoff >= sev("7a", SEV_ERROR) ){
                        fails += path(def) + "[Rule7a] component-type short name "
                        + "should not have class short name prefix "
                        + expectedShortClassPrefix + " : "
                        + "component-type " + componentType + "["
                        + componentTypeShortName + "]\n";
                    }
                }
            }
            String shortNameFromTypeWithoutPrefix = componentTypeShortName;
            if( shortNameFromTypeWithoutPrefix.startsWith(expectedShortTypePrefix) ){
                shortNameFromTypeWithoutPrefix = shortNameFromTypeWithoutPrefix.substring(expectedShortTypePrefix.length());
            }
            if( shortNameFromTypeWithoutPrefix.startsWith(expectedShortClassPrefix) ){
                shortNameFromTypeWithoutPrefix = shortNameFromTypeWithoutPrefix.substring(expectedShortClassPrefix.length());
            }
			if( !shortNameFromClass.equals(shortNameFromTypeWithoutPrefix) ){
                if( severityCutoff >= sev("7b", SEV_WARNING) ){
                    fails += path(def)+"[Rule7b] Class and type short names do not match: " 
                        +"component-class " +def.getJavaClass().getName()
                        +"[" +shortNameFromClass+"] "
                        +"!= " 
                        +"component-type "+componentType
                        +"[" +shortNameFromTypeWithoutPrefix+"]\n";
                }
			}
			
			String tagName = def.isTag()? def.getTagName() : null;
			String shortNameFromTagName = null;
			if( null != tagName ){
				Pattern regExp = compile("[a-z][a-zA-Z]*");
				if( ! regExp.matcher(tagName).matches() ){
                    if( severityCutoff >= sev("8", SEV_ERROR) ){
                        fails += path(def)+"[Rule8] Bad tag-name "+tagName+", expected camelCase, does not match "+regExp.pattern()+"\n";
                    }
				}
				
				shortNameFromTagName = Character.toUpperCase(tagName.charAt(0))+tagName.substring(1);
				
				boolean expectShortClassMatchTagName = true;
				Object[] tagNameSkip = findNonObviousTagNameSkip(nonObviousTagName, def.getJavaClass());
				if( null != tagNameSkip ){
					String expectedTagName = (String) tagNameSkip[1];
					if( !expectedTagName.equals(tagName) ){
						fails += path(def)+" Bad nonObviousTagName skip: "
								+ "actualTagName("+tagName+") "
								+ "!= expectedTagName(" +expectedTagName+")\n";
					}
					if( shortNameFromClass.equals(shortNameFromTagName) ){
						fails += path(def) + " Bad nonObviousTagName skip: "
								+ "expect unmatched class and tag-name short names, but actually match - "
								+ "component-class " + def.getJavaClass().getName() 
								+ "[" + shortNameFromClass + "], "
								+ "tag-name " + tagName 
								+ "[" + shortNameFromTagName + "]\n";
					}
				}
				if( null == tagNameSkip && !shortNameFromClass.equals(shortNameFromTagName) ){
					if( expectShortClassMatchTagName ){
                        if( severityCutoff >= sev("9", SEV_INFO) ){
                            fails += path(def)
                                    + "[Rule9] Class and tag-name short names do not match: "
                                    + "component-class "
                                    + def.getJavaClass().getName() + "["
                                    + shortNameFromClass + "] " + "!= "
                                    + "tag-name " + tagName + "["
                                    + shortNameFromTagName + "]\n";
                            }
					}
				}
			}// end ( null != tagName )
			
			String componentFamily = def.getComponentFamily();
			if( null != componentFamily && !isInheritedComponentFamily(def) ){
			    
	            String[] componentFamilyParsed = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, componentFamily);
	            String componentFamilyPrefixPackage = componentFamilyParsed[0];
//	            String componentFamilySubpackage = componentFamilyParsed[1];
//	            String componentFamilyShortName = componentFamilyParsed[2];
			    
	            
				String expectedFamilyPrefix = expectedPackagePrefix+".";
                if( ! StringUtil.equals(componentFamilyPrefixPackage, expectedPackagePrefix) ){
                    if( severityCutoff >= sev("10a", SEV_ERROR) ){
                        fails += path(def)+"[Rule10a] Bad component-family "+componentFamily+" does not have the common-prefix "+expectedFamilyPrefix+"\n";
                    }
                }
                if( ! componentFamily.equals(componentType) ){
                    if( severityCutoff >= sev("10b", SEV_WARNING) ){
                        fails += path(def)+"[Rule10b] Bad (non-inherited) component-family "+componentFamily+" does not match component-type (" +componentType+")\n";
                    }
                }
                
				String shortNameFromFamily = componentFamily.substring(componentFamily.lastIndexOf('.')+1);
//				String expectedShortNamePrefix = isUseTagNaming? expectedTagComponentShortTypePrefix : expectedAbstractComponentShortTypePrefix;
//				if( shortNameFromFamily.startsWith(expectedShortNamePrefix) ){
//					shortNameFromFamily = shortNameFromFamily.substring(shortNameFromFamily.length());
//				}
				if( !shortNameFromClass.equals(shortNameFromFamily) ){
                    if( severityCutoff >= sev("11", SEV_WARNING) ){
                        fails += path(def)+"[Rule11] Class and (non-inherited)family short names do not match: " 
                                +"component-family "+componentFamily
                                +"[" +shortNameFromFamily+"] " 
                                +"!= " 
                                +"component-class " +def.getJavaClass().getName()
                                +"[" +shortNameFromClass+"] "
                                +"\n";
                    }
				}
			}
			String rendererType = def.getRendererType();
			if( null != rendererType && !isInheritedRendererType(def) ){
			    String[] rendererTypeParsed = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, rendererType);
			    String rendererTypePrefixPackage = rendererTypeParsed[0];
			    String rendererTypeSubpackage = rendererTypeParsed[1];
                String rendererTypeShortName = rendererTypeParsed[2];
                if( ! StringUtil.equals(rendererTypePrefixPackage, expectedPackagePrefix) ){
                    if( severityCutoff >= sev("12a", SEV_ERROR) ){
                        fails += path(def)+"[Rule12a] Bad renderer-type "+rendererType+" does not have the common-prefix "+expectedPackagePrefix+"\n";
                    }
                }
			    
				// renderer-type short names must match the regular expression "[A-Z][a-zA-Z]*"
                Pattern regExp = compile("[A-Z][a-zA-Z]*");
                if( ! regExp.matcher(rendererTypeShortName).matches() ){
                    if( severityCutoff >= sev("12c", SEV_ERROR) ){
                        fails += path(def)+"[Rule12c] Bad renderer-type "+rendererType+", expected CamelCase short name, " +rendererTypeShortName+" does not match "+regExp.pattern()+"\n";
                    }
                }
                if( rendererTypeShortName.endsWith("Renderer") ){
                    if( severityCutoff >= sev("12d", SEV_ERROR) ){
                        fails += path(def)+"[Rule12d] Bad renderer-type "+rendererType+", should not end with Renderer\n";
                    }
                }
                for (String badPrefix : badRendererShortNamePrefixes) {
                    // prevent OneUI prefix.
                    if( rendererTypeShortName.startsWith(badPrefix) ){
                        if( severityCutoff >= sev("12e", SEV_WARNING) ){
                            fails += path(def)+"[Rule12e] Bad renderer-type "+rendererType+", should not begin with " +badPrefix +"\n";
                        }
                        continue;
                    }
                }
                if( ! StringUtil.isEmpty(rendererTypeSubpackage) ){
                    if( rendererTypeSubpackage.startsWith("renderkit")){
                    // renderer-type contains .renderkit.
                        if( severityCutoff >= sev("12f", SEV_WARNING) ){
                            fails += path(def)+"[Rule12f] Bad renderer-type "+rendererType+" should not have the class-only subpackage renderkit\n";
                        }
                    }
                }
                
                // expect subpackages
//                if( !StringUtil.isEmpty(rendererTypeSubpackage) ){
//                    fails += path(def)+"[Rule12b] Bad renderer-type "+rendererType+" has extra '.' separator(s), not in the expected format: "+expectedPackagePrefix+".ControlName\n";
//                }
                // expectedControlSubpackageName is "xp" or "dojo.form"
                if( !StringUtil.equals(rendererTypeSubpackage, expectedControlSubpackageName) ){
                    // fail.
                    if( StringUtil.isEmpty(expectedControlSubpackageName) ){
                        // expect no subpackage
                        if( severityCutoff >= sev("12g", SEV_WARNING) ){
                            fails += path(def) + "[Rule12g] Bad renderer-type " + rendererType + " has unexpected subpackage " + rendererTypeSubpackage + "\n";
                        }
                    }else if( rendererTypeSubpackage.startsWith(expectedControlSubpackageName+".") ){
                        // starts with xp.something
                        String extraSubpackage = rendererTypeSubpackage
                                .substring(expectedControlSubpackageName
                                        .length() + 1);
                        if( severityCutoff >= sev("12h", SEV_WARNING) ){
                            fails += path(def) + "[Rule12h] Bad renderer-type " + rendererType + " has extra subpackage " + extraSubpackage + "\n";
                        }
                    }else{ 
                        // starts with foo, not xp
                        if( severityCutoff >= sev("12i", SEV_ERROR) ){
                            fails += path(def) + "[Rule12i] Bad renderer-type " + rendererType + ", expect subpackage [" + expectedControlSubpackageName + "], was [" + rendererTypeSubpackage + "]\n";
                        }
                    }
                }
                
				// note, not checking the renderer-type against the class short-name
				// as the renderer-types are often a new invention.
			}
            }
            else if( someDefType instanceof FacesComplexDefinition ){
                FacesComplexDefinition def = (FacesComplexDefinition) someDefType;
                
                ComplexTypeExpectedNaming naming = findMatch(reg, complexTypeExpectedNamings, def);
                if( null == naming ){
                    naming = findMatch(reg, detectedComplexTypeBases, def);
                    if( null != naming ){
                        // multiple rules depend on the detected naming subpackage.
                        // Only report the problem if some of those rules
                        // are severer than the cutoff
                        boolean reportSubpackageProblems = false
                            || (severityCutoff >= sev("13b", SEV_WARNING))
                            || (severityCutoff >= sev("13c", SEV_WARNING))
                            || (severityCutoff >= sev("13e", SEV_WARNING))
                            || false;
                        
                        // need to update the JUnit test, as this detected base
                        // should be in the complexTypeExpectedNamings list
                        if( naming.origin == ComplexTypeExpectedNaming.DETECTED_IN_LIBRARY ){
                            if( naming.baseComplexDef == def ){
                                if( reportSubpackageProblems ){
                                fails += path(def)+ " Need to update JUnit test, " 
                                    +"this baseComplexType not in getComplexTypeExpectedNamings(), " 
                                    +"using detected subpackage "+naming.subpackage+" \n";
                                }
                            }else{
                                // will add the fail when iterating past that def.
                            }
                        }else{ // DETECTED_IN_DEPENDS
                            if( reportSubpackageProblems ){
                            fails += path(def)+ " Need to update JUnit test, " 
                                + "this complex-type depends an undeclared baseComplexType ("
                                + path(naming.baseComplexDef)
                                + ") not in getComplexTypeExpectedNamings(), " 
                                + "using detected subpackage "+naming.subpackage+" \n";
                            }
                        }
                    }
                }
                String complexClassName = def.getJavaClass().getName();
                String[] complexClassParsed = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, complexClassName);
                String complexClassPrefixPackage = complexClassParsed[0];
                String complexClassSubpackage = complexClassParsed[1];
                String complexClassShortName = complexClassParsed[2];
                
                if( !StringUtil.equals(complexClassPrefixPackage, expectedPackagePrefix) ){
                    if( severityCutoff >= sev("13a", SEV_ERROR) ){
                    	fails += path(def)+"[Rule13a] Bad complex-class package name "+def.getJavaClass().getName()+" does not begin with "+expectedPackagePrefix+"\n";
                    }
                }
                if( null != naming ){
                    String expectedSubpackage = naming.subpackage;
                    if( StringUtil.equals(complexClassSubpackage, expectedSubpackage) ){
                        // all is good
                    }else if( null != complexClassSubpackage && complexClassSubpackage.startsWith(expectedSubpackage+".") ){
                        // extra subpackage, instead of .resource is .resource.something
                        if( severityCutoff >= sev("13b", SEV_WARNING) ){
                            fails += path(def) + "[Rule13b] Bad complex-class package name " + def.getJavaClass().getName() 
                                + " has extra subpackage, expected [" +expectedSubpackage
                                + "] but was [" +complexClassSubpackage+ "]\n";
                        }
                    }else{
                        // != 
                        if( severityCutoff >= sev("13c", SEV_WARNING) ){
                            fails += path(def) + "[Rule13c] Bad complex-class package name " + def.getJavaClass().getName() 
                                + ", expect subpackage [" + expectedSubpackage + "], was [" + complexClassSubpackage + "]\n";
                        }
                    }
                }
                // complex-class short names must match the regular expression "[A-Z][a-zA-Z]*"
                Pattern regExp = compile("[A-Z][a-zA-Z]*");
                if( ! regExp.matcher(complexClassShortName).matches() ){
                    if( severityCutoff >= sev("13d", SEV_ERROR) ){
                        fails += path(def)+"[Rule13d] Bad complex-class "+complexClassName+", expected CamelCase short name, " +complexClassShortName+" does not match "+regExp.pattern()+"\n";
                    }
                }
                
                if( null != naming ){
                    String expectedNameSuffix = naming.nameSuffix;
                    if( !complexClassShortName.endsWith(expectedNameSuffix) ){
                        if( severityCutoff >= sev("13e", SEV_WARNING) ){
                            fails += path(def)+"[Rule13e] Bad complex-class "+complexClassName+", short name [" +complexClassShortName+"] does not have suffix ["+expectedNameSuffix+"]\n";
                        }
                    }
                }
                String complexId = def.getComplexId();
                if( ! StringUtil.equals(complexId, complexClassName) ){
                    if( severityCutoff >= sev("14a", SEV_WARNING) ){
                        fails += path(def)+"[Rule14a] Bad complex-id "+complexId+", does not match complex-class [" +complexClassName+"]\n";
                    }
                }
                if( (severityCutoff >= sev("14b", SEV_IGNORE)) || (severityCutoff >= sev("14c", SEV_IGNORE)) ){
                    // There's an alternative to Rule14a, where instead you use Rule14b and Rule14c,
                    // which are not normally tested (use getRuleSeverityOverrides to enable those and disable Rule14a).
                    String classPackage = def.getJavaClass().getPackage().getName();

                    if( def.isTag() ){
                        String expectedTagComplexId = classPackage+"."+def.getTagName();
                        if( ! StringUtil.equals(complexId, expectedTagComplexId) ){
                            if( severityCutoff >= sev("14b", SEV_IGNORE) ){
                                fails += path(def)+"[Rule14b] Bad tag complex-id "+complexId+", does not match expected [" +expectedTagComplexId+"]\n";
                            }
                        }
                    }else{ // !def.isTag
                        String classShortName = complexClassName.substring(complexClassName.lastIndexOf('.')+1);
                        String lowerCamelCaseShortName = Character.toLowerCase(classShortName.charAt(0))+classShortName.substring(1);
                        String expectedAbstractComplexId = lowerCamelCaseShortName+"Interface";
                        if( ! StringUtil.equals(complexId, expectedAbstractComplexId) ){
                            if( severityCutoff >= sev("14c", SEV_IGNORE) ){
                                fails += path(def)+"[Rule14c] Bad abstract complex-id "+complexId+", does not match expected [" +expectedAbstractComplexId+"]\n";
                            }
                        }
                    }
                }
                
            }
            else if( someDefType instanceof FacesGroupDefinition ){
                FacesGroupDefinition def = (FacesGroupDefinition) someDefType;
                
                String groupType = def.getGroupType();
                String[] complexClassParsed = parseToPrefixSubpackageAndShortName(expectedPackagePrefix, groupType);
                String groupTypePrefixPackage = complexClassParsed[0];
                String groupTypeSubpackage = complexClassParsed[1];
                String groupTypeShortName = complexClassParsed[2];
                if( "group".equals(groupTypeShortName) ){
                    if( severityCutoff >= sev("15c", SEV_WARNING) ){
                        fails += path(def) + "[Rule15c] Bad group-type short name [group] in " + groupType 
                            + ", expect package name like " +expectedPackagePrefix+".group.*"
                            +", with the group purpose as the short name.\n";
                    }
                    groupTypeSubpackage += ".group";
                    groupTypeShortName = "";
                }
                
                if( !StringUtil.equals(groupTypePrefixPackage, expectedPackagePrefix) ){
                    if( severityCutoff >= sev("15a", SEV_WARNING) ){
                        fails += path(def)+"[Rule15a] Bad group-type package name "+groupType+" does not begin with "+expectedPackagePrefix+"\n";
                    }
                }
                String expectedSubpackage = "group";
                if( StringUtil.equals(groupTypeSubpackage, expectedSubpackage) ){
                    // all is good
                }else if( null != groupTypeSubpackage && groupTypeSubpackage.startsWith(expectedSubpackage+".") ){
                    // extra subpackage, instead of .resource is .resource.something
                    if( severityCutoff >= sev("15b", SEV_WARNING) ){
                        fails += path(def) + "[Rule15b] Bad group-type package name " + groupType 
                            + " has extra subpackage, expected [" +expectedSubpackage
                            + "] but was [" +groupTypeSubpackage+ "]\n";
                    }
                }else{
                    // != 
                    if( severityCutoff >= sev("15c", SEV_WARNING) ){
                        fails += path(def) + "[Rule15c] Bad group-type package name " + groupType 
                            + ", expect subpackage [" + expectedSubpackage + "], was [" + groupTypeSubpackage + "]\n";
                    }
                }
            }
            fails += moreRules(someDefType, severityCutoff, expectedPrefixes);
		}
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testNamingConventions"));
		for (Object[] skip : unusualTagness) {
			if( !isSkipUsed(skip) ){
				fails += "Unused unusualTagness skip: " +((Class<?>)skip[0]).getName()
						+" actualIsTag=" +skip[1]+" actualNamedAsTag="+skip[2]+"\n";
			}
		}
		for (Object[] skip : nonObviousTagName) {
			if( !isSkipUsed(skip) ){
				fails += "Unused nonObviousTagName skip: "
						+ ((Class<?>) skip[0]).getName() 
						+ " tagName="+ skip[1] + "\n";
			}
		}
		if( fails.length() > 0 ){
			fail( XspTestUtil.getMultilineFailMessage(fails));
		}
	}
	/**
	 * Available to override in subclasses.
	 */
	protected String moreRules(FacesDefinition someDefType, int severityCutoff,
			String[] expectedPrefixes) {
		return "";
	}
	/**
     * @param ruleSeverityOverridesArr
     * @return
     */
    private Map<String, Integer> toRuleSeverityOverrideMap(List<Object[]> ruleSeverityOverridesArr) {
        if( ruleSeverityOverridesArr.isEmpty() ){
            return Collections.emptyMap();
        }
        Map<String, Integer> overrides = new HashMap<String, Integer>();
        for (Object[] override : ruleSeverityOverridesArr) {
            
            String rule = (String) override[0];
            Integer newSeverity = (Integer) override[1];
            overrides.put(rule, newSeverity);
        }
        return overrides;
    }
    /**
     * @param complexTypeExpectedNamings
     * @param def
     * @return
     */
    private ComplexTypeExpectedNaming findMatch(FacesSharableRegistry reg,
            List<ComplexTypeExpectedNaming> complexTypeExpectedNamings,
            FacesComplexDefinition def) {
        for (ComplexTypeExpectedNaming naming : complexTypeExpectedNamings) {
            
            if( naming.isMatch(reg, def) ){
                return naming;
            }
        }
        return null;
    }
    /**
     * @param reg
     * @param libComplexDefs
     * @param complexTypeExpectedNamings
     * @return
     */
    private List<ComplexTypeExpectedNaming> detectOtherComplexTypeBases(
            FacesSharableRegistry reg, List<FacesComplexDefinition> libComplexDefs,
            List<ComplexTypeExpectedNaming> complexTypeExpectedNamings) {
    
        if( libComplexDefs.isEmpty() ){
            return new ArrayList<ComplexTypeExpectedNaming>();
        }
        List<FacesComplexDefinition> knownSoFar = new ArrayList<FacesComplexDefinition>();
        for (ComplexTypeExpectedNaming naming : complexTypeExpectedNamings) {
            knownSoFar.add( naming.baseComplexDef );
        }
    
        List<ComplexTypeExpectedNaming> detected = new ArrayList<ComplexTypeExpectedNaming>();
        for (FacesComplexDefinition def : libComplexDefs) {
            if( def.getParent() == null || "com.ibm.xsp.BaseComplexType".equals(def.getParent().getId()) ){
                continue;
            }
            boolean isKnownBase = false;
            FacesComplexDefinition unknownBase = null;
            for (FacesComplexDefinition ancestor = RegistryUtil.getComplexParent(def); 
                    ancestor != null; 
                        ancestor = RegistryUtil.getComplexParent(ancestor) ) {
                if( knownSoFar.contains(ancestor) ){
                    isKnownBase = true;
                    break;
                }
                if (null == ancestor.getParent()
                        || "com.ibm.xsp.BaseComplexType".equals(ancestor.getParent().getId()) ) {
                    unknownBase = ancestor;
                    break;
                }
            }
            if( ! isKnownBase ){
                boolean isBaseFromLibrary = libComplexDefs.contains(unknownBase);
                int origin = isBaseFromLibrary? ComplexTypeExpectedNaming.DETECTED_IN_LIBRARY : ComplexTypeExpectedNaming.DETECTED_IN_DEPENDS;
                detected.add(new ComplexTypeExpectedNaming(reg, unknownBase, origin));
    
                knownSoFar.add(unknownBase);
            }
        }
        return detected;
    }
    private Pattern compile(String regExp){
        String camelCaseHeadDownRegExp = "[a-z][a-zA-Z]*";
        if( camelCaseHeadDownRegExp.equals(regExp) ){
            if( null == camelCaseHeadDown ){
                camelCaseHeadDown = Pattern.compile(camelCaseHeadDownRegExp);
            }
            return camelCaseHeadDown;
        }
        String camelCaseHeadUpRegExp = "[A-Z][a-zA-Z]*";
        if( camelCaseHeadUpRegExp.equals(regExp) ){
            if( null == camelCaseHeadUp ){
                camelCaseHeadUp = Pattern.compile(camelCaseHeadUpRegExp);
            }
            return camelCaseHeadUp;
        }
        throw new RuntimeException("Need to update junit test to handle different regular expressions, this one is not handled: "+regExp);
    }
    protected List<String> getBadRendererShortNamePrefixes(){
        return new ArrayList<String>();
    }
    /**
     *  {}
     * @return
     */
    protected List<Object[]> getComplexTypeExpectedNamings(){
        return new ArrayList<Object[]>();
    }
    private List<ComplexTypeExpectedNaming> parse(FacesSharableRegistry reg, List<Object[]> list){
        List<ComplexTypeExpectedNaming> output = new ArrayList<ComplexTypeExpectedNaming>();
        for (Object[] item : list) {
            String subpackage = (String) item[1];
            String nameSuffix = (String) item[2];
            if( item[0] instanceof String){
                String baseComplexId = (String) item[0];
                output.add(new ComplexTypeExpectedNaming(reg, baseComplexId, subpackage, nameSuffix));
            }else{
                Class<?> baseComplexClass = (Class<?>) item[0]; 
                output.add(new ComplexTypeExpectedNaming(reg, baseComplexClass, subpackage, nameSuffix));
            }
        }
        return output;
    }
    private static class ComplexTypeExpectedNaming{
        public static final int DETECTED_IN_LIBRARY = 1;
        public static final int DETECTED_IN_DEPENDS = 2;
        public static final int SPECIFIED_IN_SUBCLASS = 3;
        
        public final FacesComplexDefinition baseComplexDef;
        public final Class<?> baseComplexClass;
        public final String subpackage;
        public final String nameSuffix;
        public final int origin;
        public List<FacesDefinition> substitutableDefs;

        public ComplexTypeExpectedNaming(FacesSharableRegistry reg,
                Class<?> baseComplexClass, String subpackage, String nameSuffix) {
            super();
            this.baseComplexDef = RegistryUtil.getFirstComplexDefinition(reg, baseComplexClass);
            if( null == baseComplexDef ){
                throw new IllegalArgumentException("No complex-type for class: "+baseComplexClass);
            }
            this.baseComplexClass = baseComplexClass;
            this.subpackage = subpackage;
            this.origin = SPECIFIED_IN_SUBCLASS;
            this.nameSuffix = nameSuffix;
        }
        public ComplexTypeExpectedNaming(FacesSharableRegistry reg, String baseComplexId, String subpackage, String nameSuffix){
            super();
            this.baseComplexDef = (FacesComplexDefinition) reg.findDef(baseComplexId);
            if( null == baseComplexDef ){
                throw new IllegalArgumentException("No complex-type for complex-id: "+baseComplexId);
            }
            this.baseComplexClass = baseComplexDef.getJavaClass();
            this.subpackage = subpackage;
            this.origin = SPECIFIED_IN_SUBCLASS; 
            this.nameSuffix = nameSuffix;
        }
        /**
         * @param reg
         * @param unknownBase
         */
        public ComplexTypeExpectedNaming(FacesSharableRegistry reg,
                FacesComplexDefinition baseComplexDef, int origin) {
            super();
            this.baseComplexDef = baseComplexDef;
            this.baseComplexClass = baseComplexDef.getJavaClass();
            String fullPackage = baseComplexClass.getPackage().getName();
            this.subpackage = fullPackage.substring(fullPackage.lastIndexOf('.')+1);
            this.origin = origin;
            String simpleName = baseComplexClass.getSimpleName();
            if( simpleName.startsWith("I") && Character.isUpperCase(simpleName.charAt(1)) ){
                // ISomething, remove interface I prefix
                simpleName = simpleName.substring(1);
            }
            if( simpleName.startsWith("Abstract") ){
                simpleName = simpleName.substring("Abstract".length());
            }
            if( simpleName.endsWith("Impl") ){
                simpleName = simpleName.substring(0, simpleName.length() - "Impl".length());
            }
            this.nameSuffix = simpleName;
        }
        public boolean isMatch(FacesSharableRegistry reg, FacesComplexDefinition def){
            if( baseComplexClass.isAssignableFrom(def.getJavaClass()) ){
                return true;
            }
            if( null == substitutableDefs){
                substitutableDefs = RegistryUtil.getSubstitutableDefinitions(baseComplexDef, reg);
            }
            return substitutableDefs.contains(def);
        }
    }
    /**
 * @param expectedPackagePrefix
 * @param name
 * @return
 */
    private String[] parseToPrefixSubpackageAndShortName(
            String expectedPackagePrefix, String name) {
        
        final String actualPackagePrefix;
        final String subpackage;
        final String shortName;
        
        int lastDotIndex = name.lastIndexOf('.');
        if( -1 == lastDotIndex ){
            return new String[]{"","",name};
        }
        shortName = name.substring(lastDotIndex+1);
        
        String fullPackage = name.substring(0, lastDotIndex);
        
        int subpackageSeparatorDotIndex;
        if( fullPackage.startsWith(expectedPackagePrefix+".") ){
            // package begins with com.ibm.xsp.
            subpackageSeparatorDotIndex = expectedPackagePrefix.length();
        }else if( fullPackage.equals(expectedPackagePrefix) ){
            // package is com.ibm.xsp.
            subpackageSeparatorDotIndex = expectedPackagePrefix.length();
        }else if( fullPackage.length() == 0 ){
            // package is ""
            subpackageSeparatorDotIndex = 0;
        }else if( fullPackage.startsWith(expectedPackagePrefix) ){
            // begins with com.ibm.xspsomething, instead of com.ibm.xsp.something
            // subpackage = xspsomething
            int previousDot = fullPackage.lastIndexOf('.',expectedPackagePrefix.length());
            subpackageSeparatorDotIndex = -1 == previousDot? fullPackage.length() : previousDot;
        }else{
            // does not begin with com.ibm.xsp
            String truncatedExpectedPackagePrefix;
            if( expectedPackagePrefix.indexOf('.') == -1){
                truncatedExpectedPackagePrefix = expectedPackagePrefix;
            }else{
                truncatedExpectedPackagePrefix = expectedPackagePrefix.substring(0, expectedPackagePrefix.lastIndexOf('.'));
            }
            if( name.startsWith(truncatedExpectedPackagePrefix)){
                // starts with "com.ibm"
                if( '.' == name.charAt(truncatedExpectedPackagePrefix.length()) ){
                    subpackageSeparatorDotIndex = truncatedExpectedPackagePrefix.length();
                }else{
                    // starts with com.ibmsomething
                    int previousDot = name.lastIndexOf('.',truncatedExpectedPackagePrefix.length());
                    subpackageSeparatorDotIndex = (-1 == previousDot)? fullPackage.length() : previousDot;
                }
            }else{
                // does not begin with com.ibm.xsp nor com.ibm
                subpackageSeparatorDotIndex = fullPackage.length();
            }
        }
        
        actualPackagePrefix = name.substring(0, subpackageSeparatorDotIndex);
        
        if( subpackageSeparatorDotIndex<fullPackage.length() ){
            subpackage = fullPackage.substring(subpackageSeparatorDotIndex+1);
        }else{
            subpackage = "";
        }
        return new String[]{actualPackagePrefix, subpackage, shortName};
    }
    protected boolean isRequireControlSubpackageName(){
        return true;
    }
    private boolean isInheritedComponentFamily(FacesComponentDefinition def) {
        FacesComponentDefinition parent = (FacesComponentDefinition) def.getParent();
        if( null == parent ){
            return false;
        }
        return StringUtil.equals(def.getComponentFamily(), parent.getComponentFamily());
    }
    /**
     * @param def
     * @return
     */
    private boolean isInheritedRendererType(FacesComponentDefinition def) {
        
        String rendererType = def.getRendererType();
        // iterate through ancestors
        for (FacesComponentDefinition a = (FacesComponentDefinition)def.getParent();
                a != null; 
                a = (FacesComponentDefinition)a.getParent()) {
            
            if( StringUtil.equals( rendererType, a.getRendererType()) ){
                return true;
            }
        }
        return false;
    }
    private boolean isFuzzyMatch(String expected, String actual) {
        if( expected.equals(actual) ){
            return true;
        }
        boolean fuzzyEnd;
        boolean allowEmptyEnd = expected.endsWith(".*");
        fuzzyEnd = allowEmptyEnd || expected.endsWith(".+");
        if( fuzzyEnd ){
            expected = expected.substring(0, expected.length() - 2);
        }
        
        int indexInActual = actual.indexOf(expected);
        
        if (-1 == indexInActual) {
            // required suffix absent.
            if( expected.length() == 0 && (!fuzzyEnd || allowEmptyEnd) ){
    //          (!fuzzyStart || allowEmptyStart) &&
                return true;
            }
            return false;
        }
        
        if( indexInActual > 0 ){
            return false;
        }
        
        boolean endMismatch = false;
        int afterSuffixIndex = indexInActual + expected.length();
        String afterSuffix = afterSuffixIndex < actual.length()? actual.substring(afterSuffixIndex) : "";
        if( afterSuffix.length() == 0 ){
            endMismatch = fuzzyEnd || ! allowEmptyEnd;
        }else if( afterSuffix.length() > 0){
            endMismatch = !fuzzyEnd;
        }
        if( endMismatch ){
            return false;
        }
        return true;
    }
    /**
     * Can be used in the subclass to prevent testing certain controls.
     * @param defs
     * @return
     */
    protected List<FacesDefinition> filterDefinitions(
            List<FacesDefinition> defs) {
        return defs;
    }
	/**
	 * @return
	 */
	protected String[] getExpectedPrefixes() {
		return _expectedPrefixes;
	}
	/**
	 * Entries are:
	 * <pre>
	 * new Object[]{ defClass, String expectedTagName },
	 * </pre>
	 * @return
	 */
	protected List<Object[]> getNonObviousTagNameSkips() {
		return new ArrayList<Object[]>();
	}
	/**
	 * Entries are:
	 * <pre>
	 * new Object[]{ defClass, boolean expectedIsTag, boolean expectedNamedAsTag},
	 * </pre>
	 * @return
	 */
	protected List<Object[]> getUnusualTagnessSkips() {
		return new ArrayList<Object[]>();
	}
	/**
	 * Entries are the String skip text, without the trailing \n
	 * @return
	 */
	protected String[] getSkips(){
		return StringUtil.EMPTY_STRING_ARRAY;
	}
    /**
     * Entries are:
     * <pre>
     * new Object[]{ ruleString, newSeverityInteger},
     * new Object[]{ "12a", SEV_WARNING},
     * </pre>
     * @return
     */
	protected List<Object[]> getRuleSeverityOverrides(){
	    return new ArrayList<Object[]>();
	}
    protected int getRuleSeverityLevelCutoff(){
        return SEV_ANYTHING;
    }
	/**
	 * Available to call in subclass overrides of {@link #moreRules(FacesDefinition, int, String[])};
	 * @param def
	 * @return
	 */
    protected int sev(String ruleName, int defaultSeverity){
        Integer overrideSeverity = ruleSeverityOverrides.get(ruleName);
        if( null != overrideSeverity ){
            return overrideSeverity.intValue();
        }
        return defaultSeverity;
    }
	private Object[] findNonObviousTagNameSkip(List<Object[]> skipList,
			Class<?> javaClass) {
		int i = 0;
		for (Object[] skip : skipList) {
			if( javaClass.equals(skip[0]) ){
				if( skip.length == skipUsedLength ){
					throw new RuntimeException("Using nonObviousTagName skip more than once: "+javaClass);
				}
				markSkipAsUsed(skipList, i, skip);
				return skip;
			}
			i++;
		}
		return null;
	}
	private Object[] findUnusualTagnessSkip(List<Object[]> skipList,
			Class<?> javaClass) {
		int i = 0;
		for (Object[] skip : skipList) {
			if( javaClass.equals(skip[0]) ){
				if( skip.length == skipUsedLength ){
					throw new RuntimeException("Using unusualTagness skip more than once: "+javaClass);
				}
				markSkipAsUsed(skipList, i, skip);
				return skip;
			}
			i++;
		}
		return null;
	}
	private void markSkipAsUsed(List<Object[]> skipList, int skipIndex, Object[] skip) {
		skip = XspTestUtil.concat(skip, new Object[skipUsedLength - skip.length]);
		skip[skipUsedLength-1] = true;
		skipList.set(skipIndex, skip);
	}
	private boolean isSkipUsed(Object[] skip) {
		if( skip.length < skipUsedLength )
			return false;
		return true;
	}
	/**
	 * Available to call in subclass overrides of {@link #moreRules(FacesDefinition, int, String[])};
	 * @param def
	 * @return
	 */
	protected String path(FacesDefinition def) {
		return def.getFile().getFilePath()+"/"+XspRegistryTestUtil.descr(def)+" ";
	}
}
