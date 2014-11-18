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
package com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;

public class ApplicationLayoutDropAction extends XPagesPaletteDropActionDelegate {
    
    @Override
    protected Element createElement(Document doc, String prefix) {
        
        boolean isCustom = XPagesPropertiesViewUtils.isEditingCustomControl(getEditorPart());
        
        // show the warning if we're NOT a custom control, and the preference allows the warning.
        boolean shouldContinue = isCustom || showWarning();
        
        Element element = null;
        
        if (shouldContinue) {
            element = openConfigurationDialog(doc, prefix);
        }
        return element;
    }
    
    
    
    private boolean showWarning() {
        
        IPreferenceStore prefs = ExtLibToolingPlugin.getDefault().getPreferenceStore();
        boolean bHidePrompt = true;
        if (prefs != null) {
            bHidePrompt = prefs.getBoolean(ExtLibToolingPlugin.PREFKEY_HIDE_XPAGE_WARNING);
        }
        
        if (bHidePrompt)
            return true;

        Shell shell = getControl().getShell();
        
        String msg = "You are placing the Application Layout control on an XPage.\n\nThe Application Layout control is most effective when placed in a custom control, where you can configure the layout once and then use the custom control on multiple pages."; // $NLX-ApplicationLayoutDropAction.YouareplacingtheApplicationLayout-1$
        
        MessageDialogWithToggle dlg = new MessageDialogWithToggle(
          shell, 
          "Application Layout",  // $NLX-ApplicationLayoutDropAction.ApplicationLayout-1$
          null, // image 
          msg,  
          MessageDialog.WARNING, 
          new String[]{ "Continue", "Cancel" }, // $NLX-ApplicationLayoutDropAction.Continue-1$ $NLX-ApplicationLayoutDropAction.Cancel-2$
          1,
          "Do not show this message again",   // $NLX-ApplicationLayoutDropAction.Donotshowthismessageagain-1$
          bHidePrompt) {
            // this is necessary because "Continue" maps to IDialogConstants.INTERNAL_ID, 
            // (and prefs are stored as "always" or "never" by the dialog)
            // if "Continue" text changes, the use of IDialogConstants.INTERNAL_ID may need to change.
            protected void buttonPressed(int buttonId) {
                super.buttonPressed(buttonId);
                if (buttonId == IDialogConstants.INTERNAL_ID  && getPrefStore() != null && getPrefKey() != null) {
                    getPrefStore().setValue(getPrefKey(), getToggleState());
                }
            }
        };
        
        dlg.setPrefKey(ExtLibToolingPlugin.PREFKEY_HIDE_XPAGE_WARNING);
        dlg.setPrefStore(prefs);
        
        int code = dlg.open();
        boolean bShouldContinue = (code == IDialogConstants.INTERNAL_ID); 
        
        return bShouldContinue;
    }
    
    private Element openConfigurationDialog(Document doc, String prefix) {
        // Create the AppLication Layout element
        Element element = super.createElement(doc, prefix); 
        
        // Setup the panel data
        PanelExtraData panelData = new PanelExtraData();
        panelData.setDesignerProject(getDesignerProject());
        panelData.setNode(element);
        
        // Must do the following or the property editor does not function properly
        panelData.setWorkbenchPart(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor());
        panelData.setHostWorkbenchPart(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor());        
        
        // Launch the Wizard
        Shell shell = getControl().getShell();
        ApplicationLayoutDropWizard wiz = new ApplicationLayoutDropWizard(shell, panelData);
        WizardDialog dialog = new WizardDialog(shell, wiz);
        dialog.addPageChangingListener(wiz);

        if (Dialog.OK != dialog.open()) {
               return null;
        }
        
        return element;
    }
    
    
}