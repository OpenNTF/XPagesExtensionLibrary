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

package com.ibm.xsp.extlib.designer.bluemix.manifest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestAppProps extends ManifestBaseProps {
    
    public final static String NAME_TAG = "name"; // $NON-NLS-1$
    private String             _appName;
    
    public ManifestAppProps() {
        super();
    }
    
    public ManifestAppProps(Map<Object, Object> manifest) {
        super(manifest);
        if (manifest != null) {
            _appName = ManifestUtil.getStringValue(manifest, NAME_TAG);
        }
    }
    
    public String getAppName() {
        return _appName;
    }
    
    public void setAppName(String name) {
        _appName = name;
    }
    
    public Map<String, Object> convertToMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        // Ensure that appName is always trimmed
        if (_appName != null) {
            ManifestUtil.setStringValue(map, NAME_TAG, _appName.trim(), false);
        } else {
            ManifestUtil.setStringValue(map, NAME_TAG, null, false);            
        }
        super.convertToMap(map);
        return map;
    }
}