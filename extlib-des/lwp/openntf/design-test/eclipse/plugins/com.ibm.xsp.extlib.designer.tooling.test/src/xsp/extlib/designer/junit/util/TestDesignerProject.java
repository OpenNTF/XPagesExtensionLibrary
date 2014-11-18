/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.junit.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.ibm.commons.iloader.node.ILoader;
import com.ibm.designer.domino.ide.resources.extensions.DesignerDesignElement;
import com.ibm.designer.domino.ide.resources.extensions.DesignerException;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.xsp.internal.loaders.XFacesDOMLoader;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;

/**
 * @author mblout
 * 
 * As tests develop, we will find that more of these methods need to be fleshed out.
 *
 */
public class TestDesignerProject implements DesignerProject /*, IDominoDesignerProject */ {
    
    /** utility method **/
    public static TestDesignerProject create(AbstractXspTest test) {
        FacesSharableRegistry reg = TestProject.createRegistry(test);
        return new TestDesignerProject(reg);
        
    }
    
    public final FacesSharableRegistry  reg;
    public final ILoader                loader;
    
    public TestDesignerProject(FacesSharableRegistry reg) {
        this.reg = reg;
        loader = new XFacesDOMLoader(this);
    }
    
    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getFacesRegistry()
     */
    public FacesRegistry getFacesRegistry() {
        return reg;
    }
    

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getServerName()
     */
    public String getServerName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getServerCommonName()
     */
    public String getServerCommonName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getReplicaId()
     */
    public String getReplicaId() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getDatabaseName()
     */
    public String getDatabaseName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getDatabaseTitle()
     */
    public String getDatabaseTitle() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#setTitle(java.lang.String)
     */
    public void setTitle(String dbtitle) throws DesignerException {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#isDesignLockingAllowed()
     */
    public boolean isDesignLockingAllowed() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#notesDbExists()
     */
    public String notesDbExists() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#isProjectAccessible()
     */
    public boolean isProjectAccessible() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#isMultiLingual()
     */
    public boolean isMultiLingual() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#isInheritTemplate()
     */
    public boolean isInheritTemplate() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#isDesignHidden()
     */
    public boolean isDesignHidden() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getProjectNotesURL()
     */
    public String getProjectNotesURL() throws DesignerException {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#refresh()
     */
    public void refresh() throws DesignerException {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#initialize()
     */
    public void initialize() throws DesignerException {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getMasterTemplateName()
     */
    public String getMasterTemplateName() throws DesignerException {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#setMasterTemplateName(java.lang.String)
     */
    public void setMasterTemplateName(String masterTemplatename) throws DesignerException {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getInheritTemplateName()
     */
    public String getInheritTemplateName() throws DesignerException {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#setInheritTemplateName(java.lang.String)
     */
    public void setInheritTemplateName(String inheritTemplateName) throws DesignerException {
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getDesignElements(java.lang.String)
     */
    public DesignerDesignElement[] getDesignElements(String designElementTypeID) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getWebContentFolder()
     */
    public IFolder getWebContentFolder() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getProject()
     */
    public IProject getProject() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getDesignElement(com.ibm.designer.domino.ide.resources.extensions.IPath)
     */
    public DesignerDesignElement getDesignElement(IPath path) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.ide.resources.extensions.DesignerProject#getFilesOfType(java.lang.String)
     */
    public IFile[] getFilesOfType(String designElementTypeID) {
        return null;
    }

//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDxlReplicaId()
//     */
//    public String getDxlReplicaId() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setDatabaseTitle(java.lang.String)
//     */
//    public void setDatabaseTitle(String dbtitle) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setMultiLingual(boolean)
//     */
//    public void setMultiLingual(boolean isMultiLingual) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#resetDBIcon()
//     */
//    public void resetDBIcon() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setDBIcon(org.eclipse.swt.graphics.ImageData)
//     */
//    public void setDBIcon(ImageData newIcon) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDBIcon()
//     */
//    public ImageData getDBIcon() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getOldDBIcon()
//     */
//    public ImageData getOldDBIcon() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#resetOldDBIcon()
//     */
//    public void resetOldDBIcon() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setDesignLockingAllowed(boolean)
//     */
//    public void setDesignLockingAllowed(boolean isDesignLockingAllowed) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getUserName()
//     */
//    public String getUserName() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNsfPath()
//     */
//    public String getNsfPath() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setNsfDbInfo(com.ibm.designer.domino.ide.resources.ipc.NsfDbInfo)
//     */
//    public void setNsfDbInfo(NsfDbInfo dbInfo) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesURL()
//     */
//    public String getNotesURL() throws NsfException {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getApplicationPropertyProvider()
//     */
//    public IApplicationPropertyProvider getApplicationPropertyProvider() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesMonitor()
//     */
//    public NotesMonitor getNotesMonitor() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getViewDesc()
//     */
//    public String getViewDesc() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#releaseViewDesc()
//     */
//    public void releaseViewDesc() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDatabaseSelectableObject()
//     */
//    public String getDatabaseSelectableObject() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#releaseDatabaseSelection()
//     */
//    public void releaseDatabaseSelection() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isUserLSJavaAgentAllowed()
//     */
//    public boolean isUserLSJavaAgentAllowed() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#dSubReleased()
//     */
//    public void dSubReleased() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDesignSubprogram()
//     */
//    public String getDesignSubprogram() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDesignElementListLoader(com.ibm.designer.domino.ide.resources.project.IMetaModelCategory)
//     */
//    public DesignElementListLoader getDesignElementListLoader(IMetaModelCategory cat) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesFileSystem(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
//     */
//    public DesigntimeFileSystem getNotesFileSystem(IPath refreshPath, boolean refresh, IProgressMonitor monitor) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesFileSystem(boolean, org.eclipse.core.runtime.IProgressMonitor)
//     */
//    public DesigntimeFileSystem getNotesFileSystem(boolean refresh, IProgressMonitor monitor) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#canEditNotesDb()
//     */
//    public String canEditNotesDb() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#doesSpecialNoteExists(java.lang.String)
//     */
//    public boolean doesSpecialNoteExists(String metaDscrId) {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#cacheNotesDatabase(boolean)
//     */
//    public void cacheNotesDatabase(boolean checkFullAdmin) throws NsfException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#recycleNotesDatabase()
//     */
//    public void recycleNotesDatabase() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#updateNotesDesignList()
//     */
//    public void updateNotesDesignList() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#refreshDesigneElement()
//     */
//    public void refreshDesigneElement() throws DesignerProjectException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesDesignElement(org.eclipse.core.runtime.IPath)
//     */
//    public NotesDesignElement getNotesDesignElement(IPath path) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesDesignElement(org.eclipse.core.runtime.IPath, boolean)
//     */
//    public NotesDesignElement getNotesDesignElement(IPath path, boolean create) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesDesignElement(com.ibm.designer.domino.ide.resources.jni.NIndexKey)
//     */
//    public NotesDesignElement getNotesDesignElement(NIndexKey index) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#findResource(java.lang.String)
//     */
//    public IResource findResource(String notesUrl) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#findResource(java.lang.String, java.lang.String)
//     */
//    public IResource findResource(String notesUrl, String designTypeFromNContext) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDesignElement(com.ibm.designer.domino.ide.resources.jni.NIndexKey)
//     */
//    public IDesignElement getDesignElement(NIndexKey index) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesDesignElement(com.ibm.designer.domino.ide.resources.project.IMetaModelCategory, java.lang.String)
//     */
//    public NotesDesignElement getNotesDesignElement(IMetaModelCategory cat, String title) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getEfsContainer(com.ibm.designer.domino.ide.resources.project.IMetaModelCategory)
//     */
//    public IContainer getEfsContainer(IMetaModelCategory cat) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getEfsContainerPath(com.ibm.designer.domino.ide.resources.project.IMetaModelCategory)
//     */
//    public IPath getEfsContainerPath(IMetaModelCategory cat) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getLocalFolder()
//     */
//    public IFolder getLocalFolder() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getLocalFolderEfsPath()
//     */
//    public IPath getLocalFolderEfsPath() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getJavaOutputFolder()
//     */
//    public IFolder getJavaOutputFolder() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getProjectEfsPath()
//     */
//    public IPath getProjectEfsPath() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDbScriptFile()
//     */
//    public IFile getDbScriptFile() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setInheritTemplate(boolean)
//     */
//    public void setInheritTemplate(boolean bIsInheritTemplate) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getRuntimeApplication()
//     */
//    public Application getRuntimeApplication() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getFacesRegistryMaintainer()
//     */
//    public IFacesRegistryMaintainer getFacesRegistryMaintainer() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getXspDesignProperties()
//     */
//    public Map<String, String> getXspDesignProperties() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#attachXspDesignProvider(org.eclipse.core.runtime.IAdaptable)
//     */
//    public void attachXspDesignProvider(IAdaptable provider) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#detachXspDesignProvider(org.eclipse.core.runtime.IAdaptable)
//     */
//    public void detachXspDesignProvider(IAdaptable provider) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#markAccessible(com.ibm.designer.domino.ide.resources.project.DominoProjectCreationJob, org.eclipse.core.runtime.jobs.IJobChangeListener, boolean, boolean)
//     */
//    public void markAccessible(DominoProjectCreationJob job, IJobChangeListener listener, boolean bDoNow, boolean checkAccess)
//            throws CoreException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#markAccessible(com.ibm.designer.domino.ide.resources.project.DominoProjectCreationJob, org.eclipse.core.runtime.jobs.IJobChangeListener, boolean)
//     */
//    public void markAccessible(DominoProjectCreationJob job, IJobChangeListener listener, boolean bDoNow) throws CoreException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#markAccessible(com.ibm.designer.domino.ide.resources.project.DominoProjectCreationJob, org.eclipse.core.runtime.jobs.IJobChangeListener)
//     */
//    public void markAccessible(DominoProjectCreationJob job, IJobChangeListener listener) throws CoreException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#runRefreshJob()
//     */
//    public void runRefreshJob() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#runRefreshJob(org.eclipse.core.runtime.jobs.IJobChangeListener)
//     */
//    public void runRefreshJob(IJobChangeListener listener) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#runRefreshJob(com.ibm.designer.domino.ide.resources.project.IMetaModelDescriptor, org.eclipse.core.runtime.jobs.IJobChangeListener)
//     */
//    public void runRefreshJob(IMetaModelDescriptor descr, IJobChangeListener listener) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#refreshClasses()
//     */
//    public void refreshClasses() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isIgnoreInitialDelta()
//     */
//    public boolean isIgnoreInitialDelta() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setIgnoreInitialDelta(boolean)
//     */
//    public void setIgnoreInitialDelta(boolean flag) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setDesignHidden(boolean)
//     */
//    public void setDesignHidden(boolean isDesignHidden) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setDesignHiddenMsg(java.lang.String)
//     */
//    public void setDesignHiddenMsg(String msg) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDesignHiddenMsg()
//     */
//    public String getDesignHiddenMsg() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#markNsfForDelete()
//     */
//    public boolean markNsfForDelete() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isNsfAboutToBeDeleted()
//     */
//    public boolean isNsfAboutToBeDeleted() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getNotesEnvironmentVariable(java.lang.String)
//     */
//    public String getNotesEnvironmentVariable(String name) throws NsfException {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getStoreProvider()
//     */
//    public NsfStoreProvider getStoreProvider() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#buildProjectStructure()
//     */
//    public void buildProjectStructure() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isProjectInitialized()
//     */
//    public boolean isProjectInitialized() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setProjectInitialized(boolean)
//     */
//    public void setProjectInitialized(boolean flag) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setNEMSelection(boolean)
//     */
//    public void setNEMSelection(boolean activate) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getLastModifiedTime()
//     */
//    public long getLastModifiedTime() {
//        return 0;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDiskRootLocation()
//     */
//    public IPath getDiskRootLocation() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getXspResource(java.lang.String)
//     */
//    public NotesDesignElement getXspResource(String path) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getJavaFileResource(java.lang.String, java.lang.String)
//     */
//    public NotesDesignElement getJavaFileResource(String title, String path) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getJavaJarResource(java.lang.String)
//     */
//    public NotesDesignElement getJavaJarResource(String title) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDecoratedProjectName()
//     */
//    public String getDecoratedProjectName() {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isDeleteCancelled()
//     */
//    public boolean isDeleteCancelled() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isSettingClasspath()
//     */
//    public boolean isSettingClasspath() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#setSettingClasspath(boolean)
//     */
//    public void setSettingClasspath(boolean isIt) {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#notifyJniLoadCompleted()
//     */
//    public void notifyJniLoadCompleted() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#notifyIpcLoadCompleted()
//     */
//    public void notifyIpcLoadCompleted() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#notifyProjectInitialized()
//     */
//    public void notifyProjectInitialized() {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#readFlags(com.ibm.designer.domino.ide.resources.project.NotesDatabase, com.ibm.designer.domino.ide.resources.project.NotesNote)
//     */
//    public void readFlags(NotesDatabase db, NotesNote note) throws NotesAPIException {
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isPhase1LoadComplete()
//     */
//    public boolean isPhase1LoadComplete() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#isPhase2LoadComplete()
//     */
//    public boolean isPhase2LoadComplete() {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#mapToLocalLocation(org.eclipse.core.runtime.IPath, boolean)
//     */
//    public IPath mapToLocalLocation(IPath efsPath, boolean absolute) {
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject#getDiskStoreRoot()
//     */
//    public IPath getDiskStoreRoot() {
//        return null;
//    }
//
}
