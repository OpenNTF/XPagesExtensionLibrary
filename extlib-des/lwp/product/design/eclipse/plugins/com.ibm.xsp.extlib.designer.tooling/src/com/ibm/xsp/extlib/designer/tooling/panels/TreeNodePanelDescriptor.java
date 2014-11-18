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

package com.ibm.xsp.extlib.designer.tooling.panels;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_APPLICATION_CONFIGURATION;

/**
 * @author mblout
 *
 */
public class TreeNodePanelDescriptor {
    
    final String complexType;        // name of the type that contains the property (ex. 'appicationConfiguration')
    final String complexName;        // the actual name of the property that is of type 'typeName' (ex. 'configuration')
    final String propertyName;       // the links property (in typeName) that this panel is editing (ex.'bannerApplicationLinks').

    public TreeNodePanelDescriptor(String type, String name, String propertyName) {
        this.complexType  = type;
        this.complexName  = name;
        this.propertyName = propertyName;
    }
    
    /**
     * utility method for panels that are editing a (links) property
     * of the "applicationConfiguration" type of the Application Layout. 
     * @param configPropName
     * @return
     */
    public static TreeNodePanelDescriptor createConfig(String configPropName) {
        return new TreeNodePanelDescriptor(EXT_LIB_TAG_APPLICATION_CONFIGURATION, EXT_LIB_ATTR_CONFIGURATION, configPropName);
    }

}
