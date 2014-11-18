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
package com.ibm.xsp.extlib.designer.tooling.panels.data;

import org.eclipse.swt.widgets.Composite;

import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlDataPanel;

/**
 * @author doconnor
 *
 * This class has been added to override a bug in the core data panel, whereby the converter
 * attribute is associated with the wrong attribute name. This class will be removed post 854.
 */
public class ExtLibDataPanel extends JavaControlDataPanel {
    /**
     * @param parent
     * @param style
     */
    public ExtLibDataPanel(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * @param parent
     * @param style
     * @param useFirstColumn
     * @param useSecondColumn
     */
    public ExtLibDataPanel(Composite parent, int style, boolean useFirstColumn, boolean useSecondColumn) {
        super(parent, style, useFirstColumn, useSecondColumn);
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlDataPanel#isSecondColumnUsed()
     */
    @Override
    protected boolean isSecondColumnUsed() {
        return false;  //we have to hide the converter panel due to a bug in the attr detection code.. Will be fixed in 854
    }
    
    

}
