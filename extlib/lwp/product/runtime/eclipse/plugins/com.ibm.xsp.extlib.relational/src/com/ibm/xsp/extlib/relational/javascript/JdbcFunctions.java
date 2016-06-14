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
package com.ibm.xsp.extlib.relational.javascript;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.JSContext;
import com.ibm.jscript.JavaScriptException;
import com.ibm.jscript.engine.IExecutionContext;
import com.ibm.jscript.std.ArrayObject;
import com.ibm.jscript.std.ObjectObject;
import com.ibm.jscript.types.BuiltinFunction;
import com.ibm.jscript.types.FBSDefaultObject;
import com.ibm.jscript.types.FBSGlobalObject;
import com.ibm.jscript.types.FBSObject;
import com.ibm.jscript.types.FBSUndefined;
import com.ibm.jscript.types.FBSUtility;
import com.ibm.jscript.types.FBSValue;
import com.ibm.jscript.types.FBSValueVector;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;

/**
 * Extended Notes/Domino formula language for JDBC
 * <p>
 * This class implements a set of new functions available to the JavaScript
 * interpreter. They become available to Domino Designer in the category
 * "@JDBC".
 * </p>
 */
public class JdbcFunctions extends FBSDefaultObject {

	// Functions IDs
	private static final int FCT_GETCONNECTION = 1;
	private static final int FCT_DBCOLUMN = 2;

	private static final int FCT_EXECUTEQUERY = 3;
	private static final int FCT_INSERT = 4;
	private static final int FCT_UPDATE = 5;
	private static final int FCT_DELETE = 6;
	private static final int FCT_INSERT_EXT = 7;
	private static final int FCT_UPDATE_EXT = 8;

	// ============================= CODE COMPLETION ==========================
	//
	// Even though JavaScript is an untyped language, the XPages JavaScript
	// interpreter can make use of symbolic information defining the
	// objects/functions exposed. This is particularly used by Domino Designer
	// to provide the code completion facility and help the user writing code.
	//
	// Each function expose by a library can then have one or multiple
	// "prototypes", defining its parameters and the returned value type. To
	// make this definition as efficient as possible, the parameter definition
	// is compacted within a string, where all the parameters are defined
	// within parenthesis followed by the returned value type.
	// A parameter is defined by its name, followed by a colon and its type.
	// Generally, the type is defined by a single character (see bellow) or a
	// full Java class name. The returned type is defined right after the
	// closing parameter parenthesis.
	//
	// Here is, for example, the definition of the "@Date" function which can
	// take 3 different set of parameters:
	// "(time:Y):Y",
	// "(years:Imonths:Idays:I):Y",
	// "(years:Imonths:Idays:Ihours:Iminutes:Iseconds:I):Y");
	//
	// List of types
	// V void
	// C char
	// B byte
	// S short
	// I int
	// J long
	// F float
	// D double
	// Z boolean
	// T string
	// Y date/time
	// W any (variant)
	// N multiple (...)
	// L<name>; object
	// ex:
	// (entries:[Lcom.ibm.xsp.extlib.MyClass;):V
	//
	// =========================================================================

