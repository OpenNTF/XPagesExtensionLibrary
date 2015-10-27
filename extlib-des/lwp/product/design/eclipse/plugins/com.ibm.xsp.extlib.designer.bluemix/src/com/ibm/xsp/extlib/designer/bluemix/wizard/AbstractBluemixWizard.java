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

import org.cloudfoundry.client.lib.CloudCredentials;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractBluemixWizard extends Wizard implements IPageChangingListener {    
    
    public IDominoDesignerProject    project    = null;
    public boolean                   advancing  = true;

    protected final ImageDescriptor  _image;
    protected final CloudCredentials _credentials;

    private Exception                _jobException;

    public AbstractBluemixWizard() {
        super();
      
        _image = BluemixPlugin.getImageDescriptor("wizban_bluemix.png"); // $NON-NLS-1$
        
        String user = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, "");
        String password = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, "");
        _credentials = new CloudCredentials(user, password);
    }
    
    @Override
    public void addPages() {
        super.addPages();
        setWindowTitle(getTitle());        
    }

    @Override
    public boolean needsProgressMonitor() {
        return true;
    }
    
    protected boolean runJob(IRunnableWithProgress runnable) {
        // Clear any errors
        ((WizardPage)getContainer().getCurrentPage()).setErrorMessage(null);
        _jobException = null;

        // Start the job
        try {
            getContainer().run(true, true, runnable);
        } catch (Exception e) {
            _jobException = e;
        } 

        // Check for errors
        if (_jobException != null) {
            String msg = StringUtil.format("{0} : {1}", _jobException.getMessage(), BluemixUtil.getErrorText(_jobException));
            ((WizardPage)getContainer().getCurrentPage()).setErrorMessage(msg);
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "runJob", BluemixUtil.getRootCause(_jobException), "Error running job"); // $NON-NLS-1$ $NLE-AbstractBluemixWizard.Errorrunningjob-2$
            }            
            return false;
        }

        return true;
    }
    
    public void setJobException(Exception e) {
        _jobException = e;
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
    }    
    
    protected abstract String getTitle();
}