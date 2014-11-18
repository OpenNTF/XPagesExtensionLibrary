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

package com.ibm.xsp.extlib.component.dynamicview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.SystemCache;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;

public class ViewDesign {

    // ====================================================================
    //  View Design Elements
    // ====================================================================
    public static interface ViewDef {
        public boolean isCategorized();
        public boolean isHierarchical();
        public Iterator<ColumnDef> iterateColumns();
    }   
    public static interface ColumnDef {
        public String getName();
        public String getTitle();
        public int getWidth();
        public boolean isHidden();
        public boolean isLink();
        public boolean isOnClick();
        public boolean isIndentResponses();
        public boolean isCategorized();
        public boolean isSorted();
        public boolean isResortAscending();
        public boolean isResortDescending();
        public boolean isCheckbox();
        public int getAlignment();
        public int getHeaderAlignment();
        public boolean isResponse();
        public boolean isIcon();
        
        public int getNumberFmt();
        public int getNumberDigits();
        public int getNumberAttrib();
        public boolean isNumberAttribParens();
        public boolean isNumberAttribPercent();
        public boolean isNumberAttribPunctuated();
        
        public int getTimeDateFmt();
        public int getDateFmt();
        public int getTimeFmt();
        public int getTimeZoneFmt();

        public int getListSep();
    }
    public static interface ViewFactory {
        public ViewDef getViewDef(View view);
    }
    
    // ====================================================================
    //  Default implementation
    // ====================================================================
    
    public static ViewFactory getDefaultFactory(FacesContext context) {
        // The factory is stored in the application scope
        Map<String,Object> appScope = ExtLibUtil.getApplicationScope(context);
        ViewFactory f = (ViewFactory)appScope.get("extlib.viewdesign"); // $NON-NLS-1$
        if(f==null) {
            synchronized (ViewDesign.class) {
                f = (ViewFactory)appScope.get("extlib.viewdesign"); // $NON-NLS-1$
                if(f==null) {
                    f = new DefaultViewFactory();
                    appScope.put("extlib.viewdesign",f); // $NON-NLS-1$
                }
            }
        }
        return f;
    }
    
    public static class DefaultViewDef implements ViewDef {
        public static final int FLAG_CATEGORIZED    = 0x0001;
        public static final int FLAG_HIERARCHICAL   = 0x0002;
        
        public int flags;
        
        public List<ColumnDef> columns = new ArrayList<ColumnDef>();
        public Iterator<ColumnDef> iterateColumns() {
            return columns.iterator();
        }
        public boolean isCategorized() {
            return (flags&FLAG_CATEGORIZED)!=0;
        }
        public boolean isHierarchical() {
            return (flags&FLAG_HIERARCHICAL)!=0;
        }
    }   
    public static class DefaultColumnDef implements ColumnDef {
        public static final int FLAG_HIDDEN         = 0x000001;
        public static final int FLAG_LINK           = 0x000002;
        public static final int FLAG_ONCLICK        = 0x000004;
        public static final int FLAG_INDENTRESP     = 0x000008;
        public static final int FLAG_CATEGORIZED    = 0x000010;
        public static final int FLAG_SORTED         = 0x000020;
        public static final int FLAG_RESORTASC      = 0x000040;
        public static final int FLAG_RESORTDESC     = 0x000080;
        public static final int FLAG_ALIGNCENTER    = 0x000100;
        public static final int FLAG_ALIGNRIGHT     = 0x000200;
        public static final int FLAG_HALIGNCENTER   = 0x000400;
        public static final int FLAG_HALIGNRIGHT    = 0x000800;
        public static final int FLAG_CHECKBOX       = 0x001000;
        public static final int FLAG_ICON           = 0x002000;
        public static final int FLAG_RESPONSE       = 0x004000;
        public static final int FLAG_ATTRIBPARENS   = 0x010000;
        public static final int FLAG_ATTRIBPERCENT  = 0x020000;
        public static final int FLAG_ATTRIBPUNC     = 0x040000;
        
        public String name;
        public String title;
        public int width;
        public int flags;
        
        public int numberFmt;
        public int numberAttrib;
        public int numberDigits;
        public int timeDateFmt;
        public int dateFmt;
        public int timeFmt;
        public int timeZoneFmt;
        
        public int listSep;

