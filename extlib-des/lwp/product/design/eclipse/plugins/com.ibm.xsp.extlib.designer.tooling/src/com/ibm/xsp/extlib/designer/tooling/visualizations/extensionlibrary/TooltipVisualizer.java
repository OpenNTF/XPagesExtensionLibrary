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
 *      <%
 *      var labelVar=this.label;
 *      if(null==labelVar || labelVar==""){
 *          labelVar="Tooltip";
 *      }%>
 *      
 *      <xp:image url="/extlib/designer/markup/extensionlibrary/Tooltip.png">
 *      </xp:image>
 *      
 *      <xp:label>
 *      	<xp:this.value><%=labelVar%></xp:this.value>
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class TooltipVisualizer extends AbstractCommonControlVisualizer{

    private static final String TOOLTIP_IMAGE = "Tooltip.png"; // $NON-NLS-1$
    private static final String DEFAULT_TOOLTIP_TEXT = "Tooltip"; // $NLX-TooltipVisualizer.Tooltip-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String labelVarName = "labelVar"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(XSPAttributeNames.XSP_ATTR_LABEL, DEFAULT_TOOLTIP_TEXT, labelVarName));
        strBuilder.append(LINE_DELIMITER);
        
        strBuilder.append(createImageTag(TOOLTIP_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        strBuilder.append(LINE_DELIMITER);
        
        Tag labelTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        labelTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, labelVarName);
        strBuilder.append(labelTag.toString());
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