package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountTest {

  @BeforeEach
  void setUp() throws Exception {
    DatabaseTestSupport.clearAllTables();
  }

  @Test
  void signUpThenSignInReturnsCustomerName() {
    assertTrue(new Account("alice", "secret", "secret", "Alice Smith").signUp());

    assertEquals("Alice Smith", new Account("alice", "secret").signIn());
  }

  @Test
  void signUpRejectsMismatchedPasswords() {
    assertFalse(new Account("bob", "one", "two", "Bob").signUp());
  }

  @Test
  void signUpRejectsDuplicateUsername() {
    assertTrue(new Account("carol", "pw", "pw", "Carol").signUp());

    assertFalse(new Account("carol", "pw", "pw", "Carol Again").signUp());
  }

  @Test
  void signInWithWrongPasswordReturnsNoName() {
    new Account("dave", "right", "right", "Dave").signUp();

    assertNull(new Account("dave", "wrong").signIn());
  }

  @Test
  void changePasswordRequiresCurrentPassword() {
    new Account("erin", "old", "old", "Erin").signUp();

    assertFalse(new Account("erin", "bad").changePassword("new"));
    assertTrue(new Account("erin", "old").changePassword("new"));
    assertEquals("Erin", new Account("erin", "new").signIn());
  }
}
