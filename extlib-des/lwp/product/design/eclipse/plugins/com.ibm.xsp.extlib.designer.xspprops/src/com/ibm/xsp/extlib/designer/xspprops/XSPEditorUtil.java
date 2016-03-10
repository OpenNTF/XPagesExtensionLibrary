/*
 * © Copyright IBM Corp. 2011, 2016
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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.data.controls.DCCheckbox;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCLabel;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCRadioButton;
import com.ibm.commons.swt.data.controls.DCText;

/**
 * @author mleland
 *
 */
public class XSPEditorUtil {
	
    static public Section createSection(FormToolkit toolkit, Composite parent, String title, int hSpan, int vSpan) {

    	Section section = toolkit.createSection(parent, Section.SHORT_TITLE_BAR);
        GridData osectionGridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        osectionGridData.horizontalSpan = hSpan;
        osectionGridData.verticalSpan = vSpan;
        osectionGridData.horizontalIndent = 5;
        section.setLayoutData(osectionGridData);
        section.setText(title);
        
        GridLayout osectionGL = new GridLayout(1, true);
        osectionGL.marginHeight = 0;
        osectionGL.marginWidth = 0;
        section.setLayout(osectionGL);
        
        return section;
    }

    // caller sets grid data, too variable
    static public DCPanel createDCPanel(Composite parent, int cols, String attrName, String idName) {
    	return createDCPanel(parent, cols, 20, -1, attrName, idName);
    }
    
    // caller sets grid data, too variable
    static public DCPanel createDCPanel(Composite parent, int cols, int hzSpace, int vSpace, String attrName, String idName) {
    	DCPanel dcPanel = new DCPanel(parent, SWT.NONE, idName);
    	dcPanel.setParentPropertyName(attrName);
    	dcPanel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    	GridLayout dcpgl = new GridLayout(cols, false);
    	if (hzSpace != -1)	// meaning don't set it, take default
    		dcpgl.horizontalSpacing = hzSpace;
    	if (vSpace != -1)
    		dcpgl.verticalSpacing = vSpace;
	    dcpgl.marginWidth = 0;
	    dcpgl.marginHeight = 0;
	    dcPanel.setLayout(dcpgl);
    	return dcPanel;
    }
    
    static public Composite createSectionChild(Section parent, int cols) {
	    Composite child = new Composite(parent, SWT.NONE);
	    child.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
	    child.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
	    GridLayout lcgl = new GridLayout(cols, false);
	    lcgl.horizontalSpacing = 20;
	    lcgl.marginWidth = 0;
	    child.setLayout(lcgl);
	    return child;
    }
    
    static public DCCheckbox createCheckboxTF(Composite parent, String labelText, String attrName, int width) {
        return createIndentedCheckbox(parent, labelText, attrName, width, 0, Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    static public DCCheckbox createIndentedCheckboxTF(Composite parent, String labelText, String attrName, int width, int indentAmt) {
        return createIndentedCheckbox(parent, labelText, attrName, width, indentAmt, Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    static public DCCheckbox createCheckbox(Composite parent, String labelText, String attrName, int width, String checked, String unchecked) {
        return createIndentedCheckbox(parent, labelText, attrName, width, 0, checked, unchecked);
    }

    static public DCCheckbox createIndentedCheckbox(Composite parent, String labelText, String attrName, int width, int indentAmt, String checked, String unchecked) {
        DCCheckbox dcc = new DCCheckbox(parent, SWT.CHECK, attrName);   // use the attrname as the id by default
        dcc.setAttributeName(attrName);
        dcc.setText(labelText);
        dcc.setCheckedValue(checked);
        dcc.setUncheckedValue(unchecked);
        dcc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, width, 1);
        if (indentAmt != 0)
            gd.horizontalIndent = indentAmt;
        dcc.setLayoutData(gd);
        return dcc;
    }
    
    static public Label createIndentedLabel(Composite parent, String labelText, int width, int indentAmt) {
        Label newLabel = new Label(parent, SWT.NONE);
        newLabel.setText(labelText);
        GridData ourGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, width, 1);
        if (indentAmt != 0)
            ourGD.horizontalIndent = indentAmt;
        newLabel.setLayoutData(ourGD);
        newLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        return newLabel;
    }
    
    static public Label createLabel(Composite parent, String labelText, int width) {
        return createIndentedLabel(parent, labelText, width, 0);
    }
    
    static public DCLabel createDCLabel(Composite parent, String attrName, int width) {
        return createIndentedDCLabel(parent, attrName, width, 0);
    }
    
    static public DCLabel createIndentedDCLabel(Composite parent, String attrName, int width, int indentAmt) {
        DCLabel newLabel = new DCLabel(parent, SWT.NONE, attrName); // use the attrname as the id by default
        newLabel.setAttributeName(attrName);
        GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, width, 1);
        if (indentAmt != 0)
            gd.horizontalIndent = indentAmt;
        newLabel.setLayoutData(gd);
        newLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        return newLabel;
    }
    
    /**
     * this method will create a new Button on the given composite, with the given style and text.  
     * @param aComposite
     * @param aStyle
     * @param aLabel
     * @return DCCommandButton
     */
    static public Button createButton(Composite aComposite, int aStyle, String aLabel)
    {
        Button button = new Button(aComposite, aStyle);
        button.setBackground(aComposite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        button.setText(aLabel);
        button.setEnabled(true);
        return button;
    }
    
    static public CLabel createCLabel(Composite parent, String label, int hSpan) {
        CLabel pgTitle = new CLabel(parent, SWT.NONE);
        pgTitle.setText(label); 
        pgTitle.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        pgTitle.setFont(JFaceResources.getHeaderFont());
        GridData titleGridData = new GridData();
        titleGridData.grabExcessHorizontalSpace = true;
        titleGridData.horizontalSpan = hSpan;
        pgTitle.setLayoutData(titleGridData);
        return pgTitle;
    }
    
    static public Composite createFormComposite(ScrolledForm scrolledForm) {
        Composite formatComposite = scrolledForm.getBody();

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.horizontalSpacing = 30;
        gridLayout.verticalSpacing = 7;
        gridLayout.marginWidth = 14;
        gridLayout.marginHeight = 14;

        formatComposite.setLayout(gridLayout);
        formatComposite.setBackground(scrolledForm.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        return formatComposite;
    }
    
    static public DCComboBox createDCCombo(Composite parent, String attrName, int width, boolean bReadOnly, boolean bHStretch) {
        DCComboBox newCombo = new DCComboBox(parent, bReadOnly ? SWT.READ_ONLY : SWT.NONE, attrName);
        newCombo.setAttributeName(attrName);
        newCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, bHStretch, false, width, 1));
        return newCombo;
    }
    
    static public Composite createSideComposite(Composite formatComposite) {
        Composite sideComposite = new Composite(formatComposite, SWT.NONE);
        sideComposite.setBackground(formatComposite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        sideComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        sideComposite.setLayout(gridLayout);
        
        return sideComposite;
    }
    
    static public CustomComposite createZeroMarginComposite(Composite parent, int gridCols, int gdSpan, int glSpan, String id) {
    	CustomComposite holder = new CustomComposite(parent, SWT.NONE, id);
    	holder.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    	holder.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, gdSpan, 1));
    	GridLayout gridLayout = new GridLayout(gridCols, false);
    	gridLayout.horizontalSpacing = glSpan;
    	gridLayout.marginWidth = 0;
    	gridLayout.marginHeight = 0;
    	holder.setLayout(gridLayout);
    	return holder;
    }

