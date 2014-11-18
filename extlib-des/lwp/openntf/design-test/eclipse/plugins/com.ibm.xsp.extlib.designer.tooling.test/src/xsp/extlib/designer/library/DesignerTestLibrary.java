/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.library;

import com.ibm.xsp.library.AbstractXspLibrary;

/**
 * @author mblout
 *
 */
public class DesignerTestLibrary extends AbstractXspLibrary {

    public String[] getDependencies() {
        
//      return super.getDependencies();
        
      String[] libs = new String[] {
              "com.ibm.xsp.core.library", //$NON-NLS-1$
              "com.ibm.xsp.extlib.library" // $NON-NLS-1$
      };
      return libs;
    }

    /**
     * 
     */
    public DesignerTestLibrary() {
        super();
//        System.out.println("DesignerTestLibrary");
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.library.AbstractXspLibrary#getFacesConfigFiles()
//     */
//    @Override
//    public String[] getFacesConfigFiles() {
//        String[] files = new String[] {
//                "META-INF/designer-test-library-faces-config.xml" // $NON-NLS-1$
//        };
//        return files;
//    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.library.AbstractXspLibrary#getXspConfigFiles()
     */
    @Override
    public String[] getXspConfigFiles() {
        String[] files = new String[] {
                "META-INF/designer-test-library.xsp-config" // $NON-NLS-1$
        };
        return files;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.library.XspLibrary#getLibraryId()
     */
    public String getLibraryId() {
        return this.getClass().getName();
    }

}
