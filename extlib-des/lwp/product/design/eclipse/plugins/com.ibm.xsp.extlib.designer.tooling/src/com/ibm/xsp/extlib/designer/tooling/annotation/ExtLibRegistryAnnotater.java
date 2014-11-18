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

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXTLIB_EXTENSION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.LAYOUT_EXTENSION;

import org.w3c.dom.Element;

import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;

/**
 * @author mblout
 *
 */
public class ExtLibRegistryAnnotater implements com.ibm.xsp.registry.parse.RegistryAnnotater {

    /* (non-Javadoc)
     * @see com.ibm.xsp.registry.parse.RegistryAnnotater#annotate(com.ibm.xsp.registry.parse.RegistryAnnotaterInfo, com.ibm.xsp.registry.FacesExtensibleNode, org.w3c.dom.Element)
     */
    public void annotate(RegistryAnnotaterInfo info, FacesExtensibleNode fenode, Element elem) {
        if( null == fenode){
            return; // do nothing
        }
        
        if (fenode instanceof FacesProperty) {
            Object existing = fenode.getExtension(EXTLIB_EXTENSION);
            
            if( existing != null ){
                if( ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isWarnEnabled() ){
                    Object[] params = new Object[]{
                            EXTLIB_EXTENSION,
                            fenode,
                            existing,
                    };
                    String infoMsg = "Node already contains {0}. Node is {1}. Current value is {2}.";  // $NLW-ExtLibRegistryAnnotater.Nodealreadycontains0Nodeis1Curren-1$
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn(infoMsg, params);
                }
            }
            else{
                ExtLibExtension ext = ExtLibExtensionFactory.getInstance().createExtension(info, fenode, elem);
                if( null != ext ){
                    fenode.setExtension(EXTLIB_EXTENSION, ext);
                }
            }
        } else if (fenode instanceof FacesComplexDefinition) {
            Object existing = fenode.getExtension(LAYOUT_EXTENSION);
            
            if( existing != null ){
                if( ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isWarnEnabled() ){
                    Object[] params = new Object[]{
                            LAYOUT_EXTENSION,
                            fenode,
                            existing,
                    };
                    String infoMsg = "Node already contains {0}. Node is {1}. Current value is {2}.";  // $NLW-ExtLibRegistryAnnotater.Nodealreadycontains0Nodeis1Curren-1$
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn(infoMsg, params);
                }
            }
            else{
                ExtLibLayoutExtension ext = ExtLibExtensionFactory.getInstance().createLayoutExtension(info, elem);
                if( null != ext ){
                    fenode.setExtension(LAYOUT_EXTENSION, ext);
                }
            }
            
        }
    }

}
