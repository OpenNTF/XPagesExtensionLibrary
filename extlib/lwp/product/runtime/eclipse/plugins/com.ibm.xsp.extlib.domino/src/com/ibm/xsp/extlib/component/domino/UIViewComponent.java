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

package com.ibm.xsp.extlib.component.domino;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;

/**
 * @author akosugi
 * 
 *        base class for ui view controls
 */
public abstract class UIViewComponent extends UIComponentBase implements FacesExtlibJsIdWidget {

    private String storeComponentId;
    private String style;
    private String styleClass;
    // TODO selectedIds should not be stored in the component (wouldn't work if
    // the component was placed in a repeat), they should be stored in the
    // DataModel instead, 
    // see com.ibm.xsp.model.TabularDataModel.isSelectedId(String)
    private String[] selectedIds;//*todo* what for??//tbr
    //_ak// this is  to pass the ids that are selected from the view entries in client dojo widget
    // for the logics that is running on server.
    // once user selects some entries and clicks submit button on the page, the ids that were selected on that time is POSTed to the server.
    // then, listview renderer receives them in decode() method and set it to the component instance with "setSelectedIds".
    // finally the code on the server side gets the UIcomponent instance of the listview, and takes the value and does something.
    // actually, I'm not sure if this is the right way, but we're asked to do in this way by nsf converter(a prototype which converts nsf design to xsp) team in China.
    // maybe they wanted to make converter code to unify the one which is using xsp view panel.
    // we have added a client code just for this purpose in web/dwa/xsp/listview.js
    // ,but normally, our widget have an interface with which user can add the code on the client to handle the selection of the entries
    // , and usually submit requests in that code as needed.
    // If this is not required for now, I can drop this.
    // If this is mandatory, I suppose I have to assosiate this UIViewComponent instance with some subclass of TabularDataModel
    // , but it was hard for me to find out what should I do that.
    // I saw there are some UIComponent classes which has association with those class(e.g. UIViewPanel)
    // , but this class already have parent, and cannot extend from those classes.
    // Any suggestion, advice are deeply appreciated. thanks.
    
    //private Boolean autoResize;

    //action handlers as base properties//
    private String onNewEntry;
    private String onOpenEntry;
    private String onDeleteEntry;
    private String onSelectEntry;
    private String onContextMenu;
    private String jsId;

