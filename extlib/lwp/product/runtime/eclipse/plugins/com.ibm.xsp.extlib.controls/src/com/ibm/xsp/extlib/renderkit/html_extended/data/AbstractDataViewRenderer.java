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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.CategoryColumn;
import com.ibm.xsp.extlib.component.data.ExtraColumn;
import com.ibm.xsp.extlib.component.data.IconColumn;
import com.ibm.xsp.extlib.component.data.SummaryColumn;
import com.ibm.xsp.extlib.component.data.UIDataSourceIterator;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.UIForumView;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;


/**
 * Base renderer class for a Data View.
 * <p>
 * This renderer provides the common capability shared by the different 
 * renderers, like the OneUI renderer and the mobile one.
 * </p>
 * {@link AbstractDataView} renderer.
 */
public abstract class AbstractDataViewRenderer extends DataSourceIteratorRenderer {

    protected static final String ID_SHOWHIDE   = "_shimg"; // $NON-NLS-1$
    protected static final String ID_SUMMARY    = "_sum"; // $NON-NLS-1$
    protected static final String ID_SUMMARY_LINK   = "_sumLink"; // $NON-NLS-1$
    protected static final String ID_DETAIL     = "_detail"; // $NON-NLS-1$
    protected static final String XSP_PROGRESSIVE_ENH = "xsp.progressive.enhancement"; // $NON-NLS-1$
    protected static final String ENABLE        = "enable"; // $NON-NLS-1$
    protected static final String DISABLE        = "disable"; // $NON-NLS-1$
    protected static final String AUTO          = "auto"; // $NON-NLS-1$
    
    
    protected static final int PROP_MAINDIVCLASS        = 38;
    protected static final int PROP_MAINDIVSTYLE        = 39;
    
    protected static final int PROP_HASHEADERFACET      = 40;
    protected static final int PROP_HEADER_PAGER_AREA_TAG = 41;
    protected static final int PROP_HEADERSTYLE         = 42;
    protected static final int PROP_HEADERCLASS         = 43;
    protected static final int PROP_HEADERDOJOTYPE      = 44;

    protected static final int PROP_HASFOOTERFACET      = 50;
    protected static final int PROP_FOOTER_PAGER_AREA_TAG = 51;
    protected static final int PROP_FOOTERSTYLE         = 52;
    protected static final int PROP_FOOTERCLASS         = 53;
    protected static final int PROP_FOOTERDOJOTYPE      = 54;

    protected static final int PROP_FACETTAG            = 60;
    protected static final int PROP_FACETDOJOTYPE       = 61;
    
    protected static final int PROP_HEADERLEFTSTYLE     = 62;
    protected static final int PROP_HEADERLEFTCLASS     = 63;
    protected static final int PROP_HEADERMIDDLESTYLE   = 64;
    protected static final int PROP_HEADERMIDDLECLASS   = 65;
    protected static final int PROP_HEADERRIGHTSTYLE    = 66;
    protected static final int PROP_HEADERRIGHTCLASS    = 67;

    protected static final int PROP_FOOTERLEFTSTYLE     = 72;
    protected static final int PROP_FOOTERLEFTCLASS     = 73;
    protected static final int PROP_FOOTERMIDDLESTYLE   = 74;
    protected static final int PROP_FOOTERMIDDLECLASS   = 75;
    protected static final int PROP_FOOTERRIGHTSTYLE    = 76;
    protected static final int PROP_FOOTERRIGHTCLASS    = 77;
    
    protected static final int PROP_SHOWHIDEDOJOEFFECT      = 80;
    protected static final int PROP_SHOWHIDEDOJODURATION    = 81;
    protected static final int PROP_SHOWICONDETAILSCLASS    = 82;
    protected static final int PROP_HIDEICONDETAILSCLASS    = 83;
    protected static final int PROP_SHOWICONDETAILSTOOLTIP  = 84;
    protected static final int PROP_HIDEICONDETAILSTOOLTIP  = 85;

