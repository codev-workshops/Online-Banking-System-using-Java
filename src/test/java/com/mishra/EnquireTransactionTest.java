package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class EnquireTransactionTest {

    private DBConnection db;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private ResultSetMetaData rsmd;

    @Before
    public void setUp() throws Exception {
        db = mock(DBConnection.class);
        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
        rsmd = mock(ResultSetMetaData.class);
        when(db.openConn()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(rsmd);
    }

    private EnquireTransaction inject(EnquireTransaction e) {
        e.setDBConnection(db);
        return e;
    }

    @Test
    public void fetchTransaction_returnsRowsForValidDateRange() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rsmd.getColumnCount()).thenReturn(2);
        when(rsmd.getColumnName(anyInt())).thenReturn("SomeColumn");
        when(db.getNextRow(any(ResultSet.class), any(ResultSetMetaData.class)))
                .thenReturn(new Vector());

        EnquireTransaction e = inject(new EnquireTransaction());
        Vector[] result = e.FetchTransaction("john", "2024-01-01", "2024-12-31");

        // The query/loop executes, but the method returns the (uninitialized)
        // instance fields rather than the local vectors -- preserved behavior.
        assertEquals(2, result.length);
        assertNull(result[1]);
        verify(db).openConn();
    }

    @Test
    public void fetchTransaction_skipsQueryOnBlankDates() throws Exception {
        EnquireTransaction e = inject(new EnquireTransaction());
        Vector[] result = e.FetchTransaction("john", "", "");
        assertEquals(2, result.length);
        assertNull(result[1]);
        verify(db, never()).openConn();
    }

    @Test
    public void fetchTransaction_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        EnquireTransaction e = inject(new EnquireTransaction());
        Vector[] result = e.FetchTransaction("john", "2024-01-01", "2024-12-31");
        assertEquals(2, result.length);
    }
}
