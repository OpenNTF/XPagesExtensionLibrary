/*
 * © Copyright IBM Corp. 2013
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 12 May 2011
* XspTestFileUtil.java
*/
package com.ibm.xsp.test.framework;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.xsp.page.translator.Lines;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class XspTestFileUtil {

    public static String readFileContents(File javaFilePath){
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(javaFilePath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't find the file " + javaFilePath.getAbsolutePath(), e);
        }
        // skip the line with the date
        StringBuffer result = new StringBuffer();
        try {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append(Lines.NEWLINE);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Couldn't read in the file " + javaFilePath.getAbsolutePath(), e1);
        }
        try {
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't close the file " + javaFilePath.getAbsolutePath(), e);
        }
        
        return result.toString();
    }
    public static String readFileContents(URL file){
        InputStream is;
        try {
            is = file.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem opening "+file);
        }
        BufferedReader in = null;
        try{
            in = new BufferedReader(
                    new InputStreamReader(is));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append(Lines.NEWLINE);
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem reading "+file);
        }finally{
            StreamUtil.close(in);
            StreamUtil.close(is);
        }
    }

	public static final InputStream getInputFromFileName(String fileName) throws FileNotFoundException{
	    return new BufferedInputStream(new FileInputStream(fileName));
	}

}
