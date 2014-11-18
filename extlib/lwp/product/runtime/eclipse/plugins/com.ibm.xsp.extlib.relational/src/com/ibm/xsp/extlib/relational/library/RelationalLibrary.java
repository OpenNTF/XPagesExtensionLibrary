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
package com.ibm.xsp.extlib.relational.library;

import com.ibm.xsp.extlib.relational.version.RelationalVersion;
import com.ibm.xsp.library.AbstractXspLibrary;

/**
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalLibrary extends AbstractXspLibrary {

    public RelationalLibrary(){
    }

    public String getLibraryId() {
        return "com.ibm.xsp.extlib.relational.library"; //$NON-NLS-1$
    }

    public String getPluginId() {
        return "com.ibm.xsp.extlib.relational"; //$NON-NLS-1$
    }
    
    @Override
    public String[] getDependencies() {
        return new String[] {
            "com.ibm.xsp.core.library", // $NON-NLS-1$
            "com.ibm.xsp.extsn.library", // $NON-NLS-1$
            "com.ibm.xsp.domino.library", // $NON-NLS-1$
            "com.ibm.xsp.designer.library", // $NON-NLS-1$
            "com.ibm.xsp.extlib.library", // $NON-NLS-1$
        };
    }

    @Override
    public String[] getXspConfigFiles() {
        return new String[]{
                "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config", // $NON-NLS-1$
                "com/ibm/xsp/extlib/relational/config/relational-jdbc-rest.xsp-config", // $NON-NLS-1$
        };
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.library.AbstractXspLibrary#getTagVersion()
     */
    @Override
    public String getTagVersion() {
        return RelationalVersion.getCurrentVersionString();
    }
}