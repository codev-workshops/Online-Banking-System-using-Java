package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Vector;

import org.junit.Test;

public class DBConnectionTest {

    @Test
    public void openConn_returnsInjectedConnectionFromConstructor() {
        Connection conn = mock(Connection.class);
        DBConnection db = new DBConnection(conn);
        assertSame(conn, db.openConn());
    }

    @Test
    public void openConn_returnsInjectedConnectionFromSetter() {
        Connection conn = mock(Connection.class);
        DBConnection db = new DBConnection();
        db.setConnection(conn);
        assertSame(conn, db.openConn());
    }

    @Test
    public void getNextRow_mapsAllSupportedColumnTypes() throws Exception {
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
        when(rs.getString(1)).thenReturn("a");
        when(rs.getString(2)).thenReturn("b");
        when(rs.getLong(3)).thenReturn(7L);
        when(rs.getDouble(4)).thenReturn(2.5d);
        when(rs.getFloat(5)).thenReturn(1.5f);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);

        assertEquals(5, row.size()); // DATE column falls through default, adds nothing
        assertEquals("a", row.get(0));
        assertEquals("b", row.get(1));
        assertEquals(Long.valueOf(7L), row.get(2));
        assertEquals(Double.valueOf(2.5d), row.get(3));
        assertEquals(Float.valueOf(1.5f), row.get(4));
    }

    @Test
    public void closeConn_closesConnectionOnHappyPath() throws Exception {
        Connection conn = mock(Connection.class);
        DBConnection db = new DBConnection(conn);
        db.openConn();
        db.closeConn();
        verify(conn).close();
    }

    @Test
    public void closeConn_swallowsExceptionWhenConnectionUnavailable() {
        // Fresh connection field is null -> close() throws NPE, which is caught.
        DBConnection db = new DBConnection();
        db.closeConn();
    }
}
