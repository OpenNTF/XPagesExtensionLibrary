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
 *      <xp:span style="background-color:rgb(243,243,243)">
 *          
 *          <xp:label style="padding:5px" value="Hide Details">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px" value="|">
 *          </xp:label>
 *          
 *          <xp:label style="padding:5px;color:rgb(66,92,182)" value="Show Details">
 *          </xp:label>
 *  
 *      </xp:span>
 * 
 *  </xp:view>
 *
 */
public class PagerDetailVisualizer extends AbstractCommonControlVisualizer{

    private static final String SHOW_DETAILS_TEXT = "Show Details"; // $NLX-PagerDetailVisualizer.ShowDetails-1$
    private static final String HIDE_DETAILS_TEXT = "Hide Details"; // $NLX-PagerDetailVisualizer.HideDetails-1$
    private static final String SEPARATOR = "|";
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag span = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        span.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(243,243,243)"); // $NON-NLS-1$
        
        Tag label1 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, HIDE_DETAILS_TEXT);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px"); // $NON-NLS-1$
        span.addChildTag(label1);
        
        Tag label2 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SEPARATOR);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px"); // $NON-NLS-1$
        span.addChildTag(label2);
        
        Tag label3 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SHOW_DETAILS_TEXT);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px;color:rgb(66,92,182)"); // $NON-NLS-1$
        span.addChildTag(label3);
        
        strBuilder.append(span.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}