/*
 * © Copyright IBM Corp. 2009, 2013
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
* Date: 3 Jun 2009
* PrintTagNamesAndProps.java
*/


package com.ibm.xsp.test.framework.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibrary;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 3 Jun 2009
 * Unit: PrintTagNamesAndProps.java
 */
public class PrintTagNamesAndProps {

    private static final class TagsComparator implements Comparator<Object[]> {
        public int compare(Object[] o1, Object[] o2) {
            String s1 = getName(o1); 
            String s2 = getName(o2); 
            return s1.compareTo(s2);
        }
    }

    public static void main(String[] args) {
        FacesSharableRegistry registry = StandardRegistryMaintainer.getStandardRegistry();
        
        System.out.println("PrintTagNamesAndProps.main()");
        
        List<Object[]> tagsAndProps = getTagsAndProps(registry);
        
//        removeAll(tagsAndProps, SinceVersion850List.tagsAndProps, true);
//        removeAll(tagsAndProps, SinceVersion851List.tagsAndProps, true);
//        removeAll(tagsAndProps, SinceVersion852List.tagsAndProps, true);
//        removeAll(tagsAndProps, SinceVersion853List.tagsAndProps, true);
        
        print(tagsAndProps);
    }
    /**
     * @param tagsAndProps
     */
    public static void print(List<Object[]> tagsAndProps) {
        for (Object[] tagAndProp : tagsAndProps) {
            String prefixedName = getName(tagAndProp);
            
            if( isNoProps(tagAndProp) ){
                // new Object[]{"xp:validator"}
                System.out.println("new Object[]{\"" +
                        prefixedName +
                        "\", " +isNewTag(tagAndProp)+"},");
                continue;
            }
            String[] propNames = getProps(tagAndProp);
            //new Object[]{"xp:validator", {
            //    "validatorId",
            //}};
            System.out.println("new Object[]{\"" +
                    prefixedName +
                    "\", " +isNewTag(tagAndProp)+", new String[]{");
            
            for (String name : propNames) {
                System.out.println("\t\""+name+"\",");
            }
            System.out.println("}},");
        }
    }
    /**
     * Note, calling them a depends library to distinguish from a namespace library. 
     * @param registry
     * @param dependsLibraryId
     * @param fullList
     * @return
     */
    public static List<Object[]> filterToDependsLibrary(FacesSharableRegistry registry, String dependsLibraryId, List<Object[]>fullList){
        FacesSharableRegistry filterToReg = null;
        for (FacesSharableRegistry depend : registry.getDepends()) {
            if(dependsLibraryId.equals(depend.getId()) ){
                filterToReg = depend;
                break;
            }
        }
        if( null == filterToReg ){
            throw new IllegalArgumentException("Cannot find the library registry with ID: "+dependsLibraryId);
        }
        
        Map<String, FacesLibrary> prefixToNamespace = getPrefixToNamespace(registry);
        
        List<FacesDefinition> libDefs = filterToReg.findLocalDefs();
        
        List<Object[]> output = new ArrayList<Object[]>(libDefs.size());
        for (Object[] fullListTagArr : fullList) {
            
            String prefixedName = getName(fullListTagArr);
            FacesDefinition fullListDef = getDef(prefixToNamespace, prefixedName);
            if( libDefs.contains(fullListDef) ){
                
                output.add(fullListTagArr);
            }
        }
        return output;
    }
    /**
     * @param registry
     * @return
     */
    public static List<Object[]> getTagsAndProps(FacesSharableRegistry registry) {
        return getTagsAndProps(registry, registry);
    }
    
