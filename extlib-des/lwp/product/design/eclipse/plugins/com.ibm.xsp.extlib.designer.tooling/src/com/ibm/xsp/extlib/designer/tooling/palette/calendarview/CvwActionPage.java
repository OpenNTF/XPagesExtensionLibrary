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
import org.eclipse.swt.widgets.Group;

import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CvwActionPage extends WizardPage implements SelectionListener {
    private static final String PAGE_NAME   = "WizardActionPage"; // $NON-NLS-1$
    private static final String PAGE_TITLE  = "Actions"; // $NON-NLS-1$
    private static final String INITIAL_MSG = "Configure the actions for the Calendar"; // $NON-NLS-1$

    private Button              _dateRangeCheckbox;
    private Button              _todayCheckbox;
    private Button              _todayTomorrowCheckbox;
    private Button              _workWeekCheckbox;
    private Button              _fullWeekCheckbox;
    private Button              _twoWeeksCheckbox;
    private Button              _monthCheckbox;
    private Button              _yearCheckbox;
    private Button              _summaryCheckbox;
    
    private Group               _dateRangeGroup;

    public CvwActionPage() {
        super(PAGE_NAME);
    }

    @Override
    public void createControl(Composite parent) {
        setTitle(PAGE_TITLE);
        setMessage(INITIAL_MSG, IMessageProvider.INFORMATION);

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(1, 5));
        
        _dateRangeGroup = WizardUtils.createGroup(container, 1, 2);

        _dateRangeCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Include date-range actions",2, true); // $NON-NLS-1$
        _dateRangeCheckbox.addSelectionListener(this);
        
        _todayCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Today", 1, true, 20); // $NON-NLS-1$
        _todayTomorrowCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Today and tomorrow", 1, true, 20); // $NON-NLS-1$
        _workWeekCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Work week", 1, true, 20); // $NON-NLS-1$
        _fullWeekCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Full week", 1, true, 20); // $NON-NLS-1$
        _twoWeeksCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Two weeks", 1, true, 20); // $NON-NLS-1$
        _monthCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Month", 1, true, 20); // $NON-NLS-1$
        _yearCheckbox = WizardUtils.createCheckBox(_dateRangeGroup, "Year", 1, true, 20); // $NON-NLS-1$
                
        Group group = WizardUtils.createGroup(container, 1, 2);
        _summaryCheckbox = WizardUtils.createCheckBox(group, "Include summary actions", 1, true); // $NON-NLS-1$
        
        setControl(container);
        setPageComplete(true);        
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _dateRangeCheckbox) {
            WizardUtils.setCheckGroupEnabledState(_dateRangeGroup);
        }
    }    
    
    public boolean isActionMarkupRequired() {
        return (isDateRangeMarkupRequired() || getSummary());
    }
    
    public boolean isDateRangeMarkupRequired() {
        if (WizardUtils.getCheckBoxValue(_dateRangeCheckbox, true)) {
            if (getToday() || 
                getTodayTomorrow() ||
                getWorkWeek() ||
                getFullWeek() ||
                getTwoWeeks() ||
                getMonth() ||
                getYear()) {
                return true;
            }
        }
        
        return false;        
    }
    
    public boolean getToday() {
        return WizardUtils.getCheckBoxValue(_todayCheckbox, true); 
    }
    
    public boolean getTodayTomorrow() {
        return WizardUtils.getCheckBoxValue(_todayTomorrowCheckbox, true); 
    }
    
    public boolean getWorkWeek() {
        return WizardUtils.getCheckBoxValue(_workWeekCheckbox, true); 
    }
    
    public boolean getFullWeek() {
        return WizardUtils.getCheckBoxValue(_fullWeekCheckbox, true); 
    }
    
    public boolean getTwoWeeks() {
        return WizardUtils.getCheckBoxValue(_twoWeeksCheckbox, true); 
    }
    
    public boolean getMonth() {
        return WizardUtils.getCheckBoxValue(_monthCheckbox, true); 
    }
    
    public boolean getYear() {
        return WizardUtils.getCheckBoxValue(_yearCheckbox, true); 
    }
    
    public boolean getSummary() {
        return WizardUtils.getCheckBoxValue(_summaryCheckbox, true); 
    }
    
    
}