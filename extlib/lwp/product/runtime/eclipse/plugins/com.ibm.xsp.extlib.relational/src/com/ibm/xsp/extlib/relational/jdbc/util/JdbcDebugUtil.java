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

package com.ibm.xsp.extlib.relational.jdbc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.relational.RelationalLogger;

public class JdbcDebugUtil {
    
    /**
     * Parse a ResultSet and return the data in an in-memory array.
     * @param keys the list of key columns
     * @param multiples the list of multiple values columns
     */
    public static Object[][] parseResultSet( ResultSet resultSet, int[] keys, int[] multiples ) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int colCount = meta.getColumnCount();

        // Compose the multiple column array
        boolean[] bMultiple = new boolean[colCount];
        for( int i=0; i<multiples.length; i++ ) {
            bMultiple[multiples[i]-1] = true;
        }

        // Pointers to the current record
        Vector<Object> result = new Vector<Object>();

        // And browse each results
        Vector<Object> data = new Vector<Object>();
        do {
            // Read the next record
            Object[] newRecord = null;
            if( resultSet.next() ) {
                newRecord = new Object[colCount];
                for( int i=0; i<colCount; i++ ) {
                    newRecord[i] = getColumnValue( resultSet, meta, i+1 );
                }
            }

            // And compare with the old one
            if( data.size()>0 ) {
                if( newRecord==null || valuesEquals( keys, (Object[])data.elementAt(0), newRecord) ) {
                    // Compose a new object
                    Object[] o = new Object[colCount];
                    for( int j=0; j<colCount; j++ ) {
                        if( bMultiple[j] ) {
                            // Multiple value: loop for each item
                            Object[] array = new Object[data.size()];
                            for( int k=0; k<data.size(); k++ ) {
                                Object[] record = (Object[])data.elementAt(k);
                                array[k] = record[j];
                            }
                            o[j] = array;
                        } else {
                            // Single value: simply get it
                            Object[] record = (Object[])data.elementAt(0);
                            o[j] = record[j];
                        }
                    }
                    // Add it to the result
                    result.addElement(o);
                }
            }

            // Simply add the record to the one waiting
            if( newRecord==null ) {
                break;
            } else {
                data.addElement(newRecord);
            }
        } while(true);

