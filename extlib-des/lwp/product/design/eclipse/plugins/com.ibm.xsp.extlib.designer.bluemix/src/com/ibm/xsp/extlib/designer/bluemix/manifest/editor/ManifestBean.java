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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import java.io.File;

import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.BluemixManifest;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestAppProps;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestBean {
    
    private BluemixManifest  _manifest;
    private ManifestAppProps _bluemixProps;
    private File             _file;
    private long             _modifiedTime;
    
    public ManifestBean (FileStoreEditorInput fei) {
        _file = new File(fei.getURI());
        loadFromFile();
    }
    
    public ManifestAppProps getManifestProperties() {
        return _bluemixProps;
    }
    
    public void setManifestProperties(ManifestAppProps bluemixProps) {
        _bluemixProps = bluemixProps;
    }    
    
    // Loads the manifest from the file
    public void loadFromFile() {
        _manifest = new BluemixManifest(_file);
        _modifiedTime = _file.lastModified();
        _bluemixProps = _manifest.getFirstApp();       
    }
    
    // Loads the manifest from a String
    // Used if someone is editing the yml in the text editor
    public void loadFromString(String contents) {
        _manifest.loadFromYamlString(contents);
        _bluemixProps = _manifest.getFirstApp();  
        if ((_bluemixProps == null) && (StringUtil.isEmpty(contents.trim()))) {
            // Empty Manifest - make a dummy _bluemixProps so that the
            // visual editor will function
            _bluemixProps = new ManifestAppProps();
        }
    }
    
    public String getContents() {
        return _manifest.updateAndGetContents(_bluemixProps);
    }
    
    public boolean isManifestValid() {
        return _bluemixProps != null;
    }
    
    public boolean externallyModified() {
        if (_modifiedTime != _file.lastModified()) {
            return true;
        }
        return false;
    }
    
    public void resetModifiedTime() {
        _modifiedTime = _file.lastModified();
    }
    
    public String getFileName() {
        return _file.getPath();
    }
}
