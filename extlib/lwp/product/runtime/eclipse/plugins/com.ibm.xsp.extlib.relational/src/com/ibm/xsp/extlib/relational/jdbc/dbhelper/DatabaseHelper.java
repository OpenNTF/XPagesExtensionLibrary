/*
 * © Copyright IBM Corp. 2010, 2015
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

package com.ibm.xsp.extlib.relational.jdbc.dbhelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import com.ibm.commons.util.DateTime;
import com.ibm.commons.util.StringUtil;
import com.ibm.jscript.util.DateUtilities;
import com.ibm.xsp.extlib.relational.RelationalLogger;

/**
 * Access to database functionality.
 * <p>
 * This class is a helper that aims to hide the differences between the different databases. 
 * </p>
 * @author priand
 *
 */
public abstract class DatabaseHelper {
    
    public static DatabaseHelper findHelper(Connection connection) {
        try {
            String s = connection.getMetaData().getDatabaseProductName();
            if(StringUtil.indexOfIgnoreCase(s,"derby")>=0) { // $NON-NLS-1$
                return new DerbyDatabaseHelper();
            }
        } catch(SQLException ex) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(DatabaseHelper.class, "findHelper", ex, "SQLException caused when getting DatabaseHelper"); // $NON-NLS-1$ $NLE-DatabaseHelper.SQLExceptioncausedwhengettingData-2$
            }
        }
        
        // Unknown helper - use the generic one
        return new GenericDatabaseHelper();
    }
    

    public enum Type {
        GENERIC,
        DERBY,
    }
    
    
    // ==========================================================================
    // Database specific capability
    // ==========================================================================

    public abstract Type getType();
    
    public boolean supportsBoolean() {
        return true;
    }
    
    
    // ==========================================================================
    // Batch processing
    // ==========================================================================
    
    /**
     * Send DDL to the JDBC driver.
     */
    public void sendDDL( Connection connection, List<String> v ) throws SQLException {
        sendBatch(connection,v);
    }

    /**
     * Send DDL to the JDBC driver.
     */
    public void sendBatch( Connection connection, List<String> v ) throws SQLException {
        if( v!=null && !v.isEmpty() ) {
            // Debugging statements
            if(RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                RelationalLogger.RELATIONAL.traceDebugp(this, "sendBatch", ">>> Sending batch statements"); //$NON-NLS-1$ $NON-NLS-2$
                for( int i=0; i<v.size(); i++ ) {
                    RelationalLogger.RELATIONAL.traceDebugp(this, "sendBatch", "[{0}]={1}", StringUtil.toString(i), v.get(i) ); //$NON-NLS-1$ $NON-NLS-2$
                }
            }

            // If the driver supports a batch update mode, use it!
            Statement stmt = connection.createStatement();
            try {
                // And send all the SQL statments
                for( String sql: v ) {
                    try {
                        stmt.execute( sql );
                    } catch(SQLException e) {
                        e.setNextException(new SQLException( StringUtil.format("Error while executing the following SQL statement:\n{0}",sql) ));  // $NLX-DatabaseHelper.Errorwhileexecutingthefollowing0s-1$
                        throw e;
                    }
                }
                // And commit the changes
                //connection.commit();
            } finally {
                stmt.close();
            }
        }
    }
    public void sendBatch( Connection connection, List<String> v, boolean makeTransaction ) throws SQLException {
        if( makeTransaction ) {
            boolean autoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                try {
                    sendBatch( connection, v );
                    connection.commit();
                } catch( SQLException e ) {
                    connection.rollback();
                    throw e;
                }
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } else {
            sendBatch( connection, v );
        }
    }
    
    public void addDropTable( List<String> v, String schema, String table ) {
        if(!StringUtil.isEmpty(schema)) {
            addDropTable(v, StringUtil.format("{0}.{1}", schema, table) ); //$NON-NLS-1$
        } else {
            addDropTable(v, table);
        }
    }   

    public void addDropTable( List<String> v, String tableName ) {
        v.add(StringUtil.format("DROP TABLE {0}", tableName)); //$NON-NLS-1$
    }

    
    // =================================================================================
    // SQL data formatters
    // =================================================================================

    /**
     * Format the NULL value to SQL literal.
     */
    public void appendSQLNull( StringBuilder b ) {
        b.append( "NULL" ); //$NON-NLS-1$
    }
    public String getSQLNull() {
        StringBuilder b = new StringBuilder();
        appendSQLNull(b);
        return b.toString();
    }

    /**
     * Format a short constant to SQL literal.
     */
    public void appendSQLShort( StringBuilder b, short value ) {
        b.append( Short.toString(value) );
    }
    public String getSQLShort(short value) {
        StringBuilder b = new StringBuilder();
        appendSQLShort(b,value);
        return b.toString();
    }

    /**
     * Format an integer constant to SQL literal.
     */
    public void appendSQLInteger( StringBuilder b, int value ) {
        b.append( Integer.toString(value) );
    }
    public String getSQLInteger(int value) {
        StringBuilder b = new StringBuilder();
        appendSQLInteger(b,value);
        return b.toString();
    }

    /**
     * Format an integer constant to SQL literal.
     */
    public void appendSQLLong( StringBuilder b, long value ) {
        b.append( Long.toString(value) );
    }
    public String getSQLLong(long value) {
        StringBuilder b = new StringBuilder();
        appendSQLLong(b,value);
        return b.toString();
    }

    /**
     * Format a float constant to SQL literal.
     */
    public void appendSQLFloat( StringBuilder b, float value ) {
        b.append( Float.toString(value) );
    }
    public String getSQLFloat(float value) {
        StringBuilder b = new StringBuilder();
        appendSQLFloat(b,value);
        return b.toString();
    }

    /**
     * Format an double constant to SQL literal.
     */
    public void appendSQLDouble( StringBuilder b, double value ) {
        b.append( Double.toString(value) );
    }
    public String getSQLDouble(double value) {
        StringBuilder b = new StringBuilder();
        appendSQLDouble(b,value);
        return b.toString();
    }

    /**
     * Format a bigdecimal constant to SQL literal.
     */
    public void appendSQLBigDecimal( StringBuilder b, java.math.BigDecimal value ) {
        throw new UnsupportedOperationException();
//        throw new TNotImplementedException("appendBigDecimal"); //$NON-NLS-1$
    }
    public String getSQLBigDecimal(java.math.BigDecimal value) {
        StringBuilder b = new StringBuilder();
        appendSQLBigDecimal(b,value);
        return b.toString();
    }

    /**
     * Format a string constant to SQL literal.
     */
    public void appendSQLString( StringBuilder b, String value ) {
        if( value!=null ) {
            b.append( '\'' );
            int count = value.length();
            for( int i=0; i<count; i++ ) {
                char c = value.charAt(i);
                b.append(c);
                if( c=='\'' ) { // Duplicate the quotes
                    b.append(c);
                }
            }
            b.append( '\'' );
        } else {
            appendSQLNull( b );
        }
    }
    public String getSQLString(String value) {
        StringBuilder b = new StringBuilder();
        appendSQLString(b,value);
        return b.toString();
    }

    public void appendUnicodeSQLString( StringBuilder b, String value ) {
        appendSQLString(b,value);
    }
    public String getUnicodeSQLString(String value) {
        StringBuilder b = new StringBuilder();
        appendUnicodeSQLString(b,value);
        return b.toString();
    }

    public static String escapeString( String string ) {
        int idx = string.indexOf('\'');
        if( idx>=0 ) {
            StringBuilder b = new StringBuilder();
            b.append( string, 0, idx );
            int next;
            do {
                // Append the change to the str
                b.append("''"); //$NON-NLS-1$
                next = idx+1;

                // And search for the next occurence
                idx = string.indexOf('\'',next);

                // Append the string up to the next occurence of the value
                b.append( string, next, idx>=0 ? idx : string.length() );
            } while(idx>=0);

            return b.toString();
        }

        // Nothing change in the string
        return string;
    }

    /**
     * Format a boolean constant to SQL literal.
     */
    public void appendSQLBoolean( StringBuilder b, boolean value) {
        if( supportsBoolean() ) {
            b.append( value?"TRUE":"FALSE" ); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            b.append( value?"1":"0" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    public String getSQLBoolean(boolean value) {
        StringBuilder b = new StringBuilder();
        appendSQLBoolean(b,value);
        return b.toString();
    }

    /**
     * Format a date constant to SQL literal.
     */
    public void appendSQLDate( StringBuilder b, java.sql.Date value ) {
        if( value!=null ) {
            // This was modified by wh$ because in Interbase the DATE datatype
            // is a unknown datatype in Interbase dialect 3 (version 6)
            //b.append( value!=null ? StringUtil.format("DATE '{0}'",value.toString()) : "NULL" );
            DateUtilities.SQLDateStruct ds = value!=null? new DateUtilities.SQLDateStruct(value): null;
            b.append( "{d '" ); //$NON-NLS-1$
            b.append( StringUtil.toString(ds.year,4,'0') );
            b.append( '-' );
            b.append( StringUtil.toString(ds.month,2,'0') );
            b.append( '-' );
            b.append( StringUtil.toString(ds.day,2,'0') );
            b.append( "'}" ); //$NON-NLS-1$
            //b.append( ds!=null ? StringUtil.format("TIMESTAMP '{0}'",ds.toString()) : "NULL" );
        } else {
            b.append( "NULL" ); //$NON-NLS-1$
        }
        //b.append( value!=null ? StringUtil.format("{d '{0}'}",value.toString()) : "NULL" );
    }
    public String getSQLDate(java.sql.Date value) {
        StringBuilder b = new StringBuilder();
        appendSQLDate(b,value);
        return b.toString();
    }

    /**
     * Format a time constant to SQL literal.
     */
    public void appendSQLTime( StringBuilder b, java.sql.Time value ) {
        if( value!=null ) {
            DateUtilities.SQLTimeStruct dt = value!=null? new DateUtilities.SQLTimeStruct(value): null;
            b.append( "{t '" ); //$NON-NLS-1$
            b.append( StringUtil.toString(dt.hour,2,'0') );
            b.append( ':' );
            b.append( StringUtil.toString(dt.minute,2,'0') );
            b.append( ':' );
            b.append( StringUtil.toString(dt.second,2,'0') );
            b.append( "'}" ); //$NON-NLS-1$
            //b.append( dt!=null ? StringUtil.format("TIMESTAMP '{0}'",dt.toString()) : "NULL" );
        } else {
            b.append( "NULL" ); //$NON-NLS-1$
        }
        //b.append( value!=null ? StringUtil.format("{t '{0}'}",value.toString()) : "NULL" );
    }
    public String getSQLTime(java.sql.Time value) {
        StringBuilder b = new StringBuilder();
        appendSQLTime(b,value);
        return b.toString();
    }

    /**
     * Format a timestamp constant to SQL literal.  If the timestampe must be
     * offset for a particular Calendar, the Calendar argument can be supplied.
     * This is the case, e.g., when System times must be skewed for the
     * ACLServer calendar. If no offest is required, the Calendar can be null.
     */
    public void appendSQLTimestamp( StringBuilder b, java.sql.Timestamp value, Calendar targetCal ) {
        if( value!=null ) {
            java.sql.Timestamp convertedValue = value;
            if ( targetCal != null ){
                // if we have a target calendar, convert from the source TZ to
                // the target calendar TZ.
                convertedValue = DateTime.convertTimestampFromJVMTimeZone(
                                                  value, targetCal.getTimeZone());
            }
            DateUtilities.SQLDatetimeStruct dt = new DateUtilities.SQLDatetimeStruct(convertedValue);
            b.append( "{ts '" ); //$NON-NLS-1$
            b.append( StringUtil.toString(dt.year,4,'0') );
            b.append( '-' );
            b.append( StringUtil.toString(dt.month,2,'0') );
            b.append( '-' );
            b.append( StringUtil.toString(dt.day,2,'0') );
            b.append( " " ); //$NON-NLS-1$
            b.append( StringUtil.toString(dt.hour,2,'0') );
            b.append( ':' );
            b.append( StringUtil.toString(dt.minute,2,'0') );
            b.append( ':' );
            b.append( StringUtil.toString(dt.second,2,'0') );
            b.append( ".0'}" ); //$NON-NLS-1$
            //b.append( value!=null ? StringUtil.format("TIMESTAMP '{0}'",value.toString()) : "NULL" );
        } else {
            b.append( "NULL" ); //$NON-NLS-1$
        }
        //b.append( value!=null ? StringUtil.format("{ts '{0}'}",value.toString()) : "NULL" );
    }
    public String getSQLTimestamp(java.sql.Timestamp value, Calendar cal) {
        StringBuilder b = new StringBuilder();
        appendSQLTimestamp(b,value,cal);
        return b.toString();
    }
}