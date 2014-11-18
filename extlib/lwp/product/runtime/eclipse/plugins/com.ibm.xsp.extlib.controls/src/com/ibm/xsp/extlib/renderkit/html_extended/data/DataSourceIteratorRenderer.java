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

package com.ibm.xsp.extlib.renderkit.html_extended.data;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.el.PropertyResolver;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.component.UIEventHandler;
import com.ibm.xsp.component.UIViewColumnHeader;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.UIDataSourceIterator;
import com.ibm.xsp.extlib.component.data.ValueColumn;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractDataViewRenderer.ViewDefinition;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.renderkit.ContentTypeRendererUtil;
import com.ibm.xsp.renderkit.html_extended.IteratorAjaxRenderer;
import com.ibm.xsp.util.AjaxUtilEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * {@link UIDataSourceIterator} renderer.
 * <p>
 * Provides some common 
 * </p>
 */
public abstract class DataSourceIteratorRenderer extends FacesRendererEx implements IteratorAjaxRenderer {
    // The following delimiter properties are appended to the HTML 
    // IDs of the elements that are activated in order to perform the 
    // associated function.
    // TODO: Investigate if a less insane method of message passing might work
    
    // Expand/collapse all the rows
    protected final String EXPAND_DELIMITER = "__expand:"; //$NON-NLS-1$
    protected final String SHRINK_DELIMITER = "__shrink:"; //$NON-NLS-1$

    // Show/Hide the details
    protected final String SHOW_DELIMITER = "__show:"; //$NON-NLS-1$
    protected final String HIDE_DELIMITER = "__hide:"; //$NON-NLS-1$

    // Click on a column to sort it
    protected final String SORT_DELIMITER = "__asc:"; //$NON-NLS-1$
    protected final String SORT_SUMMARY = "$s"; //$NON-NLS-1$

    // Prefix for the show/hide hidden field
    protected static final String ID_HIDDEN     = "_shfld"; // $NON-NLS-1$

    // ints returned by getDominoSortIconCode
    protected static final int SORT_0_NOT_SORTABLE_COLUMN = 0;
    protected static final int SORT_1_COLUMN_SORTABLE_BOTH_CURRENTLY_NOT_SORTED = 1;
    protected static final int SORT_2_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_ASCENDING = 2;
    protected static final int SORT_3_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_DESCENDING = 3;
    protected static final int SORT_4_COLUMN_SORTABLE_ASCENDING_CURRENTLY_NOT_SORTED = 4;
    protected static final int SORT_5_COLUMN_SORTABLE_ASCENDING_CURRENTLY_SORTED_ASCENDING = 5;
    protected static final int SORT_6_COLUMN_SORTABLE_DESCENDING_CURRENTLY_NOT_SORTED = 6;
    protected static final int SORT_7_COLUMN_SORTABLE_DESCENDING_CURRENTLY_SORTED_DESCENDING = 7;


    //
    // Theme properties
    //
    protected static final int PROP_BLANKIMG            = 1;
    protected static final int PROP_BLANKIMGALT         = 2;
    protected static final int PROP_ALTTEXTCLASS        = 3;
    
    protected static final int PROP_COLLAPSEICON        = 10;
    protected static final int PROP_COLLAPSEICONSTYLE   = 11;
    protected static final int PROP_COLLAPSEICONCLASS   = 12;
    protected static final int PROP_COLLAPSEICONALT     = 13;
    protected static final int PROP_EXPANDICON          = 14;
    protected static final int PROP_EXPANDICONSTYLE     = 15;
    protected static final int PROP_EXPANDICONCLASS     = 16;
    protected static final int PROP_EXPANDICONALT       = 17;
    protected static final int PROP_EMPTYICON           = 18;
    protected static final int PROP_EMPTYICONSTYLE      = 19;
    protected static final int PROP_EMPTYICONCLASS      = 20;
    protected static final int PROP_EMPTYICONALT        = 21;
    