	public JdbcFunctions(JSContext jsContext) {

		super(jsContext, null, false);

		addFunction(FCT_GETCONNECTION, "@JdbcGetConnection", "(data:T):Ljava.sql.Connection;"); // $NON-NLS-1$
																								// $NON-NLS-2$
		addFunction(FCT_DBCOLUMN, "@JdbcDbColumn", "(connection:Wtable:Tcolumn:T):A", "(connection:Wtable:Tcolumn:Twhere:T):A",
				"(connection:Wtable:Tcolumn:Twhere:TorderBy:T):A", "(connection:Wtable:Tcolumn:Twhere:TorderBy:Tparams:A):A"); // $NON-NLS-1$
																																// $NON-NLS-2$
																																// $NON-NLS-3$
																																// $NON-NLS-4$
																																// $NON-NLS-5$
		addFunction(FCT_EXECUTEQUERY, "@JdbcExecuteQuery", "(connection:Wsql:T):Ljava.sql.ResultSet;",
				"(connection:Wsql:Tparams:A):Ljava.sql.ResultSet;"); // $NON-NLS-1$
																		// $NON-NLS-2$
																		// $NON-NLS-3$
		addFunction(FCT_INSERT, "@JdbcInsert", "(connection:Wtable:Tvalues:W):I", "(connection:Wtable:Tvalues:WcolumnNames:A):I"); // $NON-NLS-1$
																																	// $NON-NLS-2$
																																	// $NON-NLS-3$
		addFunction(FCT_UPDATE, "@JdbcUpdate", "(connection:Wtable:Tvalues:W):I", "(connection:Wtable:Tvalues:Wwhere:T):I",
				"(connection:Wtable:Tvalues:Wwhere:Tparams:A):I"); // $NON-NLS-1$
																	// $NON-NLS-2$
																	// $NON-NLS-3$
																	// $NON-NLS-4$
		addFunction(FCT_DELETE, "@JdbcDelete", "(connection:Wtable:Twhere:T):I", "(connection:Wtable:Twhere:Tparams:A):I"); // $NON-NLS-1$
																															// $NON-NLS-2$
																															// $NON-NLS-3$
		addFunction(FCT_INSERT_EXT, "@JdbcInsertExt", "(connection:Wtable:Tvalues:W):I", "(connection:Wtable:Tvalues:WcolumnNames:A):I");
		addFunction(FCT_UPDATE_EXT, "@JdbcUpdateExt", "(connection:Wtable:Tvalues:W):I", "(connection:Wtable:Tvalues:Wwhere:T):I",
				"(connection:Wtable:Tvalues:Wwhere:Tparams:A):I");
	}

	private void addFunction(int index, String functionName, String... params) {
		createMethod(functionName, FBSObject.P_NODELETE | FBSObject.P_READONLY, new NotesFunction(getJSContext(), index, functionName,
				params));
	}

	@Override
	public boolean hasInstance(FBSValue v) {
		return v instanceof FBSGlobalObject;
	}

	@Override
	public boolean isJavaNative() {
		return false;
	}

	// =================================================================================
	// Functions implementation
	// For optimization reasons, there is one NotesFunction instance per
	// function,
	// instead of one class (this avoids loading to many classes). To then
	// distinguish
	// the actual function, it uses an index member.
	// =================================================================================

	public static class NotesFunction extends BuiltinFunction {

		private String functionName;
		private int index;
		private String[] params;

		NotesFunction(JSContext jsContext, int index, String functionName, String[] params) {

			super(jsContext);
			this.functionName = functionName;
			this.index = index;
			this.params = params;
		}

		/**
		 * Index of the function.
		 * <p>
		 * There must be one instanceof this class per index.
		 * </p>
		 */
		public int getIndex() {
			return this.index;
		}

		/**
		 * Return the list of the function parameters.
		 * <p>
		 * Note that this list is not used at runtime, at least for now, but
		 * consumed by Designer code completion.<br>
		 * A function can expose multiple parameter sets.
		 * </p>
		 */
		@Override
		protected String[] getCallParameters() {
			return this.params;
		}

		/**
		 * Function name, as exposed by Designer and use at runtime.
		 * <p>
		 * This function is exposed in the JavaScript global namespace, so you
		 * should be careful to avoid any name conflict.
		 * </p>
		 */
		@Override
		public String getFunctionName() {
			return this.functionName;
		}

