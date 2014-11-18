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
 *          <xp:label style="padding:5px" value="Show">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px" value="10 ">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)" value="|">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)" value=" 25 ">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)" value="|">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)" value=" 50 ">
 *          </xp:label>
 *          
 *          <xp:label style="padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)" value="|">
 *          </xp:label>
 *          
 *          <xp:label style="padding:5px;color:rgb(66,92,182)" value="All">
 *          </xp:label>
 *          
 *          <xp:label style="padding:5px" value="items per page">
 *          </xp:label>
 *      
 *      </xp:span>
 *  
 *  </xp:view>
 *
 */
public class PagerSizesVisualizer extends AbstractCommonControlVisualizer{

    private static final String ALL_TEXT = "All"; // $NLX-PagerSizesVisualizer.All-1$
    private static final String ITEMS_PER_PAGE_TEXT = "Show {0} items per page"; // $NLX-PagerSizesVisualizer.itemsperpage-1$
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
        
        int index = ITEMS_PER_PAGE_TEXT.indexOf("{0}");
        if(index == -1){
            index = ITEMS_PER_PAGE_TEXT.length() -1;
        }
                
        String prefix = ITEMS_PER_PAGE_TEXT.substring(0, index);
        
        String suffix = "";
        if(index + "{0}".length() < ITEMS_PER_PAGE_TEXT.length()){
            suffix = ITEMS_PER_PAGE_TEXT.substring(index + "{0}".length());
        }
        Tag label1 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, prefix);
        label1.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px"); // $NON-NLS-1$
        span.addChildTag(label1);
        
        Tag label2 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, "10 ");
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px"); // $NON-NLS-1$
        span.addChildTag(label2);
        
        Tag label3 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SEPARATOR);
        label3.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)"); // $NON-NLS-1$
        span.addChildTag(label3);
        
        Tag label4 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label4.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, " 25 ");
        label4.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)"); // $NON-NLS-1$
        span.addChildTag(label4);
        
        Tag label5 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label5.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SEPARATOR);
        label5.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)"); // $NON-NLS-1$
        span.addChildTag(label5);
        
        Tag label6 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label6.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, " 50 ");
        label6.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(16,92,182)"); // $NON-NLS-1$
        span.addChildTag(label6);
        
        Tag label7 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label7.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SEPARATOR);
        label7.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding-top:5px;padding-bottom:5px;color:rgb(204,204,204)"); // $NON-NLS-1$
        span.addChildTag(label7);
        
        Tag label8 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label8.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, ALL_TEXT);
        label8.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px;color:rgb(66,92,182)"); // $NON-NLS-1$
        span.addChildTag(label8);
        
        Tag label9 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label9.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, suffix);
        label9.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:5px"); // $NON-NLS-1$
        span.addChildTag(label9);
        
        strBuilder.append(span.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}