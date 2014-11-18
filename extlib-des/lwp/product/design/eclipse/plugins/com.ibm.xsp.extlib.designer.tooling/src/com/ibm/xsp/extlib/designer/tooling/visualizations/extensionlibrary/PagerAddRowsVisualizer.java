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
 *      <xp:label style="padding:5px;background-color:rgb(243,243,243);color:rgb(66,92,182);font-weight:bold" value="Show more...">
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class PagerAddRowsVisualizer extends AbstractCommonControlVisualizer{

    private static final String ADD_ROWS_TEXT = "Show more..."; // $NLX-PagerAddRowsVisualizer.Showmore-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag labelTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        labelTag.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, ADD_ROWS_TEXT);
        labelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px;background-color:rgb(243,243,243);color:rgb(66,92,182);font-weight:bold"); // $NON-NLS-1$
        
        strBuilder.append(labelTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}