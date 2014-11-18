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
package com.ibm.xsp.extlib.designer.tooling.visualizations.dojolayout;

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
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core" style="font-size:16pt">
 * 
 *     	<%
 * 		var titleVar=this.title;
 * 		if(null==titleVar || titleVar==""){
 * 			titleVar="Tab Pane";
 * 		}
 * 		%>
 * 
 * 		<xp:table style="background-color:rgb(192,192,192);width:98%">
 * 			<xp:tr>
 * 				<xp:td style="border:none;background-color:rgb(243,243,243)">
 * 					<xp:label style="background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin">
 * 						<xp:this.value><%=titleVar%></xp:this.value>
 * 					</xp:label>
 * 				</xp:td>
 * 			</xp:tr>
 * 			<xp:tr>
 * 				<xp:td style="border:none;background-color:rgb(255,255,255)">
 * 					<xp:callback></xp:callback>
 * 				</xp:td>
 * 			</xp:tr>
 * 		</xp:table>
 * 
 *  </xp:view>
 *
 */
public class DjTabPaneVisualizer extends AbstractCommonControlVisualizer{

    private static String PANE_TITLE_STRING = "Tab Pane"; // $NLX-DjTabPaneVisualizer.TabPane-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {
        
        StringBuilder strBuilder = new StringBuilder();
        
        String titleVar = "titleVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_TITLE, PANE_TITLE_STRING, titleVar));
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(192,192,192);width:98%"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;background-color:rgb(243,243,243)"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag label = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin"); // $NON-NLS-1$
        label.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, titleVar);
        firstRowCell.addChildTag(label);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(secondRow);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;background-color:rgb(255,255,255)"); // $NON-NLS-1$
        secondRow.addChildTag(secondRowCell);
        
        Tag contentsCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        secondRowCell.addChildTag(contentsCallback);
        
        strBuilder.append(tableTag.toString());
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer#isStaticMarkup()
     */
    @Override
    public boolean isStaticMarkup() {
        return false;
    }
}