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
package com.ibm.xsp.extlib.designer.tooling.palette.view;

import org.eclipse.jface.dialogs.Dialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;

import com.ibm.designer.domino.xsp.api.palette.XPagesPaletteDropActionDelegate;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.api.util.XPagesEditorUtils;

/**
 * @author doconnor
 *
 */
public abstract class AbstractViewDropAction extends XPagesPaletteDropActionDelegate {

	/**
	 * 
	 */
	public AbstractViewDropAction() {
	}

	/* (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.palette.XPagesDropAction#createElement(org.w3c.dom.Document, java.lang.String)
     */
    @Override
    protected Element createElement(Document doc, String prefix) {
        Node target = getTarget();
        PanelExtraData panelData = new PanelExtraData();
        panelData.setDesignerProject(getDesignerProject());
        panelData.setNode(target);
        panelData.setWorkbenchPart(XPagesEditorUtils.getActiveEditor());
        GenericViewDropDialog dialog = new GenericViewDropDialog(getControl().getShell(), panelData, getDialogTitle(), getUri(), getTagName(), doc);
        Element viewNode = null;
        if(dialog.open() == Dialog.OK) {
            viewNode = dialog.getElementToInsert();
            return viewNode;
        }
        
        return null;
    }
    
    protected Node getTarget(){
        Node target = null;
        Range range = getRange();

        if (range != null) {
            target = range.getEndContainer();
        }
        if (target == null) {
            target = getFocusedNode();
        }
        //if target is still null at this point, check to see if this is a drop to source action.
        if(target == null){
            //if it is, run the drop to source setup and get a valid drop target node back. 
            target = getSelectedNodeFromSource();
        }
        return target;
    }
    
    public abstract String getDialogTitle();
}
