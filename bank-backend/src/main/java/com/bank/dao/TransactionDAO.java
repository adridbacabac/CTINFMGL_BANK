package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public static void transfer(String from, String to, double amount) throws SQLException {
        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);

        try {
            String getId = "SELECT COUNT(*) + 1 FROM transactions";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getId);
            rs.next();

            String tid = "T" + String.format("%02d", rs.getInt(1));

            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO transactions (transaction_id, from_account, to_account, amount, transaction_date)
                VALUES (?, ?, ?, ?, NOW())
            """);

            ps.setString(1, tid);
            ps.setString(2, from);
            ps.setString(3, to);
            ps.setDouble(4, amount);
            ps.executeUpdate();

            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw e;
        }
    }

    public static List<Transaction> getTransactions(String accountId) throws SQLException {
        List<Transaction> list = new ArrayList<>();

        String sql = """
            SELECT * FROM transactions
            WHERE from_account = ? OR to_account = ?
            ORDER BY transaction_date DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, accountId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("from_account"),
                        rs.getString("to_account"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("transaction_date").toLocalDateTime()
                ));
            }
        }
        return list;
    }
}
