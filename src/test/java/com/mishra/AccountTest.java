package com.mishra;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

public class AccountTest {

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

    private Account inject(Account a) {
        a.setDBConnection(db);
        return a;
    }

    @Test
    public void signUp_insertsWhenUsernameIsNew() throws Exception {
        when(rs.next()).thenReturn(false); // username does not exist yet
        Account a = inject(new Account("newuser", "pw", "pw", "New User"));
        assertTrue(a.signUp());
        verify(stmt).executeUpdate(contains("INSERT INTO Account"));
        verify(db).closeConn();
    }

    @Test
    public void signUp_failsWhenUsernameAlreadyExists() throws Exception {
        when(rs.next()).thenReturn(true); // username already present
        Account a = inject(new Account("dupe", "pw", "pw", "Dupe"));
        assertFalse(a.signUp());
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void signUp_failsOnBlankInput() throws Exception {
        Account a = inject(new Account("", "", "", ""));
        assertFalse(a.signUp());
        verify(db, never()).openConn();
    }

    @Test
    public void signUp_failsOnMismatchedPasswords() throws Exception {
        Account a = inject(new Account("user", "pw1", "pw2", "Name"));
        assertFalse(a.signUp());
        verify(db, never()).openConn();
    }

    @Test
    public void signUp_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Account a = inject(new Account("user", "pw", "pw", "Name"));
        assertFalse(a.signUp());
    }

    @Test
    public void signIn_returnsNameWhenFound() throws Exception {
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn("John Doe");
        Account a = inject(new Account("john", "pw"));
        assertEquals("John Doe", a.signIn());
        verify(db).closeConn();
    }

    @Test
    public void signIn_returnsNullWhenNotFound() throws Exception {
        when(rs.next()).thenReturn(false);
        Account a = inject(new Account("ghost", "pw"));
        assertNull(a.signIn());
    }

    @Test
    public void signIn_skipsQueryOnBlankCredentials() throws Exception {
        Account a = inject(new Account("", ""));
        assertNull(a.signIn());
        verify(db, never()).openConn();
    }

    @Test
    public void signIn_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Account a = inject(new Account("john", "pw"));
        assertNull(a.signIn());
    }

    @Test
    public void changePassword_updatesWhenAccountFound() throws Exception {
        when(rs.next()).thenReturn(true);
        Account a = inject(new Account("john", "oldpw"));
        assertTrue(a.changePassword("newpw"));
        verify(stmt).executeUpdate(contains("UPDATE Account"));
    }

    @Test
    public void changePassword_failsWhenAccountNotFound() throws Exception {
        when(rs.next()).thenReturn(false);
        Account a = inject(new Account("john", "oldpw"));
        assertFalse(a.changePassword("newpw"));
        verify(stmt, never()).executeUpdate(anyString());
    }

    @Test
    public void changePassword_handlesSQLException() throws Exception {
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("boom"));
        Account a = inject(new Account("john", "oldpw"));
        assertFalse(a.changePassword("newpw"));
    }
}
