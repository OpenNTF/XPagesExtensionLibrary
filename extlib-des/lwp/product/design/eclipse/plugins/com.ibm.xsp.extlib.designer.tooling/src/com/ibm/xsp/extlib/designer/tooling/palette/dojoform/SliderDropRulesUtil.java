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
package com.ibm.xsp.extlib.designer.tooling.palette.dojoform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class SliderDropRulesUtil {
    
    public static String HORIZONTAL_SLIDER_TITLE = "Add Horizontal Slider"; // $NLX-SliderDropDialog.AddHorizontalSlider-1$
    public static String VERTICAL_SLIDER_TITLE = "Add Vertical Slider"; // $NLX-SliderDropDialog.AddVerticalSlider-1$
    public static String ADD_LABELS_ABOVE_TEXT = "Add labels above"; // $NLX-SliderDropDialog.Addlabelsabove-1$
    public static String ADD_LABELS_BELOW_TEXT = "Add labels below"; // $NLX-SliderDropDialog.Addlabelsbelow-1$
    public static String ADD_RULES_ABOVE_TEXT = "Add rules above"; // $NLX-SliderDropDialog.Addrulesabove-1$
    public static String ADD_RULES_BELOW_TEXT = "Add rules below"; // $NLX-SliderDropDialog.Addrulesbelow-1$
    public static String ADD_LABELS_TO_RIGHT_TEXT = "Add labels to the right"; // $NLX-SliderDropDialog.Addlabelstotheright-1$
    public static String ADD_LABELS_TO_LEFT_TEXT = "Add labels to the left"; // $NLX-SliderDropDialog.Addlabelstotheleft-1$
    public static String ADD_RULES_TO_RIGHT_TEXT = "Add rules to the right"; // $NLX-SliderDropDialog.Addrulestotheright-1$
    public static String ADD_RULES_TO_LEFT_TEXT = "Add rules to the left"; // $NLX-SliderDropDialog.Addrulestotheleft-1$
    public static String MESSAGE_TEXT = "Choose where to show labels and rules (tick marks)."; // $NLX-SliderDropDialog.Choosewheretoshowlabelsandrulesti-1$
    
    public static String HORIZONTAL_LABELS_STYLE = "height:10px;font-size:75%;color:gray"; // $NON-NLS-1$
    public static String HORIZONTAL_RULES_STYLE = "height:5px"; // $NON-NLS-1$
    public static String VERTICAL_LABELS_STYLE = "height:80px;width:25px;font-size:75%;color:gray"; // $NON-NLS-1$
    public static String VERTICAL_RULES_STYLE = "height:80px;width:5px"; // $NON-NLS-1$
    
    public static int DEFAULT_LABEL_COUNT = 6;
    public static int DEFAULT_RULE_COUNT = 6;
    /**
     * This method will create a element with a given tagName using the ExtLib prefix. Calling XPagesDOMUtil.createChildElement would
     * have created the tag with an XP prefix which would be incorrect. This mirrors what XPagesDOMUtil.createChildElement does, but 
     * creates the element using the XE prefix. 
     * @param parentElement
     * @param registry
     * @param tagName
     * @return the element that we have created and added as a child of the parentElement.
     */
    public static Element createChildExtLibElement(Element parentElement, FacesRegistry registry, String tagName) {
        Document doc = parentElement.getOwnerDocument();
        Element element = XPagesDOMUtil.createElement(doc, registry, IExtLibRegistry.EXT_LIB_NAMESPACE_URI, tagName);
        if (null != element) {
            //make sure the id for the control is unique
            parentElement.appendChild(element);
            XPagesDOMUtil.formatNode(parentElement, null);
        }
        return element;
    }
    
    public static Element addSliderRuleChildToElement(Element sliderElement, FacesRegistry facesRegistry, String container){
        Element ruleElement = SliderDropRulesUtil.createChildExtLibElement(sliderElement, facesRegistry, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE);
        if(isHorizontalSlider(sliderElement)){
            ruleElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_STYLE, HORIZONTAL_RULES_STYLE);
        }
        else{
            ruleElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_STYLE, VERTICAL_RULES_STYLE);
        }
        ruleElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_COUNT, String.valueOf(DEFAULT_RULE_COUNT));
        ruleElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_CONTAINER, container);
        return ruleElement;
    }
    
    /**
     * Helper method to determine if an element is a djHorizontalSlider tag. 
     * @param slider - the slider element
     * @return true if slider is a Horizontal Slider, false otherwise.
     */
    public static boolean isHorizontalSlider(Element slider){
        if(null != slider && StringUtil.equals(slider.getLocalName(),IExtLibTagNames.EXT_LIB_TAG_HORIZONTAL_SLIDER)){
            return true;
        }
        return false;
    }
    
   
    
    /**
     * This method will add a configured djSliderRuleLabels element as a child of our _sliderElement
     * @param container -  the container area that we want the slider rule labels to appear in. 
     */
    public static Element addSliderRuleLabelsChildToElement(Element sliderElement, FacesRegistry facesRegistry, String container){
        Element labelsElement = SliderDropRulesUtil.createChildExtLibElement(sliderElement, facesRegistry, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES);
        if(isHorizontalSlider(sliderElement)){
            labelsElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_STYLE, HORIZONTAL_LABELS_STYLE);
        }
        else{
            labelsElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_STYLE, VERTICAL_LABELS_STYLE);
        }
        labelsElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_COUNT, String.valueOf(DEFAULT_LABEL_COUNT));
        labelsElement.setAttribute(IExtLibAttrNames.EXT_LIB_ATTR_CONTAINER, container);
        return labelsElement;
    }
}
