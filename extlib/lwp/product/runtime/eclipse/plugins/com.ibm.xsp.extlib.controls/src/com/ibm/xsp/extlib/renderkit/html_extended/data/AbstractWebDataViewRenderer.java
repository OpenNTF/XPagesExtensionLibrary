/*
 * © Copyright IBM Corp. 2010, 2014
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
import java.util.Locale;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import lotus.domino.ViewEntry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.ValueColumn;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * Base renderer class for a Data View for the web.
 * <p>
 * This renderer provides the common capability shared by the different 
 * renderers, like the DataView or ForumView renderers for the web.
 * </p>
 */
public abstract class AbstractWebDataViewRenderer extends AbstractDataViewRenderer {
    
    protected static final int PROP_SUMMARYTITLETAG     = 120;
    protected static final int PROP_SUMMARYTITLESTYLE   = 121;
    protected static final int PROP_SUMMARYTITLECLASS   = 122;

    protected static final int PROP_SUMMARYFACETTAG     = 123;
    protected static final int PROP_SUMMARYFACETSTYLE   = 124;
    protected static final int PROP_SUMMARYFACETCLASS   = 125;
    
    protected static final int PROP_DETAILTAG           = 130;
    protected static final int PROP_DETAILSTYLE         = 131;
    protected static final int PROP_DETAILCLASS         = 132;

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_DETAILTAG:        return "div"; // $NON-NLS-1$
            case PROP_SUMMARYTITLETAG:  return "h4"; // $NON-NLS-1$
            case PROP_SUMMARYFACETTAG:  return "div"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    
    // ================================================================
    // Summary column functions
    // ================================================================
    
    protected void writeSummary(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(!viewDef.hasSummary) {
            return;
        }

        boolean summaryVisible = !(viewDef.rowDetailVisible && viewDef.summaryOrDetailVisible);
        
        // If the summary should not be displayed, then leave
        if(!summaryVisible && !viewDef.detailsOnClient) {
            return;
        }

        String clientIdSummary = c.getClientId(context)+ID_SUMMARY;

        String hideStyle = null;
        if(!summaryVisible) {
            // We must have then detailsOnClient
            // Just renders it invisible
            hideStyle = "display: none"; // $NON-NLS-1$
        } else {
            boolean dojoEffect = (Boolean)getProperty(PROP_SHOWHIDEDOJOEFFECT);
            if(dojoEffect && c.getToggledVisibleDetail()!=null && StringUtil.equals(c.getToggledVisibleDetail(), viewDef.rowPosition)) {
                hideStyle = "opacity: 0"; // $NON-NLS-1$
                UIScriptCollector collector = UIScriptCollector.find();
                String duration = (String)getProperty(PROP_SHOWHIDEDOJODURATION);
                StringBuilder b = new StringBuilder();
                b.append("dojo.fadeIn({node:"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, clientIdSummary);
                b.append(", duration: "); // $NON-NLS-1$
                JavaScriptUtil.addNumber(b, Integer.parseInt(duration));
                b.append("}).play()"); // $NON-NLS-1$
                collector.addScriptOnLoad(b.toString());
            }
        }
        
        // Add the enclosing tag
        if(viewDef.summaryFacet==null) {
            String tagName = (String)getProperty(PROP_SUMMARYTITLETAG);
            w.startElement(tagName,c);
            w.writeAttribute("id",clientIdSummary,null); // $NON-NLS-1$
            String style = viewDef.summaryColumn.getStyle();
            if(StringUtil.isEmpty(style)) {
                style = (String)getProperty(PROP_SUMMARYTITLESTYLE);
            }
            style = ExtLibUtil.concatStyles(style, hideStyle);          
            String styleClass = viewDef.summaryColumn.getStyleClass();
            if(StringUtil.isEmpty(styleClass)) {
                styleClass = (String)getProperty(PROP_SUMMARYTITLECLASS);
            }
            
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            style = ExtLibUtil.concatStyles("margin: 0",style); // $NON-NLS-1$
            w.writeAttribute("style",style,null); // $NON-NLS-1$
            
            // Expand/collapse icon
            if(viewDef.collapsibleRows) {
                writeExpandCollapseIcon(context, w, c, viewDef);
            }
            // Write the content column 
            w.startElement("a",c);
            String linkId = c.getClientId(context)+ID_SUMMARY_LINK;
            w.writeAttribute("id", linkId, null); // $NON-NLS-1$
            String href = getSummaryColumnUrl(context, c, viewDef);
            if(StringUtil.isNotEmpty(href)) {
                RenderUtil.writeLinkAttribute(context,w,href);
            } else {
                w.writeAttribute("href","javascript:;",null); // $NON-NLS-1$ $NON-NLS-2$
                //LHEY97CCSZ adding the role=button
                w.writeAttribute("role", "button", null); // $NON-NLS-1$ // $NON-NLS-2$
            }

            String title = getTitle(context,c,viewDef,viewDef.summaryColumn);
            if(StringUtil.isNotEmpty(title)) {
                w.writeAttribute("title",title,null); // $NON-NLS-1$
            }
            writeColumnValue(context, w, c, viewDef, viewDef.summaryColumn);
            w.endElement("a");
            w.endElement(tagName);
        } else {
            String tagName = (String)getProperty(PROP_SUMMARYFACETTAG);
            w.startElement(tagName,c);
            w.writeAttribute("id",clientIdSummary,null); // $NON-NLS-1$

            String style = viewDef.summaryColumn!=null ? viewDef.summaryColumn.getStyle() : null;
            if(StringUtil.isEmpty(style)) {
                style = (String)getProperty(PROP_SUMMARYFACETSTYLE);
            }
            style = ExtLibUtil.concatStyles(style, hideStyle);          
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            String styleClass = viewDef.summaryColumn!=null ? viewDef.summaryColumn.getStyleClass() : null;
            if(StringUtil.isEmpty(styleClass)) {
                styleClass = (String)getProperty(PROP_SUMMARYFACETCLASS);
            }
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            
            // Expand/collapse icon
            if(viewDef.collapsibleRows) {
                writeExpandCollapseIcon(context, w, c, viewDef);
            }
            // Write the content column 
            FacesUtil.renderChildren(context, viewDef.summaryFacet);
            
            w.endElement(tagName);
        }
    }
    
    


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

