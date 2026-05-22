/******************************************************************************
*	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
*	Date: September, 2012													  *
*******************************************************************************/

package com.mishra;

import java.util.*;
import java.sql.*;

public class DBConnection {

    private Connection connection;
    private String URL;

    public DBConnection() {
        URL = System.getProperty("db.url", "jdbc:h2:./data/bankingdb;AUTO_SERVER=TRUE");
        connection = null;
    }

    public Connection openConn() {
        try {
            Class.forName("org.h2.Driver");
            String user = System.getProperty("db.user", "sa");
            String password = System.getProperty("db.password", "");
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
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
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
	              case Types.REAL:
	              	   currentRow.addElement(new Float(rs.getFloat(i)));
	                   break;
	              default:
	                   System.out.println("Type was: "+ rsmd.getColumnTypeName(i));
	          }

	          return currentRow;
    }
}
