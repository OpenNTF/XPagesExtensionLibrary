/*
 * © Copyright IBM Corp. 2010, 2014
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

package com.ibm.xsp.extlib.util.debug;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sun.security.action.GetBooleanAction;

import com.ibm.commons.util.DateTime;
import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.log.ExtlibCoreLogger;


/**
 * Default Java Factory.
 */
public class JavaDumpFactory implements DumpAccessorFactory {

    private static JavaDumpFactory instance = new JavaDumpFactory();
    public static JavaDumpFactory get() {
        return instance;
    }
    
    public JavaDumpFactory() {
    }
    
    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o==null) {
            return new NullValue(dumpContext);
        }
        
        Class<?> c = o.getClass();
        
        // Check for a simple value
        if(    o instanceof String
            || o instanceof Boolean
            || o instanceof Character
            || o instanceof Number
            || o instanceof Locale 
            || o instanceof TimeZone 
            || o instanceof File 
            || o instanceof Date 
            || o instanceof Calendar) {
            return new PrimitiveValue(dumpContext,o);
        }
        
        // Check for an array/collection
        if( c.isArray() ) {
            return new JavaArray(dumpContext,o);
        }
        if( o instanceof java.util.Map ) {
            return new JavaMap(dumpContext,(java.util.Map)o);
        }
        if( o instanceof Collection) {
            return new JavaCollection(dumpContext,(Collection)o);
        }
        
        // Try a Java bean
        if(dumpContext.shouldUseBeanProperties(o)) {
            try {
                return createJavaBean(dumpContext,o,null);
            } catch(Exception ex) {
            }
        }
        
        // Regular Java class
        return createJavaObject(dumpContext,o,null);
    }

    public static String toPrimitiveString(Object o) {
        if(o==null) {
            return "<null>"; // $NON-NLS-1$
        }
        if(    o instanceof String
            || o instanceof Boolean
            || o instanceof Character) {
            return o.toString();
        }
        if(o instanceof Number) {
            if(o instanceof Double) {
                double d = ((Number)o).doubleValue();
                long l = ((Number)o).longValue();
                if(d==(double)l) {
                    return Long.toString(l);
                }
            }
            if(o instanceof Float) {
                float f = ((Number)o).floatValue();
                long l = ((Number)o).longValue();
                if(f==(float)l) {
                    return Long.toString(l);
                }
            }
            return o.toString();
        }
        
        if(o instanceof Date) {
            return DateTime.formatDateTime((Date)o,DateTime.LONG_DATETIME);
        }
        
        if(o instanceof Calendar) {
            return DateTime.formatDateTime(new Date(((Calendar)o).getTimeInMillis()),DateTime.LONG_DATETIME);
        }

        if(o instanceof TimeZone) {
            return ((TimeZone)o).getDisplayName();
        }

        if(o instanceof Locale) {
            return ((Locale)o).getDisplayName();
        }
        
        return o.toString();
    }
    
    public static class NullValue extends DumpAccessor.Value {
        public NullValue(DumpContext dumpContext) {
            super(dumpContext);
        }
        @Override
        public String getTypeAsString() {
            return "null"; // $NON-NLS-1$
        }
        @Override
        public Object getValue() {
            return null;
        }
        @Override
        public String getValueAsString() {
            return "<null>"; // $NON-NLS-1$
        }
    }

    public static class PrimitiveValue extends DumpAccessor.Value {
        protected Object value;
        public PrimitiveValue(DumpContext dumpContext, Object value) {
            super(dumpContext);
            this.value = value;
        }
        @Override
        public String getTypeAsString() {
            String cName = value.getClass().getName(); 
            return cName;
        }
        @Override
        public Object getValue() {
            return value;
        }
        @Override
        public String getValueAsString() {
            return toPrimitiveString(value);
        }
    }
    
    public static class ExceptionValue extends DumpAccessor.Value {
        protected Throwable value;
        public ExceptionValue(DumpContext dumpContext, Throwable value) {
            super(dumpContext);
            this.value = value;
        }
        @Override
        public String getTypeAsString() {
            String cName = value.getClass().getName(); 
            return cName;
        }
        @Override
        public Object getValue() {
            return value;
        }
        @Override
        public String getValueAsString() {
            return value.toString();
        }
    }
    
    public static abstract class AbstractMap extends DumpAccessor.Map {
        public AbstractMap(DumpContext dumpContext) {
            super(dumpContext);
        }
        @Override
        public String[] getCategories() {
            return null;
        }
        @Override
        public Iterator<Object> getPropertyKeys(String category) {
            ArrayList<Object> list = new ArrayList<Object>();
            getAllPropertyKeys(category,list);
            (new QuickSort.JavaList(list)).sort();
            return list.iterator();
        }
        public abstract void getAllPropertyKeys(String category, List<Object> list);
        //public abstract Object getProperty(String key);
    }
    
    public static class JavaMap extends AbstractMap {
        protected java.util.Map instance;
        public JavaMap(DumpContext dumpContext, java.util.Map instance) {
            super(dumpContext);
            this.instance = instance;
        }
        @Override
        public String getStringLabel() {
            if(instance.isEmpty()) {
                return "<empty map>";  // $NLS-JavaDumpFactory.emptymap-1$
            }
            return null;
        }
        @Override
        public String getTypeAsString() {
            return "Java Map: "+instance.getClass().getName(); // $NON-NLS-1$
        }
        protected boolean accept(Object key) {
            return true;
        }
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            for(Object o: instance.keySet() ) {
                if(accept(o)) {
                    list.add(o);
                }
            }
        }
        @Override
        public Object getProperty(Object key) {
            return instance.get(key);
        }
    }

    public static DumpAccessor createJavaBean(DumpContext dumpContext, Object instance, JavaBean.IFilter filter) {
        try {
            return new JavaBean(dumpContext,instance,filter);
        } catch(Throwable t) {
            return new ExceptionValue(dumpContext,t);
        }
    }
    public static class JavaBean extends AbstractMap {
        public static interface IFilter {
            public boolean accept(PropertyDescriptor desc);
        }
        protected Object instance;
        protected PropertyDescriptor[] desc;
        protected IFilter filter;
        public JavaBean(DumpContext dumpContext, Object instance, IFilter filter) throws IntrospectionException {
            super(dumpContext);
            this.instance = instance;
            this.filter = filter;
            BeanInfo bi = java.beans.Introspector.getBeanInfo(instance.getClass());
            this.desc =  bi.getPropertyDescriptors();
        }
        public Object getInstance() {
            return instance;
        }
        @Override
        public String getStringLabel() {
            if(desc==null || desc.length==0) {
                return "<no public property>";  // $NLS-JavaDumpFactory.nopublicproperty-1$
            }
            return null;
        }
        
        @Override
        public String getTypeAsString() {
            return "Java Bean: "+instance.getClass().getName(); // $NON-NLS-1$
        }
        protected boolean accept(PropertyDescriptor desc) {
            String name = desc.getName();
            if(name.equals("class")) { // $NON-NLS-1$
                return false;
            }
            if(filter!=null) {
                if(!filter.accept(desc)) {
                    return false;
                }
            }
            return true;
        }
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            for( int i=0; i<desc.length; i++ ) {
                if(!accept(desc[i])) {
                    continue;
                }
                list.add(desc[i].getName());
            }
        }
        @Override
        public Object getProperty(Object key) {
        	return getBeanProperty(instance, desc, key);
        }
    }
    public static Object getBeanProperty(Object instance, PropertyDescriptor[] desc, Object key) {
        for( int i=0; i<desc.length; i++ ) {
            if(StringUtil.equals(desc[i].getName(),key)) {
                try {
                    Method read = desc[i].getReadMethod();
                    if(read==null) {
                        String msg = StringUtil.format("<error: No bean read method>");  // $NON-NLS-1$
                        if( ExtlibCoreLogger.CORE.isWarnEnabled() ){
                            ExtlibCoreLogger.CORE.warnp(JavaDumpFactory.class, "getBeanProperty", //$NON-NLS-1$
                                    msg);
                        }
                        return msg;
                    } else {
                        return read.invoke(instance,(Object[])null);
                    }
                } catch(Throwable e) {
                    if(e instanceof InvocationTargetException) {
                        if(e.getCause()!=null) {
                            e = e.getCause();
                        }
                    }
                    String msg = StringUtil.format("<error: {0}>",e.getMessage()); // $NON-NLS-1$
                    if( ExtlibCoreLogger.CORE.isWarnEnabled() ){
                        ExtlibCoreLogger.CORE.warnp(JavaDumpFactory.class, "getBeanProperty", //$NON-NLS-1$
                                e, msg);
                    }
                    return msg;
                }
            }
        }
        return StringUtil.format("<unknown bean property: {0}>",key); // $NON-NLS-1$
    }

    public static DumpAccessor createJavaObject(DumpContext dumpContext, Object instance, JavaObject.IFilter filter) {
        try {
            return new JavaObject(dumpContext, instance,filter);
        } catch(Throwable t) {
            return new ExceptionValue(dumpContext,t);
        }
    }
    public static class JavaObject extends AbstractMap {
        public static interface IFilter {
            public boolean accept(Field filed);
        }
        protected Object instance;
        protected Field[] fields;
        protected IFilter filter;
        public JavaObject(DumpContext dumpContext, Object instance, IFilter filter) {
            super(dumpContext);
            this.instance = instance;
            this.filter = filter;
            this.fields = instance.getClass().getFields();
        }
        public Object getInstance() {
            return instance;
        }
        @Override
        public String getStringLabel() {
            if(fields==null || fields.length==0) {
                return "<no public field>";  // $NLS-JavaDumpFactory.nopublicfield-1$
            }
            return null;
        }
        @Override
        public String getTypeAsString() {
            return "Java Object: "+instance.getClass().getName(); // $NON-NLS-1$
        }
        protected boolean accept(Field field) {
            if((field.getModifiers()&Modifier.PUBLIC)==0) {
                return false;
            }
            if((field.getModifiers()&Modifier.STATIC)!=0) {
                return false;
            }
            if(filter!=null) {
                if(!filter.accept(field)) {
                    return false;
                }
            }
            return true;
        }
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            for( int i=0; i<fields.length; i++ ) {
                if(!accept(fields[i])) {
                    continue;
                }
                list.add(fields[i].getName());
            }
        }
        @Override
        public Object getProperty(Object key) {
            for( int i=0; i<fields.length; i++ ) {
                if(StringUtil.equals(fields[i].getName(),key)) {
                    try {
                        return fields[i].get(instance);
                    } catch(Exception e) {
                        String msg = StringUtil.format("<error: {0}>",e.getMessage()); // $NON-NLS-1$
                        if( ExtlibCoreLogger.CORE.isWarnEnabled() ){
                            ExtlibCoreLogger.CORE.warnp(JavaDumpFactory.class, "getProperty", //$NON-NLS-1$
                                    e, msg);
                        }
                        return msg;
                    }
                }
            }
            String msg = StringUtil.format("<unknown field: {0}>",key); // $NON-NLS-1$
            if( ExtlibCoreLogger.CORE.isWarnEnabled() ){
                ExtlibCoreLogger.CORE.warnp(JavaDumpFactory.class, "getProperty", //$NON-NLS-1$
                        msg);
            }
            return msg;
        }
    }
    
    public static class JavaArray extends DumpAccessor.Array {
        protected Object instance;
        public JavaArray(DumpContext dumpContext, Object instance) {
            super(dumpContext);
            this.instance = instance;
        }
        @Override
        public String getTypeAsString() {
            return "Java Array: "+instance.getClass().getName(); // $NON-NLS-1$
        }
        @Override
        public Iterator<Object> arrayIterator() {
            return new Iterator<Object>() {
                int current = 0;
                int length = java.lang.reflect.Array.getLength(instance);
                public boolean hasNext() {
                    return current<length;
                }
                public Object next() {
                    if( current<length ) {
                        return java.lang.reflect.Array.get(instance, current++);
                    }
                    return null;
                }
                public void remove() {}
            };
        }
    }
    
    public static class JavaCollection extends DumpAccessor.Array {
        protected Collection instance;
        public JavaCollection(DumpContext dumpContext, Collection instance) {
            super(dumpContext);
            this.instance = instance;
        }
        @Override
        public String getTypeAsString() {
            return "Java Collection: "+instance.getClass().getName(); // $NON-NLS-1$
        }
        @Override
        public Iterator<Object> arrayIterator() {
            return ((Collection<Object>)instance).iterator();
        }
    }   
}