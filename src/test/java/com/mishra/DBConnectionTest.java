package com.mishra;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DBConnectionTest {

    @Test
    void constructorDoesNotThrow() {
        assertDoesNotThrow(() -> new DBConnection());
    }

    @Test
    void getNextRowVarchar() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(rs.getString(1)).thenReturn("test");

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);
        assertEquals(1, row.size());
        assertEquals("test", row.get(0));
    }

    @Test
    void getNextRowInteger() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnType(1)).thenReturn(Types.INTEGER);
        when(rs.getLong(1)).thenReturn(42L);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);
        assertEquals(1, row.size());
        assertEquals(42L, row.get(0));
    }

    @Test
    void getNextRowDouble() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnType(1)).thenReturn(Types.DOUBLE);
        when(rs.getDouble(1)).thenReturn(3.14);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);
        assertEquals(1, row.size());
        assertEquals(3.14, row.get(0));
    }

    @Test
    void getNextRowFloat() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnType(1)).thenReturn(Types.FLOAT);
        when(rs.getFloat(1)).thenReturn(2.5f);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);
        assertEquals(1, row.size());
        assertEquals(2.5f, ((Float) row.get(0)).floatValue());
    }

    @Test
    void getNextRowMultipleColumns() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        when(rsmd.getColumnCount()).thenReturn(3);
        when(rsmd.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(rsmd.getColumnType(2)).thenReturn(Types.INTEGER);
        when(rsmd.getColumnType(3)).thenReturn(Types.DOUBLE);
        when(rs.getString(1)).thenReturn("hello");
        when(rs.getLong(2)).thenReturn(99L);
        when(rs.getDouble(3)).thenReturn(1.23);

        DBConnection db = new DBConnection();
        Vector row = db.getNextRow(rs, rsmd);
        assertEquals(3, row.size());
        assertEquals("hello", row.get(0));
        assertEquals(99L, row.get(1));
        assertEquals(1.23, row.get(2));
    }

    @Test
    void openConnDriverNotFound() {
        DBConnection db = new DBConnection();
        assertNull(db.openConn());
    }

    @Test
    void closeConnNullConnection() {
        DBConnection db = new DBConnection();
        assertDoesNotThrow(() -> db.closeConn());
    }
}
