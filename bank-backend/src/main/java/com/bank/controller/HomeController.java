package com.bank.controller;

import com.bank.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * HomeController
 * Manages the home dashboard after user login.
 * Displays user information, account details, and navigation to other features.
 */
public class HomeController implements Initializable {

    // ===== UI COMPONENTS FROM FXML =====
    /** Displays greeting with customer name */
    @FXML private Label usernamelabel;
    /** Displays account ID */
    @FXML private Label accnum;
    /** Displays customer full name */
    @FXML private Label customername;
    /** Displays current date */
    @FXML private Label baldate;
    /** Displays account balance */
    @FXML private Label balanceamt;

    // ===== DATA MEMBERS =====
    /** Customer ID of the currently logged-in user */
    private String customerId;
    /** Database connection */
    private Connection conn;

    // ===== EVENT HANDLERS =====

    /**
     * Navigates to Help & Support page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void blippitransacHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/HelpSupport.fxml")
            );
            Parent root = loader.load();

            HelpSupportController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Help & Support");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to Money Transfer page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void blippitixHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/MoneyTransfer.fxml")
            );
            Parent root = loader.load();

            MoneyTransferController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transfer Money");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to Transaction History page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void transacHistoryHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Transactions.fxml"));
            Parent root = loader.load();

            TransactionsController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction History");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to Update Profile page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleProfileButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/UpdateProfile.fxml")
            );
            Parent root = loader.load();

            UpdateProfileController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== INITIALIZATION & DATA SETUP =====

    /**
     * Initializes the controller after FXML is loaded
     * Establishes database connection and displays current date
     *
     * @param url the URL of the FXML file
     * @param resourceBundle the ResourceBundle for localization
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Display current date in the format "Month dd, yyyy"
        baldate.setText(
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            )
        );
    }

    /**
     * Sets the customer ID and loads account data
     * Called from LoginController after successful authentication
     *
     * @param customerId the ID of the logged-in customer
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        loadAccountData();
    }

    // ===== DATABASE OPERATIONS =====

    /**
     * Loads customer and account information from the database
     * Updates all UI labels with fetched data
     */
    private void loadAccountData() {
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

                // Update UI labels with fetched data
                usernamelabel.setText(customerName);
                customername.setText(customerName);
                accnum.setText(accountId);
                balanceamt.setText(String.format("%.2f", balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
