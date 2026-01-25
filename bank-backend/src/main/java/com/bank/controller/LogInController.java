package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInController {

    @FXML private TextField numberField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button signUpLink;

    private Connection conn;

    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database connection failed.");
        }
    }

    
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        String username = numberField.getText().trim();
        String pin = passwordField.getText().trim();

        if (username.isEmpty() || pin.isEmpty()) {
            showAlert("Please enter both username and PIN.");
            return;
        }

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

    private void goToHome(ActionEvent event, String customerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCustomerId(customerId); // Must be after load()

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load home page.");
        }
    }


    @FXML
    private void signUpLinkHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CreateCustomer.fxml")
            );
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
