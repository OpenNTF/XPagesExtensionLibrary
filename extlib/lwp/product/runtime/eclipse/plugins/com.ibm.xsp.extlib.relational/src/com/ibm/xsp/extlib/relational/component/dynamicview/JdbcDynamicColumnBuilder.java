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

package com.ibm.xsp.extlib.relational.component.dynamicview;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspViewColumnHeader;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.builder.ControlBuilder.IControl;
import com.ibm.xsp.extlib.component.dynamicview.DynamicColumnBuilder;
import com.ibm.xsp.extlib.component.dynamicview.UIDynamicViewPanel;
import com.ibm.xsp.extlib.component.util.EventHandlerUtil;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataAccessorModel;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataBlockAccessor;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataBlockAccessor.ColumnDef;

/**
 * Dynamic XPage view panel.
 * 
 * This component is a view panel that creates its columns from the definition of a view. 
 * 
 * @author priand
 */
public class JdbcDynamicColumnBuilder implements DynamicColumnBuilder {

    // Customization bean
    public static abstract class JdbcViewCustomizer {
    }

    private FacesContext context;
    private UIDynamicViewPanel panel;
    
    public JdbcDynamicColumnBuilder(FacesContext context, UIDynamicViewPanel panel) {
        this.context = context;
        this.panel = panel;
    }
    
    public String getViewKey() {
        String query = findQuery();
        return query;
    }
    
    protected String findQuery() {
        DataModel dm = panel.getDataModel();
        if(dm instanceof JdbcDataAccessorModel) {
            return ((JdbcDataAccessorModel)dm).getQuery(); 
        }
        return null;
    }
    
    public void initView() {
        JdbcDataAccessorModel dataModel = (JdbcDataAccessorModel)panel.getDataModel();
        if(dataModel!=null) {
            JdbcDataBlockAccessor accessor = dataModel.getDataAccessor();
            if(accessor!=null) {
                // And create the new ones
                if(dataModel!=null) {
                    JdbcViewCustomizer bean = (JdbcViewCustomizer)panel.findCustomizationBean(context);
                    createColumns(bean, dataModel, accessor);
                }
            }
        }
    }
    protected void createColumns(JdbcViewCustomizer bean, JdbcDataAccessorModel dataModel, JdbcDataBlockAccessor accessor) {
        ControlImpl<UIDynamicViewPanel> viewControl = new ControlImpl<UIDynamicViewPanel>(panel);
        ColumnDef[] colDefs = accessor.getColumnDefs(dataModel);
        for(int i=0; i<colDefs.length; i++) {
            ColumnDef colName = colDefs[i];
            IControl viewCol = createColumn(bean, dataModel, accessor, i, colName);
            if(viewCol!=null) {
                viewControl.addChild(viewCol);
            }
        }
        ControlBuilder.buildControl(context, viewControl,true);
    }
    protected IControl createColumn(JdbcViewCustomizer bean, JdbcDataAccessorModel dataModel, JdbcDataBlockAccessor accessor, int index, ColumnDef colDef) {
// Can a column be hidden in SQL?       
//      // If the column is hidden, do not create it
//      if(colDef.isHidden()) {
//          return null;
//      }
        // Create the column object
        UIDynamicViewPanel.DynamicColumn col = new UIDynamicViewPanel.DynamicColumn();
        col.setColumnName(colDef.getName());
        col.setConverter(new JdbcDynamicColumnConverter());
        if(index==0) {
            col.setDisplayAs("link"); // $NON-NLS-1$
            // Trigger the event handler if it exists
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

        // Create the column header object
        ControlImpl<UIComponent> cCol = new ControlImpl<UIComponent>(col);

        // Add the column header
        XspViewColumnHeader colHeader = new XspViewColumnHeader();
        colHeader.setValue(colDef.getTitle());
        colHeader.setSortable(true);
        if(index==0 && panel.isShowHeaderCheckbox()) {
            colHeader.setShowCheckbox(true);
        }
        IControl cColHeader = new ControlImpl<UIComponent>(colHeader);
        cCol.addChild(cColHeader);

        return cCol;
    }
}