    static public DCRadioButton createRadio(Composite parent, String attrName, String label, String checkedVal, String idVal, int cols) {
        DCRadioButton ourButton = new DCRadioButton(parent, SWT.RADIO, idVal);
        ourButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        ourButton.setAttributeName(attrName);
        ourButton.setText(label);
        ourButton.setCheckedValue(checkedVal);
        ourButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, cols, 1));
        return ourButton;
    }
    
    static public DCText createText(Composite parent, String attrName, int horzSpan, int horzIndent, int cols) {
        DCText ourText = new DCText(parent, SWT.BORDER, attrName);
        ourText.setAttributeName(attrName);
        if (cols > 0)
            ourText.setCols(cols);
        GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, horzSpan, 1);
        if (horzIndent > 0)
            gd.horizontalIndent = horzIndent;
        ourText.setLayoutData(gd);
        return ourText;
    }
    
    static public DCText createPasswordText(Composite parent, String attrName, int horzSpan, int horzIndent, int cols) {
        DCText ourText = new DCText(parent, SWT.BORDER | SWT.PASSWORD, attrName);
        ourText.setAttributeName(attrName);
        if (cols > 0)
            ourText.setCols(cols);
        GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, horzSpan, 1);
        if (horzIndent > 0)
            gd.horizontalIndent = horzIndent;
        ourText.setLayoutData(gd);
        return ourText;
    }

    static public DCText createTextNoFill(Composite parent, String attrName, int horzSpan, int horzIndent, int cols) {
        DCText ourText = new DCText(parent, SWT.BORDER, attrName);
        ourText.setAttributeName(attrName);
        if (cols > 0)
        	ourText.setCols(cols);
        GridData gd = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, horzSpan, 1);
        if (horzIndent > 0)
        	gd.horizontalIndent = horzIndent;
        ourText.setLayoutData(gd);
    	return ourText;
    }    

    static public CustomComposite createNestedComposite(Composite parent, String id) {
        CustomComposite atComp = new CustomComposite(parent, SWT.NONE, id);
        atComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridLayout nestLayout = new GridLayout(2, false);
        nestLayout.marginHeight = 0;
        nestLayout.marginWidth = 0;
        nestLayout.horizontalSpacing = 10;
        atComp.setLayout(nestLayout);
        atComp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        return atComp;
    }
    
    static public CustomCheckBox createIndentedCheck(Composite parent, String labelText, String id, int indent) {
        CustomCheckBox dcc = new CustomCheckBox(parent, SWT.CHECK, id);
        dcc.setText(labelText);
        dcc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
        gd.horizontalIndent = indent;
        dcc.setLayoutData(gd);
        return dcc;
    }        
}
