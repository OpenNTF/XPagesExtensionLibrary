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

package com.ibm.xsp.extlib.relational.debug;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.QuickSort;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.util.debug.BasicDumpFactory;
import com.ibm.xsp.extlib.util.debug.DumpAccessor;
import com.ibm.xsp.extlib.util.debug.DumpAccessorFactory;
import com.ibm.xsp.extlib.util.debug.DumpContext;


/**
 * Default Java Factory.
 */
public class JdbcDumpFactory implements DumpAccessorFactory {

    public JdbcDumpFactory() {
    }
    
    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o instanceof DatabaseMetaData) {
            return new DatabaseMetaDataMap(dumpContext,(DatabaseMetaData)o);
        }
        if(o instanceof ResultSet) {
            return new ResultSetGrid(dumpContext,(ResultSet)o);
        }
        if(o instanceof ResultSetMetaData) {
            return new ResultSetMetaDataGrid(dumpContext,(ResultSetMetaData)o);
        }
        return null;
    }

    public static class DatabaseMetaDataMap extends BasicDumpFactory.PropertyMap {
        public DatabaseMetaDataMap(DumpContext dumpContext, DatabaseMetaData d) {
            super(dumpContext,"Database Meta Data"); // $NON-NLS-1$
            try {
                addCategory("Database & Driver"); // $NLS-JdbcDumpFactory.DatabaseDriver-1$
                addValue("getDatabaseProductName()", d.getDatabaseProductName()); // $NON-NLS-1$
                addValue("getDatabaseProductVersion()", d.getDatabaseProductVersion()); // $NON-NLS-1$
                addValue("getDriverName()", d.getDriverName()); // $NON-NLS-1$
                addValue("getDriverVersion()", d.getDriverVersion()); // $NON-NLS-1$

                addCategory("All Properties"); // $NLS-JdbcDumpFactory.AllProperties-1$
                // Read all the methods from this class that don't take a parameter and return a simple type
                // then add them as a property
                Method[] allMethods = d.getClass().getMethods();
                (new QuickSort.ObjectArray(allMethods) {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Method m1 = (Method)o1;
                        Method m2 = (Method)o2;
                        return m1.getName().compareTo(m2.getName());
                    }
                }).sort();
                for(int i=0; i<allMethods.length; i++) {
                    Method m = allMethods[i];
                    if(m.getParameterTypes().length!=0) {
                        continue;
                    }
                    Class<?> ret = m.getReturnType();
                    if(ret==String.class || ret==Boolean.TYPE || ret==Integer.TYPE || ret==Long.TYPE) {
                        String name = m.getName();
                        if(name.equals("getClass") || name.equals("hashCode") || name.equals("toString")) { // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                            continue;
                        }
                        
                        Object value = m.invoke(d);
                        addValue(m.getName(), value);
                    }
                }
            } catch(Exception ex) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                    RelationalLogger.RELATIONAL.errorp(this, "DatabaseMetaDataMap", ex, "Exception during creation of DatabaseMetaDataMap"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.ExceptionduringcreationofDatabase-2$
                }
                addValue("Exception", ex.getMessage()); // $NON-NLS-1$
            }
            
        }
    }
    public static class ResultSetGrid extends DumpAccessor.Grid {
        ResultSet rs;
        int ncol;
        public ResultSetGrid(DumpContext dumpContext, ResultSet rs) {
            super(dumpContext);
            try {
                this.rs = rs;
                ResultSetMetaData meta = rs.getMetaData();
                ncol = meta.getColumnCount();
            } catch(SQLException ex) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                    RelationalLogger.RELATIONAL.errorp(this, "ResultSetGrid", ex, "SQLException during creation of ResultSetGrid"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptionduringcreationofResul-2$
                }
            }
        }
        @Override
        public String[] getColumns() {
            try {
                ResultSetMetaData meta = rs.getMetaData();
                ArrayList<String> cols = new ArrayList<String>();
                ncol = meta.getColumnCount();
                for(int i=0; i<ncol; i++) {
                    cols.add(meta.getColumnLabel(i+1));
                }
                return cols.toArray(new String[cols.size()]);
            } catch(SQLException ex) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                    RelationalLogger.RELATIONAL.errorp(this, "ResultSetGrid.getColumns", ex, "SQLException caused when retrieving columns"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptioncausedwhenretrievingc-2$
                }
            }
            return null;
        }
        @Override
        public Object getValue(Object object, int col) {
            return ((String[])object)[col];
        }
        @Override
        public Iterator<Object> objectIterator(int first, final int count) {
            try {
                if(count<0) {
                    return null;
                }
                if(first>0) {
                    if(!rs.relative(first)) {
                        return null;
                    }
                }
                Iterator<Object> it = new Iterator<Object>() {
                    String[] values;
                    int n;
                    public boolean hasNext() {
                        return values!=null;
                    }
                    public Object next() {
                        String[] result = values;
                        values = null;
                        if(n<count) {
                            try {
                                if(rs.next()) {
                                    n++;
                                    values = new String[ncol];
                                    for(int i=0; i<ncol; i++) {
                                        try {
                                            values[i] = rs.getString(i+1); 
                                        } catch(SQLException ex) {
                                            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                                                RelationalLogger.RELATIONAL.errorp(this, "ResultSetGrid.objectIterator.next", ex, "SQLException caused when retrieving values "); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptioncausedwhenretrievingv-2$
                                            }
                                            return ex.toString();
                                        }
                                    }
                                }
                            } catch(SQLException ex) {
                                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                                    RelationalLogger.RELATIONAL.errorp(this, "ResultSetGrid.objectIterator.next", ex, "SQLException retrieving next Object from Iterator"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptionretrievingnextObjectf-2$
                                }
                                return ex.toString();
                            }
                        }
                        return result;
                    }
                    public void remove() {
                    }
                };
                it.next();
                return it;
            } catch(SQLException ex) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                    RelationalLogger.RELATIONAL.errorp(this, "ResultSetGrid.objectIterator", ex, "SQLException creating Iterator"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptioncreatingIterator-2$
                }
            }
            return null;
        }
        @Override
        public String getTypeAsString() {
            return "ResultSet"; // $NON-NLS-1$
        }
    }
    public static class ResultSetMetaDataGrid extends DumpAccessor.Grid {
        ResultSetMetaData meta;
        List<Method> columnList;
        public ResultSetMetaDataGrid(DumpContext dumpContext, ResultSetMetaData meta) {
            super(dumpContext);
            this.meta = meta;
            Method[] allMethods = meta.getClass().getMethods();
            (new QuickSort.ObjectArray(allMethods) {
                @Override
                public int compare(Object o1, Object o2) {
                    String m1 = getName((Method)o1);
                    String m2 = getName((Method)o2);
                    return m1.compareTo(m2);
                }
                private String getName(Method m) {
                    String name = m.getName();
                    // Remove the get/is getter prefix
                    if(name.startsWith("get")) { // $NON-NLS-1$
                        name = name.substring(3);
                    } else if(name.startsWith("is")) { // $NON-NLS-1$
                        name = name.substring(2);
                    }
                    // Force some columns to be the first ones, the rest will be in alpahbetic order
                    if(name.equals("SchemaName")) { // $NON-NLS-1$
                        return "!001";
                    }
                    if(name.equals("TableName")) { // $NON-NLS-1$
                        return "!002";
                    }
                    if(name.equals("ColumnName")) { // $NON-NLS-1$
                        return "!003";
                    }
                    if(name.equals("ColumnTypeName")) { // $NON-NLS-1$
                        return "!004";
                    }
                    if(name.equals("Precision")) { // $NON-NLS-1$
                        return "!005";
                    }
                    if(name.equals("Scale")) { // $NON-NLS-1$
                        return "!006";
                    }
                    if(name.equals("ColumnType")) { // $NON-NLS-1$
                        return "!007";
                    }
                    if(name.equals("ColumnClassName")) { // $NON-NLS-1$
                        return "!008";
                    }
                    return name;
                }
            }).sort();
            columnList = new ArrayList<Method>(allMethods.length);
            for(int i=0; i<allMethods.length; i++) {
                Method m = allMethods[i];
                if(m.getParameterTypes().length!=1 || m.getParameterTypes()[0]!=Integer.TYPE) {
                    continue;
                }
                columnList.add(m);
            }
        }
        @Override
        public String[] getColumns() {
            String[] allCols = new String[columnList.size()];
            for(int i=0; i<columnList.size(); i++) {
                String col = columnList.get(i).getName();
                if(col.startsWith("get")) { // $NON-NLS-1$
                    col = col.substring(3);
                } else if(col.startsWith("is")) { // $NON-NLS-1$
                    col = col.substring(2);
                }
                allCols[i] = col;
            }
            return allCols;
        }
        @Override
        public Object getValue(Object object, int col) {
            try {
                Method m = columnList.get(col);
                return m.invoke(meta, (Integer)object);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public Iterator<Object> objectIterator(final int first, final int count) {
            int colCount = 0;
            try {
                colCount = Math.min(first+count, meta.getColumnCount());
            } catch (SQLException ex) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                    RelationalLogger.RELATIONAL.errorp(this, "ResultSetMetaDataGrid.objectIterator", ex, "SQLException when calculating column count"); // $NON-NLS-1$ $NLE-JdbcDumpFactory.SQLExceptionwhencalculatingcolumn-2$
                }
            }
            final int max = Math.min(first+count, colCount);
            Iterator<Object> it = new Iterator<Object>() {
                int n = first;
                public boolean hasNext() {
                    return n<max;
                }
                public Object next() {
                    return (n++)+1; // Add one because of JDBC indexes
                }
                public void remove() {
                }
            };
            return it;
        }
        @Override
        public String getTypeAsString() {
            return "ResultSetMetaData"; // $NON-NLS-1$
        }
    }
}