    protected static final int PROP_NOROWSCLASS     = 90;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_HASHEADERFACET:       return true;
            case PROP_HEADER_PAGER_AREA_TAG:            return "div";    // $NON-NLS-1$
            case PROP_HASFOOTERFACET:       return true;
            case PROP_FOOTER_PAGER_AREA_TAG:            return "div";    // $NON-NLS-1$
            case PROP_FACETTAG:             return "div";    // $NON-NLS-1$
            // TODO these styles should be in a .css file and referenced using a styleClass
            // TODO these styles have bidi problems
            case PROP_HEADERLEFTSTYLE:      return "float:left;text-align:left;";  // $NON-NLS-1$
            case PROP_HEADERRIGHTSTYLE:     return "float:right;text-align:right;";  // $NON-NLS-1$
            case PROP_FOOTERLEFTSTYLE:      return "float:left;text-align:left;";  // $NON-NLS-1$
            case PROP_FOOTERRIGHTSTYLE:     return "float:right;text-align:right;";  // $NON-NLS-1$

            case PROP_SHOWHIDEDOJOEFFECT:   return true;
            case PROP_SHOWHIDEDOJODURATION: return "400"; // $NON-NLS-1$
            case PROP_SHOWICONDETAILSTOOLTIP:   return "Show details"; // $NLS-AbstractDataViewRenderer.Showdetails-1$
            case PROP_HIDEICONDETAILSTOOLTIP:   return "Hide details"; // $NLS-AbstractDataViewRenderer.Hidedetails-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }

        if(component instanceof AbstractDataView) {
            AbstractDataView c = (AbstractDataView)component;
            writeMainContainer(context, w, c);
        }
        
        if(component instanceof AbstractDataView.RowComponent) {
            AbstractDataView c = (AbstractDataView)component.getParent();
            writeSingleRow(context, w, c);
        }
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

    /**
     * Object that caches the current data view parameters
     * @author priand
     *
     */
    protected final class ViewDefinition {
        public AbstractDataView dataView;
        public DataModel dataModel;
        // Global properties
        public boolean showItemsFlat;
        public boolean columnTitles;
        public boolean collapsibleRows;
        public boolean collapsibleCategory;
        public boolean collapsibleDetails;
        public boolean detailsOnClient;
        public String showHideDetailFunctionName;
        public boolean hasCategoryRow;
        public int multiColumnCount;
        public int categoryCount;
        public List<CategoryColumn> categoryColumns;
        public boolean hasIconColumn;
        public IconColumn iconColumn;
        public UIComponent iconFacet;
        public boolean hasSummary;
        public SummaryColumn summaryColumn;
        public UIComponent summaryFacet;
        public boolean hasDetail;
        public UIComponent detailFacet;
        // Checkbox
        public boolean hasHeaderCheckBoxColumn;
        public boolean hasCheckBoxColumn;
        public String checkboxFieldName; // Input "checkbox" name
        public String checkboxFieldNamePrefix; // Input "checkbox" id/name prefix (for uniqueness)
        public String checkboxFieldNameSuffix; // Input "checkbox" id/name suffix (for uniqueness)
        // Extra columns
        public boolean hasExtraColumns;
        public List<ExtraColumn> extraColumns;
        public boolean summaryOrDetailVisible;
        public boolean expandedDetailDefault;
        // Row related properties
        public String rowPosition;
        public boolean rowDetailVisible;
        public boolean rowDisableHideRow;
        // Request properties
        public int first;
        public int rows;
        public int nColumns;
        // cached data
        public boolean viewRowRefresh;
        public boolean singleRowRefresh;

        // ==== rendering properties - depend on the renderer ===== 
        // For multi column rendering
        public int currentColumn;   
        // Indentation management
        public int indentLevel;
        public int initialIndentLevel;
        // ViewForum renderer -> define if we should use a table for rendering the data
        public boolean viewforumRenderAsTable;
        
        // ==== Temporary fix for 852
        public int indentOffset;
        
        public boolean isInfiniteScroll = false;
        
