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
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.extensions.NotesPlatform;
import com.ibm.designer.domino.ide.resources.ipc.NsfDbInfo;
import com.ibm.designer.domino.ide.resources.project.DominoDesignerProject;
import com.ibm.designer.domino.navigator.actions.DeleteResourceAction;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.job.DeployJob.MutexRule;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixZipUtil;

/**
 * @author Gary Marjoram
 *
 */
@SuppressWarnings("restriction") // $NON-NLS-1$
public class ImportJob extends Job {
    
    private static MutexRule    _jobRule = new MutexRule();
    private Throwable           _threadException;
    private final BluemixConfig _config;
    private final String        _zipFileName;
    private final String        _importCopyMethod;

    public ImportJob(BluemixConfig config, String zipFileName, String importCopyMethod) {
        super(BluemixUtil.productizeString("Import %BM_PRODUCT% Starter Code"));   // $NLX-ImportJob.ImportIBMBluemixStarterCode-1$
        _config = config;
        _zipFileName = zipFileName;
        _importCopyMethod = importCopyMethod;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("Importing starter code", IProgressMonitor.UNKNOWN);   // $NLX-ImportJob.ImportingStarterCode-1$
        
        try {
            // Get the NSF name from the zip file
            final String nsfName = BluemixZipUtil.getNsfFromZipFile(new File(_zipFileName));
            if (StringUtil.isEmpty(nsfName)) {
                throw new Exception("There is no NSF in the starter code zip file");  // $NLX-ImportJob.ThereisnoNSFintheStarterCodeZIPFi-1$
            }
            
            // Construct the target NSF name - c:\notes\data\xxx.nsf
            final String targetNsfName = BluemixUtil.getNotesDataDir() + File.separator + nsfName;

            // Check if the target NSF is open in Designer
            DominoDesignerProject ddp = BluemixUtil.getDesignerProjectFromWorkspace(nsfName);
            if (ddp != null) {
                // Delete the project and NSF 
                final DeleteResourceAction delAction = new DeleteResourceAction(true, new File(targetNsfName).exists());
                delAction.selectionChanged(new StructuredSelection(ddp));
                Display.getDefault().syncExec(new Runnable(){
                    public void run() {          
                        delAction.run();
                    }
                });
                
                if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                
                // Wait on the delete jobs to complete before continuing
                IJobManager jobManager = Job.getJobManager();
                jobManager.join(IDEWorkbenchMessages.DeleteResourceAction_checkJobName, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
                jobManager.join(IDEWorkbenchMessages.DeleteResourceAction_jobName, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));  
                
                if (monitor.isCanceled()) return Status.CANCEL_STATUS;
                
                // Is everything clean (Project and NSF should be gone) ??? 
                // User may have cancelled or there was an error
                if ((BluemixUtil.getDesignerProjectFromWorkspace(nsfName) != null) || (new File(targetNsfName).exists())) {
                    // Just finish - can't do anything else
                    return Status.CANCEL_STATUS;
                }
            } 

            // If the target NSF still exists at this point then it wasn't 
            // open in Designer - Delete it now
            if (new File(targetNsfName).exists()) {
                
                // Ask the user for confirmation
                final boolean[] continueJob = new boolean[1];
                PlatformUI.getWorkbench().getDisplay().syncExec (new Runnable () {
                    public void run () {
                        String msg = StringUtil.format("\"{0}\" will be overwritten, do you want to continue with this import?", targetNsfName); // $NLX-ImportJob.0willbeoverwrittendoyouwanttocont-1$
                        continueJob[0] = MessageDialog.openQuestion(null, "Importing Starter Code", msg);  // $NLX-ImportJob.ImportingStarterCode.1-1$
                    }
                });
                if (!continueJob[0]) {
                    // User has cancelled
                    return Status.CANCEL_STATUS;
                }
                
                _threadException = null;
                NotesPlatform.getInstance().syncExec(new Runnable() {
                    public void run() {
                        Database db = null;
                        try {                        
                            // Delete the existing NSF
                            monitor.subTask("Deleting database..."); // $NLX-ImportJob.Deletingdatabase-1$
                            Session sess = NotesPlatform.getInstance().getSession();
                            db = sess.getDatabase(null, nsfName);
                            db.remove();
                        } catch (Throwable e) {
                            // Record the Exception
                            _threadException = e;
                        } finally {
                            if (db != null) {
                                try {
                                    db.recycle();
                                } catch (NotesException e) {
                                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                        BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, "Failed to recycle db"); // $NON-NLS-1$ $NLE-ImportJob.Failedtorecycledb-2$
                                    }
                                }
                            }
                        }
                    }
                });   
                if (_threadException != null) {
                    throw new Exception("Could not delete database", _threadException); // $NLX-ImportJob.Couldnotdeletedatabase-1$
                }
            }            
            
