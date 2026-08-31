package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class AccountTest {
    private final Connection connection = mock(Connection.class);
    private final Statement statement = mock(Statement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private MockedConstruction<DBConnection> db() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        return mockConstruction(DBConnection.class,
                (mock, context) -> when(mock.openConn()).thenReturn(connection));
    }

    @Test
    void signUpInsertsWhenUsernameIsFree() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new Account("alice", "secret", "secret", "Alice").signUp());
        }
        verify(statement).executeUpdate("INSERT INTO Account(Username, Password, Name) VALUES ('alice','secret','Alice')");
    }

    @Test
    void signUpRejectsDuplicateUsername() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertFalse(new Account("alice", "secret", "secret", "Alice").signUp());
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void signUpRejectsInvalidInputWithoutOpeningDatabase() {
        try (MockedConstruction<DBConnection> constructed = mockConstruction(DBConnection.class)) {
            assertFalse(new Account("", "secret", "secret", "Alice").signUp());
            assertFalse(new Account("alice", "", "", "Alice").signUp());
            assertFalse(new Account("alice", "secret", "different", "Alice").signUp());
            assertEquals(0, constructed.constructed().size());
        }
    }

    @Test
    void signUpHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("database down"));
            assertFalse(new Account("alice", "secret", "secret", "Alice").signUp());
        }
    }

    @Test
    void signUpHandlesGenericException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new IllegalStateException("broken connection"));
            assertFalse(new Account("alice", "secret", "secret", "Alice").signUp());
        }
    }

    @Test
    void signInReturnsNameForMatchingCredentials() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("Alice");
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertEquals("Alice", new Account("alice", "secret").signIn());
        }
        verify(statement).executeQuery("SELECT Name FROM Account WHERE Username ='alice'AND Password ='secret'");
    }

    @Test
    void signInReturnsNullWhenThereIsNoMatch() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertNull(new Account("alice", "secret").signIn());
        }
    }

    @Test
    void signInRejectsEmptyCredentials() {
        try (MockedConstruction<DBConnection> constructed = mockConstruction(DBConnection.class)) {
            assertNull(new Account("", "secret").signIn());
            assertEquals(0, constructed.constructed().size());
        }
    }

    @Test
    void signInHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("database down"));
            assertNull(new Account("alice", "secret").signIn());
        }
    }

    @Test
    void changePasswordUpdatesExistingAccount() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new Account("alice", "old").changePassword("new"));
        }
        verify(statement).executeUpdate("UPDATE Account SET Password='new' WHERE Username ='alice'");
    }

    @Test
    void changePasswordReturnsFalseWhenAccountIsMissing() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertFalse(new Account("alice", "old").changePassword("new"));
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void changePasswordHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("database down"));
            assertFalse(new Account("alice", "old").changePassword("new"));
        }
    }
}
