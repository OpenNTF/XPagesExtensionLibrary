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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ibm.xsp.extlib.designer.xspprops.Activator;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.library.MissingLibraryWrapper;

/**
 * @author doconnor
 *
 */
public class XPageLibraryLabelProvider implements ITableLabelProvider {
    private Image warningImg = null;
    
    

    /**
     * 
     */
    public XPageLibraryLabelProvider() {
        super();
    }
 
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int column) {
        if(element instanceof MissingLibraryWrapper){
            if(warningImg == null){
            	ImageDescriptor desc = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/warning_overlay.gif");  // $NON-NLS-1$
            	if(desc != null){
            		warningImg = desc.createImage();
            	}
            }
            return warningImg;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int column) {
        if(element instanceof LibraryWrapper){
            if(column == 0){
                return ((LibraryWrapper)element).getLibraryId();
            }
        }
        if(element instanceof String){
            return (String)element;
        }
        return "";
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener arg0) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        if(warningImg != null && !warningImg.isDisposed()){
            warningImg.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object arg0, String arg1) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener arg0) {
    }

}