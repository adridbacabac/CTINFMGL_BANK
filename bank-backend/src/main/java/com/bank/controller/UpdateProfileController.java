package com.bank.controller;

import com.bank.util.DBConnection; // Your DB connection utility
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateProfileController {

    @FXML private TextField numberField;   // New Username
    @FXML private TextField pinField;      // New PIN
    @FXML private Label usernamelabel;

    private Connection conn;
    private String customerId;  // You need to set this when opening this screen

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        System.out.println("customerId received in UpdateProfile: " + customerId);
        loadUsername();   // 🔥 THIS is mandatory
    }




    @FXML
    public void initialize() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // BACK BUTTON → Home.fxml
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    // CONFIRM UPDATE BUTTON
    @FXML
    private void loginButtonHandler(ActionEvent event) throws IOException {
        String newUsername = numberField.getText();
        String newPin = pinField.getText();

        if (newUsername.isEmpty() || newPin.isEmpty()) {
            System.out.println("Username or PIN cannot be empty");
            return;
        }

        try {
            String sql = """
                UPDATE accounts
                SET username = ?, pin = ?
                WHERE customer_id = ?
                """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPin);
            pstmt.setString(3, customerId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profile updated successfully");
            } else {
                System.out.println("No matching customer found for update");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // After update → Home
        switchScene(event, "/fxml/Home.fxml");
    }

    private void loadUsername() {
        System.out.println("Loading username for customerId: " + customerId);

        String sql = "SELECT username FROM customers WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                System.out.println("Fetched username: " + username);
                usernamelabel.setText(username);
            } else {
                System.out.println("No user found for customerId: " + customerId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // SCENE SWITCH HELPER
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setUsername(String username) {
        usernamelabel.setText(username);
    }
}
