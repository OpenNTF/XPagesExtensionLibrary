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
package com.ibm.xsp.extlib.designer.xspprops.xsplibs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.xspprops.XSPAllProperties;
import com.ibm.xsp.library.LibraryServiceLoader;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.library.MissingLibraryWrapper;

/**
 * @author doconnor
 *
 */
public class XPageLibraryContentProvider implements ITreeContentProvider {
    private Map<String, LibraryWrapper> missingIdsAndLibraries = new HashMap<String, LibraryWrapper>();
    

    /**
     * 
     */
    public XPageLibraryContentProvider() {
        super();
    }
 
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object arg0) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object lib) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object input) {
        List<LibraryWrapper> libs = LibraryServiceLoader.getLibraryList();
        List<String> existingIds = new ArrayList<String>();
        if(input instanceof XSPAllProperties){
        	String dependencies = ((XSPAllProperties)input).getDependencies();
        	if(StringUtil.isNotEmpty(dependencies)){
        		existingIds.addAll(Arrays.asList(dependencies.split(",")));
        	}
        }
        if(libs != null){
            List<LibraryWrapper> extraLibs = new ArrayList<LibraryWrapper>();
            List<String> extras = Arrays.asList(LibraryServiceLoader.getExtraLibraryIds());
            for(LibraryWrapper lib : libs){
                if(extras.contains(lib.getLibraryId())){
                    extraLibs.add(lib);
                    existingIds.remove(lib.getLibraryId());
                }
            }
            if(!existingIds.isEmpty()){
            	for(String id : existingIds){
            		if(missingIdsAndLibraries.get(id) == null){
            			missingIdsAndLibraries.put(id, new MissingLibraryWrapper(id));
            		}
            		extraLibs.add(missingIdsAndLibraries.get(id));
            	}
            }
            return extraLibs.toArray(new LibraryWrapper[0]);
        }
        return new String[]{"No libraries installed"}; // $NLS-XPageLibraryContentProvider.Nolibrariesinstalled-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
    }

}