    public String getStoreComponentId() {
        if (storeComponentId != null)
            return storeComponentId;
        ValueBinding _vb = getValueBinding("storeComponentId"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setStoreComponentId(String store) {
        this.storeComponentId = store;
    }


    public String getStyle() {
        if (style != null)
            return style;
        ValueBinding _vb = getValueBinding("style"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        if (styleClass != null)
            return styleClass;
        ValueBinding _vb = getValueBinding("styleClass"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }



    public void setSelectedIds(String[] selectedIds) {
        this.selectedIds = selectedIds;
    }

    public String[] getSelectedIds() {
        return this.selectedIds;
    }
    public String getOnOpenEntry() {
        if (onOpenEntry != null)
            return onOpenEntry;
        ValueBinding _vb = getValueBinding("onOpenEntry"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnOpenEntry(String openEntryAction) {
        this.onOpenEntry = openEntryAction;
    }

    public String getOnNewEntry() {
        if (onNewEntry != null)
            return onNewEntry;
        ValueBinding _vb = getValueBinding("onNewEntry"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnNewEntry(String newEntryAction) {
        this.onNewEntry = newEntryAction;
    }

    public String getOnDeleteEntry() {
        if (onDeleteEntry != null)
            return onDeleteEntry;
        ValueBinding _vb = getValueBinding("onDeleteEntry"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnDeleteEntry(String deleteEntryAction) {
        this.onDeleteEntry = deleteEntryAction;
    }
        
    public String getOnSelectEntry() {
        if (onSelectEntry != null)
            return onSelectEntry;
        ValueBinding _vb = getValueBinding("onSelectEntry"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnSelectEntry(String selectEntryAction) {
        this.onSelectEntry = selectEntryAction;
    }

    public String getOnContextMenu() {
        if (onContextMenu != null)
            return onContextMenu;
        ValueBinding _vb = getValueBinding("onContextMenu"); // $NON-NLS-1$
        if (_vb != null)
            return (String) _vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setOnContextMenu(String handleContextMenu) {
        this.onContextMenu = handleContextMenu;
    }
    public String getJsId() {
        if (jsId != null)
            return jsId;
        ValueBinding vb = getValueBinding("jsId"); // $NON-NLS-1$
        if (vb != null)
            return (String) vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setJsId(String jsid) {
        this.jsId = jsid;
    }
    public String getDojoWidgetJsId(FacesContext context) {
        String jsId = getJsId();
        if(StringUtil.isNotEmpty(jsId)) {
            return jsId;
        }
        return ExtlibJsIdUtil.getClientIdAsJsId(this, context);
    }
    public void writeActionHandlerScripts(ResponseWriter writer) throws IOException {
        String value = this.getOnOpenEntry();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "openEntryAction", "items", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        value = this.getOnSelectEntry();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "selectEntryAction", "items,selectionMode", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        value = this.getOnNewEntry();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "newEntryAction", "calendar", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        value = this.getOnDeleteEntry();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "deleteEntryAction", "items", value); // $NON-NLS-1$ $NON-NLS-2$
        }
        this.writeHandleContextMenuScript(writer);
        
    }
    protected void writeHandleContextMenuScript(ResponseWriter writer) throws IOException {
        //calendar view and list view takes different handler name..
        //would better be unified in javascript code..
        String value = this.getOnContextMenu();
        if(StringUtil.isNotEmpty(value)){
            this.writeActionHandlerScript(writer, "handleContextMenu", "ev,items", value); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
    protected void writeActionHandlerScript(ResponseWriter writer, String name, String args, String value) throws IOException{
        HashMap<String,String> attributes = new HashMap<String,String>();
        attributes.put("type", "dojo/connect"); // $NON-NLS-1$ $NON-NLS-2$
        attributes.put("language", "JavaScript"); // $NON-NLS-1$ $NON-NLS-2$
        attributes.put("event", name); // $NON-NLS-1$
        attributes.put("args", args); // $NON-NLS-1$
        this.writeScriptTag(writer, attributes, value);
    }
    protected void writeInternalScript(ResponseWriter writer, String value) throws IOException{
        HashMap<String,String> attributes = new HashMap<String,String>();
        attributes.put("type", "text/javascript"); // $NON-NLS-1$ $NON-NLS-2$
        attributes.put("language", "JavaScript"); // $NON-NLS-2$ $NON-NLS-1$
        this.writeScriptTag(writer, attributes, value);
    }
    private void writeScriptTag(ResponseWriter writer, Map<String, String> attributes, String contents) throws IOException{
        writer.startElement("script", null); // $NON-NLS-1$
        for(String key : attributes.keySet()){
            writer.writeAttribute(key, attributes.get(key), null);
        }
        writer.writeText(contents, null);
        writer.endElement("script");     // $NON-NLS-1$
    }
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[11];
        values[0] = super.saveState(context);
        values[1] = storeComponentId;
        values[2] = style;
        values[3] = styleClass;
        values[4] = onNewEntry;
        values[5] = onOpenEntry;
        values[6] = onDeleteEntry;
        values[7] = onSelectEntry;
        values[8] = onContextMenu;
        values[9] = jsId;
        return values;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.storeComponentId = (String) values[1];
        this.style = (String) values[2];
        this.styleClass = (String) values[3];
        this.onNewEntry = (String) values[4];
        this.onOpenEntry = (String) values[5];
        this.onDeleteEntry = (String) values[6];
        this.onSelectEntry = (String) values[7];
        this.onContextMenu = (String) values[8];
        this.jsId = (String) values[9];
    }

}