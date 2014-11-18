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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author mblout
 *
 */
public class TestFile implements IFile {
    
    final File javaioFile;
    
    public TestFile(File f) {
        javaioFile = f;
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContents()
     */
    public InputStream getContents() throws CoreException {
        try {
            return new FileInputStream(javaioFile);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(InputStream in, int flags, IProgressMonitor monitor) throws CoreException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(javaioFile);
            int read = 0;
            byte[] bytes = new byte[1024];
         
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != out) {
                try {
                    out.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
        return;
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#exists()
     */
    public boolean exists() {
        return javaioFile.exists();
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     */
    public void accept(IResourceVisitor arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     */
    public void accept(IResourceProxyVisitor arg0, int arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     */
    public void accept(IResourceVisitor arg0, int arg1, boolean arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     */
    public void accept(IResourceVisitor arg0, int arg1, int arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void clearHistory(IProgressMonitor arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IPath arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IPath arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IProjectDescription arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IProjectDescription arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     */
    public IMarker createMarker(String arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createProxy()
     */
    public IResourceProxy createProxy() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean arg0, IProgressMonitor arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(int arg0, IProgressMonitor arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     */
    public void deleteMarkers(String arg0, boolean arg1, int arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     */
    public IMarker findMarker(long arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     */
    public IMarker[] findMarkers(String arg0, boolean arg1, int arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
     */
    public int findMaxProblemSeverity(String arg0, boolean arg1, int arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     */
    public String getFileExtension() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocalTimeStamp()
     */
    public long getLocalTimeStamp() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocation()
     */
    public IPath getLocation() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocationURI()
     */
    public URI getLocationURI() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     */
    public IMarker getMarker(long arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     */
    public long getModificationStamp() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getParent()
     */
    public IContainer getParent() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperties()
     */
    @SuppressWarnings("rawtypes")
    public Map getPersistentProperties() throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public String getPersistentProperty(QualifiedName arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProject()
     */
    public IProject getProject() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     */
    public IPath getProjectRelativePath() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     */
    public IPath getRawLocation() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocationURI()
     */
    public URI getRawLocationURI() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getResourceAttributes()
     */
    public ResourceAttributes getResourceAttributes() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperties()
     */
    @SuppressWarnings("rawtypes")
    public Map getSessionProperties() throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public Object getSessionProperty(QualifiedName arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     */
    public IWorkspace getWorkspace() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    public boolean isAccessible() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived()
     */
    public boolean isDerived() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived(int)
     */
    public boolean isDerived(int arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isHidden()
     */
    public boolean isHidden() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked()
     */
    public boolean isLinked() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked(int)
     */
    public boolean isLinked(int arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     */
    public boolean isLocal(int arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isPhantom()
     */
    public boolean isPhantom() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     */
    public boolean isSynchronized(int arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     */
    public boolean isTeamPrivateMember() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void refreshLocal(int arg0, IProgressMonitor arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#revertModificationStamp(long)
     */
    public void revertModificationStamp(long arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     */
    public void setDerived(boolean arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setHidden(boolean)
     */
    public void setHidden(boolean arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setLocal(boolean arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocalTimeStamp(long)
     */
    public long setLocalTimeStamp(long arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     */
    public void setPersistentProperty(QualifiedName arg0, String arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
     */
    public void setReadOnly(boolean arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setResourceAttributes(org.eclipse.core.resources.ResourceAttributes)
     */
    public void setResourceAttributes(ResourceAttributes arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    public void setSessionProperty(QualifiedName arg0, Object arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     */
    public void setTeamPrivateMember(boolean arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void touch(IProgressMonitor arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents(InputStream arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents(InputStream arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(InputStream arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(InputStream arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink(IPath arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(java.net.URI, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink(URI arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharset()
     */
    public String getCharset() throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharset(boolean)
     */
    public String getCharset(boolean arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharsetFor(java.io.Reader)
     */
    public String getCharsetFor(Reader arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContentDescription()
     */
    public IContentDescription getContentDescription() throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContents(boolean)
     */
    public InputStream getContents(boolean arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getEncoding()
     */
    public int getEncoding() throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getFullPath()
     */
    public IPath getFullPath() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IFileState[] getHistory(IProgressMonitor arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getName()
     */
    public String getName() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#isReadOnly()
     */
    public boolean isReadOnly() {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String)
     */
    public void setCharset(String arg0) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setCharset(String arg0, IProgressMonitor arg1) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(IFileState arg0, int arg1, IProgressMonitor arg2) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(InputStream arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(IFileState arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(@SuppressWarnings("rawtypes") Class arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
     */
    public boolean contains(ISchedulingRule arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
     */
    public boolean isConflicting(ISchedulingRule arg0) {
        throw new RuntimeException("not implemented"); //$NON-NLS-1$
//        return false;
    }

}
