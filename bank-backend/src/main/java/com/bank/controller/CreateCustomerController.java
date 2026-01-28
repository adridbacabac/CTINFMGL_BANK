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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CreateCustomerController
 * Handles customer registration during the account creation process.
 * Validates input, creates customer record in database, and navigates to account registration.
 */
public class CreateCustomerController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Input field for customer full name */
    @FXML
    private TextField numberField;
    /** Input field for username */
    @FXML
    private TextField numberField1;
    /** Input field for 4-digit PIN */
    @FXML
    private TextField numberField2;
    /** "Next" button to proceed with registration */
    @FXML
    private Button loginButton;

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
            showAlert("Failed to connect to the database.");
        }
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles customer creation when "Next" button is clicked
     * Validates input, inserts customer into database, and navigates to account registration
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String name = numberField.getText().trim();
        String username = numberField1.getText().trim();
        String pin = numberField2.getText().trim();

        // Validate all fields are filled
        if (name.isEmpty() || username.isEmpty() || pin.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        // Validate PIN format (exactly 4 digits)
        if (!pin.matches("\\d{4}")) {
            showAlert("PIN must be exactly 4 digits.");
            return;
        }

        // Insert customer into database
        String sql = "INSERT INTO customers (customer_name, username, pin) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, pin);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to create customer: " + e.getMessage());
        }

        // Retrieve the newly created customer ID
        String fetchSql = """
            SELECT customer_id
            FROM customers
            WHERE username = ?
            ORDER BY id DESC
            LIMIT 1
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(fetchSql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                showAlert("Customer created successfully!");
                clearFields();
                goToRegisterAccountPage(event, customerId);
            } else {
                showAlert("Failed to retrieve customer ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to create customer: " + e.getMessage());
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

    // ===== NAVIGATION =====

    /**
     * Navigates to Register Account page with the new customer ID
     *
     * @param event ActionEvent from button click
     * @param customerId the newly created customer ID
     */
    private void goToRegisterAccountPage(ActionEvent event, String customerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterAccount.fxml"));
            Parent root = loader.load();

            RegisterAccountController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register Account");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Register Account page.");
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

    /**
     * Clears all input fields
     */
    private void clearFields() {
        numberField.clear();
        numberField1.clear();
        numberField2.clear();
    }
}
