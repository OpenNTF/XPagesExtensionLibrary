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

package com.ibm.xsp.extlib.component.listview;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.builder.ControlBuilder.IControl;
import com.ibm.xsp.extlib.component.domino.UIViewComponent;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ColumnDef;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ViewDef;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ViewFactory;
import com.ibm.xsp.extlib.component.rest.DominoViewService;
import com.ibm.xsp.extlib.component.rest.IRestService;
import com.ibm.xsp.extlib.component.rest.UIRestService;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.ManagedBeanUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author akosugi
 *
 *        ui component handler for notes list view control
 */
public class UIListView extends UIViewComponent {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.listview.ListView"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.listview.ListView"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.listview.ListView"; //$NON-NLS-1$
    // PHIL:
    // Customization bean
    public static abstract class Customizer {
        public ViewFactory getViewFactory() {
            return null;
        }
        public boolean createColumns(FacesContext context, UIListView panel, ViewFactory f) {
            return false;
        }
        public IControl createColumn(FacesContext context, UIListView panel, int index, ColumnDef colDef) {
            return null;
        }
        public void afterCreateColumn(FacesContext context, int index, ColumnDef colDef, IControl column) {
        }
        public void afterCreateColumns(FacesContext context, UIListView panel) {
        }
    }

    private String structureComponentId;
    // dojoEvents is unused, and not serialized.
//  private ArrayList<DojoEvent> dojoEvents;
    private String onSortChanged;
    private Boolean hideColumns; // MWD breaking change 10/05/11 - this prop hides ALL child columns 
    private Boolean alternateRows;
    private Boolean canBeNarrowMode;
    private Boolean showColumnNameForEmptyTitle; // formerly 'showColumnName4EmptyTitle' => breaking change on 10/03/11
    private String onCellClick;
    private String onCellDblClick;
    private Boolean dynamicView;
    private String currentView;
    private String customizerBean;

