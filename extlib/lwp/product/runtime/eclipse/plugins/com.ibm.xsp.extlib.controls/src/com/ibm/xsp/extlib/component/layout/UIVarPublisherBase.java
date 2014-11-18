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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 23 Aug 2011
* UIVarPublisherBase.java
*/
package com.ibm.xsp.extlib.component.layout;

import java.io.IOException;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponentBase;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.FacesDataProvider;
import com.ibm.xsp.component.FacesEventWrapper;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;

/**
 * This is not actually a family of controls, just a useful base class for controls 
 * that need to publish some var, as it invokes the {@link #publishControlData(FacesContext)} 
 * and {@link #revokeControlData(List, FacesContext)} methods
 * at all the appropriate times in the JSF lifecycle.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public abstract class UIVarPublisherBase extends UIComponentBase implements FacesComponent{
    private transient List<ShadowedObject> _shadowedData;
    
    public UIVarPublisherBase() {
        super();
    }
    
    /**
     * Available to be overridden in the subclasses to publish other vars, 
     * besides those supported by default when the control implements {@link FacesDataProvider}
     * or one of the other interfaces with implicit supoort in {@link DataPublisher}.
     * @see DataPublisher#publishControlData(javax.faces.component.UIComponent)
     * @param context
     * @return
     */
    protected List<ShadowedObject> publishControlData(FacesContext context) {
        DataPublisher dataPublisher = ((FacesContextEx)context).getDataPublisher();
        return dataPublisher.publishControlData(this);
    }
    /**
     * Available to be overridden in the subclasses to do other revoke and clean-up code.
     * @see DataPublisher#revokeControlData(List, javax.faces.component.UIComponent)
     * @param shadowedData
     * @param context
     */
    protected void revokeControlData(List<ShadowedObject> shadowedData, FacesContext context) {
        DataPublisher dataPublisher = ((FacesContextEx)context).getDataPublisher();
        dataPublisher.revokeControlData(shadowedData, this);
    }
    /**
     * @see DataPublisher#isAllowCreateViewPublish(javax.faces.component.UIComponent)
     * @param context
     * @return
     */
    protected boolean isAllowCreateViewPublish(FacesContext context) {
        return ((FacesContextEx)context).getDataPublisher().isAllowCreateViewPublish(this);
    }
    
    public void initBeforeContents(FacesContext context) throws FacesException {
        // make the published object available to the children
        if( isAllowCreateViewPublish(context) ) {
            _shadowedData = publishControlData(context);
        }
    }

    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // default implementation
        builder.buildAll(context, this, true);
    }

    public void initAfterContents(FacesContext context) throws FacesException {
        if( null != _shadowedData ){
            revokeControlData(_shadowedData, context);
            _shadowedData = null;
        }
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        _shadowedData = publishControlData(context);
        super.encodeBegin(context);
    }
    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);
        revokeControlData(_shadowedData, context);
        _shadowedData = null;
    }

    @Override
    public void processDecodes(FacesContext context) {
        try { 
            _shadowedData = publishControlData(context);
            super.processDecodes(context);
        } finally {
            revokeControlData(_shadowedData, context);
            _shadowedData = null;
        }
    }
    @Override
    public void processUpdates(FacesContext context) {
        try { 
            _shadowedData = publishControlData(context);
            super.processUpdates(context);
        } finally {
            revokeControlData(_shadowedData, context);
            _shadowedData = null;
        }
    }
    @Override
    public void processValidators(FacesContext context) {
        try { 
            _shadowedData = publishControlData(context);
            super.processValidators(context);
        } finally {
            revokeControlData(_shadowedData, context);
            _shadowedData = null;
        }
    }
    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        try { 
            _shadowedData = publishControlData(context);
            return super.invokeOnComponent(context, clientId, callback);
        } finally {
            revokeControlData(_shadowedData, context);
            _shadowedData = null;
        }
    }
    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        try { 
            _shadowedData = publishControlData(context.getFacesContext());
            return super.visitTree(context, callback);
        } finally {
            revokeControlData(_shadowedData, context.getFacesContext());
            _shadowedData = null;
        }
    }
    @Override
    public void queueEvent(FacesEvent event) {
        super.queueEvent(new FacesEventWrapper(this, event));
    }
    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof FacesEventWrapper) {
            FacesEventWrapper wrapper = (FacesEventWrapper) event;
            
            FacesContext context = getFacesContext();
            try {
                _shadowedData = publishControlData(context);
                
                FacesEvent original = wrapper.getFacesEvent();
                original.getComponent().broadcast(original);
            }
            finally {
                revokeControlData(_shadowedData, context);
                _shadowedData = null;
            }
        }
        else { // original event was queued on this control
            super.broadcast(event);
        }
    }    
}
