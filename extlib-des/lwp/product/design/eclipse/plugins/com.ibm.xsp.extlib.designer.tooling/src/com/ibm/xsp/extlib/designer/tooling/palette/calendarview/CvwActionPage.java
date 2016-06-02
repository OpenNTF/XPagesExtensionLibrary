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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizardPage;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CvwActionPage extends AbstractWizardPage implements SelectionListener {

    private Button  _dateRangeCheckbox;
    private Button  _todayCheckbox;
    private Button  _todayTomorrowCheckbox;
    private Button  _workWeekCheckbox;
    private Button  _fullWeekCheckbox;
    private Button  _twoWeeksCheckbox;
    private Button  _monthCheckbox;
    private Button  _yearCheckbox;
    private Button  _displayFormatCheckbox;
    private Combo   _initialDateRangeCombo;

    public CvwActionPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return "Date Range and Display Format Icons"; // $NLX-CvwActionPage.DateRangeandDisplayFormatIcons-1$
    }

    @Override
    protected String getPageMsg() {
        return "Configure the date range and display format icons for this calendar"; // $NLX-CvwActionPage.Configurethedaterangeanddisplayfo-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(2, 5));
        
        Composite comp = new Composite(container, SWT.NONE);
        GridLayout gl = WizardUtils.createGridLayout(2, 0);
        gl.marginWidth = 0;
        comp.setLayout(gl);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalSpan = 2;
        comp.setLayoutData(gd);
        WizardUtils.createLabel(comp, "Initial date range: ", 1); // $NLX-CvwActionPage.Initialdaterange-1$
        _initialDateRangeCombo = WizardUtils.createCombo(comp, 1, CalendarFormat.getLabels(), CalendarFormat.WORK_WEEK.getIndex(), null);
        
        _dateRangeCheckbox = WizardUtils.createCheckBox(container, "Include date range icons",2, true); // $NLX-CvwActionPage.Includedaterangeicons-1$
        _dateRangeCheckbox.addSelectionListener(this);
        
        _todayCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.TODAY.getLabel(), 1, true, 20);
        _todayTomorrowCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.TODAY_TOMORROW.getLabel(), 1, true, 20);
        _workWeekCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.WORK_WEEK.getLabel(), 1, true, 20);
        _fullWeekCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.FULL_WEEK.getLabel(), 1, true, 20);
        _twoWeeksCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.TWO_WEEKS.getLabel(), 1, true, 20);
        _monthCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.MONTH.getLabel(), 1, true, 20);
        _yearCheckbox = WizardUtils.createCheckBox(container, CalendarFormat.YEAR.getLabel(), 1, true, 20);
                
        _displayFormatCheckbox = WizardUtils.createCheckBox(container, "Include display format icons", 2, true); // $NLX-CvwActionPage.Includedisplayformaticons-1$
        
        setControl(container);
        setPageComplete(true);        
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _dateRangeCheckbox) {
            _todayCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _todayTomorrowCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _workWeekCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _fullWeekCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _twoWeeksCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _monthCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
            _yearCheckbox.setEnabled(_dateRangeCheckbox.getSelection());
        }
    }    
    
    public boolean isActionMarkupRequired() {
        return (isDateRangeMarkupRequired() || getDisplayFormat());
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
    
    public boolean getDisplayFormat() {
        return WizardUtils.getCheckBoxValue(_displayFormatCheckbox, true); 
    }
    
    public CalendarFormat getInitialDateRange() {
        int index = WizardUtils.getComboIndex(_initialDateRangeCombo, -1);
        if (index != -1) {
            return CalendarFormat.getFromIndex(index);
        }
        
        return CalendarFormat.WORK_WEEK;
    }
}