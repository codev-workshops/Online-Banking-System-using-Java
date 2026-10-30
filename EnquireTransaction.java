package com.mishra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

/** Looks up a customer's transactions in a date range. */
public class EnquireTransaction {
  private static final Logger LOG = Logger.getLogger(EnquireTransaction.class.getName());

  /**
   * Returns {@code [columnNames, rows]} for {@code uname}'s transactions between the two dates
   * ({@code yyyy-MM-dd}).
   */
  public Vector[] FetchTransaction(String uname, String startDate, String endDate) {
    Vector columnNames = new Vector();
    Vector rows = new Vector();
    if (uname != null
        && startDate != null
        && endDate != null
        && !startDate.isEmpty()
        && !endDate.isEmpty()) {
      columnNames.addElement("Trans_Number");
      columnNames.addElement("Trans_Amount");
      columnNames.addElement("Trans_Time");
      columnNames.addElement("Trans_Date");
      columnNames.addElement("From_Acc");
      columnNames.addElement("To_Acc");
      columnNames.addElement("Trans_Type");

      DBConnection db = new DBConnection();
      try (Connection conn = db.requireConn();
          PreparedStatement stmt =
              conn.prepareStatement(
                  "SELECT TransactionNumber, TransactionAmount, TransactionTime, TransactionDate,"
                      + " FromAccount, ToAccount, TransactionType FROM Transactions"
                      + " WHERE CustomerID = ? AND TransactionDate BETWEEN ? AND ?")) {
        stmt.setString(1, uname);
        stmt.setString(2, startDate);
        stmt.setString(3, endDate);
        try (ResultSet rslt = stmt.executeQuery()) {
          ResultSetMetaData rsmd = rslt.getMetaData();
          while (rslt.next()) {
            rows.addElement(db.getNextRow(rslt, rsmd));
          }
        }
      } catch (SQLException e) {
        DBConnection.logSqlException(LOG, "FetchTransaction", e);
      }
    }
    return new Vector[] {columnNames, rows};
  }
}
