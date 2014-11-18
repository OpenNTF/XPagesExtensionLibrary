/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.data;

import java.io.IOException;

import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.theme.bootstrap.resources.Resources;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.ValueColumn;
import com.ibm.xsp.extlib.component.image.IconEntry;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;

public class DataViewRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.DataViewRenderer {

    protected static final int PROP_UNREADICONCLASS     = 300;
    protected static final int PROP_READICONCLASS       = 301;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMG:                 return Resources.get().BLANK_GIF;
            // note, for an Alt, there's a difference between the empty string and null
            case PROP_BLANKIMGALT:              return ""; //$NON-NLS-1$
            case PROP_ALTTEXTCLASS:             return "lotusAltText"; // $NON-NLS-1$
            
            
            case PROP_HEADERCLASS:              return "clearfix"; // $NON-NLS-1$
            case PROP_HEADERLEFTSTYLE:          return null; 
            case PROP_HEADERLEFTCLASS:          return "pull-left"; // $NON-NLS-1$
            case PROP_HEADERRIGHTSTYLE:         return null; 
            case PROP_HEADERRIGHTCLASS:         return "pull-right"; // $NON-NLS-1$

            case PROP_FOOTERCLASS:              return "clearfix"; // $NON-NLS-1$
            case PROP_FOOTERLEFTSTYLE:          return null; 
            case PROP_FOOTERLEFTCLASS:          return "pull-left"; // $NON-NLS-1$
            case PROP_FOOTERRIGHTSTYLE:         return null; 
            case PROP_FOOTERRIGHTCLASS:         return "pull-right"; // $NON-NLS-1$

            case PROP_TABLECLASS:               return "clearfix table dataview"; // $NON-NLS-1$
            case PROP_TABLEROWEXTRA:            return "lotusMeta lotusNowrap"; // $NON-NLS-1$
            
            case PROP_COLLAPSEICON:             return Resources.get().BLANK_GIF;
            case PROP_COLLAPSEICONSTYLE:        return "padding-right:8px"; // $NON-NLS-1$
            case PROP_COLLAPSEICONCLASS: 		return Resources.get().getIconClass("minus-sign");	
            case PROP_EXPANDICON:               return Resources.get().BLANK_GIF;            
            case PROP_EXPANDICONSTYLE:          return "padding-right:8px"; // $NON-NLS-1$
            case PROP_EXPANDICONCLASS: 			return Resources.get().getIconClass("plus-sign");	
            
            case PROP_TABLEROWINDENTPX:         return 20;
            case PROP_TABLEFIRSTCELLCLASS:      return "xspFirstCell";
            case PROP_SHOWICONDETAILSCLASS:     return Resources.get().getIconClass("chevron-down"); // $NON-NLS-1$
            case PROP_HIDEICONDETAILSCLASS:     return Resources.get().getIconClass("chevron-up"); // $NON-NLS-1$
            
            case PROP_UNREADICONCLASS:          return Resources.get().getIconClass("file")+ " xspUnreadIcon";
            case PROP_READICONCLASS:            return Resources.get().getIconClass("file")+" xspReadIcon";
            
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:   return Resources.get().getIconClass("sort-by-attributes");
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING:  return Resources.get().getIconClass("sort-by-attributes-alt");
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:             return Resources.get().getIconClass("sort");
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:     return Resources.get().getIconClass("sort-by-attributes");
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:    return Resources.get().getIconClass("sort-by-attributes-alt");
            
            // the bootstrap sort header icons are 16x13 px
            case PROP_TABLEHDRCOLIMAGE_SORT_WIDTH:           return "16"; //$NON-NLS-1$
            case PROP_TABLEHDRCOLIMAGE_SORT_HEIGHT:          return "13"; //$NON-NLS-1$
            
            // adjust the empty icon style for better indentation
            case PROP_EMPTYICONSTYLE:                        return "width:20px;height:13px;";
            
