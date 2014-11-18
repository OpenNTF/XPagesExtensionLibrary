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
 *      <xp:table style="width:152.0px;border:none;background-color:rgb(192,192,192)">
 *      
 *          <xp:tr>
 *              <xp:td style="border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)">
 *                  <xp:span style="font-weight:bold">
 *                      Container 1
 *               </xp:span>
 *              </xp:td>
 *          </xp:tr>
 *  
 *          <xp:tr>
 *              <xp:td style="border-style:solid;border-color:rgb(255,255,255);border-width:1px;height:auto;background-color:rgb(255,255,255)">
 *                  <xp:table style="color:rgb(16,82,182)">
 *                      
 *                      <xp:tr>
 *                          <xp:td style="border:none">
 *                              Item 1
 *                          </xp:td>
 *                      </xp:tr>
 *                  
 *                      <xp:tr>
 *                          <xp:td style="border:none">
 *                              Item 2
 *                          </xp:td> 
 *                      </xp:tr>
 *  
 *                      <xp:tr>
 *                          <xp:td style="border:none">
 *                              Item 3
 *                          </xp:td>
 *                      </xp:tr>
 *      
 *                  </xp:table> 
 *              </xp:td>
 *          </xp:tr>
 *  
 *          <xp:tr>
 *              <xp:td style="border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)">
 *                 Container 2
 *              </xp:td>
 *          </xp:tr>
 *  
 *          <xp:tr>
 *              <xp:td style="border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)">
 *                  Container 3
 *              </xp:td> 
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class AccordionVisualizer extends AbstractCommonControlVisualizer{

    private static final String MENU_ITEM_1_TEXT = "Container 1"; // $NLX-AccordionVisualizer.Choice1-1$
    private static final String MENU_ITEM_2_TEXT = "Item 1"; // $NLX-AccordionVisualizer.Menu11-1$
    private static final String MENU_ITEM_3_TEXT = "Item 2"; // $NLX-AccordionVisualizer.Menu12-1$
    private static final String MENU_ITEM_4_TEXT = "Item 3"; // $NLX-AccordionVisualizer.Menu13-1$
    private static final String MENU_ITEM_5_TEXT = "Container 2"; // $NLX-AccordionVisualizer.Choice2-1$
    private static final String MENU_ITEM_6_TEXT = "Container 3"; // $NLX-AccordionVisualizer.HierarchicalChoice3-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag outerTableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        outerTableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:152.0px;border:none;background-color:rgb(192,192,192)"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag firstRowSpan = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        firstRowSpan.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold"); // $NON-NLS-1$
        firstRowSpan.addTextChild(MENU_ITEM_1_TEXT);
        firstRowCell.addChildTag(firstRowSpan);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(secondRow);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-style:solid;border-color:rgb(255,255,255);border-width:1px;height:auto;background-color:rgb(255,255,255)"); // $NON-NLS-1$
        secondRow.addChildTag(secondRowCell);
        
        Tag innerTableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        innerTableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "color:rgb(16,82,182)"); // $NON-NLS-1$
        secondRowCell.addChildTag(innerTableTag);
        
        Tag innerTableFirstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableFirstRow);
        
        Tag innerTableFirstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableFirstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        innerTableFirstRowCell.addTextChild(MENU_ITEM_2_TEXT);
        innerTableFirstRow.addChildTag(innerTableFirstRowCell);
        
        Tag innerTableSecondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableSecondRow);
        
        Tag innerTableSecondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableSecondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        innerTableSecondRowCell.addTextChild(MENU_ITEM_3_TEXT);
        innerTableSecondRow.addChildTag(innerTableSecondRowCell);
        
        Tag innerTableThirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableThirdRow);
        
        Tag innerTableThirdRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableThirdRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        innerTableThirdRowCell.addTextChild(MENU_ITEM_4_TEXT);
        innerTableThirdRow.addChildTag(innerTableThirdRowCell);
        
        Tag thirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(thirdRow);
        
        Tag thirdRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        thirdRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)"); // $NON-NLS-1$
        thirdRowCell.addTextChild(MENU_ITEM_5_TEXT);
        thirdRow.addChildTag(thirdRowCell);
        
        Tag fourthRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(fourthRow);
        
        Tag fourthRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        fourthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)"); // $NON-NLS-1$
        fourthRowCell.addTextChild(MENU_ITEM_6_TEXT);
        fourthRow.addChildTag(fourthRowCell);
        
        strBuilder.append(outerTableTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}