        protected ViewDefinition(FacesContext context) {
        }
    }
    protected ViewDefinition createViewDefinition(FacesContext context) {
        ViewDefinition viewDef = new ViewDefinition(context);
        return viewDef;
    }
    protected void initViewDefinition(FacesContext context, AbstractDataView dataView, ViewDefinition viewDef) {
        List<UIComponent> children = TypedUtil.getChildren(dataView); 
        viewDef.dataView = dataView;
        viewDef.dataModel = dataView.getDataModel();
        viewDef.showItemsFlat = dataView.isShowItemsFlat(); 
        viewDef.detailsOnClient = dataView.isDetailsOnClient() || (Boolean)getProperty(PROP_SHOWHIDEONCLIENT);
        viewDef.collapsibleDetails = dataView.isCollapsibleDetail();
        viewDef.expandedDetailDefault = dataView.isExpandedDetail();
        
        if(viewDef.collapsibleDetails) {
            viewDef.showHideDetailFunctionName = ExtLibUtil.encodeJSFunctionName(dataView.getClientId(context));
        }
        viewDef.summaryColumn = dataView.getSummaryColumn();
        if(!children.isEmpty()) {
            UIComponent row = children.get(0);
            viewDef.summaryFacet = row.getFacet(AbstractDataView.FACET_SUMMARY); // row facet
            viewDef.detailFacet = row.getFacet(AbstractDataView.FACET_DETAIL);// row facet
        }
        viewDef.hasSummary = viewDef.summaryColumn!=null || viewDef.summaryFacet!=null;  
        viewDef.hasDetail = viewDef.detailFacet!=null;
        viewDef.nColumns =   (viewDef.hasCheckBoxColumn?1:0) // CheckBox
                           + 1 // Data - always
                           + (viewDef.collapsibleDetails?1:0); // Collapsible details
        viewDef.viewRowRefresh = dataView.isRowRefresh(context);
        viewDef.rows = dataView.getRows();
        
    }
    
    protected boolean getInfiniteScroll(String dataViewInfiniteScroll,FacesContext context )
    {
        boolean isAuto = false;
        
        if(dataViewInfiniteScroll == null)
        {
            isAuto = true;
        }
        else
        {
            isAuto = dataViewInfiniteScroll.equalsIgnoreCase(AUTO);  // $NON-NLS-1$
        }
        
        if(isAuto) {
            if( context instanceof FacesContextEx ) {
                FacesContextEx contextEx = (FacesContextEx)context;
                //xsp.progressive.enhancement = enable | disable
                String progEnhancement = contextEx.getProperty(XSP_PROGRESSIVE_ENH); // $NON-NLS-1$
                if(progEnhancement == null) { // is not set
                    return false;
                }
                return progEnhancement.equalsIgnoreCase(ENABLE); 
            }
        }
        
        return dataViewInfiniteScroll.equalsIgnoreCase(ENABLE); 
    }
    
    
    // ================================================================
    // Ajax Support
    // ================================================================
    
    @Override
    public void encodeRows(FacesContext context, UIDataIterator iterator, int first, int rows) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        AbstractDataView c = (AbstractDataView)iterator;

        ViewDefinition viewDef = createViewDefinition(context);
        initViewDefinition(context, c, viewDef);
        
