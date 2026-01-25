package com.bank.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Home.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("NU Bank");
        stage.setScene(scene);
        stage.setMaximized(true); 
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
