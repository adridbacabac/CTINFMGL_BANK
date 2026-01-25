package com.bank.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelpSupportController {

    @FXML
    private Button backButton;

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Load the Home.fxml file
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/bank/view/Home.fxml"));

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(homeRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
