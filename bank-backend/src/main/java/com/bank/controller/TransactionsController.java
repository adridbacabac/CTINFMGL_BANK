package com.bank.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
    private TableColumn<Transaction, String> transactionIdColumn;

    @FXML
    private TableColumn<Transaction, String> senderIdColumn;

    @FXML
    private TableColumn<Transaction, String> recipientIdColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    // DATABASE CONFIG
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @FXML
    private void initialize() {
        setupTable();
        loadTransactions();
    }

    // BACK → HOME
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    // ================= TABLE SETUP =================

    private void setupTable() {
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        senderIdColumn.setCellValueFactory(new PropertyValueFactory<>("senderId"));
        recipientIdColumn.setCellValueFactory(new PropertyValueFactory<>("recipientId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    // ================= DATABASE =================

    private void loadTransactions() {
        String sql = "SELECT transaction_id, sender_id, recipient_id, amount, transaction_date FROM transactions";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

    // ================= UI =================

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // ================= MODEL =================

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
