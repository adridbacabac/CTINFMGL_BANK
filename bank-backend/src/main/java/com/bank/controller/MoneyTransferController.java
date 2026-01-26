package com.bank.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MoneyTransferController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField accountidField;

    private String customerId;

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        System.out.println("MoneyTransfer customerId: " + customerId);
    }

    // This is the only handleProceedTransfer method you need here
    @FXML
    private void handleProceedTransfer(ActionEvent event) throws IOException {
        String recipientId = accountidField.getText();
        double amount;

        // Validate amount
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert("Invalid Amount", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid amount.");
            return;
        }

        if (recipientId == null || recipientId.isEmpty()) {
            showAlert("Invalid Recipient", "Please enter a recipient account ID.");
            return;
        }

        // Load ConfirmationPage.fxml and pass data
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfirmationPage.fxml"));
        Parent root = loader.load();

        ConfirmationPageController controller = loader.getController();
        controller.setData(customerId, recipientId, amount);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Confirm Transfer");
        stage.show();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
