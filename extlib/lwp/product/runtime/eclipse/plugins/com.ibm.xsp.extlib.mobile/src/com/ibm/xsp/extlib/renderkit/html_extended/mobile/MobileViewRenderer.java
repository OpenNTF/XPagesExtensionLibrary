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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import lotus.domino.ViewEntry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.UniqueViewIdManager;
import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.*;
import com.ibm.xsp.extlib.component.image.IconEntry;
import com.ibm.xsp.extlib.renderkit.html_extended.data.DataViewRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.data.ViewRowDataOverride;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.renderkit.html_basic.FormRenderer;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * The MobileViewRenderer is a DataViewRenderer specifically tailored for
 * displaying a DataView control on mobile devices.
 * 
 * @author jpierma
 * 
 */
public class MobileViewRenderer extends DataViewRenderer {
    
    protected static final String ID_DETAIL = "_detail"; // $NON-NLS-1$

    public static final int PROP_CONTAINERTAG = 100;

    public static final int PROP_CONTAINERDOJOTYPE = 101;

    public static final int PROP_CONTAINERSTYLECLASS = 102;

    public static final int PROP_LISTTAG = 103;

    public static final int PROP_LISTDOJOTYPE = 104;

    public static final int PROP_LISTSTYLECLASS = 105;

    public static final int PROP_ITEMDIVSTYLE = 110;

    public static final int PROP_ITEMDIVSTYLECLASS = 111;

    public static final int PROP_ITEMTAG = 112;

    public static final int PROP_ITEMDOJOTYPE = 113;

    public static final int PROP_ITEMSTYLECLASS = 114;

    public static final int PROP_ITEMTRANSITION = 115;

    public static final int PROP_SUBHEADTAG = 120;

    public static final int PROP_SUBHEADDOJOTYPE = 121;

    public static final int PROP_SUBHEADDIVSTYLE = 122;

    public static final int PROP_SUBHEADSTYLECLASS = 123;

    public static final int PROP_SUBHEADSTYLECLASS_STD = 124;

    public static final int PROP_SUMMARYCOLSTYLE = 130;

    public static final int PROP_SUMMARYCOLSTYLECLASS = 131;

    public static final int PROP_CATEGORYCOLSTYLE = 132;

    public static final int PROP_CATEGORYCOLSTYLECLASS = 133;

    public static final int PROP_ICONCOLSTYLE = 134;

    public static final int PROP_ICONCOLSTYLECLASS = 135;

    public static final int PROP_EXTRACOLSTYLE = 136;

    public static final int PROP_EXTRACOLSTYLECLASS = 137;

    public static final int PROP_SUMMARYCOLTEXTSTYLECLASS = 138;

    public static final int PROP_CATEGORYROWSTYLECLASS = 139;

    public static final int PROP_DATAROWSTYLECLASS = 140;

    // public static final int PROP_CATEGORYROWSCRIPT = 141;
    public static final int PROP_DATAROWICONSTYLECLASS = 142;

    public static final int PROP_WRAPPERTAG = 160;

    public static final int PROP_WRAPPERDOJOTYPE = 161;

    public static final int PROP_WRAPPERSTYLECLASS = 162;

    private static final int PROP_WRAPPERSTYLECLASS_MULTIPLE = 164;

    public static final String HIDDEN = "hidden"; // $NON-NLS-1$

   public static final String SUBMIT_SCROLL = "$$xspscroll"; //$NON-NLS-1$
    
