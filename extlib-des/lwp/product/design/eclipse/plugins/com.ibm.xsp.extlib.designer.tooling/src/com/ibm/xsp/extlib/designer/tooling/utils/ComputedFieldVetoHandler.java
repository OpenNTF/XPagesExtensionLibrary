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

package com.ibm.xsp.extlib.designer.tooling.utils;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;

/**
 * @author mblout
 *
 */
public abstract class ComputedFieldVetoHandler {
    
    private UpdateListener  listener = new UpdateListener();
    
    
    private class UpdateListener extends SelectionAdapter {
        
        boolean updateNext = false;
        DataNode.ComputedField cf;
        
        @Override
        public void widgetSelected(SelectionEvent event) {
            if (!updateNext) {
                return;
            }
            updateNext = false;
            updateControl(cf);
        }
        
        void armUpdate(DataNode.ComputedField cf) {
            updateNext = true;
            this.cf = cf;
        }
    };
    
    
    abstract public boolean shouldSet(DataNode.ComputedField cf, Object instance, String value, DataChangeNotifier notifier);
    abstract public void    updateControl(DataNode.ComputedField cf);

    public ComputedFieldVetoHandler(Control control) {
        if (control instanceof CompositeEditor) {
            control = ((CompositeEditor)control).getRealControl();
        }
        
        if (control instanceof Button) {
            ((Button)control).addSelectionListener(listener);
        }
        else if (control instanceof Combo) {
            ((Combo)control).addSelectionListener(listener);
        }
    }

    
    public boolean checkShouldSet(DataNode.ComputedField cf, Object instance, String value, DataChangeNotifier notifier) {
        boolean ok = shouldSet(cf, instance, value, notifier);
        if (!ok) {
            listener.armUpdate(cf);
        }
        return ok;
    }
}
