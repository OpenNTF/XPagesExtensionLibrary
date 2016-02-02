/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.component.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateManager.Options;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateManager.State;
import com.ibm.xsp.extlib.renderkit.html_extended.data.ToggleDetailEvent;
import com.ibm.xsp.extlib.renderkit.html_extended.data.ToggleRowEvent;
import com.ibm.xsp.extlib.renderkit.html_extended.data.ToggleSortColumnEvent;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.page.parse.types.FacesInstance;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;


/**
 * Base class for a {@link UIDataView}
 * <p>
 * This is a base class for data repeat control containing a data source. It renders 
 * like the regular repeat control, but it adds a few behaviors like the management of
 * expanded/collapsed rows, partial processing, as well as an embedded data source.<br>
 * The implementation of a repeat control is complex, but this intention of this class 
 * is to handle all the deep technical details and makes it easy to specialize. 
 * </p>
 */
public class UIDataSourceIterator extends UIDataIterator implements FacesDataIteratorStateHandler, FacesComponent, FacesDataIteratorAjax, ThemeControl {
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.DataSourceIterator"; //$NON-NLS-1$
    
    private DataSource data;
    private Boolean partialExecute;
    private Boolean partialRefresh;
    private String refreshId;
    private Boolean expandedDetail;
    // TODO verify the visibleDetails works correctly when this control is in a repeat iterating over different viewNames
    // there doesn't seem to be any code to handle the fact that this control
    // may be in a repeat that repeats over viewNames, with the data source view name
    // computed to use different values when in different rows of the repeat.
    private HashMap<String, Boolean> visibleDetails;

    // note, no need to keep a _shadowedObjects list and call
    // publish/revokeControlData during the process* methods, as that's already
    // handled in the superclass.
    transient private String _toggledVisibleDetail; // Just for rendering purposes when a position had been toggled
    
    /**
     * This is the key of a value stored in the attributes map and not a clientId suffix,
     * nor is it an actual clientId.  It is used to maintain the currently focussed category
     * link within a View Panel during partial refresh expand/collapse interactions.
     */
    public static final String TOGGLE_ACTION_CLIENT_ID = "__toggleActionClientId__"; //$NON-NLS-1$
    
