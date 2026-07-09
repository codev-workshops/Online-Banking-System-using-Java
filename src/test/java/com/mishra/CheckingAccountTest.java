package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

public class CheckingAccountTest {

    private DBConnection db;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    @Before
    public void setUp() throws Exception {
        db = mock(DBConnection.class);
        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
        when(db.openConn()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    private CheckingAccount inject(CheckingAccount a) {
        a.setDBConnection(db);
        return a;
    }

    @Test
    public void openAcct_createsWhenAccountIsNew() throws Exception {
        when(rs.next()).thenReturn(false);
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "100"));
        assertTrue(a.openAcct());
        verify(stmt).executeUpdate(contains("INSERT INTO CheckingAccount"));
        verify(db).closeConn();
    }

    @Test
    public void openAcct_doesNotCreateWhenAccountExists() throws Exception {
        when(rs.next()).thenReturn(true);
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "100"));
        assertFalse(a.openAcct());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void openAcct_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "100"));
        assertFalse(a.openAcct());
    }

    @Test
    public void getAccno_returnsAccountNumberFromResultSet() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("CheckingAccountNumber")).thenReturn("CHK-42");
        CheckingAccount a = inject(new CheckingAccount());
        assertEquals("CHK-42", a.getAccno("cust"));
    }

    @Test
    public void getAccno_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount());
        assertNull(a.getAccno("cust"));
    }

    @Test
    public void getBalance_noArg_readsBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(150.0f);
        CheckingAccount a = inject(new CheckingAccount("C1"));
        assertEquals(150.0f, a.getBalance(), 0.001f);
    }

    @Test
    public void getBalance_noArg_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount("C1"));
        assertEquals(-1.0f, a.getBalance(), 0.001f);
    }

    @Test
    public void getBalance_withArg_readsBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(275.0f);
        CheckingAccount a = inject(new CheckingAccount());
        assertEquals(275.0f, a.getBalance("C1"), 0.001f);
    }

    @Test
    public void getBalance_withArg_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount());
        assertEquals(-1.0f, a.getBalance("C1"), 0.001f);
    }

    @Test
    public void deposit_addsAmountToBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "25"));
        assertTrue(a.deposit("cust"));
        verify(stmt).executeUpdate(contains("UPDATE CheckingAccount"));
    }

    @Test
    public void deposit_skippedWhenIdentifiersBlank() throws Exception {
        CheckingAccount a = inject(new CheckingAccount("", "Name", "", "25"));
        assertFalse(a.deposit(""));
        verify(db, never()).openConn();
    }

    @Test
    public void deposit_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "25"));
        assertFalse(a.deposit("cust"));
    }

    @Test
    public void withdraw_deductsWhenBalanceSufficient() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "40"));
        assertTrue(a.Withdraw("cust"));
        verify(stmt).executeUpdate(contains("UPDATE CheckingAccount"));
    }

    @Test
    public void withdraw_doesNotDeductWhenBalanceInsufficient() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(10.0f);
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "40"));
        assertTrue(a.Withdraw("cust"));
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void withdraw_skippedWhenIdentifiersBlank() throws Exception {
        CheckingAccount a = inject(new CheckingAccount("", "Name", "", "40"));
        assertFalse(a.Withdraw(""));
        verify(db, never()).openConn();
    }

    @Test
    public void withdraw_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        CheckingAccount a = inject(new CheckingAccount("C1", "Name", "cust", "40"));
        a.Withdraw("cust");
    }
}
