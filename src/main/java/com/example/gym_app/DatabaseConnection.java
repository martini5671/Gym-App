package com.example.gym_app;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseConnection {
    public Connection connection;

    public Connection getConnection() {
        String url = "url_string";
        String username = "USERNAMEE";
        String password = "PASSWORD";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
        return connection;
    }
}