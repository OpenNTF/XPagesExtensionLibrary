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
package com.ibm.xsp.extlib.designer.tooling.visualizations.mobile;

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
 *      <%
 *      var titleVar=this.label;
 *      if(null==titleVar || titleVar==""){
 *          titleVar="Title";
 *      }
 *      %>
 *  
 *      <xp:table style="border-color:rgb(192,192,192);background-color:rgb(243,243,243);border-style:solid;border-width:thin;width:98%;margin:1px">
 *          <xp:tr>
 *              <xp:td style="border:none;text-align:center">
 *                  <xp:label>
 *                      <xp:this.value><%=titleVar%></xp:this.value>
 *                  </xp:label>
 *                  <xp:callback></xp:callback>
 *              </xp:td>
 *              <xp:td style="border:none;width:10%">
 *                  <xp:callback facetName="actionFacet"></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class DjxmHeadingVisualizer extends AbstractCommonControlVisualizer{

    private static String DEFAULT_TITLE_STRING = "Title"; // $NLX-DjxmHeadingVisualizer.Title-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {
        
        StringBuilder strBuilder = new StringBuilder();
        
        String titleVar = "titleVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(XSPAttributeNames.XSP_ATTR_LABEL, DEFAULT_TITLE_STRING, titleVar));
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-color:rgb(192,192,192);background-color:rgb(243,243,243);border-style:solid;border-width:thin;width:98%;margin:1px"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;text-align:center"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag label = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, titleVar);
        firstRowCell.addChildTag(label);
        
        Tag contentsCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        firstRowCell.addChildTag(contentsCallback);
        
        Tag secondRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        secondRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;width:10%"); // $NON-NLS-1$
        firstRow.addChildTag(secondRowCell);
        
        Tag actionCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        actionCallback.addAttribute(XSPAttributeNames.XSP_ATTR_FACET_NAME, "actionFacet"); // $NON-NLS-1$
        secondRowCell.addChildTag(actionCallback);
        
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