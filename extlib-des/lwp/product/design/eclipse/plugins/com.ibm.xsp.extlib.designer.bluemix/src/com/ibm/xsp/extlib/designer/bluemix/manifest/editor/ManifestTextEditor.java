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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestTextEditor extends TextEditor {
    
    private boolean _externallyModified = false;
    
    public boolean isExternallyModified() {
        return _externallyModified;
    }

    public void setExternallyModified(boolean externallyModified) {
        _externallyModified = externallyModified;
    }

    @Override
    public boolean isDirty() {
        if (_externallyModified) {
            return true;
        }
        return super.isDirty();
    }

    @Override
    protected void performRevert() {
        _externallyModified = false;
        super.performRevert();
    }
    
    @Override
    protected void performSave(boolean overwrite, IProgressMonitor progress) {
        _externallyModified = false;        
        super.performSave(overwrite, progress);
    }
}
