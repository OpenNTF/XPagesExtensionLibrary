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

package com.ibm.xsp.extlib.designer.bluemix.job;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.ibm.commons.util.DateTime;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.NotesPlatform;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.designer.domino.xsp.internal.builder.XFacesBuilder;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.BluemixManifest;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixZipUtil;

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class DeployJob extends Job {
    
    private static MutexRule             _jobRule     = new MutexRule();
    private final BluemixConfig          _config;
    private final IDominoDesignerProject _project;
    private Throwable                    _copyException;
    private String                       firstAppName = null;
    private String                       dbName       = null;

    public DeployJob(BluemixConfig config, IDominoDesignerProject project) {
        super(BluemixUtil.productizeString("Deploy to %BM_PRODUCT%")); // $NLX-DeployJob.DeploytoIBMBluemix-1$
        _config = config;
        _project = project;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        CloudFoundryClient client = null;
        dbName = _project.getDatabaseName();
        String msg = BluemixUtil.productizeString(StringUtil.format("Deploying \"{0}\" to %BM_PRODUCT%", dbName)); // $NLX-DeployJob.Deploying0toIBMBluemix-1$
        monitor.beginTask(msg, IProgressMonitor.UNKNOWN); 
        
        IJobManager jobManager = Job.getJobManager();
        try {
            // Wait on all builders to complete
            jobManager.join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
            jobManager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
            jobManager.join(XFacesBuilder.DeferFullBuildJobFamily, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, "Error waiting on builders to complete"); // $NON-NLS-1$ $NLE-DeployJob.Errorwaitingonbuilderstocomplete-2$
            }
        } 
        
        try {
            // Copy the DB to the deployment directory
            _copyException = null;
            NotesPlatform.getInstance().syncExec(new Runnable() {
                public void run() {
                    ISchedulingRule rule = null;
                    Database db = null;
                    Database tmpDb = null;
                    try {                        
                        // While we're copying protect the NSF from a build starting
                        rule = ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
                        Job.getJobManager().beginRule(rule, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
                        
                        monitor.subTask("Creating database copy..."); // $NLX-DeployJob.Creatingdatabasecopy-1$
                        
                        // Open the source db
                        Session sess = NotesPlatform.getInstance().getSession();
                        db = sess.getDatabase(_project.getServerName(), _project.getDatabaseName());
          
                        // Construct the file paths
                        String targetDbName = _config.directory + "\\" + db.getFileName();
                        String tmpFilePath;
                        
                        // Create temporary Db name
                        String tmpDbName = DateTime.formatDateTime(new Date(), "yyyyMMddHHmmssSSS"); // $NON-NLS-1$
                        if (StringUtil.equalsIgnoreCase(_config.copyMethod, "replica")) { // $NON-NLS-1$
                            // Create the temporary local replica
                            tmpFilePath = BluemixUtil.createLocalDatabaseReplica(db, tmpDbName);
                        } 
                        else {
                            // Create the temporary local copy
                            tmpFilePath = BluemixUtil.createLocalDatabaseCopy(db, tmpDbName);
                        }
                        
                        // Copy the actual or temporary to the target location
                        monitor.subTask("Copying to deployment directory..."); // $NLX-DeployJob.Copyingtodeploymentdirectory-1$
                        BluemixUtil.copyFile(new File(tmpFilePath), new File(targetDbName));
                                                
                        // Delete the temporary copy/replica
                        tmpDb = sess.getDatabase(null, tmpDbName);
                        tmpDb.remove();
                    } catch (Throwable e) {
                        // Record the Exception
                        _copyException = e;
                    } finally {
                        if (rule != null) {
                            Job.getJobManager().endRule(rule);
                        }
                        if (tmpDb != null) {
                            try {
                                tmpDb.recycle();
                            } catch (NotesException e) {
                                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                    BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, "Failed to recycle tmpDb"); // $NON-NLS-1$ $NLE-DeployJob.FailedtorecycletmpDb-2$
                                }
                            }
                        }
                        if (db != null) {
                            try {
                                db.recycle();
                            } catch (NotesException e) {
                                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                    BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, "Failed to recycle db"); // $NON-NLS-1$ $NLE-DeployJob.Failedtorecycledb-2$
                                }
                            }
                        }
                    }
                }
            });
            
            // Did the NSF copy complete successfully?
            if (_copyException != null) {
                // No throw exception
                throw new Exception("Error copying database to deployment directory", _copyException); // $NLX-DeployJob.ErrorcopyingDatabasetodeploymentd-1$
            }

            if (monitor.isCanceled()) return Status.CANCEL_STATUS;

            // Deploy to bluemix - load the manifest
            BluemixManifest manifest = new BluemixManifest(ManifestUtil.getManifestFile(_config));
            Set<String> applications = manifest.getAppNames();
            if (applications.size() > 0) {
                String user = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, "");
                String password = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, "");
                CloudCredentials credentials = new CloudCredentials(user, password);

                if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                monitor.subTask("Connecting to Cloud Space..."); // $NLX-DeployJob.ConnectingtoCloudSpace-1$
                try {
                    String target = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, "");
                    client = new CloudFoundryClient(credentials, URI.create(target).toURL(), _config.org, _config.space);
                    client.login();
                } catch (Exception e) {
                    throw new Exception("Error connecting to Cloud Space", e); // $NLX-DeployJob.ErrorconnectingtoCloudSpace-1$
                }
                
                List<CloudApplication> existingApps;                
                if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                monitor.subTask("Retrieving applications..."); // $NLX-DeployJob.RetrievingApplications-1$
                try {
                    existingApps = client.getApplications();
                } catch (Exception e) {
                    throw new Exception("Error retrieving applications from Cloud Space", e); // $NLX-DeployJob.ErrorretrievingApplicationsfromCloud-1$
                }
                
                for (String appName : applications) {
                    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                    
                    // Store the first app name for display later
                    if (firstAppName == null) {
                        firstAppName = appName;
                    }

                    // Is the app already on Bluemix ?
                    boolean newApp = true;
                    for (CloudApplication cloudApp : existingApps) {
                        if (StringUtil.equalsIgnoreCase(appName, cloudApp.getName())) {
                            newApp = false;
                            break;
                        }
                    }
                    
                    // Staging
                    String buildPack = manifest.getBuildPack(appName);
                    String command = manifest.getCommand(appName);
                    Integer timeout = manifest.getTimeout(appName);
                    Staging staging = new Staging(command, buildPack, null, timeout);      
                    
                    // Memory
                    Integer memory = manifest.getMemory(appName);
                    
                    if (newApp) {
                        // New Application
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Creating new application : {0}", appName)); // $NLX-DeployJob.CreatingnewApplication0-1$
                        try {
                            // Create the Application with staging and memory
                            client.createApplication(appName, staging, memory, null, null);                            
                        } catch (Exception e) {
                            throw new Exception("Error creating application", e); // $NLX-DeployJob.ErrorcreatingApplication-1$
                        }
                    } else {
                        // Existing Application - Stop it
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Stopping application...")); // $NLX-DeployJob.StoppingApplication-1$
                        try {
                            client.stopApplication(appName);
                        } catch (Exception e) {
                            throw new Exception("Error stopping application", e); // $NLX-DeployJob.ErrorstoppingApplication-1$
                        }                            
                        
                        // Update the staging
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating staging...")); // $NLX-DeployJob.UpdatingStaging-1$
                        try {
                            client.updateApplicationStaging(appName, staging);       
                        } catch (Exception e) {
                            throw new Exception("Error updating application staging", e); // $NLX-DeployJob.ErrorupdatingApplicationst-1$
                        }                            
                        
                        // Update the memory
                        if (memory != null) {
                            if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                            monitor.subTask(StringUtil.format("Updating memory...")); // $NLX-DeployJob.UpdatingMemory-1$
                            try {
                                client.updateApplicationMemory(appName, memory);
                            } catch (Exception e) {
                                throw new Exception("Error updating application memory", e); // $NLX-DeployJob.ErrorupdatingApplicationme-1$
                            }                            
                        }
                    }
                    
                    // Instances
                    Integer instances = manifest.getInstances(appName);
                    if (instances != null) {
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating instances...")); // $NLX-DeployJob.UpdatingInstances-1$
                        try {
                            client.updateApplicationInstances(appName, instances);
                        } catch (Exception e) {
                            throw new Exception("Error updating application instances", e); // $NLX-DeployJob.ErrorupdatingApplicationin-1$
                        }                            
                    }
                    
                    // Disk Quota
                    Integer disk = manifest.getDiskQuota(appName);
                    if (disk != null) {
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating disk quota...")); // $NLX-DeployJob.UpdatingDiskQuota-1$
                        try {
                            client.updateApplicationDiskQuota(appName, disk);
                        } catch (Exception e) {
                            throw new Exception("Error updating application disk quota", e); // $NLX-DeployJob.ErrorupdatingApplicationdi-1$
                        }                            
                    }

                    // URIs
                    List<CloudDomain> domains = client.getSharedDomains();
                    String defaultDomain = domains.isEmpty() ? "" : domains.get(0).getName();
                    List<String> uris = manifest.getUris(appName, defaultDomain);
                    if (uris != null) {
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating URIs...")); // $NLX-DeployJob.UpdatingURIs-1$
                        try {
                            client.updateApplicationUris(appName, uris);
                        } catch (Exception e) {
                            throw new Exception("Error updating application URI", e); // $NLX-DeployJob.ErrorupdatingApplicationUR-1$
                        }           
                        
                        // Write the URI to the properties file
                        if (uris.size() > 0) {
                            _config.uri = "http://" + uris.get(0) + "/" + BluemixUtil.getNsfName(dbName); // $NON-NLS-1$
                        } else {
                            _config.uri = "";
                        }
                        ConfigManager.getInstance().setConfig(_project, _config, false);
                    }
                    
                    // Env
                    Map<String, Object> env = manifest.getEnv(appName);
                    if (env != null) {
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating environment variables...")); // $NLX-DeployJob.UpdatingEnv-1$
                        try {
                            client.updateApplicationEnv(appName, ManifestUtil.convertToStringMap(env));
                        } catch (Exception e) {
                            throw new Exception("Error updating application environment variables", e); // $NLX-DeployJob.ErrorupdatingApplicationen-1$
                        }                            
                    }
                    
                    // Services
                    List<String> services = manifest.getServices(appName);
                    if (services != null) {
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        monitor.subTask(StringUtil.format("Updating services...")); // $NLX-DeployJob.UpdatingServices-1$
                        try {
                            client.updateApplicationServices(appName, services);
                        } catch (Exception e) {
                            throw new Exception("Error updating application services", e); // $NLX-DeployJob.ErrorupdatingApplicationse-1$
                        }                            
                    }

                    // Upload the files
                    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                    monitor.subTask(StringUtil.format("Uploading files...")); // $NLX-DeployJob.Uploadingfiles-1$
                    try {
                        String path = manifest.getPath(appName);
                        if (path != null) {
                            path = _config.directory + "\\" + path;
                            File file = new File(path);
                            if (file.exists()) {
                                uploadApplication(client, appName, file);
                            }
                        } else {
                            uploadApplication(client, appName, new File(_config.directory));
                        }
                    } catch (Exception e) {
                        throw new Exception("Error uploading application files", e); // $NLX-DeployJob.ErroruploadingApplicationf-1$
                    }                            
                    
                    // Start the Application
                    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                    monitor.subTask(StringUtil.format("Starting application...")); // $NLX-DeployJob.StartingApplication-1$
                    try {
                        client.startApplication(appName);
                    } catch (Exception e) {
                        throw new Exception("Error starting application", e); // $NLX-DeployJob.ErrorstartingApplication-1$
                    }             
                }  
                
                if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                
                // Wait for the apps to be started
                DominoPreferenceManager mgr = DominoPreferenceManager.getInstance();
                if (mgr.getBooleanValue(PreferenceKeys.KEY_BLUEMIX_DEPLOY_WAIT, false)) {
                    monitor.subTask(StringUtil.format("Waiting for application to start...")); // $NLX-DeployJob.WaitingforApplicationtostart-1$
                    boolean complete;
                    Long timeout = mgr.getLongValue(PreferenceKeys.KEY_BLUEMIX_DEPLOY_WAIT_TIMEOUT, false);
                    long startTime = System.currentTimeMillis();
                    do {
                        // Sleep for 5 secs
                        Thread.sleep(5000); 
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                        // Assume success
                        complete = true;
                        for (String appName : applications) {
                            if(!isApplicationRunning(client, appName)) {
                                // At least one app not running
                                complete = false;
                                break;
                            }
                        }             
                        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                    } while ((!complete) && (System.currentTimeMillis() - startTime < (timeout*1000)));
                    
                    // Did all instances start successfully
                    if (!complete) {
                        msg = StringUtil.format("All instances of \"{0}\" did not start within the timeout period: {1} seconds", firstAppName, timeout); // $NLX-DeployJob.Allinstancesof0didnotstartwithint-1$
                        throw new Exception(msg);
                    } else {
                        if (mgr.getBooleanValue(PreferenceKeys.KEY_BLUEMIX_DEPLOY_WAIT_SHOW_SUCCESS, false)) {
                            PlatformUI.getWorkbench().getDisplay().syncExec (new Runnable () {
                                public void run () {
                                    String msg = StringUtil.format("All instances of \"{0}({1})\" started successfully", firstAppName, dbName); // $NLX-DeployJob.Allinstancesof01startedsuccessful-1$
                                    MessageDialog.openInformation(null, "Deployment Success", msg); // $NLX-DeployJob.DeploymentSuccess-1$
                                }
                             });                            
                        }
                    }
                }
            }
        } catch (Throwable e) {            
            msg = BluemixUtil.getErrorText(e);
            StatusAdapter status = new StatusAdapter(new Status(IStatus.ERROR, BluemixPlugin.PLUGIN_ID, 0, msg, BluemixUtil.getRootCause(e)));
            status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, e.getMessage());
            StatusManager.getManager().handle(status, StatusManager.BLOCK);
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, BluemixUtil.productizeString("Error deploying Application to %BM_PRODUCT%")); // $NON-NLS-1$ $NLE-DeployJob.ErrordeployingApplicationtoIBMBluemi-2$
            }                                    
        } finally {
            // Logout
            if (client != null) {
                client.logout();
            }
        }
        
        return Status.OK_STATUS;        
    }   
    
    public void start() {
        setPriority(Job.BUILD);
        setUser(true);       
        setRule(_jobRule);
        schedule();
    }
    
    private static void uploadApplication(CloudFoundryClient client, String appName, File file) throws Exception {
        if (file.isDirectory()) {
            File zipFile = File.createTempFile("bluemix", ".zip"); // $NON-NLS-1$ $NON-NLS-2$
            BluemixZipUtil.zipDirectory(file.getPath(), zipFile.getPath());
            client.uploadApplication(appName, zipFile);
            zipFile.delete();
        } else if (file.isFile()) {
            client.uploadApplication(appName, file);     
        }
    }
    
    public static class MutexRule implements ISchedulingRule {
        public boolean isConflicting(ISchedulingRule rule) {
           return rule == this;
        }
        public boolean contains(ISchedulingRule rule) {
           return rule == this;
        }
    }    
    
    public static boolean isApplicationRunning(CloudFoundryClient client, String appName) {
        InstancesInfo infos = client.getApplicationInstances(appName);
        if (infos != null) {
            for (InstanceInfo info :infos.getInstances()) {
                if (info.getState() != InstanceState.RUNNING) {
                    return false;
                }
            }
            // All instances are running
            return true;
        }         
        
        // No instance info - app not running
        return false;
    }
}