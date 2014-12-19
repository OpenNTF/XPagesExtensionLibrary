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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.*;


import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CvwRestPage extends WizardPage implements SelectionListener {
    private static final String PAGE_NAME   = "WizardRestPage"; // $NON-NLS-1$
    private static final String PAGE_TITLE  = "REST Service"; // $NON-NLS-1$
    private static final String INITIAL_MSG = "Configure the columns for the REST Service"; // $NON-NLS-1$

    private Button      _notStandardCheckbox;
    private Composite   _container;
    private Group       _colGroup;
    private String[]    _colNames;
    
    public static final String[][] restCols = {{Calendar.ATTR_COL_CALENDAR_DATE, "$134"}, 
                                               {Calendar.ATTR_COL_START_TIME,    "$144"},
                                               {Calendar.ATTR_COL_END_TIME,      "$146"},
                                               {Calendar.ATTR_COL_SUBJECT,       "$147"},
                                               {Calendar.ATTR_COL_CHAIR,         "$153"},
                                               {Calendar.ATTR_COL_ENTRY_ICON,    "$149"},
                                               {Calendar.ATTR_COL_ALT_SUBJECT,   "$151"},
                                               {Calendar.ATTR_COL_CONFIDENTIAL,  "$154"},
                                               {Calendar.ATTR_COL_CUSTOM_DATA,   "$UserData"}, // $NON-NLS-1$
                                               {Calendar.ATTR_COL_ENTRY_TYPE,    "$152"},
                                               {Calendar.ATTR_COL_STATUS,        "$160"}};
    
    private Map<String,ColumnCombo> _colMap = new HashMap<String,ColumnCombo>();

    public CvwRestPage() {
        super(PAGE_NAME);
    }

    @Override
    public void createControl(Composite parent) {
        setTitle(PAGE_TITLE);
        setMessage(INITIAL_MSG, IMessageProvider.INFORMATION);

        _container = new Composite(parent, SWT.NONE);
        _container.setLayout(WizardUtils.createGridLayout(1, 0));
        
        _colGroup = WizardUtils.createGroup(_container, 1, 2);
        String txt = "This is not a standard Notes Domino calendar view"; // $NON-NLS-1$
        _notStandardCheckbox = WizardUtils.createCheckBox(_colGroup, txt, 2, false);
        _notStandardCheckbox.addSelectionListener(this);
        for (String[] column: restCols) {
            WizardUtils.createLabel(_colGroup, column[0] + " :", 1, 20);            
            _colMap.put(column[0], new ColumnCombo(_colGroup, column[1]));
        }
        
        setControl(_container);
        setPageComplete(true);        
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        CvwViewPage viewPage = ((CalendarViewDropWizard)this.getWizard()).getViewPage();
        if (visible) {
            // Column names are in the first array
            // Column titles in the second
            String [][] columns = viewPage.getViewColumns();
            if (columns == null) {
                _colNames = null;               
                for (ColumnCombo combo : _colMap.values()) {
                    combo.setChoices(null, null);
                }
            } else {
                if(_colNames != columns[0]) {
                    // View has changed - Reload the combos
                    _colNames = columns[0];
                    for (ColumnCombo combo : _colMap.values()) {
                        combo.setChoices(columns[0], columns[1]);
                    }
                }
            }
        }
        WizardUtils.setCheckGroupEnabledState(_colGroup);
    }    
    
    public String getCalendarCol(final String colName) {
        ColumnCombo combo = _colMap.get(colName);
        if (combo != null) {
            return combo.getValue();
        }
        return "";
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _notStandardCheckbox) {
            WizardUtils.setCheckGroupEnabledState(_colGroup);
        }
    }    
    
    
    private class ColumnCombo {
        private final String _def;
        private final Combo _combo;
        private String[] _values;
        
        public ColumnCombo(final Composite parent, final String def) {
            _def = def;
            _combo = WizardUtils.createEditCombo(parent, 1, null);
        }
        
        public void setChoices(final String[] values, final String[] choices) {
            _values = values;
            if (choices == null) {
                _combo.removeAll();
            } else {
                _combo.setItems(choices);
            }            
            setDefaultIfPresent();
        }
        
        public String getValue() {
            if (_values != null) {
                int idx = WizardUtils.getComboIndex(_combo, -1);
                if (idx >= 0) {
                    return _values[idx];
                }
                return _combo.getText();
            }
            
            return "";
        }
        
        private void setDefaultIfPresent() {
            if (_values != null) {
                for (int i=0; i<_values.length;i++) {
                    if (StringUtil.equals(_values[i], _def)) {
                        _combo.select(i);
                        break;
                    }
                }
            }
        } 
    }
}