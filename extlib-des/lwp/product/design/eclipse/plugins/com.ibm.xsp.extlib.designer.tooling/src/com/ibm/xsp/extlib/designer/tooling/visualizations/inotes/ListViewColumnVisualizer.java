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
package com.ibm.xsp.extlib.designer.tooling.visualizations.inotes;

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
 *      var columnTitle=this.columnTitle;
 *      if(null==columnTitle || columnTitle==""){
 *          columnTitle="";
 *      }
 *      %>
 *      
 *      <%
 *      var columnName=this.columnName;
 *      if(null==columnName || columnName==""){
 *          columnName="List View Column";
 *      }
 *      %>
 *      
 *      <%
 *      var columnHeader="";
 *      if(null==columnTitle || columnTitle==""){
 *          columnHeader=columnName;
 *      }
 *      else{
 *          columnHeader=columnTitle;
 *      }
 *      %>
 *  
 *      <xp:label style="border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px">
 *      	<xp:this.value><%=columnHeader%></xp:this.value>
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class ListViewColumnVisualizer extends AbstractCommonControlVisualizer{

    private static String DEFAULT_COLUMN_HEADER = "List View Column"; // $NLX-ListViewColumnVisualizer.ListViewColumn-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        
        StringBuilder strBuilder = new StringBuilder();
        
        String columnTitleAttributeJSVar = "columnTitle"; // $NON-NLS-1$
        String columnNameAttributeJSVar = "columnName"; // $NON-NLS-1$
        String columnHeaderJSVar = "columnHeader"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_COLUMN_TITLE, "", columnTitleAttributeJSVar));
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_COLUMN_NAME, DEFAULT_COLUMN_HEADER, columnNameAttributeJSVar));
        strBuilder.append(START_SCRIPLET_TAG);
        strBuilder.append("var "+columnHeaderJSVar+"=\"\";"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("if(null=="+columnTitleAttributeJSVar+" || "+columnTitleAttributeJSVar+"==\"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(columnHeaderJSVar+"="+columnNameAttributeJSVar+";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("else{"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(columnHeaderJSVar+"="+columnTitleAttributeJSVar+";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(END_SCRIPLET_TAG);
        
        Tag labelTag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        labelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, 
                "border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px"); // $NON-NLS-1$
        labelTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, columnHeaderJSVar);
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