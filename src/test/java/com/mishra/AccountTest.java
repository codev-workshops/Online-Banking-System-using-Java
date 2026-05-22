package com.mishra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest extends TestBase {

    @Test
    void testSignUpSuccess() {
        Account account = new Account("testuser", "pass123", "pass123", "Test User");
        assertTrue(account.signUp(), "Sign up should succeed with valid data");
    }

    @Test
    void testSignUpDuplicateUsername() {
        Account account1 = new Account("duplicate", "pass1", "pass1", "User One");
        assertTrue(account1.signUp());

        Account account2 = new Account("duplicate", "pass2", "pass2", "User Two");
        assertFalse(account2.signUp(), "Sign up should fail with duplicate username");
    }

    @Test
    void testSignUpPasswordMismatch() {
        Account account = new Account("user1", "pass1", "pass2", "Test User");
        assertFalse(account.signUp(), "Sign up should fail when passwords don't match");
    }

    @Test
    void testSignUpEmptyUsername() {
        Account account = new Account("", "pass1", "pass1", "Test User");
        assertFalse(account.signUp(), "Sign up should fail with empty username");
    }

    @Test
    void testSignInSuccess() {
        Account signUp = new Account("loginuser", "secret", "secret", "Login User");
        signUp.signUp();

        Account signIn = new Account("loginuser", "secret");
        String name = signIn.signIn();
        assertEquals("Login User", name, "Sign in should return customer name");
    }

    @Test
    void testSignInWrongPassword() {
        Account signUp = new Account("loginuser2", "correct", "correct", "User");
        signUp.signUp();

        Account signIn = new Account("loginuser2", "wrong");
        String name = signIn.signIn();
        assertNull(name, "Sign in should return null for wrong password");
    }

    @Test
    void testChangePassword() {
        Account signUp = new Account("changeuser", "oldpass", "oldpass", "Change User");
        signUp.signUp();

        Account acct = new Account("changeuser", "oldpass");
        assertTrue(acct.changePassword("newpass"), "Change password should succeed");

        Account signIn = new Account("changeuser", "newpass");
        assertEquals("Change User", signIn.signIn(), "Should sign in with new password");
    }
}
