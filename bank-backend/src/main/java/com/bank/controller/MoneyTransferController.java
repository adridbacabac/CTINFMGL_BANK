package com.bank.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private TextField accountidField;

    @FXML 
    private TextField recipientField;

    private String customerId;

    // DB config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        System.out.println("MoneyTransfer customerId: " + customerId);
    }

    // This is the only handleProceedTransfer method you need here
    @FXML
    private void handleProceedTransfer(ActionEvent event) throws IOException {

        String recipientId = accountidField.getText().trim();
        String enteredName = recipientField.getText().trim();
        double amount;

        // ===== BASIC VALIDATION =====
        if (recipientId.isEmpty()) {
            showAlert("Invalid Recipient", "Please enter recipient account ID.");
            return;
        }

        if (enteredName.isEmpty()) {
            showAlert("Invalid Recipient", "Please enter recipient name.");
            return;
        }

        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert("Invalid Amount", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid amount.");
            return;
        }

        // ===== 🔥 BLOCK WRONG NAME HERE =====
        try {
            String actualName = fetchRecipientName(recipientId);

            if (actualName == null) {
                showAlert("Invalid Recipient", "Account ID does not exist.");
                return;
            }

            if (!actualName.equalsIgnoreCase(enteredName)) {
                showAlert(
                    "Recipient Mismatch",
                    "Recipient name does not match the account ID.\n\n" +
                    "Please check the details and try again."
                );
                return; // ❌ STOP HERE — NO NAVIGATION
            }

            // ===== ONLY PROCEED IF VALID =====
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ConfirmationPage.fxml")
            );
            Parent root = loader.load();

            ConfirmationPageController controller = loader.getController();
            controller.setData(customerId, recipientId, amount);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirm Transfer");
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to validate recipient.");
        }
    }


    private String fetchRecipientName(String accountId) throws SQLException {

        String sql = """
            SELECT c.customer_name
            FROM customers c
            JOIN accounts a ON c.customer_id = a.customer_id
            WHERE a.account_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("customer_name");
            }
        }
        return null;
    }



    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
