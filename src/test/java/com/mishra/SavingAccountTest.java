package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

public class SavingAccountTest {

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

    private SavingAccount inject(SavingAccount a) {
        a.setDBConnection(db);
        return a;
    }

    @Test
    public void openAcct_createsWhenAccountIsNew() throws Exception {
        when(rs.next()).thenReturn(false);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "100"));
        assertTrue(a.openAcct());
        verify(stmt).executeUpdate(contains("INSERT INTO SavingAccount"));
        verify(db).closeConn();
    }

    @Test
    public void openAcct_doesNotCreateWhenAccountExists() throws Exception {
        when(rs.next()).thenReturn(true);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "100"));
        assertFalse(a.openAcct());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void openAcct_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "100"));
        assertFalse(a.openAcct());
    }

    @Test
    public void getAccno_returnsAccountNumberFromResultSet() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("SavingAccountNumber")).thenReturn("SAV-7");
        SavingAccount a = inject(new SavingAccount());
        assertEquals("SAV-7", a.getAccno("cust"));
    }

    @Test
    public void getAccno_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount());
        assertNull(a.getAccno("cust"));
    }

    @Test
    public void getBalance_noArg_readsBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(150.0f);
        SavingAccount a = inject(new SavingAccount("S1"));
        assertEquals(150.0f, a.getBalance(), 0.001f);
    }

    @Test
    public void getBalance_noArg_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount("S1"));
        assertEquals(-1.0f, a.getBalance(), 0.001f);
    }

    @Test
    public void getBalance_withArg_readsBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(275.0f);
        SavingAccount a = inject(new SavingAccount());
        assertEquals(275.0f, a.getBalance("S1"), 0.001f);
    }

    @Test
    public void getBalance_withArg_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount());
        assertEquals(-1.0f, a.getBalance("S1"), 0.001f);
    }

    @Test
    public void deposit_addsAmountToBalance() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "25"));
        assertTrue(a.deposit("cust"));
        verify(stmt).executeUpdate(contains("UPDATE SavingAccount"));
    }

    @Test
    public void deposit_skippedWhenIdentifiersBlank() throws Exception {
        SavingAccount a = inject(new SavingAccount("", "Name", "", "25"));
        assertFalse(a.deposit(""));
        verify(db, never()).openConn();
    }

    @Test
    public void deposit_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "25"));
        assertFalse(a.deposit("cust"));
    }

    @Test
    public void withdraw_deductsWhenBalanceSufficient() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(100.0f);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "40"));
        assertTrue(a.Withdraw("cust"));
        verify(stmt).executeUpdate(contains("UPDATE SavingAccount"));
    }

    @Test
    public void withdraw_doesNotDeductWhenBalanceInsufficient() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(10.0f);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "40"));
        assertTrue(a.Withdraw("cust"));
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void withdraw_blockedWhenCurrentBalanceIsZero() throws Exception {
        // Prime the Balance field to 0 via getBalance, exercising the
        // !(Balance == 0) guard in Withdraw.
        when(rs.next()).thenReturn(true, false);
        when(rs.getFloat(1)).thenReturn(0.0f);
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "40"));
        assertEquals(0.0f, a.getBalance(), 0.001f);
        assertFalse(a.Withdraw("cust"));
    }

    @Test
    public void withdraw_skippedWhenIdentifiersBlank() throws Exception {
        SavingAccount a = inject(new SavingAccount("", "Name", "", "40"));
        assertFalse(a.Withdraw(""));
        verify(db, never()).openConn();
    }

    @Test
    public void withdraw_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        SavingAccount a = inject(new SavingAccount("S1", "Name", "cust", "40"));
        a.Withdraw("cust");
    }
}
