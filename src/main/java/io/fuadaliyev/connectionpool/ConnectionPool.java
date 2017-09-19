package io.fuadaliyev.connectionpool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionPool {

	static final String driver = "com.mysql.jdbc.Driver";
	static final String url = "jdbc:mysql://localhost:3306/info";

	// Credentials
	static final String user = "root";
	static final String password = "fuadaliyev";

	private static GenericObjectPool gPool = null;

	@SuppressWarnings("unused")
	public DataSource setUpPool() throws Exception {
		Class.forName(driver);

		// Instance of GenericObjectPool that holds our connections pool object
		gPool = new GenericObjectPool();
		gPool.setMaxActive(5);

		// Creates a ConnectionFactory Object Which
		// Will Be Use by the Pool to Create the Connection Object!
		ConnectionFactory cf = new DriverManagerConnectionFactory(url, user, password);

		// Creates a PoolableConnectionFactory That Will Wraps
		// the Connection Object Created by the ConnectionFactory
		// to Add Object Pooling Functionality!
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);

		return new PoolingDataSource(gPool);
	}

	public GenericObjectPool getConnectionPool() {
		return gPool;
	}

	// This method is used to print the Connection Pool Status
	private void printDbStatus() {
		System.out.println("Max.: " + getConnectionPool().getMaxActive() + "; Active: "
				+ getConnectionPool().getNumActive() + "; Idle: " + getConnectionPool().getNumIdle());
	}

	public static void main(String[] args) {
		ResultSet rsObj = null;
		Connection connObj = null;
		PreparedStatement pstmtObj = null;
		ConnectionPool jdbcObj = new ConnectionPool();

		try {
			DataSource dataSource = jdbcObj.setUpPool();
			jdbcObj.printDbStatus();

			// Performing Database Operation
			System.out.println("\n====Making a New Connection Object For DB Transaction====\n");
			connObj = dataSource.getConnection();
			jdbcObj.printDbStatus();

			pstmtObj = connObj.prepareStatement("SELECT * FROM user");
			rsObj = pstmtObj.executeQuery();
			while (rsObj.next()) {
				System.out.println("Name: " + rsObj.getString("name"));
			}
			System.out.println("\n====Releasing Connectino Object To Pool====\n");
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				// Closing ResultSet Object
				if (rsObj != null) {
					rsObj.close();
				}
				// Closing PreparedStatement Object
				if (pstmtObj != null) {
					pstmtObj.close();
				}
				// Closing Connection Object
				if (connObj != null) {
					connObj.close();
				}
			} catch (Exception sqlException) {
				sqlException.printStackTrace();
			}
		}
		jdbcObj.printDbStatus();
	}
}
