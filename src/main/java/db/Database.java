package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {
	private Connection connection;

	public Database(String connector, String username, String passkey) {
		try {
			connection = DriverManager.getConnection(connector, username, passkey);
		} catch (Exception e) {
			connection = null;
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to close an existing connection to a database, if it is
	 * open
	 * 
	 * @return true, if the connection status is closed; false, otherwise
	 * @throws SQLException
	 */
	public boolean close() throws SQLException {
		boolean closed = false;
		if (connection == null)
			closed = true;
		else if (!connection.isClosed()) {
			connection.close();
			closed = connection.isClosed();
		}
		return closed;
	}

	/**
	 * This method is used read data from a database table
	 * 
	 * @param query      - query to be executed to fetch the data from DB
	 * @param parameters - parameters required to prepare the statement to be
	 *                   executed
	 * @return - List of mappings between column names and column values. Each item
	 *         in the list represents one row from the result-set.
	 * @throws SQLException
	 */
	public List<Map<String, Object>> read(String query, Object... parameters) throws SQLException {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (connection == null)
			data = null;
		else if (connection.isClosed())
			data = null;
		else {
			PreparedStatement ps = connection.prepareStatement(query);
			for (int i = 0; i < parameters.length; i++)
				ps.setObject(i + 1, parameters[i]);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> rowData = new LinkedHashMap<String, Object>();
				for (int i = 0; i < rsmd.getColumnCount(); i++)
					rowData.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));
				data.add(rowData);
			}
			rs.close();
			ps.close();
		}
		return data;
	}

	/**
	 * This method is used to execute a DDL or DML query on a DB table
	 * 
	 * @param query  - the DDL or DML query to be executed
	 * @param params - parameters required to prepare the statement
	 * @return - an integer indicating number of records affected by running the DML
	 *         or DDL Query
	 * @throws SQLException
	 */
	public int write(String query, Object[] params) throws SQLException {
		int recordsAffected = 0;
		if (connection != null) {
			if (!connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement(query);
				if (params != null) {
					for (int i = 0; i < params.length; i++)
						ps.setObject(i + 1, params[i]);
					recordsAffected = ps.executeUpdate();
				}
				ps.close();
			}
		}
		return recordsAffected;
	}

	/**
	 * This method is used to execute a DDL or DML query on a DB Table(multiple
	 * times with different sets of parameters)
	 * 
	 * @param query      - the DDL or DML query to be executed
	 * @param paramsList - ArrayList of parameters required to build the query. Each
	 *                   item in the list represent one set of parameters
	 * @return - array of integers wherein each element indicates number of records
	 *         affected by executing the given DDL or DML query with different set
	 *         of parameters
	 * @throws SQLException
	 */
	public int[] write(String query, List<Object[]> paramsList) throws SQLException {
		int[] affectedRecords = null;
		if (connection != null) {
			if (!connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement(query);
				for (Object[] params : paramsList) {
					for (int i = 0; i < params.length; i++)
						ps.setObject(i + 1, params[i]);
					ps.addBatch();
				}
				affectedRecords = ps.executeBatch();
				ps.close();
			}
		}
		return affectedRecords;
	}

	/**
	 * This method is used to run multiple DDL or DML statements with the capability
	 * of running each query multiple times with different sets of parameters
	 * 
	 * @param queriesAndParamsLists - Mapping between queries and their
	 *                              corresponding Lists of parameters required to
	 *                              prepare statements
	 * @throws SQLException
	 */
	public void write(Map<String, List<Object[]>> queriesAndParamsLists) throws SQLException {
		for (Map.Entry<String, List<Object[]>> queryAndParamsList : queriesAndParamsLists.entrySet())
			write(queryAndParamsList.getKey(), queryAndParamsList.getValue());
	}
}