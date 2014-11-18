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

package com.ibm.xsp.extlib.designer.tooling.palette.mobile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

public class MobileSwitchDropAction extends XPagesPaletteDropActionDelegate {
    
    private static String DEFAULT_LEFT_LABEL = "ON"; // $NLX-MobileSwitchDropAction.ON-1$
    private static String DEFAULT_RIGHT_LABEL = "OFF"; // $NLX-MobileSwitchDropAction.OFF-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate#createElement(org.w3c.dom.Document, java.lang.String)
     */
    @Override
    protected Element createElement(Document doc, String prefix) {
        Element element = super.createElement(doc, prefix);
        XPagesDOMUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_LEFT_LABEL, DEFAULT_LEFT_LABEL);
        XPagesDOMUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_RIGHT_LABEL, DEFAULT_RIGHT_LABEL);
        return element;
    }
    
}