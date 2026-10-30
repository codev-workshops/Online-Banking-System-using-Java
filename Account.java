/******************************************************************************
 *	Program Author: Kavita Mishra for CSCI 6810 Java and the Internet	  *
 *	Date: September, 2018													  *
 *******************************************************************************/

package com.mishra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * A customer login account.
 *
 * <p>Passwords are stored as {@link PasswordUtil} hashes. Rows created before hashing was
 * introduced hold plaintext passwords; those never verify, so the accounts need a forced password
 * reset (or a one-off migration that hashes the existing values) before they can sign in again.
 */
public class Account {
  private static final Logger LOG = Logger.getLogger(Account.class.getName());
  private static final String TIMING_DUMMY_HASH = PasswordUtil.hash("timing-equalization-only");

  private final String username;
  private final String password;
  private String password1;
  private String name;

  /** Creates an account for sign-up. */
  public Account(String un, String passW, String passW1, String nm) {
    username = un;
    password = passW;
    password1 = passW1;
    name = nm;
  }

  /** Creates an account for sign-in or password change. */
  public Account(String un, String passW) {
    username = un;
    password = passW;
  }

  /** Creates the account if the username is free and both passwords match. */
  public boolean signUp() {
    if (isBlank(username) || isBlank(password) || !password.equals(password1)) {
      return false;
    }
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement find =
            conn.prepareStatement("SELECT Username FROM Account WHERE Username = ?")) {
      find.setString(1, username);
      try (ResultSet rslt = find.executeQuery()) {
        if (rslt.next()) {
          return false;
        }
      }
      try (PreparedStatement insert =
          conn.prepareStatement("INSERT INTO Account(Username, Password, Name) VALUES (?, ?, ?)")) {
        insert.setString(1, username);
        insert.setString(2, PasswordUtil.hash(password));
        insert.setString(3, name);
        return insert.executeUpdate() == 1;
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "signUp", e);
      return false;
    }
  }

  /** Returns the customer name if the credentials are valid, otherwise {@code ""}. */
  public String signIn() {
    String storedHash = null;
    String customerName = null;
    if (isBlank(username) || isBlank(password)) {
      return "";
    }
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement("SELECT Name, Password FROM Account WHERE Username = ?")) {
      stmt.setString(1, username);
      try (ResultSet rslt = stmt.executeQuery()) {
        if (rslt.next()) {
          customerName = rslt.getString("Name");
          storedHash = rslt.getString("Password");
        }
      }
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "signIn", e);
      return "";
    }
    if (storedHash == null) {
      // Hash anyway so unknown usernames take as long as wrong passwords.
      PasswordUtil.verify(password, TIMING_DUMMY_HASH);
      return "";
    }
    if (!PasswordUtil.verify(password, storedHash) || customerName == null) {
      return "";
    }
    name = customerName;
    return customerName;
  }

  /** Replaces the password after verifying the current one. */
  public boolean changePassword(String newPassword) {
    if (isBlank(newPassword) || "".equals(signIn())) {
      return false;
    }
    try (Connection conn = new DBConnection().requireConn();
        PreparedStatement stmt =
            conn.prepareStatement("UPDATE Account SET Password = ? WHERE Username = ?")) {
      stmt.setString(1, PasswordUtil.hash(newPassword));
      stmt.setString(2, username);
      return stmt.executeUpdate() == 1;
    } catch (SQLException e) {
      DBConnection.logSqlException(LOG, "changePassword", e);
      return false;
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isEmpty();
  }
}
