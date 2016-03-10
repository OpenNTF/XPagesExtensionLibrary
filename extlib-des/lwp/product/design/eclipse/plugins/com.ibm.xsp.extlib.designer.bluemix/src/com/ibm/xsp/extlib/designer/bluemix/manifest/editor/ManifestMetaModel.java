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

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.metamodel.IDesignElementExtension;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.BluemixPlugin;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.wizard.ConfigBluemixWizard;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestMetaModel extends IDesignElementExtension {

    public ManifestMetaModel() {
    }
   
    @Override
    public String getLargeIcon() {
        return "cloud_obj.png"; // $NON-NLS-1$
    }

    @Override
    public String getSmallIcon() {
        return "cloud_obj.png"; // $NON-NLS-1$
    }

    @Override
    public String getNewDialogTitle() {
        return null;
    }

    @Override
    public String getNewDialogMessage() {
        return null;
    }

    @Override
    public String getNewDialogImageName() {
        return null;
    }

    @Override
    public String getElementNameWithAccelerator() {
        return "IBM &Bluemix Manifest"; // $NLX-ManifestMetaModel.IBMBluemixManifest-1$
    }

    @Override
    public String getTopContextNewMenuString() {
        return null;
    }

    @Override
    public String getNewMenuStringWithAccelerator() {
        return null;
    }

    @Override
    public String getNewActionButtonLabel() {
        return null;
    }

    @Override
    public String getNewActionButtonTooltip() {
        return null;
    }

    @Override
    public String getNewActionButtonImage() {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor(String imageName) {
        return BluemixPlugin.getImageDescriptor(imageName);
    }

    @Override
    public Image getImage(String imageName) {
        return null;
    }

    @Override
    public boolean openDesign(DesignerProject designerProject) {
        if(designerProject != null) {
            IProject project = designerProject.getProject();
            if(project != null) {
                BluemixConfig config = ConfigManager.getInstance().getConfig((IDominoDesignerProject) designerProject);
                if (!config.isValid(false)) {
                    // App has not been configured or bluemix.properties is corrupt
                    String msg = "To edit the Manifest this application must be configured for deployment. Do you want to open the Configuration Wizard?"; // $NLX-ManifestMetaModel.ToedittheManifestthisApplicationm-1$
                    if(MessageDialog.openQuestion(null, BluemixUtil.productizeString("%BM_PRODUCT% Manifest"), msg)) { // $NLX-ManifestMetaModel.IBMBluemixManifest.1-1$ 
                        ConfigBluemixWizard.launch();
                    }
                } else if (!ManifestUtil.doesManifestExist(config)) {
                    // App config is valid but Manifest is missing
                    String msg = "The Manifest for this application is missing. Do you want to open the Configuration Wizard?"; // $NLX-ManifestMetaModel.TheManifestforthisApplicat-1$
                    if(MessageDialog.openQuestion(null, BluemixUtil.productizeString("%BM_PRODUCT% Manifest"), msg)) { // $NLX-ManifestMetaModel.IBMBluemixManifest.1-1$ 
                        ConfigBluemixWizard.launch();
                    }
                }
                    
                // Get the config again - it might have been changed by the Wizard
                config = ConfigManager.getInstance().getConfig((IDominoDesignerProject) designerProject);
 
                // Allow user to edit a corrupt Manifest
                if (config.isValid(false) && ManifestUtil.doesManifestExist(config)) {
                    File file = ManifestUtil.getManifestFile(config);
                    if (file.exists() && file.isFile()) {
                        IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();                     
                        try {
                            page.openEditor(new BluemixManifestEditorInput(fileStore, (IDominoDesignerProject) designerProject), "com.ibm.xsp.extlib.designer.bluemix.manifest.editor"); // $NON-NLS-1$
                        } catch ( PartInitException e ) {
                            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "openDesign", e, "Failed to open Manifest Editor {0}", fileStore); // $NON-NLS-1$ $NLE-ManifestMetaModel.FailedtoopenManifestEditor0-2$
                            }
                        }
                    } 
                }
            }
        }
        return true;
    }

    @Override
    public String[] getSupportedPerspectives() {
        return new String[] {DD_PERSPECTIVE, XPAGES_PERSPECTIVE};
    }
    
    public class BluemixManifestEditorInput extends FileStoreEditorInput {
        
        public  final static String MANIFEST_PATH = "manifest.yml"; // $NON-NLS-1$

        private final IDominoDesignerProject _designerProject;
        private final IFile                  _fileLink;
     
        public BluemixManifestEditorInput(IFileStore fileStore, IDominoDesignerProject project) {
            super(fileStore);
            _designerProject = project;
            
            // Get the file link for the manifest - This allows us to sync with
            // the Navigator and close the manifest when the project is closing
            _fileLink = project.getProject().getFile(MANIFEST_PATH);
            try {                
                // Is the correct file link in place ?
                if (_fileLink.getLocationURI().equals(getURI())) {
                    // Yes, refresh the resource - this is needed or we run into
                    // file sync problems, not sure why !!!
                    _fileLink.refreshLocal(IResource.DEPTH_ZERO, null);
                } else {
                    // No, create the link - this modifies the .project file
                    // The link has to be created in the project root, links in sub-dirs
                    // cause exceptions when opening an NSF
                    _fileLink.createLink(getURI(), IResource.REPLACE, null);
                }

            } catch (CoreException e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(this, "BluemixManifestEditorInput", e, "Failed to create or refresh the manifest file link"); // $NON-NLS-1$ $NLE-ManifestMetaModel.Failedtocreateorrefreshthemanifes-2$
                }
            }
            
        }

        public IDominoDesignerProject getDesignerProject() {
            return _designerProject;
        }
        
        @SuppressWarnings("rawtypes") // $NON-NLS-1$
        public Object getAdapter(Class adapter) {
            if (adapter.equals(IFile.class) || adapter.equals(IResource.class)) {
                return _fileLink;
            }
            return super.getAdapter(adapter);
        }            
    }
}