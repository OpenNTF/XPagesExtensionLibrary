/*
 * © Copyright IBM Corp. 2012
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
* Date: 20 Mar 2012
* SkipFileParser.java
*/
package com.ibm.xsp.test.framework.setup;

import com.ibm.commons.util.StringUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SkipFileParser {

    public SkipFileContent parse(String fileContent){
        SkipFileContent content = new SkipFileContent();
        
        fileContent = fileContent.replaceAll("\r\n", "\n");
        
        String[] lines = StringUtil.splitString(fileContent, '\n');
        boolean inChunk = false;
        int startChunk = -1;
        boolean inTrace = false;
        int startTrace = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if( "".equals(line) ){
                
                if( inChunk ){
                    updateContent(content, inChunk, startChunk, inTrace, startTrace, i, lines);
                }
                inTrace = false;
                
                inChunk = true;
                startChunk = i;
            }else if( '\t' == line.charAt(0) ){
                if( !inTrace ){
                    inTrace = true;
                    startTrace = i;
                }
            }
        }
        
        
        return content;
    }

    private void updateContent(SkipFileContent content, boolean inChunk, int startChunk, boolean inTrace, int startTrace, 
            int index,
            String[] lines) {
        if( inChunk && inTrace ){
            // Look for a line like:
            // junit.framework.AssertionFailedError: 4 fail(s). :
            int beginFailsIndex = -1;
            for (int i = startChunk; i < startTrace; i++) {
                String line = lines[i];
                if( line.startsWith("junit.framework.AssertionFailedError: ") && line.endsWith(" fail(s). :") ){
                    beginFailsIndex = i+1;
                    break;
                }
            }
            if( -1 == beginFailsIndex ){
                // no line with " fails(s). :"
                return;
            }
            String[] fails = new String[startTrace - beginFailsIndex];
            System.arraycopy(lines, beginFailsIndex, fails, 0, fails.length);
            
            // Lines in the range [startChunk, beginFailsIndex] will be like:
            //
            //~ IncubatorTestSuite
            //xsp.extlibinc.test.IncubatorTestSuite
            //com.ibm.xsp.test.framework.registry.BaseComponentTypeTest
            //testComponentType(com.ibm.xsp.test.framework.registry.BaseComponentTypeTest)
            //junit.framework.AssertionFailedError: 1 fail(s). :

            // counting from the end of that range backwards:
            //String failsLine = lines[beginFailsIndex-1];
            String methodLine = lines[beginFailsIndex-2];
            String testClassLine = lines[beginFailsIndex-3];
            
            //testComponentType(com.ibm.xsp.test.framework.registry.BaseComponentTypeTest)
            String methodName = methodLine.substring(0, methodLine.indexOf('('));
            
            content.addSkips(testClassLine, methodName, fails);
        }
    }
}
