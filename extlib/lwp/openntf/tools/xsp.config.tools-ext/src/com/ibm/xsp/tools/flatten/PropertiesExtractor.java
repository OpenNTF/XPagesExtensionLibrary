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
* Date: 18 Sep 2006
* PropertiesExtractor.java
*/
package com.ibm.xsp.tools.flatten;


import java.util.*;

import org.w3c.dom.*;

import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.FileConstants;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 18 Sep 2006
 */
public class PropertiesExtractor {
    
    private static final boolean COMMENT_PROPERTIES_FILES = true;
    private static final boolean LOCALIZE_RENDERERS_AND_CONTENTS = false;
    public static String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
    private static final String DESCR_SUFFIX = "descr";
    private static final String NAME_SUFFIX = "name";
    private static final String[] TWO_STRINGS = new String[2];
    private static final Element[] TWO_ELEMENTS = new Element[2];
    public static final String[] MATCH_LOCALIZABLE = new String[]{
                        "display-name", "description"};
    
    /**
     * Does 2 things: <br>
     * 1) makes a multi-line String of .properties file contents containing the
     * labels in the Document. <br>
     * 2) optionally, if useKeysInDocument, changes the document to replace the
     * labels with references to the .properties file keys
     * 
     * @param document
     * @return
     */
    public String changeDocument(Document document, boolean useKeysInDocument, boolean printWarnings){
        List<LocalizableLocation> locations = new ArrayList<LocalizableLocation>();
        
        addLocationsToList(printWarnings, document, locations );
        
        qualifyDuplicatePrefixes(locations);
        
        if( useKeysInDocument ){
            writePropertiesKeysToDocument(locations, document);
        }
        
        List<String> contentsList = toPropertiesFileLines(printWarnings, locations);
        
        String fileContents = toPropertiesFileString(contentsList);
        
        return fileContents;
    }
    private void writePropertiesKeysToDocument(List<LocalizableLocation> locations, Document document) {
        for (LocalizableLocation loc : locations) {
            
            String prefix = loc.suggestedPrefix;
            if( null != loc.displayName ){
                String key = "%"+prefix +NAME_SUFFIX+"%";
                Element elem = loc.getDisplayNameElem();
                writeKeyToElement(document, key, elem);
            }
            if( null != loc.description ){
                String key = "%"+prefix +DESCR_SUFFIX+"%";
                Element elem = loc.getDescriptionElem();
                writeKeyToElement(document, key, elem);
            }
        }
    }
    private void writeKeyToElement(Document document, String key, Element elem) {
        // remove existing children
        NodeList kids = elem.getChildNodes();
        while( kids.getLength() > 0 ){
            Node child = kids.item(0);
            
            elem.removeChild(child);
        }
        
        // add new child
        Text newChild = document.createTextNode(key);
        elem.appendChild(newChild);
    }
    
    private List<String> toPropertiesFileLines(boolean printWarnings, List<LocalizableLocation> locations) {
        
        // build all lines into a list, ignoring duplicates
        List<String> fileContents = new ArrayList<String>(locations.size() * 2);
        for (LocalizableLocation loc : locations) {
            
            String key = loc.suggestedPrefix;
            if( null != loc.displayName ){
                String message = loc.displayName;
                addUniqueLine(printWarnings, fileContents, key, NAME_SUFFIX,
                        message, loc.displayNameComment);
            }
            if( null != loc.description ){
                String message = loc.description;
                
                checkDescrLength(printWarnings, loc, message);
                addUniqueLine(printWarnings, fileContents, key, DESCR_SUFFIX,
                        message, loc.descriptionComment);
            }
        }
        return fileContents;
    }
    private String toPropertiesFileString(List<String> fileContents) {
        // now that duplicates have been removed, convert the list to a string
        StringBuffer contents = new StringBuffer();
        for (String line : fileContents) {
            contents.append(line).append(NEWLINE);
        }
        return contents.toString();
    }

