/*
 * © Copyright IBM Corp. 2010
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
package com.ibm.xsp.extlib.component.picker.data;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Directory;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.model.domino.DominoUtils;



/**
 * Data provider for a name picker using the Domino Directory API.
 * <p>
 * !This is experimental!
 * </p>
 */
public class DominoDirectoryNamePickerData extends ValueBindingObjectImpl implements INamePickerData {
    
    private String server;
    
    public DominoDirectoryNamePickerData() {
    }

    public String getServer() {
        if (null != this.server) {
            return this.server;
        }
        ValueBinding _vb = getValueBinding("server"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setServer(String server) {
        this.server = server;
    }

    
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = super.saveState(context);
        state[1] = server;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.server = (String)state[1]; 
    }

    
    // ===================================================================
    // Name picker implementation
    // ===================================================================

    protected Directory findDirectory() throws NotesException {
        Session session = DominoUtils.getCurrentSession();
        return session.getDirectory();
    }
    
    public boolean hasCapability(int capability) {
        return false;
    }

    public String[] getSourceLabels() {
        try {
            Directory d = findDirectory();
            return new String[]{"Domino Directory"}; // $NON-NLS-1$
        } catch(NotesException ex) {
            throw new FacesExceptionEx(ex,"Error while reading the directory labels"); // $NLX-DominoDirectoryNamePickerData.Errorwhilereadingthedirectorylabe-1$
        }
    }

    public IPickerResult readEntries(IPickerOptions options) {
        return null;
    }
    
    public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames) {
        return null;
    }
}