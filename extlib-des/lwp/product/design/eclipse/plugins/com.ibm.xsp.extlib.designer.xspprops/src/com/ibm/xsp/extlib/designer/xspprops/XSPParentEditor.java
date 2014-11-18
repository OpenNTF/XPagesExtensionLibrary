/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.xspprops;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeAdapter;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.iloader.node.loaders.JavaBeanLoader;
import com.ibm.commons.swt.dialog.LWPDMessageDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.domino.xsp.module.nsf.NSFComponentModule;

/**
 * @author mleland
 *
 */
@SuppressWarnings("restriction") //$NON-NLS-1$
public class XSPParentEditor extends MultiPageEditorPart implements ISelectionProvider {
    private FileEditorInput dbPropInput = null;
    private IProject project;
    private IFile xspDesignProps;
    private XSPDesignPropsBean xspDesignPropsBean;
    private XSPPropBean dbBean = null;
    private DesignerProject dp;
    private XSPPropBeanLoader jbl = null;
    private JavaBeanLoader xspDesignLoader;
    private boolean bPromptRecompileOnExit = false;
    private IClassDef dbPropClassDef = null;
    private IClassDef xspDesignPropsClassDef;
    protected ISelection editorSelection = null;
    protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();
    private FormToolkit toolkit = null;
    private XSPPage page1 = null;
    private XSPPerfPage page2 = null;
    private XSPGenPage page3 = null;
    private PropertiesFileEditor pfe = null;
    private XSPAllProperties props = null;
    private int GENERAL_TAB = 0;
    private int PERF_TAB = 1;
    private int HTML_TAB = 2;
    private int sourcePageNum = 0;
    private int curPage = 0;
    private final String DESIGN_PATH = "AppProperties/xspdesign.properties"; // $NON-NLS-1$
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor progress) {
        if (isDirty()) {
            // save the contents of the source editor, always
            pfe.doSave(progress);
            dbBean.save(progress);  // no real saving here, but some necessary cleanup
            xspDesignPropsBean.save(progress);
            page1.getDataNode().setModelModified(false);
            page2.getDataNode().setModelModified(false);
            page3.getDataNode().setModelModified(false);
            setModified(false);
            
            if (isBPromptRecompileOnExit()) {
                if( LWPDMessageDialog.openQuestion(null, "Domino Designer", // $NLX-XSPParentEditor.DominoDesigner-1$
                    "You have changed the minimum supported version for XPages in this application.  Do you want to rebuild this application now?") ) {  // $NLX-XSPParentEditor.Youhavechangedtheminimumsupported-1$
                    try {
                        getDesignerProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
                    } catch (CoreException e) {
                    }
                }
                setBPromptRecompileOnExit(false);   // if they chose no, still should not prompt again
            }
            NSFComponentModule.setLastDesignerSave(System.currentTimeMillis()); 
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
    }
    
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        setSite(site);
        setInput(input);
        String ns = "xspdbjbl"; // $NON-NLS-1$
        if (jbl == null) {
            jbl = new XSPPropBeanLoader(ns); //$NON-NLS-1$
        }
        ns = "xspdesignjbl"; // $NON-NLS-1$
        xspDesignLoader = new JavaBeanLoader(ns); //$NON-NLS-1$

        pfe = new PropertiesFileEditor();

        dbPropInput = (FileEditorInput)input;
        if (dbPropInput != null) {
            project = getDesignerProject();
            dp = (DesignerProject)Platform.getAdapterManager().getAdapter(project, DesignerProject.class);
            if(project != null){
                try {
                    IFile f = project.getFile(DESIGN_PATH);
                    f.getParent().refreshLocal(IFile.DEPTH_ONE, new NullProgressMonitor());
                    if(!f.exists()){
                        f.getParent().refreshLocal(IFile.DEPTH_ONE, new NullProgressMonitor());
                        f.create(new ByteArrayInputStream("".getBytes()), true, new NullProgressMonitor());
                    }
                    if(f.exists()){
                        xspDesignProps = f;
                        xspDesignPropsBean = new XSPDesignPropsBean(dp, xspDesignProps);
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            
            dbBean = new XSPPropBean(dp, getBeanLoader(), this, dbPropInput, xspDesignPropsBean);
            jbl.setPropFile(dbPropInput, this);
            props = dbBean.getXspProperties();
        }

        // this is our parent bean, that's the classdef we start with
        if (dbPropClassDef == null) {
            try {
                dbPropClassDef = getBeanLoader().getClassOf(dbBean);
            } catch (NodeException e1) {
                e1.printStackTrace();
            }
        }
        
        getSite().setSelectionProvider(this);
        if(dbBean != null){
            //We need to recompile certain xpages when the current database's external library
            //dependencies change. In order to do that we need to figure out what dependencies
            //this database has when we open the Application Properties Editor
            if(props != null){
                //Get the current xsp.properties
                String dependencies = props.getDependencies();
                if(project != null){
                    try {
                        //figure out what dependencies we have!
                        project.setSessionProperty(XSPAllProperties.XSP_DEPENDENCIES_PROP_INIT, dependencies);
                    } catch (CoreException e) {
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return (pfe.isDirty() || 
                page1.getDataNode().isModelModified() || 
                page2.getDataNode().isModelModified() ||
                page3.getDataNode().isModelModified());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if (getContainer() != null){
            getContainer().setFocus();
        }
    }

    @Override
    protected void createPages() {
        Composite ourContainer = this.getContainer();
        
        if ( toolkit == null){
            toolkit = new FormToolkit(ourContainer.getDisplay() );
            toolkit.setBackground(ourContainer.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            toolkit.setBorderStyle(ourContainer.getBorderWidth());
        }
        
        ourContainer.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (toolkit != null) {
                    toolkit.dispose();
                    toolkit = null;
                }
                getDBPropObject().release();
            }
        });
        
        page1 = new XSPPage(this.getContainer(), toolkit, this);
        addPage(page1);
        setPageText(GENERAL_TAB, "General"); // $NLX-XSPParentEditor.General-1$
        
        page1.getDataNode().setClassDef(getDBPropClassDef());
        page1.setDesignPropsClassDef(getXSPDesignPropClassDef());
        page1.setDesignPropsProvider(new SingleCollection(xspDesignPropsBean));
        SingleCollection col = new SingleCollection(dbBean);
        page1.getDataNode().setDataProvider(col);
        page1.getDataNode().addDataNodeListener(new DataNodeAdapter() {
            public void onModifiedChanged(DataNode source) {
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        });
        page1.initProject();
        page1.getDataNode().setModelModified(false);
        
        page2 = new XSPPerfPage(ourContainer, toolkit, this);
        page2.getDataNode().setClassDef(getDBPropClassDef());
        page2.getDataNode().setDataProvider(col);
        page2.getDataNode().addDataNodeListener(new DataNodeAdapter() {
            public void onModifiedChanged(DataNode source) {
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        });
        page2.initProject();
        page2.getDataNode().setModelModified(false);
        addPage(page2);
        setPageText(PERF_TAB, "Persistence"); // $NLX-XSPParentEditor.Persistence-1$
        
        page3 = new XSPGenPage(ourContainer, toolkit, this);
        page3.getDataNode().setClassDef(getDBPropClassDef());
        page3.getDataNode().setDataProvider(col);
        page3.getDataNode().addDataNodeListener(new DataNodeAdapter() {
            public void onModifiedChanged(DataNode source) {
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        });
        page3.initProject();
        page3.getDataNode().setModelModified(false);
        addPage(page3);
        setPageText(HTML_TAB, "Page Generation"); // $NLX-XSPParentEditor.PageGeneration-1$
        
        // allow for extensions in here
        
        // since we're about to add one, this is the index of the source tab
        sourcePageNum = this.getPageCount();     
        try {
            addPage(pfe, getDBPropInput());
            setPageText(sourcePageNum, "Source"); // $NLX-XSPParentEditor.Source-1$
        } catch (PartInitException e) {
            e.printStackTrace();
        }
        setPartName(getTabLabel());
    }
    
    protected String getTabLabel() {
        String dbName = null;
        if (dp != null)
            dbName = dp.getDatabaseTitle();
        else
            dbName = project.getName();
        String partName =  StringUtil.format("{0} - {1}", getTitle(), dbName);
        return partName; 
    }

    public DesignerProject getDominoDesignerProject() {
        IProject ourProject = getDesignerProject();
        if (ourProject != null) {
            dp = DesignerResource.getDesignerProject(ourProject);
            return dp;
        }
        else
            return null;
    }

    protected IProject getDesignerProject() {
        if (project == null) {
            if (dbPropInput != null) {
                project = dbPropInput.getFile().getProject();
                return project;
            }
        }
        return project;
    }

    public XSPPropBeanLoader getBeanLoader() {
        return jbl;
    }

    public boolean isBPromptRecompileOnExit() {
        return bPromptRecompileOnExit;
    }

    public void setBPromptRecompileOnExit(boolean promptRecompileOnExit) {
        bPromptRecompileOnExit = promptRecompileOnExit;
    }

    public FileEditorInput getDBPropInput() {
        return dbPropInput;
    }
    
    public XSPPropBean getDBPropObject() {
        return dbBean;
    }

    public void addSelectionChangedListener(ISelectionChangedListener arg0) {
        
    }

    public ISelection getSelection() {
        if (editorSelection == null) {
            editorSelection = new StructuredSelection(getDominoDesignerProject());
        }
        return editorSelection;
    }

    public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
    }

    public void setSelection(ISelection selection) {
        editorSelection = selection;

        for (Iterator<ISelectionChangedListener> listeners = selectionChangedListeners.iterator(); listeners.hasNext(); ) {
            ISelectionChangedListener listener = listeners.next();
            listener.selectionChanged(new SelectionChangedEvent(this, selection));
        }
    }
    
    public void setModified(boolean isDirty) {
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    public IClassDef getDBPropClassDef() {
        if (dbPropClassDef == null) {
            try {
                if (getBeanLoader() == null) {
                    jbl = new XSPPropBeanLoader("dbjbl"); //$NON-NLS-1$
                }
                dbPropClassDef = getBeanLoader().getClassOf(dbBean);
            } catch (NodeException e) {
            }
        }
        return dbPropClassDef;
    }
    
    public IClassDef getXSPDesignPropClassDef() {
        if (xspDesignPropsClassDef == null) {
            try {
                xspDesignPropsClassDef = xspDesignLoader.getClassOf(xspDesignPropsBean);
            } catch (NodeException e) {
            }
        }
        return xspDesignPropsClassDef;
    }
    
    public PropertiesFileEditor getPropertiesEditor() {
        return pfe;
    }

    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);                     // do this so the delete key works right!!
        // if switching away from the source tab, update the other page data nodes
        if (curPage == sourcePageNum && newPageIndex != sourcePageNum) {
            String docContents = pfe.getDocumentProvider().getDocument(dbPropInput).get();
            Properties jProps = dbBean.getXspProperties().getPropertiesObj();
            byte[] bytes;
            InputStream is = null;
            try {
                bytes = docContents.getBytes("8859_1"); // properties files have to have this encoding
                is = new ByteArrayInputStream(bytes);
                jProps.clear();
                jProps.load(is);
                page1.getDataNode().notifyInvalidate(null);
                page2.getDataNode().notifyInvalidate(null);
                page3.getDataNode().notifyInvalidate(null);
            } catch (IOException e) {
            }
            finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        curPage = this.getActivePage();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
     */
    @Override
    public void dispose() {
        jbl = null;
        xspDesignLoader = null;
        super.dispose();
    }
}