    private void addUniqueLine(boolean printWarnings, List<String> fileContents,
            String key, String suffix, String message, String comment) {
        
        String nameKey = key + suffix;
        message = getAsResourceString(printWarnings, nameKey, message);
        String line = nameKey + "= " + message;
        if( ! fileContents.contains(line) ){
            if( null != comment ){
                fileContents.add(comment);
            }
            fileContents.add(line);
        }
    }
    /**
     * For all duplicate attribute. and property. prefixes
     * append the prefix for the corresponding definition. 
     */
    private void qualifyDuplicatePrefixes(List<LocalizableLocation> locations) {

        // sort by prefix
        locations = new ArrayList<LocalizableLocation>(locations);
        Collections.sort(locations);
        // null used as a marker for the last in the list
        locations.add(null);
        
        
        String lastPrefix = null;
        LocalizableLocation lastLoc = null;
        List<LocalizableLocation> locsWithCurrentPrefix = new ArrayList<LocalizableLocation>();
        Map<String, String> descrAndNameToPrefix = new HashMap<String, String>();
        for (LocalizableLocation loc : locations) {
            if(null != loc && loc.suggestedPrefix.equals(lastPrefix) ){
                
                if( locsWithCurrentPrefix.size() == 0 ){
                    // found the first duplicate
                    locsWithCurrentPrefix.add(lastLoc);
                }
                // found another duplicate
                locsWithCurrentPrefix.add(loc);
                
            }else if( locsWithCurrentPrefix.size() != 0){
                // gone past the last duplicate
                qualifyDuplicatePrefixSet(locsWithCurrentPrefix, descrAndNameToPrefix);
            } else if( null != lastLoc ){
                // only 1 element with that short prefix
                if( null != lastLoc.readPrefix ){
                    lastLoc.suggestedPrefix = lastLoc.readPrefix;
                }
            }
            if( null != loc ){
                lastPrefix = loc.suggestedPrefix;
                lastLoc = loc;
            }
        }
        
        if( locsWithCurrentPrefix.size() != 0 ){
            qualifyDuplicatePrefixSet(locsWithCurrentPrefix, descrAndNameToPrefix);
        }
    }

    private void qualifyDuplicatePrefixSet(List<LocalizableLocation> locsWithCurrentPrefix, Map<String, String> descrAndNameToPrefix) {
        
        // if any key read from config file for each (display-name, description)
        // that prefix gets used instead of the full prefix.
        for (LocalizableLocation dupe : locsWithCurrentPrefix) {
            if( null != dupe.readPrefix ){
                String key = dupe.displayName + "#"+dupe.description;
                descrAndNameToPrefix.put(key, dupe.readPrefix);
            }
        }
        
        for (LocalizableLocation dupe : locsWithCurrentPrefix) {
            
            // find an existing fullPrefix for this name/description pair
            String key = dupe.displayName + "#"+dupe.description;
            String readOrFullPrefix = descrAndNameToPrefix.get(key);
            if( null == readOrFullPrefix ){
                readOrFullPrefix = computeFullPrefix(dupe);
                if( descrAndNameToPrefix.containsValue(readOrFullPrefix) ){
                    // not allow duplicate full prefixes for different messages
                    throw new RuntimeException("More than one property or attribute with full prefix "+readOrFullPrefix);
                }
                descrAndNameToPrefix.put(key, readOrFullPrefix);
            }
            dupe.suggestedPrefix = readOrFullPrefix;
        }
        
        descrAndNameToPrefix.clear();
        locsWithCurrentPrefix.clear();
    }
    private String computeFullPrefix(LocalizableLocation loc) {
        String elementType = loc.defRoot.getNodeName();
        if (!(FileConstants.ATTRIBUTE.equals(elementType) || FileConstants.PROPERTY
                .equals(elementType))) {
            
            throw new RuntimeException("Duplicate prefix ("
                    + loc.suggestedPrefix + ") for an " + elementType
                    + " element.");
        }
        Element defElem = null;
        Node ancestor = loc.defRoot.getParentNode();
        while( Node.ELEMENT_NODE != ancestor.getNodeType() ){
            ancestor = ancestor.getParentNode();
        }
        if( ancestor instanceof Element ){
            defElem = (Element) ancestor;
        }else{
            throw new RuntimeException();
        }
        
        String containerPrefix = computeSuggestedPrefix( defElem );
        String fullPrefix = loc.suggestedPrefix+containerPrefix;
        return fullPrefix;
    }
    private String getAsResourceString(boolean printWarnings, String key, String localizableVal) {
        boolean startsWithWhitespace = localizableVal.length() < 1 ? false
                : Character.isWhitespace(localizableVal.charAt(0));
        if( startsWithWhitespace && '\n' != localizableVal.charAt(0) ){
            // put in a backslash to escape the whitespace
            // else it won't appear at the start of the loaded string
            localizableVal = "\\"+ localizableVal; //$NON-NLS-1$
        }
        checkMessageNewlines(printWarnings, key, localizableVal);
        localizableVal = localizableVal.replaceAll("\n", "\\\\n\\\\"+NEWLINE);
        return localizableVal;
    }