    // getProperty is used as a property dispatcher. The intended use of
    // getProperty
    // is to abstract away the direct use of a property in a method call. In
    // doing so,
    // we only have to change the value of a property in this method to modify
    // it
    // everywhere in the class. This is especially helpful if a new renderer
    // needs to
    // be created with device specific properties. Just override the ones that
    // need
    // new values and you're done.
    @Override
    protected Object getProperty(int prop) {
        switch (prop) {
        case PROP_CONTAINERTAG:
            return "ul"; // $NON-NLS-1$
        case PROP_CONTAINERDOJOTYPE:
            return "dojox.mobile.EdgeToEdgeList"; // $NON-NLS-1$
        case PROP_CONTAINERSTYLECLASS:
            return "mblDataView"; // $NON-NLS-1$

            // case PROP_HEADERDOJOTYPE: return "dojox.mobile.ListItem"; //
            // $NON-NLS-1$
            // case PROP_HEADERCLASS: return "mblVariableHeight"; // $NON-NLS-1$
        case PROP_HEADERSTYLE:
            return "height: 2em"; // $NON-NLS-1$
        case PROP_HEADERCLASS:
            return "mblVariableHeight mblListItem"; // $NON-NLS-1$
            // case PROP_FOOTERDOJOTYPE: return "dojox.mobile.ListItem"; //
            // $NON-NLS-1$
            // case PROP_FOOTERCLASS: return "mblVariableHeight"; // $NON-NLS-1$

        case PROP_FOOTERCLASS:
            return "mblVariableHeight mblListItem mblFooter"; // $NON-NLS-1$
        case PROP_FOOTER_PAGER_AREA_TAG:
            return "li"; // $NON-NLS-1$
        case PROP_LISTTAG:
            return "div"; // $NON-NLS-1$
        case PROP_ITEMDIVSTYLECLASS:
            return "mblListItemWrapper"; // $NON-NLS-1$
        case PROP_CATEGORYROWSTYLECLASS:
            return "mblCategoryRow"; // $NON-NLS-1$
        case PROP_DATAROWSTYLECLASS:
            return "mblDataRow"; // $NON-NLS-1$
        case PROP_DATAROWICONSTYLECLASS:
            return "mblDataRowIcon"; // $NON-NLS-1$
        case PROP_ITEMTAG:
            return "li"; // $NON-NLS-1$
        case PROP_ITEMDOJOTYPE:
            return "extlib.dijit.mobile.ListItem"; // $NON-NLS-1$
        case PROP_ITEMSTYLECLASS:
            return "mblVariableHeight"; // $NON-NLS-1$
        case PROP_ITEMTRANSITION:
            return "slide"; // $NON-NLS-1$
        case PROP_SUBHEADTAG:
            return "li"; // $NON-NLS-1$
        case PROP_SUBHEADDOJOTYPE:
            return ""; // $NON-NLS-1$
        case PROP_SUBHEADDIVSTYLE:
            return ""; // $NON-NLS-1$
        case PROP_SUBHEADSTYLECLASS:
            return "mblDataViewCategoryRow"; // $NON-NLS-1$
        case PROP_SUBHEADSTYLECLASS_STD:
            return "mblEdgeToEdgeCategory"; // $NON-NLS-1$
        case PROP_SUMMARYCOLSTYLE:
            return ""; // $NON-NLS-1$
        case PROP_SUMMARYCOLSTYLECLASS:
            return "mblDataViewTextTitle"; // $NON-NLS-1$
        case PROP_SUMMARYCOLTEXTSTYLECLASS:
            return "mblDataViewTextContent"; // $NON-NLS-1$
            // case PROP_CATEGORYROWSCRIPT: return "XSP.accordionifyCategory";

        case PROP_FOOTERSTYLE:
            return ""; // $NON-NLS-1$
        case PROP_FOOTERLEFTSTYLE:
            return "width:100%"; // $NON-NLS-1$
        case PROP_FOOTERRIGHTSTYLE:
            return "width:100%"; // $NON-NLS-1$
        case PROP_FOOTERMIDDLESTYLE:
            return "width:100%"; // $NON-NLS-1$

        case PROP_FOOTERLEFTCLASS:
            return "mblFooterText mblLeft"; // $NON-NLS-1$
        case PROP_FOOTERRIGHTCLASS:
            return "mblFooterText mblRight"; // $NON-NLS-1$
        case PROP_FOOTERMIDDLECLASS:
            return "mblFooterText mblCenter"; // $NON-NLS-1$
        case PROP_WRAPPERTAG:
            return "div"; // $NON-NLS-1$
        case PROP_WRAPPERDOJOTYPE:
            return "extlib.dijit.mobile.ScrollablePane"; // $NON-NLS-1$
        case PROP_WRAPPERSTYLECLASS:
            return "mblScrollablePane"; // $NON-NLS-1$
        case PROP_WRAPPERSTYLECLASS_MULTIPLE:
            return "mblScrollablePaneMultiple"; // $NON-NLS-1$

        }
        return super.getProperty(prop);
    }

    // ================================================================
    // Main data view container
    // ================================================================

