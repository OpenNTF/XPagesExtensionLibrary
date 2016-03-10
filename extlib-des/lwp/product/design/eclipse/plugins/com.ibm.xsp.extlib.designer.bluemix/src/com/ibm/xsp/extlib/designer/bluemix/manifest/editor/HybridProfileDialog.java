/*
 * © Copyright IBM Corp. 2016
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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.data.dialog.SimpleDialog;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.EditTableItem;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.preference.HybridProfile;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage.ProfileListItem;

/**
 * @author Gary Marjoram
 *
 */
public class HybridProfileDialog extends SimpleDialog implements SelectionListener, IDoubleClickListener {
    private final ArrayList<EditTableItem> _profileList;
    private ManifestTableEditor _hybridTableEditor;
    private HybridProfile _profile;
    
    public HybridProfileDialog(Shell shell) {
        super(shell);
        _profileList = new ArrayList<EditTableItem>();
        for (int i=0; i < HybridProfile.MAX_HYBRID_PROFILES; i++) {
            HybridProfile profile = HybridProfile.load(i);
            if (profile != null) {
                _profileList.add(new ProfileListItem(profile));
            }
        }
    }

    @Override
    protected String getMessage() {
        return "Select the hybrid profile to load for this application."; // $NLX-HybridProfileDialog.Selectthehybridprofiletoloadforth-1$
    }

    @Override
    protected String getDialogTitle() {
        return BluemixUtil.productizeString("%BM_PRODUCT%"); // $NON-NLS-1$
    }

    @Override
    protected void fillClientArea(Composite parent) {
        parent.setLayout(new FillLayout());          
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(SWTLayoutUtils.createLayoutDefaultSpacing(1));
        _hybridTableEditor = new ManifestTableEditor(composite, 1, new String[]{"name"}, new String[]{"Profile Name"}, true, false, 8, 60, "hybrid.profile.table.id", _profileList, false, null, this, this);     // $NON-NLS-1$ $NON-NLS-3$ $NLX-HybridProfileDialog.ProfileName-2$
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        int row = _hybridTableEditor.getSelectedRow();
        if((row >= 0) && (row < _profileList.size())) {
            _profile = ((ProfileListItem)_profileList.get(_hybridTableEditor.getSelectedRow())).getProfile();
        } else {
            _profile = null;
        }
    }
    
    @Override
    protected void validateDialog() {
        if (_profile == null) {
            invalidateDialog("");
            return;
        }
        super.validateDialog();
    }
    
    public HybridProfile getSelectedProfile() {
        return _profile;
    }

    @Override
    public void doubleClick(DoubleClickEvent event) {
        if (_profile != null) {
            setReturnCode(OK);
            close();
        } 
    }

    @Override
    protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
        return true;
    }
}