    private void addLocationsToList(boolean printWarnings, Document doc, List<LocalizableLocation> locs) {
        
        Element root = doc.getDocumentElement();
        
        // faces-config
        LocalizableLocation loc = getLocalizableInExtension(root,
                FileConstants.FACES_CONFIG_EXTENSION);
        if( null != loc ){
            loc.suggestedPrefix = root.getNodeName()+".";
            if( !loc.isDescriptionAlreadyBundleRef || !loc.isDisplayNameAlreadyBundleRef ){
                addToLocs(locs, loc);
            }
        }
        
        for (Element i : ElementUtil.getChildren(root)) {
            String elemName = i.getNodeName();
            
            if( FileConstants.COMPONENT.equals(elemName) ){
                addLocInRoot(printWarnings, locs, i);
                addPropertyAndAttributeLocationsToList(printWarnings, i, locs, false);
                // TODO handle facets also
                continue;
            }
            if( FileConstants.COMPLEX_TYPE.equals(elemName) ){
                addLocInRoot(printWarnings, locs, i);
                addPropertyAndAttributeLocationsToList(printWarnings, i, locs, false);
                continue;
            }
            if( FileConstants.CONVERTER.equals(elemName) ){
                addLocInRoot(printWarnings, locs, i);
                addPropertyAndAttributeLocationsToList(printWarnings, i, locs, false);
                continue;
            }
            if( FileConstants.VALIDATOR.equals(elemName) ){
                addLocInRoot(printWarnings, locs, i);
                addPropertyAndAttributeLocationsToList(printWarnings, i, locs, false);
                continue;
            }
            if( FileConstants.RENDER_KIT.equals(elemName) ){
                
                String kitSuggestedPrefix = computeSuggestedPrefix(i);
                String renderKitId = getRenderKitId(kitSuggestedPrefix);
                
                // get localizable
                loc = getLocalizableInRoot(i);
                if( null != loc ){
                    loc.suggestedPrefix = kitSuggestedPrefix;
                    addToLocs(locs, loc);
                }
                checkPresent(printWarnings, i, loc);                
                
                // for all renderers
                for (Element renderer : ElementUtil.getChildren(i, FileConstants.RENDERER)) {

                    if( LOCALIZE_RENDERERS_AND_CONTENTS ){
                        // get localizable
                        loc = getLocalizableInRoot(renderer);
                        if( null != loc ){                    
                            loc.suggestedPrefix = computeSuggestedPrefix(renderer, renderKitId);
                            addToLocs(locs, loc);
                        }
                        checkPresent(printWarnings, renderer, loc);                
                    }
                    
                    addPropertyAndAttributeLocationsToList(printWarnings, renderer, locs, true);
                }
                
                continue;
            } 
            if( FileConstants.GROUP.equals(elemName) ){
                
                // <group-type>com.ibm.xsp.foo</group-type>
                addPropertyAndAttributeLocationsToList(printWarnings, i, locs, false);
                // TODO handle facets also
                continue;
            }
            if( FileConstants.FACES_CONFIG_EXTENSION.equals(elemName) ){
                // do nothing
                continue;
            }
            throw new RuntimeException(elemName);
            // TODO (mkehoe) handle property-type and composite-component
//            if( FileConstants.COMPOSITE_COMPONENT.equals(elemName) ){
//                continue;
//            } 
//            if( FileConstants.PROPERTY_TYPE.equals(elemName) ){
//                // TODO (mkehoe) handle property-types
//                continue;
//            } 
        }
    }
    private void addLocInRoot(boolean printWarnings, List<LocalizableLocation> locs, Element defRoot) {
        LocalizableLocation loc = getLocalizableInRoot(defRoot);
        if( null != loc ){                    
            loc.suggestedPrefix = computeSuggestedPrefix(defRoot);
            if( !loc.isDescriptionAlreadyBundleRef || !loc.isDisplayNameAlreadyBundleRef ){
                addToLocs(locs, loc);
            }
        }
        checkPresent(printWarnings, defRoot, loc);
    }