    @Override
    protected void writeDataView(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        
        String style =  c.getStyle();
        String viewId = c.getClientId(context);
        String spId = "";
        
        if (viewDef.isInfiniteScroll) {
            //Add the dojo module
            ExtLibResources.addEncodeResource(context, ExtLibResources.extlibScrollablePane);
            String pane = (String) getProperty(PROP_WRAPPERTAG);
            startElement(w, pane, null, PROP_WRAPPERSTYLECLASS,
                    PROP_WRAPPERDOJOTYPE);
            
            
            w.writeAttribute("id", viewId, null);//$NON-NLS-1$
            spId = viewId;
            
            w.writeAttribute("dataRows", viewDef.rows, null);//$NON-NLS-1$
            //changing the id because the partial refresh requires the outermost id to be sent in the request.
            viewId+="_is";
            
            w.writeAttribute("dataViewId", viewId , null);//$NON-NLS-1$
            w.writeAttribute("roundCornerMask", "true", null);//$NON-NLS-1$
            w.writeAttribute("radius","5",null);//$NON-NLS-1$
            
            // adding the scrollPosition as a div attribute to pass it at the ScrollablePane.js
            String scroll = (String)context.getExternalContext().getRequestParameterMap().get(SUBMIT_SCROLL + "_" + spId);
            if(scroll != null) {
                w.writeAttribute("scrollToPos",scroll,null);//$NON-NLS-1$
            }
            
            
            ExternalContext ctx = context.getExternalContext();
            String servletPath = ctx.getRequestServletPath();
            
            if(servletPath != null) {
                w.writeAttribute("servletPath",servletPath,null);//$NON-NLS-1$
            }
            
            
            
            if(style != null) { 
                w.writeAttribute("style",style,null);//$NON-NLS-1$
                int hIndex = style.indexOf("height:");
                if(hIndex > -1 ) {
                    String hVal = style.substring(hIndex + "height:".length());
                    int commaIndex = hVal.indexOf(";");
                    if(commaIndex > 0) {
                        hVal = hVal.substring(0,commaIndex);
                    }
                    w.writeAttribute("height",hVal, null);//$NON-NLS-1$
                }
            }
           
        }
        String tag = (String) getProperty(PROP_CONTAINERTAG);
        startElement(w, tag, null, PROP_CONTAINERSTYLECLASS,
                PROP_CONTAINERDOJOTYPE);
        
        
        w.writeAttribute("id", viewId, null);//$NON-NLS-1$
        
        newLine(w);

        // Write the header
        boolean hasHeader = (Boolean) getProperty(PROP_HASHEADERFACET);
        if (hasHeader) {
            writeHeader(context, w, c, viewDef);
        }

        // And write the content
        UIComponent noRowsFacet = c.getFacet(AbstractDataView.FACET_NOROWS);
        if (!hasMoreRowsToRender(c) && noRowsFacet != null) {
            writeNoRowsContent(context, w, c, viewDef);
        }
        else {
            writeContent(context, w, c, viewDef);
        }

        // Write the footer
        boolean hasFooter = (Boolean) getProperty(PROP_HASFOOTERFACET);
        if (hasFooter) {
            writeFooter(context, w, c, viewDef);
        }

        w.endElement(tag);
        if (viewDef.isInfiniteScroll) {
            w.startElement(FacesRenderer.INPUT, null);
            w.writeAttribute(FacesRenderer.TYPE, HIDDEN, null);
            w.writeAttribute(FacesRenderer.NAME, SUBMIT_SCROLL + "_" + spId, null);
            w.writeAttribute(FacesRenderer.ID, spId + "_hif", null);
            w.endElement(FacesRenderer.INPUT);
            
            String pane = (String) getProperty(PROP_WRAPPERTAG);
            w.endElement(pane);
           
        }
        newLine(w);
    }

    @Override
    protected void writeContent(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        String tag = (String) getProperty(PROP_LISTTAG);
        startElement(w, tag, null, PROP_LISTSTYLECLASS, PROP_LISTDOJOTYPE);
        String aid = c.getAjaxContainerClientId(context);
        w.writeAttribute("id", aid, null); //$NON-NLS-1$
        newLine(w);

        // And the rows
        int first = c.getFirst();
        int count = c.getRows();
        writeRows(context, w, c, viewDef, first, count);

        w.endElement(tag);
        newLine(w);
    }

    @Override
    protected void writeRows(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef, int first, int rows)
            throws IOException {
        super.writeRows(context, w, c, viewDef, first, rows);
    }

