package com.mishra;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Audit trail for money movements: one line per attempt, successful or not, written to the {@value
 * #LOGGER_NAME} logger, which {@code WEB-INF/classes/logging.properties} routes to {@code
 * logs/banking-audit.<date>.log} under Tomcat.
 *
 * <p>Example: {@code event=WITHDRAWAL outcome=FAILURE customer="alice" account="checking:CHK1"
 * amount=50.00 reason="insufficient funds"}
 */
public final class AuditLog {
  /** Name of the audit logger. */
  public static final String LOGGER_NAME = "com.mishra.audit";

  private static final Logger LOG = Logger.getLogger(LOGGER_NAME);

  /** Audited operations. */
  public enum Event {
    ACCOUNT_OPEN,
    DEPOSIT,
    WITHDRAWAL,
    TRANSACTION_RECORD
  }

  private AuditLog() {}

  /**
   * Records a completed operation and returns {@code true}.
   *
   * @param balance the account balance afterwards, or {@code null} if not applicable
   * @param ref a reference such as the transaction number, or {@code null}
   */
  public static boolean success(
      Event event, String customerId, String account, float amount, Float balance, String ref) {
    StringBuilder line = start(event, "SUCCESS", customerId, account, amount);
    if (balance != null) {
      line.append(" balance=").append(money(balance));
    }
    if (ref != null) {
      line.append(" ref=").append(quote(ref));
    }
    LOG.info(line.toString());
    return true;
  }

  /** Records a rejected or failed operation and returns {@code false}. */
  public static boolean failure(
      Event event, String customerId, String account, float amount, String reason) {
    LOG.warning(
        start(event, "FAILURE", customerId, account, amount)
            .append(" reason=")
            .append(quote(reason))
            .toString());
    return false;
  }

  private static StringBuilder start(
      Event event, String outcome, String customerId, String account, float amount) {
    return new StringBuilder()
        .append("event=")
        .append(event)
        .append(" outcome=")
        .append(outcome)
        .append(" customer=")
        .append(quote(customerId))
        .append(" account=")
        .append(quote(account))
        .append(" amount=")
        .append(money(amount));
  }

  private static String money(float value) {
    return String.format(Locale.ROOT, "%.2f", value);
  }

  /** Quotes a value so user input can't break the line format or forge extra entries. */
  static String quote(String value) {
    if (value == null) {
      return "-";
    }
    String escaped =
        value.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\\p{Cntrl}", "_");
    return "\"" + escaped + "\"";
  }
}
