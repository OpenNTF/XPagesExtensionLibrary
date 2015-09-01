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

import java.net.URI;
import java.util.List;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractBluemixWizard extends Wizard implements IPageChangingListener {    
    
    public IDominoDesignerProject   project    = null;
    public BluemixConfig            origConfig = new BluemixConfig();
    public BluemixConfig            newConfig  = new BluemixConfig(); ;
    public boolean                  advancing;
    public List<CloudOrganization>  orgs;
    public List<CloudSpace>         spaces;
    public List<CloudApplication>   applications;

    protected final ImageDescriptor _image;
    protected Exception             _jobException;

    private final CloudCredentials  _credentials;
    private CloudFoundryClient      _client;
    private CloudFoundryClient      _clientCloudSpace;

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
    
    protected boolean runJob(WizardPage page, IRunnableWithProgress runnable) {
        // Clear any errors
        page.setErrorMessage(null);
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
            page.setErrorMessage(msg);
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "runJob", BluemixUtil.getRootCause(_jobException), "Error running job"); // $NON-NLS-1$ $NLE-AbstractBluemixWizard.Errorrunningjob-2$
            }            
            return false;
        }

        return true;
    }

    protected IRunnableWithProgress _getOrgsAndSpaces = new IRunnableWithProgress() {
        public void run(IProgressMonitor monitor) {
            try {
                monitor.beginTask(BluemixUtil.productizeString("%BM_PRODUCT%"), IProgressMonitor.UNKNOWN);  // $NON-NLS-1$
    
                if (_client == null) {
                    try {
                        monitor.subTask("Connecting to Server..."); // $NLX-AbstractBluemixWizard.ConnectingtoServer-1$
                        String target = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, "");
                        _client = new CloudFoundryClient(_credentials, URI.create(target).toURL());
                        _client.login();
                    } catch (Exception e) {
                        throw new Exception("Error connecting to Server", e); // $NLX-AbstractBluemixWizard.ErrorconnectingtoServer-1$
                    }
                }
    
                try {
                    monitor.subTask("Retrieving organizations..."); // $NLX-AbstractBluemixWizard.RetrievingOrganizations-1$
                    orgs = _client.getOrganizations();
                } catch (Exception e) {
                    throw new Exception("Error retrieving organizations", e); // $NLX-AbstractBluemixWizard.ErrorretrievingOrganizations-1$
                }
    
                try {
                    monitor.subTask("Retrieving spaces..."); // $NLX-AbstractBluemixWizard.RetrievingSpaces-1$
                    spaces = _client.getSpaces();
                } catch (Exception e) {
                    throw new Exception("Error retrieving spaces", e); // $NLX-AbstractBluemixWizard.ErrorretrievingSpaces-1$
                }
    
                monitor.done();
            } catch (Exception e) {
                _jobException = e;
                _client = null;
            }
        }
    };

    protected IRunnableWithProgress _getApplications  = new IRunnableWithProgress() {
        public void run(IProgressMonitor monitor) {
            try {
                monitor.beginTask(BluemixUtil.productizeString("%BM_PRODUCT%"), IProgressMonitor.UNKNOWN);  // $NON-NLS-1$
    
                try {
                    if (_clientCloudSpace != null) {
                        _clientCloudSpace.logout();
                    }
        
                    monitor.subTask("Connecting to Cloud Space..."); // $NLX-AbstractBluemixWizard.ConnectingtoCloudSpace-1$
                    String target = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, "");
                    _clientCloudSpace = new CloudFoundryClient(_credentials, URI.create(target).toURL(), newConfig.org, newConfig.space);
                    _clientCloudSpace.login();
                } catch (Exception e) {
                    throw new Exception("Error connecting to Cloud Space", e); // $NLX-AbstractBluemixWizard.ErrorconnectingtoCloudSpace-1$
                }
                
                try {
                    monitor.subTask("Retrieving applications..."); // $NLX-AbstractBluemixWizard.RetrievingApplications-1$
                    applications = _clientCloudSpace.getApplications();
                } catch (Exception e) {
                    throw new Exception("Error retrieving applications", e); // $NLX-AbstractBluemixWizard.ErrorretrievingApplications-1$
                }
    
                monitor.done();
            } catch (Exception e) {
                _jobException = e;
                _clientCloudSpace = null;
            }
        }
    };

    public String getDefaultDomain() {
        if (_clientCloudSpace != null) {
            List<CloudDomain> domains = _clientCloudSpace.getSharedDomains();
            if (!domains.isEmpty()) {
                return domains.get(0).getName();
            }
        }
        return "";
    }    
    
    protected abstract String getTitle();
}