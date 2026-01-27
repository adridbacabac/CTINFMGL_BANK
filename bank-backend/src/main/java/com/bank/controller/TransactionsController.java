package com.bank.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class TransactionsController {

    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> transactionIdCol;
    @FXML
    private TableColumn<Transaction, String> fromAccountCol;
    @FXML
    private TableColumn<Transaction, String> toAccountCol;
    @FXML
    private TableColumn<Transaction, Double> amountCol;
    @FXML
    private TableColumn<Transaction, String> dateCol;


    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    // DB config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

    private String customerId;
    private String accountId; // user's account ID

    // Call this from HomeController to set logged in user
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        loadAccountId();
        loadTransactions();
    }

    @FXML
    private void initialize() {
        setupTable();
        // Don't load transactions here, wait for customerId to be set
    }

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

    private void setupTable() {
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        fromAccountCol.setCellValueFactory(new PropertyValueFactory<>("senderId"));
        toAccountCol.setCellValueFactory(new PropertyValueFactory<>("recipientId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadTransactions() {
        if (accountId == null) {
            System.out.println("No account found for customerId: " + customerId);
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

    @FXML
    private void handleBackButton(ActionEvent event){
        System.out.println("Back button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Home.fxml")
            );
            Parent root = loader.load();

            HomeController controller = loader.getController();
            controller.setCustomerId(customerId); // Pass session/customer ID

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Transaction {
        private final String transactionId;
        private final String senderId;
        private final String recipientId;
        private final Double amount;
        private final String date;

        public Transaction(String transactionId, String senderId, String recipientId, Double amount, String date) {
            this.transactionId = transactionId;
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.amount = amount;
            this.date = date;
        }

        public String getTransactionId() { return transactionId; }
        public String getSenderId() { return senderId; }
        public String getRecipientId() { return recipientId; }
        public Double getAmount() { return amount; }
        public String getDate() { return date; }
    }
}
