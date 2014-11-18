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
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *  <%
 *  var labelValue=this.label;
 *  if(null==labelValue || labelValue==""){
 *      labelValue="Column";
 *  }
 *  %>
 *
 *  <xp:label style="border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px">
 *  	<xp:this.value><%=labelValue%></xp:this.value> 
 *  </xp:label>
 * 
 *  </xp:view>
 *
 */
public class DjxDataGridColumnVisualizer extends AbstractCommonControlVisualizer{

    private static String DEFAULT_COLUMN_HEADER = "Column"; // $NLX-DjxDataGridColumnVisualizer.Column-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        
        StringBuilder strBuilder = new StringBuilder();
        
        String labelAttributeJSVar = "labelValue"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(XSPAttributeNames.XSP_ATTR_LABEL, DEFAULT_COLUMN_HEADER, labelAttributeJSVar));
        strBuilder.append(LINE_DELIMITER);
        
        Tag labelTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        labelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, 
                "border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px"); // $NON-NLS-1$
        labelTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, labelAttributeJSVar);
        strBuilder.append(labelTag.toString());
        
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