    protected static final int PROP_SHOWHIDEONCLIENT    = 25;
    
    protected static final int PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING     = 30;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING    = 31;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORTBOTH               = 32;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING       = 33;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING      = 34;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORT_WIDTH             = 35;
    protected static final int PROP_TABLEHDRCOLIMAGE_SORT_HEIGHT             = 36;
    
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMGALT:              return ""; // //$NON-NLS-1$
            case PROP_COLLAPSEICON:             return "/.ibmxspres/global/theme/common/images/collapse.gif"; // $NON-NLS-1$
            // TODO style should be set through a styleClass, not hardcoded styles
            case PROP_COLLAPSEICONSTYLE:        return "width:13.0px;height:13.0px;padding-right:3px"; // $NON-NLS-1$
            case PROP_COLLAPSEICONALT:          return "Collapse the current row"; // $NLS-DataSourceIteratorRenderer.Collapsethecurrentrow-1$
            case PROP_EXPANDICON:               return "/.ibmxspres/global/theme/common/images/expand.gif"; // $NON-NLS-1$
            // TODO style should be set through a styleClass, not hardcoded styles
            case PROP_EXPANDICONSTYLE:          return "width:13.0px;height:13.0px;padding-right:3px"; // $NON-NLS-1$
            case PROP_EXPANDICONALT:            return "Expand the current row"; // $NLS-DataSourceIteratorRenderer.Expandthecurrentrow-1$
            case PROP_EMPTYICON:                return "/.ibmxspres/global/theme/common/images/transparent.gif"; // $NON-NLS-1$
            // TODO style should be set through a styleClass, not hardcoded styles
            case PROP_EMPTYICONSTYLE:           return "width:16.0px;height:13.0px"; // $NON-NLS-1$
            // note, for an Alt, there's a difference between the empty string and null
            case PROP_EMPTYICONALT:             return ""; //$NON-NLS-1$
            
            // Hide on client doesn't propagate well after a click on a link, or a page
            // change as the pager is partial executing itself
            // Should send a request instead of filling a hidden field
            case PROP_SHOWHIDEONCLIENT:         return false; //true;
            
            //href =  ;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:  return UIViewColumnHeader.IMAGE_SORT_BOTH_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING: return UIViewColumnHeader.IMAGE_SORT_BOTH_DESCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:            return UIViewColumnHeader.IMAGE_SORT_BOTH;
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:    return UIViewColumnHeader.IMAGE_SORTED_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:   return UIViewColumnHeader.IMAGE_SORTED_DESCENDING;
            // the domino sort header icons are 16x16 px
            case PROP_TABLEHDRCOLIMAGE_SORT_WIDTH:           return "16"; //$NON-NLS-1$
            case PROP_TABLEHDRCOLIMAGE_SORT_HEIGHT:          return "16"; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    
    // ================================================================
    // Decoding methods
    // ================================================================
    
    /**
     * Decode methods that handle the known actions (like expand/collapse).
     */
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();

        if (!component.isRendered())
            return;

        super.decode(context, component);
        
        UIDataSourceIterator dataComponent = (component instanceof UIDataSourceIterator) ? (UIDataSourceIterator)component : null;
        if(null == dataComponent){
            // Might be the row component
            return;
        }
        
        String hiddenValue = FacesUtil.getHiddenFieldValue(context);
        if (isToggleAction(context, dataComponent,hiddenValue)) {
            ActionEvent ev = createToggleEvent(dataComponent, hiddenValue);
            dataComponent.queueEvent(ev);
        }

        // Decode the show/hide hidden field
        String showHideId = dataComponent.getClientId(context)+ID_HIDDEN;
        String showHideValue = (String)context.getExternalContext().getRequestParameterMap().get(showHideId);
        if(StringUtil.isNotEmpty(showHideValue)) {
            String[] v = StringUtil.splitString(showHideValue, ',');
            ToggleDetailEvent ev = new ToggleDetailEvent(component);
            ev.setTogglePositions(v);
            dataComponent.queueEvent(ev);
        }
        
