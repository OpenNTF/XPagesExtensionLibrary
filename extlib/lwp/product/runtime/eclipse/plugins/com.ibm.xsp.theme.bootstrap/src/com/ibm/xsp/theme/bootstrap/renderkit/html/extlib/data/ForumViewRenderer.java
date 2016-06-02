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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.theme.bootstrap.util.BootstrapUtil;
import com.ibm.xsp.util.JSUtil;

public class ForumViewRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.ForumViewRenderer {

    protected static final int PROP_CHILDLISTICONCLASS  = 300;
    protected static final int PROP_MEDIABODYCLASS      = 301;
    protected static final int PROP_COLLAPSIBLEDIVCLASS = 302;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMG:                 return Resources.get().BLANK_GIF;
            
            // note, for an Alt, there's a difference between the empty string and null
            case PROP_BLANKIMGALT:              return ""; //$NON-NLS-1$
            case PROP_ALTTEXTCLASS:             return "lotusAltText";   // $NON-NLS-1$
            
            
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

            case PROP_SHOWICONDETAILSCLASS:     return Resources.get().getIconClass("chevron-down"); // $NON-NLS-1$
            case PROP_HIDEICONDETAILSCLASS:     return Resources.get().getIconClass("chevron-up"); // $NON-NLS-1$
            
            
            case PROP_MAINDIVCLASS:             return "forumView"; // $NON-NLS-1$
            case PROP_MAINLISTCLASS:            return "media-list"; // $NON-NLS-1$
            case PROP_CHILDLISTCLASS:           return "xspForumChildList"; // $NON-NLS-1$
            case PROP_MEDIABODYCLASS:           return "media-body"; // $NON-NLS-1$
            case PROP_LISTITEMCLASS:            return "media"; // $NON-NLS-1$
            case PROP_CHILDLISTICONCLASS:       return "xspForumChildListIcon"; // $NON-NLS-1$
            
            case PROP_COLLAPSIBLECONTENTSTYLE:  return null;
            case PROP_COLLAPSIBLEDIVSTYLE:      return "padding-left: 6px;"; // $NON-NLS-1$
            case PROP_COLLAPSIBLEDIVCLASS:      return "pull-right"; // $NON-NLS-1$
            
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:  return Resources.get().VIEW_COLUMN_SORT_BOTH_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING: return Resources.get().VIEW_COLUMN_SORT_BOTH_DESCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:            return Resources.get().VIEW_COLUMN_SORT_NONE;
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:    return Resources.get().VIEW_COLUMN_SORT_NORMAL;
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:   return Resources.get().VIEW_COLUMN_SORT_REVERSE;
            
