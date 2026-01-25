package com.bank.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MoneyTransferController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField recipientFiled;

    @FXML
    private TextField accountidField;

    // TEMP: replace with logged-in user later
    private final String senderAccountId = "A001";

    // DATABASE CONFIG
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // BACK → /fxml/Home.fxml
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    // SEND MONEY → /fxml/ConfirmationPage.fxml
    @FXML
    private void handleSendMoney(ActionEvent event) {
        String recipientId = accountidField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid amount.");
            return;
        }

        if (amount <= 0) {
            showAlert("Invalid Amount", "Amount must be greater than zero.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            conn.setAutoCommit(false);

            // CHECK SENDER BALANCE
            double senderBalance = getBalance(conn, senderAccountId);
            if (senderBalance < amount) {
                showAlert("Insufficient Balance", "Not enough balance.");
                return;
            }

            // CHECK RECIPIENT
            if (!accountExists(conn, recipientId)) {
                showAlert("Account Not Found", "Recipient does not exist.");
                return;
            }

            // UPDATE BALANCES
            updateBalance(conn, senderAccountId, -amount);
            updateBalance(conn, recipientId, amount);

            conn.commit();

            switchScene(event, "/fxml/ConfirmationPage.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Transaction Failed", "An error occurred.");
        }
    }

    // ================== DATABASE HELPERS ==================

    private double getBalance(Connection conn, String accountId) throws Exception {
        String sql = "SELECT balance FROM account WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }

    private boolean accountExists(Connection conn, String accountId) throws Exception {
        String sql = "SELECT 1 FROM account WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private void updateBalance(Connection conn, String accountId, double amount) throws Exception {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }

    // ================== UI HELPERS ==================

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
