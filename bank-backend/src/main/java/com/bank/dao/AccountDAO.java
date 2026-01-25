package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.sql.*;

public class AccountDAO {

    public static Account createAccount(String customerId, String type) throws SQLException {
        String countSQL = "SELECT COUNT(*) + 1 FROM accounts";
        String insertSQL = """
            INSERT INTO accounts (account_id, customer_id, account_type, balance, status)
            VALUES (?, ?, ?, 0.0, 'ACTIVE')
        """;

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(countSQL)) {

            rs.next();
            String accountId = "A" + String.format("%02d", rs.getInt(1));

            PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, accountId);
            ps.setString(2, customerId);
            ps.setString(3, type);
            ps.executeUpdate();

            return new Account(0, accountId, customerId, type, 0.0, "ACTIVE");
        }
    }

    public static void updateBalance(String accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }
}
