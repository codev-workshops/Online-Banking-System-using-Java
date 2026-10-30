/******************************************************************************
 *	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
 *	Date: October, 2013													      *
 *******************************************************************************/
package com.mishra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/** A customer's checking account. */
public class CheckingAccount {
  private static final Logger LOG = Logger.getLogger(CheckingAccount.class.getName());

  private String checkingAccountNumber;
  private String customerName;
  private String customerId;
  private float balance = -1;
  private float amount = -1;

  /** Creates an account with an opening or transaction amount. */
  public CheckingAccount(String caNum, String custName, String custId, String amt) {
    checkingAccountNumber = caNum;
    customerName = custName;
    customerId = custId;
    amount = Float.parseFloat(amt);
  }

  /** Creates a handle to an existing account. */
  public CheckingAccount(String caNum) {
    checkingAccountNumber = caNum;
  }

  /** Creates an empty handle for looking up account numbers. */
  public CheckingAccount() {}

  /** Opens the account if its number is not taken yet. */
  public boolean openAcct() {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement find =
            conn.prepareStatement(
                "SELECT CheckingAccountNumber FROM CheckingAccount WHERE CheckingAccountNumber"
                    + " = ?")) {
      find.setString(1, checkingAccountNumber);
      try (ResultSet rslt = find.executeQuery()) {
        if (rslt.next()) {
          return false;
        }
      }
      try (PreparedStatement insert =
          conn.prepareStatement(
              "INSERT INTO CheckingAccount(CheckingAccountNumber, CustomerName, Balance,"
                  + " CustomerID) VALUES (?, ?, ?, ?)")) {
        insert.setString(1, checkingAccountNumber);
        insert.setString(2, customerName);
        insert.setFloat(3, amount);
        insert.setString(4, customerId);
        return insert.executeUpdate() == 1;
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "openAcct", e);
      return false;
    }
  }

  /** Returns the account number owned by customer {@code cId}, or {@code null}. */
  public String getAccno(String cId) {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement(
                "SELECT CheckingAccountNumber FROM CheckingAccount WHERE CustomerID = ?")) {
      stmt.setString(1, cId);
      try (ResultSet rslt = stmt.executeQuery()) {
        while (rslt.next()) {
          checkingAccountNumber = rslt.getString("CheckingAccountNumber");
        }
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "getAccno", e);
    }
    return checkingAccountNumber;
  }

  /** Returns this account's balance. */
  public float getBalance() {
    return getBalance(checkingAccountNumber);
  }

  /** Returns the balance of account {@code acctNumber}. */
  public float getBalance(String acctNumber) {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement(
                "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = ?")) {
      stmt.setString(1, acctNumber);
      try (ResultSet rslt = stmt.executeQuery()) {
        while (rslt.next()) {
          balance = rslt.getFloat(1);
        }
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "getBalance", e);
    }
    return balance;
  }

  /** Adds the amount to this account if customer {@code cId} owns it. */
  public boolean deposit(String cId) {
    if (isBlank(checkingAccountNumber) || isBlank(customerId) || amount <= 0) {
      return false;
    }
    Float current = ownedBalance(cId);
    return current != null && updateBalance(cId, current + amount);
  }

  /** Takes the amount from this account if customer {@code cId} owns it and funds suffice. */
  public boolean Withdraw(String cId) {
    if (isBlank(checkingAccountNumber) || isBlank(customerId) || amount <= 0) {
      return false;
    }
    Float current = ownedBalance(cId);
    return current != null && current >= amount && updateBalance(cId, current - amount);
  }

  private Float ownedBalance(String cId) {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement(
                "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = ? AND CustomerID"
                    + " = ?")) {
      stmt.setString(1, checkingAccountNumber);
      stmt.setString(2, cId);
      try (ResultSet rslt = stmt.executeQuery()) {
        if (!rslt.next()) {
          return null;
        }
        balance = rslt.getFloat(1);
        return balance;
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "read balance", e);
      return null;
    }
  }

  private boolean updateBalance(String cId, float newBalance) {
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement(
                "UPDATE CheckingAccount SET Balance = ? WHERE CheckingAccountNumber = ? AND"
                    + " CustomerID = ?")) {
      stmt.setFloat(1, newBalance);
      stmt.setString(2, checkingAccountNumber);
      stmt.setString(3, cId);
      if (stmt.executeUpdate() != 1) {
        return false;
      }
      balance = newBalance;
      return true;
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "update balance", e);
      return false;
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isEmpty();
  }
}
