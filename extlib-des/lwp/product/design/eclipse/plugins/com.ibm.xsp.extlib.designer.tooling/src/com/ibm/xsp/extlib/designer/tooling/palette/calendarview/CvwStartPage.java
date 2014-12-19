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

package com.ibm.xsp.extlib.designer.tooling.palette.calendarview;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CvwStartPage extends WizardPage implements SelectionListener {
    private static final String PAGE_NAME   = "WizardStartPage"; // $NON-NLS-1$
    private static final String INITIAL_MSG = "Configure an iNotes Calendar control"; // $NON-NLS-1$
    private static final String LABEL_TXT   = "The iNotes Calendar control is best used in conjuction with a REST Service.\n" // $NON-NLS-1$
                                                    + "Do you wish to configure and drop both controls together?"; // $NON-NLS-1$

    private Button _yesRadio = null;
    
    protected CvwStartPage() {
        super(PAGE_NAME);
    }

    @Override
    public void createControl(Composite parent) {
        setTitle(CalendarViewDropWizard.WIZARD_TITLE);
        setMessage(INITIAL_MSG, IMessageProvider.INFORMATION);

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(1, 5));
        WizardUtils.createLabel(container, LABEL_TXT, 1);
        
        _yesRadio = WizardUtils.createRadio(container, "Yes", 1, this, 20); // $NON-NLS-1$
        WizardUtils.createRadio(container, "No (drop an unconfigured iNotes Calendar control)", 1, this, 20); // $NON-NLS-1$
        
        setControl(container);
        setPageComplete(false);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
        if (isYesSelected()) {
            setPageComplete(false);
        } else {
            setPageComplete(true);
        }
        getWizard().getContainer().updateButtons();
    }

    @Override
    public boolean canFlipToNextPage() {
        return isYesSelected();
    }
    
    public boolean isYesSelected() {
        return _yesRadio.getSelection();        
    }
}