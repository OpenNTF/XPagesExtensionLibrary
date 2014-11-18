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

package com.ibm.xsp.extlib.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import org.w3c.dom.Document;

import com.ibm.commons.xml.XMLException;
import com.ibm.commons.xml.io.XmlSerializer;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.model.AbstractDataContainer;
import com.ibm.xsp.model.AbstractDataSource;
import com.ibm.xsp.model.DataContainer;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Data source that use an object.
 * @author Philippe Riand
 */
public class ObjectDataSource extends AbstractDataSource {

    protected static class Container extends AbstractDataContainer {

        private Object object;
        
        public Container() { // Serialization ctor
        }
        public Container(String beanId, String id, Object object) {
            super(beanId,id);
            this.object = object;
        }
        public Object getObject() {
            return object;
        }
        public void serialize(ObjectOutput out) throws IOException {
            if(object instanceof Document) {
                out.writeByte(1);
                try {
                    XmlSerializer.writeDOMObject(out,(Document)object);
                } catch(XMLException e) {
                    IOException io = new IOException();
                    io.initCause(e);
                    throw io;
                }
            } else {
                out.writeByte(0);
                out.writeObject(object);
            }
        }
        public void deserialize(ObjectInput in) throws IOException {
            try {
                byte b = in.readByte();
                switch(b) {
                    case 0: {
                        object = in.readObject();
                    } break;
                    case 1: {
                        try {
                            object = XmlSerializer.readDOMObject(in);
                        } catch(XMLException e) {
                            IOException io = new IOException();
                            io.initCause(e);
                            throw io;
                        }
                    } break;
                }
            } catch (ClassNotFoundException xe) {
                IOException ioe = new IOException("Error while deserializing object"); // $NLX-ObjectDataSource.Errorwhiledeserializingobject-1$
                ioe.initCause(xe);
                throw ioe;
            }
        }
    }

    private Boolean _readonly;
    private MethodBinding _createObject;
    private MethodBinding _saveObject;
    
    public ObjectDataSource() {
    }
    
    @Override
    public boolean isReadonly() {
        if (null != this._readonly) {
            return this._readonly;
        }
        ValueBinding _vb = getValueBinding("readonly"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setReadonly(boolean readonly) {
        this._readonly = readonly;
    }

    public MethodBinding getCreateObject() {
        return _createObject;
    }
    
    public void setCreateObject(MethodBinding binding) {
        _createObject = binding;
    }
    
    public MethodBinding getSaveObject() {
        return _saveObject;
    }

    public void setSaveObject(MethodBinding binding) {
        _saveObject = binding;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = _readonly;
        state[2] = StateHolderUtil.saveMethodBinding(context, _createObject);
        state[3] = StateHolderUtil.saveMethodBinding(context, _saveObject);
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        _readonly = (Boolean)values[1];
        _createObject = StateHolderUtil.restoreMethodBinding(context, getComponent(), values[2]);
        _saveObject = StateHolderUtil.restoreMethodBinding(context, getComponent(), values[3]);
    }

    
    @Override
    protected String composeUniqueId() {
        return getClass().getName();
    }
    
    @Override
    public Object getDataObject() {
        Container ac = (Container)getDataContainer();
        if(ac!=null) {
            return ac.getObject();
        }
        return null;
    }
    
    @Override
    public void readRequestParams(FacesContext context,Map<String, Object> requestMap) {
    }
    
    @Override
    public DataContainer load(FacesContext context) throws IOException {
        Object obj = null;
        MethodBinding createObject = getCreateObject();
        if (createObject != null) {
            obj = createObject.invoke(context, null);
        }
        return new Container(getBeanId(), getUniqueId(), obj);
    }
    
    @Override
    public void refresh() {
        // instead of delegate to superclass, copy template in
        // com.ibm.xsp.extlib.model.DataAccessorSource.refresh()
        // to do a reduced refresh, that clears
        // the current value but doesn't re-load.
        FacesContext context = getFacesContext();
        if (context == null)
            return;

        // clear the current value
        putDataContainer(context, null);
    }
    
    @Override
    public boolean save(FacesContext context, DataContainer data) throws FacesExceptionEx {
        // invoke the query save method binding
        Object object = ((Container)data).getObject();
        if (_saveObject != null) {
            Object[] params = null;
            if(_saveObject instanceof MethodBindingEx){
                params = new Object[] { object };
                ((MethodBindingEx)_saveObject).setComponent(getComponent());
                ((MethodBindingEx)_saveObject).setParamNames(s_saveObjectParamNames);
            }
            if (FacesUtil.isCancelled(_saveObject.invoke(context, params))) {
                return false;
            }
            return true;
        }
        
        throw new FacesExceptionEx(null,"The save method has not been implemented in the data source"); // $NLX-ObjectDataSource.Missingsavemethodtothedatasource-1$
    }
    private static final String[] s_saveObjectParamNames = { "value" }; // $NON-NLS-1$
    
}