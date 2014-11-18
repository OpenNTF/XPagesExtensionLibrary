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

package com.ibm.xsp.extlib.renderkit.html_extended.misc;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.misc.UIDumpObject;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.debug.DumpAccessor;
import com.ibm.xsp.extlib.util.debug.DumpContext;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;


/**
 * Renderer the dumps a component.
 * 
 * @author priand
 */
public class DumpObjectRenderer extends FacesRendererEx {

	private final static String CATEGORY_ID_PREFIX = "c"; // $NON-NLS-1$
	private final static String MAP_ID_PREFIX = "m"; // $NON-NLS-1$
	private final static String ITEM_ID_PREFIX = "i"; // $NON-NLS-1$
	
    protected static class Context extends DumpContext {
        IdentityHashMap<Object, Object> stack;
        // Ident level & level are different because of the categories that do not count
        int indentLevel;
        int level;
        int maxLevel;
        String filter;
        int maxGridRows;
        int catId;
        int mapId;
        int itemId;
        Context(int maxLevel, String filter, int maxGridRows) {
            this.stack = new IdentityHashMap<Object, Object>();
            this.maxLevel = maxLevel;
            this.filter = filter;
            this.maxGridRows = maxGridRows;
        }
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if(!component.isRendered()) {
            return;
        }
        
        ResponseWriter w = context.getResponseWriter();
        UIDumpObject c = (UIDumpObject)component;
        
        Object value = c.findObject(context);
        String clientId = c.getClientId(context);
        
        generateStyle(context,w);
        
