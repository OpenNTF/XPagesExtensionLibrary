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

package com.ibm.xsp.extlib.designer.tooling.palette.dojoform;

import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_LABELS_ABOVE_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_LABELS_BELOW_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_LABELS_TO_LEFT_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_LABELS_TO_RIGHT_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_RULES_ABOVE_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_RULES_BELOW_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_RULES_TO_LEFT_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.ADD_RULES_TO_RIGHT_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.HORIZONTAL_SLIDER_TITLE;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.MESSAGE_TEXT;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.VERTICAL_SLIDER_TITLE;
import static com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil.isHorizontalSlider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;

import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.data.dialog.SimpleDialog;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.registry.FacesRegistry;

public class SliderDropDialog extends SimpleDialog {
    
    private Element _sliderElement;
    private FacesRegistry _facesRegisty;
    
    private CustomCheckBox _addLablesAboveOrLeftCheck;
    private CustomCheckBox _addLablesBelowOrRightCheck;
    private CustomCheckBox _addRulesAboveOrLeftCheck;
    private CustomCheckBox _addRulesBelowOrRightCheck;
    
    public SliderDropDialog(Shell parentShell, Element sliderElem, FacesRegistry registry) {
        super(parentShell);
        _sliderElement = sliderElem;
        _facesRegisty = registry;
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void fillClientArea(Composite parent) {
        //create all the check boxes that appear on the dialog
        if(isHorizontalSlider(_sliderElement)){
            _addLablesAboveOrLeftCheck = addCheckBox(parent, ADD_LABELS_ABOVE_TEXT);
            _addLablesBelowOrRightCheck = addCheckBox(parent, ADD_LABELS_BELOW_TEXT);
            _addRulesAboveOrLeftCheck = addCheckBox(parent, ADD_RULES_ABOVE_TEXT);
            _addRulesBelowOrRightCheck = addCheckBox(parent, ADD_RULES_BELOW_TEXT);
        }
        else{
            _addLablesAboveOrLeftCheck = addCheckBox(parent, ADD_LABELS_TO_LEFT_TEXT);
            _addLablesBelowOrRightCheck = addCheckBox(parent, ADD_LABELS_TO_RIGHT_TEXT);
            _addRulesAboveOrLeftCheck = addCheckBox(parent, ADD_RULES_TO_LEFT_TEXT);
            _addRulesBelowOrRightCheck = addCheckBox(parent, ADD_RULES_TO_RIGHT_TEXT);
        }  
    }
    
    /**
     * This method creates a custom checkBox with a given title as a child of a parent Composite
     * @param parent - the parent composite to add the checkBox to
     * @param title - the label to give the checkBox
     * @return a CustomCheckBox that has been added as a child of the parent Composite with an appropriate LayoutData and title
     */
    private CustomCheckBox addCheckBox(Composite parent, String title){
        //create the checkBox
        CustomCheckBox checkbox = new CustomCheckBox(parent, SWT.NONE, "NewXPageDialog.AddDataSource.Checkbox"); // $NON-NLS-1$
        checkbox.setText(title);
        GridData span = SWTLayoutUtils.createGDFillHorizontal();
        //the parent dialog has a 2 column layout, so we allow our checkBoxes to span both columns. 
        span.horizontalSpan = 2;
        checkbox.setLayoutData(span);
        return checkbox;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#performDialogOperation(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected boolean performDialogOperation(IProgressMonitor arg0) {
        if(null != _sliderElement && null != _facesRegisty){
            if(isHorizontalSlider(_sliderElement)){     
                if(_addLablesAboveOrLeftCheck.getSelection()){
                    //add labels above
                    SliderDropRulesUtil.addSliderRuleLabelsChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP_DECORATION);
                }
                if(_addLablesBelowOrRightCheck.getSelection()){
                    //add labels below      
                    SliderDropRulesUtil.addSliderRuleLabelsChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM_DECORATION);
                }
                if(_addRulesAboveOrLeftCheck.getSelection()){
                    //add rules above
                    SliderDropRulesUtil.addSliderRuleChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP_DECORATION);
                }
                if(_addRulesBelowOrRightCheck.getSelection()){
                    //add rules below
                    SliderDropRulesUtil.addSliderRuleChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM_DECORATION);
                }
            }
            else{
                if(_addLablesAboveOrLeftCheck.getSelection()){
                    //add labels to left
                    SliderDropRulesUtil.addSliderRuleLabelsChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT_DECORATION);
                }
                if(_addLablesBelowOrRightCheck.getSelection()){
                    //add labels to right   
                    SliderDropRulesUtil.addSliderRuleLabelsChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT_DECORATION);
                }
                if(_addRulesAboveOrLeftCheck.getSelection()){
                    //add rules to left
                    SliderDropRulesUtil.addSliderRuleChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT_DECORATION);
                }
                if(_addRulesBelowOrRightCheck.getSelection()){
                    //add rules to right
                    SliderDropRulesUtil.addSliderRuleChildToElement(_sliderElement, _facesRegisty, IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT_DECORATION);
                }
            }
        }
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#getDialogTitle()
     */
    protected String getDialogTitle(){
        if(isHorizontalSlider(_sliderElement)){
            return HORIZONTAL_SLIDER_TITLE;
        }
        else{
            return VERTICAL_SLIDER_TITLE;
        }
    }
    

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#needsProgressMonitor()
     */
    @Override
    protected boolean needsProgressMonitor() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.SimpleDialog#getMessage()
     */
    @Override
    protected String getMessage() {
        return MESSAGE_TEXT;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.SimpleDialog#getInitialSize()
     */
    @Override
    protected Point getInitialSize() {
        int width = 0;
        if(isHorizontalSlider(_sliderElement)){
            width = Math.max(_addLablesAboveOrLeftCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addLablesBelowOrRightCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addRulesAboveOrLeftCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addRulesBelowOrRightCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
        }
        else{
            width = Math.max(_addLablesAboveOrLeftCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addLablesBelowOrRightCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addRulesAboveOrLeftCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
            width = Math.max(_addRulesBelowOrRightCheck.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
        }
        Point p = super.getInitialSize();
        p.x = Math.max(width + convertHorizontalDLUsToPixels(40), convertHorizontalDLUsToPixels(160));
        getShell().setMinimumSize(p);
        return p;
    }
}