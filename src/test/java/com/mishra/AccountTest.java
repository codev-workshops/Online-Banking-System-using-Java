package com.mishra;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AccountTest {

    @Test
    void constructorFourArgs() {
        assertDoesNotThrow(() -> new Account("user", "pass", "pass", "Name"));
    }

    @Test
    void constructorTwoArgs() {
        assertDoesNotThrow(() -> new Account("user", "pass"));
    }

    @Test
    void signUpEmptyUsername() {
        Account acct = new Account("", "pass", "pass", "Name");
        assertFalse(acct.signUp());
    }

    @Test
    void signUpEmptyPassword() {
        Account acct = new Account("user", "", "", "Name");
        assertFalse(acct.signUp());
    }

    @Test
    void signUpMismatchedPasswords() {
        Account acct = new Account("user", "pass1", "pass2", "Name");
        assertFalse(acct.signUp());
    }

    @Test
    void signUpSuccess() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(false);
                })) {
            Account acct = new Account("newuser", "pass", "pass", "John");
            assertTrue(acct.signUp());
        }
    }

    @Test
    void signUpDuplicateUsername() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true);
                })) {
            Account acct = new Account("existinguser", "pass", "pass", "John");
            assertFalse(acct.signUp());
        }
    }

    @Test
    void signInEmptyUsername() {
        Account acct = new Account("", "pass");
        assertNull(acct.signIn());
    }

    @Test
    void signInEmptyPassword() {
        Account acct = new Account("user", "");
        assertNull(acct.signIn());
    }

    @Test
    void signInSuccess() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true);
                    when(mockRs.getString(1)).thenReturn("John");
                })) {
            Account acct = new Account("user", "pass");
            assertEquals("John", acct.signIn());
        }
    }

    @Test
    void signInInvalidCredentials() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(false);
                })) {
            Account acct = new Account("user", "wrongpass");
            assertNull(acct.signIn());
        }
    }

    @Test
    void changePasswordSuccess() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true);
                })) {
            Account acct = new Account("user", "oldpass");
            assertTrue(acct.changePassword("newpass"));
        }
    }

    @Test
    void changePasswordWrongCredentials() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(false);
                })) {
            Account acct = new Account("user", "wrongpass");
            assertFalse(acct.changePassword("newpass"));
        }
    }
}