    public UIDataSourceIterator() {
    }
    
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DATAITERATOR;
    }
    
    @Override
    public void _xspCleanTransientData() {
        super._xspCleanTransientData();
        _toggledVisibleDetail = null;
    }
    
    /**
     * A component can implement FacesDefinitionClass to instruct the compiler
     * to use a different class. We stop it here the Data Iterator implementation.
     * A basic data iterator uses this to either crate the component that 
     * creates the controls at load time, or iterate them dynamically at runtime. 
     */
    @Override
    public Class<? extends UIComponent> getJavaClass(FacesInstance instance) {
        return getClass();
    }

    public boolean isPartialExecute() {
        if(partialExecute!=null) {
            return partialExecute;
        }
        ValueBinding vb = getValueBinding("partialExecute"); //$NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setPartialExecute(boolean partialExecute) {
        this.partialExecute = partialExecute;
    }

    public boolean isPartialRefresh() {
        if(partialRefresh!=null) {
            return partialRefresh;
        }
        ValueBinding vb = getValueBinding("partialRefresh"); //$NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setPartialRefresh(boolean partialRefresh) {
        this.partialRefresh = partialRefresh;
    }

    public String getRefreshId() {
        if(refreshId!=null) {
            return refreshId;
        }
        ValueBinding vb = getValueBinding("refreshId"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setRefreshId(String partialRefreshId) {
        this.refreshId = partialRefreshId;
    }

    /**
     * returns the {@link com.ibm.xsp.model.DataSource} for this control.
     * @return
     */
    public DataSource getData() {
        return data;
    }

    /**
     * Sets the {@link com.ibm.xsp.model.DataSource} for this control.
     * @param data
     */
    public void setData(DataSource data) {
        this.data = data;
        if (data instanceof ComponentBindingObject) {
            ((ComponentBindingObject)data).setComponent(this);
        }
    }

    @Override
    public DataSource getDataSource() {
        DataSource ds = getData();
        if (ds != null) {
            return ds;
        }
        return super.getDataSource();
    }
    public boolean isExpandedDetail() {
        if(expandedDetail!=null) {
            return expandedDetail;
        }
        ValueBinding vb = getValueBinding("expandedDetail"); //$NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    
    public void setExpandedDetail(boolean detailExpanded) {
        this.expandedDetail = detailExpanded;
        visibleDetails = null;
    }

    public void showAll() {
        setExpandedDetail(true);
    }
    
    public boolean isShowAll() {
        return isExpandedDetail() && (visibleDetails==null || visibleDetails.isEmpty());
    }
    
    public void hideAll() {
        setExpandedDetail(false);
    }
    
    public boolean isHideAll() {
        return !isExpandedDetail() && (visibleDetails==null || visibleDetails.isEmpty());
    }
    

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        this.partialExecute = (Boolean)values[1];
        this.partialRefresh = (Boolean)values[2];
        this.refreshId = (String)values[3];
        this.data = (DataSource) FacesUtil.objectFromSerializable(context, this, values[4]);
        this.visibleDetails = (HashMap<String, Boolean>)values[5];
        this.expandedDetail = (Boolean)values[6];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[7];
        values[0] = super.saveState(context);
        values[1] = partialExecute;
        values[2] = partialRefresh;
        values[3] = refreshId;
        values[4] = FacesUtil.objectToSerializable(context, data);
        values[5] = visibleDetails;
        values[6] = expandedDetail;
        return values;
    }

    
    // ===================================================================
    // Save/restore the user state
    // ===================================================================
    
    protected static class UserState extends FacesDataIteratorStateManager.BasicState {
        private static final long serialVersionUID = 1L;

        private HashMap<String, Boolean> visibleDetails;
        
        public UserState(Options options) {
            super(options);
        }

        public HashMap<String, Boolean> getVisibleDetails() {
            return visibleDetails;
        }

        public void setVisibleDetails(HashMap<String, Boolean> visibleDetails) {
            this.visibleDetails = visibleDetails;
        }

        @Override
        public void restoreState(FacesContext context, FacesDataIterator dataIterator, boolean fullState) {
            super.restoreState(context, dataIterator, fullState);
            if(fullState) {
                UIDataSourceIterator s = (UIDataSourceIterator)dataIterator;
                s.visibleDetails = getVisibleDetails();
            }
        }

        @Override
        public void saveState(FacesContext context, FacesDataIterator dataIterator) {
            super.saveState(context, dataIterator);
            UIDataSourceIterator s = (UIDataSourceIterator)dataIterator;
            setVisibleDetails(s.visibleDetails);
        }
        
    }

    public FacesDataIterator getFacesDataIterator(FacesContext context) {
        return this;
    }
    
    public State createDataIteratorState(FacesContext context, Options options) {
        return new UserState(options);
    }

    public Options getOptions() {
        return null;
    }

    
    // ===================================================================
    // Manage show/hide details
    // ===================================================================

    public boolean isDetailVisible(String position) {
        if(isExpandedDetail()) {
            return visibleDetails==null || !visibleDetails.containsKey(position); 
        } else {
            return visibleDetails!=null && visibleDetails.containsKey(position);
        }
    }

    public boolean isDetailVisible(String position, boolean expandedDetails) {
        if(expandedDetails) {
            return visibleDetails==null || !visibleDetails.containsKey(position); 
        } else {
            return visibleDetails!=null && visibleDetails.containsKey(position);
        }
    }

    public void setDetailVisible(String position, boolean visible) {
        if(isExpandedDetail()) {
            if(visible) {
                removePosition(position);
            } else {
                putPosition(position);
            }
        } else {
            if(visible) {
                putPosition(position);
            } else {
                removePosition(position);
            }
        }
    }
    private void removePosition(String position) {
        if(visibleDetails!=null) {
            visibleDetails.remove(position);
            if(visibleDetails.isEmpty()) {
                visibleDetails = null;
            }
        }
    }
    private void putPosition(String position) {
        if(visibleDetails==null) {
            visibleDetails = new HashMap<String, Boolean>();
        }
        visibleDetails.put(position,Boolean.TRUE);
    }
    public void clearDetailVisiblePositions(){
        visibleDetails = null;
    }
    
    
    public void toggleDetailVisible(String position) {
        setDetailVisible(position, !isDetailVisible(position));
    }
    
    public String getToggledVisibleDetail() {
        return _toggledVisibleDetail;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.data.FacesDataIteratorAjax#getAjaxContainerClientId(javax.faces.context.FacesContext)
     */
    public String getAjaxContainerClientId(FacesContext context) {
        throw new UnsupportedOperationException("This method must be overridden in a subclass."); // $NLX-UIDataSourceIterator.Thismethodmustbeoverriddeninasubc-1$
    }

    
    // ===================================================================
    // Faces Component Methods
    // ===================================================================

    public void initBeforeContents(FacesContext context) throws FacesException {
        // Nothing
    }

    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Standard stuff
        builder.buildAll(context, this, true);
    }

    public void initAfterContents(FacesContext context) throws FacesException {
        //Do nothing, the default implementation
    }
    
    
    // ===================================================================
    // Events and life cycle management.
    //   1- Manages the iterator specific events
    //   2- Manages the embedded data source
    // ===================================================================

    @Override
    public void queueEvent(FacesEvent event) {
        if ((event instanceof ToggleRowEvent) || (event instanceof ToggleDetailEvent)) {
            if (isPartialExecute()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
            // We don't need to wrap it as this is just a command action to the table
            super.queueEvent(event);
        } else {
//            event = new FacesEventWrapper(this, event);
            super.queueEvent(event);
        }
    }
    /* (non-Javadoc)
     * @see com.ibm.xsp.component.UIDataIterator#publishControlData(javax.faces.context.FacesContext)
     */
    @Override
    protected List<ShadowedObject> publishControlData(FacesContext context) {
        List<ShadowedObject> shadowed = super.publishControlData(context);
        DataSource dataSource = getDataSource();
        if( null != dataSource ){
            // make the data source "var" available to computed values in the
            // contained children and facet controls.
            DataPublisher publisher = ((FacesContextEx)context).getDataPublisher();
            List<ShadowedObject> extraShadowed = publisher.pushDataSource(this, dataSource);
            if( null != shadowed ){
                shadowed.addAll( extraShadowed );
            }else{
                shadowed = extraShadowed;
            }
        }
        return shadowed;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.component.UIDataIterator#revokeControlData(java.util.List, javax.faces.context.FacesContext)
     */
    @Override
    protected void revokeControlData(List<ShadowedObject> shadowedObjects,
            FacesContext context) {
        
        DataSource dataSource = getDataSource();
        if( null != dataSource ){
            // revoke the "var" value so outer variables with the same variable
            // name are restored.
            DataPublisher publisher = ((FacesContextEx)context).getDataPublisher();
            publisher.popDataSource(shadowedObjects, this, dataSource);
        }
        
        super.revokeControlData(shadowedObjects, context);
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        
        if (event instanceof ToggleRowEvent) {
            ToggleRowEvent ev = (ToggleRowEvent)event;
            DataModel dataModel = getDataModel();
            
            //>tmg:a11y
            FacesContext context = getFacesContext();
            //<tmg:a11y
            
            if(dataModel instanceof TabularDataModel){
                TabularDataModel model = (TabularDataModel)dataModel;
                if (ev.isExpand()) {
                    model.expandRow(ev.getPosition());
                } else {
                    model.collapseRow(ev.getPosition());
                }

                //>tmg:a11y
                HtmlUtil.storeEncodeParameter(context, this, TOGGLE_ACTION_CLIENT_ID, ev.getClientId());
                //<tmg:a11y
            }
            
            // Tell JSF to switch to render response, like regular commands
            context.renderResponse();
        } else if (event instanceof ToggleDetailEvent) {
            ToggleDetailEvent ev = (ToggleDetailEvent)event;
            
            //>tmg:a11y
            FacesContext context = getFacesContext();
            //<tmg:a11y
            
            String[] pos = ev.getTogglePositions();
            for(int i=0; i<pos.length; i++) {
                if(StringUtil.isNotEmpty(pos[i])) {
                    toggleDetailVisible(pos[i]);
                    _toggledVisibleDetail = pos[i];
                }
            }

            //>tmg:a11y
            HtmlUtil.storeEncodeParameter(context, this, TOGGLE_ACTION_CLIENT_ID, ev.getClientId());
            //<tmg:a11y
            
            // Tell JSF to switch to render response, like regular commands
            context.renderResponse();

        } else if (event instanceof ToggleSortColumnEvent) {
            ToggleSortColumnEvent ev = (ToggleSortColumnEvent)event;
            DataModel dm = getDataModel();
        	
            //>tmg:a11y
            FacesContext context = getFacesContext();
            //<tmg:a11y
            
            if(dm instanceof TabularDataModel) {
                TabularDataModel tbm = (TabularDataModel)dm;
                
                String previouslySortedColumn = tbm.getResortColumn();
                if( StringUtil.isNotEmpty(previouslySortedColumn) 
                        && !StringUtil.equals(ev.getColumnName(), previouslySortedColumn) ) {
                    // SPR# BGLN85FFWC sort state only maintained on current column
                    tbm.resetResortState( previouslySortedColumn );
                }
               
                if (tbm.getResortType(ev.getColumnName()) == TabularDataModel.RESORT_ASCENDING)
                    tbm.setResortOrder(ev.getColumnName(), TabularDataModel.SORT_ASCENDING);
                else if (tbm.getResortType(ev.getColumnName()) == TabularDataModel.RESORT_DESCENDING) 
                    tbm.setResortOrder(ev.getColumnName(), TabularDataModel.SORT_DESCENDING);   
                else
                    tbm.setResortOrder(ev.getColumnName(), TabularDataModel.SORT_TOGGLE);

                //>tmg:a11y
                HtmlUtil.storeEncodeParameter(context, this, TOGGLE_ACTION_CLIENT_ID, ev.getClientId());
                //<tmg:a11y
            }
            
            // Tell JSF to switch to render response, like regular commands
            context.renderResponse();
        } else {
            super.broadcast(event);
        }
    }
    
    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);
        _toggledVisibleDetail = null; // Reset, as it had been rendered...
    }
}