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

import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.loaders.JavaBeanLoader;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestBeanLoader extends JavaBeanLoader {
    
    private final ManifestMultiPageEditor _parentEditor;
    
    public ManifestBeanLoader(String namespace, FileStoreEditorInput manifestInput, ManifestMultiPageEditor parentEditor) {
        super(namespace);
        _parentEditor = parentEditor;       
    }

    @Override
    public void setValue(Object instance, IAttribute attribute, String value, DataChangeNotifier dataChangeNotifier) throws NodeException {
        super.setValue(instance, attribute, value, dataChangeNotifier);
        
        // Ensure the source editor is kept in sync
        _parentEditor.writeContentsFromBean();
    }
}
