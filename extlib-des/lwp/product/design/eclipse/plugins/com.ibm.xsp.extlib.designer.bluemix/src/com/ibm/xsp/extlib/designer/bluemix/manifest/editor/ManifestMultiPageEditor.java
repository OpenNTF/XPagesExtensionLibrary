/*
 * © Copyright IBM Corp. 2015, 2016
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestMetaModel.BluemixManifestEditorInput;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestMultiPageEditor extends MultiPageEditorPart implements IWindowListener, ISelectionProvider {
    
    private static final int            PAGE_GENERAL = 0;
    private static final int            PAGE_ENV     = 1;
    private static final int            PAGE_HYBRID  = 2;
    private static final int            PAGE_SOURCE  = 3;

    private ManifestEditorPage          _genEditor;
    private ManifestHybridEditorPage    _hybridEditor;
    private ManifestEnvEditorPage       _envEditor;
    private ManifestTextEditor          _srcEditor;

    private IEditorInput                _editorInput;
    private FormToolkit                 _toolkit;    
    private ManifestBeanLoader          _beanLoader;
    private ManifestBean                _bean;
    private IDominoDesignerProject      _designerProject;
    private ISelection                  _editorSelection = null;
    private int                         _currentPage = PAGE_GENERAL;


    public ManifestMultiPageEditor() {
    }

    @Override
    protected void createPages() {        
        String label = BluemixUtil.productizeString(StringUtil.format("%BM_PRODUCT% Manifest - {0}", _designerProject.getDatabaseTitle())); // $NLX-ManifestMultiPageEditor.IBMBluemixManifest0-1$
        setPartName(label);       
        
        Composite ourContainer = this.getContainer();        
        if ( _toolkit == null){
            _toolkit = new FormToolkit(ourContainer.getDisplay() );
            _toolkit.setBackground(ourContainer.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            _toolkit.setBorderStyle(ourContainer.getBorderWidth());
        }
        
        ourContainer.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (_toolkit != null) {
                    _toolkit.dispose();
                    _toolkit = null;
                }
                PlatformUI.getWorkbench().removeWindowListener(ManifestMultiPageEditor.this);
            }
        });
        
        try {
            // Add the General page
            _genEditor = new ManifestEditorPage(this.getContainer(), _toolkit, this);
            setupPage(PAGE_GENERAL, _genEditor, "General");  // $NLX-ManifestMultiPageEditor.General-1$

            // Add the Environment Variables page
            _envEditor = new ManifestEnvEditorPage(this.getContainer(), _toolkit, this);
            setupPage(PAGE_ENV, _envEditor, "Environment Variables"); // $NLX-ManifestMultiPageEditor.EnvironmentVariables-1$

            // Add the Hybrid Configuration page
            _hybridEditor = new ManifestHybridEditorPage(this.getContainer(), _toolkit, this);
            setupPage(PAGE_HYBRID, _hybridEditor, "Hybrid Configuration"); // $NLX-ManifestMultiPageEditor.HybridConfiguration-1$

            // Add the Source page
            _srcEditor = new ManifestTextEditor();
            addPage(_srcEditor, _editorInput);
            setPageText(PAGE_SOURCE, "Source"); // $NLX-ManifestMultiPageEditor.Source-1$
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "createPages", e, "Failed to create visual editor"); // $NON-NLS-1$ $NLE-ManifestMultiPageEditor.Failedtocreatevisualeditor-2$
            }
        } 
        
        PlatformUI.getWorkbench().addWindowListener(this);
    }
    
    protected void setupPage(int index, AbstractManifestEditorPage page, String tabText) throws NodeException {
        page.getDataNode().setClassDef(_beanLoader.getClassOf(_bean));
        page.getDataNode().setDataProvider(new SingleCollection(_bean));
        page.getDataNode().setModelModified(false);   
        addPage(page);
        setPageText(index, tabText);
        page.refreshUI();
    }

    @Override
    protected void pageChange(int newPageIndex) {
        if (_currentPage == PAGE_SOURCE) {
            // Moving from the Source Editor to visual - update the bean from the src
            String contents = getSrcEditor().getDocumentProvider().getDocument(_editorInput).get();     
            _bean.loadFromString(contents);
        }
        
        switch (newPageIndex) {
            case PAGE_GENERAL:
                refreshPage(_genEditor);
                break;
                
            case PAGE_ENV:
                refreshPage(_envEditor);
                break;

            case PAGE_HYBRID:
                refreshPage(_hybridEditor);
                break;
                
            default:
                break;
        }
        
        super.pageChange(newPageIndex);
        _currentPage = this.getActivePage();
    }
    
    public void refreshPage(AbstractManifestEditorPage page) {
        if (_bean.isManifestValid()) {
            page.getDataNode().notifyInvalidate(null);
            page.refreshUI();
            page.hideError();
        } else {
            page.displayError();
        }        
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput editorInput) throws PartInitException {
        _editorInput = editorInput;
        _designerProject = ((BluemixManifestEditorInput)_editorInput).getDesignerProject();
        
        // Setup the bean and the bean loader
        _beanLoader = new ManifestBeanLoader("bluemix.manifest", (FileStoreEditorInput) editorInput, this); // $NON-NLS-1$
        _bean = new ManifestBean((FileStoreEditorInput)editorInput);
        
        super.init(editorSite, editorInput);
        getSite().setSelectionProvider(this);
    }

    @Override
    public void doSave(IProgressMonitor progress) {
        if (_srcEditor.isDirty()) {
            // Save 
            _srcEditor.doSave(progress);

            // Update the bean file modified time so we're
            // not prompting the user to reload
            _bean.resetModifiedTime();
            
            // Have to do this to update the dirty state of the editor
            firePropertyChange(IEditorPart.PROP_DIRTY); 
        }
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    public TextEditor getSrcEditor() {
        return _srcEditor;
    }
    
    public ManifestBean getBean() {
        return _bean;
    }
    
    public IEditorInput getEditorInput() {
        return _editorInput;
    }
    
    // Function to update the source editor with the contents of the bean
    public void writeContentsFromBean() {
        getSrcEditor().getDocumentProvider().getDocument(getEditorInput()).set(getBean().getContents());        
    }
    
    public IDominoDesignerProject getDesignerProject() {
        return _designerProject;
    }
    
    @Override
    public void setFocus() {
        super.setFocus();
        
        // Every time the editor gets focus check if it has been modified externally
        checkForExternalChange();
    }

    @Override
    public void windowActivated(IWorkbenchWindow arg0) {
        // Every time the editor gets focus check if it has been modified externally
        checkForExternalChange();
    }

    @Override
    public void windowClosed(IWorkbenchWindow arg0) {
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow arg0) {
    }

    @Override
    public void windowOpened(IWorkbenchWindow arg0) {
    }
    
    private void checkForExternalChange() {
        // Check has the file been externally modified
        if (_bean.externallyModified()) {
            // Only do this once per change
            _bean.resetModifiedTime();
            
            // Ask the reload question
            String msg = "The file '{0}' has been changed on the file system. Do you wish to replace the editor contents with these changes?"; // $NLX-ManifestMultiPageEditor.Thefile0hasbeenchangedonthefilesy-1$
            if (MessageDialog.openQuestion(null, "File changed", StringUtil.format(msg, _bean.getFileName()))) { // $NLX-ManifestMultiPageEditor.Filechanged-1$
                // Reload the file
                _srcEditor.doRevertToSaved();
                
                // Make sure the contents are reflected in the editor
                pageChange(getActivePage());
            } else {
                // User has chosen not to reload - show editor as "dirty"
                _srcEditor.setExternallyModified(true);
                firePropertyChange(IEditorPart.PROP_DIRTY); 
            }
        }        
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener arg0) {
    }

    @Override
    public ISelection getSelection() {
        if (_editorSelection == null) {
            _editorSelection = new StructuredSelection(getDesignerProject());
        }
        return _editorSelection;
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
    }

    @Override
    public void setSelection(ISelection selection) {
        _editorSelection = selection;
    }
}