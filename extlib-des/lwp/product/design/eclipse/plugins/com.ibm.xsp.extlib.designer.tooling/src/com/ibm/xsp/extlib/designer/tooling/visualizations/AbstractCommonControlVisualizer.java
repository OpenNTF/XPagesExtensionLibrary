/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.visualizations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Path;
import org.w3c.dom.Node;

import com.ibm.commons.swt.util.ComputedValueUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesRegistry;

public abstract class AbstractCommonControlVisualizer extends AbstractVisualizationFactory{

    public static final boolean DEBUG = false;
    
    //Defined prefixes
    public static final String XP_PREFIX = "xp"; // $NON-NLS-1$
    public static final String XE_PREFIX = "xe"; // $NON-NLS-1$
    public static final String XC_PREFIX = "xc"; // $NON-NLS-1$
    
    //Defined namespaces
    public static final String XP_CORE_NAMESPACE = "http://www.ibm.com/xsp/core"; // $NON-NLS-1$
    public static final String XE_EXTLIB_NAMESPACE = IExtLibTagLib.EXT_LIB_NAMESPACE_URI; // $NON-NLS-1$
    public static final String XC_CUSTOM_CONTROLS_NAMESPACE = "http://www.ibm.com/xsp/custom"; // $NON-NLS-1$
    
    //Static Strings we use when creating the visualizations
    public static final String XML_ENCODING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // $NON-NLS-1$
    public static final String LINE_DELIMITER = "\r\n"; // $NON-NLS-1$
    public static final String START_SCRIPLET_TAG = "<%";
    public static final String START_SCRIPLET_VALUE_TAG = "<%=";
    public static final String END_SCRIPLET_TAG = "%>";
    public static final String NAMESPACE_DEFINITION_PREFIX = "xmlns:"; // $NON-NLS-1$
    
    //attribute values that were not specified in XSPAttributeNames.
    public static final String XSP_ATTR_VAL_NUMBER = "number"; // $NON-NLS-1$
    public static final String XSP_ATTR_VAL_RTL = "rtl"; // $NON-NLS-1$
    public static final String XLIB_ATTR_TITLE = "title"; // $NON-NLS-1$
    public static final String XLIB_ATTR_LABEL = "label"; // $NON-NLS-1$
    
    //static images specific info 
    public static final String IMAGES_LOCATION = Path.SEPARATOR + "extlib" + Path.SEPARATOR + "designer" + Path.SEPARATOR + "markup"; // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
    public static final String DOJO_FORM_IMAGES_LOCATION = "dojoform"; // $NON-NLS-1$
    public static final String DATA_ACCESS_IMAGES_LOCATION = "dataaccess"; // $NON-NLS-1$
    public static final String EXTENSION_LIBRARY_IMAGES_LOCATION = "extensionlibrary"; // $NON-NLS-1$
    public static final String INOTES_IMAGES_LOCATION = "inotes"; // $NON-NLS-1$
    public static final String MOBILE_IMAGES_LOCATION = "mobile"; // $NON-NLS-1$
    private static final String LIB_URL_START_STRING = "xsp://"; // $NON-NLS-1$
    private static final String LIB_URL_END_STRING = "~~";
    
    //top level tag view tag that we create for any visualization
    private XPageViewTag _xPageViewTag = new XPageViewTag();
    
    //This is a cache of the markup for the given control. If the control is static the markup will not be
    //regenerated every time the node is updated.
    private String _cachedMarkup;
    
    /**
     * Returns the markup for the XML Declaration and opening view tag for the visualization.
     * @return
     */
    protected String getXPageHeader(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(XML_ENCODING);
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(_xPageViewTag.toString());
        return strBuilder.toString();
    }
    
    /**
     * Helper method to allow a user to add attributes to the xp:view tag of the visualization.
     * @param attributeName
     * @param value
     */
    protected void addAttributeToHeader(String attributeName, String value){
        if(null != _xPageViewTag){
            _xPageViewTag.addAttribute(attributeName, value);
        }
    }
    
    /**
     * Helper method to add xmlns:xe="http://www.ibm.com/xsp/coreex" to the view tag
     */
    protected void addXEPrefixToHeader(){
        if(null != _xPageViewTag){
            _xPageViewTag.addPrefix(XE_PREFIX, XP_CORE_NAMESPACE);
        }
    }
    
    /**
     * Helper method to add xmlns:xc="http://www.ibm.com/xsp/custom" to the view tag
     */
    protected void addXCPrefixToHeader(){
        if(null != _xPageViewTag){
            _xPageViewTag.addPrefix(XC_PREFIX, XC_CUSTOM_CONTROLS_NAMESPACE);
        }
    }
    
