package com.bank.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * ConfirmationPageController
 * Displays transaction confirmation details before sending money.
 * Shows sender, recipient, and transfer amount information.
 * Handles transaction processing and receipt generation.
 */
public class ConfirmationPageController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Displays recipient customer name */
    @FXML
    private Label recipient;
    /** Displays recipient account ID */
    @FXML
    private Label recipientAccID;
    /** Displays sender account ID */
    @FXML
    private Label senderAccID;
    /** Displays transfer amount */
    @FXML
    private Label totalTransferred;

    // ===== DATA MEMBERS =====
    /** Currently logged-in customer ID */
    private String customerId;
    /** Recipient account ID */
    private String recipientId;
    /** Transfer amount */
    private double amount;

    // ===== DATABASE CONFIGURATION =====
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    // ===== INITIALIZATION & DATA SETUP =====

    /**
     * Sets transfer confirmation data and populates UI labels
     * Called from MoneyTransferController after user initiates transfer
     *
     * @param customerId the sender's customer ID
     * @param recipientAccountId the recipient's account ID
     * @param amount the transfer amount
     */
    public void setData(String customerId, String recipientAccountId, double amount) {
        this.customerId = customerId;
        this.recipientId = recipientAccountId;
        this.amount = amount;

        String senderAccountId = fetchAccountIdForCustomer(customerId);
        senderAccID.setText(senderAccountId != null ? senderAccountId : "Account not found");
        recipientAccID.setText(recipientAccountId);

        String recipientName = fetchRecipientName(recipientAccountId);
        recipient.setText(recipientName != null ? recipientName : "Unknown recipient");
        totalTransferred.setText(String.format("%.2f", amount));
    }

    // ===== DATABASE OPERATIONS =====

    /**
     * Fetches the account ID for a given customer ID
     *
     * @param customerId the customer ID
     * @return the account ID, or null if not found
     */
    private String fetchAccountIdForCustomer(String customerId) {
        String accountId = null;
        String sql = "SELECT account_id FROM accounts WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                accountId = rs.getString("account_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accountId;
    }

    /**
     * Fetches the customer name for a given account ID
     *
     * @param recipientAccountId the recipient's account ID
     * @return the customer name, or null if not found
     */
    private String fetchRecipientName(String recipientAccountId) {
        String name = null;
        String sql = """
            SELECT c.customer_name
            FROM accounts a
            JOIN customers c ON a.customer_id = c.customer_id
            WHERE a.account_id = ?
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipientAccountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                name = rs.getString("customer_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    // ===== EVENT HANDLERS =====

    /**
     * Navigates back to Money Transfer page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MoneyTransfer.fxml"));
            Parent root = loader.load();

            MoneyTransferController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Money Transfer");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to Home page, canceling the transaction
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleCancelButton(ActionEvent event) {
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
     * Processes the money transfer transaction
     * Validates balance, updates accounts, inserts transaction record, and shows receipt
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleSendMoney(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            String senderAccountId = getSenderAccountId(conn, customerId);
            if (senderAccountId == null) {
                showAlert("Error", "Sender account not found.");
                return;
            }

            double senderBalance = getBalance(conn, senderAccountId);
            if (senderBalance < amount) {
                showAlert("Insufficient Balance", "Not enough funds.");
                return;
            }

            if (!accountExists(conn, recipientId)) {
                showAlert("Error", "Recipient account does not exist.");
                return;
            }

            updateBalance(conn, senderAccountId, -amount);
            updateBalance(conn, recipientId, amount);

            String transactionId = insertTransaction(conn, senderAccountId, recipientId, amount);
            conn.commit();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransactionReceipt.fxml"));
            Parent root = loader.load();

            TransactionReceiptController controller = loader.getController();
            controller.setReceiptData(
                customerId,
                fetchRecipientName(recipientId),
                recipientId,
                senderAccountId,
                amount,
                transactionId,
                LocalDateTime.now()
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction Receipt");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Transaction Failed", e.getMessage());
        }
    }

    /**
     * Inserts a transaction record and returns the generated transaction ID
     *
     * @param conn the database connection
     * @param fromAccount the sender's account ID
     * @param toAccount the recipient's account ID
     * @param amount the transfer amount
     * @return the generated transaction ID
     * @throws Exception if transaction insertion fails
     */
    private String insertTransaction(Connection conn, String fromAccount, String toAccount, double amount) throws Exception {
        String sql = """
            INSERT INTO transactions (from_account, to_account, amount, transaction_date)
            VALUES (?, ?, ?, NOW())
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fromAccount);
            ps.setString(2, toAccount);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long autoId = rs.getLong(1);
                String fetchSql = "SELECT transaction_id FROM transactions WHERE id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(fetchSql)) {
                    ps2.setLong(1, autoId);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        return rs2.getString("transaction_id");
                    }
                }
            }
        }

        throw new Exception("Transaction ID not generated.");
    }

    /**
     * Gets the account ID for a given customer
     *
     * @param conn the database connection
     * @param customerId the customer ID
     * @return the account ID, or null if not found
     * @throws Exception if database access fails
     */
    private String getSenderAccountId(Connection conn, String customerId) throws Exception {
        String sql = "SELECT account_id FROM accounts WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("account_id");
            }
        }
        return null;
    }

    /**
     * Retrieves the current balance for an account
     *
     * @param conn the database connection
     * @param accountId the account ID
     * @return the current balance
     * @throws Exception if database access fails
     */
    private double getBalance(Connection conn, String accountId) throws Exception {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }

    /**
     * Checks if an account exists
     *
     * @param conn the database connection
     * @param accountId the account ID
     * @return true if account exists, false otherwise
     * @throws Exception if database access fails
     */
    private boolean accountExists(Connection conn, String accountId) throws Exception {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    /**
     * Updates account balance by adding/subtracting amount
     *
     * @param conn the database connection
     * @param accountId the account ID
     * @param amount the amount to add (negative to subtract)
     * @throws Exception if database access fails
     */
    private void updateBalance(Connection conn, String accountId, double amount) throws Exception {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }

    /**
     * Displays an error alert dialog
     *
     * @param title the alert title
     * @param message the alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
