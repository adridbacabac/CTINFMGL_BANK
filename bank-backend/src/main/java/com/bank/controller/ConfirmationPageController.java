package com.bank.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmationPageController {

    @FXML
    private Label recipient;       // for recipient account name

    @FXML
    private Label recipientAccID;  // for recipient account id

    @FXML
    private Label senderAccID;     // for sender account id

    @FXML
    private Label totalTransferred;

    private String customerId;
    private String recipientId;
    private double amount;

    // DB config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mm041114!";

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

    public void setData(String customerId, String recipientAccountId, double amount) {
        this.customerId = customerId;
        this.recipientId = recipientAccountId;
        this.amount = amount;
        // Fetch sender account ID from customerId
        String senderAccountId = fetchAccountIdForCustomer(customerId);

        if (senderAccountId == null) {
            senderAccID.setText("Account not found");
            // Optionally show alert or handle error
        } else {
            senderAccID.setText(senderAccountId);
        }

        recipientAccID.setText(recipientAccountId);
        totalTransferred.setText(String.format("%.2f", amount));

        // If you want recipient name, fetch and set it similarly
        // recipient.setText(fetchRecipientName(recipientAccountId));
    }


    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController controller = loader.getController();
            controller.setCustomerId(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/MoneyTransfer.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

   @FXML
    private void handleSendMoney(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            // 1️⃣ Get sender account ID
            String senderAccountId = getSenderAccountId(conn, customerId);
            if (senderAccountId == null) {
                showAlert("Error", "Sender account not found.");
                return;
            }

            // 2️⃣ Check balance
            double senderBalance = getBalance(conn, senderAccountId);
            if (senderBalance < amount) {
                showAlert("Insufficient Balance", "Not enough funds.");
                return;
            }

            // 3️⃣ Check recipient
            if (!accountExists(conn, recipientId)) {
                showAlert("Error", "Recipient account does not exist.");
                return;
            }

            // 4️⃣ Update balances
            updateBalance(conn, senderAccountId, -amount);
            updateBalance(conn, recipientId, amount);

            // 5️⃣ Insert transaction + get ID
            String transactionId =
                    insertTransaction(conn, senderAccountId, recipientId, amount);

            conn.commit();

            // 6️⃣ Load receipt page
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/TransactionReceipt.fxml"));
            Parent root = loader.load();

            TransactionReceiptController controller = loader.getController();
            controller.setReceiptData(
                    fetchRecipientName(conn, recipientId), // recipient name
                    recipientId,
                    senderAccountId,
                    amount,
                    transactionId,
                    java.time.LocalDateTime.now()
            );

            // 7️⃣ Show receipt
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction Receipt");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Transaction Failed", e.getMessage());
        }
    }


    @FXML
    private String insertTransaction(Connection conn,
                                    String fromAccount,
                                    String toAccount,
                                    double amount) throws Exception {

        String sql = """
            INSERT INTO transactions (from_account, to_account, amount, transaction_date)
            VALUES (?, ?, ?, NOW())
        """;

        try (PreparedStatement ps =
                    conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, fromAccount);
            ps.setString(2, toAccount);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getString(1); // transaction_id (T00X)
            }
        }
        throw new Exception("Transaction ID not generated.");
    }
    private String fetchRecipientName(Connection conn, String accountId) throws Exception {
        String sql = "SELECT c.customer_name FROM customers c " +
                     "JOIN accounts a ON c.customer_id = a.customer_id " +
                     "WHERE a.account_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("customer_name");
            }
        }
        return "Unknown Recipient";
    }

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

    private boolean accountExists(Connection conn, String accountId) throws Exception {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private void updateBalance(Connection conn, String accountId, double amount) throws Exception {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
