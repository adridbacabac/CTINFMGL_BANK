package com.bank.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TransactionReceiptController {

    // ===== LABELS FROM FXML =====

    @FXML
    private Label recipientActID;   // recipient account name

    @FXML
    private Label recipientAccID;   // recipient account ID

    @FXML
    private Label senderAccID;      // sender account ID

    @FXML
    private Label totalAmt;         // amount

    @FXML
    private Label transacID;        // transaction ID (T00X)

    @FXML
    private Label transacDate;      // transaction date


    /**
     * Called AFTER loading the receipt page
     */
    public void setReceiptData(String recipientName,
                               String recipientAccountId,
                               String senderAccountId,
                               double amount,
                               String transactionId,
                               LocalDateTime transactionDate) {

        recipientActID.setText(recipientName);
        recipientAccID.setText(recipientAccountId);
        senderAccID.setText(senderAccountId);
        totalAmt.setText(String.format("%.2f", amount));
        transacID.setText(transactionId);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy • hh:mm a");
        transacDate.setText(transactionDate.format(formatter));
    }

    // ===== BUTTON HANDLERS =====

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    @FXML
    private void handleDoneButton(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    // ===== SCENE SWITCH HELPER =====

    private void switchScene(ActionEvent event, String fxmlPath)
            throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
