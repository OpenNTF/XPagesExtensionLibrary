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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.ibm.commons.iloader.node.lookups.api.AbstractLookup;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.xspprops.XSPEditorUtil;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractManifestEditorPage extends DCPanel {
    
    private DCPanel                         _leftComposite;
    private DCPanel                         _rightComposite;
    private CLabel                          _mainLabel;
    
    private final Image                     _errorImage;
    private final Font                      _errorFont;
    private final Font                      _titleFont;

    protected final FormToolkit             _toolkit;
    protected final ManifestMultiPageEditor _mpe;

    public AbstractManifestEditorPage(Composite parent, FormToolkit toolkit, ManifestMultiPageEditor mpe) {   
        super(parent, SWT.NONE);
        _mpe = mpe;
        _toolkit = toolkit;
        _errorImage = getDisplay().getSystemImage(SWT.ICON_ERROR);
        _errorFont = JFaceResources.getDefaultFont();
        _titleFont = JFaceResources.getHeaderFont();
        initialize();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        _errorImage.dispose();
    }

    protected void initialize() {
        GridLayout ourLayout = new GridLayout(1, false);
        ourLayout.marginHeight = 0;
        ourLayout.marginWidth = 0;
        setLayout(ourLayout);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        // Create the scrolled form
        ScrolledForm scrolledForm = _toolkit.createScrolledForm(this);
        scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite composite = XSPEditorUtil.createFormComposite(scrolledForm);
        _mainLabel = XSPEditorUtil.createCLabel(composite, getPageTitle(), 2);
        
        // Create each side 
        createLeftSide(composite);
        createRightSide(composite);
    }
    
    private void createLeftSide(Composite parent) {
        _leftComposite = new DCPanel(parent, SWT.NONE);
        _leftComposite.setParentPropertyName("manifestProperties"); // $NON-NLS-1$
        _leftComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        _leftComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        _leftComposite.setLayout(gridLayout);
        
        // Create each area
        createLeftArea(_leftComposite);
    }
    
    private void createRightSide(Composite parent) {
        _rightComposite = new DCPanel(parent, SWT.NONE);
        _rightComposite.setParentPropertyName("manifestProperties"); // $NON-NLS-1$
        _rightComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        _rightComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        _rightComposite.setLayout(gridLayout);        
        
        // Create each area
        createRightArea(_rightComposite);
    }
    
    // Display the Invalid Manifest UI
    public void displayError() {
        _leftComposite.setVisible(false);
        _rightComposite.setVisible(false);
        _mainLabel.setFont(_errorFont);
        String errorTxt = "This Manifest file is invalid. It must be formatted correctly and contain at least one application.{0}Try correcting the error in the Source tab or erase the content to start again.{0}Alternatively, you can re-run the Configuration Wizard to create a new Manifest file.";  // $NLX-AbstractManifestEditorPage.ThisManifestfileisinvalidItmustbe-1$
        _mainLabel.setText(StringUtil.format(errorTxt, "\n")); // $NON-NLS-1$
        _mainLabel.setImage(_errorImage);
        _mainLabel.layout();
        _mainLabel.getParent().layout();
    }

    // Hide the Invalid Manifest UI
    public void hideError() {
        _leftComposite.setVisible(true);
        _rightComposite.setVisible(true);
        _mainLabel.setFont(_titleFont);            
        _mainLabel.setText(getPageTitle());
        _mainLabel.setImage(null);
        _mainLabel.layout();
        _mainLabel.getParent().layout();
    }
    
    // Lookup class for dropdowns
    protected class BasicLookup extends AbstractLookup {
        private final String _list[];

        public BasicLookup(String list[]) {
            _list = list;
        }
        
        @Override
        public int size() {
            return _list.length;
        }

        @Override
        public String getCode(int index) {
            return _list[index];
        }

        @Override
        public String getLabel(int index) {
            return _list[index];
        }  
    }

    protected void refreshUI() {
    }

    protected abstract String getPageTitle();
    protected abstract void createLeftArea(Composite parent); 
    protected abstract void createRightArea(Composite parent); 
}
