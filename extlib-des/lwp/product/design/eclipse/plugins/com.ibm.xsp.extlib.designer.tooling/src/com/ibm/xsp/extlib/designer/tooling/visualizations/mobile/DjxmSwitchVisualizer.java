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
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <%
 *      var leftLabelVar=this.leftLabel;
 *      if(null==leftLabelVar || leftLabelVar==""){
 *          leftLabelVar="Left Label";
 *      }
 *      %>
 *      
 *      <%
 *      var rightLabelVar=this.rightLabel;
 *      if(null==rightLabelVar || rightLabelVar==""){
 *          rightLabelVar="Right Label";
 *      }
 *      %>
 *
 *      <xp:label style="border-color:rgb(192,192,192);border-style:solid;border-width:thin;padding:3.0px;margin-left:1px;margin-top:1px;margin-bottom:1px">
 *      	<xp:this.value><%=leftLabelVar%></xp:this.value>
 *      </xp:label>
 *      
 *      <xp:label style="border-color:rgb(192,192,192);border-style:solid;border-width:thin;background-color:rgb(192,192,192);padding:3.0px;margin-right:1px;margin-top:1px;margin-bottom:1px">
 * 			<xp:this.value><%=rightLabelVar%></xp:this.value>
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class DjxmSwitchVisualizer extends AbstractCommonControlVisualizer{

    private static final String LEFT_LABEL_STRING = "Left Label"; // $NLX-DjxmSwitchVisualizer.LeftLabel-1$
    private static final String RIGHT_LABEL_STRING = "Right Label"; // $NLX-DjxmSwitchVisualizer.RightLabel-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String leftLabelVar = "leftLabelVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_LEFT_LABEL, LEFT_LABEL_STRING, leftLabelVar));
        
        String rightLabelVar = "rightLabelVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_RIGHT_LABEL, RIGHT_LABEL_STRING, rightLabelVar));
        
        Tag leftLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        leftLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-color:rgb(192,192,192);border-style:solid;border-width:thin;padding:3.0px;margin-left:1px;margin-top:1px;margin-bottom:1px"); // $NON-NLS-1$
        leftLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, leftLabelVar);
        strBuilder.append(leftLabel.toString());
        
        Tag rightLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        rightLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-color:rgb(192,192,192);border-style:solid;border-width:thin;background-color:rgb(192,192,192);padding:3.0px;margin-right:1px;margin-top:1px;margin-bottom:1px"); // $NON-NLS-1$
        rightLabel.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, rightLabelVar);
        strBuilder.append(rightLabel.toString());
        
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