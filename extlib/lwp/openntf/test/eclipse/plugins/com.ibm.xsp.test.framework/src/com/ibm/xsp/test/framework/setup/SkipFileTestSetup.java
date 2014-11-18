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
* SkipFileTestSetup.java
*/
package com.ibm.xsp.test.framework.setup;

import java.io.File;

import com.ibm.xsp.test.framework.XspTestFileUtil;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SkipFileTestSetup extends TestSetup {
    private String projectFileName;
    
    /**
     * 
     * @param test
     * @param projectFileName, the name of the junit fails / results file in the current project
     */
    public SkipFileTestSetup(Test test, String projectFileName) {
        super(test);
        this.projectFileName = projectFileName;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File javaFile = new File(projectFileName);
        if( ! javaFile.exists() ){
            throw new IllegalArgumentException(projectFileName+" does not exist at: "+javaFile.getAbsolutePath());
        }
        String fileContentAsString = XspTestFileUtil.readFileContents(javaFile);
        
        SkipFileParser parser = new SkipFileParser();
        SkipFileContent fileContentObj = parser.parse(fileContentAsString);
        
        SkipFileContent.setStaticSkips(fileContentObj);
    }

    @Override
    protected void tearDown() throws Exception {
//        SkipFileContent.clearStaticSkips();
        super.tearDown();
    }
}
