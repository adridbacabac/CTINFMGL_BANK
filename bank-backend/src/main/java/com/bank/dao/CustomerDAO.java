package com.bank.dao;

import com.bank.model.Customer;
import com.bank.util.DBConnection;

import java.sql.*;

public class CustomerDAO {

    // CREATE CUSTOMER
    public static Customer createCustomer(String name, String username, String pin) throws SQLException {
        String getNextId = "SELECT COUNT(*) + 1 FROM customers";
        String insertSQL = "INSERT INTO customers (customer_id, customer_name, username, pin) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(getNextId)) {

            rs.next();
            String customerId = "C" + String.format("%02d", rs.getInt(1));

            PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customerId);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setString(4, pin);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            keys.next();

            return new Customer(
                    keys.getInt(1),
                    customerId,
                    name,
                    username,
                    pin
            );
        }
    }

    // LOGIN
    public static Customer login(String username, String pin) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ? AND pin = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("username"),
                        rs.getString("pin")
                );
            }
        }
        return null;
    }

    // UPDATE
    public static void updateCredentials(String customerId, String username, String pin) throws SQLException {
        String sql = "UPDATE customers SET username = ?, pin = ? WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, pin);
            ps.setString(3, customerId);
            ps.executeUpdate();
        }
    }

    // DELETE
    public static void deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ps.executeUpdate();
        }
    }
}
