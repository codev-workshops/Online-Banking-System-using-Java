package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckingAccountTest {

  private static final float DELTA = 0.001f;

  @BeforeEach
  void setUp() throws Exception {
    DatabaseTestSupport.clearAllTables();
    assertTrue(new CheckingAccount("C-1001", "Alice", "alice", "100").openAcct());
  }

  @Test
  void openAcctStoresOpeningBalance() {
    assertEquals(100f, new CheckingAccount("C-1001").getBalance(), DELTA);
    assertEquals("C-1001", new CheckingAccount().getAccno("alice"));
  }

  @Test
  void openAcctRejectsDuplicateAccountNumber() {
    assertFalse(new CheckingAccount("C-1001", "Alice", "alice", "50").openAcct());
  }

  @Test
  void depositAddsToBalance() {
    assertTrue(new CheckingAccount("C-1001", "Alice", "alice", "25.5").deposit("alice"));

    assertEquals(125.5f, new CheckingAccount().getBalance("C-1001"), DELTA);
  }

  @Test
  void withdrawSubtractsFromBalance() {
    assertTrue(new CheckingAccount("C-1001", "Alice", "alice", "40").Withdraw("alice"));

    assertEquals(60f, new CheckingAccount("C-1001").getBalance(), DELTA);
  }

  @Test
  void withdrawMoreThanBalanceLeavesBalanceUnchanged() {
    new CheckingAccount("C-1001", "Alice", "alice", "500").Withdraw("alice");

    assertEquals(100f, new CheckingAccount("C-1001").getBalance(), DELTA);
  }
}
