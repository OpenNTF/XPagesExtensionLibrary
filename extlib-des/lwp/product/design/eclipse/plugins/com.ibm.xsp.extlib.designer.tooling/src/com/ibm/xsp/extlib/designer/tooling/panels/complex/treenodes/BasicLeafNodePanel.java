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
package com.ibm.xsp.extlib.designer.tooling.panels.complex.treenodes;

import org.eclipse.swt.widgets.Composite;

import com.ibm.designer.domino.xsp.api.panels.complex.DynamicPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class BasicLeafNodePanel extends DynamicPanel {

    /**
     * @param parent
     */
    public BasicLeafNodePanel(Composite parent) {
        super(parent);
    }

    /**
     * @param parent
     * @param style
     */
    public BasicLeafNodePanel(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void createContents(Composite parent) {
        createLabel("Label:", null); // $NLX-BasicLeafNodePanel.Label-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_LABEL, null); 
    }
}