            if (monitor.isCanceled()) return Status.CANCEL_STATUS;
            
            monitor.subTask("Unzipping the starter code zip file...");  // $NLX-ImportJob.UnzippingtheStarterCodeZIPfile-1$
            BluemixZipUtil.unzipFile(_zipFileName, _config.directory);
              
            if (monitor.isCanceled()) return Status.CANCEL_STATUS;
            
            // Copy the DB to the data directory
            monitor.subTask("Copying NSF to data directory"); // $NLX-ImportJob.CopyingNSFtodatadirectory-1$
            
            // Make a copy or use the actual NSF ?
            if (StringUtil.equalsIgnoreCase(_importCopyMethod, "copy") || StringUtil.equalsIgnoreCase(_importCopyMethod, "replica")) { // $NON-NLS-1$ $NON-NLS-2$
                // We need a thread for this
                _threadException = null;
                NotesPlatform.getInstance().syncExec(new Runnable() {
                    public void run() {
                        Database db = null;
                        try {                        
                            Session sess = NotesPlatform.getInstance().getSession();
                            db = sess.getDatabase(null, BluemixUtil.getNsfFromDirectory(_config.directory).getPath());
                            if(StringUtil.equalsIgnoreCase(_importCopyMethod, "copy")) { // $NON-NLS-1$
                                BluemixUtil.createLocalDatabaseCopy(db, nsfName);
                            } else {
                                BluemixUtil.createLocalDatabaseReplica(db, nsfName);                                
                            }
                        } catch (Throwable e) {
                            // Record the Exception
                            _threadException = e;
                        } finally {
                            if (db != null) {
                                try {
                                    db.remove();
                                    db.recycle();
                                } catch (NotesException e) {
                                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                        BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", e, "Failed to remove/recycle db");  // $NON-NLS-1$ $NLE-ImportJob.Failedtorecycledb.1-2$
                                    }
                                }
                            }
                        }
                    }
                });   
                if (_threadException != null) {
                    throw new Exception("Could not copy database", _threadException);  // $NLX-ImportJob.Couldnotcopydatabase-1$
                }
            } else {
                // Use the actual database from the ZIP file
                // It will have the same replicaID
                BluemixUtil.copyFile(BluemixUtil.getNsfFromDirectory(_config.directory), new File(targetNsfName));
            }
            
            if (monitor.isCanceled()) return Status.CANCEL_STATUS;
            
            // Open the newly copied NSF in Designer
            _threadException = null;
            Display.getDefault().syncExec(new Runnable(){
                public void run() {          
                    try {
                        if (DominoResourcesPlugin.canEditDbWithError("Local", nsfName)) { // $NON-NLS-1$
                            final NsfDbInfo info = DominoResourcesPlugin.addToNotesWorkspace("Local", nsfName); // $NON-NLS-1$
                            if (info != null) {     
                                DominoResourcesPlugin.createDominoDesignerProject(info, new JobChangeAdapter(){
                                    public void done(IJobChangeEvent event) {
                                        super.done(event);
                                        // Create the link to the deployment directory
                                        DominoDesignerProject ddp = BluemixUtil.getDesignerProjectFromWorkspace(info.getFullPath());
                                        if (ddp != null) {
                                            ConfigManager.getInstance().setConfig(ddp, _config, false, null);
                                        }
                                    }
                                });
                            } else {
                                throw new Exception("addToNotesWorkspace returned null"); // $NLX-ImportJob.addToNotesWorkspacereturnednull-1$
                            }
                        } else {
                            throw new Exception("canEditDbWithError returned false"); // $NLX-ImportJob.canEditDbWithErrorreturnedfalse-1$
                        }
                    } catch (Throwable e) {
                        // Record the Exception
                        _threadException = e;
                    }
                }
            });
                                                    
            // Did the NSF open successfully?
            if (_threadException != null) {
                // No throw exception
                throw new Exception("Error opening starter code project", _threadException);  // $NLX-ImportJob.ErroropeningStarterCodeproject-1$
            }
            
        } catch (Throwable e) {            
            StatusAdapter status = new StatusAdapter(new Status(IStatus.ERROR, BluemixPlugin.PLUGIN_ID, 0, BluemixUtil.getErrorText(e), null));
            status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, e.getMessage());
            StatusManager.getManager().handle(status, StatusManager.BLOCK);
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "run", BluemixUtil.getRootCause(e), "Error importing starter code"); // $NON-NLS-1$ $NLE-ImportJob.ErrorimportingStarterCode-2$
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
}