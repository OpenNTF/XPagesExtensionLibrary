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
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core" style="font-size:16pt">
 * 
 * 		<%
 *		var titleVar=this.formTitle;
 *		if(null==titleVar || titleVar==""){
 *			titleVar="Form Title";
 *		}%>
 *		
 *		<%
 *		var descriptionVar=this.formDescription;
 *		if(null==descriptionVar || descriptionVar==""){
 *			descriptionVar="Form Description";
 *		}%> 
 *
 *      <xp:table style="width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin">
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:callback facetName="header"></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:label style="font-weight:bold">
 *                  	<xp:this.value><%=titleVar%></xp:this.value> 
 *                  </xp:label>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:label>
 *                  	<xp:this.value><%=descriptionVar%></xp:this.value>
 *                  </xp:label>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:callback>
 *                  </xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:callback facetName="footer">
 *                  </xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class FormTableVisualizer extends AbstractCommonControlVisualizer{

    private static final String TITLE_DEFAULT_TEXT = "Form Title"; // $NLX-FormTableVisualizer.FormTitle-1$
    private static final String DESCRIPTION_DEFAULT_TEXT = "Form Description"; // $NLX-FormTableVisualizer.FormDescription-1$

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String formTitleVarName = "titleVar";  // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_FORM_TITLE, TITLE_DEFAULT_TEXT, formTitleVarName));
        String formDescriptionVarName = "descriptionVar";  // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_FORM_DESCRIPTION, DESCRIPTION_DEFAULT_TEXT, formDescriptionVarName));
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag headerCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        headerCallback.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "header"); // $NON-NLS-1$
        firstRowCell.addChildTag(headerCallback);
        
        Tag secondRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(secondRow);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        secondRow.addChildTag(secondRowCell);
        
        Tag titleLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        titleLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, formTitleVarName);
        titleLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold"); // $NON-NLS-1$
        secondRowCell.addChildTag(titleLabel);
        
        Tag thirdRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(thirdRow);
        
        Tag thirdRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        thirdRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        thirdRow.addChildTag(thirdRowCell);
        
        Tag descriptionLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        descriptionLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, formDescriptionVarName);
        thirdRowCell.addChildTag(descriptionLabel);
        
        Tag fourthRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(fourthRow);
        
        Tag fourthRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        fourthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        fourthRow.addChildTag(fourthRowCell);
        
        Tag contentsCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        fourthRowCell.addChildTag(contentsCallback);
        
        Tag fifthRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(fifthRow);
        
        Tag fifthRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        fifthRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        fifthRow.addChildTag(fifthRowCell);
        
        Tag footerCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        footerCallback.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "footer"); // $NON-NLS-1$
        fifthRowCell.addChildTag(footerCallback);
        
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