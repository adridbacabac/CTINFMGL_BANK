package com.bank.controller;

import com.bank.util.DBConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // ===== LABELS FROM FXML =====
    @FXML private Label usernamelabel;     // "Hello, <name>"
    @FXML private Label accnum;            // account_id
    @FXML private Label customername;     // customer_name
    @FXML private Label baldate;           // current date
    @FXML private Label balanceamt;        // balance

    // ===== DATA =====
    private String customerId;

    private Connection conn;

    // Handler for HELP & SUPPORT button
    @FXML
    private void blippitransacHandler() {
        try {
            // Load HelpSupport.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpSupport.fxml"));
            Parent helpSupportRoot = loader.load();

            // Get current stage from any node (for example, from usernamelabel)
            Stage stage = (Stage) usernamelabel.getScene().getWindow();

            // Set the scene to HelpSupport view
            stage.setScene(new Scene(helpSupportRoot));
            stage.setTitle("Help & Support");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load Help & Support page.");
        }
    }

    // Handler for TRANSFER MONEY button
    @FXML
    private void blippitixHandler() {
        System.out.println("Transfer Money clicked");
        // TODO: add actual code
    }

    // Handler for TRANSACTION HISTORY button
    @FXML
    private void blippitransac1Handler() {
        System.out.println("Transaction History clicked");
        // TODO: add actual code
    }

    /**
     * CALLED FROM LoginController AFTER SUCCESSFUL LOGIN
     */
    public void setCustomerId(String customerId) {
        System.out.println("setCustomerId called with: " + customerId);
        this.customerId = customerId;
        loadAccountData();
    }

    @FXML
    private void handleProfileButtonAction(ActionEvent event) {
        System.out.println("PROFILE BUTTON CLICKED"); // 👈 DEBUG

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/UpdateProfile.fxml")
            );
            Parent root = loader.load();

            UpdateProfileController controller = loader.getController();
            controller.setCustomerId(customerId); // ✅ PASS LOGGED-IN USER

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        baldate.setText(
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            )
        );
    }

    /**
     * LOAD USER DATA FROM DATABASE
     */
    private void loadAccountData() {
        System.out.println("Loading data for customerId: " + customerId);

        String sql = """
                SELECT c.customer_name, a.account_id, a.balance
                FROM customers c
                JOIN accounts a ON c.customer_id = a.customer_id
                WHERE c.customer_id = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String customerName = rs.getString("customer_name");
                String accountId = rs.getString("account_id");
                double balance = rs.getDouble("balance");

                System.out.println("Fetched from DB - customerName: " + customerName +
                                ", accountId: " + accountId + ", balance: " + balance);

                // Update UI labels
                usernamelabel.setText(customerName);
                customername.setText(customerName);
                accnum.setText(accountId);
                balanceamt.setText(String.format("%.2f", balance));
            } else {
                System.out.println("No record found for customerId: " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
