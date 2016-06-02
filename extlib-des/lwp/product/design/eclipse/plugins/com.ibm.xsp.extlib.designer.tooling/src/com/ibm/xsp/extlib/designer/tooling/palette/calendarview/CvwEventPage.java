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

package com.ibm.xsp.extlib.designer.tooling.palette.calendarview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizardPage;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CvwEventPage extends AbstractWizardPage implements SelectionListener {
    
    private static String _DELETE_CONFIRM_MSG = "Delete this calendar entry?"; // $NLX-CvwEventPage.Deletethiscalendarentry-1$
    
    private Button  _newCheckbox;
    private Label   _newLabel;
    private Combo   _newCombo;
    private Button  _openCheckbox;
    private Label   _openLabel;
    private Combo   _openCombo;
    private Button  _delCheckbox;
    private Label   _delLabel;
    private Text    _delEdit;
    private Button  _reschedCheckbox;
    private Label   _reschedLabel;
    private Combo   _reschedCombo;

    protected CvwEventPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return "Calendar Events"; // $NLX-CvwEventPage.CalendarEvents-1$
    }

    @Override
    protected String getPageMsg() {
        return "Configure the events for this calendar"; // $NLX-CvwEventPage.Configuretheeventsforthiscalendar-1$
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);

        String[] xpages = getXPages();
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(2, 5));
        
        _newCheckbox = WizardUtils.createCheckBox(container, "New entry", 2, true); // $NLX-CvwEventPage.Newentry-1$
        _newCheckbox.addSelectionListener(this);
        _newLabel = WizardUtils.createLabel(container, "Target XPage: ", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-CvwEventPage.TargetXPage-1$
        _newCombo = WizardUtils.createEditCombo(container, 1, xpages, -1, null);

        _openCheckbox = WizardUtils.createCheckBox(container, "Open entry", 2, true); // $NLX-CvwEventPage.Openentry-1$
        _openCheckbox.addSelectionListener(this);
        _openLabel = WizardUtils.createLabel(container, "Target XPage: ", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-CvwEventPage.TargetXPage-1$
        _openCombo = WizardUtils.createEditCombo(container, 1, xpages, -1, null);
        
        _delCheckbox = WizardUtils.createCheckBox(container, "Delete entry", 2, true); // $NLX-CvwEventPage.Deleteentry-1$
        _delCheckbox.addSelectionListener(this);
        _delLabel = WizardUtils.createLabel(container, "Confirmation message: ", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-CvwEventPage.Confirmationmessage-1$
        _delEdit = WizardUtils.createText(container, _DELETE_CONFIRM_MSG);

        _reschedCheckbox = WizardUtils.createCheckBox(container, "Reschedule entry", 2, true); // $NLX-CvwEventPage.Rescheduleentry-1$
        _reschedCheckbox.addSelectionListener(this);
        _reschedLabel = WizardUtils.createLabel(container, "Target XPage: ", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-CvwEventPage.TargetXPage-1$
        _reschedCombo = WizardUtils.createEditCombo(container, 1, xpages, -1, null);

        setControl(container);
        setPageComplete(true);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _newCheckbox) {
            _newLabel.setEnabled(_newCheckbox.getSelection());
            _newCombo.setEnabled(_newCheckbox.getSelection());
        } else if (event.widget == _openCheckbox) {
            _openLabel.setEnabled(_openCheckbox.getSelection());
            _openCombo.setEnabled(_openCheckbox.getSelection());
        } else if (event.widget == _delCheckbox) {
            _delLabel.setEnabled(_delCheckbox.getSelection());
            _delEdit.setEnabled(_delCheckbox.getSelection());
        } else if (event.widget == _reschedCheckbox) {
            _reschedLabel.setEnabled(_reschedCheckbox.getSelection());
            _reschedCombo.setEnabled(_reschedCheckbox.getSelection());
        }
        
        getWizard().getContainer().updateButtons();
    }

    public boolean addOpenEvent() {
        return WizardUtils.getCheckBoxValue(_openCheckbox, true);
    }
    
    public String getOpenEventXPage() {
        return addExtension(WizardUtils.getComboText(_openCombo, ""));
    }
    
    public boolean addReschedEvent() {
        return WizardUtils.getCheckBoxValue(_reschedCheckbox, true);
    }
    
    public String getReschedEventXPage() {
        return addExtension(WizardUtils.getComboText(_reschedCombo, ""));
    }
    
    public boolean addDeleteEvent() {
        return WizardUtils.getCheckBoxValue(_delCheckbox, true);
    }
    
    public String getDeleteEventConfirmMsg() {
        return WizardUtils.getTextValue(_delEdit, _DELETE_CONFIRM_MSG);
    }
    
    public boolean addNewEvent() {
        return WizardUtils.getCheckBoxValue(_newCheckbox, true);
    }
    
    public String getNewEventXPage() {
        return addExtension(WizardUtils.getComboText(_newCombo, ""));
    }

    public String addExtension(String xpage) {
        xpage = xpage.trim();
        if (StringUtil.isNotEmpty(xpage)) {
            if (!xpage.toLowerCase().endsWith(".xsp")) { // $NON-NLS-1$
                xpage += ".xsp"; // $NON-NLS-1$
            }
        } else {
            // Nothing chosen - use the current XPage
            xpage = ExtLibPanelUtil.getActiveEditor().getEditorInput().getName();
        }
        return xpage;
    }
    
    public String[] getXPages() {
        DesignerDELookup xl = DesignerDELookup.getXPagesLookup(((CalendarViewDropWizard)_wiz).getPanelData().getDesignerProject());
        String[] result = new String[xl.size()];
        for (int i=0; i< xl.size(); i++) {
            result[i] = xl.getLabel(i);
        }
        return result;
    }
}