/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;

/**
 * Library resource installer
 * This class is used to copy some resources from the bundle to the actual Notes/Domino
 * installation.
 */
public class ResourceInstaller {

    private File installDirectory;
    private String library;
    private String version;
    
    private BufferedWriter writer;

    public ResourceInstaller(File installDirectory, String library, String version) {
        this.installDirectory = installDirectory;
        this.library = library;
        this.version = version;
    }

    public boolean shouldUpdate() {
        String fileName = library + "-" + version;

        // If the list file exists, no update is requires
        File fileList = new File(installDirectory, fileName);
        if (fileList.exists()) {
            return false;
        }
        
        // Else it should be updated
        return true;
    }

    public void startInstall() throws IOException {
        // Remove the existing install of the library
        removeInstall();
        
        // And prepare the installer
        String fileName = library + "-" + version;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(installDirectory,fileName)),"UTF-8")); // $NON-NLS-1$
        
        String log = StringUtil.format("Installing custom resources for library {0}",fileName); // $NLX-ResourceInstaller.Installingcustomresourcesforlibra-1$
        Platform.getInstance().log(log);
        System.out.println(log);
    }

    public void endInstall() throws IOException {
        writer.close();
    }

    public void removeInstall() {
        // Remove all the library install file
        final String filterStr = library + "-";
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(filterStr);
            }
        };
        File[] files = installDirectory.listFiles(filter);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                removeInstall(files[i]);
            }
        }
    }
    private void removeInstall(File file) {
        try {
            // Note this might left some empty directories, but at least of the files
            // will be removed...
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")); // $NON-NLS-1$
            try {
                for (String s = r.readLine(); s != null; s = r.readLine()) {
                    if (StringUtil.isNotEmpty(s)) {
                        File f = new File(installDirectory, s);
                        f.delete();
                    }
                }
            } finally {
                r.close();
            }
            file.delete();
        } catch (IOException ex) {
            Platform.getInstance().log(ex);
        }
    }

    // The resources must be installed in a zip file as there is no way for enumerating reliably
    // resources in a plug-in and a fragment
    //      http://dev.eclipse.org/mhonarc/lists/equinox-dev/msg00194.html
    // And finally, having a separate zip file is good as it isolate all the resources in a single
    // file.
// From the class loader....    
//  public void installResources(String basePath, String fileName) throws IOException {
//      String resPath = PathUtil.concatPath(basePath,fileName,'/');
//      InputStream is = getClass().getClassLoader().getResourceAsStream(resPath);
//      if(is==null) {
//          throw new IOException(StringUtil.format("Cannot find resources entry {0}",resPath));
//      }
//      ZipInputStream zin = new ZipInputStream(is);
//      for( ZipEntry ze=zin.getNextEntry(); ze!=null; ze=zin.getNextEntry() ) {
//          if(!ze.isDirectory()) {
//              copyResource(zin,ze);
//              zin.closeEntry();
//          }
//      }
//      zin.close();
//  }
    public void installResources(Bundle bundle, String resPath) throws IOException {
        // getEntry() do not look into fragment....
        //URL url = bundle.getEntry(resPath);
        URL url = FileLocator.find(bundle, new Path(resPath), null);        
        if(url==null) {
            throw new IOException(StringUtil.format("Cannot find resources entry {0}",resPath)); // $NLX-ResourceInstaller.Cannotfindresourcesentry0-1$
        }
        ZipInputStream zin = new ZipInputStream(url.openStream());
        for( ZipEntry ze=zin.getNextEntry(); ze!=null; ze=zin.getNextEntry() ) {
            if(!ze.isDirectory()) {
                //System.out.println("Copying resouce: "+ze.getName());
                copyResource(zin,ze);
                zin.closeEntry();
            }
        }
        zin.close();
    }
    private void copyResource(ZipInputStream zin, ZipEntry ze) throws IOException {
        String resourcePath = ze.getName(); 
        String targetFileName = StringUtil.replace(resourcePath, '/', File.separatorChar);
        File targetFile = new File(installDirectory,targetFileName);
        File targetDir = targetFile.getParentFile();
        targetDir.mkdirs();

        // Add it to the file list
        writer.write(resourcePath);
        writer.newLine();
        
        OutputStream os = new FileOutputStream(targetFile);
        try {
            StreamUtil.copyStream(zin, os);
        } finally {
            os.close();
        }
    }

}