        w.startElement("div", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, null); // $NON-NLS-1$
        w.writeAttribute("style", "margin-top: 16px; margin-bottom: 16px; overflow-x:auto;", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        w.startElement("table", component); // $NON-NLS-1$
        String tableSummary = "Specifies a selection of labels and their associated values displayed as tabular data."; // $NLS-DumpObjectRenderer.Specifiesaselectionoflabelsandthe-1$
        w.writeAttribute("summary", tableSummary, null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTable", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        writeTitle(w, c);
        
        int level = c.getLevels();
        if(level==0) {
            level = 999;
        }
        String filter = c.getStartFilter();
        int maxGridRows = c.getMaxGridRows();
        final boolean isUseBean = c.isUseBeanProperties();
        
        Context dumpContext = new Context(level,filter,maxGridRows) {
            @Override
            public boolean shouldUseBeanProperties(Object o) {
                return isUseBean;
            }
        };
        dumpObject(w, dumpContext, null, value, clientId);
        
        w.endElement("table"); // $NON-NLS-1$
        newLine(w);
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }

    
    
    private void generateStyle(FacesContext context, ResponseWriter w) throws IOException {
        UIViewRootEx root = (UIViewRootEx)context.getViewRoot();
        if(root.getEncodeProperty(this.getClass().getName())==null) {
            root.putEncodeProperty(this.getClass().getName(),Boolean.TRUE);
            w.write("<style type=\"text/css\">"); // $NON-NLS-1$
            w.write(".xspDumpTable {"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("border-style: solid !important;"); // $NON-NLS-1$
            w.write("border-color: black !important;"); // $NON-NLS-1$
            w.write("border-spacing: 0px !important;"); // $NON-NLS-1$
            w.write("border-collapse: collapse !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTR {"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 3px !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDTitle {"); // $NON-NLS-1$
            w.write("text-align: left;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 3px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: gray !important;"); // $NON-NLS-1$
            w.write("color: white !important;"); // $NON-NLS-1$
            w.write("font-weight:bold !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDName {"); // $NON-NLS-1$
            w.write("width: 20em !important;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDNoValue {"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDValue {"); // $NON-NLS-1$
            w.write("width: 50em !important;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDCat1 {"); // $NON-NLS-1$
            w.write("text-align: left;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: rgb(225, 225, 225) !important;"); // $NON-NLS-1$
            w.write("font-weight:bold !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDCat2 {"); // $NON-NLS-1$
            w.write("text-align: left;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpGridTable {"); // $NON-NLS-1$
            w.write("margin: 0px !important;"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("border-style: solid !important;"); // $NON-NLS-1$
            w.write("border-color: black !important;"); // $NON-NLS-1$
            w.write("border-spacing: 0px !important;"); // $NON-NLS-1$
            w.write("border-collapse: collapse !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDGridHeader {"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: rgb(225, 225, 225) !important;"); // $NON-NLS-1$
            w.write("font-weight:bold !important;"); // $NON-NLS-1$
            w.write("}");
            w.write(".xspDumpTDGridValue {"); // $NON-NLS-1$
            w.write("border-width: 1px !important;"); // $NON-NLS-1$
            w.write("padding: 2px !important;"); // $NON-NLS-1$
            w.write("border-style: outset !important;"); // $NON-NLS-1$
            w.write("border-color: gray !important;"); // $NON-NLS-1$
            w.write("background-color: white !important;"); // $NON-NLS-1$
            w.write("}");
            w.write("</style>"); // $NON-NLS-1$
            newLine(w);
        }
    }
    private void writeTitle(ResponseWriter w, UIDumpObject o) throws IOException {
        String s = HtmlUtil.toHTMLContentString(o.getTitle(), false, HtmlUtil.useHTML);
        if(StringUtil.isEmpty(s)) {
            s = HtmlUtil.toHTMLContentString(o.getObjectNames(), false, HtmlUtil.useHTML);
        }
        if(StringUtil.isEmpty(s)) {
            s = HtmlUtil.toHTMLContentString("Object Dump", false, HtmlUtil.useHTML); // $NLS-DumpObjectRenderer.ObjectDump-1$
        }
        w.startElement("tr", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.startElement("th", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTDTitle", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("colspan", "2", null); // $NON-NLS-1$
        w.write(s);
        w.endElement("th"); // $NON-NLS-1$
        
        w.endElement("tr"); // $NON-NLS-1$
        newLine(w);
    }
    
    private void dumpObject(ResponseWriter w, Context dump, String name, Object o, String clientId) throws IOException {
        // If we already reached the number of levels just forget about it
        if(dump.level>dump.maxLevel) {
            return;
        }
        
        // If the Object had already been dumped, as part of the stack, don't do it recursively...
        if(!dump.stack.containsKey(o)) {
            dump.stack.put(o,Boolean.TRUE);
            try {
                DumpAccessor a = DumpAccessor.find(dump,o);
                switch(a.getType()) {
                    case DumpAccessor.TYPE_VALUE: {
                        dumpValue(w,dump,name,(DumpAccessor.Value)a, clientId);
                    } break;
                    case DumpAccessor.TYPE_ARRAY: {
                        dumpArray(w,dump,name,(DumpAccessor.Array)a, clientId);
                    } break;
                    case DumpAccessor.TYPE_MAP: {
                        dumpMap(w,dump,name,(DumpAccessor.Map)a, clientId);
                    } break;
                    case DumpAccessor.TYPE_GRID: {
                        dumpGrid(w,dump,name,(DumpAccessor.Grid)a, clientId);
                    } break;
                }
            } finally {
                dump.stack.remove(o);
            }
        }
    }
    
    private void dumpValue(ResponseWriter w, Context dump, String name, DumpAccessor.Value a, String clientId) throws IOException {
        dumpValue(w, dump, name, a.getValueAsString(), a.getTypeAsString(), clientId);
    }
    private void dumpValue(ResponseWriter w, Context dump, String name, String value, String title, String clientId) throws IOException {
        w.startElement("tr", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.startElement("td", null); // $NON-NLS-1$
        dump.itemId++;
    	w.writeAttribute("id", clientId + "_" + ITEM_ID_PREFIX + dump.itemId, null); // $NON-NLS-1$ $NON-NLS-2$
        if (dump.catId != -1) {
        	w.writeAttribute("headers", clientId + "_" + CATEGORY_ID_PREFIX + dump.catId, null); // $NON-NLS-1$ $NON-NLS-2$
        }
        if(StringUtil.isNotEmpty(name)) {
            w.writeAttribute("class", "xspDumpTDName", null); // $NON-NLS-1$ $NON-NLS-2$
            if(StringUtil.isNotEmpty(title)) {
                w.writeAttribute("title", title, null); // $NON-NLS-1$
            }
            writeIndent(w, dump.indentLevel);
            if(StringUtil.isNotEmpty(name)) {
                w.writeText(name,null);
            } else {
                w.write("&nbsp;"); // $NON-NLS-1$
            }
            w.endElement("td"); // $NON-NLS-1$
            w.startElement("td", null); // $NON-NLS-1$
            if (dump.catId != -1) {
            	w.writeAttribute("headers", clientId + "_" + CATEGORY_ID_PREFIX + dump.catId + " " + clientId + "_" + ITEM_ID_PREFIX + dump.itemId, null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            }
            else {
            	w.writeAttribute("headers", clientId + "_" + ITEM_ID_PREFIX + dump.itemId, null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.writeAttribute("class", "xspDumpTDValue", null); // $NON-NLS-1$ $NON-NLS-2$
        } else {
            w.writeAttribute("class", "xspDumpTDNoValue", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("colspan", "2", null); // $NON-NLS-1$
        }

        String s = HtmlUtil.toHTMLContentString(value, false, HtmlUtil.useHTML);
        if(StringUtil.isNotEmpty(s)) {
            w.write(s);
        } else {
            w.write("&nbsp;"); // $NON-NLS-1$
        }
        w.endElement("td"); // $NON-NLS-1$
        
        w.endElement("tr"); // $NON-NLS-1$
        newLine(w);
    }
    private void dumpArray(ResponseWriter w, Context dump, String name, DumpAccessor.Array a, String clientId) throws IOException {
        w.startElement("tr", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.startElement("td", null); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(name)) {
            w.writeAttribute("class", "xspDumpTDName", null); // $NON-NLS-1$ $NON-NLS-2$
//            if(StringUtil.isNotEmpty(title)) {
//                w.writeAttribute("title", title, null); // $NON-NLS-1$
//            }
            writeIndent(w, dump.indentLevel);
            if(StringUtil.isNotEmpty(name)) {
                w.writeText(name,null);
            } else {
                w.write("&nbsp;"); // $NON-NLS-1$
            }
            w.endElement("td"); // $NON-NLS-1$
            w.startElement("td", null); // $NON-NLS-1$
            if (dump.catId != -1) {
            	w.writeAttribute("headers", clientId + "_" + CATEGORY_ID_PREFIX + dump.catId + " " + clientId + "_" + ITEM_ID_PREFIX + dump.itemId, null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            }
            else {
            	w.writeAttribute("headers", clientId + "_" + ITEM_ID_PREFIX + dump.itemId, null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.writeAttribute("class", "xspDumpTDValue", null); // $NON-NLS-1$ $NON-NLS-2$
        } else {
            w.writeAttribute("class", "xspDumpTDNoValue", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("colspan", "2", null); // $NON-NLS-1$
        }

        String s = HtmlUtil.toHTMLContentString("Array", false, HtmlUtil.useHTML); // $NLS-DumpObjectRenderer_HeaderForArrayArea-1$
        if(StringUtil.isNotEmpty(s)) {
            w.write(s);
        } else {
            w.write("&nbsp;"); // $NON-NLS-1$
        }
        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
        
        newLine(w);
        dump.indentLevel++;
        int index=0;
        for(Iterator<Object> it=a.arrayIterator(); it.hasNext(); ) {
        	Object o = it.next();
            dumpObject(w, dump, "["+index+"]", o, clientId);
            index++;
        }
        dump.indentLevel--;
    }
    private void dumpArrayAsString(ResponseWriter w, Context dump, String name, DumpAccessor.Array a, String clientId) throws IOException {
        // Should we layout an array in rows as well?
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(Iterator<Object> it=a.arrayIterator(); it.hasNext(); ) {
            Object o = it.next();
            DumpAccessor a2 = DumpAccessor.find(dump,o);
            if(b.length()>1) {
                b.append(',');
            }
            if(a2 instanceof DumpAccessor.Value) {
                b.append(((DumpAccessor.Value)a2).getValueAsString());
            } else {
                b.append("<object>"); // $NON-NLS-1$
            }
        }
        b.append(']');
        dumpValue(w, dump, name, b.toString(), a.getTypeAsString(), clientId);
    }
   
    private void dumpMap(ResponseWriter w, Context dump, String name, DumpAccessor.Map a, String clientId) throws IOException {
        String s = HtmlUtil.toHTMLContentString(name, false, HtmlUtil.useHTML);
        if(s!=null) {
            w.startElement("tr", null); // $NON-NLS-1$
            w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
            
            dump.mapId++;
            w.startElement("td", null); // $NON-NLS-1$
        	w.writeAttribute("id", clientId + "_" + MAP_ID_PREFIX + dump.mapId, null); // $NON-NLS-1$ $NON-NLS-2$
        	w.writeAttribute("axis", "map", null); // $NON-NLS-1$ $NON-NLS-2$
            if (dump.catId != -1) {
            	w.writeAttribute("headers", clientId + "_" + CATEGORY_ID_PREFIX + dump.catId, null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.writeAttribute("class", "xspDumpTDName", null); // $NON-NLS-1$ $NON-NLS-2$
            String title = a.getTypeAsString();
            if(StringUtil.isNotEmpty(title)) {
                w.writeAttribute("title", title, null); // $NON-NLS-1$
            }
            writeIndent(w,dump.indentLevel);
            w.write(s);
            w.endElement("td"); // $NON-NLS-1$
            w.startElement("td", null); // $NON-NLS-1$
            if (dump.catId != -1) {
            	w.writeAttribute("headers", clientId + "_" + CATEGORY_ID_PREFIX + dump.catId + " " + clientId + "_" + MAP_ID_PREFIX + dump.mapId, null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            }
            else {
            	w.writeAttribute("headers", clientId + "_" + MAP_ID_PREFIX + dump.mapId, null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.writeAttribute("class", "xspDumpTDValue", null); // $NON-NLS-1$ $NON-NLS-2$
            String sl = HtmlUtil.toHTMLContentString(a.getStringLabel(), false, HtmlUtil.useHTML);
            if(StringUtil.isNotEmpty(sl)) {
                w.write(sl);
            } else {
                JSUtil.writeTextBlank(w);
            }
            w.endElement("td"); // $NON-NLS-1$
            
            w.endElement("tr"); // $NON-NLS-1$
            newLine(w);
        }

        // If we already reached the number of levels just forget about it
        if(dump.level<dump.maxLevel) {
            String[] categories = a.getCategories();
            if(categories==null || categories.length==0) {
                categories = new String[1];
            }
            int catId = 0;
            for(int i=0; i<categories.length; i++) {
                String cat = categories[i];
                boolean showCat = categories.length>1 || StringUtil.isNotEmpty(cat); 
                boolean catHandled = false;
                for( Iterator<Object> it=a.getPropertyKeys(cat); it.hasNext(); ) {
                    Object o = it.next();
                    String key = o.toString();
                    if(dump.level==0 && StringUtil.isNotEmpty(dump.filter)) {
                        if(!key.startsWith(dump.filter)) {
                            continue;
                        }
                    }
                    //SPR #PHAN9E4CCA - ClassCastException in dumpObject with non-String-key'd SortedMaps
                    //Formerly we passed the String value, key: 'a.getProperty(key);'. Changed to pass the Object, o
                    Object value = a.getProperty(o);
                    if(!a.shouldDisplay(key, value)) {
                    	continue;
                    }
                    if(!catHandled) {
                    	catHandled = true;
                        if(showCat) {
                        	catId++;
                        	dump.catId = catId;
                            w.startElement("tr", null); // $NON-NLS-1$
                            w.startElement("th", null); // $NON-NLS-1$
                            if(dump.indentLevel>0) {
                                w.writeAttribute("class", "xspDumpTDCat2", null); // $NON-NLS-1$ $NON-NLS-2$
                            } else {
                                w.writeAttribute("class", "xspDumpTDCat1", null); // $NON-NLS-1$ $NON-NLS-2$
                            }
                            w.writeAttribute("id", clientId + "_" + CATEGORY_ID_PREFIX + catId, null); // $NON-NLS-1$ $NON-NLS-2$
                            w.writeAttribute("axis", "category", null); // $NON-NLS-1$ $NON-NLS-2$
                            w.writeAttribute("colspan", "2", null); // $NON-NLS-1$
                            dump.indentLevel++;
                            writeIndent(w,dump.indentLevel);
                            if(StringUtil.isNotEmpty(cat)) {
                                w.writeText(cat,null);
                            } else {
                            	JSUtil.writeTextBlank(w);
                            }
                            w.endElement("th"); // $NON-NLS-1$
                            w.endElement("tr"); // $NON-NLS-1$
                            newLine(w);
                        }
                        else {
                        	dump.catId = -1;
                        }
                    }
                    dump.indentLevel++; dump.level++;
                    try {
                        dumpObject(w,dump,key,value,clientId);
                    } finally {
                        dump.indentLevel--; dump.level--;
                    }
                }
                if(showCat) {
                    dump.indentLevel--;
                }
                dump.catId = -1;
            }
        }
    }

    private void dumpGrid(ResponseWriter w, Context dump, String name, DumpAccessor.Grid g, String clientId) throws IOException {
        String[] cols = g.getColumns();
        if(cols==null || cols.length==0) {
            return;
        }
        w.startElement("tr", null); // $NON-NLS-1$
        //w.writeAttribute("class", "xspDumpTR", null);
        w.startElement("td", null); // $NON-NLS-1$
        w.writeAttribute("style", "padding: 0px;", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("colspan", "2", null); // $NON-NLS-1$
        newLine(w);

        w.startElement("table", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpGridTable", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        w.startElement("tr", null); // $NON-NLS-1$
        w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
        for(int i=0; i<cols.length; i++) {
            w.startElement("td", null); // $NON-NLS-1$
            w.writeAttribute("class", "xspDumpTDGridHeader", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeText(cols[i],null);
            w.endElement("td"); // $NON-NLS-1$
        }
        w.endElement("tr"); // $NON-NLS-1$
        newLine(w);

        int max = dump.maxGridRows>0 ? dump.maxGridRows : 100;
        for( Iterator<Object> it = g.objectIterator(0, max); it.hasNext(); ) {
            Object o = it.next();
            w.startElement("tr", null); // $NON-NLS-1$
            w.writeAttribute("class", "xspDumpTR", null); // $NON-NLS-1$ $NON-NLS-2$
            for(int i=0; i<cols.length; i++) {
                w.startElement("td", null); // $NON-NLS-1$
                w.writeAttribute("class", "xspDumpTDGridValue", null); // $NON-NLS-1$ $NON-NLS-2$
                Object val = g.getValue(o, i);
                String s = val!=null ? val.toString() : null;
                if(StringUtil.isNotEmpty(s)) {
                    w.writeText(s,null);
                } else {
                    JSUtil.writeTextBlank(w);
                }
                w.endElement("td"); // $NON-NLS-1$
            }
            w.endElement("tr"); // $NON-NLS-1$
            newLine(w);
        }
        
        
        w.endElement("table"); // $NON-NLS-1$
        newLine(w);

        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
        newLine(w);
    }

    private void writeIndent(ResponseWriter w, int indent) throws IOException {
        for(int i=0; i<indent; i++) {
            JSUtil.writeTextBlank(w); // $NON-NLS-1$
            JSUtil.writeTextBlank(w); // $NON-NLS-1$
            JSUtil.writeTextBlank(w); // $NON-NLS-1$
            JSUtil.writeTextBlank(w); // $NON-NLS-1$
        }
    }
}