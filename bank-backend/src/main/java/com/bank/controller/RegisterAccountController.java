package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * RegisterAccountController
 * Handles account registration for newly created customers.
 * Allows selection of account type and creates account record with default "Active" status.
 */
public class RegisterAccountController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Choice box for selecting account type (Debit) */
    @FXML
    private ChoiceBox<String> fareChoice;
    /** "Register Account" button */
    @FXML
    private Button loginButton;

    // ===== DATA MEMBERS =====
    /** Database connection */
    private Connection conn;
    /** Customer ID passed from CreateCustomerController */
    private String customerId;

    // ===== INITIALIZATION =====

    /**
     * Initializes the controller after FXML is loaded
     * Establishes database connection and populates account type dropdown
     */
    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to connect to the database.");
        }

        // Populate account type options
        fareChoice.setItems(FXCollections.observableArrayList("Debit"));
        fareChoice.getSelectionModel().selectFirst();
    }

    // ===== DATA SETUP =====

    /**
     * Sets the customer ID passed from CreateCustomerController
     *
     * @param id the customer ID
     */
    public void setCustomerId(String id) {
        this.customerId = id;
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles account registration when "Register Account" button is clicked
     * Validates selection, inserts account into database with default "Active" status, and navigates to login page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String accountType = fareChoice.getValue();

        // Validate account type is selected
        if (accountType == null) {
            showAlert("Please select account type.");
            return;
        }

        // Validate customer ID is available
        if (customerId == null || customerId.isEmpty()) {
            showAlert("Customer ID missing. Please create a customer first.");
            return;
        }

        // Insert account into database with default "Active" status
        String sql = "INSERT INTO accounts (customer_id, account_type, status) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, accountType);
            pstmt.setString(3, "Active");  // Default status

            pstmt.executeUpdate();

            showAlert("Account registered successfully!");

            // Navigate to login page after successful registration
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogIn.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Log In");
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to register account: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load login page: " + e.getMessage());
        }
    }

    /**
     * Navigates to Login page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void goToLoginPage(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogIn.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Log In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load login page.");
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Displays an information alert dialog
     *
     * @param message the alert message
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}