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
* Date: 16 Jan 2008
* TestPageConfig.java
*/

package com.ibm.xsp.test.framework;

import com.ibm.xsp.library.FacesClassLoader;
import com.ibm.xsp.library.PageLoadingConfiguration;
import com.ibm.xsp.page.file.PageFileSystem;
import com.ibm.xsp.registry.config.ResourceBundleSource;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 16 Jan 2008
 * Unit: TestPageConfig.java
 */
public class TestPageConfig implements PageLoadingConfiguration {

    private PageFileSystem pageFileSystem;
    private FacesClassLoader facesClassLoader;
    private ResourceBundleSource bundleSource;
    public TestPageConfig(PageFileSystem pageFileSystem,
            FacesClassLoader facesClassLoader, ResourceBundleSource bundleSource) {
        super();
        this.pageFileSystem = pageFileSystem;
        this.facesClassLoader = facesClassLoader;
        this.bundleSource = bundleSource;
    }
    /**
     * @return the pageFileSystem
     */
    public PageFileSystem getPageFileSystem() {
        return pageFileSystem;
    }
    /**
     * @return the facesClassLoader
     */
    public FacesClassLoader getFacesClassLoader() {
        return facesClassLoader;
    }
    /* (non-Javadoc)
     * @see com.ibm.xsp.library.PageLoadingConfiguration#getConfigBundleSource()
     */
    public ResourceBundleSource getConfigBundleSource() {
        return bundleSource;
    }
}
