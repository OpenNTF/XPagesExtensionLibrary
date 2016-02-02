/*
 * © Copyright IBM Corp. 2016
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

package com.ibm.xsp.extlib.designer.tooling.utils;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.util.StringUtil;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractWizardPage extends WizardPage {

    protected AbstractWizard _wiz;
    protected boolean        _firstDisplay = true;
    protected boolean        _hasChanged = false;
    protected boolean        _alwaysShowError = false;

    protected AbstractWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        _wiz = (AbstractWizard) getWizard();        
        setTitle(getPageTitle());
        setMessage(StringUtil.format(getPageMsg(), "\n"), IMessageProvider.INFORMATION); // $NON-NLS-1$
        setImageDescriptor(_wiz._image);        
        setPageComplete(false);                
    } 
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (_wiz.advancing && _firstDisplay) {
                initialisePageState();
            }
            
            validatePage();
            _firstDisplay = false;
            _hasChanged = false;
        }
        super.setVisible(visible);
    }

    protected void showError(String msg) {
        if (StringUtil.isEmpty(msg)) {
            setErrorMessage(null);
            setPageComplete(true);
        } else {
            setErrorMessage(_firstDisplay && (!_alwaysShowError) ? null : msg);
            setPageComplete(false);
        }
    }
    
    protected void showWarning(String msg) {
        if (StringUtil.isEmpty(msg)) {
            setMessage(StringUtil.format(getPageMsg(), "\n"), IMessageProvider.INFORMATION); // $NON-NLS-1$
        } else {
            setMessage(msg, IMessageProvider.WARNING);        
        }
    }    
    
    protected void initialisePageState() {        
    }

    protected void savePageState() {        
    }

    protected void validatePage() {
        showError(null);
    }
    
    protected abstract String getPageTitle();
    protected abstract String getPageMsg();
    
    public boolean isFirstDisplay() {
        return _firstDisplay;
    }

    public void setFirstDisplay(boolean firstDisplay) {
        _firstDisplay = firstDisplay;
    }

    public boolean hasChanged() {
        return _hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        _hasChanged = hasChanged;
    }

    public boolean alwaysShowError() {
        return _alwaysShowError;
    }

    public void setAlwaysShowError(boolean alwaysShowError) {
        _alwaysShowError = alwaysShowError;
    } 
}