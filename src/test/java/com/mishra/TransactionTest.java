package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

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

    private Transaction inject(Transaction t) {
        t.setDBConnection(db);
        return t;
    }

    @Test
    public void recordTransaction_generatesNumberAndInserts() throws Exception {
        when(rs.next()).thenReturn(false); // generated number is unique first try
        Transaction t = inject(new Transaction("toAcc", "fromAcc", "cust", "50", "Deposit"));
        String number = t.recordTransaction();
        assertNotNull(number);
        assertFalse(number.isEmpty());
        verify(stmt).executeUpdate(contains("INSERT INTO Transactions"));
    }

    @Test
    public void recordTransaction_retriesOnCollision() throws Exception {
        when(rs.next()).thenReturn(true, false); // first number collides, second is free
        Transaction t = inject(new Transaction("toAcc", "fromAcc", "cust", "50", "Deposit"));
        assertNotNull(t.recordTransaction());
        verify(stmt, times(2)).executeQuery(anyString());
        verify(stmt).executeUpdate(contains("INSERT INTO Transactions"));
    }

    @Test
    public void recordTransaction_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Transaction t = inject(new Transaction("toAcc", "fromAcc", "cust", "50", "Deposit"));
        t.recordTransaction();
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void searchTransaction_collectsRows() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(anyString())).thenReturn("x");
        Transaction t = inject(new Transaction("2024-01-01", "2024-12-31"));
        Vector result = t.searchTransaction("john");
        assertEquals(1, result.size());
    }

    @Test
    public void searchTransaction_blankDatesReturnEmpty() throws Exception {
        when(rs.next()).thenReturn(false);
        Transaction t = inject(new Transaction("", ""));
        Vector result = t.searchTransaction("john");
        assertTrue(result.isEmpty());
    }

    @Test
    public void searchTransaction_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Transaction t = inject(new Transaction("2024-01-01", "2024-12-31"));
        Vector result = t.searchTransaction("john");
        assertTrue(result.isEmpty());
    }
}
