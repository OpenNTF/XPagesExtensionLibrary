/*
 * © Copyright IBM Corp. 2010, 2013
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
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewColumn;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.*;
import com.ibm.xsp.extlib.component.image.IconEntry;
import com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractDataViewRenderer.ViewDefinition;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.DirLangUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * {@link UIDataView} renderer.
 */
public class DataViewRenderer extends AbstractWebDataViewRenderer {

    public static final String INTERNAL_CHECKBOX_ID = UIViewColumn.INTERNAL_CHECKBOX_ID;
    
    protected static final int PROP_TABLECLASS              = 200;
    protected static final int PROP_TABLEROWCLASS           = 201;
    protected static final int PROP_TABLEFIRSTROWCLASS      = 202;
    protected static final int PROP_TABLELASTROWCLASS       = 203;
    protected static final int PROP_TABLEFIRSTCELLCLASS     = 204;
    protected static final int PROP_TABLELASTCELLCLASS      = 205;
    protected static final int PROP_TABLEROWINDENTPX        = 206;
    protected static final int PROP_TABLEROWEXTRA           = 207;

    protected static final int PROP_TABLEHDRROWCLASS        = 210;
    protected static final int PROP_TABLEHDRCOLSTYLE        = 211;
    protected static final int PROP_TABLEHDRFIRSTCOLCLASS   = 212;
    protected static final int PROP_TABLEHDRCOLCLASS        = 213;
    protected static final int PROP_TABLEHDRCOLLKASCCLASS   = 214;
    protected static final int PROP_TABLEHDRCOLLKDESCLASS   = 215;
    
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
        }
        return super.getProperty(prop);
    }

    
    // ================================================================
    // Runtime view definition object
    // ================================================================
    
    @Override
    protected void initViewDefinition(FacesContext context, AbstractDataView _dataView, ViewDefinition viewDef) {
        UIDataView dataView = (UIDataView)_dataView;
        super.initViewDefinition(context, dataView, viewDef);
        List<UIComponent> children = TypedUtil.getChildren(dataView); 
        viewDef.columnTitles = dataView.isColumnTitles();
        viewDef.collapsibleCategory = dataView.isCollapsibleCategory();
        viewDef.collapsibleRows = dataView.isCollapsibleRows();
        viewDef.multiColumnCount = Math.max(1,dataView.getMultiColumnCount());
        viewDef.categoryColumns = dataView.getCategoryColumn();
        viewDef.categoryCount = viewDef.categoryColumns!=null ? viewDef.categoryColumns.size() : 0;
        if(!children.isEmpty()) {
            UIComponent row = children.get(0);
            viewDef.iconFacet = row.getFacet(UIDataView.FACET_ICON);// row facet
        }
        viewDef.hasCategoryRow = viewDef.categoryCount>0 || getCategoryRowFacet(_dataView, 0)!=null;
        viewDef.hasCheckBoxColumn = dataView.isShowCheckbox();
        viewDef.hasHeaderCheckBoxColumn = dataView.isShowHeaderCheckbox();
        if(viewDef.hasCheckBoxColumn) {
            viewDef.checkboxFieldName = dataView.getClientId(context)+":_colcbox"; // $NON-NLS-1$
            viewDef.checkboxFieldNamePrefix = dataView.getClientId(context);
            viewDef.checkboxFieldNameSuffix = ":_colcbox"; // $NON-NLS-1$
        }
        viewDef.iconColumn = dataView.getIconColumn();
        viewDef.hasIconColumn = viewDef.iconColumn!=null || viewDef.iconFacet!=null;
        viewDef.extraColumns = dataView.getExtraColumns();
        viewDef.hasExtraColumns = viewDef.extraColumns!=null && !viewDef.extraColumns.isEmpty();
        viewDef.nColumns =   (viewDef.hasCheckBoxColumn?1:0) // CheckBox 
                           + (viewDef.hasIconColumn?1:0) // Icon
                           + 1 // Data - always
                           + (viewDef.hasExtraColumns?(viewDef.extraColumns.size()):0) // Extra columns
                           + (viewDef.collapsibleDetails?1:0); // Collapsible details
        
        // Apply different constraints
        
        // In case of multiple columns
        if(viewDef.multiColumnCount>1) {
            // Expand/collapse details must do a full refresh as we cannot refresh a row cell
            viewDef.viewRowRefresh = false;
        }
        
        viewDef.isInfiniteScroll = getInfiniteScroll(dataView.getInfiniteScroll(), context);
    }
    
    public UIComponent getExtraFacet(AbstractDataView dataView, int index){
        // the "extra" facets are row facets [see AbstractDataView.getRowFacetNames()]
        List<UIComponent> children = TypedUtil.getChildren(dataView);
        if( children.isEmpty() ){
            // Note, children will only be empty in JUnit tests 
            // where UIDataView.buildContents is not called.
            return null;
        }
        UIComponent row = children.get(0);
        if(index==0) {
            UIComponent c = row.getFacet(UIDataView.FACET_EXTRA_N);
            if(c!=null) {
                return c;
            }
        }
        return row.getFacet(UIDataView.FACET_EXTRA_N+index);
    }
    public UIComponent getCategoryRowFacet(AbstractDataView dataView, int index){
        // the "categoryRow" facets are row facets [see AbstractDataView.getRowFacetNames()]
        List<UIComponent> children = TypedUtil.getChildren(dataView);
        if( children.isEmpty() ){
            // Note, children will only be empty in JUnit tests 
            // where UIDataView.buildContents is not called.
            return null;
        }
        UIComponent row = children.get(0);
        if(index==0) {
            UIComponent c = row.getFacet(UIDataView.FACET_CATEGORY_N);
            if(c!=null) {
                return c;
            }
        }
        return row.getFacet(UIDataView.FACET_CATEGORY_N+index);
    }
    
    @Override
    protected void afterRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // In case of a multi column view, we try to fill the missing blanks by reading more rows
        if(viewDef.multiColumnCount>1) {
            if(viewDef.currentColumn<viewDef.multiColumnCount) {
                int index = viewDef.first+viewDef.rows;
                while(viewDef.currentColumn<viewDef.multiColumnCount) {
                    c.setRowIndex(index++);
                    if(!c.isRowAvailable()) {
                        break;
                    }
                    beforeRow(context, w, c, viewDef);
                    writeRow(context, w, c, viewDef);
                    afterRow(context, w, c, viewDef);
                }
            }
        }
        
        super.afterRows(context, w, c, viewDef);
    }

    
    // ================================================================
    // Decode the messages
    // ================================================================
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        super.decode(context, component);
        
        
        UIDataView dataView = (component instanceof UIDataView) ? (UIDataView)component : null;
        if(null == dataView){
            // Might be the row component
            return;
        }
        
        // Look for a checkbox field
        DataModel dm = dataView.getDataModel();
        if(dm instanceof TabularDataModel) {
            TabularDataModel tbm = (TabularDataModel)dm;
            tbm.clearSelectedIds();
            
            // iterate through the rows of the current data page
            dataView.setRowIndex(-1);
            Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
            // dataView.getRows() returns rows shown on page
            int pageFirstIndex = dataView.getFirst();
            int pageRowCount = dataView.getRows();
            int pageEndPageIndex = pageRowCount < 0 ? pageFirstIndex : pageFirstIndex+pageRowCount;
            for (int rowIndex = pageFirstIndex; rowIndex < pageEndPageIndex; rowIndex++) {
                dataView.setRowIndex(rowIndex);
                if(!dataView.isRowAvailable())
                    break;
                
                // Note _colcbox is UIViewColumn.INTERNAL_CHECKBOX_ID
                String cboxName = dataView.getClientId(context) + ":_colcbox"; // $NON-NLS-1$ $NON-NLS-2$
                
                String cboxValue = params.get(cboxName);
                if (StringUtil.isNotEmpty(cboxValue) ) {
                    if (!StringUtil.isFalseValue(cboxValue)) { // !"false".equals
                        tbm.addSelectedId(cboxValue);
                    }
                }
            }
            dataView.setRowIndex(-1);
        }
    }
    
    // ================================================================
    // Body
    // ================================================================

    
    
    @Override
    protected void writeContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("table",c); // $NON-NLS-1$
        writeTableTagAttributes(context, w, c, viewDef);
        newLine(w);

        // Write the column headers
        writeColumnHeaders(context, w, c, viewDef);
        
        // And the rows
        int first = c.getFirst();
        int count = c.getRows();
        w.startElement("tbody",c); // $NON-NLS-1$
        writeRows(context, w, c, viewDef, first, count);
        w.endElement("tbody"); // $NON-NLS-1$
        
        w.endElement("table"); // $NON-NLS-1$
        newLine(w);
    }
    
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractDataViewRenderer#writeDataView(javax.faces.context.FacesContext, javax.faces.context.ResponseWriter, com.ibm.xsp.extlib.component.data.AbstractDataView, com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractDataViewRenderer.ViewDefinition)
     */
    @Override
    protected void writeDataView(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        
        super.writeDataView(context, w, c, viewDef);
        if(viewDef.isInfiniteScroll)
        {
            throw new IOException("infiniteScroll property has limited support for this control on this version. Please check documentation.."); // $NLX-DataViewRenderer.infiniteScrollpropertyhaslimiteds-1$
        }
    }


    protected void writeTableTagAttributes(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.writeAttribute("id", c.getAjaxContainerClientId(context), null); // $NON-NLS-1$
        String tc = (String)getProperty(PROP_TABLECLASS);
        if(StringUtil.isNotEmpty(tc)) {
            w.writeAttribute("class", tc, null); // $NON-NLS-1$
        }
        w.writeAttribute("border", "0", null); // $NON-NLS-1$
        w.writeAttribute("cellspacing", "0", null); // $NON-NLS-1$
        w.writeAttribute("cellpadding", "0", null); // $NON-NLS-1$

        // Accessibility
        w.writeAttribute("role", "grid", null); // $NON-NLS-1$ $NON-NLS-2$

        // Summary
        String summary = ((UIDataView)c).getSummary();
        if(StringUtil.isEmpty(summary)){
            summary = "A collection of documents with a summary shown for each document"; // $NLS-DataViewRenderer.Tablesummary-1$
        }
        w.writeAttribute("summary", summary, null); //$NON-NLS-1$

        // ARIA Label
        String label = ((UIDataView)c).getAriaLabel();
        if (StringUtil.isNotEmpty(label)) {
            w.writeAttribute("aria-label", label, null); // $NON-NLS-1$
        }
    }
    protected void writeTableRowTagAttributes(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        DataModel dm = viewDef.dataModel;
        String styleClass = c.getRowStyleClass();
        if(StringUtil.isEmpty(styleClass)) {
            styleClass = (String)getProperty(PROP_TABLEROWCLASS); 
        }
        if(dm.getRowIndex()==viewDef.first) {
            styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_TABLEFIRSTROWCLASS),styleClass);
        }
        if(dm.getRowIndex()==(viewDef.first+viewDef.rows-1)) {
            styleClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_TABLELASTROWCLASS),styleClass);
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        String style = c.getRowStyle();
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
    }
    protected void writeTableCategoryRowTagAttributes(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        writeTableRowTagAttributes(context, w, c, viewDef);
//      String styleClass = c.getRowStyleClass();
//      if(StringUtil.isNotEmpty(styleClass)) {
//          w.writeAttribute("class", styleClass, null);
//      }
//      String style = c.getRowStyle();
//      if(StringUtil.isNotEmpty(style)) {
//          w.writeAttribute("style", style, null);
//      }
    }
    

    // ================================================================
    // Columns headers
    // ================================================================

    protected void writeColumnHeaders(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(!viewDef.columnTitles) {
            return;
        }
        w.startElement("thead",c); // $NON-NLS-1$
        w.startElement("tr",c); // $NON-NLS-1$
        String clazz = (String)getProperty(PROP_TABLEHDRROWCLASS);
        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        }

        boolean multiColumn = viewDef.multiColumnCount>1;
        if(multiColumn) {
            // TODO it seems likely that multiColumn display cannot be made accessible,
            // may need to remove this feature.
            w.startElement("th",c); // $NON-NLS-1$
            int colSpan = (viewDef.nColumns)*(viewDef.multiColumnCount);
            w.writeAttribute("colspan",colSpan,null); // $NON-NLS-1$
            w.startElement("table",c); // $NON-NLS-1$
            String summaryForNthDocumentInRow = "Subsection for each document"; // $NLS-DataViewRenderer.SubtableSummary-1$
            w.writeAttribute("summary", summaryForNthDocumentInRow, null); // $NON-NLS-1$
            w.startElement("thead",c); // $NON-NLS-1$
            w.startElement("tr",c); // $NON-NLS-1$
        }
        
        writeColumnHeadersContent(context, w, c, viewDef);

        if(multiColumn) {
            w.endElement("tr"); // $NON-NLS-1$
            w.endElement("thead"); // $NON-NLS-1$
            w.endElement("table"); // $NON-NLS-1$
            w.endElement("th"); // $NON-NLS-1$
        }

        w.endElement("tr"); // $NON-NLS-1$
        w.endElement("thead"); // $NON-NLS-1$
    }
    protected void writeColumnHeadersContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        int idx = 0;
        
        // Add the checkbox columns
        if(viewDef.hasCheckBoxColumn) {
            writeHeaderCheckBoxColumn(context, w, c, viewDef);
            idx++;
        }
        
        // Add an empty column for the icon
        if(viewDef.hasIconColumn) {
            // Just an empty column
            writeColumnHeader(context, w, c, viewDef, null, idx++);
        }
        
        // Write the summary column
        writeColumnHeader(context, w, c, viewDef, viewDef.summaryColumn, idx++);
        
        // Then the extra columns
        if(viewDef.hasExtraColumns) {
            List<ExtraColumn> extraColumns = viewDef.extraColumns;
            int count = extraColumns.size();
            for(int i=0; i<count; i++) {
                ExtraColumn col = extraColumns.get(i);
                writeColumnHeader(context, w, c, viewDef, col, idx++);
            }
        }

        // Add an empty column for the toggle twistie
        if (viewDef.collapsibleDetails) {
            // Just an empty column
            writeColumnHeader(context, w, c, viewDef, null, idx++);
        }

    }
    protected void writeHeaderCheckBoxColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("th",c); // $NON-NLS-1$
        w.writeAttribute("scope", "col", null); // $NON-NLS-1$ $NON-NLS-2$
        String colClazz = (String)getProperty(PROP_TABLEFIRSTCELLCLASS);
        if(StringUtil.isNotEmpty(colClazz)) {
            w.writeAttribute("class",colClazz,null); // $NON-NLS-1$
        }
        if(viewDef.hasHeaderCheckBoxColumn) {
            String fldName = c.getClientId(context)+":_hdrcbox"; // $NON-NLS-1$
            w.startElement("input",c); // $NON-NLS-1$
            w.writeAttribute("type","checkbox",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("name",fldName,null); // $NON-NLS-1$
            w.writeAttribute("id",fldName,null); // $NON-NLS-1$
            // Specify title attribute for accessibility
            String title = "Select all rows"; // $NLS-DataViewRenderer_HeaderCheckboxTitle-1$
            w.writeAttribute("title",title,null); // $NON-NLS-1$
            
            StringBuilder sBuf = new StringBuilder(256); //$NON-NLS-1$
            sBuf.append("XSP.attachViewColumnCheckboxToggler("); //$NON-NLS-1$
            JavaScriptUtil.addString(sBuf, c.getClientId(context));
            sBuf.append(", "); //$NON-NLS-1$
            JavaScriptUtil.addString(sBuf, c.getClientId(context));
            sBuf.append("); "); //$NON-NLS-1$
            
            JavaScriptUtil.addScriptOnLoad(sBuf.toString());
            
        }
        
        w.endElement("th"); // $NON-NLS-1$
    }

    protected void writeColumnHeader(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, ValueColumn vc, int colIndex) throws IOException {
        // Write the summary column
        w.startElement("th",c); // $NON-NLS-1$
        w.writeAttribute("scope", "col", null); // $NON-NLS-1$ $NON-NLS-2$

        String style = ExtLibUtil.concatStyles((String)getProperty(PROP_TABLEHDRCOLSTYLE),vc!=null?vc.getHeaderStyle():null);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String clazz = ExtLibUtil.concatStyleClasses((String)getProperty(colIndex==0 ? PROP_TABLEHDRFIRSTCOLCLASS : PROP_TABLEHDRCOLCLASS),vc!=null?vc.getHeaderStyleClass():null);
        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        }
        if(vc!=null) {
            String colTitle = vc.getColumnTitle();
            if(StringUtil.isNotEmpty(colTitle)) {
                w.startElement("span",c); // $NON-NLS-1$
                String colName = vc.getColumnName();
                // Kind of hard coded here, which is not clean...
                boolean dominoStyleIcons = viewDef.dataModel instanceof DominoViewDataModel;
                boolean sortable = false;
                if(StringUtil.isNotEmpty(colName)) {
                    sortable = isColumnSortable(context, c, viewDef, colName);
                }
                
                
                
                if(sortable) {
                    String linkId = c.getClientId(context) + SORT_DELIMITER + colName;
                    w.startElement("a",c);
                    w.writeAttribute("id",linkId,null); // $NON-NLS-1$
                    
                    String headerTitle = vc.getHeaderLinkTitle();
                    if(StringUtil.isEmpty(headerTitle)){
                        headerTitle = "Click to reverse sort";  // $NLS-DataViewRenderer_SortLinkTooltip_ClickToReverseSort-1$
                    }
                    //adding the Header accessibility title
                    w.writeAttribute("title", headerTitle, null); // $NON-NLS-1$
                    
                    if(!dominoStyleIcons) {
                        int sortState = getColumnSortState(context, c, viewDef, colName);
                        if(sortState==TabularDataModel.RESORT_ASCENDING) {
                            String clazz2 = (String)getProperty(PROP_TABLEHDRCOLLKASCCLASS);
                            if(StringUtil.isNotEmpty(clazz2)) {
                                w.writeAttribute("class",clazz2,null); // $NON-NLS-1$
                            }
                        } else if(sortState==TabularDataModel.RESORT_DESCENDING) {
                            String clazz2 = (String)getProperty(PROP_TABLEHDRCOLLKDESCLASS);
                            if(StringUtil.isNotEmpty(clazz2)) {
                                w.writeAttribute("class",clazz2,null); // $NON-NLS-1$
                            }
                        }
                    }
                    w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                    //LHEY97CCSZ adding the role=button
                    w.writeAttribute("role", "button", null); // $NON-NLS-1$ // $NON-NLS-2$
                    setupSubmitOnClick(context, c, linkId, linkId, null);
                }
                
                
                
                boolean multiColumn = viewDef.multiColumnCount>1;
                if(!multiColumn || sortable) {
                    w.writeText(colTitle,null);
                }
                if(sortable) {
                    w.endElement("a");
                }
                if(dominoStyleIcons) {
                    int dominoSortIconCode = getDominoSortIconCode(context, c, viewDef, colName);
                    String src = getDominoSortIcon(context, c, viewDef, dominoSortIconCode);
                    if(StringUtil.isNotEmpty(src)) {
                        w.startElement("img",c); // $NON-NLS-1$
                        w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,src),null); // $NON-NLS-1$
                        
                        String sortIconAlt = getDominoSortAlt(context, c, viewDef, dominoSortIconCode);
                        if( ExtLibRenderUtil.isAltPresent(sortIconAlt) ){
                            w.writeAttribute("alt", sortIconAlt, null); //$NON-NLS-1$
                        }
                        String sortIconWidth = (String) getProperty(PROP_TABLEHDRCOLIMAGE_SORT_WIDTH);
                        if( StringUtil.isNotEmpty(sortIconWidth) ){
                            w.writeAttribute("width", sortIconWidth, null); //$NON-NLS-1$
                        }
                        String sortIconHeight = (String) getProperty(PROP_TABLEHDRCOLIMAGE_SORT_HEIGHT);
                        if( StringUtil.isNotEmpty(sortIconHeight) ){
                            w.writeAttribute("height", sortIconHeight, null); //$NON-NLS-1$
                        }
                        w.endElement("img"); // $NON-NLS-1$
                    }
                }
                w.endElement("span"); // $NON-NLS-1$
            }
        }
        w.endElement("th"); // $NON-NLS-1$
    }

    
    // ================================================================
    // View rows
    // ================================================================

    @Override
    protected void writeRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, int first, int rows) throws IOException {
        super.writeRows(context, w, c, viewDef, first, rows);
        
        // Ensure that the current row is properly closed 
        closeOpenedRow(context, w, c, viewDef);
    }
    
    @Override
    protected void writeRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(isCategoryRow(context, c, viewDef)) {
            closeOpenedRow(context, w, c, viewDef);
            if(viewDef.hasCategoryRow) {
                writeCategoryRow(context, w, c, viewDef);
            }
        } else if(isTotalRow(context, c, viewDef)) {
            closeOpenedRow(context, w, c, viewDef);
            writeTotalRow(context, w, c, viewDef);
        } else {
            // Close the current if we exceed the number of desired columns
            if(viewDef.currentColumn>=viewDef.multiColumnCount) {
                closeOpenedRow(context, w, c, viewDef);
            }
            
            writeStandardRow(context, w, c, viewDef);
        }
    }

    protected void closeOpenedRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // If column was emitted, close it
        if(viewDef.currentColumn>0) {
            w.endElement("tr"); // $NON-NLS-1$
            newLine(w);
            viewDef.currentColumn = 0;
        }
    }
    
    protected void writeCategoryRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("tr",c); // $NON-NLS-1$
        w.writeAttribute("role", "row", null); // $NON-NLS-1$ $NON-NLS-2$
        String id = viewDef.dataView.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID; 
        w.writeAttribute("id", id, null); // $NON-NLS-1$
        
        // Write the row style attributes
        writeTableCategoryRowTagAttributes(context, w, c, viewDef);
        
        // Write the data column
        writeCategoryColumn(context, w, c, viewDef);
    }
    protected void writeCategoryColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("th",c); // $NON-NLS-1$
        w.writeAttribute("scope", "row", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
        
        int colSpan = (viewDef.nColumns)*(viewDef.multiColumnCount);
        if(colSpan>1) {
            w.writeAttribute("colspan",colSpan,null); // $NON-NLS-1$
        }
        w.startElement("h4",c); // $NON-NLS-1$
        int categoryListIndex = findCategoryListIndex(context, c, viewDef, viewDef.categoryColumns);
        CategoryColumn categoryColumn = null;
        if( -1 != categoryListIndex){ 
            categoryColumn = viewDef.categoryColumns.get(categoryListIndex);
        }
        if(categoryColumn!=null) {
            String styleClass = categoryColumn.getStyleClass();
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            int indentLevel = getIndentLevel(viewDef);
            String indentStyle = getIndentStyle(context,c,viewDef,categoryListIndex, indentLevel);
            String style = categoryColumn.getStyle();
            style = ExtLibUtil.concatStyles(indentStyle,style);
            // Ensure that the h4 margin is reset to 0
            style = ExtLibUtil.concatStyles("margin:0;",style); // $NON-NLS-1$
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        } else { 
            // Ensure that the h4 margin is reset to 0
            w.writeAttribute("style","margin: 0",null); // $NON-NLS-1$ $NON-NLS-2$
        }
        // Expand/collapse icon
        if(viewDef.collapsibleCategory) {
            writeExpandCollapseIcon(context, w, c, viewDef);
        }
        if(categoryColumn!=null) {
            UIComponent categoryFacet = getCategoryRowFacet(c,categoryListIndex); 
            if(categoryFacet!=null) {
                FacesUtil.renderChildren(context, categoryFacet);
            }else{
                writeColumnValue(context, w, c, viewDef, categoryColumn);
            }
        }
        w.endElement("h4"); // $NON-NLS-1$
        w.endElement("th"); // $NON-NLS-1$
        
        // We mark all the columns as being written
        viewDef.currentColumn = viewDef.multiColumnCount;       
    }
    /**
     * @param context
     * @param c
     * @param viewDef
     * @param categoryColumns
     * @return
     */
    protected int findCategoryListIndex(FacesContext context, AbstractDataView c,
            ViewDefinition viewDef, List<CategoryColumn> categoryColumns) {
        
        // note this assumes the current row is a category row.
        if( null == categoryColumns ){
            // this is a category row, but there are no category columns.
            return -1;
        }
        
        TabularDataModel tdm;
        DataModel dm = viewDef.dataModel;
        if(dm instanceof TabularDataModel) {
            tdm = (TabularDataModel)dm;
        }else{
            // only know how to figure out categorizations for tabular data
            return -1;
        }

        ViewRowData viewRowData = null; 
        Object rowData = tdm.getRowData();
        if (rowData instanceof ViewRowData){
            viewRowData = (ViewRowData)rowData;
        }
        
        // first attempt to find a match based on columnName
        int length = categoryColumns.size();
        for (int i = 0; i < length; i++) {
            CategoryColumn categoryColumn = categoryColumns.get(i);
            String columnName = categoryColumn.getColumnName();
            if( StringUtil.isEmpty(columnName) ){
                // columnName is required, but was not present in early openNTF extlib releases
                continue;
            }
            // verify the column is categorized in the view Design Element
            // (says nothing about this category row).
            if( ! tdm.isColumnCategorized(columnName) ){
                continue;
            }
            
            // if some value is present under the columnName in this categorizedRow,
            // then this column is the categorized column for the category row
            Object columnValue = (null == viewRowData)? null : viewRowData.getColumnValue(columnName);
            boolean isColumnValuePresent = null != columnValue && ! StringUtil.isEmpty(columnValue.toString());
            if( isColumnValuePresent ){
                return i;
            }
        }
        // Only empty values found when checking column values by columnName,
        // maybe this is the "Not Categorized" category - where documents
        // that don't have any value in the category column are placed.
        
        // a category row's position string will be like "outerCatColValue2.innerCatColValue5"
        // The number of segments in the position string will correspond 
        // to the nth category column in the view design element.
        String rowPosition = tdm.getRowPosition();
        int positionDotCount = countDots(rowPosition);
        
        for (int i = 0; i < length; i++) {
            CategoryColumn categoryColumn = categoryColumns.get(i);
            String columnName = categoryColumn.getColumnName();
            if( StringUtil.isEmpty(columnName) || ! tdm.isColumnCategorized(columnName)){
                continue;
            }
            
            // The category column index is, in the domino view design element
            // this is the nth category column - that is, the index excluding
            // non-category columns.
            int categoryColumnIndex = tdm.getCategoryColumnIndex(columnName);
            if( Integer.MIN_VALUE != categoryColumnIndex ){
                if( positionDotCount == categoryColumnIndex ){
                    return i;
                }
            }
        }
        // probably the columnName is missing
        // attempt to match by categoryIndentLevel,
        // which will only work if the columns in the XPage source exactly
        // match the column indexes in the Domino view design element,
        // and is likely to display wrong values.
        int indentLevel = tdm.getCategoryIndentLevel();
        if( indentLevel>= 0 && indentLevel < length ){
            return indentLevel;
        }
        
        if( length == 1 && StringUtil.isEmpty(categoryColumns.get(0).getColumnName()) ){
            // there is only a single categoryColumn, created 
            // by the UIDataView.buildContents method, 
            // display all category rows using that column.
            return 0;
        }
        
        // TODO If the category row also contains a total value
        // this is incorrectly displaying the total number instead
        // of the category value.
        return -1;
    }
    
    /**
     * @param rowPosition
     * @return
     */
    private int countDots(String rowPosition) {
        int count = 0;
        
        int dotIndex = rowPosition.indexOf('.');
        while( -1 != dotIndex ){
            count++;
            dotIndex = rowPosition.indexOf('.', dotIndex+1);
        }
        return count;
    }

    protected void writeTotalRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
