package com.mishra;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckingAccountTest {

    @Test
    void constructorFourArgs() {
        assertDoesNotThrow(() -> new CheckingAccount("C1", "John", "cust1", "500"));
    }

    @Test
    void constructorOneArg() {
        assertDoesNotThrow(() -> new CheckingAccount("C1"));
    }

    @Test
    void constructorNoArgs() {
        assertDoesNotThrow(() -> new CheckingAccount());
    }

    @Test
    void openAcctSuccess() throws Exception {
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
            CheckingAccount ca = new CheckingAccount("C100", "John", "cust1", "500");
            assertTrue(ca.openAcct());
        }
    }

    @Test
    void openAcctDuplicate() throws Exception {
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
            CheckingAccount ca = new CheckingAccount("C100", "John", "cust1", "500");
            assertFalse(ca.openAcct());
        }
    }

    @Test
    void getBalanceReturnsBalance() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getFloat(1)).thenReturn(1000.0f);
                })) {
            CheckingAccount ca = new CheckingAccount("C100");
            assertEquals(1000.0f, ca.getBalance());
        }
    }

    @Test
    void getBalanceNoResult() throws Exception {
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
            CheckingAccount ca = new CheckingAccount("C100");
            assertEquals(-1, ca.getBalance());
        }
    }

    @Test
    void getBalanceOverloaded() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getFloat(1)).thenReturn(2000.0f);
                })) {
            CheckingAccount ca = new CheckingAccount();
            assertEquals(2000.0f, ca.getBalance("C200"));
        }
    }

    @Test
    void getAccnoReturnsAccountNumber() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getString("CheckingAccountNumber")).thenReturn("C100");
                })) {
            CheckingAccount ca = new CheckingAccount();
            assertEquals("C100", ca.getAccno("cust1"));
        }
    }

    @Test
    void depositSuccess() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getFloat(1)).thenReturn(1000.0f);
                })) {
            CheckingAccount ca = new CheckingAccount("C100", "John", "cust1", "500");
            assertTrue(ca.deposit("cust1"));
        }
    }

    @Test
    void depositEmptyAccountNumber() {
        CheckingAccount ca = new CheckingAccount("", "John", "", "500");
        assertFalse(ca.deposit("cust1"));
    }

    @Test
    void withdrawSufficientBalance() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getFloat(1)).thenReturn(1000.0f);
                })) {
            CheckingAccount ca = new CheckingAccount("C100", "John", "cust1", "500");
            assertTrue(ca.Withdraw("cust1"));
        }
    }

    @Test
    void withdrawInsufficientBalance() throws Exception {
        try (MockedConstruction<DBConnection> mocked = Mockito.mockConstruction(DBConnection.class,
                (mock, context) -> {
                    Connection mockConn = Mockito.mock(Connection.class);
                    Statement mockStmt = Mockito.mock(Statement.class);
                    ResultSet mockRs = Mockito.mock(ResultSet.class);
                    when(mock.openConn()).thenReturn(mockConn);
                    when(mockConn.createStatement()).thenReturn(mockStmt);
                    when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
                    when(mockRs.next()).thenReturn(true, false);
                    when(mockRs.getFloat(1)).thenReturn(100.0f);
                })) {
            CheckingAccount ca = new CheckingAccount("C100", "John", "cust1", "500");
            assertTrue(ca.Withdraw("cust1"));
        }
    }

    @Test
    void withdrawEmptyFields() {
        CheckingAccount ca = new CheckingAccount("", "John", "", "500");
        assertFalse(ca.Withdraw("cust1"));
    }
}