        public String getName() {
            return name;
        }
        public String getTitle() {
            return title;
        }
        public int getWidth() {
            return width;
        }
        public boolean isHidden() {
            return (flags&FLAG_HIDDEN)!=0;
        }
        public boolean isLink() {
            return (flags&FLAG_LINK)!=0;
        }
        public boolean isOnClick() {
            return (flags&FLAG_ONCLICK)!=0;
        }
        public boolean isIndentResponses() {
            return (flags&FLAG_INDENTRESP)!=0;
        }
        public boolean isCategorized() {
            return (flags&FLAG_CATEGORIZED)!=0;
        }
        public boolean isSorted() {
            return (flags&FLAG_SORTED)!=0;
        }
        public boolean isResortAscending() {
            return (flags&FLAG_RESORTASC)!=0;
        }
        public boolean isResortDescending() {
            return (flags&FLAG_RESORTDESC)!=0;
        }
        public int getAlignment() {
            if((flags&FLAG_ALIGNCENTER)!=0) {
                return ViewColumn.ALIGN_CENTER;
            }
            if((flags&FLAG_ALIGNRIGHT)!=0) {
                return ViewColumn.ALIGN_RIGHT;
            }
            return ViewColumn.ALIGN_LEFT;
        }
        public int getHeaderAlignment() {
            if((flags&FLAG_HALIGNCENTER)!=0) {
                return ViewColumn.ALIGN_CENTER;
            }
            if((flags&FLAG_HALIGNRIGHT)!=0) {
                return ViewColumn.ALIGN_RIGHT;
            }
            return ViewColumn.ALIGN_LEFT;
        }
        public boolean isCheckbox() {
            return (flags&FLAG_CHECKBOX)!=0;
        }
        public boolean isIcon() {
            return (flags&FLAG_ICON)!=0;
        }
        public boolean isResponse() {
            return (flags&FLAG_RESPONSE)!=0;
        }
        public int getNumberFmt() {
            return numberFmt;
        }
        public int getNumberAttrib() {
            return numberAttrib;
        }
        public int getNumberDigits() {
            return numberDigits;
        }
        public boolean isNumberAttribParens() {
            return (flags&FLAG_ATTRIBPARENS)!=0;
        }
        public boolean isNumberAttribPercent() {
            return (flags&FLAG_ATTRIBPERCENT)!=0;
        }
        public boolean isNumberAttribPunctuated() {
            return (flags&FLAG_ATTRIBPUNC)!=0;
        }
        public int getTimeDateFmt() {
            return timeDateFmt;
        }
        public int getDateFmt() {
            return dateFmt;
        }
        public int getTimeFmt() {
            return timeFmt;
        }
        public int getTimeZoneFmt() {
            return timeZoneFmt;
        }
        public int getListSep() {
            return listSep;
        }
    }
    public static class DefaultViewFactory implements ViewFactory, Serializable {
        private static final long serialVersionUID = 1L;
        
        private SystemCache views = new SystemCache("View Definition",16,"xsp.extlib.viewdefsize"); // $NON-NLS-1$ $NON-NLS-2$
        
        public DefaultViewFactory() {
        }
        