    private void checkPresent(boolean printWarnings, Element defRoot, LocalizableLocation loc) {
        if( ! printWarnings ){
            return;
        }
        String suggestedPrefix = (null == loc)? computeSuggestedPrefix(defRoot) : loc.suggestedPrefix;
        
        if( null == loc || null == loc.description && null == loc.displayName ){
            if( ! suggestedPrefix.startsWith("render-kit.html.")){
                System.err.println("PropertiesExtractor.checkPresent() "
                        + "description and display-name missing for " + suggestedPrefix);
            }
            // else don't worry about missing HTML_BASIC descr/name
            return;
        }
        if( null == loc.description ){
            if( ! LOCALIZE_RENDERERS_AND_CONTENTS && suggestedPrefix.startsWith("render-kit.")){
                // not localize description for render-kits
            }else{
                System.err.println("PropertiesExtractor.checkPresent() "
                    + "description missing for " + suggestedPrefix
                    + " display-name=" + loc.displayName);
            }
        }
        if( null == loc.displayName ){
            System.err.println("PropertiesExtractor.checkPresent() "
                    + "display-name missing for " + suggestedPrefix
                    + " description=" + loc.description);
        }
        if( loc.isDescriptionAlreadyBundleRef && loc.isDisplayNameAlreadyBundleRef ){
//            System.out.println("PropertiesExtractor.checkPresent() " +
//                    "Using existing bundle refs: " +loc.displayName+
//                    " "+loc.description);
        }else if( loc.isDescriptionAlreadyBundleRef || loc.isDisplayNameAlreadyBundleRef ){
            System.err.println("PropertiesExtractor.checkPresent()"
                            + "Expect both descr and name to be bundle refs, this util cannot handle name:"
                            + loc.displayName + "  descr: " + loc.description);
        }
    }
    private void checkDescrLength(boolean printWarnings, LocalizableLocation loc, String message) {
        if( ! printWarnings ){
            return;
        }
        int nameLen = (null == loc.displayName? 0 : loc.displayName.length()) ;
        if( message.length() < nameLen + 5 ){
            // TODO skipping "Triggered" descriptions as they're often short
            if( message.startsWith("Triggered") ){
                return;
            }
            String descrKey = loc.suggestedPrefix+DESCR_SUFFIX;
            if( "complex-type.setComponentMode.descr".equals( descrKey) ){
                // description is just "Changes the component mode."
                return;
            }
            System.err.println("PropertiesExtractor.checkDescrLength() "
                    + "description probably too short " + descrKey + "= "
                    + message.replaceAll("\n", "\\\\n"));
        }
    }
    private void checkMessageNewlines(boolean printWarnings, String key, String message) {
        if( ! printWarnings ){
            return;
        }
        if( -1 != message.indexOf('\n')){
            String toDisplay = "\n>\t"+message.replaceAll("\n", "\n>\t");
            System.err.println("PropertiesExtractor.checkMessageNewlines() "
                    + "message contains newlines for " + key + " :"
                    + toDisplay);
        }
    }
    
