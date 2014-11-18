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
package com.ibm.xsp.extlib.designer.tooling.visualizations.dojoform;

import org.w3c.dom.Node;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *  <xp:inputText id="inputText1">
 *      <xp:this.converter>
 *          <xp:convertNumber type="number">
 *          </xp:convertNumber>
 *      </xp:this.converter>
 *  </xp:inputText>
 * 
 *  </xp:view>
 *
 */
public class DjNumberTextBoxVisualizer extends AbstractCommonControlVisualizer{

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag editBoxTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_EDIT_BOX);
        editBoxTag.addAttribute(XSPAttributeNames.XSP_ATTR_ID, "inputText1"); // $NON-NLS-1$
        
        Tag thisConverterTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_THIS_CONVERTER);
        editBoxTag.addChildTag(thisConverterTag);
        
        Tag convertNumberTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_CONVERTER_CONVERT_NUMBER);
        convertNumberTag.addAttribute(XSPAttributeNames.XSP_ATTR_TYPE, XSP_ATTR_VAL_NUMBER);
        thisConverterTag.addChildTag(convertNumberTag);
        
        strBuilder.append(editBoxTag.toString());
        
        return strBuilder.toString();
    }

}