            case PROP_SUMMARYTITLECLASS:                     return "xspDataViewSummary";
        }
        return super.getProperty(prop);
    }
    
    @Override
    protected void writeShowHideDetailContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(!viewDef.hasSummary || !viewDef.hasDetail) {
            return;
        }
        
        // In case this is diabled for this particular row
        if(viewDef.rowDisableHideRow) {
            return;
        }
        
        boolean detailsOnClient = viewDef.detailsOnClient;
        String linkId = c.getClientId(context) + (viewDef.rowDetailVisible?HIDE_DELIMITER:SHOW_DELIMITER) + viewDef.rowPosition;

        w.startElement("a",c);
        w.writeAttribute("id",linkId,null); // $NON-NLS-1$
        w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
        //LHEY97CCSZ adding the role=button
        w.writeAttribute("role", "button", null); // $NON-NLS-1$ // $NON-NLS-2$
        String label = (String)getProperty(viewDef.rowDetailVisible ? PROP_HIDEICONDETAILSTOOLTIP : PROP_SHOWICONDETAILSTOOLTIP);
        if(StringUtil.isNotEmpty(label)) {
            w.writeAttribute("title", label, null); // $NON-NLS-1$
            w.writeAttribute("aria-label", label, null); // $NON-NLS-1$
        }
        
        String clazz = (String)getProperty(viewDef.rowDetailVisible?PROP_HIDEICONDETAILSCLASS:PROP_SHOWICONDETAILSCLASS);
       
        w.startElement("span",c); // $NON-NLS-1$    
        w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        
        String spanId = c.getClientId(context) + "_shChevron";
        w.writeAttribute("id",spanId,null); // $NON-NLS-1$
        
        if(detailsOnClient) {
            //TODO Hacky clientId retrieval of the dataView control
            //replace with a proper getClientId method for the dataview
            //c.getClientId(context) gives back the id of the row
            String rowId = c.getClientId(context);
            String dataViewID = rowId.substring(0, rowId.lastIndexOf(":"+viewDef.dataModel.getRowIndex()));
            w.writeAttribute("onclick", "javascript:XSP.xbtShowHideDetails('"+ dataViewID + "', '"+ viewDef.dataModel.getRowIndex() + "', '" 
            	+ viewDef.rowPosition + "', " + viewDef.summaryOrDetailVisible + ", '" + getProperty(PROP_SHOWICONDETAILSCLASS)+ "', '" 
            	+ getProperty(PROP_HIDEICONDETAILSCLASS) + "', '" + getProperty(PROP_SHOWICONDETAILSTOOLTIP) + "', '" 
            	+ getProperty(PROP_HIDEICONDETAILSTOOLTIP) + "')", null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
        }
        
        if( viewDef.rowDetailVisible ){
            w.writeAttribute("title", "Hide",null); // $NLS-AbstractWebDataViewRenderer.Hide_HideDetailIconAlt-1$
        }else{
        	 w.writeAttribute("title", "Show",null); // $NLS-AbstractWebDataViewRenderer.Show-1$
        }
        w.endElement("span"); // $NON-NLS-1$
        w.endElement("a");

        if(!detailsOnClient) {
            if(viewDef.viewRowRefresh) {
                String refreshId = c.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID;
                setupSubmitOnClick(context, c, linkId, linkId, refreshId);
            } else {
                setupSubmitOnClick(context, c, linkId, linkId, null);
            }
        }
    }
    
    // From 9.0.1 JSUtils
    public static void addSingleQuoteString(StringBuilder b, String s) {
        if( null == s ){
            b.append("null"); // $NON-NLS-1$
        }else if( s.length() == 0 ){
            b.append("''"); // $NON-NLS-1$
        }else{
            b.append('\'');
            JSUtil.appendJavaScriptString(b, s);
            b.append('\'');
        }
    }   
    
    @Override
	protected void writeExpandCollapseIcon(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        boolean leaf = isRowLeaf(context, c, viewDef);
        if(leaf) {
            String icon = (String)getProperty(PROP_EMPTYICON);
            if(icon!=null) {
                w.startElement("img",c); // $NON-NLS-1$
                w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,icon),null); // $NON-NLS-1$
                String iconAlt = (String) getProperty(PROP_EMPTYICONALT);
                if( ExtLibRenderUtil.isAltPresent(iconAlt) ){
                    // "" - present but empty
                    w.writeAttribute("alt",iconAlt,null); //$NON-NLS-1$
                }
                String style = (String)getProperty(PROP_EMPTYICONSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style",style,null); // $NON-NLS-1$
                }
                String clazz = (String)getProperty(PROP_EMPTYICONCLASS);
                if(StringUtil.isNotEmpty(clazz)) {
                    w.writeAttribute("class",clazz,null); // $NON-NLS-1$
                }
                w.endElement("img"); // $NON-NLS-1$
            }
        } else {
            boolean expanded = isRowExpanded(context, c, viewDef);
            String icon = (String)getProperty(expanded ? PROP_COLLAPSEICON : PROP_EXPANDICON);
            if(icon!=null) {
                String linkId = c.getClientId(context) + (expanded?SHRINK_DELIMITER:EXPAND_DELIMITER) + viewDef.rowPosition;
                w.startElement("a",c);
                w.writeAttribute("id",linkId,null); // $NON-NLS-1$
                w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                //LHEY97CCSZ adding the role=button
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ // $NON-NLS-2$
                String iconAlt = (String) getProperty(expanded? PROP_COLLAPSEICONALT : PROP_EXPANDICONALT);
                w.writeAttribute("title", iconAlt, null); //$NON-NLS-1$
                w.writeAttribute("aria-label", iconAlt, null); //$NON-NLS-1$

                w.startElement("span",c); // $NON-NLS-1$
                w.writeAttribute("title", iconAlt, null); //$NON-NLS-1$
                String style = (String)getProperty(expanded ? PROP_COLLAPSEICONSTYLE : PROP_EXPANDICONSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style",style,null); // $NON-NLS-1$
                }
                String clazz = (String)getProperty(expanded ? PROP_COLLAPSEICONCLASS : PROP_EXPANDICONCLASS);
                if(StringUtil.isNotEmpty(clazz)) {
                    w.writeAttribute("class",clazz,null); // $NON-NLS-1$
                }
                w.endElement("span"); // $NON-NLS-1$
                w.endElement("a"); // $NON-NLS-1$
                
                setupSubmitOnClick(context, c, linkId, linkId, null);
            }
        }
    }
    
    @Override
    protected void writeIconColumn(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("td",c); // $NON-NLS-1$
        w.writeAttribute("role", "gridcell", null); // $NON-NLS-1$ $NON-NLS-2$

        if(viewDef.iconColumn!=null) {
            String colStyle = viewDef.iconColumn.getStyle();
            if(StringUtil.isNotEmpty(colStyle)) {
                w.writeAttribute("style",colStyle,null); // $NON-NLS-1$
            }
            String colClazz = viewDef.iconColumn.getStyleClass();
            if(!viewDef.hasCheckBoxColumn && !viewDef.hasIconColumn) {
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
                    boolean unread = src.contains("xpPostUnread"); // $NON-NLS-1$
                    w.startElement("div",c); // $NON-NLS-1$
                    
                    String alt = entry.getAlt();
                    String title = entry.getTitle();
                    if(StringUtil.isEmpty(title)){
                        if(StringUtil.isEmpty(alt)){
                            w.writeAttribute("title", alt, null); //$NON-NLS-1$
                        }
                    }else{
                        w.writeAttribute("title", title, null); //$NON-NLS-1$
                    }
                    
                    String iconStyle = entry.getStyle();
                    if(StringUtil.isNotEmpty(iconStyle)) {
                        w.writeAttribute("style",iconStyle,null); // $NON-NLS-1$
                    }
                    String iconClass = (String)getProperty(unread?PROP_UNREADICONCLASS:PROP_READICONCLASS);
                    
                    if(StringUtil.isNotEmpty(iconClass)) {
                        w.writeAttribute("class",iconClass,null); // $NON-NLS-1$
                    }
                    w.endElement("div"); // $NON-NLS-1$
                }
            }
        }
        w.endElement("td"); // $NON-NLS-1$
    }
    
    @Override
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
        
        //Remove padding-top when collapse/expand icon is displayed
        if(viewDef.collapsibleRows) {
        	String summaryStyle = viewDef.summaryColumn.getStyle();
        	String style = ExtLibUtil.concatStyles("padding-top:0px;", summaryStyle);
        	viewDef.summaryColumn.setStyle(style);
        }
        // Write the summary data
        writeSummary(context, w, c, viewDef);
        
        // Write the details
        writeDetail(context, w, c, viewDef);
        
        w.endElement("div"); // $NON-NLS-1$
        w.endElement("td"); // $NON-NLS-1$
    }
    
    @Override
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
                    String sort = getDominoSortIcon(context, c, viewDef, dominoSortIconCode);
                    if(StringUtil.isNotEmpty(sort)) {
                          w.startElement("div",c); // $NON-NLS-1$
                          if(StringUtil.isNotEmpty(sort)) {
                              w.writeAttribute("class", sort, null); //$NON-NLS-1$
                          }
                          w.endElement("div"); // $NON-NLS-1$
                    }
                }
                w.endElement("span"); // $NON-NLS-1$
            }
        }
        w.endElement("th"); // $NON-NLS-1$
    }
}