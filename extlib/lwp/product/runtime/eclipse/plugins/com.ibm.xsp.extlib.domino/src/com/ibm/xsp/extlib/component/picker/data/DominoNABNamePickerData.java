/*
 * � Copyright IBM Corp. 2010, 2015
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.acl.NoAccessSignal;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.domino.ExtlibDominoLogger;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.component.UIViewRootEx;



/**
 * Data provider for a name picker using the Domino NAB.
 */
public class DominoNABNamePickerData extends AbstractDominoViewPickerData implements INamePickerData {

    public static final String NAB_ALL          = "all"; // $NON-NLS-1$
    public static final String NAB_ALLPUBLIC    = "all-public"; // $NON-NLS-1$
    public static final String NAB_ALLPRIVATE   = "all-private"; // $NON-NLS-1$
    public static final String NAB_FIRST        = "first"; // $NON-NLS-1$
    public static final String NAB_FIRSTPUBLIC  = "first-public"; // $NON-NLS-1$
    public static final String NAB_DATABASENAME = "db-name"; // $NON-NLS-1$
    
    private String addressBookSel;
    private String addressBookDb;
    private String nameList;
    private Boolean people;
    private Boolean groups;
    private String searchType;
    private String valueNameFormat;
    
    public DominoNABNamePickerData() {
    }

