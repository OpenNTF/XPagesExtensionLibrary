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
package com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary;

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
 *      <xp:panel style="padding:5px;width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin">
 *          <xp:callback>
 *          	<xp:this.facetName>#{Switch Facet}</xp:this.facetName>
 *          </xp:callback>
 *      </xp:panel>
 * 
 *  </xp:view>
 *
 */
public class SwitchFacetVisualizer extends AbstractCommonControlVisualizer{

    private static final String FACET_STRING = "Switch Facet"; // $NLX-SwitchFacetVisualizer.Switch-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag panelTag = new Tag(XP_PREFIX,XSPTagNames.XSP_TAG_PANEL);
        panelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px;width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag callbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        callbackTag.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "#{" + FACET_STRING + "}");
        panelTag.addChildTag(callbackTag);
        
        strBuilder.append(panelTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}