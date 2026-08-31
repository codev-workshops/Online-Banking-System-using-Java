package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class SavingAccountTest {
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
            assertTrue(new SavingAccount("200", "Alice", "alice", "25.5").openAcct());
        }
        verify(statement).executeUpdate("INSERT INTO SavingAccount(SavingAccountNumber, CustomerName, Balance, CustomerID) VALUES ('200','Alice','25.5', 'alice')");
    }

    @Test
    void openAcctRejectsExistingAccount() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertFalse(new SavingAccount("200", "Alice", "alice", "25.5").openAcct());
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void openAcctHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertFalse(new SavingAccount("200", "Alice", "alice", "25.5").openAcct());
        }
    }

    @Test
    void getAccnoReturnsDatabaseValue() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("SavingAccountNumber")).thenReturn("201");
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertEquals("201", new SavingAccount().getAccno("alice"));
        }
    }

    @Test
    void getBalanceOverloadsReturnDatabaseValue() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(12.5f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            SavingAccount account = new SavingAccount("201");
            assertEquals(12.5f, account.getBalance());
            when(resultSet.next()).thenReturn(true, false);
            assertEquals(12.5f, account.getBalance("202"));
        }
        verify(statement).executeQuery("SELECT Balance FROM SavingAccount WHERE SavingAccountNumber ='201'");
        verify(statement).executeQuery("SELECT Balance FROM SavingAccount WHERE SavingAccountNumber ='202'");
    }

    @Test
    void getBalanceHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertEquals(-1f, new SavingAccount("201").getBalance());
        }
    }

    @Test
    void depositUpdatesBalance() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(10f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new SavingAccount("201", "Alice", "alice", "2.5").deposit("alice"));
        }
        verify(statement).executeUpdate("UPDATE SavingAccount SET Balance = '12.5' WHERE SavingAccountNumber = '201'");
    }

    @Test
    void depositHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertFalse(new SavingAccount("201", "Alice", "alice", "2.5").deposit("alice"));
        }
    }

    @Test
    void withdrawUpdatesWhenFundsAreSufficient() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(10f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new SavingAccount("201", "Alice", "alice", "2.5").Withdraw("alice"));
        }
        verify(statement).executeUpdate("UPDATE SavingAccount SET Balance = '7.5' WHERE  SavingAccountNumber = '201'");
    }

    @Test
    void withdrawSkipsUpdateWhenFundsAreInsufficient() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getFloat(1)).thenReturn(1f);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new SavingAccount("201", "Alice", "alice", "2.5").Withdraw("alice"));
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void withdrawSkipsDatabaseWhenInitialBalanceIsZero() throws Exception {
        SavingAccount account = new SavingAccount("201", "Alice", "alice", "2.5");
        java.lang.reflect.Field balance = SavingAccount.class.getDeclaredField("Balance");
        balance.setAccessible(true);
        balance.set(account, 0f);
        try (MockedConstruction<DBConnection> constructed = mockConstruction(DBConnection.class)) {
            assertFalse(account.Withdraw("alice"));
            assertEquals(0, constructed.constructed().size());
        }
    }

    @Test
    void withdrawHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertTrue(new SavingAccount("201", "Alice", "alice", "2.5").Withdraw("alice"));
        }
    }
}
