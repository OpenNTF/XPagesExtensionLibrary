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
 *  <xp:span style="color:rgb(114,114,114)">
 *      Link | 
 *  </xp:span>
 *  
 *  <xp:span style="font-weight:bold;color:rgb(40,100,160)">
 *      Link
 *  </xp:span>
 *  
 *  <xp:span style="color:rgb(114,114,114)">
 *      | 
 *  </xp:span>
 *  
 *  <xp:span style="font-weight:bold;color:rgb(40,100,160)">
 *      Link
 *  </xp:span>
 * 
 *  </xp:view>
 *
 */
public class DjLinkSelectVisualizer extends AbstractCommonControlVisualizer{

    private static final String LINK_A_TEXT = "Link"; // $NLX-DjLinkSelectVisualizer.Link-1$
    private static final String LINK_B_TEXT = "Link"; // $NLX-DjLinkSelectVisualizer.Link.1-1$
    private static final String LINK_C_TEXT = "Link"; // $NLX-DjLinkSelectVisualizer.Link.2-1$
    private static final String SEPARATOR = " | ";
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {
        
        StringBuilder strBuilder = new StringBuilder();
        
        Tag linkASpanTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        linkASpanTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(114,114,114)"); // $NON-NLS-1$
        linkASpanTag.addTextChild(LINK_A_TEXT +  SEPARATOR);
        strBuilder.append(linkASpanTag.toString());
        
        Tag linkBSpanTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        linkBSpanTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold;color:rgb(40,100,160)"); // $NON-NLS-1$
        linkBSpanTag.addTextChild(LINK_B_TEXT);
        strBuilder.append(linkBSpanTag.toString());
        
        Tag separatorSpanTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        separatorSpanTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(114,114,114)"); // $NON-NLS-1$
        separatorSpanTag.addTextChild(SEPARATOR);
        strBuilder.append(separatorSpanTag.toString());
        
        Tag linkCSpanTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        linkCSpanTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold;color:rgb(40,100,160)"); // $NON-NLS-1$
        linkCSpanTag.addTextChild(LINK_C_TEXT);
        strBuilder.append(linkCSpanTag.toString());
        
        return strBuilder.toString();
    }

}