    private String computeSuggestedPrefix(Element e, String renderKitId) {
        String prefix = e.getNodeName()+"."+renderKitId+".";
        
        String compFamily = ElementUtil.extractValue(e,FileConstants.COMPONENT_FAMILY);
        compFamily = compFamily.substring(compFamily.lastIndexOf('.')+1);
        
        String rendererType = ElementUtil.extractValue(e,FileConstants.RENDERER_TYPE);
        rendererType = rendererType.substring(rendererType.lastIndexOf('.')+1);
        
        prefix += compFamily +"_"+ rendererType+".";
        return prefix;
    }
    private String getRenderKitId(String kitSuggestedPrefix) {
        return kitSuggestedPrefix.substring(kitSuggestedPrefix.indexOf('.')+1, kitSuggestedPrefix.length() - 1);
    }
    private String computeSuggestedPrefix(Element e) {

        String elemName = e.getNodeName();
        String prefix = elemName+".";
        
        if( FileConstants.COMPONENT.equals(elemName) ){
            String tagName = ElementUtil.extractValue(
                    ElementUtil.getExtension(e,
                            FileConstants.COMPONENT_EXTENSION),
                            FileConstants.TAG_NAME);
            if ( null == tagName ) {
                tagName = ElementUtil.extractValue(e,
                        FileConstants.COMPONENT_TYPE);
            }
            prefix += tagName;
        }
        else if( FileConstants.VALIDATOR.equals(elemName)){
            String id = ElementUtil.extractValue(e,FileConstants.VALIDATOR_ID);
            prefix += id;
        }
        else if( FileConstants.COMPLEX_TYPE.equals(elemName) ){
            String tagName = ElementUtil.extractValue(
                    ElementUtil.getExtension(e,
                            FileConstants.COMPLEX_EXTENSION),
                            FileConstants.TAG_NAME);
            if ( null == tagName ) {
                tagName = ElementUtil.extractValue(e,
                        FileConstants.COMPLEX_ID);
            }
//            String tagName = ElementUtil.extractValue(e,FileConstants.COMPLEX_NAME);
            prefix += tagName;
        }
        else if( FileConstants.CONVERTER.equals(elemName) ){
            String id = ElementUtil.extractValue(e,FileConstants.CONVERTER_ID);
            // extra .id is a left-over from when used to have converter-for-class converters
            prefix += "id."+id;
        }
        else if( FileConstants.RENDER_KIT.equals(elemName) ){
            // calculate render-kit-id or hardcoded alias
            String renderKitId = ElementUtil.extractValue(e,FileConstants.RENDER_KIT_ID);
            if ( null == renderKitId 
                    || "HTML_BASIC".equals(renderKitId)) {
                renderKitId = "html";
            }
            prefix += renderKitId;
        }
        else if( FileConstants.RENDERER.equals(elemName) ){
            String kitSuggestedPrefix = computeSuggestedPrefix((Element)e.getParentNode());
            String renderKitId = getRenderKitId(kitSuggestedPrefix);
            return computeSuggestedPrefix(e, renderKitId);
        }
        else if( FileConstants.PROPERTY.equals(elemName) ){
            String propName = ElementUtil.extractValue(e, FileConstants.PROPERTY_NAME);
            prefix += propName;
        }
        else if( FileConstants.ATTRIBUTE.equals(elemName) ){
            String propName = ElementUtil.extractValue(e, FileConstants.ATTRIBUTE_NAME);
            prefix += propName;
        }
        else if( FileConstants.GROUP.equals(elemName) ){
            String keySuffix = getKeySuffix(e);
            if( null == keySuffix ){
                String msg = "No .properties key prefix for the <group> ";
                msg += ElementUtil.extractValue(e, FileConstants.GROUP_TYPE);
                msg += ". Please provide a key-suffix for the group like so: " 
                    +"<!-- key-suffix: events --> " 
                    +"Or provide an explicit key comment for each property, " 
                    +"like so: <!-- key: property.something. -->";
                throw new RuntimeException(msg );
            }
            prefix += keySuffix;
        }
        else{
            throw new RuntimeException(elemName);
        }
        prefix += ".";
        if( -1 != prefix.indexOf("null") ){
            throw new RuntimeException("Bad key prefix "+prefix);
        }
        return prefix;
    }
    private void addToLocs(List<LocalizableLocation> locs, LocalizableLocation loc) {
        if( ! locs.contains(loc) ){
            locs.add(loc);
        }
    }

