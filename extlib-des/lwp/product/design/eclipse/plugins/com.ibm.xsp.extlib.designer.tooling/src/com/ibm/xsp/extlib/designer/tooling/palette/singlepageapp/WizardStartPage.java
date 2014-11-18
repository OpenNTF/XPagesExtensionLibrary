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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.designer.domino.navigator.NavigatorPlugin;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class WizardStartPage extends WizardBasePage implements SelectionListener {
    private static final Image STAR = NavigatorPlugin.getImage("design/defaultdeselt.png"); // $NON-NLS-1$
    
    private Composite   container;
    private Button      newBtn;
    private Button      editBtn;
    private Button      deleteBtn;
    private Button      createCCBtn;
    private TableViewer tableViewer;
    private WizardData  wizardData;

    protected WizardStartPage(WizardData wd) {
        super();
        setTitle(WizardData.WIZARD_TITLE);
        wizardData = wd;
    }

    @Override
    public void refreshData() {
        super.refreshData();
        setMessage(getStepTxt() + "Configure the Application Pages for this Single Page Application.", IMessageProvider.INFORMATION); // $NLX-WizardStartPage.ConfiguretheApplicationPagesforth-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        
        container = new Composite(parent, SWT.NONE);   
        
        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);
        
        int[] colWeights = {6, 47, 47};
        tableViewer = WizardUtils.createTableViewer(container, 1, 3, colWeights);
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);         
        
        // Create the Initial column
        TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("");  
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return null;
            }
            
            @Override
            public Image getImage(Object element) {
                if (((WizardDataSub)element).isInitialPage) {
                    return STAR;
                } 
                return null;
            }
            
            @Override
            public String getToolTipText(Object element) {
                if (((WizardDataSub)element).isInitialPage) {
                    return "Initial Application Page"; // $NLX-WizardStartPage.InitialApplicationPage-1$
                }
                return null;
            }

            @Override
            public Point getToolTipShift(Object object) {
              return new Point(5, 5);
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
              return 100; // msec
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
              return 5000; // msec
            }
            
        });
        col.setEditingSupport(new InitialEditingSupport());

        // Create the Name column
        col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Name");  // $NLX-WizardStartPage.Name-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
              return ((WizardDataSub)element).name;
            }
          });
        
        // Create the Type column
        col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Type");  // $NLX-WizardStartPage.Type-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (((WizardDataSub)element).type >= 0) {
                    return WizardData.PAGE_TYPES[((WizardDataSub)element).type];
                }
                return "";
            }
          });
        
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                editPageEntry();
                refreshButtonState();
            }
        });        
        
        // Add a key listener to listen for the SPACE key
        tableViewer.getTable().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.SPACE) {
                    int sel = tableViewer.getTable().getSelectionIndex();
                    if (sel >= 0) {
                        checkPage(sel);
                        tableViewer.refresh();                        
                    }
                }
            }
        });
        
        Composite buttonComposite = new Composite(container, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 1;
        buttonComposite.setLayout(layout);
        newBtn = WizardUtils.createButton(buttonComposite, "New", this); // $NLX-WizardStartPage.New-1$
        deleteBtn = WizardUtils.createButton(buttonComposite, "Delete", this); // $NLX-WizardStartPage.Delete-1$
        editBtn = WizardUtils.createButton(buttonComposite, "Edit", this); // $NLX-WizardStartPage.Edit-1$
        GridData buttonLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
        buttonComposite.setLayoutData(buttonLayoutData);
        
        createCCBtn = WizardUtils.createCheckBox(container, "Create a Custom Control for the content of each Application Page", 2, true); // $NLX-WizardStartPage.CreateCustomControlsforeachApplic-1$
        createCCBtn.addSelectionListener(this);            

        refreshButtonState();
        
        setControl(container);
        setPageComplete(true);
    }
    
    //
    // Displays the Dialog for editing a Page entry
    //
    private void editPageEntry() {
        WizardDataSub sp;
        int sel = tableViewer.getTable().getSelectionIndex();
        if (sel >= 0) {
            sp = wizardData.pageList.get(sel);
            EditNavigatorDialog dialog = new EditNavigatorDialog(getShell(), "Edit Application Page", "Edit the Name and Type for this Application Page", sp); // $NLX-WizardStartPage.EditApplicationPage-1$ $NLX-WizardStartPage.EdittheNameandTypeforthisApplicat-2$
            dialog.create();
            if (dialog.open() == Window.OK) {
                refreshTable(sel);
            }
        }
    }
    
    public boolean getCreateCC() {
        return WizardUtils.getCheckBoxValue(createCCBtn, true);
    }    
    
    //
    // Puts the buttons in the correct enabled state
    //
    private void refreshButtonState() {
        wizardData.calculateWizardStepCount();
        refreshData();
        boolean state = tableViewer.getTable().getSelectionIndex() >= 0;
        deleteBtn.setEnabled(state);
        editBtn.setEnabled(state);
        
        // Any Errors on the Page ??
        if (getCreateCC()) {
            if (wizardData.isCustomControlConflict()) {
                setErrorMessage("There is a conflict with an existing Custom Control."); // $NLX-WizardStartPage.ThereisaconflictwithanexistingCus-1$
                setPageComplete(false);
                return;
            }
        } 
        
        // No Errors
        setPageComplete(true);
        setErrorMessage(null);        
    }

    public void widgetSelected(SelectionEvent event) {
        WizardDataSub sp;
        int sel = tableViewer.getTable().getSelectionIndex();
        
        if (event.widget == newBtn) {
            sp = new WizardDataSub(wizardData, wizardData.pageList.size());
            EditNavigatorDialog dialog = new EditNavigatorDialog(getShell(), "New Application Page", "Enter the Name and Type for this new Application Page.", sp); // $NLX-WizardStartPage.NewApplicationPage-1$ $NLX-WizardStartPage.EntertheNameandTypeforthisnewAppl-2$
            dialog.create();
            if (dialog.open() == Window.OK) {
                wizardData.pageList.add(sp);
                refreshTable(wizardData.pageList.size()-1);
            }             
        } else if (event.widget == deleteBtn) {
            if (sel >=0) {
                wizardData.deletePage(sel);
                refreshTable(sel <= wizardData.pageList.size()-1 ? sel : wizardData.pageList.size()-1);
            }
        } else if (event.widget == editBtn) {
            editPageEntry();
        }
        
        refreshButtonState();
        getWizard().getContainer().updateButtons();                
    }
    
    //
    // Refreshes the Table Data and set the index
    //
    private void refreshTable(int sel) {
        tableViewer.setInput(wizardData.pageList.toArray());     
        tableViewer.getTable().setSelection(sel);
        if (getInitialPageIndex() == -1) {
            checkPage(0);
            tableViewer.refresh();
        }
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }
    
    //
    // Returns the next Wizard Page to display
    //
    @Override
    public WizardPage getNextPage() {
        refreshData();
        
        if(wizardData.pageList.size() > 0) {
            // Add the first sub page
            WizardDataSub pd = wizardData.pageList.get(0);
            if (pd != null) {
                if (pd.wizardPage[0] == null) {
                    pd.wizardPage[0] = new WizardSubPageMain(pd);
                    ((Wizard)(getWizard())).addPage(pd.wizardPage[0]);
                }
                return(pd.wizardPage[0]);
            }
        }
        return(null);
    }
    
    //
    // Sets the checkbox for "idx" and unchecks all others
    //
    protected void checkPage(int idx) {
        for (WizardDataSub ds:wizardData.pageList) {
            ds.isInitialPage = (ds.index == idx);
        }        
    }
    
    //
    // Gets the initial page index
    //
    public int getInitialPageIndex() {
        for (WizardDataSub ds:wizardData.pageList) {
            if (ds.isInitialPage) {
                return ds.index;
            }
        }
        
        return -1;
    }
        
    //
    // Class for the Add/Edit Entry Dialog
    //
    private class EditNavigatorDialog extends TitleAreaDialog {

        private Text txtLabel;
        private Combo comboType;

        private String dialogTitle;
        private String dialogDesc;
                
        private WizardDataSub subPage;

        public EditNavigatorDialog(Shell parentShell, String title, String desc, WizardDataSub sp) {
            super(parentShell);
            dialogTitle = title;
            dialogDesc = desc;
            subPage = sp;
        }

        @Override
        public void create() {
            super.create();
            setTitle(dialogTitle);
            setMessage(dialogDesc, IMessageProvider.INFORMATION);
            if (subPage.name.length() == 0) {
                // Disable the ok button
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }            
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite area = (Composite) super.createDialogArea(parent);

            Composite container = new Composite(area, SWT.NONE);
            container.setLayoutData(new GridData(GridData.FILL_BOTH));

            GridLayout layout = WizardUtils.createGridLayout(2, 10);
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            container.setLayout(layout);

            WizardUtils.createLabel(container, "Name :", 1); // $NLX-WizardStartPage.Name.1-1$
            txtLabel = WizardUtils.createText(container);
            txtLabel.setText(subPage.name);
            txtLabel.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    // Get the widget whose text was modified
                    Text text = (Text) event.widget;
                    String pageName = text.getText();
                    
                    // Validate the text
                    if (pageName.length() == 0) {
                        displayError("Name cannot be blank.");      // $NLX-WizardStartPage.Namecannotbeblank-1$
                        return;
                    }
                   
                    // Check each char
                    boolean invalidChar = false;
                    for (int i=0; i<pageName.length(); i++) {
                        if ((pageName.charAt(i) != '_') && (!Character.isDigit(pageName.charAt(i))) && (!Character.isLetter(pageName.charAt(i)))) {
                            invalidChar = true;
                            break;
                        }
                    }
                    
                    if (invalidChar) {
                        displayError("A name can only contain numbers, letters and underscores (_)."); // $NLX-WizardStartPage.Anamecanonlycontainnumbersletters-1$
                        return;
                    }
                    
                    if( wizardData.doesSubPageExist(pageName, subPage.index)) {
                        displayError("Page already exists.");    // $NLX-WizardStartPage.Pagealreadyexists-1$
                        return;
                    }
                    
                    if (getCreateCC()) {
                        if (wizardData.doesCustomControlExist(pageName)) {
                            displayError(MessageFormat.format("A Custom Control with this name ({0}) already exists.", WizardData.CUSTOM_CONTROL_PREFIX + text.getText()));    // $NLX-WizardStartPage.ACustomControlwiththisname0alread-1$
                            return;
                        }
                    }
                    
                    clearError();                                                
                }
            });
            
            WizardUtils.createLabel(container, "Type :", 1); // $NLX-WizardStartPage.Type.1-1$
            comboType = WizardUtils.createCombo(container, 1, WizardData.PAGE_TYPES, subPage.type, null);

            return area;
        }

        @Override
        protected boolean isResizable() {
            return true;
        }

        // save content of the fields because they get disposed
        // as soon as the Dialog closes
        private void saveInput() {
            subPage.name = txtLabel.getText();
            
            // If the type of the Page is being changed then the
            // first Wizard subPage will have to be recreated to
            // display different controls
            if (subPage.type != comboType.getSelectionIndex()) {
                subPage.wizardPage[0] = null;
            }
            
            subPage.type = comboType.getSelectionIndex();
            
        }

        @Override
        protected void okPressed() {
            saveInput();
            super.okPressed();
        }
        
        // 
        // Displays and error and disables OK
        //
        protected void displayError(String error) {
            setErrorMessage(error);            
            getButton(IDialogConstants.OK_ID).setEnabled(false);            
        }

        //
        // Clears the error display and enables OK
        //
        protected void clearError() {
            setErrorMessage(null);            
            getButton(IDialogConstants.OK_ID).setEnabled(true);            
        }
    }
    
    //
    // Class that allows the changing of the Initial Application
    // Page with a mouse click in the first column
    //
    private class InitialEditingSupport extends EditingSupport {

        public InitialEditingSupport() {
            super(tableViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            return ((WizardDataSub)element).isInitialPage;
        }

        @Override
        protected void setValue(Object element, Object value) {
            if ((Boolean) value) {
                checkPage(((WizardDataSub)element).index);
                tableViewer.refresh();
            }
        }
    }
}