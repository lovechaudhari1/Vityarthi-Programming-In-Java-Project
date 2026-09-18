package com.campuslab.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class DatabaseManager {
    private static DatabaseManager instance;
    private final String url;

    private DatabaseManager() {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Path.of("config", "db.properties"))) {
            properties.load(input);
            url = properties.getProperty("db.url", "jdbc:sqlite:campuslab.db");
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config/db.properties", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initializeDatabase() throws SQLException, IOException {
        String schema = Files.readString(Path.of("sql", "schema.sql"));

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            for (String sql : schema.split(";")) {
                String command = sql.trim();
                if (!command.isEmpty()) {
                    statement.execute(command);
                }
            }
        }
    }
}
