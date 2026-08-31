package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

class DBConnectionTest {
    @AfterEach
    void resetDriver() {
        SQLServerDriver.failConnections = false;
    }

    @Test
    void openConnReturnsDriverConnection() {
        SQLServerDriver.failConnections = false;
        assertNotNull(new DBConnection().openConn());
    }

    @Test
    void openConnReturnsNullWhenDriverFails() {
        SQLServerDriver.failConnections = true;
        assertNull(new DBConnection().openConn());
    }

    @Test
    void closeConnClosesConnection() throws Exception {
        Connection connection = mock(Connection.class);
        DBConnection db = new DBConnection();
        java.lang.reflect.Field field = DBConnection.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(db, connection);
        db.closeConn();
        verify(connection).close();
    }

    @Test
    void closeConnSwallowsNullConnection() {
        assertDoesNotThrow(() -> new DBConnection().closeConn());
    }

    @Test
    void getNextRowSupportsAllHandledColumnTypes() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(metadata.getColumnCount()).thenReturn(5);
        when(metadata.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metadata.getColumnType(2)).thenReturn(Types.LONGVARCHAR);
        when(metadata.getColumnType(3)).thenReturn(Types.INTEGER);
        when(metadata.getColumnType(4)).thenReturn(Types.DOUBLE);
        when(metadata.getColumnType(5)).thenReturn(Types.FLOAT);
        when(rs.getString(1)).thenReturn("one");
        when(rs.getString(2)).thenReturn("two");
        when(rs.getLong(3)).thenReturn(3L);
        when(rs.getDouble(4)).thenReturn(4.5);
        when(rs.getFloat(5)).thenReturn(5.5f);
        Vector row = new DBConnection().getNextRow(rs, metadata);
        assertEquals(5, row.size());
        assertEquals("one", row.get(0));
        assertEquals("two", row.get(1));
        assertEquals(3L, row.get(2));
        assertEquals(4.5, row.get(3));
        assertEquals(5.5f, row.get(4));
    }

    @Test
    void getNextRowIgnoresDefaultColumnType() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(metadata.getColumnCount()).thenReturn(1);
        when(metadata.getColumnType(1)).thenReturn(Types.BOOLEAN);
        when(metadata.getColumnTypeName(1)).thenReturn("BOOLEAN");
        assertTrue(new DBConnection().getNextRow(rs, metadata).isEmpty());
        verifyNoInteractions(rs);
    }
}