    @Override
    protected void writeRow(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if (isCategoryRow(context, c, viewDef)) {
            if (viewDef.hasCategoryRow) {
                writeCategoryRow(context, w, c, viewDef);
            }
        }
        else if (isTotalRow(context, c, viewDef)) {
            writeTotalRow(context, w, c, viewDef);
        }
        else {
            writeStandardRow(context, w, c, viewDef);
        }
    }

    protected void writeRowStart(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        
        w.startElement("div", null); // $NON-NLS-1$
        boolean isCategoryRow = isCategoryRow(context, c, viewDef);
        String tableId = c.getClientId(context);
        String id = tableId + NamingContainer.SEPARATOR_CHAR
                + UIDataView.ROW_ID;

        String divStyleClass = (String) getProperty(PROP_ITEMDIVSTYLECLASS);
        if (isCategoryRow) {
            divStyleClass = ExtLibUtil.concatStyleClasses(divStyleClass,
                    (String) getProperty(PROP_CATEGORYROWSTYLECLASS));
            boolean expanded = isRowExpanded(context, c, viewDef);
            id += (expanded ? SHRINK_DELIMITER : EXPAND_DELIMITER)
                    + viewDef.rowPosition;
            
        }
        else {
            divStyleClass = ExtLibUtil.concatStyleClasses(divStyleClass,
                    (String) getProperty(PROP_DATAROWSTYLECLASS));
            if (viewDef.hasIconColumn) {
                divStyleClass = ExtLibUtil.concatStyleClasses(divStyleClass,
                        (String) getProperty(PROP_DATAROWICONSTYLECLASS));
            }
        }

        w.writeAttribute("id", id, null); // $NON-NLS-1$
        
        if (StringUtil.isNotEmpty(divStyleClass)) {
            w.writeAttribute("class", divStyleClass, null); // $NON-NLS-1$
        }
        String divStyle = (String) getProperty(PROP_ITEMDIVSTYLE);
        if (StringUtil.isNotEmpty(divStyle)) {
            w.writeAttribute("style", divStyle, null); // $NON-NLS-1$
        }
        if (isCategoryRow) {
            // add JavaScript to handle custom show/hide functionality
            setupSubmitOnClick(context, c, id, id, null);
            String tag = (String) getProperty(PROP_SUBHEADTAG);
            w.startElement(tag, null);

            String themeName = ((FacesContextEx) context).getStyleKit()
                    .getName();
            String styleClass;
            if (!themeName
                    .equalsIgnoreCase(ApplicationRenderer.IPHONE_THEME_NAME)
                    && !themeName
                            .equalsIgnoreCase(ApplicationRenderer.ANDROID_THEME_NAME)
                    && !themeName
                            .equalsIgnoreCase(ApplicationRenderer.BLACKBERRY_THEME_NAME)) {
                styleClass = (String) getProperty(PROP_SUBHEADSTYLECLASS_STD);
            }
            else {
                styleClass = (String) getProperty(PROP_SUBHEADSTYLECLASS);
            }

            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
        }
        else {
            // Dojo list item
            String tag = (String) getProperty(PROP_ITEMTAG);
            w.startElement(tag, null);
            String djType = (String) getProperty(PROP_ITEMDOJOTYPE);
            if (StringUtil.isNotEmpty(djType)) {
                w.writeAttribute("dojoType", djType, null); // $NON-NLS-1$
            }
            String styleClass = (String) getProperty(PROP_ITEMSTYLECLASS);
            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            String transition = (String) getProperty(PROP_ITEMTRANSITION);
            if (StringUtil.isNotEmpty(transition)) {
                w.writeAttribute("transition", transition, null); // $NON-NLS-1$
            }
            if (viewDef.hasIconColumn) {
                String icon = getIconColumnUrl(context, w, c, viewDef);
                if (StringUtil.isNotEmpty(icon)) {
                    // TODO not writing out icon column Alt text
                    w.writeAttribute("icon", icon, null);//$NON-NLS-1$
                }
            }
            String href = getRowUrl(context, c, viewDef);
            if (StringUtil.isNotEmpty(href) && !href.startsWith("#")) { // $NON-NLS-1$
                href = "#" + href; // $NON-NLS-1$
            }
            if (StringUtil.isNotEmpty(href)) {
                w.writeAttribute("moveTo", href, null); // $NON-NLS-1$
            }
        }
        newLine(w);
    }

