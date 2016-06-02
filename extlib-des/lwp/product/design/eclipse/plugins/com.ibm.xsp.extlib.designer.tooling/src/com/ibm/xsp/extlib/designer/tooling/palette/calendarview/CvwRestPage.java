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

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.navigator.NavigatorPlugin;
import com.ibm.designer.domino.xsp.utils.PropertyPanelTooltipUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizardPage;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.*;


/**
 * @author Gary Marjoram
 *
 */
public class CvwRestPage extends AbstractWizardPage implements ControlListener {

    private static final Image  _WARNING_ICON = NavigatorPlugin.getImage("design/error_column.png"); // $NON-NLS-1$

    private final RestColumn[]  _restCols = {new RestColumn("Calendar Date", Calendar.ATTR_COL_CALENDAR_DATE, "$134",        true),   // $NLX-CvwRestPage.CalendarDate-1$
                                             new RestColumn("Start Time",    Calendar.ATTR_COL_START_TIME,    "$144",        false),  // $NLX-CvwRestPage.StartTime-1$
                                             new RestColumn("End Time",      Calendar.ATTR_COL_END_TIME,      "$146",        false),  // $NLX-CvwRestPage.EndTime-1$
                                             new RestColumn("Subject",       Calendar.ATTR_COL_SUBJECT,       "$147",        true),   // $NLX-CvwRestPage.Subject-1$
                                             new RestColumn("Chair",         Calendar.ATTR_COL_CHAIR,         "$153",        false),  // $NLX-CvwRestPage.Chair-1$
                                             new RestColumn("Entry Icon",    Calendar.ATTR_COL_ENTRY_ICON,    "$149",        false),  // $NLX-CvwRestPage.EntryIcon-1$
                                             new RestColumn("Alt Subject",   Calendar.ATTR_COL_ALT_SUBJECT,   "$151",        false),  // $NLX-CvwRestPage.AltSubject-1$
                                             new RestColumn("Confidential",  Calendar.ATTR_COL_CONFIDENTIAL,  "$154",        false),  // $NLX-CvwRestPage.Confidential-1$
                                             new RestColumn("Custom Data",   Calendar.ATTR_COL_CUSTOM_DATA,   "$UserData",   false),  // $NON-NLS-2$ $NLX-CvwRestPage.CustomData-1$
                                             new RestColumn("Entry Type",    Calendar.ATTR_COL_ENTRY_TYPE,    "$152",        false),  // $NLX-CvwRestPage.EntryType-1$
                                             new RestColumn("Status",        Calendar.ATTR_COL_STATUS,        "$160",        false)}; // $NLX-CvwRestPage.Status-1$

    private TableViewer         _tableViewer;
    private TableViewerColumn   _editCol;
    private String[][]          _viewColumns; 

    public CvwRestPage(String pageName) {
        super(pageName);
    }
    
    @Override
    protected String getPageTitle() {
        return "Calendar REST Service"; // $NLX-CvwRestPage.CalendarRESTService-1$
    }

