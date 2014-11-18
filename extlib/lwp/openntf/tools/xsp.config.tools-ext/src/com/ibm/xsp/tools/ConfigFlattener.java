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
 * Created on 13-Apr-2005
 * Created by Maire Kehoe (mkehoe@ie.ibm.com)
 */
package com.ibm.xsp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.xsp.page.translator.Lines;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.ElementUtil.ElementIterator;
import com.ibm.xsp.registry.parse.FileConstants;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.tools.flatten.ConfigFlattenerInput;
import com.ibm.xsp.tools.flatten.PropertiesExtractor;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ConfigFlattener {
    private static String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	
    public static final boolean OUTPUT_PROPERTIES_FILES = true;
    public static final boolean PUT_KEYS_IN_DOCUMENT = true;
    public static void main(String[] args) {
        new ConfigFlattener().run( new ConfigFlattenerInput(args));
    }
    public void run(ConfigFlattenerInput input){
        if( ! input.isValid()){
            input.printUsage();
            return;
        }
        try {
            flatten(input);

        } catch (RuntimeException ex){
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Error e) {
            e.printStackTrace();
            throw e;
        }
    }
    private void flatten(ConfigFlattenerInput input)
            throws Exception {
        
        input.print(); 
        
        Document document = ToolsUtil.readInFile(input.getInFile());
        
        if( input.isFacesConfigMode() ){
            Element root = document.getDocumentElement();
            removeElements(root, "component");
            removeElements(root, "attribute");
            removeElements(root, "renderer-extension");
            removeElements(root, "description");
            removeElements(root, "display-name");
        }
        else if (! input.isInlineMode() && OUTPUT_PROPERTIES_FILES) {
            // remove attributes & properties ignored by the registry
            removeNonTagAttributes(document.getDocumentElement());
            
            // ignore all warnings in core-test 
            boolean printWarnings = !(input.isIgnoreWarnings());
            
            // this does 2 things:
            // -) builds a string containing lines for the .properties file
            // -) inserts %key% in the document instead of the messages
            String extactedProperties = new PropertiesExtractor()
                    .changeDocument(document, PUT_KEYS_IN_DOCUMENT,
                            printWarnings);
            
            File extraPropsFile = getExtraPropsFile(input.getInFileName(), input.getExtraPropsFolder());
            // check the document's category %key%s are in the -extra file 
            validateComplexCategories(document, extraPropsFile);
            
            String props = "";
            props = appendCopyrightProps(props);
            
            if( input.isDoNotTranslate() ){
                props += "## G11N " + "DNT -- Do Not Translate " + NEWLINE;
            }else{
                props += "# NLS_ENCODING=UNICODE "+NEWLINE;
                props += "# NLS_MARKUP=IBMNJDK2 "+NEWLINE;
                // \\wsa4.notesdev.ibm.com\workplace\dailybuilds\DSI8.5\
                // DSI8.5_20080624.0630\lwp04.wct-des\lwp\build\trans\results\CHKPII_SAUI.txt
                // used to contain: 
                //   core-actions_en.properties  JAVA-PRB    922   
                //   Unknown single quote handling for this file.  
                //   Special NLS_MESSAGEFORMAT comment must be added.
                // before this line was added:
                props += "# NLS_MESSAGEFORMAT_NONE"+NEWLINE;
                // note, must concat strings with + as, so ConfigFlattener.java 
                // does not show up as to be translated:
                props += "## G11N" + " SA "
                        + "UI -- Special IT Audience resources follow"
                        + NEWLINE;
            }
            props = appendExtraProps(props, extraPropsFile);
            props += extactedProperties;

            ToolsUtil.writeOut(input.getOutPropertiesFile(), props);
        }
        if( ! input.isInlineMode() ){
        removeComments(document);
        }
        removeElements(document.getDocumentElement(), "icon");
        removeXmlBaseAttributes(document.getDocumentElement(), input.isInlineMode());

        String str = ToolsUtil.convertToString(document);
        if( input.isInlineMode() ){
            str = insertLine(str, "<!-- The raw files are not used at runtime nor in Designer, and will be removed from the build output. -->\n");
        }
        
        str = changeNewlines(str);
        str = prependCopyrightXml(str);
        
        ToolsUtil.writeOut(input.getOutFile(), str);
        
        System.out.println("flattened.");
    }

    /**
     * @param str
     */
    private String changeNewlines(String str) {
        // Since the 9.0.1_N / 2014-11-09 change to the xml serializer
        // the xml will be using the OS newline, but this ConfigFlattener
        // and related classes may be using \n, and the copyright text files
        // may have any possible newline.
        // Convert from random newlines to the operating system newline
        String modOne = str.replace("\r\n", "\n");
        String modTwo = modOne.replace("\r","\n");
        String modThree = modTwo.replace("\n",NEWLINE);
        return modThree;
    }
    /**
     * @param document
     * @return if this node was removed.
     */
    private boolean removeNonTagAttributes(Element element) {
        
        // recursively remove from children
        NodeList kids = element.getChildNodes();
        for (int i = 0; i < kids.getLength(); i++) {
            Node kid = kids.item(i);
            if( Node.ELEMENT_NODE != kid.getNodeType() ){
                continue;
            }
            if( removeNonTagAttributes((Element) kid) ){
                i--;
            }
        }
        
        String elemName = element.getNodeName();
        boolean isProp = FileConstants.PROPERTY.equals(elemName);
        if( !isProp && !FileConstants.ATTRIBUTE.equals(elemName)){
            return false;
        }
        
        String extName = isProp? FileConstants.PROPERTY_EXTENSION:FileConstants.ATTRIBUTE_EXTENSION;
        // <tag-attribute>false</tag-attribute>
        String tagAttrStr = ElementUtil.extractValue(
                ElementUtil.getExtension(element,
                        extName),
                FileConstants.TAG_ATTRIBUTE);
        if( ParseUtil.getJsfBoolean(tagAttrStr, true)){
            return false;
        }
        
        // this property not a tag-attribute
        Node parent = element.getParentNode();
        parent.removeChild(element);
        return true;
    }
    /**
     * @param document
     * @param extraPropsFile
     */
    private void validateComplexCategories(Document document, File extraPropsFile){
        
        Map<String, String> extraProps = getExtraProps(extraPropsFile);
        Set<String> unreferencedKeys = new HashSet<String>(extraProps.keySet());
        
        Element root = document.getDocumentElement();
        for (Element i : ElementUtil.getChildren(root)) {
            String name = i.getTagName();
            
            boolean isComplexType = FileConstants.COMPLEX_TYPE.equals(name);
            if (!isComplexType && !FileConstants.VALIDATOR.equals(name)
                    && !FileConstants.CONVERTER.equals(name)) {
                continue;
            }
            
            String category = null;
            String extensionName = isComplexType? FileConstants.COMPLEX_EXTENSION : name+"-extension";
            ElementIterator extension = ElementUtil.getExtension(i, extensionName);
            if( null != extension ){
                Element designerExtension = ElementUtil.getFirstChildElement(extension, "designer-extension");
                if( null != designerExtension ){
                    category = ElementUtil.extractValue(designerExtension, "category");
                }
            }
            if( null == category ){
                continue;
            }
            if( category.length() <= 2 || '%' != category.charAt(0) || '%' != category.charAt(category.length()-1)){
                System.err.println("Unresourced " + name + " category: "
                        + category + " \t should use %complex-category."
                        + category.toLowerCase() + "% \t category."
                        + category.toLowerCase() + " = " + category);
                continue;
            }
            String key = category.substring(1, category.length()-1);
            
            String existing = extraProps.get(key);
            if( null == existing ){
                System.err.println("Category not in -extra.props file: " + key );
            }else{
                unreferencedKeys.remove(key);
            }
        }
        if( unreferencedKeys.size() > 0 ){
            for (String key : unreferencedKeys) {
                System.err.println("Unused key in -extra.props file: " + key
                        + " = " + extraProps.get(key));
            }
        }
    }
    /**
     * @param extraPropsFile
     * @return
     */
    private Map<String, String> getExtraProps(File extraPropsFile) {
        Map<String, String> extraProps = null;
        if( null != extraPropsFile ){
            Properties props = new Properties();
            try {
                props.load( new FileInputStream(extraPropsFile) );
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            extraProps = asStringMap(props);
        }
        if( null == extraProps){
            extraProps = Collections.emptyMap();
        }
        return extraProps;
    }
    /**
     * @param props
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, String> asStringMap(Properties props) {
        return (Map)props;
    }
    /**
     * @param props
     * @param name
     * @return
     * @throws IOException 
     */
    private String appendExtraProps(String props, File extraPropsFile) throws IOException {
        if( null == extraPropsFile ){
            return props;
        }
        String extraProps = readFileContents(extraPropsFile);
        return props + extraProps;
    }
    /**
     * @param props
     * @param configFileName
     * @return
     */
    private File getExtraPropsFile(String configFileName, String extraPropsFolder) {
        if(configFileName.startsWith("raw-")) {
            configFileName = configFileName.replaceFirst("raw-", "");
        }
        
        String nameLessExtsn = configFileName.substring(0, configFileName.lastIndexOf('.'));
        String extraFile = extraPropsFolder + nameLessExtsn + "-extra_en.properties";
        File file = new File(extraFile);
        if( ! file.exists() ){
            return null;
        }
        return file;
    }
    
    /**
     * @param documentElement
     */
    private void removeElements(Element e, String elementName) {
        NodeList kids = e.getChildNodes();
        int i = 0;
        while( i < kids.getLength() ){
            Node kid = kids.item(i);
            
            if( kid.getNodeType() == Node.ELEMENT_NODE ){
                
                if( elementName.equals(kid.getNodeName())  ){
                    e.removeChild(kid);
                    continue;
                }
                
                removeElements((Element)kid, elementName);
            }
            i++;
        }
    }
    /**
     * @param str
     * @return
     * @throws IOException 
     */
    private String prependCopyrightXml(String str) throws IOException {
        String copyrightFile = System.getProperty("user.dir")+ "/faces/COPYRIGHT.xml";
        String copyright = readFileContents(new File(copyrightFile));
        copyright = replaceYearPlaceholder(copyright);
        return insertLine(str, copyright);
    }
    private static String THIS_YEAR = null;
    private String replaceYearPlaceholder(String copyright) {
        String yearPlaceholder = "${year}";
        int placeholderIndex = copyright.indexOf(yearPlaceholder);
        if( -1 != placeholderIndex ){
            if( null == THIS_YEAR ){
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                int thisYear = cal.get(Calendar.YEAR);
                String thisYearStr = Integer.toString(thisYear);
                THIS_YEAR = thisYearStr;
            }
            copyright = copyright.substring(0, placeholderIndex)
                    + THIS_YEAR
                    + copyright.substring(placeholderIndex+yearPlaceholder.length());
        }
        return copyright;
    }
    /**
     * @param str
     * @param copyright
     * @return
     */
    private String insertLine(String str, String copyright) {
        // the first line contains the 
        // <?xml version="1.0" encoding="UTF-8"?>
        int firstNewline = str.indexOf(NEWLINE);
        String firstLine = str.substring(0, firstNewline+NEWLINE.length());
        str = str.substring(firstNewline+NEWLINE.length());
        return firstLine + copyright + str;
    }
    /**
     * @param str
     * @return
     * @throws IOException 
     */
    private String appendCopyrightProps(String str) throws IOException {
        String copyrightFile = System.getProperty("user.dir")+ "/faces/" +
                "COPYRIGHT.properties";
        String copyright = readFileContents(new File(copyrightFile));
        copyright = replaceYearPlaceholder(copyright);
        return str + copyright;
    }
    
    private String readFileContents(File javaFilePath) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader(javaFilePath));

        StringBuffer result = new StringBuffer();
        String line;
        boolean isComment = false;
        boolean isExtraPropsFile = false;
        while ((line = in.readLine()) != null) {
            if(javaFilePath.getName().endsWith("-extra_en.properties")) {
                isExtraPropsFile = true;
            } else {
                isExtraPropsFile = false;
            }
            
            if(isExtraPropsFile && line.startsWith("#")) {
                isComment = true;
            } else {
                isComment = false;
            }
            
            if(!isComment) {
                result.append(line).append(Lines.NEWLINE);
            }
        }
        in.close();
        return result.toString();
    }

    /**
     * @param e
     * @return
     */
    private void removeComments(Node e) {
        NodeList kids = e.getChildNodes();
        int i = 0;
        while( i < kids.getLength() ){
            Node kid = kids.item(i);
            if( kid.getNodeType() == Node.COMMENT_NODE ){
                e.removeChild(kid);
            }else{
                if( kid.getNodeType() == Node.ELEMENT_NODE ){
                    removeComments(kid);
                }
                i++;
            }
        }
    }
    public void removeXmlBaseAttributes(Element e, boolean isInlineMode) {
        boolean removedPeerComments = false;
        NodeList kids = e.getChildNodes();
        int i = 0;
        while( i < kids.getLength() ){
            Node kid = kids.item(i);
            
            if( kid.getNodeType() == Node.ELEMENT_NODE ){
                Element childElement = (Element)kid;
                
                String attr = childElement.getAttribute("xml:base");
                if( null != attr ){
                    if( isInlineMode ){
                        String pathEnd = attr.substring(attr.lastIndexOf('/'));
                        if( pathEnd.length() < attr.length() ){
                            System.out.println("ConfigFlattener.removeXmlBaseAttributes() xml:base="+pathEnd);
                            childElement.setAttribute("xml:base", pathEnd);
                        }// else removedPeerComments is true
                        
                        if( !removedPeerComments ){
                            removedPeerComments = true;
                            // remove Sun copyright headers that were included with these base elements
                            removeComments( e );
                            
                            // removeComments will have made i invalid, so re-process all the peer nodes
                            i = 0;
                            continue;
                        }
                    }else{
                        childElement.removeAttribute("xml:base");
                    }
                }
                
                removeXmlBaseAttributes(childElement, isInlineMode);
            }
            i++;
        }
    }
}

