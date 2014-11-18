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

package com.ibm.xsp.extlib.renderkit.html_basic.listview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.domino.ExtlibJsIdUtil;
import com.ibm.xsp.extlib.component.listview.UIListView;
import com.ibm.xsp.extlib.component.listview.UIListViewColumn;
import com.ibm.xsp.extlib.resources.domino.DojoResourceConstants;
import com.ibm.xsp.extlib.resources.domino.DojoResources;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author akosugi
 *
 *        renderer for notes list view
 */
public class ListViewRenderer extends FacesRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        String id = component.getClientId(context) + ":_sel"; // $NON-NLS-1$
        UIListView uiComponent = (UIListView) component;
        Map<String, String> map = TypedUtil.getRequestParameterMap(context.getExternalContext());
        if (map.containsKey(id)) {
            String selectedIds = map.get(id);
            uiComponent.setSelectedIds(StringUtil.isNotEmpty(selectedIds) ? selectedIds.split(",") : null);
        }
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIListView uiComponent = (UIListView) component;
        boolean rendered = component.isRendered();

        if (!rendered)
            return;
        UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
        rootEx.addEncodeResource(DojoResources.notesFullListView);
        rootEx.addEncodeResource(DojoResources.listViewCSS);
        rootEx.setDojoParseOnLoad(true);
        rootEx.setDojoTheme(true);

        String store = ExtlibJsIdUtil.findDojoWidgetId(context, uiComponent, uiComponent.getStoreComponentId());
        String structure = ExtlibJsIdUtil.findDojoWidgetId(context, uiComponent, uiComponent.getStructureComponentId());
        String id = uiComponent.getClientId(context);
        String jsId = uiComponent.getDojoWidgetJsId(context);

        if(StringUtil.isEmpty(structure) && component.getChildCount() == 0){//need to be revised//
            rootEx.addEncodeResource(DojoResources.dominoDesignStore);
            w.startElement("span", null); // $NON-NLS-1$
            w.writeAttribute(DojoResourceConstants.dojoType,
                    DojoResourceConstants.DominoReadDesign, null);
            structure = uiComponent.getDojoWidgetJsId(context) + "_default_view_design_jsid"; // $NON-NLS-1$
            w.writeAttribute("jsId", structure , null); // $NON-NLS-1$
            w.writeAttribute("dwa", "false", null); // $NON-NLS-1$ $NON-NLS-2$
            w.endElement("span"); // $NON-NLS-1$
        }
        w.startElement("div", uiComponent); // $NON-NLS-1$
        String style = uiComponent.getStyle();
        if (StringUtil.isNotEmpty(style))
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        String classname = uiComponent.getStyleClass();
        if (StringUtil.isNotEmpty(classname))
            w.writeAttribute("class", classname, null); // $NON-NLS-1$

        w.writeAttribute(DojoResourceConstants.dojoType,
                DojoResourceConstants.notesFullListView, null);
        if(StringUtil.isNotEmpty(store))
            w.writeAttribute("store", store, null); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(structure))
            w.writeAttribute("structure", structure, null); // $NON-NLS-1$
        if (StringUtil.isNotEmpty(id))
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        if (StringUtil.isNotEmpty(jsId))
            w.writeAttribute("jsId", jsId, null); // $NON-NLS-1$
        if(uiComponent.isHideColumns()){ // the internal attr tag can remain 'hideColumn'
            w.writeAttribute("hideColumn", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        if(uiComponent.isAlternateRows()){
            w.writeAttribute("alternateRows", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        if(uiComponent.isCanBeNarrowMode()){
            w.writeAttribute("canBeNarrowMode", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        }

        String hookedEvents = "";
        if( StringUtil.isNotEmpty(uiComponent.getOnCellClick()) ){
            hookedEvents = "click"; // $NON-NLS-1$
        }
        if( StringUtil.isNotEmpty(uiComponent.getOnCellDblClick()) ){
            if( hookedEvents.length() > 0 ){
                hookedEvents += ",dblclick"; // $NON-NLS-1$
            }else{
                hookedEvents = "dblclick"; // $NON-NLS-1$
            }
        }
        if( hookedEvents.length() > 0 ){
            w.writeAttribute("hookedEvents", hookedEvents, null); // $NON-NLS-1$
        }

    }
    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
    throws IOException {
        UIListView uiComponent = (UIListView) component;
        List<UIComponent> list = TypedUtil.getChildren(component);
        ArrayList<JsonJavaObject> cells = new ArrayList<JsonJavaObject>();
        for( UIComponent c: list ) {
            if(c instanceof UIListViewColumn) {
                UIListViewColumn col = (UIListViewColumn)c;
                if(col.isHidden()){
                    continue;
                }
                JsonJavaObject cell = new JsonJavaObject();
                String width = col.getWidth();
                String title = col.getColumnTitle();
                String columnName = col.getColumnName();
                String narrowDisplay = col.getNarrowDisplay();

                int idx = 0;
                if(StringUtil.isNotEmpty(width)){
                    if("auto".equals(width)){ // $NON-NLS-1$
                        //width should not be declared
                    }else if(-1 != (idx = width.indexOf("px"))){ // $NON-NLS-1$
                        cell.putBoolean("bChars", false); // $NON-NLS-1$
                        cell.putInt("nWidth", Integer.valueOf(width.substring(0,idx)).intValue()); // $NON-NLS-1$
                    }else{
                        cell.putInt("nWidth", Integer.valueOf(width).intValue()); // $NON-NLS-1$
                    }
                }
                if(StringUtil.isNotEmpty(columnName)){
                    cell.putString("sName", columnName); // $NON-NLS-1$
                }

                if(StringUtil.isNotEmpty(title)){
                    cell.putString("sTitle", title); // $NON-NLS-1$
                }else if(!uiComponent.isShowColumnNameForEmptyTitle()){
                    cell.put("sTitle", ""); // $NON-NLS-1$
                }
                if(col.isExtendable()){
                    cell.putBoolean("bExtend", true); // $NON-NLS-1$
                }
                if(col.isShowGradient()){
                    cell.putBoolean("bShowGradientColor", true); // $NON-NLS-1$
                }
                if(col.isFixedWidth()){
                    cell.putBoolean("bFixed", true); // $NON-NLS-1$
                }
                if(StringUtil.isNotEmpty(narrowDisplay)){
                    cell.putString("sNarrowDisplay", narrowDisplay); // $NON-NLS-1$
                }
                int sequenceNumber = col.getSequenceNumber();
                if(sequenceNumber > Integer.MIN_VALUE){
                    cell.putInt("nSequenceNumber", sequenceNumber); // $NON-NLS-1$
                }
                if(col.isBeginWrapUnder()){
                    cell.putBoolean("bBeginWrapUnder", true); // $NON-NLS-1$
                }
                if(col.isTwistie()){
                    cell.putBoolean("bTwistie", true); // $NON-NLS-1$
                }
                if(col.isResponse()){
                    cell.putBoolean("bResponse", true); // $NON-NLS-1$
                }
                if(col.isCategory()){
                    cell.putBoolean("bCategory", true); // $NON-NLS-1$
                }
                if(col.getSort() != UIListViewColumn.SORT_NONE ){
                    cell.putInt("bSort", col.getSort()); // $NON-NLS-1$
                }
                if(col.isIcon()){
                    cell.putBoolean("bIsIcon", true); // $NON-NLS-1$
                }
                cells.add(cell);
            }
        }
        if(cells.size() > 0){
            JsonJavaObject entry = new JsonJavaObject();
            entry.put("cells", cells); // $NON-NLS-1$
            ArrayList<JsonJavaObject> structure = new ArrayList<JsonJavaObject>();
            structure.add(entry);
            ResponseWriter w = context.getResponseWriter();
            try {
                w.writeAttribute("structure", JsonGenerator.toJson(JsonJavaFactory.instance,structure,true), null); // $NON-NLS-1$
            } catch (JsonException e) {
                IOException ex = new IOException();
                ex.initCause(e);
                throw ex;
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIListView uiComponent = (UIListView) component;
        uiComponent.writeActionHandlerScripts(w);
        w.endElement("div"); // $NON-NLS-1$

    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}