    protected void writeRowListAttributes(FacesContext context,
            ResponseWriter w, AbstractDataView c, ViewDefinition viewDef)
            throws IOException {
        String djType = (String) getProperty(PROP_ITEMDOJOTYPE);
        if (StringUtil.isNotEmpty(djType)) {
            w.writeAttribute("dojoType", djType, null); // $NON-NLS-1$
        }
    }

    protected String getRowUrl(FacesContext context, AbstractDataView c,
            ViewDefinition viewDef) {
        // Try a fixed href on the summary column
        String href = getColumnUrl(context, c, viewDef, viewDef.summaryColumn);
        if (StringUtil.isNotEmpty(href)) {
            return href;
        }
        // Try a URL for the row
        String pageUrl = getPageUrl(context, c, viewDef);
        if (StringUtil.isNotEmpty(pageUrl)) {
            return pageUrl;
        }
        return null;
    }

    @Override
    protected String getColumnUrl(FacesContext context, AbstractDataView c,
            ViewDefinition viewDef, ValueColumn col) {
        if (col != null) {
            String href = col.getHref();
            if (StringUtil.isNotEmpty(href)) {
                return href;
            }
        }
        return null;
    }

    protected String getPageUrl(FacesContext context, AbstractDataView c,
            ViewDefinition viewDef) {
        String pageName = c.getPageName();
        if (StringUtil.isNotEmpty(pageName)) {
            Object row = viewDef.dataModel.getRowData();
            if (row instanceof ViewRowData) {
                ViewRowData viewRowData = (ViewRowData) row;
                boolean readOnly = c.isOpenDocAsReadonly();
                ViewRowDataOverride helper = new ViewRowDataOverride(
                        (ViewEntry) viewRowData);
                String url = helper.getOpenPageURL(pageName, readOnly);
                return url.replace("?", "&");
            }
        }
        return null;
    }

    protected void writeRowEnd(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if (isCategoryRow(context, c, viewDef)) {
            String tag = (String) getProperty(PROP_SUBHEADTAG);
            w.endElement(tag);
        }
        else {
            String tag = (String) getProperty(PROP_ITEMTAG);
            w.endElement(tag);
        }
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }

    @Override
    protected void writeStandardRow(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        writeRowStart(context, w, c, viewDef);

        // Write the data column
        writeDataColumn(context, w, c, viewDef);

        if (viewDef.hasExtraColumns) {
            writeExtraColumns(context, w, c, viewDef);
        }

        writeRowEnd(context, w, c, viewDef);
    }

    @Override
    protected void writeCategoryRow(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        writeRowStart(context, w, c, viewDef);

        // Write the category data
        // writeDataColumn(context, w, c, viewDef);
        writeCategory(context, w, c, viewDef);

        writeRowEnd(context, w, c, viewDef);
    }

    @Override
    protected void writeTotalRow(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        writeRowStart(context, w, c, viewDef);

        // Write the total data
        // writeDataColumn(context, w, c, viewDef);

        writeRowEnd(context, w, c, viewDef);
    }

    @Override
    protected void writeDataColumn(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // Write the summary data
        writeSummary(context, w, c, viewDef);

        // Write the details
        writeDetail(context, w, c, viewDef);
    }

