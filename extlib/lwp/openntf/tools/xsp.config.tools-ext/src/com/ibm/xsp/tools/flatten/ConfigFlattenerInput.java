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
* Date: 2 Jan 2007
* ConfigFlattenerInput.java
*/
package com.ibm.xsp.tools.flatten;

import java.io.File;
import java.util.Map;

import com.ibm.xsp.tools.ToolsUtil;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 2 Jan 2007
 * Unit: ConfigFlattenerInput.java
 */
public class ConfigFlattenerInput {
    
    private final boolean valid;
    private String inFileString;
    private String outFileName;
    private String outFolder;
    private boolean facesConfigMode;
    private boolean inlineMode;
    private boolean ignoreWarnings;
    private boolean doNotTranslate;
    
    private File inFile;
    private File outFile;
    
    private String extraPropsFolder;

    public ConfigFlattenerInput(String[] args) {
        if( null == args || args.length < 6 ){
            valid = false;
            return;
        }
        
        printDebugging(args);
        
        Map<String, String> switches = ToolsUtil.processArgs( args );
        
        extraPropsFolder = switches.get("--extraPropsFolder");
        
        inFileString = switches.get("--in");
        outFileName = switches.get("--outFileName");
        if (null == inFileString || null == outFileName ) {
            valid = false;
            return;
        }
        
        outFolder = switches.get("--outFolder");
        String modeStr = switches.get("--mode");
        facesConfigMode = "faces-config".equals(modeStr);
        inlineMode = "inline".equals(modeStr);
        ignoreWarnings = "true".equals(switches.get("--ignoreWarnings"));
        doNotTranslate = "true".equals(switches.get("--doNotTranslate"));

        inFile = new File(inFileString);
        outFile = new File(outFolder + outFileName);
        valid = true;
    }
    @SuppressWarnings("unused")
    private void printDebugging(String[] args) {
        if( false ){ // debugging
            String argsStr = "";
            for (String arg : args) {
                argsStr += arg + ' ';
            }
            System.out.println("ConfigFlattenerInput.ConfigFlattenerInput() Debugging. args are: \n"+argsStr);
        }
    }
    public void printUsage() {
        System.out.println("Usage:");
        System.out.println("\tjava ConfigFlattener" 
                + " --in <filename>"
                + " --outFileName <filename>" 
                + " --outFolder <folder>"
                + " [--mode faces-config|inline]"
                + " [--ignoreWarnings true]"
                + " [--doNotTranslate true]"
                );
    }
    public void print(){
        // ==== Print to System.out =======================================
        System.out.println("Flattening file "+inFile.getName()+" -> " +
                outFile.getParentFile().getName()+"/"+outFile.getName()
                +" \t("+inFile+") to ("+outFile+")");
        if( facesConfigMode ){
            System.out.println("-\t using --mode faces-config");
        }
        if( inlineMode ){
            System.out.println("-\t using --mode inline");
        }
        if( ignoreWarnings ){
            System.out.println("-\t using --ignoreWarnings true");
        }
        if( doNotTranslate ){
            System.out.println("-\t using --doNotTranslate true");
        }
    }
    /**
     * @return the facesConfigMode
     */
    public boolean isFacesConfigMode() {
        return facesConfigMode;
    }
    
    /**
     * @return the inlineMode
     */
    public boolean isInlineMode() {
        return inlineMode;
    }
    /**
     * @return the inFileString
     */
    public String getInFileString() {
        return inFileString;
    }
    /**
     * @return the outFileName
     */
    public String getOutFileName() {
        return outFileName;
    }
    /**
     * @return the outFolder
     */
    public String getOutFolder() {
        return outFolder;
    }
    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }
    /**
     * @return the inFile
     */
    public File getInFile() {
        return inFile;
    }
    /**
     * @return the inFile name
     */
    public String getInFileName() {
        return inFile.getName();
    }
    /**
     * @return the outFile
     */
    public File getOutFile() {
        return outFile;
    }

    public String getOutFileBaseName(){
        int lastDotIndex = outFileName.lastIndexOf('.');
        if( -1 == lastDotIndex ){
            return outFileName;
        }
        return outFileName.substring(0, lastDotIndex);
    }
    public File getOutPropertiesFile(){
        return new File(outFolder +getOutFileBaseName()+"_en.properties");
    }
    
    /**
     * @return the extraPropsFolder
     */
    public String getExtraPropsFolder() {
        return extraPropsFolder;
    }
    /**
     * @return the ignoreWarnings
     */
    public boolean isIgnoreWarnings() {
        return ignoreWarnings;
    }
    /**
     * @return the doNotTranslate
     */
    public boolean isDoNotTranslate() {
        return doNotTranslate;
    }
    
}
