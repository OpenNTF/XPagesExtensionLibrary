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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;

/**
 * @author Gary Marjoram
 *
 */
public class Utils {
    
    private static final Pattern JAVA_CLASS_PATTERN = Pattern.compile("[A-Za-z_$]+[a-zA-Z0-9_$]*"); // $NON-NLS-1$

    //
    // Utility function to retrieve the package names from a Jar
    //
    public static void getJarPackages(final String jarName, final List<String> packageList) throws Exception {
        ZipInputStream zip = null;
        try {
            // If file is not zip/jar this will cause an Exception
            ZipFile file = new ZipFile(new File(jarName));
            file.close();

            // Scan Jar for packages
            zip = new ZipInputStream(new FileInputStream(jarName));
            ZipEntry ze;
            while ((ze = zip.getNextEntry()) != null) {
                String entry = ze.getName();
                if (entry.endsWith(".class")) { // $NON-NLS-1$
                    int idx = entry.lastIndexOf('/');
                    if (idx >= 0) {
                        entry = entry.substring(0, idx).replace('/', '.');
                        if (!packageList.contains(entry)) {
                            packageList.add(entry);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = StringUtil.format("Error processing \"{0}\"", jarName); // $NLX-Utils.Errorprocessing0-1$
            throw new Exception(msg, e);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    //
    // Utility function to read a file from a bundle
    //
    public static String getFileContents(final Bundle bundle, final String resName) throws Exception {
        URL resURL = bundle.getResource(resName);
        String content;
        
        InputStream is = resURL.openStream();
        try {
            java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A"); // $NON-NLS-1$ $NON-NLS-2$
            content = s.hasNext() ? s.next() : "";
        } finally {
            StreamUtil.close(is);
        }

        return content;
    }

    //
    // Utility function to write an input stream to a jar
    //
    public static void writeJarEntry(final JarOutputStream jar, final InputStream is) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            byte[] buf = new byte[8192];
            while (true) {
                int count = bis.read(buf);
                if (count == -1)
                    break;
                jar.write(buf, 0, count);
            }
            jar.closeEntry();
        } finally {
            StreamUtil.close(bis);
        }
    }

    //
    // Utility function to create a directory in the file system
    //
    public static void createDirectory(final String dir) throws Exception {
        File f = new File(dir);
        f.mkdirs();
        if (!f.exists()) {
            String msg = StringUtil.format("Could not create \"{0}\" directory", dir); // $NLX-Utils.Couldnotcreate0directory-1$
            throw (new Exception(msg));
        }
    }

    //
    // Utility function to check the validity of a class name
    //
    public static boolean isValidClassName(final String text) {
        for (String part : text.split("\\.")) {
            if (!JAVA_CLASS_PATTERN.matcher(part).matches()) {
                return false;
            }
        }
        return text.length() > 0;
    }
}