    @Override
    protected String getPageMsg() {
        return "Choose the view columns containing the required calendar data items"; // $NLX-CvwRestPage.Choosetheviewcolumnscontainingthe-1$
    }    

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(2, 5));
        
        // Create the Table Viewer
        int[] colWeights = {5, 40, 55};
        _tableViewer = WizardUtils.createTableViewer(container, 2, 3, colWeights);
        _tableViewer.setContentProvider(new ArrayContentProvider());
        _tableViewer.getTable().addControlListener(this);

        GridData gd = (GridData) _tableViewer.getTable().getLayoutData();
        gd.heightHint = (_tableViewer.getTable().getItemHeight() * _restCols.length) + _tableViewer.getTable().getHeaderHeight();       
        ColumnViewerToolTipSupport.enableFor(_tableViewer, ToolTip.NO_RECREATE);         
        
        // Create the Warning column
        TableViewerColumn col = new TableViewerColumn(_tableViewer, SWT.LEFT);
        col.getColumn().setText("");  
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return null;
            }
            
            @Override
            public Image getImage(Object element) {
                if (((RestColumn)element).showWarning()) {
                    return _WARNING_ICON;
                } 
                return null;
            }
            
            @Override
            public String getToolTipText(Object element) {
                if (((RestColumn)element).showWarning()) {
                    return "It is recommended to set a view column for this data item"; // $NLX-CvwRestPage.Itisrecommendedtosetaviewcolumnfo-1$
                }
                return null;
            }
        });

        // Create the Data Item column
        col = new TableViewerColumn(_tableViewer, SWT.LEFT);
        col.getColumn().setText("Data Item"); // $NLX-CvwRestPage.DataItem-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
              return ((RestColumn)element).getLabel();
            }
            
            @Override
            public String getToolTipText(Object element) {
                return (((RestColumn)element).getTooltip());
            }            
          });
        
        // Create the View Column column
        _editCol = new TableViewerColumn(_tableViewer, SWT.LEFT);
        _editCol.getColumn().setText("View Column"); // $NLX-CvwRestPage.ViewColumn-1$
        _editCol.getColumn().setResizable(false);
        _editCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((RestColumn)element).getTextValue();
            }
        });
        
        setControl(container);
        setPageComplete(true);        
    }
    
    @Override
    protected void initialisePageState() {     
        // load the view data
        loadViewData();
        
        // Initialise the table viewer
        _editCol.setEditingSupport(new CellEditingSupport());            
        _tableViewer.setInput(_restCols);             
    }    
        
    public void loadViewData() {
        // Get the view column information
        // Column names are in the first array
        // Column titles in the second
        CvwViewPage viewPage = ((CalendarViewDropWizard)this.getWizard()).getViewPage();
        String[][] cols  = viewPage.getViewColumns();
        int colLen = cols == null ? 0 : cols[0].length;
        
        // Copy the arrays leaving an empty string in the first position
        _viewColumns = new String[2][colLen+1];
        _viewColumns[0][0] = "";
        _viewColumns[1][0] = "";
        for (int i=0; i < colLen; i++) {
            _viewColumns[0][i+1] = cols[0][i];
            // check if title and name are the same
            if (StringUtil.equals(cols[0][i], cols[1][i])) {
                _viewColumns[1][i+1] = cols[1][i];
            } else {
                _viewColumns[1][i+1] = StringUtil.format("{0} ({1})", cols[1][i], cols[0][i]);
            }
        }
        
        // Setup _restCols
        List<String> colNames = Arrays.asList(_viewColumns[0]);
        for(RestColumn col:_restCols) {
            col.setValue(colNames.indexOf(col.getDefaultCol()));
        }        
    }
    
    public int getRestColCount() {
        return _restCols.length;
    }
    
    public String getRestColAttr(int idx) {
        return _restCols[idx].getAttr();
    }
    
    public String getRestColViewCol(int idx) {
        if (_viewColumns != null) {
            int val = _restCols[idx].getValue();
            if (val >= 0 && val < _viewColumns[0].length) {
                return _viewColumns[0][val];
            }
        }
        return "";
    }    
    
    @Override
    public void controlMoved(ControlEvent arg0) {
    }

    @Override
    public void controlResized(ControlEvent event) {
        if (event.widget == _tableViewer.getTable()) {
            Table table = (Table) event.widget;
            table.getColumn(2).setWidth(table.getClientArea().width - table.getColumn(0).getWidth() - table.getColumn(1).getWidth());
        }
    }   
    
    private class RestColumn {
        private final String  _label;
        private final String  _attr;
        private final String  _defaultCol;
        private final boolean _required;
        private int           _value;
        
        public RestColumn(String label, String attr, String defaultCol, boolean required) {
            _label = label;
            _attr = attr;
            _defaultCol = defaultCol;
            _required = required;
            _value = -1;
        }
        
        public String getLabel() {
            return _label;
        }

        public String getAttr() {
            return _attr;
        }

        public String getDefaultCol() {
            return _defaultCol;
        }
        
        public String getTooltip() {
            // Get the tooltip from the FacesRegistry
            FacesRegistry fr = _wiz.project.getFacesRegistry();
            if (fr != null) {
                FacesDefinition fd = fr.findDef(EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_CALENDAR_JSON_LEGACY_SERVICE);
                if (fd != null) {
                    return StringUtil.format("{0}\n{1}", _attr, PropertyPanelTooltipUtil.getTooltipString(fd, _attr)); // $NON-NLS-1$
                }
            }
            return _attr;
        }

        public int getValue() {
            return _value;
        }
        
        public String getTextValue() {
            if (_value >= 0 && (_value < _viewColumns[1].length)) {
                return _viewColumns[1][_value];
            } 
            return "";    
        }

        public void setValue(int value) {
            _value = value;
        }
        
        public boolean showWarning() {
            if (_required && _value <= 0) {
                return true;
            }
            return false;
        }
    }
    
    private class CellEditingSupport extends EditingSupport {
        public CellEditingSupport() {
            super(_tableViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            ComboBoxCellEditor ce = new ComboBoxCellEditor(_tableViewer.getTable(), _viewColumns[1], SWT.READ_ONLY);
            ((CCombo)ce.getControl()).setVisibleItemCount(10);
            return ce;
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            return ((RestColumn)element).getValue();
        }

        @Override
        protected void setValue(Object element, Object value) {
            ((RestColumn)element).setValue((Integer) value);
            _tableViewer.refresh();
        }
    }

}