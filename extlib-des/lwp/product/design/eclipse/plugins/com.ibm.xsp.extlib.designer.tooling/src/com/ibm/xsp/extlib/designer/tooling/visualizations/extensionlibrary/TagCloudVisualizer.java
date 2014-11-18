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

import com.ibm.commons.util.StringUtil;
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
 *      <xp:panel style="height:150.0px;width:120.0px;border-width:1px;border-style:solid">
 *          <xp:image url="/extlib/designer/markup/extensionlibrary/Twisty.png">
 *          </xp:image>
 *      
 *          <xp:span style="font-weight:bold">
 *              Top 10 Tags
 *          </xp:span>
 *      
 *          <xp:image style="width:115px;margin-top:5px;margin-bottom:5px" url="/extlib/designer/markup/extensionlibrary/TagCloudSlider.png">
 *          </xp:image>
 *      
 *          <%
 *          var labelArray = ["Agenda","Cloud","Communities","Connections","Lotusphere","Notes","Schedule","Sessions","iNotes","Lotus"];
 *      
 *          function getRandomStyle(){
 *              var rand = Math.floor(Math.random()*4);
 *              if(rand==0){
 *                  return("font-size:9pt;color:rgb(150,209,241);font-weight:bold");
 *              }
 *              if(rand==1){
 *                  return("font-size:9pt;color:rgb(14,78,112);font-weight:bold");
 *              }
 *              if(rand==2){
 *                  return("font-size:10pt;color:rgb(14,78,112);font-weight:bold");
 *              }
 *              if(rand==3){
 *                  return("font-size:12pt;color:rgb(14,78,112);font-weight:bold");
 *              }
 *          }
 *      
 *          for (var x=0;x<labelArray.length;x++){%>
 *              <xp:label style="<%=getRandomStyle()%>" value="<%=labelArray[x]%>">
 *              </xp:label>
 *          <%}%>
 *      
 *      </xp:panel>
 * 
 *  </xp:view>
 *
 */
public class TagCloudVisualizer extends AbstractCommonControlVisualizer{

    private static final String SLIDER_IMAGE = "TagCloudSlider.png"; // $NON-NLS-1$
    private static final String TWISTY_IMAGE = "Twisty.png"; // $NON-NLS-1$
    
    private static final String CLOUD_TITLE_STRING = "Top 10 Tags"; // $NLX-TagCloudVisualizer.Top25Tags-1$
    
    private static final String CLOUD_STRING_1 = "Agenda"; // $NLX-TagCloudVisualizer.Agenda-1$
    private static final String CLOUD_STRING_2 = "Cloud"; // $NLX-TagCloudVisualizer.Cloud-1$
    private static final String CLOUD_STRING_3 = "Communities"; // $NLX-TagCloudVisualizer.Communities-1$
    private static final String CLOUD_STRING_4 = "Connections"; // $NLX-TagCloudVisualizer.Connections-1$
    private static final String CLOUD_STRING_5 = "Social"; // $NLX-TagCloudVisualizer.Lotusphere-1$
    private static final String CLOUD_STRING_6 = "Notes"; // $NLX-TagCloudVisualizer.Notes-1$
    private static final String CLOUD_STRING_7 = "Schedule"; // $NLX-TagCloudVisualizer.Schedule-1$
    private static final String CLOUD_STRING_8 = "Sessions"; // $NLX-TagCloudVisualizer.Sessions-1$
    private static final String CLOUD_STRING_9 = "iNotes"; // $NLX-TagCloudVisualizer.iNotes-1$
    private static final String CLOUD_STRING_10 = "IBM"; // $NLX-TagCloudVisualizer.lotus-1$
    
    private static final String STYLE_1 = "font-size:9pt;color:rgb(150,209,241);font-weight:bold"; // $NON-NLS-1$
    private static final String STYLE_2 = "font-size:9pt;color:rgb(14,78,112);font-weight:bold"; // $NON-NLS-1$
    private static final String STYLE_3 = "font-size:10pt;color:rgb(14,78,112);font-weight:bold"; // $NON-NLS-1$
    private static final String STYLE_4 = "font-size:12pt;color:rgb(14,78,112);font-weight:bold"; // $NON-NLS-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag panelTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_PANEL);
        panelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "height:150.0px;width:120.0px;border-width:1px;border-style:solid"); // $NON-NLS-1$
        
        Tag twistyImage = createImageTagObj(TWISTY_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION);
        panelTag.addChildTag(twistyImage);
        
        Tag spanTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_SPAN);
        spanTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold"); // $NON-NLS-1$
        spanTag.addTextChild(CLOUD_TITLE_STRING);
        panelTag.addChildTag(spanTag);
        
        Tag sliderImageTag = createImageTagObj(SLIDER_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION);
        sliderImageTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:115px;margin-top:5px;margin-bottom:5px"); // $NON-NLS-1$
        panelTag.addChildTag(sliderImageTag);
        
        panelTag.addTextChild(createJavaScriptToGenerateLabels());
        
        strBuilder.append(panelTag.toString());
        strBuilder.append(LINE_DELIMITER);
        return strBuilder.toString();
    }
    
    private String createJavaScriptToGenerateLabels(){
        String[] labels = {CLOUD_STRING_1,CLOUD_STRING_2, CLOUD_STRING_3, CLOUD_STRING_4, CLOUD_STRING_5, CLOUD_STRING_6, CLOUD_STRING_7,
                CLOUD_STRING_8, CLOUD_STRING_9, CLOUD_STRING_10};
        
        StringBuilder jsBuilder = new StringBuilder();
        jsBuilder.append(START_SCRIPLET_TAG);
        jsBuilder.append(LINE_DELIMITER);
        
        //build the array in js that we will use to populate the tag cloud.
        jsBuilder.append("var labelArray = ["); // $NON-NLS-1$
        for(int i=0; i<labels.length; i++){
            String label = labels[i];
            if(StringUtil.isNotEmpty(label)){
                jsBuilder.append("\""+label+"\"");
                if(i<labels.length-1){
                    jsBuilder.append(",");
                }
            }
        }
        jsBuilder.append("];");
        
        //create the function to generate a random style.
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("function getRandomStyle(){"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("var rand = Math.floor(Math.random()*4);"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("if(rand==0){"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("return(\""+STYLE_1+"\");"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("}");
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("if(rand==1){"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("return(\""+STYLE_2+"\");"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("}");
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("if(rand==2){"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("return(\""+STYLE_3+"\");"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("}");
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("if(rand==3){"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("return(\""+STYLE_4+"\");"); // $NON-NLS-1$
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("}");
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append("}");
        jsBuilder.append(LINE_DELIMITER);
        
        //generate loop to create labels.
        jsBuilder.append("for (var x=0;x<labelArray.length;x++){"); // $NON-NLS-1$
        jsBuilder.append(END_SCRIPLET_TAG);
        jsBuilder.append(LINE_DELIMITER);
        //create the label tag with js bindings
        Tag labelTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        labelTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_VALUE, "labelArray[x]"); // $NON-NLS-1$
        labelTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_STYLE, "getRandomStyle()"); // $NON-NLS-1$
        jsBuilder.append(labelTag.toString());
        //close loop.
        jsBuilder.append(LINE_DELIMITER);
        jsBuilder.append(START_SCRIPLET_TAG);
        jsBuilder.append("}");
        jsBuilder.append(END_SCRIPLET_TAG);
        jsBuilder.append(LINE_DELIMITER);
        
        return jsBuilder.toString();
                
    }
    
}