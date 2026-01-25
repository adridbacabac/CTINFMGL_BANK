package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterAccountController {

    @FXML
    private ChoiceBox<String> fareChoice;   // Account Type

    @FXML
    private ChoiceBox<String> fareChoice1;  // Status

    @FXML
    private Button loginButton;             // Register Account button

    private Connection conn;

    // Customer ID passed from CreateCustomerController
    private String customerId;

    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally show alert here for connection failure
        }

        fareChoice.setItems(FXCollections.observableArrayList("Debit", "Credit", "Current"));
        fareChoice1.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        fareChoice.getSelectionModel().selectFirst();
        fareChoice1.getSelectionModel().selectFirst();
    }


    // Setter to receive customer ID from previous screen
    public void setCustomerId(String id) {
        this.customerId = id;
    }

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

    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String accountType = fareChoice.getValue();
        String status = fareChoice1.getValue();

        if (accountType == null || status == null) {
            showAlert("Please select account type and status.");
            return;
        }

        if (customerId == null || customerId.isEmpty()) {
            showAlert("Customer ID missing. Please create a customer first.");
            return;
        }

        String sql = "INSERT INTO accounts (customer_id, account_type, status) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, accountType);
            pstmt.setString(3, status);
            pstmt.executeUpdate();

            showAlert("Account registered successfully!");

            // Load and show the login page after successful registration
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


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
