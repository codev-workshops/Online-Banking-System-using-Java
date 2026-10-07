package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SavingAccountTest {

  private static final float DELTA = 0.001f;

  @BeforeEach
  void setUp() throws Exception {
    DatabaseTestSupport.clearAllTables();
    assertTrue(new SavingAccount("S-2001", "Alice", "alice", "200").openAcct());
  }

  @Test
  void openAcctStoresOpeningBalance() {
    assertEquals(200f, new SavingAccount("S-2001").getBalance(), DELTA);
    assertEquals("S-2001", new SavingAccount().getAccno("alice"));
  }

  @Test
  void depositAddsToBalance() {
    assertTrue(new SavingAccount("S-2001", "Alice", "alice", "50").deposit("alice"));

    assertEquals(250f, new SavingAccount().getBalance("S-2001"), DELTA);
  }

  @Test
  void withdrawSubtractsFromBalance() {
    assertTrue(new SavingAccount("S-2001", "Alice", "alice", "75").Withdraw("alice"));

    assertEquals(125f, new SavingAccount("S-2001").getBalance(), DELTA);
  }
}
