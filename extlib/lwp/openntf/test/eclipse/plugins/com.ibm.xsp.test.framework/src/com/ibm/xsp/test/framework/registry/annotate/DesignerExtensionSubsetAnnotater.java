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
* Date: 8 Apr 2011
* DesignerExtensionSubsetAnnotater.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;

/**
 * 
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public abstract class DesignerExtensionSubsetAnnotater implements RegistryAnnotater{
    public void annotate(RegistryAnnotaterInfo info,
            FacesExtensibleNode parsed, Element elem) {
        
        if( isApplicableExtensibleNode(parsed) ){
            
            String extensionContainerName = getExtensionName(elem.getLocalName());
            for (Element extensionContainer : ElementUtil.getChildren(elem, extensionContainerName)) {
                for (Element extensionBlock : ElementUtil.getChildren(extensionContainer, "designer-extension")) {
                    addExtensions(parsed, extensionBlock);
                }
            }
        }
    }
    private static Map<String, String> s_elemToExtName = null;
    private String getExtensionName(String elemName) {
        if( null == s_elemToExtName ){
            Map<String, String> elemToExtName = new HashMap<String, String>();
            String[][] nameToExtArr = new String[][]{
                // this info gleamed from 
                // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format
                {"complex-type", "complex-extension"},
                {"composite-component", "composite-extension"},
                {"property-type", "property-extension"},
            };
            for (String[] nameToExt : nameToExtArr) {
                elemToExtName.put(nameToExt[0],nameToExt[1]);
            }
            s_elemToExtName = elemToExtName;
        }
        String extName = s_elemToExtName.get(elemName);
        if( null != extName ){
            // special case
            return extName;
        }
        // the normal case
        return elemName+"-extension";
    }
    /**
     * For instanceof checks on the node, to filter elements to search.
     * @param parsed
     * @return
     */
    protected abstract boolean isApplicableExtensibleNode(FacesExtensibleNode parsed);
    
    private String[] extensionNames;
    private String[] extensionValues;
    private void addExtensions(FacesExtensibleNode parsed, Element extensionBlock) {
        if( null == extensionNames ){
            extensionNames = createExtNameArr();
            extensionValues = new String[extensionNames.length];
        }
        ElementUtil.extractValues(extensionBlock, extensionNames, extensionValues);
        int index = 0;
        for (String value : extensionValues) {
            if( null != value ){
                String extensionName = extensionNames[index];
                Object parsedValue = parseValue(extensionName, value);
                parsed.setExtension(extensionName, parsedValue);
            }
            index++;
        }
    }
    protected Object parseValue(String extensionName, String value) {
        return value;
    }
    /**
     * The name of the extensions that should be parsed from the designer-extension 
     * and saved as Strings returned from {@link FacesExtensibleNode#getExtension(String)}.
     * The element names are used as the extension keys. 
     * @return
     */
    protected abstract String[] createExtNameArr();
}