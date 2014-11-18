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

import org.w3c.dom.Element;

import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class DojoButtonDropAction extends XPagesPaletteDropActionDelegate {

    /**
     * 
     */
    public DojoButtonDropAction() {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate#fillDefaultProperties(org.w3c.dom.Element)
     */
    @Override
    protected void fillDefaultProperties(Element element) {
        super.fillDefaultProperties(element);
        XPagesDOMUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, "Dojo Button"); // $NLX-DojoButtonDropAction.DojoButton-1$
    }
}