    public UIListView() {
        setRendererType(RENDERER_TYPE);
//      dojoEvents = new ArrayList<DojoEvent>();
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    public String getCustomizerBean() {
       if (customizerBean == null) {
            ValueBinding vb = getValueBinding("customizerBean"); //$NON-NLS-1$
            if (vb != null) {
                return (String)vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return customizerBean;
    }

    public void setCustomizerBean(String customizerBean) {
        this.customizerBean = customizerBean;
    }



//  public ArrayList<DojoEvent> getDojoEvents() {
//      return this.dojoEvents;
//  }
//
//  public void addDojoEvent(DojoEvent dojoEvent) {
//      dojoEvents.add(dojoEvent);
//  }
    
    public String getStructureComponentId() {
        if (structureComponentId != null)
            return structureComponentId;
        ValueBinding _vb = getValueBinding("structureComponentId"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setStructureComponentId(String structure) {
        this.structureComponentId = structure;
    }

    public String getOnSortChanged() {
        if (onSortChanged != null)
            return onSortChanged;
        ValueBinding _vb = getValueBinding("onSortChanged"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnSortChanged(String sortChangedAction) {
        this.onSortChanged = sortChangedAction;
    }

    public String getOnCellClick() {
        if (null != this.onCellClick) {
            return this.onCellClick;
        }
        ValueBinding _vb = getValueBinding("onCellClick"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setOnCellClick(String onCellClick) {
        this.onCellClick = onCellClick;
    }

    public String getOnCellDblClick() {
        if (null != this.onCellDblClick) {
            return this.onCellDblClick;
        }
        ValueBinding _vb = getValueBinding("onCellDblClick"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setOnCellDblClick(String onCellDblClick) {
        this.onCellDblClick = onCellDblClick;
    }

    public boolean isHideColumns() {
        if (hideColumns != null)
            return hideColumns;
        ValueBinding _vb = getValueBinding("hideColumns"); // $NON-NLS-1$
        if (_vb != null){
            Object val = _vb.getValue(getFacesContext());
            if( val instanceof Boolean ){
                return (Boolean) val;
            }
        }
        return false;
    }

    public void setHideColumns(boolean hideColumns) {
        this.hideColumns = hideColumns;
    }

    public boolean isAlternateRows() {
        if (null != this.alternateRows) {
            return this.alternateRows;
        }
        ValueBinding _vb = getValueBinding("alternateRows"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setAlternateRows(boolean alternateRows) {
        this.alternateRows = alternateRows;
    }
    public boolean isCanBeNarrowMode() {
        if (null != this.canBeNarrowMode) {
            return this.canBeNarrowMode;
        }
        ValueBinding _vb = getValueBinding("canBeNarrowMode"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setCanBeNarrowMode(boolean canBeNarrowMode) {
        this.canBeNarrowMode = canBeNarrowMode;
    }

    // MWD Commenting this out -- see if anybody cares
    // TODO Remove completely after beta and before IFR1 if no feedback
//    /**
//     * @deprecated Use {@link #isShowColumnName4EmptyTitle()} instead
//     */
//    public Boolean getShowColumnName4EmptyTitle() {
//        return isShowColumnName4EmptyTitle();
//    }

    public boolean isShowColumnNameForEmptyTitle() {
        if (null != this.showColumnNameForEmptyTitle) {
            return this.showColumnNameForEmptyTitle;
        }
        ValueBinding vb = getValueBinding("showColumnNameForEmptyTitle"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }


    // MWD  Commenting this out -- see if anybody cares 
    // TODO Remove completelt after beta and before IFR1 if no feedback
//    /**
//     * @deprecated use {@link #setShowColumnName4EmptyTitle(boolean)} instead.
//     */
//    public void setShowColumnName4EmptyTitle(Boolean showColumnName4EmptyTitle) {
//        if( null != showColumnName4EmptyTitle ){
//            setShowColumnName4EmptyTitle(showColumnName4EmptyTitle.booleanValue());
//        }
//    }
    public void setShowColumnNameForEmptyTitle(boolean showColumnNameForEmptyTitle) {
        this.showColumnNameForEmptyTitle = showColumnNameForEmptyTitle;
    }


    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.domino.UIViewComponent#getJsId()
     */
    @Override
    public String getJsId() {
        return super.getJsId();
    }

    @Override
    public void writeActionHandlerScripts(ResponseWriter writer)
            throws IOException {
        super.writeActionHandlerScripts(writer);
        String value = this.getOnSortChanged();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "sortChangedAction", "col", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        value = this.getOnCellClick();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "onCellClick", "ev,ext", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        value = this.getOnCellDblClick();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "onCellDblClick", "ev,ext", value); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[12];
        values[0] = super.saveState(context);
        values[1] = structureComponentId;
        values[2] = onSortChanged;
        values[3] = hideColumns;
        values[4] = alternateRows;
        values[5] = canBeNarrowMode;
        values[6] = dynamicView;
        values[7] = currentView;
        values[8] = customizerBean;
        values[9] = showColumnNameForEmptyTitle;
        values[10] = onCellClick;
        values[11] = onCellDblClick;
        return values;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.structureComponentId = (String) values[1];
        this.onSortChanged = (String) values[2];
        this.hideColumns = (Boolean) values[3];
        this.alternateRows = (Boolean) values[4];
        this.canBeNarrowMode = (Boolean) values[5];
        this.dynamicView = (Boolean) values[6];
        this.currentView = (String) values[7];
        this.customizerBean = (String) values[8];
        this.showColumnNameForEmptyTitle = (Boolean) values[9];
        this.onCellClick = (String) values[10];
        this.onCellDblClick = (String) values[11];
    }


    // ====================================================================
    //  Dynamic Columns Management
    // ====================================================================

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if(dynamicView==null) {
            dynamicView = isDynamicView();
        }
        if(dynamicView) {
            updateColumns(context);
        }
        super.encodeBegin(context);
    }


    protected boolean isDynamicView() {
        // If at least one column had been added, the we consider that the view is not dynamic
        if(getChildCount()>0) {
            List<UIComponent> children = TypedUtil.getChildren(this);
            for(UIComponent c: children) {
                if(c instanceof UIListViewColumn) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void updateColumns(FacesContext context) {
        try {
            String viewKey=findViewKey(context);
            if(!StringUtil.equals(viewKey, currentView)) {
                Customizer bean = loadCustomizationBean(context);
                clearColumns(context,bean);
                ViewFactory f = getViewFactory(context,bean);
                if(f!=null) {
                    createColumns(context,bean,f,ViewDesign.loadView(viewKey));
                }
                this.currentView = viewKey;
            }
        } catch(NotesException ex) {}
    }

    protected String findViewKey(FacesContext context) throws NotesException {
        // Find the data store and the view it points to
        String storeId = getStoreComponentId();
        if(StringUtil.isNotEmpty(storeId)) {
            UIComponent c = FacesUtil.getComponentFor(this, storeId);
            if(c instanceof UIRestService) {
                IRestService svc = ((UIRestService)c).getService();
                if(svc instanceof DominoViewService) {
                    String databaseName = ((DominoViewService)svc).getDatabaseName();
                    String viewName = ((DominoViewService)svc).getViewName();
                    return ViewDesign.getViewKey(databaseName, viewName);
                }
            }
        }
        return null;
    }

    protected Customizer loadCustomizationBean(FacesContext context) {
        String bean = getCustomizerBean();
        if(StringUtil.isNotEmpty(bean)) {
            return (Customizer)ManagedBeanUtil.getBean(context, bean);
        }
        return null;
    }

    protected void clearColumns(FacesContext context, Customizer bean) {
        getChildren().clear();
    }

    protected void createColumns(FacesContext context, Customizer bean, ViewFactory f, View view) {
        if(bean==null || !bean.createColumns(context, this, f)) {
            ViewDef viewDef = f.getViewDef(view);
            if(viewDef!=null) {
                // The view control already exists, it is simply wrapped into a ControlImpl
                // We then create the columns  and ask the control builder to actually
                // add the columns to the view panel and call the FacesComponent methods
                ControlImpl<UIListView> viewControl = new ControlImpl<UIListView>(this);
                int index = 0;
                for(Iterator<ColumnDef> it=viewDef.iterateColumns(); it.hasNext(); index++) {
                    ColumnDef colDef = it.next();
                    IControl viewCol = createColumn(context,bean,index,colDef);
                    viewControl.addChild(viewCol);
                }

                ControlBuilder.buildControl(context, viewControl,true);
            }
        }
        if(bean!=null) {
            bean.afterCreateColumns(context, this);
        }
    }
    protected IControl createColumn(FacesContext context, Customizer bean, int index, ColumnDef colDef) {
        if(bean!=null) {
            IControl col = bean.createColumn(context, this, index, colDef);
            if(col!=null) {
                return col;
            }
        }
        // Create the column object
        UIListViewColumn col = new UIListViewColumn();
        col.setColumnName(colDef.getName());
        col.setColumnTitle(colDef.getTitle());
        col.setWidth(String.valueOf(colDef.getWidth()));
        col.setResponse(colDef.isResponse());
        col.setTwistie(colDef.isIndentResponses());
        col.setHidden(colDef.isHidden());
        col.setCategory(colDef.isCategorized());
        boolean isAsc = colDef.isResortAscending();
        boolean isDsc = colDef.isResortDescending();
        int sort;
        if( isAsc ){
            sort = isDsc ? UIListViewColumn.SORT_BOTH : UIListViewColumn.SORT_ASCENDING;
        }else{
            sort = isDsc ? UIListViewColumn.SORT_DESCENDING : UIListViewColumn.SORT_NONE;
        }
        col.setSort( sort );
        col.setIcon( colDef.isIcon() );
        ControlImpl<UIListViewColumn> cCol = new ControlImpl<UIListViewColumn>(col);

        if(bean!=null) {
            bean.afterCreateColumn(context, index, colDef, cCol);
        }
        return cCol;
    }

    protected ViewFactory getViewFactory(FacesContext context, Customizer bean) {
        if(bean!=null) {
            ViewFactory f = bean.getViewFactory();
            if(f!=null) {
                return f;
            }
        }
        return ViewDesign.getDefaultFactory(context);
    }
}