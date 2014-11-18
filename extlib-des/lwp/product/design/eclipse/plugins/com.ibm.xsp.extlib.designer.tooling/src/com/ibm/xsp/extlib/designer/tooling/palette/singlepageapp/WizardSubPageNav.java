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
import java.util.ArrayList;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class WizardSubPageNav extends WizardSubPage {
    private Button                  newBtn;
    private Button                  editBtn;
    private Button                  deleteBtn;
    private Button                  moveUpBtn;
    private Button                  moveDownBtn;
    private TableViewer             tableViewer;
    private ArrayList <NavTarget>   targetList;

    protected WizardSubPageNav(WizardDataSub pd) {
        super(pd);
        targetList = new ArrayList<NavTarget>();
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);   
        
        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);
        
        tableViewer = WizardUtils.createTableViewer(container, 1, 2, null);
        
        // Create Label column
        TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Entry");  // $NLX-WizardSubPageNav.Entry-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
              return ((NavTarget)element).label;
            }
          });
        
        // Create Target the column
        col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Target Page");  // $NLX-WizardSubPageNav.TargetPage-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (((NavTarget)element).page >= 0) {
                    return wizardData.getPageNameList()[((NavTarget)element).page];
                }
                return "";
            }
          });
        
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                editNavEntry();
            }
        });        
        
        Composite buttonComposite = new Composite(container, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 1;
        buttonComposite.setLayout(layout);
        newBtn = WizardUtils.createButton(buttonComposite, "New", this); // $NLX-WizardSubPageNav.New-1$
        deleteBtn = WizardUtils.createButton(buttonComposite, "Delete", this); // $NLX-WizardSubPageNav.Delete-1$
        editBtn = WizardUtils.createButton(buttonComposite, "Edit", this); // $NLX-WizardSubPageNav.Edit-1$
        moveUpBtn = WizardUtils.createButton(buttonComposite, "Move Up", this); // $NLX-WizardSubPageNav.MoveUp-1$
        moveDownBtn = WizardUtils.createButton(buttonComposite, "Move Down", this); // $NLX-WizardSubPageNav.MoveDown-1$
        GridData buttonLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
        buttonComposite.setLayoutData(buttonLayoutData);
     
        refreshButtonState();

        setControl(container);
        setPageComplete(true);
    }
    
    //
    // Displays the Edit Entry Dialog
    //
    private void editNavEntry() {
        int sel = tableViewer.getTable().getSelectionIndex();
        if (sel >= 0) {
            EditNavigatorDialog dialog = new EditNavigatorDialog(getShell(), "Edit Application Navigator Entry", "Edit Application Navigator Entry details.", targetList.get(sel)); // $NLX-WizardSubPageNav.EditApplicationNavigatorEntry-1$ $NLX-WizardSubPageNav.EditApplicationNavigatorEntrydeta-2$
            dialog.create();
            if (dialog.open() == Window.OK) {
                NavTarget nt;
                nt = dialog.getNavTarget();                    
                targetList.set(sel, nt);
                tableViewer.setInput(targetList.toArray());
                tableViewer.getTable().setSelection(sel);
            }
        }        
    }
    
    @Override
    public void refreshData() {
        super.refreshData();
        setMessage(getStepTxt() + MessageFormat.format("Configure the Application Navigator for the \"{0}\" Application Page.", pageData.name), IMessageProvider.INFORMATION);     // $NLX-WizardSubPageNav.ConfiguretheApplicationNavigatorf-1$
        tableViewer.setInput(targetList.toArray());
    }
    
    private void refreshButtonState() {
        boolean state = tableViewer.getTable().getSelectionIndex() >= 0;
        deleteBtn.setEnabled(state);
        editBtn.setEnabled(state);
        moveUpBtn.setEnabled(state);
        moveDownBtn.setEnabled(state);
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        NavTarget nt;
        int sel = tableViewer.getTable().getSelectionIndex();
        
        if (event.widget == newBtn) {
            EditNavigatorDialog dialog = new EditNavigatorDialog(getShell(), "New Application Navigator Entry", "Enter new Application Navigator Entry details.", null); // $NLX-WizardSubPageNav.NewApplicationNavigatorEntry-1$ $NLX-WizardSubPageNav.EnternewApplicationNavigatorEntry-2$
            dialog.create();
            if (dialog.open() == Window.OK) {
                nt = dialog.getNavTarget();
                targetList.add(nt);
                tableViewer.setInput(targetList.toArray());
                tableViewer.getTable().setSelection(targetList.size()-1);
            }             
        } else if (event.widget == deleteBtn) {
            if (sel >=0) {
                targetList.remove(sel);
                tableViewer.setInput(targetList.toArray());
                tableViewer.getTable().setSelection(sel <= targetList.size()-1 ? sel : targetList.size()-1);
            }
        } else if (event.widget == editBtn) {
            editNavEntry();
        } else if (event.widget == moveUpBtn) {
            if (sel > 0) {
                NavTarget tmp = targetList.get(sel-1);
                targetList.set(sel-1, targetList.get(sel));
                targetList.set(sel, tmp);
                tableViewer.setInput(targetList.toArray());
                tableViewer.getTable().setSelection(sel-1);
            }
        } else if (event.widget == moveDownBtn) {
            if (sel < targetList.size()-1) {
                NavTarget tmp = targetList.get(sel+1);
                targetList.set(sel+1, targetList.get(sel));
                targetList.set(sel, tmp);
                tableViewer.setInput(targetList.toArray());
                tableViewer.getTable().setSelection(sel+1);
            }
        }
        refreshButtonState();
    }

    public int getNavItemCount() {
        return targetList.size();
    }
    
    public String getNavItemLabel(int idx) {
        if (idx < getNavItemCount()) {
            return targetList.get(idx).label;
        }
        
        return "";
    }

    public int getNavItemPage(int idx) {
        if (idx < getNavItemCount()) {
            return targetList.get(idx).page;
        }
        
        return -1;
    }
    
    @Override
    public void pageDeleted(int idx) {
        for (int i=0; i < targetList.size(); i++) {
            NavTarget nt = targetList.get(i);
            if (nt.page == idx) {
                nt.page = -1;
            } else if (nt.page > idx) {
                nt.page -= 1;                    
            }                    
        }
    }

    @Override
    public void addElementMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();
        
        Element outline = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_OUTLINE);
        Element treeNodes = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_THIS_TREE_NODES);      
        for (int i=0; i < getNavItemCount(); i++) {
            Element leaf = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_BASIC_LEAF_NODE);
            FormModelUtil.setAttribute(leaf, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, getNavItemLabel(i));
            if (getNavItemPage(i) >= 0) {
                FormModelUtil.setAttribute(leaf, IExtLibAttrNames.EXT_LIB_ATTR_HREF, "#" + wizardData.getSubPageName(getNavItemPage(i)));
            }
            treeNodes.appendChild(leaf);
        }
        outline.appendChild(treeNodes);
        base.appendChild(outline);        
    }
    
    //
    // Class for Edit/Add Navigator Item
    //
    private class EditNavigatorDialog extends TitleAreaDialog {

        private Text txtLabel;
        private WizardPageCombo comboTarget;

        private String label;
        private int page;
        
        private String dialogTitle;
        private String dialogDesc;
                
        private NavTarget navTarget;

        public EditNavigatorDialog(Shell parentShell, String title, String desc, NavTarget nt) {
            super(parentShell);
            dialogTitle = title;
            dialogDesc = desc;
            if (nt == null) {
                navTarget = new NavTarget("", -1);
            } else {
                navTarget = nt;
            }
        }

        @Override
        public void create() {
            super.create();
            setTitle(dialogTitle);
            setMessage(dialogDesc, IMessageProvider.INFORMATION);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite area = (Composite) super.createDialogArea(parent);

            Composite container = new Composite(area, SWT.NONE);
            container.setLayoutData(new GridData(GridData.FILL_BOTH));

            GridLayout layout = new GridLayout(2, false);
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            container.setLayout(layout);

            WizardUtils.createLabel(container, "Entry :", 1); // $NLX-WizardSubPageNav.Entry.1-1$
            txtLabel = WizardUtils.createText(container);
            txtLabel.setText(navTarget.label);

            WizardUtils.createLabel(container, "Target Page :", 1); // $NLX-WizardSubPageNav.TargetPage.1-1$
            comboTarget = new WizardPageCombo(container, 1, pageData, true, WizardData.PAGE_TYPE_INVALID);
            comboTarget.refresh();
            comboTarget.setSelectedIndex(navTarget.page);

            return area;
        }

        @Override
        protected boolean isResizable() {
            return true;
        }

        // save content of the fields because they get disposed
        // as soon as the Dialog closes
        private void saveInput() {
            label = txtLabel.getText();
            page = comboTarget.getSelectedIndex();
        }

        @Override
        protected void okPressed() {
            saveInput();
            super.okPressed();
        }

        public NavTarget getNavTarget() {
            return new NavTarget(label, page);
        }
    }     
    
    //
    // Models a Navigation Item
    //
    private class NavTarget {
        public String label;
        public int page;
        
        public NavTarget(String lbl, int pg) {
            label = lbl;
            page = pg;
        }
    }    
}
