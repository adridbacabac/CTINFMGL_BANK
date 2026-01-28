package com.bank.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MoneyTransferController
 * Handles money transfer functionality including recipient validation and transfer initiation.
 * Validates recipient account ID, name, and transfer amount before proceeding to confirmation.
 */
public class MoneyTransferController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Input field for transfer amount */
    @FXML
    private TextField amountField;
    /** Input field for recipient account ID */
    @FXML
    private TextField accountidField;
    /** Input field for recipient name verification */
    @FXML
    private TextField recipientField;

    // ===== DATA MEMBERS =====
    /** Currently logged-in customer ID */
    private String customerId;

    // ===== DATABASE CONFIGURATION =====
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    // ===== DATA SETUP =====

    /**
     * Sets the customer ID of the currently logged-in user
     *
     * @param customerId the customer ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles money transfer initiation when "Proceed" button is clicked
     * Validates recipient account ID, name verification, and transfer amount
     * Navigates to confirmation page if all validations pass
     *
     * @param event ActionEvent from button click
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void handleProceedTransfer(ActionEvent event) throws IOException {
        String recipientId = accountidField.getText().trim();
        String enteredName = recipientField.getText().trim();
        double amount;

        // Validate recipient account ID
        if (recipientId.isEmpty()) {
            showAlert("Invalid Recipient", "Please enter recipient account ID.");
            return;
        }

        // Validate recipient name is provided
        if (enteredName.isEmpty()) {
            showAlert("Invalid Recipient", "Please enter recipient name.");
            return;
        }

        // Validate and parse transfer amount
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

        // Verify recipient account exists and name matches
        try {
            String actualName = fetchRecipientName(recipientId);

            if (actualName == null) {
                showAlert("Invalid Recipient", "Account ID does not exist.");
                return;
            }

            // Validate entered name matches account name
            if (!actualName.equalsIgnoreCase(enteredName)) {
                showAlert(
                    "Recipient Mismatch",
                    "Recipient name does not match the account ID.\n\n" +
                    "Please check the details and try again."
                );
                return;
            }

            // All validations passed - proceed to confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfirmationPage.fxml"));
            Parent root = loader.load();

            ConfirmationPageController controller = loader.getController();
            controller.setData(customerId, recipientId, amount);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirm Transfer");
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to validate recipient.");
        }
    }

    /**
     * Navigates back to Home page
     *
     * @param event ActionEvent from button click
     */
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

    // ===== DATABASE OPERATIONS =====

    /**
     * Fetches the customer name for a given account ID
     *
     * @param accountId the recipient's account ID
     * @return the customer name, or null if account not found
     * @throws SQLException if database access fails
     */
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

    // ===== UTILITY METHODS =====

    /**
     * Displays an error alert dialog
     *
     * @param title the alert title
     * @param message the alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
