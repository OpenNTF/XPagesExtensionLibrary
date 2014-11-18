/*
 * © Copyright IBM Corp. 2010, 2012
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
package com.ibm.xsp.extlib.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.actions.AbstractDocumentAction;
import com.ibm.xsp.actions.document.DocumentAdapter;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.exception.EvaluationExceptionEx;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.util.FacesUtil;

public class MoveToAction extends AbstractDocumentAction {

    private boolean _forceFullRefresh;
    private boolean _forceFullRefreshSet;
    private boolean _saveDocument;
    private boolean _saveDocumentSet;
    private String _targetPage; 
    private String _transitionType;
    private String _direction;
    
    public MoveToAction() {
        super();
    }
    
    public void setForceFullRefresh(boolean forceFullRefresh) {
        this._forceFullRefresh = forceFullRefresh;
        this._forceFullRefreshSet = true;
    }
    //Look at componentsetmodeaction for how to do booleans correctly yay
    public boolean isForceFullRefresh() {
        if(this._forceFullRefreshSet){
            return this._forceFullRefresh;    
        }
        ValueBinding _vb = getValueBinding("forceFullRefresh"); //$NON-NLS-1$
        if (_vb != null) {
            Object _result = _vb.getValue(getFacesContext());
            if (_result == null) {
                return false;
            }
            else {
                return ((Boolean) _result).booleanValue();
            }
        }
        return false;
    }

    public void setSaveDocument(boolean _saveDocument) {
        this._saveDocument = _saveDocument;
        this._saveDocumentSet = true;
    }

    public boolean isSaveDocument() {
        if(this._saveDocumentSet){
            return this._saveDocument;    
        }
        ValueBinding _vb = getValueBinding("saveDocument"); //$NON-NLS-1$
        if (_vb != null) {
            Object _result = _vb.getValue(getFacesContext());
            if (_result == null) {
                return false;
            }
            else {
                return ((Boolean) _result).booleanValue();
            }
        }
        return false;
    }

    public void setTargetPage(String targetPage) {
        this._targetPage = targetPage;
    }

    public String getTargetPage() {
            if (_targetPage == null) {
                ValueBinding vb = getValueBinding("targetPage"); //$NON-NLS-1$
                if (vb != null) {
                    return (String)vb.getValue(FacesContext.getCurrentInstance());
                }
            }
            return _targetPage;
    }
    
    public void setTransitionType(String transitionType) {
        this._transitionType = transitionType;
    }

    public String getTransitionType() {
            if (_transitionType == null) {
                ValueBinding vb = getValueBinding("transitionType"); //$NON-NLS-1$
                if (vb != null) {
                    return (String)vb.getValue(FacesContext.getCurrentInstance());
                }
            }
            return _transitionType;
    }
    
    public void setDirection(String direction) {
        this._direction = direction;
    }
    
    public String getDirection() {
        if (_direction == null) {
            ValueBinding vb = getValueBinding("direction"); //$NON-NLS-1$
            if (vb != null) {
                return (String)vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return _direction;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[8];
        state[0] = super.saveState(context);
        state[1] = _saveDocument;
        state[2] = _forceFullRefresh;
        state[3] = _targetPage;
        state[4] = _saveDocumentSet;
        state[5] = _forceFullRefreshSet;
        state[6] = _transitionType;
        state[7] = _direction;
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _saveDocument = (Boolean)values[1];
        _forceFullRefresh = (Boolean)values[2];
        _targetPage = (String)values[3];
        _saveDocumentSet = (Boolean)values[4];
        _forceFullRefreshSet = (Boolean)values[5];
        _transitionType = (String)values[6];
        _direction = (String)values[7];
    }
    
    @Override
    public Class<String> getType(FacesContext context) throws MethodNotFoundException {
        // the Button and EventHandler "action" properties
        // have a return type of String (the optional pageName to transition to)
        return String.class;
    }

    @Override
    public Object invoke(FacesContext context, Object[] params)
            throws EvaluationException, MethodNotFoundException {
        if ( isSaveDocument() ) {
            try {
                // Try to first save from the data source
                // This allows datasource events to be processed
                // Else, just execute a regular save of the document
                DataSource ds = getDocumentDataSource(context);
                if(ds!=null) {
                    ds.save(context, false);
                } else {
                    // document adapter allows multiple document types to be supported
                    DocumentAdapter documentAdapter = getDocumentAdapter(context);
                    documentAdapter.save(context, getDocument(context));
                }
            } catch (Exception e) {
                throw new EvaluationExceptionEx("Error while saving document",e,this); // $NLX-MoveToAction.Errorwhilesavingdocument-1$
            }
        }

        FacesContextEx ctx = (FacesContextEx)context;
        
        UIMobilePage parentMobilePage = null;
        //this should the the ID of the mobile page the action is performed on
        String partialExecuteAreaClientId = ctx.getPartialRefreshId();
        if( StringUtil.isNotEmpty(partialExecuteAreaClientId) ){
            String partialExecuteAreaControlId = extractControlId(partialExecuteAreaClientId);
            if ( StringUtil.isNotEmpty(partialExecuteAreaControlId)){
                // Searching relative to the XPage root control, 
                // not relative to the control, which may be in a custom control.
                UIComponent partialExecuteControl = FacesUtil.getComponentFor(
                        context.getViewRoot(),
                        partialExecuteAreaControlId);
                if( partialExecuteControl instanceof UIMobilePage) {
                    parentMobilePage = (UIMobilePage) partialExecuteControl;
                }
            }
        }
        if( null == parentMobilePage ){
            // the partial execute area is not a mobile page control.
            // TODO logging
            return null;
        }
        
        
        String targetPage = getTargetPage();
        
        int dirInt;
        String direction=getDirection();
        dirInt = ("Right to Left".equals(direction) || "rtl".equals(direction) || "-1".equals(direction)) ? -1 : 1; // $NON-NLS-1$ //$NON-NLS-2$
    
        String transitionAnimation = getTransitionType();
        if( null == transitionAnimation ){
            // SPR#MKEE92GMW5
            // just immediately move to the next appPage
            // with no transition animation.
            transitionAnimation = "none"; // $NON-NLS-1$
        }
        
        Map<String,Object> actionParams = new HashMap<String,Object>();
        actionParams.put("resetContent", isForceFullRefresh()); // $NON-NLS-1$
        
        // register an action, to be output by the MobilePageContentRenderer
        parentMobilePage.createMoveToAction(targetPage, dirInt, transitionAnimation, actionParams);
        
        // null: no XPage redirect to a different .xsp
        return null;
    }
    
    
    private String extractControlId( String clientId ) {
        //TODO: This is still not exactly right. No guarantee that this is actually the ID we are looking for
        Pattern p = Pattern.compile("(?:(?!.*:.*)[a-zA-Z][a-zA-Z0-9]+)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // $NON-NLS-1$
        Matcher m = p.matcher(clientId);
        if (m.find()){
            return m.group();
        }
        return null;
    }

}