        writeRows(context, w, c, viewDef, first, rows);
    }
    
    
    // ================================================================
    // Hidden field
    // ================================================================

    protected void writeShowHide(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        if(viewDef.detailsOnClient && viewDef.collapsibleDetails) {
            writeClientShowHideHiddenField(context, w, c, viewDef);
            addClientShowHideScript(context, w, c, viewDef);
        }
    }

    protected void addClientShowHideScript(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // TODO this code should be in a .js file, not inline in this renderer
        StringBuilder b = new StringBuilder();
        //function view__id0_dataView1(idx,pos){
        b.append("function "); // $NON-NLS-1$
        b.append(viewDef.showHideDetailFunctionName);
        b.append("(idx,pos){\n"); // $NON-NLS-1$
        //var id="view:_id0:dataView1";
        b.append("var id="); // $NON-NLS-1$
        JavaScriptUtil.addString(b, c.getClientId(context));
        b.append(";\n"); // $NON-NLS-1$
        //var img=id+":"+idx+"_shimg";
        b.append("var img=id+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ":"); // $NON-NLS-1$
        b.append("+idx+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ID_SHOWHIDE); // $NON-NLS-1$
        b.append(";\n"); // $NON-NLS-1$
        //var sum=id+":"+idx+"_sum";
        b.append("var sum=id+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ":"); // $NON-NLS-1$
        b.append("+idx+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ID_SUMMARY);
        b.append(";"); // $NON-NLS-1$
        //var det=id+":"+idx+"_detail";
        b.append("var det=id+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ":"); // $NON-NLS-1$
        b.append("+idx+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ID_DETAIL);
        b.append(";\n"); // $NON-NLS-1$
        //var inp=dojo.byId(id+"_shfld");
        b.append("var inp=dojo.byId(id+"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ID_HIDDEN);
        b.append(");\n"); // $NON-NLS-1$
        //var a=inp.value?inp.value.split(","):[];\n
        b.append("var a=inp.value?inp.value.split("); // $NON-NLS-1$
        JavaScriptUtil.addString(b, ","); // $NON-NLS-1$
        b.append("):[];\n"); // $NON-NLS-1$
        b.append("var ia=dojo.indexOf(a,pos);\n"); // $NON-NLS-1$
        //b.append("alert('a='+a.toString()+', ia='+ia);\n");
        b.append("if(ia>=0) a.splice(ia,1); else a.push(pos); inp.value=a.toString();\n"); // $NON-NLS-1$
        //var vis=dojo.style(det,"display")!="none";
        b.append("var vis=dojo.style(det,"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
        b.append(")!="); // $NON-NLS-1$
        JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
        b.append(";\n"); // $NON-NLS-1$
        //dojo.byId(img).className =vis?"lotusIcon16 lotusIconShow":"lotusIcon16 lotusIconHide";
        // Set the icon class
        String hideClass = (String)getProperty(PROP_HIDEICONDETAILSCLASS);
        String showClass = (String)getProperty(PROP_SHOWICONDETAILSCLASS);
        b.append("dojo.byId(img).className =vis?"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, showClass);
        b.append(":"); //$NON-NLS-1$
        JavaScriptUtil.addString(b, hideClass);
        b.append(";\n"); //$NON-NLS-1$
        
        String hideLabel = (String)getProperty(PROP_HIDEICONDETAILSTOOLTIP);
        String showLabel = (String)getProperty(PROP_SHOWICONDETAILSTOOLTIP);
        
        if(StringUtil.isNotEmpty(showLabel) && StringUtil.isNotEmpty(hideLabel)) {
            //var toggleTitle = vis?"Show details":"Hide details";
            b.append("var toggleTitle = vis?"); //$NON-NLS-1$
            JSUtil.addString(b,showLabel);
            b.append(":"); //$NON-NLS-1$
            JSUtil.addString(b,hideLabel);
            b.append(";\n"); //$NON-NLS-1$
            //dojo.query(dojo.byId(img)).attr("title", toggleTitle).attr("alt", toggleTitle).attr("aria-label", toggleTitle);
            b.append("dojo.query(dojo.byId(img).parentNode).attr("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, "aria-label"); // $NON-NLS-1$
            b.append(", toggleTitle).attr("); //$NON-NLS-1$
            JavaScriptUtil.addString(b, "title"); // $NON-NLS-1$
            b.append(", toggleTitle);\n"); //$NON-NLS-1$
        }

        // Hide/Show the detail pane
        boolean dojoEffect = (Boolean)getProperty(PROP_SHOWHIDEDOJOEFFECT);
        if(dojoEffect) {
            String duration = (String)getProperty(PROP_SHOWHIDEDOJODURATION);
            if(viewDef.summaryOrDetailVisible) {
                //if(vis){dojo.style(det,'display','none');dojo.style(sum,'display','block')
                //}else{dojo.style(sum,'display','none'); dojo.style(det,{opacity: '0',display:'block'});dojo.fadeIn({node:dojo.byId(det),duration:400}).play();}
                b.append("if(vis){dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append(");dojo.style(sum,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append(")}else{dojo.style(sum,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append("); dojo.style(det,{opacity: "); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "0"); // $NON-NLS-1$
                b.append(",display:"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append("});dojo.fadeIn({node:dojo.byId(det),duration:"); // $NON-NLS-1$
                JavaScriptUtil.addNumber(b, Integer.parseInt(duration));
                b.append("}).play();}\n"); // $NON-NLS-1$
            } else {
                //if(vis){dojo.style(det,'display','none');
                //}else{dojo.style(det,{opacity: '0',display:'block'});dojo.fadeIn({node:dojo.byId(det),duration:400}).play();}
                b.append("if(vis){dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append(");}else{dojo.style(det,{opacity: "); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "0"); // $NON-NLS-1$
                b.append(",display:"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append("});dojo.fadeIn({node:dojo.byId(det),duration:"); // $NON-NLS-1$
                JavaScriptUtil.addNumber(b, Integer.parseInt(duration));
                b.append("}).play();}\n"); // $NON-NLS-1$
            }
        } else {
            if(viewDef.summaryOrDetailVisible) {
                // if(vis){dojo.style(det,'display','none');dojo.style(sum,'display','block');
                //}else{dojo.style(sum,'display','none');dojo.style(det,'display','block')}
                b.append("if(vis){dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append(");dojo.style(sum,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append(");}else{dojo.style(sum,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append(");dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append(")}\n"); // $NON-NLS-1$
            } else {
                // if(vis){dojo.style(det,'display','none');
                //}else{dojo.style(det,'display','block')}
                b.append("if(vis){dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "none"); // $NON-NLS-1$
                b.append(");}else{dojo.style(det,"); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "display"); // $NON-NLS-1$
                b.append(","); // $NON-NLS-1$
                JavaScriptUtil.addString(b, "block"); // $NON-NLS-1$
                b.append(")}\n"); // $NON-NLS-1$
            }
        }
        
        b.append("}\n"); // $NON-NLS-1$
        
        UIScriptCollector col = UIScriptCollector.find();
        col.addScript(b.toString());
    }

    protected void writeClientShowHideHiddenField(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        String id = c.getClientId(context)+ID_HIDDEN;
        w.startElement("input",c);  // this is for the uistate $NON-NLS-1$
        w.writeAttribute("id",id,null); // $NON-NLS-1$
        w.writeAttribute("name",id,null); // $NON-NLS-1$
        w.writeAttribute("type","hidden",null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("value","",null);     // $NON-NLS-1$
        w.endElement("input"); // $NON-NLS-1$
    }
    
    

    // ================================================================
    // Header
    // ================================================================
    
    protected void writeHeader(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // container facets
        UIComponent header = c.getFacet(AbstractDataView.FACET_HEADER);
        if( null != header ){
            writeHeaderFacet(context, w, c, viewDef, header);
        }
        UIComponent noRowsFacet = c.getFacet(AbstractDataView.FACET_NOROWS);
        if (!hasMoreRowsToRender(c) && noRowsFacet != null) {
            return;
        }
        
        UIComponent pagerTop = c.getFacet(AbstractDataView.FACET_PAGERTOP);
        UIComponent pagerTopLeft = c.getFacet(AbstractDataView.FACET_PAGERTOPLEFT);
        UIComponent pagerTopRight = c.getFacet(AbstractDataView.FACET_PAGERTOPRIGHT);
        
        if( null != pagerTop && !pagerTop.isRendered() ){
            pagerTop = null;
        }
        if( null != pagerTopLeft && !pagerTopLeft.isRendered() ){
            pagerTopLeft = null;
        }
        if( null != pagerTopRight && !pagerTopRight.isRendered() ){
            pagerTopRight = null;
        }
        
        if( pagerTop !=null || pagerTopLeft!=null || pagerTopRight!=null) {
            writeHeaderPagerArea(context, w, c, viewDef, pagerTop, pagerTopLeft, pagerTopRight);
        }
    }
    protected void writeHeaderFacet(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef, UIComponent header) throws IOException {
        FacesUtil.renderComponent(context, header);
    }

    protected void writeHeaderPagerArea(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent pagerTop, UIComponent pagerTopLeft, UIComponent pagerTopRight) throws IOException {
        String tag = (String)getProperty(PROP_HEADER_PAGER_AREA_TAG);
        if(StringUtil.isNotEmpty(tag)) {
            startElement(w, tag, PROP_HEADERSTYLE, PROP_HEADERCLASS, PROP_HEADERDOJOTYPE);
        }
        writeHeaderLeft(context, w, c, viewDef, pagerTopLeft);
        writeHeaderMiddle(context, w, c, viewDef, pagerTop);
        writeHeaderRight(context, w, c, viewDef, pagerTopRight);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
        newLine(w);
    }
    protected void writeHeaderLeft(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_HEADERLEFTSTYLE, PROP_HEADERLEFTCLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }
    protected void writeHeaderMiddle(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_HEADERMIDDLESTYLE, PROP_HEADERMIDDLECLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }
    protected void writeHeaderRight(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_HEADERRIGHTSTYLE, PROP_HEADERRIGHTCLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }

    
    // ================================================================
    // Footer
    // ================================================================
    
    protected void writeFooter(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // container facets
        if (hasMoreRowsToRender(c)) {
            UIComponent pagerBottom = c.getFacet(AbstractDataView.FACET_PAGERBOTTOM);
            UIComponent pagerBottomLeft = c.getFacet(AbstractDataView.FACET_PAGERBOTTOMLEFT);
            UIComponent pagerBottomRight = c.getFacet(AbstractDataView.FACET_PAGERBOTTOMRIGHT);
            
            if( null != pagerBottom && !pagerBottom.isRendered() ){
                pagerBottom = null;
            }
            if( null != pagerBottomLeft && !pagerBottomLeft.isRendered() ){
                pagerBottomLeft = null;
            }
            if( null != pagerBottomRight && !pagerBottomRight.isRendered() ){
                pagerBottomRight = null;
            }
            
            if(pagerBottom !=null || pagerBottomLeft!=null || pagerBottomRight!=null) {
                writeFooterPagerArea(context, w, c, viewDef, pagerBottom, pagerBottomLeft, pagerBottomRight);
            }
        }
        
        UIComponent footer = c.getFacet(AbstractDataView.FACET_FOOTER);
        if( null != footer ){
            writeFooterFacet(context, w, c, viewDef, footer);
        }
    }
    protected void writeFooterFacet(FacesContext context, ResponseWriter w,
            AbstractDataView c, ViewDefinition viewDef, UIComponent footer) throws IOException {
        FacesUtil.renderComponent(context, footer);
    }

    protected void writeFooterPagerArea(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent pagerBottom, UIComponent pagerBottomLeft, UIComponent pagerBottomRight) throws IOException {
        String tag = (String)getProperty(PROP_FOOTER_PAGER_AREA_TAG);
        if(StringUtil.isNotEmpty(tag)) {
            startElement(w, tag, PROP_FOOTERSTYLE, PROP_FOOTERCLASS, PROP_FOOTERDOJOTYPE);
        }
        writeFooterLeft(context, w, c, viewDef, pagerBottomLeft);
        writeFooterMiddle(context, w, c, viewDef, pagerBottom);
        writeFooterRight(context, w, c, viewDef, pagerBottomRight);
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
        newLine(w);
    }
    protected void writeFooterLeft(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_FOOTERLEFTSTYLE, PROP_FOOTERLEFTCLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }
    protected void writeFooterMiddle(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_FOOTERMIDDLESTYLE, PROP_FOOTERMIDDLECLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }
    protected void writeFooterRight(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, UIComponent facet) throws IOException {
        if(facet!=null) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, PROP_FOOTERRIGHTSTYLE, PROP_FOOTERRIGHTCLASS, null);
            }
            FacesUtil.renderComponent(context, facet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }

    
    
    // ================================================================
    // Write a single row - used by partial refresh
    // ================================================================

    protected void writeSingleRow(FacesContext context, ResponseWriter w, AbstractDataView c) throws IOException {
        ViewDefinition viewDef = new ViewDefinition(context);
        initViewDefinition(context, c, viewDef);
        viewDef.singleRowRefresh = true;
        
        // The data iterator already put us on the right row
        beforeRow(context, w, c, viewDef);
        writeRow(context, w, c, viewDef);
        afterRow(context, w, c, viewDef);
    }
    

    // ================================================================
    // Common implementation
    // ================================================================
    
    @Override
    protected final void writeMainContainer(FacesContext context, ResponseWriter w, UIDataSourceIterator _c) throws IOException {
        AbstractDataView c = (AbstractDataView)_c;
        ViewDefinition viewDef = createViewDefinition(context);
        initViewDefinition(context, c, viewDef);
        writeDataView(context, w, c, viewDef);
    }
        
    protected void writeDataView(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        // Start the main frame
        w.startElement("div",c); // $NON-NLS-1$
        w.writeAttribute("id",c.getClientId(context),null); // $NON-NLS-1$
        
        String style = c.getStyle();
        if(StringUtil.isEmpty(style)) {
            style = (String)getProperty(PROP_MAINDIVSTYLE);
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = c.getStyleClass();
        if(StringUtil.isEmpty(styleClass)) {
            styleClass = (String)getProperty(PROP_MAINDIVCLASS);
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }

        if (c instanceof UIDataView) {
            UIDataView dv = (UIDataView)c;
            String role = dv.getRole();
            if(!StringUtil.isEmpty(role)) {
                w.writeAttribute("role", role, null); // $NON-NLS-1$
            }
        }

        // Write the support field for show/hide
        writeShowHide(context, w, c, viewDef);
                
        // Write the header
        boolean hasHeader = (Boolean)getProperty(PROP_HASHEADERFACET);
        if(hasHeader) {
            writeHeader(context, w, c, viewDef);
        }
        
        // Write the body
        UIComponent noRowsFacet = c.getFacet(AbstractDataView.FACET_NOROWS);
        if (!hasMoreRowsToRender(c) && noRowsFacet != null) {
            writeNoRowsContent(context, w, c, viewDef);
        }
        else {
            writeContent(context, w, c, viewDef);
        }
        
        // Write the footer
        boolean hasFooter = (Boolean)getProperty(PROP_HASFOOTERFACET);
        if(hasFooter) {
            writeFooter(context, w, c, viewDef);
        }
        
        // Close the main container
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }
    
    protected abstract void writeContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException;
    
    protected void writeNoRowsContent(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
        UIComponent noRowsFacet = c.getFacet(AbstractDataView.FACET_NOROWS);
        if( null != noRowsFacet) {
            String tag = (String)getProperty(PROP_FACETTAG);
            if(StringUtil.isNotEmpty(tag)) {
                startElement(w, tag, null, PROP_NOROWSCLASS, null);
            }
            FacesUtil.renderComponent(context, noRowsFacet);
            if(StringUtil.isNotEmpty(tag)) {
                w.endElement(tag);
            }
            newLine(w);
        }
    }
    
    protected void writeRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef, int first, int rows) throws IOException {
        try {
            // Initialize the view definition
            viewDef.first = first;
            viewDef.rows = rows;

            beforeRows(context, w, c, viewDef);
            
            // Horrible fix for 852...
            if(ExtLibUtil.isXPages852()) {
                // We use the offset that was previously stored when we read the very first row
                // This assumes that the view had been already read from the beginning.
                Object o = c.getAttributes().get("__view_indent_offset"); // $NON-NLS-1$
                if(o instanceof Integer) {
                    viewDef.indentOffset = (Integer)o;
                }
            }
            
            for(int i=0; i<rows; i++) {
                int index = first+i;
                c.setRowIndex(index);
                if(!c.isRowAvailable()) {
                    if(index == 0 && c instanceof UIForumView){
                        w.startElement("li", null); //$NON-NLS-1$
                        w.writeComment("no content"); //$NON-NLS-1$
                        w.endElement("li"); //$NON-NLS-1$
                    }
                    break;
                }
                if(ExtLibUtil.isXPages852()) {
                    if(index==0) {
                        // There is a bug in the view datamodel in 852
                        // When the view do start from the root level (ex: from a parent id), then 
                        viewDef.indentOffset = Math.max(0,calculateIndentOffset(context, c, viewDef));
                        if(viewDef.indentOffset>0) {
                            TypedUtil.getAttributes(c).put("__view_indent_offset",Integer.valueOf(viewDef.indentOffset)); // $NON-NLS-1$
                        }
                    }
                }
                beforeRow(context, w, c, viewDef);
                writeRow(context, w, c, viewDef);
                afterRow(context, w, c, viewDef);
            }

            afterRows(context, w, c, viewDef);
        } finally {
            //reset
            c.setRowIndex(-1);
        }
    }
    protected void beforeRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
    }
    protected void afterRows(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException {
    }
    protected void beforeRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException{
        viewDef.rowPosition = getRowPosition(context, c, viewDef);
        viewDef.rowDisableHideRow = c.isDisableHideRow();
        viewDef.rowDetailVisible = isRowDetailVisible(context, c, viewDef);
    }
    protected void afterRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException{
    }

    protected abstract void writeRow(FacesContext context, ResponseWriter w, AbstractDataView c, ViewDefinition viewDef) throws IOException;
    
    protected boolean hasMoreRowsToRender(AbstractDataView c) {
        if (c.getFirst() > 0) return true;
        
        boolean more = false;
        // preserve old index
        int idx = c.getRowIndex();
        // set index to first row for current page. If step 10, then first row for Page 1 is 0, first row for Page 2 is 10
        c.setRowIndex(c.getFirst());
        // calculate if more rows are available on current page
        boolean isRowAvailable = c.isRowAvailable();
        more = c.getFirst() > 0 || isRowAvailable;
        // restore index
        c.setRowIndex(idx);
        // Navigation to previous page could be considered later, but right now we'll stay on the same page
        // if we are on a page > 1 and no more rows on it, then navigate to previous page
        // if (c.getFirst() > 0 && !isRowAvailable) c.gotoPreviousPage();
        return more;
    }
}