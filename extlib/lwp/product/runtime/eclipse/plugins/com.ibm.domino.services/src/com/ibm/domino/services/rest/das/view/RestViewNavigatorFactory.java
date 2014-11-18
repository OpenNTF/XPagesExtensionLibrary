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

package com.ibm.domino.services.rest.das.view;

import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FORM;
import static com.ibm.domino.services.rest.RestServiceConstants.SORT_ORDER_DESCENDING;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.ViewNavigator;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.ResponseCode;
import com.ibm.domino.services.ServiceException;
import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;


/**
 * Domino View Service.
 */
public class RestViewNavigatorFactory {

    /**
     * Factory that creates a RestViewNavigator based on the view parameters.
     */
    public static RestViewNavigator createNavigator(View view, ViewParameters parameters) throws ServiceException {
        // NOI navigators
        try {
            String ftSearch = parameters.getSearch();
            if(StringUtil.isNotEmpty(ftSearch)) {
                return new FullTextNavigator(view,parameters);
            }
            
            // Key filtered navigator
            Object keys = parameters.getKeys();
            if(keys!=null) {
                return new SearchKeyNavigator(view,parameters);
            }
            
            // On the parent id
            String parentId = parameters.getParentId();
            if(parentId!=null) {
                return new ParentNavigator(view,parameters);
            }
            
            // On a category
            String cat = parameters.getCategoryFilter();
            if(cat!=null) {
                return new CategoryNavigator(view,parameters);
            }
            
            // On a startkey
            Object startKeys = parameters.getStartKeys();
            if(startKeys != null) {
                return new StartKeyNavigator(view,parameters);
            }
            
            // Regular navigator...
            return new GenericNavigator(view,parameters);
        } catch(NotesException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }

    public static RestViewNavigator createNavigatorForDesign(View view, ViewParameters parameters) throws ServiceException {
        // NOI navigators
        try {
            return new NOINavigatorForDesign(view,parameters);
        } catch(NotesException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }
    
    
    /**
     * Abstract Backend classes navigator.
     */
    public static abstract class NOINavigator extends RestViewNavigator {
        View view;
        Vector<ViewColumn> columns;
        ViewEntry entry;
        Vector<Object> columnValues;
        protected NOINavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
            this.view = view;
            this.columns = (Vector<ViewColumn>)view.getColumns();
        }
        protected void resortView() {
            // resort the view if sort criteria are specified
            String sortColumn = getParameters().getSortColumn();
            if (StringUtil.isNotEmpty(sortColumn)) {
                try {
                    String sortOrder = getParameters().getSortOrder();
                    boolean ascending = !StringUtil.equals(sortOrder, SORT_ORDER_DESCENDING);
                    view.resortView(sortColumn, ascending);
                } catch(NotesException ex) {
                    // Swallow the error if sort doesn't work
                }
            }
        }
        // Global methods
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            try {
                return view.getEntryCount();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        
        // Set the current entry
        public boolean initEntry(ViewEntry e) throws NotesException {
            if(entry!=null) {
                entry.recycle();
            }
            columnValues = null;
            entry = e;
            if(e!=null) {
                e.setPreferJavaDates(true);
                return true;
            }
            return false;
        }
        // System columns
        @Override
        public String getUniversalId() throws ServiceException {
            try {
                return entry.getUniversalID();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public String getNoteId() throws ServiceException {
            try {
                return entry.getNoteID();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public String getPosition() throws ServiceException {
            try {
                return entry.getPosition('.');
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public boolean getRead() throws ServiceException {
            try {
                return entry.getRead();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public int getSiblings() throws ServiceException {
            try {
                return entry.getSiblingCount();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public int getDescendants() throws ServiceException {
            try {
                return entry.getDescendantCount();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public int getChildren() throws ServiceException {
            try {
                return entry.getChildCount();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public int getIndent() throws ServiceException {
            try {
                return entry.getIndentLevel();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public boolean isDocument() throws ServiceException {
            try {
                return entry.isDocument();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public boolean isCategory() throws ServiceException {
            try {
                return entry.isCategory();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public boolean isResponse() throws ServiceException {
            try{
                Object op = entry.getParent();
                if(null != op && op instanceof ViewEntry){
                    ViewEntry parent = (ViewEntry)op;
                    return parent.isDocument();
                }
                return false;
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        // Column values
        @Override
        public int getColumnCount() throws ServiceException {
            return columns.size();
        }
        @Override
        public String getColumnName(int index) throws ServiceException {
            try {
                ViewColumn c = columns.get(index);
                return c.getItemName();
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public Object getColumnValue(int index) throws ServiceException {
            try {
                if(index>=0) {
                    if(columnValues==null) {
                        columnValues = entry.getColumnValues();
                    }
                    ViewColumn c = columns.get(index);
                    int idx = c.getColumnValuesIndex();
                    if(idx!=DominoViewEntry.VC_NOT_PRESENT) {
                        //Looks like the vector is smaller when the entry is a category                     
                        if(idx<columnValues.size()) {
                            return columnValues.get(idx);
                        }
                    }
                }
                return null; // No values...
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public Object getColumnValue(String name) throws ServiceException {
            return getColumnValue(findColumnPosition(name));
        }
        
        // Added by Tim Tripcony
        // Hoping to add support for Form Formulas later on
        @Override
        public String getForm() throws ServiceException {
            String result = "";
            if (isDocument()) {
                try {
                    Document document = entry.getDocument();
                    if (document != null) {
                        result = document.getItemValueString(ITEM_FORM);
                    }
                } catch (NotesException ex) {
                    throw new ServiceException(ex,""); // $NON-NLS-1$
                }
            }
            return result;
        }
        
        public int findColumnPosition(String name) throws ServiceException {
            try {
                int count = columns.size();
    
                // Look for a programmatic name first
                for(int i=0; i<count; i++) {
                    ViewColumn c = columns.get(i);
                    String s = c.getItemName();
                    if(StringUtil.equalsIgnoreCase(s, name)) {
                        return i;
                    }
                }
    
                // Then default to the title
                for(int i=0; i<count; i++) {
                    ViewColumn c = columns.get(i);
                    String s = c.getTitle();
                    if(StringUtil.equalsIgnoreCase(s, name)) {
                        return i;
                    }
                }
                
                // Nothing found!
                return Integer.MIN_VALUE;
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public String getItemName(String columnName) throws ServiceException {
            try {
                int pos = findColumnPosition(columnName);
                if(pos>=0) {
                    ViewColumn col = columns.get(pos);
                    if(!col.isFormula()) {
                        // Hope that the item name is correct here...
                        return col.getItemName();
                    }
                }
                return null;
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        
        @Override
        public ViewEntry getViewEntry() {
        	return entry;
        }
    }
    public static class NOINavigatorForDesign extends NOINavigator {
        protected NOINavigatorForDesign(View view, ViewParameters parameters) throws NotesException {
            super(view, parameters);
        }
        // This method is valid for this navigator
        //public abstract String getItemName(String columnName) throws ServiceException;

        @Override
        public int getTopLevelEntryCount() throws ServiceException {throw new IllegalStateException();}
        @Override
        public boolean first(int start, int count) throws ServiceException {throw new IllegalStateException();}
        @Override
        public boolean next() throws ServiceException {throw new IllegalStateException();}
        @Override
        public String getUniversalId() throws ServiceException {throw new IllegalStateException();}
        @Override
        public String getNoteId() throws ServiceException {throw new IllegalStateException();}
        @Override
        public String getPosition() throws ServiceException {throw new IllegalStateException();}
        @Override
        public boolean getRead() throws ServiceException {throw new IllegalStateException();}
        @Override
        public int getSiblings() throws ServiceException {throw new IllegalStateException();}
        @Override
        public int getDescendants() throws ServiceException {throw new IllegalStateException();}
        @Override
        public int getChildren() throws ServiceException {throw new IllegalStateException();}
        @Override
        public int getIndent() throws ServiceException {throw new IllegalStateException();}
        @Override
        public int getColumnCount() throws ServiceException {throw new IllegalStateException();}
        @Override
        public String getColumnName(int index) throws ServiceException {throw new IllegalStateException();}
        @Override
        public Object getColumnValue(int index) throws ServiceException {throw new IllegalStateException();}
        @Override
        public Object getColumnValue(String name) throws ServiceException {throw new IllegalStateException();}
        @Override
        public boolean isDocument() throws ServiceException {throw new IllegalStateException();}
    }

    
    /**
     * Abstract Backend classes navigator based on a ViewEntryCollection.
     */
    public static abstract class ViewCollectionNavigator extends NOINavigator {
        ViewEntryCollection collection;
        protected ViewCollectionNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
        }
        @Override
        public void recycle() {
            try {
                if(collection != null) {
                    collection.recycle();
                    collection = null;
                }
            } catch(NotesException ex) {
            	if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
            		Loggers.SERVICES_LOGGER.info("Exception thrown on recycle.", ex);   // $NON-NLS-1$
            	}
            }
            super.recycle();
        }
        boolean bFirst = false;
        @Override
        public boolean first(int start, int count) throws ServiceException {
            if (bFirst)
                return bFirst;
            try {
                collection = createCollection();
                if(!initEntry(collection.getFirstEntry())) {
                    return false;
                }
                if(start>0) {
                    for(int i=0; i<start; i++) {
                        if(!initEntry(collection.getNextEntry())) {
                            return false;
                        }
                    }
                }
                bFirst = true;
                return true;
            } catch(NotesException ex) {
                throw new ServiceException(ex, ResponseCode.BAD_REQUEST, ex.getLocalizedMessage());
            }
        }
        @Override
        public boolean next() throws ServiceException {
            try {
                if(!initEntry(collection.getNextEntry())) {
                    return false;
                }
                return true;
            } catch(NotesException ex) {
                throw new ServiceException(ex, ResponseCode.BAD_REQUEST, ex.getLocalizedMessage());
            }
        }
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            if (collection != null) {
                try {
                    return collection.getCount();
                } catch(NotesException ex) {
                    throw new ServiceException(ex, ResponseCode.BAD_REQUEST, ex.getLocalizedMessage());
                }
            } else {
                return super.getTopLevelEntryCount();
            }
        }
        protected abstract ViewEntryCollection createCollection() throws NotesException, ServiceException;
    }
    
    /**
     * Backend classes navigator filtering on a FT search.
     */
    public static class FullTextNavigator extends ViewCollectionNavigator {
        public FullTextNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
        }
        @Override
        protected ViewEntryCollection createCollection() throws NotesException, ServiceException {
            
            Database db = view.getParent();
            if (!db.isFTIndexed()) {
                throw new ServiceException(null, "Database is not full text indexed."); // $NON-NLS-1$
            }
            String search = getParameters().getSearch();
            int maxDocs = getParameters().getSearchMaxDocs();
            String sortColumn = getParameters().getSortColumn();
            // if 'sortColumn' is specified then use FTSearchSorted
            // FTSearchSorted call will fail if the DB is not full text indexed
            if (StringUtil.isNotEmpty(sortColumn)) {
                try {
                    String sortOrder = getParameters().getSortOrder();
                    boolean ascending = !StringUtil.equals(sortOrder, SORT_ORDER_DESCENDING);
                    if (maxDocs < 0)
                    	maxDocs = 0;
                    boolean exact = getParameters().isKeysExactMatch();
                    boolean variants = false, fuzzy = false;
                    view.FTSearchSorted(search, maxDocs, sortColumn, ascending, exact, variants, fuzzy);
                    
                } catch(NotesException ex) {
	                throw new ServiceException(ex, ResponseCode.BAD_REQUEST, ex.getLocalizedMessage());
                }
                return view.getAllEntries();                
            }            
            if(maxDocs<=0) {
                view.FTSearch(search);
            } else {
                view.FTSearch(search,maxDocs);
            }
            return view.getAllEntries();
        }
    }

    /**
     * Backend classes navigator filtering on a search key.
     */
    public static class SearchKeyNavigator extends ViewCollectionNavigator {
        protected SearchKeyNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
            resortView();
        }
        @Override
        protected ViewEntryCollection createCollection() throws NotesException {
            boolean exact = getParameters().isKeysExactMatch();
            Object keys = getParameters().getKeys();
            if(keys instanceof Vector<?>) {
                Vector v = (Vector)keys;
                for (int i = 0; i < v.size(); i++) {
                    if (v.get(i) instanceof java.util.Date) {
                        DateTime notesDate = view.getParent().getParent().createDateTime((java.util.Date)v.get(i));
                        v.remove(i);
                        v.add(i, notesDate);
                    }
                }
                return view.getAllEntriesByKey((Vector<?>)keys,exact);
            } else if (keys instanceof java.util.Date) {
                DateTime notesDate = view.getParent().getParent().createDateTime((java.util.Date)keys);
                return view.getAllEntriesByKey(notesDate,exact);
            } else {
                return view.getAllEntriesByKey(keys,exact);
            }
        }
    }

    /**
     * Abstract Backend classes navigator based on the ViewNavigator.
     */
    public static abstract class ViewNavigatorNavigator extends NOINavigator {
        ViewNavigator navigator;
        protected ViewNavigatorNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
        }
        @Override
        public void recycle() {
            if(navigator!=null) {
                try {
                    navigator.recycle();
                    navigator = null;
                } catch(NotesException ex) {}
            }
            super.recycle();
        }
        boolean bFirst = false;
        @Override
        public boolean first(int start, int count) throws ServiceException {
            if (bFirst)
                return bFirst;
            try {
                navigator = createNavigator();
                int maxLevels = getParameters().getExpandLevel();
                if(maxLevels!=Integer.MAX_VALUE) {
                    navigator.setMaxLevel(maxLevels);
                }
                if(!navigator.gotoFirst()) {
                    return false;
                }
                navigator.setBufferMaxEntries(count);
                if(start>0) {
                    int skipped = navigator.skip(start);
                    if(skipped<start) {
                        return false;
                    }
                }
                bFirst = initEntry(navigator.getCurrent());
                return bFirst;
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }
        @Override
        public boolean next() throws ServiceException {
            try {
                return initEntry(navigator.getNext());
            } catch(NotesException ex) {
                throw new ServiceException(ex,""); // $NON-NLS-1$
            }
        }       
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            if (view!=null) {
                try {                   
                    if(view.isCategorized()) {
                        return navigator.getCount();
                    }
                    else {
                        return view.getEntryCount();    
                    }
                } catch(NotesException ex) {
                    throw new ServiceException(ex,""); // $NON-NLS-1$
                }
            } else {
                return super.getTopLevelEntryCount();
            }
        }
        protected abstract ViewNavigator createNavigator() throws NotesException;
    }

    /**
     * Backend classes generic navigator.
     */
    public static class GenericNavigator extends ViewNavigatorNavigator {
        public GenericNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
            resortView();
        }
        @Override
        protected ViewNavigator createNavigator() throws NotesException {
            return view.createViewNav();
        }
    }

    /**
     * Backend classes navigator starting from a particular key.
     */
    public static class StartKeyNavigator extends ViewNavigatorNavigator {
        public StartKeyNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
            resortView();
        }
        @Override
        protected ViewNavigator createNavigator() throws NotesException {
            Object startKeys = getParameters().getStartKeys();
            ViewEntry ve = view.getEntryByKey(startKeys);
            if(ve!=null) {
                return view.createViewNavFrom(ve);
            }
            return view.createViewNav();
        }
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            if (navigator!=null) {              
                return navigator.getCount();
            } else {
                return super.getTopLevelEntryCount();
            }
        }
    }

    /**
     * Backend classes navigator filtering on a category.
     */
    public static class CategoryNavigator extends ViewNavigatorNavigator {
        public CategoryNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
        }
        @Override
        protected ViewNavigator createNavigator() throws NotesException {
            String cat = getParameters().getCategoryFilter();
            return view.createViewNavFromCategory(cat);
        }
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            if (navigator!=null) {              
                return navigator.getCount();
            } else {
                return super.getTopLevelEntryCount();
            }
        }
    }
    
    /**
     * Backend classes navigator filtering on a parent id.
     */
    public static class ParentNavigator extends ViewNavigatorNavigator {
        public ParentNavigator(View view, ViewParameters parameters) throws NotesException {
            super(view,parameters);
        }
        @Override
        protected ViewNavigator createNavigator() throws NotesException {
            String parentId = getParameters().getParentId();
            Document doc = null;
            
            if (parentId.length() == 32)                
                doc = view.getParent().getDocumentByUNID(parentId);
            else
                doc = view.getParent().getDocumentByID(parentId);
            
            return view.createViewNavFromDescendants(doc);
        }
        @Override
        public int getTopLevelEntryCount() throws ServiceException {
            if (navigator!=null) {              
                return navigator.getCount();
            } else {
                return super.getTopLevelEntryCount();
            }
        }
    }   
}