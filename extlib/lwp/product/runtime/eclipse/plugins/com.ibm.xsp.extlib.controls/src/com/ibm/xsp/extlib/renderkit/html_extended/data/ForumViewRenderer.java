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

import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.UIForumView;
import com.ibm.xsp.extlib.util.ExtLibUtil;


/**
 * 
 */
public class ForumViewRenderer extends AbstractWebDataViewRenderer {
    
    protected static final int PROP_MAINLISTSTYLE           = 200;
    protected static final int PROP_MAINLISTCLASS           = 201;
    protected static final int PROP_CHILDLISTSTYLE          = 202;
    protected static final int PROP_CHILDLISTCLASS          = 203;
    protected static final int PROP_LISTITEMSTYLE           = 204;
    protected static final int PROP_LISTITEMCLASS           = 205;

    protected static final int PROP_COLLAPSIBLECONTENTSTYLE = 210;
    protected static final int PROP_COLLAPSIBLEDIVSTYLE = 211;
    
    
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
        UIForumView dataView = (UIForumView)_dataView;
        super.initViewDefinition(context, dataView, viewDef);

        // Define if the items must be rendered in a table, 
        // Use a table for IE as CSS float is largely buggy - Hope IE9 fixes this...
        viewDef.viewforumRenderAsTable = XSPContext.getXSPContext(context).getUserAgent().isIE(0,8);
        
        // It is only one or the other, never both...
        viewDef.summaryOrDetailVisible = true;
        
        // Just to be sure...
        viewDef.collapsibleRows = false;
    }
    
    @Override
    protected void writeContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(DEBUG) {
            w.writeComment("Start ForumView content"); // $NON-NLS-1$
            newLine(w);
        }
        w.startElement("ul",c); // $NON-NLS-1$
        w.writeAttribute("id", c.getAjaxContainerClientId(context), null); // $NON-NLS-1$
        
        String style = (String)getProperty(PROP_MAINLISTSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_MAINLISTCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        newLine(w);
        
        // And the rows
        int first = c.getFirst();
        int count = c.getRows();
        writeRows(context, w, c, viewDef, first, count);
        
        w.endElement("ul"); // $NON-NLS-1$
        newLine(w);
        if(DEBUG) {
            w.writeComment("End ForumView content"); // $NON-NLS-1$
            newLine(w);
        }
    }
    
    @Override
    protected void beforeRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        super.beforeRow(context, w, c, viewDef);
        
        // In case of the first row, we generate the first indentation
        if(!viewDef.singleRowRefresh) {
            DataModel dm = viewDef.dataModel;
            if(dm.getRowIndex()==viewDef.first) {
                //int indent = (dm instanceof TabularDataModel) ? ((TabularDataModel)dm).getIndentLevel() : 0;
                int initialLevel = getColumnIndentLevel(context, c, viewDef);
                if(initialLevel>0) {
                    startFirstRow(context, w, c, viewDef, initialLevel);
                    viewDef.initialIndentLevel = initialLevel;
                }
            }
        }
    }
    
    @Override
    protected void afterRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        super.afterRows(context, w, c, viewDef);
        
        if(!viewDef.singleRowRefresh) {
            if(!viewDef.singleRowRefresh) {
                int levelToClose = viewDef.indentLevel-viewDef.initialIndentLevel; 
                if(levelToClose>0) {
                    for(int i=0; i<levelToClose; i++ ) {
                        endChildren(context, w, c, viewDef);
                    }
                }
            }
            if(viewDef.initialIndentLevel>0) {
                endFirstRow(context, w, c, viewDef, viewDef.initialIndentLevel);
            }
        }
    }
    
    @Override
    protected void writeRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(isCategoryRow(context, c, viewDef)) {
            // Not valid...
        } else if(isTotalRow(context, c, viewDef)) {
            // Not valid...
        } else {
            // Standard row
            writeStandardRow(context, w, c, viewDef);
        }
    }
    
    protected void writeStandardRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(!viewDef.singleRowRefresh) {
            int indent = getColumnIndentLevel(context, c, viewDef);
            // Fix the indentation
            if(indent<viewDef.indentLevel) {
                int limit = viewDef.indentLevel-indent;
                for(int i=0; i<limit; i++ ) {
                    endChildren(context, w, c, viewDef);
                    endItem(context, w, c, viewDef);
                    viewDef.indentLevel--;
                }
            } else if(indent>viewDef.indentLevel) {
                int limit = indent-viewDef.indentLevel;
                for(int i=0; i<limit; i++ ) {
                    startItem(context, w, c, viewDef, false);
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
            // When display a forum view, then we need to create a first enclosing div
            // that sets the global margin to 7px (for OneUI). Else, an enclosing forumPost
            // won't render properly here. Then, this also serve for the positioning of the
            // expand/collapse icon which is using a float: left style. Without this div, it
            // will position relative to a div upper in the hierarchy
            w.startElement("div",c); // $NON-NLS-1$
            String st1 = (String)getProperty(PROP_COLLAPSIBLECONTENTSTYLE);
            if(StringUtil.isNotEmpty(st1)) {
                w.writeAttribute("style", st1, null); // $NON-NLS-1$
            }
    
            // Encode the expand/collapse details icon
            if(viewDef.collapsibleDetails) {
                writeCollapsibleContent(context, w, c, viewDef);
            }
            
            // Encode the row content
            writeStandardContent(context, w, c, viewDef);
    
            // Main div...
            w.endElement("div"); // $NON-NLS-1$
        }

        endItem(context, w, c, viewDef);
    }

    protected void writeCollapsibleContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // The style here is ensuring a float: left positioning
        w.startElement("div",c); // $NON-NLS-1$
        String st2 = (String)getProperty(PROP_COLLAPSIBLEDIVSTYLE);
        if(StringUtil.isNotEmpty(st2)) {
            w.writeAttribute("style", st2, null); // $NON-NLS-1$
        }
        writeShowHideDetailContent(context, w, c, viewDef);
        w.endElement("div"); // $NON-NLS-1$
    }
    
    protected void writeStandardContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // If both the summary and the detail have to be in the client, we render them here
        writeSummary(context, w, c, viewDef);
        writeDetail(context, w, c, viewDef);
    }

    protected void startFirstRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, int indentLevel) throws IOException {
        for(int i=0; i<indentLevel; i++) {
            startItem(context, w, c, viewDef, false);
            startChildren(context, w, c, viewDef);
        }
    }

    protected void endFirstRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, int indentLevel) throws IOException {
        for(int i=0; i<indentLevel; i++) {
            endChildren(context, w, c, viewDef);
            endItem(context, w, c, viewDef);
        }
    }
    
    protected void startChildren(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
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

    protected void endChildren(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        w.endElement("ul"); // $NON-NLS-1$
        newLine(w);
    }

    protected void startItem(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, boolean emitId) throws IOException {
        w.startElement("li",c); // $NON-NLS-1$
        if(emitId) {
            String id = viewDef.dataView.getClientId(context)+NamingContainer.SEPARATOR_CHAR+UIDataView.ROW_ID; 
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        }
        String style = (String)getProperty(PROP_LISTITEMSTYLE);
        // In case we do not render as a table, we should also fix the margin
        if(!viewDef.viewforumRenderAsTable) {
            // In case of a table, the margin are auto computed and not null
            // In case of div, they're just null so we should fix them
            style = ExtLibUtil.concatStyles(style, "margin: 7px 7px 7px 0;"); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_LISTITEMCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        newLine(w);
    }

    protected void endItem(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
       w.endElement("li"); // $NON-NLS-1$
        newLine(w);
    }
}