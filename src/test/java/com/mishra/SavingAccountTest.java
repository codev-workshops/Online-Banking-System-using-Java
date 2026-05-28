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

class SavingAccountTest {

    @Test
    void constructorFourArgs() {
        assertDoesNotThrow(() -> new SavingAccount("S1", "John", "cust1", "500"));
    }

    @Test
    void constructorOneArg() {
        assertDoesNotThrow(() -> new SavingAccount("S1"));
    }

    @Test
    void constructorNoArgs() {
        assertDoesNotThrow(() -> new SavingAccount());
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
            SavingAccount sa = new SavingAccount("S100", "John", "cust1", "500");
            assertTrue(sa.openAcct());
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
            SavingAccount sa = new SavingAccount("S100", "John", "cust1", "500");
            assertFalse(sa.openAcct());
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
            SavingAccount sa = new SavingAccount("S100");
            assertEquals(1000.0f, sa.getBalance());
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
            SavingAccount sa = new SavingAccount("S100");
            assertEquals(-1, sa.getBalance());
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
            SavingAccount sa = new SavingAccount();
            assertEquals(2000.0f, sa.getBalance("S200"));
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
                    when(mockRs.getString("SavingAccountNumber")).thenReturn("S100");
                })) {
            SavingAccount sa = new SavingAccount();
            assertEquals("S100", sa.getAccno("cust1"));
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
            SavingAccount sa = new SavingAccount("S100", "John", "cust1", "500");
            assertTrue(sa.deposit("cust1"));
        }
    }

    @Test
    void depositEmptyAccountNumber() {
        SavingAccount sa = new SavingAccount("", "John", "", "500");
        assertFalse(sa.deposit("cust1"));
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
            SavingAccount sa = new SavingAccount("S100", "John", "cust1", "500");
            assertTrue(sa.Withdraw("cust1"));
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
            SavingAccount sa = new SavingAccount("S100", "John", "cust1", "500");
            assertTrue(sa.Withdraw("cust1"));
        }
    }

    @Test
    void withdrawEmptyFields() {
        SavingAccount sa = new SavingAccount("", "John", "", "500");
        assertFalse(sa.Withdraw("cust1"));
    }
}
