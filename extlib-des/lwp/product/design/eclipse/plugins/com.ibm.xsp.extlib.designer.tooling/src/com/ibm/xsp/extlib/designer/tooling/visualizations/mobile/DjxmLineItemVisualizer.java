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
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <%
 *      var labelVar=this.title;
 *      if(null==labelVar || labelVar==""){
 *          labelVar="Label";
 *      }
 *      %>
 *  
 *      <xp:table style="border-color:rgb(192,192,192);border-style:solid;border-width:thin;width:98%;margin:1px">
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:label style="margin-right:2px">
 *                  	<xp:this.value><%=labelVar%></xp:this.value>
 *                  </xp:label>
 *                  <xp:callback></xp:callback>
 *              </xp:td>
 *              <xp:td style="width:5%;border:none">
 *                  <xp:image url="/extlib/designer/markup/mobile/StaticLineItemIcon.png"></xp:image>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 *
 *  </xp:view>
 *
 */
public class DjxmLineItemVisualizer extends AbstractCommonControlVisualizer{

    private static final String DEFAULT_LABEL = "Label";  // $NLX-DjxmLineItemVisualizer.Label-1$
    private static final String LINE_ITEM_IMAGE="StaticLineItemIcon.png"; // $NON-NLS-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String labelVarName = "labelVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(XLIB_ATTR_LABEL, DEFAULT_LABEL, labelVarName));
        strBuilder.append(LINE_DELIMITER);
        
        Tag table = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        table.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-color:rgb(192,192,192);border-style:solid;border-width:thin;width:98%;margin:1px"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        table.addChildTag(firstRow);
        
        Tag firstRowCell1 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell1.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell1);
        
        Tag titleLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        titleLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "margin-right:2px"); // $NON-NLS-1$
        titleLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, labelVarName);
        firstRowCell1.addChildTag(titleLabel);
        
        Tag contentsCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        firstRowCell1.addChildTag(contentsCallback);
        
        Tag firstRowCell2 = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell2.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:5%;border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell2);
        
        Tag dialgoXButtonImage = createImageTagObj(LINE_ITEM_IMAGE, MOBILE_IMAGES_LOCATION);
        firstRowCell2.addChildTag(dialgoXButtonImage);
        
        strBuilder.append(table.toString());
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