 /*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 4 Dec 2007
* IgnoreHeaderCompare.java
* Copied from the \lwp04.wct-des FE to this \extlib FE on 2012-May-02.
*/

package com.ibm.xsp.tools;

import java.io.*;
import java.util.Map;

import com.ibm.xsp.page.translator.Lines;

/**
 * Diffs 2 files ignoring the contents of the copyright header.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 4 Dec 2007
 * Unit: IgnoreHeaderCompare.java
 */
public class IgnoreHeaderCompare {

    /**
     * @param args
     */
    public static void main(String[] argsArray) {
        CompareInput args = new CompareInput(argsArray);
        boolean same = new IgnoreHeaderCompare().isContentsSame(args);
        if( same ){
            System.out.println("true");
        }
//        else{
//            System.err.println("IgnoreHeaderCompare contents are different.");
//        }
    }
    public boolean isContentsSame(CompareInput args){
        if( !args.isValid() ){
            args.print();
            // safe default
            return true;
        }
        File oldPath = new File(args.oldLocation); 
        File newPath = new File(args.newLocation); 
        if( ! newPath.exists() || ! oldPath.exists()){
            // unchanged
            return true;
        }
        
        String existing = readFileContents(oldPath);
        String newContents = readFileContents(newPath);
        if( null == existing || null == newContents ){
            // some error in those methods
            return true;
        }
        
        // the delimiter is after the copyright header.
        String delimiter;
        if( "xml".equals(args.type) ){
            delimiter = "<faces-config>";
        }else{ // "props"
            delimiter = "## G11N"; 
        }
        existing = afterDelimiter(existing, delimiter);
        newContents = afterDelimiter(newContents, delimiter);
        
        existing = stripEmptyIcons(existing);
        newContents = stripEmptyIcons(newContents);

//        System.err.println("IgnoreHeaderCompare.fileContentsSame() Old Contents: =========================\n"+existing);
//        System.err.println("IgnoreHeaderCompare.fileContentsSame() New Contents: =========================\n"+newContents);
        return newContents.equals( existing );
    }
    /**
     * @param existing
     * @return
     */
    private String stripEmptyIcons(String existing) {
        StringBuffer b = new StringBuffer(existing);
        String icon = "<icon/>";
        int iconIndex = b.indexOf(icon);
        while( -1 != iconIndex ){
            
            // the line containing the empty icon
            int start = b.lastIndexOf("\n", iconIndex);
            if( -1 == start ){
                start = 0;
            }
            int end = b.indexOf("\n", iconIndex);
            if( -1 == end ){
                end = b.length();
            }
            // remove the line
            b.replace(start, end, "");
            
            iconIndex = b.indexOf("<icon/>", start);
        }
        return b.toString();
    }
    private String afterDelimiter(String existing, String delimiter) {
        int index = existing.indexOf(delimiter);
        if( index < 0 ){
            return existing;
        }
        return existing.substring(index);
    }
    private String readFileContents(File javaFilePath){
        if( javaFilePath.isDirectory() ){
            throw new IllegalArgumentException(
                    "The java file path is a directory: "
                            + javaFilePath.getAbsolutePath());
        }
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(javaFilePath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Couldn't find the file " + javaFilePath.getAbsolutePath());
            return null;
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
            System.err.println("Couldn't read in the file " + javaFilePath.getAbsolutePath());
        }
        try {
            in.close();
        }
        catch (IOException e) {
            System.err.println("Couldn't close the file " + javaFilePath.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        
        return result.toString();
    }


    private static class CompareInput{
        public String type;
        public String oldLocation;
        public String newLocation;
        public CompareInput(String[] argArray) {
            Map<String, String> args = ToolsUtil.processArgs( argArray );
            String skipStr = args.get("--skip");
            if( null != skipStr ){
                RuntimeException complaint = new RuntimeException("Argument --skip no longer supported");
                complaint.printStackTrace();
                throw complaint;
            }
            
            type = args.get("--type");
            if( null == type ){
                type = "xml";
            }
            oldLocation = args.get("--old");
            newLocation = args.get("--new");
//            System.err.println("IgnoreHeaderCompare skip: "+skipStr+" ("+skip+")");
//            System.err.println("IgnoreHeaderCompare type: "+type);
//            System.err.println("IgnoreHeaderCompare old: "+oldLocation);
//            System.err.println("IgnoreHeaderCompare new: "+newLocation);
        }
        public boolean isValid(){
            return null != oldLocation && null != newLocation; 
        }
        public void print(){
            System.err.println("IgnoreHeaderCompare Invalid arguments. old="+oldLocation+" new="+newLocation);
        }
        
    }
}
