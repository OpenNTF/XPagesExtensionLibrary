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
package com.ibm.xsp.extlib.designer.tooling.panels.core.common;

import org.eclipse.swt.widgets.Composite;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlBasicsPanel;

/**
 * @author doconnor
 *
 */
public class BasicsPanelWithLabel extends JavaControlBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public BasicsPanelWithLabel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel#hasValue()
     */
    @Override
    protected boolean hasValue() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel#getValueAttr()
     */
    @Override
    protected String getValueAttr() {
        return XSPAttributeNames.XSP_ATTR_LABEL;
    }
}