		/**
		 * Actual code execution.
		 * <p>
		 * The JS runtime calls this method when the method is executed within a
		 * JavaScript formula.
		 * </p>
		 * 
		 * @param context
		 *            the JavaScript execution context (global variables,
		 *            function...)
		 * @param args
		 *            the arguments passed to the function
		 * @params _this the "this" object when the method is called as a "this"
		 *         member
		 */
		@Override
		public FBSValue call(IExecutionContext context, FBSValueVector args, FBSObject _this) throws JavaScriptException {

			try {
				// Else execute the formulas
				switch (index) {

				case FCT_GETCONNECTION: {
					if (args.size() >= 1) {
						String name = args.get(0).stringValue();
						return FBSUtility.wrap(context.getJSContext(),
								JdbcUtil.createNamedConnection(FacesContext.getCurrentInstance(), name));
					}
				}
					break;

				case FCT_DBCOLUMN: {
					if (args.size() >= 3) {
						Connection c = getConnection(args.get(0));
						String tbName = args.get(1).stringValue();
						String colName = args.get(2).stringValue();
						String where = args.size() > 3 && !args.get(3).isNull() ? args.get(3).stringValue() : null;
						String orderBy = args.size() > 4 && !args.get(4).isNull() ? args.get(4).stringValue() : null;
						FBSValue params = args.size() > 5 && !args.get(5).isNull() ? args.get(5) : null;

						String sql = StringUtil.format("SELECT {0} FROM {1}", colName, tbName); // $NON-NLS-1$
						if (StringUtil.isNotEmpty(where)) {
							sql = sql + " WHERE " + where; // $NON-NLS-1$
						}
						if (StringUtil.isNotEmpty(orderBy)) {
							sql = sql + " ORDER BY " + orderBy; // $NON-NLS-1$
						}
						PreparedStatement st = c.prepareStatement(sql);
						try {
							initParameters(st, params);
							ResultSet rs = st.executeQuery();
							try {
								ArrayObject result = new ArrayObject(getJSContext());
								while (rs.next()) {
									Object value = rs.getObject(1);
									result.addArrayValue(FBSUtility.wrap(context.getJSContext(), value));
								}
								// Make the result similar to @DbColumn
								if (result.getArrayLength() == 0) {
									return FBSUndefined.undefinedValue;
								} else if (result.getArrayLength() == 1) {
									return result.get(0);
								} else {
									return result;
								}
							} finally {
								rs.close();
							}
						} finally {
							st.close();
						}
					}
				}
					break;

				case FCT_EXECUTEQUERY: {
					if (args.size() >= 2) {
						Connection c = getConnection(args.get(0));
						String sql = args.get(1).stringValue();
						FBSValue params = args.size() > 2 && !args.get(2).isNull() ? args.get(2) : null;

						PreparedStatement st = c.prepareStatement(sql);
						initParameters(st, params);
						ResultSet rs = st.executeQuery();
						return FBSUtility.wrap(context.getJSContext(), rs);
					}
				}
					break;

				case FCT_INSERT: {
					FBSValue retVal_ = doInsert(args, context, true);
					if (null != retVal_) {
						return retVal_;
					}
				}
					break;

				case FCT_INSERT_EXT: {
					FBSValue retVal_ = doInsert(args, context, false);
					if (null != retVal_) {
						return retVal_;
					}
				}
					break;

				case FCT_UPDATE: {
					FBSValue retVal_ = doUpdate(args, context, true);
					if (null != retVal_) {
						return retVal_;
					}
				}
					break;

				case FCT_UPDATE_EXT: {
					FBSValue retVal_ = doUpdate(args, context, false);
					if (null != retVal_) {
						return retVal_;
					}
				}
					break;

				case FCT_DELETE: {
					if (args.size() >= 2) {
						Connection c = getConnection(args.get(0));
						String tbName = args.get(1).stringValue();
						String where = args.size() >= 2 && !args.get(2).isNull() ? args.get(2).stringValue() : null;
						FBSValue params = args.size() >= 3 && !args.get(3).isNull() ? args.get(3) : null;

						StringBuilder b = new StringBuilder();
						b.append("DELETE FROM  "); // $NON-NLS-1$
						JdbcUtil.appendTableName(b, tbName);
						if (StringUtil.isNotEmpty(where)) {
							b.append(" WHERE "); // $NON-NLS-1$
							b.append(where);
						}
						String sql = b.toString();
						PreparedStatement st = c.prepareStatement(sql);
						try {
							if (params != null) {
								initParameters(st, params);
							}
							int count = st.executeUpdate();
							return FBSUtility.wrap(context.getJSContext(), count);
						} finally {
							st.close();
						}
					}
				}
					break;

				default: {
					throw new InterpretException(null, StringUtil.format("Internal error: unknown function \'{0}\'", functionName)); // $NLX-JdbcFunctions.Internalerrorunknownfunction0-1$
				}

				}

				// } catch (InterpretException e) {
				// throw e;
				// } catch (NotesException e) {
				// // This case covers where a call to session.evaluate() throws
				// a NotesException
				// // We want to continue rendering the page but allow @IsError
				// to pick up on this issue
				// // so we return @Error (NaN / FBSUndefined.undefinedValue)
				// return FBSUndefined.undefinedValue;
			} catch (Exception e) {
				throw new InterpretException(e, StringUtil.format("Error while executing function \'{0}\'", functionName)); // $NLX-JdbcFunctions.Errorwhileexecutingfunction0-1$
			}
			throw new InterpretException(null, StringUtil.format("Cannot evaluate function \'{0}\'", functionName)); // $NLX-JdbcFunctions.Cannotevaluatefunction0-1$
		}
	}

