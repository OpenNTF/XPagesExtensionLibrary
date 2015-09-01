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

package com.ibm.xsp.extlib.designer.bluemix.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.NsfException;
import com.ibm.designer.domino.ide.resources.project.DominoDesignerProject;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.ui.commons.IDesignerSelection;
import com.ibm.designer.prj.resources.commons.IDesignElement;
import com.ibm.designer.prj.resources.commons.IMetaModelDescriptor;
import com.ibm.workplace.noteswc.NotesViewContext;
import com.ibm.workplace.noteswc.editors.NotesEditorPart;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ToolbarAction implements IWorkbenchWindowPulldownDelegate2 {
    
    static public IDominoDesignerProject project = null;
    static public String                 xpage   = null;

    @Override
    public void run(IAction action) {
        DeployAction.deployWithQuestion();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        project = null;
        xpage = null;
        
        if ( selection instanceof IStructuredSelection && !selection.isEmpty()) {
            Object selectionObject = ((IStructuredSelection)selection).getFirstElement();
            if (selectionObject instanceof IFile) {
                // Record if an XPage is selected in the Navigator
                xpage = BluemixUtil.getXPageName((IFile) selectionObject);
                selectionObject = ((IResource)selectionObject).getProject();
            } 

            if(selectionObject instanceof IDesignerSelection) {
                project = (IDominoDesignerProject)((IDesignerSelection)selectionObject).getDesignerProject();
                selectionObject = ((IDesignerSelection)selectionObject).getSelectionObject();
            } else if (selectionObject instanceof DominoDesignerProject) {
                project = (IDominoDesignerProject)selectionObject;
            } else if (selectionObject instanceof IMetaModelDescriptor) {
                project = (IDominoDesignerProject)((IStructuredSelection)selection).toList().get(1);
            } else if (selectionObject instanceof IDesignElement) {
                project = DominoResourcesPlugin.getNotesDesignElement((IDesignElement)selectionObject).getDesignerProject();
            } else if (selectionObject instanceof IProject) { 
                try {
                    project = DominoResourcesPlugin.getDominoDesignerProject((IProject)selectionObject);
                } catch (NsfException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(this, "selectionChanged", e, "Failed to get Domino Designer Project"); // $NON-NLS-1$ $NLE-ToolbarAction.FailedtogetDominoDesignerProject-2$
                    }
                }
            } 
        }
        
        if (project == null) {            
            // Can't get the project from the Selection - Try open editor instead
            try {
                IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();                
                if (editorPart instanceof NotesEditorPart) {
                    NotesViewContext context = ((NotesEditorPart)editorPart).getContext();
                    String url = context.getUrlWithPath();
                    if (!StringUtil.isEmpty(url)) {
                        project = DominoResourcesPlugin.getDominoDesignerProject (url);
                    }
                } else {
                    project = BluemixUtil.getDominoDesignerProject (editorPart);
                    if ((project != null) && (editorPart.getEditorInput() instanceof IFileEditorInput)) {   
                        IFileEditorInput file = (IFileEditorInput)editorPart.getEditorInput(); 
                        if (file != null) {
                            xpage = BluemixUtil.getXPageName(file.getFile());
                        }
                    }
                }
            } catch (Exception e){
                // if we are not a NotesEditorPart or maybe we don't have any active editors, 
                // just do nothing
            }
        }
        
        if (project != null) {
            if(!project.isProjectAccessible() || !project.getProject().isOpen() || !project.isPhase1LoadComplete()){
                project = null;
            }
        }
        
        // Enabled or disable the action
        action.setEnabled(project != null);
    }
    
    @Override
    public void dispose() {
    }

    private void initMenu(Menu fMenu) {     
        fMenu.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {                
                Menu m = (Menu) e.widget;
                MenuItem[] items = m.getItems();
                for (int i = 0; i < items.length; i++) {
                    items[i].dispose();
                }
                fillMenu(m);                
            }
        });
    }
    
    void fillMenu(Menu menu) {
        addToMenu(menu, new DeployAction());   
        addToMenu(menu, new ConfigAction());   
        addToMenu(menu, new OpenAction());   
    }

    protected void addToMenu(Menu menu, IAction action) {       
        ActionContributionItem item = new ActionContributionItem(action);
        item.fill(menu, -1);        
    }

    @Override
    public Menu getMenu(Menu parent) {  
        Menu fMenu = new Menu(parent);
        initMenu(fMenu);
        return fMenu;
    }
    
    @Override
    public Menu getMenu(Control parent) {
        Menu fMenu = new Menu(parent);
        initMenu(fMenu);
        return fMenu;
    }

    @Override
    public void init(IWorkbenchWindow arg0) {
    }
}