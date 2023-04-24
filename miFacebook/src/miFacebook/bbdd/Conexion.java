package miFacebook.bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion implements AutoCloseable{
	private String server, schema, user, password, port; 
	
	private static Connection conn = null;
	
	protected Conexion(String server, String schema, String port, String user, String password) {
		this.server = server;
		this.schema = schema;
		this.user = user;
		this.password = password;
		this.port = port;
	}
	
	private void openConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				String connectionString = "jdbc:mysql://" + server + ":" + port + "/" + schema + 
						"?useJDBCCompliantTimezoneShift=true&serverTimezone=UTC";
				
				try {
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					conn = DriverManager.getConnection(connectionString,user,password);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void closeConnection() {
		try {
			if (conn!=null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected Statement getStatement() throws SQLException {
		openConnection();
		return conn.createStatement();
	}

	@Override
	public void close() throws Exception {
		closeConnection();
	}

	protected PreparedStatement prepareStatement(String sqlQuery) throws SQLException {
		openConnection();
		return conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	}

}
