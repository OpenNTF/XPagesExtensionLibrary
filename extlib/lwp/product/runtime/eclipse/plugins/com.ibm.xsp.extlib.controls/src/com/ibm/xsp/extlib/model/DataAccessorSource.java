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
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.FacesContextListener;
import com.ibm.xsp.model.AbstractDataContainer;
import com.ibm.xsp.model.AbstractDataSource;
import com.ibm.xsp.model.DataContainer;
import com.ibm.xsp.model.ModelDataSource;

/**
 * Generic data source that handle data coming from a data accessor.
 * @author Philippe Riand
 */
public abstract class DataAccessorSource extends AbstractDataSource implements ModelDataSource {

    public static class Container extends AbstractDataContainer {

        private DataAccessor dataAccessor;
        private boolean clearOnRendering;

        // FacesContextListener to discard the domino resources at the end of the request
        private transient FacesContextListener _contextListener;
        private int generationId;
        
        public Container() { // Serialization ctor
        }
        public Container(String beanId, String id, DataAccessor dataAccessor, boolean clearOnRendering) {
            super(beanId,id);
            this.dataAccessor = dataAccessor;
            this.clearOnRendering = clearOnRendering;
        }
        public int getGenerationId() {
            return generationId;
        }
        public DataAccessorSource getDataSource() {
            return dataAccessor.getDataSource();
        }
        public void setDataSource(DataAccessorSource dataSource) {
            this.dataAccessor.setDataSource(dataSource);
        }
        public DataAccessor getDataAccessor() {
            return dataAccessor;
        }
        public void serialize(ObjectOutput out) throws IOException {
            out.writeObject(dataAccessor);
            out.writeInt(generationId);
            out.writeBoolean(clearOnRendering);
        }
        public void deserialize(ObjectInput in) throws IOException {
            try {
                dataAccessor = (DataAccessor)in.readObject();
                generationId = in.readInt();
                clearOnRendering = in.readBoolean();
            } catch (ClassNotFoundException xe) {
                IOException ioe = new IOException("Error while deserializing object"); // $NLX-DataAccessorSource.Errorwhiledeserializingobject-1$
                ioe.initCause(xe);
                throw ioe;
            }
        }
        public void installFacesListener() {
            if(_contextListener==null) {
                FacesContextEx context = FacesContextEx.getCurrentInstance(); 
                _contextListener = new FacesContextListener() {
                    public void beforeContextReleased(FacesContext facesContext) {
                        _contextListener = null;
                    }
                    public void beforeRenderingPhase(FacesContext facesContext) {
                        generationId = generationIdCounter++;
                        // If, for any reason, the view had been read during the previous phases
                        // then we have to refresh it. This prevents some "Entry in index not found"
                        // when document are added/deleted during the POST phases 
                        // This flag is set by readEntries when the data are read
                        if(clearOnRendering) {
                            // Keep the count cache
                            dataAccessor.clearData(false);
                        } else {
                            dataAccessor.updateCount();
                        }
                    }
                };
                
                // In case of a POST message with a partial execution ID on another control, then
                // the context is not registered *before* the phase starts
                if(context.isRenderingPhase()) {
                    generationId = generationIdCounter++;
                    if(clearOnRendering) {
                        // Keep the count cache
                        dataAccessor.clearData(false);
                    } else {
                        dataAccessor.updateCount();
                    }
                }
                
                context.addRequestListener(_contextListener);
            }
        }
    }
    private static int generationIdCounter;

    private Boolean clearOnRendering;
    private String cacheSuffix;
    
    protected DataAccessorSource() {
    }

    public boolean isClearOnRendering() {
        if (null != this.clearOnRendering) {
            return this.clearOnRendering;
        }
        ValueBinding vb = getValueBinding("clearOnRendering"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setClearOnRendering(boolean clearOnRendering) {
        this.clearOnRendering = clearOnRendering;
    }
    
    public String getCacheSuffix() {
        if (null != cacheSuffix) {
            return cacheSuffix;
        }
        ValueBinding valueBinding = getValueBinding("cacheSuffix"); //$NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    
    public void setCacheSuffix(String cacheSuffix) {
        this.cacheSuffix = cacheSuffix;
    }
    
    
    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = clearOnRendering;
        state[2] = cacheSuffix;
        return state;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        clearOnRendering = (Boolean)values[1];
        cacheSuffix = (String)values[2];
    }

    /**
     * This looks like a bug in XPages core where the data container is instantly recreated.
     * Then, when refresh() is called from the action phase, then the DataContainer is instantly
     * read with the wrong ids, as it is not yet refreshed because the page is rendered again.
     * The fix is done in this data source to be sure that nothing breaks in XPages core.
     */
    @Override
    public void refresh() {
        FacesContext context = getFacesContext();
        if (context == null)
            return;

        // clear the current value
        putDataContainer(context, null);
    }
    
    protected abstract DataAccessor createAccessor();
    
//    @Override
//    protected String composeUniqueId() {
//        // This is the best we can do here
//        return getClass().getName();
//    }
    
    @Override
    protected String composeUniqueId() {
        // For components containing multiple datasources (view, panel...), we distinguish
        // the data sources by adding the position to the unique id
        // For other components containing a single data source (view panel..) then we 
        // dont't care about this.
        if(isDataShared()) {
            StringBuilder b = new StringBuilder();
            b.append(getClass().getName());
            String suffix = getCacheSuffix();
            if(StringUtil.isNotEmpty(suffix)) {
                b.append('_');
                b.append(suffix);
            } else {
                appendDefaultSharedCacheSuffix(b);
            }
            return b.toString();
        } else {
            // This is the best we can do here
            return getClass().getName();
        }
    }
    protected void appendDefaultSharedCacheSuffix(StringBuilder b) {
        //throw new FacesExceptionEx(null,"A cache suffix must be provided for a data source of type {0} using scope {1}",getClass().getName(),scope);
    }
     
    @Override
    public DataAccessor getDataObject() {
        Container ac = (Container)getDataContainer();
        if(ac!=null) {
            return ac.getDataAccessor();
        }
        return null;
    }
    
    public DataModel getDataModel() {
        return new DataAccessorModel(this,(Container)getDataContainer());
    }
    
    @Override
    public void readRequestParams(FacesContext context,Map<String, Object> requestMap) {
    }
    
    @Override
    public DataContainer load(FacesContext context) throws IOException {
        DataAccessor ac = createAccessor();
        return new Container(getBeanId(), getUniqueId(),ac, isClearOnRendering());
    }
    
    @Override
    public Container getDataContainer(FacesContext context) {
        Container c = (Container)super.getDataContainer(context);
        if(c!=null) {
            c.setDataSource(this);
        }
        return c;
    }
    
    @Override
    public boolean isReadonly() {
        return true;
    }
    @Override
    public boolean save(FacesContext context, DataContainer data) throws FacesExceptionEx {
        return false;
    }
}