    /**
     * Helper method to add xmlns:<prefix>="<namseSpaceURI" to the view tag
     */
    protected void addPrefixToHeader(String prefix, String nameSpaceURI){
        if(null != _xPageViewTag){
            _xPageViewTag.addPrefix(prefix, nameSpaceURI);
        }
    }
    
    /**
     * Create a tag object with the given prefix and tagName. 
     * @param prefix
     * @param tagName
     * @return the created Tag object
     */
    protected Tag createTag(String prefix, String tagName){
        return new Tag(prefix, tagName);
    }
    
    /**
     * Create a tag object with the given prefix and tagName. Then add all the attributes in the
     * attributes HasMap to that tag
     * @param prefix
     * @param tagName
     * @param attributes
     * @return the created Tag object with the given attributes set on it. 
     */
    protected Tag createTag(String prefix, String tagName, HashMap<String,String>attributes){
        return new Tag(prefix, tagName, attributes);
    }
    
    /**
     * Create the markup for an opening tag given a prefix and tagName
     * @param prefix
     * @param tagName
     * @return the string representing an opening tag in the form <prefix:tagName>
     */
    protected String createStartTag(String prefix, String tagName){
        return "<" + prefix + ":" + tagName + ">";
    }
    
    /**
     * Create the markup for a closing tag given a prefix and tagName
     * @param prefix
     * @param tagName
     * @return the string representing a closing tag in the form </prefix:tagName>
     */
    protected String createEndTag(String prefix, String tagName){
        return "</" + prefix + ":" + tagName + ">";
    }
    
    /**
     * Create the markup for an opening tag given a prefix, tagName and Map of attributes
     * @param prefix
     * @param tagName
     * @param attributes
     * @return the string representing an opening tag in the form <prefix:tagName attributeA="valueA" attributeB="valueB"...>
     */
    protected String createStartTag(String prefix, String tagName, HashMap<String,String>attributes){
        HashMap<String,String>complexAttributes = new HashMap<String,String>();
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<" + prefix + ":" + tagName);
        if(null != attributes && !attributes.isEmpty()){
            //sort the attributes map, so that they are always added in alphabetical order
            Set<String> attributeNameKeys = attributes.keySet();
            TreeSet<String> attributeNames = new TreeSet<String>(attributeNameKeys);
            Iterator<String> attrNamesIter = attributeNames.iterator();
            while(attrNamesIter.hasNext()){
                String attributeName = attrNamesIter.next();
                String attributeValue = attributes.get(attributeName);
                if(StringUtil.isNotEmpty(attributeName) && StringUtil.isNotEmpty(attributeValue)){
                    if(ComputedValueUtils.isStringComputed(attributeValue)||isJSVarAttributeBinding(attributeValue)){
                        complexAttributes.put(attributeName,attributeValue);
                    }
                    else{
                        strBuilder.append(" " + attributeName + "=\"" + attributeValue + "\"");
                    }
                }
            }
        }
        strBuilder.append(">");
        //add the complex attributes
        if(!complexAttributes.isEmpty()){
            Set<String> complexAttributeNameKeys = complexAttributes.keySet();
            TreeSet<String> complexAttributeNames = new TreeSet<String>(complexAttributeNameKeys);
            Iterator<String> complexAttrNamesIter = complexAttributeNames.iterator();
            while(complexAttrNamesIter.hasNext()){
                String complexAttributeName = complexAttrNamesIter.next();
                String complexAttributeValue = attributes.get(complexAttributeName);
                if(StringUtil.isNotEmpty(complexAttributeName) && StringUtil.isNotEmpty(complexAttributeValue)){
                    strBuilder.append(LINE_DELIMITER);
                    strBuilder.append("<" + prefix + ":this." + complexAttributeName + ">"); // $NON-NLS-1$
                    strBuilder.append(complexAttributeValue);
                    strBuilder.append("</" + prefix + ":this."+ complexAttributeName + ">"); // $NON-NLS-1$
                }
            }
        }
        return strBuilder.toString();
    }
    
    
    /**
     * Simple implementation of a tag. Allows you to create a tag, add attributes and child tags to it without having to worry
     * about start and end tags. 
     * 
     * If you create a tag and add multiple child tags, you only need to call toString on the parent tag and it will return the
     * xsp markup of the parent and all its children. 
     */
    protected class Tag{
        