    private void addPropertyAndAttributeLocationsToList(boolean printWarnings, Element defRoot, List<LocalizableLocation> locs, boolean isRendererContents) {
        LocalizableLocation loc;
        
        for (Element elem : ElementUtil.getChildren(defRoot)) {
            String elemName = elem.getNodeName();
            boolean isProp = FileConstants.PROPERTY.equals(elemName);
            if( !isProp && !FileConstants.ATTRIBUTE.equals(elemName)){
                continue;
            }
            if( isRendererContents && !LOCALIZE_RENDERERS_AND_CONTENTS ){
                // don't warn about missing renderer attribute descr/names
                continue;
            }
            // get localizable
            loc = getLocalizableInRoot(elem);
            if( null != loc ){
                String propName = ElementUtil.extractValue(elem,
                        (isProp ? FileConstants.PROPERTY_NAME
                                : FileConstants.ATTRIBUTE_NAME));
                loc.suggestedPrefix = elemName + "."+ propName+".";
                if( !loc.isDescriptionAlreadyBundleRef || !loc.isDisplayNameAlreadyBundleRef ){
                    addToLocs(locs, loc);
                }
            }
            checkPresent(printWarnings, elem, loc);
        }
    }
    private LocalizableLocation getLocalizableInRoot(Element root) {
        Iterable<Element> children = ElementUtil.getChildren(root);
        extractTagLessValues(children, MATCH_LOCALIZABLE, TWO_STRINGS, TWO_ELEMENTS);
        LocalizableLocation loc = createNullOrLocation(root, TWO_STRINGS, TWO_ELEMENTS);
        return loc;
    }
    private LocalizableLocation getLocalizableInExtension(Element root, String extensionName) {
        Iterable<Element> configExt = ElementUtil.getExtensions(root, extensionName);
        extractTagLessValues(configExt, MATCH_LOCALIZABLE, TWO_STRINGS, TWO_ELEMENTS);
        LocalizableLocation loc = createNullOrLocation(root, TWO_STRINGS, TWO_ELEMENTS);
        return loc;
    }
    private LocalizableLocation createNullOrLocation(Element root, String[] values, Element[] elements) {
        values[0] = getTrimmed(values[0]);
        values[1] = getTrimmed(values[1]);
        if( values[0] != null || values[1] != null){
            
            LocalizableLocation loc = new LocalizableLocation(root, values[0],
                    elements[0], values[1], elements[1]);
            return loc;
        }
        return null;
    }
    private String getTrimmed(String value) {
        if( value != null ){
            value = value.trim();
            if( 0 == value.length() ){
                value = null;
            }
        }
        return value;
    }
    
