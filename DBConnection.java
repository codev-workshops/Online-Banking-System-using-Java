/******************************************************************************
 *	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
 *	Date: September, 2012													  *
 *******************************************************************************/

package com.mishra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opens JDBC connections to the banking database.
 *
 * <p>Settings come from environment variables or system properties: {@code DB_URL} (a JDBC URL
 * without credentials), {@code DB_USER} and {@code DB_PASSWORD}. Without {@code DB_URL} it falls
 * back to a local SQL Server using Windows integrated authentication.
 */
public class DBConnection {
  private static final Logger LOG = Logger.getLogger(DBConnection.class.getName());
  private static final String DEFAULT_URL =
      "jdbc:sqlserver://127.0.0.1:1433;databaseName=JavaClass;integratedSecurity=true;";

  private Connection connection;

  /** Opens a connection, or returns {@code null} if the database is unreachable. */
  public Connection openConn() {
    String url = setting("DB_URL", DEFAULT_URL);
    String user = setting("DB_USER", null);
    String password = setting("DB_PASSWORD", "");
    if (url.toLowerCase().contains("password=")) {
      LOG.warning("DB_URL contains a password; pass it in DB_PASSWORD instead");
    }
    try {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      connection =
          user == null
              ? DriverManager.getConnection(url)
              : DriverManager.getConnection(url, user, password);
    } catch (ClassNotFoundException | SQLException e) {
      LOG.log(Level.SEVERE, "Could not open the database connection", e);
      connection = null;
    }
    return connection;
  }

  /** Opens a connection, throwing instead of returning {@code null} when that fails. */
  public Connection requireConn() throws SQLException {
    Connection conn = openConn();
    if (conn == null) {
      throw new SQLException("Database connection unavailable");
    }
    return conn;
  }

  /** Closes the connection opened by {@link #openConn()}. */
  public void closeConn() {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      LOG.log(Level.WARNING, "Can't close the database connection", e);
    }
  }

  /** Returns the current row of {@code rs} as a vector of column values. */
  public Vector getNextRow(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
    Vector currentRow = new Vector();
    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
      switch (rsmd.getColumnType(i)) {
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
          currentRow.addElement(rs.getString(i));
          break;
        case Types.INTEGER:
          currentRow.addElement(Long.valueOf(rs.getLong(i)));
          break;
        case Types.DOUBLE:
          currentRow.addElement(Double.valueOf(rs.getDouble(i)));
          break;
        case Types.FLOAT:
          currentRow.addElement(Float.valueOf(rs.getFloat(i)));
          break;
        default:
          LOG.fine("Type was: " + rsmd.getColumnTypeName(i));
      }
    }
    return currentRow;
  }

  /** Logs a failed database operation server-side, including chained exceptions. */
  static void logSqlException(Logger log, String operation, SQLException e) {
    for (SQLException next = e; next != null; next = next.getNextException()) {
      log.log(
          Level.SEVERE,
          operation
              + " failed (SQLState "
              + next.getSQLState()
              + ", vendor code "
              + next.getErrorCode()
              + ")",
          next);
    }
  }

  private static String setting(String name, String fallback) {
    String value = System.getenv(name);
    if (value == null || value.isEmpty()) {
      value = System.getProperty(name);
    }
    return value == null || value.isEmpty() ? fallback : value;
  }
}