        //map to hold any attributes set on the tag
        private HashMap<String, String> _attributesMap = new HashMap<String, String>();
        //List of all the child Tags added to this tag
        private ArrayList<Tag> _childTags = new ArrayList<Tag>();
        //the prefix for the tag
        private String _prefix;
        //The tagName for the tag
        private String _tagName;
        
        /**
         * Simple constructor to create a Tag with a given prefix and tagName
         * @param prefix
         * @param tagName
         */
        public Tag(String prefix, String tagName){
            _prefix = prefix;
            _tagName = tagName;
        }
        
        /**
         * Simple constructor to create a Tag with a given prefix and tagName, with the provided attributes set on it.
         * @param prefix
         * @param tagName
         * @param attributes
         */
        public Tag(String prefix, String tagName, HashMap<String, String> attributes){
            _prefix = prefix;
            _tagName = tagName;
            _attributesMap = attributes;
        }
        
        /**
         * Add a given Tag object as a child of this Tag Object
         * @param tag
         */
        public void addChildTag(Tag tag){
            _childTags.add(tag);
        }
        
        /**
         * This method will add a child Tag which just contains text. It is basically a way to add
         * plain text as a child of a Tag. The text will not be wrapped with special tags that define
         * it as being text. 
         * @param text
         */
        public void addTextChild(String text){
            TextTag textTag = new TextTag(text);
            _childTags.add(textTag);
        }
        
        /**
         * 
         * This method will create markup like this <%=jsVarToOutput%> as a child of this tag.
         * @param jsVarToOutput
         */
        public void addJSVarTextChild(String jsVarToOutput){
            TextTag textTag = new TextTag(START_SCRIPLET_VALUE_TAG + jsVarToOutput + END_SCRIPLET_TAG);
            _childTags.add(textTag);
        }
        
        /**
         * Takes a given Tag object and returns whether or not that Tag is already a child of this Tag. 
         * @param tag
         * @return
         */
        public boolean hasChildTag(Tag tag){
            return _childTags.contains(tag);
        }
        
        /**
         * Add an attribute with the given attributeName and value to this Tag
         * @param attributeName
         * @param value
         */
        public void addAttribute(String attributeName, String value){
            _attributesMap.put(attributeName, value);
        }
        
        /**
         * The get value specified for the given attributeName for this Tag. 
         * @param attributeName
         * @return
         */
        public String getAttributeValue(String attributeName){
            return _attributesMap.get(attributeName);
        }
        
        /**
         * This method takes in an attribute name and javaScript variable name. 
         * It then set the attribute to be bound to the value of the javaScrtipt variable name
         * So it will create markup like this <prefix:tagName attributeName="<%=jsVarToSetAttributeTo%>">...
         * @param attributeName
         * @param jsVarToSetAttributeTo
         */
        public void addJSVarAttributeBinding(String attributeName, String jsVarToSetAttributeTo){
            _attributesMap.put(attributeName, START_SCRIPLET_VALUE_TAG + jsVarToSetAttributeTo + END_SCRIPLET_TAG);
        }
        
        /**
         * Returns whether or not this Tag has child Tags added to it. 
         * @return
         */
        public boolean hasChildren(){
            return _childTags.size()>0;
        }
        
        /**
         * Returns all the child Tags that have been added to this Tag. 
         * @return a Tag[] of child Tags.
         */
        public Tag[] getChildTags(){
            return _childTags.toArray(new Tag[_childTags.size()]);
        }
        
        /**
         * Returns a HashMap containing all the attributes and their values that have been set on this Tag
         * @return
         */
        public HashMap<String, String> getAttributes(){
            return _attributesMap;
        }
        
        /**
         * Returns the prefix for this Tag
         * @return
         */
        public String getPrefix(){
            return _prefix;
        }
        
        /**
         * Returns the TagName for this Tag.
         * @return
         */
        public String getTagName(){
            return _tagName;
        }
        
        /**
         * This method will return the xsp markup for this tag and all of its child tags. 
         * @return the complete xsp source markup for this tag, it's attributes and all of its chid tags. 
         */
        public String toString(){
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(createStartTag(_prefix, _tagName, _attributesMap));
            if(hasChildren()){
                //strBuilder.append(LINE_DELIMITER);
                Tag[] children = getChildTags();
                for(int i=0; i<children.length; i++){
                    Tag child = children[i];
                    strBuilder.append(child.toString());
                }
            }
            //strBuilder.append(LINE_DELIMITER);
            strBuilder.append(createEndTag(_prefix,_tagName));
            //strBuilder.append(LINE_DELIMITER);
            return strBuilder.toString();
        }
    }
    
