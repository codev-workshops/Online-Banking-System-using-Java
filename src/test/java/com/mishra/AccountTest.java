package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountTest {

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
    void signUpHappyPathInsert() throws Exception {
        when(rs.next()).thenReturn(false);
        Account a = new Account("user", "pw", "pw", "Name");
        a.setDbConnection(db);
        assertTrue(a.signUp());
        verify(stmt).executeUpdate(contains("INSERT INTO Account"));
        verify(db).closeConn();
    }

    @Test
    void signUpDuplicateUsername() throws Exception {
        when(rs.next()).thenReturn(true);
        Account a = new Account("user", "pw", "pw", "Name");
        a.setDbConnection(db);
        assertFalse(a.signUp());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void signUpEmptyFields() {
        Account a = new Account("", "", "", "");
        a.setDbConnection(db);
        assertFalse(a.signUp());
        verifyNoInteractions(db);
    }

    @Test
    void signUpPasswordMismatch() {
        Account a = new Account("user", "pw", "different", "Name");
        a.setDbConnection(db);
        assertFalse(a.signUp());
        verifyNoInteractions(db);
    }

    @Test
    void signUpSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S1", 7));
        Account a = new Account("user", "pw", "pw", "Name");
        a.setDbConnection(db);
        assertFalse(a.signUp());
    }

    @Test
    void signInFound() throws Exception {
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn("John Doe");
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertEquals("John Doe", a.signIn());
    }

    @Test
    void signInNotFound() throws Exception {
        when(rs.next()).thenReturn(false);
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertNull(a.signIn());
    }

    @Test
    void signInEmptyCredentialsGuard() {
        Account a = new Account("", "");
        a.setDbConnection(db);
        assertNull(a.signIn());
        verifyNoInteractions(db);
    }

    @Test
    void signInSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertNull(a.signIn());
    }

    @Test
    void changePasswordUpdated() throws Exception {
        when(rs.next()).thenReturn(true);
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertTrue(a.changePassword("newpw"));
        verify(stmt).executeUpdate(contains("UPDATE Account"));
    }

    @Test
    void changePasswordNotFound() throws Exception {
        when(rs.next()).thenReturn(false);
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertFalse(a.changePassword("newpw"));
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    void changePasswordSqlException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom", "S2", 9));
        Account a = new Account("user", "pw");
        a.setDbConnection(db);
        assertFalse(a.changePassword("newpw"));
    }
}
