package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionTest {

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
    void recordTransactionRetriesThenInserts() throws Exception {
        // first generated number collides (next()==true), second is free (next()==false)
        when(rs.next()).thenReturn(true, false);
        Transaction t = new Transaction("Checking", "cust", "50");
        t.setDbConnection(db);
        String number = t.recordTransaction();
        assertNotNull(number);
        verify(stmt, atLeast(2)).executeQuery(anyString());
        verify(stmt).executeUpdate(contains("INSERT INTO Transactions"));
    }

    @Test
    void recordTransactionSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 3));
        Transaction t = new Transaction("Checking", "cust", "50");
        t.setDbConnection(db);
        assertDoesNotThrow(t::recordTransaction);
    }

    @Test
    void searchTransactionBuildsVector() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("TransactionNumber")).thenReturn("1234");
        when(rs.getString("TransactionAmount")).thenReturn("50");
        when(rs.getString("TransactionType")).thenReturn("Deposit");
        when(rs.getString("TransactionTime")).thenReturn("10:00:00");
        when(rs.getString("TransactionDate")).thenReturn("2020-06-01");
        when(rs.getString("FromAccount")).thenReturn("100");
        when(rs.getString("ToAccount")).thenReturn("200");
        Transaction t = new Transaction("2020-01-01", "2020-12-31");
        t.setDbConnection(db);
        Vector result = t.searchTransaction("user");
        assertEquals(1, result.size());
        Vector row = (Vector) result.get(0);
        assertEquals("1234", row.get(0));
        assertEquals("200", row.get(6));
    }

    @Test
    void searchTransactionEmptyDateGuard() throws Exception {
        when(rs.next()).thenReturn(false);
        Transaction t = new Transaction("", "");
        t.setDbConnection(db);
        Vector result = t.searchTransaction("user");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchTransactionSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Transaction t = new Transaction("2020-01-01", "2020-12-31");
        t.setDbConnection(db);
        assertTrue(t.searchTransaction("user").isEmpty());
    }
}
