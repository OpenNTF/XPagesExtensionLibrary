/*
 * © Copyright IBM Corp. 2015
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
import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomTable;
import com.ibm.commons.swt.controls.custom.CustomTableColumn;
import com.ibm.commons.swt.data.dialog.SimpleDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.EditTableItem;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestEditorPage.TableEntry;;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestServicesDialog extends SimpleDialog {
    
    private final ArrayList<CloudService>  _cloudServices;
    private final ArrayList<EditTableItem> _serviceList;
    private TableViewer                    _serviceTable;
    
    public ManifestServicesDialog(Shell shell, ArrayList<CloudService> cloudServices, ArrayList<EditTableItem> serviceList) {
        super(shell);
        _cloudServices = cloudServices;
        _serviceList = serviceList;
    }

    @Override
    protected String getMessage() {
        return "Select the services to bind this application to."; // $NLX-ManifestServicesDialog.SelecttheServicestobindthi-1$
    }

    @Override
    protected String getDialogTitle() {
        return BluemixUtil.productizeString("%BM_PRODUCT% Services"); // $NLX-ManifestServicesDialog.IBMBluemixServices-1$
    }

    @Override
    protected void fillClientArea(Composite parent) {
        this.setTitle(BluemixUtil.productizeString("%BM_PRODUCT% Services")); // $NLX-ManifestServicesDialog.IBMBluemixServices-1$
        parent.setLayout(new FillLayout());  
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(SWTLayoutUtils.createLayoutDefaultSpacing(1));
        
        // Create the Table
        CustomTable table = new CustomTable(composite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, "bluemix.services"); // $NON-NLS-1$
        table.setLayoutData(SWTLayoutUtils.createGDFill());
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // Create the Name Column
        CustomTableColumn col = new CustomTableColumn(table, SWT.NONE, "bluemix.services.name"); // $NON-NLS-1$
        col.setText("Name"); // $NLX-ManifestServicesDialog.Name-1$
        col.setWidthUnit(CustomTableColumn.UNIT_PERCENT);
        col.setColWidth(60);

        // Create the Plan Column
        col = new CustomTableColumn(table, SWT.NONE, "bluemix.services.plan");  // $NON-NLS-1$
        col.setText("Plan"); // $NLX-ManifestServicesDialog.Plan-1$
        col.setWidthUnit(CustomTableColumn.UNIT_PERCENT);
        col.setColWidth(40);

        // Table Viewer
        _serviceTable = new TableViewer(table);
        
        // Create the Label Provider
        _serviceTable.setLabelProvider(new ITableLabelProvider() {
            @Override
            public void addListener(ILabelProviderListener arg0) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object arg0, String arg1) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener arg0) {
            }

            @Override
            public Image getColumnImage(Object arg0, int arg1) {
                return null;
            }

            @Override
            public String getColumnText(Object obj, int col) {
                if (obj instanceof CloudService) {
                    CloudService cs = (CloudService) obj;
                    if (col == 0) {
                        return cs.getName();
                    } else if (col == 1) {
                        return cs.getPlan();
                    }
                }
                return null;
            }    
        });

        // Create the Content Provider
        _serviceTable.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            }
            
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            @Override
            public Object[] getElements(Object input) {
                return ((List<CloudService>)input).toArray();                
            }        
        });

        _serviceTable.setInput(_cloudServices);  
        
        // Tick the services from the yaml file
        TableItem[] items = _serviceTable.getTable().getItems(); 
        for (TableItem item : items) {
            if (item.getData() instanceof CloudService) {
                CloudService cs = (CloudService) item.getData();
                for (EditTableItem service : _serviceList) {
                    if (StringUtil.equals(service.getColumn(0), cs.getName())) {
                        item.setChecked(true);
                        break;
                    }
                }
            }
        }        
    }

    @Override
    protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
        // OK was pressed
        // Record the ticked Services
        _serviceList.clear();
        TableItem[] items = _serviceTable.getTable().getItems(); 
        for (TableItem item : items) {
            if (item.getChecked() == true) {
                if (item.getData() instanceof CloudService) {
                    CloudService cs = (CloudService) item.getData();
                    _serviceList.add(new TableEntry(cs.getName()));
                }
            }
        }
        return true;
    }

}