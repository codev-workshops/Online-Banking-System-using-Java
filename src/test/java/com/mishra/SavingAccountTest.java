package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SavingAccountTest {

    private DBConnection db;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        db = mock(DBConnection.class);
        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
        when(db.openConn()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    private static void setBalance(SavingAccount a, float value) throws Exception {
        Field f = SavingAccount.class.getDeclaredField("Balance");
        f.setAccessible(true);
        f.setFloat(a, value);
    }

    @Test
    void openAcctInsert() throws Exception {
        when(rs.next()).thenReturn(false);
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.openAcct());
        verify(stmt).executeUpdate(contains("INSERT INTO SavingAccount"));
    }

    @Test
    void openAcctAlreadyExists() throws Exception {
        when(rs.next()).thenReturn(true);
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.openAcct());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void getAccno() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("SavingAccountNumber")).thenReturn("888");
        SavingAccount a = new SavingAccount();
        a.setDbConnection(db);
        assertEquals("888", a.getAccno("cust"));
    }

    @Test
    void getBalanceNoArg() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(500.0f);
        SavingAccount a = new SavingAccount("100");
        a.setDbConnection(db);
        assertEquals(500.0f, a.getBalance());
    }

    @Test
    void getBalanceWithArg() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(600.0f);
        SavingAccount a = new SavingAccount();
        a.setDbConnection(db);
        assertEquals(600.0f, a.getBalance("100"));
    }

    @Test
    void depositHappyPath() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.deposit("cust"));
        verify(stmt).executeUpdate(contains("UPDATE SavingAccount SET Balance = '150.0'"));
    }

    @Test
    void depositEmptyAccountGuard() {
        SavingAccount a = new SavingAccount("", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.deposit("cust"));
        verifyNoInteractions(db);
    }

    @Test
    void withdrawSufficientFunds() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(200.0f);
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.Withdraw("cust"));
        verify(stmt).executeUpdate(contains("UPDATE SavingAccount SET Balance = '150.0'"));
    }

    @Test
    void withdrawInsufficientFunds() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(10.0f);
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        a.Withdraw("cust");
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void withdrawZeroBalanceGuard() throws Exception {
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        setBalance(a, 0f);
        a.setDbConnection(db);
        assertFalse(a.Withdraw("cust"));
        verifyNoInteractions(db);
    }

    @Test
    void openAcctSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.openAcct());
    }

    @Test
    void getAccnoSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount();
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getAccno("cust"));
    }

    @Test
    void getBalanceNoArgSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount("100");
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getBalance());
    }

    @Test
    void getBalanceWithArgSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount();
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getBalance("100"));
    }

    @Test
    void depositSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.deposit("cust"));
    }

    @Test
    void withdrawSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        SavingAccount a = new SavingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.Withdraw("cust"));
    }
}
