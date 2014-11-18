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
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core" style="font-size:16pt">
 * 
 * 		<%
 *		var labelVar=this.label;
 *		if(null==labelVar || labelVar==""){
 *			labelVar="Label";
 *		}
 *		%>
 *
 *     	<xp:table style="width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin">
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:callback facetName="help"></xp:callback>
 *                  <xp:label value=" "></xp:label>
 *                  <xp:callback facetName="label"></xp:callback>
 *                  <xp:label value=" "></xp:label>
 *                  <xp:label>
 *                   	<xp:this.value><%=labelVar%></xp:this.value>
 *                   </xp:label>
 *                  <xp:label value=" "></xp:label>
 *                  <xp:callback></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class FormRowVisualizer extends AbstractCommonControlVisualizer{

    private static final String TITLE_DEFAULT_TEXT = "Label"; // $NLX-FormRowVisualizer.Label-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String formRowLabelVarName = "labelVar";  // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(XSPAttributeNames.XSP_ATTR_LABEL, TITLE_DEFAULT_TEXT, formRowLabelVarName));
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag helpCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        helpCallback.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "help"); // $NON-NLS-1$
        firstRowCell.addChildTag(helpCallback);
        
        Tag spcaingLabel1 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        spcaingLabel1.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, " ");
        firstRowCell.addChildTag(spcaingLabel1);
        
        Tag labelCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        labelCallback.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "label"); // $NON-NLS-1$
        firstRowCell.addChildTag(labelCallback);
        
        Tag spcaingLabel2 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        spcaingLabel2.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, " ");
        firstRowCell.addChildTag(spcaingLabel2);
        
        Tag titleLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        titleLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, formRowLabelVarName);
        firstRowCell.addChildTag(titleLabel);
        
        Tag spcaingLabel3 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        spcaingLabel3.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, " ");
        firstRowCell.addChildTag(spcaingLabel3);
        
        Tag contentCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        firstRowCell.addChildTag(contentCallback);
        
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