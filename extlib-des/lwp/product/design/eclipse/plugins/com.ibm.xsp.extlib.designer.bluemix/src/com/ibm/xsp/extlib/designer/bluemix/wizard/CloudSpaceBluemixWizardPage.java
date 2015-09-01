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

package com.ibm.xsp.extlib.designer.bluemix.wizard;

import java.util.ArrayList;

import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CloudSpaceBluemixWizardPage extends AbstractBluemixWizardPage implements ControlListener, ISelectionChangedListener {
    
    private TableViewer _orgViewer;
    private TableViewer _spaceViewer;

    protected CloudSpaceBluemixWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return "Organization and Space"; // $NLX-CloudSpaceBluemixWizardPage.OrganizationSpace-1$
    }

    @Override
    protected String getPageMsg() {
        return "Choose the organization and space for deployment."; // $NLX-CloudSpaceBluemixWizardPage.ChoosetheOrganizationandSpaceford-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(WizardUtils.createGridLayout(2, 5));

        WizardUtils.createLabel(container, "Organizations:", 1, 0, true, GridData.FILL_HORIZONTAL); // $NLX-CloudSpaceBluemixWizardPage.Organizations-1$
        WizardUtils.createLabel(container, "Spaces:", 1, 0, true, GridData.FILL_HORIZONTAL); // $NLX-CloudSpaceBluemixWizardPage.Spaces-1$
          
        _orgViewer = WizardUtils.createTableViewer(container, 1, 1, null, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
        ColumnViewerToolTipSupport.enableFor(_orgViewer, ToolTip.NO_RECREATE);
        _orgViewer.getTable().setToolTipText("Choose organization"); // $NLX-CloudSpaceBluemixWizardPage.ChooseOrganization-1$
        _orgViewer.getTable().setHeaderVisible(false);
        _orgViewer.getTable().setLinesVisible(false);
        _orgViewer.getTable().addControlListener(this);
        _orgViewer.setContentProvider(new ArrayContentProvider());
        _orgViewer.addSelectionChangedListener(this);

        // Create the only column
        TableViewerColumn col = new TableViewerColumn(_orgViewer, SWT.LEFT);
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider());
        
        _spaceViewer = WizardUtils.createTableViewer(container, 1, 1, null, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
        ColumnViewerToolTipSupport.enableFor(_spaceViewer, ToolTip.NO_RECREATE);
        _spaceViewer.getTable().setToolTipText("Choose space"); // $NLX-CloudSpaceBluemixWizardPage.ChooseSpace-1$
        _spaceViewer.getTable().setHeaderVisible(false);
        _spaceViewer.getTable().setLinesVisible(false);
        _spaceViewer.getTable().addControlListener(this);
        _spaceViewer.setContentProvider(new ArrayContentProvider());
        _spaceViewer.addSelectionChangedListener(this);

        // Create the only column
        col = new TableViewerColumn(_spaceViewer, SWT.LEFT);
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider());
        
        setControl(container);             
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible && _wiz.advancing) {
            String [] orgs = getOrgs();
            int orgIdx = orgs.length > 0 ? 0 : -1;
            _orgViewer.setInput(orgs);
            _orgViewer.getTable().select(orgIdx);
            updateSpaces(orgIdx);
        }
        super.setVisible(visible);
    }

    @Override
    public void controlResized(ControlEvent event) {
        Table table = (Table) event.widget;
        table.getColumn(0).setWidth(table.getClientArea().width);
    };    
    
    @Override
    public void controlMoved(ControlEvent event) {
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (event.getSource() == _orgViewer) {
            updateSpaces(_orgViewer.getTable().getSelectionIndex());
        }
    }
    
    public void updateSpaces(int orgIdx) {
        if (orgIdx >= 0) {
            _spaceViewer.setInput(getSpaces(_wiz.orgs.get(orgIdx).getName()));
            _spaceViewer.refresh();        
            _spaceViewer.getTable().select(0);
        } else {
            _spaceViewer.setInput(null);
            _spaceViewer.refresh();        
        }
        validatePage();
    }
    
    private String[] getOrgs() {
        ArrayList <String> list = new ArrayList<String>();
        for (CloudOrganization org : _wiz.orgs) {
            list.add(org.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getSpaces(String org) {
        ArrayList <String> list = new ArrayList<String>();
        for (CloudSpace space : _wiz.spaces) {
            if (StringUtil.equalsIgnoreCase(org, space.getOrganization().getName())) {
                list.add(space.getName());
            }
        }
        return list.toArray(new String[list.size()]);
    }
    
    public String getOrg() {
        if (!_orgViewer.getTable().isDisposed()) {
            return _wiz.orgs.get(_orgViewer.getTable().getSelectionIndex()).getName();
        }        
        return "";
    }
    
    public String getSpace() {
        if (!_spaceViewer.getTable().isDisposed()) {
            return(getSpaces(getOrg())[_spaceViewer.getTable().getSelectionIndex()]);
        }
        return "";
    }
    
    @Override
    protected void validatePage() {
        if (_spaceViewer.getTable().getSelectionIndex() >= 0) {
            showError(null);
        } else {
            showError("You must select a Cloud Space"); // $NLX-CloudSpaceBluemixWizardPage.YoumustselectaCloudSpace-1$
        }
    }

}