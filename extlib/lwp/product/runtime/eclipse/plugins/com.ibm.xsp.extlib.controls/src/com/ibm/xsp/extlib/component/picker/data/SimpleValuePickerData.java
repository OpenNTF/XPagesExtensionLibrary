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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.Platform;
import com.ibm.commons.util.ArrayIterator;
import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.types.FBSValue;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;


/**
 * Simple data provider for a value picker.
 * <p>
 * This data provider is relying on properties that can be computed using
 * a value binding.
 * </p>
 */
public class SimpleValuePickerData extends ValueBindingObjectImpl implements IValuePickerData {

    private Object valueList;
    private String valueListSeparator;
    private String labelSeparator;
    private Boolean caseInsensitive;

    public SimpleValuePickerData() {
    }

    public String[] getSourceLabels() {
        return null;
    }

    public Object getValueList() {
        if (valueList != null) {
            return valueList;
        }        
        ValueBinding vb = getValueBinding("valueList"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setValueList(Object valueList) {
        this.valueList = valueList;
    }

    public String getValueListSeparator() {
        if (valueListSeparator != null) {
            return valueListSeparator;
        }        
        ValueBinding vb = getValueBinding("valueListSeparator"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setValueListSeparator(String valueListSeparator) {
        this.valueListSeparator = valueListSeparator;
    }

    public String getLabelSeparator() {
        if (labelSeparator != null) {
            return labelSeparator;
        }        
        ValueBinding vb = getValueBinding("labelSeparator"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setLabelSeparator(String labelSeparator) {
        this.labelSeparator = labelSeparator;
    }

    public boolean isCaseInsensitive() {
        if(caseInsensitive!=null) {
            return caseInsensitive;
        }
        ValueBinding vb = getValueBinding("caseInsensitive"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        valueList = _values[1];
        valueListSeparator = (String)_values[2];
        labelSeparator = (String)_values[3];
        caseInsensitive = (Boolean)_values[4];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[5];
        _values[0] = super.saveState(_context);
        _values[1] = valueList;
        _values[2] = valueListSeparator;
        _values[3] = labelSeparator;
        _values[4] = caseInsensitive;
        return _values;
    }

    
    
    // ====================================================================
    // Data access implementation
    // ====================================================================
    
    public boolean hasCapability(int capability) {
        switch(capability) {
            case CAPABILITY_MULTIPLESOURCES:    return false;
            case CAPABILITY_EXTRAATTRIBUTES:    return false;
        }
        return true;
    }

    public IPickerResult readEntries(IPickerOptions options) {
        FacesContext context = FacesContext.getCurrentInstance();
        int start = options.getStart();
        int count = options.getCount();
        String key = options.getKey();
        String startKey = options.getStartKey();
        List<ShadowedObject> _shadowedData = pushVar(context, start, count, key, startKey);
        try {
            Object values = getValueList();
            if(values!=null) {
                String labelSep = getLabelSeparator();
                if(StringUtil.isEmpty(labelSep)) {
                    labelSep = null;
                }
                
                List<IPickerEntry> entries = new ArrayList<IPickerEntry>(64);
                for(Iterator<Object> it=getIterator(values); it.hasNext(); ) {
                    Object o = it.next();
                    if(o!=null) {
                        IPickerEntry e = getEntry(o,labelSep);
                        if(e!=null) {
                            entries.add(e);
                        }
                    }
                }
        
                int nEntries = -1;
                return new SimplePickerResult(entries,nEntries);
            }
            return null;
        } finally {
            popVar(context,_shadowedData);
        }
    }
    private List<ShadowedObject> pushVar(FacesContext context, int start, int count, Object key, Object startKey) {
        List<ShadowedObject> _shadowedData = new ArrayList<ShadowedObject>(1);
        DataPublisher dataPublisher = ((FacesContextEx)context).getDataPublisher();
        dataPublisher.pushObject(_shadowedData, "start", start); // $NON-NLS-1$
        dataPublisher.pushObject(_shadowedData, "count", count); // $NON-NLS-1$
        dataPublisher.pushObject(_shadowedData, "key", key); // $NON-NLS-1$
        dataPublisher.pushObject(_shadowedData, "startKey", startKey); // $NON-NLS-1$
        return _shadowedData;
    }
    private void popVar(FacesContext context, List<ShadowedObject> _shadowedData) {
        ((FacesContextEx)context).getDataPublisher().popObjects(_shadowedData);
    }
        
    public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames) {
        FacesContext context = FacesContext.getCurrentInstance();
        List<IPickerEntry> entries = new ArrayList<IPickerEntry>(ids.length);
        for(int i=0; i<ids.length; i++) {
            entries.add(null);
        }
        String labelSep = getLabelSeparator();
        if(StringUtil.isEmpty(labelSep)) {
            labelSep = null;
        }
        final boolean searchLabel = labelSep!=null; 
        int start = 0;
        int count = Integer.MAX_VALUE;
        String startKey = null;
        for(int i=0; i<ids.length; i++) {
            Object key = ids[i];
            List<ShadowedObject> _shadowedData = pushVar(context, start, count, key, startKey);
            try {
                Object values = getValueList();
                if(values!=null) {
                    boolean caseInsensitive = isCaseInsensitive();
                    for(Iterator<Object> it=getIterator(values); it.hasNext(); ) {
                        Object o = it.next();
                        if(o!=null) {
                            IPickerEntry e = getEntry(o,labelSep);
                            if(e!=null) {
                                //String value = toString(searchLabel?e.getLabel():e.getValue());
                                String value = toString(e.getValue());
                                if(caseInsensitive) {
                                    if(StringUtil.equalsIgnoreCase(value,ids[i].toString())) {
                                        entries.set(i,e);
                                        break;
                                    }
                                } else {
                                    if(StringUtil.equals(value,ids[i])) {
                                        entries.set(i,e);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                popVar(context,_shadowedData);
            }
        }
        // Sort the entries
        (new QuickSort.JavaList(entries) {
            @Override
            public int compare(Object o1, Object o2) {
                if(o1==null) {
                    if(o2==null) return 0;
                    return -1;
                }
                if(o2==null) {
                    return 1;
                }
                String s1 = SimpleValuePickerData.toString(searchLabel ? ((IPickerEntry)o1).getLabel() : ((IPickerEntry)o1).getValue());
                String s2 = SimpleValuePickerData.toString(searchLabel ? ((IPickerEntry)o2).getLabel() : ((IPickerEntry)o2).getValue());
                return StringUtil.compareToIgnoreCase(s1, s2);
            }
        }).sort();
        return entries;
    }
    private static String toString(Object o) {
        return o!=null ? o.toString() : null;
    }
    
    protected IPickerEntry getEntry(Object o, String sep) {
        if(o instanceof FBSValue) {
            try {
                FBSValue v = (FBSValue)o;
                if(v.isArray()) {
                    int l = v.getArrayLength();
                    if(l==1) {
                        return new SimplePickerResult.Entry(v.getArrayValue(0).toJavaObject(),null);
                    } else if(l>1) {
                        return new SimplePickerResult.Entry(v.getArrayValue(1).toJavaObject(),v.getArrayValue(0).toJavaObject());
                    } else {
                        return null;
                    }
                }
                o = v.stringValue();
            } catch(InterpretException ex) {
                Platform.getInstance().log(ex);
                return null;
            }
        }
        if(o instanceof String) {
            String s = (String)o;
            int pos = sep!=null ? s.indexOf(sep) : -1;
            if(pos>=0) { 
                return new SimplePickerResult.Entry(s.substring(pos+1),s.substring(0,pos));
            } else {
                return new SimplePickerResult.Entry(s,null);
            }
        }
        if(o instanceof List) {
            List<?> ls = (List<?>)o;
            int l = ls.size();
            if(l==1) {
                return new SimplePickerResult.Entry(ls.get(0),null);
            } else if(l>1) {
                return new SimplePickerResult.Entry(ls.get(1),ls.get(0));
            } else {
                return null;
            }
        }
        if(o.getClass().isArray()) {
            int l = Array.getLength(o);
            if(l==1) {
                return new SimplePickerResult.Entry(Array.get(o,0),null);
            } else if(l>1) {
                return new SimplePickerResult.Entry(Array.get(o,1),Array.get(o,0));
            } else {
                return null;
            }
        }
        return null;
    }

    protected Iterator<Object> getIterator(Object values) {
        if(values instanceof String) {
            String valuesStr = (String)values;
            String separator = getValueListSeparator();
            if( null == separator ){
                // separator defaults to newline
                if( -1 != valuesStr.indexOf("\r\n") ){ // $NON-NLS-1$
                    separator = "\r\n"; // $NON-NLS-1$
                }else{
                    separator = "\n"; // $NON-NLS-1$
                }
            }
            String[] l = StringUtil.splitString(valuesStr, separator, false);
            return new ArrayIterator<Object>(l);
        }
        if(values.getClass().isArray()) {
            return new ArrayIterator<Object>(values);
        }
        if(values instanceof List) {
            return ((List<Object>)values).iterator();
        }
        if(values instanceof FBSValue) {
            return new JSIterator((FBSValue)values);
        }
        return null;
    }

    // Utility to iterate through JS values
    private static class JSIterator implements Iterator<Object> {
        private FBSValue value;
        private int length;
        private int current;
        private JSIterator(FBSValue value) {
            this.value = value;
            this.length = value.getArrayLength();
            this.current = 0;
        }
        public boolean hasNext() {
            return current<length;
        }
        public Object next() {
            if( current<length ) {
                try {
                    return value.getArrayValue(current++);
                } catch(InterpretException ex) {
                    Platform.getInstance().log(ex);
                }
            }
            return null;
        }
        public void remove() {
        }
    }
}