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
package com.ibm.xsp.extlib.designer.tooling.propeditor;

import java.util.ArrayList;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerDesignElement;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;

/**
 * View and Folder Design Element Picker. This property editor will provide a dialog to select either
 * view or folder Design Elements from the Designer Project. The display labels take the form 
 * "View/Folder Name - Last Alias". When one of the Views/Folders is selected, the last alias 
 * (or View/Folder name if no alias is specified) will be set in source.   
 */
public class ViewAndFolderDesignElementPicker extends AbstractDesignElementPicker {

    private static String VIEW_NAME_ALIAS_SEPARATOR = " - ";
    private static String DIALOG_TITLE = "Select View or Folder"; // $NLX-ViewAndFolderDesignElementPicker.SelectVieworFolder-1$
    private static String VIEW_DESIGN_ELEMENT_STRING = "View"; // $NLX-ViewAndFolderDesignElementPicker.View-1$
    private static String FOLDER_DESIGN_ELEMENT_STRING = "Folder"; // $NLX-ViewAndFolderDesignElementPicker.Folder-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.propeditor.AbstractDesignElementPicker#getDesignElementIDs()
     */
    @Override
    protected String[] getDesignElementIDs() {
        return new String[]{DesignerResource.TYPE_VIEW,DesignerResource.TYPE_FOLDER};
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.propeditor.AbstractDesignElementPicker#getDialogTitle(java.lang.String)
     */
    @Override
    protected String getDialogTitleText() {
        return DIALOG_TITLE;
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.propeditor.AbstractDesignElementPicker#useDialog()
     */
    @Override
    protected boolean useDialog() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.propeditor.AbstractDesignElementPicker#getLabelForDesignElementID(java.lang.String)
     */
    @Override
    protected String getLabelForDesignElementID(String designElementId) {
        if(StringUtil.equals(designElementId, DesignerResource.TYPE_VIEW)){
            return VIEW_DESIGN_ELEMENT_STRING;
        }
        else if(StringUtil.equals(designElementId, DesignerResource.TYPE_FOLDER)){
            return FOLDER_DESIGN_ELEMENT_STRING;
        }
        else{
            return super.getLabelForDesignElementID(designElementId);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.propeditor.AbstractDesignElementPicker#createDesignElementLookup(com.ibm.designer.domino.ide.resources.extensions.DesignerDesignElement[])
     */
    @Override
    protected StringLookup createDesignElementLookup(DesignerDesignElement[] designElements) {
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        
        for(int i=0; i<designElements.length; i++){
            DesignerDesignElement view = designElements[i];
            String viewName = view.getName();
            String viewAlias = super.getAliasToDisplay(view);
            
            if(StringUtil.isNotEmpty(viewName)){
                String label = viewName;
                if(StringUtil.isNotEmpty(viewAlias)){
                    label = label + VIEW_NAME_ALIAS_SEPARATOR + viewAlias;
                    labels.add(label);
                    codes.add(viewAlias);
                }
                else{
                    labels.add(label);
                    codes.add(viewName);
                }
            }
        }
        
        //ensure consistency of our codes and labels before creating a StringLookup from them.
        if(codes.size()>0 && labels.size()>0 && codes.size()==labels.size()){
            String[] codesArray = codes.toArray(new String[codes.size()]);
            String[] labelsArray = labels.toArray(new String[labels.size()]);
            if(null != codesArray && null != labelsArray && codesArray.length>0 && labelsArray.length>0 && codesArray.length==labelsArray.length){
                return new StringLookup(codesArray,labelsArray);
            }
        }
        return null;
    }
    
    
    
}