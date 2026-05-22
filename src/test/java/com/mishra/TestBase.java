package com.mishra;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestBase {

    protected static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    @BeforeAll
    static void setupDatabase() {
        System.setProperty("db.url", TEST_DB_URL);
        System.setProperty("db.user", "sa");
        System.setProperty("db.password", "");
    }

    @BeforeEach
    void initSchema() throws Exception {
        Connection conn = DriverManager.getConnection(TEST_DB_URL, "sa", "");
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS Transactions");
        stmt.execute("DROP TABLE IF EXISTS CheckingAccount");
        stmt.execute("DROP TABLE IF EXISTS SavingAccount");
        stmt.execute("DROP TABLE IF EXISTS Account");

        InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
        String sql = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        for (String s : sql.split(";")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                stmt.execute(trimmed);
            }
        }
        stmt.close();
        conn.close();
    }
}