        // decode any event handlers...
        if (component.getRendersChildren()) {
            List<?> children = component.getChildren();
            for (Iterator<?> i = children.iterator(); i.hasNext();) {
                UIComponent child = (UIComponent) i.next();
                if (child instanceof UIEventHandler)
                    child.decode(context);
            }
        }
    }
    private boolean isToggleAction(FacesContext context, UIDataSourceIterator component, String submitterId){
        // The id must be an expand/collapse action 
        if (StringUtil.isNotEmpty(submitterId)) { 
            if( submitterId.contains(EXPAND_DELIMITER) 
                || submitterId.contains(SHRINK_DELIMITER)
                || submitterId.contains(SHOW_DELIMITER)
                || submitterId.contains(HIDE_DELIMITER)
                || submitterId.contains(SORT_DELIMITER)) {
                String parentId = component.getClientId(context);
                if (submitterId.startsWith(parentId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private ActionEvent createToggleEvent(UIDataSourceIterator component, String catRowId) {
        if (catRowId.lastIndexOf(EXPAND_DELIMITER) != -1) {
            int delimiter_len = EXPAND_DELIMITER.length(); 
            ToggleRowEvent ev = new ToggleRowEvent(component);
            ev.setPosition(catRowId.substring(catRowId.lastIndexOf(EXPAND_DELIMITER) + delimiter_len));
            ev.setExpand(true);
            return ev;
        }
        if (catRowId.lastIndexOf(SHRINK_DELIMITER) != -1) {
            int delimiter_len = SHRINK_DELIMITER.length(); 
            ToggleRowEvent ev = new ToggleRowEvent(component);
            ev.setPosition(catRowId.substring(catRowId.lastIndexOf(SHRINK_DELIMITER) + delimiter_len));
            ev.setExpand(false);
            return ev;
        }
        if (catRowId.lastIndexOf(SHOW_DELIMITER) != -1) {
            int delimiter_len = SHOW_DELIMITER.length(); 
            ToggleDetailEvent ev = new ToggleDetailEvent(component);
            ev.setTogglePositions(new String[]{catRowId.substring(catRowId.lastIndexOf(SHOW_DELIMITER) + delimiter_len)});
            return ev;
        }
        if (catRowId.lastIndexOf(HIDE_DELIMITER) != -1) {
            int delimiter_len = HIDE_DELIMITER.length(); 
            ToggleDetailEvent ev = new ToggleDetailEvent(component);
            ev.setTogglePositions(new String[]{catRowId.substring(catRowId.lastIndexOf(HIDE_DELIMITER) + delimiter_len)});
            return ev;
        }
        if (catRowId.lastIndexOf(SORT_DELIMITER) != -1) {
            int delimiter_len = SORT_DELIMITER.length(); 
            ToggleSortColumnEvent ev = new ToggleSortColumnEvent(component);
            String columnName = catRowId.substring(catRowId.lastIndexOf(SORT_DELIMITER) + delimiter_len);
            ev.setColumnName(columnName);
            return ev;
        }
        return null;
    }

    
    // ================================================================
    // Rendering methods
    // ================================================================
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIDataSourceIterator c = (UIDataSourceIterator)component;
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        writeMainContainer(context, w, c);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }

    
    // ================================================================
    // Ajax Support
    // ================================================================
    
    public void encodeRows(FacesContext context, UIDataIterator iterator, int first, int rows) throws IOException {
        throw new NotImplementedException();
    }

    
    // ================================================================
    // Main Container
    // ================================================================
    
    protected void writeMainContainer(FacesContext context, ResponseWriter w, UIDataSourceIterator c) throws IOException {
        throw new NotImplementedException();
    }

    
    // ================================================================
    // Row properties
    // ================================================================

    protected boolean isCategoryRow(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.isRowCategory();
        }
        return false;
    }

    protected boolean isTotalRow(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.isRowTotal();
        }
        return false;
    }

    protected boolean isRowExpanded(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.isRowExpanded();
        }
        return false;
    }

    protected boolean isRowDetailVisible(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        if( viewDef.rowDisableHideRow ){
            return true;
        }
        String rowPosition = viewDef.rowPosition;
        if(StringUtil.isNotEmpty(rowPosition)) {
            return c.isDetailVisible(rowPosition,viewDef.expandedDetailDefault);
        }
        return viewDef.expandedDetailDefault;
    }

    protected String getRowPosition(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.getRowPosition();
        }
        return null;
    }

    protected boolean isRowLeaf(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.isRowLeaf();
        }
        return true;
    }

    protected boolean isColumnSortable(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, String columnName) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.isColumnSortable(columnName);
        }
        return false;
    }

    protected int getColumnSortState(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, String columnName) {
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            String sortColumn = tbm.getResortColumn();
            if(sortColumn==null) {
                int sortState = tbm.getResortState(columnName);
                return sortState;
            } else if(StringUtil.equals(columnName, sortColumn)) {
                int sortState = tbm.getResortState(columnName);
                return sortState;
            }
            //System.out.println("SortColumn: "+sortColumn);
            return tbm.getResortType(columnName);
        }
        return TabularDataModel.RESORT_NONE;
    }
    /**
     * Unused, kept for backward compatibility
     * @param context
     * @param c
     * @param viewDef
     * @param columnName
     * @return
     * @deprecated
     */
    public String getDominoSortIcon(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, String columnName){
        
        int dominoSortIconCode = getDominoSortIconCode(context, c, viewDef, columnName);
        
        return getDominoSortIcon(context, c, viewDef, dominoSortIconCode);
    }
    protected String getDominoSortIcon(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, 
            int dominoSortIconCode){
        
        switch(dominoSortIconCode){
            case SORT_1_COLUMN_SORTABLE_BOTH_CURRENTLY_NOT_SORTED:{
                String href = (String)getProperty(PROP_TABLEHDRCOLIMAGE_SORTBOTH);
                return href;
            }
            case SORT_2_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_ASCENDING:{
                String href = (String)getProperty(PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING);
                return href;
            }
            case SORT_3_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_DESCENDING:{
                String href = (String)getProperty(PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING);
                return href;
            }
            case SORT_4_COLUMN_SORTABLE_ASCENDING_CURRENTLY_NOT_SORTED:
            case SORT_7_COLUMN_SORTABLE_DESCENDING_CURRENTLY_SORTED_DESCENDING:{
                String href = (String)getProperty(PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING);
                return href;
            }
            case SORT_5_COLUMN_SORTABLE_ASCENDING_CURRENTLY_SORTED_ASCENDING:
            case SORT_6_COLUMN_SORTABLE_DESCENDING_CURRENTLY_NOT_SORTED:{
                String href = (String)getProperty(PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING);
                return href;
            }
        }
        return null;
    }

    protected String getDominoSortAlt(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, 
            int dominoSortIconCode){
        // TODO extract strings to getProperty method
        switch(dominoSortIconCode){
            case SORT_1_COLUMN_SORTABLE_BOTH_CURRENTLY_NOT_SORTED:{
                String sortIconAlt;
                sortIconAlt = "Sort Toggle"; // $NLS-DataViewRenderer_SortToggle-1$
                return sortIconAlt;
            }
            case SORT_2_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_ASCENDING:
            case SORT_5_COLUMN_SORTABLE_ASCENDING_CURRENTLY_SORTED_ASCENDING:{
                String sortIconAlt;
                sortIconAlt = "Ascending"; //  $NLS-DataViewRenderer_SortAscending-1$ 
                return sortIconAlt;
            }
            case SORT_3_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_DESCENDING:
            case SORT_7_COLUMN_SORTABLE_DESCENDING_CURRENTLY_SORTED_DESCENDING:{
                String sortIconAlt;
                sortIconAlt = "Descending" ; // $NLS-DataViewRenderer_SortDescending-1$
                return sortIconAlt;
            }
            case SORT_4_COLUMN_SORTABLE_ASCENDING_CURRENTLY_NOT_SORTED:
            case SORT_6_COLUMN_SORTABLE_DESCENDING_CURRENTLY_NOT_SORTED:{
                String sortIconAlt;
                sortIconAlt = "Not Sorted"; // $NLS-DataViewRenderer_NotSorted-1$
                return sortIconAlt;
            }
        }
        return null;
    }
    
    /**
     * There are 8 states:
     * <ul>
     *  <li>0: Not a sortable column.</li>
     *  <li>1: Column resortable in both directions, corrently not sorted.</li>
     *  <li>2: Column resortable in both directions, corrently sorted ascending.</li>
     *  <li>3: Column resortable in both directions, corrently sorted descending.</li>
     *  <li>4: Column can be unsorted or sorted ascending, currently not sorted.</li>
     *  <li>5: Column can be unsorted or sorted ascending, corrently sorted ascending.</li>
     *  <li>6: Column can be unsorted or sorted descending, currently not sorted.</li>
     *  <li>7: Column can be unsorted or sorted descending, currently sorted descending.</li>
     * </ul>
     */
    protected int getDominoSortIconCode(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, String columnName) {
        // Note the fix for:
        // SPR# BGLN85FFWC sort state only maintained on current column
        // has been moved to UIDataSourceIterator.broadcast(FacesEvent)
        
        int dominoSortIconProperty = SORT_0_NOT_SORTABLE_COLUMN;
        DataModel dm = viewDef.dataModel;
        if(null != dm && dm instanceof TabularDataModel) {
            TabularDataModel dataModel = (TabularDataModel)dm;
            int resort = dataModel.getResortType(columnName);
            if (resort == TabularDataModel.RESORT_BOTH) {
                if ( ! StringUtil.equalsIgnoreCase(columnName, dataModel.getResortColumn())) {
                    dominoSortIconProperty = SORT_1_COLUMN_SORTABLE_BOTH_CURRENTLY_NOT_SORTED;
                } else {
                    int resortState = dataModel.getResortState(columnName);
                    if (resortState == TabularDataModel.RESORT_ASCENDING){
                        dominoSortIconProperty = SORT_2_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_ASCENDING;
                    }else if (resortState == TabularDataModel.RESORT_DESCENDING){
                        dominoSortIconProperty = SORT_3_COLUMN_SORTABLE_BOTH_CURRENTLY_SORTED_DESCENDING;
                    }else{
                        dominoSortIconProperty = SORT_1_COLUMN_SORTABLE_BOTH_CURRENTLY_NOT_SORTED;
                    }
                }
            }
            else if (resort == TabularDataModel.RESORT_ASCENDING || resort == TabularDataModel.RESORT_DESCENDING) {
                int resortState = dataModel.getResortState(columnName);
                if( resort == TabularDataModel.RESORT_ASCENDING ){
                    // 4 or 5
                    if( resortState == TabularDataModel.RESORT_ASCENDING ){ 
                        // the meaning of resortState == RESORT_ASCENDING is counter-intuative,
                        // it doesn't give the current state, but rather the action that would
                        // be performed if you clicked the header.
                        dominoSortIconProperty = SORT_4_COLUMN_SORTABLE_ASCENDING_CURRENTLY_NOT_SORTED;
                    }else{
                        dominoSortIconProperty = SORT_5_COLUMN_SORTABLE_ASCENDING_CURRENTLY_SORTED_ASCENDING;
                    }
                }else{ // resort == TabularDataModel.RESORT_DESCENDING
                    // 6 or 7
                    if (resortState == TabularDataModel.RESORT_DESCENDING){
                        // the meaning of resortState == RESORT_DESCENDING is counter-intuative,
                        // it doesn't give the current state, but rather the action that would
                        // be performed if you clicked the header.
                        dominoSortIconProperty = SORT_6_COLUMN_SORTABLE_DESCENDING_CURRENTLY_NOT_SORTED;
                    }else{ 
                        dominoSortIconProperty = SORT_7_COLUMN_SORTABLE_DESCENDING_CURRENTLY_SORTED_DESCENDING;
                    }
                }
            }
        }
        return dominoSortIconProperty;
    }

    protected int getColumnIndentLevel(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        // If the view is forced as flat...
        if(viewDef.showItemsFlat) {
            return 0;
        }
        // Else get it from the data model
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.getColumnIndentLevel()-viewDef.indentOffset;
        }
        return 0;
    }

    // This is for an 852 temp fix
    protected int calculateIndentOffset(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef) {
        // Else get it from the data model
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            return tbm.getColumnIndentLevel();
        }
        return 0;
    }
    
    
    // ================================================================
    // Write/Access the column value
    // ================================================================

    protected void writeColumnValue(FacesContext context, ResponseWriter w, UIDataSourceIterator c, ViewDefinition viewDef, ValueColumn vc) throws IOException {
        String value = formatColumnValue(context, c, viewDef, vc);
        if(StringUtil.isNotEmpty(value)) {
            String contentType = vc.getContentType();
            if( "html".equalsIgnoreCase(contentType) ){//$NON-NLS-1$
                // TODO allow different filters, not just the default "acf" filter
                String filterName = "acf"; //$NON-NLS-1$
                value = ((FacesContextEx)context).filterHtml(filterName, value);
            }
            ContentTypeRendererUtil.render(context, c, w, contentType, value);
            //w.writeText(value, null);
        } else {
            writeColumnEmptyValue(context, w, c, viewDef, vc);
        }
    }
    protected void writeColumnEmptyValue(FacesContext context, ResponseWriter w,
            UIDataSourceIterator c, ViewDefinition viewDef, ValueColumn vc)
            throws IOException {
        JSUtil.writeTextBlank(w); // &nbsp;
    }
    
    protected String formatColumnValue(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, ValueColumn vc) throws IOException {
        Object value = getColumnValue(context, c, viewDef, vc);
        if(value!=null) {
            Converter cv = findConverter(context, c, viewDef, vc, value);
            String v = cv!=null ? cv.getAsString(context, c, value) : value.toString();
            return v;
        }
        return null;
    }

    protected Converter findConverter(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, ValueColumn vc, Object value) {
        // Explicit converter
        Converter converter = vc.getConverter();
        if(converter!=null) {
            return converter;
        }

        Class<?> converterType = value.getClass();
        if (converterType == null || converterType == String.class || converterType == Object.class) {
            return null;
        }
        
        // Acquire an appropriate converter instance.
        try {
            Application application = context.getApplication();
            return application.createConverter(converterType);
        } catch (Exception e) {
        }
        
        return null;
    }

    protected Object getColumnValue(FacesContext context, UIDataSourceIterator c, ViewDefinition viewDef, ValueColumn vc) throws IOException {
        // Look for a computed value
        Object value = vc.getValue();
        if(value!=null) {
            return value;
        }
        
        // Look for a column name
        String colName = vc.getColumnName();
        if(StringUtil.isNotEmpty(colName)) {
            // Read from a rowData object
            Object rowData = viewDef.dataModel.getRowData();
            if(rowData instanceof ViewRowData) {
                ViewRowData vr = (ViewRowData)rowData;
                return vr.getColumnValue(colName);
            }
            // Use the JSF property resolver
            PropertyResolver pr = context.getApplication().getPropertyResolver();
            return pr.getValue(rowData, colName);
        }
        
        // Ok no value found
        return null;
    }
    
    // clientId:    The id of the component that will receive the event 
    // targetId:    The component to attach the event on the client
    protected void setupSubmitOnClick(FacesContext context, UIDataSourceIterator component, String clientId, String targetId, String refreshId) {
        boolean immediate = false;
        UIComponent subTree = ((FacesContextEx)context).getSubTreeComponent();
        
        boolean partialExec = component.isPartialExecute();
        String execId = null;
        if(partialExec) {
            // The exec ID must be the actual table id, as there is not sub component for
            // handling collapse/expand. Moreover, because this method is called per row
            // the table client id has a trailing row number that should be removed.
            // Also, because there isn't a sub component, processDecode() is called on the
            // table, which implies that all the rows are decoded. But the other phases
            // are skipped.
            execId = component.getClientId(context);
            execId = execId.substring(0,execId.lastIndexOf(':'));
            immediate = true;
        } else {
            if(subTree!=null) {
                partialExec = true;
                execId = subTree.getClientId(context);
                immediate = true;
            }
        }

        boolean partialRefresh = component.isPartialRefresh();
        if(partialRefresh) {
            if(StringUtil.isEmpty(refreshId)) {
                refreshId = component.getRefreshId();
                if (StringUtil.isEmpty(refreshId)) {
                    refreshId = AjaxUtilEx.getRefreshId(context, component);
                }
            }
        } else {
            if(subTree!=null) {
                partialRefresh = true;
                refreshId = subTree.getClientId(context);
            }
        }
        
        /// call some javascript in xspClient.js
        final String event = "onclick"; // $NON-NLS-1$
        // Note, the onclick event is also triggered if the user tabs to the
        // image and presses enter. (Not just when clicked with a mouse.)
        // when the span is clicked, put its id in the hidden field and submit the form.
        StringBuilder buff = new StringBuilder();
        if (partialRefresh) {
            JavaScriptUtil.appendAttachPartialRefreshEvent(
                    buff, 
                    clientId, 
                    targetId,
                    execId,
                    event, 
                    /* clientSideScriptName */null, 
                    immediate?JavaScriptUtil.VALIDATION_NONE:JavaScriptUtil.VALIDATION_FULL,
                    /*refreshId*/refreshId,
                    /*onstart*/null,
                    /*oncomplete*/null,
                    /*onerror*/null
                    );

        }
        else {
            JavaScriptUtil.appendAttachEvent(
                    buff, 
                    clientId, 
                    targetId,
                    execId,
                    event, 
                    /* clientSideScriptName */null, 
                    /*submit*/true, 
                    immediate?JavaScriptUtil.VALIDATION_NONE:JavaScriptUtil.VALIDATION_FULL);
        }
        String script = buff.toString();
        // then add the script block we just generated.
        JavaScriptUtil.addScriptOnLoad(script);
    }

    protected void startElement(ResponseWriter w, Object tag, Object style, Object clazz, Object dojoType) throws IOException {
        String sTag = getString(tag);
        if(StringUtil.isNotEmpty(sTag)) {
            w.startElement(sTag,null);
            String sDojoType = getString(dojoType);
            if(StringUtil.isNotEmpty(sDojoType)) {
                DojoRendererUtil.writeDojoHtmlAttributes(FacesContext.getCurrentInstance(), null, sDojoType);
                //w.writeAttribute("dojoType", sDojoType, null); // $NON-NLS-1$
            }
            String sStyle = getString(style);
            if(StringUtil.isNotEmpty(sStyle)) {
                w.writeAttribute("style", sStyle, null); // $NON-NLS-1$
            }
            String sClazz = getString(clazz);
            if(StringUtil.isNotEmpty(sClazz)) {
                w.writeAttribute("class", sClazz, null); // $NON-NLS-1$
            }
        }
    }
    protected final String getString(Object o) {
        if(o==null) {
            return null;
        }
        if(o instanceof String) {
            return (String)o;
        }
        if(o instanceof Number) {
            int prop = ((Number)o).intValue();
            return (String)getProperty(prop);
        }
        throw new IllegalArgumentException();
    }
}