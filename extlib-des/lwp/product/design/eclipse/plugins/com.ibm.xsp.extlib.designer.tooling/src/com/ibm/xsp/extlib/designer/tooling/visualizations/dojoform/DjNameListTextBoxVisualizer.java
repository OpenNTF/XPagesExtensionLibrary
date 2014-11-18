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
package com.ibm.xsp.extlib.designer.tooling.visualizations.dojoform;

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
 *  <xp:button value=" User Name" style="width:px;background-color:rgb(247,247,255);border-color:rgb(198,219,247);border-style:solid;border-width:1px;color:rgb(16,93,181)" icon="xsp://com.ibm.xsp.extlib.library~~/extlib/designer/markup/dojoform/DojoListTextBoxX.png" id="button1" dir="rtl">
 *  </xp:button>
 *  
 *  <xp:button value=" User Name" style="width:px;background-color:rgb(247,247,255);border-color:rgb(198,219,247);border-style:solid;border-width:1px;color:rgb(16,93,181)" icon="xsp://com.ibm.xsp.extlib.library~~/extlib/designer/markup/dojoform/DojoListTextBoxX.png" id="button2" dir="rtl">
 *  </xp:button>
 * 
 *  </xp:view>
 *
 */
public class DjNameListTextBoxVisualizer extends AbstractCommonControlVisualizer{

    private static final String LIST_TEXT_BOX_X_IMAGE = "DojoListTextBoxX.png"; // $NON-NLS-1$
    private static final String BUTTON_1_TEXT = "User Name"; // $NLX-DjNameListTextBoxVisualizer.UserName-1$
    private static final String BUTTON_2_TEXT = "User Name"; // $NLX-DjNameListTextBoxVisualizer.UserName.1-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        String imageURL = generateImageURL(nodeToVisualize, registry, LIST_TEXT_BOX_X_IMAGE, DOJO_FORM_IMAGES_LOCATION);
        StringBuilder strBuilder = new StringBuilder();
        
        Tag button1Tag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_BUTTON);
        button1Tag.addAttribute(XSPAttributeNames.XSP_ATTR_ID, "button1"); // $NON-NLS-1$
        button1Tag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:px;background-color:rgb(247,247,255);border-color:rgb(198,219,247);border-style:solid;border-width:1px;color:rgb(16,93,181)"); // $NON-NLS-1$
        button1Tag.addAttribute(XSPAttributeNames.XSP_ATTR_BUTTON_ICON, imageURL);
        button1Tag.addAttribute(XSPAttributeNames.XSP_ATTR_BIDI, XSP_ATTR_VAL_RTL);
        button1Tag.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, padString(BUTTON_1_TEXT));
        strBuilder.append(button1Tag.toString());
        
        Tag button2Tag = createTag(XP_PREFIX, XSPTagNames.XSP_TAG_BUTTON);
        button2Tag.addAttribute(XSPAttributeNames.XSP_ATTR_ID, "button2"); // $NON-NLS-1$
        button2Tag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:px;background-color:rgb(247,247,255);border-color:rgb(198,219,247);border-style:solid;border-width:1px;color:rgb(16,93,181)"); // $NON-NLS-1$
        button2Tag.addAttribute(XSPAttributeNames.XSP_ATTR_BUTTON_ICON, imageURL);
        button2Tag.addAttribute(XSPAttributeNames.XSP_ATTR_BIDI, XSP_ATTR_VAL_RTL);
        button2Tag.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, padString(BUTTON_2_TEXT));
        strBuilder.append(button2Tag.toString());
        
        return strBuilder.toString();
    }
    
    private String padString(String str){
        return " " + str;
    }
}