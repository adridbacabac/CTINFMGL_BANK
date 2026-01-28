package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    /** JDBC URL for the banking database */
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    /** Database username */
    private static final String USER = "root";
    /** Database password */
    private static final String PASSWORD = "Mm041114!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
