/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.eclipse.tools.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomButton;
import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.controls.custom.CustomLabel;
import com.ibm.commons.swt.controls.custom.CustomText;
import com.ibm.commons.swt.data.dialog.LWPDCommonDialog;
import com.ibm.commons.swt.dialog.LWPDMessageDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.xsp.eclipse.tools.doc.Registry;
import com.ibm.xsp.eclipse.tools.html.HTMLGenerator;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * 
 */
public class DocGeneratorDialog extends LWPDCommonDialog {
    private DesignerProject _application = null;
    private FacesRegistry _registry = null;
    private String _location = "C:\\Temp\\XPages-Doc";

    private CustomCheckBox standardComponents;
    private CustomCheckBox extlibComponents;
    private CustomCheckBox customComponents;
    private CustomCheckBox othersComponents;
    private CustomText locationText;
    
    public DocGeneratorDialog(Shell shell) {
        super(shell);
        
    }

    @Override
    protected void fillClientArea(Composite parent) {
        //Set the dialog title text
        setMessage("Select the components for which you would like to generate documentation");
        //create a parent
        CustomComposite myParent = new CustomComposite(parent, SWT.NONE, "parent.id");
        myParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(3));
        myParent.setLayoutData(SWTLayoutUtils.createGDFill());
        
        CustomLabel nameLabel = new CustomLabel(myParent, SWT.NONE, "name.label.id");
        nameLabel.setText("Application name:");
        
        CustomText nameText = new CustomText(myParent, SWT.BORDER | SWT.READ_ONLY, "name.text.id");
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        if(selection instanceof StructuredSelection) {
            Object o = ((StructuredSelection)selection).getFirstElement();
            if(o != null) {
                if(o instanceof IProject) {
                        _application = DesignerResource.getDesignerProject((IProject)o);
                }
                if(o instanceof DesignerProject) {
                    _application = ((DesignerProject)o);
                    ((DesignerProject)o).getDatabaseName();
                }
            }
        }
        //
        if(_application != null) {
            _registry = _application.getFacesRegistry();
        }
        nameText.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        if(_application != null) {
            nameText.setText(StringUtil.getNonNullString(_application.getDatabaseName()));
        }
        //dummy label for grid alignment...
        new CustomLabel(myParent, SWT.NONE, "dummy");
        
        CustomLabel locationLabel = new CustomLabel(myParent, SWT.NONE, "location.label.id");
        locationLabel.setText("Output location:");
        
        locationText = new CustomText(myParent, SWT.BORDER, "location.text.id");
        locationText.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        
        // TODO: read a preference
        locationText.setText(_location);
        
        //a browse button that allows the user to select a location to output the doc..
        final CustomButton browse = new CustomButton(myParent, SWT.PUSH, "browse.buton.id");
        browse.setText("Browse...");
        browse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                //create a browse dialog
                DirectoryDialog dlg = new DirectoryDialog(browse.getShell());
                dlg.setFilterPath(StringUtil.getNonNullString(locationText.getText()));
                String loc = dlg.open();
                if(StringUtil.isNotEmpty(loc)) {
                	_location = loc;
                    locationText.setText(_location);
                }
            }
        });
        
        //allow the user to set extra options depending on the info
        //retrieved from the faces registry.
        Group generationOptions = new Group(myParent, SWT.NONE);
        generationOptions.setText("Generation Options");
        GridData optionsData = SWTLayoutUtils.createGDFill();
        optionsData.horizontalSpan = 3;
        generationOptions.setLayoutData(optionsData);
        generationOptions.setLayout(SWTLayoutUtils.createLayoutDefaultSpacing(1));
        
        
        standardComponents = new CustomCheckBox(generationOptions, SWT.CHECK, "standard.components.id");
        standardComponents.setText("Standard Components");
        standardComponents.setSelection(true);

        extlibComponents = new CustomCheckBox(generationOptions, SWT.CHECK, "extlib.components.id");
        extlibComponents.setText("Extension Library Components");
        extlibComponents.setSelection(true);
        
        customComponents = new CustomCheckBox(generationOptions, SWT.CHECK, "custom.components.id");
        customComponents.setText("Database Custom Controls");
        customComponents.setSelection(true);
        
        othersComponents = new CustomCheckBox(generationOptions, SWT.CHECK, "other.components.id");
        othersComponents.setText("Other Custom Controls");
        othersComponents.setSelection(true);

        if(_registry != null) {
            //add more options!
        }
        
    }

    @Override
    protected boolean performDialogOperation(IProgressMonitor monitor) {
    	if(_registry==null) {
            LWPDMessageDialog.openError(getShell(), 
            		"Documentation Generation", 
            		StringUtil.format("Unable to access the database control registry"));
			return false;
    	}
		String location =locationText.getText();
		if(StringUtil.isEmpty(location)) {
            LWPDMessageDialog.openError(getShell(), 
            		"Documentation Generation", 
            		StringUtil.format("Documentation location should not be empty"));
			return false;
		}
		File locationDir = new File(location);
		if(!locationDir.isDirectory()) {
            LWPDMessageDialog.openError(getShell(), 
            		"Documentation Generation", 
            		StringUtil.format("Documentation location is not a valid directory"));
			return false;
		}
		if(locationDir.exists()) {
			purgeDirectory(locationDir);
		} else {
			locationDir.mkdirs();
		}
		
        try {
        	// TODO: fill the options
        	HTMLGenerator.Options options = new HTMLGenerator.Options();
        	options.includeStandard = standardComponents.getSelection();
        	options.includeExtLib = extlibComponents.getSelection();
        	options.includeCustom = customComponents.getSelection();
        	options.includeOthers = othersComponents.getSelection();
            Registry reg = new Registry(_registry);
            HTMLGenerator gen = new HTMLGenerator(reg,locationDir,options);
            gen.generate();
            LWPDMessageDialog.openInformation(getShell(), 
            		"Documentation Generation", 
            		StringUtil.format("Control documentation had been successfully generated in directory {0}",location));
            return true;
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return false;
    }
    public void purgeDirectory(File dir) {
    	File[] children = dir.listFiles();
    	if(children!=null) {
    		for(int i=0; i<children.length; i++) {
    			File f = children[i];
    			if(f.isDirectory()) {
    				purgeDirectory(f);
    			} else {
    				f.delete();
    			}
    		}
    	}
    }    
    @Override
    protected String getDialogTitle() {
        return "Generate XPages Documentation";
    }
}
