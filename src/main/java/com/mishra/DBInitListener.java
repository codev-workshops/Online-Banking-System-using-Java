package com.mishra;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DBInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DBConnection db = new DBConnection();
            Connection conn = db.openConn();
            if (conn != null) {
                InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
                if (is != null) {
                    String sql = new BufferedReader(new InputStreamReader(is))
                            .lines().collect(Collectors.joining("\n"));
                    Statement stmt = conn.createStatement();
                    for (String s : sql.split(";")) {
                        String trimmed = s.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                    stmt.close();
                    System.out.println("Database schema initialized successfully.");
                }
                db.closeConn();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