        public ViewDef getViewDef(View view) {
            if(view==null) {
                return null;
            }
            try {
                String viewKey = getViewKey(view);
                DefaultViewDef viewDef = (DefaultViewDef)views.get(viewKey);
                if(viewDef==null) {
                    boolean hasLink = false;
                    
                    // Read the view
                    viewDef = new DefaultViewDef();
                    if(view.isHierarchical()) viewDef.flags |= DefaultViewDef.FLAG_HIERARCHICAL;
                    if(view.isCategorized()) viewDef.flags |= DefaultViewDef.FLAG_CATEGORIZED;
                    
                    // Read the columns
                    Vector<ViewColumn> vcols = view.getColumnCount() >0 ? view.getColumns() : null;
                    if(vcols!=null) {
                        for(int i=0; i<vcols.size(); i++) {
                            ViewColumn vc = vcols.get(i);
                            DefaultColumnDef colDef = new DefaultColumnDef();
                            colDef.name = vc.getItemName();
                            colDef.title = vc.getTitle();
                            colDef.width = vc.getWidth();
                            if(vc.isResponse()) colDef.flags |= DefaultColumnDef.FLAG_RESPONSE;
                            if(vc.isHidden()) colDef.flags |= DefaultColumnDef.FLAG_HIDDEN;
                            if(vc.isCategory()) colDef.flags |= DefaultColumnDef.FLAG_CATEGORIZED;
                            if(vc.isSorted()) colDef.flags |= DefaultColumnDef.FLAG_SORTED;
                            if(vc.isResortAscending()) colDef.flags |= DefaultColumnDef.FLAG_RESORTASC;
                            if(vc.isResortDescending()) colDef.flags |= DefaultColumnDef.FLAG_RESORTDESC;
                            switch(vc.getAlignment()) {
                                case ViewColumn.ALIGN_CENTER: colDef.flags |= DefaultColumnDef.FLAG_ALIGNCENTER;
                                case ViewColumn.ALIGN_RIGHT:  colDef.flags |= DefaultColumnDef.FLAG_ALIGNRIGHT;
                            }
                            switch(vc.getHeaderAlignment()) {
                                case ViewColumn.ALIGN_CENTER: colDef.flags |= DefaultColumnDef.FLAG_HALIGNCENTER;
                                case ViewColumn.ALIGN_RIGHT:  colDef.flags |= DefaultColumnDef.FLAG_HALIGNRIGHT;
                            }
                            if(!colDef.isHidden()) {
                                // The first column that is not a formula displays as a link
                                if(!hasLink) {
                                    if(vc.getColumnValuesIndex()!=DominoViewEntry.VC_NOT_PRESENT && !vc.isCategory() && !vc.isIcon()) {
                                        colDef.flags |=  DefaultColumnDef.FLAG_LINK
                                                        |DefaultColumnDef.FLAG_ONCLICK
                                                        |DefaultColumnDef.FLAG_CHECKBOX;
                                        hasLink = true;
                                    }
                                }
                            }
                            // Find the display formats
                            colDef.numberFmt = vc.getNumberFormat();
                            colDef.numberDigits = vc.getNumberDigits();
                            colDef.numberAttrib = vc.getNumberAttrib();
                            if(vc.isNumberAttribParens()) colDef.flags |= DefaultColumnDef.FLAG_ATTRIBPARENS;
                            if(vc.isNumberAttribPercent()) colDef.flags |= DefaultColumnDef.FLAG_ATTRIBPERCENT;
                            if(vc.isNumberAttribPunctuated()) colDef.flags |= DefaultColumnDef.FLAG_ATTRIBPUNC;
                            colDef.timeDateFmt = vc.getTimeDateFmt();
                            colDef.dateFmt = vc.getDateFmt();
                            colDef.timeFmt = vc.getTimeFmt();
                            colDef.timeZoneFmt = vc.getTimeZoneFmt();
                            colDef.listSep = vc.getListSep();
    //System.out.println("Col: "+colDef.getName());                     
    //System.out.println("    numberFmt:"+colDef.numberFmt+" [attrib: "+colDef.numberAttrib+", digits:"+colDef.numberDigits+"]");                       
    //System.out.println("    timeDateFmt:"+colDef.timeDateFmt+", dateFmt:"+colDef.dateFmt+", timeFmt:"+colDef.timeFmt+", timeZoneFmt:"+colDef.timeZoneFmt);                        
    //System.out.println("    colSep:"+colDef.listSep);                     
                            
                            // Defines if the twisty should be shown
                            if(vc.isShowTwistie()) {
                                colDef.flags |= DefaultColumnDef.FLAG_INDENTRESP;
                            }
                            if(vc.isIcon()) colDef.flags |= DefaultColumnDef.FLAG_ICON;
                            viewDef.columns.add(colDef);
                        }
                    }
                }
                return viewDef;
            } catch(NotesException ex) {
                throw new FacesExceptionEx(ex,"Error while accessing view {0}",view.toString()); // $NLX-ViewDesign.Errorwhileaccessingview0-1$
            }
        }
    }
    
    
    // ====================================================================
    //  Utilities
    // ====================================================================
    
    public static String getViewKey(String databaseName, String viewName) throws NotesException {
        StringBuilder b = new StringBuilder();
        if(StringUtil.isNotEmpty(databaseName)) {
            b.append(databaseName);
        }
        b.append('!');
        b.append(viewName);
        return b.toString();
    }

    public static String getViewKey(View view) throws NotesException {
        if(view!=null) {
            return getViewKey(view.getParent().getFilePath(),view.getName());
        }
        return null;
    }   

    public static View loadView(String viewKey) throws NotesException {
        if(StringUtil.isNotEmpty(viewKey)) {
            int sep = viewKey.lastIndexOf('!'); // Can be part of server!!database
            String dbName = viewKey.substring(0,sep);
            String viewName = viewKey.substring(sep+1);
            Database db = DominoUtils.openDatabaseByName(dbName);
            View view = db.getView(viewName);
            return view;
        }
        return null;
    }   
}