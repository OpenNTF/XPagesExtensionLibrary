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
package com.ibm.xsp.extlib.relational.resources.provider;

import java.io.InputStream;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.vfs.VFS;
import com.ibm.commons.vfs.VFSFile;
import com.ibm.designer.runtime.Application;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;


/**
 * Provider of JNDI conections.
 * @author priand
 */
public class NSFFileJdbcProvider extends AbstractFileJdbcProvider {
    
    public NSFFileJdbcProvider() {
    }
    
    @Override
    protected String[] getFileEntries() throws ResourceFactoriesException {
        // Get access to the resource
        try {
            Application app = Application.get();
            List<VFSFile> files = app.getVFS().getFolder(JdbcUtil.JDBC_ROOT).findFiles("*.jdbc"); // $NON-NLS-1$
            if(files!=null && !files.isEmpty()) {
                String[] s = new String[files.size()];
                for(int i=0; i<s.length; i++) {
                    s[i] = files.get(i).getNameWithoutExtension();
                }
                return s;
            }
            return null;
        } catch(Exception ex) {
            throw new ResourceFactoriesException(ex,StringUtil.format("Error while reading {0} resource list", "JDBC")); // $NLX-NSFFileJdbcProvider.Errorwhilereading0resourcelist-1$ $NON-NLS-2$
        }
    }
    
    @Override
    protected InputStream getFileContent(String name) throws ResourceFactoriesException {
        // Get access to the resource
        try {
            Application app = Application.get();
            VFSFile file = app.getVFS().getFile(JdbcUtil.JDBC_ROOT+VFS.SEPARATOR+name+".jdbc"); // $NON-NLS-1$
            if(file.exists()) {
                return file.getInputStream();
            }
        } catch(Exception ex) {
            throw new ResourceFactoriesException(ex,StringUtil.format("Error while reading {0} resource {1}", "JDBC", name)); // $NLX-NSFFileJdbcProvider.Errorwhilereading0resource1-1$ $NON-NLS-2$
        }
        
        // Not available yet
        return null;
    }
}