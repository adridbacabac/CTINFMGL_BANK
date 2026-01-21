package com.bank.dao;

import com.bank.db.DBConnection;
import java.sql.*;

public class TransactionDAO {

    public void transferMoney(
            String fromAcc, String toAcc, int amount, String transId) throws Exception {

        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);

        try {
            // deduct
            PreparedStatement deduct = con.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE account_id=?");
            deduct.setInt(1, amount);
            deduct.setString(2, fromAcc);
            deduct.executeUpdate();

            // add
            PreparedStatement add = con.prepareStatement(
                    "UPDATE accounts SET balance = balance + ? WHERE account_id=?");
            add.setInt(1, amount);
            add.setString(2, toAcc);
            add.executeUpdate();

            // insert transaction record
            PreparedStatement trans = con.prepareStatement(
                    "INSERT INTO transactions VALUES (?, ?, ?, ?, CURDATE(), 'online')");
            trans.setString(1, transId);
            trans.setString(2, fromAcc);
            trans.setString(3, toAcc);
            trans.setInt(4, amount);
            trans.executeUpdate();

            con.commit();
            System.out.println("Transfer successful!");

        } catch (Exception e) {
            con.rollback();
            System.out.println("Transfer failed. Rolled back.");
        }
    }
}