    private static void extractTagLessValues(Iterable<Element> parent,
            String[] tagNames, String[] contents, Element [] sources){
        
        // mostly copied from ElementUtil.extractValues
        final int numArgs = tagNames.length;
        Arrays.fill(contents, 0, numArgs, null);
        Arrays.fill(sources, 0, numArgs, null);
        for (Element i : parent) {
            String tagName = i.getTagName();
            for (int j = 0; j < tagNames.length; j++) {
                if (tagName.equals(tagNames[j])) {
                    contents[j] = getElementContentsAsTagLessString(i);
                    if( contents[j].length() == 0){
                        contents[j] = null;
                    }else{
                        sources[j] = i;
                    }
                }
            }
        }
        return;
    }
    /**
     * Note this method is also in BaseDesignerExtensionFactory
     */
    private static String getElementContentsAsTagLessString(Element element) {
        StringBuffer buffer = new StringBuffer();
        appendChildren(element, buffer);   
        return buffer.toString();
    }
    private static void appendChildren(Element element, StringBuffer buffer) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            
            if( child instanceof CDATASection || child instanceof Text ){
                buffer.append( ((CharacterData)child).getData() );
            }
            if( child instanceof Element ){
                appendChildren((Element)child, buffer);
            }
        }
    }
    
    private static String getComment(Element elem){
        
        if( COMMENT_PROPERTIES_FILES && null != elem ){
            Node previous = elem.getPreviousSibling();
            previous = toBeforeWhitespace(previous);
            if( previous instanceof Comment ){
                String value = ((Comment)previous).getData().trim();
                if( value.startsWith("#") ){
                    return value;
                }
            }
        }
        return null;
    }
    private static Node toBeforeWhitespace(Node previous) {
        while( previous instanceof Text ){
            if( ((Text)previous).getData().trim().length() != 0 ){
                break;
            }
            previous = previous.getPreviousSibling();
        }
        return previous;
    }
    private static String getKeyPrefix(Element descr){
        
        //<property>
        //    <!-- key: property.style. -->
        //    <!-- # 'style' should not be translated -->
        //    <description>A style to set</description>
        
        if( null != descr ){
            boolean allowPropsComment = true;
            // for all comments before the description (moving backwards)
            for (Node previous = toBeforeWhitespace(descr.getPreviousSibling()); 
                    previous instanceof Comment;
                    previous = toBeforeWhitespace(previous.getPreviousSibling())) {
                
                    String value = ((Comment)previous).getData().trim();
                    if( value.startsWith("key:") ){
                        String prefix = value.substring("key:".length());
                        prefix = prefix.trim();
                        if( 0 == prefix.length() ){
                            // key was empty
                            return null;
                        }
                        return prefix;
                    }else if( allowPropsComment && value.startsWith("#") ){
                        // not break
                        allowPropsComment = false;
                        continue;
                    }
            }
        }
        return null;
    }
    private static String getKeySuffix(Element group){
        
        //<group>
        //    <!-- some comment -->
        //    <!-- key-suffix: events -->
        //    <group-type>com.ibm.xsp.group.events</description>
        
        Node child = group.getFirstChild();
        if( null != child ){
            for(Node i = child; i != null; i = i.getNextSibling() ){
                if( i instanceof Text && 0 == ((Text)i).getData().trim().length() ){
                    // skip whitespace
                    continue;
                }
                if( i instanceof Comment ){
                    String value = ((Comment)i).getData().trim();
                    if( value.startsWith("key-suffix:") ){
                        String prefix = value.substring("key-suffix:".length());
                        prefix = prefix.trim();
                        if( 0 == prefix.length() ){
                            // key was empty
                            return null;
                        }
                        return prefix;
                    }
                    // ignore comment, not break.
                    continue;
                }
                // not a comment, so break
                break;
            }
        }
        return null;
    }
    private static boolean isBundleRef(String displayName) {
        return null != displayName && displayName.startsWith("%");
    }
    private static class LocalizableLocation implements Comparable<LocalizableLocation>{
        private String suggestedPrefix;
        public String readPrefix;
        public final Element defRoot;
        
        public final String description;
        private Element descriptionElem;
        public String descriptionComment;
        public boolean isDescriptionAlreadyBundleRef;
        
        public final String displayName;
        public Element displayNameElem;
        public String displayNameComment;
        public boolean isDisplayNameAlreadyBundleRef;
        
        public LocalizableLocation(final Element defRoot,
                final String displayName, final Element displayNameElem,
                final String description, final Element descriptionElem) {
            super();
            this.defRoot = defRoot;
            this.displayName = displayName;
            setDisplayNameElem(displayNameElem);
            this.isDisplayNameAlreadyBundleRef = isBundleRef(displayName);
            this.description = description;
            setDescriptionElem(descriptionElem);
            this.isDescriptionAlreadyBundleRef = isBundleRef(displayName);
            readPrefix = getKeyPrefix(descriptionElem);
        }
        public Element getDescriptionElem() {
            return descriptionElem;
        }
        public void setDescriptionElem(Element descriptionElem) {
            this.descriptionElem = descriptionElem;
            this.descriptionComment = getComment(descriptionElem);
        }
        public Element getDisplayNameElem() {
            return displayNameElem;
        }
        public void setDisplayNameElem(Element displayNameElem) {
            this.displayNameElem = displayNameElem;
            this.displayNameComment = getComment(displayNameElem);
        }

        public int compareTo(LocalizableLocation other) {
            if( null == suggestedPrefix || null == other.suggestedPrefix ){
                // the suggestedPrefix should be set immediately after the
                // LocalizableLocation is created, before this method is called.
                throw new RuntimeException("One of these is null: ("
                        + suggestedPrefix + ") or (" + other.suggestedPrefix
                        + ")");
            }
            int compare = suggestedPrefix.compareTo(other.suggestedPrefix);
            if( 0 == compare ){
                if( (null == displayName) != (null == other.displayName)){
                    return null == displayName? -1 : 1;
                }
                if( null != displayName ){
                    compare = displayName.compareTo(other.displayName);
                }
            }
            if( 0 == compare ){
                if( (null == description) != (null == other.description)){
                    return null == description? -1 : 1;
                }
                if( null != description ){
                    compare = description.compareTo(other.description);
                }
            }
            if( 0 == compare ){
                compare = toString().compareTo(other.toString());
            }
            return compare;
        }
        public String toString(){
            return suggestedPrefix;
        }
    }

}
