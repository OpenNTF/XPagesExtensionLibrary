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

package com.ibm.xsp.extlib.designer.relational.jdbc.plugin.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import com.ibm.commons.util.DateTime;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.xsp.extlib.designer.relational.Activator;
import com.ibm.xsp.extlib.designer.relational.utils.*;

/**
 * @author Gary Marjoram
 *
 */
public class JdbcPluginGenerator {
    private String              _clazz;
    private String              _outputDir;
    private boolean             _updateSite;
    private boolean             _deleteProject;
    private ProjectDef          _projectDef      = new ProjectDef();
    private static final String FEATURE_RES      = "resources/feature.tpl"; // $NON-NLS-1$
    private static final String SITE_RES         = "resources/site.tpl"; // $NON-NLS-1$
    private static final String JAVA_RES         = "resources/JdbcDriverProvider.tpl"; // $NON-NLS-1$
    private static final String JAVA_FILE_NAME   = "JdbcDriverProvider.java"; // $NON-NLS-1$
    private static final String JAVA_CLASS_NAME  = "JdbcDriverProvider.class"; // $NON-NLS-1$
    private static final String PLUGIN_RES       = "resources/plugin.tpl"; // $NON-NLS-1$
    private static final String PLUGIN_FILE_NAME = "plugin.xml"; // $NON-NLS-1$
    private static final String PLUGIN_TAG       = "%PLUGIN-NAME%"; // $NON-NLS-1$
    private static final String CLASS_TAG        = "%CLASS-NAME%"; // $NON-NLS-1$
    private static final String VERSION_TAG      = "%VERSION%"; // $NON-NLS-1$

    //
    // Sets up the project definition for generation
    //
    public JdbcPluginGenerator(final String prjName, final String clazz, final List<String> jars, final String outputDir,
            final boolean updateSite, final boolean deleteProject) {
        // Setup the Plugin Project Definition
        _projectDef.name = prjName;
        _projectDef.version = "1.0.0." + DateTime.formatDateTime(new Date(), "yyyyMMdd-HHmm"); // $NON-NLS-1$
        _projectDef.libs = jars;
        _projectDef.libFolder = "lib"; // $NON-NLS-1$
        _projectDef.srcFolders.add("src"); // $NON-NLS-1$
        _projectDef.bundles.add("com.ibm.commons"); // $NON-NLS-1$
        _projectDef.bundles.add("com.ibm.commons.jdbc"); // $NON-NLS-1$

        // Store other items needed for generation
        _clazz = clazz;
        _outputDir = outputDir;
        _updateSite = updateSite;
        _deleteProject = deleteProject;
    }

    //
    // Main function for generating the plugin and update site
    //
    public void generateUpdateSite(final IProgressMonitor monitor) throws Exception {
        IProject project = null;

        try {
            if (_updateSite) {
                monitor.beginTask("Creating Update Site", 10); // $NLX-JdbcPluginGenerator.CreatingUpdateSite-1$
            }
            else {
                monitor.beginTask("Creating Plug-in", 8); // $NLX-JdbcPluginGenerator.CreatingPlugin-1$
            }

            prepareJars();

            updateProgress(monitor, "Creating temporary project..."); // $NLX-JdbcPluginGenerator.CreatingTemporaryProject-1$
            project = ProjectUtils.createPluginProject(_projectDef);

            updateProgress(monitor, "Creating Manifest..."); // $NLX-JdbcPluginGenerator.CreatingManifest-1$
            ProjectUtils.createManifest(project, _projectDef);

            updateProgress(monitor, "Creating build.properties..."); // $NLX-JdbcPluginGenerator.Creatingbuildproperties-1$
            ProjectUtils.createBuildProperties(project, _projectDef);

            updateProgress(monitor, "Copying JAR files..."); // $NLX-JdbcPluginGenerator.CopyingJarFiles-1$
            ProjectUtils.copyFilesIntoProject(project, "lib", _projectDef.libs); // $NON-NLS-1$

            updateProgress(monitor, "Creating Java file..."); // $NLX-JdbcPluginGenerator.CreatingJavaFile-1$
            createJavaFile(project);

            updateProgress(monitor, "Creating plugin.xml..."); // $NLX-JdbcPluginGenerator.Creatingpluginxml-1$
            createPluginXmlFile(project);

            updateProgress(monitor, "Building project..."); // $NLX-JdbcPluginGenerator.BuildingProject-1$
            ProjectUtils.buildProject(project);

            if (_updateSite) {
                updateProgress(monitor, "Creating site.xml..."); // $NLX-JdbcPluginGenerator.Creatingsitexml-1$
                createSiteXml();

                updateProgress(monitor, "Creating Feature JAR..."); // $NLX-JdbcPluginGenerator.CreatingFeatureJar-1$
                createFeatureJar(project);
            }

            updateProgress(monitor, "Exporting Plug-in JAR..."); // $NLX-JdbcPluginGenerator.ExportingPluginJar-1$
            createPluginJar(project);

        } catch (Exception e) {
            // Throw it up for display
            throw (e);
        } finally {
            try {
                if (project != null) {
                    updateProgress(monitor, "Closing temporary project..."); // $NLX-JdbcPluginGenerator.ClosingTemporaryProject-1$
                    ProjectUtils.closeAndDeleteProject(project, _deleteProject);
                }
                monitor.done();
            } catch (Exception e) {
                if (RelationalLogger.EXT_LIB_RELATIONAL_LOGGER.isWarnEnabled()) {
                    RelationalLogger.EXT_LIB_RELATIONAL_LOGGER.warn(e, "generateUpdateSite : Exception closing / deleting project"); // $NLW-JdbcPluginGenerator.generateUpdateSiteExceptionclosin-1$
                }
            }
        }
    }

