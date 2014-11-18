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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardSubPageDataView extends WizardSubPage {
    
    private Combo            summaryCombo;
    private WizardPageCombo  targetCombo;
    private Button           infiniteEnabled;
    private Button           infiniteDisabled;
    private Button           infiniteAppDef;
    private String[]         colNames;
    
    protected WizardSubPageDataView(WizardDataSub pd) {
        super(pd);
    }
    
    @Override
    public void refreshData() {
        super.refreshData();
        setMessage(getStepTxt() + MessageFormat.format("Configure the View Control for the \"{0}\" Application Page.", pageData.name), IMessageProvider.INFORMATION);  // $NLX-WizardSubPageDataView.ConfiguretheViewControlforthe0App-1$
        
        // Column names are in the first array
        // Column titles in the second
        String [][] columns = ((WizardSubPageDataSource)pageData.wizardPage[1]).getViewColumns();
        if (columns == null) {
            colNames = null;
            summaryCombo.removeAll();            
        } else {
            if(colNames != columns[0]) {
                // View has changed - Reload the combo and select the first item
                colNames = columns[0];
                summaryCombo.setItems(columns[1]);
                summaryCombo.select(0);
            }
        }

        targetCombo.refresh();
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);   
        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);

        WizardUtils.createLabel(container, "View control :", 1); // $NLX-WizardSubPageDataView.Viewcontrol-1$
        Combo controlCombo = WizardUtils.createCombo(container ,1, null);
        String [] controlItems = {"DataView"}; // $NLX-WizardSubPageDataView.DataView-1$
        controlCombo.setItems(controlItems);
        controlCombo.select(0);
        
        Group mainGroup = WizardUtils.createGroup(container, 2, 2);
        WizardUtils.createLabel(mainGroup, "Summary column :", 1); // $NLX-WizardSubPageDataView.Summarycolumn-1$
        summaryCombo = WizardUtils.createCombo(mainGroup ,1, null);

        WizardUtils.createLabel(mainGroup, "Target Page :", 1); // $NLX-WizardSubPageDataView.TargetPage-1$
        targetCombo = new WizardPageCombo(mainGroup, 1, pageData, true, WizardData.PAGE_TYPE_FORM);
        targetCombo.refresh();
        if (targetCombo.getOptionCount() == 2) {
            // If there's only one Document Viewer Page then select it
            targetCombo.setSelectedItem(1);
        }
        
        WizardUtils.createLabel(mainGroup, "Infinite scroll :", 1); // $NLX-WizardSubPageDataView.Infinitescroll-1$
        infiniteEnabled = WizardUtils.createRadio(mainGroup, "Enabled", 1, null); // $NLX-WizardSubPageDataView.Enabled-1$
        WizardUtils.createLabel(mainGroup, "", 1);
        infiniteDisabled = WizardUtils.createRadio(mainGroup, "Disabled", 1, null); // $NLX-WizardSubPageDataView.Disabled-1$
        WizardUtils.createLabel(mainGroup, "", 1);
        infiniteAppDef = WizardUtils.createRadio(mainGroup, "Application default", 1, null); // $NLX-WizardSubPageDataView.Applicationdefault-1$
        infiniteEnabled.setSelection(true);
        
        setControl(container);
        setPageComplete(true);
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
    }

    @Override
    public void pageDeleted(int idx) {
        if(targetCombo != null) {        
            targetCombo.pageDeleted(idx);
        }        
    }
    
    public int getTargetIndex() {
        if(targetCombo == null) {
            return -1;
        } else {
            return targetCombo.getSelectedIndex();
        }
    }
    
    public String getSummaryCol() {
        if (colNames != null) {
            int idx = WizardUtils.getComboIndex(summaryCombo, -1);
            if (idx >= 0) {
                return colNames[idx];
            }
        }
        
        return "";
    }
    
    public String getInfiniteScroll() {
        if (WizardUtils.getCheckBoxValue(infiniteDisabled, false)) {
            return "disable"; // $NON-NLS-1$
        }

        if (WizardUtils.getCheckBoxValue(infiniteAppDef, false)) {
            return "";
        }
        
        return "enable"; // $NON-NLS-1$
    }

    //
    // Adds the markup for this page
    //
    @Override
    public void addElementMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();
        Element dataView = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_DATA_VIEW);
        
        // Read-only mode for the moment
        FormModelUtil.setAttribute(dataView, XSPAttributeNames.XSP_ATTR_OPEN_DOC_AS_READONLY, "true"); // $NON-NLS-1$
        
        // Target Page
        int idx = getTargetIndex();
        if (idx >= 0) {
            FormModelUtil.setAttribute(dataView, IExtLibAttrNames.EXT_LIB_ATTR_PAGE_NAME, wizardData.getSubPageName(idx));
        }
        
        // Add the value for the DataSource - Get it from the dataSource Wizard Page
        String dsName = ((WizardSubPageDataSource)pageData.wizardPage[1]).getVarName();
        if (!StringUtil.isEmpty(dsName)) {
            FormModelUtil.setAttribute(dataView, XSPAttributeNames.XSP_ATTR_PARAMETERS_VALUE, "#{" + dsName + "}");    
        }
        
        // Infinite Scroll
        if (StringUtil.isNotEmpty(getInfiniteScroll())) {
            FormModelUtil.setAttribute(dataView, IExtLibAttrNames.EXT_LIB_ATTR_INFINITE_SCROLL, getInfiniteScroll());
        }
        
        // Summary Column
        if (StringUtil.isNotEmpty(getSummaryCol())) {
            Element sc = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_THIS_SUMMARY_COLUMN);                
            Element vsc = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_VIEW_SUMMARY_COLUMN);                
            FormModelUtil.setAttribute(vsc, IExtLibAttrNames.EXT_LIB_ATTR_COLUMN_NAME, getSummaryCol());
            sc.appendChild(vsc);
            dataView.appendChild(sc);
        }
        
        base.appendChild(dataView);
    }    
    
    @Override
    public boolean validatePage() {
        String errorMsg = "This mobile application may have errors or may not function\ncorrectly as a result."; // $NLX-WizardSubPageDataView.Thismobileapplicationmayhavecompi-1$
        if ((getTargetIndex() < 0) && StringUtil.isEmpty(getSummaryCol())) {
            String msg = "You have not selected a Summary Column or Target Page for this DataView.\n" + errorMsg; // $NLX-WizardSubPageDataView.YouhavenotselectedaSummaryColumno-1$
            return WizardUtils.displayContinueDialog(this.getShell(), WizardData.WIZARD_TITLE, msg); 
        }
        
        if (getTargetIndex() < 0) {
            String msg = "You have not selected a Target Page for this DataView.\n" +  errorMsg; // $NLX-WizardSubPageDataView.YouhavenotselectedaTargetPagefort-1$
            return WizardUtils.displayContinueDialog(this.getShell(), WizardData.WIZARD_TITLE, msg); 
        }

        if (StringUtil.isEmpty(getSummaryCol())) {
            String msg = "You have not selected a Summary Column for this DataView.\n" + // $NLX-WizardSubPageDataView.YouhavenotselectedaSummaryColumnf-1$
                         "This mobile application will not function correctly as a result."; // $NLX-WizardSubPageDataView.Thismobileapplicationwillnotfunct-1$
            return WizardUtils.displayContinueDialog(this.getShell(), WizardData.WIZARD_TITLE, msg); 
        }
        
        return true; 
    }
    
}