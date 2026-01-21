package com.bank.dao;

import com.bank.db.DBConnection;
import java.sql.*;   

public class CustomerDAO {

    // Add a new customer
    public void addCustomer(String accountId, String name, String username, int pin) throws Exception {
        String sql = "INSERT INTO customers (account_id, customer_name, username, pin) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, accountId);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setInt(4, pin);

            ps.executeUpdate();
            System.out.println("Customer added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all customers
    public void viewCustomers() throws Exception{
        String sql = "SELECT * FROM customers";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("Customers:");
            while (rs.next()) {
                System.out.println(
                    rs.getString("account_id") + " | " +
                    rs.getString("customer_name") + " | " +
                    rs.getString("username") + " | " +
                    rs.getInt("pin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update an existing customer by account ID
    public void updateCustomer(String accountId, String name, String username, int pin) throws Exception {
        String sql = "UPDATE customers SET customer_name = ?, username = ?, pin = ? WHERE account_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, username);
            ps.setInt(3, pin);
            ps.setString(4, accountId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("No customer found with account ID: " + accountId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a customer by account ID
    public void deleteCustomer(String accountId) throws Exception {
        String sql = "DELETE FROM customers WHERE account_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("No customer found with account ID: " + accountId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
