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

package com.ibm.xsp.extlib.renderkit.dojo.grid;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridColumn;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridRow;
import com.ibm.xsp.extlib.component.rest.UIRestService;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;


public class DojoGridRenderer extends DojoWidgetRenderer {

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.dojoxGridDataGrid;
    }
    
    @Override
    protected Resource[] getExtraResources(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.GRID_EXTRA_RESOURCES;
    }

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "dojox.grid.DataGrid"; // $NON-NLS-1$
    }

    @Override
    protected String getTagName() {
        return "table"; // $NON-NLS-1$
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoDataGrid) {
            UIDojoDataGrid c = (UIDojoDataGrid)dojoComponent;

            String role = c.getWaiRole();
            if (StringUtil.isEmpty(role)) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs, "waiRole", "presentation"); // $NON-NLS-1$ $NON-NLS-2$
            }

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"jsId",c.getJsId()); // $NON-NLS-1$

            String storeId = UIRestService.findRestServiceStoreId(context, c, c.getStoreComponentId());
            if(StringUtil.isEmpty(storeId)) {
                storeId = c.getStore();
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"store",storeId); // $NON-NLS-1$

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rowSelector",c.getRowSelector()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"selectionMode",c.getSelectionMode()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"headerMenu",c.getHeaderMenu()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"autoHeight",c.getAutoHeight()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"singleClickEdit",c.isSingleClickEdit()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"loadingMessage",c.getLoadingMessage()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"errorMessage",c.getErrorMessage()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"selectable",c.isSelectable()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"updateDelay",c.getUpdateDelay(),-1); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"initialWidth",c.getInitialWidth()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"escapeHTMLInData",c.isEscapeHTMLInData(), true); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rowsPerPage",c.getRowsPerPage(),-1); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"query",c.getQuery()); // $NON-NLS-1$
            
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onStyleRow",c.getOnStyleRow()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onRowClick",c.getOnRowClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onRowDblClick",c.getOnRowDblClick()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onRowContextMenu",c.getOnRowContextMenu()); // $NON-NLS-1$
        }
    }
    

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }
        
        JSUtil.writeln(writer);
        writer.startElement("thead", component); // $NON-NLS-1$
        
        // Write the columns that are standalone
        boolean columnHead = false;
        boolean nonColumn = false;
        for( UIComponent c: TypedUtil.getChildren(component) ) {
            if(c instanceof UIDojoDataGridColumn) {
                if(!columnHead) {
                    JSUtil.writeln(writer);
                    writer.startElement("tr", component); // $NON-NLS-1$
                    columnHead = true;
                }
                emitColumn(context, writer, (UIDojoDataGridColumn)c);
            } else {
                nonColumn = true;
            }
        }
        if(columnHead) {
            JSUtil.writeln(writer);
            writer.endElement("tr"); // $NON-NLS-1$
        }
        
        // Now, write the other rows, if any
        if(nonColumn) {
            for( UIComponent c: TypedUtil.getChildren(component) ) {
                if(c instanceof UIDojoDataGridRow) {
                    JSUtil.writeln(writer);
                    writer.startElement("tr", component); // $NON-NLS-1$
                    for( UIComponent c2: TypedUtil.getChildren(c) ) {
                        if(c2 instanceof UIDojoDataGridColumn) {
                            emitColumn(context, writer, (UIDojoDataGridColumn)c2);
                        }
                    }
                    JSUtil.writeln(writer);
                    writer.endElement("tr"); // $NON-NLS-1$
                }
            }
        }
        
        JSUtil.writeln(writer);
        writer.endElement("thead"); // $NON-NLS-1$
        JSUtil.writeln(writer);
    }
    protected void emitColumn(FacesContext context, ResponseWriter writer, UIDojoDataGridColumn c) throws IOException {
        if(!c.isRendered()) {
            return;
        }
        JSUtil.writeln(writer);
        writer.startElement("th", c); // $NON-NLS-1$
        
        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"field",c.getField()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"width",c.getWidth()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"cellType",c.getCellType()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"formatter",c.getFormatter()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"get",c.getGet()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"options",c.getOptions()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"editable",c.isEditable()); // $NON-NLS-1$
        DojoRendererUtil.addDojoHtmlAttributes(attrs,"hidden",c.isHidden()); // $NON-NLS-1$
        DojoRendererUtil.writeDojoHtmlAttributesMap(context,attrs);
        
        String title = c.getLabel();
        if(StringUtil.isNotEmpty(title)) {
            writer.writeText(title, "label"); // $NON-NLS-1$
        }

        writer.endElement("th"); // $NON-NLS-1$
    }
}