/******************************************************************************
 *	Program Author: Kavita Mishra for CSCI 6810 Java and the Internet	  *
 *	Date: September, 2018													      *
 *******************************************************************************/
package com.mishra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

/** A deposit, withdrawal or transfer, and lookups over a customer's transaction history. */
public class Transaction {
  private static final Logger LOG = Logger.getLogger(Transaction.class.getName());
  private static final Random RANDOM = new Random();

  private String transactionNumber;
  private String transactionType;
  private String fromAccount;
  private String toAccount;
  private String customerId;
  private float amount = -1;
  private String startdate;
  private String enddate;
  private final Vector transStore = new Vector();

  /** Creates a transaction into {@code accountType} for the given customer. */
  public Transaction(String accountType, String custId, String amt) {
    toAccount = accountType;
    customerId = custId;
    amount = Float.parseFloat(amt);
  }

  /** Creates a transaction between two accounts for the given customer. */
  public Transaction(String toAcc, String fromAcc, String custId, String amt, String transType) {
    toAccount = toAcc;
    fromAccount = fromAcc;
    customerId = custId;
    transactionType = transType;
    amount = Float.parseFloat(amt);
  }

  /** Creates a date-range query; dates are {@code yyyy-MM-dd}. */
  public Transaction(String sd, String ed) {
    startdate = sd;
    enddate = ed;
  }

  /** Saves the transaction and returns its number, or {@code null} if it was not saved. */
  public String recordTransaction() {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement find =
            conn.prepareStatement(
                "SELECT TransactionNumber FROM Transactions WHERE TransactionNumber = ?");
        PreparedStatement insert =
            conn.prepareStatement(
                "INSERT INTO Transactions(TransactionNumber, TransactionType, TransactionAmount,"
                    + " TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
      boolean taken = true;
      while (taken) {
        transactionNumber = Integer.toString(RANDOM.nextInt(9999) + 1000);
        find.setString(1, transactionNumber);
        try (ResultSet rslt = find.executeQuery()) {
          taken = rslt.next();
        }
      }
      insert.setString(1, transactionNumber);
      insert.setString(2, transactionType);
      insert.setFloat(3, amount);
      insert.setString(4, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
      insert.setString(5, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      insert.setString(6, fromAccount);
      insert.setString(7, toAccount);
      insert.setString(8, customerId);
      insert.executeUpdate();
      AuditLog.success(
          AuditLog.Event.TRANSACTION_RECORD,
          customerId,
          auditAccount(),
          amount,
          null,
          transactionType + " #" + transactionNumber);
      return transactionNumber;
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "recordTransaction", e);
      AuditLog.failure(
          AuditLog.Event.TRANSACTION_RECORD, customerId, auditAccount(), amount, "database error");
      return null;
    }
  }

  private String auditAccount() {
    return fromAccount == null ? toAccount : fromAccount + "->" + toAccount;
  }

  /** Returns {@code uName}'s transactions between the start and end dates. */
  public Vector searchTransaction(String uName) {
    if (uName == null || startdate == null || enddate == null) {
      return transStore;
    }
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement(
                "SELECT * FROM Transactions WHERE CustomerID = ?"
                    + " AND TransactionDate BETWEEN ? AND ?")) {
      stmt.setString(1, uName);
      stmt.setString(2, startdate);
      stmt.setString(3, enddate);
      try (ResultSet rslt = stmt.executeQuery()) {
        while (rslt.next()) {
          Vector columnNames = new Vector();
          columnNames.add(0, rslt.getString("TransactionNumber"));
          columnNames.add(1, rslt.getString("TransactionAmount"));
          columnNames.add(2, rslt.getString("TransactionType"));
          columnNames.add(3, rslt.getString("TransactionTime"));
          columnNames.add(4, rslt.getString("TransactionDate"));
          columnNames.add(5, rslt.getString("FromAccount"));
          columnNames.add(6, rslt.getString("ToAccount"));
          transStore.add(columnNames);
        }
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "searchTransaction", e);
    }
    return transStore;
  }
}