            case PROP_SUMMARYTITLECLASS:                    return "media-heading"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        // Encode the necessary resource
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibExtLib);
        
        super.encodeBegin(context, component);
    }
    
    @Override
    protected void startChildren(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.startElement("div",c); // $NON-NLS-1$
        String iconStyleClass = (String)getProperty(PROP_CHILDLISTICONCLASS);
        if(StringUtil.isNotEmpty(iconStyleClass)) {
            w.writeAttribute("class", iconStyleClass, null); // $NON-NLS-1$
        }
        w.endElement("div"); // $NON-NLS-1$
        
        w.startElement("ul",c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_CHILDLISTSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_CHILDLISTCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
       }
        newLine(w);
    }
    
    @Override
    protected void startItem(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, boolean emitId) throws IOException {
        w.startElement("li",c); // $NON-NLS-1$
        if(emitId) {
            String id = viewDef.dataView.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID; 
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        }
        String style = (String)getProperty(PROP_LISTITEMSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        
        String styleClass = (String)getProperty(PROP_LISTITEMCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        newLine(w);
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
        
        if(detailsOnClient) {
            writeDetailsOnClientJS(context, w, c, viewDef);
        }
      
        String clazz = (String)getProperty(viewDef.rowDetailVisible?PROP_HIDEICONDETAILSCLASS:PROP_SHOWICONDETAILSCLASS);
        
        w.startElement("span",c); // $NON-NLS-1$    
        w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        
        String spanId = c.getClientId(context) + "_shChevron"; // $NON-NLS-1$
        w.writeAttribute("id",spanId,null); // $NON-NLS-1$
        
        // Defect 198012 - replace unnecessary title attribute with sr-only div containing text
        BootstrapUtil.renderIconTextForA11Y(w, label);
        
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
    
    @Override
    protected void writeStandardRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        
        if(!viewDef.singleRowRefresh) {
            int indent = getColumnIndentLevel(context, c, viewDef);
            // Fix the indentation
            if(indent<viewDef.indentLevel) {
                int limit = viewDef.indentLevel-indent;
                for(int i=0; i<limit; i++ ) {
                    endChildren(context, w, c, viewDef);
                    //endItem(context, w, c, viewDef);
                    viewDef.indentLevel--;
                }
            } else if(indent>viewDef.indentLevel) {
                int limit = indent-viewDef.indentLevel;
                for(int i=0; i<limit; i++ ) {
                    //startItem(context, w, c, viewDef, false);
                    startChildren(context, w, c, viewDef);
                    viewDef.indentLevel++;
                }
            }
        }
        
        // Start the new item to be displayed
        startItem(context, w, c, viewDef, true);

        // Use a table for IE as CSS float is largely buggy - Hope IE9 fixes this...
        if(viewDef.viewforumRenderAsTable) {
            w.startElement("table",c); // $NON-NLS-1$
            w.writeAttribute("style", "margin: 0; padding:0; border-width: 0; width: 100%", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("role", "presentation", null); // $NON-NLS-1$ $NON-NLS-2$
            w.startElement("tr",c); // $NON-NLS-1$
        
            // Encode the row content
            w.startElement("td",c); // $NON-NLS-1$
            w.writeAttribute("style", "width: 100%", null); // $NON-NLS-1$ $NON-NLS-2$
            writeStandardContent(context, w, c, viewDef);
            w.endElement("td"); // $NON-NLS-1$
        
            // Encode the expand/collapse details icon
            if(viewDef.collapsibleDetails) { 
                w.startElement("td",c); // $NON-NLS-1$
                w.writeAttribute("valign", "top", null); // $NON-NLS-1$ $NON-NLS-2$
                //w.writeAttribute("style", "padding: -7px; width: 100%", null);
                writeShowHideDetailContent(context, w, c, viewDef);
                w.endElement("td"); // $NON-NLS-1$
            }
            
            // And close the item...
            w.endElement("tr"); // $NON-NLS-1$
            w.endElement("table"); // $NON-NLS-1$
        } else {
            // If both the summary and the detail have to be in the client, we render them here
            if(!viewDef.rowDetailVisible) {
                //When detail is not visible, add the media-body div
                w.startElement("div",c); // $NON-NLS-1$
                w.writeAttribute("class", (String)getProperty(PROP_MEDIABODYCLASS), null); // $NON-NLS-1$
            }
            
            // Encode the row content
            writeStandardContent(context, w, c, viewDef);

            if(!viewDef.rowDetailVisible) {
                w.endElement("div"); // $NON-NLS-1$
            }
        }

        endItem(context, w, c, viewDef);
    }

    @Override
    protected void writeStandardContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // If both the summary and the detail have to be in the client, we render them here
        writeSummary(context, w, c, viewDef);
        
        // Moved collapse/expand icon after summary, before detail to fix reading/tab order
        // for a11y. Used to be in writeStandardRow, just before writeStandardContent call
        // Encode the expand/collapse details icon
        if(viewDef.collapsibleDetails) {
          writeCollapsibleContent(context, w, c, viewDef);
        }
        
        writeDetail(context, w, c, viewDef);
    }

    @Override
    protected void writeCollapsibleContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // The style here is ensuring a float: left positioning
        w.startElement("div",c); // $NON-NLS-1$
        String st2 = (String)getProperty(PROP_COLLAPSIBLEDIVSTYLE);
        if(StringUtil.isNotEmpty(st2)) {
            w.writeAttribute("style", st2, null); // $NON-NLS-1$
        }
        
        String clazz = (String)getProperty(PROP_COLLAPSIBLEDIVCLASS);
        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class", clazz, null); // $NON-NLS-1$
        }
        
        writeShowHideDetailContent(context, w, c, viewDef);
        w.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeSummaryTitleStyles(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, String hideStyle) throws IOException {
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
        w.writeAttribute("style",style,null); // $NON-NLS-1$
    }
    
    @Override
    protected void writeShowHide(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        //Disable superclass client show/hide JS, as bootstrap version implemented below
        //in writeDetailsOnClientJS and in xsp.mixin.js. But keep the hidden field
        if(viewDef.detailsOnClient && viewDef.collapsibleDetails) {
            writeClientShowHideHiddenField(context, w, c, viewDef);
            //addClientShowHideScript(context, w, c, viewDef);
        }
    }
    
    protected void writeDetailsOnClientJS(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        //TODO re-work the hide/show detail on client JS in super classes to make this
        //a single reusable method across all data views and forum views
        //TODO Hacky clientId retrieval of the dataView control
        //replace with a proper getClientId method for the dataview
        //c.getClientId(context) gives back the id of the row
        String rowId = c.getClientId(context);
        String dataViewID = rowId.substring(0, rowId.lastIndexOf(":"+viewDef.dataModel.getRowIndex()));
        
        //Add JS onclick code to handle toggling aria-checked attribute
        StringBuilder onclick = new StringBuilder();
        onclick.append("return XSP.xbtShowHideDetails("); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, dataViewID);
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, Integer.toString(viewDef.dataModel.getRowIndex()));
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, viewDef.rowPosition);
        onclick.append(", "); // $NON-NLS-1$
        onclick.append(viewDef.summaryOrDetailVisible);
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, (String)getProperty(PROP_SHOWICONDETAILSCLASS));
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, (String)getProperty(PROP_HIDEICONDETAILSCLASS));
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, (String)getProperty(PROP_SHOWICONDETAILSTOOLTIP));
        onclick.append(", "); // $NON-NLS-1$
        JSUtil.addSingleQuoteString(onclick, (String)getProperty(PROP_HIDEICONDETAILSTOOLTIP));
        onclick.append(")"); // $NON-NLS-1$
        w.writeAttribute("onclick", onclick.toString(), null); // $NON-NLS-1$
        
        // Build JS for onkeyup event to swap collapse/expand properties
        // when enter or space are pressed (see ExtLib.js)
        StringBuilder onkeydown = new StringBuilder();
        onkeydown.append("var xbtIsTriggerKey = XSP.xbtIsTriggerKey(event);"); // $NON-NLS-1$
        onkeydown.append("if(xbtIsTriggerKey){"); // $NON-NLS-1$
        onkeydown.append("event.preventDefault();"); // $NON-NLS-1$
        onkeydown.append("event.stopPropagation();"); // $NON-NLS-1$
        onkeydown.append(onclick);
        onkeydown.append("}"); // $NON-NLS-1$
        w.writeAttribute("onkeydown", onkeydown.toString(), null); // $NON-NLS-1$
    }
}