        // And compose the result array
        if( result.size()>0 ) {
            Object[][] resultsArr = result.toArray(new Object[result.size()][]);
            return resultsArr;
        }
        return new Object[0][];
    }
    private static Object getColumnValue( ResultSet resultSet, ResultSetMetaData meta, int col ) throws SQLException {
        int type = meta.getColumnType(col);
        switch( type ) {
            case Types.BIT:
                            return new Boolean(resultSet.getBoolean(col));
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                            return resultSet.getString(col);
            case Types.TINYINT:
            case Types.SMALLINT:
                            return new Short(resultSet.getShort(col));
            case Types.INTEGER:
                            return new Integer(resultSet.getInt(col));
            case Types.BIGINT:
                            return new Long(resultSet.getLong(col));
            case Types.NUMERIC:
                            if( meta.getScale(col)>0 ) {
                                return new Double(resultSet.getDouble(col));
                            }
                            if( meta.getPrecision(col)>=9 ) {
                                return new Long(resultSet.getLong(col));
                            }
                            return new Integer(resultSet.getInt(col));
            case Types.FLOAT:
                            return new Float(resultSet.getFloat(col));
            case Types.DOUBLE:
            case Types.REAL:
            case Types.DECIMAL:
                            return new Double(resultSet.getDouble(col));
        }
        throw new SQLException( StringUtil.format("Data type not yet handled ({0})", StringUtil.toString(type)) ); // $NLX-JdbcDebugUtil.Datatypenotyethandled0-1$
    }
    private static boolean valuesEquals( int[] keys, Object[] values1, Object[] values2 )  {
        for( int i=0; i<keys.length; i++ ) {
            int key = keys[i-1];
            if( !valuesEquals(values1[key],values2[key]) ) {
                return false;
            }
        }
        return true;
    }
    private static boolean valuesEquals( Object value1, Object value2 ) {
        if( value1==null || value2==null) {
            return value1==value2;
        }
        return value1.equals(value2);
    }


    // =========================================================================
    // DUMP methods for debug
    // =========================================================================

    /**
     * Dump the resultset meta data.
     */
    public static void dumpMetaData( ResultSetMetaData meta ) {
        if(RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            try {
                if( meta!=null ) {
                    StringBuilder b = new StringBuilder();
                    b.append("Meta data\n"); // $NON-NLS-1$
                    b.append(StringUtil.format("Column count={0}\n", StringUtil.toString(meta.getColumnCount()))); //$NON-NLS-1$
                    for( int i=1; i<=meta.getColumnCount(); i++ ) {
                        b.append(StringUtil.format("  Column #{0}\n", StringUtil.toString(i))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Name={0}\n", StringUtil.toString(meta.getColumnName(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Label={0}\n", StringUtil.toString(meta.getColumnLabel(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Type={0}\n", StringUtil.toString(meta.getColumnType(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Type name={0}\n", StringUtil.toString(meta.getColumnTypeName(i)))); //$NON-NLS-1$
                        //JDBC2: b.append(StringUtil.format("    Class name={0}", StringUtil.toString(meta.getColumnClassName(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Precision={0}\n", StringUtil.toString(meta.getPrecision(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Scale={0}\n", StringUtil.toString(meta.getScale(i)))); //$NON-NLS-1$
                        b.append(StringUtil.format("    Display size={0}\n", StringUtil.toString(meta.getColumnDisplaySize(i)))); //$NON-NLS-1$
                    }
                    RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpMetaData", b.toString()); // $NON-NLS-1$
                } else {
                    RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpMetaData", "ResultSetMetaData was null" ); //$NON-NLS-1$ $NON-NLS-2$
                }
            } catch( Exception ex ) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()){
                    RelationalLogger.RELATIONAL.errorp(JdbcDebugUtil.class, "dumpMetaData", ex, "Unhandled exception dumping ResultSet meta data", ex); // $NON-NLS-1$ $NLE-JdbcDebugUtil.UnhandledexceptiondumpingResultSe-2$
                }
            }
        }
    }

    /**
     * Dump the resultset meta data.
     */
    public static void dumpMetaData( ResultSet result ) {
        try {
            if( result!=null ) {
                ResultSetMetaData meta = result.getMetaData();
                dumpMetaData( meta );
            } else {
                if(RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                    RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpMetaData", "ResultSet is null"); // $NON-NLS-1$ $NON-NLS-2$
                }
            }
        } catch( Exception ex ) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()){
                RelationalLogger.RELATIONAL.errorp(JdbcDebugUtil.class, "dumpMetaData", ex, "Unhandled exception dumping ResultSet meta data"); // $NON-NLS-1$ $NLE-JdbcDebugUtil.UnhandledexceptiondumpingResultSe.1-2$
            }
        }
    }

    
    public static class DumpParams {
        public boolean startFromFirst = false;
        public boolean printIfEmpty = true;
        public int maxRows = 30;
    }
    
    /**
     * Dump a result set.
     */
    public static int dumpResultSet( ResultSet result ) {
        return dumpResultSet(result,null);
    }
    public static int dumpResultSet( ResultSet result, DumpParams params) {
        if(RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            if(params==null) {
                params = new DumpParams();
            }
            try {
                StringBuilder b = new StringBuilder();
                if(params.startFromFirst) {
                    result.beforeFirst();
                }
                ResultSetMetaData meta = result.getMetaData();
                int count=0;
                while(count<params.maxRows) {
                    boolean next = result.next();
                    if( count==0 && (params.printIfEmpty || next) ) {
                        b.append(prtSeparator(meta));
                        b.append(prtHeader(meta));
                        b.append(prtSeparator(meta));
                    }
                    if(next) {
                        b.append(prtRow(meta,result));
                        count++;
                    } else {
                        break;
                    }
                }
                if( count>0 || params.printIfEmpty ) {
                    b.append(prtSeparator(meta));
                }
                RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpResultSet", b.toString()); // $NON-NLS-1$
                return count;
            } catch( Exception ex ) {
                if(RelationalLogger.RELATIONAL.isErrorEnabled()){
                    RelationalLogger.RELATIONAL.errorp(JdbcDebugUtil.class, "dumpResultSet", ex, "Unhandled exception dumping ResultSet data"); // $NON-NLS-1$ $NLE-JdbcDebugUtil.UnhandledexceptiondumpingResultSe.2-2$
                }
            }
        }
        return 0;
    }
    public static void dumpResultSetCurrentRow( ResultSet result ) {
        try {
            ResultSetMetaData meta = result.getMetaData();
            prtRow(meta,result);
        } catch( Exception ex ) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()){
                RelationalLogger.RELATIONAL.errorp(JdbcDebugUtil.class, "dumpResultSetCurrentRow", ex, "Unhandled exception dumping current row of ResultSet"); // $NON-NLS-1$ $NLE-JdbcDebugUtil.Unhandledexceptiondumpingcurrentr-2$
            }
        }
    }
    private static int colSize( ResultSetMetaData meta, int col ) throws Exception {
        String cLabel = meta.getColumnLabel(col);
        int sz = meta.getColumnDisplaySize(col);
        switch( meta.getColumnType(col) ) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR: {
                sz = Math.min( Math.max( sz, 20 ), 64 );
            }
        }
        return Math.max( sz, cLabel.length() );
    }
    private static String prtSeparator( ResultSetMetaData meta ) throws Exception {
        StringBuilder b = new StringBuilder();
        int colCount = meta.getColumnCount();
        for( int i=1; i<=colCount; i++ ) {
            int sz = colSize(meta,i);
            b.append( '+' );
            b.append(pad("-", sz) );
        }
        b.append( "+\n" ); //$NON-NLS-1$
        return b.toString();
    }
    private static String prtHeader( ResultSetMetaData meta ) throws Exception {
        StringBuilder b = new StringBuilder();
        int colCount = meta.getColumnCount();
        for( int i=1; i<=colCount; i++ ) {
            int sz = colSize(meta,i);
            b.append( '|' );
            b.append( pad(meta.getColumnLabel(i), sz) );
        }
        b.append( "|\n" ); //$NON-NLS-1$
        return b.toString();
    }
    private static String prtRow( ResultSetMetaData meta, ResultSet result ) throws Exception {
        StringBuilder b = new StringBuilder();
        int colCount = meta.getColumnCount();
        for( int i=1; i<=colCount; i++ ) {
            int sz = colSize(meta,i);
            b.append( '|' );
            b.append( pad(colString(meta,result,i), sz) );
        }
        b.append( "|\n" ); //$NON-NLS-1$
        return b.toString();
    }
    private static String colString( ResultSetMetaData meta, ResultSet result, int col ) throws Exception {
        return result.getString(col);
    }
    // TODO: in TString ?
    private static String pad( String s, int sz ) {
        if( s!=null ) {
            int strLen = s.length();
            if( strLen==sz ) {
                return s;
            }
            if( strLen>sz ) {
                return s.substring(0,sz);
            }
            return s + StringUtil.repeat( ' ', sz-strLen );
        } else {
            return StringUtil.repeat( ' ', sz );
        }
    }

    /**
     * Dump all the tables.
     */
    public static void dumpTables( Connection con, boolean details ) {
    	if(RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            try {
	            // Dump the table list
	            ResultSet tables = con.getMetaData().getTables(null,null,"%",new String[]{"TABLE"}); //$NON-NLS-1$ //$NON-NLS-2$
	            dumpResultSet(tables);
	            tables.close();
	
	            // And dump each table
	            if( details ) {
	                ResultSet tables2 = con.getMetaData().getTables(null,null,"%",new String[]{"TABLE"}); //$NON-NLS-1$ //$NON-NLS-2$
	                while( tables2.next() ) {
	                    String cat = tables2.getString(1);
	                    String schem = tables2.getString(2);
	                    String name = tables2.getString(3);
	                    
	                    StringBuilder b1 = new StringBuilder();
	                    b1.append(StringUtil.format( "Table={0} (catalog={1}, schema={2})", name, cat, schem )); //$NON-NLS-1$
	                    //b1.append( "COLUMNS" ); //$NON-NLS-1$
	                    //RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpTables", b1.toString()); // $NON-NLS-1$
	                    //dumpColumns( con, cat, schem, name );
	                   
	                    StringBuilder b2 = new StringBuilder();
	                    b2.append( "PRIMARY KEY" ); //$NON-NLS-1$
	                    RelationalLogger.RELATIONAL.traceDebugp(JdbcDebugUtil.class, "dumpTables", b2.toString()); // $NON-NLS-1$
	                    dumpPrimaryKey( con, cat, schem, name );
	                }
	                tables2.close();
	            }
	        } catch( SQLException ex ) {
	            if(RelationalLogger.RELATIONAL.isErrorEnabled()){
	                RelationalLogger.RELATIONAL.errorp(JdbcDebugUtil.class, "dumpTables", ex, "SQLException occured dumping table data"); // $NON-NLS-1$ $NLE-JdbcDebugUtil.SQLExceptionoccureddumpingtableda-2$
	            }
	        }
    	}
    }
//    private static void dumpColumns( Connection con, String cat, String schem, String name ) throws SQLException {
//        // Dump the columns for that table
//        ResultSet cols = con.getMetaData().getColumns(cat,schem,name,"%"); //$NON-NLS-1$
//        dumpResultSet(cols);
//        cols.close();
//    }
    private static void dumpPrimaryKey( Connection con, String cat, String schem, String name ) throws SQLException {
        // Dump the primary key for that table
        ResultSet keys = con.getMetaData().getPrimaryKeys(cat,schem,name);
        dumpResultSet(keys);
        keys.close();
    }

    /**
     */
}