                w.startElement("img",c); // $NON-NLS-1$
                w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,icon),null); // $NON-NLS-1$
                w.writeAttribute("alt", iconAlt, null); //$NON-NLS-1$
                String style = (String)getProperty(expanded ? PROP_COLLAPSEICONSTYLE : PROP_EXPANDICONSTYLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style",style,null); // $NON-NLS-1$
                }
                String clazz = (String)getProperty(expanded ? PROP_COLLAPSEICONCLASS : PROP_EXPANDICONCLASS);
                if(StringUtil.isNotEmpty(clazz)) {
                    w.writeAttribute("class",clazz,null); // $NON-NLS-1$
                }
                w.endElement("img"); // $NON-NLS-1$
                w.endElement("a");
                
                setupSubmitOnClick(context, c, linkId, linkId, null);
            }
        }
    }
    
    
    protected String getSummaryColumnUrl(FacesContext context, AbstractDataView c, ViewDefinition viewDef) {
        // Try a fixed href
        String href = getColumnUrl(context, c, viewDef, viewDef.summaryColumn);
        if(StringUtil.isNotEmpty(href)) {
            return href;
        }
        // Try a URL for the row
        Object row = viewDef.dataModel.getRowData();
        if (row instanceof ViewRowData){
            ViewRowData viewRowData = (ViewRowData)row;
            String pageName = c.getPageName();
            boolean readOnly = c.isOpenDocAsReadonly();
            // SPR#PHAN9BMHN6 was always casting to ViewEntry, which gave ClassCastException
            // with 3rd party viewRowData implementations.
            if( viewRowData instanceof ViewEntry ){
                ViewRowDataOverride helper = new ViewRowDataOverride((ViewEntry)viewRowData);
                return helper.getOpenPageURL(pageName, readOnly);
            }else{ 
                return viewRowData.getOpenPageURL(pageName, readOnly);
            }
        }
        return null;
    }
    
    
    
    protected String getTitle(FacesContext context, AbstractDataView c, ViewDefinition viewDef, ValueColumn col) {
        if(col!=null) {
            String title = col.getLinkTitle();
            if(StringUtil.isNotEmpty(title)) {
                return title;
            }
        }
        return null;
    }
    
    protected String getColumnUrl(FacesContext context, AbstractDataView c, ViewDefinition viewDef, ValueColumn col) {
        if(col!=null) {
            String href = col.getHref();
            if(StringUtil.isNotEmpty(href)) {
                return href;
            }
        }
        return null;
    }

    
    // ================================================================
    // Show/Hide detail button
    // ================================================================
    
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
        
        if(detailsOnClient) {
            StringBuilder b = new StringBuilder();
            b.append(viewDef.showHideDetailFunctionName);
            b.append("(");
            JSUtil.addSingleQuoteString(b,Integer.toString(viewDef.dataModel.getRowIndex()));
            b.append(",");
            JSUtil.addSingleQuoteString(b,viewDef.rowPosition);
            b.append(")");
            w.writeAttribute("onclick","javascript:"+b.toString(),null); // $NON-NLS-1$ $NON-NLS-2$
        }
        w.startElement("img",c); // $NON-NLS-1$
        String imgid = c.getClientId(context)+ID_SHOWHIDE;
        w.writeAttribute("id",imgid,null); // $NON-NLS-1$
        String clazz = (String)getProperty(viewDef.rowDetailVisible?PROP_HIDEICONDETAILSCLASS:PROP_SHOWICONDETAILSCLASS);
        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        }
        
        //fix for MLML8MXRVX in IE only
        boolean changeImageSize = false;
        if(viewDef.viewforumRenderAsTable){
            
        Locale locale = context.getExternalContext().getRequestLocale();
        if( null == locale ){
            locale = context.getViewRoot().getLocale();
            if( null == locale){
                locale = Locale.getDefault();
            }
        }
        
        String lang = locale.getLanguage();
        if (StringUtil.isNotEmpty(lang) && lang.equals("ko") || lang.equals("zh") || lang.equals("ja") || lang.equals("zh-tw"))// $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$
            changeImageSize = true;
        
        }
        
        if(changeImageSize)
            w.writeAttribute("width","40",null); // $NON-NLS-1$
        else
            w.writeAttribute("width","16",null); // $NON-NLS-1$
        
        if(changeImageSize)
            w.writeAttribute("height","40",null); // $NON-NLS-1$
        else
            w.writeAttribute("height","16",null); // $NON-NLS-1$
        
        String bgif = (String)getProperty(PROP_BLANKIMG);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        String blankImageAlt = (String)getProperty(PROP_BLANKIMGALT);
        if( ExtLibRenderUtil.isAltPresent(blankImageAlt) ){
            w.writeAttribute("alt",blankImageAlt,null); // $NON-NLS-1$
        }
        w.startElement("span",c); // $NON-NLS-1$
        String altTextClass = (String)getProperty(PROP_ALTTEXTCLASS);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("class",altTextClass,null); // $NON-NLS-1$
        }
        if( viewDef.rowDetailVisible ){
            w.writeText("Hide",null); // $NLS-AbstractWebDataViewRenderer.Hide_HideDetailIconAlt-1$
        }else{
            w.writeText("Show",null); // $NLS-AbstractWebDataViewRenderer.Show-1$
        }
        w.endElement("span"); // $NON-NLS-1$
        w.endElement("img"); // $NON-NLS-1$
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
    
    // ================================================================
    // Detail part
    // ================================================================
    
    protected void writeDetail(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(!viewDef.hasDetail) {
            return;
        }

        String id = c.getClientId(context)+ID_DETAIL;
        
        // If the detail should not be displayed, then leave
        boolean detailVisible = viewDef.rowDetailVisible;
        if(!detailVisible && !viewDef.detailsOnClient) {
            return;
        }
        
        String hideStyle = null;
        if(!detailVisible) {
            // We must have then detailsOnClient
            // Just renders it invisible
            hideStyle = "display: none"; // $NON-NLS-1$
        } else {
            // TODO this fade effect doesn't change the tooltips and alt text from Show to Hide
            boolean dojoEffect = (Boolean)getProperty(PROP_SHOWHIDEDOJOEFFECT);
            if(dojoEffect && StringUtil.equals(c.getToggledVisibleDetail(), viewDef.rowPosition)) {
                hideStyle = "opacity: 0"; // $NON-NLS-1$
                UIScriptCollector collector = UIScriptCollector.find();
                String duration = (String)getProperty(PROP_SHOWHIDEDOJODURATION);
                StringBuilder b = new StringBuilder();
                b.append("dojo.fadeIn({node:"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, id);
                b.append(", duration: "); // $NON-NLS-1$
                JavaScriptUtil.addNumber(b, Integer.parseInt(duration));
                b.append("}).play()"); // $NON-NLS-1$
                collector.addScriptOnLoad(b.toString());
            }
        }
        
        UIComponent detail = viewDef.detailFacet;  
        if(detail!=null) {
            String tagName = (String)getProperty(PROP_DETAILTAG);
            w.startElement(tagName,c);
            w.writeAttribute("id",id,null); // $NON-NLS-1$
            String style = (String)getProperty(PROP_DETAILSTYLE);
            String styleClass = (String)getProperty(PROP_DETAILCLASS);
            if(viewDef.collapsibleRows) {
                String collapsibleSize = "20px"; //(Boolean)getProperty(PROP_SHOWHIDEONCLIENT); $NON-NLS-1$
                style = ExtLibUtil.concatStyles(style, "padding-left: "+collapsibleSize); // $NON-NLS-1$
            }
            style = ExtLibUtil.concatStyles(style, hideStyle);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
            }
            FacesUtil.renderComponent(context, detail);
            w.endElement(tagName);
        }
    }   
}