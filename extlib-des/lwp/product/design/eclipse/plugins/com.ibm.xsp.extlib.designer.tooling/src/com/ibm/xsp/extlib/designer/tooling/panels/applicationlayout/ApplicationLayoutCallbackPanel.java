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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeAdapter;
import com.ibm.commons.iloader.node.DataNodeListener;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.swt.controls.custom.CustomLabel;
import com.ibm.commons.swt.data.controls.DCCheckbox;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.ide.xsp.components.api.panels.XSPPropLayout1;
import com.ibm.xsp.extlib.designer.tooling.utils.ComputedFieldVetoHandler;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author mblout
 *
 */

public class ApplicationLayoutCallbackPanel extends XSPPropLayout1  {
    
    final static String FACET_ID_LEFT   = "facetLeft";   //$NON-NLS-1$
    final static String FACET_ID_NULL   = "facetMiddle"; //$NON-NLS-1$
    final static String FACET_ID_RIGHT  = "facetRight";  //$NON-NLS-1$
    final static String FACET_KEY_LEFT  = "LeftColumn";  //$NON-NLS-1$
    final static String FACET_KEY_RIGHT = "RightColumn"; //$NON-NLS-1$
    
    IPanelExtraData _extraData;
    DCCheckbox      _left;
    DCCheckbox      _middle;
    DCCheckbox      _right;
    
    private final DataNodeListener _listener = new DataNodeAdapter() {
        public void onValueChanged2( DataNode source, int record, Object object, IMember member ) {
//            ApplicationLayoutCallbackPanel.this.checkEnablement();
            // need to call this so checkboxes update when editing source directly
            source.notifyInvalidate(null);
        }
    };
    
    
    private class CheckVetoHandler extends ComputedFieldVetoHandler {
        
        public CheckVetoHandler(Control control) {
            super(control);
        }

        public boolean shouldSet(DataNode.ComputedField cf, Object instance, String value, DataChangeNotifier notifier) {
            boolean ok = true;
            if (null == value) {
                // do we need to check contents?
//                Element callback = getCallback(cf.getName(), cf.hasName());
//                if (null != callback && isCallbackModified(callback)) {
                String[] buttons = {"Yes", "No"}; // $NLX-ApplicationLayoutCallbackPanel.Yes-1$ $NLX-ApplicationLayoutCallbackPanel.No-2$
                MessageDialog dlg = new MessageDialog(ApplicationLayoutCallbackPanel.this.getShell(), "Domino Designer",  // $NLX-ApplicationLayoutCallbackPanel.DominoDesigner-1$
                        null, "Removing the drop target will remove the callback and any content it contains. Do you want to continue?",    // $NLX-ApplicationLayoutCallbackPanel.Removingthedroptargetwillremoveth-1$
                        MessageDialog.WARNING, buttons, 1);
                
                int result = dlg.open();
                ok = (result == 0);  
//                }
            }  
            return ok;
        }
        
        public void updateControl(DataNode.ComputedField cf) {
            getDataNode().notifyInvalidate(null);
        }
    };

    public ApplicationLayoutCallbackPanel(IPanelExtraData extra, Composite parent) {
    super(parent, SWT.NONE);

    initialize();
    GridData gd = createFillGD(getNumParentColumns());
    if (parent.getLayoutData() instanceof GridData) {
        int width = ((GridData)parent.getLayoutData()).widthHint;
        if (width > 0)
            gd.widthHint = width;
    }
    setLayoutData(gd);
    setExtraData(extra);
    setCurrentParent(this);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.verticalSpacing = 10;
    this.setLayout(gridLayout);

    createCallbackControls();
}


    
    public void setExtraData(IPanelExtraData data) {
        _extraData = data;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout1#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createContents() {
        super.createContents();
        createCallbackControls();
    }
    
    
    private Label createWrapLabel(String text) {
        Composite parent = getCurrentParent();
        
        Label t = new CustomLabel(parent, SWT.WRAP | SWT.READ_ONLY, ""); //$NON-NLS-1$
        t.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        GridData gd = createSpanGD(2);
        if (parent.getLayoutData() instanceof GridData) {
            int width = ((GridData)parent.getLayoutData()).widthHint;
            if (width > getControlIndentAmt())
                gd.widthHint = width - getControlIndentAmt();
        }
        t.setLayoutData(gd);
        t.setText(text);
        return t;
    }

    
    private GridData getIndentGD() {
        GridData gd = createSpanGD(2);
        gd.horizontalIndent = getControlIndentAmt();
        return gd;
    }
    
    private void createCallbackControls() {

        getDataNode().addDataNodeListener(_listener);
        
        FacesRegistry reg = _extraData.getDesignerProject().getFacesRegistry();
        
        String msg1 = "Drag a control into the column if you want the control to appear on each XPage that contains this custom control (for example, an outline)."; // $NLX-ApplicationLayoutCallbackPanel.Dragacontrolintothecolumnifyouwan-1$
        String msg2 = "Or, enable the column as a drop target if you want to specify what should appear in the column on each XPage containing this custom control (for example, to show a different view on each page)."; // $NLX-ApplicationLayoutCallbackPanel.Orenablethecolumnasadroptargetify-1$
        
        String labeltext = "Enable drop target:";  // $NLX-ApplicationLayoutCallbackPanel.Enabledroptarget-1$

        createWrapLabel(msg1);
        createWrapLabel(msg2);
        
        createWrapLabel(labeltext);
        
        CallbackComputedField cfLeft = new CallbackComputedField(FACET_ID_LEFT, FACET_KEY_LEFT, true, reg); 
        getDataNode().addComputedField(cfLeft);
        _left = createDCCheckBox(FACET_ID_LEFT, Boolean.toString(true), null, "Left column", getIndentGD(), "applayout.cbpanel.left"); // $NON-NLS-2$ $NLX-ApplicationLayoutCallbackPanel.Leftcolumn-1$
        _left.setMultiSel(false);
        cfLeft.setVetoHandler(new CheckVetoHandler(_left));

        CallbackComputedField cfMiddle = new CallbackComputedField(FACET_ID_NULL, null, false, reg); 
        getDataNode().addComputedField(cfMiddle);
        _middle = createDCCheckBox(FACET_ID_NULL, Boolean.toString(true), null, "Middle column", getIndentGD(), "applayout.cbpanel.middle"); // $NON-NLS-2$ $NLX-ApplicationLayoutCallbackPanel.Middlecolumn-1$
        _middle.setMultiSel(false);
        cfMiddle.setVetoHandler(new CheckVetoHandler(_middle));

        CallbackComputedField cfRight= new CallbackComputedField(FACET_ID_RIGHT, FACET_KEY_RIGHT, true, reg); 
        getDataNode().addComputedField(cfRight);
        _right = createDCCheckBox(FACET_ID_RIGHT, Boolean.toString(true), null, "Right column", getIndentGD(), "applayout.cbpanel.right");  // $NON-NLS-2$ $NLX-ApplicationLayoutCallbackPanel.Rightcolumn-1$
        _right.setMultiSel(false);
        cfRight.setVetoHandler(new CheckVetoHandler(_right));
        
        String pstext = "If you don’t want a column to appear, leave it empty."; // $NLX-ApplicationLayoutCallbackPanel.Ifyoudontwantacolumntoappearleave-1$
        createLabel(pstext, createSpanGD(2));

    }
    

    public ApplicationLayoutCallbackPanel(Composite parent, int style) {
        super(parent, style);
    }
        
}