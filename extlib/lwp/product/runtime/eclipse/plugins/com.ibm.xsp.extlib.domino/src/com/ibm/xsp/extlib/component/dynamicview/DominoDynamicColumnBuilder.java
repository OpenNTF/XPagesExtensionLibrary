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

package com.ibm.xsp.extlib.component.dynamicview;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.binding.ValueBindingEx;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspViewColumnHeader;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.builder.ControlBuilder.IControl;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ColumnDef;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ViewDef;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ViewFactory;
import com.ibm.xsp.extlib.component.util.EventHandlerUtil;
import com.ibm.xsp.model.domino.DominoViewDataModel;

/**
 * Dynamic XPage view panel.
 * 
 * This component is a view panel that creates its columns from the definition of a view. 
 * 
 * @author priand
 */
public class DominoDynamicColumnBuilder implements DynamicColumnBuilder {

    // Customization bean
    public static abstract class DominoViewCustomizer {
        public ViewFactory getViewFactory() {
            return null;
        }
        public boolean createColumns(FacesContext context, UIDynamicViewPanel panel, ViewFactory f) {
            return false;
        }
        public IControl createColumn(FacesContext context, UIDynamicViewPanel panel, int index, ColumnDef colDef) {
            return null;
        }
        public void afterCreateColumn(FacesContext context, int index, ColumnDef colDef, IControl column) {
        }
        public void afterCreateColumns(FacesContext context, UIDynamicViewPanel panel) {
        }
    }

    
    private FacesContext context;
    private UIDynamicViewPanel panel;
    
    public DominoDynamicColumnBuilder(FacesContext context, UIDynamicViewPanel panel) {
        this.context = context;
        this.panel = panel;
    }
    
    public String getViewKey() {
        try {
            View view = findView();
            String key=ViewDesign.getViewKey(view);
            return key;
        } catch(NotesException ex) {
            throw new FacesException(ex);
        }
    }
    
    protected View findView() {
        DataModel dm = panel.getDataModel();
        if(dm instanceof DominoViewDataModel) {
            return ((DominoViewDataModel)dm).getView(); 
        }
        return null;
    }
    
    public void initView() {
        DominoViewCustomizer bean = (DominoViewCustomizer)panel.findCustomizationBean(context);

        View view = findView();
        ViewFactory f = getViewFactory(bean);
        ViewDef viewDef = f!=null ? f.getViewDef(view) : null;
        
        // And create the new ones
        if(viewDef!=null) {
            createColumns(bean, f, viewDef);
        }
    }
    protected void createColumns(DominoViewCustomizer bean, ViewFactory f, ViewDef viewDef) {
        if(bean==null || !bean.createColumns(context, panel, f)) {
            // The view control already exists, it is simply wrapped into a ControlImpl
            // We then create the columns  and ask the control builder to actually 
            // add the columns to the view panel and call the FacesComponent methods
            ControlImpl<UIDynamicViewPanel> viewControl = new ControlImpl<UIDynamicViewPanel>(panel);
            int index = 0;
            for(Iterator<ColumnDef> it=viewDef.iterateColumns(); it.hasNext(); index++) {
                ColumnDef colDef = it.next();
                IControl viewCol = createColumn(bean,index,viewDef,colDef);
                if(viewCol!=null) {
                    viewControl.addChild(viewCol);
                }
            }
            
            ControlBuilder.buildControl(context, viewControl,true);
        }
        if(bean!=null) {
            bean.afterCreateColumns(context, panel);
        }
    }
    protected IControl createColumn(DominoViewCustomizer bean, int index, ViewDef viewDef, ColumnDef colDef) {
        if(bean!=null) {
            IControl col = bean.createColumn(context, panel, index, colDef);
            if(col!=null) {
                return col;
            }
        }
        // If the column is hidden, do not create it
        if(colDef.isHidden()) {
            return null;
        }
        // Create the column object
        UIDynamicViewPanel.DynamicColumn col = new UIDynamicViewPanel.DynamicColumn();
        col.setColumnName(colDef.getName());
        col.setConverter(new ViewColumnConverter(viewDef,colDef));
        if(colDef.isLink()) {
            col.setDisplayAs("link"); // $NON-NLS-1$
            // Trigger the event handler if it exists
            if(colDef.isOnClick()) {
                XspEventHandler eh = EventHandlerUtil.findHandler(panel,"onColumnClick"); // $NON-NLS-1$
                if(eh!=null) {
                    // Make a copy of the handler an change the event name
                    XspEventHandler neh = new XspEventHandler();
                    Object state = eh.saveState(context);
                    neh.restoreState(context, state);
                    neh.setEvent("onclick"); // $NON-NLS-1$
                    col.getChildren().add(neh);
                }
            }
        }
        if(colDef.isIndentResponses() && viewDef.isHierarchical()) {
            col.setIndentResponses(true);
        }
        switch(colDef.getAlignment()) {
            case ViewColumn.ALIGN_LEFT: {
                // As is...
            } break;
            case ViewColumn.ALIGN_CENTER: {
                col.setStyle("text-align: center"); // $NON-NLS-1$
            } break;
            case ViewColumn.ALIGN_RIGHT: {
                col.setStyle("text-align: right"); // $NON-NLS-1$
            } break;
        }
        if(colDef.isCheckbox() && panel.isShowCheckbox()) {
            col.setShowCheckbox(true);
        }

        if(colDef.isIcon()) {
            String expr = "#{javascript:@ViewIconUrl(this.getValue())}"; // $NON-NLS-1$
            ValueBindingEx vbex = (ValueBindingEx)ApplicationEx.getInstance().createValueBinding(expr);
            vbex.setComponent(col);
            col.setValueBinding("iconSrc",vbex); // $NON-NLS-1$
            col.setDisplayAs("hidden"); // $NON-NLS-1$
        }
        ControlImpl<UIComponent> cCol = new ControlImpl<UIComponent>(col);

        // Add the column header
        XspViewColumnHeader colHeader = new XspViewColumnHeader();
        colHeader.setValue(colDef.getTitle());
        if(colDef.isResortAscending() || colDef.isResortAscending() ) {
            colHeader.setSortable(true);
        }
        switch(colDef.getHeaderAlignment()) {
            case ViewColumn.ALIGN_LEFT: {
                // As is...
            } break;
            case ViewColumn.ALIGN_CENTER: {
                colHeader.setStyle("text-align: center"); // $NON-NLS-1$
            } break;
            case ViewColumn.ALIGN_RIGHT: {
                colHeader.setStyle("text-align: right"); // $NON-NLS-1$
            } break;
        }
        if(colDef.isCheckbox() && panel.isShowHeaderCheckbox()) {
            colHeader.setShowCheckbox(true);
        }
        IControl cColHeader = new ControlImpl<UIComponent>(colHeader);
        cCol.addChild(cColHeader);
        
        if(bean!=null) {
            bean.afterCreateColumn(context, index, colDef, cCol);
        }
        return cCol;
    }

    protected ViewFactory getViewFactory(DominoViewCustomizer bean) {
        if(bean!=null) {
            ViewFactory f = bean.getViewFactory();
            if(f!=null) {
                return f;
            }
        }
        return ViewDesign.getDefaultFactory(context);
    }
}