/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.dbproperties.XSPProperties;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class SinglePageAppDropAction extends XPagesPaletteDropActionDelegate {

    @Override
    protected Element createElement(Document doc, String prefix) {        
        return openConfigurationWizard(doc, prefix);
    }
    
    private Element openConfigurationWizard(Document doc, String prefix) {
        
        Shell shell = getControl().getShell();
        
        // Setup the Panel Data
        Element element = super.createElement(doc, prefix); 
        PanelExtraData panelData = new PanelExtraData();
        panelData.setDesignerProject(getDesignerProject());
        panelData.setNode(element);
                
        // Check that the mobile prefix has been configured
        // and that the XPage has the correct prefix
        if (checkXPagePrefix(panelData) == false) {
            return null;
        }
        
        // Launch the Wizard
        SinglePageAppDropWizard wiz = new SinglePageAppDropWizard(shell, panelData);
        WizardDialog dialog = new WizardDialog(shell, wiz);
        dialog.addPageChangingListener(wiz);
        dialog.addPageChangedListener(wiz);
        if (WizardDialog.OK != dialog.open()) {
               return null;
        }
        
        return element;
    }    
    
    //
    // Function to check that the mobile prefix has been configured and 
    // that the prefix for the XPage is correct
    //
    public boolean checkXPagePrefix(PanelExtraData panelData) {
        try {
            String errorMsg = "Your mobile application will not display correctly as a result."; // $NLX-SinglePageAppDropAction.Yourmobileapplicationwillnotrende-1$
            IDominoDesignerProject designerProject = DominoResourcesPlugin.getDominoDesignerProject(panelData.getDesignerProject().getProject());
            XSPProperties properties = new XSPProperties(designerProject);
            Properties props = properties.getPropertiesObj();
            String mobilePrefix = props.getProperty(XspPropertyConstants.XSP_MOBILE_THEME, "");
            if (StringUtil.isEmpty(mobilePrefix)) {
                String msg = "No mobile prefix has been specified in XSP Properties.\n" +  errorMsg; // $NLX-SinglePageAppDropAction.NomobileprefixhasbeenspecifiedinX-1$                             
                return WizardUtils.displayContinueDialog(getControl().getShell(), WizardData.WIZARD_TITLE, msg);
            } else {
                String xpageName = WizardUtils.getXPageFileName();
                if (!xpageName.startsWith(mobilePrefix)) {
                    String msg = MessageFormat.format("The XPage you are editing does not have the correct mobile prefix \"{0}\".\n", mobilePrefix) + errorMsg; // $NLX-SinglePageAppDropAction.TheXPageyouareeditingdoesnothavet-1$                                 
                    return WizardUtils.displayContinueDialog(getControl().getShell(), WizardData.WIZARD_TITLE, msg); 
                }
            }
        } catch (Exception e) {
            // No big deal, we simply wont display a warning
        }        
        
        return true;
    }
    
}