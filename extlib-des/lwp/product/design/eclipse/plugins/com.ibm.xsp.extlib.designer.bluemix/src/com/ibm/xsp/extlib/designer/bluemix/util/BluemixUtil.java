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

package com.ibm.xsp.extlib.designer.bluemix.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.ibm.commons.iloader.node.validators.UrlValidator;
import com.ibm.commons.iloader.node.validators.support.Messages;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.NsfException;
import com.ibm.designer.domino.ide.resources.metamodel.IMetaModelConstants;
import com.ibm.designer.domino.ide.resources.project.DominoDesignerProject;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.napi.NotesAPIException;
import com.ibm.designer.domino.napi.NotesSession;
import com.ibm.designer.prj.resources.commons.IDesignElement;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestMetaModel.BluemixManifestEditorInput;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestMultiPageEditor;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class BluemixUtil {
    
    // Get the root cause of a throwable
    public static Throwable getRootCause(Throwable throwable) {
        if ((throwable.getCause() != null) && (throwable.getCause() != throwable))
            return getRootCause(throwable.getCause());

        return throwable;
    }
    
    // Get some text from a throwable for display
    public static String getErrorText(Throwable t) {
        String msg = null;
        
        Throwable root = getRootCause(t);
        if (root instanceof CloudFoundryException) {
            msg = ((CloudFoundryException)root).getDescription();
        } else if (root instanceof NotesException) {
            msg = ((NotesException)root).text;
        }
        
        if (StringUtil.isEmpty(msg)) {
            msg = root.getMessage();
        }
        
        if (StringUtil.isEmpty(msg)) {
            msg = root.toString();
        }        
        
        return msg;
    }
    
    // Is the Bluemix Server configured ?
    public static boolean isServerConfigured() {
        // Check for Blanks
        if(StringUtil.isEmpty(PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, ""))) {
            return false;
        }
        if(StringUtil.isEmpty(PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, ""))) {
            return false;
        }
        if(StringUtil.isEmpty(PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, ""))) {
            return false;
        }

        // Validate the URL
        UrlValidator urlValidator = new UrlValidator(true);
        if (!urlValidator.isValid(PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, ""), new Messages())) {
            return false;
        }
        
        return true;
    }
    
    // Prompt the user to open the Server preferences
    public static void displayConfigureServerDialog() {
        String msg = BluemixUtil.productizeString("The %BM_PRODUCT% Server connection is not configured correctly. Open the %BM_PRODUCT% preferences?"); // $NLX-BluemixUtil.TheIBMBluemixServerconnectionisnotco-1$
        if(MessageDialog.openQuestion(null, "Server Configuration", msg)) { // $NLX-BluemixUtil.ServerConfiguration-1$ 
            PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PreferencePage.BLUEMIX_PREF_PAGE, null, null);
            dialog.open();
        }
    }
    
    // Given the full path of a database return the NSF file name
    public static String getNsfName(String dbName) {
        if (dbName.contains(File.separator)) {
            int idx = dbName.lastIndexOf(File.separator);
            return dbName.substring(idx+1);
        } 
        
        return(dbName);
    }
    
    // Open a URL in the default browser
    public static void openUrlInDefaultBrowser(URL url) {
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

        try {
            IWebBrowser browser = support.getExternalBrowser();
            browser.openURL(url);
        }
        catch (PartInitException e) {
            if(BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()){
                BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "openUrlInDefaultBrowser", e, "Failed to initialize browser part"); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtoinitializebrowserpart-2$
            }
        } 
        catch (Exception e) {
            if(BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()){
                BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "openUrlInDefaultBrowser", e, "Failed to launch browser"); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtolaunchbrowser-2$
            }            
        }
    }    
    
    // Given a project return the manifest editor if open
    public static ManifestMultiPageEditor getManifestEditor(IDominoDesignerProject project) {
        for (IEditorReference ref : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
            try {
                if (ref.getEditorInput() instanceof BluemixManifestEditorInput) {
                    if (((BluemixManifestEditorInput)ref.getEditorInput()).getDesignerProject() == project) {
                        return (ManifestMultiPageEditor) ref.getEditor(false);
                    }
                }
            } catch (PartInitException e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "getManifestEditor", e, "Failed to get manifest editor"); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtogetmanifesteditor-2$
                }
            }
        }
        return null;  
    }
    
    // Get the Notes data directory
    public static String getNotesDataDir() throws Throwable {
        final String[] result = new String[1];
        Display.getDefault().syncExec(new Runnable(){
            public void run() {          
                NotesSession session = null;
                try {
                    session = new NotesSession();
                    result[0] = session.getDataDirectory();
                } finally {
                    if(session != null){
                        try {
                            session.recycle();
                        } catch (NotesAPIException e) {
                            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "getNotesDataDir", e, "Failed to recycle session"); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtorecyclesession-2$
                            }
                        }
                    }
                }
            }
        });
        return result[0];
    }
    
    // Given a source and dest copies a file
    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
        }
    }        
    
    // Given a path check if its part of the Notes or Domino installation
    public static boolean isNotesDominoPath(String dir) {
        IPath path = new Path(dir.trim()).addTrailingSeparator();
        String[] notesFiles = new String[]{"notes.ini", "nlnotes.exe"}; // $NON-NLS-1$ $NON-NLS-2$
        String[] dataFiles = new String[]{"IBM_CredStore", "IBM_TECHNICAL_SUPPORT"}; // $NON-NLS-1$ $NON-NLS-2$
        
        // Go backwards through the path looking for specific files
        while(true) {
            if(containsFiles(path.toFile().listFiles(), dataFiles)) {
                return true;
            }
            
            if(containsFiles(path.toFile().listFiles(), notesFiles)) {
                return true;
            }
            
            if (path.segmentCount() == 0) {
                // We've reached the root - bail
                break;
            }
            path = path.removeLastSegments(1);
        }          
        
        // Not a Notes/Domino path
        return false;
    }
    
    private static boolean containsFiles(File[] fileList, String[] files) {
        for (String file:files) {
            boolean found = false;
            
            for (File f:fileList) {
                if (StringUtil.equalsIgnoreCase(f.getName(), file)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    // Get the first NSF found in a directory
    public static File getNsfFromDirectory(String directory) {
        for (File file : new File(directory).listFiles()) {
            if (file.getName().toLowerCase().endsWith((".nsf"))) { // $NON-NLS-1$
                return file;
            }
        }
        
        return null;
    }
    
    // Given a file return the corresponding XPage if any
    public static String getXPageName(IFile file) {
        try {
            IDesignElement desEl = DominoResourcesPlugin.getDesignElement(file);
            if((desEl != null) && desEl.getMetaModelID().equals(IMetaModelConstants.XSPPAGES)) {
                return file.getName();
            }
        } catch (CoreException e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "getXPageName", e, "Failed to get design element"); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtogetdesignelement-2$
            }
        }       
        
        return null;
    }    
    
    // Given an editorPart return the Designer project
    public static IDominoDesignerProject getDominoDesignerProject (IEditorPart editorPart) {
        if ( editorPart != null ) {
            if ( editorPart.getEditorInput() instanceof IFileEditorInput ) {    
                IFileEditorInput file = (IFileEditorInput)editorPart.getEditorInput();            
                IProject prj = file.getFile().getProject();
                try {
                    IDominoDesignerProject desPrj = DominoResourcesPlugin.getDominoDesignerProject(prj);
                    return desPrj;
                } catch (NsfException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "getDominoDesignerProject", e, "Failed to get Domino Designer Project"); // $NON-NLS-1$ $NLE-BluemixUtil.FailedtogetDominoDesignerProject-2$
                    }
                }
            }
            
            if (editorPart instanceof ManifestMultiPageEditor) {
                return ((ManifestMultiPageEditor)editorPart).getDesignerProject();
            }
        }
        return null;
    }        
    
    // Given an NSF name return the Designer project if any
    public static DominoDesignerProject getDesignerProjectFromWorkspace(String nsfName) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (root != null) {
            IProject[] projects = root.getProjects();
            for (int i = 0; i < projects.length; i++) {
                try {
                    DominoDesignerProject ddp = (DominoDesignerProject)DominoResourcesPlugin.getDominoDesignerProject(projects[i]);
                    if (ddp != null) {
                        String path = ddp.getNsfPath();
                        if (StringUtil.equalsIgnoreCase(path, nsfName)) {
                            return ddp;
                        }
                    }
                } catch (NsfException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "getDesignerProjectFromWorkspace", e, "Failed to get Domino Designer Project"); // $NON-NLS-1$ $NLE-BluemixUtil.FailedtogetDominoDesignerProject-2$
                    }
                }
            }
        }   
        return null;
    }
    
    // Write a properties file
    public static void writeProperties(Properties props, IPath path) {
        File file = new File(path.toOSString());
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            props.store(os, null);
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "writeProperties", e, "Failed to write properties {0}", file); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtowriteproperties0-2$
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "writeProperties", e, "Error closing os {0}", file); //  $NLE-BluemixUtil.Errorclosingos0-2$ $NON-NLS-1$
                    }
                }
            }
        }
    }
    
    // Read a properties file
    public static void readProperties(Properties props, IPath path) {
        File file = new File(path.toOSString());
        props.clear();
        if (file.exists()) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                props.load(is);
            } catch (Exception e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "readProperties", e, "Failed to read properties {0}", file); // $NON-NLS-1$ $NLE-BluemixUtil.Failedtoreadproperties0-2$
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                            BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "readProperties", e, "Error closing is {0}", file); // $NON-NLS-1$ $NLE-BluemixUtil.Errorclosingis0-2$
                        }
                    }
                }
            }
        }
    }

    // Replace a product tag in a String
    public static String productizeString(String str) {
        if(StringUtil.isNotEmpty(str)) {
            return str.replaceAll("%BM_PRODUCT%", "IBM Bluemix"); // $NLX-BluemixUtil.IBMBluemix-2$ $NON-NLS-1$
        }
        return str;
    }
    
    // Creates a complete local copy of a db with a different replicaID
    public static String createLocalDatabaseCopy(Database db, String destDbName) throws Throwable {
        Database newDb = null;
        String newDbPath = null;
        try {
            // Make a copy of the database
            newDb = db.createCopy(null, destDbName);
            newDbPath = newDb.getFilePath();
                
            // Copy all the docs
            DocumentCollection col = db.getAllDocuments();
            Document doc = col.getFirstDocument();
            while (doc != null) {
                doc.copyToDatabase(newDb);
                doc = col.getNextDocument();
            }
            
            // Copy the profile docs
            col = db.getProfileDocCollection(null);
            doc = col.getFirstDocument();
            while (doc != null) {
                doc.copyToDatabase(newDb);
                doc = col.getNextDocument();
            }
        } finally {
            if (newDb != null) {
                try {
                    // Ensure db is flushed to disk
                    newDb.recycle();
                    // Add a pause for safety, just in case another thread handles the flush
                    Thread.sleep(1000);
                } catch (NotesException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(null, "createLocalDatabaseCopy", e, "Failed to recycle newDb"); // $NON-NLS-1$ $NLE-BluemixUtil.FailedtorecyclenewDb-2$
                    }
                }
            }
        }
        
        return newDbPath;
    }
    
    // Creates a local replica of a db
    public static String createLocalDatabaseReplica(Database db, String destDbName) throws Throwable {
        Database newDb = null;
        String newDbPath = null;
        try {
            // Make a replica of the database
            newDb = db.createReplica(null, destDbName);
            newDbPath = newDb.getFilePath();
        } finally {
            if (newDb != null) {
                try {
                    // Ensure db is flushed to disk
                    newDb.recycle();
                    // Add a pause for safety, just in case another thread handles the flush
                    Thread.sleep(1000);
                } catch (NotesException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(null, "createLocalDatabaseReplica", e, "Failed to recycle newDb");  // $NON-NLS-1$ $NLE-BluemixUtil.FailedtorecyclenewDb.1-2$
                    }
                }
            }
        }
        
        return newDbPath;
    }    
    
}