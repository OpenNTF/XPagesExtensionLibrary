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
 *      <%
 *      var titleVar=this.title;
 *      if(null==titleVar || titleVar==""){
 *          titleVar="Border Pane";
 *      }
 *      %>
 *      
 *      <%
 *      var panePosVar=this.region;
 *      if(null==panePosVar || panePosVar==""){
 *          panePosVar="";
 *      }else if(panePosVar=="top"){
 *          panePosVar=": Top";
 *      }else if(panePosVar=="left"){
 *          panePosVar=": Left";
 *      }else if(panePosVar=="center"){
 *          panePosVar=": Center";
 *      }else if(panePosVar=="right"){
 *          panePosVar=": Right";
 *      }else if(panePosVar=="bottom"){
 *          panePosVar=": Bottom";
 *      }else if(panePosVar=="leading"){
 *          panePosVar=": Leading";
 *      }else if(panePosVar=="trailing"){
 *          panePosVar=": Trailing";
 *      }else{
 *      	panelPosVar="";	
 *      }
 *      %>
 *  
 *      <xp:table style="width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin">
 *          <xp:tr>
 *              <xp:td style="border:none">
 *                  <xp:label style="margin-right:5px">
 *                  	<xp:this.value><%=titleVar%><%=panePosVar%></xp:this.value>
 *                  </xp:label>
 *                  <xp:callback></xp:callback>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class DjBorderPaneVisualizer extends AbstractCommonControlVisualizer{

    private static String PANE_TITLE_STRING = "Border Pane"; // $NLX-DjBorderPaneVisualizer.BorderPane-1$
    private static String PANE_TOP_STRING = "Top"; // $NLX-DjBorderPaneVisualizer.Top-1$
    private static String PANE_LEFT_STRING = "Left"; // $NLX-DjBorderPaneVisualizer.Left-1$
    private static String PANE_CENTER_STRING = "Center"; // $NLX-DjBorderPaneVisualizer.Center-1$
    private static String PANE_RIGHT_STRING = "Right"; // $NLX-DjBorderPaneVisualizer.Right-1$
    private static String PANE_BOTTOM_STRING = "Bottom"; // $NLX-DjBorderPaneVisualizer.Bottom-1$
    private static String PANE_LEADING_STRING = "Leading"; // $NLX-DjBorderPaneVisualizer.Leading-1$
    private static String PANE_TRAILING_STRING = "Trailing"; // $NLX-DjBorderPaneVisualizer.Trailing-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        String titleVar = "titleVar"; // $NON-NLS-1$
        String panePosVar = "panePosVar"; // $NON-NLS-1$
        
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_TITLE, PANE_TITLE_STRING, titleVar));
        
        strBuilder.append(START_SCRIPLET_TAG + LINE_DELIMITER);
        strBuilder.append("var " + panePosVar + "=this." + IExtLibAttrNames.EXT_LIB_ATTR_REGION + ";"); // $NON-NLS-2$ $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("if(null=="+panePosVar + " || " + panePosVar + "==\"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_TOP_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_LEFT_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_CENTER+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_CENTER_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_RIGHT_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_BOTTOM_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEADING+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_LEADING_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else if("+panePosVar + "==\""+IExtLibAttrNames.EXT_LIB_ATTR_VAL_TRAILING+"\"){"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\": "+PANE_TRAILING_STRING+"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append("else{"); // $NON-NLS-1$
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(panePosVar + "=\"\";");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append("}");
        strBuilder.append(LINE_DELIMITER);
        strBuilder.append(END_SCRIPLET_TAG);
        strBuilder.append(LINE_DELIMITER);
        
        Tag tableTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        tableTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag firstRow = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        tableTag.addChildTag(firstRow);
        
        Tag firstRowCell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        firstRowCell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none"); // $NON-NLS-1$
        firstRow.addChildTag(firstRowCell);
        
        Tag label = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "margin-right:5px"); // $NON-NLS-1$
        label.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, START_SCRIPLET_VALUE_TAG + titleVar + END_SCRIPLET_TAG + START_SCRIPLET_VALUE_TAG + panePosVar + END_SCRIPLET_TAG);
        firstRowCell.addChildTag(label);
        
        Tag contentsCallback = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        firstRowCell.addChildTag(contentsCallback);
        
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