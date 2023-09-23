package FrameworkNDrivers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {//to start the Chat Server.
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/InterfaceAdapter/ServerGUI.fxml"));
        primaryStage.setTitle("Chat Server");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }
}