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
 *      <xp:span style="color:rgb(16,92,182)">
 *          Home
 *      </xp:span> 
 *      &gt; 
 *      <xp:span style="color:rgb(16,92,182)">
 *          Page
 *      </xp:span> 
 *      <xp:span style="color:rgb(0,0,0)">
 *          &gt; Sub Page
 *      </xp:span>
 * 
 *  </xp:view>
 *
 */
public class BreadCrumbsVisualizer extends AbstractCommonControlVisualizer{

    private static final String HOME_STRING = "Home"; // $NLX-BreadCrumbsVisualizer.Home-1$
    private static final String USER_BEAN_STRING = "Page"; // $NLX-BreadCrumbsVisualizer.UserBean-1$
    private static final String VIEW_STATE_STRING = "Subpage"; // $NLX-BreadCrumbsVisualizer.ViewState-1$
    private static final String OPEN_ANGLED_BRACKET_STRING = "&gt;"; // $NON-NLS-1$
    
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag firstSpan = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        firstSpan.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(16,92,182)"); // $NON-NLS-1$
        firstSpan.addTextChild(HOME_STRING);
        strBuilder.append(firstSpan.toString());
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(OPEN_ANGLED_BRACKET_STRING);
        strBuilder.append(LINE_DELIMITER);
        
        Tag secondSpan = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        secondSpan.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(16,92,182)"); // $NON-NLS-1$
        secondSpan.addTextChild(USER_BEAN_STRING);
        strBuilder.append(secondSpan.toString());
        strBuilder.append(LINE_DELIMITER);
        
        Tag thirdSpan = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        thirdSpan.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(0,0,0)"); // $NON-NLS-1$
        thirdSpan.addTextChild(OPEN_ANGLED_BRACKET_STRING + " " + VIEW_STATE_STRING);
        strBuilder.append(thirdSpan.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}