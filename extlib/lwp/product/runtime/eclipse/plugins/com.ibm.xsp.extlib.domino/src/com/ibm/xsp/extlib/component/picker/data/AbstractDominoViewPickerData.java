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
package com.ibm.xsp.extlib.component.picker.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.ViewNavigator;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;


/**
 * Abstract data provider for a picker that reads the data from a Domino view.
 * <p>
 * This class does not preclude how the 
 * </p>
 */
public abstract class AbstractDominoViewPickerData extends ValueBindingObjectImpl implements IPickerData {

	public static final String SEARCH_STARTFROM	= "startFrom";	// default //$NON-NLS-1$
	public static final String SEARCH_MATCH		= "match"; //$NON-NLS-1$
	public static final String SEARCH_FTSEARCH	= "ftSearch"; //$NON-NLS-1$

    public AbstractDominoViewPickerData() {
    }
    
    protected abstract EntryMetaData createEntryMetaData(IPickerOptions options) throws NotesException;

    public String getSearchType() {
    	return null;
    }

    
    // ====================================================================
    // Data access implementation
    // ====================================================================
    
    public abstract static class EntryMetaData {
        private View view;
        private IPickerOptions options;
        protected EntryMetaData(IPickerOptions options) throws NotesException {
            this.options = options;
            this.view = openView();
        }
        public View getView() {
            return view;
        }
        public IPickerOptions getOptions() {
            return options;
        }
        protected int findSortColumnIndex(Vector<ViewColumn> vc) throws NotesException {
            int fc = -1;
            // Find the first sorted column
            int nc = vc.size();
            for(int i=0; i<nc; i++) {
                ViewColumn c = vc.get(i); 
                if(c.isSorted()) {
                    return i;
                }
                if(fc<0 && c.getColumnValuesIndex()!=DominoViewEntry.VC_NOT_PRESENT) {
                    fc = i;
                }
            }
            // Else, return the first column
            return fc;
        }
        protected int findColumnIndex(Vector<ViewColumn> vc, String name) throws NotesException {
            int nc = vc.size();
            // Look for a programmatic name first
            for(int i=0; i<nc; i++) {
                if(StringUtil.equalsIgnoreCase(vc.get(i).getItemName(),name)) {
                    return i;
                }
            }
            // Then default to the title
            for(int i=0; i<nc; i++) {
                if(StringUtil.equalsIgnoreCase(vc.get(i).getTitle(),name)) {
                    return i;
                }
            }
            return -1;
        }
        
        protected abstract View openView() throws NotesException;
        protected abstract Entry createEntry(ViewEntry ve) throws NotesException;
    }
    public abstract static class Entry implements IPickerEntry {
        private EntryMetaData metaData;
        private Object value;
        private Object label;
        private Object[] attributes;
        @SuppressWarnings("unchecked") // $NON-NLS-1$
        protected Entry(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            this.metaData = metaData;
            // Read the values from the view entry
            Vector<Object> columnValues = ve.getColumnValues();
            
            // Read the value
            this.value = readValue(ve, columnValues);
            
            // Read the label
            this.label = readLabel(ve, columnValues);
            
            // Read the extra attributes
            this.attributes = readAttributes(ve, columnValues);
        }
        public EntryMetaData getMetaData() {
            return metaData;
        }
        public Object getValue() {
            return value;
        }
        public Object getLabel() {
            return label;
        }
        public int getAttributeCount() {
            return 0; 
        }
        public String getAttributeName(int index) {
            return null; 
        }
        public Object getAttributeValue(int index) {
            return attributes[index];
        }
        protected abstract Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException;
        protected abstract Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException;
        protected abstract Object[] readAttributes(ViewEntry ve, Vector<Object> columnValues) throws NotesException;
    }
    public static class Result implements IPickerResult {
        private List<IPickerEntry> entries;
        private int count;
        protected Result(List<IPickerEntry> entries, int count) {
            this.entries = entries;
            this.count = count;
        }
        public List<IPickerEntry> getEntries() {
            return entries;
        }
        public int getTotalCount() {
            return count;
        }
    }

    public boolean hasCapability(int capability) {
        if(capability==CAPABILITY_MULTIPLESOURCES) {
            return false;
        }
        return true;
    }
    