//      w.startElement("tr",c);
//      String id = viewDef.dataView.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID; 
//      w.writeAttribute("id", id, null);
//      w.endElement("tr");
//      newLine(w);
    }
    
    protected void writeStandardRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // Start a new TR if we start a new row
        if(viewDef.currentColumn==0) {
            w.startElement("tr",c); // $NON-NLS-1$
            w.writeAttribute("role", "row", null); // $NON-NLS-1$ $NON-NLS-2$
            String id = viewDef.dataView.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID; 
            w.writeAttribute("id", id, null); // $NON-NLS-1$
            // Write the row style attributes
            writeTableRowTagAttributes(context, w, c, viewDef);
        }
        
        if( viewDef.summaryOrDetailVisible && viewDef.rowDetailVisible ){
            writeDetailRow(context, w, c, viewDef);
        }else{
            // Write the Checkbox column
            if(viewDef.hasCheckBoxColumn) {
                writeCheckBoxColumn(context, w, c, viewDef);
            }

            // Write the Icon column
            if(viewDef.hasIconColumn) {
                writeIconColumn(context, w, c, viewDef);
            }
            
            // Write the data column
            writeDataColumn(context, w, c, viewDef);
            
            // Write the extra columns
            if(viewDef.hasExtraColumns) {
                writeExtraColumns(context, w, c, viewDef);
            }
            
            // Write the hide/show icon
            writeShowHideDetailCell(context, w, c, viewDef);
        }
        
        // Leave the row opened for more columns....
        //w.endElement("tr");
        //newLine(w);
        
        viewDef.currentColumn++;
    }
    protected void writeDetailRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        
        boolean displayIconCell = viewDef.hasIconColumn;
        boolean displayShowHideCell = viewDef.collapsibleDetails;
        
        if( displayIconCell ){
            // empty icon column.
            w.startElement("td", c); // $NON-NLS-1$
            w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
            w.endElement("td"); // $NON-NLS-1$
        }
        
        // start the detail cell
        w.startElement("td",c); // $NON-NLS-1$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$

        // colspan the td
        int colSpan = viewDef.nColumns + (displayIconCell? -1 : 0) + (displayShowHideCell? -1 : 0);
        if(colSpan>1) {
            w.writeAttribute("colspan",colSpan,null); // $NON-NLS-1$
        }
        
        // Enclosing divs
        w.startElement("div",c); // $NON-NLS-1$
        
        if(true) {
            int level = getColumnIndentLevel(context, c, viewDef);
            int catLevel = Math.max(0, viewDef.categoryCount-1);
            String style = getIndentStyle(context, c, viewDef, level+catLevel);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
        }
        
        writeDetail(context, w, c, viewDef);
        
        w.endElement("div"); // $NON-NLS-1$
        
        w.endElement("td"); // $NON-NLS-1$
        // end the detail cell
        
        if( displayShowHideCell ){
            // Write the hide/show icon
            writeShowHideDetailCell(context, w, c, viewDef);
        }
        
    }
    
    protected void writeCheckBoxColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("td",c); // $NON-NLS-1$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
        String colClazz = (String)getProperty(PROP_TABLEFIRSTCELLCLASS);
        if(StringUtil.isNotEmpty(colClazz)) {
            w.writeAttribute("class",colClazz,null); // $NON-NLS-1$
        }
        String value = null;
        if(viewDef.dataModel instanceof TabularDataModel) {
            value = ((TabularDataModel)viewDef.dataModel).getRowId();
        }
        if(StringUtil.isNotEmpty(value)) {
            w.startElement("input",c); // $NON-NLS-1$
            w.writeAttribute("type","checkbox",null); // $NON-NLS-1$ $NON-NLS-2$
            // For a group of inputs, the name must be the same for each element in
            // order to establish the "grouping" of the elements
            // the ID is different though.. it should be unique regardless of grouping or not
            w.writeAttribute("name",viewDef.checkboxFieldNamePrefix + ":" + c.getRowIndex() + viewDef.checkboxFieldNameSuffix, null); // $NON-NLS-1$
            // See xspClientDojo.js _toggleViewColumnCheckBoxes function - we can put row index only in the middle
            String id = viewDef.checkboxFieldNamePrefix + ":" + c.getRowIndex() + viewDef.checkboxFieldNameSuffix; // $NON-NLS-1$
            w.writeAttribute("id",id,null); // $NON-NLS-1$
            w.writeAttribute("value",value,null); // $NON-NLS-1$
            // Specify title attribute for accessibility
            String title = "Select the current row"; // $NLS-DataViewRenderer_RowCheckboxTitle-1$
            w.writeAttribute("title",title,null); // $NON-NLS-1$

            w.startElement("label", null); // $NON-NLS-1$
            w.writeAttribute("for", id, null); // $NON-NLS-1$
            w.writeAttribute("style", "display:none", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeText(title, null);
            w.endElement("label"); // $NON-NLS-1$
        } else {
            // Just nothing...
        }
        
        w.endElement("td"); // $NON-NLS-1$
    }
    
    protected void writeIconColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("td",c); // $NON-NLS-1$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$

        if(viewDef.iconColumn!=null) {
            String colStyle = viewDef.iconColumn.getStyle();
            if(StringUtil.isNotEmpty(colStyle)) {
                w.writeAttribute("style",colStyle,null); // $NON-NLS-1$
            }
            String colClazz = viewDef.iconColumn.getStyleClass();
            if(!viewDef.hasCheckBoxColumn) {
                colClazz = ExtLibUtil.concatStyleClasses(colClazz,(String)getProperty(PROP_TABLEFIRSTCELLCLASS));
            }
            if(StringUtil.isNotEmpty(colClazz)) {
                w.writeAttribute("class",colClazz,null); // $NON-NLS-1$
            }
        }

        if(viewDef.iconFacet!=null) {
            FacesUtil.renderComponent(context, viewDef.iconFacet);
        } else {
            IconEntry entry = findIcon(context, w, c, viewDef);
            if(entry!=null) {
                String src = entry.getUrl();
                if(StringUtil.isNotEmpty(src)) {
                    w.startElement("img",c); // $NON-NLS-1$
                    if(StringUtil.isNotEmpty(src)) {
                        w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,src),null); // $NON-NLS-1$
                    }
                    
                    String alt = entry.getAlt();
                    String title = entry.getTitle();
                    if(ExtLibRenderUtil.isAltPresent(alt)){
                        w.writeAttribute("alt", alt, null); //$NON-NLS-1$
                        if(StringUtil.isEmpty(title)){
                            w.writeAttribute("title", alt, null); //$NON-NLS-1$
                        }
                    }
                    if(StringUtil.isNotEmpty(title)){
                        w.writeAttribute("title", title, null); //$NON-NLS-1$
                    }
                    
                    String iconStyle = entry.getStyle();
                    if(StringUtil.isNotEmpty(iconStyle)) {
                        w.writeAttribute("style",iconStyle,null); // $NON-NLS-1$
                    }
                    String iconClass = ExtLibUtil.concatStyleClasses(entry.getStyleClass(),(String)getProperty(PROP_TABLEFIRSTCELLCLASS));
                    if(StringUtil.isNotEmpty(iconClass)) {
                        w.writeAttribute("class",iconClass,null); // $NON-NLS-1$
                    }
                    w.endElement("img"); // $NON-NLS-1$
                }
            }
        }
        w.endElement("td"); // $NON-NLS-1$
    }
    protected IconEntry findIcon(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        List<IconEntry> icons = viewDef.iconColumn.getIcons(); 
        if(icons!=null && !icons.isEmpty()) {
            // Get the value
            Object value = getColumnValue(context, c, viewDef, viewDef.iconColumn);
            for(IconEntry icon: icons) {
                // Check if the value is equal
                if(StringUtil.equals(value, icon.getSelectedValue())) {
                    return icon;
                }
                // Else, check if it is selected
                if(icon.isSelected()) {
                    return icon;
                }
            }
        }
        return null;
    }
    
    protected void writeDataColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // Prevent the column to be diisplayed when no data exists
        if(!viewDef.hasSummary && !viewDef.hasDetail) {
            return;
        }

        w.startElement("td",c); // $NON-NLS-1$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
        int width = 100/viewDef.multiColumnCount;
        w.writeAttribute("style","width: "+width+"%",null); // $NON-NLS-1$ $NON-NLS-2$
        if(!viewDef.hasCheckBoxColumn && !viewDef.hasIconColumn) {
            String clazz = (String)getProperty(PROP_TABLEFIRSTCELLCLASS);
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class",clazz,null); // $NON-NLS-1$
            }
        }

        // Enclosing divs
        w.startElement("div",c); // $NON-NLS-1$
        
        if(true) {
            int level = getColumnIndentLevel(context, c, viewDef);
            int catLevel = Math.max(0, viewDef.categoryCount-1);
            String style = getIndentStyle(context, c, viewDef, level+catLevel);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
        }
        
        // Write the summary data
        writeSummary(context, w, c, viewDef);
        
        // Write the details
        writeDetail(context, w, c, viewDef);
        
        w.endElement("div"); // $NON-NLS-1$
        w.endElement("td"); // $NON-NLS-1$
    }
    protected String getIndentStyle(FacesContext context, AbstractDataView c, ViewDefinition viewDef, int level) throws IOException {
        if(level>0) {
            Integer indentPx = (Integer)getProperty(PROP_TABLEROWINDENTPX);
            if(indentPx>0) {
            	String paddingDir = DirLangUtil.isRTL(c) ? "padding-right:" : "padding-left:";
                String style = paddingDir + (level * indentPx) + "px !important"; // $NON-NLS-1$ $NON-NLS-2$
                return style;
            }
        }
        return null;
    }
    protected String getIndentStyle(FacesContext context, AbstractDataView c, ViewDefinition viewDef, int level, int sublevel) throws IOException {
    	// Ref SPR# PHAN9E3FUG Subcategories created from backslashes in Categories need to be handled better in the Data View
    	FacesContextEx ctx = (context instanceof FacesContextEx) ? (FacesContextEx)context : null; 
		if (ctx != null) {
        	boolean doAutoIndent = true;
			String propAutoIndent = ctx.getProperty("xsp.domino.view.embeddedsubcategories.autoindent"); // $NON-NLS-1$
			if (!StringUtil.isEmpty(propAutoIndent)) {
				doAutoIndent = Boolean.parseBoolean(propAutoIndent);
			}
			if (doAutoIndent) {
				if(level>0 || sublevel>0) {
		            Integer indentPxSub = (Integer)getProperty(PROP_TABLEROWINDENTPX);            
		            if(indentPxSub>0) {
		            	// Category column width needs to be wider than the subcategory width
		            	// These properties (PROP_TABLEROWINDENTPX etc) do not appear to be configurable
		            	// WITHOUT subclassing Java classes? Should be handled by themes! 
		            	// Quick remedy is just to increase the width by an arbitrary number of pixels
		                Integer indentPxCol = indentPxSub + 10; 
		                int padValue = (indentPxCol * level) + (indentPxSub * sublevel);
		                String paddingDir = DirLangUtil.isRTL(c) ? "padding-right:" : "padding-left:";
		                String style = paddingDir + (padValue) + "px !important"; // $NON-NLS-1$ $NON-NLS-2$
		                return style;
		            }
		        }
			} else {
				return getIndentStyle(context, c, viewDef, level);
			}
		}
        return null;
    }
    protected int getIndentLevel(ViewDefinition viewDef) throws IOException {
    	 // assumes that the caller 
   	     int level = 0;    	
    	 TabularDataModel tdm;
         DataModel dm = viewDef.dataModel;
         if(dm instanceof TabularDataModel) {
             tdm = (TabularDataModel)dm;
             level = tdm.getColumnIndentLevel();
         }
        return level;
    }
    protected void writeShowHideDetailCell(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(viewDef.collapsibleDetails) {
            w.startElement("td",c); // $NON-NLS-1$
            String clazz = (String)getProperty(PROP_TABLELASTCELLCLASS);
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class",clazz,null); // $NON-NLS-1$
            }
            if(viewDef.hasSummary && viewDef.hasDetail && !viewDef.rowDisableHideRow) {
                w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
                writeShowHideDetailContent(context, w, c, viewDef);
            }
            w.endElement("td"); // $NON-NLS-1$
        }
    }
    
    protected void writeExtraColumns(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        List<ExtraColumn> extraColumns = viewDef.extraColumns;
        int count = extraColumns.size();
        for(int i=0; i<count; i++) {
            ExtraColumn col = extraColumns.get(i);
            writeExtraColumn(context, w, c, viewDef, col, i);
        }
    }
    protected void writeExtraColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, ExtraColumn col, int colIdx) throws IOException {
        w.startElement("td",c); // $NON-NLS-1$
        String value = formatColumnValue(context, c, viewDef, col);
        if(!StringUtil.isEmpty(value)) {
            w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        UIComponent facet = getExtraFacet(c,colIdx);
        if(facet!=null) {
            // TODO should use the column complex-type's style, and styleClass
            // even when using the facet for the column contents.
            FacesUtil.renderComponent(context, facet);
        } else {
            String style = col.getStyle();
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            String clazz = col.getStyleClass();
            if(StringUtil.isEmpty(clazz)) {
                clazz = (String)getProperty(PROP_TABLEROWEXTRA);
            }
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            }
            // Write a link if there is an href
            String href = getColumnUrl(context, c, viewDef, col);
            if(StringUtil.isNotEmpty(href)) {
                w.startElement("a",c);
                RenderUtil.writeLinkAttribute(context,w,href);
                // Write the title if there is any
                String title = getTitle(context, c, viewDef, col);
                
                if(StringUtil.isNotEmpty(title)) {
                    w.writeAttribute("title", title,null); // $NON-NLS-1$
                }
            }
            
            writeColumnValue(context, w, c, viewDef, col);
            if(StringUtil.isNotEmpty(href)) {
                
                w.endElement("a");
            }
        }
        w.endElement("td"); // $NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.renderkit.html_extended.data.DataSourceIteratorRenderer#writeColumnEmptyValue(javax.faces.context.FacesContext, javax.faces.context.ResponseWriter, com.ibm.xsp.extlib.component.data.UIDataSourceIterator, com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractDataViewRenderer.ViewDefinition, com.ibm.xsp.extlib.component.data.ValueColumn)
     */
    @Override
    protected void writeColumnEmptyValue(FacesContext context,
            ResponseWriter w, UIDataSourceIterator c, ViewDefinition viewDef,
            ValueColumn vc) throws IOException {
        if( vc instanceof CategoryColumn ){
            // For SPR#DEGN8NUCN3, empty categorized columns should say "Not Categorized", 
            // same as in the DataTable and ViewPanel controls.
            // "Not Categorized"
            // reusing the translated string from the xpages runtime
            String emptyCategoryColumnValue = com.ibm.xsp.extsn.ResourceHandler
                    .getString("DataTableRendererEx.NotCategorized"); //$NON-NLS-1$
            w.writeText(emptyCategoryColumnValue, null);
        }else{
            // For SPR#TWET98EATX, commenting out the addition of a space character
            // as in certain circumstances it creates a link out of the space.
            //Adding hidden style to the anchor to pass RPT 
            // &nbsp;
            //super.writeColumnEmptyValue(context, w, c, viewDef, vc);
            w.writeAttribute("style","display:none;",null); // $NON-NLS-1$ $NON-NLS-2
        }
    }
}