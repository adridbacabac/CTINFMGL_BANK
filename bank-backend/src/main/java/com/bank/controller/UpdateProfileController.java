package com.bank.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateProfileController {

    @FXML
    private TextField numberField;   // New Username

    @FXML
    private TextField pinField;      // New PIN

    @FXML
    private Label usernamelabel;

    // BACK BUTTON → Home.fxml
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "/com/bank/view/Home.fxml");
    }

    // CONFIRM UPDATE BUTTON
    @FXML
    private void loginButtonHandler(ActionEvent event) throws IOException {
        // TODO: Add update logic (database / model update)

        System.out.println("Updated Username: " + numberField.getText());
        System.out.println("Updated PIN: " + pinField.getText());

        // After update → Home
        switchScene(event, "/fxml/Home.fxml");
    }

    // SCENE SWITCH HELPER
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // OPTIONAL: set username when loading page
    public void setUsername(String username) {
        usernamelabel.setText(username);
    }
}
