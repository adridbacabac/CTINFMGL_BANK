package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UpdateProfileController
 * Handles customer profile management including username updates, PIN changes, and account deletion.
 * Provides validation and security features for profile modifications.
 */
public class UpdateProfileController {

    // ===== DATABASE CONFIGURATION =====
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    // ===== UI COMPONENTS FROM FXML =====
    /** Input field for new username */
    @FXML
    private TextField numberField;
    /** Input field for new PIN */
    @FXML
    private TextField pinField;
    /** Display label for current username */
    @FXML
    private Label usernamelabel;

    // ===== DATA MEMBERS =====
    /** Database connection */
    private Connection conn;
    /** Currently logged-in customer ID */
    private String customerId;

    // ===== INITIALIZATION =====

    /**
     * Initializes the controller after FXML is loaded
     * Establishes database connection and loads current username
     */
    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
            tryLoadUsername();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== DATA SETUP =====

    /**
     * Sets the customer ID and loads username
     * Called from HomeController after successful login
     *
     * @param customerId the customer ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        tryLoadUsername();
    }

    // ===== NAVIGATION =====

    /**
     * Navigates back to Home page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out user and navigates to Login page
     *
     * @param event ActionEvent from button click
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void logoutButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.show();
    }

    // ===== ACCOUNT MANAGEMENT =====

    /**
     * Handles account deletion with confirmation dialog
     * Deletes all related transactions, accounts, and customer record
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        Window window = ((Node) event.getSource()).getScene().getWindow();
        alert.initOwner(window);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    conn.setAutoCommit(false);

                    // Delete all transactions linked to customer's accounts
                    String deleteTransactionsSql = """
                        DELETE t FROM transactions t
                        JOIN accounts a ON t.from_account = a.account_id OR t.to_account = a.account_id
                        WHERE a.customer_id = ?
                        """;
                    try (PreparedStatement ps = conn.prepareStatement(deleteTransactionsSql)) {
                        ps.setString(1, customerId);
                        ps.executeUpdate();
                    }

                    // Delete all accounts
                    String deleteAccountsSql = "DELETE FROM accounts WHERE customer_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteAccountsSql)) {
                        ps.setString(1, customerId);
                        ps.executeUpdate();
                    }

                    // Delete customer
                    String deleteCustomerSql = "DELETE FROM customers WHERE customer_id = ?";
                    int rowsDeleted;
                    try (PreparedStatement ps = conn.prepareStatement(deleteCustomerSql)) {
                        ps.setString(1, customerId);
                        rowsDeleted = ps.executeUpdate();
                    }

                    if (rowsDeleted > 0) {
                        conn.commit();

                        // Redirect to login page after deletion
                        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(loginRoot));
                        stage.setTitle("Login");
                        stage.show();
                    } else {
                        conn.rollback();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ===== USERNAME UPDATE =====

    /**
     * Handles username update when "Update Username" button is clicked
     * Validates username format and length before updating database
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleUpdateUsername(ActionEvent event) {
        String newUsername = numberField.getText().trim();

        // Validate username is not empty
        if (newUsername.isEmpty()) {
            showError("Invalid Username", "Username cannot be empty.");
            return;
        }

        // Validate minimum length
        if (newUsername.length() < 3) {
            showError("Invalid Username", "Username must be at least 3 characters long.");
            return;
        }

        // Validate allowed characters (alphanumeric and underscore)
        if (!newUsername.matches("^[a-zA-Z0-9_]+$")) {
            showError("Invalid Username", "Username can only contain letters, numbers, and underscores (_).");
            return;
        }

        String sql = "UPDATE customers SET username = ? WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, customerId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                usernamelabel.setText(newUsername);
                showSuccess("Username Updated", "Your username was updated successfully.");
            } else {
                showError("Update Failed", "Username was not updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Something went wrong while updating username.");
        }
    }

    // ===== PIN UPDATE =====

    /**
     * Handles PIN update when "Update PIN" button is clicked
     * Validates PIN format (4 digits) and logs out user after successful update
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleUpdatePin(ActionEvent event) {
        String newPin = pinField.getText().trim();

        // Validate PIN is not empty
        if (newPin.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid PIN", "PIN field is empty", "Please enter a 4-digit PIN.");
            return;
        }

        // Validate PIN is exactly 4 digits
        if (!newPin.matches("\\d{4}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid PIN", "Incorrect PIN format", "PIN must contain exactly 4 numbers.");
            return;
        }

        String sql = "UPDATE customers SET pin = ? WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPin);
            pstmt.setString(2, customerId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "PIN Updated", "PIN updated successfully",
                    "For security reasons, you will be logged out.");

                // Logout and redirect to login page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update PIN", "Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== DATABASE OPERATIONS =====

    /**
     * Loads and displays the current username from database
     */
    private void tryLoadUsername() {
        if (conn == null || customerId == null) return;

        String sql = "SELECT username FROM customers WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                usernamelabel.setText(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Displays an error alert dialog
     *
     * @param title the alert title
     * @param message the alert message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a success alert dialog
     *
     * @param title the alert title
     * @param message the alert message
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an alert dialog with custom title, header, and message
     *
     * @param type the alert type
     * @param title the alert title
     * @param header the alert header
     * @param message the alert message
     */
    private void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
