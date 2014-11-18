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
package com.ibm.xsp.extlib.designer.tooling.annotation;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ibm.commons.xml.DOMUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.parse.FileConstants;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;

/**
 * @author mblout
 *
 */
public class ExtLibExtensionFactory {
    
    // singleton support
    private static ExtLibExtensionFactory _instance;
    private ExtLibExtensionFactory() {}
    public static ExtLibExtensionFactory getInstance() {
        if (null == _instance)
            _instance = new ExtLibExtensionFactory();
        return _instance;
    }
    
    /**
     * see parse(). Looks for extensions in either <property-extension> or <attribute-extension>
     * @param info
     * @param node
     * @param elem
     * @return
     */
    public ExtLibExtension createExtension(
            RegistryAnnotaterInfo info, FacesExtensibleNode node, Element elem) {
        
        ExtLibExtension ele = null;
        
        FacesProperty prop = (FacesProperty) node;
        String extensionName = prop.isAttribute()? ATTRIBUTE_EXTENSION : PROPERTY_EXTENSION;

        NodeList nodelist = elem.getElementsByTagName(extensionName);
        
        for (int i = 0; i < nodelist.getLength(); i++) {
            
            if (nodelist.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element child = (Element)nodelist.item(i);
            
            return parse(child);
        }
        
        return ele;
    }
    
    /**
     * @param info
     * @param elem
     * @return
     */
    public ExtLibLayoutExtension createLayoutExtension(RegistryAnnotaterInfo info, Element elem) {
        
        // Search for <complex-extension>
        NodeList nodeList = elem.getElementsByTagName(FileConstants.COMPLEX_EXTENSION);
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            // Search for <layout-extension>
            NodeList layoutList = ((Element)nodeList.item(i)).getElementsByTagName(LAYOUT_EXTENSION);
            for (int x = 0; x < layoutList.getLength(); x++) {
                if (layoutList.item(x).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element el = (Element) layoutList.item(x);
                ExtLibLayoutExtension layoutExt = new ExtLibLayoutExtension();
                boolean valid = false;
                
                // Get the <responsive> attribute
                NodeList nodes = el.getElementsByTagName(RESPONSIVE);
                if (null != nodes && nodes.getLength() > 0) {
                    valid = true;
                    layoutExt.setResponsive(Boolean.valueOf(DOMUtil.getTextValue(nodes.item(0)).trim()));
                }

                // Get the <layout-image> attribute
                nodes = el.getElementsByTagName(LAYOUT_IMAGE);
                if (null != nodes && nodes.getLength() > 0) {
                    valid = true;
                    layoutExt.setImage(info.getDesignIconUrl(DOMUtil.getTextValue(nodes.item(0)).trim()));
                }

                // Get the <sample-url> attribute
                nodes = el.getElementsByTagName(SAMPLE_URL);
                if (null != nodes && nodes.getLength() > 0) {
                    valid = true;
                    String urlStr = DOMUtil.getTextValue(nodes.item(0)).trim();
                    try {
                        layoutExt.setSampleURL(new URL(urlStr));
                    } catch (MalformedURLException e) {
                        if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isWarnEnabled()){
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warnp(this, "createLayoutExtension", e, "Could not get URL as it is malformed - {0}", urlStr);  // $NON-NLS-1$ $NLW-ExtLibExtensionFactory.CouldnotgetURLasitismalformed0-2$
                        }
                    }
                }
                
                if (valid) {
                    return layoutExt;
                }
            }                
        }
        
        return null;
    }
    
    /**
     * if any of the specific extensions this is looking for are found, and ExtLibExstension is returned.
     * If none if found, this returns <code>null</code>.
     * @param e
     * @return
     */
    private ExtLibExtension parse(Element e) {
        
        ExtLibExtension ext = null; 
            
        String defval = null;
        // look for <default-value>true</default-value>
        NodeList defvalNodes = e.getElementsByTagName(DEFAULT_VALUE);
        if (null != defvalNodes && defvalNodes.getLength() > 0) {
            defval = DOMUtil.getTextValue(defvalNodes.item(0));
        }
        
        
        // look for <designer-extension><exclude-types>type1,type2,type</exclude-types></designer-extension>
        String[] excludes = null;
        
        Element de = getDesignerExtensionElement(e);
        
        if (null != de) {
            NodeList excludesNodes = de.getElementsByTagName(EXCLUDE_TYPES);
            // only takes he first occurence
            if (null != excludesNodes && excludesNodes.getLength() > 0) {
                // commma-separated list, no spaces
                String excl = DOMUtil.getTextValue(excludesNodes.item(0));
                if (null != excl) { 
                    excl = excl.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    excludes = excl.split(","); //$NON-NLS-1$
                }
            }
        }

        if (null != defval || null != excludes) {
            ext = new ExtLibExtension();
            ext.defaultValue = defval;
            ext.excludeTypes = excludes;
        }
        
        return ext;
    }
    
    
    /**
     * if more than one <designer-extension>, return the first one
     * @param e
     * @return
     */
    private Element getDesignerExtensionElement(Element e) {
        
        Element de = null;

        NodeList designerExts = e.getElementsByTagName(DESIGNER_EXTENSION);
        if (null != designerExts && designerExts.getLength() > 0) {
            Node n = designerExts.item(0);
            if (n instanceof Element && n.getParentNode().equals(e)) {
                de = (Element)n;
            }
        }
        
        return de;
    }
        

    /**
     * gets the text node value
     * @param elem
     * @return
     */
    public static String getValue(Element elem){
        if( null == elem ){
            return null;
        }
        Node firstChild = elem.getFirstChild();
        if (null == firstChild)
            return null;

        if (!(firstChild instanceof Text)) {
            if( ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isTraceDebugEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.traceDebugp(
                        ExtLibExtensionFactory.class,
                        "getValue", //$NON-NLS-1$
                        "An element with name " + elem.getTagName() //$NON-NLS-1$
                                + " does not contain a Text node as its " //$NON-NLS-1$
                                + "first child, so it will be " //$NON-NLS-1$
                                + "ignored. Its first child is the " //$NON-NLS-1$
                                + firstChild.getClass().getName() + " " //$NON-NLS-1$
                                + firstChild + "."); //$NON-NLS-1$
            }
            return null;
        }
        Text text = (Text) firstChild;
        return text.getData().trim();
    }
}