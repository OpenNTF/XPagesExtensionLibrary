/*
 * © Copyright IBM Corp. 2014, 2016
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

package com.ibm.xsp.extlib.designer.tooling.palette.calendarview;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;

/**
 * @author Gary Marjoram
 *
 */
public class CalendarViewDropAction extends XPagesPaletteDropActionDelegate {
    @Override
    protected Element createElement(Document doc, String prefix) {        
        return openConfigurationWizard(doc, prefix);
    }
    
    private Element openConfigurationWizard(Document doc, String prefix) { 
        // We must create an element for the panelData
        // We might overwrite this depending on chosen wizard options
        Element element = super.createElement(doc, prefix);

        // Setup the Panel Data
        PanelExtraData panelData = new PanelExtraData();
        panelData.setDesignerProject(getDesignerProject());
        panelData.setNode(element);
        panelData.setDocument(doc);

        // Launch the Wizard
        Shell shell = getControl().getShell();
        CalendarViewDropWizard wiz = new CalendarViewDropWizard(shell, panelData);
        WizardDialog dialog = new WizardDialog(shell, wiz);
        dialog.addPageChangingListener(wiz);
        if (WizardDialog.OK != dialog.open()) {
            return null;
        }
        
        // Return the root element
        return (Element) panelData.getNode();
    }        
}
