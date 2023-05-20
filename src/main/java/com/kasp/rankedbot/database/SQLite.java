package com.kasp.rankedbot.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite {

    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        connection = null;
        try {
            File file = new File("data.db");
            if (!file.exists()) {
                file.createNewFile();
            }

            String link = "jdbc:sqlite:" + file.getPath();

            connection = DriverManager.getConnection(link);
            statement = connection.createStatement();
            System.out.println("Successfully connected to the database");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateData(String sql) {
        if (statement != null) {
            try {
                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultSet queryData(String sql) {
        if (statement != null) {
            try {
                return statement.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