    /**
     * Special Tag type to represent the view tag in a visualization.
     * Just gives an easier way of adding namespace declarations to the view tag.  
     */
    private class XPageViewTag extends Tag{
        
        /**
         * Just call up to Tag to create a new view tag, and add the standard XP prefix to it.
         */
        protected XPageViewTag() {
            super(XP_PREFIX, XSPTagNames.XSP_TAG_VIEW);
            addPrefix(XP_PREFIX, XP_CORE_NAMESPACE);
        }
        
        /**
         * Add a namespace declaration to the view tag. We really just set an "xmlns:<prefix>" attribute
         * on the Tag with its value set to <nameSpaceURI>
         * @param prefix
         * @param nameSpaceURI
         */
        public void addPrefix(String prefix, String nameSpaceURI){
            super.addAttribute(NAMESPACE_DEFINITION_PREFIX + prefix, nameSpaceURI);
        }
        
        /**
         * Override parent behavior to add custom view tag behavior. We don't want to generate the 
         * closing tag and we don't need to worry about child tags.The view tag is added to the page
         * independently of any markup created by a visualization class.  
         * @return  
         */
        public String toString(){
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(createStartTag(getPrefix(), getTagName(), getAttributes()));
            //strBuilder.append(LINE_DELIMITER);
            return strBuilder.toString();
        }
    }
    
    /**
     * Special representation of a TextTag. This is used to add plain text as a child of a Tag.
     * Tags essentially map to Nodes in a Dom. This TextNode represents a TextNode, where it doesn't
     * have a prefix or tagName so it does not generate opening and closing tags. 
     */
    private class TextTag extends Tag{
        
        private String _text;
        
        private TextTag(String text){
            //text nodes do not have prefixes or tagNames.
            super(null,null);
            _text = text;
        }
        
        public String toString(){
            return _text;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getFullXSPMarkupForControl(org.w3c.dom.Node, com.ibm.xsp.registry.FacesRegistry)
     */
    public String getFullXSPMarkupForControl(Node nodeToVisualize, FacesRegistry registry) {
        if(isStaticMarkup() && StringUtil.isNotEmpty(_cachedMarkup)){
            return _cachedMarkup;
        }
        StringBuilder strBuilder = new StringBuilder();
        //call this first so that we can add extra prefixes to the header before we add the header text. 
        String markup = getXSPMarkupForControl(nodeToVisualize, null, registry);
        strBuilder.append(getXPageHeader());
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(markup);
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(XSP_FOOTER);
        strBuilder.append(LINE_DELIMITER);
        if(DEBUG){
            System.out.println(strBuilder.toString());
        }
        _cachedMarkup = strBuilder.toString();
        return _cachedMarkup;
    }
    
    /**
     * This method is used to determine if the render-markup for visualizations are static or dynamic.
     * If a visualization updates based on its attributes or child nodes, then you must
     * override this method and return false. Otherwise the cached version will always be 
     * returned and the visualization will not update.
     * @return true if the markup is static, false otherwise.
     */
    public boolean isStaticMarkup(){
        return true;
    }
    
    /**
     * This is a helper method to generate a javascript blob that to be included in a visualization to
     * get the value of an attribute set on the element being visualized. 
     * 
     * So say this class was being used to generate a visualization for an xe:djxButton tag, and the djxButtonTag has a
     * label attribute set on it. You could use this method to add javaScript to the visualization to get the value of the
     * label attribute set on the xe:djxButton tag. 
     * 
     * You also need to specify a defaultValue. This is the value that will be returned if the attribute in question has not been
     * set.
     * 
     * The markup this method generates will look like this
     * 
     * <%
     *  var variableName=this.attributeName;
     *  if(null==variableName || variableName ==""){
     *      variableName=defaultValue;
     *  }
     * %>
     * 
     * @param attributeName
     * @param defaultValue
     * @param variableName
     * @return
     */
    protected String generateFunctionToGetAttributeValue(String attributeName, String defaultValue, String variableName){
        return generateFunctionToGetAttributeValue(attributeName, defaultValue, variableName, true);
    }
    
    protected String generateFunctionToGetAttributeValue(String attributeName, String defaultValue, String variableName, boolean escapeDefaultValue){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(START_SCRIPLET_TAG + LINE_DELIMITER);
        strBuilder.append("var " + variableName + "=this." + attributeName + ";"); // $NON-NLS-2$ $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        
        strBuilder.append("if(null=="+variableName + " || " + variableName + "==\"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        if(escapeDefaultValue){
            strBuilder.append(variableName + "=escape(\""+defaultValue+"\");"); //$NON-NLS-1$ //$NON-NLS-2$
        }else{
            strBuilder.append(variableName + "=\""+defaultValue+"\";");
        }
        
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("else {"); //$NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(variableName + "=escape(" + variableName + ");"); // $NON-NLS-2$ $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append(END_SCRIPLET_TAG);
        strBuilder.append(LINE_DELIMITER);
        return strBuilder.toString();
    }
    
    /**
     * Helper method to create an image tag in a visualization. 
     * @param imageName - the name (e.g. image.png) of the image file to add
     * @param imageSubFolder - we already assume that all images in sub folders of the folder extlib/designer/markup. 
     * So this set the subfolder of the markup folder that contains the image.
     * @return The source of the created image tag. 
     */
    protected String createImageTag(String imageName, String imageSubFolder){
        Tag imageTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_IMAGE);
        imageTag.addAttribute(XSPAttributeNames.XSP_ATTR_URL, getURLForImage(imageName, imageSubFolder));
        return imageTag.toString();
    }
    
