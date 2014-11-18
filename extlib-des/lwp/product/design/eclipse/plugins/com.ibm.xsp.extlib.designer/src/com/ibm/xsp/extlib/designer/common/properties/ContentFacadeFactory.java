/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.xsp.extlib.designer.common.properties;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;

/**
 * @author mblout
 *
 */
public class ContentFacadeFactory {
    
    private static ContentFacadeFactory instance = null;
    
    public static ContentFacadeFactory instance() {
        if (null == instance) {
            instance = new ContentFacadeFactory();
        }
        return instance;
    }
    
    
    /**
     * 
     * @param o
     * @return
     */
    public PreservingProperties.ContentFacade getFacadeForObject(Object o) {
        if (o instanceof IFile) {
            return new EFSFileProvider((IFile)o);
        }
// @TODO might want to implement other instances of ContentFacade
//        else if (o instanceof String) {
//          how do we get IFile from a path here, vs. java.io.File?
//        }
//        else if (o instanceof java.io.File {
//        }
         
        return null;
    }
    
    
    public PreservingProperties.ContentFacade getFacadeByName(String name) {
        //@todo: need to implement ContentFacadeFactory.getFacadeByName
        // we really should have a project at this point to get an IFile.
        // not sure how to implement
        throw new RuntimeException("not implemented yet."); //$NON-NLS-1$
        
         
//        return null;
    }




    public static class EFSFileProvider implements PreservingProperties.ContentFacade {
        
        final IFile file;
        
//      final int flags = IResource.FORCE | IResource.KEEP_HISTORY;
        final int flags = 0;

        
        public EFSFileProvider(IFile file) {
            this.file = file;
        }

        public String getName() {
            return file.getName();
        }

        // caller expected to close stream
        public InputStream getContents() {
            try {
                return file.getContents();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setContents(InputStream in) {
            try {
                file.setContents(in, flags, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* (non-Javadoc)
         * @see com.ibm.xsp.extlib.designer.tooling.utils.PreservingProperties.ContentFacade#append(java.io.InputStream)
         */
        public void append(InputStream in) {
            try {
                file.appendContents(in, flags, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
        
    

}
