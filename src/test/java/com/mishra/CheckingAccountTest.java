package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class CheckingAccountTest {
    private final Connection connection = mock(Connection.class);
    private final Statement statement = mock(Statement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private MockedConstruction<DBConnection> db() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        return mockConstruction(DBConnection.class,
                (mock, context) -> when(mock.openConn()).thenReturn(connection));
    }

    @Test
    void openAcctInsertsNewAccount() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new CheckingAccount("100", "Alice", "alice", "25.5").openAcct());
        }
        verify(statement).executeUpdate("INSERT INTO CheckingAccount(CheckingAccountNumber, CustomerName, Balance, CustomerID) VALUES ('100','Alice',25.5, 'alice')");
    }

    @Test
    void openAcctRejectsExistingAccount() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertFalse(new CheckingAccount("100", "Alice", "alice", "25.5").openAcct());
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void openAcctHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertFalse(new CheckingAccount("100", "Alice", "alice", "25.5").openAcct());
        }
    }

    @Test
    void getAccnoReturnsDatabaseValue() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("CheckingAccountNumber")).thenReturn("101");
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertEquals("101", new CheckingAccount().getAccno("alice"));
        }
    }

    @Test
    void getBalanceOverloadsReturnDatabaseValue() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(12.5f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            CheckingAccount account = new CheckingAccount("101");
            assertEquals(12.5f, account.getBalance());
            when(resultSet.next()).thenReturn(true, false);
            assertEquals(12.5f, account.getBalance("102"));
        }
        verify(statement).executeQuery("SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber ='101'");
        verify(statement).executeQuery("SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber ='102'");
    }

    @Test
    void getBalanceHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertEquals(-1f, new CheckingAccount("101").getBalance());
        }
    }

    @Test
    void depositUpdatesBalance() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(10f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new CheckingAccount("101", "Alice", "alice", "2.5").deposit("alice"));
        }
        verify(statement).executeUpdate("UPDATE CheckingAccount SET Balance = '12.5' WHERE CheckingAccountNumber = '101'");
    }

    @Test
    void depositHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertFalse(new CheckingAccount("101", "Alice", "alice", "2.5").deposit("alice"));
        }
    }

    @Test
    void withdrawUpdatesWhenFundsAreSufficient() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(10f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new CheckingAccount("101", "Alice", "alice", "2.5").Withdraw("alice"));
        }
        verify(statement).executeUpdate("UPDATE CheckingAccount SET Balance = '7.5' WHERE  CheckingAccountNumber = '101'");
    }

    @Test
    void withdrawSkipsUpdateWhenFundsAreInsufficient() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(1f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new CheckingAccount("101", "Alice", "alice", "2.5").Withdraw("alice"));
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void withdrawHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertTrue(new CheckingAccount("101", "Alice", "alice", "2.5").Withdraw("alice"));
        }
    }
}
