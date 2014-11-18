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
 *      <xp:table style="background-color:#E2E2E2;width:98%">
 *          <xp:tr>
 *              <xp:td style="background-color:rgb(255,255,255);border:none;padding:4px" colspan="2">
 *                  <xp:callback facetName="viewTitle"></xp:callback>
 *                  <xp:callback facetName="headerPager"></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="background-color:rgb(255,255,255);border:none">Column 1</xp:td>
 *              <xp:td style="background-color:rgb(255,255,255);border:none">Column 2</xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="background-color:rgb(255,255,255);border:none"></xp:td>
 *              <xp:td style="background-color:rgb(255,255,255);border:none"></xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td
 *                  style="background-color:rgb(255,255,255);border:none;padding:4px" colspan="2">
 *                  <xp:callback facetName="footerPager"></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *   </xp:view>
 *
 */
public class DynamicViewPanelVisualizer extends AbstractCommonControlVisualizer{

    private static final String FIRST_COLUMN_TEXT = "Column 1"; // $NLX-DynamicViewPanelVisualizer.Column1-1$
    private static final String SECOND_COLUMN_TEXT = "Column 2"; // $NLX-DynamicViewPanelVisualizer.Column2-1$

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:#E2E2E2;width:98%"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none;padding:4px"); // $NON-NLS-1$
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_COLSPAN,"2");
        firstRow.addChildTag(firstRowCell);
        
        Tag viewTitleCallbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        viewTitleCallbackTag.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "viewTitle");// $NON-NLS-1$
        firstRowCell.addChildTag(viewTitleCallbackTag);
        
        Tag headerPagerCallbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        headerPagerCallbackTag.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "headerPager");// $NON-NLS-1$
        firstRowCell.addChildTag(headerPagerCallbackTag);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(secondRow);
        
        Tag secondRowFirstCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowFirstCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none"); // $NON-NLS-1$
        secondRowFirstCell.addTextChild(FIRST_COLUMN_TEXT);
        secondRow.addChildTag(secondRowFirstCell);
        
        Tag secondRowSecondCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowSecondCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none"); // $NON-NLS-1$
        secondRowSecondCell.addTextChild(SECOND_COLUMN_TEXT);
        secondRow.addChildTag(secondRowSecondCell);
        
        Tag thirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(thirdRow);
        
        Tag thirdRowFirstCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        thirdRowFirstCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none"); // $NON-NLS-1$
        thirdRow.addChildTag(thirdRowFirstCell);
        
        Tag thirdRowSecondCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        thirdRowSecondCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none"); // $NON-NLS-1$
        thirdRow.addChildTag(thirdRowSecondCell);
        
        Tag fourthRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(fourthRow);
        
        Tag fourthRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        fourthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none;padding:4px"); // $NON-NLS-1$
        fourthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_COLSPAN, "2");
        fourthRow.addChildTag(fourthRowCell);
        
        Tag footerPagerCallbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        footerPagerCallbackTag.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "footerPager");// $NON-NLS-1$
        fourthRowCell.addChildTag(footerPagerCallbackTag);
        
        strBuilder.append(tableTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}