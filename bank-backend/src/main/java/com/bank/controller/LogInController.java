package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * LogInController
 * Handles user authentication for the banking application.
 * Validates credentials and navigates authenticated users to the home page.
 */
public class LogInController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Input field for username */
    @FXML
    private TextField numberField;
    /** Input field for PIN (password) */
    @FXML
    private PasswordField passwordField;
    /** "Login" button */
    @FXML
    private Button loginButton;
    /** "Sign Up" link button */
    @FXML
    private Button signUpLink;

    // ===== DATA MEMBERS =====
    /** Database connection */
    private Connection conn;

    // ===== INITIALIZATION =====

    /**
     * Initializes the controller after FXML is loaded
     * Establishes database connection
     */
    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database connection failed.");
        }
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles login when "Login" button is clicked
     * Authenticates user credentials against database and navigates to home page on success
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String username = numberField.getText().trim();
        String pin = passwordField.getText().trim();

        // Validate both fields are filled
        if (username.isEmpty() || pin.isEmpty()) {
            showAlert("Please enter both username and PIN.");
            return;
        }

        // Query database for matching credentials
        String sql = "SELECT customer_id FROM customers WHERE username = ? AND pin = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, pin);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                goToHome(event, customerId);
            } else {
                showAlert("Invalid username or PIN.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Login error: " + e.getMessage());
        }
    }

    /**
     * Navigates to customer registration page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void signUpLinkHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateCustomer.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signUpLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Customer");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load registration page.");
        }
    }

    // ===== NAVIGATION =====

    /**
     * Navigates to Home page with authenticated customer ID
     *
     * @param event ActionEvent from button click
     * @param customerId the authenticated customer ID
     */
    private void goToHome(ActionEvent event, String customerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load home page.");
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
