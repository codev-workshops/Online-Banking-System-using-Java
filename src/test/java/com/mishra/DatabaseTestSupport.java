package com.mishra;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Helpers for tests running against the in-memory H2 database configured in the pom. */
final class DatabaseTestSupport {

  private DatabaseTestSupport() {}

  static void clearAllTables() throws SQLException {
    DBConnection db = new DBConnection();
    try (Connection conn = db.openConn();
        Statement stmt = conn.createStatement()) {
      stmt.executeUpdate("DELETE FROM Account");
      stmt.executeUpdate("DELETE FROM CheckingAccount");
      stmt.executeUpdate("DELETE FROM SavingAccount");
      stmt.executeUpdate("DELETE FROM Transactions");
    }
  }
}
