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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.xsp.internal.events.EventsXSPDefinitions;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardSubPageMain extends WizardSubPage {
    private Text            pageLabel;
    private Text            backLabel;
    private Text            toolBarText;
    private Text            tabBtnText;
    private Button          headerBtn;
    private Button          backBtn;
    private Button          tabBarBtn;
    private Button          newDocBtn;
    private Button          editSaveDocBtn;
    private Button          deleteDocBtn;
    private Button          bottomBtn;
    private Group           headerGroup;
    private Group           tabBarGroup;
    private Group           backGroup;
    private WizardPageCombo backCombo;
    
    protected WizardSubPageMain(WizardDataSub pd) {
        super(pd);
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);   
        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);
                
        headerGroup = WizardUtils.createGroup(container, 2, 2); 
        headerBtn = WizardUtils.createCheckBox(headerGroup, "Add a Page Heading", 2, true); // $NLX-WizardSubPageMain.AddaPageHeading-1$
        headerBtn.addSelectionListener(this); 
        WizardUtils.createLabel(headerGroup, "Label :", 1); // $NLX-WizardSubPageMain.Label-1$
        pageLabel = WizardUtils.createText(headerGroup);
        pageLabel.setText(pageData.name);

        WizardUtils.createLabel(headerGroup, "Tool Bar Buttons :", 1); // $NLX-WizardSubPageMain.ToolBarButtons-1$
        toolBarText = WizardUtils.createText(headerGroup);
        toolBarText.setToolTipText("Enter a comma separated list of buttons");  // $NLX-WizardSubPageMain.Enteracommaseparatedlistofbuttons-1$

        if (pageData.type == WizardData.PAGE_TYPE_VIEW) {
            WizardUtils.createLabel(headerGroup, "", 1);
            newDocBtn = WizardUtils.createCheckBox(headerGroup, "Add 'New' Document action button", 1, true);  // $NLX-WizardSubPageMain.AddNewDocumentactionbutton-1$
        }

        backGroup = WizardUtils.createGroup(headerGroup, 2, 2);     
        backBtn = WizardUtils.createCheckBox(backGroup, "Add a Back Button", 2, pageData.index != 0); // $NLX-WizardSubPageMain.AddaBackButton-1$
        backBtn.addSelectionListener(this); 
        WizardUtils.createLabel(backGroup, "Label :", 1); // $NLX-WizardSubPageMain.Label.1-1$
        backLabel = WizardUtils.createText(backGroup, "Back"); // $NLX-WizardSubPageMain.Back-1$
        WizardUtils.createLabel(backGroup, "Target Page :", 1); // $NLX-WizardSubPageMain.TargetPage-1$
        backCombo = new WizardPageCombo(backGroup, 1, pageData, true, WizardData.PAGE_TYPE_INVALID);
        backCombo.refresh();
        backCombo.setSelectedIndex(pageData.index - 1);
        
        tabBarGroup = WizardUtils.createGroup(container, 2, 3); 
        tabBarBtn = WizardUtils.createCheckBox(tabBarGroup, "Add a Tab Bar", 3, pageData.type == WizardData.PAGE_TYPE_FORM); // $NLX-WizardSubPageMain.AddaTabBar-1$
        tabBarBtn.addSelectionListener(this); 
        
        WizardUtils.createLabel(tabBarGroup, "Tab Bar Buttons :", 1); // $NLX-WizardSubPageMain.TabBarButtons-1$
        tabBtnText = WizardUtils.createText(tabBarGroup, 2);
        tabBtnText.setToolTipText("Enter a comma separated list of buttons"); // $NLX-WizardSubPageMain.Enteracommaseparatedlistofbuttons-1$

        if (pageData.type == WizardData.PAGE_TYPE_FORM) {
            WizardUtils.createLabel(tabBarGroup, "", 1);
            editSaveDocBtn = WizardUtils.createCheckBox(tabBarGroup, "Add 'Edit/Save' Document action buttons", 2, true); // $NLX-WizardSubPageMain.AddEditSaveDocumentactionbuttons-1$
            
            WizardUtils.createLabel(tabBarGroup, "", 1);
            deleteDocBtn = WizardUtils.createCheckBox(tabBarGroup, "Add 'Delete' Document action button", 2, true); // $NLX-WizardSubPageMain.AddDeleteDocumentactionbutton-1$

            WizardUtils.createLabel(tabBarGroup, "Position :", 1);  // $NLX-WizardSubPageMain.Position-1$
            WizardUtils.createRadio(tabBarGroup, "Top", 1, null);  // $NLX-WizardSubPageMain.Top-1$
            bottomBtn = WizardUtils.createRadio(tabBarGroup, "Bottom", 1, null);  // $NLX-WizardSubPageMain.Bottom-1$
            bottomBtn.setSelection(true);
        }
        
        WizardUtils.setCheckGroupEnabledState(headerGroup);
        WizardUtils.setCheckGroupEnabledState(backGroup);
        WizardUtils.setCheckGroupEnabledState(tabBarGroup);

        setControl(container);
        setPageComplete(true);
    }
    
    @Override
    public void refreshData() {
        super.refreshData();
        backCombo.refresh();
        setMessage(getStepTxt() + MessageFormat.format("Configure the \"{0}\" Application Page.", pageData.name), IMessageProvider.INFORMATION); // $NLX-WizardSubPageMain.Configurethe0ApplicationPage-1$
    }
    
    public String getSubPageLabel() {
        return WizardUtils.getTextValue(pageLabel, "");
    }
    
    public boolean getSubPageHeader() {
        return WizardUtils.getCheckBoxValue(headerBtn, true);
    }

    public boolean getSubPageBackBtn() {
        return WizardUtils.getCheckBoxValue(backBtn, false);
    }

    public String getSubPageBackText() {
        return WizardUtils.getTextValue(backLabel, "Back");  // $NLX-WizardSubPageMain.Back.1-1$
    }

    public boolean getSubPageTabBar() {
        return WizardUtils.getCheckBoxValue(tabBarBtn, pageData.type == WizardData.PAGE_TYPE_FORM);
    }
    
    public int getSubPageBackIndex() {
        if(backCombo == null) {
            return -1;
        } else {
            return backCombo.getSelectedIndex();
        }
    }
    
    public String[] getSubPageTabBtns() {
        String txt = WizardUtils.getTextValue(tabBtnText, "");
        String[] parts = txt.split(",");
        for (int i=0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
        
    public String [] getSubPageToolBarBtns() {
        String txt = WizardUtils.getTextValue(toolBarText, "");
        String[] parts = txt.split(",");
        for (int i=0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
    
    public boolean getNewDocBtn() {
        return WizardUtils.getCheckBoxValue(newDocBtn, pageData.type == WizardData.PAGE_TYPE_VIEW);
    }
    
    public boolean getEditSaveDocBtn() {
        return WizardUtils.getCheckBoxValue(editSaveDocBtn, pageData.type == WizardData.PAGE_TYPE_FORM);
    }

    public boolean getDeleteDocBtn() {
        return WizardUtils.getCheckBoxValue(deleteDocBtn, pageData.type == WizardData.PAGE_TYPE_FORM);
    }
    
    public int getTabBarPosition() {
        if (WizardUtils.getCheckBoxValue(bottomBtn, pageData.type == WizardData.PAGE_TYPE_FORM)) {
            return WizardData.TAB_BAR_BOTTOM;
        }
        return WizardData.TAB_BAR_TOP;        
    }

    //
    // Enables/Disables groups of controls
    //
    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == headerBtn) {
            WizardUtils.setCheckGroupEnabledState(headerGroup);
        } else if (event.widget == backBtn) {
            WizardUtils.setCheckGroupEnabledState(backGroup);
        } else if (event.widget == tabBarBtn) {
            WizardUtils.setCheckGroupEnabledState(tabBarGroup);
        }
    }

    @Override
    public void pageDeleted(int idx) {
        if(backCombo != null) {        
            backCombo.pageDeleted(idx);
        }
    }
    
    //
    // Gets the next Page in the Wizard
    //
    @Override
    public WizardPage getNextPage() {
        boolean newPage = true;
        refreshData();
        switch(pageData.type) {
            case WizardData.PAGE_TYPE_NAVIGATOR:
                if (!(pageData.wizardPage[1] instanceof WizardSubPageNav) ) {
                    pageData.wizardPage[1] = new WizardSubPageNav(pageData);
                    ((Wizard)getWizard()).addPage(pageData.wizardPage[1]);
                }
                return(pageData.wizardPage[1]);

            case WizardData.PAGE_TYPE_VIEW:
                if (pageData.wizardPage[1] instanceof WizardSubPageDataSource) {
                    if (((WizardSubPageDataSource)pageData.wizardPage[1]).getType() == WizardSubPageDataSource.DS_VIEW) {
                        newPage = false;
                    }
                }
                if (newPage) {
                    pageData.wizardPage[1] = new WizardSubPageDataSource(pageData, WizardSubPageDataSource.DS_VIEW);
                    ((Wizard)getWizard()).addPage(pageData.wizardPage[1]);
                }                
                return(pageData.wizardPage[1]);

            case WizardData.PAGE_TYPE_FORM:
                if (pageData.wizardPage[1] instanceof WizardSubPageDataSource) {
                    if (((WizardSubPageDataSource)pageData.wizardPage[1]).getType() == WizardSubPageDataSource.DS_DOC) {
                        newPage = false;
                    }
                }
                if (newPage) {
                    pageData.wizardPage[1] = new WizardSubPageDataSource(pageData, WizardSubPageDataSource.DS_DOC);
                    ((Wizard)getWizard()).addPage(pageData.wizardPage[1]);
                }                
                return(pageData.wizardPage[1]);
        }        
        return super.getNextPage();
    }

    //
    // Generate the markup for the Page
    //
    @Override
    public void addElementMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();
        
        if(getSubPageHeader()) {
            // Create the Header
            Element newHeader = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_PAGE_HEADER);            
            FormModelUtil.setAttribute(newHeader, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, getSubPageLabel());
            
            // Back Button
            if(getSubPageBackBtn()) {
                FormModelUtil.setAttribute(newHeader, IExtLibAttrNames.EXT_LIB_ATTR_BACK, getSubPageBackText());
                int backIdx = getSubPageBackIndex();
                if (backIdx >= 0) {
                    FormModelUtil.setAttribute(newHeader, IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO, wizardData.getSubPageName(backIdx));
                }
            }

            // Panel for ToolBar Buttons
            Element facet = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_THIS_FACETS);                
            Element panel = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_PANEL);
            FormModelUtil.setAttribute(panel, XSPTagNames.XSP_TAG_FACET_KEY, "actionFacet"); // $NON-NLS-1$
            
            // ToolBar Buttons
            String[] toolBarBtns = getSubPageToolBarBtns();            
            for (String toolBarBtn:toolBarBtns) {
                if (toolBarBtn.length() > 0) {
                    Element newToolBarBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TOOLBAR_BUTTON);
                    FormModelUtil.setAttribute(newToolBarBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, toolBarBtn);
                    panel.appendChild(newToolBarBtn);
                }
            }       
            
            // Check for document handling toolbar buttons
            switch (pageData.type) {
                case WizardData.PAGE_TYPE_VIEW:
                    if (getNewDocBtn()) {
                        // Add a new document button
                        Element newToolBarBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TOOLBAR_BUTTON);
                        FormModelUtil.setAttribute(newToolBarBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, "New"); // $NON-NLS-1$
                        
                        // Event Handler
                        Element eventHandler = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EVENT_HANDLER);
                        WizardUtils.setXspEventHandlerAttrs(eventHandler, "onClick", "true", "complete"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                        
                        // this.action
                        Element action = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, EventsXSPDefinitions.ACTION_TAG);
                        
                        // moveTo
                        Element moveTo = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO);
                        // Get target Page Index
                        int idx = ((WizardSubPageDataView)pageData.wizardPage[2]).getTargetIndex();
                        WizardUtils.setXspMoveToAttrs(moveTo, "Left to Right", "slide", "true", wizardData.getSubPageName(idx), null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                        
                        // Append all the elements
                        action.appendChild(moveTo);
                        eventHandler.appendChild(action);
                        newToolBarBtn.appendChild(eventHandler);
                        panel.appendChild(newToolBarBtn);                        
                    }
                    break;
            }
            
            facet.appendChild(panel);
            newHeader.appendChild(facet);
            
            base.appendChild(newHeader);
        }

        if (getTabBarPosition() == WizardData.TAB_BAR_TOP) {
            addTabBarMarkup(base, registry);
        }
    }        
    
    public void addTabBarMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();

        if(getSubPageTabBar()) {
            Element newTabBar = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TAB_BAR);
            FormModelUtil.setAttribute(newTabBar, IExtLibAttrNames.EXT_LIB_ATTR_BAR_TYPE, "segmentedControl" ); // $NON-NLS-1$
            String[] tabBtns = getSubPageTabBtns(); 
            
            // TabBar Buttons
            for (String tabBtn:tabBtns) {
                if (tabBtn.length() > 0) {
                    Element newBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TAB_BAR_BTN);
                    FormModelUtil.setAttribute(newBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, tabBtn);                
                    newTabBar.appendChild(newBtn);
                }
            }
            
            switch (pageData.type) {
                case WizardData.PAGE_TYPE_FORM:
                    // Get the var name if any
                    String dsName = ((WizardSubPageDataSource)pageData.wizardPage[1]).getVarName();
    
                    if (getEditSaveDocBtn()) {
                        //
                        // Add an edit document button 
                        //
                        Element newTabBarBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TAB_BAR_BTN);
                        FormModelUtil.setAttribute(newTabBarBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, "Edit"); // $NON-NLS-1$
                        if (!StringUtil.isEmpty(dsName)) {
                            FormModelUtil.setAttribute(newTabBarBtn, XSPAttributeNames.XSP_ATTR_RENDERED, "#{javascript:!" + dsName + ".isEditable();}"); // $NON-NLS-1$ $NON-NLS-2$
                        }
                        
                        // Event Handler
                        Element eventHandler = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EVENT_HANDLER);
                        WizardUtils.setXspEventHandlerAttrs(eventHandler, "onClick", "true", "complete"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                        
                        // this.action
                        Element action = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, EventsXSPDefinitions.ACTION_TAG);
                        
                        // change document mode
                        Element changeMode = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, "changeDocumentMode"); // $NON-NLS-1$
                        FormModelUtil.setAttribute(changeMode, XSPAttributeNames.XSP_ATTR_MODE, "edit"); // $NON-NLS-1$
                        if (!StringUtil.isEmpty(dsName)) {
                            FormModelUtil.setAttribute(changeMode, XSPAttributeNames.XSP_ATTR_VAR, dsName);
                        }
                            
                        // Append all the elements
                        action.appendChild(changeMode);
                        eventHandler.appendChild(action);
                        newTabBarBtn.appendChild(eventHandler);
                        newTabBar.appendChild(newTabBarBtn);                                                
    
                        //
                        // Add a save document button
                        //
                        newTabBarBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TAB_BAR_BTN);
                        FormModelUtil.setAttribute(newTabBarBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, "Save"); // $NON-NLS-1$
                        if (!StringUtil.isEmpty(dsName)) {
                            FormModelUtil.setAttribute(newTabBarBtn, XSPAttributeNames.XSP_ATTR_RENDERED, "#{javascript:" + dsName + ".isEditable();}"); // $NON-NLS-1$ $NON-NLS-2$
                        }
    
                        // Event Handler
                        eventHandler = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EVENT_HANDLER);
                        WizardUtils.setXspEventHandlerAttrs(eventHandler, "onClick", "true", "complete"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                                                
                        // this.action
                        action = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, EventsXSPDefinitions.ACTION_TAG);
                        
                        // moveTo
                        Element moveTo = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO);
                        WizardUtils.setXspMoveToAttrs(moveTo, "Right to Left", "slide", "true", getSourcePageName(), "true"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
                        
                        // Append all the elements
                        action.appendChild(moveTo);
                        eventHandler.appendChild(action);
                        newTabBarBtn.appendChild(eventHandler);
                        newTabBar.appendChild(newTabBarBtn);                        
                    }
                    
                    if (getDeleteDocBtn()) {
                        // Add a delete documet button
                        Element newTabBarBtn = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_TAB_BAR_BTN);
                        FormModelUtil.setAttribute(newTabBarBtn, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, "Delete"); // $NON-NLS-1$
                        if (!StringUtil.isEmpty(dsName)) {
                            FormModelUtil.setAttribute(newTabBarBtn, XSPAttributeNames.XSP_ATTR_RENDERED, "#{javascript:!" + dsName + ".isNewNote();}"); // $NON-NLS-1$ $NON-NLS-2$
                        }
                        
                        // Event Handler
                        Element eventHandler = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EVENT_HANDLER);
                        WizardUtils.setXspEventHandlerAttrs(eventHandler, "onClick", "true", "complete"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                        
                        // this.action
                        Element action = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, EventsXSPDefinitions.ACTION_TAG);                        
                        
                        // this.actionGroup
                        Element actionGroup = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, "actionGroup");         // $NON-NLS-1$
                        
                        // deleteDocument
                        Element deleteDoc = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, "deleteDocument"); // $NON-NLS-1$
                        deleteDoc.setAttribute(XSPAttributeNames.XSP_ATTR_NAME, "");
    
                        FormModelUtil.setAttribute(deleteDoc, XSPAttributeNames.XSP_ATTR_VALIDATOR_MESSAGE, "Are you sure you want to delete this document?"); // $NLX-WizardSubPageMain.AreyousureyouwanttodeletethisDocu-1$
                        if (!StringUtil.isEmpty(dsName)) {
                            FormModelUtil.setAttribute(deleteDoc, XSPAttributeNames.XSP_ATTR_VAR, dsName);
                        }
                        
                        // moveTo
                        Element moveTo = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO);
                        WizardUtils.setXspMoveToAttrs(moveTo, "Right to Left", "slide", "true", getSourcePageName(), null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                        
                        // Append all the elements
                        actionGroup.appendChild(deleteDoc);
                        actionGroup.appendChild(moveTo);
                        action.appendChild(actionGroup);
                        eventHandler.appendChild(action);
                        newTabBarBtn.appendChild(eventHandler);
                        newTabBar.appendChild(newTabBarBtn);                                                
                    }
                    break;
            }
            
            base.appendChild(newTabBar);
        }                    
    }
    
    //
    // Gets the source Document Collection page for this Viewer page
    //
    protected String getSourcePageName() {
        // Base it on the Back Button if one is configured
        if (getSubPageBackBtn() == true) {
            int backIdx = getSubPageBackIndex();
            if (backIdx >= 0) {
                return wizardData.getSubPageName(backIdx);
            }
        }
        
        // No Back button - Find the first Doc Collection page
        // with this as a target
        for (WizardDataSub subPage : wizardData.pageList) {
            if (subPage.type == WizardData.PAGE_TYPE_VIEW) {
                if (((WizardSubPageDataView)subPage.wizardPage[2]).getTargetIndex() == pageData.index) {
                    return subPage.name;
                }
            }
        }
        
        return "";
    }
}