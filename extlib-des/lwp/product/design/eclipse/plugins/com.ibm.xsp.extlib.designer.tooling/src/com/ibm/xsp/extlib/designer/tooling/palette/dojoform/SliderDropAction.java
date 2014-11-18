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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;

public class SliderDropAction extends XPagesPaletteDropActionDelegate {
    
    private static String HORIZONTAL_SLIDER_DEFAULT_STYLE = "margin-left:5px;width:200px;height:20px"; // $NON-NLS-1$
    private static String VERTICAL_SLIDER_DEFAULT_STYLE = "margin-left:5px;width:75px;height:80px"; // $NON-NLS-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate#createElement(org.w3c.dom.Document, java.lang.String)
     */
    @Override
    protected Element createElement(Document doc, String prefix) {
        Element element = openConfigurationDialog(doc, prefix);
        return element;
    }
    
    /**
     * This method is used to open the Slider Drop Dialog. The dialog is used to add labels and rules to the slider.
     * The dialog will add any required child elements to the slider element we pass into it. 
     * @param doc
     * @param prefix
     * @return The slider element complete with labels and rules as added by the Slider Drop Dialog
     */
    private Element openConfigurationDialog(Document doc, String prefix) { 
        Element element = createDefaultSlider(doc, prefix); 
        Shell shell = getControl().getShell();
        SliderDropDialog dialog = new SliderDropDialog(shell, element, getFacesRegistry());
        if (Dialog.OK != dialog.open()) {
               return null;
        }
        return element;
    }

    /**
     * This method will create the default slider element that will be dropped to the page if none of the extra
     * labels or rules are added by the Slider Drop Dialog
     * @param doc
     * @param prefix
     * @return a Slider element complete with number converter and default style. 
     */
    private Element createDefaultSlider(Document doc, String prefix){
        Element element = super.createElement(doc, prefix); 
        if(SliderDropRulesUtil.isHorizontalSlider(element)){
            element.setAttribute(XSPAttributeNames.XSP_ATTR_STYLE, HORIZONTAL_SLIDER_DEFAULT_STYLE);
        }
        else{
            element.setAttribute(XSPAttributeNames.XSP_ATTR_STYLE, VERTICAL_SLIDER_DEFAULT_STYLE);
        }
        addNumberConverterToElement(element);
        return element;
    }
    
    /**
     * This method will add a number converter to the given element.  
     * @param element
     */
    private void addNumberConverterToElement(Element element){
        Element thisConverterElement = XPagesDOMUtil.addComplexProperty(element, XSPAttributeNames.XSP_ATTR_CONVERTER, null);
        Element convertNumberElement = XPagesDOMUtil.createChildElement(thisConverterElement, getFacesRegistry(), XSPTagNames.XSP_TAG_CONVERTER_CONVERT_NUMBER);
        XPagesDOMUtil.setAttribute(convertNumberElement, XSPAttributeNames.XSP_ATTR_INTEGER_ONLY, "true"); // $NON-NLS-1$
    }

}