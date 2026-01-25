package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProfileController {

    @FXML private TextField numberField; // new username
    @FXML private TextField pinField;    // new pin
    @FXML private Label usernamelabel;

    private Connection conn;
    private String customerId;

    // 🔥 CALLED FROM HomeController
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        tryLoadUsername();
    }

    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
            tryLoadUsername();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        // Load the previous scene (e.g. Home)
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void loginButtonHandler(ActionEvent event) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        // Get the current window to properly position the alert
        Window window = ((Node) event.getSource()).getScene().getWindow();
        alert.initOwner(window);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User confirmed deletion
                try {
                    String sql = "DELETE FROM customers WHERE customer_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, customerId);
                        int rowsDeleted = pstmt.executeUpdate();

                        if (rowsDeleted > 0) {
                            System.out.println("Account deleted successfully.");

                            // Redirect to login page after deletion
                            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setScene(new Scene(loginRoot));
                            stage.setTitle("Login");
                            stage.show();
                        } else {
                            System.out.println("No matching account found to delete.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Account deletion canceled.");
            }
        });
    }


    // =========================
    // UPDATE USERNAME BUTTON
    // =========================
    @FXML
    private void handleUpdateUsername(ActionEvent event) {
        String newUsername = numberField.getText().trim();

        if (newUsername.isEmpty()) {
            System.out.println("Username cannot be empty");
            return;
        }

        String sql = "UPDATE customers SET username = ? WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, customerId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Username updated");
                usernamelabel.setText(newUsername); // 🔥 update UI immediately
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // UPDATE PIN BUTTON
    // =========================
    @FXML
    private void handleUpdatePin(ActionEvent event) {
        String newPin = pinField.getText().trim();

        if (newPin.isEmpty()) {
            System.out.println("PIN cannot be empty");
            return;
        }

        String sql = "UPDATE customers SET pin = ? WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPin);
            pstmt.setString(2, customerId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("PIN updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD USERNAME
    // =========================
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
}