    //
    // Extracting the package names from the driver jars
    //
    public void prepareJars() throws Exception {
        for (String jar : _projectDef.libs) {
            Utils.getJarPackages(jar, _projectDef.exports);
        }
        Collections.sort(_projectDef.exports);
    }

    //
    // Update the progress bar
    //
    protected void updateProgress(final IProgressMonitor monitor, final String msg) throws InterruptedException {
        monitor.subTask(msg);
        monitor.worked(1);
        Thread.sleep(100);
    }

    //
    // Create the Java file for the plugin from a template
    //
    protected void createJavaFile(final IProject project) throws Exception {
        String contents = getResourceFile(JAVA_RES);
        ProjectUtils.writeJavaFile(project, "src", _projectDef.name, JAVA_FILE_NAME, contents); // $NON-NLS-1$
    }

    //
    // Creates plugin.xml from a template file
    //
    protected void createPluginXmlFile(final IProject project) throws Exception {
        String contents = getResourceFile(PLUGIN_RES);
        ProjectUtils.writeFile(PLUGIN_FILE_NAME, project, contents);
    }

    //
    // Creates the plugin Jar
    //
    protected void createPluginJar(final IProject project) throws Exception {
        try {
            String directory = _updateSite ? _outputDir + "/plugins/" : _outputDir + "/"; // $NON-NLS-1$
            Utils.createDirectory(directory);

            // Build the List of files
            HashMap<String, String> fileMap = new HashMap<String, String>();
            fileMap.put("META-INF/MANIFEST.MF", ""); // $NON-NLS-1$
            fileMap.put("plugin.xml", ""); // $NON-NLS-1$
            for (String jar : _projectDef.libs) {
                fileMap.put("lib/" + new File(jar).getName(), ""); // $NON-NLS-1$
            }
            String baseName = _projectDef.name.replace(".", "/") + "/" + JAVA_CLASS_NAME;
            fileMap.put(baseName, "bin/" + baseName); // $NON-NLS-1$

            // Create the Jar
            JarOutputStream jar = new JarOutputStream(new FileOutputStream(directory + _projectDef.name + "_" + _projectDef.version
                    + ".jar")); // $NON-NLS-1$
            for (String file : fileMap.keySet()) {
                JarEntry entry = new JarEntry(file);
                jar.putNextEntry(entry);
                String srcFile = fileMap.get(file);
                if (srcFile.length() == 0) {
                    srcFile = file;
                }
                InputStream is = project.getFile((srcFile)).getContents(true);
                try {
                    Utils.writeJarEntry(jar, is);
                } finally {
                    StreamUtil.close(is);
                }
            }
            jar.close();
        } catch (Exception e) {
            throw new Exception("Error creating Plug-in JAR", e); // $NLX-JdbcPluginGenerator.ErrorcreatingPluginJar-1$
        }
    }

    //
    // Create the feature Jar file from a template
    //
    protected void createFeatureJar(final IProject project) throws Exception {
        JarOutputStream jar = null;
        InputStream is = null;
        try {
            String directory = _outputDir + "/features/"; // $NON-NLS-1$
            Utils.createDirectory(directory);

            // Create the Jar
            jar = new JarOutputStream(new FileOutputStream(directory + _projectDef.name + ".feature_" + _projectDef.version + ".jar")); // $NON-NLS-1$ $NON-NLS-2$
            JarEntry entry = new JarEntry("feature.xml"); // $NON-NLS-1$
            jar.putNextEntry(entry);

            String contents = getResourceFile(FEATURE_RES);
            is = new ByteArrayInputStream(contents.getBytes("UTF-8")); // $NON-NLS-1$
            Utils.writeJarEntry(jar, is);
        } catch (Exception e) {
            throw new Exception("Error creating Feature JAR", e); // $NLX-JdbcPluginGenerator.ErrorcreatingFeatureJar-1$
        }  finally {
            StreamUtil.close(is);
            if (jar != null) {
                jar.close();
            }
        }
    }

    //
    // Creates site.xml from a template file
    //
    protected void createSiteXml() throws Exception {
        FileOutputStream fos = null;
        try {
            Utils.createDirectory(_outputDir);
            String contents = getResourceFile(SITE_RES);
            File f = new File(_outputDir, "site.xml"); // $NON-NLS-1$
            fos = new FileOutputStream(f);
            fos.write(contents.getBytes("UTF-8")); // $NON-NLS-1$
        } catch (Exception e) {
            throw new Exception("Error creating \"site.xml\"", e); // $NLX-JdbcPluginGenerator.Errorcreatingsitexml-1$
        } finally {
            StreamUtil.close(fos);
        }
    }

    //
    // Function to read a template file into a String and replace any
    // tags with real values
    //
    protected String getResourceFile(final String file) throws Exception {
        try {
            String contents = Utils.getFileContents(Activator.getDefault().getBundle(), file);
            contents = contents.replace(PLUGIN_TAG, _projectDef.name);
            contents = contents.replace(VERSION_TAG, _projectDef.version);
            contents = contents.replace(CLASS_TAG, _clazz);
            return contents;
        } catch (Exception e) {
            String msg = StringUtil.format("Could not read \"{0}\"", file); // $NLX-JdbcPluginGenerator.Couldnotread0-1$
            throw new Exception(msg, e);
        }
    }
}