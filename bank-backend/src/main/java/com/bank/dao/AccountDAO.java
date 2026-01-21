package com.bank.dao;

import com.bank.db.DBConnection;
import java.sql.*;

public class AccountDAO {

    // CREATE
    public void addAccount(String id, String type, int balance, String status) throws Exception {
        String sql = "INSERT INTO accounts VALUES (?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, id);
        ps.setString(2, type);
        ps.setInt(3, balance);
        ps.setString(4, status);
        ps.executeUpdate();
    }

    // READ
    public void viewAccounts() throws Exception {
        String sql = "SELECT * FROM accounts";
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            System.out.println(rs.getString("account_id") +
                    " | " + rs.getInt("balance"));
        }
    }

    // UPDATE
    public void updateBalance(String accountId, int newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance=? WHERE account_id=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, newBalance);
        ps.setString(2, accountId);
        ps.executeUpdate();
    }

    // DELETE
    public void deleteAccount(String accountId) throws Exception {
        String sql = "DELETE FROM accounts WHERE account_id=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, accountId);
        ps.executeUpdate();
    }
}
