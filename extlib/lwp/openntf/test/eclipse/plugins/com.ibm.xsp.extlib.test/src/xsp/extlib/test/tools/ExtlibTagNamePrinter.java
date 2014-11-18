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
* Date: 11 Feb 2011
* ExtlibTagNamePrinter.java
*/

package xsp.extlib.test.tools;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ConfigParserFactory;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionsHaveDisplayNamesTest.DefinitionDescrAnnotater;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 11 Feb 2011
 * Unit: ExtlibTagNamePrinter.java
 */
public class ExtlibTagNamePrinter {

    public static void main(String[] args){
        System.out.println("ExtlibTagNamePrinter.main()");
        ConfigParserFactory.addAnnotater( new DefinitionDescrAnnotater());
        FacesSharableRegistry reg = StandardRegistryMaintainer.getStandardRegistry();
        String libraryId = "com.ibm.xsp.extlib.library";
        List<FacesDefinition> defs = getLibraryDefs(reg, libraryId);
        Collections.reverse(defs);
        System.out.println("ExtlibTagNamePrinter.main() start");
        FacesProject proj = null;
        FacesLibraryFragment file = null;
        for (FacesDefinition def : defs) {
            if( proj != RegistryUtil.getProject(def) ){
               proj = RegistryUtil.getProject(def);
               System.out.println("ExtlibTagNamePrinter.main() library: "+proj.getId() );
            }
            if( file != def.getFile() ){
                file = def.getFile();
                System.out.println("file: "+file.getFilePath() );
            }
            if( def.isTag() ){
            if( ! (def instanceof FacesGroupDefinition) ){
                String uses = uses(def, libraryId);
                uses = uses.length() == 0? "" : "\t uses: "+uses;
//                String parent = parent(def, libraryId);
                System.out.println("\t"
                        + toTagRef(def)
                        + "\t |"
                        + displayName(def) 
                        + "|\t (" + descr(def) + ")"
//                        + (parent.length()>0?"\n\t":"")
//                        + parent
//                        + (uses.length()>0?"\n\t":"")
//                        + uses
                        );
            }
            }
        }
        System.out.println("ExtlibTagNamePrinter.main() end");
    }


//	/**
//	 * @param def
//	 * @return
//	 */
//	private static String parent(FacesDefinition def, String libraryId) {
//	    FacesDefinition parentDef = def.getParent();
//	    if( null == parentDef ){
//	    	return "";
//	    }
//		if( def instanceof FacesComplexDefinition 
//				&& "com.ibm.xsp.BaseComplexType".equals(parentDef.getId()) ){
//			return "\t no-parent";
//		}
//		if( def instanceof FacesComponentDefinition ){
//			// only interested in library parents for components
//			if( !libraryId.equals(RegistryUtil.getRegistry(parentDef).getId()) ){
//				return "";
//			}
//		}
//		return "\t parent: " + toTagRef(parentDef);
//	}


	/**
	 * @param reg
	 * @param string
	 * @return
	 */
	private static List<FacesDefinition> getLibraryDefs(
			FacesSharableRegistry reg, String libraryId) {
		for (FacesSharableRegistry subset : reg.getDepends()) {
			String subsetId = subset.getId();
			if( libraryId.equals(subsetId) ){
				return subset.findLocalDefs();
			}
		}
		return Collections.emptyList();
	}

	/**
	 * @param def
	 * @return
	 */
	private static String uses(FacesDefinition def, String libraryId) {
		Set<String> uses = new HashSet<String>(); 
		for (FacesProperty prop : RegistryUtil.getProperties(def)) {
			if( prop instanceof FacesContainerProperty ){
				prop = ((FacesContainerProperty)prop).getItemProperty();
			}
			if( prop instanceof FacesSimpleProperty ){
				FacesSimpleProperty simple = (FacesSimpleProperty) prop;
				FacesDefinition typeDef = simple.getTypeDefinition();
				if( null != typeDef && libraryId.equals(RegistryUtil.getRegistry(typeDef).getId()) ){
					uses.add(toTagRef(typeDef));
				}
			}
		}
		if( uses.isEmpty() ){
			return "";
		}
		return StringUtil.concatStrings(StringUtil.toStringArray(uses), ' ', false);
	}

	/**
	 * @param def
	 * @return
	 */
	private static String toTagRef(FacesDefinition def) {
		String prefix = def.getFirstDefaultPrefix();
		String separator = def.isTag()? ":" : "-";
		String id = def.getId();
		String type = def instanceof FacesComponentDefinition ? "(n)"
				: def instanceof FacesComplexDefinition ? "(x)" 
				: def instanceof FacesGroupDefinition? "(g)" 
				: "(o)";
		return type + "\t"+prefix + separator + id;
	}

	/**
	 * @param def
	 * @return
	 */
	private static String descr(FacesDefinition def) {
		String descr = (String) def.getExtension("description");
		if( null == descr ){
			return null;
		}
		return descr.replace("\n", "  ").trim();
	}

	/**
	 * @param def
	 * @return
	 */
	private static String displayName(FacesDefinition def) {
		return (String) def.getExtension("display-name");
	}
}
