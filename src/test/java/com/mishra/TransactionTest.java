package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class TransactionTest {
    private final Connection connection = mock(Connection.class);
    private final Statement statement = mock(Statement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private MockedConstruction<DBConnection> db() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        return mockConstruction(DBConnection.class,
                (mock, context) -> when(mock.openConn()).thenReturn(connection));
    }

    @Test
    void constructorsParseAmountAndRecordTransactionOnFreeNumber() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertNotNull(new Transaction("Checking", "alice", "12.5").recordTransaction());
        }
        verify(statement).executeUpdate(argThat(sql -> sql.startsWith("INSERT INTO Transactions(")
                && sql.contains("'Deposit'") == false && sql.contains("'12.5'")));
    }

    @Test
    void recordTransactionRetriesWhenGeneratedNumberCollides() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertNotNull(new Transaction("Checking", "Savings", "alice", "12.5", "Transfer")
                    .recordTransaction());
        }
        verify(statement, times(2)).executeQuery(startsWith("SELECT TransactionNumber FROM Transactions"));
        verify(statement).executeUpdate(contains("'Transfer'"));
    }

    @Test
    void recordTransactionHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertNull(new Transaction("Checking", "alice", "12.5").recordTransaction());
        }
        verify(statement, never()).executeUpdate(anyString());
    }

    @Test
    void searchTransactionReturnsSevenColumnsForEachRow() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("TransactionNumber")).thenReturn("1", "2");
        when(resultSet.getString("TransactionAmount")).thenReturn("10", "20");
        when(resultSet.getString("TransactionType")).thenReturn("Deposit", "Withdraw");
        when(resultSet.getString("TransactionTime")).thenReturn("10:00", "11:00");
        when(resultSet.getString("TransactionDate")).thenReturn("2024-01-01", "2024-01-02");
        when(resultSet.getString("FromAccount")).thenReturn("cash", "checking");
        when(resultSet.getString("ToAccount")).thenReturn("checking", "cash");
        try (MockedConstruction<DBConnection> ignored = db()) {
            Vector rows = new Transaction("2024-01-01", "2024-01-31").searchTransaction("alice");
            assertEquals(2, rows.size());
            assertEquals(7, ((Vector) rows.get(0)).size());
            assertEquals("1", ((Vector) rows.get(0)).get(0));
            assertEquals("checking", ((Vector) rows.get(0)).get(6));
            assertEquals("2", ((Vector) rows.get(1)).get(0));
        }
    }

    @Test
    void searchTransactionReturnsEmptyVectorForNoRowsAndBlankDates() throws Exception {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<DBConnection> ignored = db()) {
            assertTrue(new Transaction("", "").searchTransaction("alice").isEmpty());
        }
        verify(statement).executeQuery("SELECT * FROM Transactions WHERE TransactionDate BETWEEN '' AND ''");
    }

    @Test
    void searchTransactionHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db()) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            assertTrue(new Transaction("2024-01-01", "2024-01-31").searchTransaction("alice").isEmpty());
        }
    }
}