	// ============================================================================
	// Utilities
	// ============================================================================

	protected static Connection getConnection(FBSValue connection) throws SQLException, InterpretException {
		FBSValue _c = connection;
		if (_c.isString()) {
			return JdbcUtil.createNamedConnection(FacesContext.getCurrentInstance(), _c.stringValue());
		} else {
			return (Connection) _c.toJavaObject(Connection.class);
		}
	}

	protected static void initParameters(PreparedStatement st, FBSValue params) throws SQLException, InterpretException {
		initParameters(st, params, 0);
	}

	protected static void initParameters(PreparedStatement st, FBSValue params, int offset) throws SQLException, InterpretException {
		if (params != null) {
			int count = params.getArrayLength();
			for (int i = 0; i < count; i++) {
				FBSValue v = params.getArrayValue(i);
				Object o = v.toJavaObject();
				st.setObject(i + offset + 1, o);
			}
		}
	}

	private static class SQLValues {
		Map<String, Object> namedValues;
		List<Object> values;

		// Calculate the parameters
		// If it is an array, then the insert/update will correspond to *
		// If it is a map, then the entry will be the column name/values
		SQLValues(FBSValue params) throws SQLException, InterpretException {
			if (params instanceof ObjectObject) {
				FBSDefaultObject o = (FBSDefaultObject) params;
				namedValues = new HashMap<String, Object>();
				for (Iterator<JSProperty> it = o.getPropertyIterator(); it.hasNext();) {
					JSProperty p = it.next();
					String name = p.getPropertyName();
					Object value = p.getValue().toJavaObject();
					namedValues.put(name, value);
				}
			} else if (params instanceof ArrayObject) {
				ArrayObject o = (ArrayObject) params;
				int n = o.getArrayLength();
				values = new ArrayList<Object>(n);
				for (int i = 0; i < n; i++) {
					FBSValue v = o.getArrayValue(i);
					values.add(v.toJavaObject());
				}
			} else {
				Object o = params.toJavaObject();
				if (o != null) {
					if (o instanceof Map) {
						namedValues = (Map) o;
					} else if (o.getClass().isArray()) {
						int n = Array.getLength(o);
						values = new ArrayList<Object>(n);
						for (int i = 0; i < n; i++) {
							values.add(Array.get(o, i));
						}
					} else if (o instanceof List) {
						values = (List) o;
					}
				}
			}
		}
	}

	protected static List<Object> initInsertValues(StringBuilder b, FBSValue params, Boolean uCase) throws SQLException, InterpretException {
		SQLValues sqlValues = new SQLValues(params);
		// In case of an array
		if (sqlValues.values != null) {
			return sqlValues.values;
		}
		if (sqlValues.namedValues != null) {
			boolean first = true;
			sqlValues.values = new ArrayList<Object>(sqlValues.namedValues.size());
			b.append(" (");
			for (Map.Entry<String, Object> e : sqlValues.namedValues.entrySet()) {
				if (!first) {
					b.append(',');
				} else {
					first = false;
				}
				JdbcUtil.appendColumnName(b, e.getKey(), uCase);
				sqlValues.values.add(e.getValue());
			}
			b.append(')');
			return sqlValues.values;
		}

		throw new SQLException(StringUtil.format("No valid values passed to the {0} statement", "INSERT")); // $NLX-JdbcFunctions.Novalidvaluespassedtothe0statemen-1$
																											// $NON-NLS-2$
	}

