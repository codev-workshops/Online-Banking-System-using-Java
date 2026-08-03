package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckingAccountTest {

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

    @Test
    void openAcctInsert() throws Exception {
        when(rs.next()).thenReturn(false);
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.openAcct());
        verify(stmt).executeUpdate(contains("INSERT INTO CheckingAccount"));
    }

    @Test
    void openAcctAlreadyExists() throws Exception {
        when(rs.next()).thenReturn(true);
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.openAcct());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void getAccno() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("CheckingAccountNumber")).thenReturn("777");
        CheckingAccount a = new CheckingAccount();
        a.setDbConnection(db);
        assertEquals("777", a.getAccno("cust"));
    }

    @Test
    void getBalanceNoArg() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(250.0f);
        CheckingAccount a = new CheckingAccount("100");
        a.setDbConnection(db);
        assertEquals(250.0f, a.getBalance());
    }

    @Test
    void getBalanceWithArg() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(325.0f);
        CheckingAccount a = new CheckingAccount();
        a.setDbConnection(db);
        assertEquals(325.0f, a.getBalance("100"));
    }

    @Test
    void depositHappyPath() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.deposit("cust"));
        verify(stmt).executeUpdate(contains("UPDATE CheckingAccount SET Balance = '150.0'"));
    }

    @Test
    void depositEmptyAccountGuard() {
        CheckingAccount a = new CheckingAccount("", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.deposit("cust"));
        verifyNoInteractions(db);
    }

    @Test
    void withdrawSufficientFunds() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(200.0f);
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertTrue(a.Withdraw("cust"));
        verify(stmt).executeUpdate(contains("UPDATE CheckingAccount SET Balance = '150.0'"));
    }

    @Test
    void withdrawInsufficientFunds() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(10.0f);
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        a.Withdraw("cust");
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void openAcctSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.openAcct());
    }

    @Test
    void getAccnoSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount();
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getAccno("cust"));
    }

    @Test
    void getBalanceNoArgSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount("100");
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getBalance());
    }

    @Test
    void getBalanceWithArgSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount();
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.getBalance("100"));
    }

    @Test
    void depositSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertFalse(a.deposit("cust"));
    }

    @Test
    void withdrawSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 5));
        CheckingAccount a = new CheckingAccount("100", "Name", "cust", "50");
        a.setDbConnection(db);
        assertDoesNotThrow(() -> a.Withdraw("cust"));
    }
}
