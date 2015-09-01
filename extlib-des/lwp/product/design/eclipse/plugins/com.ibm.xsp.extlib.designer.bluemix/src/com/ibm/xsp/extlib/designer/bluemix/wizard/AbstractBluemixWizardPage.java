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

package com.ibm.xsp.extlib.designer.bluemix.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.util.StringUtil;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractBluemixWizardPage extends WizardPage {

    protected AbstractBluemixWizard _wiz;
    protected boolean               _firstDisplay = true;

    protected AbstractBluemixWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        _wiz = (AbstractBluemixWizard) getWizard();        
        setTitle(getPageTitle());
        setMessage(StringUtil.format(getPageMsg(), "\n"), IMessageProvider.INFORMATION); // $NON-NLS-1$
        setImageDescriptor(_wiz._image);        
        setPageComplete(false);                
    } 
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            validatePage();
            _firstDisplay = false;
        }
        super.setVisible(visible);
    }

    protected void showError(String msg) {
        if (StringUtil.isEmpty(msg)) {
            setErrorMessage(null);
            setPageComplete(true);
        } else {
            setErrorMessage(_firstDisplay ? null : msg);
            setPageComplete(false);
        }
    }
    
    
    protected abstract String getPageTitle();
    protected abstract String getPageMsg();
    protected abstract void validatePage();
}