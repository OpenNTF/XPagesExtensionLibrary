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
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;

/**
 * @author Gary Marjoram
 *
 */
public class BluemixZipUtil {
    
    static public void zipDirectory(String srcDirName, String zipFileName) throws Exception {    
        ZipOutputStream zipOs = null;
        try {
            zipOs = new ZipOutputStream(new FileOutputStream(zipFileName));
            addDirectoryContentsToZip("", srcDirName, zipOs);
        } finally {
            if (zipOs != null) {
                zipOs.flush();
                zipOs.close();
            }
        }
    }

    static private void addDirectoryContentsToZip(String zipPath, String srcFolderName, ZipOutputStream zipOs) throws Exception {
        File folder = new File(srcFolderName);
        for (String fileName : folder.list()) {
            addFileToZip(zipPath, srcFolderName + "/" + fileName, zipOs);
        }
    }
        
    static private void addFileToZip(String zipPath, String srcFileName, ZipOutputStream zipOs) throws Exception {
        byte[] buf = new byte[1024];
        File file = new File(srcFileName);
        String zipFilePath = StringUtil.isEmpty(zipPath) ? file.getName() : zipPath + "/" + file.getName();
        
        if (file.isDirectory()) {
            zipOs.putNextEntry(new ZipEntry(zipFilePath + "/"));            
            zipOs.closeEntry();
            addDirectoryContentsToZip(zipFilePath, srcFileName, zipOs);
        }
        else {
            FileInputStream in = null;
            try {
                int len;
                zipOs.putNextEntry(new ZipEntry(zipFilePath));
                in = new FileInputStream(srcFileName);
                while ((len = in.read(buf)) > 0) {
                    zipOs.write(buf, 0, len);
                }
            } finally {
                if (in != null) {
                    in.close();
                    zipOs.closeEntry();
                }
            }
        }
    }

    static public void unzipFile(String zipFile, String outputFolder) throws Exception {
        byte[] buf = new byte[1024];
        ZipInputStream zis = null;
        
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                
                // Create sub-folders
                new File(newFile.getParent()).mkdirs();

                if (ze.isDirectory()) {
                    // Handles empty folders in the zip file
                    newFile.mkdirs();
                } else {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                        }
                    } finally {
                        if (fos != null) {
                            fos.flush();
                            fos.close();                            
                        }
                    }
                }
                ze = zis.getNextEntry();
            }
        } finally {
            if (zis != null) {
                zis.closeEntry();
                zis.close();
            }
        }
    }
    
    static public boolean doesZipContain(final File file, String [] checkFiles) {
        for (String checkFile: checkFiles) {
            if (!doesZipContain(file, checkFile)) {
                return false;
            }
        }
        
        return true;
    }
        
    static public boolean doesZipContain(final File file, String checkFile) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (checkFile.charAt(0) == '*') {
                    if (fileName.toLowerCase().endsWith(checkFile.toLowerCase().substring(1))) {
                        zis.closeEntry();
                        zis.close();
                        zis = null;
                        return true;                        
                    }
                } else {
                    if (StringUtil.equalsIgnoreCase(fileName, checkFile)) {
                        zis.closeEntry();
                        zis.close();
                        zis = null;
                        return true;
                    }
                }
                ze = zis.getNextEntry();
            }
        } catch (Exception e) {
            // Not a zip file ?
            return false;
        } finally {
            try {
                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
            } catch (Exception e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixZipUtil.class, "doesZipContain", e, "Failed to close ZIP entry"); // $NON-NLS-1$ $NLE-BluemixZipUtil.Failedtoclosezipentry-2$
                }
            }
        }
        return false;
    }
    
    static public boolean isValidZipFile(final File file) {
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(file);
            return true;
        } catch (ZipException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                }
            } catch (IOException e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixZipUtil.class, "isValidZipFile", e, "Failed to close ZIP file"); // $NON-NLS-1$ $NLE-BluemixZipUtil.Failedtoclosezipfile-2$
                }
            }
        }  
    }
    
    public static String getNsfFromZipFile(final File file) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (fileName.toLowerCase().endsWith(".nsf")) { // $NON-NLS-1$
                    zis.closeEntry();
                    zis.close();
                    zis = null;
                    return fileName;                        
                }
                ze = zis.getNextEntry();
            }
        } catch (Exception e) {
            // Not a zip file ?
            return null;
        } finally {
            try {
                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
            } catch (Exception e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixZipUtil.class, "getNsfFromZipFile", e, "Failed to close ZIP entry"); // $NON-NLS-1$ $NLE-BluemixZipUtil.Failedtoclosezipentry-2$
                }
            }
        }
        
        return null;
    }
    
}