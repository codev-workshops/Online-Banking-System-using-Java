/******************************************************************************
*	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
*	Date: September, 2012													  *
*******************************************************************************/

package com.mishra;

import java.util.*;
import java.sql.*;

public class DBConnection {

    private Connection connection;
    private final String URL;
    private final String user;
    private final String password;

    public DBConnection() {
        URL = env("DB_URL", "jdbc:postgresql://localhost:5432/javaclass");
        user = env("DB_USER", "banking");
        password = env("DB_PASSWORD", "banking");
        connection = null;
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    public Connection openConn() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, user, password);
        }
        catch ( Exception e ) {
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }

    public void closeConn() {
        try {
            connection.close();
        }
        catch( Exception e ) {
           System.err.println ("Can't close the database connection.");
        }
    }

     public Vector getNextRow(ResultSet rs,ResultSetMetaData rsmd) throws SQLException
	 {
	     Vector currentRow = new Vector();

	     for(int i=1;i<=rsmd.getColumnCount();i++)
	         switch(rsmd.getColumnType(i))
	         {
	              case Types.VARCHAR:
	              case Types.LONGVARCHAR:
	                   currentRow.addElement(rs.getString(i));
	                   break;
	              case Types.INTEGER:
	                   currentRow.addElement(new Long(rs.getLong(i)));
	                   break;
	              case Types.DOUBLE:
	              	   currentRow.addElement(new Double(rs.getDouble(i)));
	                   break;
	              case Types.FLOAT:
	              	   currentRow.addElement(new Float(rs.getFloat(i)));
	                   break;
	              default:
	                   System.out.println("Type was: "+ rsmd.getColumnTypeName(i));
	          }

	          return currentRow;
    }
}