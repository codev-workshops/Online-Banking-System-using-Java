package com.mishra;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {
  @Test
  void hashVerifiesOnlyTheOriginalPassword() {
    String hash = PasswordUtil.hash("s3cret!");
    assertTrue(PasswordUtil.verify("s3cret!", hash));
    assertFalse(PasswordUtil.verify("S3cret!", hash));
    assertFalse(PasswordUtil.verify("", hash));
  }

  @Test
  void hashIsSaltedAndNeverContainsThePassword() {
    String first = PasswordUtil.hash("s3cret!");
    assertNotEquals(first, PasswordUtil.hash("s3cret!"));
    assertFalse(first.contains("s3cret!"));
    assertTrue(first.startsWith("pbkdf2_sha256$"));
    assertTrue(first.length() <= 255);
  }

  @Test
  void legacyPlaintextAndMalformedValuesNeverVerify() {
    assertFalse(PasswordUtil.verify("pw123", "pw123"));
    assertFalse(PasswordUtil.verify("pw123", null));
    assertFalse(PasswordUtil.verify(null, PasswordUtil.hash("pw123")));
    assertFalse(PasswordUtil.verify("pw123", "pbkdf2_sha256$abc$!!$!!"));
    assertFalse(PasswordUtil.verify("pw123", "pbkdf2_sha256$0$AAAA$AAAA"));
  }
}
