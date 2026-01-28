package com.bank.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * TransactionsController
 * Displays transaction history for the logged-in customer.
 * Shows all transactions where customer is either sender or recipient.
 */
public class TransactionsController {

    // ===== UI COMPONENTS FROM FXML =====
    /** Table view for displaying transaction history */
    @FXML
    private TableView<Transaction> transactionsTable;
    /** Column for transaction ID */
    @FXML
    private TableColumn<Transaction, String> transactionIdCol;
    /** Column for sender account ID */
    @FXML
    private TableColumn<Transaction, String> fromAccountCol;
    /** Column for recipient account ID */
    @FXML
    private TableColumn<Transaction, String> toAccountCol;
    /** Column for transaction amount */
    @FXML
    private TableColumn<Transaction, Double> amountCol;
    /** Column for transaction date */
    @FXML
    private TableColumn<Transaction, String> dateCol;

    // ===== DATA MEMBERS =====
    /** List of transactions to display in table */
    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    /** Currently logged-in customer ID */
    private String customerId;
    /** Customer's account ID */
    private String accountId;

    // ===== DATABASE CONFIGURATION =====
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    // ===== INITIALIZATION & DATA SETUP =====

    /**
     * Initializes the controller after FXML is loaded
     * Sets up table columns and their data bindings
     */
    @FXML
    private void initialize() {
        setupTable();
    }

    /**
     * Sets the customer ID and loads transaction history
     * Called from HomeController after successful login
     *
     * @param customerId the customer ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        loadAccountId();
        loadTransactions();
    }

    // ===== TABLE SETUP =====

    /**
     * Configures table columns to bind with Transaction object properties
     */
    private void setupTable() {
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        fromAccountCol.setCellValueFactory(new PropertyValueFactory<>("senderId"));
        toAccountCol.setCellValueFactory(new PropertyValueFactory<>("recipientId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    // ===== DATABASE OPERATIONS =====

    /**
     * Retrieves the account ID for the current customer
     */
    private void loadAccountId() {
        String sql = "SELECT account_id FROM accounts WHERE customer_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getString("account_id");
            } else {
                accountId = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all transactions for the current customer
     * Includes transactions where customer is sender or recipient
     */
    private void loadTransactions() {
        if (accountId == null) {
            transactionList.clear();
            transactionsTable.setItems(transactionList);
            return;
        }

        String sql = """
            SELECT transaction_id, from_account AS sender_id, to_account AS recipient_id, amount, transaction_date 
            FROM transactions 
            WHERE from_account = ? OR to_account = ?
            ORDER BY transaction_date DESC
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, accountId);
            ResultSet rs = ps.executeQuery();

            transactionList.clear();
            while (rs.next()) {
                transactionList.add(new Transaction(
                    rs.getString("transaction_id"),
                    rs.getString("sender_id"),
                    rs.getString("recipient_id"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_date")
                ));
            }
            transactionsTable.setItems(transactionList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== EVENT HANDLERS =====

    /**
     * Navigates back to Home page
     *
     * @param event ActionEvent from button click
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
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

    // ===== NESTED TRANSACTION MODEL =====

    /**
     * Transaction
     * Represents a single transaction record with all relevant details.
     */
    public static class Transaction {
        private final String transactionId;
        private final String senderId;
        private final String recipientId;
        private final Double amount;
        private final String date;

        /**
         * Constructs a Transaction object
         *
         * @param transactionId the unique transaction ID
         * @param senderId the sender's account ID
         * @param recipientId the recipient's account ID
         * @param amount the transfer amount
         * @param date the transaction date
         */
        public Transaction(String transactionId, String senderId, String recipientId, Double amount, String date) {
            this.transactionId = transactionId;
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.amount = amount;
            this.date = date;
        }

        // ===== GETTERS =====
        public String getTransactionId() { return transactionId; }
        public String getSenderId() { return senderId; }
        public String getRecipientId() { return recipientId; }
        public Double getAmount() { return amount; }
        public String getDate() { return date; }
    }
}
