package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @BeforeEach
  void setUp() throws Exception {
    DatabaseTestSupport.clearAllTables();
  }

  @Test
  void recordedTransactionIsFoundByDateRangeSearch() {
    String number =
        new Transaction("C-1001", "S-2001", "alice", "42.0", "Transfer").recordTransaction();
    assertNotNull(number);

    String today = LocalDate.now().toString();
    Vector<?> results = new Transaction(today, today).searchTransaction("alice");

    assertEquals(1, results.size());
    Vector<?> row = (Vector<?>) results.get(0);
    assertEquals(number, row.get(0));
    assertEquals("Transfer", row.get(2));
    assertEquals("S-2001", row.get(5));
    assertEquals("C-1001", row.get(6));
  }

  @Test
  void searchOutsideDateRangeFindsNothing() {
    new Transaction("C-1001", "S-2001", "alice", "10", "Deposit").recordTransaction();

    assertTrue(new Transaction("2000-01-01", "2000-01-31").searchTransaction("alice").isEmpty());
  }
}
