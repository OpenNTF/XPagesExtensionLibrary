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
package com.ibm.xsp.extlib.designer.tooling.panels;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeListener;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * @author mblout
 *
 */
public class ExtLibPanelUtil {

    
    /**
     * By default the DataNode for a panel points at the selected tag (ex. applicationLayout). 
     * If the panel will not be dealing with attributes of that tag, but instead will be dealing 
     * with properties of a child (complex property) of the selected tag 
     * 
     * For example, the "configuration" attribute properties of a applicationLayout.  
     * 
     * 
     * @param comp
     * @param listener dana node listener if the caller needs to update UI on changes
     * @param memberName name of the child complex property
     */
    public static void initDataNode(Composite comp, DataNodeListener listener, String memberName) {
        DataNode dataNode = DCUtils.findDataNode(comp, false);
        if (dataNode != null) {
            if (listener != null){
                dataNode.addDataNodeListener(listener);
            }
            if(StringUtil.isNotEmpty(memberName)){
                IMember config = dataNode.getMember(memberName); 
                if (config != null) {
                    try {
                        Object child = dataNode.getObject(dataNode.getCurrentObject(), (IAttribute) config);
                        if (child != null) {
                            dataNode.setDataProvider(new SingleCollection(child));
                            ILoader loader = dataNode.getLoader();
                            IClassDef def = loader.getClassOf(child);
                            dataNode.setClassDef(def);
                        }
                    } catch (NodeException e) {
                        if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(ExtLibPanelUtil.class, "initDataNode", e, "NodeException generated when trying to access the attribute: {0}", memberName); // $NON-NLS-1$ $NLE-ExtLibPanelUtil.NodeExceptiongeneratedwhentryingt-2$
                        }
                    }
                }
            }
        }
    }
    
    static public IEditorPart getActiveEditor() {
        IWorkbenchPage page = getActiveWorkbenchPage();
        if(null != page){
            return page.getActiveEditor();
        }
        return null;
    }    
    
    public static IWorkbenchPage getActiveWorkbenchPage(){
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if(null != window){
            IWorkbenchPage page = window.getActivePage();
            return page;
        }
        return null;
    }
    
    public static IWorkbenchWindow getActiveWorkbenchWindow(){
        IWorkbench workbench = PlatformUI.getWorkbench();
        if(null != workbench){
            return workbench.getActiveWorkbenchWindow();
        }
        return null;
    }
    
    
    public static IDominoDesignerProject getDesignerProject(IEditorPart editor) {
        return (IDominoDesignerProject) editor.getAdapter(IDominoDesignerProject.class);
    }
    
    

}