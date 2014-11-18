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
package com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout;


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_ONEUI_CONFIGURATION;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.designer.domino.xsp.editor.EditorUtils;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanelNoOptions;
import com.ibm.xsp.extlib.designer.tooling.utils.ComputedFieldVetoHandler;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class ApplicationLayoutBasicsPanel extends XSPBasicsPanelNoOptions {
    
    
    private class VetoHandler extends ComputedFieldVetoHandler {
        
        public VetoHandler(Control control) {
            super(control);
        }

        public boolean shouldSet(DataNode.ComputedField cf, Object instance, String value, DataChangeNotifier notifier) {
            boolean doit = showConfigWarning();
            return doit;
        }
        
        public void updateControl(DataNode.ComputedField cf) {
            getDataNode().notifyInvalidate(null);
        }
    };


    
    /**
     * @param parent
     * @param style
     */
    public ApplicationLayoutBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }
    
// we want the "config" combo to appear before the visible checkbox, so this won't work    
//    protected void createPostSection() {
//        createAppLayoutControls(getCurrentParent());
//    }

   
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.BasicsPanel#createAutomatedSection()
     */
    @Override
    protected void createAutomatedSection() {
        if (hasName()) {
            createName();
        }
        
        createAppLayoutControls(getCurrentParent());

        if (hasVisible()) {
            createVisible();
        }
    }
    

    protected void createAppLayoutControls(Composite parent) {
        DataNode real = DCUtils.findDataNode(parent, true);
        
        FacesRegistry registry = getExtraData().getDesignerProject().getFacesRegistry();
        ConfigurationField configField = new ConfigurationField(getDataNode(), registry);
        real.addComputedField(configField);
        
        createLabel("Configuration:", null, getLabelToolTipText(EXT_LIB_TAG_ONEUI_CONFIGURATION));  // $NLX-ApplicationLayoutBasicsPanel.Configuration-1$
        DCCompositeCombo combo = createComboComputed(configField.getName(), configField.getLookup(), createControlGDFill(getNumLeftColumns() - 1), false, false);
        combo.setIsComputable(false);
        
        configField.setVetoHandler(new VetoHandler(combo));

    }
    

    
    @Override
    protected void createRightContents(Composite rightChild) {
        if (isCustomControl()) {
            Composite p = rightChild;
            
            Group group = new Group(p, SWT.NONE);
            setCurrentParent(group);
            group.setLayout(createChildLayout(2));
            group.setText("Content area"); // $NLX-ApplicationLayoutBasicsPanel.Callbacks-1$
            GridData gd = createControlGDNoWidth(1);
            gd.widthHint = 360;
            group.setLayoutData(gd);
    
            new ApplicationLayoutCallbackPanel(getExtraData(), getCurrentParent());
    
            setCurrentParent(p);
        }
        else {
            super.createRightContents(rightChild);
        }
            

    }

    private boolean isCustomControl() {
        IEditorPart editor = EditorUtils.getActiveEditor();
        boolean isCustomControl = XPagesPropertiesViewUtils.isEditingCustomControl(editor);
        return isCustomControl;
    }
    

    private boolean showConfigWarning() {
        String msg = "If you change the configuration, all attribute values associated with the current configuration will be lost. Do you want to continue?"; // $NLX-ApplicationLayoutBasicsPanel.Youareabouttochangetheconfigurati-1$
        MessageDialog dlg = new MessageDialog(
                getShell(), 
                "Domino Designer", // $NLX-ApplicationLayoutDropDialog.Dominodesigner-1$
                null, // image 
                msg,  
                MessageDialog.WARNING, 
                new String[]{ "Continue", "Cancel" }, // $NLX-ApplicationLayoutBasicsPanel.Continue-1$ $NLX-ApplicationLayoutBasicsPanel.Cancel-2$
                1);

        int code = dlg.open();
        // "Continue" was returning 256.. but then started returning 0 at some point...did SWT version change (it was pending)?
        boolean bShouldContinue = (code == MessageDialog.OK);  //Only continue if 'OK' (Continue) is pressed - otherwise bail out

        return bShouldContinue;
    }
}