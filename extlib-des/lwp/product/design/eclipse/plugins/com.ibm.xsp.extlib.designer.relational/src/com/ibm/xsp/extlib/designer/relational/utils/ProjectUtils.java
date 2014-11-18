/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.relational.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ProjectUtils {

    //
    // Utility function to create a plugin project based on a projectDef
    //
    public static IProject createPluginProject(final ProjectDef projectDef) throws Exception {

        IProject project = null;

        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            project = root.getProject(projectDef.name + "_" + projectDef.version);
            if (project.exists()) {
                project.delete(true, true, null);
            }
            project.create(null);
            project.open(null);
            IProjectDescription description = project.getDescription();
            description.setNatureIds(new String[] { "org.eclipse.pde.PluginNature", JavaCore.NATURE_ID }); // $NON-NLS-1$
            project.setDescription(description, null);
            IJavaProject javaProject = JavaCore.create(project);

            // Contruct the classpath
            List<IClasspathEntry> classpathList = new ArrayList<IClasspathEntry>();

            // Source folders
            for (String folderName : projectDef.srcFolders) {
                IFolder folder = project.getFolder(folderName);
                // Create the src folders in the project
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }
                IClasspathEntry srcClasspathEntry = JavaCore.newSourceEntry(folder.getFullPath());
                classpathList.add(srcClasspathEntry);
            }

            // Add Lib Classpaths
            if (!StringUtil.isEmpty(projectDef.libFolder)) {
                IFolder folder = project.getFolder(projectDef.libFolder);
                // Create the lib folder in the project
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
                for (String lib : projectDef.libs) {
                    IClasspathEntry srcClasspathEntry = JavaCore.newLibraryEntry(
                            new Path(folder.getFullPath() + "/" + new File(lib).getName()), null, null, true);
                    classpathList.add(srcClasspathEntry);
                }
            }

            classpathList.add(JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"))); // $NON-NLS-1$
            classpathList.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins"))); // $NON-NLS-1$

            // Set the project classpath
            javaProject.setRawClasspath(classpathList.toArray(new IClasspathEntry[classpathList.size()]), null);
        } catch (Exception e) {
            if (project != null) {
                closeAndDeleteProject(project, true);
            }
            throw new Exception("Error creating temporary Plug-in project in the Workspace", e); // $NLX-ProjectUtils.ErrorcreatingtemporaryPluginproje-1$
        }

        return project;
    }

    //
    // Utility function to write a file to the plugin project
    //
    public static void writeFile(final String name, final IContainer container, final String content) throws Exception {
        InputStream is = null;
        try {
            IFile file = container.getFile(new Path(name));
            is = new ByteArrayInputStream(content.getBytes(file.getCharset()));
            file.create(is, true, null);
        } catch (Exception e) {
            String msg = StringUtil.format("Could not create the file \"{0}\"", name); // $NLX-ProjectUtils.Couldnotcreatethefile0-1$
            throw new Exception(msg, e);
        } finally {
            StreamUtil.close(is);
        }
    }

    //
    // Utility function to write a Java file to the plugin project
    //
    public static void writeJavaFile(final IProject project, final String folder, final String packageName, final String name, final String content)
            throws Exception {
        try {
            IJavaProject javaProject = JavaCore.create(project);
            IFolder sourceFolder = project.getFolder(folder);
            IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment(packageName, true, null);
            pack.createCompilationUnit(name, content, true, null);
        } catch (Exception e) {
            String msg = StringUtil.format("Could not create the Java file \"{0}\"", name); // $NLX-ProjectUtils.CouldnotcreatetheJavafile0-1$
            throw new Exception(msg, e);
        }
    }

    //
    // Utility function to create the build.properties file
    //
    public static void createBuildProperties(final IProject project, final ProjectDef projectDef) throws Exception {
        try {
            StringBuilder content = new StringBuilder("source.. = "); // $NON-NLS-1$
            Iterator<String> iterator = projectDef.srcFolders.iterator();
            while (iterator.hasNext()) {
                content.append(iterator.next()).append('/');
                if (iterator.hasNext()) {
                    content.append(",");
                }
            }
            content.append("\r\n"); // $NON-NLS-1$
            content.append("bin.includes = META-INF/,plugin.xml,.\r\n"); // $NON-NLS-1$
            writeFile("build.properties", project, content.toString()); // $NON-NLS-1$
        } catch (Exception e) {
            throw new Exception("Error creating build.properties", e); // $NLX-ProjectUtils.Errorcreatingbuildproperties-1$
        }
    }

    //
    // Utility function to create the MANIFEST.MF file 
    //
    public static void createManifest(final IProject project, final ProjectDef projectDef) throws Exception {

        try {
            StringBuilder content = new StringBuilder("Manifest-Version: 1.0\r\n"); // $NON-NLS-1$
            content.append("Bundle-ManifestVersion: 2\r\n"); // $NON-NLS-1$
            content.append("Bundle-Name: " + projectDef.name + "\r\n"); // $NON-NLS-1$ $NON-NLS-2$
            content.append("Bundle-SymbolicName: " + projectDef.name + "; singleton:=true\r\n"); // $NON-NLS-1$ $NON-NLS-2$
            content.append("Bundle-Version: " + projectDef.version + "\r\n"); // $NON-NLS-1$ $NON-NLS-2$

            // Required Bundles
            content.append("Require-Bundle: "); // $NON-NLS-1$
            int i = 0;
            for (String entry : projectDef.bundles) {
                if (i++ > 0) {
                    content.append(",\r\n"); // $NON-NLS-1$
                }
                content.append(" " + entry);
            }
            content.append("\r\n"); // $NON-NLS-1$

            // Exported Packages
            content.append("Export-Package: "); // $NON-NLS-1$
            i = 0;
            for (String entry : projectDef.exports) {
                if (i++ > 0) {
                    content.append(",\r\n"); // $NON-NLS-1$
                }
                content.append(" " + entry);
            }
            content.append("\r\n"); // $NON-NLS-1$

            // Bundle ClassPath
            content.append("Bundle-ClassPath: ."); // $NON-NLS-1$
            if (!StringUtil.isEmpty(projectDef.libFolder)) {
                for (String entry : projectDef.libs) {
                    content.append(",\r\n lib/" + new File(entry).getName()); // $NON-NLS-1$
                }
            }
            content.append("\r\n"); // $NON-NLS-1$

            content.append("Bundle-RequiredExecutionEnvironment: J2SE-1.5\r\n"); // $NON-NLS-1$

            IFolder folder = project.getFolder("META-INF"); // $NON-NLS-1$
            folder.create(true, true, null);
            writeFile("MANIFEST.MF", folder, content.toString()); // $NON-NLS-1$
        } catch (Exception e) {
            throw new Exception("Error creating MANIFEST.MF", e); // $NLX-ProjectUtils.ErrorcreatingMANIFESTMF-1$
        }
    }

    //
    // Utility function to copy a list of files in to the plugin project
    //
    public static void copyFilesIntoProject(final IProject project, final String folder, final List<String> fileList) throws Exception {
        try {
            for (String file : fileList) {
                FileInputStream fis = new FileInputStream(file);    
                try {
                    String libFile = new File(file).getName();
                    IFile libFileProj = project.getFile(folder + "/" + libFile);
                    libFileProj.create(fis, true, null);
                } finally {
                    StreamUtil.close(fis);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not copy JARs into project", e); // $NLX-ProjectUtils.Couldnotcopyjarsintoproject-1$
        } 
    }

    //
    // Utility function to build the project
    //
    public static void buildProject(final IProject project) throws Exception {
        try {
            project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
            project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        } catch (Exception e) {
            throw new Exception("Could not build project", e); // $NLX-ProjectUtils.Couldnotbuildproject-1$
        }

        IMarker[] markers = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
        if (markers.length > 0) {
            throw new Exception("Plug-in project did not compile : Ensure that the Class and JAR files are correct."); // $NLX-ProjectUtils.PluginprojectdidnotcompileEnsuret-1$
        }
    }

    //
    // Utility function to close and delete the plugin project
    //
    public static void closeAndDeleteProject(final IProject project, final boolean delete) throws Exception {
        project.close(null);
        if (delete) {
            project.delete(true, true, null);
        }
    }
}