    /**
     * Helper method to create an image tag object in a visualization. 
     * @param imageName - the name (e.g. image.png) of the image file to add
     * @param imageSubFolder - we already assume that all images in sub folders of the folder extlib/designer/markup. 
     * So this set the subfolder of the markup folder that contains the image.
     * @return The created image tag object. 
     */
    protected Tag createImageTagObj(String imageName, String imageSubFolder){
        Tag imageTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_IMAGE);
        imageTag.addAttribute(XSPAttributeNames.XSP_ATTR_URL, getURLForImage(imageName, imageSubFolder));
        return imageTag;
    }
    
    /**
     * Generate the OS specific path to the image given an imageName and subFolder of extlib/designer/markup
     * @param imageName
     * @param imageSubFolder
     * @return
     */
    private String getURLForImage(String imageName, String imageSubFolder){
        return IMAGES_LOCATION + Path.SEPARATOR + imageSubFolder + Path.SEPARATOR + imageName;
    }
    
    /**
     * The only XSP tag that currently supports rendering an Image when used in a visualization is the xp:image tag. 
     * It has custom code to look for images from the library that contains the node being visualized, instead of 
     * looking in the Designer Project. 
     * 
     * For controls that need to display images (like buttons with specified images etc..) we need to give them a custom
     * URL so that they can be found when the visualization is being displayed. 
     * 
     * The URL needs to take the format xsp://libraryId~~imageURL
     * This will tell Designer to use the library with the given libraryID to find the image at the given imageURL.
     * 
     * This method used the node being visualized to find the library associated with that Node, and uses that to
     * generate the complete URL.
     * 
     * @param node
     * @param registry
     * @param imageName
     * @param imagesFolderLocation
     * @return A complete URL in the form xsp://libraryId~~imageURL
     */
    public String generateImageURL(Node node, FacesRegistry registry, String imageName, String imagesFolderLocation){
        String id = "";
        String imageURL = "";
        if(null != registry && null != node){
            FacesComponentDefinition def = registry.findComponent(node.getNamespaceURI(), node.getLocalName());
            if(null != def){
                FacesLibraryFragment fragment = def.getFile();
                if(null != fragment){
                    FacesProject proj = fragment.getProject();
                    if(null != proj){
                        id = proj.getId();
                    }
                }
            }
        }
        if(StringUtil.isNotEmpty(id)){
             imageURL = LIB_URL_START_STRING + id + LIB_URL_END_STRING + getURLForImage(imageName, imagesFolderLocation);
        }
        if(StringUtil.isNotEmpty(imageURL)){
             return imageURL;
        }
        return getURLForImage(imageName, imagesFolderLocation);
    }
    
    /**
     * This method takes a node and get the xsp source markup for it, including the
     * source for any child nodes. 
     * @param n
     * @return
     */
    public String getMarkupForNode(Node n){
        try {
            String nodeStr = super.printNode(n);
            if(StringUtil.isNotEmpty(nodeStr)){
                return nodeStr;
            }
       } catch (XMLException e) {
            //do nothing
       }
       return null;
    }
    
    /**
     * Test to see if an attribute value is actually a js binding. i.e. if it contains any scriptlet tags. 
     * @param attributeValue
     * @return
     */
    public boolean isJSVarAttributeBinding(String attributeValue){
        if(StringUtil.isNotEmpty(attributeValue)){
            if(attributeValue.contains(START_SCRIPLET_TAG) ||
                    attributeValue.contains(START_SCRIPLET_VALUE_TAG) ||
                    attributeValue.contains(END_SCRIPLET_TAG)){
                return true;
            }
        }
        return false;
    }
}