	protected static List<Object> initUpdateValues(StringBuilder b, FBSValue params, Boolean uCase) throws SQLException, InterpretException {
		SQLValues sqlValues = new SQLValues(params);
		if (sqlValues.namedValues != null) {
			boolean first = true;
			sqlValues.values = new ArrayList<Object>(sqlValues.namedValues.size());
			b.append(" SET "); // $NON-NLS-1$
			for (Map.Entry<String, Object> e : sqlValues.namedValues.entrySet()) {
				if (!first) {
					b.append(',');
				} else {
					first = false;
				}
				JdbcUtil.appendColumnName(b, e.getKey(), uCase);
				b.append("=?");
				sqlValues.values.add(e.getValue());
			}
			return sqlValues.values;
		}

		throw new SQLException(StringUtil.format("No valid values passed to the {0} statement", "UPDATE")); // $NLX-JdbcFunctions.Novalidvaluespassedtothe0statemen.1-1$
																											// $NON-NLS-2$
	}

	protected static FBSValue doInsert(FBSValueVector args, IExecutionContext context, Boolean uCase) throws SQLException,
			InterpretException {
		if (args.size() >= 2) {
			Connection c = getConnection(args.get(0));
			String tbName = args.get(1).stringValue();
			FBSValue values = args.get(2);
			FBSValue idColumnNames = args.size() > 3 && !args.get(3).isNull() ? args.get(3) : null;
			;

			StringBuilder b = new StringBuilder();
			b.append("INSERT INTO "); // $NON-NLS-1$
			JdbcUtil.appendTableName(b, tbName);
			List<Object> v = initInsertValues(b, values, uCase);
			b.append(" VALUES("); // $NON-NLS-1$
			for (int i = 0; i < v.size(); i++) {
				if (i != 0) {
					b.append(',');
				}
				b.append('?');
			}
			b.append(")");
			String sql = b.toString();
			PreparedStatement st = null;
			if (idColumnNames == null) {
				st = c.prepareStatement(sql);
			} else {
				List<Object> vNames = initInsertValues(null, idColumnNames, uCase);
				String[] columnNames = new String[vNames.size()];
				for (int i = 0; i < vNames.size(); i++) {
					columnNames[i] = (String) vNames.get(i);
				}
				st = c.prepareStatement(sql, columnNames);
			}
			try {
				for (int i = 0; i < v.size(); i++) {
					st.setObject(i + 1, v.get(i));
				}
				int count = st.executeUpdate();
				if (idColumnNames != null) {
					ResultSet rs = st.getGeneratedKeys();
					if (rs.next()) {
						Object value = rs.getBigDecimal(1);
						return FBSUtility.wrap(context.getJSContext(), value);
					}
				}
				return FBSUtility.wrap(context.getJSContext(), count);
			} finally {
				st.close();
			}
		}
		return null;
	}

	public static FBSValue doUpdate(FBSValueVector args, IExecutionContext context, Boolean uCase) throws SQLException, InterpretException {
		if (args.size() >= 3) {
			Connection c = getConnection(args.get(0));
			String tbName = args.get(1).stringValue();
			FBSValue values = args.get(2);
			String where = args.size() >= 3 && !args.get(3).isNull() ? args.get(3).stringValue() : null;
			FBSValue params = args.size() >= 4 && !args.get(4).isNull() ? args.get(4) : null;

			StringBuilder b = new StringBuilder();
			b.append("UPDATE "); // $NON-NLS-1$
			JdbcUtil.appendTableName(b, tbName);
			List<Object> v = initUpdateValues(b, values, uCase);
			if (StringUtil.isNotEmpty(where)) {
				b.append(" WHERE "); // $NON-NLS-1$
				b.append(where);
			}
			String sql = b.toString();
			PreparedStatement st = c.prepareStatement(sql);
			try {
				for (int i = 0; i < v.size(); i++) {
					st.setObject(i + 1, v.get(i));
				}
				if (params != null) {
					initParameters(st, params, v.size());
				}
				int count = st.executeUpdate();
				return FBSUtility.wrap(context.getJSContext(), count);
			} finally {
				st.close();
			}
		}
		return null;
	}
}