    // Writes out main content of a category row
    protected void writeCategory(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {

        int categoryListIndex = findCategoryListIndex(context, c, viewDef,
                viewDef.categoryColumns);
        CategoryColumn categoryColumn = (-1 == categoryListIndex) ? null
                : viewDef.categoryColumns.get(categoryListIndex);
        if (null == categoryColumn) {
            return;
        }

        UIComponent categoryFacet = getCategoryRowFacet(c, categoryListIndex);

        // Add the enclosing tag
        if (categoryFacet == null) {
            w.startElement("div", c); // $NON-NLS-1$
            String styleClass = ExtLibUtil.concatStyleClasses(
                    (String) getProperty(PROP_EXTRACOLSTYLECLASS),
                    categoryColumn.getStyleClass());
            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            String style = ExtLibUtil.concatStyles(
                    (String) getProperty(PROP_EXTRACOLSTYLE),
                    categoryColumn.getStyle());
            if (StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            writeColumnValue(context, w, c, viewDef, categoryColumn);
            w.endElement("div"); // $NON-NLS-1$
        }
        else {
            // Write the content column
            FacesUtil.renderChildren(context, categoryFacet);
        }
    }

    @Override
    protected void writeSummary(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if (viewDef.summaryColumn == null && viewDef.summaryFacet == null) {
            return;
        }
        // Add the enclosing tag
        if (viewDef.summaryFacet == null) {
            w.startElement("div", c); // $NON-NLS-1$
            String styleClass = ExtLibUtil.concatStyleClasses(
                    (String) getProperty(PROP_SUMMARYCOLSTYLECLASS),
                    viewDef.summaryColumn.getStyleClass());
            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            String style = ExtLibUtil.concatStyles(
                    (String) getProperty(PROP_SUMMARYCOLSTYLE),
                    viewDef.summaryColumn.getStyle());
            if (StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            writeColumnValue(context, w, c, viewDef, viewDef.summaryColumn);
            w.endElement("div"); // $NON-NLS-1$
        }
        else {
            // Write the content column
            FacesUtil.renderChildren(context, viewDef.summaryFacet);
        }
    }

    @Override
    protected void writeDetail(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        UIComponent detail = viewDef.detailFacet;
        if (detail != null) {
            String id = c.getClientId(context) + ID_DETAIL;
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("id", id, null); // $NON-NLS-1$
            String styleClass = (String) getProperty(PROP_SUMMARYCOLTEXTSTYLECLASS);
            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            String style = viewDef.summaryColumn != null ? viewDef.summaryColumn
                    .getStyle() : null;
            if (StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            FacesUtil.renderComponent(context, detail);
            w.endElement("div"); // $NON-NLS-1$
        }
    }

    @Override
    protected void writeExtraColumn(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef, ExtraColumn col,
            int colIdx) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        UIComponent facet = getExtraFacet(c, colIdx);
        if (facet != null) {
            FacesUtil.renderComponent(context, facet);
        }
        else {
            String styleClass = ExtLibUtil.concatStyleClasses(
                    (String) getProperty(PROP_EXTRACOLSTYLECLASS),
                    col.getStyleClass());
            if (StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
            String style = ExtLibUtil.concatStyles(
                    (String) getProperty(PROP_EXTRACOLSTYLE), col.getStyle());
            if (StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            // Write a link if there is an href
            String href = getColumnUrl(context, c, viewDef, col);
            if (StringUtil.isNotEmpty(href)) {
                w.startElement("a", c);
                RenderUtil.writeLinkAttribute(context, w, href);
            }
            writeColumnValue(context, w, c, viewDef, col);
            if (StringUtil.isNotEmpty(href)) {
                w.endElement("a");
            }
        }
        w.endElement("div"); // $NON-NLS-1$
    }

    @Override
    protected void writeIconColumn(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if (viewDef.iconFacet != null) {
            w.startElement("div", c); // $NON-NLS-1$

            if (viewDef.iconColumn != null) {
                String colStyle = viewDef.iconColumn.getStyle();
                if (StringUtil.isNotEmpty(colStyle)) {
                    w.writeAttribute("style", colStyle, null); // $NON-NLS-1$
                }
                String colClazz = viewDef.iconColumn.getStyleClass();
                if (!viewDef.hasCheckBoxColumn) {
                    colClazz = ExtLibUtil.concatStyleClasses(colClazz,
                            (String) getProperty(PROP_TABLEFIRSTCELLCLASS));
                }
                if (StringUtil.isNotEmpty(colClazz)) {
                    w.writeAttribute("class", colClazz, null); // $NON-NLS-1$
                }
            }

            FacesUtil.renderComponent(context, viewDef.iconFacet);

            w.endElement("div"); // $NON-NLS-1$
        }
    }

    protected String getIconColumnUrl(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if (viewDef.iconFacet != null) // we are using an icon facet, this is
                                       // handled later in writeStandardRow
            return null;
        IconEntry entry = findIcon(context, w, c, viewDef);
        if (entry != null) {
            String src = entry.getUrl();
            if (StringUtil.isNotEmpty(src)) {
                return HtmlRendererUtil.getImageURL(context, src);
            }
        }
        return null;
    }

    @Override
    public void encodeRows(FacesContext context, UIDataIterator iterator,
            int first, int rows) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        AbstractDataView c = (AbstractDataView) iterator;

        ViewDefinition viewDef = createViewDefinition(context);
        initViewDefinition(context, c, viewDef);

        writeRows(context, w, c, viewDef, first, rows);
    }

}