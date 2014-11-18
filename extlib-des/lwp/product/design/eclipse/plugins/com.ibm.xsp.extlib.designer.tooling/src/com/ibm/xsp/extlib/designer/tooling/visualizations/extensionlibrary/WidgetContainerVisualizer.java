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
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 * 		<%
 *		var titleBarTextVar=this.titleBarText;
 *		if(null==titleBarTextVar || titleBarTextVar==""){
 *			titleBarTextVar="Title Bar";
 *		}
 *		%> 
 *
 *      <xp:table style="background-color:#E2E2E2;width:98%">
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:span style="font-weight:bold;color:rgb(91,91,91)"><%=titleBarTextVar%></xp:span>
 *              </xp:td>    
 *              <xp:td style="border:none;width:5%">
 *                  <xp:image url="/extlib/designer/markup/extensionlibrary/SectionMenu12.png">
 *                  </xp:image>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td colspan="2" style="background-color:rgb(255,255,255);border:none">
 *                  <xp:callback></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *   </xp:view>
 *
 */
public class WidgetContainerVisualizer extends AbstractCommonControlVisualizer{

    private static final String TITLE_BAR_TEXT = "Title Bar"; // $NLX-WidgetContainerVisualizer.TitleBar-1$
    private static final String TITLE_MENU_IMAGE_NAME = "SectionMenu12.png"; // $NON-NLS-1$

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String titleBarTextVarName = "titleBarTextVar";  // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_TITLE_BAR_TEXT, TITLE_BAR_TEXT, titleBarTextVarName));
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:#E2E2E2;width:98%"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowFirstCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowFirstCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowFirstCell);
        
        Tag firstRowFirstCellSpan = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        firstRowFirstCellSpan.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold;color:rgb(91,91,91)"); // $NON-NLS-1$
        firstRowFirstCellSpan.addJSVarTextChild(titleBarTextVarName);
        firstRowFirstCell.addChildTag(firstRowFirstCellSpan);
        
        Tag firstRowSecondCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowSecondCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;width:5%"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowSecondCell);
        
        Tag imageTag = createImageTagObj(TITLE_MENU_IMAGE_NAME, EXTENSION_LIBRARY_IMAGES_LOCATION);
        firstRowSecondCell.addChildTag(imageTag);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(secondRow);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);border:none"); // $NON-NLS-1$
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_COLSPAN,"2");
        secondRow.addChildTag(secondRowCell);
        
        Tag contentAreaCallbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        secondRowCell.addChildTag(contentAreaCallbackTag);
        
        strBuilder.append(tableTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer#isStaticMarkup()
     */
    @Override
    public boolean isStaticMarkup(){
		return false;
	}
}