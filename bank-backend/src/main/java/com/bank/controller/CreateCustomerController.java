package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateCustomerController {

    @FXML
    private TextField numberField;   // Customer Name
    
    @FXML
    private TextField numberField1;  // Username
    
    @FXML
    private TextField numberField2;  // PIN
    
    @FXML
    private Button loginButton;      // "Next" button
    
    private Connection conn;
    
    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to connect to the database.");
        }
    }
    
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String name = numberField.getText().trim();
        String username = numberField1.getText().trim();
        String pin = numberField2.getText().trim();

        if (name.isEmpty() || username.isEmpty() || pin.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        if (!pin.matches("\\d{4}")) {
            showAlert("PIN must be exactly 4 digits.");
            return;
        }

        String sql = "INSERT INTO customers (customer_name, username, pin) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, pin);
            pstmt.executeUpdate();
            
            showAlert("Customer created successfully!");
            clearFields();

            // TODO: Navigate to RegisterAccount page here
            // For example, load RegisterAccount.fxml and pass customer info

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to create customer: " + e.getMessage());
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void clearFields() {
        numberField.clear();
        numberField1.clear();
        numberField2.clear();
    }
}
