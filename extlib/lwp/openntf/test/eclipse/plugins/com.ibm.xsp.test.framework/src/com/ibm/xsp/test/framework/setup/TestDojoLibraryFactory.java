/*
 * © Copyright IBM Corp. 2013
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
* Date: 14 Mar 2011
* TestDojoLibraryFactory.java
*/

package com.ibm.xsp.test.framework.setup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.DojoLibrary;
import com.ibm.xsp.context.DojoLibraryFactory;
import com.ibm.xsp.core.Version;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TestDojoLibraryFactory extends DojoLibraryFactory {
    // This TestDojoLibraryFactory is needed in the junit tests
    // because the DojoLibrary in the ..xsp.dojo isn't on the junit classpath,
    // and without it the console will contain the stack trace:
    // 17-May-2011 13:53:04 com.ibm.xsp.context.RequestParameters logDojoLibraryNotAvailable
    // SEVERE: Dojo library not available, cause: No Dojo library found on the server.
    // com.ibm.xsp.context.DojoLibraryNotAvailableEx: No Dojo library found on the server.
    //    at com.ibm.xsp.context.RequestParameters.findDojoLibrary(RequestParameters.java:238)


    public class TestDojoLibrary extends DojoLibrary {
        @Override
        public boolean isDefaultLibrary() {
            return true;
        }
        @Override
        public boolean isDefaultIbmLibrary() {
            return true;
        }
        @Override
        public Version getVersion() {
            return new Version(1,5,0);
        }
        @Override
        public String getVersionTag() {
            return "1.5.0";
        }
        @Override
        public String getResourceUrl(String url, boolean optimize) {
            String prefix = "/.ibmxspres/dojoroot/";
            if( StringUtil.isNotEmpty(url) && url.startsWith(prefix) ){
                String path = url.substring(prefix.length());
                return "/xsp/.ibmxspres/dojoroot-1.5.0/"+path;
            }
            return null;
        }
        @Override
        public boolean exists(String path) {
            throw new NotImplementedException();
        }
        @Override
        public InputStream getFileInputStream(String path) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public boolean hasIbmModules() {
            return true;
        }
        @Override
        public boolean useIbmLayers() {
            return false;
        }
    }

    @Override
    public Collection<DojoLibrary> getLibraries() {
        DojoLibrary lib = new TestDojoLibrary();
        return Collections.singletonList(lib);
    }

}
