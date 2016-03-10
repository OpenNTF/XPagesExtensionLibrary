/*
 * © Copyright IBM Corp. 2015
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

package com.ibm.xsp.extlib.designer.bluemix.config;

import java.io.File;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;

/**
 * @author Gary Marjoram
 *
 */
public class BluemixConfig implements Cloneable {
    
    public String directory;
    public String org;
    public String space;
    public String appName;
    public String host;
    public String uri;
    public String copyMethod;

    public boolean isValid(boolean includeManifest) {
        if (StringUtil.isEmpty(directory)) {
            return false;
        }

        File file = new File(directory);
        if ((!file.exists()) || (!file.isDirectory())) {
            return false;
        }

        if (StringUtil.isEmpty(org)) {
            return false;
        }

        if (StringUtil.isEmpty(space)) {
            return false;
        }

        if (includeManifest && StringUtil.isEmpty(appName)) {
            // appName will be blank if the manifest is invalid or missing
            return false;
        }

        // You can cf-push without Host
        return true;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(this, "clone", e, "Failed to clone"); // $NON-NLS-1$ $NLE-BluemixConfig.Failedtoclone-2$
            }
            return null;
        }
    }
}