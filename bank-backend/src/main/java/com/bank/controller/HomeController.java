package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // ===== LABELS =====
    @FXML private Label usernamelabel;
    @FXML private Label custoomername;
    @FXML private Label accnum;
    @FXML private Label balanceamt;
    @FXML private Label baldate;

    // ===== BUTTONS =====
    @FXML private Button blippitix;
    @FXML private Button blippitransac1;
    @FXML private Button blippitransac;

    // ===== DATA =====
    private String customerId;
    private Connection conn;

    // ===============================
    // CALLED AUTOMATICALLY WHEN FXML LOADS
    // ===============================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Static UI data
        baldate.setText(
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
        );

        baltime.setText(
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
    }

    // ===============================
    // CALLED BY LOGIN CONTROLLER
    // ===============================
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        System.out.println("Logged in customer: " + customerId);
        loadAccountData();
    }

    // ===============================
    // LOAD USER ACCOUNT DATA FROM DB
    // ===============================
    private void loadAccountData() {

        String sql = """
            SELECT 
                c.firstname,
                c.lastname,
                a.account_id,
                a.balance,
                a.expiry_date
            FROM customers c
            JOIN accounts a ON c.customer_id = a.customer_id
            WHERE c.customer_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                String accountId = rs.getString("account_id");
                double balance = rs.getDouble("balance");
                String expiry = rs.getString("expiry_date");

                // Update UI
                usernamelabel.setText(fullName);
                cardlabel.setText(fullName);
                accnum.setText(accountId);
                balanceamt.setText(String.format("%.2f", balance));
                expdate.setText(expiry);

            } else {
                usernamelabel.setText("No account found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // BUTTON HANDLERS
    // ===============================
    @FXML
    private void blippitixHandler() {
        System.out.println("Transfer Money clicked");
    }

    @FXML
    private void blippitransacHandler() {
        System.out.println("Transaction History clicked");
    }
}