    /**
     * @param registryToList
     * @param fullRegistry
     *            (if registryToList contains non-tag defs that have no
     *            descendants in registryToList, but do have descendants in
     *            fullRegistry, those defs will be included in the listing.)
     * @return
     */
    public static List<Object[]> getTagsAndProps(FacesSharableRegistry registryToList, FacesSharableRegistry fullRegistry) {
        List<String> tagsAndParents = getTagsAndUsedParents(registryToList, fullRegistry);
        Collections.sort(tagsAndParents);
        
        Map<String, FacesLibrary> prefixToNamespace = getPrefixToNamespace(fullRegistry);
        
        List<Object[]> tagsAndProps = new ArrayList<Object[]>();
        
        for (String prefixedName : tagsAndParents) {
            FacesDefinition def = getDef(prefixToNamespace, prefixedName);
            Collection<String> propNames = def.getDefinedPropertyNames();
            if( propNames.isEmpty() ){
                tagsAndProps.add(new Object[]{prefixedName, true});
                continue;
            }
            Object[] tagAndProp = new Object[]{prefixedName, true, null};
            List<String> sortedNames = new ArrayList<String>(propNames);
            Collections.sort(sortedNames);
            Object[] editedTag = withProps(tagAndProp, StringUtil.toStringArray(sortedNames));
            tagsAndProps.add(editedTag);
        }
        return tagsAndProps;
    }
    public static void addAll(List<Object[]> oldTags, Object[][] recentTags){
        for (Object[] recentTag : recentTags) {
            String recentPrefixedName = getName(recentTag);
            
            int tagIndex = indexOf(oldTags, recentPrefixedName);
            if( tagIndex < 0 ){
                // new tag
                int insertIndex = -(tagIndex+1);
                oldTags.add(insertIndex, recentTag);
                continue;
            }
            // change to existing tag, props added
            Object[] oldTag = oldTags.get(tagIndex);
            String[] oldProps = getProps(oldTag); 
            String[] newProps = getProps(recentTag);
            
            oldProps = XspTestUtil.concat(oldProps, newProps);
            Arrays.sort(oldProps);
            Object[] editedTag = withProps(oldTag, oldProps);
            oldTags.set(tagIndex, editedTag);
        }
    }
    public static void removeAll(List<Object[]> current,
            Object[][] oldTags, boolean markAsOld) {
        for (Object[] oldTag : oldTags) {
            String oldPrefixedName = getName(oldTag);
            
            int tagIndex = indexOf(current, oldPrefixedName);
            if( tagIndex < 0 ){
                // old tag not available anymore
                // (could lead to runtime errors)
                continue;
            }
            Object[] currentTag = current.get(tagIndex);
            String[] oldProps = getProps(oldTag);
            String[] currentProps = getProps(currentTag); 
            
            if( Arrays.deepEquals(oldProps, currentProps) ){
                // same props, so remove entire old tag
                current.remove(tagIndex);
                continue;
            }
            // some current props not in oldProps,
            // so change current props to only have new props.
            ArrayList<String> newProps = new ArrayList<String>();
            newProps.addAll(Arrays.asList(currentProps));
            newProps.removeAll(Arrays.asList(oldProps));
            if( newProps.isEmpty() ){
                // oldProps contained propertys not in currentProps
                current.remove(tagIndex);
                continue;
            }
            Object[] editedTag = withProps(currentTag, StringUtil.toStringArray(newProps));
            current.set(tagIndex, editedTag);
            if( markAsOld ){
                setNewTag(currentTag, false);
            }
        }
    }
    private static int indexOf(List<Object[]> current, String oldPrefixedName) {
        return Collections.binarySearch(current, new Object[]{oldPrefixedName}, 
                new TagsComparator());
    }

    /**
     * @param prefixToNamespace
     * @param prefixedName
     * @return
     */
    public static FacesDefinition getDef(
            Map<String, FacesLibrary> prefixToNamespace, String prefixedName) {
//        boolean isTag = prefixedName.contains(":");
        String normalized = prefixedName.replace('-', ':');
        String[] prefixAndName = normalized.split(":");
        FacesLibrary lib = prefixToNamespace.get(prefixAndName[0]);
        if( null == lib ){
            throw new RuntimeException("Internal logic error, " 
                    + "prefix map does not contain prefix for "
                    + prefixAndName[0]+", which should originally have come from that map.");
        }
        FacesDefinition def = lib.getDefinition(prefixAndName[1]);
        if( null != def ){
            return def;
        }
        for (FacesLibraryFragment file : lib.getFiles()) {
            for (FacesDefinition fileDef : file.getDefs()) {
                if( fileDef.getReferenceId().equals(prefixAndName[1]) ){
                    return fileDef;
                }
            }
        }
        return null;
    }

