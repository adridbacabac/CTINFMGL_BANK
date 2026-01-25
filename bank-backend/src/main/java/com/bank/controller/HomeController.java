package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // ===== LABELS FROM FXML =====
    @FXML private Label usernamelabel;     // "Hello, <name>"
    @FXML private Label accnum;            // account_id
    @FXML private Label custoomername;     // customer_name
    @FXML private Label baldate;           // current date
    @FXML private Label balanceamt;        // balance

    // ===== DATA =====
    private String customerId;

    private Connection conn;

    // Handler for HELP & SUPPORT button
    @FXML
    private void blippitransacHandler() {
        System.out.println("Help & Support clicked");
        // TODO: add actual code
    }

    // Handler for TRANSFER MONEY button
    @FXML
    private void blippitixHandler() {
        System.out.println("Transfer Money clicked");
        // TODO: add actual code
    }

    // Handler for TRANSACTION HISTORY button
    @FXML
    private void blippitransac1Handler() {
        System.out.println("Transaction History clicked");
        // TODO: add actual code
    }

    /**
     * CALLED FROM LoginController AFTER SUCCESSFUL LOGIN
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        loadAccountData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        baldate.setText(
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            )
        );
    }

    /**
     * LOAD USER DATA FROM DATABASE
     */
    private void loadAccountData() {

        String sql = """
                SELECT customer_name, account_id, balance
                FROM customers
                WHERE customer_id = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String customerName = rs.getString("customer_name");
                String accountId = rs.getString("account_id");
                double balance = rs.getDouble("balance");

                // ===== UPDATE UI =====
                usernamelabel.setText(customerName);
                custoomername.setText(customerName);
                accnum.setText(accountId);
                balanceamt.setText(String.format("%.2f", balance));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
