package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditLogTest {
  private final Logger logger = Logger.getLogger(AuditLog.LOGGER_NAME);
  private final List<LogRecord> records = new ArrayList<>();
  private final Handler capture =
      new Handler() {
        @Override
        public void publish(LogRecord logRecord) {
          records.add(logRecord);
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
      };

  @BeforeEach
  void attach() {
    logger.addHandler(capture);
    logger.setUseParentHandlers(false);
  }

  @AfterEach
  void detach() {
    logger.removeHandler(capture);
    logger.setUseParentHandlers(true);
  }

  @Test
  void successIsLoggedAtInfoWithBalanceAndReference() {
    assertTrue(AuditLog.success(AuditLog.Event.DEPOSIT, "alice", "checking:C1", 5f, 105f, "4711"));
    assertEquals(Level.INFO, records.get(0).getLevel());
    assertEquals(
        "event=DEPOSIT outcome=SUCCESS customer=\"alice\" account=\"checking:C1\" amount=5.00"
            + " balance=105.00 ref=\"4711\"",
        records.get(0).getMessage());
  }

  @Test
  void failureIsLoggedAtWarningWithReason() {
    assertFalse(
        AuditLog.failure(
            AuditLog.Event.WITHDRAWAL, "alice", "saving:S1", 50f, "insufficient funds"));
    assertEquals(Level.WARNING, records.get(0).getLevel());
    assertEquals(
        "event=WITHDRAWAL outcome=FAILURE customer=\"alice\" account=\"saving:S1\" amount=50.00"
            + " reason=\"insufficient funds\"",
        records.get(0).getMessage());
  }

  @Test
  void userInputCannotForgeFieldsOrLines() {
    AuditLog.failure(
        AuditLog.Event.DEPOSIT, "x\" outcome=SUCCESS\nevent=DEPOSIT", null, 1f, "bad\\input");
    String line = records.get(0).getMessage();
    assertFalse(line.contains("\n"));
    assertEquals(
        "event=DEPOSIT outcome=FAILURE customer=\"x\\\" outcome=SUCCESS_event=DEPOSIT\" account=-"
            + " amount=1.00 reason=\"bad\\\\input\"",
        line);
  }
}
