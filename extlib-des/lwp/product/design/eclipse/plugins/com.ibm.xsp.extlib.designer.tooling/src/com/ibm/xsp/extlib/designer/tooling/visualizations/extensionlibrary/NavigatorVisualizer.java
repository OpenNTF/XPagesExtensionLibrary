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
 *      <xp:table style="width:auto;border:none;border-color:rgb(64,86,116);border-style:solid;border-width:1px;background-color:rgb(241,241,241)">
 *          
 *          <xp:tr>
 *              <xp:td style="border:none;padding-left:18px;padding-right:20.0px">
 *                  Page 1  
 *              </xp:td>
 *          </xp:tr>
 *          
 *          <xp:tr>
 *              <xp:td style="border:none;background-color:rgb(64,86,116);color:rgb(255,255,255);padding-right:20.0px">
 *              <xp:image style="margin-right:1.0px" url="/extlib/designer/markup/extensionlibrary/TwistyWhite12.png">
 *              </xp:image>
 *                  Page 2
 *              </xp:td>    
 *          </xp:tr>
 *          
 *          <xp:tr>
 *              <xp:td style="border:none;padding-right:20.0px">
 *                  <xp:table>
 *                      
 *                      <xp:tr>
 *                          <xp:td style="border:none;padding-left:30px">
 *                              Subpage 1
 *                          </xp:td>
 *                      </xp:tr>
 *                      
 *                      <xp:tr>
 *                          <xp:td style="border:none;padding-left:30px">
 *                              Subpage 2
 *                          </xp:td>    
 *                      </xp:tr>
 *      
 *                      <xp:tr>
 *                          <xp:td style="border:none;padding-left:30px">
 *                              Subpage 3
 *                          </xp:td>    
 *                      </xp:tr>
 *
 *                  </xp:table>     
 *              </xp:td> 
 *          </xp:tr>
 *          
 *          <xp:tr>
 *              <xp:td style="border:none;padding-left:18px;padding-right:20.0px">
 *                  Page 3
 *              </xp:td>
 *          </xp:tr>
 *      
 *      </xp:table>
 * 
 *   </xp:view>
 *
 */
public class NavigatorVisualizer extends AbstractCommonControlVisualizer{

    private static final String FIRST_LINK_TEXT = "Page 1"; // $NLX-NavigatorVisualizer.AllDocuments-1$
    private static final String SECOND_LINK_TEXT = "Subpage 1"; // $NLX-NavigatorVisualizer.ByMostRecent-1$
    private static final String THIRD_LINK_TEXT = "Subpage 2"; // $NLX-NavigatorVisualizer.ByAuthor-1$
    private static final String FOURTH_LINK_TEXT = "Subpage 3"; // $NLX-NavigatorVisualizer.ByTag-1$
    private static final String FIFTH_LINK_TEXT = "Page 2"; // $NLX-NavigatorVisualizer.MyDocuments-1$
    private static final String SIXTH_LINK_TEXT = "Page 3";  // $NLX-NavigatorVisualizer.Page3-1$
    private static final String TWISTY_IMAGE = "TwistyWhite12.png"; // $NON-NLS-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag outerTableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        outerTableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:auto;border:none;border-color:rgb(64,86,116);border-style:solid;border-width:1px;background-color:rgb(241,241,241)"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-left:18px;padding-right:20.0px"); // $NON-NLS-1$
        firstRowCell.addTextChild(FIRST_LINK_TEXT);
        firstRow.addChildTag(firstRowCell);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(secondRow);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;background-color:rgb(64,86,116);color:rgb(255,255,255);padding-right:20.0px"); // $NON-NLS-1$
        Tag imageTag = createImageTagObj(TWISTY_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION);
        imageTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "margin-right:1.0px"); // $NON-NLS-1$
        secondRowCell.addChildTag(imageTag);
        secondRowCell.addTextChild(FIFTH_LINK_TEXT);
        secondRow.addChildTag(secondRowCell);
        
        Tag thirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(thirdRow);
        
        Tag thirdRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        thirdRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-right:20.0px"); // $NON-NLS-1$
        thirdRow.addChildTag(thirdRowCell);
        
        Tag innerTableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        thirdRowCell.addChildTag(innerTableTag);
        
        Tag innerTableFirstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableFirstRow);
        
        Tag innerTableFirstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableFirstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-left:30px"); // $NON-NLS-1$
        innerTableFirstRowCell.addTextChild(SECOND_LINK_TEXT);
        innerTableFirstRow.addChildTag(innerTableFirstRowCell);
        
        Tag innerTableSecondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableSecondRow);
        
        Tag innerTableSecondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableSecondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-left:30px"); // $NON-NLS-1$
        innerTableSecondRowCell.addTextChild(THIRD_LINK_TEXT);
        innerTableSecondRow.addChildTag(innerTableSecondRowCell);
        
        Tag innerTableThirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        innerTableTag.addChildTag(innerTableThirdRow);
        
        Tag innerTableThirdRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        innerTableThirdRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-left:30px"); // $NON-NLS-1$
        innerTableThirdRowCell.addTextChild(FOURTH_LINK_TEXT);
        innerTableThirdRow.addChildTag(innerTableThirdRowCell);
        
        
        Tag fourthRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        outerTableTag.addChildTag(fourthRow);
        
        Tag fourthRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        fourthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;padding-left:18px;padding-right:20.0px"); // $NON-NLS-1$
        fourthRowCell.addTextChild(SIXTH_LINK_TEXT);
        fourthRow.addChildTag(fourthRowCell);
        
        strBuilder.append(outerTableTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}