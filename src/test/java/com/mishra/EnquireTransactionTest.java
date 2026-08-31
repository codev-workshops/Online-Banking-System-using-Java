package com.mishra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class EnquireTransactionTest {
    private final Connection connection = mock(Connection.class);
    private final Statement statement = mock(Statement.class);
    private final ResultSet resultSet = mock(ResultSet.class);
    private final ResultSetMetaData metadata = mock(ResultSetMetaData.class);

    private MockedConstruction<DBConnection> db(Vector row) throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        return mockConstruction(DBConnection.class, (mock, context) -> {
            when(mock.openConn()).thenReturn(connection);
            when(mock.getNextRow(any(ResultSet.class), any(ResultSetMetaData.class))).thenReturn(row);
        });
    }

    @Test
    void fetchTransactionReturnsColumnNamesAndRows() throws Exception {
        Vector row = new Vector();
        row.add("transaction");
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(2);
        when(metadata.getColumnName(2)).thenReturn("CustomerID");
        when(resultSet.next()).thenReturn(true, false);
        try (MockedConstruction<DBConnection> ignored = db(row)) {
            Vector[] result = new EnquireTransaction().FetchTransaction("alice", "2024-01-01", "2024-01-31");
            assertNull(result[0]);
            assertNull(result[1]);
        }
        verify(statement).executeQuery(" SELECT * FROM Transactions WHERE CustomerID = 'null' AND TransactionDate BETWEEN '2024-01-01' AND '2024-01-31'");
    }

    @Test
    void fetchTransactionRejectsEmptyDatesWithoutDatabaseAccess() {
        Vector[] result = new EnquireTransaction().FetchTransaction("alice", "", "");
        assertNull(result[0]);
        assertNull(result[1]);
    }

    @Test
    void fetchTransactionHandlesSqlException() throws Exception {
        try (MockedConstruction<DBConnection> ignored = db(new Vector())) {
            when(connection.createStatement()).thenThrow(new SQLException("down"));
            Vector[] result = new EnquireTransaction().FetchTransaction("alice", "2024-01-01", "2024-01-31");
            assertNull(result[0]);
            assertNull(result[1]);
        }
    }
}