    /**
     * @param registry
     * @return
     */
    public static Map<String, FacesLibrary> getPrefixToNamespace(
            FacesRegistry registry) {
        Map<String, FacesLibrary> prefixToNamespace = new HashMap<String, FacesLibrary>();
        for (String namespace : registry.getNamespaceUris()) {
            FacesLibrary lib = registry.getLibrary(namespace);
            String prefix = lib.getFirstDefaultPrefix();
            
            prefixToNamespace.put(prefix, lib);
        }
        return prefixToNamespace;
    }

    private static List<String> getTagsAndUsedParents(FacesSharableRegistry registryToList, FacesSharableRegistry fullRegistry) {
        
        String parentTag = "is-used-non-tag-parent";
        markUsedNonTagParents(fullRegistry, parentTag);
        
        List<String> tagsAndParents = new ArrayList<String>();
        
        for (String namespace : registryToList.getNamespaceUris()) {
            FacesLibrary namespaceLib = registryToList.getLibrary(namespace);
            String prefix = namespaceLib.getFirstDefaultPrefix();
            
            for (FacesDefinition def : namespaceLib.getDefs()) {
                if( ! (def instanceof FacesComplexDefinition || def instanceof FacesComponentDefinition)){
                    continue;
                }
                String name = prefix + ":" + def.getTagName();
                if( ! def.isTag() ){
                    boolean isCustomControlBase = UIIncludeComposite.class.equals(def.getJavaClass());
                    if( null != def.getExtension(parentTag) ){
                        // non-tag that is ancestor of something in fullRegistry
                        name = prefix + "-"+def.getId();
                    }else if( isCustomControlBase ){
                        // UIIncludeComposite is actually used, even though it doesn't 
                        // have a tag-name; it's the parent to all the custom controls.
                        name = prefix + "-"+def.getId();
                    }else{
                        continue;
                    }
                }
                if( tagsAndParents.contains(name) ){
                    throw new RuntimeException("More than 1 parent control named "+name);
                }
                
                tagsAndParents.add(name);
            }
        }
        return tagsAndParents;
    }
    private static void markUsedNonTagParents(FacesSharableRegistry fullRegistry, String usedParentTag) {
        for (FacesDefinition defInReg : fullRegistry.findDefs()) {
            if( defInReg.isTag() ){
                markAncestorsOf(defInReg, usedParentTag);
            }
        }
    }
    private static void markAncestorsOf(FacesDefinition defInReg,
            String usedParentTag) {
        
        FacesDefinition parent = defInReg.getParent();
        if( null == parent ){
            return;
        }
        String existingTag = (String) parent.getExtension(usedParentTag);
        if( null != existingTag ){
            return;
        }
        parent.setExtension(usedParentTag, usedParentTag);
        markAncestorsOf(parent, usedParentTag);
    }
    public static boolean isNoProps(Object[] oldTag) {
        return oldTag.length < 3;
    }
    public static String[] getProps(Object[] tagAndProp) {
        if( isNoProps(tagAndProp) ){
            return StringUtil.EMPTY_STRING_ARRAY;
        }
        return (String[])tagAndProp[2];
    }
    public static Object[] withProps(Object[] tag, String[] props) {
        if( isNoProps(tag) ){
            return new Object[]{tag[0], tag[1], props};
        }
        tag[2] = props;
        return tag;
    }
    public static String getName(Object[] tagAndProp) {
        return (String) tagAndProp[0];
    }
    public static boolean isNewTag(Object[] tag){
        return (Boolean) tag[1];
    }
    public static void setNewTag(Object[] tag, boolean isNewTag){
        tag[1] = isNewTag;
    }

}