    public IPickerResult readEntries(IPickerOptions options) {
        try {
            EntryMetaData meta = createEntryMetaData(options);
            View view = meta.getView();
            view.setAutoUpdate(false);
            try {
                ArrayList<IPickerEntry> entries = new ArrayList<IPickerEntry>(); 

                int start = options.getStart();
                int count = options.getCount();
                String key = options.getKey();
                String _startKey = options.getStartKey();
                if(StringUtil.isNotEmpty(_startKey)) {
                	key = _startKey;
                }

                String searchType = getSearchType();
                if(StringUtil.isEmpty(searchType)) {
                	searchType = SEARCH_STARTFROM;
                }
                
                if(StringUtil.equals(searchType, SEARCH_MATCH)) {
                    ViewEntryCollection vc;
                    if (!StringUtil.isEmpty(key)) {
                        vc = view.getAllEntriesByKey(key);
                    } else {
                        // key is empty, default to return all, but check option
                        String optionName = "xsp.extlib.dominoViewPicker.searchMatch.emptyKey.all"; //$NON-NLS-1$
                        String optionValue = FacesContextEx.getCurrentInstance().getProperty(optionName);
                        if( "false".equals(optionValue) ){ //$NON-NLS-1$
                            // when explicitly set to "false" then revert to the old pre-901v14 behavior,
                            // match to empty key, returns none. [Change for SPR#MKEE9ZKDJR, and for 
                            // GitPR#14,PartD3: https://github.com/OpenNTF/XPagesExtensionLibrary/pull/14 ]
                            vc = view.getAllEntriesByKey(key);
                        }else{ // default
                            vc = view.getAllEntries();
                        }
                    }
                    ViewEntry ve = start>0 ? vc.getNthEntry(start) : vc.getFirstEntry();
                    for(int i=0; i<count && ve!=null; i++) {
                        entries.add(meta.createEntry(ve));
                        ve = vc.getNextEntry(ve);
                    }
                    int nEntries = vc.getCount();
                    return new Result(entries,nEntries);
                } else if(StringUtil.equals(searchType, SEARCH_FTSEARCH)) {
                	applyFTSearch(options, view, key);
                    ViewEntryCollection vc = view.getAllEntries();
                    ViewEntry ve = start>0 ? vc.getNthEntry(start) : vc.getFirstEntry();
                    for(int i=0; i<count && ve!=null; i++) {
                        entries.add(meta.createEntry(ve));
                        ve = vc.getNextEntry(ve);
                    }
                    int nEntries = vc.getCount();
                    return new Result(entries,nEntries);
                } else {
                    ViewNavigator nav = view.createViewNav();
                    try {
                        ViewEntry ve=null;
                        if(key!=null) {
                            int searchOptions = DominoUtils.FIND_GREATER_THAN|DominoUtils.FIND_EQUAL|DominoUtils.FIND_PARTIAL|DominoUtils.FIND_CASE_INSENSITIVE;
                            ve = DominoUtils.getViewEntryByKeyWithOptions(view, key, searchOptions);
                        } else {
                            ve=nav.getCurrent();
                        }
                        if(start>0) {
                            if(nav.skip(start)!=start) {
                                // ok not all of them are skipped, stop the process
                                count = 0;
                            }
                        }
                        for(int i=0; i<count && ve!=null; i++) {
                            entries.add(meta.createEntry(ve));
                            ve=nav.getNext(ve);
                        }
    
                        int nEntries = -1;
                        return new Result(entries,nEntries);
                    } finally {
                        nav.recycle();
                    }
                }
            } finally {
                // Recycle the view?
            }
        } catch(Exception ex) {
            Platform.getInstance().log(ex);
            // Swallow the exception for the end user and return an empty picker
            return new EmptyPickerResult();
        }
    }
    
    protected void applyFTSearch(IPickerOptions options, View view, String key) throws NotesException {
    	if(StringUtil.isNotEmpty(key)) {
    		view.FTSearch(key);
    	}
    }
        
    public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames) {
        try {
            EntryMetaData meta = createEntryMetaData(null);
            View view = meta.getView();
            view.setAutoUpdate(false);
            try {
                // TODO: use the options here?
                ArrayList<IPickerEntry> entries = new ArrayList<IPickerEntry>(ids.length);
                for(int i=0; i<ids.length; i++) {
                    ViewEntry ve = view.getEntryByKey(ids[i],true);
                    if(ve!=null) {
                        Entry e = meta.createEntry(ve);
                        entries.add(e);
                    } else {
                        entries.add(null);
                    }
                    //ve.recycle();
                }
                return entries;
            } finally {
                // Recycle the view?
            }
        } catch(NotesException ex) {
            throw new FacesExceptionEx(ex,"Error while loading entry"); // $NLX-AbstractDominoViewPickerData.Errorwhileloadingentry-1$
        }
    }   
}