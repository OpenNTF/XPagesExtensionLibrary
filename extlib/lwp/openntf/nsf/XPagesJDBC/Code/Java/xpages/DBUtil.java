package xpages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.commons.Platform;
import com.ibm.xsp.extlib.jdbc.dbhelper.DatabaseHelper;
import com.ibm.xsp.extlib.util.JdbcUtil;

public class DBUtil {
	
	public static final String BEAN_NAME = "dbUtil";

	// Look for the managed bean
	public static DBUtil get() {
		FacesContext context = FacesContext.getCurrentInstance();
		DBUtil bean = (DBUtil)context.getApplication().getVariableResolver().resolveVariable(context, BEAN_NAME);
		return bean;
	}
	
	private String connectionName;
	
	private transient DatabaseHelper helper;
	private transient Connection connection;
	
	public DBUtil() {
		this.connectionName = "derby1";
	}
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public Connection getConnection() throws SQLException {
		if(connection==null) {
			connection = JdbcUtil.getConnection(FacesContext.getCurrentInstance(),connectionName);
		}
		return connection;
	}
	
	public DatabaseHelper getHelper() throws SQLException {
		if(helper==null) {
			helper = DatabaseHelper.findHelper(getConnection());
		}
		return helper;
	}
	
	public String getSchema() {
		return "PHIL";
	}
	
	public boolean isDatabaseInitialized() throws SQLException {
		try {
			return JdbcUtil.tableExists(getConnection(), getSchema());
		} catch(Exception ex) {
			Platform.getInstance().log(ex);
			return false;
		}
	}
	
	public List<String> listTables() {
		try {
			return JdbcUtil.listTables(getConnection(), getSchema(), null);
		} catch(Exception ex) {
			Platform.getInstance().log(ex);
			return Collections.<String>emptyList();
		}
	}
}
