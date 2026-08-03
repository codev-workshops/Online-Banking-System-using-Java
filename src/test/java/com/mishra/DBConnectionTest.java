package com.mishra;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DBConnectionTest {

    @Test
    void getNextRowCoversAllSwitchCases() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData rsmd = mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(6);
        when(rsmd.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(rsmd.getColumnType(2)).thenReturn(Types.LONGVARCHAR);
        when(rsmd.getColumnType(3)).thenReturn(Types.INTEGER);
        when(rsmd.getColumnType(4)).thenReturn(Types.DOUBLE);
        when(rsmd.getColumnType(5)).thenReturn(Types.FLOAT);
        when(rsmd.getColumnType(6)).thenReturn(Types.DATE); // default branch
        when(rsmd.getColumnTypeName(6)).thenReturn("DATE");

        when(rs.getString(1)).thenReturn("varcharValue");
        when(rs.getString(2)).thenReturn("longVarcharValue");
        when(rs.getLong(3)).thenReturn(42L);
        when(rs.getDouble(4)).thenReturn(3.14d);
        when(rs.getFloat(5)).thenReturn(2.5f);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);

        assertEquals(5, row.size()); // default case adds nothing
        assertEquals("varcharValue", row.get(0));
        assertEquals("longVarcharValue", row.get(1));
        assertEquals(42L, row.get(2));
        assertEquals(3.14d, row.get(3));
        assertEquals(2.5f, row.get(4));
    }

    @Test
    void closeConnOnMock() throws Exception {
        DBConnection db = new DBConnection();
        Connection conn = mock(Connection.class);
        Field f = DBConnection.class.getDeclaredField("connection");
        f.setAccessible(true);
        f.set(db, conn);
        db.closeConn();
        verify(conn).close();
    }

    @Test
    void closeConnOnNullConnection() {
        DBConnection db = new DBConnection();
        // connection is null by default; closeConn swallows the exception
        assertDoesNotThrow(db::closeConn);
    }

    @Test
    void openConnHandlesFailureGracefully() {
        DBConnection db = new DBConnection();
        // No SQL Server available; openConn catches the exception and returns null.
        assertNull(db.openConn());
    }
}
