package com.bank.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TransactionReceiptController {

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    @FXML
    private void handleDone(ActionEvent event) throws IOException {
        switchScene(event, "/fxml/Home.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
