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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;

public class ForumViewRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.ForumViewRenderer {

    protected static final int PROP_CHILDLISTICONCLASS = 300;
    
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
            case PROP_CHILDLISTCLASS:           return "media xspForumChildList"; // $NON-NLS-1$
            case PROP_LISTITEMCLASS:            return "media-body"; // $NON-NLS-1$
            case PROP_CHILDLISTICONCLASS:       return "xspForumChildListIcon"; // $NON-NLS-1$
            
            //case PROP_COLLAPSIBLECONTENTSTYLE:    return "margin: 7px;";
            case PROP_COLLAPSIBLEDIVSTYLE:      return "float: right;padding-left: 6px;"; // $NON-NLS-1$
            
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:  return Resources.get().VIEW_COLUMN_SORT_BOTH_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING: return Resources.get().VIEW_COLUMN_SORT_BOTH_DESCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:            return Resources.get().VIEW_COLUMN_SORT_NONE;
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:    return Resources.get().VIEW_COLUMN_SORT_NORMAL;
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:   return Resources.get().VIEW_COLUMN_SORT_REVERSE;
        }
        return super.getProperty(prop);
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
        // In case we do not render as a table, we should also fix the margin
        if(!viewDef.viewforumRenderAsTable) {
            // In case of a table, the margin are auto computed and not null
            // In case of div, they're just null so we should fix them

            //In bootstrap we dont need these additional margins on list container children
            if(emitId) {
                style = ExtLibUtil.concatStyles(style, "margin: 0px 7px 7px 0;"); // $NON-NLS-1$
            }
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
}