    public String getAddressBookSel() {
        if (null != this.addressBookSel) {
            return this.addressBookSel;
        }
        ValueBinding _vb = getValueBinding("addressBookSel"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setAddressBookSel(String addressBookSel) {
        this.addressBookSel = addressBookSel;
    }

    public String getAddressBookDb() {
        if (null != this.addressBookDb) {
            return this.addressBookDb;
        }
        ValueBinding _vb = getValueBinding("addressBookDb"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setAddressBookDb(String addressBookDb) {
        this.addressBookDb = addressBookDb;
    }

    public String getNameList() {
        if (null != this.nameList) {
            return this.nameList;
        }
        ValueBinding _vb = getValueBinding("nameList"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public boolean isPeople() {
        if (null != this.people) {
            return this.people;
        }
        ValueBinding _vb = getValueBinding("people"); //$NON-NLS-1$
        if (_vb != null) {
            Object obj = _vb.getValue(getFacesContext());
            if( obj instanceof Boolean ){// non-null
                return (Boolean) obj;
            }
        }
        return true;
    }
    public void setPeople(boolean people) {
        this.people = people;
    }

    public boolean isGroups() {
        if (null != this.groups) {
            return this.groups;
        }
        ValueBinding _vb = getValueBinding("groups"); //$NON-NLS-1$
        if (_vb != null) {
            Object obj = _vb.getValue(getFacesContext());
            if( obj instanceof Boolean ){// non-null
                return (Boolean) obj;
            }
        }
        return true;
    }
    public void setGroups(boolean groups) {
        this.groups = groups;
    }

    @Override
    public String getSearchType() {
        if (searchType != null) {
            return searchType;
        }
        ValueBinding vb = getValueBinding("searchType"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * Gets the format a name should be returned as.
     * 
     * @return String specific format
     */
    public String getValueNameFormat() {
        if (null != this.valueNameFormat) {
            return this.valueNameFormat;
        }
        ValueBinding _vb = getValueBinding("valueNameFormat"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }
    /**
     * Loads the value name format (String)
     * 
     * @param valueNameFormat
     *            String
     * @author withersp
     */
    public void setValueNameFormat(String valueNameFormat) {
        this.valueNameFormat = valueNameFormat;
    }
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[8];
        state[0] = super.saveState(context);
        state[1] = addressBookSel;
        state[2] = addressBookDb;
        state[3] = nameList;
        state[4] = people;
        state[5] = groups;
        state[6] = valueNameFormat;
        state[7] = searchType;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.addressBookSel = (String)state[1]; 
        this.addressBookDb = (String)state[2]; 
        this.nameList = (String)state[3]; 
        this.people = (Boolean)state[4]; 
        this.groups = (Boolean)state[5]; 
        this.valueNameFormat = (String) state[6];
        this.searchType = (String) state[7];
    }

    private static enum NameFormat {
        ABBREVIATED, COMMON, CANONICAL, UNFORMATTED
    }
    private NameFormat parseNameFormat(String nameFormatStr){
        if( null != nameFormatStr && nameFormatStr.length() > 0 ){
            // from returnNameFormat="common" to COMMON (only 4 possible values, don't care about locale)
            String upper = nameFormatStr.toUpperCase(Locale.US);
            try{
                NameFormat specified = NameFormat.valueOf(upper);
                return specified;
            }catch(IllegalArgumentException ex){
                // unknown string in XPage source default to unformatted,
                // not usually log (traceDebug is disabled by default).
                if( ExtlibDominoLogger.DOMINO.isTraceDebugEnabled() ){
                    String debugMsg = "Unknown property value for valueNameFormat=\"{0}\" on the tag xe:dominoNABNamePicker in the XPage {1}."; //$NON-NLS-1$
                    String pageName = "<unknown>"; //$NON-NLS-1$
                    FacesContextEx context = FacesContextEx.getCurrentInstance();
                    UIViewRootEx viewRoot = (null == context)? null : (UIViewRootEx)context.getViewRoot();
                    String viewPageName = (null == viewRoot)? null : viewRoot.getPageName();
                    if( null != viewPageName){
                        pageName = viewPageName;
                    }
                    debugMsg = StringUtil.format(debugMsg, nameFormatStr, pageName);
                    ExtlibDominoLogger.DOMINO.traceDebugp(this, "parseNameFormat", ex, debugMsg); //$NON-NLS-1$
                }
            }
        }
        return NameFormat.UNFORMATTED;
    }
    private static String formatName(String baseName, NameFormat format) throws NotesException {
        // optionally reformat the name, as contributed in #14 subpart D1:
        // https://github.com/OpenNTF/XPagesExtensionLibrary/pull/14
        if( StringUtil.isEmpty(baseName) ){
            // don't format empty string
            return baseName;
        }
        if (NameFormat.UNFORMATTED == format) {
            return baseName;
        } else {
            Session sess = ExtLibUtil.getCurrentSession();
            Name nm = sess.createName(baseName); // throws NotesException
            switch(format){
                case ABBREVIATED: return nm.getAbbreviated();
                case CANONICAL: return nm.getCanonical();
                case COMMON: return nm.getCommon();
                default: return baseName; // won't happen
            }
        }
    }
    
    
    // ====================================================================
    // Data access implementation
    // ====================================================================
    
    private static class NABDb implements Serializable { // Serializable because it goes to a scope
        private static final long serialVersionUID = 1L;
        String      name;
        String      title;
        boolean     publicNab;
        boolean     privateNab;
        NABDb(Database db) throws NotesException {
            this(db.getFilePath(),db.getTitle());
            this.publicNab = db.isPublicAddressBook();
            this.privateNab = db.isPrivateAddressBook();
        }
        NABDb(String name, String title) throws NotesException {
            this.name = name;
            this.title = title;
            if(StringUtil.isEmpty(title)) {
                this.title = name;
            }
        }
    }
    
    // Compose the list of all the address books, once for ever...
    // Beyond the cache, this guarantees that the NAB are always retrieved
    // in the same order.
    // The list has to be cached at the session level, as different users can
    // have different ACLs for the databases.
    private static final String KEY_NABS = "extlib.pickers.domino.nabs"; //$NON-NLS-1$ 
    private NABDb[] getSessionAddressBooks() throws NotesException {
        Map<String,Object> sc = ExtLibUtil.getSessionScope();
        NABDb[] addressBooks = sc!=null ? (NABDb[])sc.get(KEY_NABS) : null;
        if(addressBooks==null) {
            // Try with the current user
            Session session = ExtLibUtil.getCurrentSession();
            addressBooks = getSessionAddressBooks(session);
            if(addressBooks!=null && addressBooks.length>0) {
                if(sc!=null) {
                    sc.put(KEY_NABS, addressBooks);
                }
            } else {
                // No NAB is avail - we don't throw a signal from here as it forces authentication
                // as soon as the page is displayed (the control asks for the NAB when rendering)
                //throw new NoAccessSignal();
            }
        }
        return addressBooks;
    }
    private static NABDb[] getSessionAddressBooks(Session session) throws NotesException {
        if(session!=null) { // Unit tests
            ArrayList<NABDb> nabs = new ArrayList<NABDb>();
            Vector<?> vc = session.getAddressBooks();
            if(vc!=null) {
                for(int i=0; i<vc.size(); i++) {
                    Database db = (Database)vc.get(i);
                    try {
                        db.open();
                        try {
                            NABDb nab = new NABDb(db);
                            nabs.add(nab);
                        } finally {
                            db.recycle();
                        }
                    } catch(NotesException ex) {
                        // Opening the database can fail if the user doesn't sufficient
                        // rights. In this vase, we simply ignore this NAB and continue 
                        // with the next one.
                    }
                }
            }
            return nabs.toArray(new NABDb[nabs.size()]);
        }
        return null;
    }
    
    public String[] getSourceLabels() {
        try {
            String sel = getAddressBookSel();
            
            NABDb[] sessNabs = getSessionAddressBooks();
            ArrayList<String> labels = new ArrayList<String>();
            if(StringUtil.isEmpty(sel) || sel.equals(NAB_ALL)) {
                for(int i=0; i<sessNabs.length; i++) {
                    labels.add(sessNabs[i].title);
                }
            } else if(sel.equals(NAB_ALLPUBLIC)) {
                for(int i=0; i<sessNabs.length; i++) {
                    if(sessNabs[i].publicNab) {
                        labels.add(sessNabs[i].title);
                    }
                }
            } else if(sel.equals(NAB_ALLPRIVATE)) {
                for(int i=0; i<sessNabs.length; i++) {
                    if(sessNabs[i].privateNab) {
                        labels.add(sessNabs[i].title);
                    }
                }
            } else if(sel.equals(NAB_FIRST)) {
                if(sessNabs.length>0) {
                    labels.add(sessNabs[0].title);
                }
            } else if(sel.equals(NAB_FIRSTPUBLIC)) {
                for(int i=0; i<sessNabs.length; i++) {
                    if(sessNabs[i].publicNab) {
                        labels.add(sessNabs[i].title);
                        break;
                    }
                }
            } else if(sel.equals(NAB_DATABASENAME)) {
                Database db = DominoUtils.openDatabaseByName(getAddressBookDb());
                return new String[]{db.getTitle()};
            } else {
                throw new FacesExceptionEx(null,"Unknown address book selection type {0}",sel); // $NLX-DominoNABNamePickerData.Unknownaddressbookselectiontype0-1$
            }
            
            return labels.toArray(new String[labels.size()]);
        } catch(NotesException ex) {
            throw new FacesExceptionEx(ex,"Error while retrieving the provider source labels"); // $NLX-DominoNABNamePickerData.Errorwhileretrievingtheproviderso-1$
        }
    }
    

    @Override
    protected EntryMetaData createEntryMetaData(IPickerOptions options) throws NotesException {
        String list = getNameList();
        if(StringUtil.isEmpty(list)) {
            boolean people = isPeople();
            boolean groups = isGroups();
            if(people && groups) {
                list = "peopleAndGroups"; // $NON-NLS-1$
            } else if(people) {
                list = "people"; // $NON-NLS-1$
            } else if(groups) {
                list = "groups"; // $NON-NLS-1$
            }
        }
        if(StringUtil.isNotEmpty(list)) {
            if(list.equals("peopleAndGroups")) { // $NON-NLS-1$
                return new _EntryMetaDataPeopleAndGroup(options);
            } else if(list.equals("peopleByLastName")) { // $NON-NLS-1$
                return new _EntryMetaDataPeopleByLastName(options);
            } else if(list.equals("people")) { // $NON-NLS-1$
                return new _EntryMetaDataPeople(options);
            } else if(list.equals("groups")) { // $NON-NLS-1$
                return new _EntryMetaDataGroup(options);
            }
        }
        return null;
    }
    
    public abstract class _EntryMetaData extends EntryMetaData {
        private NameFormat nameFormatEnum;
        protected _EntryMetaData(IPickerOptions options) throws NotesException {
            super(options);
            String nameFormatStr = getValueNameFormat();
            nameFormatEnum = parseNameFormat(nameFormatStr);
        }
        protected abstract String getViewName();
        @Override
        protected View openView() throws NotesException {
            // Find the database
            Database nabDb = findNAB();
            if(nabDb==null) {
                throw new FacesExceptionEx(null,"Not able to find a valid address book for the name picker"); // $NLX-DominoNABNamePickerData.Notabletofindavalidaddressbookfor-1$
            }
            // Find the view
            String viewName = getViewName();
            if(StringUtil.isEmpty(viewName)) {
                throw new FacesExceptionEx(null,"Not able to find a view in the address book that matches the selection criterias"); // $NLX-DominoNABNamePickerData.Notabletofindaviewintheaddressboo-1$
            }
            
            View view = nabDb.getView(viewName);
            return view;
        }

        protected Database findNAB() throws NotesException {
            String sel = getAddressBookSel();
            
            // Assume the first one for now - should be extended in the future
            IPickerOptions o = getOptions();
            int source = o!=null ? o.getSource() : 0;
            
            NABDb[] sessNabs = getSessionAddressBooks();
            if(sessNabs!=null && sessNabs.length>0) {
                if(StringUtil.isEmpty(sel) || sel.equals(NAB_ALL)) {
                    return DominoUtils.openDatabaseByName(sessNabs[source].name);
                } else if(sel.equals(NAB_ALLPUBLIC)) {
                    int cpt = 0;
                    for(int i=0; i<sessNabs.length; i++) {
                        if(sessNabs[i].publicNab) {
                            if(source==cpt++) {
                                return DominoUtils.openDatabaseByName(sessNabs[i].name);
                            }
                        }
                    }
                } else if(sel.equals(NAB_ALLPRIVATE)) {
                    int cpt = 0;
                    for(int i=0; i<sessNabs.length; i++) {
                        if(sessNabs[i].privateNab) {
                            if(source==cpt++) {
                                return DominoUtils.openDatabaseByName(sessNabs[i].name);
                            }
                        }
                    }
                } else if(sel.equals(NAB_FIRST)) {
                    if(sessNabs.length>0) {
                        return DominoUtils.openDatabaseByName(sessNabs[0].name);
                    }
                } else if(sel.equals(NAB_FIRST)) {
                    for(int i=0; i<sessNabs.length; i++) {
                        if(sessNabs[i].publicNab) {
                            return DominoUtils.openDatabaseByName(sessNabs[i].name);
                        }
                    }
                } else if(sel.equals(NAB_DATABASENAME)) {
                    return DominoUtils.openDatabaseByName(getAddressBookDb());
                } else {
                    throw new FacesExceptionEx(null,"Unknown address book selection type {0}",sel); // $NLX-DominoNABNamePickerData.Unknownaddressbookselectiontype0.1-1$
                }
            } else {
                // If no NAB is avail, request authentication
                // We force the authetication here, but it does not work outside of basic authentication
                // Moreover, the page is not refreshed afterwards to show the new user.
                throw new NoAccessSignal();
            }
            
            return null;
        }
        
    }
    public static abstract class _Entry extends Entry {
        //private Object[] attributes;
        protected _Entry(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
        }
        @Override
        public _EntryMetaData getMetaData() {
            return (_EntryMetaData)super.getMetaData();
        }
        @Override
        protected Object[] readAttributes(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            return null;
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////
    // People
    public class _EntryMetaDataPeople extends _EntryMetaData {
        protected _EntryMetaDataPeople(IPickerOptions options) throws NotesException {
            super(options);
        }
        @Override
        protected String getViewName() {
            return "($VIMPeople)"; // $NON-NLS-1$
        }
        @Override
        protected Entry createEntry(ViewEntry ve) throws NotesException {
            return new _EntryPeople(this,ve);
        }
    }
    public static class _EntryPeople extends _Entry {
        protected _EntryPeople(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
        }
        @Override
        protected Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            String value = (String) columnValues.get(0);
            NameFormat format = getMetaData().nameFormatEnum;
            return formatName(value, format);
        }
        @Override
        protected Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            String first = (String)columnValues.get(1);
            String mid = (String)columnValues.get(2);
            String last = (String)columnValues.get(3);
            StringBuilder b = new StringBuilder();
            if(StringUtil.isNotEmpty(first)) {
                b.append(first);
            }
            if(StringUtil.isNotEmpty(mid)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(mid);
            }
            if(StringUtil.isNotEmpty(last)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(last);
            }
            
            return b.toString();
        }
    }
    public class _EntryMetaDataPeopleByLastName extends _EntryMetaData {
        protected _EntryMetaDataPeopleByLastName(IPickerOptions options) throws NotesException {
            super(options);
        }
        @Override
        protected String getViewName() {
            return "($VIMPeopleByLastName)"; // $NON-NLS-1$
        }
        @Override
        protected Entry createEntry(ViewEntry ve) throws NotesException {
            return new _EntryPeopleByLastName(this,ve);
        }
    }
    public static class _EntryPeopleByLastName extends _Entry {
        protected _EntryPeopleByLastName(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
        }
        @Override
        protected Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            String value = (String) columnValues.get(1);
            NameFormat format = getMetaData().nameFormatEnum;
            return formatName(value, format);
        }
        @Override
        protected Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            String first = (String)columnValues.get(2);
            String mid = (String)columnValues.get(3);
            String last = (String)columnValues.get(0);
            StringBuilder b = new StringBuilder();
            if(StringUtil.isNotEmpty(last)) {
                b.append(last);
            }
            if(StringUtil.isNotEmpty(first)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(first);
            }
            if(StringUtil.isNotEmpty(mid)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(mid);
            }
            
            return b.toString();
        }
    }

    
    //////////////////////////////////////////////////////////////////////
    // Groups
    public class _EntryMetaDataGroup extends _EntryMetaData {
        protected _EntryMetaDataGroup(IPickerOptions options) throws NotesException {
            super(options);
        }
        @Override
        protected String getViewName() {
            return "($VIMGroups)"; // $NON-NLS-1$
        }
        @Override
        protected Entry createEntry(ViewEntry ve) throws NotesException {
            return new _EntryGroup(this,ve);
        }
    }
    public static class _EntryGroup extends _Entry {
        protected _EntryGroup(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
        }
        @Override
        protected Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            return columnValues.get(0);
        }
        @Override
        protected Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            return columnValues.get(0);
        }
    }
    
    //////////////////////////////////////////////////////////////////////
    // People and groups
    public class _EntryMetaDataPeopleAndGroup extends _EntryMetaData {
        protected _EntryMetaDataPeopleAndGroup(IPickerOptions options) throws NotesException {
            super(options);
        }
        @Override
        protected String getViewName() {
            return "($VIMPeopleAndGroups)"; // $NON-NLS-1$
        }
        @Override
        protected Entry createEntry(ViewEntry ve) throws NotesException {
            return new _EntryPeopleAndGroup(this,ve);
        }
    }
    public static class _EntryPeopleAndGroup extends _Entry {
        protected _EntryPeopleAndGroup(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
        }
        @Override
        protected Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            // optionally reformat the name, as contributed in #14 subpart D1:
            // https://github.com/OpenNTF/XPagesExtensionLibrary/pull/14
			if ("G".equals(columnValues.get(0))) {
				// Groups are never canonical, only have a basic value
            return columnValues.get(1);
			} else {
	            String value = (String) columnValues.get(1);
	            NameFormat format = getMetaData().nameFormatEnum;
	            return formatName(value, format);
			}
        }
        @Override
        protected Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            String first = (String)columnValues.get(2);
            String mid = (String)columnValues.get(3);
            String last = (String)columnValues.get(4);
            StringBuilder b = new StringBuilder();
            if(StringUtil.isNotEmpty(first)) {
                b.append(first);
            }
            if(StringUtil.isNotEmpty(mid)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(mid);
            }
            if(StringUtil.isNotEmpty(last)) {
                if(b.length()>0) {
                    b.append(" ");
                }
                b.append(last);
            }
            
            return b.toString();
        }
    }
}