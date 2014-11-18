package xpages;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.jdbc.dbhelper.DatabaseHelper;
import com.ibm.xsp.extlib.util.JdbcUtil;

import extlib.SampleDataUtil;

public class DataInitializer {

	private DBUtil dbUtil;
	
	public DataInitializer() {
		this.dbUtil = DBUtil.get();
	}
	
	public Connection getConnection() throws SQLException {
		return dbUtil.getConnection();
	}
	
	public DatabaseHelper getHelper() throws SQLException {
		return dbUtil.getHelper();
	}
	

	// ==========================================================================
	// Database initialization
	// ==========================================================================
	
	public void initializeDatabase() throws SQLException, IOException {
		if(dbUtil.isDatabaseInitialized()) {
			deleteTables();
		}
		createTables();
		loadTables();
	}

	private void deleteTables() throws SQLException {
		List<String> l = JdbcUtil.listTables(getConnection(), dbUtil.getSchema(), null);
		if(l!=null && !l.isEmpty()) {
			ArrayList<String> sql = new ArrayList<String>();
			for(String t: l) {
				getHelper().addDropTable(sql, t);
			}
			getHelper().sendBatch(getConnection(), sql);
		}
	}
	
	private void createTables() throws SQLException {
		switch(getHelper().getType()) {
			case DERBY: {
				createTables_Derby();
			} break;
			// Could be used for other than Derby databases initialization if DDL is ok 
//			case GENERIC: {
//				createTables_Derby();
//			} break;
			default: {
				throw new FacesExceptionEx(null,"Unsupported database");
			}
		}
	}

	private void createTables_Derby() throws SQLException {
		Statement st = getConnection().createStatement();
		try {
			String usr = "CREATE TABLE users ("
					    +"id INT NOT NULL,"
					    +"firstname VARCHAR(64),"
					    +"lastname VARCHAR(64),"
					    +"city VARCHAR(64),"
					    +"state VARCHAR(2),"
					    +"CONSTRAINT p_users PRIMARY KEY (id)"
			   		    +")";
			st.execute(usr);
			String states = "CREATE TABLE states ("
			    +"state VARCHAR(2) NOT NULL,"
			    +"label VARCHAR(64),"
			    +"CONSTRAINT p_states PRIMARY KEY (state)"
	   		    +")";
			st.execute(states);
		} finally {
			st.close();
		}
	}

	
	// ==========================================================================
	// Load data
	// ==========================================================================

	public void resetData() throws SQLException, IOException {
		emptyTables();
		loadTables();
	}

	public void emptyTables() throws SQLException {
		Connection c = getConnection();
		Statement st = c.createStatement();
		try {
			st.execute("DELETE from users");
			st.execute("DELETE from states");
		} finally {
			st.close();
		}
	}

	public void loadTables() throws SQLException, IOException {
		Connection c = getConnection();
		PreparedStatement st = c.prepareStatement("INSERT INTO users (id,firstname,lastname,city,state) VALUES (?,?,?,?,?)");
		try {
			String[] firstNames = SampleDataUtil.readFirstNames();
			String[] lastNames = SampleDataUtil.readLastNames();
			String[] cities = SampleDataUtil.readCities();
			for(int i=0; i<128; i++) {
				Integer id = i;
				String firstName = firstNames[(int)(Math.random()*firstNames.length)];
				String lastName = lastNames[(int)(Math.random()*lastNames.length)];
				String fullcity = cities[(int)(Math.random()*cities.length)];
				String city = SampleDataUtil.cityName(fullcity);
				String state = SampleDataUtil.cityState(fullcity);
				createUser(st,id,firstName,lastName,city,state);
			}
		} finally {
			st.close();
		}
		st = c.prepareStatement("INSERT INTO states (state,label) VALUES (?,?)");
		try {
			String[] states = SampleDataUtil.readStates();
			for( int i=0; i<states.length; i++ ) {
				String[] s = StringUtil.splitString(states[i], ',');
				createState(st, s[1], s[0]);
			}
		} finally {
			st.close();
		}
	}
	
	void createUser(PreparedStatement st, Integer id, String firstName, String lastName, String city, String state) throws SQLException {
		st.setInt(1, id);
		st.setString(2, firstName);
		st.setString(3, lastName);
		st.setString(4, city);
		st.setString(5, state);
		st.execute();
	}		
	
	void createState(PreparedStatement st, String state, String label) throws SQLException {
		st.setString(1, state);
		st.setString(2, label);
		st.execute();
	}		
	
}
