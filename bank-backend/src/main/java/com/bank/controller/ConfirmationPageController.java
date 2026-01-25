package com.bank.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmationPageController {

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/MoneyTransfer.fxml");
    }

    @FXML
    private void handleSendMoney(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/TransactionReceipt.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
