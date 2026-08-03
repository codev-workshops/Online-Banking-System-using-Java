package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnquireTransactionTest {

    private DBConnection db;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private ResultSetMetaData rsmd;

    @BeforeEach
    void setUp() throws Exception {
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

    @Test
    void fetchTransactionEmptyDateGuard() {
        // Guard is false: the DB branch is skipped entirely and no rows are fetched.
        EnquireTransaction e = new EnquireTransaction();
        e.setDbConnection(db);
        Vector[] result = e.FetchTransaction("user", "", "");
        assertEquals(2, result.length);
        verifyNoInteractions(db);
    }

    @Test
    void fetchTransactionPopulated() throws Exception {
        // Populated result: the row-iteration branch runs and calls getNextRow.
        when(rs.next()).thenReturn(true, false);
        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnName(anyInt())).thenReturn("TransactionNumber");
        Vector aRow = new Vector();
        aRow.addElement("1234");
        when(db.getNextRow(any(ResultSet.class), any(ResultSetMetaData.class))).thenReturn(aRow);

        EnquireTransaction e = new EnquireTransaction();
        e.setDbConnection(db);
        Vector[] result = e.FetchTransaction("user", "2020-01-01", "2020-12-31");

        assertEquals(2, result.length);
        verify(db).getNextRow(any(ResultSet.class), any(ResultSetMetaData.class));
        verify(db).closeConn();
    }
}
