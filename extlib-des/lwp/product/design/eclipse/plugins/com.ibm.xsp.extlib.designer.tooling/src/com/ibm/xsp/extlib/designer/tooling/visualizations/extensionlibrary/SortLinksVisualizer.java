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
 *      <xp:span>
 *      
 *          <xp:label style="padding:5px" value="Sort by:">
 *          </xp:label>
 *      
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)" value="Key 1">
 *          </xp:label>
 *      
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)" value=" | ">
 *          </xp:label>
 *      
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)" value="Key 2">
 *          </xp:label>
 *      
 *      </xp:span>
 * 
 *  </xp:view>
 *
 */
public class SortLinksVisualizer extends AbstractCommonControlVisualizer{

    private static final String SORT_BY_TEXT = "Sort by:"; // $NLX-SortLinksVisualizer.Sortby-1$
    private static final String BY_AUTHOR_TEXT = "Key 1"; // $NLX-SortLinksVisualizer.ByAuthor-1$
    private static final String BY_DATE_TEXT = "Key 2"; // $NLX-SortLinksVisualizer.ByDate-1$
    private static final String SEPARATOR = " | ";
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag span = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        
        Tag label1 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SORT_BY_TEXT);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px"); // $NON-NLS-1$
        span.addChildTag(label1);
        
        Tag label2 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, BY_AUTHOR_TEXT);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)"); // $NON-NLS-1$
        span.addChildTag(label2);
        
        Tag label3 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SEPARATOR);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)"); // $NON-NLS-1$
        span.addChildTag(label3);
        
        Tag label4 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label4.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, BY_DATE_TEXT);
        label4.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)"); // $NON-NLS-1$
        span.addChildTag(label4);
        
        strBuilder.append(span.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}